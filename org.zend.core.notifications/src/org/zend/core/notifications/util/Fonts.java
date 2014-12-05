/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.util;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;

/**
 * Set of default fonts available in {@link Fonts} cache.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public enum Fonts {

	DEFAULT(JFaceResources.getFontRegistry().defaultFont()),

	BOLD(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT)),

	ITALIC(JFaceResources.getFontRegistry().getItalic(
			JFaceResources.DEFAULT_FONT));

	private Font font;

	private Fonts(Font font) {
		this.font = font;
	}

	public Font getFont() {
		return font;
	}

}
