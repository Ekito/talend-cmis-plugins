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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.talend.designer.cmis.data.TypeDefinitionModel;

public class TypeDefinitionTreeViewer extends TreeViewer {

	public TypeDefinitionTreeViewer(Composite parent, int style) {
		super(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		getControl().setLayoutData(gridData);
		
	}
	
	public void setSelection(TypeDefinitionModel selection, boolean reveal) {
		super.setSelection(new StructuredSelection(selection), reveal);
		expandToLevel(selection, 1);
		getControl().setFocus();
		
	}

}
