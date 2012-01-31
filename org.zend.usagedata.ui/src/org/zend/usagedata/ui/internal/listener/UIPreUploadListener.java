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
package org.zend.usagedata.ui.internal.listener;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.zend.usagedata.UsageDataActivator;
import org.zend.usagedata.recording.IPreUploadListener;
import org.zend.usagedata.recording.IUploader;
import org.zend.usagedata.ui.internal.Messages;
import org.zend.usagedata.ui.internal.UIUsageDataActivator;
import org.zend.usagedata.ui.internal.message.CalloutWindow;

/**
 * Implementation of {@link IPreUploadListener}. When usage data is collected it
 * provides GUI notification about it and allows user to decide if he/she wants
 * to send collected data or not.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class UIPreUploadListener implements IPreUploadListener {

	private static Point hintLocation;
	private static CalloutWindow calloutWindow;
	private int status;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.usagedata.recording.IPreUploadListener#handleUpload(org.zend
	 * .usagedata.recording.IUploader)
	 */
	public int handleUpload(final IUploader uploader) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				if (!UsageDataActivator.getDefault().getSettings()
						.shouldAskBeforeUploading()) {
					status = OK;
				} else {
					CalloutWindow callout = createCallout(uploader);
					callout.setBlockOnOpen(true);
					status = callout.open();
					if (callout.isDoNotDisplay()) {
						UsageDataActivator.getDefault().getSettings()
								.setAskBeforeUploading(false);
						if (status == CANCEL) {
							UsageDataActivator.getDefault().getSettings()
									.setEnabled(false);
						}
					}
				}
			}
		});
		return status;
	}

	/**
	 * Set hint location for a message about collected data.
	 * 
	 * @param hint
	 *            position is which message should be displayed
	 */
	public static void setHintMessageLocation(Point hint) {
		hintLocation = hint;
	}

	private CalloutWindow createCallout(IUploader uploader) {
		if (hintLocation == null) {
			throw new IllegalStateException();
		}

		final Display display = Display.getDefault();

		if (calloutWindow != null) {
			calloutWindow.close();
		}
		calloutWindow = new CalloutWindow(display, SWT.TITLE);
		calloutWindow.setLocation(hintLocation);
		calloutWindow.setMargins(10, 10, 10, 10);
		calloutWindow.setText(Messages.UIPreUploadListener_Title);
		calloutWindow.setDescription(MessageFormat.format(
				Messages.UIPreUploadListener_Description, UIUsageDataActivator
						.getDefault().getProductName()));
		calloutWindow.setIsShowMessage(false);

		calloutWindow.setAnchor(SWT.RIGHT | SWT.BOTTOM);

		return calloutWindow;
	}

}
