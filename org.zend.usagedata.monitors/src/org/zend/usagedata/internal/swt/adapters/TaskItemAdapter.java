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
package org.zend.usagedata.internal.swt.adapters;

import org.eclipse.swt.widgets.TaskItem;
import org.zend.usagedata.internal.swt.ComponentType;

/**
 * Represents adapter for SWT {@link TaskItem} component.
 * 
 * @author wojciech.galanciak@gmail.com
 * 
 */
public class TaskItemAdapter extends ItemAdapter {

	private static final String PREFIX = ComponentType.TASK_ITEM.getPrefix();

	public static final String OVERLAY_TEXT = PREFIX + "o"; //$NON-NLS-1$
	public static final String PROGRESS = PREFIX + "p"; //$NON-NLS-1$
	public static final String PROGRESS_STATE = PREFIX + "s"; //$NON-NLS-1$

	private TaskItem taskItem;

	public TaskItemAdapter(TaskItem item, int style) {
		super(item, style);
		this.taskItem = item;
		this.componentType = ComponentType.TASK_ITEM;
	}

	/**
	 * @return overlay text
	 */
	public String getOverlayText() {
		return taskItem != null ? taskItem.getOverlayText() : null;
	}

	/**
	 * @return task item progress
	 */
	public Integer getProgress() {
		return taskItem != null ? taskItem.getProgress() : null;
	}

	/**
	 * @return task item progress state
	 */
	public Integer getProgressState() {
		return taskItem != null ? taskItem.getProgressState() : null;
	}

	@Override
	protected void buildMessage() {
		super.buildMessage();
		message.addMessage(OVERLAY_TEXT, getOverlayText());
		message.addMessage(PROGRESS, getProgress());
		message.addMessage(PROGRESS_STATE, getProgressState());
	}

}
