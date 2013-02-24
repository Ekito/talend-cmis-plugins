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
package org.talend.designer.cmis.ui.folder;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.talend.designer.cmis.CMISComponent;
import org.talend.designer.cmis.model.FolderModel;

/**
 * A label provider that display the icon and name of the object type.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public class FolderLabelProvider implements ILabelProvider {

	public static Image CLOSED_FOLDER_ICON = ImageDescriptor.createFromFile(CMISComponent.class,"/icons/closedFolder.gif").createImage();
	public static Image OPEN_FOLDER_ICON = ImageDescriptor.createFromFile(CMISComponent.class,"/icons/openFolder.gif").createImage();

	public Image getImage(Object element) {
		Image result = CLOSED_FOLDER_ICON;
//	    CMISFolderNode node = (CMISFolderNode) element;
//	    if (node.hasChildren()) {
//	      result = getExpandedState(element) ? OPEN_FOLDER_ICON : CLOSED_FOLDER_ICON;
//	    }
	    return result;
	    
	}

	public String getText(Object element) {
		return ((FolderModel)element).getDisplayName();
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
