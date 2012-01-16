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

public interface UploadSettings {

	/**
	 * This method answers whether or not we want to ask the server to 
	 * provide a log of activity. 
	 * 
	 * @return true if we're logging, false otherwise.
	 */
	public abstract boolean isLoggingServerActivity();

	/**
	 * This method returns the target URL for uploads.
	 * 
	 * @return the target URL for uploads.
	 */
	public abstract String getUploadUrl();

	public abstract boolean hasUserAcceptedTermsOfUse();

	public abstract boolean isEnabled();

	public abstract String getUserId();

	public abstract String getWorkspaceId();

	public abstract String getUserAgent();

}