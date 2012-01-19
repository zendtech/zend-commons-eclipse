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

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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

	private static final String TERMS_FILE = "terms.html"; //$NON-NLS-1$

	private Button acceptTermOfUse;

	private boolean termsAccepted;

	protected DetailsPage() {
		super("Upload Details"); //$NON-NLS-1$
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

		createTermsOfUseSection(composite);

		setControl(composite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return acceptTermOfUse.getSelection();
	}

	/**
	 * @return selection for "accept terms of use" checkbox
	 */
	public boolean isTermsAccepted() {
		return termsAccepted;
	}

	private void createTermsOfUseSection(Composite composite) {
		Browser browser = new Browser(composite, SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.heightHint = 300;
		browser.setLayoutData(layoutData);
		browser.setUrl(getTermsOfUseUrl());

		acceptTermOfUse = new Button(composite, SWT.CHECK);
		acceptTermOfUse.setText(Messages.TermsOfUsePage_acceptTerms);
		acceptTermOfUse.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false));
		acceptTermOfUse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				termsAccepted = acceptTermOfUse.getSelection();
				setPageComplete(termsAccepted);
			}
		});
	}

	private String getTermsOfUseUrl() {
		URL terms = FileLocator.find(UIUsageDataActivator.getDefault()
				.getBundle(), new Path(TERMS_FILE), null);
		try {
			return FileLocator.toFileURL(terms).toString();
		} catch (IOException e) {
			UIUsageDataActivator.log(e);
		}
		return null;
	}

}
