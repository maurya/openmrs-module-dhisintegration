package org.openmrs.module.dhisintegration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import org.openmrs.api.context.Context;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.dhisintegration.api.db.DhisMetadataUtils.ContentType;
import org.openmrs.module.dhisintegration.api.impl.CodeGenerator;
import org.openmrs.util.OpenmrsUtil;

/**
 * @author rfriedman
 *
 */

public class OrgUnitDisplay {
	private static Map<String,OrgUnitDisplay> index = new HashMap<String,OrgUnitDisplay>();
	private static Map<String,Set<String>> parentChild = new HashMap<String,Set<String>>();
	private static String MODULE_NAME = "Dhis Integration";

	private String uid;
	private String name;
	private String code;
	private String parent;
	private Integer level;
	private String qualifiedName;
	private String server;
	
// Constructor
	
	/**
	 * This is the constructor for the class
	 * It assures that there is a name, code and uid for each member
	 * It puts the newly created member in the index
	 * 
	 */
	public OrgUnitDisplay(String dhisName, String dhisCode, String dhisUid) {
		if (isNullOrEmpty(dhisUid)) {
			this.uid=CodeGenerator.generateCode();
		} else {
			this.uid=dhisUid;
		}
		
		if (isNullOrEmpty(dhisCode)) {
			this.code=uid;
		} else {
			this.code=dhisCode;
		}
		
		if (isNullOrEmpty(dhisName)) {
			this.name=code;
		} else {
			this.name=dhisName;
		}
		index.put(uid, this);
	}

	private static Boolean isNullOrEmpty(String s) {
		if (s==null)
			return true;
		else if (s.length()==0)
			return true;
		return false;
	}

// Object methods
	
	@Override
	public String toString() {
		return uid;
	}

	@Override
	public int hashCode() {
		return uid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (! (obj instanceof OrgUnitDisplay))
			return false;
		OrgUnitDisplay o = (OrgUnitDisplay) obj;
		return buildQualifiedName(this.getUid()).equals(buildQualifiedName(o.getUid()));
	}

//	Getters
	
	public String getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public String getParent() {
		return parent;
	}

	public Integer getLevel() {
		return level;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}
	
	/**
	 * This method gets the server of the org unit
	 * This is a hack, as every org unit will have the same server
	 * The static method getServerName returns the server of the first org unit
	 * 
	 * @result server name
	 */
	public String getServer() {
		return server;
	}

//	Setters
	
	public void setServer(String serverName) {
		server=serverName;
	}
	
	public void setLevel(Integer level) {
		
		if (level<1 || level>9) {
			this.level=null;
		} else {
			this.level=level;
		}
	}
	
	/**
	 * This method sets the parent of the org unit
	 * If the parent does not exist, it will be created unless both name and uid are empty
	 * The parentChild index is updated
	 * 
	 * @param name	name of the parent
	 * @param code	code of the parent
	 * @param uid	uid of the parent
	 */
	public void setParent(String name, String code, String uid, String child) {
		
		if (isNullOrEmpty(uid)) {
			this.parent=null;
			return;
		}
		this.parent = uid;
		
		Set<String> children = parentChild.get(uid);
		if (children==null) {
			children = new HashSet<String>();
		} 
		children.add(child);
		parentChild.put(uid, children);
	}
	
	private void setQualifiedName(String s) {
		qualifiedName=s;
	}
	
	
//	Static methods
	
	public static Map<String,OrgUnitDisplay> getIndex() {
		return index;
	}
	
	/**
	 * This method gets the server which the class is currently holding 
	 * The instance method getServer returns the server of an org unit
	 * This is a hack, as every org unit will have the same server
	 * 
	 * @result server name
	 */
	public static String getServerName() {
		if (index.isEmpty()) return null;
		return index.get(0).getServer();
	}

	/**
	 * This method builds the qualified name for an OrgUnit
	 * The qualified name is the name of each org unit in the hierarchy, root first,
	 * separated by tab
	 * 
	 * @param uid	the uid of the org unit for which to build the name
	 * @return	the qualified name
	 */
	private static String buildQualifiedName(String uid) {
		String s = index.get(uid).getName();
		String u = index.get(uid).getParent();
		if (u==null) return s;
		do {
			s=index.get(u).getName() + "\t" + s;
			u=index.get(u).getParent();
		} while (u!=null);
		return s;
	}
	
	/**
	 * This method sets the qualified name of all org units in the index
	 * It returns them in hierarchy order, root first
	 * 
	 * @return sorted set of org unit displays
	 */
	public static SortedSet<OrgUnitDisplay> getAllHierarchical() {
		for (String uid : index.keySet()) {
			index.get(uid).setQualifiedName(buildQualifiedName(uid));
		}
		SortedSet<OrgUnitDisplay> output = new TreeSet(new HierarchicalComparator());
		output.addAll(index.values());
		return output;
	}
	
	/**
	 * This method generates the org unit tree in jTree XML format
	 * It calls getAllHierarchical
	 * 
	 * @result	string version of xml for tree
	 */
	public static String getAllOrgsAsXml() {
		final String CRLF="\r\n";
		StringBuffer sb = new StringBuffer();
		Stack<String> uids = new Stack<String>();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		sb.append(CRLF);
		sb.append("<root>");
		sb.append(CRLF);

		SortedSet<OrgUnitDisplay> orgs=getAllHierarchical();
		
		for (OrgUnitDisplay org : orgs) {
// use the flat style			

// start the new item
			sb.append("<item id=\"");
			sb.append(org.getUid());
			sb.append("\" parent_id=\"");
			if (org.getParent()==null) {
				sb.append("0");
			} else {
				sb.append(org.getParent());
			}
			sb.append("\" >");
			sb.append(CRLF);

// put in the payload
			sb.append("<content>");
			sb.append(CRLF);
			sb.append("<name ><![CDATA[");
			sb.append(org.getName());
			sb.append("]]></name>");
			sb.append(CRLF);
			sb.append("</content>");
			sb.append(CRLF);
			sb.append("</item>");
			sb.append(CRLF);
		}
		sb.append("</root>");
		sb.append(CRLF);
		return sb.toString();
	}

