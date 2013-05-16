package org.talend.cmis.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

public class CmisSessionManager {

	private Session session;
	private Boolean allVersionSearchable;

	public CmisSessionManager(Session session){
		this.session = session;
		allVersionSearchable = session.getRepositoryInfo().getCapabilities().isAllVersionsSearchableSupported();
	}

	public Document createDocument(String folderPath, Map<String, Object> properties, Map<String, Object> keys, URL contentStreamUrl) throws Exception {

		//Check that the document does not already exist in the folder
		Folder parentFolder =null;

		//Issue with nuxeo 5.6 : query with IN_FOLDER criteria doesn't work 
		if (folderPath != null)
		{
			parentFolder = (Folder)session.getObjectByPath(folderPath);
		}

		ItemIterable<QueryResult> queryResult = find(keys, parentFolder);

		//Issue with Alfresco 4.0 : getTotalNumItems() return -1 everytime
		if (queryResult != null && queryResult.getPageNumItems() > 0)
		{
			throw new Exception("An object already exists with the following criteria : " + formatPredicate(keys));
		}

		Document document = createDocument(folderPath, properties, contentStreamUrl);

		return document;
	}

	public Document createOrUpdateDocument(String folderPath, Map<String, Object> properties, Map<String, Object> keys, URL contentStreamUrl) throws Exception {

		Folder parentFolder =null;

		if (folderPath != null)
		{
			parentFolder = (Folder)session.getObjectByPath(folderPath);
		}

		//Check if the document already exists
		ItemIterable<QueryResult> queryResult = find(keys, parentFolder);

		Document document = null;
		if (queryResult == null || queryResult.getPageNumItems() <= 0)
		{
			document = createDocument(folderPath, properties, contentStreamUrl);
		}
		else if (queryResult.getPageNumItems() == 1)
		{
			for (QueryResult result : queryResult) {

				String objectId = result.getPropertyValueByQueryName(PropertyIds.OBJECT_ID);
				document = (Document) session.getObject(session.createObjectId(objectId));
				document = updateDocument(document, properties, contentStreamUrl);
			}
		}else
		{
			throw new Exception("More than one object exist with the following criteria : " + formatPredicate(keys));
		}


		return document;
	}

	public Document updateDocument(String folderPath, Map<String, Object> properties, Map<String, Object> keys, URL contentStreamUrl) throws Exception {

		Folder parentFolder =null;

		if (folderPath != null)
		{
			parentFolder = (Folder)session.getObjectByPath(folderPath);
		}

		//Check if the document already exists
		ItemIterable<QueryResult> queryResult = find(keys, parentFolder);

		Document document = null;
		if (queryResult == null || queryResult.getPageNumItems() <= 0)
		{
			throw new Exception("No document exists with the following criteria : " + formatPredicate(keys, parentFolder));
		}
		else if (queryResult.getPageNumItems() == 1)
		{
			for (QueryResult result : queryResult) {

				String objectId = result.getPropertyValueByQueryName(PropertyIds.OBJECT_ID);
				document = (Document) session.getObject(session.createObjectId(objectId));
				document = updateDocument(document, properties, contentStreamUrl);
			}

		}else
		{
			throw new Exception("More than one object exist with the following criteria : " + formatPredicate(keys));
		}


		return document;
	}

	public void deleteCmisObject(Map<String, Object> keys) throws Exception
	{
		deleteCmisObject(keys, null);
	}

	public void deleteCmisObject(Map<String, Object> keys, String folderPath) throws Exception
	{
		Folder parentFolder =null;

		if (folderPath != null && !folderPath.equals(""))
		{
			parentFolder = (Folder)session.getObjectByPath(folderPath);
		}
		//Check if the object exists
		ItemIterable<QueryResult> queryResult = find(keys, parentFolder);

		if (queryResult == null || queryResult.getPageNumItems() <= 0)
		{
			throw new Exception("No object exists with the following criteria : " + formatPredicate(keys,parentFolder));
		}
		else if (queryResult.getPageNumItems() == 1)
		{
			for (QueryResult result : queryResult) {

				String objectId = result.getPropertyValueByQueryName(PropertyIds.OBJECT_ID);
				CmisObject object = session.getObject(session.createObjectId(objectId));
				
				if (object instanceof Folder)
				{
					((Folder)object).deleteTree(true, UnfileObject.DELETESINGLEFILED, false);
				}else
				{
					object.delete(true);
				}
			}

		}else
		{
			throw new Exception("More than one object exist with the following criteria : " + formatPredicate(keys,parentFolder));
		}

	}


