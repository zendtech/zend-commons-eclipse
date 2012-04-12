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

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.zend.usagedata.UsageDataActivator;
import org.zend.usagedata.ui.internal.Messages;
import org.zend.usagedata.ui.internal.UIUsageDataActivator;

/**
 * Upload Wizard which allows to upload collected data to the server.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class UploadWizard extends Wizard {

	private DetailsPage detailsPage;

	public UploadWizard() {
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.UploadDetailsWizard_Title);
		setDefaultPageImageDescriptor(UIUsageDataActivator
				.getImageDescriptor(UIUsageDataActivator.UDC_DIALOG_ICON));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (detailsPage != null) {
			UsageDataActivator.getDefault().getSettings()
					.setAskBeforeUploading(true);
			boolean termsAccepted = detailsPage.isTermsAccepted();
			UsageDataActivator.getDefault().getSettings()
					.setUserAcceptedTermsOfUse(termsAccepted);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		detailsPage = new DetailsPage();
		addPage(detailsPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		getShell().setMinimumSize(550, 300);

	}

}
