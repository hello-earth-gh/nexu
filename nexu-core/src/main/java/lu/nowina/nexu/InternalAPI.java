/**
 * © Nowina Solutions, 2015-2015
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import lu.nowina.nexu.api.AuthenticateRequest;
import lu.nowina.nexu.api.AuthenticateResponse;
import lu.nowina.nexu.api.CardAdapter;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.Execution;
import lu.nowina.nexu.api.GetCertificateRequest;
import lu.nowina.nexu.api.GetCertificateResponse;
import lu.nowina.nexu.api.GetIdentityInfoRequest;
import lu.nowina.nexu.api.GetIdentityInfoResponse;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.NexuRequest;
import lu.nowina.nexu.api.RequestValidator;
import lu.nowina.nexu.api.ScAPI;
import lu.nowina.nexu.api.SignatureRequest;
import lu.nowina.nexu.api.SignatureResponse;
import lu.nowina.nexu.api.TokenId;
import lu.nowina.nexu.api.plugin.HttpPlugin;
import lu.nowina.nexu.flow.Flow;
import lu.nowina.nexu.flow.FlowRegistry;
import lu.nowina.nexu.flow.operation.OperationFactory;
import lu.nowina.nexu.generic.ConnectionInfo;
import lu.nowina.nexu.generic.DatabaseWebLoader;
import lu.nowina.nexu.generic.GenericCardAdapter;
import lu.nowina.nexu.generic.SCDatabase;
import lu.nowina.nexu.generic.SCInfo;
import lu.nowina.nexu.view.core.UIDisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.token.SignatureTokenConnection;

/**
 * Implementation of the NexuAPI
 * 
 * @author David Naramski
 *
 */
public class InternalAPI implements NexuAPI {

	public static final ThreadGroup EXECUTOR_THREAD_GROUP = new ThreadGroup("ExecutorThreadGroup");
	
	private Logger logger = LoggerFactory.getLogger(InternalAPI.class.getName());

	private UserPreferences prefs;

	private CardDetector detector;

	private List<CardAdapter> adapters = new ArrayList<>();

	private Map<TokenId, SignatureTokenConnection> connections = new HashMap<>();

	private Map<String, HttpPlugin> httpPlugins = new HashMap<>();

	private UIDisplay display;

	private SCDatabase myDatabase;

	private DatabaseWebLoader webDatabase;

	private FlowRegistry flowRegistry;

	private OperationFactory operationFactory;
	
	private ExecutorService executor;

	private Future<?> currentTask;
	
	private RequestValidator requestValidator;
	
