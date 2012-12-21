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
package org.talend.designer.cmis.query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Query {

	public static String SELECT = "SELECT";
	public static String FROM = "FROM";
	public static String ALIAS = "AS";
	
	private StringBuffer queryStatement;
	
	private HashMap<String,Character> fromDescriptors = new HashMap<String,Character>();
	private HashMap<String,Character> selectFieldDescriptors = new HashMap<String,Character>();
	
	public Query() {
	}
	
	public Character appendFrom(String fromTypeId)
	{
		//Count the number of from in order to get the good letter
		int fromNb = fromDescriptors.size();
		int charNum = fromNb + 97; // 97 is the a letter representation
		Character alias = new Character((char)charNum);
		fromDescriptors.put(fromTypeId, alias);
		
		return alias;
	}
	
	public void appendSelect(Character fromAlias, String propertyId)
	{
		selectFieldDescriptors.put(propertyId, fromAlias);
	}
	
	private void prepareStatement()
	{
		queryStatement = new StringBuffer();
		prepareSelectStatement();
		prepareFromStatement();
		
	}
	
	private void prepareSelectStatement() {
		queryStatement.append(SELECT);
		queryStatement.append(" ");
		
		for (Iterator<Entry<String, Character>> iterator = selectFieldDescriptors.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Character> fieldDescriptor = (Entry<String, Character>) iterator.next();
			
			String propertyId = fieldDescriptor.getKey();
			Character fromAlias = fieldDescriptor.getValue();
			
			queryStatement.append(fromAlias);
			queryStatement.append(".");
			queryStatement.append(propertyId);
			
			if (iterator.hasNext())
			{
				queryStatement.append(", ");
			}else
			{
				queryStatement.append(" ");
			}
		}
	}
	
	private void prepareFromStatement() {
		queryStatement.append(FROM);
		queryStatement.append(" ");
		
		for (Iterator<Entry<String, Character>> iterator = fromDescriptors.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Character> fromDescriptor = (Entry<String, Character>) iterator.next();
			String fromType = fromDescriptor.getKey();
			Character fromAlias = fromDescriptor.getValue();
			
			queryStatement.append(fromType);
			queryStatement.append(" ");
			queryStatement.append(Query.ALIAS);
			queryStatement.append(" ");
			queryStatement.append(fromAlias);
			queryStatement.append(" ");
			
		}
		
	}
	
	public String getQueryStatement()
	{
		prepareStatement();
		return queryStatement.toString();
	}
	
}
