/*******************************************************************************
 * Copyright (c) 2016 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.notifications.core;

import org.eclipse.swt.widgets.Composite;

/**
 * Notification extension which allows notification to provide its own content
 * within notification dialog.
 */
public interface INotificationExtension {

	/**
	 * Provides notification content.
	 * 
	 * <p>
	 * The <code>isSingle</code> parameter indicates whether notification is the
	 * only one notification displayed within the dialog. If so, notification
	 * should not display its icon and label.
	 * </p>
	 * 
	 * @param parent
	 *            - composite parent for notification content
	 * @param isSingle
	 *            - if <code>true</code> the notification is only one displayed
	 *            within the dialog; <code>false</code> otherwise
	 */
	void createContent(Composite parent, boolean isSingle);
}
