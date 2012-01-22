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

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.zend.usagedata.gathering.IUsageDataService;
import org.zend.usagedata.gathering.IUsageMonitor;

/**
 * Instances of the {@link FormattingUsageMonitor} class monitor main window
 * tool bar selection. Each of change is stored with following attributes:
 * <ul>
 * <li>button tooltip,</li>
 * <li>connected action.</li>
 * </ul>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ToolbarUsageMonitor implements IUsageMonitor {

	private static final String EMPTY_ACTION = "EMPTY_ACTION"; //$NON-NLS-1$

	public static final String MONITOR_ID = "org.zend.toolbarUsageMonitor"; //$NON-NLS-1$

	private IUsageDataService usageDataService;
	private CoolBar coolBar;

	private SelectionAdapter listener = new SelectionAdapter() {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			e.display.asyncExec(new Runnable() {

				@Override
				public void run() {
					if (e.getSource() instanceof ToolItem) {
						ToolItem item = (ToolItem) e.getSource();
						Object data = item.getData();
						String actionId = EMPTY_ACTION;
						if (data instanceof ActionContributionItem) {
							ActionContributionItem actionContribution = (ActionContributionItem) data;
							actionId = actionContribution.getId();
						}
						usageDataService.recordEvent(MONITOR_ID,
								item.getToolTipText(), actionId,
								"SWT.Selection"); //$NON-NLS-1$
					}
				}
			});
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.epp.usagedata.internal.gathering.UsageMonitor#register(org
	 * .eclipse.epp.usagedata.internal.gathering.UsageDataService)
	 */
	public void startMonitoring(IUsageDataService usageDataService) {
		this.usageDataService = usageDataService;
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		coolBar = getFirstCoolBar(shell.getChildren());
		addButtonsListener(coolBar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.epp.usagedata.internal.gathering.UsageMonitor#deregister()
	 */
	public void stopMonitoring() {
		removeButtonsListener(coolBar);
	}

	private void addButtonsListener(CoolBar coolBar) {
		CoolItem[] items = coolBar.getItems();
		for (CoolItem coolItem : items) {
			if (coolItem.getControl() instanceof ToolBar) {
				ToolBar bar = (ToolBar) coolItem.getControl();
				ToolItem[] toolItems = bar.getItems();
				for (final ToolItem toolItem : toolItems) {
					toolItem.addSelectionListener(listener);
				}
			}
		}
	}

	private void removeButtonsListener(CoolBar coolBar2) {
		CoolItem[] items = coolBar.getItems();
		for (CoolItem coolItem : items) {
			if (coolItem.getControl() instanceof ToolBar) {
				ToolBar bar = (ToolBar) coolItem.getControl();
				ToolItem[] toolItems = bar.getItems();
				for (ToolItem toolItem : toolItems) {
					toolItem.removeSelectionListener(listener);
				}
			}
		}
	}

	private CoolBar getFirstCoolBar(Control[] children) {
		for (Control control : children) {
			if (control instanceof CoolBar) {
				return (CoolBar) control;
			}
		}
		for (Control control : children) {
			if (control instanceof Composite)
				return (getFirstCoolBar(((Composite) control).getChildren()));
		}
		return null;
	}

}