package org.talend.designer.cmis.model;

import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.Updatability;

public class PropertyDefinitionModel {
	
	private String objectTypeId;
	private PropertyDefinition<?> propertyDefinition;
	
	public PropertyDefinitionModel(String objectTypeId, PropertyDefinition<?> propertyDefinition) {
		super();
		this.objectTypeId = objectTypeId;
		this.propertyDefinition = propertyDefinition;
	}
	
	public String getObjectTypeId() {
		return objectTypeId;
	}

	public String getId() {
		return propertyDefinition.getId();
	}

	public String getPropertyType() {
		return propertyDefinition.getPropertyType().value();
	}

	public String getDefaultValue() {
		return propertyDefinition.getDefaultValue() != null ? propertyDefinition.getDefaultValue().toString() : "";
	}

	public Boolean isMultiValue() {
		return propertyDefinition.getCardinality().equals(Cardinality.MULTI);
	}
	
	public Boolean isRequired() {
		return propertyDefinition.isRequired();
	}
	
	public Boolean isQueryable() {
		return propertyDefinition.isQueryable();
	}
	
	public Boolean isUpdatable() {

		Updatability updatable = propertyDefinition.getUpdatability();
		return !updatable.value().equals(Updatability.READONLY.value());
	}
	
	public String getDisplayName() {
		return propertyDefinition.getDisplayName();
	}
}
