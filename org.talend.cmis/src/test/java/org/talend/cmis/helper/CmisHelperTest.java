package org.talend.cmis.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CmisHelperTest {

	private static Session session;

	private static Map<String,Object> DOCUMENT_1_PROPERTIES;
	private static Map<String,Object> DOCUMENT_1_KEYS;
	private static final String DOCUMENT_1_NAME = "Document1";
	private static final String DOCUMENT_1_TYPE = "Picture";
	private static final String DOCUMENT_1_PROPERTY_1_NAME = "ipr:copyright_holder";
	private static final String DOCUMENT_1_PROPERTY_1_VALUE = "Ekito";
	private static final String DOCUMENT_1_FOLDER_PATH = "/default-domain/workspaces/IT Department/Projects/";
	
	
	private static Map<String,Object> DOCUMENT_2_PROPERTIES;
	private static Map<String,Object> DOCUMENT_2_KEYS;


	private static final String DOCUMENT_2_NAME = "Document2";
	private static final String DOCUMENT_2_TYPE = "Picture";
	private static final String DOCUMENT_2_PROPERTY_1_NAME = "ipr:copyright_holder";
	private static final String DOCUMENT_2_PROPERTY_1_VALUE = "Ekito";
	private static final String DOCUMENT_2_FOLDER_PATH = "/default-domain/workspaces/IT Department/Projects/";

	private static HashMap<String, Object> FOLDER_1_PROPERTIES;
	
	private static final String FOLDER_1_NAME = "folderTest";
	private static final String FOLDER_1_TYPE = "Folder";
	
	private static final String FOLDER_1_FOLDER_PATH = "/default-domain/workspaces/IT Department/Projects/";
	private static final String FOLDER_1_FOLDER_PATH_RECURSIVE = "/default-domain/workspaces/IT Department/Projects/ParentFolder/";
	
	static {
		
		DOCUMENT_1_PROPERTIES = new HashMap<String, Object>();
		DOCUMENT_1_PROPERTIES.put(PropertyIds.NAME, DOCUMENT_1_NAME);
		DOCUMENT_1_PROPERTIES.put(PropertyIds.OBJECT_TYPE_ID, DOCUMENT_1_TYPE);
		
		DOCUMENT_1_KEYS = new HashMap<String, Object>();
		DOCUMENT_1_KEYS.put(PropertyIds.NAME, DOCUMENT_1_NAME);
		DOCUMENT_1_KEYS.put(PropertyIds.OBJECT_TYPE_ID, DOCUMENT_1_TYPE);
		
		DOCUMENT_2_PROPERTIES = new HashMap<String, Object>();
		DOCUMENT_2_PROPERTIES.put(PropertyIds.NAME, DOCUMENT_2_NAME);
		DOCUMENT_2_PROPERTIES.put(PropertyIds.OBJECT_TYPE_ID, DOCUMENT_2_TYPE);
		
		DOCUMENT_2_KEYS = new HashMap<String, Object>();
		DOCUMENT_2_KEYS.put(PropertyIds.NAME, DOCUMENT_2_NAME);
		DOCUMENT_2_KEYS.put(PropertyIds.OBJECT_TYPE_ID, DOCUMENT_2_TYPE);
		
		FOLDER_1_PROPERTIES = new HashMap<String, Object>();
		FOLDER_1_PROPERTIES.put(PropertyIds.NAME, FOLDER_1_NAME);
		FOLDER_1_PROPERTIES.put(PropertyIds.OBJECT_TYPE_ID, FOLDER_1_TYPE);
	}
	
	@BeforeClass
	public static void oneTimeSetUp() throws Exception {

//		createAtomPubSession();
		createWSSession();
	}
	
	

	private static void createAtomPubSession() {
		Map<String, String> parameters = new HashMap<String, String>();
		
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.USER, "Administrator");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.PASSWORD, "Administrator");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.ATOMPUB_URL, "http://cmis.demo.nuxeo.org/nuxeo/atom/cmis");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.REPOSITORY_ID, "default");

		// session locale
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.LOCALE_ISO3166_COUNTRY, "");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.LOCALE_ISO639_LANGUAGE, "us");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.CONNECT_TIMEOUT, "10000");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.COMPRESSION, "true");
		
		//Create a new CMIS session
		SessionFactory sf = org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl.newInstance();
		session = sf.createSession(parameters);
	}
	
	private static void createWSSession() {
		Map<String, String> parameters = new HashMap<String, String>();
		
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.USER, "Administrator");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.PASSWORD, "Administrator");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.BINDING_TYPE, BindingType.WEBSERVICES.value());
		String webserviceURL = "http://cmis.demo.nuxeo.org/nuxeo/webservices/cmis";
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.REPOSITORY_ID, "default");

		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_ACL_SERVICE, webserviceURL+"/ACLService?wsdl");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, webserviceURL+"/DiscoveryService?wsdl");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, webserviceURL+"/MultiFilingService?wsdl");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, webserviceURL+"/NavigationService?wsdl");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_OBJECT_SERVICE, webserviceURL+"/ObjectService?wsdl");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_POLICY_SERVICE, webserviceURL+"/PolicyService?wsdl");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, webserviceURL+"/RelationshipService?wsdl");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, webserviceURL+"/RepositoryService?wsdl");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_VERSIONING_SERVICE, webserviceURL+"/VersioningService?wsdl");
		// session locale
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.LOCALE_ISO3166_COUNTRY, "");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.LOCALE_ISO639_LANGUAGE, "us");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.CONNECT_TIMEOUT, "10000");
		parameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.COMPRESSION, "true");
		
		//Create a new CMIS session
		SessionFactory sf = org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl.newInstance();
		session = sf.createSession(parameters);
	}



	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		deleteTestData();
	}


	private static void deleteTestData() {
		
		
		
	}

	@Test
	public void testCreateDocument() {

		Document doc = null;
		try {
			doc = CmisHelper.createDocument(session, DOCUMENT_1_FOLDER_PATH, DOCUMENT_1_PROPERTIES, DOCUMENT_1_KEYS, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(doc);
	}

	@Test
	public void testCreateOrUpdateDocumentUpdate() {
		
		DOCUMENT_1_PROPERTIES.put(DOCUMENT_1_PROPERTY_1_NAME, DOCUMENT_1_PROPERTY_1_VALUE);
		
		Document doc = null;
		try {
			doc = CmisHelper.createOrUpdateDocument(session, DOCUMENT_1_FOLDER_PATH, DOCUMENT_1_PROPERTIES, DOCUMENT_1_KEYS, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(doc);
	}
	
	@Test
	public void testCreateOrUpdateDocumentCreate() {
		
		Document doc = null;
		try {
			doc = CmisHelper.createOrUpdateDocument(session, DOCUMENT_2_FOLDER_PATH, DOCUMENT_2_PROPERTIES, DOCUMENT_2_KEYS, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(doc);
	}
	
	@Test
	public void testUpdateDocument() {

		DOCUMENT_2_PROPERTIES.put(DOCUMENT_2_PROPERTY_1_NAME, DOCUMENT_2_PROPERTY_1_VALUE);
		
		Document doc = null;
		try {
			doc = CmisHelper.updateDocument(session, DOCUMENT_2_FOLDER_PATH, DOCUMENT_2_PROPERTIES, DOCUMENT_2_KEYS, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(doc);
	}
	
	@Test
	public void testCreateFolder(){
		
		Folder folder = null;
		try {
			folder = CmisHelper.createFolder(session, FOLDER_1_FOLDER_PATH, FOLDER_1_PROPERTIES, FOLDER_1_PROPERTIES, false);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}
		
		Assert.assertNotNull(folder);
	}
	
	@Test
	public void testCreateFolderParentNotExists(){
		
		Folder folder = null;
		try {
			folder = CmisHelper.createFolder(session, FOLDER_1_FOLDER_PATH_RECURSIVE, FOLDER_1_PROPERTIES, FOLDER_1_PROPERTIES, false);
		} catch (Exception e) {
			if (e instanceof CmisObjectNotFoundException)
				Assert.assertTrue(true);
			else
				Assert.assertTrue(false);
		}
		
		Assert.assertNull(folder);
	}
	
	@Test
	public void testCreateFolderRecursive(){
		
		Folder folder = null;
		try {
			folder = CmisHelper.createFolder(session, FOLDER_1_FOLDER_PATH_RECURSIVE, FOLDER_1_PROPERTIES, FOLDER_1_PROPERTIES, true);
			Assert.assertNotNull(folder);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}
		
	}
	
	@Test
	public void testDeleteDocuments()
	{
		try {
			CmisHelper.deleteCmisObject(session, DOCUMENT_1_KEYS);
			CmisHelper.deleteCmisObject(session, DOCUMENT_2_KEYS);
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void testDeleteFolders()
	{
		try {
			CmisHelper.deleteCmisObject(session, FOLDER_1_PROPERTIES, FOLDER_1_FOLDER_PATH);
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
}
