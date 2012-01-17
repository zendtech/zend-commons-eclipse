/*******************************************************************************
 * Copyright (c) 2008, 2012 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    The Eclipse Foundation - initial API and implementation
 *******************************************************************************/
package org.zend.usagedata.ui.internal.wizards;

import org.zend.usagedata.gathering.UsageDataEvent;

class UsageDataEventWrapper {

	private final UsageDataEvent event;
	Boolean isIncludedByFilter = null;

	public UsageDataEventWrapper(UsageDataEvent event) {
		this.event = event;
	}

	public String getKind() {
		return event.kind;
	}

	public String getBundleId() {
		return event.bundleId;
	}

	public String getBundleVersion() {
		return event.bundleVersion;
	}

	public long getWhen() {
		return event.when;
	}

	public String getDescription() {
		return event.description;
	}

	public String getWhat() {
		return event.what;
	}

	public synchronized void resetCaches() {
		isIncludedByFilter = null;
	}
}