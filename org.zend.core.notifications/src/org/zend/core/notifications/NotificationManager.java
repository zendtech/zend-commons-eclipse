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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Shell;
import org.zend.core.notifications.internal.ui.Notification;
import org.zend.core.notifications.ui.ActionType;
import org.zend.core.notifications.ui.IActionListener;
import org.zend.core.notifications.ui.INotification;
import org.zend.core.notifications.ui.INotificationChangeListener;
import org.zend.core.notifications.ui.NotificationSettings;

/**
 * Global notification manager is responsible for managing notification
 * displaying and platform specific behaviors.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class NotificationManager implements INotificationChangeListener {

	private static final String EXTENSION = "org.zend.core.notifications.platformSpecific"; //$NON-NLS-1$
	private static final String LIMIT_KEY = ".limit"; //$NON-NLS-1$
	private static final int LIMIT_DEFAULT = 5;

	private static NotificationManager manager;

	private List<INotification> queue;
	private List<INotification> active;
	private List<IActionListener> listeners;
	private IEclipsePreferences prefs;

	private NotificationManager() {
		this.queue = Collections
				.synchronizedList(new ArrayList<INotification>());
		this.active = Collections
				.synchronizedList(new ArrayList<INotification>());
		this.prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		initializePlatfromListeners();
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
	public static void registerNotification(INotification notification) {
		NotificationManager manager = getInstance();
		manager.queue.add(notification);
		if (manager.resolve()) {
			notification.display();
			notification.addChangeListener(manager);
			if (manager.queue.size() > 0) {
				INotification toAdd = manager.queue.remove(0);
				manager.addActive(toAdd);
			}
		}
	}

	/**
	 * Create new {@link INotification} instance with provided settings.
	 * 
	 * @param settings
	 * @return new {@link INotification} instance
	 */
	public static INotification createNotification(NotificationSettings settings) {
		return new Notification(settings);
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
		return new Notification(parent, settings);
	}

	@Override
	public void statusChanged(INotification notification) {
		removeActive(notification);
		if (queue.size() == 0) {
			return;
		}
		INotification n = queue.get(queue.size() - 1);
		if (manager.resolve()) {
			n.display();
			n.addChangeListener(manager);
			manager.queue.remove(n);
			manager.addActive(n);
		}
	}

	/**
	 * @return number of notifications which are displayed now
	 */
	public static int getActiveSize() {
		return getInstance().active.size();
	}

	private static NotificationManager getInstance() {
		if (manager == null) {
			manager = new NotificationManager();
		}
		return manager;
	}

	private boolean resolve() {
		if (!active.isEmpty()) {
			List<INotification> modifiable = new ArrayList<INotification>(
					active);
			Collections.reverse(modifiable);
			int limit = prefs.getInt(LIMIT_KEY, LIMIT_DEFAULT);
			if (active.size() < limit && !isOverflow(modifiable)) {
				for (INotification notification : modifiable) {
					if (notification.isAvailable()) {
						notification.moveUp();
					} else {
						removeActive(notification);
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}

	private void moveDown(int index) {
		for (int i = 0; i < index; i++) {
			INotification notification = active.get(i);
			if (notification.isAvailable()) {
				notification.moveDown();
			}
		}
	}

	private void addActive(INotification notification) {
		active.add(notification);
		actionPerformed(ActionType.SHOW);
	}

	private void removeActive(INotification notification) {
		int index = active.indexOf(notification);
		if (index != 0) {
			moveDown(index);
		}
		active.remove(notification);
		actionPerformed(ActionType.HIDE);
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

	private void actionPerformed(ActionType type) {
		for (IActionListener listener : listeners) {
			listener.performAction(type);
		}
	}

	private void initializePlatfromListeners() {
		listeners = new ArrayList<IActionListener>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION);
		for (IConfigurationElement element : elements) {
			if ("behavior".equals(element.getName())) { //$NON-NLS-1$
				try {
					Object listener = element
							.createExecutableExtension("class"); //$NON-NLS-1$
					if (listener instanceof IActionListener) {
						listeners.add((IActionListener) listener);
					}
				} catch (CoreException e) {
					Activator.log(e);
				}
			}
		}
	}

}
