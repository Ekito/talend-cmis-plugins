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

import java.util.List;

import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.designer.cmis.CMISComponent;

public class CMISPropertyLabelProvider implements ITableLabelProvider {

	public static Image COLUMN_ICON = ImageDescriptor.createFromFile(CMISComponent.class,"/icons/columns.gif").createImage();
	
	public Image getColumnImage(Object element, int columnIndex) {
		
		if (columnIndex == 0)
		{
			return COLUMN_ICON;
		}
		else if (columnIndex == 3)
		{
			PropertyDefinition<?> propertyDefinition = (PropertyDefinition<?>) element;
			if (propertyDefinition.isRequired()) {
	            return ImageProvider.getImage(ECoreImage.MODULE_REQUIRED_ICON);
	        } else {
	            return ImageProvider.getImage(ECoreImage.MODULE_NOTREQUIRED_ICON);
	        }
		}
		
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		PropertyDefinition<?> propertyDefinition = (PropertyDefinition<?>) element;
		String result = null;
		List<?> defaultValue;
		switch (columnIndex) {
		case 0:
			result = propertyDefinition.getId();
			break;
		case 1 :
			result = propertyDefinition.getDisplayName();
			break;
		case 2 :
			result = propertyDefinition.getPropertyType().value();
			break;
		case 3 :
			result = "";
			break;
		case 4 :
			defaultValue = propertyDefinition.getDefaultValue();
			result =  defaultValue != null ? defaultValue.toString() : null;
			break;
		default :
			break; 	
	}
	return result;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}
}
