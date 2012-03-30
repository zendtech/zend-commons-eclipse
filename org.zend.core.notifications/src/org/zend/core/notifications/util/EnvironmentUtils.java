/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.util;

public class EnvironmentUtils {

	enum OS {
		WINDOWS, LINUX, MAC, UNKNOWN;
	}

	public static OS getOsName() {
		OS os = OS.UNKNOWN;
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) { //$NON-NLS-1$ //$NON-NLS-2$
			os = OS.WINDOWS;
		} else if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) { //$NON-NLS-1$ //$NON-NLS-2$
			os = OS.LINUX;
		} else if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) { //$NON-NLS-1$ //$NON-NLS-2$
			os = OS.MAC;
		}

		return os;
	}

	public static boolean isUnderLinux() {
		return EnvironmentUtils.getOsName() == OS.LINUX;
	}

	public static boolean isUnderWindows() {
		return EnvironmentUtils.getOsName() == OS.WINDOWS;
	}

	public static boolean isUnderMacOSX() {
		return EnvironmentUtils.getOsName() == OS.MAC;
	}

}
