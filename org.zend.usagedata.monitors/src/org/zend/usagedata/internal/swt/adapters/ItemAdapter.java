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

import org.eclipse.swt.widgets.Item;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link Item} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class ItemAdapter extends WidgetAdapter {

	private static final String PREFIX = ComponentType.ITEM.getPrefix();

	public static final String TEXT = PREFIX + "t"; //$NON-NLS-1$

	private Item item;

	public ItemAdapter(Item item, int style) {
		super(item, style);
		this.item = item;
		this.componentType = ComponentType.ITEM;
	}

	/**
	 * @return item text
	 */
	public String getText() {
		return item.getText();
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(TEXT, getText());
	}

}
