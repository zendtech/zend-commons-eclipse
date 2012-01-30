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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.zend.usagedata.internal.swt.ComponentType;
import org.zend.usagedata.internal.swt.SWTUsageEvent;
import org.zend.usagedata.internal.swt.SWTUsageMonitor;
import org.zend.usagedata.internal.swt.adapters.ComboAdapter;

/**
 * Event handler for SWT {@link Combo} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class ComboFilter extends AbstractFilter {

	public ComboFilter(SWTUsageMonitor monitor, int... types) {
		super(monitor, types);
	}

	@Override
	public void handleEvent(Event event) {
		if (hasType(event.type)) {
			if (event.widget instanceof Combo) {
				ComboAdapter adapter = new ComboAdapter((Combo) event.widget,
						event.type);
				SWTUsageEvent previous = getLastEvent();
				if (previous != null
						&& previous.getKind() == SWT.Selection
						&& previous.getAdapter().getComponentType() == ComponentType.COMBO) {
					return;
				}

				record(new SWTUsageEvent(event.type, adapter));
			}
		}
	}

}
