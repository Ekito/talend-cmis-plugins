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

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.talend.designer.cmis.i18n.Messages;
import org.talend.designer.cmis.manager.impl.CMISFolderManager;
import org.talend.designer.cmis.model.FolderModel;

/**
 * Dialog allowing to choose typing, i.e. an CMIS base type and CMIS object type within CMIS model definitions.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public class FolderDialog extends Dialog {

	private FolderTreeViewer folderTreeViewer;

	private String folderPath;

	private CMISFolderManager cMISFolderManager;

	public FolderDialog(Shell parentShell, CMISFolderManager cMISFolderManager) {
		super(parentShell);
		
		this.cMISFolderManager = cMISFolderManager;

		setBlockOnOpen(false);
		setShellStyle(SWT.APPLICATION_MODAL | SWT.BORDER | SWT.RESIZE | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.TITLE);
	}

	@Override
	protected void okPressed() {
		// saving to model before closing :
		super.okPressed();
		setReturnCode(SWT.OK);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 12;
		composite.setLayout(layout);

		Label objectTypeLabel = new Label(composite, SWT.NULL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		objectTypeLabel.setLayoutData(gridData);
		objectTypeLabel.setText(Messages.getString("cmis.folderSelector.tree.label"));

		
		//The tree displaying the folder tree
		folderTreeViewer = new FolderTreeViewer(composite, SWT.FULL_SELECTION);
		
		folderTreeViewer.setContentProvider(new FolderTreeContentProvider());
		folderTreeViewer.setLabelProvider(new FolderLabelProvider());
		
		ArrayList<FolderModel> rootFolderNodes = cMISFolderManager.getRootFolderNodes();
//		ArrayList<CMISObjectTypeNode> treeInput = modelManager.getAvailableObjectTypeNodes();
//		ArrayList<CMISFolderNode> folderList = new ArrayList<CMISFolderNode>();
//		folderList.add(rootFolderNode);
		folderTreeViewer.setInput(rootFolderNodes);
//		
//		//Select the current object type in the tree
//		CMISObjectTypeNode selectedObjectType = modelManager.getSelectedObjectTypeNode();
//		folderTreeViewer.setSelection(selectedObjectType, true);
		
		
		//Add a selection listener to update 
		((Tree)folderTreeViewer.getControl()).addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				FolderModel selectedData = (FolderModel) e.item.getData();
				cMISFolderManager.setSelectedFolderPath(selectedData.getFolderPath());

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// Nothing implemented here

			}
		});
		
		applyDialogFont(composite);
		return composite;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
//		IComponent component = cmisManager.getCMISComponent().getComponent();
//		Image createImage = CoreImageProvider.getComponentIcon(component, ICON_SIZE.ICON_32);
//		newShell.setImage(createImage);
//		newShell.setText(cmisManager.getCMISComponent().getUniqueName());
//		newShell.setSize(900, 600);
	}

	@Override
	protected Point getInitialLocation(Point initialSize)
	{
		//Center the dialog window
		Shell shell = this.getShell();
		Monitor primary = shell.getMonitor();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = shell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		return new Point(x, y);

	}
	
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getFolderPath() {
		return this.folderPath;
	}

}
