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
								// Or create an HTML notification:
								var notification = webkitNotifications.createHTMLNotification(
								  'infobar.html'  
								);
								// Then show the notification.
								notification.show();
								chrome.tabs.sendRequest(tab.id, resp);
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

function openEvents() {
	chrome.tabs.insertCSS(null, { file: "style/style.css" }, function() {
		chrome.tabs.executeScript(null, { file: "scripts/jquery.js" }, function() {
			chrome.tabs.executeScript(null, { file: "scripts/tinyfader.js" }, function() {
		    	chrome.tabs.getSelected(null, function(tab) {
		    		chrome.tabs.sendRequest(tab.id, {details: "now"});
		    	});
			});
		});
	});
}
 