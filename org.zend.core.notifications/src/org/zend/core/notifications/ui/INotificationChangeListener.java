/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.ui;

/**
 * Represents listener on notification changes.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface INotificationChangeListener {

	/**
	 * Inform listener about notification change.
	 * 
	 * @param notification
	 */
	void statusChanged(INotification notification);

}
