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

import java.util.ArrayList;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.talend.designer.cmis.CMISComponent;
import org.talend.designer.cmis.i18n.Messages;
import org.talend.designer.cmis.manager.TypeDefinitionManager;
import org.talend.designer.cmis.model.TypeDefinitionModel;
import org.talend.designer.cmis.ui.metadata.TypeDefinitionLabelProvider;
import org.talend.designer.cmis.ui.metadata.TypeDefinitionTreeContentProvider;
import org.talend.designer.cmis.ui.metadata.TypeDefinitionTreeViewer;
import org.talend.designer.cmis.ui.metadata.PropertyDefinitionTableViewer;
import org.talend.designer.cmis.ui.metadata.PropertyDefinitionContentProvider;
import org.talend.designer.cmis.ui.metadata.PropertyDefinitionLabelProvider;

public class TypeDefinitionSelectorComposite extends Composite {

	private TypeDefinitionManager modelManager;
	private TypeDefinitionTreeViewer objectTypeTreeViewer;
	private PropertyDefinitionTableViewer propertiesTableViewer;

	private TypeDefinitionSelectorComposite(Composite parent, int style) {
		super(parent, style);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        setLayout(layout);
	}
	
	public TypeDefinitionSelectorComposite(Composite parent, int style, TypeDefinitionManager modelManager) {
		this(parent, style);
		
		this.modelManager = modelManager;
		
		SashForm sash = new SashForm(this, SWT.VERTICAL);
		sash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createTypeComposite(sash);
		createAttributeComposite(sash);
		
		sash.setWeights(new int[]{2,5});
	}
	
	private void createTypeComposite(SashForm sash) {
		
		Composite typeComposite = new Composite(sash, SWT.NONE);
		GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = 2;
        layout.marginWidth = 0;
        layout.marginTop = 2;
        layout.verticalSpacing = 4;
        layout.horizontalSpacing = 0;
        typeComposite.setLayout(layout);
		typeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createTypeToolbar(typeComposite);
		createCMISTypesTree(typeComposite);
		
	}

	private void createAttributeComposite(SashForm sash) {
		Composite attributeComposite = new Composite(sash, SWT.NONE);
		GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = 2;
        layout.marginWidth = 0;
        layout.marginTop = 2;
        layout.verticalSpacing = 4;
        layout.horizontalSpacing = 0;
        attributeComposite.setLayout(layout);
		attributeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createAttributeToolbar(attributeComposite);
		createCMISAttributesTable(attributeComposite);
		
	}
	
	private void createTypeToolbar(Composite typeComposite) {
		Label label = new Label(typeComposite, SWT.NONE);
        label.setText("  " + Messages.getString("cmis.typesTree.label")); //$NON-NLS-1$ //$NON-NLS-2$
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
        ToolBarManager toolBarMgr = new ToolBarManager(SWT.FLAT);
        toolBarMgr.createControl(this);
        
//        if (refreshAllConnectionsAction == null) {
//            refreshAllConnectionsAction = new RefreshAllConnectionAction();
//        }
//        
//        toolBarMgr.add(refreshAllConnectionsAction);
        toolBarMgr.update(true);
        toolBarMgr.getControl().setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    }
	
	private void createCMISTypesTree(Composite typeComposite) {
		objectTypeTreeViewer = new TypeDefinitionTreeViewer(typeComposite, SWT.FULL_SELECTION);
		
		objectTypeTreeViewer.setContentProvider(new TypeDefinitionTreeContentProvider());
		objectTypeTreeViewer.setLabelProvider(new TypeDefinitionLabelProvider());
		
		ArrayList<TypeDefinitionModel> treeInput = modelManager.getAvailableTypeDefinition();
		objectTypeTreeViewer.setInput(treeInput);
		
		//Select the current object type in the tree
		TypeDefinitionModel selectedObjectType = modelManager.getSelectedTypeDefinition();
		objectTypeTreeViewer.setSelection(selectedObjectType, true);
		
	}
	
	private void createAttributeToolbar(Composite attributeComposite) {
        Label label = new Label(attributeComposite, SWT.NONE);
        label.setText("  " + Messages.getString("cmis.attributesTable.label")); //$NON-NLS-1$
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
        ToolBarManager toolBarMgr = new ToolBarManager(SWT.FLAT);
        toolBarMgr.createControl(this);
        
//        if (refreshAllConnectionsAction == null) {
//            refreshAllConnectionsAction = new RefreshAllConnectionAction();
//        }
//        
//        toolBarMgr.add(refreshAllConnectionsAction);
        toolBarMgr.update(true);
        toolBarMgr.getControl().setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    }
	
	private void createCMISAttributesTable(Composite attributeComposite) {
		
		propertiesTableViewer = PropertyDefinitionTableViewer.newCheckList(attributeComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		
		propertiesTableViewer.setContentProvider(new PropertyDefinitionContentProvider());
		propertiesTableViewer.setLabelProvider(new PropertyDefinitionLabelProvider());
		
		// The input for the table viewer is the list of available properties for the selected object.
		PropertyDefinition<?>[] propertyDefinitions = modelManager.getAvailablePropertyDefinitions();
		propertiesTableViewer.setInput(propertyDefinitions);

		Map<String, PropertyDefinition<?>> selectedPropertyDefinitions = modelManager.getSelectedPropertyDefinitions();
		
		for (int i = 0; i < propertyDefinitions.length; i++) {
			PropertyDefinition<?> propertyDefinition = propertyDefinitions[i];
			String propertyId = propertyDefinition.getId();
			if (selectedPropertyDefinitions.containsKey(propertyId))
			{
				propertiesTableViewer.setChecked(propertyDefinition, true);
			} 
			
			//If the property is required and it's an output component,
			//then it is automatically added to the selected properties
			String componentName = (String) modelManager.getComponent()
					.getElementParameter("COMPONENT_NAME").getValue();
			
			if (componentName.endsWith(CMISComponent.OUTPUT_COMPONENT_SUFFIX)
					&& propertyDefinition.isRequired())
			{
				propertiesTableViewer.setChecked(propertyDefinition, true);
				modelManager.addSelectedPropertyDefinition(propertyDefinition);
			}
			
		}
		
		propertiesTableViewer.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				PropertyDefinition<?> propertyDefinition = (PropertyDefinition<?>) event.getElement();
				if (event.getChecked())
				{
					modelManager.addSelectedPropertyDefinition(propertyDefinition);
				}else
				{
					String componentName = (String) modelManager.getComponent()
							.getElementParameter("COMPONENT_NAME").getValue();
					
					if (componentName.endsWith(CMISComponent.OUTPUT_COMPONENT_SUFFIX)
							&& propertyDefinition.isRequired())
					{
						event.getCheckable().setChecked(propertyDefinition, true);
					}
					else
					{
						modelManager.removeSelectedPropertyDefinition(propertyDefinition);
					}
				}
				
			}
		});
	}
	
	
	
	public TypeDefinitionTreeViewer getObjectTypeTreeViewer() {
		return objectTypeTreeViewer;
	}
	
	public PropertyDefinitionTableViewer getPropertiesTableViewer() {
		return propertiesTableViewer;
	}
	
}
