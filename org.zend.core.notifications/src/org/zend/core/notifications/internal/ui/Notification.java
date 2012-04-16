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
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.zend.core.notifications.Activator;
import org.zend.core.notifications.NotificationManager;
import org.zend.core.notifications.ui.ActionType;
import org.zend.core.notifications.ui.IActionListener;
import org.zend.core.notifications.ui.IBody;
import org.zend.core.notifications.ui.IComparator;
import org.zend.core.notifications.ui.INotification;
import org.zend.core.notifications.ui.INotificationChangeListener;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.util.ColorCache;
import org.zend.core.notifications.util.EnvironmentUtils;
import org.zend.core.notifications.util.FontName;
import org.zend.core.notifications.util.Fonts;
import org.zend.core.notifications.util.ImageCache;

public class Notification implements IActionListener, INotification {

	private static final int DEFAULT_WIDTH = 230;

	protected Shell shell;
	protected Shell parent;
	protected Point currentLocation;

	protected NotificationSettings settings;
	protected List<INotificationChangeListener> listeners;

	private NotificationManager manager;

	public Notification(NotificationSettings settings,
			NotificationManager manager) {
		this(null, settings, manager);
	}

	public Notification(Shell parent, NotificationSettings settings,
			NotificationManager manager) {
		this.parent = parent;
		this.settings = settings;
		this.manager = manager;
		this.listeners = Collections
				.synchronizedList(new ArrayList<INotificationChangeListener>());
	}

	public boolean display() {
		if (parent == null) {
			Display.getDefault().syncExec(new Runnable() {

				public void run() {
					parent = Display.getDefault().getActiveShell();
				}
			});
		}
		return doDisplay();
	}

	public void performAction(ActionType type) {
		switch (type) {
		case HIDE:
			hide();
			break;
		default:
			return;
		}
	}

	public void addChangeListener(INotificationChangeListener listener) {
		listeners.add(listener);
	}

	public void moveUp(int value) {
		Point curLoc = shell.getLocation();
		shell.setLocation(curLoc.x, curLoc.y - value);
		currentLocation = shell.getLocation();
	}

	public void moveDown(int value) {
		Point curLoc = shell.getLocation();
		shell.setLocation(curLoc.x, curLoc.y + value);
		currentLocation = shell.getLocation();
	}

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

	public boolean isAvailable() {
		return isAvailable(shell);
	}

