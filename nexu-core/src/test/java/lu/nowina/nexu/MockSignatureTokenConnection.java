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

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.MaskGenerationFunction;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;

public class MockSignatureTokenConnection implements SignatureTokenConnection {

	private List<DSSPrivateKeyEntry> keys;

	public MockSignatureTokenConnection(DSSPrivateKeyEntry... keys) {
		if (keys == null || keys.length == 0) {
			this.keys = Collections.emptyList();
		} else {
			this.keys = Arrays.asList(keys);
		}
	}

	@Override
	public void close() {
	}

	@Override
	public List<DSSPrivateKeyEntry> getKeys() throws DSSException {
		return keys;
	}

	@Override
	public SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, DSSPrivateKeyEntry keyEntry) throws DSSException {
		return new SignatureValue(SignatureAlgorithm.RSA_SHA256, "value".getBytes());
	}

	@Override
	public SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, MaskGenerationFunction mgf,
			DSSPrivateKeyEntry keyEntry) throws DSSException {
		return new SignatureValue(SignatureAlgorithm.RSA_SSA_PSS_SHA256_MGF1, "value".getBytes());
	}

   @Override
   public SignatureValue signDigest(Digest digest, DSSPrivateKeyEntry keyEntry) throws DSSException {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public SignatureValue signDigest(Digest digest, MaskGenerationFunction mgf, DSSPrivateKeyEntry keyEntry) throws DSSException {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public SignatureValue sign(ToBeSigned tbs, SignatureAlgorithm sa, DSSPrivateKeyEntry dsspke) throws DSSException {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public SignatureValue signDigest(Digest digest, SignatureAlgorithm sa, DSSPrivateKeyEntry dsspke) throws DSSException {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

}
