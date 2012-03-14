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
package org.zend.usagedata.ui.internal.listener;

import org.zend.core.notifications.NotificationManager;
import org.zend.core.notifications.ui.IBody;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.ui.NotificationType;
import org.zend.usagedata.recording.IPreUploadListener;
import org.zend.usagedata.ui.internal.Messages;
import org.zend.usagedata.ui.internal.notification.UsageNotificationBody;

/**
 * Implementation of {@link IPreUploadListener}. When usage data is collected it
 * provides GUI notification about it and allows user to decide if he/she wants
 * to send collected data or not.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class UIPreUploadListener implements IPreUploadListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.usagedata.recording.IPreUploadListener#handleUpload()
	 */
	public void handleUpload() {
		IBody body = new UsageNotificationBody();
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(Messages.UIPreUploadListener_Title)
				.setType(NotificationType.INFO).setBody(body).setBorder(true);
		NotificationManager.registerNotification(NotificationManager
				.createNotification(settings));
	}


}
