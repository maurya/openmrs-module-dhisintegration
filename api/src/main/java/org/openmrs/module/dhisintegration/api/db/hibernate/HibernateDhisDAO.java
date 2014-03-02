package org.openmrs.module.dhisintegration.api.db.hibernate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
import org.openmrs.module.dhisintegration.api.db.DhisDAO;

public class HibernateDhisDAO implements DhisDAO{

	private static Log log = LogFactory.getLog(HibernateDhisDAO.class);

    private SessionFactory sessionFactory;
	private Transaction trans = null;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
    
    //integration server
	@Override
	public IntegrationServer getIntegrationServerById(Integer id) {
		return (IntegrationServer) sessionFactory.getCurrentSession().get(
				IntegrationServer.class, id);
	}

	@Override
	public IntegrationServer getIntegrationServerByUuid(String uuid) {
		return (IntegrationServer) sessionFactory.getCurrentSession().createCriteria(IntegrationServer.class)
		        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	@Override
	public IntegrationServer getIntegrationServerByName(String serverName) {
		return (IntegrationServer) sessionFactory.getCurrentSession()
				.createCriteria(IntegrationServer.class)
				.add(Restrictions.eq("serverName", serverName)).uniqueResult();
	}

	@Override
	public IntegrationServer getIntegrationServerByUrl(String url) {
		return (IntegrationServer) sessionFactory.getCurrentSession()
				.createCriteria(IntegrationServer.class)
				.add(Restrictions.eq("url", url)).uniqueResult();
	}

	@Override
	public IntegrationServer saveIntegrationServer(
			IntegrationServer IntegrationServer) {
		sessionFactory.getCurrentSession().saveOrUpdate(IntegrationServer);
		return IntegrationServer;
	}

	@Override
	public void deleteIntegrationServer(
			IntegrationServer IntegrationServer) {
		sessionFactory.getCurrentSession().delete(IntegrationServer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IntegrationServer> getAllIntegrationServers() {
		List<IntegrationServer> list = sessionFactory.getCurrentSession().createCriteria(IntegrationServer.class).list();
		return list;
	}
	
	//Report Template
		@Override
		public ReportTemplate getReportTemplateById(Integer id) {
			return (ReportTemplate) sessionFactory.getCurrentSession().get(
					ReportTemplate.class, id);
		}

		@Override
		public ReportTemplate getReportTemplateByUuid(String uuid) {
			return (ReportTemplate) sessionFactory.getCurrentSession().createCriteria(ReportTemplate.class)
			        .add(Restrictions.eq("uuid", uuid)).uniqueResult();	
			
		}

		@Override
		public ReportTemplate getReportTemplateByUid(String uid,
				IntegrationServer integrationServer) {
			return (ReportTemplate) sessionFactory.getCurrentSession().createCriteria(ReportTemplate.class)
					.add(Restrictions.eq("uid", uid))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}

		@Override
		public ReportTemplate getReportTemplateByCode(String code,
				IntegrationServer integrationServer) {
			return (ReportTemplate) sessionFactory.getCurrentSession().createCriteria(ReportTemplate.class)
					.add(Restrictions.eq("code", code))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}

		@Override
		public List<ReportTemplate> getReportTemplatesByServer(IntegrationServer integrationServer) {
			@SuppressWarnings("unchecked")
			List<ReportTemplate> list = sessionFactory.getCurrentSession()
											.createCriteria(ReportTemplate.class)
											.add(Restrictions.eq("integrationServer", integrationServer))
											.addOrder(Order.asc("id")).list();
			return list;
		}

		@Override
		public ReportTemplate saveReportTemplate(ReportTemplate ReportTemplate) {
			sessionFactory.getCurrentSession().saveOrUpdate(ReportTemplate);
			return ReportTemplate;
		}

		@Override
		public void deleteReportTemplate(
				ReportTemplate ReportTemplate) {
			sessionFactory.getCurrentSession().delete(ReportTemplate);
		}

		@Override
		public List<ReportTemplate> getAllReportTemplates() {
			
			@SuppressWarnings("unchecked")
			List<ReportTemplate> ReportTemplateList=sessionFactory.getCurrentSession().createCriteria(ReportTemplate.class).list();

	return ReportTemplateList;
		}

		//Org Unit Methods
		
		@Override
		public OrgUnit getOrgUnitById(Integer id) {
			return (OrgUnit) sessionFactory.getCurrentSession().get(
					OrgUnit.class, id);
		}

		@Override
		public OrgUnit getOrgUnitByUuid(String uuid) {
			return (OrgUnit) sessionFactory.getCurrentSession().createCriteria(OrgUnit.class)
			        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
		}
		
		@Override
		public OrgUnit getOrgUnitByUid(String uid,
				IntegrationServer integrationServer) {
			return (OrgUnit) sessionFactory.getCurrentSession().createCriteria(OrgUnit.class)
					.add(Restrictions.eq("uid", uid))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}

		@Override
		public OrgUnit getOrgUnitByCode(String code,
				IntegrationServer integrationServer) {
			return (OrgUnit) sessionFactory.getCurrentSession().createCriteria(OrgUnit.class)
					.add(Restrictions.eq("code", code))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}


		@Override
		public List<OrgUnit> getAllOrgUnits() {
			@SuppressWarnings("unchecked")
			List<OrgUnit> OrgUnitList=sessionFactory.getCurrentSession().createCriteria(OrgUnit.class).list();

			return OrgUnitList;
		}

		@Override
		public List<OrgUnit> getOrgUnitByServer(IntegrationServer integrationServer) {
			@SuppressWarnings("unchecked")
			List<OrgUnit> list = sessionFactory.getCurrentSession()
											.createCriteria(OrgUnit.class)
											.add(Restrictions.eq("integrationServer", integrationServer))
											.addOrder(Order.asc("id")).list();
			return list;

		}

		@Override
		public List<OrgUnit> getOrgUnitByParent(OrgUnit OrgUnit) {
			@SuppressWarnings("unchecked")
			List<OrgUnit> list = sessionFactory.getCurrentSession()
											.createCriteria(OrgUnit.class)
											.add(Restrictions.eq("parentOrg", OrgUnit))
											.addOrder(Order.asc("id")).list();
			return list;

		}

		@Override
		public OrgUnit saveOrgUnit(OrgUnit OrgUnit) {
			sessionFactory.getCurrentSession().saveOrUpdate(OrgUnit);
			return OrgUnit;
		}

		@Override
		public void deleteOrgUnit(OrgUnit OrgUnit) {
			sessionFactory.getCurrentSession().delete(OrgUnit);
		}
	
		//data elements
		@Override
		public DataElement getDataElementById(Integer id) {
			return (DataElement) sessionFactory.getCurrentSession().get(
					DataElement.class, id);
		}

		@Override
		public DataElement getDataElementByUuid(String uuid) {
			return (DataElement) sessionFactory.getCurrentSession().createCriteria(DataElement.class)
			        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
		}
		
		@Override
		public DataElement getDataElementByUid(String uid,
				IntegrationServer integrationServer) {
			return (DataElement) sessionFactory.getCurrentSession().createCriteria(DataElement.class)
					.add(Restrictions.eq("uid", uid))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}

		@Override
		public DataElement getDataElementByCode(String code,
				IntegrationServer integrationServer) {
			return (DataElement) sessionFactory.getCurrentSession().createCriteria(DataElement.class)
					.add(Restrictions.eq("code", code))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}

		@Override
		public List<DataElement> getDataElementsByServer(IntegrationServer integrationServer) {
			@SuppressWarnings("unchecked")
			List<DataElement> list = sessionFactory.getCurrentSession().createCriteria(DataElement.class)
			        .add(Restrictions.eq("integrationServer", integrationServer)).addOrder(Order.asc("id")).list();
			return list;
		}

		@Override
		public DataElement saveDataElement(DataElement DataElement) {
			sessionFactory.getCurrentSession().saveOrUpdate(DataElement);
			return DataElement;
		}

		@Override
		public void deleteDataElement(DataElement DataElement) {
			sessionFactory.getCurrentSession().delete(DataElement);
			
		}
		
		
		//data value templates
		@Override
		public DataValueTemplate getDataValueTemplateById(Integer id) {
			return (DataValueTemplate) sessionFactory.getCurrentSession().get(
					DataValueTemplate.class, id);
		}

		@Override
		public DataValueTemplate getDataValueTemplateByUuid(String uuid) {
			return (DataValueTemplate) sessionFactory.getCurrentSession().createCriteria(DataValueTemplate.class)
			        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
		}

		@Override
		public List<DataValueTemplate> getDataValueTemplateByReportTemplate(
				ReportTemplate reportTemplate) {	
			@SuppressWarnings("unchecked")
			List<DataValueTemplate> list = sessionFactory.getCurrentSession().createCriteria(DataValueTemplate.class)
			        .add(Restrictions.eq("reportTemplate", reportTemplate)).addOrder(Order.asc("id")).list();
			return list;
		}

		@Override
		public List<DataValueTemplate> getDataValueTemplateByDataElement(
				DataElement dataElement) {
			@SuppressWarnings("unchecked")
			List<DataValueTemplate> list = sessionFactory.getCurrentSession().createCriteria(DataValueTemplate.class)
			        .add(Restrictions.eq("dataElement", dataElement)).addOrder(Order.asc("id")).list();
			return list;
		}

		@Override
		public List<DataValueTemplate> getDataValueTemplateByCategoryOption(
				CategoryOption categoryOption) {
			@SuppressWarnings("unchecked")
			List<DataValueTemplate> list = sessionFactory.getCurrentSession().createCriteria(DataValueTemplate.class)
			        .add(Restrictions.eq("categoryOption", categoryOption)).addOrder(Order.asc("id")).list();
			return list;
		}

		@Override
		public DataValueTemplate saveDataValueTemplate(
				DataValueTemplate DataValueTemplate) {
			sessionFactory.getCurrentSession().saveOrUpdate(DataValueTemplate);
			return DataValueTemplate;
		}

		@Override
		public void deleteDataValueTemplate(DataValueTemplate DataValueTemplate) {
			sessionFactory.getCurrentSession().delete(DataValueTemplate);
		}

		//Category Combo Methods
		@Override
		public CategoryCombo getCategoryComboById(Integer id) {
			return (CategoryCombo) sessionFactory.getCurrentSession().get(
					CategoryCombo.class, id);
		}

		@Override
		public CategoryCombo getCategoryComboByUuid(String uuid) {
			return (CategoryCombo) sessionFactory.getCurrentSession().createCriteria(CategoryCombo.class)
			        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
		}

		@Override
		public CategoryCombo getCategoryComboByUid(String uid,
				IntegrationServer integrationServer) {
			return (CategoryCombo) sessionFactory.getCurrentSession().createCriteria(CategoryCombo.class)
					.add(Restrictions.eq("uid", uid))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}

		@Override
		public CategoryCombo getCategoryComboByCode(String code,
				IntegrationServer integrationServer) {
			return (CategoryCombo) sessionFactory.getCurrentSession().createCriteria(CategoryCombo.class)
					.add(Restrictions.eq("code", code))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}
		
		@Override
		public List<CategoryCombo> getCategoryComboByServer(
				IntegrationServer integrationServer) {
			@SuppressWarnings("unchecked")
			List<CategoryCombo> list = sessionFactory.getCurrentSession().createCriteria(CategoryCombo.class)
			        .add(Restrictions.eq("integrationServer", integrationServer)).addOrder(Order.asc("id")).list();
			return list;
		}
		
		@Override
		public CategoryCombo saveCategoryCombo(CategoryCombo CategoryCombo) {
			sessionFactory.getCurrentSession().saveOrUpdate(CategoryCombo);
			return CategoryCombo;
		}

		@Override
		public void deleteCategoryCombo(CategoryCombo CategoryCombo) {
			sessionFactory.getCurrentSession().delete(CategoryCombo);
			
		}

		//category option
		@Override
		public CategoryOption getCategoryOptionById(Integer id) {
			return (CategoryOption) sessionFactory.getCurrentSession().get(
					CategoryOption.class, id);
		}

		@Override
		public CategoryOption getCategoryOptionByUuid(String uuid) {
			return (CategoryOption) sessionFactory.getCurrentSession().createCriteria(CategoryOption.class)
			        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
		}
		
		@Override
		public CategoryOption getCategoryOptionByUid(String uid,
				IntegrationServer integrationServer) {
			return (CategoryOption) sessionFactory.getCurrentSession().createCriteria(CategoryOption.class)
					.add(Restrictions.eq("uid", uid))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}

		@Override
		public CategoryOption getCategoryOptionByCode(String code,
				IntegrationServer integrationServer) {
			return (CategoryOption) sessionFactory.getCurrentSession().createCriteria(CategoryOption.class)
					.add(Restrictions.eq("code", code))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}


		@Override
		public List<CategoryOption> getCategoryOptionByServer(IntegrationServer integrationServer) {
			@SuppressWarnings("unchecked")
			List<CategoryOption> list = sessionFactory.getCurrentSession().createCriteria(CategoryOption.class)
			        .add(Restrictions.eq("integrationServer", integrationServer)).addOrder(Order.asc("id")).list();
			return list;
		}

		@Override
		public CategoryOption saveCategoryOption(CategoryOption CategoryOption) {
			sessionFactory.getCurrentSession().saveOrUpdate(CategoryOption);
			return CategoryOption;
		}

		@Override
		public void deleteCategoryOption(CategoryOption CategoryOption) {
			sessionFactory.getCurrentSession().delete(CategoryOption);
		}

		//Option Set Methods

		@Override
		public OptionSet getOptionSetById(Integer id) {
			return (OptionSet) sessionFactory.getCurrentSession().get(
					OptionSet.class, id);
		}

		@Override
		public OptionSet getOptionSetByUuid(String uuid) {
			return (OptionSet) sessionFactory.getCurrentSession().createCriteria(OptionSet.class)
			        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
		}
		
		@Override
		public OptionSet getOptionSetByUid(String uid,
				IntegrationServer integrationServer) {
			return (OptionSet) sessionFactory.getCurrentSession().createCriteria(OptionSet.class)
			        .add(Restrictions.eq("uid", uid))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}

		@Override
		public OptionSet getOptionSetByCode(String code,
				IntegrationServer integrationServer) {
			return (OptionSet) sessionFactory.getCurrentSession().createCriteria(OptionSet.class)
			        .add(Restrictions.eq("code", code))
			        .add(Restrictions.eq("integrationServer", integrationServer))
			        .uniqueResult();
		}

		

		@Override
		public List<OptionSet> getOptionSetsByServer(
				IntegrationServer integrationServer) {
			@SuppressWarnings("unchecked")
			List<OptionSet> list = sessionFactory.getCurrentSession().createCriteria(OptionSet.class)
			        .add(Restrictions.eq("integrationServer", integrationServer)).addOrder(Order.asc("id")).list();
			return list;
		}
		
		@Override
		public OptionSet saveOptionSet(OptionSet optionSet) {
			sessionFactory.getCurrentSession().saveOrUpdate(optionSet);
			return optionSet;
		}

		@Override
		public void deleteOptionSet(OptionSet optionSet) {
			sessionFactory.getCurrentSession().delete(optionSet);
			
		}	
		
	//option
	
	@Override
	public Option getOptionById(Integer id) {
		return (Option) sessionFactory.getCurrentSession().get(
				Option.class, id);
	}

	@Override
	public Option getOptionByUuid(String uuid) {
		return (Option) sessionFactory.getCurrentSession().createCriteria(Option.class)
		        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}
	
	@Override
	public Option getOptionByUid(String uid, IntegrationServer integrationServer) {
		return (Option) sessionFactory.getCurrentSession().createCriteria(Option.class)
		        .add(Restrictions.eq("uid", uid))
		        .add(Restrictions.eq("integrationServer", integrationServer))
		        .uniqueResult();
	}

	@Override
	public Option getOptionByCode(String code,
			IntegrationServer integrationServer) {
		return (Option) sessionFactory.getCurrentSession().createCriteria(Option.class)
		        .add(Restrictions.eq("code", code))
		        .add(Restrictions.eq("integrationServer", integrationServer))
		        .uniqueResult();
	}

	@Override
	public List<Option> getOptionsByServer(IntegrationServer integrationServer) {
		@SuppressWarnings("unchecked")
		List<Option> list = sessionFactory.getCurrentSession().createCriteria(Option.class)
		        .add(Restrictions.eq("integrationServer", integrationServer)).addOrder(Order.asc("id")).list();
		return list;
	}

	@Override
	public Option saveOption(Option Option) {
			sessionFactory.getCurrentSession().saveOrUpdate(Option);
			return Option;
	}

	@Override
	public void deleteOption(Option Option) {
			sessionFactory.getCurrentSession().delete(Option);
	}



	
// misc
	

	


//	@Override
//	public List<OptionSet> getOptionSetsByReportTemplate(
//			ReportTemplate ReportTemplate) {
//		List<DataValueTemplate> DataValueTemplateList=getDataValueTemplateByReportTemplate(ReportTemplate);
//		Set<CategoryOption> categoryOptionList = new HashSet<CategoryOption>();
//		for(DataValueTemplate d:DataValueTemplateList){
//			categoryOptionList.add(d.getCategoryOption());
//			}
//		int[] tags = {};
//		
//		String sql = "select distinct o from OptionSet o"  +
//                "join o.categoryCombos cc " +
//                "where cc.id in (:tags)";
//		@SuppressWarnings("unchecked")
//		List<OptionSet> OptionSets = sessionFactory.getCurrentSession().createSQLQuery(sql)
//			    .addEntity("OptionSet", OptionSet.class)
//			    .setParameterList("tags", tags)
//			.list();
//		return OptionSets;
//	}

	
	@Override
	public Map<String,ClassMetadata> getHibernateClassMetadata() {
		return sessionFactory.getAllClassMetadata();
	}
		
	@Override
	public void commit() {
		if (trans==null) {
			trans=sessionFactory.getCurrentSession().beginTransaction();
		} else {
			trans.commit();
			trans=null;
		}
	}
	
	@Override
	public OpenmrsDhisObject getExistingByUid(Class<? extends OpenmrsDhisObject> k,
			String uid, IntegrationServer is) {
		if (is==null || uid==null) {
			return null;
		} else if (uid.length()==0)
			return null;
		return (OpenmrsDhisObject) sessionFactory.getCurrentSession().createCriteria(k)
		        .add(Restrictions.eq("uid", uid))
		        .add(Restrictions.eq("integrationServer", is))
		        .uniqueResult();		
	}

	public List<ReportMapDisplay> getReportMapDisplay(IntegrationServer is) {
	// get one row per data element for all reports for this server
		String sql = 
				" from ReportTemplate as rt inner join rt.dataElements as de inner join de.categoryCombo as cc" +
				" where rt.integrationServer.id=" + is.getId().toString() +
				" order by rt.name,cc.name,de.name";
		List<Object[]> qr = sessionFactory.getCurrentSession().createQuery(sql).list();
		List<ReportMapDisplay> result = new ArrayList<ReportMapDisplay>(0);
	// start building the first rmd
		ReportTemplate rt = (ReportTemplate) qr.get(0)[0];
		CategoryCombo cc = (CategoryCombo) qr.get(0)[2];
		ReportMapDisplay rmd = new ReportMapDisplay(rt, cc);
		for (OptionSet os : cc.getOptionSets()) {
			rmd.addOptionSet(os);
		}
		Collections.sort(rmd.getOptionSets());
	// step through the query result
		for (Object[] oo : qr) {
			rt = (ReportTemplate) oo[0];
			cc = (CategoryCombo) oo[2];
	// if we have a report/combo break, write the current rmd and start a new one
			if (!rmd.getReportId().equals(rt.getId()) || !rmd.getComboId().equals(cc.getId())) {
				result.add(rmd);
				rmd = new ReportMapDisplay(rt, cc);
				for (OptionSet os : cc.getOptionSets()) {
					rmd.addOptionSet(os);
				}
				Collections.sort(rmd.getOptionSets());
			}
	// add the data element to the existing rmd
			DataElement de = (DataElement) oo[1];
			rmd.addElement(de);
		}
	// save the last rmd
		result.add(rmd);
		return result;
	}

}
