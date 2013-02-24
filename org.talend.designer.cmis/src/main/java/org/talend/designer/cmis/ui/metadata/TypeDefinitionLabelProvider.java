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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.talend.designer.cmis.CMISComponent;
import org.talend.designer.cmis.model.TypeDefinitionModel;

/**
 * A label provider that display the icon and name of the object type.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public class TypeDefinitionLabelProvider implements ILabelProvider {

	public static Image TABLE_ICON = ImageDescriptor.createFromFile(CMISComponent.class,"/icons/table.gif").createImage();
	
	public Image getImage(Object element) {
		return TABLE_ICON;
	}

	public String getText(Object element) {
		return ((TypeDefinitionModel)element).getDisplayName();
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
