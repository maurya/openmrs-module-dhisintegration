package org.openmrs.module.dhisintegration.api;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.ReportTemplate;
import org.openmrs.module.dhisintegration.api.impl.DhisReportingUtils;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class DhisReportingUtilsTest extends BaseModuleContextSensitiveTest {
	private static Log log = LogFactory.getLog(DhisServiceTest.class);

	private DhisService ds;
	private ReportDefinitionService rds;

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() {
		ds=Context.getService(DhisService.class);
		rds=Context.getService(ReportDefinitionService.class);
		try {
			super.authenticate();
		} catch (Exception e) { }
	}
	
	@Rollback(false)
	@Test
	public void buildReport_ShouldWork() {
		ReportTemplate rt = ds.getReportTemplateById(15);
		ReportDefinition rd = DhisReportingUtils.buildReport(rt);
		rd = rds.saveDefinition(rd);
		Assert.assertNotNull("Report definition should not be null",rd);
		rt.setMappedReportUuid(rd.getUuid());
	}

}
