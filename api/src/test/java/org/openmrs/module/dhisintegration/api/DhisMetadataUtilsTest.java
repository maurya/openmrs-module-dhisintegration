package org.openmrs.module.dhisintegration.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.hibernate.metadata.ClassMetadata;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.ChangeRecord;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.UndefinedCohortDefinition;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.dhisintegration.api.db.DhisDAO;
import org.openmrs.module.dhisintegration.api.db.DhisMetadataUtils;
import org.openmrs.module.dhisintegration.api.db.IntegrationException;
import org.openmrs.module.dhisintegration.api.db.ServerMetadata;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class DhisMetadataUtilsTest extends BaseModuleContextSensitiveTest {
	
	IntegrationServer is;
	DhisService ds;

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() {
		is = new IntegrationServer();
		is.setServerName("local");
		is.setServerDescription("Test data");
		is.setUrl("http://localhost:8080/dhis");
		is.setUserName("admin");
		is.setPassword("district");
		ds=Context.getService(DhisService.class);
		try {
			super.authenticate();
		} catch (Exception e) {
		}
	}

	@Test
	public void DhisMetadataUtils_shouldGetStatusOfDemoServer() {
		String s = DhisMetadataUtils.testConnection(is);
		Assert.assertNull(s);
		
	}

	@Test
	public void createNewServer_shouldWorkForResources(){
		String s="";
		ServerMetadata sm = new ServerMetadata();
		try {
			DhisMetadataUtils.getServerMetadata("MasterTemplatePlus.xml", "cats.xml", "opts.xml");
			sm.buildDBObjects("MasterTemplatePlus");
		} catch (Exception e) {
			s=e.getMessage();
		}
		
		Integer nde=sm.getDataElements().size();
		Integer ndis=sm.getDisaggregations().size();
		Integer nrt=sm.getReportTemplates().size();
		Integer ncat=sm.getCats().size();
		Integer nopt=sm.getOpts().size();
		
		Assert.assertNotNull("Master is null", sm.getMaster() );
		Assert.assertNotNull("Categories is null", sm.getCats());
		Assert.assertNotNull("Options is null", sm.getOpts());
		Assert.assertTrue("DataElements is empty", nde!=0);
		Assert.assertTrue("Disaggregations is empty", ndis!=0);
		Assert.assertTrue("ReportTemplates is empty", nrt!=0);
		Assert.assertTrue("Categories is empty", ncat!=0);
		Assert.assertTrue("Options is empty", nopt!=0);
		
		Assert.assertEquals("Exception returned","",s);
	}

	@Rollback(false)
	@Test
	public void createNewServer_shouldWorkForAPI(){
		String s="";
		try {
//			DhisMetadataUtils.getServerMetadata(is);
			ServerMetadata sm = new ServerMetadata();
			sm.buildDBObjects("local");
		} catch (Exception e) {
			s=e.getMessage();
		}
		Assert.assertEquals("Exception returned","",s);
	}

	@Rollback(false)
	@Test
	public void updateServer_shouldWorkForAPI(){
		String s="";
		List<ChangeRecord> crs=new ArrayList<ChangeRecord>();
		is = ds.getIntegrationServerByName("local");
		try {
//			DhisMetadataUtils.getServerMetadata(is);
			ServerMetadata sm = new ServerMetadata();
			crs = sm.updateServer(is.getServerName());
		} catch (Exception e) {
			s=e.getMessage();
		}
		if (s==null) s="";
		Assert.assertTrue("No changes",crs.size()>0);
		Assert.assertEquals("Exception returned","",s);
	}

}
