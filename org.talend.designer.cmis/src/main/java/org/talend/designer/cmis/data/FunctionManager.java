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

import org.talend.designer.cmis.CMISComponent;

public class FunctionManager {

	
	private CMISComponent cmisComponent;
	private SessionManager sessionManager;
	private FolderManager folderManager;
	
	public FunctionManager(CMISComponent component,
			SessionManager sessionManager) {
		this.cmisComponent = component;
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
