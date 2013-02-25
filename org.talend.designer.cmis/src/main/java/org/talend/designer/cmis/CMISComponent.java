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
package org.talend.designer.cmis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.talend.commons.exception.SystemException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.commons.ui.swt.dialogs.ProgressDialog;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.components.IODataComponent;
import org.talend.core.model.process.AbstractExternalNode;
import org.talend.core.model.process.IComponentDocumentation;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IExternalData;
import org.talend.core.model.temp.ECodePart;
import org.talend.designer.cmis.manager.PropertyDefinitionFilter;
import org.talend.designer.cmis.manager.QueryManager;
import org.talend.designer.cmis.manager.impl.CMISComponentManager;
import org.talend.designer.cmis.manager.impl.InputPropertyDefinitionFilterImpl;
import org.talend.designer.cmis.manager.impl.OutputPropertyDefinitionFilterImpl;
import org.talend.designer.codegen.ICodeGeneratorService;
import org.talend.repository.ui.wizards.metadata.ContextSetsSelectionDialog;

/**
 * Component class of tCMISOutput
 * 
 * @author Julien Boulay - Ekito - www.ekito.fr
 * 
 */
public class CMISComponent extends AbstractExternalNode {

	public static String PLUGIN_ID = "org.talend.designer.cmis";
	
	public static String INPUT_COMPONENT_SUFFIX = "Input";
	public static String OUTPUT_COMPONENT_SUFFIX = "Output";

	private CMISComponentManager cmisManager;

	public CMISComponent() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.IExternalNode#initialize()
	 */
	public void initialize() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.IExternalNode#getComponentDocumentation(java.lang.String, java.lang.String)
	 */
	public IComponentDocumentation getComponentDocumentation(String componentName, String tempFolderPath) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.IExternalNode#getExternalData()
	 */
	public IExternalData getExternalData() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.IExternalNode#loadDataIn(java.io.InputStream, java.io.Reader)
	 */
	public void loadDataIn(InputStream inputStream, Reader reader) throws IOException, ClassNotFoundException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.IExternalNode#loadDataOut(java.io.OutputStream, java.io.Writer)
	 */
	public void loadDataOut(OutputStream out, Writer writer) throws IOException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.IExternalNode#open(org.eclipse.swt.widgets.Composite)
	 */
	public int open(Composite parent) {
		return open(parent.getDisplay());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.IExternalNode#open(org.eclipse.swt.widgets.Display)
	 */
	public int open(Display display) {

		cmisManager = new CMISComponentManager(this);
		
		String componentName = (String) getElementParameter("COMPONENT_NAME").getValue();
		
		//Set the managers corresponding to the type of component (Input or Output)
		PropertyDefinitionFilter propertyDefinitionFilter = null;
		QueryManager queryManager = null;
		
		if (componentName.endsWith(CMISComponent.INPUT_COMPONENT_SUFFIX)) {
			propertyDefinitionFilter = new InputPropertyDefinitionFilterImpl();
			queryManager = new QueryManager(this);
		}else
		{
			propertyDefinitionFilter = new OutputPropertyDefinitionFilterImpl();
		}
		
		cmisManager.getEditorManager().getTypeDefinitionManager().setPropertyDefinitionFilter(propertyDefinitionFilter);
		cmisManager.getEditorManager().setQueryManager(queryManager);
		
		//Select the execution context if necessary
		boolean isContextDependant = cmisManager.getEditorManager().getSessionManager().isContextDependant();
		
		IContext selectedContext = null;
		
		if (isContextDependant) {
			List<IContext> contextList = getProcess().getContextManager().getListContext();
			
			if (contextList.size() > 1) {
				ContextSetsSelectionDialog setsDialog = new ContextSetsSelectionDialog(null, getProcess().getContextManager(), false);
				setsDialog.open();
				String selectedContextString = setsDialog.getSelectedContext();
				selectedContext = getProcess().getContextManager().getContext(selectedContextString);
			}else
			{
				selectedContext = getProcess().getContextManager().getDefaultContext();
			}
		}
		
		final IContext theSelectedContext = selectedContext;
		
		//Create the session, load the model
    	ProgressDialog progressDialog = new ProgressDialog(display.getActiveShell()) {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            	try {
            		//Create the CMIS session
            		cmisManager.getEditorManager().createSession(theSelectedContext);
            		//Load the model
        			cmisManager.getEditorManager().loadModel(); // NB. or when modelManager is created
        		} catch (Exception ex) {
        			ExceptionHandler.process(ex);
        			cmisManager.getEditorManager().getTypeDefinitionManager().clear();
        		}
            }
        };

        try {
            progressDialog.executeProcess();
        } catch (InvocationTargetException e) {
            MessageBoxExceptionHandler.process(e.getTargetException(), display.getActiveShell());
        } catch (InterruptedException e) {
            // Nothing to do
        }
        
        Shell shell = cmisManager.createUI(display);

        while (shell != null && !shell.isDisposed()) {
        	try {
        		if (!display.readAndDispatch()) {
        			display.sleep();
        		}
        	} catch (Throwable e) {
        		ExceptionHandler.process(e);
        	}
        }

		
		int returnCode = cmisManager.getEditorDialog() != null ? cmisManager.getEditorDialog().getReturnCode() : Dialog.CANCEL;
		
		return returnCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.IExternalNode#setExternalData(org.talend.core.model.process.IExternalData)
	 */
	public void setExternalData(IExternalData persistentData) {
		// do nothing here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.AbstractExternalNode#renameMetadataColumnName(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	protected void renameMetadataColumnName(String conectionName, String oldColumnName, String newColumnName) {
		// used in super's metadataInputChanged() and metadataOutputChanged()
		// but since we don't need them, we don't need renameMetadataColumnName() either
	}

	/* (non-Javadoc)
	 * @see org.talend.core.model.process.AbstractExternalNode#metadataInputChanged(org.talend.core.model.components.IODataComponent, java.lang.String)
	 */
	@Override
	public void metadataInputChanged(IODataComponent dataComponent, String connectionToApply) {
		// column mappings are automatically remapped by talend on change
		// since they are done using COLUMN_LIST fields
	}

	/* (non-Javadoc)
	 * @see org.talend.core.model.process.AbstractExternalNode#metadataOutputChanged(org.talend.core.model.components.IODataComponent, java.lang.String)
	 */
	@Override
	public void metadataOutputChanged(IODataComponent dataComponent, String connectionToApply) {
		// column mappings are automatically remapped by talend on change
		// since they are done using COLUMN_LIST fields
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.INode#getGeneratedCode()
	 */
	public String getGeneratedCode() {
		try {
			ICodeGeneratorService service = (ICodeGeneratorService) GlobalServiceRegister.getDefault().getService(
					ICodeGeneratorService.class);
			return service.createCodeGenerator().generateComponentCode(this, ECodePart.MAIN);
		} catch (SystemException e) {
			ExceptionHandler.process(e);
		}
		return ""; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.talend.core.model.process.IExternalNode#renameInputConnection(java.lang.String, java.lang.String)
	 */
	public void renameInputConnection(String oldName, String newName) {

	}

	/* (non-Javadoc)
	 * @see org.talend.core.model.process.IExternalNode#renameOutputConnection(java.lang.String, java.lang.String)
	 */
	public void renameOutputConnection(String oldName, String newName) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.model.process.IExternalNode#getTMapExternalData()
	 */
	public IExternalData getTMapExternalData() {
		return null;
	}

}
