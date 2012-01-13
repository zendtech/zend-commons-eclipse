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
package org.zend.usagedata.ui.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Plug-in messages.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.usagedata.ui.internal.messages"; //$NON-NLS-1$

	public static String UIPreUploadListener_Description;
	public static String UIPreUploadListener_Title;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
