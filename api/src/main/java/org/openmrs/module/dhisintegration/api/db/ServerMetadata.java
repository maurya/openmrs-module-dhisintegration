package org.openmrs.module.dhisintegration.api.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.CategoryCombo;
import org.openmrs.module.dhisintegration.CategoryOption;
import org.openmrs.module.dhisintegration.ChangeRecord;
import org.openmrs.module.dhisintegration.ChangeRecord.ChangeType;
import org.openmrs.module.dhisintegration.DataElement;
import org.openmrs.module.dhisintegration.DataValueTemplate;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.Option;
import org.openmrs.module.dhisintegration.OptionSet;
import org.openmrs.module.dhisintegration.OrgUnitDisplay;
import org.openmrs.module.dhisintegration.ReportTemplate;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.dhisintegration.api.db.DhisMetadataUtils.ContentType;
import org.openmrs.module.dhisintegration.api.jaxb.CategoriesType;
import org.openmrs.module.dhisintegration.api.jaxb.CategoryCombosType;
import org.openmrs.module.dhisintegration.api.jaxb.MetaData;
import org.openmrs.module.dhisintegration.api.jaxb.OrgUnitType;
import org.openmrs.module.dhisintegration.api.jaxb.ReportTemplates;

/**
 * This object holds methods that require unmarshalled DHIS xml.
 * It should be created when this functionality is needed and then destroyed.
 * 
 */
public class ServerMetadata {

	private static Log log = LogFactory.getLog(ServerMetadata.class);
	private final Double hash = Math.random();
	
	private String name;
	private IntegrationServer is;
	private ReportTemplates master;
	private MetaData opts;
	private MetaData cats;
	private MetaData orgs;
	private DhisService ds = Context.getService(DhisService.class);

	private List<ChangeRecord> changes;
	private List<ReportTemplate> reports;
	private List<DataElement> dataElements;
	private List<CategoryCombo> catCombos;
	private List<CategoryOption> catOptions;
	private List<OptionSet> optionSets;
	private List<Option> options;

	
	/**
	 * Creates the initial DB objects for a new server by
	 * parsing xml and traversing metadata
	 *  
	 * @param name the name of the server to be built
	 */
	public void buildDBObjects(String name) throws IntegrationException {
		log.info("In buildObjects");
		this.name=name;
		is=ds.getIntegrationServerByName(name);
		if (is==null) {
			is=new IntegrationServer();
			is.setServerName(name);
			ds.saveIntegrationServer(is);
		}
 
		try {
			master = DhisMetadataUtils.UnmarshalMaster("New", name);
			cats = DhisMetadataUtils.UnmarshalMetaData(ContentType.CATS,"New", name);
			opts = DhisMetadataUtils.UnmarshalMetaData(ContentType.OPTS,"New", name);
		} catch (IntegrationException ie) {
			throw ie;
		} catch (Exception e) {
			throw new IntegrationException(e.getMessage(),e);
		}
		
		String result = "";
		
		processOpts();
		processCats();
		processMaster();
		
		return;
	}

	private void processOpts() {
		for (CategoriesType.Category xcat : getOpts()) {
			OptionSet ops = (OptionSet) ds.getExistingByUid(OptionSet.class, xcat.getId(), is);
			if (ops==null) {
				ops = new OptionSet(xcat.getName(),"",xcat.getId());
				ops.setIntegrationServer(is);
				ops = ds.saveOptionSet(ops);
			}
			for (CategoriesType.Category.CategoryOptions.CategoryOption xco : xcat.getCategoryOptions().getCategoryOption()) {
				Option opv = (Option) ds.getExistingByUid(Option.class, xco.getId(), is);
				if (opv==null) {
					opv = new Option(xco.getName(),"",xco.getId());
					opv.setIntegrationServer(is);
					opv.setCohortdefUuid(ds.getUndefinedCohortDefinition().getUuid());
					opv = ds.saveOption(opv);
				}
				ops.getOptions().add(opv);
				opv.getOptionSets().add(ops);
				opv=ds.saveOption(opv);
			}
			ops=ds.saveOptionSet(ops);
		}
		return;
	}
	
