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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link Text} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class TextAdapter extends ControlAdapter {

	private static final String PREFIX = ComponentType.TEXT.getPrefix();

	public static final String EDITABLE = PREFIX + "e"; //$NON-NLS-1$
	public static final String HAS_CONTENT = PREFIX + "c"; //$NON-NLS-1$
	public static final String TEXT = PREFIX + "t"; //$NON-NLS-1$
	public static final String LABEL = PREFIX + "l"; //$NON-NLS-1$

	private Text text;

	public TextAdapter(Text text, int eventType) {
		super(text, eventType);
		this.text = text;
		this.componentType = ComponentType.TEXT;
	}

	/**
	 * @return <code>true</code> if text has content; otherwise return
	 *         <code>false</code>
	 */
	public boolean hasContent() {
		return text.getCharCount() > 0 ? true : false;
	}

	/**
	 * To avoid collecting personal data text value is replaced by string of 'X'
	 * with the same length.
	 * 
	 * @return text value
	 */
	public String getTextValue() {
		return text.getText().replaceAll(".", "X"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @return <code>true</code> if text is editable; otherwise return
	 *         <code>false</code>
	 */
	public boolean isEditable() {
		return text.getEditable();
	}

	/**
	 * @return label text which describes this text
	 */
	public String getLabel() {
		Control[] children = text.getParent().getChildren();
		Control previous = null;
		for (int j = 0; j < children.length; j++) {
			if (children[j] == text && j > 0) {
				previous = children[j - 1];
			}
		}
		if (previous != null && previous instanceof Label) {
			return ((Label) previous).getText();
		}
		return null;
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(EDITABLE, isEditable());
		message.addMessage(HAS_CONTENT, hasContent());
		message.addMessage(TEXT, getTextValue());
		String label = getLabel();
		if (label != null) {
			message.addMessage(LABEL, label);
		}
	}

}
