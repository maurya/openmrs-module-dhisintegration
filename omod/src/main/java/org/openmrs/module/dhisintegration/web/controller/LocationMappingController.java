package org.openmrs.module.dhisintegration.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LocationMappingController {
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/dhisintegration/locationMapping", method = RequestMethod.GET)
	@Authorized("Manage DHIS Locations")
	public void viewResult(@RequestParam(required=false, value="name") String name,
			ModelMap model){
		DhisService dhisService = Context.getService(DhisService.class);
		IntegrationServer server=dhisService.getIntegrationServerByName(name);
		String json="[{\"data\" : \"Search engines\", \"children\" :[{\"data\":\"Yahoo\"},{\"data\":\"Bing\"}, {\"data\":\"Google\", \"children\":[{\"data\":\"Youtube\"},{\"data\":\"Gmail\"},{\"data\":\"Orkut\"}]}]},{\"data\" : \"Networking sites\",\"children\" :[{\"data\":\"Facebook\"},{\"data\":\"Twitter\"}]}]";
		model.addAttribute("json",json);
		model.addAttribute("server",server);
	}
}