	public int getHeight() {
		return settings.getHeight();
	}

	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (this.settings != null && obj instanceof Notification) {
			Notification n = (Notification) obj;
			IComparator c = this.settings.getComparator();
			return c != null && c.equals((n.settings.getComparator()));
		}
		return false;
	}

	protected void statusChanged() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				for (INotificationChangeListener listener : listeners) {
					listener.statusChanged(Notification.this);
				}
			}
		});
	}

	protected boolean doDisplay() {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				shell = createShell();
				if (isAvailable() && isAvailable(parent)
						&& parent.getMonitor() != null) {
					parent.addListener(SWT.Move, new Listener() {

						public void handleEvent(Event event) {
							if (isAvailable()) {
								shell.setLocation(currentLocation);
							}
						}
					});
					shell.addListener(SWT.Resize, new Listener() {

						public void handleEvent(Event e) {
							try {
								Rectangle rect = shell.getClientArea();
								Image newImage = new Image(
										Display.getDefault(), Math.max(1,
												rect.width), rect.height);
								GC gc = new GC(newImage);
								gc.setForeground(settings.getGradientFrom());
								gc.setBackground(settings.getGradientTo());
								if (settings.isGradient()) {
									gc.fillGradientRectangle(rect.x, rect.y,
											rect.width, rect.height, true);
								} else {
									gc.fillRectangle(rect.x, rect.y,
											rect.width, rect.height);
								}
								if (settings.hasBorder()) {
									gc.setLineWidth(1);
									gc.setForeground(settings.getBorderColor());
									gc.drawRoundRectangle(rect.x, rect.y,
											rect.width - 1, rect.height - 1,
											20, 20);
								}
								gc.dispose();
								shell.setRegion(createRoundedCorners());
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
					createBody(container);
					createMenu();
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

	protected void initShell() {
		int width = Math.max(settings.getWidth(), DEFAULT_WIDTH);
		Point size = shell.computeSize(width, SWT.DEFAULT);
		settings.setHeight(Math.max(size.y, settings.getHeight()));
		shell.setSize(width, settings.getHeight());
		setLocation();
		if (EnvironmentUtils.isUnderWindows()
				|| EnvironmentUtils.isUnderMacOSX()) {
			shell.setAlpha(0);
		} else {
			shell.setAlpha(255);
		}
		shell.setVisible(true);
	}

	protected void setLocation() {
		Point size = shell.getSize();
		Rectangle clientArea = parent.getMonitor().getClientArea();
		int startX = clientArea.x + clientArea.width - size.x - 2;
		int startY = clientArea.y + clientArea.height - size.y - 2;
		shell.setLocation(startX, startY);
	}

	protected Shell createShell() {
		if (parent != null) {
			Shell shell = new Shell(parent, SWT.NO_TRIM | SWT.NO_FOCUS
					| SWT.TOOL);
			shell.setLayout(new FillLayout());
			shell.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_BLACK));
			shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
			shell.setAlpha(255);
			return shell;
		}
		return null;
	}

	protected Composite createContainer(Shell shell) {
		final Composite container = new Composite(shell, SWT.NO_FOCUS);
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = layout.horizontalSpacing = 0;
		container.setLayout(layout);
		return container;
	}

	protected void createBody(Composite container) {
		IBody customBody = settings.getBody();
		Composite body = null;
		if (customBody != null) {
			body = customBody.createContent(container, settings);
			body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
					1));
			body.pack(true);
			customBody.addActionListener(this);
		} else {
			body = new Composite(container, SWT.NONE);
			body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
					1));
			GridLayout layout = new GridLayout(1, true);
			layout.horizontalSpacing = layout.verticalSpacing = 2;
			body.setLayout(layout);
			Label text = new Label(body, SWT.WRAP);
			text.setFont(Fonts.get(FontName.DEFAULT));
			text.setLayoutData(new GridData(GridData.FILL_BOTH));
			text.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_BLACK));
			String message = settings.getMessage();
			if (message != null) {
				text.setText(settings.getMessage());
			}
		}
	}

	protected void createMenu() {
		Menu menu = new Menu(shell);
		MenuItem closeItem = new MenuItem(menu, SWT.PUSH);
		closeItem.setText(Messages.Notification_ContextMenuClose);
		closeItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				hide();
			}
		});
		MenuItem closeAllItem = new MenuItem(menu, SWT.PUSH);
		closeAllItem.setText(Messages.Notification_ContextMenuCloseAll);
		closeAllItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				manager.hideAll();
			}
		});
		IBody customBody = settings.getBody();
		if (customBody != null) {
			customBody.addMenuItems(menu);
		}
		setMenu(shell, menu);
	}

	protected void setMenu(Control parent, Menu menu) {
		if (parent != null) {
			if (parent instanceof Composite) {
				Control[] children = ((Composite) parent).getChildren();
				for (Control control : children) {
					setMenu(control, menu);
				}
			}
			parent.setMenu(menu);
		}
	}

	protected void createTitle(Composite container) {
		CLabel titleLabel = new CLabel(container, SWT.NONE);
		titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));
		titleLabel.setText(settings.getTitle());
		titleLabel.setForeground(ColorCache.getColor(74, 94, 116));
		titleLabel.setFont(Fonts.get(FontName.BOLD));
	}

	protected void createImage(Composite container) {
		Image icon = settings.getIcon();
		if (icon != null) {
			CLabel imgLabel = new CLabel(container, SWT.NONE);
			imgLabel.setLayoutData(new GridData(
					GridData.VERTICAL_ALIGN_BEGINNING
							| GridData.HORIZONTAL_ALIGN_BEGINNING));
			imgLabel.setImage(icon);
		}
	}

	protected void createClose(Composite container) {
		if (settings.isClosable()) {
			final CLabel button = new CLabel(container, SWT.NONE);
			button.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING
					| GridData.HORIZONTAL_ALIGN_BEGINNING));
			button.setImage(ImageCache.getCloseOut());
			button.addMouseTrackListener(new MouseTrackAdapter() {
				@Override
				public void mouseEnter(MouseEvent e) {
					button.setImage(ImageCache.getCloseIn());
				}

				@Override
				public void mouseExit(MouseEvent e) {
					button.setImage(ImageCache.getCloseOut());
				}
			});
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					button.setImage(ImageCache.getCloseOut());
					hide();
					statusChanged();
				}
			});
		}
	}

	protected void show() {
		final Runnable run = new Runnable() {

			public void run() {
				try {
					if (isAvailable(shell)) {
						int cur = shell.getAlpha() + settings.getFadeInStep();
						if (cur > settings.getAlpha()) {
							shell.setAlpha(settings.getAlpha());
							startTimer();
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

	protected void hide() {
		final Runnable run = new Runnable() {

			public void run() {
				try {
					if (isAvailable(shell)) {
						int cur = shell.getAlpha();
						int next = cur - settings.getFadeOutStep();
						if (next <= 0) {
							shell.setAlpha(0);
							shell.dispose();
							statusChanged();
							return;
						}
						shell.setAlpha(next);
						if (shell.getAlpha() == cur) {
							shell.setAlpha(0);
							shell.dispose();
							statusChanged();
							return;
						}
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

	protected void startTimer() {
		if (settings.getDelay() != -1) {
			Runnable run = new Runnable() {

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
	}

	protected boolean isAvailable(Shell shell) {
		if (shell == null || shell.isDisposed()) {
			return false;
		}
		return true;
	}

	protected Region createRoundedCorners() {
		Region region = new Region();
		Point point = shell.getSize();
		region.add(0, 0, point.x, point.y);

		region.subtract(0, 0, 7, 1);
		region.subtract(0, 1, 5, 1);
		region.subtract(0, 2, 4, 1);
		region.subtract(0, 3, 3, 1);
		region.subtract(0, 4, 2, 1);
		region.subtract(0, 5, 1, 1);
		region.subtract(0, 6, 1, 1);

		region.subtract(point.x - 7, 0, 7, 1);
		region.subtract(point.x - 5, 1, 5, 1);
		region.subtract(point.x - 4, 2, 4, 1);
		region.subtract(point.x - 3, 3, 3, 1);
		region.subtract(point.x - 2, 4, 2, 1);
		region.subtract(point.x - 1, 5, 1, 1);
		region.subtract(point.x - 1, 6, 1, 1);

		region.subtract(point.x - 7, point.y - 1, 7, 1);
		region.subtract(point.x - 5, point.y - 2, 5, 1);
		region.subtract(point.x - 4, point.y - 3, 4, 1);
		region.subtract(point.x - 3, point.y - 4, 3, 1);
		region.subtract(point.x - 2, point.y - 5, 2, 1);
		region.subtract(point.x - 1, point.y - 6, 1, 1);
		region.subtract(point.x - 1, point.y - 7, 1, 1);

		region.subtract(0, point.y - 1, 7, 1);
		region.subtract(0, point.y - 2, 5, 1);
		region.subtract(0, point.y - 3, 4, 1);
		region.subtract(0, point.y - 4, 3, 1);
		region.subtract(0, point.y - 5, 2, 1);
		region.subtract(0, point.y - 6, 1, 1);
		region.subtract(0, point.y - 7, 1, 1);

		return region;
	}

}
