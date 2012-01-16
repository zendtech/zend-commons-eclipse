/*******************************************************************************
 * Copyright (c) 2004, 2012 Stefan Zeiger and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.novocode.com/legal/epl-v10.html
 * 
 * Contributors:
 *     Stefan Zeiger (szeiger@novocode.com) - initial API and implementation
 *     Zend Technologies Ltd. - block on open
 *******************************************************************************/
package org.zend.usagedata.ui.internal.message;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.zend.usagedata.ui.internal.Messages;
import org.zend.usagedata.ui.internal.UIUsageDataActivator;

/**
 * A Shell wrapper which creates balloon popup windows.
 * 
 * <p>
 * By default, a balloon window has no title bar or system controls. The
 * following styles are supported:
 * </p>
 * 
 * <ul>
 * <li>SWT.ON_TOP - Keep the window on top of other windows</li>
 * <li>SWT.TOOL - Add a drop shadow to the window (on supported platforms)</li>
 * <li>SWT.CLOSE - Show a "close" control on the title bar (implies SWT.TITLE)</li>
 * <li>SWT.TITLE - Show a title bar</li>
 * </ul>
 * 
 * @author Stefan Zeiger (szeiger@novocode.com)
 * @since Jul 2, 2004 Licensed under Eclipse Public License 1.0.
 */

public class CalloutWindow {

	public static final int OK = 0;
	public static final int CANCEL = 1;

	private final Shell shell;
	private final Composite contents;
	private Label titleLabel;
	private Canvas titleImageLabel;
	private final int style;
	private int preferredAnchor = SWT.BOTTOM | SWT.RIGHT;
	private boolean autoAnchor = true;
	private int locX = Integer.MIN_VALUE, locY = Integer.MIN_VALUE;
	private int marginLeft = 12, marginRight = 12, marginTop = 5,
			marginBottom = 10;
	private int titleSpacing = 3, titleWidgetSpacing = 8;
	private ToolBar systemControlsBar;
	private ArrayList<Control> selectionControls = new ArrayList<Control>();
	private boolean addedGlobalListener;
	private ArrayList<Listener> selectionListeners = new ArrayList<Listener>();
	private Link descriptionLabel;
	private String description;
	private int delayClose;
	private Button checkboxDontShow;
	private Button agreeToSend;
	private boolean isShowMessage;
	private boolean doNotDisplay = false;
	private boolean block = false;
	private Timer timer;
	private int returnCode = CalloutWindow.CANCEL;

	public CalloutWindow(Shell parent, int style) {
		this(null, parent, style);
	}

	public CalloutWindow(Display display, int style) {
		this(display, null, style);
	}

