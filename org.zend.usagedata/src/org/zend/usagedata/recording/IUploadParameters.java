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

import java.io.File;

import org.zend.usagedata.internal.settings.UploadSettings;

public interface IUploadParameters {

	void setSettings(UploadSettings settings);

	void setFiles(File[] files);

	UploadSettings getSettings();

	File[] getFiles();

}