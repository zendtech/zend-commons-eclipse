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

import org.eclipse.swt.widgets.List;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link List} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class ListAdapter extends ControlAdapter {

	private static final String PREFIX = ComponentType.LIST.getPrefix();

	public static final String SELECTION = PREFIX + "s"; //$NON-NLS-1$
	public static final String SELECTION_INDEX = PREFIX + "i"; //$NON-NLS-1$

	private List list;

	public ListAdapter(List list, int eventType) {
		super(list, eventType);
		this.list = list;
		this.componentType = ComponentType.LIST;
	}

	/**
	 * @return selection index
	 */
	public int getSelectionIndex() {
		return list.getSelectionIndex();
	}

	/**
	 * @return selection
	 */
	public String getSelection() {
		StringBuilder result = new StringBuilder();
		result.append("\""); //$NON-NLS-1$
		String[] selection = list.getSelection();
		for (String s : selection) {
			result.append(s);
			result.append("|"); //$NON-NLS-1$
		}
		result.append("\""); //$NON-NLS-1$
		return result.toString();
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(SELECTION, getSelection());
		message.addMessage(SELECTION_INDEX, getSelectionIndex());
	}

}
