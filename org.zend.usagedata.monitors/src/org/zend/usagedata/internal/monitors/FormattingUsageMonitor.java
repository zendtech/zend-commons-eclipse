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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.zend.usagedata.gathering.IUsageDataService;
import org.zend.usagedata.gathering.IUsageMonitor;

/**
 * Instances of the {@link FormattingUsageMonitor} class monitor changes in PHP
 * formatting preferences. Each of change is stored with following attributes:
 * <ul>
 * <li>formatting profile and preference key,</li>
 * <li>old value,</li>
 * <li>new value.</li>
 * </ul>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class FormattingUsageMonitor implements IUsageMonitor {

	public static final String MONITOR_ID = "org.zend.formattingUsageMonitor"; //$NON-NLS-1$

	private static final String ZEND_FORMATTER_NODE = "com.zend.php.formatter.core"; //$NON-NLS-1$
	private static final String FORMATTER_PROFILE_NODE = "formatterProfile"; //$NON-NLS-1$
	private static final String FORMATTER_PROFILES_NODE = "formatterprofiles"; //$NON-NLS-1$

	private IUsageDataService usageDataService;

	private IPreferenceChangeListener listener = new IPreferenceChangeListener() {

		@Override
		public void preferenceChange(PreferenceChangeEvent event) {
			String key = event.getKey().substring(
					event.getKey().lastIndexOf(".") + 1); //$NON-NLS-1$
			if (!FORMATTER_PROFILES_NODE.equals(key)) {
				String profile = node.get(FORMATTER_PROFILE_NODE, ""); //$NON-NLS-1$
				usageDataService.recordEvent(MONITOR_ID, profile, key,
						String.valueOf(event.getOldValue()),
						String.valueOf(event.getNewValue()));
			}
		}

	};

	private IEclipsePreferences node;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.epp.usagedata.internal.gathering.UsageMonitor#register(org
	 * .eclipse.epp.usagedata.internal.gathering.UsageDataService)
	 */
	public void startMonitoring(IUsageDataService usageDataService) {
		this.usageDataService = usageDataService;
		node = InstanceScope.INSTANCE.getNode(ZEND_FORMATTER_NODE);
		node.addPreferenceChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.epp.usagedata.internal.gathering.UsageMonitor#deregister()
	 */
	public void stopMonitoring() {
		node.removePreferenceChangeListener(listener);
	}

}