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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link Button} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class ButtonAdapter extends ControlAdapter {

	private static final String PREFIX = ComponentType.BUTTON.getPrefix();

	public static final String TEXT = PREFIX + "t"; //$NON-NLS-1$
	public static final String SELECTION = PREFIX + "s"; //$NON-NLS-1$
	public static final String TYPE = PREFIX + "t1"; //$NON-NLS-1$

	private Button button;

	public ButtonAdapter(Button button, int eventType) {
		super(button, eventType);
		this.button = button;
		this.componentType = ComponentType.BUTTON;
	}

	/**
	 * @return button text
	 */
	public String getText() {
		return button != null ? button.getText() : null;
	}

	/**
	 * @return <code>true</code> if button is selected; otherwise return
	 *         <code>false</code>
	 */
	public Boolean isSelected() {
		return button != null ? button.getSelection() : null;
	}

	public int getType() {
		if ((getStyle() & SWT.PUSH) == SWT.PUSH) {
			return SWT.PUSH;
		} else if ((getStyle() & SWT.CHECK) == SWT.CHECK) {
			return SWT.CHECK;
		} else if ((getStyle() & SWT.RADIO) == SWT.RADIO) {
			return SWT.RADIO;
		}
		return -1;
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(TEXT, getText());
		message.addMessage(SELECTION, isSelected());
		message.addMessage(TYPE, getType());
	}

}
