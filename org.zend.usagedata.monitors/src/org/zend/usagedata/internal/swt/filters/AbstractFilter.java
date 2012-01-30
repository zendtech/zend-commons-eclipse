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
package org.zend.usagedata.internal.swt.filters;

import org.eclipse.swt.widgets.Event;
import org.zend.usagedata.internal.swt.IMonitor;
import org.zend.usagedata.monitors.AbstractMonitor;

/**
 * Abstract class which represents a filter for handling events which have
 * particular type. Implementor should be used with {@link IMonitor}
 * implementation to handle SWT events. Implementor has to provide its
 * implementation of {@link AbstractFilter#handleEvent(Event)} method.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public abstract class AbstractFilter {

	private int[] types;
	private final AbstractMonitor monitor;

	/**
	 * Creates instance of class which extends {@link AbstractFilter}.
	 * 
	 * @param monitor
	 * @param types
	 *            - array of SWT events types
	 */
	public AbstractFilter(AbstractMonitor monitor, int... types) {
		this.types = types;
		this.monitor = monitor;
	}

	/**
	 * Abstract method which has to be implemented by {@link AbstractFilter}
	 * implementors. It is called by {@link EventListener} in
	 * {@link EventListener#handleEvent(Event)} method each time when event has
	 * been thrown which has particular SWT event type.
	 * 
	 * @param event
	 */
	public abstract void handleEvent(Event event);

	/**
	 * Checks if filter is dedicated to handle particular SWT event type.
	 * 
	 * @param eventType
	 * @return <code>true</code> if filter should be used for provided SWT event
	 *         type; <code>false</code> if this filter is not dedicated for this
	 *         SWT event type.
	 */
	public final boolean hasType(int eventType) {
		for (int i = 0; i < types.length; i++) {
			if (types[i] == eventType) {
				return true;
			}
		}
		return false;
	}

	protected void record(int kind, String description, String shellTitle) {
		monitor.recordEvent("", String.valueOf(kind), description, shellTitle); //$NON-NLS-1$
	}

}