	private void processCats() {
		
	// step through the category options
		for (CategoryCombosType.CategoryOptionCombo xcoc : getCats()) {
			
	// build the category combo if necessary
			CategoryCombosType.CategoryOptionCombo.CategoryCombo xcc = xcoc.getCategoryCombo();
			CategoryCombo cc = (CategoryCombo) ds.getExistingByUid(CategoryCombo.class, xcc.getId(), is);
			if (cc==null) {
				cc = new CategoryCombo(xcc.getName(),"",xcc.getId());
				cc.setIntegrationServer(is);
				cc = ds.saveCategoryCombo(cc);
			}
			
	// build the category option if necessary
			CategoryOption co = (CategoryOption) ds.getExistingByUid(CategoryOption.class, xcoc.getId(), is);
			if (co==null) {
				co = new CategoryOption(xcoc.getName(),"",xcoc.getId());
				co.setIntegrationServer(is);
				co = ds.saveCategoryOption(co);
			}
			
	// add the category combo and category option to each other's collections
			cc.getCategoryOptions().add(co);
			co.getCategoryCombos().add(cc);
			
	// add the options and category options to each other's collections
			for (CategoryCombosType.CategoryOptionCombo.CategoryOptions.CategoryOption xov : xcoc.getCategoryOptions().getCategoryOption()) {
				Option ov = ds.getOptionByUid(xov.getId(), is);
				if (ov!=null) {
					co.getOptions().add(ov);
					ov.getCategoryOptions().add(co);
					ov = ds.saveOption(ov);
				}
			}

	// save the updated category combo and category option
			ds.saveCategoryCombo(cc);
			ds.saveCategoryOption(co);
		}
		
	// find the option sets for the category combos
	// -- from the options in the category options in the catcombo, get all possible option sets
		List<CategoryCombo> ccs = ds.getCategoryComboByServer(is);
		for (CategoryCombo cc : ccs) {
			Set<OptionSet> oss = new HashSet<OptionSet>();
			for (CategoryOption co : cc.getCategoryOptions()) {
				for (Option op : co.getOptions()) {
					oss.addAll(op.getOptionSets());
				}
			}
	// -- eliminate those which are not capable of supporting all category options of the cc
			Set<OptionSet> nos = new HashSet<OptionSet>();
			for (OptionSet os : oss) {
				for (CategoryOption co : cc.getCategoryOptions()) {
					Set<Option> tos = new HashSet<Option>(co.getOptions());
					tos.retainAll(os.getOptions());
					if (tos.isEmpty()) {
						nos.add(os);
						break;
					}
				}
			}
			oss.removeAll(nos);

	// -- get the options of option sets usable by the cc
			Map<OptionSet,Set<Option>> osmap = new HashMap<OptionSet,Set<Option>>();
			for (CategoryOption co : cc.getCategoryOptions()) {
				for (OptionSet os : oss) {
					osmap.put(os, os.getOptions());
				}
			}
	// -- build in tos the set of all option sets containing at least one option in os.options
			nos = new HashSet<OptionSet>();
			for (OptionSet os : osmap.keySet()) {
				if (!nos.contains(os)) {
					Set<OptionSet> tos = new HashSet<OptionSet>();
					for (OptionSet os2 : osmap.keySet()) {
						if (os2.equals(os)) {
							tos.add(os2);
						} else if (!tos.isEmpty() && !nos.contains(os2)) {
							Set<Option> tov = new HashSet<Option>(os2.getOptions());
							tov.retainAll(os.getOptions());
							if (!tov.isEmpty()) {
								tos.add(os2);
							}
						}
					}
		// -- use the smallest set
					if (tos.size()>1) {
						OptionSet minops = os;
						for (OptionSet os2 : tos) {
							if (os2.getOptions().size()<minops.getOptions().size()) {
								minops = os2;
							}
						}
						tos.remove(minops);
						nos.addAll(tos);
					}
				}
			}
			for (OptionSet os : nos) {
				osmap.remove(os);
			}
			cc.setOptionSets(osmap.keySet());
			ds.saveCategoryCombo(cc);
		}
			
		return;
	}

	/** Here is an example of what the above should handle:
	 * Option Set 1 (OS1) contains Options 1 and 2 (O1, O2)
	 * OS2 contains O3, O4
	 * OS3 contains O3, O4, O5
	 * OS4 contains O3, O4, O5, O6
	 * CatCombo 1 (CC1) contains Category Options 1-6 (CO1-CO6)
	 * CO1 contains O1, O3
	 * CO2 contains O1, O4
	 * CO3 contains O1, O5
	 * CO4 contains O2, O3
	 * CO5 contains O2, O4
	 * CO6 contains O2, O5
	 * Map(OS1) contains O1, O2
	 * Map(OS2) contains O3, O4 -- not used because OS2 is not optionset of O5 in CO3 and CO6 
	 * Map(OS3) contains O3, O4, O5
	 * Map(OS4) contains O3, O4, O5 -- not used because it has 4 members while OS3 has 3 
	 * CC1.OptionSets contains OS1, OS3
	 */
	
