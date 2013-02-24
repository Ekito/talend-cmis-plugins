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

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.talend.commons.ui.runtime.image.ImageUtils.ICON_SIZE;
import org.talend.core.model.components.IComponent;
import org.talend.core.ui.images.CoreImageProvider;
import org.talend.designer.cmis.manager.EditorManager;
import org.talend.designer.cmis.manager.TypeDefinitionManager;
import org.talend.designer.cmis.model.TypeDefinitionModel;

/**
 * Dialog allowing to choose typing, i.e. a CMIS base type and CMIS object type within CMIS model definitions.
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public class EditorDialog extends Dialog {

	private EditorManager editorManager;

	private TypeDefinitionSelectorComposite typesComposite;
	private FunctionEditorComposite functionComposite;

	public EditorDialog(Shell parentShell, EditorManager editorManager) {
		super(parentShell);
		this.editorManager = editorManager;

		// customizing Dialog
		setBlockOnOpen(false);
		setShellStyle(SWT.APPLICATION_MODAL | SWT.BORDER | SWT.RESIZE | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.TITLE);
	}

	@Override
	protected void okPressed() {
		// saving to model before closing :
		this.editorManager.save();
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
		
		SashForm sash = new SashForm(composite, SWT.HORIZONTAL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		sash.setLayoutData(gridData);
		
		createCMISTypesArea(sash);

		String componentName = editorManager.getComponent().getComponent().getName();
		//if componentName like '*Input' display query editor
//		if (componentName.endsWith("Input"))
//		{
//			createCMISQueryArea(sash);
//		}
//		//else if componentName like '*Output' display function editor
//		else if (componentName.endsWith("Output"))
//		{
//			createCMISFunctionArea(sash);
//		}
		
		return composite;
	}
	
	private void createCMISTypesArea(Composite composite)
	{
		
		typesComposite = new TypeDefinitionSelectorComposite(composite, SWT.BORDER, editorManager.getModelManager());
		((Tree)typesComposite.getObjectTypeTreeViewer().getControl()).addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				Object selectedData = e.item.getData();
				if (selectedData instanceof TypeDefinitionModel)
				{
					TypeDefinitionManager modelManager = editorManager.getModelManager();
					//Get the previous selected properties
					Map<String,PropertyDefinition<?>> previousSelectedPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>(modelManager.getSelectedPropertyDefinitions());
					
					//Clear the selected properties on the data model
					modelManager.clearSelectedPropertyDefinition();
					
					TypeDefinitionModel selectedObjectTypeNode = (TypeDefinitionModel)selectedData;
					//Set the selected type node and the default selected property definitions 
					modelManager.setSelectedObjectTypeNode(selectedObjectTypeNode);

					PropertyDefinition<?>[] availablePropertyDefinitions = modelManager.getAvailablePropertyDefinitions();
					
					
					typesComposite.getPropertiesTableViewer().setInput(availablePropertyDefinitions);
					
					for (int i = 0; i < availablePropertyDefinitions.length; i++) {
						PropertyDefinition<?> propertyDefinition = availablePropertyDefinitions[i];
						String propertyId = propertyDefinition.getId();
						if (previousSelectedPropertyDefinitions.containsKey(propertyId))
						{
							typesComposite.getPropertiesTableViewer().setChecked(propertyDefinition, true);
							modelManager.addSelectedPropertyDefinition(propertyDefinition);
						}
					}
					
				}

			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
	}
	
	private void createCMISFunctionArea(Composite composite) {
		
		functionComposite = new FunctionEditorComposite(composite, SWT.BORDER, editorManager.getFunctionManager());
	}
	
	private void createCMISQueryArea(Composite composite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		IComponent component = editorManager.getComponent().getComponent();
		Image createImage = CoreImageProvider.getComponentIcon(component, ICON_SIZE.ICON_32);
		newShell.setImage(createImage);
		newShell.setText(editorManager.getComponent().getUniqueName());
		newShell.setSize(900, 600);
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

}
