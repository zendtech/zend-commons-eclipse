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
import org.eclipse.swt.graphics.Image;
import org.zend.core.notifications.Activator;
import org.zend.core.notifications.internal.ui.Notification;
import org.zend.core.notifications.util.Colors;

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

	public static final int DEFAULT_WIDTH = 230;

	private static final int DEFAULT_ALPHA = 225;
	private static final int DEFAULT_DELAY = -1;

	private static final int DEFAULT_FADE_TIMER = 25;
	private static final int DEFAULT_FADE_IN = 15;
	private static final int DEFAULT_FADE_OUT = 15;

	private String title;
	private String message;
	private boolean isGradient = true;
	private int delay = -1;
	private int alpha = -1;
	private NotificationType type;
	private IBody body;
	private boolean hasBorder;
	private boolean closable;
	private IEclipsePreferences prefs;
	private int height = -1;
	private Image icon;
	private IComparator comparator;

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

	/**
	 * Set notification height if it should be different then default one.
	 * 
	 * @param height
	 * @return settings
	 */
	public NotificationSettings setHeight(int height) {
		this.height = height;
		return this;
	}

	/**
	 * Set notification icon. This icon will be used only if
	 * {@link NotificationSettings#getType()} returns
	 * {@link NotificationType#CUSTOM}.
	 * 
	 * @param icon
	 * @return settings
	 */
	public NotificationSettings setIcon(Image icon) {
		this.icon = icon;
		return this;
	}

	/**
	 * Set notification comparator. It is used in
	 * {@link Notification#equals(Object)} to compare two notifications.
	 * 
	 * @param comparator
	 * @return settings
	 */
	public NotificationSettings setComparator(IComparator comparator) {
		this.comparator = comparator;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * Returns gradient status. Default value is <code>true</code>.
	 * 
	 * @return <code>true</code> if background has gradient; otherwise return
	 *         <code>false</code>
	 */
	public boolean isGradient() {
		return isGradient;
	}

	/**
	 * Returns delay value. If there is no preference value set for this
	 * attribute, default is <code>-1</code> which means that notification is
	 * displayed permanently.
	 * 
	 * @return delay value
	 */
	public int getDelay() {
		return delay > -1 ? delay : prefs.getInt(Activator.PLUGIN_ID
				+ DELAY_KEY, DEFAULT_DELAY);
	}

	/**
	 * Returns alpha value. If there is no preference value set for this
	 * attribute, default is <code>225</code>.
	 * 
	 * @return delay value
	 */
	public int getAlpha() {
		return alpha > -1 ? alpha : prefs.getInt(Activator.PLUGIN_ID
				+ ALPHA_KEY, DEFAULT_ALPHA);
	}

	/**
	 * @return notification type
	 * @see NotificationType
	 */
	public NotificationType getType() {
		return type;
	}

	/**
	 * Returns value which defines period of time between fading cycles.
	 * 
	 * @return fade timer value
	 */
	public int getFadeTimer() {
		return prefs.getInt(Activator.PLUGIN_ID + FADE_TIMER_KEY,
				DEFAULT_FADE_TIMER);
	}

	/**
	 * Returns fading in step value which determines speed of increase of alpha
	 * value for notification's shell.
	 * 
	 * @return fade in step value
	 */
	public int getFadeInStep() {
		return prefs.getInt(Activator.PLUGIN_ID + FADE_IN_KEY, DEFAULT_FADE_IN);
	}

	/**
	 * Returns fading out step value which determines speed of decrease of alpha
	 * value for notification's shell.
	 * 
	 * @return fade out step value
	 */
	public int getFadeOutStep() {
		return prefs.getInt(Activator.PLUGIN_ID + FADE_OUT_KEY,
				DEFAULT_FADE_OUT);
	}

	/**
	 * @return notification height
	 */
	public int getHeight() {
		return height != -1 ? height : prefs.getInt(Activator.PLUGIN_ID
				+ HEIGHT_KEY, -1);
	}

	/**
	 * @return notification width
	 */
	public int getWidth() {
		return prefs.getInt(Activator.PLUGIN_ID + WIDTH_KEY, -1);
	}

	/**
	 * Returns custom body for the notification.
	 * 
	 * @return custom body
	 */
	public IBody getBody() {
		return body;
	}

	/**
	 * Returns border status. Default value is <code>false</code> which means
	 * that border is not displayed.
	 * 
	 * @return <code>true</code> if border is displayed; otherwise return
	 *         <code>false</code>
	 */
	public boolean hasBorder() {
		return hasBorder;
	}

	/**
	 * Returns gradient end color. If {@link NotificationSettings#isGradient()}
	 * returns <code>false</code> this value is used as a notification background
	 * color.
	 * 
	 * @return color which is used as a end of a gradient
	 */
	public Color getGradientTo() {
		return Colors.getColorRegistry().get(
				"org.eclipse.ui.workbench.INACTIVE_TAB_BG_START"); //$NON-NLS-1$
	}

	/**
	 * Returns gradient start color. If
	 * {@link NotificationSettings#isGradient()} returns <code>false</code> this
	 * value is ignored.
	 * 
	 * @return color from which gradient is started.
	 */
	public Color getGradientFrom() {
		return Colors.getColorRegistry()
				.get("org.eclipse.ui.workbench.INACTIVE_TAB_BG_END"); //$NON-NLS-1$
	}

	/**
	 * Returns border color. If {@link NotificationSettings#hasBorder()} returns
	 * <code>false</code> this value is ignored.
	 * 
	 * @return border color
	 */
	public Color getBorderColor() {
		return Colors.getColorRegistry()
				.get("org.eclipse.ui.workbench.INACTIVE_TAB_TEXT_COLOR"); //$NON-NLS-1$
	}

	/**
	 * Returns value which determines that close button is displayed.
	 * 
	 * @return <code>true</code> if close button is displayed; otherwise return
	 *         <code>false</code>
	 */
	public boolean isClosable() {
		return closable;
	}

	/**
	 * Returns notification icon.
	 * 
	 * @return icon
	 */
	public Image getIcon() {
		if (getType() != NotificationType.CUSTOM) {
			return getType().getImage();
		}
		return icon;
	}

	/**
	 * @return comparator associated with notification. If returns
	 *         <code>null</code>, it means that notification is compared using
	 *         its reference.
	 */
	public IComparator getComparator() {
		return comparator;
	}

}
