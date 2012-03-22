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
import org.eclipse.swt.widgets.CoolItem;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link CoolItem} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class CoolItemAdapter extends ItemAdapter {

	private static final String PREFIX = ComponentType.COOL_ITEM.getPrefix();

	public static final String ACTION = PREFIX + "a"; //$NON-NLS-1$

	private CoolItem coolItem;

	public CoolItemAdapter(CoolItem item, int style) {
		super(item, style);
		this.coolItem = item;
		this.componentType = ComponentType.COOL_ITEM;
	}

	/**
	 * @return name of the action associated with this cool item
	 */
	public String getAction() {
		if (coolItem != null) {
			Object data = coolItem.getData();
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
		message.addMessage(ACTION, getAction());
	}

}
