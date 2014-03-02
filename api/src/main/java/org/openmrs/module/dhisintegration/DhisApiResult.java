package org.openmrs.module.dhisintegration;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.dhisintegration.api.jaxb.ImportSummary;

public class DhisApiResult {
	
	private Boolean error=false;
	private String status="";
	private List<ChangeRecord> changes;
	private List<OrgUnit> removed;
	private ImportSummary summary;
	
	public Boolean getError() {
		return error;
	}
	
	public void setError(Boolean error) {
		this.error=error;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status=status;
	}
	
	public List<ChangeRecord> getChanges() {
		if (changes==null)
			return new ArrayList<ChangeRecord>();
		else
			return changes;
	}
	
	public void setChanges(List<ChangeRecord> changes) {
		this.changes = changes;
	}
	
	public List<OrgUnit> getRemoved() {
		if (removed==null)
			return new ArrayList<OrgUnit>();
		else
			return removed;
	}
	
	public void setRemoved(List<OrgUnit> removed) {
		this.removed=removed;
	}
	
	public ImportSummary getSummary() {
		return summary;
	}
	
	public void setSummary(ImportSummary summary) {
		this.summary=summary;
	}
	
}
