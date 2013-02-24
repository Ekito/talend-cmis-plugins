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

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IElement;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.IExternalNode;
import org.talend.core.model.utils.ContextParameterUtils;
import org.talend.core.model.utils.TalendTextUtils;

public class SessionManager {
	
	
	public static final String PARAM_CMIS_BINDING_TYPE = "CMIS_BINDING_TYPE";
	public static final String PARAM_CMIS_ATOMPUB_URL = "CMIS_ATOMPUB_URL";
	public static final String PARAM_CMIS_WEBSERVICES_URL = "CMIS_WEBSERVICES_URL";
	public static final String PARAM_CMIS_USER_LOGIN = "CMIS_USER_LOGIN";
	public static final String PARAM_CMIS_USER_PASSWORD = "CMIS_USER_PASSWORD";
	public static final String PARAM_CMIS_REPOSITORY = "CMIS_REPOSITORY";
	public static final String PARAM_COUNTRY_CONNECTION = "COUNTRY_CONNECTION";
	public static final String PARAM_LANGUAGE_CONNECTION = "LANGUAGE_CONNECTION";

	private IExternalNode component;
	private Session session;
	private String bindingType;
	private String atomPubURL;
	private String webServicesURL;
	private String username;
	private String password;
	private String repositoryId;
	private String country;
	private String language;
	
	protected boolean isContextDependant;
	
	public SessionManager(IExternalNode component) {
		this.component = component;
		init(component);
	}

	private void init(IExternalNode cmisComponent) {

		bindingType = loadParameterValue(cmisComponent, PARAM_CMIS_BINDING_TYPE);
		atomPubURL = loadParameterValue(cmisComponent, PARAM_CMIS_ATOMPUB_URL);
		webServicesURL = loadParameterValue(cmisComponent, PARAM_CMIS_WEBSERVICES_URL);
		username = loadParameterValue(cmisComponent, PARAM_CMIS_USER_LOGIN);
		password = loadParameterValue(cmisComponent, PARAM_CMIS_USER_PASSWORD);
		repositoryId = loadParameterValue(cmisComponent, PARAM_CMIS_REPOSITORY);
		country = loadParameterValue(cmisComponent, PARAM_COUNTRY_CONNECTION);
		language = loadParameterValue(cmisComponent, PARAM_LANGUAGE_CONNECTION);
	}
	
	public Map<String, String> getSessionParameters(IContext context) {

		IContext selectedContext = null;

		if (context != null)
		{
			selectedContext = context;
		}
		else if (isContextDependant)
		{
			selectedContext = component.getProcess().getContextManager().getDefaultContext();
		}

		Map<String, String> resolvedSessionParameters = new HashMap<String, String>();

		resolvedSessionParameters = new HashMap<String, String>();

		// user credentials
		resolvedSessionParameters.put(SessionParameter.USER,
				parseScriptContextCode(username, selectedContext));
		resolvedSessionParameters.put(SessionParameter.PASSWORD,
				parseScriptContextCode(password, selectedContext));

		// connection settings
		String resolvedBindingType = parseScriptContextCode(bindingType, selectedContext);

		resolvedSessionParameters.put(SessionParameter.BINDING_TYPE, resolvedBindingType);

		if (resolvedBindingType.equals(BindingType.ATOMPUB.value()))
		{
			resolvedSessionParameters.put(SessionParameter.ATOMPUB_URL,
					parseScriptContextCode(atomPubURL, selectedContext));

		} else if (resolvedBindingType.equals(BindingType.WEBSERVICES.value()))
		{
			String resolvedWSUrl = parseScriptContextCode(webServicesURL, selectedContext);

			resolvedSessionParameters.put(SessionParameter.WEBSERVICES_ACL_SERVICE, resolvedWSUrl + "/ACLService?wsdl");
			resolvedSessionParameters.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, resolvedWSUrl + "/DiscoveryService?wsdl");
			resolvedSessionParameters.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, resolvedWSUrl + "/MultiFilingService?wsdl");
			resolvedSessionParameters.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, resolvedWSUrl + "/NavigationService?wsdl");
			resolvedSessionParameters.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, resolvedWSUrl + "/ObjectService?wsdl");
			resolvedSessionParameters.put(SessionParameter.WEBSERVICES_POLICY_SERVICE, resolvedWSUrl + "/PolicyService?wsdl");
			resolvedSessionParameters.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, resolvedWSUrl + "/RelationshipService?wsdl");
			resolvedSessionParameters.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, resolvedWSUrl + "/RepositoryService?wsdl");
			resolvedSessionParameters.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE, resolvedWSUrl + "/VersioningService?wsdl");
		}

		resolvedSessionParameters.put(SessionParameter.REPOSITORY_ID,
				parseScriptContextCode(repositoryId, selectedContext));

		// session locale
		resolvedSessionParameters.put(SessionParameter.LOCALE_ISO3166_COUNTRY,
				parseScriptContextCode(country, selectedContext));
		resolvedSessionParameters.put(SessionParameter.LOCALE_ISO639_LANGUAGE,
				parseScriptContextCode(language, selectedContext));

		return resolvedSessionParameters;
	}
	
	public boolean isContextDependant()
	{
		return isContextDependant;
	}
	
	protected String loadParameterValue(IElement elem, String key)
	{
		if (elem == null || key == null) {
			return "";
		}

		IElementParameter elemParam = elem.getElementParameter(key);
		if (elemParam != null) {
			Object value = elemParam.getValue();

			if (value instanceof String) {
				if (ContextParameterUtils.isContainContextParam((String)value))
				{
					isContextDependant = true;
				}
				return (String) value;
			}
		}
		return "";
	}
	
	protected String parseScriptContextCode(String code, IContext context)
	{
		code = ContextParameterUtils.parseScriptContextCode(code, context);
		return TalendTextUtils.removeQuotes(code);
	}

	public Session getSession() {
		return session;
	}
	
	public void createSession(IContext context) {
		Map<String, String> parameters = getSessionParameters(context);
		SessionFactory f = SessionFactoryImpl.newInstance();
		session = f.createSession(parameters);
	}
}
