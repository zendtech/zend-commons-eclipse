/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.usagedata.internal.swt;

import org.zend.usagedata.internal.swt.adapters.WidgetAdapter;

/**
 * Represents event which is generated during user interaction with a GUI. It
 * provides information what kind of SWT event was performed and on which SWT
 * component.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class SWTUsageEvent {

	private int kind;

	private WidgetAdapter adapter;

	public SWTUsageEvent(int kind, WidgetAdapter adapter) {
		super();
		this.kind = kind;
		this.adapter = adapter;
	}

	/**
	 * @return SWT component adapter
	 */
	public WidgetAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @return SWT event type
	 */
	public int getKind() {
		return kind;
	}

}
