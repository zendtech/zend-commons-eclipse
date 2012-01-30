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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.zend.usagedata.internal.swt.adapters.TreeAdapter;
import org.zend.usagedata.internal.swt.adapters.TreeItemAdapter;
import org.zend.usagedata.internal.swt.adapters.WidgetAdapter;
import org.zend.usagedata.monitors.AbstractMonitor;

/**
 * Event handler for SWT {@link Tree} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class TreeFilter extends AbstractFilter {

	public TreeFilter(AbstractMonitor monitor, int... types) {
		super(monitor, types);
	}

	@Override
	public void handleEvent(Event event) {
		if (hasType(event.type)) {
			if (event.widget instanceof Tree) {
				WidgetAdapter adapter = null;
				if (event.type == SWT.Expand) {
					adapter = new TreeAdapter((Tree) event.widget, event.type);
				}
				if (event.type == SWT.Selection) {
					adapter = new TreeItemAdapter((TreeItem) event.item,
							event.type);
				}
				record(event.type, adapter.getMessage(),
						adapter.getShellTitle());
			}
		}
	}

}
