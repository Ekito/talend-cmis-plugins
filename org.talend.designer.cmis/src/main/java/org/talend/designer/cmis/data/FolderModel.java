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

public class FolderModel {

	
	private FolderModel parentFolder;
	private Folder folder;
	private ArrayList<FolderModel> children;
	private String displayName;
	
	public FolderModel(FolderModel parent, Folder folder) {
		this.parentFolder = parent;
		this.folder = folder;
	}
	
	public ArrayList<FolderModel> getChildren()
	{
		if (children == null)
		{
			children = new ArrayList<FolderModel>();
			List<Tree<FileableCmisObject>> folderTree = folder.getFolderTree(1);
			
			for (Tree<FileableCmisObject> tree : folderTree) {
				Folder childFolder = (Folder) tree.getItem();
				children.add(new FolderModel(this, childFolder));
				
			}
		}
		
		return children;
	}
	
	public FolderModel getParent() {
		return parentFolder;
	}

	public String getDisplayName() {
		return folder.getName();
	}
	
	public String getFolderId() {
		return folder.getId();
	}

	public String getFolderPath() {
		return folder.getPath();
	}

	public boolean hasChildren() {
		return getChildren().size() > 0;
	}
}
