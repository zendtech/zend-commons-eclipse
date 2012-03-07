/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.internal.ui.progress;

import org.eclipse.core.runtime.IProgressMonitorWithBlocking;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ProgressIndicator;

/**
 * Responsible for reacting on process progress.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ProgressMonitor implements IProgressMonitorWithBlocking {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private volatile boolean fIsCanceled;
	private ProgressIndicator progressIndicator;
	private ITaskListener taskListener;

	public ProgressMonitor(ProgressIndicator indicator, ITaskListener listener) {
		this.progressIndicator = indicator;
		this.taskListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String,
	 * int)
	 */
	public void beginTask(String name, int totalWork) {
		if (progressIndicator.isDisposed()) {
			return;
		}
		if (name == null) {
			taskListener.taskChanged(EMPTY_STRING);
		} else {
			taskListener.taskChanged(name);
		}
		if (totalWork == UNKNOWN) {
			progressIndicator.beginAnimatedTask();
		} else {
			progressIndicator.beginTask(totalWork);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#done()
	 */
	public void done() {
		if (!progressIndicator.isDisposed()) {
			progressIndicator.sendRemainingWork();
			progressIndicator.done();
			taskListener.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) {
		if (name == null) {
			taskListener.taskChanged(EMPTY_STRING);
		} else {
			taskListener.taskChanged(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
	 */
	public boolean isCanceled() {
		return fIsCanceled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
	 */
	public void setCanceled(boolean b) {
		fIsCanceled = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String name) {
		if (name == null) {
			taskListener.subTaskChanged(EMPTY_STRING);
		} else {
			taskListener.subTaskChanged(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
	 */
	public void worked(int work) {
		internalWorked(work);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
	 */
	public void internalWorked(double work) {
		if (!progressIndicator.isDisposed()) {
			progressIndicator.worked(work);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitorWithBlocking#clearBlocked()
	 */
	public void clearBlocked() {
		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IProgressMonitorWithBlocking#setBlocked(org.
	 * eclipse.core.runtime.IStatus)
	 */
	public void setBlocked(IStatus reason) {
		//
	}

}