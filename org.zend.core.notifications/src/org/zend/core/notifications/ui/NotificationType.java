/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.ui;

import org.eclipse.swt.graphics.Image;
import org.zend.core.notifications.util.ImageCache;

/**
 * Represents notification type.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public enum NotificationType {

	ERROR(ImageCache.getImage("icons/error.png")), //$NON-NLS-1$

	WARNING(ImageCache.getImage("icons/warn.png")), //$NON-NLS-1$

	INFO(ImageCache.getImage("icons/info.png")), //$NON-NLS-1$

	CUSTOM(null);

	private Image image;

	private NotificationType(Image img) {
		image = img;
	}

	public Image getImage() {
		return image;
	}

}
