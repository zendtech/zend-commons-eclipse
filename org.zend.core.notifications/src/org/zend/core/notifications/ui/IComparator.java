/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.core.notifications.ui;

import org.zend.core.notifications.internal.ui.Notification;

/**
 * Interface which should be implemented by comparator class. Such class can be
 * used to compare two notifications. If two notifications are the same then the
 * old one is hidden and the new is shown on the bottom of notifications stack.
 * This mechanism allows to displaying multiple notifications which context is
 * the same.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface IComparator {

	/**
	 * Compare two {@link IComparator} implementations. This method is called in
	 * {@link Notification#equals(Object)} and it is used to compare two
	 * notifications.
	 * 
	 * @param comparator
	 * @return <code>true</code> if comparators are the same; otherwise return
	 *         <code>false</code>
	 */
	boolean equals(IComparator comparator);

}
