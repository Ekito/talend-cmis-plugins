package org.talend.cmis.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
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
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

public final class CMISHelper {

	private CMISHelper(){}

	public static Document createDocument(Session session, String folderPath, Map<String, Object> properties, Map<String, Object> keys, String contentStreamFilePath) throws Exception {

		//Check that the document does not already exist
		ItemIterable<QueryResult> queryResult = find(session, keys);

		if (queryResult.getTotalNumItems() > 0)
		{
			throw new Exception("An object already exists with the following criteria : " + formatPredicate(keys));
		}

		Document document = createDocument(session, folderPath, properties, contentStreamFilePath);

		return document;
	}

	public static Document createOrUpdateDocument(Session session, String folderPath, Map<String, Object> properties, Map<String, Object> keys, String contentStreamFilePath) throws Exception {
		//Check if the document already exists
		ItemIterable<QueryResult> queryResult = find(session, keys);

		Document document = null;
		if (queryResult.getTotalNumItems() == 0)
		{
			document = createDocument(session, folderPath, properties, contentStreamFilePath);
		}
		else if (queryResult.getTotalNumItems() == 1)
		{
			for (QueryResult result : queryResult) {

				String objectId = result.getPropertyValueByQueryName(PropertyIds.OBJECT_ID);
				document = (Document) session.getObject(session.createObjectId(objectId));
				document = updateDocument(session, document, properties, contentStreamFilePath);
			}
		}else
		{
			throw new Exception("More than one object exist with the following criteria : " + formatPredicate(keys));
		}


		return document;
	}

	public static Document updateDocument(Session session, Map<String, Object> properties, Map<String, Object> keys, String contentStreamFilePath) throws Exception {
		//Check if the document already exists
		ItemIterable<QueryResult> queryResult = find(session, keys);

		Document document = null;
		if (queryResult.getTotalNumItems() == 0)
		{
			throw new Exception("Any document exist with the following criteria : " + formatPredicate(keys));
		}
		else if (queryResult.getTotalNumItems() == 1)
		{
			for (QueryResult result : queryResult) {

				String objectId = result.getPropertyValueByQueryName(PropertyIds.OBJECT_ID);
				document = (Document) session.getObject(session.createObjectId(objectId));
				document = updateDocument(session, document, properties, contentStreamFilePath);
			}

		}else
		{
			throw new Exception("More than one object exist with the following criteria : " + formatPredicate(keys));
		}


		return document;
	}

	public static void deleteCmisObject(Session session, Map<String, Object> keys) throws Exception
	{
		deleteCmisObject(session, keys, null);
	}
	
	public static void deleteCmisObject(Session session, Map<String, Object> keys, String folderPath) throws Exception
	{
		Folder parentFolder =null;
		
		if (folderPath != null)
		{
			parentFolder = (Folder)session.getObjectByPath(folderPath);
		}
		//Check if the object exists
		ItemIterable<QueryResult> queryResult = find(session, keys, parentFolder);

		if (queryResult.getTotalNumItems() == 0)
		{
			throw new Exception("Any object exist with the following criteria : " + formatPredicate(keys,parentFolder));
		}
		else if (queryResult.getTotalNumItems() == 1)
		{
			for (QueryResult result : queryResult) {

				String objectId = result.getPropertyValueByQueryName(PropertyIds.OBJECT_ID);
				CmisObject object = session.getObject(session.createObjectId(objectId));
				object.delete(true);
			}

		}else
		{
			throw new Exception("More than one object exist with the following criteria : " + formatPredicate(keys,parentFolder));
		}

	}
	

	public static Folder createFolder(Session session, String parentFolderPath, Map<String, Object> properties, Map<String, Object> keys, boolean recursive) throws Exception
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
				
				targetFolder = createFolder(session, grandParentFolderPath, parentProperties, parentProperties, recursive);
			}else {
				throw e;
			}
		}
		
		ItemIterable<QueryResult> queryResult = find(session, keys, targetFolder);

		if (queryResult.getTotalNumItems() > 0)
		{
			throw new Exception("An object already exists with the following criteria : " + formatPredicate(keys, targetFolder));
		}
		
		Folder folder = targetFolder.createFolder(properties);

		return folder;
	}
	
	private static Document createDocument(Session session, String folderPath, Map<String, Object> properties, String contentStreamFilePath) throws FileNotFoundException
	{
		//Retrieve the folder from path
		Folder targetFolder = (Folder)session.getObjectByPath(folderPath);

		ContentStream contentStream = createContentStream(session, contentStreamFilePath);

		Document document = targetFolder.createDocument(properties, contentStream, org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR);

		return document;
	}
	
	private static Document updateDocument(Session session,
			Document document, Map<String, Object> properties,
			String contentStreamFilePath) throws FileNotFoundException {
		ObjectId objectId = document.checkOut();
		document = (Document) session.getObject(objectId);
		document.checkIn(false, properties, null, "new version from Talend");
		ContentStream contentStream = null;
		
		if (contentStreamFilePath != null)
		{
			contentStream = createContentStream(session, contentStreamFilePath);
		}
		if (contentStream != null)
		{
			document = document.setContentStream(contentStream, true);
		}
		return document;
	}

	private static ContentStream createContentStream(Session session, String contentStreamFilePath) throws FileNotFoundException
	{
		ContentStream contentStream = null;

		if (contentStreamFilePath != null)
		{
			File contentFile = new File(contentStreamFilePath);

			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String mimetype = fileNameMap.getContentTypeFor(contentStreamFilePath);

			FileInputStream contentFileInputStream = new FileInputStream(contentFile);
			long length = contentFile.length();
			contentStream = session.getObjectFactory()
					.createContentStream(contentFile.getName(),
							length,
							mimetype,
							contentFileInputStream);
		}

		return contentStream;
	}

	private static ItemIterable<QueryResult> find(Session session,
			Map<String, Object> keys) {

		return find(session, keys, null);

	}

	private static ItemIterable<QueryResult> find(Session session,
			Map<String, Object> keys, ObjectId folderId) {

		StringBuilder queryStatement = new StringBuilder();

		String objectTypeId = (String) keys.get(PropertyIds.OBJECT_TYPE_ID);

		queryStatement.append("SELECT ")
		.append(PropertyIds.OBJECT_ID)
		.append(" FROM ")
		.append(objectTypeId)
		.append(" WHERE ")
		.append(formatPredicate(keys, folderId));

		return session.query(queryStatement.toString(), false);

	}
	
	private static String formatPredicate(Map<String, Object> keys)
	{
		return formatPredicate(keys, null);
	}
	
	private static String formatPredicate(Map<String, Object> keys, ObjectId folderId)
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
 
	
}
