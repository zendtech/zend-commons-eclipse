chrome.experimental.webRequest.onResponseStarted.addListener(function(details) {
	var res_headers = details.responseHeaders;
	if (res_headers != undefined) {
		for ( var h in res_headers) {
			if (res_headers[h].name == "X-Zend-Monitor") {
				var xhr = new XMLHttpRequest();
				xhr.open("GET", res_headers[h].value, true);
				xhr.onreadystatechange = function() {
					if (xhr.readyState == 4) {
						resp = JSON.parse(xhr.responseText);
						chrome.tabs.getSelected(null, function(tab) {
							if (resp.events.length != 0) {
								chrome.experimental.infobars.show({
									"tabId" : tab.id,
									"path" : "infobar.html"
								}, function() {
									chrome.tabs.sendRequest(tab.id, resp);
								});
							} else {
								chrome.extension.getBackgroundPage().console.log(xhr.responseText);
								// show a "clean" icon
								chrome.pageAction.setIcon({
									"tabId" : tab.id,
									"path" : "images/green.jpg"
								});
							}
						});
					}
				};
				xhr.send();
			}
		}
	}
}, null, [ "responseHeaders" ]);
