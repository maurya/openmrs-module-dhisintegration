package org.openmrs.module.dhisintegration.api;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dhisintegration.api.db.ServerMetadata;
import org.openmrs.module.dhisintegration.api.jaxb.CategoriesType;
import org.openmrs.module.dhisintegration.api.jaxb.CategoryCombosType;
import org.openmrs.module.dhisintegration.api.jaxb.DataValueSet;
import org.openmrs.module.dhisintegration.api.jaxb.ImportSummary;
import org.openmrs.module.dhisintegration.api.jaxb.MetaData;
import org.openmrs.module.dhisintegration.api.jaxb.OrgUnitType;
import org.openmrs.module.dhisintegration.api.jaxb.ReportTemplates;

public class JaxbObjects {

//	singleton

	private static Log log = LogFactory.getLog(JaxbObjects.class);
	
	private static JAXBContext jc;
	private static Unmarshaller um;
	private static Marshaller mm;
	static {
		try {
			jc = JAXBContext.newInstance(ReportTemplates.class,MetaData.class,
					CategoriesType.class,CategoryCombosType.class,OrgUnitType.class,
					DataValueSet.class,ImportSummary.class);
			um = jc.createUnmarshaller();
			mm = jc.createMarshaller();
			mm.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		} catch (JAXBException e) {
			jc= null;
			um = null;
			mm = null;
		}
	}
	
	private JaxbObjects() {};
	
	public static JAXBContext getJC() {
		return jc;
	}
	
	public static Unmarshaller getUM() {
		return um;
	}
	
	public static Marshaller getMM() {
		return mm;
	}

}
