chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
	if (!zend.options.isNotificationsEnabled()) {
		return;
	}
	
	if (request.method == "ContentEvaluateCookie") { // request from content.js to evaluate a cookie
		var cn = searchContainer(request.domain);
		if (cn) {
			resetRequests();
			zend.path = request.path;
		}
	} else if (request.method == "showNotifications") { // request to show notification bar about new events
		org.zend.showNotification();
    	addRequests(request);
    } else if (request.method == "requestSummary") { // request fresh summary data
    	sendSummary();
    } else if (request.method == "signout") { // request to signout
    	org.zend.signout();
    	sendResponse({ status: 'done' }); 
    } else {
    	sendResponse({ }); 
    }
});

chrome.cookies.onChanged.addListener(function(cookieInfo) {
	if (!zend.options.isNotificationsEnabled()) {
		return;
	}
	
	if (cookieInfo.cause === 'expired') {
		return;
	}
	
	var domain = cookieInfo.cookie.domain;
	var cn = searchContainer(domain);
	if (cn) {
		zend.path = cookieInfo.cookie.path;
		containerName = cn;
		searchEvents(cookieInfo.cookie.name+"="+cookieInfo.cookie.value);
	}		
});

function addRequests(request) {
	zend.lastRequest = request;
	zend.allRequests.push(request.request);
	updateSummary(request);
	sendSummary();
}

function resetRequests() {
	zend.summary = {
			requests : 0,
			events : 0,
			critical : 0,
			warning :0,
			normal : 0
	};
	zend.allRequests = [];
	zend.resetRequests = true;
}

var zend = zend || {};
resetRequests();

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

	showNotification : function() {
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

function searchContainer (domain) {
	var dot = domain.indexOf('.');
	var containerName = undefined;
	if (dot != -1) {
		// is container name included in the list of containers
		containerName = domain.substr(0, dot);
	}
	
	length = parseInt(localStorage['containers_length']);
	if (length == 0) {
		return false;
	} 
	for ( var i = 0; i < length; i++) {
		if (localStorage['containers' + i] == containerName) {
			return containerName;
		}
	}
    return false;
}

tabOpen = false;
function openEvents() {
	if (tabOpen) {
		chrome.tabs.get(tabOpen, function (tab) {
			if (tab) { // tab is open
				chrome.extension.sendRequest(zend.lastRequest, function(response){});
				chrome.tabs.update(tabOpen, {selected : true});
			} else { // tab was closed, need to re-open it
				tabOpen = undefined;
				openEvents();
			}
		});
	} else { // tab was never opened
		chrome.tabs.create({'url': chrome.extension.getURL('main.html')}, function(tab) {
			tabOpen = tab.id;
		});
		clearActionBadge();
	}
}

function neverForThisApplication() {
	
}

chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
	if (tabId !== tabOpen) {
		return;
	}
	
	if (changeInfo.status === 'complete') {
		chrome.extension.sendRequest({method : "backgroundPublishRequests", requests : zend.allRequests }, function(response){});
	}
});

chrome.tabs.onSelectionChanged.addListener(function (tabId, changeInfo) {
	if (tabId === tabOpen) {
		clearActionBadge();
	}
});

function setActionBadge() {
	var badgeColor = [0,153,191,255]; // default is blue for 'normal' events
	if (zend.summary.critical > 0) {
		badgeColor = [180,33,10,255]; // red
	} else if (zend.summary.warning > 0) {
		badgeColor = [237,144,46,255]; // yellow
	}
	
	chrome.browserAction.setBadgeBackgroundColor({color: badgeColor});
	chrome.browserAction.setBadgeText({text : ''+zend.summary.events});
}

function clearActionBadge() {
	chrome.browserAction.setBadgeText({text : ''});
}

function windowClosed() {
	org.zend.isNotificationVisible = false;	
}


function isValidDomain(domain) {
	return domain == "devpaas.zend.com" || domain == "projectx.zend.com"
			|| domain == "my.phpcloud.com";
}

