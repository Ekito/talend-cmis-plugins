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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.designer.cmis.model.FolderModel;

public class FolderTreeViewer extends TreeViewer {

	
	public FolderTreeViewer(Composite parent, int style) {
		super(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		getControl().setLayoutData(gridData);
		
		getTree().addTreeListener(new TreeListener() {
			
			public void treeCollapsed(TreeEvent e) {
			    updateImage((TreeItem) e.item, true);
			  }

			  public void treeExpanded(TreeEvent e) {
			    updateImage((TreeItem) e.item, false);
			  }

			  private void updateImage(TreeItem item, boolean isCollapsed) {
			    Image image = isCollapsed ? FolderLabelProvider.CLOSED_FOLDER_ICON : FolderLabelProvider.OPEN_FOLDER_ICON;
			    item.setImage(image);
			  }
		});
	}
	
	public void setSelection(FolderModel selection, boolean reveal) {
		super.setSelection(new StructuredSelection(selection), reveal);
		expandToLevel(selection, 1);
		getControl().setFocus();
		
	}

	
}
