/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.ui;

/**
 * Represents notification message displayed on a screen in the right bottom
 * corner.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface INotification {

	/**
	 * @return <code>true</code> if notification was displayed successfully;
	 *         otherwise return <code>false</code>
	 */
	boolean display();

	/**
	 * Add notification listener to be able to listen on notification changes.
	 * 
	 * @param listener
	 */
	void addChangeListener(INotificationChangeListener listener);

	/**
	 * @return <code>true</code> if notification is still displayed; otherwise
	 *         return <code>false</code>
	 */
	boolean isAvailable();

	/**
	 * @return <code>true</code> if this is the last notification which fits on
	 *         the screen; otherwise return <code>false</code>
	 */
	boolean isLast();

	/**
	 * Move notification up in notifications stack.
	 */
	void moveUp();

	/**
	 * Move notification down in notifications stack.
	 */
	void moveDown();

}