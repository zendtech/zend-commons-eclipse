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

import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.widgets.FormText;
import org.zend.usagedata.internal.swt.SWTUsageEvent;
import org.zend.usagedata.internal.swt.SWTUsageMonitor;
import org.zend.usagedata.internal.swt.adapters.FormTextAdapter;

/**
 * Event handler for SWT {@link FormText} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class FormTextFilter extends AbstractFilter {

	public FormTextFilter(SWTUsageMonitor monitor, int... types) {
		super(monitor, types);
	}

	@Override
	public void handleEvent(Event event) {
		if (hasType(event.type)) {
			if (event.widget instanceof FormText) {
				FormTextAdapter adapter = new FormTextAdapter(
						(FormText) event.widget, event.type);
				record(new SWTUsageEvent(event.type, adapter));
			}
		}
	}

}
