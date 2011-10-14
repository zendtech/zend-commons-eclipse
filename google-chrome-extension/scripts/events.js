function populateRequest(index, request) {
	var autoSwitch = false;
	/// autoswitch (at the end of the process) only if we have no current events
	if (! $('.request-item').length) {
		autoSwitch = true;
	}
	
	var event = '<li id="request_' + index + '" class="new-event request-item">';
	
	event += '<div class="event-title no-wrap">';
	if (request.codeTracing) {
		event += '<img class="request-icon" src="images/studio.png" title="Open code tracing in Studio" onclick="openCodeTracingSnapshot(\'' + request.container + '\', \'' + request.codeTracing + '\');"/>';
	} else {
		event += '<img class="request-icon" src="images/studio-disabled.png" title="Code tracing is unavailable" />';
	}
	event += '<span class="event-url" title="'+request.url + '">'+request.url + '</span></div><ul>';
	
	var lastEventIndex = null;
	jQuery.each(request.events, function(eventIndex, eventElement) {
		event += '<li><div class="event-type" id="' + getEventTypeIcon(eventElement.severity) + '"></div>';
		event += '<span class="event-type-desc" id="event_' + eventIndex + '" onClick="switchEvent(' + index + ', ' + eventIndex + ')">' +  eventElement.name + '</span>';
		if (eventElement.eventId && eventElement.issueId) {
			event += ' <img class="event-icon" src="images/debug.gif" title="Debug this event" onclick="openTunnelAndDebug(\'' + request.container + '\', '+eventElement.eventId+', '+eventElement.issueId+')" />';
		} 
		event += "</li>";
		lastEventIndex = lastEventIndex == null ? eventIndex : lastEventIndex;
	});
	
	event += '</ul></li>';

	if (index == 0) {
		$('.outer-west ul').html(event);
	} else {
		$('.outer-west > ul > li:last').after(event);
	}
	
	$('.event-title span.event-url').truncate({
	    width: '235',
	    token: '&hellip;',
	    center: true,
	});
	
	if (autoSwitch) {
		switchEvent(index, lastEventIndex);
	}
}

function openTunnelAndDebugEvent(event) {
	return openTunnelAndDebug(event.data[0], event.data[1], event.data[2]);
}

function openTunnelAndDebug(container, eventId, issueId) {
	enableSshTunnel(container);
	//if (enableSshTunnel(container)) {
		startDebug(container, issueId, eventId, function() { }, function() { });
	//}
}

function getEventTypeIcon(type) {
	if (type == 'critical') {
		return 'critical-small';
	} else if (type == 'warning') {
		return 'warning-small';
	} else {
		return 'normal-small';
	}
}

function addRequests(requests) {
	if (window.lock) {
		return ;
	}
	
	jQuery.each(requests, function(index, element) {
		addRequest(element);
	});
}

function addRequest(request) {
	if (window.lock) {
		return ;
	}
	
	window.requests.push(request);
	
	var requests = getRequests();
	var requestsLength = requests.length;
	
	var totalEvents = 0;
	var totalCritical = 0;
	var totalWarning = 0;
	var totalNormal = 0;
	jQuery.each(requests, function(index, element) {
		totalEvents += element.events.length;
		jQuery.each(element.events, function(eventIndex, eventElement) {
			if (eventElement.severity == 'critical') {
				totalCritical++;
			} else if (eventElement.severity == 'warning') {
				totalWarning++;
			} else {
				totalNormal++;
			}
		});
	});
	
	populateRequest(requestsLength - 1, request);
	populateSummary('', requestsLength, totalEvents, totalCritical, totalWarning, totalNormal);
	document.title = 'Events: ' + totalCritical + ', ' + totalWarning + ', ' + totalNormal;
}

function getRequests() {
	return window.requests;
}

function switchEvent(index, eventIndex) {
	var requests = getRequests();
	var request = requests[index];
	event = requests[index].events[eventIndex];
	
	jQuery.each($('#events-list > li'), function(index, element) {
		$(element).removeClass('selected-request');
	});
	
	jQuery.each($('.event-type-desc'), function(index, element) {
		$(element).removeClass('selected-event');
	});
	
	$('#request_' + index).removeClass('new-event');
	$('#request_' + index).addClass('selected-request');
	$('#request_' + index + ' #event_' + eventIndex).addClass('selected-event');
	
	populateSuperglobals(event.get, event.post, event.cookie, event.server, event.session);
	populateBacktrace(event.backtrace);
	populateDescription(event.type, event.description);
	populateStudioButtons(request.container, request.codeTracing, event.eventId, event.issueId);
}

function enableSshTunnel (containerName) {
	try {
		var url = "http://127.0.0.1:28029/org.zend.php.zendserver.deployment.debug.openSshTunnel?container="+containerName;
		var rf = new XMLHttpRequest();
		rf.open("GET", url, false);
		rf.send(null);
		if (rf.status!=200)
			return false;
		return rf.responseText;

	} catch(e) { 
		alert("Cannot connect to Zend Studio. Make sure it's launched and not behind firewall");
		return false; 
	}
}

/**
 * Opens AMF file in Zend Studio
 */
function openCodeTracingSnapshotEvent(event) {
	
	return openCodeTracingSnapshot (event.data[0], event.data[1]);
}
function openCodeTracingSnapshot (containerName, amfid) {
	
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
			alert("Cannot connect to Zend Studio. Make sure it's launched and not behind firewall");
			return false; 
		}
	};
	var error = function(error) {
		console.log(error);
	};
	
	downloadAmf(containerName, amfid, success, error);
}

function resetEvents() {
	window.requests = new Array();
	
	populateSummary('', 0, 0, 0, 0, 0);
	document.title = 'Events: ' + 0 + ', ' + 0 + ', ' + 0;
	$('.outer-west ul').html('');
	
	$('.super-global-table').hide();
	$('.super-global-table tbody tr').remove();
	$('label.has-content-label').hide();
	$('a.toggler').hide();
	$('label.no-content-label').show();
	
	$('.stack-trace-list').html('');
	populateZniffingUrl('');
	
	populateStudioButtons('', 0, 0, 0);
	resetDescription();
}