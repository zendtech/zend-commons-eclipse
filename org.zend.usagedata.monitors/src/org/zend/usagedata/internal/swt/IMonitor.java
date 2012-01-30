/*******************************************************************************
 * Copyright (c) 2011 Wojciech Galanciak
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     wojciech.galanciak@gmail.com - initial API and implementation
 *******************************************************************************/
package org.zend.usagedata.internal.swt;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.zend.usagedata.internal.swt.filters.AbstractFilter;

/**
 * Implementor of this interface is a representation of a monitor which is
 * responsible for managing and collecting SWT {@link Listener} implementations
 * (see {@link EventListener}). Implementors should be registered for a selected
 * root display element and used to handle UI events.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public interface IMonitor {

	/**
	 * Adds filter to all listener which are dedicated to any of filter's SWT
	 * event type.
	 * 
	 * @param filter
	 * @see AbstractFilter
	 */
	void addFilter(AbstractFilter filter);

	/**
	 * Adds event listener which will be registered to listen on a SWT events.
	 * 
	 * @param listener
	 * @see EventListener
	 */
	void addListener(EventListener listener);

	/**
	 * Registers all event listener added by
	 * {@link ISWTMonitor#addListener(EventListener)}.
	 * 
	 * @param display
	 */
	void registerMonitor(Display display);

	/**
	 * Unregister all event listeners addedy by
	 * {@link ISWTMonitor#addListener(EventListener)}.
	 */
	void unregisterMonitor();

}
