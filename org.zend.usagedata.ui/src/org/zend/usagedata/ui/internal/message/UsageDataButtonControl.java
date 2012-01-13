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
package org.zend.usagedata.ui.internal.message;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.zend.usagedata.ui.internal.UIUsageDataActivator;
import org.zend.usagedata.ui.internal.listener.UIPreUploadListener;

/**
 * Shows the usage data manager icon.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class UsageDataButtonControl extends WorkbenchWindowControlContribution {

	private Composite composite;
	private Label label;

	public UsageDataButtonControl() {
	}

	public UsageDataButtonControl(String id) {
		super(id);
	}

	@Override
	protected Control createControl(Composite parent) {
		Point parentSize = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginBottom = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);

		label = new Label(composite, SWT.FLAT);

		Image image = UIUsageDataActivator.getImageDescriptor(
				UIUsageDataActivator.CALLOUT_MANAGER_ICON).createImage();
		label.setImage(image);
		GridData data = new GridData(GridData.FILL_VERTICAL);
		data.verticalIndent = 0;
		data.horizontalIndent = 0;
		data.widthHint = 20;
		if (parentSize.x != 0) {
			data.heightHint = parentSize.x;
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!composite.isDisposed()) {
					final Point bounds = composite.toDisplay(0, 0);
					UIPreUploadListener.setHintMessageLocation(bounds);
				}
			}
		});

		label.setLayoutData(data);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
		return composite;
	}

}
