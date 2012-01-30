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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.UUID;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.zend.usagedata.IUsageDataSettings;
import org.zend.usagedata.UsageDataActivator;

/**
 * This class provides a convenient location to find the settings
 * for this bundle. Some settings are in the preferences; others
 * are found in system properties. Still more are simply provided
 * as constant values.
 * 
 * @author Wayne Beaton
 *
 */
public class UsageDataSettings implements UploadSettings, IUsageDataSettings {

	private static final String USAGEDATA_FILE_NAME = "usagedata"; //$NON-NLS-1$
	private static final String DEFAULT_ID = "unknown"; //$NON-NLS-1$
	private static final String UPLOAD_FILE_PREFIX = "upload"; //$NON-NLS-1$
	private static final String DEFAULT_FORMAT = ".csv"; //$NON-NLS-1$

	// 5 days
	static final int UPLOAD_PERIOD_DEFAULT = 5 * 24 * 60 * 60 * 1000;

	// 5 minutes
	static final int ASK_TIME = 5 * 60 * 1000;

	static final String UPLOAD_URL_KEY = UsageDataActivator.PLUGIN_ID
			+ ".upload-url"; //$NON-NLS-1$

	static final String UPLOAD_URL_DEFAULT = "http://wojtek.my.phpcloud.com/udc/index.php"; //$NON-NLS-1$

