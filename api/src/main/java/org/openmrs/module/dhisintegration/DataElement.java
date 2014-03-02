package org.openmrs.module.dhisintegration;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;

public class DataElement extends OpenmrsDhisObject {
	public static Log log = LogFactory.getLog(DataElement.class);

	private String dataElementType;
	private String cohortDefinitionUuid;
	private Date lastUpdated;
	private CategoryCombo categoryCombo;
	private IntegrationServer integrationServer;
	private Set<ReportTemplate> reportTemplates =  new HashSet<ReportTemplate>(0);

	public DataElement() {
		super();
	}

	public DataElement(String name,String code,String uid) {
		super(name,code,uid);
	}

	public String getDataElementType() {
		return dataElementType;
	}

	public void setDataElementType(String dataElementType) {
		this.dataElementType = dataElementType;
	}
	
	public String getCohortDefinitionUuid() {
		return cohortDefinitionUuid;
	}

	public void setCohortDefinitionUuid(String cohortDefinitionUuid) {
		this.cohortDefinitionUuid = cohortDefinitionUuid;
	}
	
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public void setCategoryCombo(CategoryCombo categoryCombo)
    {
        this.categoryCombo = categoryCombo;
    }

    public CategoryCombo getCategoryCombo()
    {
        return this.categoryCombo;
    }

	
	public void setIntegrationServer(IntegrationServer integrationServer)
    {
        this.integrationServer = integrationServer;
    }

    public IntegrationServer getIntegrationServer()
    {
        return this.integrationServer;
    }

    public void setReportTemplates(Set<ReportTemplate> reportTemplates)
    {
        this.reportTemplates = reportTemplates;
    }

    public Set<ReportTemplate> getReportTemplates()
    {
        return this.reportTemplates;
    }
    
}
