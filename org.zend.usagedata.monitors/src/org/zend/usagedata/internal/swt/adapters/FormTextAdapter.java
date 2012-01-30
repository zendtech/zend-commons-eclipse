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

import org.eclipse.ui.forms.widgets.FormText;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for {@link FormText} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class FormTextAdapter extends ControlAdapter {

	private static final String PREFIX = ComponentType.FORM_TEXT.getPrefix();

	public static final String TEXT = PREFIX + "t"; //$NON-NLS-1$

	private FormText formText;

	public FormTextAdapter(FormText text, int eventType) {
		super(text, eventType);
		this.formText = text;
		this.componentType = ComponentType.FORM_TEXT;
	}

	/**
	 * @return selected link text
	 */
	public String getSelectedLinkText() {
		return formText.getSelectedLinkText();
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(TEXT, getSelectedLinkText());
	}

}
