package org.openmrs.module.dhisintegration.api.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.OrgUnitDisplay;
import org.openmrs.module.dhisintegration.api.DhisService;
import org.openmrs.module.dhisintegration.api.JaxbObjects;
import org.openmrs.module.dhisintegration.api.jaxb.DataValueSet;
import org.openmrs.module.dhisintegration.api.jaxb.MetaData;
import org.openmrs.module.dhisintegration.api.jaxb.OrgUnitType;
import org.openmrs.module.dhisintegration.api.jaxb.ReportTemplates;
import org.openmrs.module.dhisintegration.api.jaxb.ImportSummary;
import org.openmrs.util.OpenmrsUtil;

public class DhisMetadataUtils {

	private static Log log = LogFactory.getLog(DhisMetadataUtils.class);
	private static String MODULE_NAME = "Dhis Integration";
	private static String NAMESPACE = "http://www.dhis.org/schema/dxf/2.0";

	private static String catsCall = "/api/categoryOptionCombos?viewClass=detailed&paging=false";
	private static String optsCall = "/api/categories?viewClass=export&paging=false";
	private static String orgsCall = "/api/organisationUnits?viewClass=export&paging=false";
	private static String rptsCall = "/api/dataSets";

	public static enum ContentType {
		MASTER, CATS, OPTS, ORGS
	}

	/**
	 * This method gets the 3 metadata files via the DHIS API into
	 * <application dir>/Dhis Integration/<server name>/New. The
	 * files are called master.xml, cats.xml, opts.xml.
	 * 
	 * @param server the server object which is to be accessed.
	 */
	public static void getServerMetadata(IntegrationServer server)
			throws IntegrationException {
		
		getDhisMetadataFromAPI(ContentType.MASTER, server);
		getDhisMetadataFromAPI(ContentType.CATS, server);
		getDhisMetadataFromAPI(ContentType.OPTS, server);
	}

	/**
	 * This method gets the 3 metadata files via resources in the project, it is
	 * intended primarily for testing.
	 * 
	 * @param master the resource to be used as the master xml
	 * @param cats the resource to be used as the cats xml
	 * @param opts the resource to be used as the opts xml
	 */
	public static void getServerMetadata(String master, String cats, String opts) throws IntegrationException {
		String name = master.endsWith(".xml") ? master.substring(0,master.length()-4) : master;
		
		getDhisMetadataFromResource(ContentType.MASTER, master, name);
		getDhisMetadataFromResource(ContentType.CATS, cats, name);
		getDhisMetadataFromResource(ContentType.OPTS, opts, name);
	}
	
