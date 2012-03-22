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

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link Combo} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class ComboAdapter extends ControlAdapter {

	private static final String PREFIX = ComponentType.COMBO.getPrefix();

	public static final String TEXT = PREFIX + "t"; //$NON-NLS-1$
	public static final String LABEL = PREFIX + "l"; //$NON-NLS-1$

	private Combo combo;

	public ComboAdapter(Combo combo, int eventType) {
		super(combo, eventType);
		this.combo = combo;
		this.componentType = ComponentType.COMBO;
	}

	/**
	 * @return combo text
	 */
	public String getText() {
		return combo != null ? combo.getText() : null;

	}

	/**
	 * @return label which describes this combo
	 */
	public String getLabel() {
		if (combo != null) {
			Control[] children = combo.getParent().getChildren();
			Control previous = null;
			for (int j = 0; j < children.length; j++) {
				if (children[j] == combo && j > 0) {
					previous = children[j - 1];
				}
			}
			if (previous != null && previous instanceof Label) {
				return ((Label) previous).getText();
			}
		}
		return null;
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(TEXT, getText());
		message.addMessage(LABEL, getLabel());
	}

}
