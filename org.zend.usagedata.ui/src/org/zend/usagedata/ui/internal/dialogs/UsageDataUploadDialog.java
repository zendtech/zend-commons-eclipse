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
package org.zend.usagedata.ui.internal.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;
import org.zend.usagedata.IUsageDataSettings;
import org.zend.usagedata.UsageDataActivator;
import org.zend.usagedata.ui.internal.Messages;
import org.zend.usagedata.ui.internal.UIUsageDataActivator;

/**
 * Usage data upload dialog which contains details about UDC, additional
 * settings and upload preview.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class UsageDataUploadDialog extends MessageDialog {
	
	private static final String[] labels = new String[] { Messages.UsageDataUploadDialog_Send, Messages.UsageDataUploadDialog_Cancel };

	private Button askBeforeUploadingCheckbox;
	private int previewSectionHeight = 0;

	public UsageDataUploadDialog(Shell parentShell) {
		super(parentShell, Messages.UsageDataUploadDialog_Title, UIUsageDataActivator
				.getImageDescriptor(
						UIUsageDataActivator.INFO_ICON)
				.createImage(), Messages.UsageDataPreferencesPage_Description,
				MessageDialog.INFORMATION,
				labels, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.
	 * swt.widgets.Composite)
	 */
	@Override
	protected Control createCustomArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		composite.setLayout(new GridLayout(2, false));

		askBeforeUploadingCheckbox = new Button(composite, SWT.CHECK
				| SWT.LEFT);
		askBeforeUploadingCheckbox
				.setText(Messages.UsageDataUploadDialog_DoNotShowAgain);
		askBeforeUploadingCheckbox.setLayoutData(new GridData(SWT.FILL,
				SWT.TOP, true, true, 2, 1));

		createPreviewSection(composite);

		return composite;
	}

	protected SharedScrolledComposite getParentScrolledComposite(Control control) {
		Control parent = control.getParent();
		if (parent != null) {
			if (parent instanceof SharedScrolledComposite) {
				return (SharedScrolledComposite) parent;
			} else {
				parent = parent.getParent();
				if (parent instanceof SharedScrolledComposite) {
					return (SharedScrolledComposite) parent;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.MessageDialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == 0) {
			UsageDataActivator
					.getDefault()
					.getPreferenceStore()
					.setValue(IUsageDataSettings.ASK_TO_UPLOAD_KEY,
							!askBeforeUploadingCheckbox.getSelection());
		}
		super.buttonPressed(buttonId);
	}

	private void createPreviewSection(final Composite parent) {
		final ExpandableComposite expComposite = new ExpandableComposite(
				parent, SWT.NONE, ExpandableComposite.TWISTIE
						| ExpandableComposite.CLIENT_INDENT);
		expComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				if (e.getState()) {
					Shell shell = parent.getShell();
					Point shellSize = shell.getSize();
					if (previewSectionHeight == 0) {
						Point groupSize = expComposite.computeSize(SWT.DEFAULT,
								SWT.DEFAULT, true);
						previewSectionHeight = groupSize.y;
					}
					shell.setSize(shellSize.x, shellSize.y
							+ previewSectionHeight);
					parent.layout();
				}
				if (!e.getState()) {
					parent.layout();
					Shell shell = parent.getShell();
					Point shellSize = shell.getSize();
					shell.setSize(shellSize.x, shellSize.y
							- previewSectionHeight);
				}

				SharedScrolledComposite scrolledComposite = getParentScrolledComposite(expComposite
						.getParent());
				if (scrolledComposite != null) {
					scrolledComposite.reflow(true);
				}
			}
		});
		expComposite.setText(Messages.UsageDataUploadDialog_Preview);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		expComposite.setLayoutData(gd);
		Composite advancedSection = new Composite(expComposite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		advancedSection.setLayout(layout);
		expComposite.setClient(advancedSection);
	}

}