	private void processMaster() {
	// add codes to disaggregations
		for (ReportTemplates.Disaggregations.Disaggregation xd : getMaster().getDisaggregations().getDisaggregation()) {
			CategoryOption co = ds.getCategoryOptionByUid(xd.getUid(), is);
			if (co != null) {
				co.setCode(xd.getCode());
				ds.saveCategoryOption(co);
			}
		}
			
	// process data elements
		for (ReportTemplates.DataElements.DataElement xde : getDataElements()) {
			DataElement de = (DataElement) ds.getExistingByUid(DataElement.class, xde.getUid(), is);
			if (de==null) {
				de = new DataElement(xde.getName(),xde.getCode(),xde.getUid());
				de.setIntegrationServer(is);
				de.setCohortDefinitionUuid(ds.getUndefinedCohortDefinition().getUuid());

				ds.saveDataElement(de);
			}
		}				

	// process report definitions
		for (ReportTemplates.ReportTemplate xrt : getReportTemplates()) {
			ReportTemplate rt = (ReportTemplate) ds.getExistingByUid(ReportTemplate.class, xrt.getUid(), is);
			if (rt==null) {
				rt = new ReportTemplate(xrt.getName(),xrt.getCode(),xrt.getUid());
				rt.setIntegrationServer(is);
				rt.setFrequency(xrt.getPeriodType());
				rt = ds.saveReportTemplate(rt);
			}
			for (ReportTemplates.ReportTemplate.DataValueTemplates.DataValueTemplate xdv : xrt.getDataValueTemplates().getDataValueTemplate()) {
				DataValueTemplate dv = new DataValueTemplate();
				dv.setReportTemplate(rt);
				dv.setIntegrationServer(is);
				DataElement de = ds.getDataElementByCode(xdv.getDataElement(),is);
				if (de==null) {
					de = ds.getDataElementByUid(xdv.getDataElement(),is);
				}
				if (de!=null) {
					if (!rt.getDataElements().contains(de)) {
						rt.getDataElements().add(de);
					}
					if (!de.getReportTemplates().contains(rt)) {
						de.getReportTemplates().add(rt);
					}
					dv.setDataElement(de);
					dv.setCategoryOption(ds.getCategoryOptionByUid(xdv.getDisaggregation(),is));
					dv=ds.saveDataValueTemplate(dv);
					rt.getDataValueTemplates().add(dv);
					if (de.getCategoryCombo() == null) {
						CategoryOption co = ds.getCategoryOptionByUid(xdv.getDisaggregation(),is);
						if (co!=null) {
							Iterator<CategoryCombo> it = co.getCategoryCombos().iterator();
							CategoryCombo cb=it.next();
							if (cb != null) {
								de.setCategoryCombo(cb);
							}
						}
					}
				}
			}
		}

		// find the catcombo for the data element 
		// note this is done across reports in case reports are divided by dimension values
		List<DataElement> des = ds.getDataElementsByServer(is);
		for (DataElement de : des) {
			Set<CategoryCombo> ccs = new HashSet<CategoryCombo>();
			Set<CategoryOption> cos = new HashSet<CategoryOption>();
			List<DataValueTemplate> dvts = ds.getDataValueTemplateByDataElement(de);
			for (DataValueTemplate dvt : dvts) {
				ccs.addAll(dvt.getCategoryOption().getCategoryCombos());
				cos.add(dvt.getCategoryOption());
			}
			Set<CategoryCombo> ncs = new HashSet<CategoryCombo>();
			for (CategoryCombo cc : ccs) {
				Set<CategoryOption> tco = new HashSet<CategoryOption>(cos);
				tco.removeAll(cc.getCategoryOptions());
				if (!tco.isEmpty()) {
					ncs.add(cc);
				}
			}	
			ccs.removeAll(ncs);
			CategoryCombo mincc=null;
			for (CategoryCombo cc: ccs) {
				if (mincc==null) {
					mincc=cc;
				} else if (cc.getCategoryOptions().size()<mincc.getCategoryOptions().size()) {
					mincc=cc;
				}
			}
			de.setCategoryCombo(mincc);
			ds.saveDataElement(de);
		}
		return;
	}
	

