/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.core.notifications.internal.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.zend.core.notifications.Activator;
import org.zend.core.notifications.ui.ActionType;
import org.zend.core.notifications.ui.IActionListener;
import org.zend.core.notifications.ui.IBody;
import org.zend.core.notifications.ui.INotification;
import org.zend.core.notifications.ui.INotificationChangeListener;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.ui.NotificationType;
import org.zend.core.notifications.util.ColorCache;
import org.zend.core.notifications.util.FontCache;
import org.zend.core.notifications.util.ImageCache;

public class Notification implements IActionListener, INotification {

	private Shell shell;
	private Shell parent;
	private Point currentLocation;

	private NotificationSettings settings;
	private List<INotificationChangeListener> listeners;

	public Notification(NotificationSettings settings) {
		this(null, settings);
	}

	public Notification(Shell parent, NotificationSettings settings) {
		this.parent = parent;
		this.settings = settings;
		this.listeners = Collections
				.synchronizedList(new ArrayList<INotificationChangeListener>());
	}

	@Override
	public boolean display() {
		if (parent == null) {
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					parent = Display.getDefault().getActiveShell();
				}
			});
		}
		return doDisplay();
	}

	@Override
	public void performAction(ActionType type) {
		switch (type) {
		case HIDE:
			hide();
			break;
		default:
			return;
		}
	}

	@Override
	public void addChangeListener(INotificationChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void moveUp(int value) {
		Point curLoc = shell.getLocation();
		shell.setLocation(curLoc.x, curLoc.y - value);
		currentLocation = shell.getLocation();
	}

	@Override
	public void moveDown(int value) {
		Point curLoc = shell.getLocation();
		shell.setLocation(curLoc.x, curLoc.y + value);
		currentLocation = shell.getLocation();
	}

	@Override
	public boolean isLast() {
		if (shell != null && !shell.isDisposed()) {
			Monitor monitor = shell.getMonitor();
			if (monitor != null) {
				Point curLoc = shell.getLocation();
				if (curLoc.y - settings.getHeight() < 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isAvailable() {
		return isAvailable(shell);
	}

	@Override
	public int getHeight() {
		return settings.getHeight();
	}

	private void statusChanged() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				for (INotificationChangeListener listener : listeners) {
					listener.statusChanged(Notification.this);
				}
			}
		});
	}

	private boolean doDisplay() {
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				shell = createShell();
				if (isAvailable() && isAvailable(parent)
						&& parent.getMonitor() != null) {
					parent.addListener(SWT.Move, new Listener() {

						@Override
						public void handleEvent(Event event) {
							if (isAvailable()) {
								shell.setLocation(currentLocation);
							}
						}
					});
					shell.addListener(SWT.Resize, new Listener() {

						@Override
						public void handleEvent(Event e) {
							try {
								Rectangle rect = shell.getClientArea();
								Image newImage = new Image(
										Display.getDefault(), Math.max(1,
												rect.width), rect.height);
								GC gc = new GC(newImage);
								gc.setForeground(settings.getGradientFrom());
								gc.setBackground(settings.getGradientTo());
								gc.fillRoundRectangle(rect.x, rect.y,
										rect.width, rect.height, 20, 20);
								ImageData imageData = newImage.getImageData();
								if (settings.isGradient()) {
									gc.fillGradientRectangle(rect.x, rect.y,
											rect.width, rect.height, true);
								}
								if (settings.hasBorder()) {
									gc.setLineWidth(1);
									gc.setForeground(settings.getBorderColor());
									gc.drawRoundRectangle(rect.x, rect.y,
											rect.width - 1, rect.height - 1,
											20, 20);
								}
								gc.dispose();
								Region region = new Region();
								Rectangle pixel = new Rectangle(0, 0, 1, 1);
								for (int y = 0; y < imageData.height; y++) {
									for (int x = 0; x < imageData.width; x++) {
										if (imageData.getPixel(x, y) < 16000000) {
											pixel.x = imageData.x + x;
											pixel.y = imageData.y + y;
											region.add(pixel);
										}
									}
								}
								shell.setRegion(region);
								shell.setBackgroundImage(newImage);
							} catch (Exception exception) {
								Activator.log(exception);
							}
						}
					});
					Composite container = createContainer(shell);
					createImage(container);
					createTitle(container);
					createClose(container);
					createDefaultBody(container);
					initShell();
					show();
					currentLocation = shell.getLocation();
				} else {
					parent = null;
				}
			}
		});
		if (shell == null || parent == null) {
			return false;
		}
		return true;
	}

	private void initShell() {
		Point size = shell.computeSize(settings.getWidth(), SWT.DEFAULT);
		settings.setHeight(size.y);
		shell.setSize(size.x, size.y);
		setLocation();
		shell.setAlpha(0);
		shell.setVisible(true);
	}

	private void setLocation() {
		Point size = shell.getSize();
		Rectangle clientArea = parent.getMonitor().getClientArea();
		int startX = clientArea.x + clientArea.width - size.x - 2;
		int startY = clientArea.y + clientArea.height - size.y - 2;
		shell.setLocation(startX, startY);
	}

	private Shell createShell() {
		if (parent != null) {
			Shell shell = new Shell(parent, SWT.NO_FOCUS | SWT.NO_TRIM);
			shell.setLayout(new FillLayout());
			shell.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_BLACK));
			shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
			shell.setAlpha(255);
			return shell;
		}
		return null;
	}

	private Composite createContainer(Shell shell) {
		final Composite container = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = layout.horizontalSpacing = 0;
		container.setLayout(layout);
		return container;
	}

	private void createDefaultBody(Composite container) {
		IBody customBody = settings.getBody();
		if (customBody != null) {
			Composite body = customBody.createContent(container);
			body.setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
			body.pack(true);
			customBody.addActionListener(this);
		} else {
			Composite composite = new Composite(container, SWT.NONE);
			composite.setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
			GridLayout layout = new GridLayout(1, true);
			layout.horizontalSpacing = layout.verticalSpacing = 2;
			composite.setLayout(layout);
			Label text = new Label(composite, SWT.WRAP);
			Font tf = text.getFont();
			FontData tfd = tf.getFontData()[0];
			tfd.height = 11;
			text.setFont(FontCache.getFont(tfd));
			text.setLayoutData(new GridData(GridData.FILL_BOTH));
			text.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_BLACK));
			text.setText(settings.getMessage());
		}
	}

	private void createTitle(Composite container) {
		CLabel titleLabel = new CLabel(container, SWT.NONE);
		titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));
		titleLabel.setText(settings.getTitle());
		titleLabel.setForeground(ColorCache.getColor(74, 94, 116));
		Font f = titleLabel.getFont();
		FontData fd = f.getFontData()[0];
		fd.setStyle(SWT.BOLD);
		fd.height = 11;
		titleLabel.setFont(FontCache.getFont(fd));
	}

	private void createImage(Composite container) {
		NotificationType type = settings.getType();
		if (type != null) {
			CLabel imgLabel = new CLabel(container, SWT.NONE);
			imgLabel.setLayoutData(new GridData(
					GridData.VERTICAL_ALIGN_BEGINNING
							| GridData.HORIZONTAL_ALIGN_BEGINNING));
			imgLabel.setImage(type.getImage());
		}
	}

	private void createClose(Composite container) {
		if (settings.isClosable()) {
			final CLabel button = new CLabel(container, SWT.NONE);
			button.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING
					| GridData.HORIZONTAL_ALIGN_BEGINNING));
			button.setImage(ImageCache.getCloseImage());
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					button.setImage(ImageCache.getCloseImagePressed());
				}

				@Override
				public void mouseUp(MouseEvent e) {
					button.setImage(ImageCache.getCloseImage());
					hide();
					statusChanged();
				}
			});
		}
	}

	private void show() {
		final Runnable run = new Runnable() {

			@Override
			public void run() {
				try {
					if (isAvailable(shell)) {
						int cur = shell.getAlpha() + settings.getFadeInStep();
						if (cur > settings.getAlpha()) {
							shell.setAlpha(settings.getAlpha());
							if (settings.getDelay() != -1) {
								startTimer();
							}
							return;
						}
						shell.setAlpha(cur);
						Display.getDefault().timerExec(settings.getFadeTimer(),
								this);
					}
				} catch (Exception e) {
					Activator.log(e);
				}
			}
		};
		if (isAvailable(shell)) {
			Display.getDefault().timerExec(settings.getFadeTimer(), run);
		}
	}

	private void hide() {
		final Runnable run = new Runnable() {

			@Override
			public void run() {
				try {
					if (isAvailable(shell)) {
						int cur = shell.getAlpha();
						cur -= settings.getFadeOutStep();
						if (cur <= 0) {
							shell.setAlpha(0);
							shell.dispose();
							statusChanged();
							return;
						}
						shell.setAlpha(cur);
						shell.getDisplay().timerExec(settings.getFadeTimer(),
								this);
					}
				} catch (Exception e) {
					Activator.log(e);
				}
			}
		};
		if (isAvailable(shell)) {
			shell.getDisplay().timerExec(settings.getFadeTimer(), run);
		}
	}

	private void startTimer() {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				try {
					if (shell == null || shell.isDisposed()) {
						return;
					}
					hide();
				} catch (Exception e) {
					Activator.log(e);
				}
			}
		};
		shell.getDisplay().timerExec(settings.getDelay(), run);
	}

	private boolean isAvailable(Shell shell) {
		if (shell == null || shell.isDisposed()) {
			return false;
		}
		return true;
	}

}
