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

import org.eclipse.swt.widgets.ExpandItem;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link ExpandItem} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class ExpandItemAdapter extends ItemAdapter {

	private static final String PREFIX = ComponentType.EXPAND_ITEM.getPrefix();

	public static final String EXPANDED = PREFIX + "e"; //$NON-NLS-1$

	private ExpandItem expandItem;

	public ExpandItemAdapter(ExpandItem item, int style) {
		super(item, style);
		this.expandItem = item;
		this.componentType = ComponentType.EXPAND_ITEM;
	}

	/**
	 * @return <code>true</code> if expand item is expanded; otherwise return
	 *         <code>false</code>
	 */
	public Boolean isExpanded() {
		return expandItem != null ? expandItem.getExpanded() : null;
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(EXPANDED, isExpanded());
	}

}
