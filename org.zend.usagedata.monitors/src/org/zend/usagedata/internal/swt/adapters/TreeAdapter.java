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

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link Tree} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class TreeAdapter extends ControlAdapter {

	private static final String PREFIX = ComponentType.TREE.getPrefix();

	public static final String SELECTION = PREFIX + "s"; //$NON-NLS-1$

	private Tree tree;

	public TreeAdapter(Tree tree, int style) {
		super(tree, style);
		this.tree = tree;
		this.componentType = ComponentType.TREE;
	}

	/**
	 * @return tree selection
	 */
	private String getSelection() {
		if (tree == null) {
			return null;
		}
		TreeItem[] items = tree.getSelection();
		StringBuilder result = new StringBuilder();
		for (TreeItem treeItem : items) {
			result.append(treeItem.getText());
			result.append("|"); //$NON-NLS-1$
		}
		return result.toString();
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		String selecton = getSelection();
		if (selecton == null) {
			message.addMessage(SELECTION, selecton);
		} else if (selecton.length() > 0) {
			message.addMessage(SELECTION, getSelection());
		}
	}

}
