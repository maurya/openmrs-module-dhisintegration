package org.openmrs.module.dhisintegration.api.db;

import java.util.List;

import java.util.Map;
import java.util.Set;

import org.hibernate.metadata.ClassMetadata;
import org.openmrs.module.dhisintegration.CategoryCombo;
import org.openmrs.module.dhisintegration.CategoryOption;
import org.openmrs.module.dhisintegration.DataElement;
import org.openmrs.module.dhisintegration.DataValueTemplate;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.OpenmrsDhisObject;
import org.openmrs.module.dhisintegration.Option;
import org.openmrs.module.dhisintegration.OptionSet;
import org.openmrs.module.dhisintegration.OrgUnit;
import org.openmrs.module.dhisintegration.ReportMapDisplay;
import org.openmrs.module.dhisintegration.ReportTemplate;

public interface DhisDAO {
	
	//Integration Server Methods
	IntegrationServer getIntegrationServerById(Integer id);
	
	IntegrationServer getIntegrationServerByUuid(String uuid);
	
	IntegrationServer getIntegrationServerByName(String name);
	
	IntegrationServer getIntegrationServerByUrl(String url);
	
	IntegrationServer saveIntegrationServer(IntegrationServer IntegrationServer);
	
	void deleteIntegrationServer(IntegrationServer IntegrationServer);
	
	List<IntegrationServer> getAllIntegrationServers();
	
	
	//report template methods
		ReportTemplate getReportTemplateById(Integer id);
		
		ReportTemplate getReportTemplateByUuid(String uuid);
		
		ReportTemplate getReportTemplateByUid(String uid,IntegrationServer integrationServer);
		
		ReportTemplate getReportTemplateByCode(String code,IntegrationServer integrationServer);
		
		List<ReportTemplate> getAllReportTemplates();
		
		List<ReportTemplate> getReportTemplatesByServer(IntegrationServer integrationServer);
		
		ReportTemplate saveReportTemplate(ReportTemplate ReportTemplate);
		
		void deleteReportTemplate(ReportTemplate ReportTemplate);	
		
		//Org Units methods
		OrgUnit getOrgUnitById(Integer id);
		
		OrgUnit getOrgUnitByUuid(String uuid);
		
		OrgUnit getOrgUnitByUid(String uid,IntegrationServer integrationServer);
		
		OrgUnit getOrgUnitByCode(String code,IntegrationServer integrationServer);
		
		List<OrgUnit> getAllOrgUnits();
		
		List<OrgUnit> getOrgUnitByServer(IntegrationServer integrationServer);
		
		List<OrgUnit> getOrgUnitByParent(OrgUnit OrgUnit);
		
		OrgUnit saveOrgUnit(OrgUnit OrgUnit);
		
		void deleteOrgUnit(OrgUnit OrgUnit);	
		
		
		//data element methods
		DataElement getDataElementById(Integer id);
		
		DataElement getDataElementByUuid(String uuid);
		
		DataElement getDataElementByUid(String uid,IntegrationServer integrationServer);
		
		DataElement getDataElementByCode(String code,IntegrationServer integrationServer);
		
		List<DataElement> getDataElementsByServer(IntegrationServer integrationServer);
			
		DataElement saveDataElement(DataElement DataElement);
		
		void deleteDataElement(DataElement DataElement);	

		//Data value template methods
		DataValueTemplate getDataValueTemplateById(Integer id);
		
		DataValueTemplate getDataValueTemplateByUuid(String uuid);
		
		List<DataValueTemplate> getDataValueTemplateByReportTemplate(ReportTemplate ReportTemplate);
		
		List<DataValueTemplate> getDataValueTemplateByDataElement(DataElement DataElement);

		List<DataValueTemplate> getDataValueTemplateByCategoryOption(CategoryOption CategoryOption);

		DataValueTemplate saveDataValueTemplate(DataValueTemplate DataValueTemplate);
		
		void deleteDataValueTemplate(DataValueTemplate DataValueTemplate);	
		

		//category combo methods
		CategoryCombo getCategoryComboById(Integer id);
		
		CategoryCombo getCategoryComboByUuid(String uuid);
		
		CategoryCombo getCategoryComboByUid(String uid,IntegrationServer integrationServer);
		
		CategoryCombo getCategoryComboByCode(String code,IntegrationServer integrationServer);
		
		List<CategoryCombo> getCategoryComboByServer(IntegrationServer integrationServer);
		
		CategoryCombo saveCategoryCombo(CategoryCombo CategoryCombo);
		
		void deleteCategoryCombo(CategoryCombo CategoryCombo);	
			

		
		//category option methods
		CategoryOption getCategoryOptionById(Integer id);
		
		CategoryOption getCategoryOptionByUuid(String uuid);
		
		CategoryOption getCategoryOptionByUid(String uid,IntegrationServer integrationServer);
		
		CategoryOption getCategoryOptionByCode(String code,IntegrationServer integrationServer);
		
		List<CategoryOption> getCategoryOptionByServer(IntegrationServer integrationServer);
		
		CategoryOption saveCategoryOption(CategoryOption CategoryOption);
		
		void deleteCategoryOption(CategoryOption CategoryOption);	
			

		//Option Set methods
		OptionSet getOptionSetById(Integer id);
		
		OptionSet getOptionSetByUuid(String uuid);
		
		OptionSet getOptionSetByUid(String uid,IntegrationServer integrationServer);
		
		OptionSet getOptionSetByCode(String code,IntegrationServer integrationServer);
		
		List<OptionSet> getOptionSetsByServer(IntegrationServer integrationServer);
		
		OptionSet saveOptionSet(OptionSet OptionSet);
		
		void deleteOptionSet(OptionSet OptionSet);
	
	
	//Option methods
	Option getOptionById(Integer id);
	
	Option getOptionByUuid(String uuid);
	
	Option getOptionByUid(String uid,IntegrationServer integrationServer);
	
	Option getOptionByCode(String code,IntegrationServer integrationServer);
	
	List<Option> getOptionsByServer(IntegrationServer integrationServer);
	
	Option saveOption(Option Option);
	
	void deleteOption(Option Option);
	
	//misc
//	
//	 List<OptionSet> getOptionSetsByReportTemplate(ReportTemplate ReportTemplate);
	
	Map<String,ClassMetadata> getHibernateClassMetadata();

	void commit();
	
	OpenmrsDhisObject getExistingByUid(Class<? extends OpenmrsDhisObject> k,String uid,IntegrationServer is);

	public List<ReportMapDisplay> getReportMapDisplay(IntegrationServer is);

}
