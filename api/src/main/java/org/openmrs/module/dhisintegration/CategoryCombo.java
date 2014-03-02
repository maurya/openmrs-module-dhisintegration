package org.openmrs.module.dhisintegration;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;

public class CategoryCombo extends OpenmrsDhisObject {
	public static Log log = LogFactory.getLog(CategoryCombo.class);

	private Set<CategoryOption> categoryOptions=  new HashSet<CategoryOption>(0);
	private Set<OptionSet> optionSets=  new HashSet<OptionSet>(0);
	private IntegrationServer integrationServer;
	
	public CategoryCombo() {
		super();
	}

	public CategoryCombo(String name,String code,String uid) {
		super(name,code,uid);
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