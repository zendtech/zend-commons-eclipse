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

import org.zend.usagedata.internal.swt.adapters.AbstractAdapter;
import org.zend.usagedata.internal.swt.filters.AbstractFilter;
import org.zend.usagedata.monitors.AbstractMonitor;

/**
 * Abstract SWT usage monitor. It extends default {@link AbstractMonitor} with
 * keeping last event which was recored. It can be used by implementation of
 * {@link AbstractFilter} to filter redundant events.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public abstract class SWTUsageMonitor extends AbstractMonitor {

	protected IMonitor monitor;

	protected SWTUsageEvent last;

	public SWTUsageEvent getLastEvent() {
		return last;
	}

	/**
	 * Record provided SWT event.
	 * 
	 * @param event
	 */
	public void recordEvent(SWTUsageEvent event) {
		last = event;
		AbstractAdapter adapter = event.getAdapter();
		recordEvent(getId(), String.valueOf(event.getKind()),
				adapter.getMessage(), adapter.getShell());
	}

}