	public Folder createFolder(String parentFolderPath, Map<String, Object> properties, Map<String, Object> keys, boolean recursive) throws Exception
	{
		//Retrieve the parent folder from path
		Folder targetFolder = null;
		try {
			targetFolder = (Folder)session.getObjectByPath(parentFolderPath);
		} catch (CmisObjectNotFoundException e) {
			if (recursive)
			{
				if (parentFolderPath.endsWith("/"))
				{
					parentFolderPath = parentFolderPath.substring(0, parentFolderPath.lastIndexOf("/"));
				}
				String grandParentFolderPath = parentFolderPath.substring(0,parentFolderPath.lastIndexOf("/"));
				String parentName = parentFolderPath.substring(parentFolderPath.lastIndexOf("/")+1);
				Map<String,Object> parentProperties = new HashMap<String, Object>();
				parentProperties.put(PropertyIds.NAME, parentName);
				//By default, parent folder is created with the same type as it child
				parentProperties.put(PropertyIds.OBJECT_TYPE_ID, properties.get(PropertyIds.OBJECT_TYPE_ID));

				targetFolder = createFolder(grandParentFolderPath, parentProperties, parentProperties, recursive);
			}else {
				throw new CmisObjectNotFoundException(parentFolderPath + " not found", e);
			}
		}

		ItemIterable<QueryResult> queryResult = find(keys, targetFolder);

		if (queryResult != null && queryResult.getPageNumItems() > 0)
		{
			throw new Exception("An object already exists with the following criteria : " + formatPredicate(keys, targetFolder));
		}

		Folder folder = targetFolder.createFolder(properties);

		return folder;
	}

	private Document createDocument(String folderPath, Map<String, Object> properties, URL contentStreamURL) throws FileNotFoundException
	{
		//Retrieve the folder from path
		Folder targetFolder = (Folder)session.getObjectByPath(folderPath);

		ContentStream contentStream = createContentStream(contentStreamURL);

		Document document = targetFolder.createDocument(properties, contentStream, org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR);

		return document;
	}

	private Document updateDocument(Document document, Map<String, Object> properties,
			URL contentStreamURL) throws FileNotFoundException {

//		For Nuxeo, document are by default PWC
//		if (!document.isVersionSeriesCheckedOut())
//		{
//			ObjectId objectId = document.checkOut();
//			document = (Document) session.getObject(objectId);
//		}else
//		{
//			String objectId = document.getVersionSeriesCheckedOutId();
//			document = (Document) session.getObject(session.createObjectId(objectId));
//		}
//		document.checkIn(false, properties, null, "new version from Talend");

		ContentStream contentStream = null;

		if (contentStreamURL != null)
		{
			contentStream = createContentStream(contentStreamURL);
		}
		if (contentStream != null)
		{
			//AtomPub binding does not return an id
			document.setContentStream(contentStream, true);
		}
		
		ObjectId documentId = document.updateProperties(properties, true);
		document = (Document) session.getObject(documentId);
		
		return document;
	}

	private ContentStream createContentStream(URL contentStreamURL)
	{
		ContentStream contentStream = null;

		if (contentStreamURL != null)
		{
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String mimetype = fileNameMap.getContentTypeFor(contentStreamURL.getFile());

			URLConnection contentStreamConn;
			try {
				contentStreamConn = contentStreamURL.openConnection();
			
		    int length = contentStreamConn.getContentLength();
		      
			InputStream contentInputStream = contentStreamConn.getInputStream();
			contentStream = session.getObjectFactory()
					.createContentStream(contentStreamURL.getFile(),
							length,
							mimetype,
							contentInputStream);
			} catch (IOException e) {
				throw new CmisRuntimeException("An error occured while creating contentStream", e);
			}
		}

		return contentStream;
	}

