if (chrome.extension) {
	chrome.extension.sendRequest({method : "requestSummary"}, function(response){});
	chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
		if (request.method == "updateSummary") {
			var s =request.summary;
			populateSummary(s.url, s.requests, s.events, s.critical, s.warning, s.normal);
		}
	});
}

function populateSummary(url, requests, events, critical, warning, normal) {
	$('#summary-bar-url').text(url);
	$('#summary-requests-count').text(requests);
	$('#summary-events-count').text(events);
	$('#summary-icon-critical').text(critical);
	$('#summary-icon-warning').text(warning);
	$('#summary-icon-normal').text(normal);
}

function openTab() {
    chrome.tabs.create({'url': chrome.extension.getURL('main.html')}, function(tab) {
    });

}