/*******************************************************************************
 * Copyright (c) 2007, 2012 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    The Eclipse Foundation - initial API and implementation
 *******************************************************************************/
package org.zend.usagedata.internal.settings;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.zend.usagedata.IUsageDataSettings;
import org.zend.usagedata.UsageDataActivator;

public class UsageDataCapturePreferenceInitializer extends AbstractPreferenceInitializer {

	private static final boolean ASK_TO_UPLOAD_DEFAULT = true;

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferenceStore = UsageDataActivator.getDefault()
				.getPreferenceStore();
		preferenceStore
				.setDefault(IUsageDataSettings.CAPTURE_ENABLED_KEY, true);
		preferenceStore.setDefault(
				IUsageDataSettings.USER_ACCEPTED_TERMS_OF_USE_KEY, false);
		preferenceStore.setDefault(IUsageDataSettings.ASK_TO_UPLOAD_KEY,
				ASK_TO_UPLOAD_DEFAULT);
	}

}
