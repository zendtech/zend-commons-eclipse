/*******************************************************************************
 * Copyright (c) 2016 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.notifications.util;

import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.ui.NotificationsUi;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationAction;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationHandler;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationsPlugin;
import org.zend.php.notifications.popup.PopupNotificationSink;

@SuppressWarnings("restriction")
public class NotificationsHelper {

	public static void notify(AbstractNotification... notifications) {
		List<AbstractNotification> notificationsList = Arrays.asList(notifications);
		NotificationsUi.getService().notify(notificationsList);
	}
	
	public static NotificationAction getNotificationAction(AbstractNotification notification) {
		String id = notification.getEventId();
		return getNotificationAction(id);
	}
	
	public static NotificationAction getNotificationAction(String eventId) {
		NotificationHandler handler = NotificationsPlugin.getDefault().getModel().getNotificationHandler(eventId);
		if (handler == null)
			return null;
		
		List<NotificationAction> actions = handler.getActions();
		for (NotificationAction action : actions) {
			if(!action.getSinkDescriptor().getId().equalsIgnoreCase(PopupNotificationSink.ID))
				continue;
			
			return action;
		}
		return null;
	}
}
