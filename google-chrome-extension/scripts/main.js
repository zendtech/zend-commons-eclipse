function populateZniffingUrl(url) {
	$('#zniffing-url').text(url);
}

chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
	if (request.method == "events") {
		addRequests(request.events);
	}
});

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