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

import org.eclipse.core.runtime.ListenerList;
import org.zend.usagedata.recording.IUploadParameters;
import org.zend.usagedata.recording.IUploader;

public abstract class AbstractUploader implements IUploader {

	private ListenerList uploadListeners = new ListenerList();
	private IUploadParameters uploadParameters;

	public AbstractUploader() {
	}
	
	public void addUploadListener(UploadListener listener) {
		uploadListeners.add(listener);
	}

	public void removeUploadListener(UploadListener listener) {
		uploadListeners.remove(listener);
	}
	
	public void fireUploadComplete(UploadResult result) {
		for (Object listener : uploadListeners.getListeners()) {
			((UploadListener)listener).uploadComplete(result);
		}
	}	
	
	public IUploadParameters getUploadParameters() {
		return uploadParameters;
	}

	public void setUploadParameters(IUploadParameters uploadParameters) {
		this.uploadParameters = uploadParameters;
	}
	
	protected void checkValues() {
		if (uploadParameters == null) throw new RuntimeException("The UploadParameters must be set."); //$NON-NLS-1$
	}
}
