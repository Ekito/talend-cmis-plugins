package org.talend.designer.cmis.manager;

import org.talend.designer.cmis.model.PropertyDefinitionModel;

public interface PropertyDefinitionFilter {

	public boolean isSelectable(
			PropertyDefinitionModel propertyDefinitionModel);

}
