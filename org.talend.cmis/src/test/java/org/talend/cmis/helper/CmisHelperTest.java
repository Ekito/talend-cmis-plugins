/*******************************************************************************
 * Copyright (c) 2012 Julien Boulay - Ekito - www.ekito.fr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Julien Boulay - Ekito - www.ekito.fr - initial API and implementation
 ******************************************************************************/
package org.talend.cmis.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Julien Boulay - Ekito - www.ekito.fr
 *
 */
@RunWith(Parameterized.class)
public class CmisHelperTest extends TestCase{

	public static HashMap<String, String> testSuiteParameters = null;
	private static Session session;

	public CmisHelperTest(String name, HashMap<String, String> map) {
		super(name);
		testSuiteParameters = map;
		createSession();
		
	}
	
	@Parameters(name="{index}: {0}")
    public static Collection<Object[]> configs() {
        Map<String, String> alfrescoAtomPub = null;
		Map<String, String> alfrescoWebservices = null;
		Map<String, String> nuxeoAtomPub = null;
		try {
			alfrescoAtomPub = getTestSuiteParameters("AlfrescoAtomPub-parameters.properties");
			
			alfrescoWebservices = getTestSuiteParameters("AlfrescoWebServices-parameters.properties");
			
			nuxeoAtomPub = getTestSuiteParameters("NuxeoAtomPub-parameters.properties");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return Arrays.asList(new Object[][] {
                { "AlfrescoAtomPub", alfrescoAtomPub },
                { "AlfrescoWebservices", alfrescoWebservices },
                { "NuxeoAtomPub", nuxeoAtomPub}
        });
    }

    @Before
    public void setUp() throws Exception {
    	super.setUp();
    	
    	deleteRootFolder();
    	initRootFolder();
    	
    }
    
    @After
    public void tearDown() throws Exception {
    	super.tearDown();
    	
    	deleteRootFolder();
    }
    
	@Test
	public void testCreateDocument() {

		Document doc = null;
		try {
			doc = CmisHelper.createDocument(session, getRootFolderPath(), getDocumentProperties(), getDocumentKeys(), getContentStreamFilePath("smiley1.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(doc);
	}

	@Test
	public void testCreateOrUpdateDocumentUpdate() {
		
//		DOCUMENT_1_PROPERTIES.put(DOCUMENT_1_PROPERTY_1_NAME, DOCUMENT_1_PROPERTY_1_VALUE);
		
		Document doc = null;
		try {
			//First document version initialization
			createDocument();
			
			doc = CmisHelper.createOrUpdateDocument(session, getRootFolderPath(), getDocumentProperties(), getDocumentKeys(), getContentStreamFilePath("smiley2.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(doc);
	}
	
	@Test
	public void testCreateOrUpdateDocumentCreate() {
		
		Document doc = null;
		try {
			doc = CmisHelper.createOrUpdateDocument(session, getRootFolderPath(), getDocumentProperties(), getDocumentKeys(), getContentStreamFilePath("smiley1.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(doc);
	}
	
	@Test
	public void testUpdateDocument() {

		Document doc = null;
		try {
			//First document version initialization
			createDocument();
			
			doc = CmisHelper.updateDocument(session, getRootFolderPath(), getDocumentProperties(), getDocumentKeys(), getContentStreamFilePath("smiley2.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(doc);
	}
	
	@Test
	public void testCreateFolder(){
		
		Folder folder = null;
		try {
			folder = CmisHelper.createFolder(session, getRootFolderPath(), getFolderProperties(), getFolderProperties(), false);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}
		
		Assert.assertNotNull(folder);
	}
	
	@Test
	public void testCreateFolderParentNotExists(){
		
		Folder folder = null;
		try {
			folder = CmisHelper.createFolder(session, getRootFolderPath() + "/ParentNotExists", getFolderProperties(), getFolderProperties(), false);
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
			folder = CmisHelper.createFolder(session, getRootFolderPath()  + "/Folder_00/Folder_01/", getFolderProperties(), getFolderProperties(), true);
			Assert.assertNotNull(folder);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}
		
	}
	
	@Test
	public void testDeleteFolder()
	{
		try {
			createFolder();
			
			CmisHelper.deleteCmisObject(session, getFolderProperties(), getRootFolderPath());
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void testDeleteDocument()
	{
		try {
			createDocument();
			
			CmisHelper.deleteCmisObject(session, getDocumentProperties(), getRootFolderPath());
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	/*
	 *********************************************************************************
	 * 						PRIVATE SECTION
	 * Utilities for parameterized TestSuite initialization  
	 *********************************************************************************
	 */
	
	private void createDocument()
	{
		Folder rootFolder = (Folder) session.getObjectByPath(getRootFolderPath());
		String contentStreamFilePath = getContentStreamFilePath("smiley1.png");
		File contentFile = new File(contentStreamFilePath);

		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String mimetype = fileNameMap.getContentTypeFor(contentStreamFilePath);

		FileInputStream contentFileInputStream = null;
		try {
			contentFileInputStream = new FileInputStream(contentFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		long length = contentFile.length();
		ContentStream contentStream = session.getObjectFactory()
				.createContentStream(contentFile.getName(),
						length,
						mimetype,
						contentFileInputStream);
		rootFolder.createDocument(getDocumentProperties(), contentStream, VersioningState.MINOR);
	}
	
	private void createFolder()
	{
		Folder rootFolder = (Folder) session.getObjectByPath(getRootFolderPath());
		rootFolder.createFolder(getFolderProperties());
	}
	
	private String getContentStreamFilePath(String contentFileName) {
		
		URL contentFolder = CmisHelperTest.class.getClassLoader().getResource("content/" + contentFileName);
		
		return contentFolder.getPath();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static HashMap<String, String> getTestSuiteParameters(String resourcePath) throws IOException
	{
		InputStream in = CmisHelperTest.class.getClassLoader().getResourceAsStream(resourcePath);
		Properties properties = new Properties();
		properties.load(in);
		in.close();
		
		HashMap<String, String> testSuiteParameters = new HashMap<String, String>((Map) properties); 
		
		return testSuiteParameters;
	}
    
	private HashMap<String, String> getSessionParameters(HashMap<String,String> testSuiteParameters)
	{
		HashMap<String, String> sessionParameters = new HashMap<String, String>(); 
		
		sessionParameters.put(SessionParameter.BINDING_TYPE, testSuiteParameters.get(SessionParameter.BINDING_TYPE));
		sessionParameters.put(SessionParameter.USER, testSuiteParameters.get(SessionParameter.USER));
		sessionParameters.put(SessionParameter.PASSWORD, testSuiteParameters.get(SessionParameter.PASSWORD));
		sessionParameters.put(SessionParameter.REPOSITORY_ID, testSuiteParameters.get(SessionParameter.REPOSITORY_ID));
		sessionParameters.put(SessionParameter.LOCALE_ISO3166_COUNTRY, testSuiteParameters.get(SessionParameter.LOCALE_ISO3166_COUNTRY));
		sessionParameters.put(SessionParameter.LOCALE_ISO639_LANGUAGE, testSuiteParameters.get(SessionParameter.LOCALE_ISO639_LANGUAGE));
		sessionParameters.put(SessionParameter.CONNECT_TIMEOUT, testSuiteParameters.get(SessionParameter.CONNECT_TIMEOUT));
		sessionParameters.put(SessionParameter.COMPRESSION, testSuiteParameters.get(SessionParameter.COMPRESSION));
		
		if (testSuiteParameters.get(SessionParameter.BINDING_TYPE).equals(BindingType.ATOMPUB.value()))
		{
			sessionParameters.put(SessionParameter.ATOMPUB_URL, testSuiteParameters.get(SessionParameter.ATOMPUB_URL));
		}
		else if (testSuiteParameters.get(SessionParameter.BINDING_TYPE).equals(BindingType.WEBSERVICES.value()))
		{
			String webserviceURL = testSuiteParameters.get("org.apache.chemistry.opencmis.binding.webservices.url");
			sessionParameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_ACL_SERVICE, webserviceURL+"/ACLService?wsdl");
			sessionParameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, webserviceURL+"/DiscoveryService?wsdl");
			sessionParameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, webserviceURL+"/MultiFilingService?wsdl");
			sessionParameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, webserviceURL+"/NavigationService?wsdl");
			sessionParameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_OBJECT_SERVICE, webserviceURL+"/ObjectService?wsdl");
			sessionParameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_POLICY_SERVICE, webserviceURL+"/PolicyService?wsdl");
			sessionParameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, webserviceURL+"/RelationshipService?wsdl");
			sessionParameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, webserviceURL+"/RepositoryService?wsdl");
			sessionParameters.put(org.apache.chemistry.opencmis.commons.SessionParameter.WEBSERVICES_VERSIONING_SERVICE, webserviceURL+"/VersioningService?wsdl");
		}
		return sessionParameters;
	}
	
	private void createSession() {
		//Create a new CMIS session
		SessionFactory sf = org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl.newInstance();
		
		HashMap<String, String> sessionParameters = getSessionParameters(testSuiteParameters);
		session = sf.createSession(sessionParameters);
	}
	
	private Map<String,Object> getDocumentProperties()
	{
		Map<String,Object> propMap = new HashMap<String, Object>();
		propMap.put(PropertyIds.NAME, testSuiteParameters.get(DOCUMENT_NAME));
		propMap.put(PropertyIds.OBJECT_TYPE_ID, testSuiteParameters.get(DOCUMENT_TYPE));
		return propMap;
	}
	
	private Map<String,Object> getDocumentKeys()
	{
		Map<String,Object> keyMap = new HashMap<String, Object>();
		keyMap.put(PropertyIds.NAME, testSuiteParameters.get(DOCUMENT_NAME));
		keyMap.put(PropertyIds.OBJECT_TYPE_ID, testSuiteParameters.get(DOCUMENT_TYPE));
		return keyMap;
	}
	
	private Map<String,Object> getRootFolderProperties()
	{
		Map<String,Object> propMap = new HashMap<String, Object>();
		propMap.put(PropertyIds.NAME, testSuiteParameters.get(ROOTFOLDER_NAME));
		propMap.put(PropertyIds.OBJECT_TYPE_ID, testSuiteParameters.get(ROOTFOLDER_TYPE));
		return propMap;
	}
	
	private Map<String,Object> getFolderProperties()
	{
		Map<String,Object> propMap = new HashMap<String, Object>();
		propMap.put(PropertyIds.NAME, testSuiteParameters.get(FOLDER_NAME));
		propMap.put(PropertyIds.OBJECT_TYPE_ID, testSuiteParameters.get(FOLDER_TYPE));
		return propMap;
	}
	
	private String getRootFolderPath()
	{
		Folder rootFolder = session.getRootFolder();
		String rootFolderPath = rootFolder.getPath();
		
		String targetFolderPath = rootFolderPath + testSuiteParameters.get(ROOTFOLDER_NAME);
		
		return targetFolderPath;
	}
	
	private void deleteRootFolder()
    {
    	//Delete root folder
    	try {
			Folder rootFolder = (Folder) session.getObjectByPath(getRootFolderPath());
			rootFolder.deleteTree(true, UnfileObject.DELETESINGLEFILED, false);
			
		} catch (CmisObjectNotFoundException e) {
			//Nothing to do, if the root folder does not exist
		}
    }
	
    private void initRootFolder()
    {
    	session.getRootFolder().createFolder(getRootFolderProperties());
    }
    
    private static final String ROOTFOLDER_NAME = "rootfolder.name";
	private static final String ROOTFOLDER_TYPE = "rootfolder.type";
	private static final String FOLDER_NAME = "folder.name";
	private static final String FOLDER_TYPE = "folder.type";
	private static final String DOCUMENT_NAME = "document.name";
	private static final String DOCUMENT_TYPE = "document.type";
}
