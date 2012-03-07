/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.internal.ui.progress;

/**
 * Listener responsible for updating notification during progress of a process.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface ITaskListener {

	/**
	 * Set notification message.
	 * 
	 * @param text
	 */
	void taskChanged(String text);

	/**
	 * Set subtask notification message.
	 * 
	 * @param text
	 */
	void subTaskChanged(String text);

	/**
	 * Called when process has been ended.
	 */
	void done();

}
