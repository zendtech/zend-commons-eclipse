/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.util;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.graphics.Image;
import org.zend.core.notifications.Activator;

/**
 * Class for caching images
 * 
 * @author Wojciech Galanciak, 2012
 */
public class ImageCache {

	private static HashMap<String, Image> images;

	static {
		images = new HashMap<String, Image>();
	}

	/**
	 * Returns an image for specified path.
	 * 
	 * @param path
	 *            relative path to plug-in root
	 * 
	 * @return {@link Image} or null if image was not found
	 */
	public static Image getImage(String path) {
		Image image = images.get(path);
		if (image == null) {
			image = createImage(path);
			images.put(path, image);
		}
		return image;
	}

	/**
	 * Returns an image for notification close button focused out.
	 * 
	 * @return {@link Image} or null if image was not found
	 */
	public static Image getCloseOut() {
		return getImage("icons/close.gif"); //$NON-NLS-1$
	}

	/**
	 * Returns an image for notification close button focused in.
	 * 
	 * @return {@link Image} or null if image was not found
	 */
	public static Image getCloseIn() {
		return getImage("icons/close_active.gif"); //$NON-NLS-1$
	}

	public static void dispose() {
		Iterator<Image> e = images.values().iterator();
		while (e.hasNext())
			e.next().dispose();

	}

	private static Image createImage(String fileName) {
		Image img = Activator.getImageDescriptor(fileName).createImage();
		return img;
	}

}