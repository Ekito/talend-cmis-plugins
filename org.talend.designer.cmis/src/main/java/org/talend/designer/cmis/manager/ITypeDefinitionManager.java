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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.talend.core.model.process.IExternalNode;
import org.talend.designer.cmis.model.TypeDefinitionModel;

/**
 * This class holds the model and UI managers on behalf of the component.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public interface ITypeDefinitionManager {

	/**
	 * @return the object types available in the cmis database
	 */
	public ArrayList<TypeDefinitionModel> getAvailableTypeDefinition();
	
	public PropertyDefinition<?>[] getAvailablePropertyDefinitions();

	public HashMap<String, PropertyDefinition<?>> getAvailablePropertyDefinitionsMap();

	public PropertyDefinition<?>[] filterPropertyDefinitions(
			PropertyDefinition<?>[] availablePropertyDefinitions);

	/**
	 * @param selectedTypeDefinition
	 *            the selected object type
	 */
	public void setSelectedObjectTypeNode(
			TypeDefinitionModel selectedTypeDefinition);
	
	/**
	 * @return the selected object type
	 */
	public TypeDefinitionModel getSelectedTypeDefinition();

	public void addSelectedPropertyDefinition(
			PropertyDefinition<?> propertyDefinition);

	public void removeSelectedPropertyDefinition(
			PropertyDefinition<?> propertyDefinition);

	public Map<String, PropertyDefinition<?>> getSelectedPropertyDefinitions();

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
