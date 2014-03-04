package org.openmrs.module.dhisintegration.web.controller;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.DhisApiResult;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.dhisintegration.api.DhisApiService;
import org.openmrs.web.controller.PortletController;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("**/dhisApi.portlet")
@Authorized("Run Reports")
public class DhisApiPortletController extends PortletController {
	
	private static final Log log = LogFactory.getLog(DhisApiPortletController.class);
	@SuppressWarnings("unchecked")
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		Date asOf;
		Date sent;
		Future<DhisApiResult> future;
		String done;
		
		log.info("In DhisApiPortlet...");
		
		HttpSession session = request.getSession();
		String operation = request.getParameter("operation");
		String server = request.getParameter("server");
		log.info("op: " + operation + " svr: " + server);
		DhisService ds = Context.getService(DhisService.class);
		DhisApiService as = Context.getService(DhisApiService.class);
		IntegrationServer is = ds.getIntegrationServerByName(server);
		model.remove("done");
		
		if ("TEST".equalsIgnoreCase(operation)) {
			log.info("TEST");
			future = (Future<DhisApiResult>) as.testConnection(is);
			session.setAttribute("apiresult", future);
			
		} else if ("CREATE_API".equalsIgnoreCase(operation)) {
			log.info("CREATE_API");
			future = (Future<DhisApiResult>) as.createServerMetadata(is);
			session.setAttribute("apiresult", future);
			return;

		} else if ("CREATE_SVR".equalsIgnoreCase(operation)) {
			future = (Future<DhisApiResult>) as.createServerMetadata(server);
			session.setAttribute("apiresult", future);

		} else if ("UPDATE_API".equalsIgnoreCase(operation)) {
			log.info("UPDATE_API");
			future = (Future<DhisApiResult>) as.updateServerMetadata(is);
			session.setAttribute("apiresult", future);

		} else if ("UPDATE_SVR".equalsIgnoreCase(operation)) {
			future = (Future<DhisApiResult>) as.updateServerMetadata(server);
			session.setAttribute("apiresult", future);

		} else if ("ORGS_API".equalsIgnoreCase(operation)) {
			log.info("ORGS_API");
			future = (Future<DhisApiResult>) as.updateServerOrgUnits(is);
			session.setAttribute("apiresult", future);

		} else if ("ORGS_SVR".equalsIgnoreCase(operation)) {
			future = (Future<DhisApiResult>) as.updateServerOrgUnits(server);
			session.setAttribute("apiresult", future);

		} else if ("SEND".equalsIgnoreCase(operation)) {
			String report = request.getParameter("report");
			log.info("SEND rpt: " + report + " asof: " + request.getParameter("asof") + " sent: " + request.getParameter("sent"));

			DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");
			try {
				asOf = df.parse(request.getParameter("asof"));
				sent = df.parse(request.getParameter("sent"));
			} catch (ParseException pe) {
				asOf = new Date();
				sent = new Date();
			}
			future = (Future<DhisApiResult>) as.sendFileToServer(server, report, asOf, sent);
			session.setAttribute("apiresult", future);

		} else if ("STATUS".equalsIgnoreCase(operation)) {
			done="";
			if (session.getAttribute("apiresult")!=null) {
				future = (Future<DhisApiResult>) session.getAttribute("apiresult");
				if (future.isDone()) {
					done="DONE";
				}
			}
			log.info("STATUS done: " + done);
			model.put("done",done);
			
		} else if ("CANCEL".equalsIgnoreCase(operation)) {
			done="";
			if (session.getAttribute("apiresult")!=null) {
				future = (Future<DhisApiResult>) session.getAttribute("apiresult");
				if (!future.isDone()) {
					future.cancel(true);
					DhisApiResult result = new DhisApiResult();
					result.setError(true);
					result.setStatus("integration.ApiPortlet.Cancelled");
					session.setAttribute("apiresult", new AsyncResult<DhisApiResult> (result));
					done = "CANCEL";
				}
			}
			log.info("CANCEL done: " + done);
			model.put("done", done);
			
		}
		return;

	}

}
