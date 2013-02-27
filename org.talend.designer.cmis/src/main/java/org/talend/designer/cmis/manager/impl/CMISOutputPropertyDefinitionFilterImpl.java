package org.talend.designer.cmis.manager.impl;

import org.talend.designer.cmis.manager.OutputPropertyDefinitionFilterImpl;
import org.talend.designer.cmis.model.PropertyDefinitionModel;


public class CMISOutputPropertyDefinitionFilterImpl extends
		OutputPropertyDefinitionFilterImpl {

	public static final String OBJECT_TYPE_ID = "cmis:objectTypeId";
	
	@Override
	public boolean isSelectable(PropertyDefinitionModel propertyDefinitionModel) {
		return super.isSelectable(propertyDefinitionModel) && !propertyDefinitionModel.getId().equals(OBJECT_TYPE_ID);
	}
}
