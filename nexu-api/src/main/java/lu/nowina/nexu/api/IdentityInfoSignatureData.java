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

import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.x509.CertificateToken;


/**
 * This class holds data regarding signature of some identity information.
 * 
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 *
 */
public class IdentityInfoSignatureData {

	private byte[] rawData;
	private SignatureValue signatureValue;
	private CertificateToken[] certificateChain;
	
	public IdentityInfoSignatureData() {
	}
		
	public IdentityInfoSignatureData(byte[] rawData, SignatureValue signatureValue, CertificateToken[] certificateChain) {
		super();
		this.rawData = rawData;
		this.signatureValue = signatureValue;
		this.certificateChain = certificateChain;
	}
	
	public byte[] getRawData() {
		return rawData;
	}
	
	public SignatureValue getSignatureValue() {
		return signatureValue;
	}
	
	public CertificateToken[] getCertificateChain() {
		return certificateChain;
	}

	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
	}

	public void setSignatureValue(SignatureValue signatureValue) {
		this.signatureValue = signatureValue;
	}

	public void setCertificateChain(CertificateToken[] certificateChain) {
		this.certificateChain = certificateChain;
	}
}
