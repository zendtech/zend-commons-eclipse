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
package org.zend.usagedata.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.zend.usagedata.monitors.Activator;

/**
 * Utility class for monitors.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitorUtils {

	/**
	 * Reads and returns list of values separated by new lines from specified
	 * configuration file.
	 * 
	 * @param file
	 *            - configuration file path (relative to project root)
	 * @return list of values read from specified file
	 */
	public static List<String> getValues(String file) {
		List<String> result = new ArrayList<String>();
		InputStream stream = Activator.getDefault().getStream(file);
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
