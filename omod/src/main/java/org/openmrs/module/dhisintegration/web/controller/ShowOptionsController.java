package org.openmrs.module.dhisintegration.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.Option;
import org.openmrs.module.dhisintegration.OptionSet;
import org.openmrs.module.dhisintegration.ReportTemplate;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShowOptionsController {
	@RequestMapping(value = "/module/dhisintegration/showOptions", method = RequestMethod.GET)
	@Authorized("Manage Options")
	public void showDataElementsAndOptions(@RequestParam(required=false, value="reportTemplateId") int reportTemplateId,
			@RequestParam(required=true, value="server") String server,
			ModelMap model) {
		DhisService dhisService = Context.getService(DhisService.class);
		ReportTemplate reportTemplate=dhisService.getReportTemplateById(reportTemplateId);
		model.addAttribute("reportTemplate",reportTemplate);
		model.addAttribute("server",server);
		Set<OptionSet> OptionSetList = dhisService.getOptionSetsByReportTemplate(reportTemplate);
		model.addAttribute("OptionSetList",OptionSetList);

		Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
		CohortDefinitionService cs=Context.getService(CohortDefinitionService.class);
		List<CohortDefinition> cohortList=cs.getAllDefinitions(false);
		Map<String,CohortDefinition> uuidToCohortDefinitionMap=new HashMap<String, CohortDefinition>();
		for(CohortDefinition c: cohortList){
			uuidToCohortDefinitionMap.put(c.getUuid(), c);
		}
		model.addAttribute("uuidToCohortDefinitionMap",uuidToCohortDefinitionMap);
	}
	
	@RequestMapping(value = "/module/dhisintegration/saveOptionsSetMapping", method = RequestMethod.POST)
	@Authorized("Manage Options")
    public void saveReportTemplate(@RequestParam(value = "uuid", required=true) String uuid,
    		@RequestParam(value = "id", required=true) String id){
		try {
			if(uuid!=null){
			DhisService dhisService = Context.getService(DhisService.class);	
			Option option=dhisService.getOptionById(Integer.parseInt((id)));
			
			option.setCohortdefUuid(uuid);
			dhisService.saveOption(option);
			}
        }
        catch (Exception e) {
	       
        }		
    }

}
