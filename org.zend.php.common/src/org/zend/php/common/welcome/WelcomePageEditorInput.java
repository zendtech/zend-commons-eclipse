package org.zend.php.common.welcome;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.StatusHandler;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.internal.browser.WebBrowserUIPlugin;
import org.zend.php.common.ZendCatalogViewer;

public class WelcomePageEditorInput extends WebBrowserEditorInput {
	
	private String discoveryFile;
	private boolean showCategories;
	private String progressDialogMsg;

	public WelcomePageEditorInput() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WelcomePageEditorInput(URL url, boolean b) {
		super(url, b);
		// TODO Auto-generated constructor stub
	}

	public WelcomePageEditorInput(URL url, int style, String browserId, String discoveryFile, boolean showCategories, String progressDialogMsg) {
		super(url, style, browserId);
		this.discoveryFile = discoveryFile;
		this.showCategories = showCategories;
		this.progressDialogMsg = progressDialogMsg;
		// TODO Auto-generated constructor stub
	}

	public WelcomePageEditorInput(URL url) {
		super(url);
		// TODO Auto-generated constructor stub
	}

	public WelcomePageEditorInput(URL url, int style) {
		super(url, style);
	}

	public String getFactoryId() {
		return ELEMENT_FACTORY_ID;
	}

	public String getName() {
		return "Welcome";
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	public boolean exists() {
		return true;
	}

	public IPersistableElement getPersistable() {
		return this;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof WebBrowserEditorInput) {
			if (this.getURL().equals(((WebBrowserEditorInput) obj).getURL()))
				return true;
		}
		return false;
	}

	public static final String ELEMENT_FACTORY_ID = WelcomePageEditorInput.class
			.getName();

	public String getDiscoveryDirFileName() {
		return discoveryFile;
	}
	
	public void saveState(IMemento memento) {
		super.saveState(memento);
		if (discoveryFile != null) {
			memento.putString("discoveryFile", discoveryFile);
		}
		memento.putBoolean("categories", showCategories);
		memento.putString("progressDialogMsg", progressDialogMsg);
	}
	
	public IAdaptable createElement(IMemento memento) {
		int style = 0;
		Integer integer = memento.getInteger("style");
		if (integer != null) {
			style = integer.intValue();
		}

		URL url = null;
		String str = memento.getString("url");
		if (str != null) {
			try {
				url = new URL(str);
			}
			catch (MalformedURLException e) {
				String msg = "Malformed URL while initializing browser editor"; //$NON-NLS-1$
				WebBrowserUIPlugin.logError(msg, e);
			}
		}

		String id = memento.getString("id");
		String name = memento.getString("name");
		String tooltip = memento.getString("tooltip");
		String discoveryFileName = memento.getString("discoveryFile");
		Boolean showCategories =memento.getBoolean("categories");
		if (showCategories == null) {
			showCategories = false;
		}
		String progressDialogMsg = memento.getString("progressDialogMsg");
		
		WebBrowserEditorInput input = new WelcomePageEditorInput(url, style, id, discoveryFileName, showCategories, progressDialogMsg);
		input.setName(name);
		input.setToolTipText(tooltip);
		return input;
	}

	public StatusHandler getStatusHandler() {
		return null;
	}

	public void initFeaturesViewer(ZendCatalogViewer viewer) {
		viewer.setDiscoveryDirFileName(getDiscoveryDirFileName());
		viewer.setShowCategories(doShowCategories());
		viewer.setOperationName(progressDialogMsg);
	}

	public boolean doShowCategories() {
		return showCategories;
	}

}
