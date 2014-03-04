package org.openmrs.module.dhisintegration.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.ReportTemplate;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.dhisintegration.api.db.DhisMetadataUtils;
import org.openmrs.module.dhisintegration.api.db.IntegrationException;
import org.openmrs.module.dhisintegration.api.db.ServerMetadata;

import com.mysql.jdbc.StringUtils;

@Controller
public class IntegrationServerAdminController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/dhisintegration/integrationServerAdmin", method = RequestMethod.GET)
	@Authorized("View Server")
	public void showServerList(ModelMap model) {	
		List<IntegrationServer> servers = new ArrayList<IntegrationServer>();
		DhisService dhisService = Context.getService(DhisService.class);
		servers=dhisService.getAllIntegrationServers();
		model.addAttribute("serverItems",servers);
		model.addAttribute("integrationServer", new IntegrationServer());
	}
	
    @RequestMapping(value="/module/dhisintegration/deleteServer" ,method = RequestMethod.POST)
    @Authorized("Manage Server")
    public String purgeServer(@RequestParam(required=false, value="serverName") String serverName) {

    	IntegrationServer server = new IntegrationServer();
		try {
			
			DhisService dhisService = Context.getService(DhisService.class);	
			server=dhisService.getIntegrationServerByName(serverName);
			dhisService.deleteIntegrationServer(server);
        }
        catch (Exception e) {
	        log.error("unable to get the file", e);
        }		
    	return "redirect:/module/dhisintegration/integrationServerAdmin.form";
    } 
    
    @RequestMapping(value = "/module/dhisintegration/getServerDetails", method = RequestMethod.POST)
    @Authorized("Manage Server")
	public @ResponseBody
	void getTemplate(@RequestParam(value="serverName",required=true)String serverName, ModelMap model) {
    	IntegrationServer server = new IntegrationServer();
		try {
			
			DhisService dhisService = Context.getService(DhisService.class);	
			server=dhisService.getIntegrationServerByName(serverName);
			model.put("integrationServer",server);
        }
        catch (Exception e) {
	        log.error("unable to get the file", e);
        }		
		
	}
    
//    @RequestMapping(value = "/module/dhisintegration/testServerConnection", method = RequestMethod.POST)
//    @Authorized("Manage Server")
//    public String testConnection(@RequestParam(value="serverName",required=true)  String serverName){
//	
//			
//			DhisService dhisService = Context.getService(DhisService.class);
//			IntegrationServer server=dhisService.getIntegrationServerByName(serverName);
//			String testResult=DhisMetadataUtils.testConnection(server);
//       
//		if(!StringUtils.isNullOrEmpty(testResult))
//			return "failure";
//		
//		return "success";
//    }
    
    @RequestMapping(value = "/module/dhisintegration/saveIntegrationServer", method = RequestMethod.POST)
    @Authorized("Manage Server")
    public String saveServer(@ModelAttribute(value="integrationServer") IntegrationServer server,
    		ModelMap model, HttpServletRequest request){
		try {
			
			DhisService dhisService = Context.getService(DhisService.class);	
			server = dhisService.saveIntegrationServer(server);
			if (!server.getUrl().isEmpty() && !server.getUserName().isEmpty() && !server.getPassword().isEmpty()) {
				model.addAttribute("done", server.getServerName());
			}
        }
        catch (Exception e) {
	        log.error("unable to save the server", e);
        }		
		return "redirect:/module/dhisintegration/integrationServerAdmin.form";
    }
    
    
}
