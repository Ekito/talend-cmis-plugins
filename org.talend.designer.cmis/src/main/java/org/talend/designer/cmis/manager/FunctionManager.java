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
package org.talend.designer.cmis.manager;

import org.talend.core.model.process.IExternalNode;

public class FunctionManager {

	
	private IExternalNode component;
	private SessionManager sessionManager;
	private FolderManager folderManager;
	
	public FunctionManager(IExternalNode component,
			SessionManager sessionManager) {
		this.component = component;
		this.sessionManager = sessionManager;
		this.folderManager = new FolderManager(component, sessionManager);
	}
	
	public FolderManager getFolderManager() {
		return folderManager;
	}
	
	public void save() {
		this.folderManager.save();
		
	}
}
