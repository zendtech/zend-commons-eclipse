package org.zend.core.notifications.util;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

public class Colors {
	
	/**
	 * Returns the color registry of the active color theme.
	 * 
	 * @return a <code>ColorRegistry</code> object
	 * @see ColorRegistry
	 */
	public static final ColorRegistry getColorRegistry() {
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();
		return currentTheme.getColorRegistry();
	}

}
