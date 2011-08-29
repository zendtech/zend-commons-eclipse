function showDetails() {
	chrome.tabs.insertCSS(null, { file: "style/style.css" }, function() {
		chrome.tabs.executeScript(null, { file: "scripts/jquery.js" }, function() {
			chrome.tabs.executeScript(null, { file: "scripts/tinyfader.js" }, function() {
		    	chrome.tabs.getSelected(null, function(tab) {
		    		chrome.tabs.sendRequest(tab.id, {details: "now"});
		    	});
			});
		});
	});
	return nope();
}
function nope() {
    window.close();
	return false;
}
function never() {
	return nope();
}
