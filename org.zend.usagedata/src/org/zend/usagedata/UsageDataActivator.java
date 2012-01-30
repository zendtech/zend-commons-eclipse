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
package org.zend.usagedata;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;
import org.zend.usagedata.gathering.IUsageDataService;
import org.zend.usagedata.internal.gathering.UsageDataService;
import org.zend.usagedata.internal.recording.UsageDataRecorder;
import org.zend.usagedata.internal.recording.uploading.UploadManager;
import org.zend.usagedata.internal.settings.UsageDataSettings;

public class UsageDataActivator extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.zend.usagedata"; //$NON-NLS-1$

	// The shared instance
	private static UsageDataActivator plugin;

	private UploadManager uploadManager;

	private UsageDataSettings settings;

	private UsageDataRecorder usageDataRecorder;

	private BundleContext context;

	private IUsageDataService service;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.context = context;

		uploadManager = new UploadManager();
		settings = new UsageDataSettings();

		settings.setStartTime(System.currentTimeMillis());

		usageDataRecorder = new UsageDataRecorder();
		usageDataRecorder.start();

		service = new UsageDataService();
		service.addUsageDataEventListener(usageDataRecorder);

		getPreferenceStore().addPropertyChangeListener(
				new IPropertyChangeListener() {

					public void propertyChange(PropertyChangeEvent event) {
						if (IUsageDataSettings.CAPTURE_ENABLED_KEY
								.equals(event.getProperty())) {
							if (isTrue(event.getNewValue())) {
								service.startMonitoring();
							} else {
								service.stopMonitoring();
							}
						}
					}

					private boolean isTrue(Object newValue) {
						if (newValue instanceof Boolean)
							return ((Boolean) newValue).booleanValue();
						if (newValue instanceof String)
							return Boolean.valueOf((String) newValue);
						return false;
					}

				});

		UIJob job = new UIJob("Usage Data Service Starter") { //$NON-NLS-1$
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (settings.isEnabled()) {
					service.startMonitoring();
				}
				return Status.OK_STATUS;
			}

		};
		job.schedule(1000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		usageDataRecorder.stop();
		service.removeUsageDataEventListener(usageDataRecorder);

		if (service != null)
			service.stopMonitoring();

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static UsageDataActivator getDefault() {
		return plugin;
	}

	public IUsageDataSettings getSettings() {
		return settings;
	}

	public void log(int status, String message, Object... arguments) {
		log(status, (Exception) null, message, arguments);
	}

	public void log(int status, Throwable exception, String message,
			Object... arguments) {
		log(status, exception, String.format(message, arguments));
	}

	public void log(int status, Throwable e, String message) {
		getLog().log(new Status(status, PLUGIN_ID, message, e));
	}

	public void log(Status status) {
		getLog().log(status);
	}

	public void earlyStartup() {
		// Don't actually need to do anything, but still need the method.
	}

	public UploadManager getUploadManager() {
		return uploadManager;
	}

	public BundleContext getContext() {
		return context;
	}

}
