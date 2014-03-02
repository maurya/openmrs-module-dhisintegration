package org.openmrs.module.dhisintegration;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;

public class IntegrationServer extends BaseOpenmrsObject {
	public static Log log = LogFactory.getLog(IntegrationServer.class);

	private Integer id;
	private String serverName;
	private String serverDescription;
	private String userName;
	private String password;
	private String url;
	private String emailorurl;
	private String transportType;
	private String masterTemplate;
	private Date lastUpdated;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id=id;
	}
	
	public int getIntegrationServerId() {
		return this.getId();
	}

	public void setIntegrationServerId(int integrationServerId) {
		this.setId(integrationServerId);
	}
	
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String name) {
		this.serverName = name;
	}
	
	public String getServerDescription() {
		return serverDescription;
	}

	public void setServerDescription(String description) {
		this.serverDescription = description;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public String getEmailorurl() {
		return emailorurl;
	}

	public void setEmailorurl(String emailorurl) {
		this.emailorurl = emailorurl;
	}
	
	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}
	
	public String getMasterTemplate() {
		return masterTemplate;
	}

	public void setMasterTemplate(String masterTemplate) {
		this.masterTemplate = masterTemplate;
	}
	
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
