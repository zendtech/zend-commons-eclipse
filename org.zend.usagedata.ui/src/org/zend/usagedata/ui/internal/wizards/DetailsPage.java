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
package org.zend.usagedata.ui.internal.wizards;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.zend.usagedata.recording.IUploader;
import org.zend.usagedata.ui.internal.Messages;
import org.zend.usagedata.ui.internal.UIUsageDataActivator;

/**
 * Upload Wizard page which contains general information about data uploading
 * and collected data preview.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class DetailsPage extends WizardPage {

	private Button askBeforeUploadingCheckbox;

	private boolean askBeforeUploading;
	private int previewSectionHeight = 0;
	private IUploader uploader;

	protected DetailsPage(IUploader uploader) {
		super("Upload Details"); //$NON-NLS-1$
		this.uploader = uploader;
		setDescription(Messages.DetailsPage_Description);
		setTitle(MessageFormat.format(Messages.DetailsPage_Title,
				UIUsageDataActivator.getDefault().getProductName()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Label description = new Label(composite, SWT.WRAP);
		description.setText(MessageFormat.format(
				Messages.DetailsPage_UDCDescription, UIUsageDataActivator
						.getDefault().getProductName()));
		GridDataFactory
				.fillDefaults()
				.align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false)
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH),
						SWT.DEFAULT).applyTo(description);

		askBeforeUploadingCheckbox = new Button(composite, SWT.CHECK | SWT.LEFT);
		askBeforeUploadingCheckbox
				.setText(Messages.UsageDataUploadDialog_DoNotShowAgain);
		askBeforeUploadingCheckbox.setLayoutData(new GridData(SWT.FILL,
				SWT.TOP, true, false));
		askBeforeUploadingCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				askBeforeUploading = askBeforeUploadingCheckbox.getSelection();
			}
		});

		createPreviewSection(composite);

		setControl(composite);
	}

	/**
	 * @return selection for "ask before uploading" checkbox
	 */
	public boolean isAskBeforeUploading() {
		return askBeforeUploading;
	}

	private void createPreviewSection(final Composite parent) {
		final ExpandableComposite expComposite = new ExpandableComposite(
				parent, SWT.NONE, ExpandableComposite.TWISTIE);
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
			}
		});
		expComposite.setText(Messages.UsageDataUploadDialog_Preview);
		expComposite
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite advancedSection = new Composite(expComposite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		advancedSection.setLayout(layout);

		Control uploadPreview = new UploadPreview(
				uploader.getUploadParameters()).createControl(advancedSection);
		uploadPreview
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		expComposite.setClient(advancedSection);
	}

}