	private long startTime;

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#getPeriodBetweenUploads()
	 */
	public long getPeriodBetweenUploads() {
		return UPLOAD_PERIOD_DEFAULT;
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#getLastUploadTime()
	 */
	public long getLastUploadTime() {
		if (getPreferencesStore().contains(LAST_UPLOAD_KEY)) {
			return getPreferencesStore().getLong(LAST_UPLOAD_KEY);
		}
		long period = System.currentTimeMillis();
		getPreferencesStore().setValue(LAST_UPLOAD_KEY, period);
		UsageDataActivator.getDefault().savePluginPreferences();

		return period;
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#isTimeToUpload()
	 */
	public boolean isTimeToUpload() {
		if (PlatformUI.getWorkbench().isClosing())
			return false;
		return System.currentTimeMillis() - getLastUploadTime() > getPeriodBetweenUploads();
	}

	@Override
	public boolean isTimeToAsk() {
		if (PlatformUI.getWorkbench().isClosing()) {
			return false;
		}
		return System.currentTimeMillis() - startTime > ASK_TIME;
	}

	@Override
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#getEventFile()
	 */
	public File getEventFile() {
		return new File(getWorkingDirectory(), USAGEDATA_FILE_NAME + DEFAULT_FORMAT);
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#computeDestinationFile()
	 */
	public File computeDestinationFile() {
		int index = 0;
		File parent = getWorkingDirectory();
		File file = null;
		// TODO Unlikely (impossible?), but what if this spins forever.
		while (true) {
			file = new File(parent, UPLOAD_FILE_PREFIX + index++
					+ DEFAULT_FORMAT);
			if (!file.exists())
				return file;
		}
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#getUserId()
	 */
	public String getUserId() {
		return getExistingOrGenerateId(
				new File(System.getProperty("user.home")), "." + UsageDataActivator.PLUGIN_ID //$NON-NLS-1$ //$NON-NLS-2$
				+ ".userId"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#getWorkspaceId()
	 */
	public String getWorkspaceId() {
		return getExistingOrGenerateId(getWorkingDirectory(), "." //$NON-NLS-1$
				+ UsageDataActivator.PLUGIN_ID + ".workspaceId"); //$NON-NLS-1$
	}


	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#isLoggingServerActivity()
	 */
	public boolean isLoggingServerActivity() {
		return "true".equals(System.getProperty(LOG_SERVER_ACTIVITY_KEY)); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#getUsageDataUploadFiles()
	 */
	public File[] getUsageDataUploadFiles() {
		return getWorkingDirectory().listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(UPLOAD_FILE_PREFIX);
			}

		});
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#setLastUploadTime()
	 */
	public void setLastUploadTime() {
		getPreferencesStore().setValue(LAST_UPLOAD_KEY, System.currentTimeMillis());
		UsageDataActivator.getDefault().savePluginPreferences();
	}
	
	/**
	 * <p>
	 * This method either finds an existing id or generates a new one. The id is
	 * stored in file system at the given path and file. If the file exists, the
	 * id is extracted from it. If the file does not exist, or if an id cannot
	 * be determined from its contents, a new id is generated and then stored in
	 * the file. If the file cannot be read or written (i.e. an IOException
	 * occurs), the operation is aborted and "unknown" is returned.
	 * </p>
	 * 
	 * @param directory
	 *           the directory that will contain the stored id.
	 * @param fileName
	 *            name of the file containing the id.
	 * @return a globally unique id.
	 */
	private String getExistingOrGenerateId(File directory, String fileName) {
		if (!directory.exists()) return DEFAULT_ID;
		if (!directory.isDirectory()) {
		} // TODO Think of something else
		File file = new File(directory, fileName);
		if (file.exists()) {
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				char[] buffer = new char[256];
				int count = reader.read(buffer);
				// TODO what if the file can't be read, or if there is no
				// content?
				return new String(buffer, 0, count);
			} catch (IOException e) {
				handleCannotReadFileException(file, e);
				return DEFAULT_ID;
			} finally {
				close(reader);
			}
		} else {
			String id = UUID.randomUUID().toString();
			FileWriter writer = null;
			try {
				// TODO What if there is a collection with another process?
				writer = new FileWriter(file);
				writer.write(id);
				return id;
			} catch (IOException e) {
				handleCannotReadFileException(file, e);
				return DEFAULT_ID;
			} finally {
				close(writer);
			}
		}
	}

	private void handleCannotReadFileException(File file, IOException e) {
		UsageDataActivator
				.getDefault()
				.log(IStatus.WARNING,
						e,
						"Cannot read the existing id from %1$s; using the default.", file.toString()); //$NON-NLS-1$
	}

	private IPreferenceStore getPreferencesStore() {
		return UsageDataActivator.getDefault().getPreferenceStore();
	}
	
	private File getWorkingDirectory() {
		return UsageDataActivator.getDefault().getStateLocation().toFile();
	}
	
	/**
	 * Convenience method for closing a {@link Writer} that could possibly be
	 * <code>null</code>.
	 * 
	 * @param writer
	 *            the {@link Writer} to close.
	 */
	private void close(Writer writer) {
		if (writer == null)
			return;
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Handle exception
		}
	}

	/**
	 * Convenience method for closing a {@link Reader} that could possibly be
	 * <code>null</code>.
	 * 
	 * @param reader
	 *            the {@link Reader} to close.
	 */
	private void close(Reader reader) {
		if (reader == null)
			return;
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Handle exception
		}
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#shouldAskBeforeUploading()
	 */
	public boolean shouldAskBeforeUploading() {
		return getPreferencesStore().getBoolean(ASK_TO_UPLOAD_KEY);
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#hasUserAcceptedTermsOfUse()
	 */
	public boolean hasUserAcceptedTermsOfUse() {
		return getPreferencesStore().getBoolean(USER_ACCEPTED_TERMS_OF_USE_KEY);
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#setUserAcceptedTermsOfUse(boolean)
	 */
	public void setUserAcceptedTermsOfUse(boolean value) {
		getPreferencesStore().setValue(USER_ACCEPTED_TERMS_OF_USE_KEY, value);
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#isEnabled()
	 */
	public boolean isEnabled() {
		if (System.getProperties().containsKey(CAPTURE_ENABLED_KEY)) {
			return "true".equals(System.getProperty(CAPTURE_ENABLED_KEY)); //$NON-NLS-1$
		} else if (getPreferencesStore().contains(CAPTURE_ENABLED_KEY)) {
			return getPreferencesStore().getBoolean(CAPTURE_ENABLED_KEY);
		} else {
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#setAskBeforeUploading(boolean)
	 */
	public void setAskBeforeUploading(boolean value) {
		getPreferencesStore().setValue(ASK_TO_UPLOAD_KEY, value);
		UsageDataActivator.getDefault().savePluginPreferences();
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#setEnabled(boolean)
	 */
	public void setEnabled(boolean value) {
		// The preferences store actually does this for us. However, for
		// completeness, we're checking the value to potentially avoid
		// messing with the service.
		if (getPreferencesStore().getBoolean(CAPTURE_ENABLED_KEY) == value)
			return;

		getPreferencesStore().setValue(CAPTURE_ENABLED_KEY, value);

		// The activator should be listening to changes in the preferences store
		// and will change the state of the service as a result of us setting
		// the value here.
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#getUserAgent()
	 */
	public String getUserAgent() {
		return "Zend UDC/" + UsageDataActivator.getDefault().getBundle().getHeaders().get("Bundle-Version"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/* (non-Javadoc)
	 * @see org.zend.usagedata.internal.settings.IUsageDataSettings#getUploadUrl()
	 */
	public String getUploadUrl() {
		if (System.getProperties().containsKey(UPLOAD_URL_KEY)) {
			return System.getProperty(UPLOAD_URL_KEY);
		}
		return UPLOAD_URL_DEFAULT;
	}

}
