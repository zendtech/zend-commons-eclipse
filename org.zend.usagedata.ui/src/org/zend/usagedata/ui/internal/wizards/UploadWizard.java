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
import org.zend.usagedata.recording.IUploader;
import org.zend.usagedata.ui.internal.Messages;

/**
 * Upload Wizard which allows to upload collected data to the server.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class UploadWizard extends Wizard {

	private DetailsPage detailsPage;
	private TermsOfUsePage termsOfUsePage;

	private IUploader uploader;

	public UploadWizard(IUploader uploader) {
		this.uploader = uploader;
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.UploadDetailsWizard_Title);
		// TODO add setDefaultPageImageDescriptor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (termsOfUsePage != null) {
			boolean termsAccepted = termsOfUsePage.isTermsAccepted();
			UsageDataActivator.getDefault().getSettings()
					.setUserAcceptedTermsOfUse(termsAccepted);
		}
		if (detailsPage != null) {
			boolean askBeforeUpload = detailsPage.isAskBeforeUploading();
			UsageDataActivator.getDefault().getSettings()
					.setAskBeforeUploading(!askBeforeUpload);
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
		detailsPage = new DetailsPage(uploader);
		addPage(detailsPage);
		boolean termsAccepted = UsageDataActivator.getDefault().getSettings()
				.hasUserAcceptedTermsOfUse();
		if (!termsAccepted) {
			termsOfUsePage = new TermsOfUsePage();
			addPage(termsOfUsePage);
		}
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
