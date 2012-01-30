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

import org.eclipse.swt.widgets.ToolItem;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link ToolItem} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class ToolItemAdapter extends ItemAdapter {

	private static final String PREFIX = ComponentType.TOOL_ITEM.getPrefix();

	public static final String ENABLED = PREFIX + "e"; //$NON-NLS-1$
	public static final String SELECTED = PREFIX + "s"; //$NON-NLS-1$
	public static final String TOOLTIP = PREFIX + "t"; //$NON-NLS-1$

	private ToolItem toolItem;

	public ToolItemAdapter(ToolItem item, int style) {
		super(item, style);
		this.toolItem = item;
		this.componentType = ComponentType.TOOL_ITEM;
	}

	/**
	 * @return <code>true</code> if tool item is enabled; otherwise return
	 *         <code>false</code>
	 */
	public boolean isEnabled() {
		return toolItem.getEnabled();
	}

	/**
	 * @return <code>true</code> if tool item is selected; otherwise return
	 *         <code>false</code>
	 */
	public boolean isSelected() {
		return toolItem.getSelection();
	}

	/**
	 * @return tool item tooltip text
	 */
	public String getTooltip() {
		return toolItem.getToolTipText();
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(ENABLED, isEnabled());
		message.addMessage(SELECTED, isSelected());
		message.addMessage(TOOLTIP, getTooltip());
	}

}
