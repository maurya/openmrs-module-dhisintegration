package org.openmrs.module.dhisintegration.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.CategoryCombo;
import org.openmrs.module.dhisintegration.CategoryOption;
import org.openmrs.module.dhisintegration.DataElement;
import org.openmrs.module.dhisintegration.Option;
import org.openmrs.module.dhisintegration.OptionSet;
import org.openmrs.module.dhisintegration.ReportTemplate;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.dhisintegration.api.db.DhisMetadataUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.definition.BaseReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;


public class DhisReportingUtils {

	private static Log log = LogFactory.getLog(DhisReportingUtils.class);
	
	public static ReportDefinition buildReport(ReportTemplate rt) throws APIException {
		DhisService ds = Context.getService(DhisService.class);
		return buildReport(rt, rt.getName(), ds.getAllPatients());
	}
	
	public static ReportDefinition buildReport(ReportTemplate rt, String reportName, CohortDefinition baseCohortDefinition) 
			throws APIException {
		ReportDefinition ret = new BaseReportDefinition();
		// add parameters for startDate, endDate, and location
		ret.addParameter(ReportingConstants.START_DATE_PARAMETER);
		ret.addParameter(ReportingConstants.END_DATE_PARAMETER);
		ret.addParameter(ReportingConstants.LOCATION_PARAMETER);
		ret.setName(reportName);
		
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		DataSetDefinitionService dds = Context.getService(DataSetDefinitionService.class);
		CohortIndicatorAndDimensionDataSetDefinition dsd = new CohortIndicatorAndDimensionDataSetDefinition();

		// build the dimensions
		for (DataElement de : rt.getDataElements()) {
			for (OptionSet os : de.getCategoryCombo().getOptionSets()) {
				if (dsd.getDimension(os.getUuid())==null) {
					// add a dimension for the option set
					CohortDefinitionDimension dim = new CohortDefinitionDimension();
					dim.setName(os.getUuid());
					dim.setDescription(os.getName());
				
					// add an option for each option
					for (Option ov : os.getOptions()) {
						dim.addCohortDefinition(ov.getUuid(), cds.getDefinitionByUuid(ov.getCohortdefUuid()),new HashMap<String,Object>());
					}
					dsd.addDimension(os.getUuid(), dim, new HashMap<String,Object>());
				}
			}
		}
		
		// build the indicator specifications
		for (DataElement de : rt.getDataElements()) {
			CohortIndicatorAndDimensionSpecification spec = dsd.new CohortIndicatorAndDimensionSpecification();
			spec.setIndicatorNumber(de.getCode());
			
			// build the indicator
			CohortIndicator ci = new CohortIndicator(de.getUuid());
			ci.setCohortDefinition(cds.getDefinitionByUuid(de.getUuid()), new HashMap<String,Object>());
			ci.setDescription(de.getName());
			spec.setIndicator(new Mapped<CohortIndicator>(ci, new HashMap<String,Object>()));
			
			// set the dimension and add the indicator for each category option
			for (CategoryOption co : de.getCategoryCombo().getCategoryOptions()) {
				spec.setLabel(co.getUuid());
				Map<String, List<String>> dimOpt = new HashMap<String,List<String>>(co.getOptions().size());
				for (Option ov : co.getOptions()) {
					String osUuid="";
					for (OptionSet os : de.getCategoryCombo().getOptionSets()) {
						if (ov.getOptionSets().contains(os)) {
							osUuid = os.getUuid();
							break;
						}
					}
					List<String> ovList = new ArrayList<String>(1);
					ovList.add(ov.getUuid());
					dimOpt.put(osUuid,ovList);
				}
				spec.setDimensionOptions(dimOpt);
				dsd.addSpecification(spec);
			}
		}
		ret.addDataSetDefinition(dsd, new HashMap<String,Object>());
		
		ret.setBaseCohortDefinition(baseCohortDefinition,new HashMap<String,Object>());
		
		return ret;
	}

}
