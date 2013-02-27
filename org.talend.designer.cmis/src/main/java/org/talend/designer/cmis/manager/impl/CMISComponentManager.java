/*******************************************************************************
 * Copyright (c) 2012 jboulay.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     jboulay - initial API and implementation
 ******************************************************************************/

package org.talend.designer.cmis.manager.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.process.AbstractNode;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.designer.cmis.CMISComponent;
import org.talend.designer.cmis.i18n.Messages;
import org.talend.designer.cmis.manager.EditorManager;
import org.talend.designer.cmis.ui.EditorDialog;

/**
 * This class holds the model and UI managers on behalf of the component.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public class CMISComponentManager {

	private CMISComponent cmisComponent;
	private EditorDialog editorDialog;
	private EditorManager editorManager;

	public CMISComponentManager(CMISComponent cmisComponent) {
		this.cmisComponent = cmisComponent;
		this.editorManager = new EditorManager(cmisComponent);
	}

	/**
	 * @return the associated cmis component
	 */
	public CMISComponent getCMISComponent() {
		return cmisComponent;
	}

	/**
	 * @return the editor dialog
	 */
	public EditorDialog getEditorDialog() {
		return editorDialog;
	}

	/**
	 * @return the editor manager
	 */
	public EditorManager getEditorManager() {
		return editorManager;
	}

	/**
	 * Checks the connections and creates the UI (a dialog actually)
	 * 
	 * @param parent
	 * @return
	 */
	public EditorDialog createUI(Composite parent) {
		IConnection inConn = null;
		AbstractNode connector = this.cmisComponent;
		for (IConnection conn : connector.getIncomingConnections()) {
			if ((conn.getLineStyle().equals(EConnectionType.FLOW_MAIN))
					|| (conn.getLineStyle().equals(EConnectionType.FLOW_REF))) {
				inConn = conn;
				break;
			}
		}
		if (inConn != null) {
			if (!inConn.getMetadataTable().sameMetadataAs(
					connector.getMetadataList().get(0))) {
				MessageBox messageBox = new MessageBox(parent.getShell(),
						SWT.APPLICATION_MODAL | SWT.OK);
				messageBox.setText(Messages
						.getString("cmis.schemaError.title"));
				messageBox.setMessage(Messages
						.getString("cmis.schemaError.msg"));
				if (messageBox.open() == SWT.OK) {
					return null;
				}
			}
		}

		// then create and open the model dialog :
		EditorDialog editorDialog = new EditorDialog(
				parent.getShell(), editorManager);
		
		//Set Model, image and others...
		editorDialog.open();

		// NB. this dialog is blocking ; model save is done in its okPressed()
		return editorDialog;
	}

	/**
	 * Checks the connections and creates the UI (a dialog actually)
	 * 
	 * @param display
	 * @return
	 */
	public Shell createUI(Display display) {
		this.editorDialog = createUI(display.getActiveShell());
		return this.editorDialog != null ? this.editorDialog.getShell()
				: null;
	}

}
