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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;

public class CMISObjectTypeNode {

	private CMISObjectTypeNode parent;
	private String objectTypeId;
	private String baseTypeId;
	
	private ArrayList<CMISObjectTypeNode> children;
	private PropertyDefinition<?>[] propertyDefinitions;
	private String displayName;
	private ObjectType objectType;
	
	public CMISObjectTypeNode(CMISObjectTypeNode parent, ObjectType objectType) {
		super();
		this.objectType = objectType;
		this.parent = parent;
		this.objectTypeId = objectType.getId();
		this.baseTypeId = objectType.getBaseTypeId().value();
		this.displayName = objectType.getDisplayName();
	}
	
	public CMISObjectTypeNode getParent() {
		return parent;
	}

	public ArrayList<CMISObjectTypeNode> getChildren() {
		if (children == null)
		{
			fillChildren(objectType);
		}
		return children;
	}

	public String getObjectTypeId() {
		return objectTypeId;
	}
	
	public String getBaseTypeId() {
		return baseTypeId;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public PropertyDefinition<?>[] getPropertyDefinitions() {
		if (propertyDefinitions == null)
		{
			fillPropertyDefinitions(objectType);
		}
		return propertyDefinitions;
	}
	
	private void fillPropertyDefinitions(ObjectType objectType) {
		Map<String, PropertyDefinition<?>> propertyDefinitionsMap = objectType.getPropertyDefinitions();
		Collection<PropertyDefinition<?>> propertyDefinitionCollection = propertyDefinitionsMap.values();
		propertyDefinitions = propertyDefinitionCollection.toArray(new PropertyDefinition<?>[propertyDefinitionCollection.size()]);
	}

	private void fillChildren(ObjectType objectType) {
		children = new ArrayList<CMISObjectTypeNode>();
		ItemIterable<ObjectType> childrenIterable = objectType.getChildren();

		for (Iterator<ObjectType> iterator = childrenIterable.iterator(); iterator
				.hasNext();) {
			ObjectType child = (ObjectType) iterator.next();

			CMISObjectTypeNode childNode = new CMISObjectTypeNode(this, child);
			children.add(childNode);
		}

	}
	
	public CMISObjectTypeNode findById(String objectTypeId) {
		CMISObjectTypeNode result = null;

		if (objectTypeId.equals(this.objectTypeId)) {
			result = this;
		} else {
			for (Iterator<CMISObjectTypeNode> iterator = getChildren().iterator(); iterator
					.hasNext()
					&& result == null;) {
				CMISObjectTypeNode child = (CMISObjectTypeNode) iterator.next();
				result = child.findById(objectTypeId);
			}
		}
		return result;
	}
}
