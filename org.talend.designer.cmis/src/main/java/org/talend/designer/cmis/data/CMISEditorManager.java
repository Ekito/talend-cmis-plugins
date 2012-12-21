/*******************************************************************************
 * Copyright (c) 2012 jboulay.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     jboulay - initial API and implementation
 ******************************************************************************/

package org.talend.designer.cmis.data;

import org.talend.designer.cmis.CMISComponent;

public class CMISEditorManager {
	
	private CMISSessionManager cmisSessionManager;
	private CMISModelManager cmisModelManager;
	private CMISFunctionManager cmisFunctionManager;
	private CMISComponent cmisComponent;

	public CMISEditorManager(CMISComponent component) {
		this.cmisComponent = component;
		this.cmisSessionManager = new CMISSessionManager(component);
		this.cmisModelManager = new CMISModelManager(component, cmisSessionManager);
		this.cmisFunctionManager = new CMISFunctionManager(component, cmisSessionManager);
	}
	
	public CMISComponent getCmisComponent() {
		return cmisComponent;
	}
	
	public CMISFunctionManager getFunctionManager() {
		return cmisFunctionManager;
	}
	
	public CMISModelManager getModelManager() {
		return cmisModelManager;
	}
	
	public CMISSessionManager getSessionManager() {
		return cmisSessionManager;
	}

	public void save() {

		cmisModelManager.save();
		cmisFunctionManager.save();
	}
}
