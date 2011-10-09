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
					return;
				}

				// copy code-tracing id
				var id = response.requestSummary['code-tracing'];
				if (id) {
					var matches = /amf=(.*)[&]?/.exec(id);
					org.zend.codeTraceId = matches[1]; // match[0] is "amfid=.....", match[1] is "....." 
				}

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

				if (hasFader()) {
					addSlides();
					all_events = all_events.concat(events);
					events = [];
					updateSummary(all_events);
				}

				chrome.extension.sendRequest({
					method : "showNotifications"
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
var slideshow;

chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
	if (request.details == "openEvents") {
		if (!hasFader()) {
			createFader();
		} 
		addSlides();
		all_events = all_events.concat(events);
		events = [];
		updateSummary(all_events);
	}
});

function updateSummary(events) {
	var summaryDom = $("#zend_ex_summary").html("<div id='totalEvents'>&nbsp;</div>" +
			"<div id='superglobalcookie'></div>" +
			"<div id='superglobalget'></div>" +
			"<div id='superglobalpost'></div>" +
			"<div id='superglobalserver'></div>" +
			"<div id='superglobalsession'></div>");
	$("#totalEvents", summaryDom).html("total " + events.length + " events.");
	
	// take super-globals from first event, but they should be equal for all events.
	var superGlobals = events[0]['super-globals'];
	
	var prettyTable = function(array) {
		var html = "<table>";
		for (entry in array) {
			html += "<tr><td>"+array[entry].key+"</td><td>"+array[entry].value+"</td></tr>";
		}
		html += "</table>";
		return html;
	};
	
	$("#superglobalcookie", summaryDom).html("Cookies:"+prettyTable(superGlobals.cookie.cookie));
	$("#superglobalget", summaryDom).html("Get:"+prettyTable(superGlobals.get.get));
	$("#superglobalpost", summaryDom).html("Post:"+prettyTable(superGlobals.post.post));
	$("#superglobalserver", summaryDom).html("Server:"+prettyTable(superGlobals.server.server));
	$("#superglobalsession", summaryDom).html("Session:"+prettyTable(superGlobals.session.session));
}

function addSlides() {
	var slidesUl = $("#slides");
	for ( var i = 0; i < events.length; i++) {
		// todo: prettify
		var slideLiMsg = '<h1>' + events[i].type +  '</h1><p>' + events[i].description +  '</p>' + '<a id="debugEvent" href="#">Debug Event</a><br/>';
		if (org.zend.codeTraceId) {
			slideLiMsg += '<a href="#" id="traceEvent">Open Code Tracing Snapshot</a>';
		}
		slideLiMsg += '<p>' + events[i].severity + '</p>';
		var slideLi = $("<li>").html(slideLiMsg);
		$('#debugEvent', slideLi).click((function(url) {
			return function() {
				debugEvent(url);
			};
		})(events[i]['debug-url']));
		
		$('#traceEvent', slideLi).click(function(url) {
			org.zend.studio.openCodeTracingSnapshot(org.zend.codeTraceId);
		});
		
		slideLi.addClass("zend_content");
		slidesUl.append(slideLi);

		slideshow.l++;
	}

	var paginationUl = $("#pagination");
	for ( var i = 0; i < events.length; i++) {
		j = i + 1;
		var paginationLi = $("<li>").html(j);
		var aff = (function(n) {
			return function() {
				slideshow.pos(n);
			};
		})(j);
		paginationLi.click(aff);
		paginationUl.append(paginationLi);
	}
}

function hasFader() {
	return document.getElementById("wrapper") != undefined;
}

function createFader() {
	// slides
	var divSlides = getSlidesDiv();

	// pagination
	var divPagination = getPaginationDiv();

	// wrapper
	var div = $(document.createElement('div'));
	div.attr("id", "wrapper");
	div.append(divSlides);
	div.append(divPagination);

	// add wrapper
	$("body").prepend(div);

	// <div id="fade" class="black_overlay"></div>
	var fadeDiv = $(document.createElement('div'));
	fadeDiv.attr("id", "fade");
	fadeDiv.attr("class", "black_overlay");
	$("body").prepend(fadeDiv);

	// init slideshow
	slideshow = new TINY.fader.fade('slideshow', {
		id : 'slides',
		auto : 0,
		resume : false,
		navid : 'pagination',
		activeclass : 'current',
		visible : true,
		position : 0
	});
}

function getSlidesDiv() {
	// slides div

	var divLeft = $(document.createElement('div'));
	divLeft.attr("class", "sliderbutton");
	divLeft.html('<img src="' + chrome.extension.getURL("images/left.gif")
			+ '" width="32" height="38" alt="Previous" />');
	$("img", divLeft).click(function() {
		slideshow.move(-1);
	});

	var divRight = $(document.createElement('div'));
	divRight.attr("class", "sliderbutton");
	divRight.html('<img src="' + chrome.extension.getURL("images/right.gif")
			+ '" width="32" height="38" alt="Next" />');
	$("img", divRight).click(function() {
		slideshow.move(1);
	});

	var divCenter = $(document.createElement('div'));
	divCenter.attr("id", "slideshow");

	var slidesUl = $(document.createElement('ul'));
	slidesUl.attr("id", "slides");

	// build summary page
	var slideLi = $("<li>").html(
			'<h1>Summary</h1><div id="zend_ex_summary"></div>');
	slideLi.addClass("zend_content");
	slidesUl.append(slideLi);
	divCenter.append(slidesUl);

	var divSlides = $(document.createElement('div'));
	divSlides.append(divLeft);
	divSlides.append(divCenter);
	divSlides.append(divRight);
	return divSlides;
}

function getPaginationDiv() {

	var paginationUl = $(document.createElement('ul'));
	paginationUl.attr("id", "pagination");
	paginationUl.attr("class", "pagination");

	// build summary page
	var paginationLi = $("<li>");
	paginationLi.click(function() {
		slideshow.pos(0);
	});
	paginationLi.html("Summary");
	paginationUl.append(paginationLi);

	return paginationUl;
}

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