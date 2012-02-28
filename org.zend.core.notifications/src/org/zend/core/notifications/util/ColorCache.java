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
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Class for caching colors.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public final class ColorCache {

	public static final RGB BLACK = new RGB(0, 0, 0);
	public static final RGB WHITE = new RGB(255, 255, 255);

	private static Map<RGB, Color> colorTable;
	private static ColorCache instance;

	static {
		colorTable = new HashMap<RGB, Color>();
		new ColorCache();
	}

	private ColorCache() {
		instance = this;
	}

	public static ColorCache getInstance() {
		return instance;
	}

	/**
	 * Disposes of all colors. DO ONLY CALL THIS WHEN YOU ARE SHUTTING DOWN YOUR
	 * APPLICATION!
	 */
	public static void dispose() {
		Iterator<Color> e = colorTable.values().iterator();
		while (e.hasNext())
			e.next().dispose();

		colorTable.clear();
	}

	public static Color getWhite() {
		return getColorFromRGB(new RGB(255, 255, 255));
	}

	public static Color getBlack() {
		return getColorFromRGB(new RGB(0, 0, 0));
	}

	public static Color getColorFromRGB(RGB rgb) {
		Color color = colorTable.get(rgb);

		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			colorTable.put(rgb, color);
		}

		return color;
	}

	public static Color getColor(int r, int g, int b) {
		RGB rgb = new RGB(r, g, b);
		Color color = colorTable.get(rgb);

		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			colorTable.put(rgb, color);
		}

		return color;
	}

}