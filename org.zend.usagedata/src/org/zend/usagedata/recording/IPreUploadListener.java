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
package org.zend.usagedata.recording;

/**
 * Interface for preUploadListeners extension point.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface IPreUploadListener {

	public static final int OK = 0;
	public static final int CANCEL = 1;

	/**
	 * Method called before data upload will be performed. If any of registered
	 * pre-upload listeners returns null, upload will be cancelled. Uploading
	 * will be stopped until all listeners end their execution.
	 */
	void handleUpload();

}
