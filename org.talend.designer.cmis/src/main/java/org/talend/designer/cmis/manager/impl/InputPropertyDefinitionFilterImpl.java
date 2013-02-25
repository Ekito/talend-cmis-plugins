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

import org.talend.designer.cmis.manager.PropertyDefinitionFilter;
import org.talend.designer.cmis.model.PropertyDefinitionModel;

/**
 * Filters properties in the propertyDefinition panel. Queryable properties are the only selected properties.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public class InputPropertyDefinitionFilterImpl implements PropertyDefinitionFilter {

	
	public List<PropertyDefinitionModel> filter(
			List<PropertyDefinitionModel> propertyDefinitions) {

		ArrayList<PropertyDefinitionModel> selectablePropertyDefinition = new ArrayList<PropertyDefinitionModel>();

		for (PropertyDefinitionModel propertyDefinition : propertyDefinitions) {
			
			if (propertyDefinition.isQueryable()) {
				selectablePropertyDefinition.add(propertyDefinition);
			}
		}
		return selectablePropertyDefinition;
	}

}