	/**
	 * This method generates the org unit tree in jTree Json format
	 * It calls getAllHierarchical
	 * 
	 * @result	string version of xml for tree
	 */
	public static String getAllOrgsAsJson() {
		final String CRLF="\r\n";
		StringBuffer sb = new StringBuffer();
		Stack<String> uids = new Stack<String>();
		sb.append("\"json_data\": {");
		sb.append(CRLF);
		sb.append("\"data\": [");
		sb.append(CRLF);

		SortedSet<OrgUnitDisplay> orgs=getAllHierarchical();
		
		for (OrgUnitDisplay org : orgs) {
			try{
	// close out previous level(s) if necessary
				do {
					if (uids.empty()) break;
					if (org.getParent()!=null)
						if (org.getParent().equals(uids.peek())) break;	
					sb.append(" ] }");
					sb.append(CRLF);
					if (!uids.isEmpty()) 
						uids.pop();
				} while (true);
				

	// see if we are beginning an array, if not insert a comma
				if (sb.lastIndexOf("[")<sb.lastIndexOf("}")) {
					sb.append(",");
					sb.append(CRLF);
				}

	// create the data element
				sb.append("{ \"data\": \"");
				sb.append(org.getName());
				sb.append("\", \"attr\": { \"id\": \"");
				sb.append(org.getUid());
				if (!isNullOrEmpty(org.getParent())) {
					sb.append("\" , \"parentId\": \"");
					sb.append(org.getParent());
				}
				sb.append("\" }");

	// if there are children, expect them and push their parent on the stack
				if (parentChild.get(org.getUid())!=null) {
					if (parentChild.get(org.getUid()).size()!=0) {
						sb.append(", \"children\" : [");
						sb.append(CRLF);
						uids.push(org.getUid());
					} else {
						sb.append(" }");
					}
				} else {
					sb.append(" }");
				}

			} catch (Exception e) {
				sb.append(CRLF);
				sb.append("==INSIDE ==================");
				sb.append(CRLF);
				sb.append(e.getMessage());
				sb.append(CRLF);
				sb.append(org.getUid());
				if (parentChild.get(org.getUid())== null) {
					sb.append("  no children");
				} else {
					sb.append("  " + parentChild.get(org.getUid()).size() + " chlidren");
				}
				sb.append(CRLF);
				sb.append("===========================");
				sb.append(CRLF);
			}
		}
		// close out previous level(s) if necessary
		try {
			do {
				if (uids.empty()) break;
				sb.append(" ] }");
				uids.pop();
			} while (true);
			sb.append(CRLF);
		} catch (Exception e) {
			sb.append(CRLF);
			sb.append("==OUTSIDE ==================");
			sb.append(CRLF);
			sb.append(e.getMessage());
			sb.append(CRLF);
			sb.append("============================");
			sb.append(CRLF);
		}
		sb.append("] }");
		sb.append(CRLF);
		return sb.toString();
	}
	
/**
 * This method builds a file object corresponding to an xml file 
 * 
 * @param meta	content type to be downloaded
 * @param subdir	name of subdir to be downloaded to (New, Current)
 * @param server	name of server to be downloaded to
 */
private static File getServerFile(ContentType meta, String subdir, String server) {
	final StringBuilder sb = new StringBuilder();
//	sb.append("/home/rfriedman/.OpenMRS/");
	sb.append(MODULE_NAME);
	sb.append(File.separatorChar);
	sb.append(server);
	sb.append(File.separatorChar);
	sb.append(subdir);
	File folder = OpenmrsUtil.getDirectoryInApplicationDataDirectory(sb.toString());
//	File folder = new File(sb.toString());
	return new File(folder, meta.toString().toLowerCase() + ".xml");
}
	
	
	/**
	 * This method compares the org units of a server with the org units in the index
	 * org units which do not exist in the index are returned 
	 * org units whose names have changed are set to the new name
	 * 
	 * @param server the server to be tested
	 * @return list of deleted org units
	 */
	public static List<OrgUnit> findDeletedOrgs(String server) {
		DhisService ds = Context.getService(DhisService.class);
		IntegrationServer is = ds.getIntegrationServerByName(server);
		if (is == null) return null;
		List<OrgUnit> orgs=ds.getOrgUnitByServer(is);
		List<OrgUnit> deletedOrgs = new ArrayList<OrgUnit>();
		for (OrgUnit org : orgs) {
			OrgUnitDisplay oud=index.get(org.getUid());
			if (oud==null) {
				deletedOrgs.add(org);
			} else if (!oud.getName().equals(org.getName())) {
				org.setName(oud.getName());
				ds.saveOrgUnit(org);
			}
		}
		return deletedOrgs;
	}
	
	/**
	 * This method clears the index
	 * It should be called when the module shuts down to ensure garbage collection
	 * 
	 */
	public static void Reset() {
		parentChild.clear();
		index.clear();
	}
	
	public static OrgUnitDisplay getOrgUnitByUid(String uid) {
		return index.get(uid);
	}
	
	private static class HierarchicalComparator implements Comparator<OrgUnitDisplay> {

		@Override
		public int compare(OrgUnitDisplay arg0, OrgUnitDisplay arg1) {
			return arg0.getQualifiedName().compareToIgnoreCase(arg1.getQualifiedName());
		}		
	}
	
	
}

