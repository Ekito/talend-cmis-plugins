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
package org.talend.designer.cmis.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.talend.designer.cmis.CMISComponent;

public class CMISFolderManager {
	
	private CMISComponent cmisComponent;
	private CMISSessionManager sessionManager;
	private ArrayList<CMISFolderNode> rootFolderNodes;
	private String selectedFolderPath;

	public CMISFolderManager(CMISComponent component,
			CMISSessionManager sessionManager) {
		this.cmisComponent = component;
		this.sessionManager = sessionManager;
	}
	
	public ArrayList<CMISFolderNode> getRootFolderNodes()
	{

		if (rootFolderNodes == null)
		{
			rootFolderNodes = new ArrayList<CMISFolderNode>();
			Folder rootFolder = sessionManager.getSession().getRootFolder();
			List<Tree<FileableCmisObject>> childFolders = rootFolder.getFolderTree(1);
			
			CMISFolderNode rootFolderNode = new CMISFolderNode(null, rootFolder);
			
			for (Tree<FileableCmisObject> childFolderTree : childFolders) {
				Folder childFolder = (Folder) childFolderTree.getItem();
				rootFolderNodes.add(new CMISFolderNode(rootFolderNode, childFolder));
				
			}
			
		}
		
		return rootFolderNodes;
	}

	public void setSelectedFolderPath(String selectedFolderPath) {
		this.selectedFolderPath = selectedFolderPath;
	}
	
	public String getSelectedFolderPath() {
		return selectedFolderPath;
	}

	public void save() {
		// TODO Auto-generated method stub
		
	}
}
