/*******************************************************************************
 * Copyright (c) 2011 Wojciech Galanciak
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     wojciech.galanciak@gmail.com - initial API and implementation
 *******************************************************************************/
package org.zend.usagedata.internal.swt;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Helper class which allows to build a message in the following format:
 * 
 * <code>key1=value1;key2=value2;</code>
 * 
 * @author wojciech.galanciak@gmail.com
 */
public class EventMessage {

	private Map<String, String> map;

	private char separator = ';';

	public EventMessage() {
		this.map = new TreeMap<String, String>();
	}

	public EventMessage(char separator) {
		this();
		this.separator = separator;
	}

	/**
	 * Add new key-value pair to the message.
	 * 
	 * @param key
	 * @param value
	 */
	public void addMessage(String key, String value) {
		map.put(key, value);
	}

	/**
	 * Add new key-value pair to the message.
	 * 
	 * @param key
	 * @param value
	 */
	public void addMessage(String key, int value) {
		addMessage(key, String.valueOf(value));
	}

	/**
	 * Add new key-value pair to the message.
	 * 
	 * @param key
	 * @param value
	 */
	public void addMessage(String key, boolean value) {
		addMessage(key, String.valueOf(value));
	}

	/**
	 * Build and return message string in the following format:
	 * 
	 * <code>key1=value1;key2=value2;</code>
	 * 
	 * @return message
	 */
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		Set<Entry<String, String>> set = map.entrySet();
		for (Entry<String, String> entry : set) {
			builder.append(entry.getKey());
			builder.append('=');
			builder.append(entry.getValue());
			builder.append(separator);
		}
		return builder.toString();
	}

}
