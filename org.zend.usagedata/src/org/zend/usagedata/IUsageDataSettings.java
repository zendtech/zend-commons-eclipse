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

import java.io.File;

import org.zend.usagedata.internal.settings.UploadSettings;

public interface IUsageDataSettings {


	public static final String LAST_UPLOAD_KEY = UsageDataActivator.PLUGIN_ID
			+ ".last-upload"; //$NON-NLS-1$
	public static final String ASK_TO_UPLOAD_KEY = UsageDataActivator.PLUGIN_ID
			+ ".ask"; //$NON-NLS-1$
	public static final String LOG_SERVER_ACTIVITY_KEY = UsageDataActivator.PLUGIN_ID
			+ ".log-server"; //$NON-NLS-1$
	public static final String CAPTURE_ENABLED_KEY = UsageDataActivator.PLUGIN_ID
			+ ".enabled"; //$NON-NLS-1$
	public static final String USER_ACCEPTED_TERMS_OF_USE_KEY = UsageDataActivator.PLUGIN_ID
			+ ".terms_accepted"; //$NON-NLS-1$

	/**
	 * First if the system property {@value #UPLOAD_PERIOD_KEY} has been set,
	 * use that value. Next, check to see if there is a value stored (same key)
	 * in the preferences store. Finally, use the default value,
	 * {@value #UPLOAD_PERIOD_DEFAULT}. If the obtained value is deemed to be
	 * unreasonable (less than {@value #PERIOD_REASONABLE_MINIMUM}), that a
	 * reasonable minimum value is returned instead.
	 * 
	 * @return
	 */
	long getPeriodBetweenUploads();

	/**
	 * The last upload time is stored in the preferences. If no value is
	 * currently set, the current time is used (and is stored for the next time
	 * we're asked). Time is expressed in milliseconds. There is no mechanism
	 * for overriding this value.
	 * 
	 * @return
	 */
	long getLastUploadTime();

	/**
	 * This method answers <code>true</code> if enough time has passed since
	 * the last upload to warrant starting a new one. If an upload has not yet
	 * occurred, it answers <code>true</code> if the required amount of time
	 * has passed since the first time this method was called. It answers
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if it is time to upload; <code>false</code>
	 *         otherwise.
	 */
	boolean isTimeToUpload();

	/** 
	 * This method returns the {@link File} where usage data events should be persisted.
	 *  
	 * @return the {@link File} where usage data events are persisted.
	 */
	File getEventFile();

	/**
	 * When it's time to start uploading the usage data, the file that's used
	 * to persist the data is moved (renamed) and a new file is created. The
	 * moved file is then uploaded to the server. This method finds an appropriate
	 * destination for the moved file. The destination {@link File} will be in the
	 * bundle's state location, but will not actually exist in the file system.
	 * 
	 * @return a destination {@link File} for the move operation. 
	 */
	File computeDestinationFile();

	/**
	 * This method returns an identifier for the workstation. This value
	 * is common to all workspaces on a single machine. The value
	 * is persisted (if possible) in a hidden file in the users's working 
	 * directory. If an existing file cannot be read, or a new file cannot
	 * be written, this method returns "unknown".
	 * 
	 * @return an identifier for the workstation.
	 */
	String getUserId();

	/**
	 * This method returns an identifier for the workspace. This value is unique
	 * to the workspace. It is persisted (if possible) in a hidden file in the bundle's
	 * state location.If an existing file cannot be read, or a new file cannot
	 * be written, this method returns "unknown".
	 * 
	 * @return an identifier for the workspace.
	 */
	String getWorkspaceId();

	/**
	 * This method answers whether or not we want to ask the server to 
	 * provide a log of activity. This method only answers <code>true</code>
	 * if the "{@value #LOG_SERVER_ACTIVITY_KEY}" system property is set
	 * to "true". This is mostly useful for debugging.
	 * 
	 * @return true if we're logging, false otherwise.
	 * 
	 * @see UploadSettings#isLoggingServerActivity()
	 */
	boolean isLoggingServerActivity();

	/**
	 * This method answers an array containing the files that are available
	 * for uploading.
	 * 
	 * @return
	 */
	File[] getUsageDataUploadFiles();

	/**
	 * This method sets the {@value #LAST_UPLOAD_KEY} property to the
	 * current time.
	 */
	void setLastUploadTime();

	boolean shouldAskBeforeUploading();

	boolean hasUserAcceptedTermsOfUse();

	void setUserAcceptedTermsOfUse(boolean value);

	boolean isEnabled();

	void setAskBeforeUploading(boolean value);

	void setEnabled(boolean value);

	String getUserAgent();

	String getUploadUrl();

}