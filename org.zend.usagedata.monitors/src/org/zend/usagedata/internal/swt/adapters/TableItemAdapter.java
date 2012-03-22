/*******************************************************************************
 * Copyright (c) 2011 Wojciech Galanciak
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     wojciech.galanciak@gmail.com - initial API and implementation
 *******************************************************************************/
package org.zend.usagedata.internal.swt.adapters;

import org.eclipse.swt.widgets.TableItem;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link TableItem} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class TableItemAdapter extends ItemAdapter {

	private static final String PREFIX = ComponentType.TABLE_ITEM.getPrefix();

	public static final String CHECKED = PREFIX + "c"; //$NON-NLS-1$

	private TableItem tableItem;

	public TableItemAdapter(TableItem item, int style) {
		super(item, style);
		this.tableItem = item;
		this.componentType = ComponentType.TABLE_ITEM;
	}

	/**
	 * @return <code>true</code> if table item is checked; otherwise return
	 *         <code>false</code>
	 */
	public Boolean isChecked() {
		return tableItem != null ? tableItem.getChecked() : null;
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(CHECKED, isChecked());
	}

}