	/**
	 * This method downloads a metadata xml file into the server's New subdirectory in application space
	 * 
	 * @param meta	content type to be downloaded
	 * @param is	server to be downloaded from
	 */
	public static void getDhisMetadataFromAPI(ContentType meta, IntegrationServer is) throws IntegrationException {
		URL url;
		String selector="";
		String accept="";
		
		switch (meta) {
		case MASTER:
			selector = rptsCall;
			accept = "application/dsd+xml";
			break;
		case CATS:
			selector = catsCall;
			accept = "application/xml";
			break;
		case OPTS:
			selector = optsCall;
			accept = "application/xml";
			break;
		case ORGS:
			selector = orgsCall;
			accept = "application/xml";
			break;
		}
		
		Credentials creds = new UsernamePasswordCredentials(
				is.getUserName(), is.getPassword());
		DefaultHttpClient httpclient = new DefaultHttpClient();
		BasicHttpContext localcontext = new BasicHttpContext();

//		Setup the GET
		try {
			url = new URL(is.getUrl());
			HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(),
					url.getProtocol());
			
			HttpGet httpGet = new HttpGet(is.getUrl() + selector);
			Header bs = new BasicScheme().authenticate(creds, httpGet,
					localcontext);
			httpGet.setHeader("Authorization", bs.getValue());
			httpGet.setHeader("Content-Type", "application/xml");
			httpGet.setHeader("Accept", accept);

//		GET the response			
			HttpResponse response = httpclient.execute(targetHost, httpGet,
					localcontext);
			HttpEntity entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new IntegrationException(response.getStatusLine()
						.getReasonPhrase(), null);
			} 
			
//		Copy the XML from the response to the application directory			
			if (entity != null) {
				File of = getServerFile(meta, "New", is.getServerName());
				OutputStream os = new FileOutputStream(of);
				IOUtils.copy(entity.getContent(), os);
			}

// 		Handle exceptions and close the connection			

		} catch (Exception e) {
			throw new IntegrationException(e.getMessage(),e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * This method downloads a resource xml file into the server's New subdirectory in application space
	 * 
	 * @param meta	content type to be downloaded
	 * @param resource	name of resource to be downloaded
	 * @param server	name of server to be downloaded from
	 */
	public static void getDhisMetadataFromResource(ContentType meta, String resource, String server) 
			throws IntegrationException {
		File of = getServerFile(meta, "New", server);
		try {
			OutputStream os = new FileOutputStream(of);
			InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(resource);
			IOUtils.copy(in, os);
		} catch (Exception e) {
			throw new IntegrationException(e.getMessage(),e);
		}
	}
	
	/**
	 * This method downloads an xml file into the server's New subdirectory in application space
	 * 
	 * @param meta	content type to be downloaded
	 * @param inFile	file object to be downloaded
	 * @param server	name of server to be downloaded from
	 */
	public static void getDhisMetadataFromFile(ContentType meta, File inFile, String server) 
			throws IntegrationException {
		File of = getServerFile(meta, "New", server);
		try {
			OutputStream os = new FileOutputStream(of);
			InputStream in = new FileInputStream(inFile);
			IOUtils.copy(in, os);
		} catch (Exception e) {
			throw new IntegrationException(e.getMessage(),e);
		}
	}
	
	/**
	 * This method builds a file object corresponding to a report xml file 
	 * 
	 * @param server	name of server to be downloaded to
	 * @param fileName	Name of report to be saved
	 * @param asOf	startDate of reporting period
	 * @param sent	sent date of report or null if not sent
	 * @return File object representing absolute location of file
	 */
	public static File getServerReportFile(String server, String report, Date asOf, Date sent) {
		final StringBuilder sb = new StringBuilder();
		sb.append(MODULE_NAME);
		sb.append(File.separatorChar);
		sb.append(server);
		sb.append(File.separatorChar);
		sb.append("Reports");
		
		File folder = OpenmrsUtil.getDirectoryInApplicationDataDirectory(sb.toString());
		DateFormat df = new SimpleDateFormat("yyyyMMdd");

		sb.delete(0, sb.length()-1);
		sb.append(report);
		sb.append(".");
		sb.append(df.format(asOf));
		if (sent!=null) {
			sb.append(".");
			sb.append(df.format(sent));
		}
		sb.append(".xml");

		return new File(folder, sb.toString());
	}
	
	/**
	 * This method builds a file object corresponding to a report xml file 
	 * 
	 * @param server	name of server to be downloaded to
	 * @param filname	Name of report to be saved
	 * @return File object representing absolute location of file
	 */
	public static File getServerReportFile(String server, String filename) {
		final StringBuilder sb = new StringBuilder();
		sb.append(MODULE_NAME);
		sb.append(File.separatorChar);
		sb.append(server);
		sb.append(File.separatorChar);
		sb.append("Reports");
		
		File folder = OpenmrsUtil.getDirectoryInApplicationDataDirectory(sb.toString());
		if (filename.endsWith(".xml")) {
			return new File(folder, filename);
		} else {
			return new File(folder, filename + ".xml");
		}
	}
	
	
	/**
	 * This method builds a file object corresponding to an xml file 
	 * 
	 * @param meta	content type to be downloaded
	 * @param subdir	name of subdir to be downloaded to (New, Current)
	 * @param server	name of server to be downloaded to
	 */
	public static File getServerFile(ContentType meta, String subdir, String server) {
		final StringBuilder sb = new StringBuilder();
		sb.append("/home/rfriedman/.OpenMRS/");
		sb.append(MODULE_NAME);
		sb.append(File.separatorChar);
		sb.append(server);
		sb.append(File.separatorChar);
		sb.append(subdir);
//		File folder = OpenmrsUtil.getDirectoryInApplicationDataDirectory(sb.toString());
		File folder = new File(sb.toString());
		return new File(folder, meta.toString().toLowerCase() + ".xml");
	}
	
	/**
	 * This method unmarshals a master.xml file in the server's subdir
	 * The passage through JAXBElement comes from 
	 * http://stackoverflow.com/questions/819720/no-xmlrootelement-generated-by-jaxb
	 * and is used because no root element is generated for MetaData (TODO: Still true?)
	 * 
	 * @param subdir	subdir of file to be unmarshaled from
	 * @param server	name of server to be unmarshaled form
	 */
	public static ReportTemplates UnmarshalMaster(String subdir, String server) throws IntegrationException {
		ReportTemplates user=null;
		try {
			File f=getServerFile(ContentType.MASTER,subdir,server);
			FileInputStream in = new FileInputStream(f);
			StreamSource ss = new StreamSource(in);
			JAXBElement<ReportTemplates> userElement = (JAXBElement<ReportTemplates>) JaxbObjects.getUM().unmarshal(ss,ReportTemplates.class);
			user = userElement.getValue();
		} catch (Exception e) {
			throw new IntegrationException(e.getMessage(),e);
		}
		return user;
	}
	
	/**
	 * This method unmarshals a metadata xml file in the server's subdir
	 * The passage through JAXBElement comes from 
	 * http://stackoverflow.com/questions/819720/no-xmlrootelement-generated-by-jaxb
	 * and is used because no root element is generated for MetaData TODO: Still true?
	 * 
	 * @param meta	content type to be downloaded
	 * @param subdir	subdir of file to be unmarshaled from
	 * @param server	name of server to be unmarshaled form
	 */
	public static MetaData UnmarshalMetaData(ContentType meta, String subdir, String server) throws IntegrationException {
		MetaData user=null;
		try {
			FileInputStream in = new FileInputStream(getServerFile(meta,subdir,server));
			StreamSource ss = new StreamSource(in);
			JAXBElement<MetaData> userElement = (JAXBElement<MetaData>) JaxbObjects.getUM().unmarshal(ss,MetaData.class);
			user = userElement.getValue();
		} catch (Exception e) {
			throw new IntegrationException(e.getMessage(),e);
		}
		return user;
	}

	/**
	 * This method performs the connection test function
	 * 
	 * @param server the server to be tested
	 * @return null if connection tests ok, else a localized error message
	 */
	public static String testConnection(IntegrationServer server) {
		String result = null;
		Credentials creds = new UsernamePasswordCredentials(
				server.getUserName(), server.getPassword());
		DefaultHttpClient httpclient = new DefaultHttpClient();
		BasicHttpContext localcontext = new BasicHttpContext();

		final URL url;
		try {
			url = new URL(server.getUrl());
			HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(),
					url.getProtocol());
			HttpGet httpGet = new HttpGet(server.getUrl() + "/api");

			Header bs = new BasicScheme().authenticate(creds, httpGet,
					localcontext);
			httpGet.setHeader("Authorization", bs.getValue());
			httpGet.setHeader("Content-Type", "text/html");
			httpGet.setHeader("Accept", "text/html");

			HttpResponse response = httpclient.execute(targetHost, httpGet,
					localcontext);
			if (response.getStatusLine().getStatusCode() != 200) {
				result = response.getStatusLine().getReasonPhrase();
			}
		} catch (MalformedURLException e) {
			result = MODULE_NAME + ".General.Errors.MalformedUrl";
		} catch (AuthenticationException e) {
			result = MODULE_NAME + ".General.Errors.Authentication";
		} catch (ClientProtocolException e) {
			result = MODULE_NAME + ".General.Errors.ClientProtocol";
		} catch (IOException e) {
			result = MODULE_NAME + ".General.Errors.IOFailure";
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		return result;
	}
	
	/**
	 * This method builds the org unit display list from orgs.xml in the server New subdir
	 * It is then available via OrgUnitDisplay.getAllHierarchical()
	 * 
	 * @param rebuild	forces rebuilding of the tree even if it is for same server
	 * @param server the server to be tested
	 */
	public static void getOrgUnitDisplay(Boolean rebuild, String server) throws IntegrationException {
		MetaData orgs = UnmarshalMetaData(ContentType.ORGS, "New", server);
		if (orgs==null) return;
		if (orgs.getOrganisationUnits()==null) return;
		if (server.equals(OrgUnitDisplay.getServerName()) && !rebuild) return;
		OrgUnitDisplay.Reset();
		for (OrgUnitType.OrganisationUnit org : orgs.getOrganisationUnits().getOrganisationUnit()) {
			OrgUnitDisplay d = new OrgUnitDisplay(org.getName(),org.getCode(),org.getId());
			d.setLevel(org.getLevel().intValue());
			d.setServer(server);
			if (org.getParent()!=null) {
				d.setParent(org.getParent().getName(), org.getParent().getCode(), org.getParent().getId(), d.getUid());
			} 
		}
		return;
	}

	public static void copyNewToCurrent(String server) throws IntegrationException {
		for (ContentType ct : ContentType.values()) {
			File of = getServerFile(ct,"Current",server);
			File inf = getServerFile(ct,"New",server);
			if (inf.canRead()) {
				try {
					OutputStream os = new FileOutputStream(of);
					InputStream ins = new FileInputStream(inf);
					IOUtils.copy(ins, os);
				} catch (Exception e) {
					throw new IntegrationException(e.getMessage(),e);
				}
			}
		}
	}
	
	public static void saveReportToFile(DataValueSet dvs, String server, String report, Date asOf, Date sent) throws IntegrationException {
		OutputStream os;
		try {
			os = new FileOutputStream(getServerReportFile(server, report, asOf, sent));
			JaxbObjects.getMM().marshal((Object) dvs, os);
		} catch (Exception e) {
			throw new IntegrationException(e.getMessage(),e);
		}
	}
	
	public static DataValueSet getReportFromFile(String server, String report, Date asOf, Date sent) throws IntegrationException {
		DataValueSet user=null;
		File inf = getServerReportFile(server, report, asOf, sent);
		if (inf.canRead()) {
			try {
				InputStream ins = new FileInputStream(inf);
				StreamSource ss = new StreamSource(ins);
				JAXBElement<DataValueSet> userElement = (JAXBElement<DataValueSet>) JaxbObjects.getUM().unmarshal(ss,DataValueSet.class);
				user = userElement.getValue();
			} catch (Exception e) {
				throw new IntegrationException(e.getMessage(),e);
			}
		}
		return user;
	}

	/**
	 * This method uploads a dataValueSet xml file from the server's Reports subdirectory in application space
	 * 
	 * @param meta	content type to be downloaded
	 * @param is	server to be downloaded from
	 */
	public static ImportSummary sendReportViaAPI(String server, String report, Date asOf, Date sent) throws IntegrationException {

		ImportSummary summary;
		URL url;
		final String selector="/api/dataValueSets?dataElementIdScheme=uid&orgUnitIdScheme=uid";
		final String accept="application/xml";
		
		File f = getServerReportFile(server, report, asOf, sent);
		DhisService ds=Context.getService(DhisService.class);
		IntegrationServer is=ds.getIntegrationServerByName(server);
		
		Credentials creds = new UsernamePasswordCredentials(
				is.getUserName(), is.getPassword());
		DefaultHttpClient httpclient = new DefaultHttpClient();
		BasicHttpContext localcontext = new BasicHttpContext();

//		Setup the POST
		try {
			url = new URL(is.getUrl());
			HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(),
					url.getProtocol());
			
			HttpPost httpPost = new HttpPost(is.getUrl() + selector);
			Header bs = new BasicScheme().authenticate(creds, httpPost,
					localcontext);
			httpPost.setHeader("Authorization", bs.getValue());
			httpPost.setHeader("Content-Type", "application/xml");
			httpPost.setHeader("Accept", accept);
            httpPost.setEntity(new FileEntity(f) );

//		POST and GET the response			
			HttpResponse response = httpclient.execute(targetHost, httpPost,
					localcontext);
			HttpEntity entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new IntegrationException(response.getStatusLine()
						.getReasonPhrase(), null);
			} 
			
//		Unmarshal the XML and return the top level structure			
			if (entity != null) {
                summary = (ImportSummary) JaxbObjects.getUM().unmarshal( entity.getContent() );
	        } else  {
                summary = new ImportSummary();
                summary.setStatus( "ERROR" );
	        }

// 		Handle exceptions and close the connection			

		} catch (Exception e) {
			throw new IntegrationException(e.getMessage(),e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return summary;
	}

}