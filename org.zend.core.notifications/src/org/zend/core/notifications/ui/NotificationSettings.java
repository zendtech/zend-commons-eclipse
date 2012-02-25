/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.graphics.Color;
import org.zend.core.notifications.Activator;
import org.zend.core.notifications.internal.util.ColorCache;

/**
 * 
 * Represents notification settings. It allows to customize notification UI and
 * behavior.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class NotificationSettings {

	public static final String WIDTH_KEY = ".width"; //$NON-NLS-1$
	public static final String HEIGHT_KEY = ".height"; //$NON-NLS-1$
	public static final String ALPHA_KEY = ".alpha"; //$NON-NLS-1$
	public static final String DELAY_KEY = ".delay"; //$NON-NLS-1$
	public static final String FADE_TIMER_KEY = ".fade.fimer"; //$NON-NLS-1$
	public static final String FADE_IN_KEY = ".fade.in"; //$NON-NLS-1$
	public static final String FADE_OUT_KEY = ".fade.out"; //$NON-NLS-1$

	private static final int DEFAULT_HEIGHT = 100;
	private static final int DEFAULT_WIDTH = 280;

	private static final int DEFAULT_ALPHA = 225;
	private static final int DEFAULT_DELAY = -1;

	private static final int DEFAULT_FADE_TIMER = 25;
	private static final int DEFAULT_FADE_IN = 15;
	private static final int DEFAULT_FADE_OUT = 15;

	private String title;
	private String message;
	private boolean isGradient;
	private int delay = -1;
	private int alpha = -1;
	private NotificationType type;
	private IBody body;
	private boolean hasBorder;
	private boolean closable;
	private IEclipsePreferences prefs;

	public NotificationSettings() {
		this.prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
	}

	/**
	 * @param title
	 *            notification title
	 * @return settings
	 */
	public NotificationSettings setTitle(String title) {
		this.title = title;
		return this;
	}

	/**
	 * Set notification message. It is used only if there is no custom
	 * {@link IBody} provided.
	 * 
	 * @param title
	 * @return settings
	 */
	public NotificationSettings setMessage(String message) {
		this.message = message;
		return this;
	}

	/**
	 * define if notification background should have a gradient or not.
	 * 
	 * @param gradient
	 * @return settings
	 */
	public NotificationSettings setGradient(boolean value) {
		this.isGradient = value;
		return this;
	}

	/**
	 * Set displaying delay. If not set then notification wait until user
	 * perform any action on it.
	 * 
	 * @param delay
	 * 
	 * @return settings
	 */
	public NotificationSettings setDelay(int delay) {
		this.delay = delay;
		return this;
	}

	/**
	 * Set max value of notification alpha which must be between 0 (transparent)
	 * and 255 (opaque).
	 * 
	 * @param alpha
	 * @return settings
	 */
	public NotificationSettings setAlpha(int alpha) {
		this.alpha = alpha;
		return this;
	}

	/**
	 * Set notification type.
	 * 
	 * @see NotificationType
	 * @param type
	 * @return settings
	 */
	public NotificationSettings setType(NotificationType type) {
		this.type = type;
		return this;
	}

	/**
	 * Set custom notification body which implements {@link IBody}.
	 * 
	 * @param mody
	 * @return settings
	 */
	public NotificationSettings setBody(IBody body) {
		this.body = body;
		return this;
	}

	/**
	 * Set if border should be visible or not.
	 * 
	 * @param value
	 * @return settings
	 */
	public NotificationSettings setBorder(boolean value) {
		this.hasBorder = value;
		return this;
	}

	/**
	 * Set if it should be possible to close notification manually (close
	 * button).
	 * 
	 * @param value
	 * @return settings
	 */
	public NotificationSettings setClosable(boolean closable) {
		this.closable = closable;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public boolean isGradient() {
		return isGradient;
	}

	public int getDelay() {
		return delay > -1 ? delay : prefs.getInt(Activator.PLUGIN_ID
				+ DELAY_KEY, DEFAULT_DELAY);
	}

	public int getAlpha() {
		return alpha > -1 ? alpha : prefs.getInt(Activator.PLUGIN_ID
				+ ALPHA_KEY, DEFAULT_ALPHA);
	}

	public NotificationType getType() {
		return type;
	}

	public int getFadeTimer() {
		return prefs.getInt(Activator.PLUGIN_ID + FADE_TIMER_KEY,
				DEFAULT_FADE_TIMER);
	}

	public int getFadeInStep() {
		return prefs.getInt(Activator.PLUGIN_ID + FADE_IN_KEY, DEFAULT_FADE_IN);
	}

	public int getFadeOutStep() {
		return prefs.getInt(Activator.PLUGIN_ID + FADE_OUT_KEY,
				DEFAULT_FADE_OUT);
	}

	public int getHeight() {
		return prefs.getInt(Activator.PLUGIN_ID + HEIGHT_KEY, DEFAULT_HEIGHT);
	}

	public int getWidth() {
		return prefs.getInt(Activator.PLUGIN_ID + WIDTH_KEY, DEFAULT_WIDTH);
	}

	public IBody getBody() {
		return body;
	}

	public boolean hasBorder() {
		return hasBorder;
	}

	public Color getGradientTo() {
		return ColorCache.getColor(113, 149, 174);
	}

	public Color getGradientFrom() {
		return ColorCache.getColor(154, 194, 224);
	}

	public Color getBorderColor() {
		return ColorCache.getColor(77, 92, 100);
	}

	public boolean isClosable() {
		return closable;
	}

}
