package lu.nowina.nexu;

import eu.europa.esig.dss.enumerations.CertificateSourceType;
import eu.europa.esig.dss.enumerations.KeyUsageBit;
import eu.europa.esig.dss.enumerations.QCStatement;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.QcStatements;
import eu.europa.esig.dss.spi.DSSASN1Utils;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.QcStatementUtils;
import eu.europa.esig.dss.spi.x509.CertificatePolicy;
import java.io.File;
import java.util.List;
import java.util.Set;

public class DssTest {

	public static void main(String args[]) {
		// CertificateToken certificateToken = DSSUtils.loadCertificate(new
		// File("C:\\Users\\landry.soules\\Dev\\Projects\\Nexu\\Tests\\Landry Soules
		// (Signature).DER"));
		CertificateToken certificateToken = DSSUtils.loadCertificate(
//				new File("C:\\Users\\landry.soules\\Dev\\Projects\\Nexu\\Tests\\Landry Soules (Authentication).DER"));
				new File("/home/landry/Dev/Projects/Nexu/Tests/landry_soules_signature.pem"));
		System.out.println("certificate token : " + certificateToken.toString());

		certificateToken.getCertificate();
		
		List<CertificatePolicy> policies = DSSASN1Utils.getCertificatePolicies(certificateToken);
		for(CertificatePolicy policy: policies) {
			System.out.println(policy.getOid() + " " + policy.getCpsUrl());
		}
		
		QcStatements qcStatementsIdList = QcStatementUtils.getQcStatements(certificateToken);
		System.out.println(qcStatementsIdList);

		for (QCStatement oid : QCStatement.values()) {

			System.out.println(oid + " " + oid.getOid() + " " + oid.getDescription());
		}

		List<KeyUsageBit> keyUsageBits = certificateToken.getKeyUsageBits();
		for (KeyUsageBit keyUsageBit : keyUsageBits) {
			System.out.println(keyUsageBit);
		}

		System.out.println("----------");

		System.out.println("Issuer : " + certificateToken.getIssuerX500Principal().getName());
		System.out
				.println("Other data : " + DSSASN1Utils.get(certificateToken.getIssuerX500Principal()).get("2.5.4.3"));
		System.out.println("Extended key usage : " + DSSASN1Utils.getExtendedKeyUsage(certificateToken));
		List<KeyUsageBit> kubs = certificateToken.getKeyUsageBits();
		for (KeyUsageBit kub : kubs) {
			System.out.println("Usage : " + kub.name() + " | " + kub.toString());
		}
      // in DSS 5.6 vs 5.3 getSources() has been removed and is part of CertificateWrapper instead of CertificateToken...
//		Set<CertificateSourceType> set = certificateToken.getSources();
//		for(CertificateSourceType cst : set) {
//			System.out.println(cst);
//		}
	}
}
