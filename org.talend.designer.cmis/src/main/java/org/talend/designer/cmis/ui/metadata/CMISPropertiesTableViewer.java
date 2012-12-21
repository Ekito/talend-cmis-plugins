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

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.talend.designer.cmis.i18n.Messages;

public class CMISPropertiesTableViewer extends CheckboxTableViewer {

	private static String ID_COLUMN = Messages.getString("cmis.attributesTable.idColumnName");  
	private static String NAME_COLUMN = Messages.getString("cmis.attributesTable.nameColumnName");
	private static String TYPE_COLUMN = Messages.getString("cmis.attributesTable.typeColumnName");
	private static String MANDATORY_COLUMN = Messages.getString("cmis.attributesTable.mandatoryColumnName");
	private static String DEFAULT_COLUMN = Messages.getString("cmis.attributesTable.defaultColumnName");
	
	private CMISPropertiesComparator propertiesComparator;

	public CMISPropertiesTableViewer(Table table) {
		super(table);
		
		
		// Set the default sorter for the viewer
		propertiesComparator = new CMISPropertiesComparator();
		setComparator(propertiesComparator);
		propertiesComparator.setColumn(CMISPropertiesComparator.ID);
		
		//Get the table and set the layout and table column titles
		Table propertiesTable = getTable();
		GridData gridData = new GridData(GridData.FILL_BOTH);
		propertiesTable.setLayoutData(gridData);		
					
		propertiesTable.setLinesVisible(true);
		propertiesTable.setHeaderVisible(true);

		createTableViewerColumn(ID_COLUMN, 160, 0, SWT.NONE);
		createTableViewerColumn(NAME_COLUMN, 160, 1, SWT.NONE);
		createTableViewerColumn(TYPE_COLUMN, 80, 2, SWT.NONE);
		createTableViewerColumn(MANDATORY_COLUMN, 80, 3, SWT.CENTER);
		createTableViewerColumn(DEFAULT_COLUMN, 80, 4, SWT.NONE);
		
	}
	
	private TableViewerColumn createTableViewerColumn(String title, int bound,
			final int colNumber, int style) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(this,
				style);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;

	}
	
	private SelectionAdapter getSelectionAdapter(final TableColumn column,
			final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				propertiesComparator.setColumn(index);
				int dir = getTable().getSortDirection();
				if (getTable().getSortColumn() == column) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {

					dir = SWT.DOWN;
				}
				getTable().setSortDirection(dir);
				getTable().setSortColumn(column);
				refresh();
			}
		};
		return selectionAdapter;
	}
	
	public static CMISPropertiesTableViewer newCheckList(Composite parent, int style) {
        Table table = new Table(parent, SWT.CHECK | style);
        return new CMISPropertiesTableViewer(table);
    }

}
