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

import org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ContentAssistantFacade;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.php.internal.ui.editor.PHPStructuredTextViewer;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.zend.usagedata.monitors.AbstractMonitor;
import org.zend.usagedata.monitors.MonitorUtils;

/**
 * Instances of the {@link FormattingUsageMonitor} class monitor php content
 * assist usage. It collects following information:
 * <ul>
 * <li>selected proposal,</li>
 * <li>usage time (between opening and closing proposal window),</li>
 * <li>replacement string,</li>
 * <li>true if it was triggered by auto activation, false otherwise.</li>
 * </ul>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ContentAssistMonitor extends AbstractMonitor {

	public static final String MONITOR_ID = "org.zend.contentAssistMonitor"; //$NON-NLS-1$

	private IPageListener pageListener = new IPageListener() {

		@Override
		public void pageOpened(IWorkbenchPage page) {
			hookListener(page);
		}

		@Override
		public void pageClosed(IWorkbenchPage page) {
			unhookListener(page);
		}

		@Override
		public void pageActivated(IWorkbenchPage page) {
		}
	};

	private IPartListener partListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
			unhookListener(part);
		}

		public void partOpened(IWorkbenchPart part) {
			hookListener(part);
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

	private ICompletionListener completionListener = new ICompletionListener() {

		private ICompletionProposal proposal;
		private long startTime;
		private long endTime;

		@Override
		public void selectionChanged(ICompletionProposal proposal,
				boolean smartToggle) {
			this.proposal = proposal;
		}

		@Override
		public void assistSessionStarted(ContentAssistEvent event) {
			startTime = System.currentTimeMillis();
		}

		@Override
		public void assistSessionEnded(ContentAssistEvent event) {
			endTime = System.currentTimeMillis();
			if (proposal != null) {
				recordEvent(
						MONITOR_ID,
						MonitorUtils.replaceCommas(proposal.getDisplayString()),
						String.valueOf(endTime - startTime),
						String.valueOf(event.isAutoActivated),
						getReplacementLength());
				proposal = null;
				startTime = endTime = 0;
			}
		}

		private String getReplacementLength() {
			if (proposal instanceof AbstractScriptCompletionProposal) {
				AbstractScriptCompletionProposal p = (AbstractScriptCompletionProposal) proposal;
				return MonitorUtils.replaceCommas(p.getReplacementString()
						.substring(0, p.getReplacementLength()));
			}
			return ""; //$NON-NLS-1$
		}

	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.usagedata.monitors.AbstractMonitor#doStartMonitoring()
	 */
	protected void doStartMonitoring() {
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
		window.addPageListener(pageListener);
		for (IWorkbenchPage page : window.getPages()) {
			hookListener(page);
		}
	}

	private void unhookListener(IWorkbenchWindow window) {
		if (window == null) {
			return;
		}
		window.removePageListener(pageListener);
		for (IWorkbenchPage page : window.getPages()) {
			unhookListener(page);
		}
	}

	private void hookListener(IWorkbenchPage page) {
		page.addPartListener(partListener);
		hookListener(page.getActivePart());
	}

	private void unhookListener(IWorkbenchPage page) {
		page.removePartListener(partListener);
		unhookListener(page.getActivePart());
	}

	private void hookListener(IWorkbenchPart part) {
		if (part != null) {
			IWorkbenchPartSite site = part.getSite();
			if (site instanceof IEditorSite) {
				ContentAssistantFacade facade = getContentAssistantFacade(part);
				if (facade != null) {
					facade.addCompletionListener(completionListener);
				}
			}
		}
	}

	private void unhookListener(IWorkbenchPart part) {
		if (part != null) {
			IWorkbenchPartSite site = part.getSite();
			if (site instanceof IEditorSite) {
				ContentAssistantFacade facade = getContentAssistantFacade(part);
				if (facade != null) {
					facade.removeCompletionListener(completionListener);
				}
			}
		}
	}

	@SuppressWarnings("restriction")
	private ContentAssistantFacade getContentAssistantFacade(IWorkbenchPart part) {
		if (part instanceof PHPStructuredEditor) {
			PHPStructuredEditor editor = (PHPStructuredEditor) part;
			ISourceViewer viewer = editor.getViewer();
			if (viewer instanceof PHPStructuredTextViewer) {
				return ((PHPStructuredTextViewer) viewer)
						.getContentAssistFacade();
			}
		}
		return null;
	}

}