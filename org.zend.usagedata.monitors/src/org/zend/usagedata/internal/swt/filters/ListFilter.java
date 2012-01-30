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
package org.zend.usagedata.internal.swt.filters;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.zend.usagedata.internal.swt.adapters.ListAdapter;
import org.zend.usagedata.monitors.AbstractMonitor;

/**
 * Event handler for SWT {@link List} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class ListFilter extends AbstractFilter {

	public ListFilter(AbstractMonitor monitor, int... types) {
		super(monitor, types);
	}

	@Override
	public void handleEvent(Event event) {
		if (hasType(event.type)) {
			if (event.widget instanceof List) {
				ListAdapter adapter = new ListAdapter((List) event.widget,
						event.type);
				record(event.type, adapter.getMessage(),
						adapter.getShellTitle());
			}
		}
	}

}
