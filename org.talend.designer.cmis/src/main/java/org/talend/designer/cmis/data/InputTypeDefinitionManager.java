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
import java.util.HashMap;

import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.talend.designer.cmis.CMISComponent;
import org.talend.designer.cmis.query.Query;

/**
 * This class holds the model and UI managers on behalf of the component.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public class InputTypeDefinitionManager extends TypeDefinitionManager {

	public static final String PARAM_QUERY = "QUERY";

	public InputTypeDefinitionManager(CMISComponent cmisComponent, SessionManager sessionManager) {
		super(cmisComponent, sessionManager);
	}

	public PropertyDefinition<?>[] filterPropertyDefinitions(
			PropertyDefinition<?>[] availablePropertyDefinitions) {

		ArrayList<PropertyDefinition<?>> selectablePropertyDefinition = new ArrayList<PropertyDefinition<?>>();

		for (int i = 0; i < availablePropertyDefinitions.length; i++) {
			PropertyDefinition<?> propertyDefinition = availablePropertyDefinitions[i];

			if (propertyDefinition.isQueryable()) {
				selectablePropertyDefinition.add(propertyDefinition);
			}
		}
		return selectablePropertyDefinition.toArray(new PropertyDefinition<?>[selectablePropertyDefinition.size()]);
	}

	/**
	 * Saves the model to the component
	 */
	public void save() {

		super.save();

		this.saveQuery(PARAM_QUERY);

		getCmisComponent().getElementParameter("UPDATE_COMPONENTS").setValue(
				Boolean.TRUE);
	}

	public void saveQuery(String paramQuery) {

		Query query = new Query();

		String objectTypeId = getSelectedTypeDefinition().getObjectTypeId();
		Character alias = query.appendFrom(objectTypeId);

		HashMap<String, PropertyDefinition<?>> queryPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>(
				getSelectedPropertyDefinitions());

		for (PropertyDefinition<?> queryProperty : queryPropertyDefinitions.values()) {
			String propertyId = queryProperty.getId();

			query.appendSelect(alias, propertyId);
		}

		getCmisComponent()
		.getElementParameter(paramQuery).setValue("\"" + query.getQueryStatement() + "\"");
	}

}
