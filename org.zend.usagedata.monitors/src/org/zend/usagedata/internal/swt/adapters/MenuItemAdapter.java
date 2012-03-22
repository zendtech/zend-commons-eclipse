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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link MenuItem} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class MenuItemAdapter extends ItemAdapter {

	private static final String PREFIX = ComponentType.MENU_ITEM.getPrefix();

	public static final String ENABLED = PREFIX + "e"; //$NON-NLS-1$
	public static final String SELECTION = PREFIX + "s"; //$NON-NLS-1$
	public static final String ACTION = PREFIX + "a"; //$NON-NLS-1$
	public static final String PATH = PREFIX + "p"; //$NON-NLS-1$

	private MenuItem menuItem;

	public MenuItemAdapter(MenuItem item, int style) {
		super(item, style);
		this.menuItem = item;
		this.componentType = ComponentType.MENU_ITEM;
	}

	/**
	 * @return <code>true</code> if menu item is enabled; otherwise return
	 *         <code>false</code>
	 */
	public Boolean isEnabled() {
		return menuItem != null ? menuItem.getEnabled() : null;
	}

	/**
	 * @return <code>true</code> if menu item is selected; otherwise return
	 *         <code>false</code>
	 */
	public Boolean isSelected() {
		return menuItem != null ? menuItem.getSelection() : null;
	}

	/**
	 * @return menu path
	 */
	public String getMenuPath() {
		if (menuItem == null) {
			return null;
		}
		String path = menuItem.getText();
		Menu parent = menuItem.getParent();
		while (parent != null) {
			MenuItem parentItem = parent.getParentItem();
			if (parentItem != null) {
				path = parent.getParentItem().getText() + "||" + path; //$NON-NLS-1$
				parent = parent.getParentItem().getParent();
			} else {
				break;
			}
		}
		return path;
	}

	/**
	 * @return action name associated with this menu item
	 */
	public String getAction() {
		if (menuItem != null) {
			Object data = menuItem.getData();
			if (data instanceof ActionContributionItem) {
				ActionContributionItem actionContribution = (ActionContributionItem) data;
				IAction action = actionContribution.getAction();
				if (action != null) {
					return action.getClass().getName();
				}
			}
		}
		return null;
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(ENABLED, isEnabled());
		message.addMessage(SELECTION, isSelected());
		message.addMessage(ACTION, getAction());
		message.addMessage(PATH, getMenuPath());
	}

}
