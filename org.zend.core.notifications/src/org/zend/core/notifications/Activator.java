/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.zend.core.notifications"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private Shell parent;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		parent = getWorkbenchShell(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public Shell getParent() {
		return parent;
	}

	/**
	 * Log an error or exception.
	 * 
	 * @param e
	 *            error or exception
	 */
	public static void log(Throwable e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public Image getImage(String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			getImageRegistry().put(path, getImageDescriptor(path));
			image = getImageRegistry().get(path);
		}
		return image;
	}

	/**
	 * Returns an image for notification close button focused out.
	 * 
	 * @return {@link Image} or null if image was not found
	 */
	public static Image getCloseOut() {
		return Activator.getDefault().getImage("icons/close.gif"); //$NON-NLS-1$
	}

	/**
	 * Returns an image for notification close button focused in.
	 * 
	 * @return {@link Image} or null if image was not found
	 */
	public static Image getCloseIn() {
		return Activator.getDefault().getImage("icons/close_active.gif"); //$NON-NLS-1$
	}

	private Shell getWorkbenchShell(Shell previousShell) {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		if (windows != null && windows.length > 0) {
			for (IWorkbenchWindow window : windows) {
				final Shell shell = window.getShell();
				if (shell != null && !shell.isDisposed()
						&& shell != previousShell) {
					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							shell.addDisposeListener(new DisposeListener() {

								public void widgetDisposed(DisposeEvent e) {
									if (e.widget instanceof Shell) {
										parent = getWorkbenchShell((Shell) e.widget);
									}
								}
							});
						}
					});
					return shell;
				}
			}
		}
		return null;
	}

}
