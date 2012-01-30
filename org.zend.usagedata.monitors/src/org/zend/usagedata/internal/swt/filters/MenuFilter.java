/*******************************************************************************
 * Copyright (c) 2011, 2012 Wojciech Galanciak
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     wojciech.galanciak@gmail.com - initial API and implementation
 *******************************************************************************/
package org.zend.usagedata.internal.swt.filters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;
import org.zend.usagedata.internal.swt.SWTUsageEvent;
import org.zend.usagedata.internal.swt.SWTUsageMonitor;
import org.zend.usagedata.internal.swt.adapters.MenuItemAdapter;
import org.zend.usagedata.internal.swt.adapters.WidgetAdapter;

/**
 * Event handler for SWT {@link Menu} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class MenuFilter extends AbstractFilter {

	public MenuFilter(SWTUsageMonitor monitor, int... types) {
		super(monitor, types);
	}

	@Override
	public void handleEvent(Event event) {
		if (hasType(event.type)) {
			WidgetAdapter adapter = null;
			int type = event.type;
			Widget widget = event.widget;
			if (type == SWT.Selection && widget instanceof MenuItem) {
				adapter = new MenuItemAdapter((MenuItem) event.widget,
						event.type);
			}
			if (adapter != null) {
				record(new SWTUsageEvent(event.type, adapter));
			}

		}
	}
}
