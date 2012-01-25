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
package org.zend.usagedata.monitors;

import org.zend.usagedata.gathering.IUsageDataService;
import org.zend.usagedata.gathering.IUsageMonitor;

/**
 * Abstract monitor class which allows implementors to focus only on specific
 * actions related to data which should be collected.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public abstract class AbstractMonitor implements IUsageMonitor {

	private IUsageDataService usageDataService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.epp.usagedata.internal.gathering.UsageMonitor#register(org
	 * .eclipse.epp.usagedata.internal.gathering.UsageDataService)
	 */
	public final void startMonitoring(IUsageDataService usageDataService) {
		this.usageDataService = usageDataService;
		doStartMonitoring();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.epp.usagedata.internal.gathering.UsageMonitor#deregister()
	 */
	public final void stopMonitoring() {
		this.usageDataService = null;
		doStopMonitoring();
	}

	/**
	 * Record event using {@link IUsageDataService}.
	 * 
	 * @param monitorId
	 *            - monitor id
	 * @param arg1
	 *            - custom argument
	 * @param arg2
	 *            - custom argument
	 * @param arg3
	 *            - custom argument
	 * @param arg4
	 *            - custom argument
	 */
	public final void recordEvent(String monitorId, String arg1,
			String arg2, String arg3, String arg4) {
		if (usageDataService != null) {
			usageDataService.recordEvent(monitorId, arg1, arg2, arg3, arg4);
		}
	}

	/**
	 * Record event using {@link IUsageDataService}.
	 * 
	 * @param monitorId
	 *            - monitor id
	 * @param arg1
	 *            - custom argument
	 * @param arg2
	 *            - custom argument
	 * @param arg3
	 *            - custom argument
	 */
	public final void recordEvent(String monitorId, String arg1,
			String arg2, String arg3) {
		recordEvent(monitorId, arg1, arg2, arg3, ""); //$NON-NLS-1$
	}

	/**
	 * Perform additional actions in
	 * {@link AbstractMonitor#startMonitoring(IUsageDataService)}.
	 */
	protected abstract void doStartMonitoring();

	/**
	 * Perform additional actions in {@link AbstractMonitor#stopMonitoring()}.
	 */
	protected abstract void doStopMonitoring();

}
