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
package org.zend.usagedata.internal.swt;

/**
 * Component type name and its prefix which is used during data collecting.
 * 
 * @author wojciech.galanciak@gmail.com
 */
public enum ComponentType {

	BUTTON("button", "b"), //$NON-NLS-1$ //$NON-NLS-2$

	COMBO("combo", "c1"), //$NON-NLS-1$ //$NON-NLS-2$

	CONTROL("control", "c"), //$NON-NLS-1$ //$NON-NLS-2$

	COOL_ITEM("coolItem", "c2"), //$NON-NLS-1$ //$NON-NLS-2$

	EXPAND_ITEM("expandItem", "e"), //$NON-NLS-1$ //$NON-NLS-2$

	FORM_TEXT("formText", "f"), //$NON-NLS-1$ //$NON-NLS-2$

	ITEM("item", "i"), //$NON-NLS-1$ //$NON-NLS-2$

	KEY("key", "k"), //$NON-NLS-1$ //$NON-NLS-2$

	LINK("link", "l1"), //$NON-NLS-1$ //$NON-NLS-2$

	LIST("list", "l"), //$NON-NLS-1$ //$NON-NLS-2$

	MENU_ITEM("menuItem", "m"), //$NON-NLS-1$ //$NON-NLS-2$

	MOUSE("mouse", "m1"), //$NON-NLS-1$ //$NON-NLS-2$

	TABLE_ITEM("tableItem", "t1"), //$NON-NLS-1$ //$NON-NLS-2$

	TEXT("text", "t"), //$NON-NLS-1$ //$NON-NLS-2$

	TOOL_ITEM("toolItem", "t3"), //$NON-NLS-1$ //$NON-NLS-2$

	TREE("tree", "t6"), //$NON-NLS-1$ //$NON-NLS-2$

	TREE_ITEM("treeItem", "t2"), //$NON-NLS-1$ //$NON-NLS-2$

	WIDGET("widget", "w"), //$NON-NLS-1$ //$NON-NLS-2$

	TABLE_TREE_ITEM("tableTreeItem", "t4"), //$NON-NLS-1$ //$NON-NLS-2$-1$

	TASK_ITEM("taskItem", "t5"); //$NON-NLS-1$ //$NON-NLS-2$

	private String name;
	private String prefix;

	private ComponentType(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}

	/**
	 * @return component type name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return component type prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param name
	 * @return component type based on specified name
	 */
	public static ComponentType byName(String name) {
		if (name == null) {
			return null;
		}

		ComponentType[] values = values();
		for (ComponentType type : values) {
			if (name.equals(type.getName())) {
				return type;
			}
		}
		return null;
	}

}
