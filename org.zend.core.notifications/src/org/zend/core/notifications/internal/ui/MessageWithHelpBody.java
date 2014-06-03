/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.internal.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.zend.core.notifications.Activator;
import org.zend.core.notifications.ui.IActionListener;
import org.zend.core.notifications.ui.IBody;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.util.FontName;
import org.zend.core.notifications.util.Fonts;

/**
 * {@link IBody} for a notification with link to help. It also contains an
 * optional checkbox to ask user if particular notification should be showed
 * again.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class MessageWithHelpBody implements IBody {

	private String message;
	private String helpContextId;
	private String messageId;
	private boolean doNotShow;

	public MessageWithHelpBody(String message, String helpContextId) {
		super();
		this.message = message;
		this.helpContextId = helpContextId;
	}

	public Composite createContent(Composite container,
			NotificationSettings settings) {
		Composite composite = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = layout.verticalSpacing = 2;
		composite.setLayout(layout);
		Link text = new Link(composite, SWT.WRAP);
		text.setFont(Fonts.get(FontName.DEFAULT));
		text.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true));
		text.setText(message);
		if (helpContextId != null) {
			text.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {
					PlatformUI.getWorkbench().getHelpSystem()
							.displayHelp(helpContextId);
				}
			});
		}
		if (doNotShow) {
			createDoNotShowCheckbox(composite);
		}
		return composite;
	}

	public void doNotShowCheckbox(boolean enabled, String messageId) {
		this.doNotShow = enabled;
		this.messageId = messageId;
	}

	public void addActionListener(IActionListener listener) {
		// do nothing
	}

	public void addMenuItems(Menu menu) {
	}

	private void createDoNotShowCheckbox(Composite parent) {
		final Button doNotShowCheckbox = new Button(parent, SWT.CHECK);
		doNotShowCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IEclipsePreferences prefs = InstanceScope.INSTANCE
						.getNode(Activator.PLUGIN_ID);
				prefs.putBoolean(messageId, !doNotShowCheckbox.getSelection());
			}
		});
		doNotShowCheckbox.setText("Do not display this message again.");
	}

}
