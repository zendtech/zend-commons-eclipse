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
package org.zend.usagedata.internal.monitors;

import org.eclipse.core.runtime.IStatus;
import org.zend.usagedata.monitors.AbstractMonitor;

/**
 * Instances of the {@link FeatureUsageMonitor} class monitor features
 * popularity. It records each installation and removal of any feature provided
 * by customization mechanism.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class FeatureUsageMonitor extends AbstractMonitor {

	public static final String MONTIOR_ID = "org.zend.featuresUsageMonitor"; //$NON-NLS-1$

	private static AbstractMonitor monitor;

	public FeatureUsageMonitor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.usagedata.monitors.AbstractMonitor#doStartMonitoring()
	 */
	@Override
	public void doStartMonitoring() {
		FeatureUsageMonitor.monitor = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.usagedata.monitors.AbstractMonitor#doStopMonitoring()
	 */
	@Override
	protected void doStopMonitoring() {
		FeatureUsageMonitor.monitor = null;
	}

	/**
	 * Records feature usage event.
	 * 
	 * @param monitorId
	 *            - id of monitor which records an event
	 * @param added
	 *            - list of added features separated by ';'
	 * @param removed
	 *            - list of removed features separated by ';'
	 * @param status
	 *            - status code of features installation/removal operation
	 * @param statusMessage
	 *            - status message, empty if status severity is
	 *            {@link IStatus#OK}
	 */
	static void recordFeatureEvent(String monitorId, String added,
			String removed, String status, String statusMessage) {
		monitor.recordEvent(monitorId, added, removed, status, statusMessage);
	}

	/**
	 * @return <code>true</code> if features monitoring is enabled; otherwise
	 *         return <code>false</code>
	 */
	static boolean isMonitoring() {
		return monitor != null ? true : false;
	}

}
