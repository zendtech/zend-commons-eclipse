/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.internal.ui.progress;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.zend.core.notifications.Activator;
import org.zend.core.notifications.internal.ui.Notification;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.util.FontName;
import org.zend.core.notifications.util.Fonts;

/**
 * Extension of {@link Notification} class which has ability to show progress of
 * a long term process.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ProgressNotification extends Notification {

	private Label message;
	private TaskChangeListener taskChangeListener;
	private IRunnableWithProgress runnable;
	private ProgressIndicator indicator;

	private class TaskChangeListener implements ITaskListener {

		@Override
		public void taskChanged(final String text) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					message.setText(text);
				}
			});
		}

		@Override
		public void subTaskChanged(String text) {
			// TODO add support for subtasks
		}

		@Override
		public void done() {
			hide();
		}

	}

	public ProgressNotification(Shell parent, NotificationSettings settings,
			IRunnableWithProgress runnable) {
		super(parent, settings);
		this.taskChangeListener = new TaskChangeListener();
		this.runnable = runnable;
	}

	public ProgressNotification(NotificationSettings settings,
			IRunnableWithProgress runnable) {
		super(settings);
		this.taskChangeListener = new TaskChangeListener();
		this.runnable = runnable;
	}

	@Override
	protected void createBody(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
				1));
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = layout.verticalSpacing = 2;
		composite.setLayout(layout);
		message = new Label(composite, SWT.WRAP);
		message.setFont(Fonts.get(FontName.DEFAULT));
		message.setLayoutData(new GridData(GridData.FILL_BOTH));
		message.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_BLACK));
		indicator = new ProgressIndicator(composite, SWT.NONE);
		indicator
				.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
	}

	@Override
	protected void startTimer() {
		final ProgressMonitor monitor = new ProgressMonitor(indicator,
				taskChangeListener);
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				monitor.setCanceled(true);
			}
		});
		try {
			ModalContext.run(runnable, true, monitor, parent.getDisplay());
		} catch (InvocationTargetException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
	}

}
