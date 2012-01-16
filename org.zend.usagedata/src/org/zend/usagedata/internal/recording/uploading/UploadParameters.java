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

import org.zend.usagedata.internal.settings.UploadSettings;
import org.zend.usagedata.recording.IUploadParameters;


public class UploadParameters implements IUploadParameters {

	private File[] files;
	private UploadSettings settings;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.usagedata.internal.recording.uploading.IUploadParameters#setSettings
	 * (org.zend.usagedata.internal.settings.UploadSettings)
	 */
	public void setSettings(UploadSettings settings) {
		this.settings = settings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.usagedata.internal.recording.uploading.IUploadParameters#setFiles
	 * (java.io.File[])
	 */
	public void setFiles(File[] files) {
		this.files = files;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.usagedata.internal.recording.uploading.IUploadParameters#getSettings
	 * ()
	 */
	public UploadSettings getSettings() {
		return settings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.usagedata.internal.recording.uploading.IUploadParameters#getFiles
	 * ()
	 */
	public File[] getFiles() {
		return files;
	}

}
