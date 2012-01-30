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
package org.zend.usagedata.internal.monitors;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.zend.usagedata.internal.swt.EventListener;
import org.zend.usagedata.internal.swt.EventMonitor;
import org.zend.usagedata.internal.swt.IMonitor;
import org.zend.usagedata.internal.swt.filters.ButtonFilter;
import org.zend.usagedata.internal.swt.filters.ComboFilter;
import org.zend.usagedata.internal.swt.filters.FormTextFilter;
import org.zend.usagedata.internal.swt.filters.ItemFilter;
import org.zend.usagedata.internal.swt.filters.LinkFilter;
import org.zend.usagedata.internal.swt.filters.ListFilter;
import org.zend.usagedata.internal.swt.filters.MenuFilter;
import org.zend.usagedata.internal.swt.filters.TableFilter;
import org.zend.usagedata.internal.swt.filters.TextFilter;
import org.zend.usagedata.internal.swt.filters.TreeFilter;
import org.zend.usagedata.monitors.AbstractMonitor;
import org.zend.usagedata.monitors.MonitorUtils;

/**
 * Instances of the {@link WizardUsageMonitor} class monitor the use of wizards.
 * List of wizards which should be monitored is provided in configuration file.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class WizardUsageMonitor extends AbstractMonitor {

	public static final String MONITOR_ID = "org.zend.wizardUsageMonitor"; //$NON-NLS-1$

	private static final String WIZARDS_FILE = "config" + File.separator + "wizards.titles"; //$NON-NLS-1$ //$NON-NLS-2$

	private IMonitor monitor;
	private List<String> titles;

	private IWindowListener windowListener = new IWindowListener() {
		public void windowOpened(IWorkbenchWindow window) {
		}

		public void windowClosed(IWorkbenchWindow window) {
		}

		@Override
		public void windowActivated(IWorkbenchWindow window) {
			monitor.unregisterMonitor();
		}

		@Override
		public void windowDeactivated(IWorkbenchWindow window) {
			monitor.registerMonitor(window.getShell().getDisplay());
		}

	};

	public WizardUsageMonitor() {
		this.monitor = new EventMonitor();
		initMonitor();
	}

	@Override
	public void recordEvent(String monitorId, String kind, String description,
			String shellTitle) {
		if (titles.contains(shellTitle)) {
			super.recordEvent(MONITOR_ID, kind, description, shellTitle);
		}	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.usagedata.monitors.AbstractMonitor#doStartMonitoring()
	 */
	protected void doStartMonitoring() {
		titles = MonitorUtils.getValues(WIZARDS_FILE);
		IWorkbench workbench = PlatformUI.getWorkbench();
		hookListeners(workbench);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.usagedata.monitors.AbstractMonitor#doStopMonitoring()
	 */
	protected void doStopMonitoring() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		unhookListeners(workbench);
	}

	private void hookListeners(IWorkbench workbench) {
		workbench.addWindowListener(windowListener);
	}

	private void unhookListeners(IWorkbench workbench) {
		workbench.removeWindowListener(windowListener);
	}

	private void initMonitor() {
		monitor.addListener(new EventListener(SWT.Expand));
		monitor.addListener(new EventListener(SWT.Show));
		monitor.addListener(new EventListener(SWT.FocusOut));
		monitor.addListener(new EventListener(SWT.Selection));
		monitor.addFilter(new ComboFilter(this, SWT.Selection, SWT.FocusOut));
		monitor.addFilter(new LinkFilter(this, SWT.Selection));
		monitor.addFilter(new ListFilter(this, SWT.Selection));
		monitor.addFilter(new ItemFilter(this, SWT.Selection));
		monitor.addFilter(new TableFilter(this, SWT.Selection));
		monitor.addFilter(new TextFilter(this, SWT.Selection, SWT.FocusOut));
		monitor.addFilter(new TreeFilter(this, SWT.Selection, SWT.Expand));
		monitor.addFilter(new MenuFilter(this, SWT.Selection, SWT.Show));
		monitor.addFilter(new FormTextFilter(this, SWT.Selection));
		monitor.addFilter(new ButtonFilter(this, SWT.Selection));
	}

}