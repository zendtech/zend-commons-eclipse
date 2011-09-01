function showDetails() {
	var bg = chrome.extension.getBackgroundPage();
	bg.openEvents();
	return nope();
}
function nope() {
    window.close();
	return false;
}
function never() {
	return nope();
}
