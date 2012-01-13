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

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
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

/**
 * Data collecting preference page.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class UsageDataPreferencesPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private static final int MILLISECONDS_IN_ONE_DAY = 24 * 60 * 60 * 1000;

	private static final long MINIMUM_PERIOD_IN_DAYS = IUsageDataSettings.PERIOD_REASONABLE_MINIMUM
			/ MILLISECONDS_IN_ONE_DAY;
	private static final long MAXIMUM_PERIOD_IN_DAYS = 90;

	private Button captureEnabledCheckbox;
	private Text uploadPeriodText;
	private Label label;
	private Text lastUploadText;
	private Button askBeforeUploadingCheckbox;

	public UsageDataPreferencesPage() {
		setDescription(Messages.UsageDataPreferencesPage_Description);
		setPreferenceStore(UsageDataActivator.getDefault().getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(IUsageDataSettings.CAPTURE_ENABLED_KEY,
				captureEnabledCheckbox.getSelection());
		getPreferenceStore().setValue(IUsageDataSettings.ASK_TO_UPLOAD_KEY,
				askBeforeUploadingCheckbox.getSelection());
		getPreferenceStore().setValue(
				IUsageDataSettings.UPLOAD_PERIOD_KEY,
				Long.valueOf(uploadPeriodText.getText())
						* MILLISECONDS_IN_ONE_DAY);
		return super.performOk();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#isValid()
	 */
	@Override
	public boolean isValid() {
		if (!isValidUploadPeriod(uploadPeriodText.getText()))
			return false;
		return true;
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
		askBeforeUploadingCheckbox.setSelection(getPreferenceStore()
				.getDefaultBoolean(IUsageDataSettings.ASK_TO_UPLOAD_KEY));
		uploadPeriodText.setText(String.valueOf(getPreferenceStore()
				.getDefaultLong(IUsageDataSettings.UPLOAD_PERIOD_KEY)
				/ MILLISECONDS_IN_ONE_DAY));
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

		composite.setLayout(new GridLayout());

		Composite generalArea = createGeneralArea(composite);

		askBeforeUploadingCheckbox = new Button(generalArea, SWT.CHECK
				| SWT.LEFT);
		askBeforeUploadingCheckbox
				.setText(Messages.UsageDataPreferencesPage_AskBeforeUpload);

		createUploadingArea(generalArea);

		initializeValues();

		return composite;
	}

	private Composite createGeneralArea(Composite parent) {
		captureEnabledCheckbox = new Button(parent, SWT.CHECK | SWT.LEFT);
		captureEnabledCheckbox
				.setText(Messages.UsageDataPreferencesPage_EnableCapture);

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		composite.setLayout(new GridLayout());

		captureEnabledCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnabled(captureEnabledCheckbox.getSelection());
			}
		});
		return composite;
	}

	private void createUploadingArea(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.UsageDataPreferencesPage_Uploading);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		group.setLayout(new GridLayout(3, false));

		GridData fieldLayoutData = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		fieldLayoutData.horizontalIndent = FieldDecorationRegistry.getDefault()
				.getMaximumDecorationWidth();

		createUploadPeriodField(group);
		createLastUploadField(group);
	}

	private void createUploadPeriodField(Group composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.UsageDataPreferencesPage_UploadingPeriod);

		uploadPeriodText = new Text(composite, SWT.SINGLE | SWT.BORDER
				| SWT.RIGHT);
		uploadPeriodText.setTextLimit(2);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gridData.horizontalIndent = FieldDecorationRegistry.getDefault()
				.getMaximumDecorationWidth();
		gridData.horizontalSpan = 1;
		GC gc = new GC(uploadPeriodText.getDisplay());
		gc.setFont(uploadPeriodText.getFont());
		gridData.widthHint = gc.stringExtent(String
				.valueOf(MAXIMUM_PERIOD_IN_DAYS)).x;
		gc.dispose();
		uploadPeriodText.setLayoutData(gridData);

		new Label(composite, SWT.NONE)
				.setText(Messages.UsageDataPreferencesPage_Days);

		final ControlDecoration rangeErrorDecoration = new ControlDecoration(
				uploadPeriodText, SWT.LEFT | SWT.TOP);
		rangeErrorDecoration
				.setDescriptionText(MessageFormat
						.format(Messages.UsageDataPreferencesPage_uploadPeriodDescription,
								new Object[] { MINIMUM_PERIOD_IN_DAYS,
										MAXIMUM_PERIOD_IN_DAYS }));
		rangeErrorDecoration.setImage(getErrorImage());
		rangeErrorDecoration.hide();

		uploadPeriodText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String contents = uploadPeriodText.getText();
				if (isValidUploadPeriod(contents))
					rangeErrorDecoration.hide();
				else {
					rangeErrorDecoration.show();
				}
				updateApplyButton();
				getContainer().updateButtons();
			}
		});
		if (System.getProperty(IUsageDataSettings.UPLOAD_PERIOD_KEY) != null) {
			addOverrideWarning(uploadPeriodText);
		}
	}

	private void createLastUploadField(Group composite) {
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.UsageDataPreferencesPage_LastUpload);

		lastUploadText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		lastUploadText.setEnabled(false);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalIndent = FieldDecorationRegistry.getDefault()
				.getMaximumDecorationWidth();
		gridData.horizontalSpan = 2;
		lastUploadText.setLayoutData(gridData);
	}

	private void addOverrideWarning(Control control) {
		FieldDecoration decoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
		ControlDecoration warning = new ControlDecoration(control, SWT.BOTTOM
				| SWT.LEFT);
		warning.setImage(decoration.getImage());
		warning.setDescriptionText(Messages.UsageDataPreferencesPage_OverriddenWarning);
	}

	private Image getErrorImage() {
		return FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
				.getImage();
	}

	private boolean isValidUploadPeriod(String text) {
		try {
			long value = Long.parseLong(text);
			if (value < MINIMUM_PERIOD_IN_DAYS)
				return false;
			if (value > MAXIMUM_PERIOD_IN_DAYS)
				return false;
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
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
		uploadPeriodText.setText(String.valueOf(getPreferenceStore().getLong(
				IUsageDataSettings.UPLOAD_PERIOD_KEY)
				/ MILLISECONDS_IN_ONE_DAY));
		askBeforeUploadingCheckbox.setSelection(getPreferenceStore()
				.getBoolean(IUsageDataSettings.ASK_TO_UPLOAD_KEY));
		updateEnabled(captureEnabledCheckbox.getSelection());

	}

	private void updateEnabled(boolean value) {
		uploadPeriodText.setEnabled(value);
		askBeforeUploadingCheckbox.setEnabled(value);
	}

}
