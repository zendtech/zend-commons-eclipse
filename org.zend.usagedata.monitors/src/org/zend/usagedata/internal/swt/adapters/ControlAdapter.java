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

import org.eclipse.swt.widgets.Control;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link Control} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class ControlAdapter extends WidgetAdapter {

	private static final String PREFIX = ComponentType.CONTROL.getPrefix();

	public static final String ENABLED = PREFIX + "e"; //$NON-NLS-1$
	public static final String TOOLTIP = PREFIX + "t"; //$NON-NLS-1$

	private Control control;

	public ControlAdapter(Control control, int eventType) {
		super(control, eventType);
		this.control = control;
		this.componentType = ComponentType.CONTROL;
	}

	/**
	 * @return <code>true</code> if control is selected; otherwise return
	 *         <code>false</code>
	 */
	public boolean isEnabled() {
		return control.getEnabled();
	}

	/**
	 * @return control tooltip
	 */
	public String getTooltip() {
		return control.getToolTipText();
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(ENABLED, isEnabled());
		message.addMessage(TOOLTIP, getTooltip());
	}

}
