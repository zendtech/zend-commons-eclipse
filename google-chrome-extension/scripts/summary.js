function populateSummary(url, requests, events, critical, error, warning) {
	$('#summary-bar-url').text(url);
	$('#summary-requests-count').text(requests);
	$('#summary-events-count').text(events);
	$('#summary-icon-critical').text(critical);
	$('#summary-icon-error').text(error);
	$('#summary-icon-warning').text(warning);
}

function openTab() {
    chrome.tabs.create({'url': chrome.extension.getURL('main.html')}, function(tab) {
    });

}