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

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.TreeItem;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link TreeItem} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class TreeItemAdapter extends ItemAdapter {

	private static final String PREFIX = ComponentType.TREE_ITEM.getPrefix();

	public static final String CHECKED = PREFIX + "c"; //$NON-NLS-1$
	public static final String EXPANDED = PREFIX + "e"; //$NON-NLS-1$
	public static final String ACTION = PREFIX + "a"; //$NON-NLS-1$
	public static final String ROW_TEXT = PREFIX + "t"; //$NON-NLS-1$

	private TreeItem treeItem;

	public TreeItemAdapter(TreeItem item, int style) {
		super(item, style);
		this.treeItem = item;
		this.componentType = ComponentType.TREE_ITEM;
	}

	/**
	 * @return tree item row text
	 */
	public String getRowText() {
		String result = ""; //$NON-NLS-1$
		int count = treeItem.getItemCount();
		for (int i = 0; i < count; i++) {
			result += treeItem.getText(i);
			if (i < count - 1) {
				result += ";"; //$NON-NLS-1$
			}
		}
		return result;
	}

	/**
	 * @return <code>true</code> if tree item is checked; otherwise return
	 *         <code>false</code>
	 */
	public boolean isChecked() {
		return treeItem.getChecked();
	}

	/**
	 * @return <code>true</code> if tree item is expanded; otherwise return
	 *         <code>false</code>
	 */
	public boolean isExpanded() {
		return treeItem.getExpanded();
	}

	/**
	 * @return action name associated with this tree item
	 */
	public String getAction() {
		Object data = treeItem.getData();
		if (data instanceof ActionContributionItem) {
			ActionContributionItem actionContribution = (ActionContributionItem) data;
			IAction action = actionContribution.getAction();
			if (action != null) {
				return action.getClass().getName();
			}
		}
		return null;
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(CHECKED, isChecked());
		message.addMessage(EXPANDED, isExpanded());
		String action = getAction();
		if (action != null) {
			message.addMessage(ACTION, action);
		}
		String row = getRowText();
		if (!row.equals("")) { //$NON-NLS-1$
			message.addMessage(ROW_TEXT, row);
		}
	}

}
