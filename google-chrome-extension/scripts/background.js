// API message to check if a given container is the user container
chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
    if (request.method == "isValidContainer") {
    	sendResponse({data: searchConatiner(request.key)});
    } else if (request.method == "showNotifications") {
    	org.zend.showNotification();
    	sendResponse({ status: 'done' }); 
    } else if (request.method == "signout") {
    	org.zend.signout();
    	sendResponse({ status: 'done' }); 
    } else {
    	sendResponse({ }); 
    }
});


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
    	delete localStorage['projectxsess'];
    	delete localStorage['containers_length'];
    	setSessionId(null);
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

function openEvents() {
	chrome.tabs.getSelected(null, function(tab) {
		chrome.tabs.sendRequest(tab.id, {
			details : "openEvents"
		});
	});
}

function windowClosed() {
	org.zend.isNotificationVisible = false;	
}
