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

import java.util.List;

import org.eclipse.core.runtime.IStatus;

import com.zend.php.customization.IProfileModificationListener;

/**
 * Implementation of {@link IProfileModificationListener}. It listens on profile
 * modifications and record those events using usage reporitng mechanism.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ProfileModificationListener implements
		IProfileModificationListener {

	public ProfileModificationListener() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.zend.php.customization.IProfileModificationListener#profileChanged
	 * (java.util.List, java.util.List, org.eclipse.core.runtime.IStatus)
	 */
	@Override
	public void profileChanged(List<String> added, List<String> removed,
			IStatus status) {
		if (FeatureUsageMonitor.isMonitoring()) {
			String message = status.getSeverity() != IStatus.OK ? status
					.getMessage() : "";
			FeatureUsageMonitor.recordEvent(FeatureUsageMonitor.MONTIOR_ID,
					getString(added), getString(removed),
					String.valueOf(status.getSeverity()), message);
		}
	}

	private String getString(List<String> added) {
		StringBuilder builder = new StringBuilder();
		for (String id : added) {
			builder.append(id);
			builder.append(";");
		}
		return added.size() > 0 ? builder.toString() : "";
	}

}
