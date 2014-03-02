package org.openmrs.module.dhisintegration;

import java.util.ArrayList;
import java.util.UUID;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;

public class ReportMapDisplay implements Comparable<ReportMapDisplay> {

	public class Triple implements Comparable<Triple> {
		private Integer id;
		private String name;
		private Boolean mapped;

		Triple() {
			super();
		}
		
		Triple(DataElement de) {
			super();
			this.id = de.getId();
			this.name = de.getName();
			this.mapped = isMapped(de.getCohortDefinitionUuid());
		}
		
		Triple(OptionSet os) {
			super();
			this.id = os.getId();
			this.name = os.getName();
			this.mapped = true;
			for (Option o : os.getOptions()) {
				this.mapped &= isMapped(o.getCohortdefUuid());
			}
		}
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Boolean getMapped() {
			return mapped;
		}
		public void setMapped(Boolean mapped) {
			this.mapped = mapped;
		}
		
		@Override
		public int compareTo(Triple other) {
			int retval = this.name.compareToIgnoreCase(other.getName());
			if (retval == 0)
				retval = this.id.compareTo(other.getId());
			return retval;
		}
		
	}

	private UUID id = UUID.randomUUID();
	private Integer reportId;
	private String reportName;
	private Integer comboId;
	private String comboName;
	private ArrayList<Triple> elements = new ArrayList<Triple>(0);
	private ArrayList<Triple> optionSets = new ArrayList<Triple>(0);
	
	public ReportMapDisplay() {
		super();
	}
	
	public ReportMapDisplay(ReportTemplate report, CategoryCombo combo) {
		super();
		this.reportId=report.getId();
		this.reportName = report.getName();
		this.comboId = combo.getId();
		this.comboName = combo.getName();
	}

	public UUID getId() {
		return id;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Integer getComboId() {
		return comboId;
	}

	public void setComboId(Integer comboId) {
		this.comboId = comboId;
	}

	public String getComboName() {
		return comboName;
	}

	public void setComboName(String comboName) {
		this.comboName = comboName;
	}

	public ArrayList<Triple> getElements() {
		return elements;
	}

	public void setElements(ArrayList<Triple> elements) {
		this.elements = elements;
	}
	
	public void addElement(DataElement de) {
		this.elements.add(new Triple(de));
	}

	public ArrayList<Triple> getOptionSets() {
		return optionSets;
	}

	public void setOptionSets(ArrayList<Triple> optionSets) {
		this.optionSets = optionSets;
	}
	
	public void addOptionSet(OptionSet os) {
		this.optionSets.add(new Triple(os));
	}
	
	@Override
	public int compareTo(ReportMapDisplay other) {
		int retval;
		retval = this.getReportName().compareToIgnoreCase(other.getReportName());
		if (retval == 0) {
			retval = this.getComboName().compareToIgnoreCase(other.getComboName());
		}
		if (retval == 0) {
			retval = this.getId().compareTo(other.getId());
		}
		return retval;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		ReportMapDisplay other;
		try {
			other = (ReportMapDisplay) obj;
		} catch (Exception e) {
			return false;
		}
		return (this.getId() == other.getId());
	}

	@Override
	public String toString() {
		return "RMD[" + getReportName() + "," + getComboName() + "]";
	}

	public static Boolean isMapped(String cohortdefUUID) {
		if (cohortdefUUID == null) return false;
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		if (UndefinedCohortDefinition.class.isAssignableFrom(cds.getDefinitionByUuid(cohortdefUUID).getClass()))
			return false;
		return true;
	}

}
