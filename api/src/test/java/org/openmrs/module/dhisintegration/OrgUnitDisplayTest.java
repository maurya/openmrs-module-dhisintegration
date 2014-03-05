package org.openmrs.module.dhisintegration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.dhisintegration.api.db.DhisMetadataUtils;
import org.openmrs.module.dhisintegration.api.db.IntegrationException;
import org.openmrs.module.dhisintegration.api.db.ServerMetadata;
import org.openmrs.module.dhisintegration.api.db.DhisMetadataUtils.ContentType;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class OrgUnitDisplayTest extends BaseModuleContextSensitiveTest {

	private IntegrationServer is;
	private IntegrationServer isDemo;
	private DhisService ds;
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() {
		ds=Context.getService(DhisService.class);

		is = ds.getIntegrationServerByName("local");
		if (is==null) {
			is = new IntegrationServer();
			is.setServerName("dhis");
			is.setServerDescription("DHIS demo server");
			is.setUrl("http://apps.dhis2.org/demo");
			is.setUserName("admin");
			is.setPassword("district");
			ds.saveIntegrationServer(is);
		}
		
		isDemo = ds.getIntegrationServerByName("OrgTest");
		if (isDemo==null) {
			isDemo = new IntegrationServer();
			isDemo.setServerName("OrgTest");
			isDemo.setServerDescription("OrgTest");
			ds.saveIntegrationServer(isDemo);
		}
			
		try {
			super.authenticate();
		} catch (Exception e) {
		}
	}

	@Test
	public void OrgUnitDisplay_shouldGetNotFoundFromResources() {
		OrgUnitDisplay.Reset();
		String s = "";
		List<OrgUnit> notFound = null;
		try {
			DhisMetadataUtils.getDhisMetadataFromResource(ContentType.ORGS, "orgs.xml", "OrgTest");
			DhisMetadataUtils.getOrgUnitDisplay(true, "OrgTest");
			notFound = OrgUnitDisplay.findDeletedOrgs("Test");
		} catch (Exception e) {
			s = e.getMessage();
			if (s==null) s=e.getLocalizedMessage();
		}
		
		Assert.assertEquals("Exception was thrown", "",s);
		Assert.assertEquals("Org units were not found",0,notFound.size());
	}

	@Test
	public void OrgUnitDisplay_shouldGetOrgsAsXmlFromResources() {
		OrgUnitDisplay.Reset();
		String s = "";
		String out = "";
		try {
			DhisMetadataUtils.getOrgUnitDisplay(true, "dhis");
			Map<String,OrgUnitDisplay> map = OrgUnitDisplay.getIndex();
			
			out = OrgUnitDisplay.getAllOrgsAsXml();
		} catch (Exception e) {
			s = e.getMessage();
			if (s==null) s=e.getLocalizedMessage();
		}
		
		try {
			File of = DhisMetadataUtils.getServerFile(ContentType.ORGS,"Test","Test");
			IOUtils.copy(new StringReader(out), new FileOutputStream(of));
		} catch (IOException e) {
		}
		
		Assert.assertEquals("Exception was thrown", null,s);
		Assert.assertTrue("Output is short",out.length()>20);
	}

	@Test
	public void OrgUnitDisplay_shouldGetOrgsAsJsonFromResources() {
		OrgUnitDisplay.Reset();
		String s = "";
		String out = "";
		try {
			DhisMetadataUtils.getOrgUnitDisplay(true, "local");
			Map<String,OrgUnitDisplay> map = OrgUnitDisplay.getIndex();
			
			out = OrgUnitDisplay.getAllOrgsAsJson();
		} catch (Exception e) {
			s = e.getMessage();
			if (s==null) s=e.getLocalizedMessage();
		}
		
		try {
			File of = DhisMetadataUtils.getServerFile(ContentType.ORGS,"New","local");
			String path=of.getAbsolutePath();
			of=new File(path.replace(".xml", ".json"));
			IOUtils.copy(new StringReader(out), new FileOutputStream(of));
		} catch (IOException e) {
		}
		
//		Assert.assertEquals("Exception was thrown", null,s);
		Assert.assertTrue("Output is short",out.length()>20);
	}

	@Rollback(false)
	@Test
	public void OrgUnitDisplay_shouldGetOrgsFromDemoServer() {
		OrgUnitDisplay.Reset();
		String s = "";
		List<OrgUnit> notFound = null;
		try {
//			DhisMetadataUtils.getServerMetadata(is);
			DhisMetadataUtils.getDhisMetadataFromAPI(ContentType.ORGS,is);
			DhisMetadataUtils.getOrgUnitDisplay(true, is.getServerName());
			notFound = OrgUnitDisplay.findDeletedOrgs(is.getServerName());
		} catch (Exception e) {
			s = e.getMessage();
			if (s==null) s=e.getLocalizedMessage();
		}
		
//		Assert.assertEquals("Exception was thrown", "",s);
		Assert.assertEquals("Org units were not found",0,notFound.size());
	}
}
 