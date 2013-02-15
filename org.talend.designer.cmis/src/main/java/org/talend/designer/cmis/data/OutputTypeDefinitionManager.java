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

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.talend.designer.cmis.CMISComponent;

/**
 * This class holds the model and UI managers on behalf of the component.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public class OutputTypeDefinitionManager extends TypeDefinitionManager{

	public OutputTypeDefinitionManager(CMISComponent cmisComponent, SessionManager sessionManager) {
		super(cmisComponent, sessionManager);
	}

	public PropertyDefinition<?>[] filterPropertyDefinitions(
			PropertyDefinition<?>[] availablePropertyDefinitions) {
		
		ArrayList<PropertyDefinition<?>> selectablePropertyDefinition = new ArrayList<PropertyDefinition<?>>();
		
		for (int i = 0; i < availablePropertyDefinitions.length; i++) {
			PropertyDefinition<?> propertyDefinition = availablePropertyDefinitions[i];
			
			Updatability updatable = propertyDefinition.getUpdatability();
			//Remove READONLY properties and ObjectTypeId
			if (!propertyDefinition.getId().equals(PropertyIds.OBJECT_TYPE_ID) 
					&& !updatable.value().equals(Updatability.READONLY.value())) {
				selectablePropertyDefinition.add(propertyDefinition);
			}
		}
		return selectablePropertyDefinition.toArray(new PropertyDefinition<?>[selectablePropertyDefinition.size()]);
	}

}
