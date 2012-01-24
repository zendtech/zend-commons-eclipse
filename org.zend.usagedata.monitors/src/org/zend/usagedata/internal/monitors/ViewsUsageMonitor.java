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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.zend.usagedata.gathering.IUsageDataService;
import org.zend.usagedata.gathering.IUsageMonitor;
import org.zend.usagedata.monitors.Activator;

/**
 * Instances of the {@link ViewsUsageMonitor} class monitor the use of views in
 * a context of certain perspective. More specifically, it is notified whenever
 * a view closed or activated (given focus) and sends some of these events to
 * the UsageDataService.
 * <p>
 * List of perspectives which should be consider during collecting information
 * about views usage is stored in perspectives file.
 * </p>
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class ViewsUsageMonitor implements IUsageMonitor {

	public static final String MONITOR_ID = "org.zend.viewsUsageMonitor"; //$NON-NLS-1$

	private static final String PERSPECTIVES_FILE = "perspectives"; //$NON-NLS-1$
	private static final String ACTIVATED = "activated"; //$NON-NLS-1$
	private static final String VIEW = "view"; //$NON-NLS-1$

	private List<String> perspectives;
	private IUsageDataService usageDataService;

	private IPartListener partListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
			recordEvent(ACTIVATED, part);
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
		}
	};

	private IWindowListener windowListener = new IWindowListener() {
		public void windowOpened(IWorkbenchWindow window) {
			hookListener(window);
		}

		public void windowClosed(IWorkbenchWindow window) {
			unhookListener(window);
		}

		@Override
		public void windowActivated(IWorkbenchWindow window) {
		}

		@Override
		public void windowDeactivated(IWorkbenchWindow window) {
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
		perspectives = getPerspectives();
		if (perspectives != null && perspectives.size() > 0) {
			hookListeners(workbench);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.epp.usagedata.internal.gathering.UsageMonitor#deregister()
	 */
	public void stopMonitoring() {
		if (perspectives != null && perspectives.size() > 0) {
			IWorkbench workbench = PlatformUI.getWorkbench();
			unhookListeners(workbench);
		}
	}

	private void hookListeners(final IWorkbench workbench) {
		workbench.addWindowListener(windowListener);
		for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			hookListener(window);
		}
	}

	private void unhookListeners(final IWorkbench workbench) {
		if (workbench.getDisplay().isDisposed()) {
			return;
		}
		workbench.removeWindowListener(windowListener);
		for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			unhookListener(window);
		}
	}

	private void hookListener(IWorkbenchWindow window) {
		if (window == null) {
			return;
		}
		for (IWorkbenchPage page : window.getPages()) {
			page.addPartListener(partListener);
		}
	}

	private void unhookListener(IWorkbenchWindow window) {
		if (window == null) {
			return;
		}
		for (IWorkbenchPage page : window.getPages()) {
			page.removePartListener(partListener);
		}
	}

	private void recordEvent(String event, IWorkbenchPart part) {
		IWorkbenchPartSite site = part.getSite();
		if (site instanceof IViewSite) {
			IPerspectiveDescriptor perspective = site.getPage()
					.getPerspective();
			if (perspectives.contains(perspective.getId())) {
				usageDataService.recordEvent(MONITOR_ID, event, VIEW, site.getId(),
						perspective.getId());
			}
		}
	}

	private List<String> getPerspectives() {
		List<String> result = new ArrayList<String>();
		InputStream stream = Activator.getDefault()
				.getStream(PERSPECTIVES_FILE);
		if (stream != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					result.add(line.trim());
				}
			} catch (IOException e) {
				Activator.log(e);
				return null;
			}
		}
		return result;
	}

}