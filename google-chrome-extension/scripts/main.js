function populateZniffingUrl(url) {
	$('#zniffing-url').text(url);
}
if (chrome.extension) {
	chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
		if (request.method == "events") {
			
			console.log(request);
			
			var newRequests = {url: 'mypass/user/new_login',
					events : []};
			
			for (i in request.events) {
				var ev = request.events[i];
				var newEvent = {
						name : "name",
						severity : ev.severity,
						type : ev.type,
						description : ev.description,
						get : ev['super-globals'].get,
						post : ev['super-globals'].post, 
						cookie : ev['super-globals'].cookie,
						server : ev['super-globals'].server,
						session : ev['super-globals'].session,
						backtrace : ev.backtrace
				};
				newRequests.events.push(newEvent);
				
			}
			addRequest(newRequests);
		}
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

function showHelp() {
	$('#help-content').modal({opacity: 0, modal: false, autoPosition: true, position: [10, 10]});
}