package org.openmrs.module.dhisintegration.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Authorized;
import org.openmrs.web.controller.PortletController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("**/reportParamsMapping.portlet")
@Authorized("Run Reports")
public class ReportParamsMappingPortletController extends PortletController {
	
	private static final Log log = LogFactory.getLog(MappingCohortPortletController.class);
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model
			) {
		if (log.isDebugEnabled())
			log.debug("In ReportParamsMappingPortlet...");


	}

}
