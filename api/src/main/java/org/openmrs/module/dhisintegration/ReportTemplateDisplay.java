package org.openmrs.module.dhisintegration;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;

public class ReportTemplateDisplay {
	public static Log log = LogFactory.getLog(ReportTemplateDisplay.class);

	private Integer id;
	private String uuid;
	private String name;
	private String code;
	private String uid;
	private String frequency;
	private String mappedReportUuid;
	private String mappedReportName="";
	private String baseCohortUuid;
	private String baseCohortName="";
	private IntegrationServer integrationServer;
	
	public ReportTemplateDisplay(ReportTemplate rt) {
		id=rt.getId();
		uuid=rt.getUuid();
		name=rt.getName();
		code=rt.getCode();
		uid=rt.getUid();
		frequency=rt.getFrequency();
		mappedReportUuid=rt.getMappedReportUuid();
		integrationServer=rt.getIntegrationServer();
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public String getMappedReportName() {
		return mappedReportName;
	}

	public void setMappedReportName(String mappedReportName) {
		this.mappedReportName = mappedReportName;
	}

	public String getBaseCohortUuid() {
		return baseCohortUuid;
	}

	public void setBaseCohortUuid(String baseCohortUuid) {
		this.baseCohortUuid = baseCohortUuid;
	}

	public String getBaseCohortName() {
		return baseCohortName;
	}

	public void setBaseCohortName(String baseCohortName) {
		this.baseCohortName = baseCohortName;
	}

	public IntegrationServer getIntegrationServer() {
		return integrationServer;
	}

	public void setIntegrationServer(IntegrationServer integrationServer) {
		this.integrationServer = integrationServer;
	}


}