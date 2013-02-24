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
package org.talend.designer.cmis.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.talend.core.model.process.IExternalNode;
import org.talend.designer.cmis.manager.SessionManager;
import org.talend.designer.cmis.model.FolderModel;

public class CMISFolderManager {
	
	private IExternalNode component;
	private SessionManager sessionManager;
	private ArrayList<FolderModel> rootFolderNodes;
	private String selectedFolderPath;

	public CMISFolderManager(IExternalNode component,
			SessionManager sessionManager) {
		this.component = component;
		this.sessionManager = sessionManager;
	}
	
	public ArrayList<FolderModel> getRootFolderNodes()
	{

		if (rootFolderNodes == null)
		{
			rootFolderNodes = new ArrayList<FolderModel>();
			Folder rootFolder = sessionManager.getSession().getRootFolder();
			List<Tree<FileableCmisObject>> childFolders = rootFolder.getFolderTree(1);
			
			FolderModel rootFolderNode = new FolderModel(null, rootFolder);
			
			for (Tree<FileableCmisObject> childFolderTree : childFolders) {
				Folder childFolder = (Folder) childFolderTree.getItem();
				rootFolderNodes.add(new FolderModel(rootFolderNode, childFolder));
				
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
