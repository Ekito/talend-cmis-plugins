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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.talend.designer.cmis.CMISComponent;
import org.talend.designer.cmis.query.Query;

/**
 * This class holds the model and UI managers on behalf of the component.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public class CMISModelManager {

	public static final String PARAM_OBJECT_TYPE = "OBJECT_TYPE";

	public static final String PARAM_BASE_TYPE_ID = "BASE_TYPE_ID";

	public static final String PARAM_QUERY = "QUERY";

	// property mapping and additional items
	public static final String PARAM_PROPERTY_MAPPING = "PROPERTY_MAPPING";

	public static final String PARAM_ITEM_ID = "ID";

	public static final String PARAM_ITEM_NAME = "NAME";

	public static final String PARAM_ITEM_TYPE = "TYPE";

	public static final String PARAM_ITEM_MANDATORY = "MANDATORY";

	public static final String PARAM_ITEM_DEFAULT = "DEFAULT";

	private static final int UNKNOWN_FILTER = 0;
	
	private static final int QUERYABLE_FILTER = 1;
	
	private static final int UPDATABLE_FILTER = 2;
	
	private CMISComponent cmisComponent;
	private CMISSessionManager cmisSessionManager;

	// data model
	private ArrayList<CMISObjectTypeNode> availableObjectTypeNodes = new ArrayList<CMISObjectTypeNode>();
	private PropertyDefinition<?>[] availablePropertyDefinitions;
	private HashMap<String, PropertyDefinition<?>> availablePropertyDefinitionsMap;
	
	private CMISObjectTypeNode selectedObjectTypeNode;
	private Map<String, PropertyDefinition<?>> selectedPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

	public CMISModelManager(CMISComponent cmisComponent, CMISSessionManager sessionManager) {
		this.cmisComponent = cmisComponent;
		this.cmisSessionManager = sessionManager;
	}

	/**
	 * @return the object types available in the cmis database
	 */
	public ArrayList<CMISObjectTypeNode> getAvailableObjectTypeNodes() {
		return availableObjectTypeNodes;
	}
	
	public PropertyDefinition<?>[] getAvailablePropertyDefinitions() {
		return availablePropertyDefinitions;
	}

	public HashMap<String, PropertyDefinition<?>> getAvailablePropertyDefinitionsMap() {
		return availablePropertyDefinitionsMap;
	}

	public void setAvailablePropertyDefinitionsMap(
			HashMap<String, PropertyDefinition<?>> availablePropertyDefinitionMap) {
		this.availablePropertyDefinitionsMap = availablePropertyDefinitionMap;
	}

	private PropertyDefinition<?>[] filterPropertyDefinitions(
			PropertyDefinition<?>[] availablePropertyDefinitions) {
		
		String componentName = (String) cmisComponent.getElementParameter("COMPONENT_NAME").getValue();
		int filterType = UNKNOWN_FILTER;
		
		if (componentName.endsWith(CMISComponent.INPUT_COMPONENT_SUFFIX))
			filterType = QUERYABLE_FILTER;
		else if (componentName.endsWith(CMISComponent.OUTPUT_COMPONENT_SUFFIX))
			filterType = UPDATABLE_FILTER;
		
		if (filterType == UNKNOWN_FILTER)
		{
			return availablePropertyDefinitions;
		}
		
		ArrayList<PropertyDefinition<?>> selectablePropertyDefinition = new ArrayList<PropertyDefinition<?>>();
		
		for (int i = 0; i < availablePropertyDefinitions.length; i++) {
			PropertyDefinition<?> propertyDefinition = availablePropertyDefinitions[i];
			
			switch (filterType) {
			case QUERYABLE_FILTER:
				if (propertyDefinition.isQueryable())
					selectablePropertyDefinition.add(propertyDefinition);
				break;
			case UPDATABLE_FILTER:
				Updatability updatable = propertyDefinition.getUpdatability();
				//Remove READONLY properties and ObjectTypeId
				if (!propertyDefinition.getId().equals(PropertyIds.OBJECT_TYPE_ID)
						&& !updatable.value().equals(Updatability.READONLY.value()))
					selectablePropertyDefinition.add(propertyDefinition);
				break;
			default:
				selectablePropertyDefinition.add(propertyDefinition);
				break;
			}
		}
		return selectablePropertyDefinition.toArray(new PropertyDefinition<?>[selectablePropertyDefinition.size()]);
	}

	/**
	 * @param selectedObjectTypeNode
	 *            the selected object type
	 */
	public void setSelectedObjectTypeNode(
			CMISObjectTypeNode selectedObjectTypeNode) {
		this.selectedObjectTypeNode = selectedObjectTypeNode;
		
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
	 * @return the selected object type
	 */
	public CMISObjectTypeNode getSelectedObjectTypeNode() {
		return selectedObjectTypeNode;
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
		selectedObjectTypeNode = null;
		selectedPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
		availableObjectTypeNodes = new ArrayList<CMISObjectTypeNode>();
	}

	/**
	 * Loads the model of the component
	 * 
	 */
	public void load() {
		this.clear();

		// load all the objectTypes from the CMIS server.
		//TODO : optimiser en ne chargeant que les noeuds nécessaires !!
		ItemIterable<ObjectType> availableObjectTypesIterable = cmisSessionManager.getSession()
				.getTypeChildren(null, true);

		// Get the selected object type
		String selectedObjectTypeId = (String) cmisComponent
				.getElementParameter(PARAM_OBJECT_TYPE).getValue();

		selectedObjectTypeId = selectedObjectTypeId.replaceAll("\"", "");

		// Create a real model for the TreeViewer for a better management of the
		// tree nodes
		for (Iterator<ObjectType> iterator = availableObjectTypesIterable
				.iterator(); iterator.hasNext();) {
			ObjectType availableObjectType = (ObjectType) iterator.next();

			CMISObjectTypeNode objectTypeNode = new CMISObjectTypeNode(null,
					availableObjectType);
			availableObjectTypeNodes.add(objectTypeNode);

			CMISObjectTypeNode foundObjectTypeNode = objectTypeNode
					.findById(selectedObjectTypeId);

			if (foundObjectTypeNode != null)
				setSelectedObjectTypeNode(foundObjectTypeNode);
				
		}
		//Select the first node if any selected
		if (getSelectedObjectTypeNode() == null)
		{
			setSelectedObjectTypeNode(availableObjectTypeNodes.get(0));
		}
		
		// Get the selected object properties
		this.loadMetadatas(PARAM_PROPERTY_MAPPING);

	}

	/**
	 * Saves the model to the component
	 */
	public void save() {

		cmisComponent.getElementParameter(PARAM_OBJECT_TYPE).setValue(
				selectedObjectTypeNode.getObjectTypeId());

		cmisComponent.getElementParameter(PARAM_BASE_TYPE_ID).setValue(
				selectedObjectTypeNode.getBaseTypeId());

		// Save the available metadatas;
		this.saveMetadatas(PARAM_PROPERTY_MAPPING);

		// Save the query
		String componentName = (String) cmisComponent.getElementParameter("COMPONENT_NAME").getValue();
		
		if (componentName.endsWith(CMISComponent.INPUT_COMPONENT_SUFFIX))
			this.saveQuery(PARAM_QUERY);
		
		cmisComponent.getElementParameter("UPDATE_COMPONENTS").setValue(
				Boolean.TRUE);
	}

	/**
	 * Load the metadatas in the model manager
	 * 
	 * @param paramPropertyMapping
	 */
	@SuppressWarnings("unchecked")
	private void loadMetadatas(String metadataMappingParamName) {

		List<Map<String, String>> metadataMappingTable = (List<Map<String, String>>) cmisComponent
				.getElementParameter(metadataMappingParamName).getValue();

		HashMap<String, PropertyDefinition<?>> propertyDefinitionMap = getAvailablePropertyDefinitionsMap();
		
		for (Map<String, String> metadataMappingRow : new ArrayList<Map<String, String>>(
				metadataMappingTable)) {
			String propertyId = metadataMappingRow.get(PARAM_ITEM_ID);

			PropertyDefinition<?> propertyDefinition = propertyDefinitionMap.get(propertyId);
			selectedPropertyDefinitions.put(propertyId, propertyDefinition);

		}
	}

	private void saveQuery(String paramQuery) {

		Query query = new Query();
		
		String objectTypeId = selectedObjectTypeNode.getObjectTypeId();
		Character alias = query.appendFrom(objectTypeId);
		
		HashMap<String, PropertyDefinition<?>> queryPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>(
				selectedPropertyDefinitions);

		for (PropertyDefinition<?> queryProperty : queryPropertyDefinitions.values()) {
			String propertyId = queryProperty.getId();
			
			query.appendSelect(alias, propertyId);
		}
		
		cmisComponent
		.getElementParameter(paramQuery).setValue("\"" + query.getQueryStatement() + "\"");
	}
	
	/**
	 * Saves the given metadata to the given map
	 * 
	 * @param metadataMappingParamName
	 * @param objectTypeId
	 */
	@SuppressWarnings("unchecked")
	protected void saveMetadatas(String metadataMappingParamName) {
		List<Map<String, String>> metadataMappingTable = (List<Map<String, String>>) cmisComponent
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
	protected void fillPropertyMappingRow(
			Map<String, String> metadataMappingRow,
			PropertyDefinition<?> propertyDef) {
		// NB. reputting PARAM_ITEM_NAME is not required if the metadata with
		// such a name already existed, but not a problem either
		if (metadataMappingRow != null
				&& propertyDef != null)
		{
			metadataMappingRow.put(PARAM_ITEM_ID, propertyDef.getId());
			metadataMappingRow.put(PARAM_ITEM_NAME, propertyDef.getDisplayName());
			metadataMappingRow.put(PARAM_ITEM_TYPE, propertyDef.getPropertyType()
					.value());
			metadataMappingRow.put(PARAM_ITEM_MANDATORY, propertyDef.isRequired()
					.toString());
			List<?> defaultValue = propertyDef.getDefaultValue(); 
			if (defaultValue != null)
			{
				metadataMappingRow.put(PARAM_ITEM_DEFAULT,	defaultValue.toString());
			}
		}
	}

	public CMISComponent getCmisComponent() {
		return cmisComponent;
	}

}