	// basic object methods

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return hash.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return false;
	}

	// getters for the underlying components -- no setters
	
	public IntegrationServer getServer() {
		return is;
	}

	public String getName() {
		return name;
	}
	
	public ReportTemplates getMaster() {
		return master;
	}

	public List<ReportTemplates.DataElements.DataElement> getDataElements() {
		if (master==null) {
			return null;
		}
		if (master.getDataElements()==null) {
			return null;
		}
		return master.getDataElements().getDataElement();
	}

	public List<ReportTemplates.Disaggregations.Disaggregation> getDisaggregations() {
		if (master==null) {
			return null;
		}
		if (master.getDisaggregations()==null) {
			return null;
		}
		return master.getDisaggregations().getDisaggregation();
	}

	public List<ReportTemplates.ReportTemplate> getReportTemplates() {
		if (master==null) {
			return null;
		}
		return master.getReportTemplate();
	}

	public List<CategoriesType.Category> getOpts() {
		if (opts==null) {
			return null;
		}
		if (opts.getCategories()==null) {
			return null;
		}
		return opts.getCategories().getCategory();
	}

	public List<CategoryCombosType.CategoryOptionCombo> getCats() {
		if (cats==null) {
			return null;
		}
		if (cats.getCategoryOptionCombos()==null) {
			return null;
		}
		return cats.getCategoryOptionCombos().getCategoryOptionCombo();
	}
	
	public List<OrgUnitType.OrganisationUnit> getOrgs() {
		if (orgs==null) {
			return null;
		}
		if (orgs.getOrganisationUnits()==null) {
			return null;
		}
		return orgs.getOrganisationUnits().getOrganisationUnit();
	}
	
	/**
	 * 	This method prepares the org unit display.
	 *  The whole process to do so:
	 *      ServerMetadata sm = new ServerMetadata(...) // or use existing
	 *      sm.getOrgUnits(); // if not already done
	 *      sm.prepareOrgUnitDisplay(); 
	 *      SortedSet ss = OrgUnitDisplay.getAllHierarchical();
	 */
	public void prepareOrgUnitDisplay() {
		OrgUnitDisplay.Reset();
		if (orgs==null) return;
		if (orgs.getOrganisationUnits()==null) return;
		for (OrgUnitType.OrganisationUnit org : orgs.getOrganisationUnits().getOrganisationUnit()) {
			OrgUnitDisplay d = new OrgUnitDisplay(org.getName(),org.getCode(),org.getId());
			d.setLevel(org.getLevel().intValue());
			d.setServer(this.getName());
			if (org.getParent()!=null) {
				d.setParent(org.getParent().getName(), org.getParent().getCode(), org.getParent().getId(), d.getUid());
			}
		}
	}

	/**
	 * Updates DB objects for an existing server by
	 * parsing xml and traversing metadata
	 *  
	 * @param name the name of the server to be built
	 */
	public List<ChangeRecord> updateServer(String name) throws IntegrationException {
		log.info("In updateServer");
		this.name=name;
		is=ds.getIntegrationServerByName(name);
		if (is==null) {
			is=new IntegrationServer();
			is.setServerName(name);
			ds.saveIntegrationServer(is);
		}
 
		try {
			master = DhisMetadataUtils.UnmarshalMaster("New", name);
			cats = DhisMetadataUtils.UnmarshalMetaData(ContentType.CATS,"New", name);
			opts = DhisMetadataUtils.UnmarshalMetaData(ContentType.OPTS,"New", name);
		} catch (IntegrationException ie) {
			throw ie;
		} catch (Exception e) {
			throw new IntegrationException(e.getMessage(),e);
		}
		
		String result = "";
		changes = new ArrayList<ChangeRecord>();
		reports = ds.getReportTemplatesByServer(is);
		dataElements = ds.getDataElementsByServer(is);
		catCombos = ds.getCategoryComboByServer(is);
		catOptions = ds.getCategoryOptionByServer(is);
		optionSets = ds.getOptionSetsByServer(is);
		options = ds.getOptionsByServer(is);
		
		updateOpts();
		updateCats();
		updateMaster();
		
		log.info(dumpChanges());
		return changes;
	}

	private String dumpChanges() {
		final String CRLF = "\n\r";
		StringBuffer sb = new StringBuffer();
		sb.append(changes.size() + " changes");
		sb.append(CRLF);
		for (ChangeRecord cr : changes) {
			sb.append(cr.objClass + " " + cr.uid + "\t");
			sb.append(cr.name + "\t");
			sb.append(cr.code + "\t");
			sb.append(cr.oldName + "\t");
			sb.append(cr.oldCode + "\t");
			sb.append(cr.newFreq + CRLF);
		}
		return sb.toString();
	}
	
	private void updateOpts() {
		ChangeRecord cr=null;
		Set<Option> setOptions;
		for (CategoriesType.Category xcat : getOpts()) {
			OptionSet ops = (OptionSet) ds.getExistingByUid(OptionSet.class, xcat.getId(), is);
			if (ops==null) {
				ops = new OptionSet(xcat.getName(),"",xcat.getId());
				ops.setIntegrationServer(is);
				ops = ds.saveOptionSet(ops);
				cr = new ChangeRecord();
				cr.objClass = OptionSet.class.getSimpleName();
				cr.name = ops.getName();
				cr.code = ops.getCode();
				cr.uid = ops.getUid();
				cr.change = ChangeType.ADD;
				changes.add(cr);
				setOptions = null;
			} else {
				setOptions= new HashSet(ops.getOptions());
				optionSets.remove(ops);
				cr = new ChangeRecord();
				cr.objClass = OptionSet.class.getSimpleName();
				cr.uid = ops.getUid();
				if (!ops.getName().equals(xcat.getName())) {
					cr.oldName = ops.getName();
					cr.name = xcat.getName();
					ops.setName(xcat.getName());
					cr.change = ChangeType.CHANGE;
				} else {
					cr.name=ops.getName();
				}
				if (cr.change!=null) {
					changes.add(cr);
					ops = ds.saveOptionSet(ops);
				}
			}
			for (CategoriesType.Category.CategoryOptions.CategoryOption xco : xcat.getCategoryOptions().getCategoryOption()) {
				Option opv = (Option) ds.getExistingByUid(Option.class, xco.getId(), is);
				if (opv==null) {
					opv = new Option(xco.getName(),"",xco.getId());
					opv.setIntegrationServer(is);
					opv.setCohortdefUuid(ds.getUndefinedCohortDefinition().getUuid());
					opv = ds.saveOption(opv);
					cr = new ChangeRecord();
					cr.objClass = Option.class.getSimpleName();
					cr.name = opv.getName();
					cr.code = opv.getCode();
					cr.uid = opv.getUid();
					cr.change = ChangeType.ADD;
					changes.add(cr);
				} else {
					options.remove(opv);
					cr = new ChangeRecord();
					cr.objClass = Option.class.getSimpleName();
					cr.uid = opv.getUid();
					if (!opv.getName().equals(xco.getName())) {
						cr.oldName = opv.getName();
						cr.name = xco.getName();
						opv.setName(xco.getName());
						cr.change = ChangeType.CHANGE;
					} else {
						cr.name=opv.getName();
					}
					if (cr.change!=null) {
						changes.add(cr);
						opv = ds.saveOption(opv);
					}
				}
				if (cr.change==ChangeType.ADD) {
					ops.getOptions().add(opv);
					opv.getOptionSets().add(ops);
					opv=ds.saveOption(opv);
				}
			}
			ops=ds.saveOptionSet(ops);
			if (setOptions!=null) {
				if (!ops.getOptions().containsAll(setOptions) || !setOptions.containsAll(ops.getOptions())) {
					boolean found = false;
					for (ChangeRecord crt : changes) {
						if (crt.objClass.equals(OptionSet.class.getSimpleName()) && crt.uid.equals(ops.getUid()) && crt.change.equals(ChangeType.ADD)) {
							found = true;
							break;
						}
					}
					if (!found) {
						cr=new ChangeRecord();
						cr.objClass=OptionSet.class.getSimpleName();
						cr.name=ops.getName();
						cr.code=ops.getCode();
						cr.uid=ops.getUid();
						cr.change=ChangeType.REVISE;
						changes.add(cr);
					}
				}
			}
		}
		for (Option opv : options) {
			cr = new ChangeRecord();
			cr.objClass=Option.class.getSimpleName();
			cr.name=opv.getName();
			cr.code=opv.getCode();
			cr.uid=opv.getUid();
			cr.change=ChangeType.DELETE;
			changes.add(cr);
			opv.getOptionSets().clear();
			opv.getCategoryOptions().clear();
			ds.deleteOption(opv);
		}
		for (OptionSet ops : optionSets) {
			cr = new ChangeRecord();
			cr.objClass=OptionSet.class.getSimpleName();
			cr.name=ops.getName();
			cr.code=ops.getCode();
			cr.uid=ops.getUid();
			cr.change=ChangeType.DELETE;
			changes.add(cr);
			ops.getOptions().clear();
			ops.getCategoryCombos().clear();
			ds.deleteOptionSet(ops);
		}
		return;
	}
	
	private void updateCats() {
		ChangeRecord cr;
		ChangeRecord crx;
		Set setOptions;
	// step through the category options
		for (CategoryCombosType.CategoryOptionCombo xcoc : getCats()) {
			
	// build the category combo if necessary
			CategoryCombosType.CategoryOptionCombo.CategoryCombo xcc = xcoc.getCategoryCombo();
			CategoryCombo cc = (CategoryCombo) ds.getExistingByUid(CategoryCombo.class, xcc.getId(), is);
			if (cc==null) {
				cc = new CategoryCombo(xcc.getName(),"",xcc.getId());
				cc.setIntegrationServer(is);
				cc = ds.saveCategoryCombo(cc);
				cr = new ChangeRecord();
				cr.objClass = CategoryCombo.class.getSimpleName();
				cr.name = cc.getName();
				cr.code = cc.getCode();
				cr.uid = cc.getUid();
				cr.change = ChangeType.ADD;
				changes.add(cr);
				setOptions=null;
			} else {
				setOptions= new HashSet<CategoryOption>(cc.getCategoryOptions());
				catCombos.remove(cc);
				cr = new ChangeRecord();
				cr.objClass = CategoryCombo.class.getSimpleName();
				cr.uid = cc.getUid();
				if (!cc.getName().equals(xcc.getName())) {
					cr.oldName = cc.getName();
					cr.name = xcc.getName();
					cc.setName(xcc.getName());
					cr.change = ChangeType.CHANGE;
				} else {
					cr.name=cc.getName();
				}
				if (cr.change!=null) {
					changes.add(cr);
					cc = ds.saveCategoryCombo(cc);
				}
			}
			
	// build the category option if necessary
			CategoryOption co = (CategoryOption) ds.getExistingByUid(CategoryOption.class, xcoc.getId(), is);
			if (co==null) {
				co = new CategoryOption(xcoc.getName(),"",xcoc.getId());
				co.setIntegrationServer(is);
				co = ds.saveCategoryOption(co);
				crx = new ChangeRecord();
				crx.objClass = CategoryOption.class.getSimpleName();
				crx.name = co.getName();
				crx.code = co.getCode();
				crx.uid = co.getUid();
				crx.change = ChangeType.ADD;
				changes.add(crx);
			} else {
				catOptions.remove(co);
				crx = new ChangeRecord();
				crx.objClass = CategoryOption.class.getSimpleName();
				crx.uid = co.getUid();
				if (!co.getName().equals(xcoc.getName())) {
					crx.oldName = co.getName();
					crx.name = xcoc.getName();
					co.setName(xcoc.getName());
					crx.change = ChangeType.CHANGE;
				} else {
					crx.name=co.getName();
				}
				if (crx.change!=null) {
					changes.add(crx);
					co = ds.saveCategoryOption(co);
				}
			}
			
	// add the category combo and category option to each other's collections
			if (crx.change==ChangeType.ADD) {
				cc.getCategoryOptions().add(co);
				co.getCategoryCombos().add(cc);
	// add the options and category options to each other's collections
				for (CategoryCombosType.CategoryOptionCombo.CategoryOptions.CategoryOption xov : xcoc.getCategoryOptions().getCategoryOption()) {
					Option ov = ds.getOptionByUid(xov.getId(), is);
					if (ov!=null) {
						co.getOptions().add(ov);
						ov.getCategoryOptions().add(co);
						ov = ds.saveOption(ov);
					}
				}
			}
			
	// save the possibly updated category combo and category option
			ds.saveCategoryCombo(cc);
			ds.saveCategoryOption(co);
			if (setOptions!=null) {
				if (!cc.getCategoryOptions().containsAll(setOptions) || !setOptions.containsAll(cc.getCategoryOptions())) {
					boolean found = false;
					for (ChangeRecord crt : changes) {
						if (crt.objClass.equals(CategoryCombo.class.getSimpleName()) && crt.uid.equals(cc.getUid()) && crt.change.equals(ChangeType.ADD)) {
							found = true;
							break;
						}
					}
					if (!found) {
						cr=new ChangeRecord();
						cr.objClass=CategoryCombo.class.getSimpleName();
						cr.name=cc.getName();
						cr.code=cc.getCode();
						cr.uid=cc.getUid();
						cr.change=ChangeType.REVISE;
						changes.add(cr);
					}
				}
			}
		}

	// find the option sets for the category combos
	// -- from the options in the category options in the catcombo, get all possible option sets
		List<CategoryCombo> ccs = ds.getCategoryComboByServer(is);
		for (CategoryCombo cc : ccs) {
			if (!catCombos.contains(cc)) {
				Set<OptionSet> oss = new HashSet<OptionSet>();
				for (CategoryOption co : cc.getCategoryOptions()) {
					for (Option op : co.getOptions()) {
						oss.addAll(op.getOptionSets());
					}
				}
		// -- eliminate those which are not capable of supporting all category options of the cc
				Set<OptionSet> nos = new HashSet<OptionSet>();
				for (OptionSet os : oss) {
					for (CategoryOption co : cc.getCategoryOptions()) {
						Set<Option> tos = new HashSet<Option>(co.getOptions());
						tos.retainAll(os.getOptions());
						if (tos.isEmpty()) {
							nos.add(os);
							break;
						}
					}
				}
				oss.removeAll(nos);
	
		// -- get the options of option sets usable by the cc
				Map<OptionSet,Set<Option>> osmap = new HashMap<OptionSet,Set<Option>>();
				for (CategoryOption co : cc.getCategoryOptions()) {
					for (OptionSet os : oss) {
						osmap.put(os, os.getOptions());
					}
				}
		// -- build in tos the set of all option sets containing at least one option in os.options
				nos = new HashSet<OptionSet>();
				for (OptionSet os : osmap.keySet()) {
					if (!nos.contains(os)) {
						Set<OptionSet> tos = new HashSet<OptionSet>();
						for (OptionSet os2 : osmap.keySet()) {
							if (os2.equals(os)) {
								tos.add(os2);
							} else if (!tos.isEmpty() && !nos.contains(os2)) {
								Set<Option> tov = new HashSet<Option>(os2.getOptions());
								tov.retainAll(os.getOptions());
								if (!tov.isEmpty()) {
									tos.add(os2);
								}
							}
						}
			// -- use the smallest set
						if (tos.size()>1) {
							OptionSet minops = os;
							for (OptionSet os2 : tos) {
								if (os2.getOptions().size()<minops.getOptions().size()) {
									minops = os2;
								}
							}
							tos.remove(minops);
							nos.addAll(tos);
						}
					}
				}
				for (OptionSet os : nos) {
					osmap.remove(os);
				}
				if (!cc.getOptionSets().isEmpty()) {
					if (!cc.getOptionSets().containsAll(osmap.keySet()) || !osmap.keySet().containsAll(cc.getOptionSets())) {
						boolean found = false;
						for (ChangeRecord crt : changes) {
							if (crt.objClass.equals(CategoryCombo.class.getSimpleName()) && crt.uid.equals(cc.getUid()) && crt.change.equals(ChangeType.ADD)) {
								found = true;
								break;
							}
						}
						if (!found) {
							cr = new ChangeRecord();
							cr.objClass=CategoryCombo.class.getSimpleName();
							cr.name=cc.getName();
							cr.code=cc.getCode();
							cr.uid=cc.getUid();
							cr.change=ChangeType.REVISE;
						}
					}
				}
				cc.setOptionSets(osmap.keySet());
				ds.saveCategoryCombo(cc);
			}
		}
				// delete the unmatched objects
		for (CategoryOption opv : catOptions) {
			cr = new ChangeRecord();
			cr.objClass=CategoryOption.class.getSimpleName();
			cr.name=opv.getName();
			cr.code=opv.getCode();
			cr.uid=opv.getUid();
			cr.change=ChangeType.DELETE;
			changes.add(cr);
			opv.getCategoryCombos().clear();
			opv.getOptions().clear();
			ds.deleteCategoryOption(opv);
		}
		for (CategoryCombo ops : catCombos) {
			cr = new ChangeRecord();
			cr.objClass=CategoryCombo.class.getSimpleName();
			cr.name=ops.getName();
			cr.code=ops.getCode();
			cr.uid=ops.getUid();
			cr.change=ChangeType.DELETE;
			changes.add(cr);
			ops.getCategoryOptions().clear();
			ops.getOptionSets().clear();
			List<DataElement> des = ds.getDataElementsByServer(is);
			for (DataElement de : des) {
				if (ops.equals(de.getCategoryCombo())) {
					de.setCategoryCombo(null);
					ds.saveDataElement(de);
					crx = new ChangeRecord();
					crx.objClass=DataElement.class.getSimpleName();
					crx.name=de.getName();
					crx.code=de.getCode();
					crx.uid=de.getUid();
					crx.change=ChangeType.REVISE;
					crx.oldName=ops.getName();
					changes.add(crx);
				}
			}
			ds.deleteCategoryCombo(ops);
		}

		return;
	}
	private void updateMaster() {
		ChangeRecord cr;
		ChangeRecord crx;
	// add codes to disaggregations
		for (ReportTemplates.Disaggregations.Disaggregation xd : getMaster().getDisaggregations().getDisaggregation()) {
			CategoryOption co = ds.getCategoryOptionByUid(xd.getUid(), is);
			if (co != null) {
				co.setCode(xd.getCode());
				ds.saveCategoryOption(co);
			}
		}
			
	// process data elements
		for (ReportTemplates.DataElements.DataElement xde : getDataElements()) {
			DataElement de = (DataElement) ds.getExistingByUid(DataElement.class, xde.getUid(), is);
			if (de==null) {
				de = new DataElement(xde.getName(),xde.getCode(),xde.getUid());
				de.setIntegrationServer(is);
				de.setCohortDefinitionUuid(ds.getUndefinedCohortDefinition().getUuid());
				de = ds.saveDataElement(de);
				cr = new ChangeRecord();
				cr.objClass = DataElement.class.getSimpleName();
				cr.name = de.getName();
				cr.code = de.getCode();
				cr.uid = de.getUid();
				cr.change = ChangeType.ADD;
				changes.add(cr);
			} else {
				dataElements.remove(de);
				cr = new ChangeRecord();
				cr.objClass = DataElement.class.getSimpleName();
				cr.uid = de.getUid();
				if (!de.getName().equals(xde.getName())) {
					cr.oldName = de.getName();
					cr.name = xde.getName();
					de.setName(xde.getName());
					cr.change = ChangeType.CHANGE;
				} else {
					cr.name=de.getName();
				}
				if (!"".equals(xde.getCode()) && !de.getCode().equals(xde.getCode())) {
					cr.oldCode = de.getCode();
					cr.code = xde.getCode();
					de.setCode(xde.getCode());
					cr.change = ChangeType.CHANGE;
				} else {
					cr.code=de.getCode();
				}
				if (cr.change!=null) {
					changes.add(cr);
					de = ds.saveDataElement(de);
				}
			}
		}

// process report definitions
		for (ReportTemplates.ReportTemplate xrt : getReportTemplates()) {
			ReportTemplate rt = (ReportTemplate) ds.getExistingByUid(ReportTemplate.class, xrt.getUid(), is);
			if (rt==null) {
				rt = new ReportTemplate(xrt.getName(),xrt.getCode(),xrt.getUid());
				rt.setIntegrationServer(is);
				rt.setFrequency(xrt.getPeriodType());
				rt = ds.saveReportTemplate(rt);
				cr = new ChangeRecord();
				cr.objClass = ReportTemplate.class.getSimpleName();
				cr.name = rt.getName();
				cr.code = rt.getCode();
				cr.uid = rt.getUid();
				cr.change = ChangeType.ADD;
				changes.add(cr);
			} else {
				reports.remove(rt);
				cr = new ChangeRecord();
				cr.objClass = ReportTemplate.class.getSimpleName();
				cr.uid = rt.getUid();
				if (!rt.getName().equals(xrt.getName())) {
					cr.oldName = rt.getName();
					cr.name = xrt.getName();
					rt.setName(xrt.getName());
					cr.change = ChangeType.CHANGE;
				} else {
					cr.name=rt.getName();
				}
				if (!"".equals(xrt.getCode()) && !rt.getCode().equals(xrt.getCode())) {
					cr.oldCode = rt.getCode();
					cr.code = xrt.getCode();
					rt.setCode(xrt.getCode());
					cr.change = ChangeType.CHANGE;
				} else {
					cr.code=rt.getCode();
				}
				if (!rt.getFrequency().equals(xrt.getPeriodType())) {
					cr.newFreq = xrt.getPeriodType();
					rt.setFrequency(xrt.getPeriodType());
					cr.change = ChangeType.CHANGE;
				}
				if (cr.change!=null) {
					changes.add(cr);
					rt = ds.saveReportTemplate(rt);
				}

			}
// Replace all old data value templates
			List<DataValueTemplate> dvt = ds.getDataValueTemplateByReportTemplate(rt);
			rt.getDataValueTemplates().clear();
			for (DataValueTemplate dv : dvt) {
				ds.deleteDataValueTemplate(dv);
			}
			for (ReportTemplates.ReportTemplate.DataValueTemplates.DataValueTemplate xdv : xrt.getDataValueTemplates().getDataValueTemplate()) {
				DataValueTemplate dv = new DataValueTemplate();
				dv.setReportTemplate(rt);
				dv.setIntegrationServer(is);
				DataElement de = ds.getDataElementByCode(xdv.getDataElement(),is);
				if (de==null) {
					de = ds.getDataElementByUid(xdv.getDataElement(),is);
				}
				if (de!=null) {
					if (!rt.getDataElements().contains(de)) {
						rt.getDataElements().add(de);
					}
					if (!de.getReportTemplates().contains(rt)) {
						de.getReportTemplates().add(rt);
					}
					dv.setDataElement(de);
					dv.setCategoryOption(ds.getCategoryOptionByUid(xdv.getDisaggregation(),is));
					dv=ds.saveDataValueTemplate(dv);
					rt.getDataValueTemplates().add(dv);
				}
			}
		}
		
		// delete unmatched data elements
		for (DataElement de : dataElements) {
			cr = new ChangeRecord();
			cr.objClass=DataElement.class.getSimpleName();
			cr.name=de.getName();
			cr.code=de.getCode();
			cr.uid=de.getUid();
			cr.change=ChangeType.DELETE;
			changes.add(cr);
			ds.deleteDataElement(de);
		}

		// find the catcombo for the data element 
		// note this is done across reports in case reports are divided by dimension values
		List<DataElement> des = ds.getDataElementsByServer(is);
		for (DataElement de : des) {
			Set<CategoryCombo> ccs = new HashSet<CategoryCombo>();
			Set<CategoryOption> cos = new HashSet<CategoryOption>();
			List<DataValueTemplate> dvts = ds.getDataValueTemplateByDataElement(de);
			for (DataValueTemplate dvt : dvts) {
				ccs.addAll(dvt.getCategoryOption().getCategoryCombos());
				cos.add(dvt.getCategoryOption());
			}
			Set<CategoryCombo> ncs = new HashSet<CategoryCombo>();
			for (CategoryCombo cc : ccs) {
				Set<CategoryOption> tco = new HashSet<CategoryOption>(cos);
				tco.removeAll(cc.getCategoryOptions());
				if (!tco.isEmpty()) {
					ncs.add(cc);
				}
			}	
			ccs.removeAll(ncs);
			CategoryCombo mincc=null;
			for (CategoryCombo cc: ccs) {
				if (mincc==null) {
					mincc=cc;
				} else if (cc.getCategoryOptions().size()<mincc.getCategoryOptions().size()) {
					mincc=cc;
				}
			}
			if (!mincc.equals(de.getCategoryCombo())) {
				if (de.getCategoryCombo()!=null) {
					cr = new ChangeRecord();
					cr.objClass = DataElement.class.getSimpleName();
					cr.name = de.getName();
					cr.code = de.getCode();
					cr.uid = de.getUid();
					cr.change = ChangeType.REVISE;
					cr.newFreq = mincc.getName();
					changes.add(cr);
				}
				de.setCategoryCombo(mincc);
				ds.saveDataElement(de);
			}
		}

		for (ReportTemplate rt : reports) {
			cr = new ChangeRecord();
			cr.objClass=ReportTemplate.class.getSimpleName();
			cr.name=rt.getName();
			cr.code=rt.getCode();
			cr.uid=rt.getUid();
			cr.change=ChangeType.DELETE;
			changes.add(cr);
			List<DataValueTemplate> dvt = ds.getDataValueTemplateByReportTemplate(rt);
			for (DataValueTemplate dv : dvt) {
				rt.getDataValueTemplates().remove(dv);
				ds.deleteDataValueTemplate(dv);
			}
			List<DataElement> del = new ArrayList<DataElement>(rt.getDataElements());
			rt.getDataElements().clear();
			for (DataElement de : del) {
				ds.deleteDataElement(de);
			}
			ds.deleteReportTemplate(rt);
		}
		
		return;
	}
	

}
