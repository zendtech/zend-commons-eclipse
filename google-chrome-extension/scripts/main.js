if (chrome.extension) {
	chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
		if (request.method == "showNotifications") {
			if (request.requests) {
				addRequests(request.requests);
				populateZniffingUrl(request.requests[request.requests.length - 1].url);
			} else {
				addRequest(request.request);
				populateZniffingUrl(request.request.url);
			}
		} else if (request.method == "resetRequests") {
			resetEvents();
		}
	});
}

function populateZniffingUrl(url) {
	$('#zniffing-url').text(url);
	
	var topMenuWidth = parseInt($('#main-top-menu').css('width'));
	var actualWidth = topMenuWidth - 500;
	
	if (actualWidth < 0) {
		actualWidth = 0;
	}
	
	$('#zniffing-url').truncate({
	    width: actualWidth,
	    token: '&hellip;',
	    center: true,
	});
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
		$('#codetracing-button').bind('click', [container, codetracing], openCodeTracingSnapshotEvent);
		codetracingButton.title = "Open code tracing in Studio";
		codetracingButton.src = "images/codetracing.png";
	} else {
		codetracingButton.onclick = '';
		codetracingButton.title = "Code tracing is unavailable";
		codetracingButton.src = "images/codetracing-disabled.png";
	}
	
	debugButton = $('#debug-button').get(0);
	if (eventId && issueId) {
		$('#debug-button').bind('click', [container, eventId, issueId], openTunnelAndDebugEvent);
		debugButton.title = "Debug event in Studio";
		debugButton.src = "images/debug-button.png";
	} else {
		debugButton.onclick = '';
		debugButton.title = "Debugging is unavailable";
		debugButton.src = "images/debug-button-disabled.png";
	}
}

function truncateEventUrls() {
	var eventTitleUrl = $('.event-title span.event-url');
	eventTitleUrl.each(function(index, element) {
		var eventTitle = $('.event-title');
		var eventTitleWidth = parseInt(eventTitle.css('width'));
		$(element).html(element.title);
		$(element).truncate({
			width: eventTitleWidth - 23,
			token: '&hellip;',
			center: true,
		});
	});
	return true;
}

function copySizeToPane() {
	var pane = arguments[1];
	var css = arguments[2];
	var newHeight = parseInt(css.css.height);
	var headlineHeight = parseInt(pane.find('.section-headline').css('height'));
	pane.find('.section-body').css('height', newHeight - headlineHeight);
	return true;
}