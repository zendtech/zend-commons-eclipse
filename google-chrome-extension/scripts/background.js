chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
	if (request.method == "ContentEvaluateCookie") {
		var cn = searchConatiner(request.key);
		if (cn) {
			zend.path = request.path;
			containerName = cn;
			resetRequests();
			searchEvents(request.cookie);
		}
	} else if (request.method == "showNotifications") {
    	showNotifications(request);
    } else if (request.method == "requestSummary") { 
    	sendSummary();
    } else if (request.method == "signout") {
    	org.zend.signout();
    	sendResponse({ status: 'done' }); 
    } else {
    	sendResponse({ }); 
    }
});

chrome.cookies.onChanged.addListener(function(cookieInfo) {
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

function resetRequests() {
	zend.allRequests = [];
	chrome.extension.sendRequest({method:"resetRequests"});
}

function showNotifications(request) {
	org.zend.showNotification();
	zend.lastRequest = request;
	zend.allRequests.push(request.request);
	chrome.browserAction.setPopup({popup:"summary.html"});
	updateSummary(request);
	sendSummary();
}

var zend = zend || {};
zend.summary = {
	requests : 0,
	events : 0,
	critical : 0,
	warning :0,
	normal : 0
};
zend.allRequests = [];

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
			if (tab) {
				// empty, if tab is open, the event was already added. we only need to set focus
				chrome.tabs.update(tabOpen, {selected : true});
			} else {
				tabOpen = undefined;
				openEvents();
			}
		});
	} else {
		chrome.tabs.create({'url': chrome.extension.getURL('main.html')}, function(tab) {
			tabOpen = tab.id;
		});
	}
}

chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
	if (tabId !== tabOpen) {
		return;
	}
	
	if (changeInfo.status === 'complete') {
		chrome.extension.sendRequest({method : "showNotifications", requests : zend.allRequests }, function(response){});
	}
});

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
				console.log('no events');
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
			
			showNotifications({
				method : "showNotifications",
				request : newRequest
			});
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