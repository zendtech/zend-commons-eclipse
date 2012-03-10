/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.util;

/**
 * Set of default fonts available in {@link Fonts} cache.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public enum FontName {
	
	DEFAULT("default"), //$NON-NLS-1$
	
	BOLD("default_bold"), //$NON-NLS-1$
	
	ITALIC("default_italic"), //$NON-NLS-1$

	ITALIC_BOLD("default_italic_bold"); //$NON-NLS-1$

	private final String name;

	private FontName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
