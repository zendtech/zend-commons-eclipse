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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.zend.usagedata.internal.swt.filters.AbstractFilter;

/**
 * {@link IMonitor} implementation.
 * 
 * @author wojciech.galanciak@gmail.com
 */
public class EventMonitor implements IMonitor {

	private Display root;

	private List<EventListener> listeners = new ArrayList<EventListener>();

	public void addListener(EventListener handler) {
		listeners.add(handler);
	}

	public void addFilter(AbstractFilter filter) {
		for (EventListener handler : listeners) {
			handler.addFilter(filter);
		}
	}

	public void registerMonitor(Display display) {
		this.root = display;
		for (EventListener l : listeners) {
			final EventListener listener = l;
			root.syncExec(new Runnable() {

				public void run() {
					root.addFilter(listener.getType(), listener);
				}
			});
		}
	}

	public void unregisterMonitor() {
		if (root == null || root.isDisposed()) {
			return;
		}
		for (EventListener l : listeners) {
			final EventListener listener = l;
			root.syncExec(new Runnable() {

				public void run() {
					root.removeFilter(listener.getType(), listener);
				}
			});
		}
	}

}
