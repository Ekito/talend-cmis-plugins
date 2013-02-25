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

package org.talend.designer.cmis.manager;

import java.util.Map;

import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IExternalNode;
import org.talend.designer.cmis.manager.impl.SessionManager;
import org.talend.designer.cmis.model.PropertyDefinitionModel;
import org.talend.designer.cmis.model.TypeDefinitionModel;

public class EditorManager {
	
	private SessionManager sessionManager;
	private DefaultTypeDefinitionManagerImpl defaultTypeDefinitionManagerImpl;
	private FunctionManager functionManager;
	private QueryManager queryManager;
	private IExternalNode component;

	public EditorManager(IExternalNode component) {
		this.component = component;
		this.sessionManager = new SessionManager(component);
		this.defaultTypeDefinitionManagerImpl = new DefaultTypeDefinitionManagerImpl(component, sessionManager);
		this.functionManager = new FunctionManager(component, sessionManager);
	}
	
	public IExternalNode getComponent() {
		return component;
	}
	
	public FunctionManager getFunctionManager() {
		return functionManager;
	}
	
	public DefaultTypeDefinitionManagerImpl getTypeDefinitionManager() {
		return defaultTypeDefinitionManagerImpl;
	}
	
	public SessionManager getSessionManager() {
		return sessionManager;
	}
	
	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}

	public void createSession(IContext context) {
		sessionManager.createSession(context);
	}
	
	public void loadModel()
	{
		defaultTypeDefinitionManagerImpl.load();
	}

	public void save() {

		defaultTypeDefinitionManagerImpl.save();
		saveFunction();
		saveQuery();
		
		component.getElementParameter("UPDATE_COMPONENTS").setValue(
				Boolean.TRUE);
	}
	
	private void saveFunction() {
		if (functionManager != null) {
			functionManager.save();
		}
	}

	private void saveQuery()
	{
		if (queryManager != null)
		{
			TypeDefinitionModel selectedTypeDefinitionModel = defaultTypeDefinitionManagerImpl.getSelectedTypeDefinition();
			Map<String, PropertyDefinitionModel> selectedPropertyDefinitions = defaultTypeDefinitionManagerImpl.getSelectedPropertyDefinitions();
			queryManager.saveQuery(selectedTypeDefinitionModel, selectedPropertyDefinitions);
		}
	}
}
