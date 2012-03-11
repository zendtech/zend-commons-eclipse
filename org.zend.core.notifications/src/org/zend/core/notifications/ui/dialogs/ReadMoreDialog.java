/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog which allows to display long notifiaction description.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ReadMoreDialog extends Dialog {

	private String message;
	private String title;

	public ReadMoreDialog(Shell parentShell, String title, String message) {
		super(parentShell);
		this.title = title;
		this.message = message;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);

		final ScrolledComposite scrollComposite = new ScrolledComposite(
				(Composite) control, SWT.V_SCROLL | SWT.BORDER);

		final Text text = new Text(scrollComposite, SWT.WRAP | SWT.READ_ONLY);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		text.setText(message);
		scrollComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		scrollComposite.setLayout(new GridLayout(1, true));
		scrollComposite.setContent(text);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = scrollComposite.getClientArea();
				scrollComposite.setMinSize(text.computeSize(r.width,
						SWT.DEFAULT));
			}
		});
		parent.getShell().setText(title);
		return control;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(500, 500);
		Rectangle monitorArea = newShell.getDisplay().getPrimaryMonitor()
				.getBounds();
		Rectangle shellArea = newShell.getBounds();
		int x = monitorArea.x + (monitorArea.width - shellArea.width) / 2;
		int y = monitorArea.y + (monitorArea.height - shellArea.height) / 3;
		newShell.setLocation(x, y);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, OK, "Close", true); //$NON-NLS-1$
	}

}
