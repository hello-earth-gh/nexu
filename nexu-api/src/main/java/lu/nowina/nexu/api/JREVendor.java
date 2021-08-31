/**
 * Â© Nowina Solutions, 2015-2015
 *
 * ConceÌ�deÌ�e sous licence EUPL, version 1.1 ou â€“ deÌ€s leur approbation par la Commission europeÌ�enne - versions ulteÌ�rieures de lâ€™EUPL (la Â«LicenceÂ»).
 * Vous ne pouvez utiliser la preÌ�sente Å“uvre que conformeÌ�ment aÌ€ la Licence.
 * Vous pouvez obtenir une copie de la Licence aÌ€ lâ€™adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation leÌ�gale ou contractuelle eÌ�crite, le logiciel distribueÌ� sous la Licence est distribueÌ� Â«en lâ€™eÌ�tatÂ»,
 * SANS GARANTIES OU CONDITIONS QUELLES QUâ€™ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques speÌ�cifiques relevant de la Licence.
 */
package lu.nowina.nexu.api;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumerate JRE Vendors detected by NexU
 * 
 * @author David Naramski
 *
 */
@XmlType(name = "javaVendor")
@XmlEnum
public enum JREVendor {

	ORACLE, OPENJDK, NOT_RECOGNIZED;

	private static Logger logger = LoggerFactory.getLogger(JREVendor.class);

	public static JREVendor forJREVendor(String jreVendor) {
		if (jreVendor.toLowerCase().contains("oracle")) {
			return ORACLE;
		} 
		// MOD 4535992
		else if (jreVendor.toLowerCase().contains("openjdk")) {
			return ORACLE;
		}
		// END MOD 4535992
		else {
			logger.warn("JRE not recognized " + jreVendor);
			return NOT_RECOGNIZED;
		}
	}

}
