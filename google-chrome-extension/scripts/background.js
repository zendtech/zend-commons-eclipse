// API message to check if a given container is the user container
chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
    if (request.method == "isValidContainer") {
    	sendResponse({data: searchConatiner(request.key)});
    } else if (request.method == "showNotifications") {
    	org.zend.showNotification();
    	org.zend.request = request;
    	chrome.browserAction.setPopup({popup:"summary.html"});
    	sendResponse({ status: 'done' });
    	updateSummary(request);
    	sendSummary();
    } else if (request.method == "requestSummary") { 
    	sendSummary();
    } else if (request.method == "signout") {
    	org.zend.signout();
    	sendResponse({ status: 'done' }); 
    } else {
    	sendResponse({ }); 
    }
});

var zend = zend || {};
zend.summary = {
	requests : 0,
	events : 0,
	critical : 0,
	warning :0,
	normal : 0
};

function updateSummary(newReq) {
	zend.summary.requests++;
	zend.summary.url = newReq.request.url;
	for (var i in newReq.request.events) {
		zend.summary.events++;
		var ev = newReq.request.events[i];
		switch (ev.severity) {
			case 'critical' : zend.summary.critical++; break;
			case'warning' : zend.summary.warning++; break;
			case 'normal' : zend.summary.normal++; break;
			
		}
	}
}

function sendSummary() {
	chrome.extension.sendRequest({method : "updateSummary", summary : zend.summary}, function(response){});
}


var org = org || {};
org.zend = {

	isNotificationVisible : false,

	showNotification : function() {
		if (org.zend.isNotificationVisible) {
			return;
		}
		org.zend.isNotificationVisible = true;
		// Create an HTML notification:
		var notification = webkitNotifications
				.createHTMLNotification('infobar.html');// 
		// Then show the notification.
		notification.show();

	},
	
	signout : function() {
    	delete localStorage["username"];
    	delete localStorage['phpcloudsess'];
    	delete localStorage['containers_length'];
    	setSessionId(null);
    	chrome.browserAction.setPopup({popup:"popup.html"});
	}
};

function searchConatiner (container) {
	length = parseInt(localStorage['containers_length']);
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

tabOpen = false;
function openEvents() {
	if (tabOpen) {
		chrome.tabs.get(tabOpen, function (tab) {
			if (tab === undefined) {
				tabOpen = undefined;
				openEvents();
			}
		});
	} else {
		chrome.tabs.create({'url': chrome.extension.getURL('main.html')}, function(tab) {
			tabOpen = tab.id;
		});
	}
	org.zend.request.method = "events";
	chrome.extension.sendRequest(org.zend.request, function(response){});
}

function windowClosed() {
	org.zend.isNotificationVisible = false;	
}
