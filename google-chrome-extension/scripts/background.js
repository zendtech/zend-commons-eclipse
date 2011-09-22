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
		var notification = webkitNotifications
				.createHTMLNotification('infobar.html');
		notification.onclose = function() {
			org.zend.googlechrome.isNotificationVisible = false;
		};
		// Then show the notification.
		notification.show();

	}
};

/**
 * Tests for Zend Developer Cloud domain
 * 
 * @param domain
 * @returns
 */
function isZendDevCloudDomain(domain) {
	return domain == "devpaas.zend.com" || domain == "projectx.zend.com" || domain == "phpcloud.com";
}

/**
 * the URL of the monitor event, for example
 * ZENDMONITORURLB58BC4B6D21892DCB18B484854AA257C, returns
 * B58BC4B6D21892DCB18B484854AA257C
 * 
 * @param name
 * @returns
 */
function getMonitorRequestId(name) {
	prefix = "ZENDMONITORURL";
	length = prefix.length;
	if (name.length < (length + 1) || name.substr(0, length) != prefix) {
		return null;
	}
	return name.substr(length);
}

function listener(info) {
	// is the right domain
	domain = info.cookie.domain;
	dot = domain.indexOf('.');
	if (dot == -1 || !isZendDevCloudDomain(domain.substr(dot+1))) {
		return;
	}
	
	// is valid request id 
	requestId = getMonitorRequestId(info.cookie.name);
	if (requestId == null) {
		return;
	}

	// is container name included in the list of containers
	containerName = domain.substr(0, dot);
	if (searchConatiner(containerName) == -1) {
		return;
	}

	requestSummary(containerName, requestId, function() { alert("hello1"); }, function() { alert("hello2"); });
}

function searchConatiner (container) {
	var length = parseInt(localStorage['containers_length']);
	if (length == 0) {
		return -1;
	} 
	for ( var i = 0; i < length; i++) {
		if (localStorage['containers' + i] == container) {
			return i;
		}
	}
    return -1;
}


chrome.cookies.onChanged.addListener(listener);

//
// chrome.experimental.webRequest.onResponseStarted.addListener(function(details)
// {
// var res_headers = details.responseHeaders;
// if (res_headers != undefined) {
// for ( var h in res_headers) {
// if (res_headers[h].name == "X-Zend-Monitor") {
// var xhr = new XMLHttpRequest();
// xhr.open("GET", res_headers[h].value, true);
// xhr.onreadystatechange = function() {
// if (xhr.readyState == 4) {
// try {
// resp = JSON.parse(xhr.responseText);
// } catch (ex) {
// // didn't get the response - host might be unreachable. TODO maybe show an
// error?
// return;
// };
// chrome.tabs.getSelected(null, function(tab) {
// if (resp.events.length != 0) {
// chrome.tabs.sendRequest(tab.id, resp);
// org.zend.googlechrome.showNotification();
// } else {
// chrome.extension.getBackgroundPage().console.log(xhr.responseText);
// // show a "clean" icon
// chrome.pageAction.setIcon({
// "tabId" : tab.id,
// "path" : "images/green.jpg"
// });
// }
// });
// }
// };
// xhr.send();
// }
// }
// }
// }, null, [ "responseHeaders" ]);

function openEvents() {
	chrome.tabs.insertCSS(null, {
		file : "style/style.css"
	}, function() {
		chrome.tabs.executeScript(null, {
			file : "scripts/tinyfader.js"
		}, function() {
			chrome.tabs.executeScript(null, {
				file : "scripts/jquery.js"
			}, function() {
				chrome.tabs.getSelected(null, function(tab) {
					chrome.tabs.sendRequest(tab.id, {
						details : "openEvents"
					});
				});
			});
		});
	});
}
