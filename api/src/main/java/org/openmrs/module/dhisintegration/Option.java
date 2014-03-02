package org.openmrs.module.dhisintegration;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;

public class Option extends OpenmrsDhisObject {
	public static Log log = LogFactory.getLog(Option.class);

	private String cohortdefUuid;
	private Set<CategoryOption> categoryOptions=  new HashSet<CategoryOption>(0);
	private Set<OptionSet> optionSets=  new HashSet<OptionSet>(0);
	private IntegrationServer integrationServer;

	public Option() {
		super();
	}

	public Option(String name,String code,String uid) {
		super(name,code,uid);
	}
	
	public String getCohortdefUuid() {
		return cohortdefUuid;
	}

	public void setCohortdefUuid(String cohortdefUuid) {
		this.cohortdefUuid = cohortdefUuid;
	}
	
	 public Set<CategoryOption> getCategoryOptions() {
	        return categoryOptions;
	    }
	 
	    public void setCategoryOptions(Set<CategoryOption> categoryOptions) {
	        this.categoryOptions = categoryOptions;
	    }
	
	 public Set<OptionSet> getOptionSets() {
	        return optionSets;
	    }
	 
	    public void setOptionSets(Set<OptionSet> optionSets) {
	        this.optionSets = optionSets;
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