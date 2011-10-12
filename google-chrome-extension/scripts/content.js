chrome.extension.sendRequest({
	method : "ContentEvaluateCookie",
	domain : window.location.hostname,
	path : window.location.pathname,
	cookie : document.cookie
});