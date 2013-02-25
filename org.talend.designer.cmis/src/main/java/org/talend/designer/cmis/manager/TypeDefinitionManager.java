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

import java.util.List;
import java.util.Map;

import org.talend.core.model.process.IExternalNode;
import org.talend.designer.cmis.model.PropertyDefinitionModel;
import org.talend.designer.cmis.model.TypeDefinitionModel;

/**
 * This class holds the model and UI managers on behalf of the component.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public interface TypeDefinitionManager {

	/**
	 * @return the object types available in the cmis database
	 */
	public List<TypeDefinitionModel> getAvailableTypeDefinition();
	
	public List<PropertyDefinitionModel> getAvailablePropertyDefinitions();

	public Map<String, PropertyDefinitionModel> getAvailablePropertyDefinitionsMap();

	/**
	 * @param selectedTypeDefinition
	 *            the selected object type
	 */
	public void setSelectedTypeDefinitionModel(
			TypeDefinitionModel selectedTypeDefinition);
	
	/**
	 * @return the selected object type
	 */
	public TypeDefinitionModel getSelectedTypeDefinition();

	public void addSelectedPropertyDefinition(
			PropertyDefinitionModel propertyDefinition);

	public void removeSelectedPropertyDefinition(
			PropertyDefinitionModel propertyDefinitionModel);

	public Map<String, PropertyDefinitionModel> getSelectedPropertyDefinitions();

	/**
	 * Clears the model
	 */
	public void clear();

	/**
	 * Loads the model of the component
	 * 
	 */
	public void load();
	
	/**
	 * Saves the model to the component
	 */
	public void save();
	
	public IExternalNode getComponent();

}
