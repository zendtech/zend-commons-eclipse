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
package org.zend.usagedata.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.usagedata.ui.internal.UIUsageDataActivator;

/**
 * Usage data UI plug-in settings. It allows to set and get all plug-in related
 * settings.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class UIUploadSettings {

	private static final String DO_NOT_SHOW_AGAIN = UIUsageDataActivator.PLUGIN_ID
			+ ".do_not_show_again"; //$NON-NLS-1$

	/**
	 * Sets value of preference which is responsible for displaying message
	 * about collected data.
	 * 
	 * @param value
	 */
	public static void setDoNotShowAgain(boolean value) {
		IEclipsePreferences instanceScope = InstanceScope.INSTANCE
				.getNode(UIUsageDataActivator.PLUGIN_ID);
		instanceScope.putBoolean(DO_NOT_SHOW_AGAIN, value);
		try {
			instanceScope.flush();
		} catch (BackingStoreException e) {
			UIUsageDataActivator.log(e);
		}
	}

	/**
	 * @return if <code>true</code> then message will be displayed when data is
	 *         collected; otherwise message will not be displayed.
	 */
	public static boolean isDoNotShowAgain() {
		IEclipsePreferences instanceScope = InstanceScope.INSTANCE
				.getNode(UIUsageDataActivator.PLUGIN_ID);
		return instanceScope.getBoolean(DO_NOT_SHOW_AGAIN, false);
	}

}
