function showDetails() {
	var bg = chrome.extension.getBackgroundPage();
	bg.openEvents();
	window.close();
	return false;
}
function never() {
	var bg = chrome.extension.getBackgroundPage();
	bg.neverForThisApplication();
	window.close();
	return false;
}
