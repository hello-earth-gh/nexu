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
package lu.nowina.nexu.api;

import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.x509.CertificateToken;

public class SignatureResponse {

	private byte[] signatureValue;
	private SignatureAlgorithm signatureAlgorithm;
	private CertificateToken certificate;
	private CertificateToken[] certificateChain;

	public SignatureResponse(SignatureValue signatureValue, CertificateToken certificate, CertificateToken[] certificateChain) {
		this.signatureValue = signatureValue.getValue();
		this.signatureAlgorithm = signatureValue.getAlgorithm();
		this.certificate = certificate;
		this.certificateChain = certificateChain;
	}

	public byte[] getSignatureValue() {
		return signatureValue;
	}

	public SignatureAlgorithm getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	public CertificateToken getCertificate() {
		return certificate;
	}

	public CertificateToken[] getCertificateChain() {
		return certificateChain;
	}
}
