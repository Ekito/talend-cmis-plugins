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

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.talend.designer.cmis.CMISComponent;

public class CMISSessionManager {
	
	public static final String PARAM_CMIS_SERVER_URL = "CMIS_SERVER_URL";
	public static final String PARAM_CMIS_USER_LOGIN = "CMIS_USER_LOGIN";
	public static final String PARAM_CMIS_USER_PASSWORD = "CMIS_USER_PASSWORD";
	public static final String PARAM_CMIS_REPOSITORY = "CMIS_REPOSITORY";
	public static final String PARAM_COUNTRY_CONNECTION = "COUNTRY_CONNECTION";
	public static final String PARAM_LANGUAGE_CONNECTION = "LANGUAGE_CONNECTION";

	private CMISComponent cmisComponent;
	private Session session;

	public CMISSessionManager(CMISComponent cmisComponent) {
		this.cmisComponent = cmisComponent;
		initSession(cmisComponent);
		
	}

	private void initSession(CMISComponent cmisComponent2) {
		String atomPubURL = (String) cmisComponent.getElementParameter(
				PARAM_CMIS_SERVER_URL).getValue();
		atomPubURL = atomPubURL.replaceAll("\"", "");

		String username = (String) cmisComponent.getElementParameter(
				PARAM_CMIS_USER_LOGIN).getValue();
		username = username.replaceAll("\"", "");

		String password = (String) cmisComponent.getElementParameter(
				PARAM_CMIS_USER_PASSWORD).getValue();
		password = password.replaceAll("\"", "");

		String repositoryId = (String) cmisComponent.getElementParameter(
				PARAM_CMIS_REPOSITORY).getValue();
		repositoryId = repositoryId.replaceAll("\"", "");

		String country = (String) cmisComponent.getElementParameter(
				PARAM_COUNTRY_CONNECTION).getValue();
		country = country.replaceAll("\"", "");

		String language = (String) cmisComponent.getElementParameter(
				PARAM_LANGUAGE_CONNECTION).getValue();
		language = language.replaceAll("\"", "");

		SessionFactory f = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();

		// user credentials
		parameter.put(SessionParameter.USER, username);
		parameter.put(SessionParameter.PASSWORD, password);

		// connection settings
		parameter.put(SessionParameter.ATOMPUB_URL, atomPubURL);
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB
				.value());
		parameter.put(SessionParameter.REPOSITORY_ID, repositoryId);

		// session locale
		parameter.put(SessionParameter.LOCALE_ISO3166_COUNTRY, country);
		parameter.put(SessionParameter.LOCALE_ISO639_LANGUAGE, language);

		// create session
		this.session = f.createSession(parameter);		
	}
	
	public Session getSession() {
		return session;
	}
}