	public InternalAPI(UIDisplay display, UserPreferences prefs, SCDatabase store, CardDetector detector, DatabaseWebLoader webLoader,
			FlowRegistry flowRegistry, OperationFactory operationFactory) {
		this.display = display;
		this.prefs = prefs;
		this.myDatabase = store;
		this.detector = detector;
		this.webDatabase = webLoader;
		this.flowRegistry = flowRegistry;
		this.operationFactory = operationFactory;
		this.executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(EXECUTOR_THREAD_GROUP, r);
			}
		});
		this.currentTask = null;
	}

	@Override
	public List<DetectedCard> detectCards() {
		return detector.detectCard();
	}

	@Override
	public List<Match> matchingCardAdapters(DetectedCard d) {
		if (d == null) {
			logger.warn("DetectedCard argument should not be null");
			return Collections.emptyList();
		}
		List<Match> cards = new ArrayList<>();
		for (CardAdapter card : adapters) {
			if (card.accept(d)) {
				logger.info("Card is instance of " + card.getClass().getSimpleName());
				cards.add(new Match(card, d));
			}
		}
		if (cards.isEmpty()) {
			SCInfo info = null;
			if (webDatabase != null && webDatabase.getDatabase() != null) {
				info = webDatabase.getDatabase().getInfo(d.getAtr());
				if (info == null) {
					logger.warn("Card " + d.getAtr() + " is not in the web database");
				} else {
					cards.add(new Match(new GenericCardAdapter(info), d));
				}

			}
			if (info == null && myDatabase != null) {
				info = myDatabase.getInfo(d.getAtr());
				if (info == null) {
					logger.warn("Card " + d.getAtr() + " is not in the personal database");
				} else {
					cards.add(new Match(new GenericCardAdapter(info), d));
				}
			}
		}
		return cards;
	}

	@Override
	public void registerCardAdapter(CardAdapter adapter) {
		adapters.add(adapter);
	}

	@Override
	public EnvironmentInfo getEnvironmentInfo() {
		EnvironmentInfo info = EnvironmentInfo.buildFromSystemProperties(System.getProperties());
		return info;
	}

	@Override
	public TokenId registerTokenConnection(SignatureTokenConnection connection) {
		TokenId id = new TokenId();
		connections.put(id, connection);
		return id;
	}

	@Override
	public SignatureTokenConnection getTokenConnection(TokenId tokenId) {
		return connections.get(tokenId);
	}

	private <I, O> Execution<O> executeRequest(Flow<I, O> flow, I request) {
		final Execution<O> resp = new Execution<>();

		try {
			final O response;
			if(!EXECUTOR_THREAD_GROUP.equals(Thread.currentThread().getThreadGroup())) {
				final Future<O> task;
				// Prevent race condition on currentTask
				synchronized (this) {
					if((currentTask != null) && !currentTask.isDone()) {
						currentTask.cancel(true);
					}

					task = executor.submit(() -> {
						return flow.execute(this, request);
					});
					currentTask = task;
				}

				response = task.get();
			} else {
				// Allow re-entrant calls
				response = flow.execute(this, request);
			}
			if (response != null) {
				resp.setSuccess(true);
				resp.setResponse(response);
			} else {
				resp.setSuccess(false);
				resp.setError("no_response");
				resp.setErrorMessage("No response");
			}
			return resp;
		}  catch (Exception e) {
			logger.error("Cannot execute request", e);
			resp.setSuccess(false);
			resp.setError("exception");
			resp.setErrorMessage("Exception during execution");
			return resp;
		}
	}

	@Override
	public Execution<GetCertificateResponse> getCertificate(GetCertificateRequest request) {
		Execution<GetCertificateResponse> error = returnNullIfValid(request);
		if(error != null) {
			return error;
		}
		Flow<GetCertificateRequest, GetCertificateResponse> flow = flowRegistry.getFlow(FlowRegistry.CERTIFICATE_FLOW, display);
		flow.setOperationFactory(operationFactory);
		return executeRequest(flow, request);
	}

	@Override
	public Execution<SignatureResponse> sign(SignatureRequest request) {
		Execution<SignatureResponse> error = returnNullIfValid(request);
		if(error != null) {
			return error;
		}
		Flow<SignatureRequest, SignatureResponse> flow = flowRegistry.getFlow(FlowRegistry.SIGNATURE_FLOW, display);
		flow.setOperationFactory(operationFactory);
		return executeRequest(flow, request);
	}

	@Override
	public Execution<GetIdentityInfoResponse> getIdentityInfo(GetIdentityInfoRequest request) {
		Execution<GetIdentityInfoResponse> error = returnNullIfValid(request);
		if(error != null) {
			return error;
		}
		final Flow<GetIdentityInfoRequest, GetIdentityInfoResponse> flow =
				flowRegistry.getFlow(FlowRegistry.GET_IDENTITY_INFO_FLOW, display);
		flow.setOperationFactory(operationFactory);
		return executeRequest(flow, request);
	}
	
	@Override
	public Execution<AuthenticateResponse> authenticate(AuthenticateRequest request) {
		Execution<AuthenticateResponse> error = returnNullIfValid(request);
		if(error != null) {
			return error;
		}
		final Flow<AuthenticateRequest, AuthenticateResponse> flow =
				flowRegistry.getFlow(FlowRegistry.AUTHENTICATE_FLOW, display);
		flow.setOperationFactory(operationFactory);
		return executeRequest(flow, request);
	}
	
	public <T> Execution<T> returnNullIfValid(NexuRequest request) {
		if(requestValidator == null) {
			// no validator
			return null;
		} else {
			if(requestValidator.verify(request)) {
				// ok
				return null;
			} else {
				Execution<T> execution = new Execution<>();
				execution.setSuccess(false);
				execution.setError("request.not.signed");
				execution.setErrorMessage("Request is not signed");
				return execution;
			}
		}
	}

	public HttpPlugin getPlugin(String context) {
		return httpPlugins.get(context);
	}

	public void registerHttpContext(String context, HttpPlugin plugin) {
		httpPlugins.put(context, plugin);
	}

	public void store(String detectedAtr, ScAPI selectedApi, String apiParam) {
		if (myDatabase != null) {

			EnvironmentInfo env = getEnvironmentInfo();
			ConnectionInfo cInfo = new ConnectionInfo();
			cInfo.setSelectedApi(selectedApi);
			cInfo.setEnv(env);
			cInfo.setApiParam(apiParam);

			myDatabase.add(detectedAtr, cInfo);
		}
	}

	public DatabaseWebLoader getWebDatabase() {
		return webDatabase;
	}

	public UserPreferences getPrefs() {
		return prefs;
	}
	
	public void setRequestValidator(RequestValidator requestValidator) {
		this.requestValidator = requestValidator;
	}

}
