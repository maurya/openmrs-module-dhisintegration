package org.openmrs.module.dhisintegration;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;

public class ReportTemplate extends OpenmrsDhisObject {
	public static Log log = LogFactory.getLog(ReportTemplate.class);

	private String frequency;
	private String mappedReportUuid;
	private Date lastUpdated;
	private IntegrationServer integrationServer;
	private Set<DataValueTemplate> dataValueTemplates = new HashSet<DataValueTemplate>(0);
	private Set<DataElement> dataElements = new HashSet<DataElement>(0);
	
	public ReportTemplate() {
		super();
	}

	public ReportTemplate(String name,String code,String uid) {
		super(name,code,uid);
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getMappedReportUuid() {
		return mappedReportUuid;
	}

	public void setMappedReportUuid(String mappedReportUuid) {
		this.mappedReportUuid = mappedReportUuid;
	}
	
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public void setIntegrationServer(IntegrationServer integrationServer)
    {
        this.integrationServer = integrationServer;
    }

    public IntegrationServer getIntegrationServer()
    {
        return this.integrationServer;
    }
    
    public Set<DataValueTemplate> getDataValueTemplates() {
        return dataValueTemplates;
    }
 
    public void setDataValueTemplates(Set<DataValueTemplate> dataValueTemplates) {
        this.dataValueTemplates = dataValueTemplates;
    }
    
    public Set<DataElement> getDataElements() {
        return dataElements;
    }
 
    public void setDataElements(Set<DataElement> dataElements) {
        this.dataElements = dataElements;
    }
}