/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TaskBar;
import org.eclipse.swt.widgets.TaskItem;
import org.zend.core.notifications.internal.ui.MessageWithHelpBody;
import org.zend.core.notifications.internal.ui.Notification;
import org.zend.core.notifications.internal.ui.progress.ProgressNotification;
import org.zend.core.notifications.ui.ActionType;
import org.zend.core.notifications.ui.INotification;
import org.zend.core.notifications.ui.INotificationChangeListener;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.ui.NotificationType;

/**
 * Global notification manager is responsible for managing notification
 * displaying and platform specific behaviors.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class NotificationManager implements INotificationChangeListener {

	private static final String LIMIT_KEY = ".limit"; //$NON-NLS-1$
	private static final int LIMIT_DEFAULT = 5;

	private static NotificationManager manager;

	private List<INotification> queue;
	private List<INotification> active;
	private IEclipsePreferences prefs;

	private NotificationManager() {
		this.queue = Collections
				.synchronizedList(new ArrayList<INotification>());
		this.active = Collections
				.synchronizedList(new ArrayList<INotification>());
		this.prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
	}

	/**
	 * Register new notification in global notification manager. In result it
	 * will be added to waiting queue and be there until it will be possible to
	 * display it. Waiting time is determined by maximal number of notifications
	 * which can be displayed in the same time on the screen. There are two
	 * possible limitations:
	 * <ul>
	 * <li>height of a screen - new notifications are displayed until they can
	 * fit on the screen,</li>
	 * <li>limit is defined - plug-in preference define limit of the number of
	 * notifications which can be displayed at once.</li>
	 * </ul>
	 * 
	 * @param notification
	 */
	public static void registerNotification(final INotification notification) {
		final NotificationManager manager = getInstance();
		manager.queue.add(notification);
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				if (manager.resolve(notification)) {
					notification.addChangeListener(manager);
					if (manager.queue.size() > 0) {
						INotification toAdd = manager.queue.remove(0);
						manager.addActive(toAdd);
					} else {
						manager.queue.remove(notification);
					}
				}
			}
		});
	}

	/**
	 * Register new notification in global notification manager. In result it
	 * will be added to waiting queue and be there until it will be possible to
	 * display it. Waiting time is determined by maximal number of notifications
	 * which can be displayed in the same time on the screen. There are two
	 * possible limitations:
	 * <ul>
	 * <li>height of a screen - new notifications are displayed until they can
	 * fit on the screen,</li>
	 * <li>limit is defined - plug-in preference define limit of the number of
	 * notifications which can be displayed at once.</li>
	 * </ul>
	 * 
	 * @param notification
	 * @param listener
	 *            it should be added if some actions should be performed after
	 *            progress is completed
	 */
	public static void registerNotification(final INotification notification,
			final INotificationChangeListener listener) {
		final NotificationManager manager = getInstance();
		manager.queue.add(notification);
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				if (manager.resolve(notification)) {
					notification.addChangeListener(manager);
					notification.addChangeListener(listener);
					if (manager.queue.size() > 0) {
						INotification toAdd = manager.queue.remove(0);
						manager.addActive(toAdd);
					} else {
						manager.queue.remove(notification);
					}
				}
			}
		});
	}

	/**
	 * Create new {@link INotification} instance with provided settings.
	 * 
	 * @param settings
	 * @return new {@link INotification} instance
	 */
	public static INotification createNotification(NotificationSettings settings) {
		Shell parent = Activator.getDefault().getParent();
		if (parent != null) {
			return new Notification(parent, settings, getInstance());
		}
		return new Notification(settings, getInstance());
	}

	/**
	 * Create new {@link INotification} instance with provided settings as a
	 * child of specified shell.
	 * 
	 * @param shell
	 *            parent shell
	 * @param settings
	 * @return new {@link INotification} instance
	 */
	public static INotification createNotification(Shell parent,
			NotificationSettings settings) {
		return new Notification(parent, settings, getInstance());
	}

	/**
	 * Create and register new {@link INotification} instance using default
	 * configuration with following features:
	 * <ul>
	 * <li>type is {@link NotificationType#INFO},</li>
	 * <li>no close button,</li>
	 * <li>border visible,</li>
	 * <li>title, message and delay are set according to provided arguments as
	 * parameters.</li>
	 * <li>gradient background with default colors.</li>
	 * </ul>
	 * 
	 * It is equivalent to following separate calls:
	 * <ul>
	 * <li>{@link NotificationManager#createNotification(NotificationSettings)}</li>
	 * <li>{@link NotificationManager#registerNotification(INotification)}</li>
	 * </ul>
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param delay
	 *            displaying delay
	 */
	public static void registerInfo(String title, String message, int delay) {
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(title).setDelay(delay).setMessage(message)
				.setType(NotificationType.INFO).setBorder(true)
				.setClosable(true);
		registerNotification(createNotification(settings));
	}

	/**
	 * Create and register new {@link INotification} instance using default
	 * configuration with following features:
	 * <ul>
	 * <li>type is {@link NotificationType#INFO},</li>
	 * <li>no close button,</li>
	 * <li>border visible,</li>
	 * <li>title, message and delat are set according to provided arguments as
	 * parameters.</li>
	 * <li>gradient background with default colors.</li>
	 * </ul>
	 * 
	 * It is equivalent to following separate calls:
	 * <ul>
	 * <li>{@link NotificationManager#createNotification(NotificationSettings)}</li>
	 * <li>{@link NotificationManager#registerNotification(INotification)}</li>
	 * </ul>
	 * 
	 * @param parent
	 *            notification parent shell
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param delay
	 *            displaying delay
	 */
	public static void registerInfo(Shell parent, String title, String message,
			int delay) {
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(title).setDelay(delay).setMessage(message)
				.setClosable(true).setType(NotificationType.INFO)
				.setBorder(true);
		registerNotification(createNotification(parent, settings));
	}

	/**
	 * Create and register new {@link INotification} instance using default
	 * configuration with following features:
	 * <ul>
	 * <li>type is {@link NotificationType#WARNING},</li>
	 * <li>no close button,</li>
	 * <li>border visible,</li>
	 * <li>title, message and delat are set according to provided arguments as
	 * parameters.</li>
	 * <li>gradient background with default colors.</li>
	 * </ul>
	 * 
	 * It is equivalent to following separate calls:
	 * <ul>
	 * <li>{@link NotificationManager#createNotification(NotificationSettings)}</li>
	 * <li>{@link NotificationManager#registerNotification(INotification)}</li>
	 * </ul>
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param delay
	 *            displaying delay
	 */
	public static void registerWarning(String title, String message, int delay) {
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(title).setDelay(delay).setMessage(message)
				.setType(NotificationType.WARNING).setBorder(true)
				.setClosable(true);
		registerNotification(createNotification(settings));
	}

	/**
	 * Create and register new {@link INotification} instance using default
	 * configuration with following features:
	 * <ul>
	 * <li>type is {@link NotificationType#ERROR},</li>
	 * <li>no close button,</li>
	 * <li>border visible,</li>
	 * <li>title, message and delat are set according to provided arguments as
	 * parameters.</li>
	 * <li>gradient background with default colors.</li>
	 * </ul>
	 * 
	 * It is equivalent to following separate calls:
	 * <ul>
	 * <li>{@link NotificationManager#createNotification(NotificationSettings)}</li>
	 * <li>{@link NotificationManager#registerNotification(INotification)}</li>
	 * </ul>
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param delay
	 *            displaying delay
	 */
	public static void registerError(String title, String message, int delay) {
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(title).setDelay(delay).setMessage(message)
				.setType(NotificationType.ERROR).setBorder(true)
				.setClosable(true);
		registerNotification(createNotification(settings));
	}

	/**
	 * Create and register new {@link INotification} instance which support
	 * progress monitoring using default configuration with following features:
	 * <ul>
	 * <li>type is {@link NotificationType#INFO},</li>
	 * <li>close button,</li>
	 * <li>border is visible,</li>
	 * <li>title and message are set according to provided arguments as
	 * parameters.</li>
	 * <li>gradient background with default colors.</li>
	 * </ul>
	 * This notification provide ability to show progress of a process which is
	 * performed. It has fixed height so it is not calculated based on
	 * notification body content.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param runnable
	 *            process which should be run
	 * @param closable
	 *            if <code>true</code> then it will be possible to close
	 *            notification manually
	 */
	public static void registerProgress(String title, String message,
			IRunnableWithProgress runnable, boolean closable) {
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(title).setType(NotificationType.INFO).setBorder(true)
				.setClosable(closable).setMessage(message);
		Shell parent = Activator.getDefault().getParent();
		if (parent != null) {
			registerNotification(new ProgressNotification(parent, settings,
					runnable, getInstance()));
		} else {
			registerNotification(new ProgressNotification(settings, runnable,
					getInstance()));
		}
	}

	/**
	 * Create and register new {@link INotification} instance which support
	 * progress monitoring using default configuration with following features:
	 * <ul>
	 * <li>type is {@link NotificationType#INFO},</li>
	 * <li>close button,</li>
	 * <li>border is visible,</li>
	 * <li>title and message are set according to provided arguments as
	 * parameters.</li>
	 * <li>gradient background with default colors.</li>
	 * </ul>
	 * This notification provide ability to show progress of a process which is
	 * performed. It has fixed height so it is not calculated based on
	 * notification body content.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param runnable
	 *            process which should be run
	 * @param closable
	 *            if <code>true</code> then it will be possible to close
	 *            notification manually
	 * @param listener
	 *            it should be added if some actions should be performed after
	 *            progress is completed
	 */
	public static void registerProgress(String title, String message,
			IRunnableWithProgress runnable, boolean closable,
			INotificationChangeListener listener) {
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(title).setType(NotificationType.INFO).setBorder(true)
				.setClosable(closable).setMessage(message);
		Shell parent = Activator.getDefault().getParent();
		INotification n = null;
		if (parent != null) {
			n = new ProgressNotification(parent, settings, runnable,
					getInstance());
		} else {
			n = new ProgressNotification(settings, runnable, getInstance());
		}
		registerNotification(n, listener);
	}

	/**
	 * Show info notification which contains a message with one link to help.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param helpContextId
	 *            help context id
	 * @param delay
	 *            time in ms after which notification should be hidden
	 */
	public static void showInfoWithHelp(String title, String message,
			String helpContextId, int delay) {
		showMessageWithHelp(title, message, helpContextId, delay,
				NotificationType.INFO, false, null);
	}

	/**
	 * Show info notification which contains a message with one link to help and
	 * "Do not show this message again" checkbox.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param helpContextId
	 *            help context id
	 * @param delay
	 *            time in ms after which notification should be hidden
	 * @param messageId
	 *            id which will be used as a preference key to store
	 *            "do not show again" checkbox selection
	 * 
	 */
	public static void showInfoWithHelp(String title, String message,
			String helpContextId, int delay, String messageId) {
		showMessageWithHelp(title, message, helpContextId, delay,
				NotificationType.INFO, true, messageId);
	}

	/**
	 * Show warning notification which contains a message with one link to help.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param helpContextId
	 *            help context id
	 * @param delay
	 *            time in ms after which notification should be hidden
	 */
	public static void showWarningWithHelp(String title, String message,
			String helpContextId, int delay) {
		showMessageWithHelp(title, message, helpContextId, delay,
				NotificationType.WARNING, false, null);
	}

	/**
	 * Show warning notification which contains a message with one link to help
	 * and "Do not show this message again" checkbox.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param helpContextId
	 *            help context id
	 * @param delay
	 *            time in ms after which notification should be hidden
	 * @param messageId
	 *            id which will be used as a preference key to store
	 *            "do not show again" checkbox selection
	 */
	public static void showWarningWithHelp(String title, String message,
			String helpContextId, int delay, String messageId) {
		showMessageWithHelp(title, message, helpContextId, delay,
				NotificationType.WARNING, true, messageId);
	}

	public void statusChanged(INotification notification) {
		removeActive(notification);
		if (queue.size() == 0) {
			return;
		}
		INotification n = queue.get(queue.size() - 1);
		if (manager.resolve(n)) {
			n.addChangeListener(manager);
			manager.queue.remove(n);
			manager.addActive(n);
		}
	}

	/**
	 * @return number of notifications which are displayed now
	 */
	public static int getNotificationsNumber() {
		return getInstance().active.size();
	}

	/**
	 * Hide all active notifications.
	 */
	public void hideAll() {
		List<INotification> toRemove = new ArrayList<INotification>(active);
		for (INotification notification : toRemove) {
			((Notification) notification).performAction(ActionType.HIDE);
		}
	}

	private static NotificationManager getInstance() {
		if (manager == null) {
			manager = new NotificationManager();
		}
		return manager;
	}

	private boolean resolve(INotification added) {
		synchronized (active) {
			if (!active.isEmpty()) {
				List<INotification> modifiable = new ArrayList<INotification>(
						active);
				Collections.reverse(modifiable);
				int limit = prefs.getInt(LIMIT_KEY, LIMIT_DEFAULT);
				for (INotification notification : modifiable) {
					if (!notification.isAvailable()) {
						removeActive(notification);
					} else if (added.equals(notification)) {
						((Notification) notification)
								.performAction(ActionType.HIDE);
					}
				}
				if (active.size() < limit && !isOverflow(modifiable)) {
					if (!added.display()) {
						return false;
					}
					for (INotification notification : modifiable) {
						if (notification.isAvailable()) {
							notification.moveUp(added.getHeight());
						}
					}
				} else {
					return false;
				}
			} else {
				return added.display();
			}
		}
		return true;
	}

	private void moveDown(int index, INotification removed) {
		synchronized (active) {
			for (int i = 0; i < index; i++) {
				INotification notification = active.get(i);
				if (notification.isAvailable()) {
					notification.moveDown(removed.getHeight());
				}
			}
		}
	}

	private void addActive(INotification notification) {
		active.add(notification);
		updateOverlayText();
	}

	private void removeActive(INotification notification) {
		int index = active.indexOf(notification);
		if (index != 0) {
			moveDown(index, notification);
		}
		active.remove(notification);
		updateOverlayText();
	}

	private boolean isOverflow(List<INotification> list) {
		if (list != null && list.size() > 0) {
			INotification notification = list.get(list.size() - 1);
			if (notification.isLast()) {
				return true;
			}
		}
		return false;
	}

	private void updateOverlayText() {
		TaskBar bar = Display.getDefault().getSystemTaskBar();
		if (bar == null) {
			return;
		}
		TaskItem item = bar.getItem(Activator.getDefault().getParent());
		if (item == null) {
			item = bar.getItem(null);
		}
		if (item != null) {
			int size = NotificationManager.getNotificationsNumber();
			String value = size > 0 ? String.valueOf(size) : ""; //$NON-NLS-1$
			item.setOverlayText(value);
		}
	}

	/**
	 * Show notification of specified type which contains a message with one
	 * link to help.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param helpContextId
	 *            help context id
	 * @param delay
	 *            time in ms after which notification should be hidden
	 * @param type
	 *            notification type
	 * @param doNotShow
	 *            if <code>true</code> then "do not show" checkbox is added to
	 *            the notification
	 * @param messageId
	 *            it is requited if doNotShow parameter is true
	 */
	private static void showMessageWithHelp(String title, String message,
			String helpContextId, int delay, NotificationType type,
			boolean doNotShow, String messageId) {
		MessageWithHelpBody body = new MessageWithHelpBody(message,
				helpContextId);
		if (doNotShow && messageId != null) {
			body.doNotShowCheckbox(true, messageId);
		}
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(title).setType(NotificationType.INFO).setBody(body)
				.setBorder(true).setDelay(delay).setClosable(true)
				.setType(type);
		if (shouldShow(messageId)) {
			NotificationManager.registerNotification(NotificationManager
					.createNotification(settings));
		}
	}

	/**
	 * Check if message should be displayed again.
	 * 
	 * @param messageId
	 * @return <code>true</code> if message should be displayed; otherwise
	 *         return <code>false</code>
	 */
	private static boolean shouldShow(String messageId) {
		if (messageId != null) {
			IEclipsePreferences prefs = InstanceScope.INSTANCE
					.getNode(Activator.PLUGIN_ID);
			return prefs.getBoolean(messageId, false);
		}
		return true;
	}

}
