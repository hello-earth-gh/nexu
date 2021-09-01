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
package lu.nowina.nexu.server.controller;

import javax.servlet.http.HttpServletRequest;

import lu.nowina.nexu.server.business.PlatformStatisticManager;
import lu.nowina.nexu.server.manager.SCDatabaseManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SCDatabaseController {

	private static final String UTF8 = "UTF-8";

	@Autowired
	private SCDatabaseManager databaseManager;

	@Autowired
	private PlatformStatisticManager platformStatisticManager;

	@RequestMapping("/database.xml")
	public ResponseEntity<byte[]> getDatabase(HttpServletRequest req) throws Exception {
		platformStatisticManager.addNewStatistic(req.getParameterMap());
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);
		headers.add(HttpHeaders.CONTENT_ENCODING, UTF8);

		ResponseEntity<byte[]> entity = new ResponseEntity<>(databaseManager.getData(), headers, HttpStatus.OK);
		return entity;

	}

}
