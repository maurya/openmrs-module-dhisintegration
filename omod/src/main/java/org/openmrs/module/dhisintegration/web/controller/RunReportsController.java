package org.openmrs.module.dhisintegration.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.dhisintegration.ReportTemplate;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.SimpleHtmlReportRenderer;
import org.openmrs.module.reporting.web.renderers.DefaultWebRenderer;

@Controller
public class RunReportsController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/dhisintegration/runReports", method = RequestMethod.GET)
	@Authorized("Run Reports")
	public void showRunReports(ModelMap model) {
		
		List<ReportTemplate> reports = new ArrayList<ReportTemplate>();
		DhisService dhisService = Context.getService(DhisService.class);
		ReportDefinitionService rds= Context.getService(ReportDefinitionService.class);
		reports=dhisService.getAllReportTemplates();
		
		model.addAttribute("reports",reports);
		List<ReportDefinition> reportList=rds.getAllDefinitions(false);
		Map<String,ReportDefinition> uuidToReportDefinitionMap=new HashMap<String, ReportDefinition>();
		for(ReportDefinition r: reportList){

			uuidToReportDefinitionMap.put(r.getUuid(), r);
		}
		model.addAttribute("uuidToReportDefinitionMap",uuidToReportDefinitionMap);
	}
	
	@RequestMapping(value = "/module/dhisintegration/submitReportMappings", method = RequestMethod.POST)
	@Authorized("Run Reports")
    public void saveReportTemplate(@RequestParam(value = "startDate", required=true) String startDate,
    		@RequestParam(value = "endDate", required=true) String endDate,
    		@RequestParam(value = "Location", required=true) String Location){
		ReportDefinitionService rds= Context.getService(ReportDefinitionService.class);
		ReportService rs=Context.getService(ReportService.class);
	ReportDefinition rd=rds.getDefinitionByUuid("ba0bf442-a564-472b-83e5-4991d6c1c539");
	Map<String, Object> params = new LinkedHashMap<String, Object>();
	Object value = null;
	for(Parameter p: rd.getParameters()){
		if(p.getName().equals("location")){

			value=Location;
		}
		else if(p.getName().equals("startDate")){
			
			value=startDate;
		}
		else if(p.getName().equals("endDate")){
			
			value=endDate;
		}
		
		value = WidgetUtil.parseInput(value, p.getType(), p.getCollectionType());
		params.put(p.getName(), value);
	}
	ReportRequest rr=new ReportRequest();
	RenderingMode rm=new RenderingMode();
	DefaultWebRenderer dwr=new DefaultWebRenderer();	
	rm.setRenderer(dwr);
	rr.setReportDefinition(new Mapped<ReportDefinition>(rd, params));
	rr.setBaseCohort(null);
    rr.setRenderingMode(rm);
    rr.setPriority(Priority.NORMAL);
    rr.setSchedule("");
	
	// TODO: We might want to check here if this exact same report request is already queued and just re-direct if so
	
	rr = rs.queueReport(rr);
	rs.processNextQueuedReports();
	
    }

}
