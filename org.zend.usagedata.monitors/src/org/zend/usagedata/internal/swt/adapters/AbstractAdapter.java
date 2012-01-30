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

import org.zend.usagedata.internal.swt.EventMessage;

/**
 * Abstract class which represents adapter for SWT component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public abstract class AbstractAdapter {

	protected EventMessage message = new EventMessage();

	/**
	 * @return message string for the adapter
	 */
	public final String getMessage() {
		buildMessage();
		return message.getMessage();
	}

	protected abstract void buildMessage();

}
