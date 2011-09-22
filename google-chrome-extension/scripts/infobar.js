function showDetails() {
	var bg = chrome.extension.getBackgroundPage();
	bg.openEvents();
	return nope();
}
function nope() {
	var bg = chrome.extension.getBackgroundPage();
	bg.windowClosed();
	window.close();
	return false;
}
function never() {
	return nope();
}