function getMonitorRequestId(cookie) {
	var prefix = "ZENDMONITORURL";
	var length = prefix.length;

	// is valid request id
	var arrcookies = cookie.split(";");
	for ( var i = 0; i < arrcookies.length; i++) {
		name = arrcookies[i].substr(0, arrcookies[i].indexOf("="));
		name = trim(name);
		if (name.length > length && name.substr(0, length) == prefix) {
			return name.substr(length);
		}
	}
	return null;
};

function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

function flatten(kvArray) {
	var result = {};
	for (var i in kvArray) {
		var kvElem = kvArray[i];
		result[kvElem.key] = kvElem.value;
	}
	return result;
}

function searchEvents(cookie) {
	requestId = getMonitorRequestId(cookie);
	if (requestId != null) {
		var summary_success = function(response) {
			var c = response.requestSummary["events-count"];
			if (c == 0) {
				//console.log('no events');
				return;
			}

			// copy code-tracing id
			var id = response.requestSummary['code-tracing'];
			if (id) {
				var matches = /amf=(.*)[&]?/.exec(id);
				org.zend.codeTraceId = matches[1]; // match[0] is "amfid=.....", match[1] is "....." 
			}

			var events = [];
			// copy events
			if (c == 1) {
				var e = response.requestSummary.events.event;
				events.push(e);
			} else {
				for ( var i = 0; i < c; i++) {
					var e = response.requestSummary.events.event[i];
					events.push(e);
				}
			}
			
			var newRequest = {url: zend.path,
					container : containerName,
					codeTracing : org.zend.codeTraceId,
					events : []};
			
			for (var i in events) {
				var ev = events[i];
				
				var params=ev['debug-url'].split("/")[5].split("&");
				var issueId = params[0].split("=")[1];;
				var groupId = params[1].split("=")[1];
				
				var severity = ev.severity;
				if (severity == 'severe') {
					severity = 'critical';
				}
				
				var backtrace = [];
				if (ev.backtrace && ev.backtrace.backtrace) {
					for (var j = 0; j < ev.backtrace.backtrace.length; j++) { // 2 - backtrace max length
						var entry ={
								method : ev.backtrace.backtrace[j]['function'],
								filename : ev.backtrace.backtrace[j].file,
								line : ev.backtrace.backtrace[j].line
						};
						var classname = ev.backtrace.backtrace[j]['class'];
						if (typeof classname === 'string') {
							entry['class'] = classname;
						}
						backtrace.push(entry);
					}
				}
				
				var newEvent = {
						name : ev.type,
						severity : severity,
						type : ev.type,
						issueId : issueId,
						eventId : groupId,
						description : ev.description,
						get : flatten(ev['super-globals'].get.get),
						post : flatten(ev['super-globals'].post.post), 
						cookie : flatten(ev['super-globals'].cookie.cookie),
						server : flatten(ev['super-globals'].server.server),
						session : flatten(ev['super-globals'].session.session),
						backtrace : backtrace
				};
				newRequest.events.push(newEvent);
				
			}
			
			var notfEvent = {
				method : "backgroundPublishRequests",
				request : newRequest
			};
			if (zend.resetRequests) {
				notfEvent.resetRequests = true;
				zend.resetRequests = undefined;
			}
			org.zend.showNotification();
			addRequests(notfEvent);
			setActionBadge();
		};

		var summary_error = function(message) {
			chrome.extension.sendRequest({
				method : "signout"
			}, function(response) {
				chrome.extension.sendRequest({
					method : "refreshPopupContent"
				}, function(response) {
				});
			});
		};

		requestSummary(containerName, requestId,
				summary_success, summary_error);
	}
}

function getZdeSettingString(ZDE_DetectPort){
	try {
		var url = "http://127.0.0.1:"+ZDE_DetectPort;
		var rf = new XMLHttpRequest();
		rf.open("GET", url, false);
		// to prevent leaks see Mozilla bug #206947
		rf.overrideMimeType("text/xml");
		rf.send(null);
		if (rf.status!=200)
			return false;
		return rf.responseText;

	} catch(e) {
		console.log(e);
		return false; 
	}
}