/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.usagedata.ui.internal.notification;

import java.text.MessageFormat;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.zend.core.notifications.ui.ActionType;
import org.zend.core.notifications.ui.IActionListener;
import org.zend.core.notifications.ui.IBody;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.util.FontName;
import org.zend.core.notifications.util.Fonts;
import org.zend.usagedata.UsageDataActivator;
import org.zend.usagedata.ui.internal.Messages;
import org.zend.usagedata.ui.internal.UIUsageDataActivator;
import org.zend.usagedata.ui.internal.wizards.UploadWizard;

/**
 * Custom notification body for UDC message.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class UsageNotificationBody implements IBody {

	private IActionListener listener;

	public Composite createContent(Composite container,
			NotificationSettings settings) {
		Composite composite = createEntryComposite(container);
		createDescription(composite);
		createOkLink(composite);
		createCancelLink(composite);
		return composite;
	}

	public void addActionListener(IActionListener listener) {
		this.listener = listener;
	}

	private Composite createEntryComposite(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(3, true);
		layout.horizontalSpacing = layout.verticalSpacing = 2;
		composite.setLayout(layout);
		return composite;
	}

	private void createDescription(Composite composite) {
		Link label = new Link(composite, SWT.WRAP);
		label.setFont(Fonts.get(FontName.DEFAULT));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 3, 1));
		label.setText(MessageFormat.format(
				Messages.UIPreUploadListener_Description, UIUsageDataActivator
						.getDefault().getProductName()));
		label.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						showUploadWizard();
					}
				});
			}
		});
	}

	private void createOkLink(Composite composite) {
		Link repeatLink = createLink(composite, Messages.CalloutWindow_Agree, 2);
		repeatLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				UsageDataActivator.getDefault().getSettings()
						.setUserAcceptedTermsOfUse(true);
				UsageDataActivator.getDefault().getSettings()
						.setAskBeforeUploading(false);
				UsageDataActivator.getDefault().getSettings().setEnabled(true);
				listener.performAction(ActionType.HIDE);
			}
		});
	}

	private void createCancelLink(Composite composite) {
		Link repeatLink = createLink(composite,
				Messages.CalloutWindow_DoNotAgree, 1);
		repeatLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				UsageDataActivator.getDefault().getSettings()
						.setUserAcceptedTermsOfUse(false);
				UsageDataActivator.getDefault().getSettings()
						.setAskBeforeUploading(false);
				UsageDataActivator.getDefault().getSettings().setEnabled(false);
				listener.performAction(ActionType.HIDE);
			}
		});
	}

	private Link createLink(Composite parent, String text, int hspan) {
		Link link = new Link(parent, SWT.NO_FOCUS);
		link.setText(text);
		link.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true,
				hspan, 1));
		return link;
	}

	private void showUploadWizard() {
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), new UploadWizard());
		if (dialog.open() == 0) {
			UsageDataActivator.getDefault().getSettings()
					.setUserAcceptedTermsOfUse(true);
			UsageDataActivator.getDefault().getSettings().setEnabled(true);
		} else {
			UsageDataActivator.getDefault().getSettings().setEnabled(false);
		}
		UsageDataActivator.getDefault().getSettings()
				.setAskBeforeUploading(false);
		listener.performAction(ActionType.HIDE);
	}

}
