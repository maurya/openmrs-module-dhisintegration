package org.openmrs.module.dhisintegration.api;

import java.util.Iterator;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.Option;
import org.openmrs.module.dhisintegration.OptionSet;
import org.openmrs.module.dhisintegration.OrgUnit;
import org.openmrs.module.dhisintegration.ReportMapDisplay;
import org.openmrs.module.dhisintegration.ReportTemplateDisplay;
import org.openmrs.module.dhisintegration.UndefinedCohortDefinition;
import org.openmrs.module.dhisintegration.UndefinedCohortDefinitionEvaluator;
import org.openmrs.module.dhisintegration.api.db.hibernate.HibernateDhisDAO;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.annotation.Rollback;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DhisServiceTest extends BaseModuleContextSensitiveTest {

	private static Log log = LogFactory.getLog(DhisServiceTest.class);
	
	private static final String ALL_PATIENTS = "All Patients";
	private DhisService ds;

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() {
		ds=Context.getService(DhisService.class);
		try {
			super.authenticate();
		} catch (Exception e) { }
	}
	
	@Rollback(true)
	@Test
	public void UndefinedCohortDefinition_shouldAddCohortDefOnlyIfNecessary() {
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		List<CohortDefinition> defs = cds.getAllDefinitions(true);
		int nBefore=0;
		for (CohortDefinition cd : defs) {
			if (cd instanceof UndefinedCohortDefinition) {
				nBefore++;
			}
		}
		CohortDefinition undefined = ds.getUndefinedCohortDefinition();
		defs = cds.getAllDefinitions(true);
		int nAfter=0;
		for (CohortDefinition cd : defs) {
			if (cd instanceof UndefinedCohortDefinition) {
				nAfter++;
			}
		}
		
		if (nBefore==0 && nAfter==0) {
			Assert.assertEquals("Undefined cohort def was not created", nAfter,1);
		} else if (nBefore==0) {
			Assert.assertEquals("More than one undefined cohort def was created", nAfter, 1);
			CohortDefinition undef2 = ds.getUndefinedCohortDefinition();
			defs = cds.getAllDefinitions(true);
			nAfter=0;
			for (CohortDefinition cd : defs) {
				if (cd instanceof UndefinedCohortDefinition) {
					nAfter++;
				}
			}
			Assert.assertEquals("More than one undefined cohort def was created", nAfter, 1);
			Assert.assertSame("Same object should always be returned", undefined, undef2);
		} else {
			Assert.assertEquals("No undefined cohort def should be created", nAfter, nBefore);
		}
		Assert.assertNotNull("Undefined cohort def is null",undefined);
	}

	@Test
	public void UndefinedCohortDefinition_shouldEvaluateToEmptyCohort() throws EvaluationException {
		CohortDefinition undef = ds.getUndefinedCohortDefinition();
		EvaluationContext ec = new EvaluationContext();
		CohortDefinitionEvaluator cde = new UndefinedCohortDefinitionEvaluator();
		EvaluatedCohort c = cde.evaluate(undef,ec);
		
		Assert.assertEquals("Should evaluate to an empty cohort",c.getMemberIds().size(),0);
	}
	
	@Ignore
	@Rollback(false)
	@Test
	public void getAllPatients_shouldAddCohortDefOnlyIfNecessary() {
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		List<CohortDefinition> defs = cds.getAllDefinitions(true);
		int nBefore=0;
		for (CohortDefinition cd : defs) {
			if (cd.getClass().equals(AllPatientsCohortDefinition.class)
					&& cd.getName().equals(ALL_PATIENTS)) {
				nBefore++;
			}
		}
		CohortDefinition allPat = ds.getAllPatients();
		defs = cds.getAllDefinitions(true);
		int nAfter=0;
		for (CohortDefinition cd : defs) {
			if (cd.getClass().equals(AllPatientsCohortDefinition.class)
					&& cd.getName().equals(ALL_PATIENTS)) {
				nAfter++;
			}
		}
		
		if (nBefore==0 && nAfter==0) {
			Assert.assertEquals("All patients was not created", nAfter,1);
		} else if (nBefore==0) {
			Assert.assertEquals("More than one all patients was created", nAfter, 1);
			allPat = ds.getAllPatients();
			defs = cds.getAllDefinitions(true);
			nAfter=0;
			for (CohortDefinition cd : defs) {
				if (cd.getClass().equals(AllPatientsCohortDefinition.class)) {
					nAfter++;
				}
			}
			Assert.assertEquals("More than one all patients was created", nAfter, 1);
		} else {
			Assert.assertEquals("No all patients should be created", nAfter, nBefore);
		}
		Assert.assertNotNull("AllPatients returned null",allPat);
	}

	@Test
	public void Uid_shouldBe11CharsEvenIfNull() {
		OrgUnit o = new OrgUnit("",null,"");
		Assert.assertEquals("Uid should be 11 chars", 11, o.getUid().length());
		Assert.assertEquals("Code should equal uid",o.getUid(), o.getCode());
		Assert.assertEquals("Name should be set to code", o.getCode(), o.getName());
	}
	
	@Rollback(true)
	@Test
	public void SaveIntegrationServer_shouldSave() {
		IntegrationServer is = new IntegrationServer();
		is.setServerName("Test");
		is.setTransportType("none");
		is=ds.saveIntegrationServer(is);
		Assert.assertTrue("IntegrationServer should have id assigned",is.getId()>0);
		Assert.assertNotNull("IntegrationServer should be findable",ds.getIntegrationServerById(is.getId()));
	}
	
	@Rollback(true)
	@Test
	public void SaveOptionSet_shouldSave() {
		IntegrationServer is = new IntegrationServer();
		is.setServerName("Test");
		is.setTransportType("none");
		is=ds.saveIntegrationServer(is);
		
		OptionSet os = new OptionSet(null,null,null);
		os.setIntegrationServer(is);
		os=ds.saveOptionSet(os);
		Assert.assertTrue("OptionSet should have id assigned",os.getId()>0);
		Assert.assertNotNull("Integration server should be findable",ds.getOptionSetById(os.getId()));
	}
	
	@Rollback(true)
	@Test
	public void SaveOption_shouldSave() {
		IntegrationServer is = new IntegrationServer();
		is.setServerName("Test");
		is.setTransportType("none");
		is=ds.saveIntegrationServer(is);
		
		Option op = new Option(null,null,null);
		op.setIntegrationServer(is);
		op=ds.saveOption(op);
		Assert.assertTrue("OptionSet should have id assigned",op.getId()>0);
		Assert.assertNotNull("Integration server should be findable",ds.getOptionById(op.getId()));
	}

	@NotTransactional
	@Rollback(false)
	@Test
	public void SaveOptionSet_shouldSaveAddedOption() {
	// clean out any old data (remove sets from options, then option; remove options from sets, then set; remove server)
		IntegrationServer is = ds.getIntegrationServerByName("Test");
		if (is!=null) {
			List<Option> lop = ds.getOptionsByServer(is);
			for (Option op : lop) {
				Iterator oposit = op.getOptionSets().iterator();
				while (oposit.hasNext()) {
					op.getOptionSets().remove(oposit.next());
				}
				ds.deleteOption(op);
			}
			List<OptionSet> los = ds.getOptionSetsByServer(is);
			for (OptionSet os : los) {
				Iterator osopit = os.getOptions().iterator();
				while (osopit.hasNext()) {
					os.getOptions().remove(osopit.next());
				}
				ds.deleteOptionSet(os);
			}
			ds.deleteIntegrationServer(is);
		}
			
		
		is = new IntegrationServer();
		is.setServerName("Test");
		is.setTransportType("none");
		is=ds.saveIntegrationServer(is);

		Option op = new Option(null,null,null);
		op.setIntegrationServer(is);
		op=ds.saveOption(op);
		
		OptionSet os = new OptionSet(null,null,null);
		os.setIntegrationServer(is);
		os.getOptions().add(op);
		os=ds.saveOptionSet(os);
		
		op.getOptionSets().add(os);
		op=ds.saveOption(op);
		Assert.assertTrue("OptionSet.Options is empty",os.getOptions().size()>0);
		Assert.assertTrue("Option.OptionSets is empty",op.getOptionSets().size()>0);
	}

	@Test
	public void getReportMapDisplay_shouldWork() {
		IntegrationServer is = ds.getIntegrationServerByName("local");
		List<ReportMapDisplay> display = ds.getReportMapDisplay(is);
		log.info(display.size());
		Assert.assertEquals("Wrong number of rows returned",5, display.size());
		for (ReportMapDisplay rmd : display) {
			Assert.assertTrue("No data elements in "+rmd.toString(),rmd.getElements().size()>0);
			Assert.assertTrue("No option sets in "+rmd.toString(),rmd.getOptionSets().size()>0);
		}
	}

	@Test
	public void getReportTemplateDisplay_shouldWork() {
		IntegrationServer is = ds.getIntegrationServerByName("local");
		List<ReportTemplateDisplay> display = ds.getReportTemplateDisplay(is);
		log.info(display.size());
		Assert.assertEquals("Wrong number of rows returned",2, display.size());
		for (ReportTemplateDisplay rtd : display) {
			if (rtd.getMappedReportUuid()!= null) {
				Assert.assertNotNull("Report name is null",rtd.getMappedReportName());
				if (rtd.getBaseCohortUuid()!=null) {
					Assert.assertNotNull("Base cohort name is null",rtd.getBaseCohortName());
				}
			}
		}
	}
}
 