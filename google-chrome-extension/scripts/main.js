if (chrome.extension) {
	chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
		if (request.method == "showNotifications") {
			addRequest(request.request);
			populateZniffingUrl(request.request.url);
		}
	});
}

function populateZniffingUrl(url) {
	$('#zniffing-url').text(url);
}

function toggleLock(img) {
	if (window.lock) {
		img.src="images/lock-open.png";
		img.title="Zniffer is accepting requests";
	} else {
		img.src="images/lock-close.png";
		img.title="Zniffer is not accepting new requests' information";
	}
	window.lock = !window.lock;
}

function showAbout() {
	$('#about-content').modal({opacity: 0, modal: false, autoPosition: true, position: [40, 5]});
}

function populateStudioButtons(container, codetracing, eventId, issueId) {
	codetracingButton = $('#codetracing-button').get(0);
	if (codetracing) {
		codetracingButton.onclick = 'openCodeTracingSnapshot(\'' + container + '\', \'' + codetracing + '\');';
		codetracingButton.title = "Open code tracing in Studio";
		codetracingButton.src = "images/codetracing.png";
	} else {
		codetracingButton.onclick = '';
		codetracingButton.title = "Code tracing is unavailable";
		codetracingButton.src = "images/codetracing-disabled.png";
	}
	
	debugButton = $('#debug-button').get(0);
	if (eventId && issueId) {
		debugButton.onclick = 'openTunnelAndDebug(\'' + container + '\', \'' + eventId + '\', \'' + issueId + '\');';
		debugButton.title = "Debug event in Studio";
		debugButton.src = "images/debug-button.png";
	} else {
		debugButton.onclick = '';
		debugButton.title = "Debugging is unavailable";
		debugButton.src = "images/debug-button-disabled.png";
	}
}