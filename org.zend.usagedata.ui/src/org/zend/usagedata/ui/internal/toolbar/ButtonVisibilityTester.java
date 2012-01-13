/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.usagedata.ui.internal.toolbar;

import org.eclipse.core.expressions.PropertyTester;
import org.zend.usagedata.IUsageDataSettings;
import org.zend.usagedata.UsageDataActivator;

/**
 * Test if data usage button should be visible based on
 * {@link IUsageDataSettings#CAPTURE_ENABLED_KEY} preference value.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ButtonVisibilityTester extends PropertyTester {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (UsageDataActivator.getDefault().getSettings().isEnabled()) {
			return true;
		}
		return false;
	}

}
