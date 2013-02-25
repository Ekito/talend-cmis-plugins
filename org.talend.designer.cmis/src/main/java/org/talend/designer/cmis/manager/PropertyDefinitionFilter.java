package org.talend.designer.cmis.manager;

import java.util.List;

import org.talend.designer.cmis.model.PropertyDefinitionModel;

public interface PropertyDefinitionFilter {

	public List<PropertyDefinitionModel> filter(
			List<PropertyDefinitionModel> propertyDefinitions);

}
