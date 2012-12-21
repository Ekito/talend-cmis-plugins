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
package org.talend.designer.cmis.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.talend.designer.cmis.data.CMISFunctionManager;
import org.talend.designer.cmis.i18n.Messages;
import org.talend.designer.cmis.ui.folder.CMISFolderDialog;

public class CMISFunctionEditorComposite extends Composite {

	private CMISFunctionManager functionManager;
	private CMISFolderDialog folderDialog;
	private Text folderPath;

	private CMISFunctionEditorComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		setLayout(layout);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		setLayoutData(gridData);

	}

	public CMISFunctionEditorComposite(Composite parent, int style, CMISFunctionManager functionManager){
		this(parent, style);

		this.functionManager = functionManager;

		createFunctionSelector();
		createFolderSelector();
		createDownloadSelector();
		createUploadSelector();

		//TODO Query editor
	}

	private void createUploadSelector() {
		// TODO Auto-generated method stub

	}

	private void createDownloadSelector() {
		// TODO Auto-generated method stub

	}

	private void createFunctionSelector() {
		// TODO Auto-generated method stub

	}

	private void createFolderSelector() {

		Composite folderSelector = new Composite(this, SWT.BORDER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		folderSelector.setLayout(gridLayout);

		createFolderSelectorLabel(folderSelector);
		createFolderTextField(folderSelector);
		createFolderBrowseButton(folderSelector);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		folderSelector.setLayoutData(gridData);

	}

	private void createFolderSelectorLabel(Composite folderSelector) {
		Label label = new Label(folderSelector, SWT.NONE);
		label.setText(Messages.getString("cmis.folderSelector.label")); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
	}

	private void createFolderTextField(Composite folderSelector) {
		folderPath = new Text(folderSelector, SWT.BORDER);
		folderPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	private void createFolderBrowseButton(Composite folderSelector) {
		Button button = new Button(folderSelector, SWT.PUSH);
		button.setText("Browse");
		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		folderDialog = new CMISFolderDialog(this.getShell(), functionManager.getFolderManager());
		Listener openerListener = new Listener() {
			public void handleEvent(Event event) {
				folderDialog.open();
//				folderPath.setText(functionManager.getFolderManager().getSelectedFolderPath());
			}
		};

		button.addListener(SWT.Selection, openerListener);
	}

}
