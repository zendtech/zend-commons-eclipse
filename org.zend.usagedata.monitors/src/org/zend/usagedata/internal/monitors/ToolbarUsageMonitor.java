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

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener3;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.zend.usagedata.gathering.IUsageDataService;
import org.zend.usagedata.gathering.IUsageMonitor;
import org.zend.usagedata.internal.MonitorUtils;

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

	private static final String PERSPECTIVES_FILE = "config" + File.separator + "toolbar.perspectives"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String EMPTY_ACTION = "EMPTY_ACTION"; //$NON-NLS-1$

	public static final String MONITOR_ID = "org.zend.toolbarUsageMonitor"; //$NON-NLS-1$

	private IUsageDataService usageDataService;
	private List<String> perspectives;

	private SelectionAdapter listener = new SelectionAdapter() {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			if (e.display == null || e.display.isDisposed()) {
				return;
			}
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

	private IWindowListener windowListener = new IWindowListener() {
		public void windowOpened(IWorkbenchWindow window) {
			window.addPerspectiveListener(perspectiveListener);
			hookListener(window);
		}

		public void windowClosed(IWorkbenchWindow window) {
			window.removePerspectiveListener(perspectiveListener);
			unhookListener(window);
		}

		@Override
		public void windowActivated(IWorkbenchWindow window) {
		}

		@Override
		public void windowDeactivated(IWorkbenchWindow window) {
		}

	};

	private IPerspectiveListener3 perspectiveListener = new IPerspectiveListener3() {

		@Override
		public void perspectiveChanged(IWorkbenchPage page,
				IPerspectiveDescriptor perspective, String changeId) {
		}

		@Override
		public void perspectiveActivated(final IWorkbenchPage page,
				IPerspectiveDescriptor perspective) {
			page.getWorkbenchWindow().getShell().getDisplay()
					.asyncExec(new Runnable() {

						@Override
						public void run() {
							hookListener(page.getWorkbenchWindow());
						}
					});
		}

		@Override
		public void perspectiveChanged(IWorkbenchPage page,
				IPerspectiveDescriptor perspective,
				IWorkbenchPartReference partRef, String changeId) {
		}

		@Override
		public void perspectiveSavedAs(IWorkbenchPage page,
				IPerspectiveDescriptor oldPerspective,
				IPerspectiveDescriptor newPerspective) {
		}

		@Override
		public void perspectiveOpened(IWorkbenchPage page,
				IPerspectiveDescriptor perspective) {
		}

		@Override
		public void perspectiveDeactivated(IWorkbenchPage page,
				IPerspectiveDescriptor perspective) {
			unhookListener(page.getWorkbenchWindow());
		}

		@Override
		public void perspectiveClosed(IWorkbenchPage page,
				IPerspectiveDescriptor perspective) {
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
		IWorkbench workbench = PlatformUI.getWorkbench();
		perspectives = MonitorUtils.getValues(PERSPECTIVES_FILE);
		hookListeners(workbench);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.epp.usagedata.internal.gathering.UsageMonitor#deregister()
	 */
	public void stopMonitoring() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		unhookListeners(workbench);
	}

	private void hookListeners(IWorkbench workbench) {
		workbench.addWindowListener(windowListener);
		for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			window.addPerspectiveListener(perspectiveListener);
			if (perspectives.contains(window.getActivePage().getPerspective()
					.getId())) {
				hookListener(window);
			}
		}
	}

	private void unhookListeners(IWorkbench workbench) {
		workbench.removeWindowListener(windowListener);
		for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			window.removePerspectiveListener(perspectiveListener);
			unhookListener(window);
		}
	}

	private void hookListener(IWorkbenchWindow window) {
		if (perspectives.contains(window.getActivePage().getPerspective()
				.getId())) {
			Shell shell = window.getShell();
			CoolBar coolBar = getFirstCoolBar(shell.getChildren());
			addButtonsListener(coolBar);
		}
	}

	private void unhookListener(IWorkbenchWindow window) {
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IPerspectiveDescriptor perspective = page.getPerspective();
				if (perspective != null) {
					if (perspectives.contains(window.getActivePage()
							.getPerspective().getId())) {
						Shell shell = window.getShell();
						CoolBar coolBar = getFirstCoolBar(shell.getChildren());
						removeButtonsListener(coolBar);
					}
				}
			}
		}
	}

	private void addButtonsListener(CoolBar coolBar) {
		CoolItem[] items = coolBar.getItems();
		for (CoolItem coolItem : items) {
			if (coolItem.getControl() instanceof ToolBar) {
				ToolBar bar = (ToolBar) coolItem.getControl();
				ToolItem[] toolItems = bar.getItems();
				for (final ToolItem toolItem : toolItems) {
					if (toolItem.getData() instanceof Separator) {
						continue;
					}
					toolItem.addSelectionListener(listener);
				}
			}
		}
	}

	private void removeButtonsListener(CoolBar coolBar) {
		if (coolBar == null || coolBar.isDisposed()) {
			return;
		}
		CoolItem[] items = coolBar.getItems();
		for (CoolItem coolItem : items) {
			if (coolItem.getControl() instanceof ToolBar) {
				ToolBar bar = (ToolBar) coolItem.getControl();
				ToolItem[] toolItems = bar.getItems();
				for (ToolItem toolItem : toolItems) {
					if (coolBar != null && !coolBar.isDisposed()) {
						if (toolItem.getData() instanceof Separator) {
							continue;
						}
						toolItem.removeSelectionListener(listener);
					}
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
			if (control instanceof Composite) {
				Control[] c = ((Composite) control).getChildren();
				if (c != null) {
					return getFirstCoolBar(c);
				}
			}
		}
		return null;
	}

}