	public CalloutWindow(Display display, Shell parent, final int style) {
		this.timer = new Timer();
		this.style = style;
		int shellStyle = style & (SWT.ON_TOP | SWT.TOOL);
		if (parent == null) {
			parent = display.getActiveShell();
		}
		System.out.println(parent);
		this.shell = new Shell(parent, SWT.ON_TOP | SWT.NO_TRIM | shellStyle);
		this.contents = new Composite(shell, SWT.NONE);

		final Color c = new Color(shell.getDisplay(), 255, 255, 225);
		shell.setBackground(c);
		shell.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		contents.setBackground(shell.getBackground());
		contents.setForeground(shell.getForeground());

		selectionControls.add(shell);
		selectionControls.add(contents);

		final Listener globalListener = new Listener() {
			public void handleEvent(Event event) {
				Widget w = event.widget;
				for (int i = selectionControls.size() - 1; i >= 0; i--) {
					if (selectionControls.get(i) == w) {
						if ((style & SWT.CLOSE) != 0) {
							for (int j = selectionListeners.size() - 1; j >= 0; j--)
								selectionListeners.get(j).handleEvent(event);
						} else {
							shell.close();
						}
						event.doit = false;
					}
				}
			}
		};

		shell.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event event) {
				if (!addedGlobalListener) {
					shell.getDisplay().addFilter(SWT.MouseDown, globalListener);
					addedGlobalListener = true;
				}
			}
		});

		shell.addListener(SWT.Hide, new Listener() {
			public void handleEvent(Event event) {
				if (addedGlobalListener) {
					shell.getDisplay().removeFilter(SWT.MouseDown,
							globalListener);
					addedGlobalListener = false;
				}
			}
		});

		shell.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				if (addedGlobalListener) {
					shell.getDisplay().removeFilter(SWT.MouseDown,
							globalListener);
					addedGlobalListener = false;
				}
				c.dispose();
			}
		});
	}

	/**
	 * Adds a control to the list of controls which close the balloon window.
	 * The background, title image and title text are included by default.
	 */

	public void addSelectionControl(Control c) {
		selectionControls.add(c);
	}

	public void addListener(int type, Listener l) {
		if (type == SWT.Selection)
			selectionListeners.add(l);
	}

	/**
	 * Set the location of the anchor. This must be one of the following values:
	 * SWT.NONE, SWT.LEFT|SWT.TOP, SWT.RIGHT|SWT.TOP, SWT.LEFT|SWT.BOTTOM,
	 * SWT.RIGHT|SWT.BOTTOM
	 */

	public void setAnchor(int anchor) {
		switch (anchor) {
		case SWT.NONE:
		case SWT.LEFT | SWT.TOP:
		case SWT.RIGHT | SWT.TOP:
		case SWT.LEFT | SWT.BOTTOM:
		case SWT.RIGHT | SWT.BOTTOM:
			break;
		default:
			throw new IllegalArgumentException("Illegal anchor value " + anchor); //$NON-NLS-1$
		}
		this.preferredAnchor = anchor;
	}

	public void setAutoAnchor(boolean autoAnchor) {
		this.autoAnchor = autoAnchor;
	}

	public void setLocation(int x, int y) {
		this.locX = x;
		this.locY = y;
	}

	public void setLocation(Point p) {
		this.locX = p.x;
		this.locY = p.y;
	}

	public void setText(String title) {
		shell.setText(title);
	}

	public void setImage(Image image) {
		shell.setImage(image);
	}

	public void setMargins(int marginLeft, int marginRight, int marginTop,
			int marginBottom) {
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
	}

	public void setMargins(int marginX, int marginY) {
		setMargins(marginX, marginX, marginY, marginY);
	}

	public void setMargins(int margin) {
		setMargins(margin, margin, margin, margin);
	}

	public void setTitleSpacing(int titleSpacing) {
		this.titleSpacing = titleSpacing;
	}

	public void setTitleWidgetSpacing(int titleImageSpacing) {
		this.titleWidgetSpacing = titleImageSpacing;
	}

	public Shell getShell() {
		return shell;
	}

	public void prepareForOpen() {
		Point contentsSize = contents.getSize();
		Point titleSize = new Point(0, 0);

		boolean showTitle = ((style & (SWT.CLOSE | SWT.TITLE)) != 0);
		if (showTitle) {
			if (titleLabel == null) {
				titleLabel = new Label(shell, SWT.NONE);
				titleLabel.setBackground(shell.getBackground());
				titleLabel.setForeground(shell.getForeground());
				FontData[] fds = shell.getFont().getFontData();
				for (int i = 0; i < fds.length; i++) {
					fds[i].setStyle(fds[i].getStyle() | SWT.BOLD);
				}
				final Font font = new Font(shell.getDisplay(), fds);
				titleLabel.addListener(SWT.Dispose, new Listener() {
					public void handleEvent(Event event) {
						font.dispose();
					}
				});
				titleLabel.setFont(font);
				selectionControls.add(titleLabel);
			}
			String titleText = shell.getText();
			titleLabel.setText(titleText == null ? "" : titleText); //$NON-NLS-1$
			titleLabel.pack();
			titleSize = titleLabel.getSize();

			if (systemControlsBar == null && (style & SWT.CLOSE) != 0) {
				// Color closeFG = shell.getForeground(), closeBG =
				// shell.getBackground();
				// Color closeFG =
				// shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY),
				// closeBG = shell.getBackground();
				Color closeFG = shell.getDisplay().getSystemColor(
						SWT.COLOR_WIDGET_FOREGROUND), closeBG = shell
						.getDisplay().getSystemColor(
								SWT.COLOR_WIDGET_BACKGROUND);
				final Image closeImage = createCloseImage(shell.getDisplay(),
						closeBG, closeFG);
				shell.addListener(SWT.Dispose, new Listener() {
					public void handleEvent(Event event) {
						closeImage.dispose();
					}
				});
				systemControlsBar = new ToolBar(shell, SWT.FLAT);
				systemControlsBar.setBackground(closeBG);
				systemControlsBar.setForeground(closeFG);
				ToolItem closeItem = new ToolItem(systemControlsBar, SWT.PUSH);
				closeItem.setImage(closeImage);
				closeItem.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						shell.close();
					}
				});
				systemControlsBar.pack();
				Point closeSize = systemControlsBar.getSize();
				titleSize.x += closeSize.x + titleWidgetSpacing;
				if (closeSize.y > titleSize.y)
					titleSize.y = closeSize.y;
			}

			if (descriptionLabel == null) {
				descriptionLabel = new Link(shell, SWT.NONE);
				descriptionLabel.setBackground(shell.getBackground());
				descriptionLabel.setForeground(shell.getForeground());
				FontData[] fds = shell.getFont().getFontData();
				final Font font = new Font(shell.getDisplay(), fds);
				descriptionLabel.addListener(SWT.Dispose, new Listener() {
					public void handleEvent(Event event) {
						font.dispose();
					}
				});
				descriptionLabel.setFont(font);
				selectionControls.add(descriptionLabel);
				String descriptionText = getDescription();
				descriptionLabel.setText(descriptionText == null ? "" //$NON-NLS-1$
						: descriptionText);
				descriptionLabel.pack();
				Point descriptionSize = descriptionLabel.getSize();
				titleSize.x = Math.max(titleSize.x, descriptionSize.x);
				titleSize.y += descriptionSize.y;
				descriptionLabel.setLocation(marginLeft, marginTop + 19);
				descriptionLabel.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						shell.getDisplay().syncExec(new Runnable() {

							@Override
							public void run() {
								timer.cancel();
								// TODO add real message dialog
								MessageDialog dialog = new MessageDialog(
										PlatformUI.getWorkbench()
												.getActiveWorkbenchWindow()
												.getShell(),
										Messages.UIPreUploadListener_Title,
										UIUsageDataActivator.getImageDescriptor(
												UIUsageDataActivator.INFO_ICON)
												.createImage(),
										"put message here", 0, new String[] {
												"Yes", "No" }, 0);
								if (dialog.open() == 0) {
									returnCode = CalloutWindow.OK;
								} else {
									returnCode = CalloutWindow.CANCEL;
								}
								close();
							}
						});
					}
				});
			}

			if (agreeToSend == null) {
				agreeToSend = new Button(shell, SWT.PUSH);
				agreeToSend.setBackground(shell.getBackground());
				agreeToSend.setForeground(shell.getForeground());
				FontData[] fds = shell.getFont().getFontData();
				final Font font = new Font(shell.getDisplay(), fds);
				agreeToSend.addListener(SWT.Dispose, new Listener() {
					public void handleEvent(Event event) {
						font.dispose();
					}
				});
				agreeToSend.setFont(font);
				agreeToSend.addSelectionListener(new SelectionListener() {

					public void widgetSelected(SelectionEvent e) {
						widgetDefaultSelected(e);
					}

					public void widgetDefaultSelected(SelectionEvent e) {
						returnCode = CalloutWindow.CANCEL;
						close();
					}
				});
				selectionControls.add(agreeToSend);

				String dontText = "Yes"; //$NON-NLS-1$
				agreeToSend.setText(dontText);
				agreeToSend.pack();
				Point dontSize = agreeToSend.getSize();
				titleSize.x = Math.max(titleSize.x, dontSize.x);
				titleSize.y += dontSize.y;
				agreeToSend.setLocation(
						marginLeft,
						descriptionLabel.getBounds().x
								+ descriptionLabel.getBounds().height
								+ marginTop + 15);
			}

			if (checkboxDontShow == null && isShowMessage) {
				checkboxDontShow = new Button(shell, SWT.CHECK);
				checkboxDontShow.setBackground(shell.getBackground());
				checkboxDontShow.setForeground(shell.getForeground());
				FontData[] fds = shell.getFont().getFontData();
				final Font font = new Font(shell.getDisplay(), fds);
				checkboxDontShow.addListener(SWT.Dispose, new Listener() {
					public void handleEvent(Event event) {
						font.dispose();
					}
				});
				checkboxDontShow.setFont(font);
				checkboxDontShow.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						widgetDefaultSelected(e);
						if (checkboxDontShow.getSelection()) {
							doNotDisplay = true;
						}
					}
				});
				selectionControls.add(checkboxDontShow);

				String dontText = "Don't show this message again."; //$NON-NLS-1$
				checkboxDontShow.setText(dontText);
				checkboxDontShow.pack();
				Point dontSize = checkboxDontShow.getSize();
				titleSize.x = Math.max(titleSize.x, dontSize.x);
				titleSize.y += dontSize.y;
				checkboxDontShow.setLocation(
						marginLeft,
						descriptionLabel.getBounds().x
								+ descriptionLabel.getBounds().height
								+ agreeToSend.getBounds().height + marginTop
								+ 15);
			}

			titleSize.y += titleSpacing;
			if (titleSize.x > contentsSize.x) {
				contentsSize.x = titleSize.x;
				contents.setSize(contentsSize.x, contentsSize.y);
			}
			contentsSize.y += titleSize.y + marginBottom;
		}

		Rectangle screen = shell.getDisplay().getClientArea();

		int anchor = preferredAnchor;
		if (anchor != SWT.NONE && autoAnchor && locX != Integer.MIN_VALUE) {
			if ((anchor & SWT.LEFT) != 0) {
				if (locX + contentsSize.x + marginLeft + marginRight - 16 >= screen.x
						+ screen.width)
					anchor = anchor - SWT.LEFT + SWT.RIGHT;
			} else // RIGHT
			{
				if (locX - contentsSize.x - marginLeft - marginRight + 16 < screen.x)
					anchor = anchor - SWT.RIGHT + SWT.LEFT;
			}
			if ((anchor & SWT.TOP) != 0) {
				if (locY + contentsSize.y + 20 + marginTop + marginBottom >= screen.y
						+ screen.height)
					anchor = anchor - SWT.TOP + SWT.BOTTOM;
			} else // BOTTOM
			{
				if (locY - contentsSize.y - 20 - marginTop - marginBottom < screen.y)
					anchor = anchor - SWT.BOTTOM + SWT.TOP;
			}
		}

		final Point shellSize = (anchor == SWT.NONE) ? new Point(contentsSize.x
				+ marginLeft + marginRight, contentsSize.y + marginTop
				+ marginBottom) : new Point(contentsSize.x + marginLeft
				+ marginRight, contentsSize.y + marginTop + marginBottom + 20);

		if (shellSize.x < 54 + marginLeft + marginRight)
			shellSize.x = 54 + marginLeft + marginRight;
		if (anchor == SWT.NONE) {
			if (shellSize.y < 10 + marginTop + marginBottom)
				shellSize.y = 10 + marginTop + marginBottom;
		} else {
			if (shellSize.y < 30 + marginTop + marginBottom)
				shellSize.y = 30 + marginTop + marginBottom;
		}

		shell.setSize(shellSize);
		int titleLocY = marginTop + (((anchor & SWT.TOP) != 0) ? 20 : 0);
		contents.setLocation(marginLeft, titleSize.y + titleLocY);
		if ((anchor & SWT.TOP) != 0) {
			Point pt = descriptionLabel.getLocation();
			pt.y += 20;
			descriptionLabel.setLocation(pt);
			if (checkboxDontShow != null) {
				pt = checkboxDontShow.getLocation();
				pt.y += 20;
				checkboxDontShow.setLocation(pt);
			}
		}
		if (showTitle) {
			// int realTitleHeight = titleSize.y - titleSpacing;
			if (titleImageLabel != null) {
				titleImageLabel.setLocation(marginLeft, titleLocY);
				titleLabel.setLocation(marginLeft + titleImageLabel.getSize().x
						+ titleWidgetSpacing, titleLocY);
			} else
				titleLabel.setLocation(marginLeft, titleLocY);
			if (systemControlsBar != null)
				systemControlsBar.setLocation(shellSize.x - marginRight
						- systemControlsBar.getSize().x, titleLocY);
		}

		final Region region = new Region();
		region.add(createOutline(shellSize, anchor, true));

		shell.setRegion(region);
		shell.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				region.dispose();
			}
		});

		final int[] outline = createOutline(shellSize, anchor, false);
		shell.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				event.gc.drawPolygon(outline);
			}
		});

		if (locX != Integer.MIN_VALUE) {
			Point shellLoc = new Point(locX, locY);
			if ((anchor & SWT.BOTTOM) != 0)
				shellLoc.y = shellLoc.y - shellSize.y + 1;
			if ((anchor & SWT.LEFT) != 0)
				shellLoc.x -= 15;
			else if ((anchor & SWT.RIGHT) != 0)
				shellLoc.x = shellLoc.x - shellSize.x + 16;

			if (autoAnchor) {
				if (shellLoc.x < screen.x)
					shellLoc.x = screen.x;
				else if (shellLoc.x > screen.x + screen.width - shellSize.x)
					shellLoc.x = screen.x + screen.width - shellSize.x;

				if (anchor == SWT.NONE) {
					if (shellLoc.y < screen.y)
						shellLoc.y = screen.y;
					else if (shellLoc.y > screen.y + screen.height
							- shellSize.y)
						shellLoc.y = screen.y + screen.height - shellSize.y;
				}
			}

			shell.setLocation(shellLoc);
		}
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int open() {
		prepareForOpen();
		shell.open();

		// run the event loop if specified
		if (block) {
			runEventLoop(shell);
		}

		return returnCode;
	}

	/*
	 * public void open() { if (calloutId != null && calloutId.length() > 0) {
	 * IPreferenceStore preferenceStore = Activator.getDefault()
	 * .getPreferenceStore(); preferenceStore.setDefault(calloutId, true);
	 * boolean shouldOpen = preferenceStore.getBoolean(calloutId); if
	 * (!shouldOpen) { return; } }
	 * 
	 * prepareForOpen(); shell.open();
	 * 
	 * if (delayClose > 0) { final Timer timer = new Timer(); timer.schedule(new
	 * TimerTask() {
	 * 
	 * @Override public void run() { Display.getDefault().asyncExec(new
	 * Runnable() { public void run() { if (shell != null &&
	 * !shell.isDisposed()) { close(); } timer.cancel(); } }); } }, delayClose);
	 * }
	 * 
	 * }
	 */

	public void close() {
		if (shell != null && !shell.isDisposed()) {
			shell.close();
		}
	}

	public void setDelayClose(int delayClose) {
		this.delayClose = delayClose;
	}

	public void setVisible(boolean visible) {
		if (visible)
			prepareForOpen();
		shell.setVisible(visible);
	}

	public void setIsShowMessage(boolean isShowMessage) {
		this.isShowMessage = isShowMessage;
	}

	/**
	 * Sets whether the <code>open</code> method should block until the window
	 * closes.
	 * 
	 * @param shouldBlock
	 *            <code>true</code> if the <code>open</code> method should not
	 *            return until the window closes, and <code>false</code> if the
	 *            <code>open</code> method should return immediately
	 */
	public void setBlockOnOpen(boolean shouldBlock) {
		block = shouldBlock;
	}

	public boolean isDoNotDisplay() {
		return doNotDisplay;
	}

	private static int[] createOutline(Point size, int anchor, boolean outer) {
		int o = outer ? 1 : 0;
		int w = size.x + o;
		int h = size.y + o;

		switch (anchor) {
		case SWT.RIGHT | SWT.BOTTOM:
			return new int[] {
					// top and top right
					5, 0, w - 6, 0,
					w - 6,
					1,
					w - 4,
					1,
					w - 4,
					2,
					w - 3,
					2,
					w - 3,
					3,
					w - 2,
					3,
					w - 2,
					5,
					w - 1,
					5,
					// right and bottom right
					w - 1, h - 26, w - 2,
					h - 26,
					w - 2,
					h - 24,
					w - 3,
					h - 24,
					w - 3,
					h - 23,
					w - 4,
					h - 23,
					w - 4,
					h - 22,
					w - 6,
					h - 22,
					w - 6,
					h - 21,
					// bottom with anchor
					w - 16, h - 21, w - 16, h - 1, w - 16 - o, h - 1,
					w - 16 - o, h - 2, w - 17 - o, h - 2, w - 17 - o, h - 3,
					w - 18 - o, h - 3, w - 18 - o, h - 4, w - 19 - o, h - 4,
					w - 19 - o, h - 5, w - 20 - o, h - 5, w - 20 - o, h - 6,
					w - 21 - o, h - 6, w - 21 - o, h - 7, w - 22 - o, h - 7,
					w - 22 - o, h - 8, w - 23 - o, h - 8, w - 23 - o, h - 9,
					w - 24 - o, h - 9, w - 24 - o, h - 10, w - 25 - o, h - 10,
					w - 25 - o, h - 11, w - 26 - o, h - 11, w - 26 - o, h - 12,
					w - 27 - o, h - 12, w - 27 - o, h - 13, w - 28 - o, h - 13,
					w - 28 - o, h - 14, w - 29 - o, h - 14, w - 29 - o, h - 15,
					w - 30 - o, h - 15, w - 30 - o, h - 16, w - 31 - o, h - 16,
					w - 31 - o, h - 17, w - 32 - o, h - 17, w - 32 - o, h - 18,
					w - 33 - o, h - 18, w - 33 - o, h - 19, w - 34 - o, h - 19,
					w - 34 - o, h - 20, w - 35 - o, h - 20, w - 35 - o, h - 21,
					// bottom left
					5, h - 21, 5, h - 22, 3, h - 22, 3, h - 23, 2, h - 23, 2,
					h - 24, 1, h - 24, 1, h - 26, 0, h - 26,
					// left and top left
					0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
		case SWT.LEFT | SWT.BOTTOM:
			return new int[] {
					// top and top right
					5, 0, w - 6, 0, w - 6, 1, w - 4,
					1,
					w - 4,
					2,
					w - 3,
					2,
					w - 3,
					3,
					w - 2,
					3,
					w - 2,
					5,
					w - 1,
					5,
					// right and bottom right
					w - 1, h - 26, w - 2, h - 26, w - 2, h - 24,
					w - 3,
					h - 24,
					w - 3,
					h - 23,
					w - 4,
					h - 23,
					w - 4,
					h - 22,
					w - 6,
					h - 22,
					w - 6,
					h - 21,
					// bottom with anchor
					34 + o, h - 21, 34 + o, h - 20, 33 + o, h - 20, 33 + o,
					h - 19, 32 + o, h - 19, 32 + o, h - 18, 31 + o, h - 18,
					31 + o, h - 17, 30 + o, h - 17, 30 + o, h - 16, 29 + o,
					h - 16, 29 + o, h - 15, 28 + o, h - 15, 28 + o, h - 14,
					27 + o, h - 14, 27 + o, h - 13, 26 + o, h - 13, 26 + o,
					h - 12, 25 + o, h - 12, 25 + o, h - 11, 24 + o, h - 11,
					24 + o, h - 10, 23 + o, h - 10, 23 + o, h - 9, 22 + o,
					h - 9, 22 + o, h - 8, 21 + o, h - 8, 21 + o, h - 7, 20 + o,
					h - 7, 20 + o, h - 6, 19 + o, h - 6, 19 + o, h - 5, 18 + o,
					h - 5, 18 + o, h - 4, 17 + o, h - 4, 17 + o, h - 3, 16 + o,
					h - 3, 16 + o, h - 2, 15 + o, h - 2, 15, h - 1, 15, h - 21,
					// bottom left
					5, h - 21, 5, h - 22, 3, h - 22, 3, h - 23, 2, h - 23, 2,
					h - 24, 1, h - 24, 1, h - 26, 0, h - 26,
					// left and top left
					0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
		case SWT.RIGHT | SWT.TOP:
			return new int[] {
					// top with anchor
					5, 20, w - 35 - o, 20, w - 35 - o, 19, w - 34 - o, 19,
					w - 34 - o, 18, w - 33 - o, 18, w - 33 - o, 17, w - 32 - o,
					17, w - 32 - o, 16, w - 31 - o, 16, w - 31 - o, 15,
					w - 30 - o, 15, w - 30 - o, 14, w - 29 - o, 14, w - 29 - o,
					13, w - 28 - o, 13, w - 28 - o, 12, w - 27 - o, 12,
					w - 27 - o, 11, w - 26 - o, 11, w - 26 - o, 10, w - 25 - o,
					10, w - 25 - o, 9, w - 24 - o, 9, w - 24 - o, 8,
					w - 23 - o, 8, w - 23 - o, 7, w - 22 - o, 7, w - 22 - o, 6,
					w - 21 - o, 6, w - 21 - o, 5, w - 20 - o, 5, w - 20 - o, 4,
					w - 19 - o, 4, w - 19 - o, 3, w - 18 - o, 3, w - 18 - o, 2,
					w - 17 - o, 2, w - 17 - o, 1, w - 16 - o, 1, w - 16 - o, 0,
					w - 16,
					0,
					w - 16,
					20,
					// top and top right
					w - 6, 20, w - 6, 21, w - 4, 21, w - 4, 22, w - 3, 22,
					w - 3, 23, w - 2, 23, w - 2, 25,
					w - 1,
					25,
					// right and bottom right
					w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4,
					w - 3, h - 3, w - 4, h - 3, w - 4, h - 2, w - 6, h - 2,
					w - 6, h - 1,
					// bottom and bottom left
					5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4,
					1, h - 4, 1, h - 6, 0, h - 6,
					// left and top left
					0, 25, 1, 25, 1, 23, 2, 23, 2, 22, 3, 22, 3, 21, 5, 21 };
		case SWT.LEFT | SWT.TOP:
			return new int[] {
					// top with anchor
					5, 20, 15, 20, 15, 0, 15 + o, 0, 16 + o, 1, 16 + o, 2,
					17 + o, 2, 17 + o, 3, 18 + o, 3, 18 + o, 4, 19 + o, 4,
					19 + o, 5, 20 + o, 5, 20 + o, 6, 21 + o, 6, 21 + o, 7,
					22 + o, 7, 22 + o, 8, 23 + o, 8, 23 + o, 9, 24 + o, 9,
					24 + o, 10, 25 + o, 10, 25 + o, 11, 26 + o, 11, 26 + o, 12,
					27 + o, 12, 27 + o, 13, 28 + o, 13, 28 + o, 14, 29 + o, 14,
					29 + o, 15, 30 + o, 15, 30 + o, 16, 31 + o, 16, 31 + o, 17,
					32 + o, 17, 32 + o, 18, 33 + o, 18, 33 + o, 19,
					34 + o,
					19,
					34 + o,
					20,
					// top and top right
					w - 6, 20, w - 6, 21, w - 4, 21, w - 4, 22, w - 3, 22,
					w - 3, 23, w - 2, 23, w - 2, 25,
					w - 1,
					25,
					// right and bottom right
					w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4,
					w - 3, h - 3, w - 4, h - 3, w - 4, h - 2, w - 6, h - 2,
					w - 6, h - 1,
					// bottom and bottom left
					5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4,
					1, h - 4, 1, h - 6, 0, h - 6,
					// left and top left
					0, 25, 1, 25, 1, 23, 2, 23, 2, 22, 3, 22, 3, 21, 5, 21 };
		default:
			return new int[] {
					// top and top right
					5, 0, w - 6, 0, w - 6, 1, w - 4, 1, w - 4, 2, w - 3, 2,
					w - 3, 3, w - 2, 3, w - 2, 5,
					w - 1,
					5,
					// right and bottom right
					w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4,
					w - 3, h - 3, w - 4, h - 3, w - 4, h - 2, w - 6, h - 2,
					w - 6, h - 1,
					// bottom and bottom left
					5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4,
					1, h - 4, 1, h - 6, 0, h - 6,
					// left and top left
					0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
		}
	}

	private static final Image createCloseImage(Display display, Color bg,
			Color fg) {
		int size = 11, off = 1;
		Image image = new Image(display, size, size);
		GC gc = new GC(image);
		gc.setBackground(bg);
		gc.fillRectangle(image.getBounds());
		gc.setForeground(fg);
		gc.drawLine(0 + off, 0 + off, size - 1 - off, size - 1 - off);
		gc.drawLine(1 + off, 0 + off, size - 1 - off, size - 2 - off);
		gc.drawLine(0 + off, 1 + off, size - 2 - off, size - 1 - off);
		gc.drawLine(size - 1 - off, 0 + off, 0 + off, size - 1 - off);
		gc.drawLine(size - 1 - off, 1 + off, 1 + off, size - 1 - off);
		gc.drawLine(size - 2 - off, 0 + off, 0 + off, size - 2 - off);
		/*
		 * gc.drawLine(1, 0, size-2, 0); gc.drawLine(1, size-1, size-2, size-1);
		 * gc.drawLine(0, 1, 0, size-2); gc.drawLine(size-1, 1, size-1, size-2);
		 */
		gc.dispose();
		return image;
	}

	/**
	 * Runs the event loop for the given shell.
	 * 
	 * @param loopShell
	 *            the shell
	 */
	private void runEventLoop(Shell loopShell) {

		// Use the display provided by the shell if possible
		final Display display;
		if (shell == null) {
			display = Display.getCurrent();
		} else {
			display = loopShell.getDisplay();
		}

		if (delayClose > 0) {
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					display.asyncExec(new Runnable() {
						public void run() {
							if (shell != null && !shell.isDisposed()) {
								close();
							}
							timer.cancel();
						}
					});
				}
			}, delayClose);
		}

		while (loopShell != null && !loopShell.isDisposed()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} catch (Throwable e) {
				UIUsageDataActivator.log(e);
			}
		}
		if (!display.isDisposed())
			display.update();
	}

}