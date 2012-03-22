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

import org.eclipse.swt.custom.TableTreeItem;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link TableTreeItem} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
@SuppressWarnings("deprecation")
public class TableTreeItemAdapter extends ItemAdapter {

	private static final String PREFIX = ComponentType.TABLE_TREE_ITEM
			.getPrefix();

	public static final String EXPANDED = PREFIX + "e"; //$NON-NLS-1$

	private TableTreeItem tableTreeItem;

	public TableTreeItemAdapter(TableTreeItem item, int style) {
		super(item, style);
		this.tableTreeItem = item;
		this.componentType = ComponentType.TABLE_TREE_ITEM;
	}

	/**
	 * @return <code>true</code> if table tree item is expanded; otherwise
	 *         return <code>false</code>
	 */
	public Boolean isExpanded() {
		return tableTreeItem != null ? tableTreeItem.getExpanded() : null;
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(EXPANDED, isExpanded());
	}

}
