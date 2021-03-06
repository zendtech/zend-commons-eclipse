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
package org.zend.usagedata.internal.recording.uploading;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.zend.usagedata.IUsageDataSettings;
import org.zend.usagedata.UsageDataActivator;
import org.zend.usagedata.internal.settings.UploadSettings;
import org.zend.usagedata.recording.IPreUploadListener;
import org.zend.usagedata.recording.IUploadParameters;
import org.zend.usagedata.recording.IUploader;

public class UploadManager {

	private static final String PREUPLOAD_LISTENERS = UsageDataActivator.PLUGIN_ID
			+ ".preUploadListeners"; //$NON-NLS-1$

	public static final int UPLOAD_STARTED_OK = 0;
	public static final int NO_FILES_TO_UPLOAD = 1;
	public static final int UPLOAD_IN_PROGRESS = 2;
	public static final int WORKBENCH_IS_CLOSING = 3;
	public static final int NO_UPLOADER = 4;
	public static final int UPLOAD_DISABLED = 5;
	
	private Object lock = new Object();
	private IUploader uploader;
	private ListenerList uploadListeners = new ListenerList();
	private boolean askedAboutUpload;

	/**
	 * This method starts the upload. The first thing it does is find the files
	 * containing data that needs to be uploaded. If no data is found, then the
	 * method simply returns and the universe is left to unfold as it will. If
	 * data is found, we continue.
	 * <p>
	 * The settings are checked to see what the user wants us to do with the
	 * data. If the user has authorized that the data be uploaded, an upload job
	 * is spawned. If the settings indicate that the user must be asked what to
	 * do, then an editor is opened which invites the user to decide what to do
	 * with the information.
	 * </p>
	 * <p>
	 * This method returns a status code. The value is
	 * {@link #UPLOAD_IN_PROGRESS} if an upload is already in progress when the
	 * request is made, {@link #NO_FILES_TO_UPLOAD} if no files are available
	 * for upload, {@link #WORKBENCH_IS_CLOSING} if the workbench is closing at
	 * the time the request is made, {@link #NO_UPLOADER} if an uploader cannot
	 * be found, or {@value #UPLOAD_STARTED_OK} if a new upload is started.
	 * </p>
	 * 
	 * @param shouldUpload
	 *            - <code>true</code> if data should be uploaded;
	 *            <code>false</code> if only preUpload listeners should be
	 *            called.
	 * 
	 * @return a status code.
	 */
	public int startUpload(boolean shouldUpload) {
		if (!getSettings().isEnabled()) return UPLOAD_DISABLED;
		if (PlatformUI.getWorkbench().isClosing()) return WORKBENCH_IS_CLOSING;
		
		File[] usageDataUploadFiles;
		synchronized (lock) {
			if (!handlePreUploadListeners() || !shouldUpload) {
				return UPLOAD_STARTED_OK;
			}

			if (uploader != null) {
				return UPLOAD_IN_PROGRESS;
			}
			
			usageDataUploadFiles = findUsageDataUploadFiles();
			if (usageDataUploadFiles.length == 0) {
				return NO_FILES_TO_UPLOAD;
			}

			uploader = getUploader();
			if (uploader == null) {
				return NO_UPLOADER;
			}
		}
		
		getSettings().setLastUploadTime();
		
		IUploadParameters uploadParameters = new UploadParameters();
		uploadParameters.setSettings((UploadSettings) getSettings());
		uploadParameters.setFiles(usageDataUploadFiles);
		
		uploader.setUploadParameters(uploadParameters);
		
		/*
		 * Add a listener to the new uploader so that it will notify
		 * us when it is complete. Then, we'll notify our own listeners.
		 */
		uploader.addUploadListener(new UploadListener() {
			public void uploadComplete(UploadResult result) {
				uploader = null;
				fireUploadComplete(result);
			}
		});
		
		uploader.startUpload();
		return UPLOAD_STARTED_OK;
	}

	private boolean handlePreUploadListeners() {
		if (UsageDataActivator.getDefault().getSettings().isEnabled()) {
			if (UsageDataActivator.getDefault().getSettings()
					.shouldAskBeforeUploading()) {
				if (!askedAboutUpload) {
					List<IPreUploadListener> listeners = getListeners();
					for (IPreUploadListener listener : listeners) {
						listener.handleUpload();
						askedAboutUpload = true;
					}
				}
				return false;
			}
			return true;
		}
		return false;
	}

	private List<IPreUploadListener> getListeners() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(PREUPLOAD_LISTENERS);
		List<IPreUploadListener> result = new ArrayList<IPreUploadListener>();
		for (IConfigurationElement element : elements) {
			if ("preUploadListener".equals(element.getName())) { //$NON-NLS-1$
				try {
					Object listener = element
							.createExecutableExtension("class"); //$NON-NLS-1$
					if (listener instanceof IPreUploadListener) {
						result.add((IPreUploadListener) listener);
					}
				} catch (CoreException e) {
					UsageDataActivator.getDefault().log(
							new Status(IStatus.ERROR,
									UsageDataActivator.PLUGIN_ID, e
											.getMessage(), e));
				}
			}
		}
		return result;
	}

	private File[] findUsageDataUploadFiles() {
		return getSettings().getUsageDataUploadFiles();
	}
	
	private IUsageDataSettings getSettings() {
		return UsageDataActivator.getDefault().getSettings();
	}
	
	/**
	 * This method returns the {@link IUploader} to use to upload data to the
	 * server.
	 * 
	 * @return basic uploader instance
	 */
	private IUploader getUploader() {
		if (uploader == null) {
			uploader = new BasicUploader();
		}
		return uploader;
	}
	
	public void addUploadListener(UploadListener listener) {
		uploadListeners.add(listener);
	}

	public void removeUploadListener(UploadListener listener) {
		uploadListeners.remove(listener);
	}
	
	protected void fireUploadComplete(UploadResult result) {
		for (Object listener : uploadListeners.getListeners()) {
			((UploadListener)listener).uploadComplete(result);
		}
	}
}
