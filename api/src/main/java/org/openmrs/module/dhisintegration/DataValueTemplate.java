package org.openmrs.module.dhisintegration;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;

public class DataValueTemplate extends BaseOpenmrsObject {
	public static Log log = LogFactory.getLog(DataValueTemplate.class);

	private Integer id;
	private DataElement dataElement;
	private CategoryOption categoryOption;
	private ReportTemplate reportTemplate;
	private Date lastUpdated;
	private IntegrationServer integrationServer;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id=id;
	}
	
	public int getDataValueTemplateId() {
		return this.getId();
	}

	public void setDataValueTemplateId(int dataValueTemplateId) {
		this.setId(dataValueTemplateId);
	}

	public DataElement getDataElement() {
		return dataElement;
	}

	public void setDataElement(DataElement dataElement) {
		this.dataElement = dataElement;
	}
	public CategoryOption getCategoryOption() {
		return categoryOption;
	}

	public void setCategoryOption(CategoryOption categoryOption) {
		this.categoryOption = categoryOption;
	}
	public ReportTemplate getReportTemplate() {
		return reportTemplate;
	}

	public void setReportTemplate(ReportTemplate reportTemplate) {
		this.reportTemplate = reportTemplate;
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

}