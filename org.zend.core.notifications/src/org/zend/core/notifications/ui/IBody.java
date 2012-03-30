/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * Represents notification body. It allows to define custom body and communicate
 * with notification to be able to change its state according to user actions.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface IBody {

	/**
	 * Create custom user interface for a notification.
	 * 
	 * @param container
	 * @param notification settings
	 * @return created content
	 */
	Composite createContent(Composite container, NotificationSettings settings);

	/**
	 * Provide custom menu items for a notification.
	 * 
	 * @param menu
	 */
	void addMenuItems(Menu menu);

	/**
	 * Add action listener to be able to communcate with notification and be
	 * able to perform actions on it according to user actions in custom user
	 * interface (e.g. hide notification).
	 * 
	 * @param listener
	 */
	void addActionListener(IActionListener listener);

}
