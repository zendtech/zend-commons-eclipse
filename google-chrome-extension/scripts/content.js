var ZDE_DetectPort = 20080; 

var org = org || {};
org.zend = {

	isValidDomain : function(domain) {
		return domain == "devpaas.zend.com" || domain == "projectx.zend.com"
				|| domain == "my.phpcloud.com";
	},

	getMonitorRequestId : function(name) {
		var prefix = "ZENDMONITORURL";
		var length = prefix.length;

		// is valid request id
		var arrcookies = document.cookie.split(";");
		for ( var i = 0; i < arrcookies.length; i++) {
			name = arrcookies[i].substr(0, arrcookies[i].indexOf("="));
			name = trim(name);
			if (name.length > length && name.substr(0, length) == prefix) {
				return name.substr(length);
			}
		}
		return null;
	},
	
	/**
	 * Chrome-Studio communication related methods
	 */
	studio : {
		/**
		 * Opens SSH tunnel in Zend Studio
		 */
		enableSshTunnel : function (){
			try {
				var url = "http://127.0.0.1:28029/org.zend.php.zendserver.deployment.debug.openSshTunnel?container="+containerName;
				var rf = new XMLHttpRequest();
				rf.open("GET", url, false);
				rf.send(null);
				if (rf.status!=200)
					return false;
				return rf.responseText;

			} catch(e) { 
				console.log(e);
				return false; 
			}
		},
		
		/**
		 * Opens AMF file in Zend Studio
		 */
		openCodeTracingSnapshot : function (amfid) {
			
			var success = function(response) {
				
				
				try {
					var url = "http://127.0.0.1:28029/org.zend.php.zendserver.deployment.ui.OpenCodeTracingSnapshot";
					var rf = new XMLHttpRequest();
					rf.open("POST", url, false);
					rf.send(response);
					if (rf.status!=200)
						return false;
					return rf.responseText;

				} catch(e) { 
					console.log(e);
					return false; 
				}
			};
			var error = function(error) {
				console.log(error);
			};
			
			downloadAmf(containerName, amfid, success, error);
		}
	}
};


function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

function isValidDomainResponseFunc(response) {
	if (response.data != -1) {
		requestId = org.zend.getMonitorRequestId(document.cookie);
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
				
				var newRequest = {url: 'url',
						container : containerName,
						codeTracing : org.zend.codeTraceId,
						events : []};
				
				for (i in events) {
					var ev = events[i];
					
					var params=ev['debug-url'].split("/")[5].split("&");
					var issueId = params[0].split("=")[1];;
					var groupId = params[1].split("=")[1];
					
					var newEvent = {
							name : "name",
							severity : ev.severity,
							type : ev.type,
							issueId : issueId,
							eventId : groupId,
							description : ev.description,
							get : ev['super-globals'].get,
							post : ev['super-globals'].post, 
							cookie : ev['super-globals'].cookie,
							server : ev['super-globals'].server,
							session : ev['super-globals'].session,
							backtrace : ev.backtrace
					};
					newRequest.events.push(newEvent);
					
				}
				
				chrome.extension.sendRequest({
					method : "showNotifications",
					request : newRequest
				}, function(response) {
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
	}


// search for the magic cookie....
domain = window.location.hostname;
dot = domain.indexOf('.');
if (dot != -1 && org.zend.isValidDomain(domain.substr(dot + 1))) {
	// is container name included in the list of containers
	containerName = domain.substr(0, dot);
	chrome.extension.sendRequest({
		method : "isValidContainer",
		key : containerName
	}, isValidDomainResponseFunc);
}

var events = [];
var all_events = [];


function debugEvent(url) {
	var settings = getZdeSettingString(ZDE_DetectPort);
	if (!settings) {
		alert("Can't connect to Zend Studio. Make sure that it's running.");
	//	return;
	}
	
	org.zend.studio.enableSshTunnel();
	
	console.log('debug '+org.zend.sessionid);
	var params=url.split("/")[5].split("&");
	var issueId = params[0].split("=")[1];;
	var groupId = params[1].split("=")[1];
	startDebug(containerName, issueId, groupId, function() { }, function() { });
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