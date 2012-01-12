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
package org.zend.usagedata.gathering;

import org.zend.usagedata.internal.gathering.UsageDataEventListener;

public interface IUsageDataService {

	/**
	 * This method starts the monitoring process. If the service has already been
	 * "started" when this method is called, nothing happens (i.e. multiple calls
	 * to this method are tolerated).
	 */
	void startMonitoring();

	/**
	 * This method stops the monitoring process. If the service is already stopped
	 * when this method is called, nothing happens (i.e. multiple calls
	 * to this method are tolerated).
	 */
	void stopMonitoring();

	boolean isMonitoring();

	/**
	 * This method queues an event containing the given information for
	 * processing.
	 * 
	 * @param what
	 *            what happened? was it an activation, started, clicked, ... ?
	 * @param kind
	 *            what kind of thing caused it? view, editor, bundle, ... ?
	 * @param description
	 *            information about the event. e.g. name of the command, view,
	 *            editor, ...
	 * @param bundleId
	 *            symbolic name of the bundle that owns the thing that caused
	 *            the event.
	 */
	void recordEvent(String what, String kind, String description,
			String bundleId);

	/**
	 * <p>
	 * This method queues an event containing the given information for
	 * processing.
	 * </p>
	 * 
	 * @param what
	 *            what happened? was it an activation, started, clicked, ... ?
	 * @param kind
	 *            what kind of thing caused it? view, editor, bundle, ... ?
	 * @param description
	 *            information about the event. e.g. name of the command, view,
	 *            editor, ...
	 * @param bundleId
	 *            symbolic name of the bundle that owns the thing that caused
	 *            the event.
	 * @param bundleVersion
	 *            the version of the bundle that owns the thing that caused the
	 *            event.
	 */
	void recordEvent(String what, String kind, String description,
			String bundleId, String bundleVersion);

	void addUsageDataEventListener(UsageDataEventListener listener);

	void removeUsageDataEventListener(UsageDataEventListener listener);

}