package org.openmrs.module.dhisintegration;


import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Location;

public class OrgUnit extends OpenmrsDhisObject {
	public static Log log = LogFactory.getLog(OrgUnit.class);

	private Set<OrgUnit> childOrgs;
	private IntegrationServer integrationServer;
	private Set<Location> locations;

	public OrgUnit() {
		super();
	}
	
	public OrgUnit(String name,String code,String uid) {
		super(name,code,uid);
	}

	public int getOrgUnitId() {
		return this.getId();
	}

	public void setOrgUnitId(int orgUnitId) {
		this.setId(orgUnitId);
	}
	
	
	public Set<OrgUnit> getChildOrgs() {
		if (childOrgs==null) {
			childOrgs=new HashSet<OrgUnit>();
		}
		return childOrgs;
	}

	public void setChildOrgs(Set<OrgUnit> childOrgs) {
		this.childOrgs = childOrgs;
	}
	
	public void setIntegrationServer(IntegrationServer integrationServer)
    {
        this.integrationServer = integrationServer;
    }

    public IntegrationServer getIntegrationServer()
    {
        return this.integrationServer;
    }
    
    public Set<Location> getLocations() {
		if (locations==null) {
			locations=new HashSet<Location>();
		}
		return locations;
	}

	public void setLocations(Set<Location> locations) {
		this.locations = locations;
	}
}