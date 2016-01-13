/*******************************************************************************
 * Copyright (c) 2016 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.notifications.popup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.core.NotificationSink;
import org.eclipse.mylyn.commons.notifications.core.NotificationSinkEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class PopupNotificationSink extends NotificationSink {

	public static String ID = "org.zend.php.notifications.sink.popup"; //$NON-NLS-1$
	
	private static final long DELAY_OPEN = 1 * 1000;
	
	private final Set<AbstractNotification> currentlyNotifying = Collections.synchronizedSet(new HashSet<AbstractNotification>());
	
	private final Job openJob = new Job(Messages.PopupNotificationSink_JobName) {

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			try {
				if (Platform.isRunning() && PlatformUI.getWorkbench() != null
						&& PlatformUI.getWorkbench().getDisplay() != null
						&& !PlatformUI.getWorkbench().getDisplay().isDisposed()) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {
							WeakHashMap<Object, Object> cancelledTokens = new WeakHashMap<Object, Object>();
							
							if (popup != null && popup.getReturnCode() == Window.CANCEL) {
								List<AbstractNotification> notifications = popup.getNotifications();
								for (AbstractNotification notification : notifications) {
									if (notification.getToken() != null) {
										cancelledTokens.put(notification.getToken(), null);
									}
								}
							}

							for (Iterator<AbstractNotification> it = currentlyNotifying.iterator(); it.hasNext();) {
								AbstractNotification notification = it.next();
								if (notification.getToken() != null
										&& cancelledTokens.containsKey(notification.getToken())) {
									it.remove();
								}
							}

							synchronized (PopupNotificationSink.class) {
								if (currentlyNotifying.size() > 0) {
									showPopup();
								}
							}
						}
					});
				}
			} finally {
				if (popup != null) {
					schedule(popup.getDelayClose() / 2);
				}
			}
			
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			return Status.OK_STATUS;
		}
		
	};
	
	private NotificationPopup popup;
	
	public PopupNotificationSink() {
		openJob.setSystem(true);
	}

	private void cleanNotified() {
		currentlyNotifying.clear();
	}

	public boolean isAnimationsEnabled() {
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		return store.getBoolean(IWorkbenchPreferenceConstants.ENABLE_ANIMATIONS);
	}

	@Override
	public void notify(NotificationSinkEvent event) {
		currentlyNotifying.addAll(event.getNotifications());

		if (!openJob.cancel()) {
			try {
				openJob.join();
			} catch (InterruptedException e) {
				// ignore
			}
		}
		openJob.schedule(DELAY_OPEN);
	}

	public void showPopup() {
		if (popup != null) {
			popup.close();
		}

		Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		popup = new NotificationPopup(shell);
		popup.setFadingEnabled(isAnimationsEnabled());
		List<AbstractNotification> toDisplay = new ArrayList<AbstractNotification>(currentlyNotifying);
		Collections.sort(toDisplay);
		popup.setNotifications(toDisplay);
		cleanNotified();
		popup.setBlockOnOpen(false);
		popup.open();
	}
}
