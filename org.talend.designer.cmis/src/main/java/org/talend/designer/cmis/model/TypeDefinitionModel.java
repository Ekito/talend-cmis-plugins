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
package org.talend.designer.cmis.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;

public class TypeDefinitionModel {

	private TypeDefinitionModel parent;
	private ObjectType objectType;
	
	private List<TypeDefinitionModel> children;
	private List<PropertyDefinitionModel> propertyDefinitions;
	
	public TypeDefinitionModel(TypeDefinitionModel parent, ObjectType objectType) {
		super();
		this.parent = parent;
		this.objectType = objectType;
	}
	
	public TypeDefinitionModel getParent() {
		return parent;
	}

	public List<TypeDefinitionModel> getChildren() {
		if (children == null)
		{
			fillChildren(objectType);
		}
		return children;
	}

	public String getObjectTypeId() {
		return objectType.getId();
	}
	
	public String getBaseTypeId() {
		return objectType.getBaseTypeId().value();
	}
	
	public String getDisplayName() {
		return objectType.getDisplayName();
	}
	
	public List<PropertyDefinitionModel> getPropertyDefinitions() {
		if (propertyDefinitions == null)
		{
			fillPropertyDefinitions(objectType);
		}
		return propertyDefinitions;
	}
	
	private void fillPropertyDefinitions(ObjectType objectType) {
		
		Map<String, PropertyDefinition<?>> propertyDefinitionsMap = objectType
				.getPropertyDefinitions();
		
		propertyDefinitions = new ArrayList<PropertyDefinitionModel>();
		
		for (PropertyDefinition<?> propertyDefinition : propertyDefinitionsMap
				.values()) {
			PropertyDefinitionModel propertyDefinitionModel = new PropertyDefinitionModel(objectType.getId(), propertyDefinition);
			propertyDefinitions.add(propertyDefinitionModel);
		}
		
	}

	private void fillChildren(ObjectType objectType) {
		children = new ArrayList<TypeDefinitionModel>();
		ItemIterable<ObjectType> childrenIterable = objectType.getChildren();

		for (Iterator<ObjectType> iterator = childrenIterable.iterator(); iterator
				.hasNext();) {
			ObjectType child = (ObjectType) iterator.next();

			TypeDefinitionModel childNode = new TypeDefinitionModel(this, child);
			children.add(childNode);
		}

	}
	
	public TypeDefinitionModel findById(String objectTypeId) {
		TypeDefinitionModel result = null;

		if (objectTypeId.equals(objectType.getId())) {
			result = this;
		} else {
			for (Iterator<TypeDefinitionModel> iterator = getChildren().iterator(); iterator
					.hasNext()
					&& result == null;) {
				TypeDefinitionModel child = (TypeDefinitionModel) iterator.next();
				result = child.findById(objectTypeId);
			}
		}
		return result;
	}
}
