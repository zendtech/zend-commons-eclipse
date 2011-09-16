var org = org || {};
org.zend = org.zend || {};
org.zend.googlechrome = {
	
	isNotificationVisible : false,
		
	showNotification : function() {
		if (org.zend.googlechrome.isNotificationVisible) {
			return;
		}
		org.zend.googlechrome.isNotificationVisible = true;
		
		// Create an HTML notification:
		var notification = webkitNotifications.createHTMLNotification(
		  'infobar.html'  
		);
		notification.onclose = function() {
			org.zend.googlechrome.isNotificationVisible = false;
		};
		// Then show the notification.
		notification.show();
		
	}
};

chrome.experimental.webRequest.onResponseStarted.addListener(function(details) {
	var res_headers = details.responseHeaders;
	if (res_headers != undefined) {
		for ( var h in res_headers) {
			if (res_headers[h].name == "X-Zend-Monitor") {
				var xhr = new XMLHttpRequest();
				xhr.open("GET", res_headers[h].value, true);
				xhr.onreadystatechange = function() {
					if (xhr.readyState == 4) {
						try {
							resp = JSON.parse(xhr.responseText);							
						} catch (ex) {
							// didn't get the response - host might be unreachable. TODO maybe show an error?
							return; 
						};
						chrome.tabs.getSelected(null, function(tab) {
							if (resp.events.length != 0) {
								chrome.tabs.sendRequest(tab.id, resp);
								org.zend.googlechrome.showNotification();
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
		chrome.tabs.executeScript(null, { file: "scripts/tinyfader.js" }, function() {
			chrome.tabs.executeScript(null, { file: "scripts/jquery.js" }, function() {
				chrome.tabs.getSelected(null, function(tab) {
					chrome.tabs.sendRequest(tab.id, {details: "openEvents"});
				});
			});
		});
	});
}
 