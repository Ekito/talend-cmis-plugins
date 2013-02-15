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
package org.talend.designer.cmis.ui.metadata;

import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class PropertyDefinitionComparator extends ViewerComparator {

	public final static int ID	= 0;
	public final static int NAME		= 1;
	public final static int TYPE		= 2;
	public final static int MANDATORY	= 3;
	public final static int DEFAULT		= 4;
	
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public PropertyDefinitionComparator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}
	
	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		PropertyDefinition<?> prop1 = (PropertyDefinition<?>) e1;
		PropertyDefinition<?> prop2 = (PropertyDefinition<?>) e2;
		int result = 0;
		
		switch (propertyIndex) {
		case ID:
			result = prop1.getId().compareTo(prop2.getId());
			break;
		case NAME:
			result = prop1.getDisplayName().compareTo(prop2.getDisplayName());
			break;
		case TYPE:
			result = prop1.getPropertyType().compareTo(prop2.getPropertyType());
			break;
		case MANDATORY:
			result = prop1.isRequired().compareTo(prop2.isRequired());
			break;
		case DEFAULT:
			result = prop1.getDefaultValue() != null ? prop1.getDefaultValue().toString().compareTo(prop2.getDefaultValue().toString()) : 0;
			break;

		default:
			break;
		}
		
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			result = -result;
		}

		return result;
	}
	
	
	
}
