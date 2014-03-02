package org.openmrs.module.dhisintegration;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;

public class CategoryOption extends OpenmrsDhisObject {
	public static Log log = LogFactory.getLog(CategoryOption.class);

	private Set<Option> options=  new HashSet<Option>(0);
	private Set<CategoryCombo> categoryCombos= new HashSet<CategoryCombo>(0);
	//private CategoryCombo categoryCombo;
	private IntegrationServer integrationServer;
	
	public CategoryOption() {
		super();
	}

	public CategoryOption(String name,String code,String uid) {
		super(name,code,uid);
	}
	
	 public Set<Option> getOptions() {
	        return options;
	    }
	 
	    public void setOptions(Set<Option> options) {
	        this.options = options;
	    }
	    
	    public Set<CategoryCombo> getCategoryCombos() {
	        return categoryCombos;
	    }
	 
	    public void setCategoryCombos(Set<CategoryCombo> categoryCombos) {
	        this.categoryCombos = categoryCombos;
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