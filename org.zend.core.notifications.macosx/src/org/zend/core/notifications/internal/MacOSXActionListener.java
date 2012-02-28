/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.internal;

import org.eclipse.swt.internal.cocoa.NSApplication;
import org.eclipse.swt.internal.cocoa.NSDockTile;
import org.eclipse.swt.internal.cocoa.NSString;
import org.zend.core.notifications.NotificationManager;
import org.zend.core.notifications.ui.ActionType;
import org.zend.core.notifications.ui.IActionListener;

/**
 * Implementation of {@link IActionListener} for platform specific behavior in a
 * result of hiding or showing notification.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
@SuppressWarnings("restriction")
public class MacOSXActionListener implements IActionListener {

	@Override
	public void performAction(ActionType type) {
		int size = NotificationManager.getNotificationsNumber();
		NSApplication app = NSApplication.sharedApplication();
		NSDockTile dock = app.dockTile();
		String value = size > 0 ? String.valueOf(size) : ""; //$NON-NLS-1$
		dock.setBadgeLabel(NSString.stringWith(value));
	}

}
