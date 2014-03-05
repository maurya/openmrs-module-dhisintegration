package org.openmrs.module.dhisintegration.api;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.hibernate.metadata.ClassMetadata;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.CategoryCombo;
import org.openmrs.module.dhisintegration.CategoryOption;
import org.openmrs.module.dhisintegration.ChangeRecord;
import org.openmrs.module.dhisintegration.DataElement;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.Option;
import org.openmrs.module.dhisintegration.OptionSet;
import org.openmrs.module.dhisintegration.ReportTemplate;
import org.openmrs.module.dhisintegration.api.db.IntegrationException;
import org.openmrs.module.dhisintegration.api.db.ServerMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.annotation.Rollback;

public class ServerMetadataTest extends BaseModuleContextSensitiveTest {

	private IntegrationServer is;
	private DhisService ds;
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	
	@Before
	public void setup() {
		is = new IntegrationServer();
		is.setServerName("dhis");
		is.setServerDescription("DHIS demo server");
		is.setUrl("http://apps.dhis2.org/demo");
		is.setUserName("admin");
		is.setPassword("district");
		ds=Context.getService(DhisService.class);
		try {
			super.authenticate();
		} catch (Exception e) {
		}
	}
	
	@Test
	public void ServerMetadata_shouldSeeDhisObjects() {
		int n=0;
		Map<String,ClassMetadata> h=ds.getHibernateClassMetadata();
		for (String s : h.keySet()) {
			if (s.contains("IntegrationServer")) n++;
			else if (s.contains("ReportTemplate")) n++;
			else if (s.contains("DataValueTemplate")) n++;
			else if (s.contains("CategoryCombo")) n++;
			else if (s.contains("CategoryOption")) n++;
			else if (s.contains("OptionSet")) n++;
			else if (s.contains("Option")) n++;
			else if (s.contains("DataElement")) n++;
			else if (s.contains("OrgUnit")) n++;
		}
		h=null;
		Assert.assertEquals("Dhis objects are missing",n,9);
	}

	@Rollback(false)
	@Test
	public void BuildObjects_shouldWorkForExistingServer() {
		final String server = "local";
		String s = null;
		ServerMetadata sm = new ServerMetadata();
		try {
			sm.buildDBObjects(server);
		} catch (IntegrationException e) {
			s = e.getMessage();
		}
		
		if ("".equals(s)) s=null;
		Assert.assertNull("Exception has been thrown: "+s,s);
	}

	@Rollback(false)
	@Test
	public void UpdateObjects_shouldWorkForExistingServer() {
		final String server = "local";
		String s = null;
		ServerMetadata sm = new ServerMetadata();
		try {
			List<ChangeRecord> crs = sm.updateServer(server);
		} catch (IntegrationException e) {
			s = e.getMessage();
		}
		
		if ("".equals(s)) s=null;
		Assert.assertNull("Exception has been thrown: "+s,s);
	}
}
