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
package org.zend.usagedata.recording;

import org.zend.usagedata.internal.recording.uploading.UploadListener;
import org.zend.usagedata.internal.recording.uploading.UploadResult;

public interface IUploader {

	boolean isUploadInProgress();

	void startUpload();

	void addUploadListener(UploadListener listener);

	void fireUploadComplete(UploadResult result);

	void setUploadParameters(IUploadParameters uploadParameters);

	IUploadParameters getUploadParameters();
	
}
