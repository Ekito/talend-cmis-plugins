package org.talend.designer.cmis.manager;

import java.util.Map;

import org.talend.core.model.process.IExternalNode;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.designer.cmis.model.PropertyDefinitionModel;
import org.talend.designer.cmis.model.Query;
import org.talend.designer.cmis.model.TypeDefinitionModel;

public class QueryManager {

	public static final String PARAM_QUERY = "QUERY";
	
	private IExternalNode component;
	
	public QueryManager(IExternalNode component) {
		this.component = component;
	}
	
	public void saveQuery(TypeDefinitionModel selectedTypeDefinitionModel,
			Map<String, PropertyDefinitionModel> selectedPropertyDefinitions) {

		Query query = new Query();

		String objectTypeId = selectedTypeDefinitionModel.getObjectTypeId();
		Character alias = query.appendFrom(objectTypeId);

		for (PropertyDefinitionModel queryProperty : selectedPropertyDefinitions.values()) {
			String propertyId = queryProperty.getId();

			query.appendSelect(alias, propertyId);
		}

		String queryStatement = TalendTextUtils.addQuotes(query.getQueryStatement());
		component.getElementParameter(PARAM_QUERY).setValue(queryStatement);
		
	}
}
