package org.openmrs.module.dhisintegration.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.metadata.ClassMetadata;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.CategoryCombo;
import org.openmrs.module.dhisintegration.ChangeRecord;
import org.openmrs.module.dhisintegration.DataElement;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.ReportTemplate;
import org.openmrs.module.dhisintegration.UndefinedCohortDefinition;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.dhisintegration.api.db.DhisDAO;
import org.openmrs.module.dhisintegration.api.db.DhisMetadataUtils;
import org.openmrs.module.dhisintegration.api.db.IntegrationException;
import org.openmrs.module.dhisintegration.api.db.ServerMetadata;
import org.openmrs.module.dhisintegration.api.db.hibernate.HibernateDhisDAO;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class ManageReportTemplateControllerTest extends BaseModuleContextSensitiveTest {
	
	private static Log log = LogFactory.getLog(ManageReportTemplateControllerTest.class);

	IntegrationServer server;
	DhisService ds;

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() {
		ds=Context.getService(DhisService.class);
		server=ds.getIntegrationServerByName("dhis");
		try {
			super.authenticate();
		} catch (Exception e) {
		}
	}

	@Test
	public void ReportTemplateController_shouldPrepareComboTable() {
		Map<String,Object> model = new HashMap<String,Object>();
		List<ReportTemplate> reportTemplates = new ArrayList<ReportTemplate>();
		reportTemplates=ds.getReportTemplatesByServer(server);
		model.put("reportTemplates",reportTemplates);
		
		ReportDefinitionService rds=Context.getService(ReportDefinitionService.class);

		List<ReportDefinition> reportList=rds.getAllDefinitions(false);
		Map<String,ReportDefinition> uuidToReportDefinitionMap=new HashMap<String, ReportDefinition>();
		for(ReportDefinition r: reportList){

			uuidToReportDefinitionMap.put(r.getUuid(), r);
		}
		model.put("uuidToReportDefinitionMap",uuidToReportDefinitionMap);

		Map<DataElement,CategoryCombo> DataElementToCategoryComboDictionary= new HashMap<DataElement, CategoryCombo>();
		for(ReportTemplate r: reportTemplates){
			DataElementToCategoryComboDictionary.putAll(ds.getDataElementToCategoryComboDictionaryByReportTemplate(r));		
		}
		Map<CategoryCombo,List<DataElement>> CategoryComboToDataElementDictionary = new HashMap<CategoryCombo,List<DataElement>>();
		
		List<DataElement> temporaryDataElementList=new ArrayList<DataElement>();
		
		for(Map.Entry<DataElement,CategoryCombo> entry : DataElementToCategoryComboDictionary.entrySet()){
			
			temporaryDataElementList=CategoryComboToDataElementDictionary.get(entry.getValue());
			if(temporaryDataElementList== null){	
				temporaryDataElementList=new ArrayList<DataElement>();
				CategoryComboToDataElementDictionary.put(entry.getValue(), temporaryDataElementList);
			}
			temporaryDataElementList.add(entry.getKey());		
		}
		model.put("CategoryComboToDataElementDictionary",CategoryComboToDataElementDictionary);
		
		log.info(model);
		
		Assert.assertEquals("All elements did not get created", 3, model.size());
		
	}


}
