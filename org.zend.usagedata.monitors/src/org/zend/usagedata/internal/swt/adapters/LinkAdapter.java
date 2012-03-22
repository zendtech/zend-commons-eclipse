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

import org.eclipse.swt.widgets.Link;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link Link} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class LinkAdapter extends ControlAdapter {

	private static final String PREFIX = ComponentType.LINK.getPrefix();

	public static final String TEXT = PREFIX + "t"; //$NON-NLS-1$

	private Link link;

	public LinkAdapter(Link link, int eventType) {
		super(link, eventType);
		this.link = link;
		link.getText();
		this.componentType = ComponentType.LINK;
	}

	/**
	 * @return link text
	 */
	public String getText() {
		return link != null ? link.getText() : null;
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(TEXT, getText());
	}

}