	private ItemIterable<QueryResult> find(Map<String, Object> keys) {

		return find(keys, null);

	}

	private ItemIterable<QueryResult> find(Map<String, Object> keys, ObjectId folderId) {

		ItemIterable<QueryResult> queryResult = null;

		if (keys != null && keys.size() > 0)
		{
			StringBuilder queryStatement = new StringBuilder();

			String objectTypeId = (String) keys.get(PropertyIds.OBJECT_TYPE_ID);

			queryStatement.append("SELECT ")
			.append(PropertyIds.OBJECT_ID)
			.append(" FROM ")
			.append(objectTypeId)
			.append(" WHERE ")
			.append(formatPredicate(keys, folderId));

			queryResult = session.query(queryStatement.toString(), allVersionSearchable);
		}
		return queryResult;

	}

	private String formatPredicate(Map<String, Object> keys)
	{
		return formatPredicate(keys, null);
	}

	private String formatPredicate(Map<String, Object> keys, ObjectId folderId)
	{
		StringBuilder predicate = new StringBuilder();
		for (Entry<String,Object> keyEntry : keys.entrySet()) {
			String key = keyEntry.getKey();
			Object keyValue = keyEntry.getValue();

			if (keyValue != null && !key.equals(PropertyIds.OBJECT_TYPE_ID))
			{
				if (predicate.length() > 0)
				{
					predicate.append(" and ");
				}
				predicate.append(key)
				.append(" = '")
				.append(keyValue)
				.append("'");
			}
		}

		if (folderId != null)
		{
			if (predicate.length() > 0)
			{
				predicate.append(" and ");
			}

			predicate.append("IN_FOLDER('")
			.append(folderId.getId())
			.append("')");
		}

		return predicate.toString();
	}

	public CmisObject getObjectByPath(String path)
	{
		return session.getObjectByPath(path);
	}

	public void extractCMISObjectContent(CmisObject cmisObject, String contentPath)
	{
		if (cmisObject instanceof Document)
		{
			Document cmisDocument = (Document) cmisObject;
			extractDocumentContent(cmisDocument, contentPath);
		}
		else if (cmisObject instanceof Folder)
		{
			Folder cmisFolder = (Folder) cmisObject;
			ItemIterable<CmisObject> childrens = cmisFolder.getChildren();

			//Read the document list of the folder and download the content
			for (Iterator<CmisObject> childIterator = childrens.iterator(); childIterator.hasNext();) {


				CmisObject childObject = (CmisObject) childIterator.next();

				if (childObject instanceof Document)
				{
					Document cmisDocument = (Document) childObject;
					extractDocumentContent(cmisDocument, contentPath);
					//TODO : add "download content recursively" option and download content for subfolders

				}
			}
		}
	}

	private void extractDocumentContent(Document cmisDocument, String contentPath)
	{
		String filename = cmisDocument.getContentStreamFileName();

		//Test if filename is not null, else this document has not content
		if (filename != null)
		{
			InputStream inputStream = cmisDocument.getContentStream().getStream();
			OutputStream outputStream = null;
			try
			{
				//Check that the contentPath exist
				checkContentPath(contentPath);
				outputStream = new FileOutputStream(contentPath + "/" + filename);
				byte buf[]=new byte[1024];
				int len;
				while((len = inputStream.read(buf))>0)
				{
					outputStream.write(buf, 0, len);
				}
				
				outputStream.close();
				inputStream.close();
			}catch (IOException e)
			{
				try {
					if (outputStream != null)
						outputStream.close();

					inputStream.close();
				}catch (IOException e1)
				{
					e1.printStackTrace();
				}
				throw new RuntimeException(e);
			}
		}
	}

	private void checkContentPath(String contentPath)
	{
		if (contentPath != null
				&& contentPath.trim().length() != 0) {
			File contentPathDir = new File(contentPath);
			//If the path doesn't exist, create it
			if (!contentPathDir.exists()) {
				contentPathDir.mkdirs();
			}
		}
	}

}
