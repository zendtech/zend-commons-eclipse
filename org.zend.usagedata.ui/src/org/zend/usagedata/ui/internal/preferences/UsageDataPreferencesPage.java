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
package org.zend.usagedata.ui.internal.preferences;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.zend.usagedata.IUsageDataSettings;
import org.zend.usagedata.UsageDataActivator;
import org.zend.usagedata.ui.internal.Messages;
import org.zend.usagedata.ui.internal.UIUsageDataActivator;

/**
 * Data collecting preference page.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class UsageDataPreferencesPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Button captureEnabledCheckbox;
	private Label label;
	private Text lastUploadText;

	public UsageDataPreferencesPage() {
		setDescription(MessageFormat.format(
				Messages.DetailsPage_UDCDescription, UIUsageDataActivator
						.getDefault().getProductName()));
		setPreferenceStore(UsageDataActivator.getDefault().getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		boolean oldValue = getPreferenceStore().getBoolean(
				IUsageDataSettings.CAPTURE_ENABLED_KEY);
		boolean newValue = captureEnabledCheckbox.getSelection();
		if (!oldValue && newValue) {
			getPreferenceStore().setValue(IUsageDataSettings.ASK_TO_UPLOAD_KEY,
					false);
			getPreferenceStore().setValue(
					IUsageDataSettings.USER_ACCEPTED_TERMS_OF_USE_KEY, true);
		}
		getPreferenceStore().setValue(IUsageDataSettings.CAPTURE_ENABLED_KEY,
				newValue);
		return super.performOk();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		captureEnabledCheckbox.setSelection(getPreferenceStore()
				.getDefaultBoolean(IUsageDataSettings.CAPTURE_ENABLED_KEY));
		lastUploadText.setText(getLastUploadDateAsString());
		super.performDefaults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));

		Composite generalArea = createGeneralArea(composite);

		createLastUploadField(generalArea);

		initializeValues();

		return composite;
	}

	private Composite createGeneralArea(Composite parent) {
		captureEnabledCheckbox = new Button(parent, SWT.CHECK | SWT.LEFT);
		captureEnabledCheckbox
				.setText(Messages.UsageDataPreferencesPage_EnableCapture);
		captureEnabledCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false));
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		group.setLayout(new GridLayout(2, false));
		return group;
	}

	private void createLastUploadField(Composite parent) {
		label = new Label(parent, SWT.NONE);
		label.setText(Messages.UsageDataPreferencesPage_LastUpload);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		lastUploadText = new Text(parent, SWT.SINGLE | SWT.BORDER
				| SWT.READ_ONLY);
		// lastUploadText.setEnabled(false);
		lastUploadText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				true));
	}

	private String getLastUploadDateAsString() {
		long time = UsageDataActivator.getDefault().getSettings()
				.getLastUploadTime();
		Date date = new Date(time);
		return date.toString();
	}

	private void initializeValues() {
		captureEnabledCheckbox.setSelection(getPreferenceStore().getBoolean(
				IUsageDataSettings.CAPTURE_ENABLED_KEY));
		lastUploadText.setText(getLastUploadDateAsString());
	}

}
