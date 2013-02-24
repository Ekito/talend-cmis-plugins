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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.talend.core.model.process.IExternalNode;
import org.talend.designer.cmis.model.TypeDefinitionModel;

/**
 * This class holds the model and UI managers on behalf of the component.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public abstract class TypeDefinitionManager implements ITypeDefinitionManager{

	public static final String PARAM_OBJECT_TYPE = "OBJECT_TYPE";

	public static final String PARAM_BASE_TYPE_ID = "BASE_TYPE_ID";

	// property mapping and additional items
	public static final String PARAM_PROPERTY_MAPPING = "PROPERTY_MAPPING";

	public static final String PARAM_OBJECT_TYPE_ID = "OBJECT_TYPE_ID";
	
	public static final String PARAM_ITEM_ID = "ID";

	public static final String PARAM_ITEM_TYPE = "TYPE";

	public static final String PARAM_ITEM_DEFAULT = "DEFAULT";

	private IExternalNode component;
	
	private SessionManager sessionManager;

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	// data model
	private ArrayList<TypeDefinitionModel> availableTypeDefinitionModel = new ArrayList<TypeDefinitionModel>();
	private PropertyDefinition<?>[] availablePropertyDefinitions;
	private HashMap<String, PropertyDefinition<?>> availablePropertyDefinitionsMap;
	
	private TypeDefinitionModel selectedTypeDefinitionModel;
	private Map<String, PropertyDefinition<?>> selectedPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

	public TypeDefinitionManager(IExternalNode component, SessionManager sessionManager) {
		this.component = component;
		this.sessionManager = sessionManager;
	}

	/**
	 * @return the object types available in the cmis database
	 */
	public ArrayList<TypeDefinitionModel> getAvailableTypeDefinition() {
		return availableTypeDefinitionModel;
	}
	
	public PropertyDefinition<?>[] getAvailablePropertyDefinitions() {
		return availablePropertyDefinitions;
	}

	public HashMap<String, PropertyDefinition<?>> getAvailablePropertyDefinitionsMap() {
		return availablePropertyDefinitionsMap;
	}

	/**
	 * @param selectedTypeDefinitionModel
	 *            the selected object type
	 */
	public void setSelectedObjectTypeNode(
			TypeDefinitionModel selectedObjectTypeNode) {
		this.selectedTypeDefinitionModel = selectedObjectTypeNode;
		
		PropertyDefinition<?>[] availablePropertyDefinitions = selectedObjectTypeNode.getPropertyDefinitions();
		availablePropertyDefinitions = filterPropertyDefinitions(availablePropertyDefinitions);
		this.availablePropertyDefinitions = availablePropertyDefinitions;
		
		HashMap<String, PropertyDefinition<?>> propertyDefinitionMap = new HashMap<String, PropertyDefinition<?>>();

		for (int i = 0; i < availablePropertyDefinitions.length; i++) {
			PropertyDefinition<?> propertyDefinition = availablePropertyDefinitions[i];
			propertyDefinitionMap.put(propertyDefinition.getId(), propertyDefinition);
		}
		
		this.availablePropertyDefinitionsMap = propertyDefinitionMap;
	}
	
	/**
	 * @return the selected TypeDefinition
	 */
	public TypeDefinitionModel getSelectedTypeDefinition() {
		return selectedTypeDefinitionModel;
	}

	public void addSelectedPropertyDefinition(
			PropertyDefinition<?> propertyDefinition) {
		selectedPropertyDefinitions.put(propertyDefinition.getId(),
				propertyDefinition);
	}

	public void removeSelectedPropertyDefinition(
			PropertyDefinition<?> propertyDefinition) {
		selectedPropertyDefinitions.remove(propertyDefinition.getId());
	}

	public void clearSelectedPropertyDefinition() {
		selectedPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
	}
	
	public Map<String, PropertyDefinition<?>> getSelectedPropertyDefinitions() {
		return selectedPropertyDefinitions;
	}

	/**
	 * Clears the model
	 */
	public void clear() {
		selectedTypeDefinitionModel = null;
		selectedPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
		availableTypeDefinitionModel = new ArrayList<TypeDefinitionModel>();
	}

	/**
	 * Loads the model of the component
	 * 
	 */
	public void load() {
		this.clear();

		// load all the TypeDefinition from the CMIS server.
		ItemIterable<ObjectType> availableObjectTypesIterable = getSessionManager().getSession()
				.getTypeChildren(null, true);

		// Get the selected object type
		String selectedTypeDefinition = (String) getComponent()
				.getElementParameter(PARAM_OBJECT_TYPE).getValue();

		selectedTypeDefinition = selectedTypeDefinition.replaceAll("\"", "");

		// Create a real model for the TreeViewer for a better management of the
		// tree nodes
		for (Iterator<ObjectType> iterator = availableObjectTypesIterable
				.iterator(); iterator.hasNext();) {
			ObjectType availableObjectType = (ObjectType) iterator.next();

			TypeDefinitionModel typeDefinition = new TypeDefinitionModel(null,
					availableObjectType);
			getAvailableTypeDefinition().add(typeDefinition);

			TypeDefinitionModel foundTypeDefinition = typeDefinition
					.findById(selectedTypeDefinition);

			if (foundTypeDefinition != null)
				setSelectedObjectTypeNode(foundTypeDefinition);
				
		}
		//Select the first node if any selected
		if (getSelectedTypeDefinition() == null)
		{
			setSelectedObjectTypeNode(getAvailableTypeDefinition().get(0));
		}
		
		// Get the selected object properties
		this.loadMetadatas(PARAM_PROPERTY_MAPPING);

	}

	/**
	 * Saves the model to the component
	 */
	public void save() {

		component.getElementParameter(PARAM_OBJECT_TYPE).setValue(
				selectedTypeDefinitionModel.getObjectTypeId());

		component.getElementParameter(PARAM_OBJECT_TYPE_ID).setValue(
				"\"" + selectedTypeDefinitionModel.getObjectTypeId() + "\"");

		component.getElementParameter(PARAM_BASE_TYPE_ID).setValue(
				selectedTypeDefinitionModel.getBaseTypeId());

		// Save the available metadatas;
		this.saveMetadatas(PARAM_PROPERTY_MAPPING);

		component.getElementParameter("UPDATE_COMPONENTS").setValue(
				Boolean.TRUE);
	}

	/**
	 * Load the metadatas in the model manager
	 * 
	 * @param paramPropertyMapping
	 */
	@SuppressWarnings("unchecked")
	private void loadMetadatas(String metadataMappingParamName) {

		List<Map<String, String>> metadataMappingTable = (List<Map<String, String>>) component
				.getElementParameter(metadataMappingParamName).getValue();

		HashMap<String, PropertyDefinition<?>> propertyDefinitionMap = getAvailablePropertyDefinitionsMap();
		
		for (Map<String, String> metadataMappingRow : new ArrayList<Map<String, String>>(
				metadataMappingTable)) {
			String propertyId = metadataMappingRow.get(PARAM_ITEM_ID);

			PropertyDefinition<?> propertyDefinition = propertyDefinitionMap.get(propertyId);
			selectedPropertyDefinitions.put(propertyId, propertyDefinition);

		}
	}

	/**
	 * Saves the given metadata to the given map
	 * 
	 * @param metadataMappingParamName
	 * @param objectTypeId
	 */
	@SuppressWarnings("unchecked")
	private void saveMetadatas(String metadataMappingParamName) {
		List<Map<String, String>> metadataMappingTable = (List<Map<String, String>>) component
				.getElementParameter(metadataMappingParamName).getValue();

		HashMap<String, PropertyDefinition<?>> remainingPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>(
				selectedPropertyDefinitions);

		// handling previously know metadata ; using a copy of their list so
		// we're able do removes in it
		for (Map<String, String> metadataMappingRow : new ArrayList<Map<String, String>>(
				metadataMappingTable)) {
			String propertyId = metadataMappingRow.get(PARAM_ITEM_ID);

			
			// trying to find the corresponding existing metadata...
			PropertyDefinition<?> propertyDef = selectedPropertyDefinitions
					.get(propertyId);

			if (propertyDef == null) {
				// has been removed ; let's remove it from the parameter
				metadataMappingTable.remove(metadataMappingRow);
				continue;
			}

			remainingPropertyDefinitions.remove(propertyId);
		}

		// let's add the remaining metadata as new rows
		for (PropertyDefinition<?> propertyDefinition : remainingPropertyDefinitions
				.values()) {
			Map<String, String> metadataMappingRow = new HashMap<String, String>();
			fillMetadataMappingRow(metadataMappingRow, propertyDefinition,
					metadataMappingParamName);
			metadataMappingTable.add(metadataMappingRow);
		}

		// Sort the mapping table
		Collections.sort(metadataMappingTable,
				new Comparator<Map<String, String>>() {
					public int compare(Map<String, String> o1,
							Map<String, String> o2) {
						return o1.get(PARAM_ITEM_ID).compareTo(
								o2.get(PARAM_ITEM_ID));
					};
				});
	}

	/**
	 * Fills a (parameter) metadata row with metadata info
	 * 
	 * @param metadataMappingRow
	 * @param propertyDef
	 * @param metadataMappingParamName
	 */
	private void fillMetadataMappingRow(Map<String, String> metadataMappingRow,
			PropertyDefinition<?> propertyDef, String metadataMappingParamName) {
		if (PARAM_PROPERTY_MAPPING.equals(metadataMappingParamName)) {
			// case properties :
			fillPropertyMappingRow(metadataMappingRow, propertyDef);
		}
	}

	/**
	 * Fills a (parameter) property metadata row with property info
	 * 
	 * @param metadataMappingRow
	 * @param propertyDef
	 */
	private void fillPropertyMappingRow(
			Map<String, String> metadataMappingRow,
			PropertyDefinition<?> propertyDef) {

		if (metadataMappingRow != null
				&& propertyDef != null)
		{
			String objectTypeId = getSelectedTypeDefinition().getObjectTypeId();
			metadataMappingRow.put(PARAM_OBJECT_TYPE_ID, objectTypeId );
			metadataMappingRow.put(PARAM_ITEM_ID, propertyDef.getId());
			metadataMappingRow.put(PARAM_ITEM_TYPE, propertyDef.getPropertyType()
					.value());
			List<?> defaultValue = propertyDef.getDefaultValue(); 
			if (defaultValue != null)
			{
				metadataMappingRow.put(PARAM_ITEM_DEFAULT,	defaultValue.toString());
			}
		}
	}
	public IExternalNode getComponent() {
		return component;
	}

}
