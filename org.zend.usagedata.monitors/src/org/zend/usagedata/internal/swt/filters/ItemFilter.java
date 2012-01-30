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

import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TaskItem;
import org.eclipse.swt.widgets.ToolItem;
import org.zend.usagedata.internal.swt.SWTUsageEvent;
import org.zend.usagedata.internal.swt.SWTUsageMonitor;
import org.zend.usagedata.internal.swt.adapters.CoolItemAdapter;
import org.zend.usagedata.internal.swt.adapters.ExpandItemAdapter;
import org.zend.usagedata.internal.swt.adapters.ItemAdapter;
import org.zend.usagedata.internal.swt.adapters.TableTreeItemAdapter;
import org.zend.usagedata.internal.swt.adapters.TaskItemAdapter;
import org.zend.usagedata.internal.swt.adapters.ToolItemAdapter;

/**
 * Event handler for SWT {@link Item} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
@SuppressWarnings("deprecation")
public class ItemFilter extends AbstractFilter {

	public ItemFilter(SWTUsageMonitor monitor, int... types) {
		super(monitor, types);
	}

	@Override
	public void handleEvent(Event event) {
		if (hasType(event.type)) {
			if (event.widget instanceof Item) {
				ItemAdapter adapter = null;
				Item item = (Item) event.widget;
				if (item instanceof CoolItem) {
					adapter = new CoolItemAdapter((CoolItem) item, event.type);
				}
				if (item instanceof ExpandItem) {
					adapter = new ExpandItemAdapter((ExpandItem) item,
							event.type);
				}
				if (item instanceof TableTreeItem) {
					adapter = new TableTreeItemAdapter((TableTreeItem) item,
							event.type);
				}
				if (item instanceof TaskItem) {
					adapter = new TaskItemAdapter((TaskItem) item, event.type);
				}
				if (item instanceof ToolItem) {
					adapter = new ToolItemAdapter((ToolItem) item, event.type);
				}
				if (adapter != null) {
					record(new SWTUsageEvent(event.type, adapter));
				}
			}
		}
	}
}
