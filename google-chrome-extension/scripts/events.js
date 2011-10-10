function populateRequest(index, request) {
	var event = '<li id="request_' + index + '">';
	
	event += '<div class="event-title">' +
			 '<div class="studio-icon"></div>' + request.url + '</div><ul>';
	
	jQuery.each(request.events, function(eventIndex, eventElement) {
		event += '<li><div class="event-type" id="' + getEventTypeIcon(eventElement.severity) + '"></div>' + 
				 '<div class="event-type" id="debug-small"></div>' +
				 '<div class="event-type-desc" id="event_' + eventIndex + '" onClick="switchEvent(' + index + ', ' + eventIndex + ')">' +  eventElement.name + '</div></li>'; 
	});
	
	event += '</ul></li>';
	
	if (index == 0) {
		$('.outer-west ul').html(event);
	} else {
		$('.outer-west > ul > li:last').after(event);
	}
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
	jQuery.each(requests, function(index, element) {
		addRequest(element);
	});
}

function addRequest(request) {
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
}

function getRequests() {
	return window.requests;
}

function switchEvent(index, eventIndex) {
	var requests = getRequests();
	event = requests[index].events[eventIndex];
	
	jQuery.each($('#events-list > li'), function(index, element) {
		$(element).removeClass('selected-request');
	});
	
	jQuery.each($('.event-type-desc'), function(index, element) {
		$(element).removeClass('selected-event');
	});
	
	$('#request_' + index).addClass('selected-request');
	$('#request_' + index + ' #event_' + eventIndex).addClass('selected-event');
	
	populateSuperglobals(event.get, event.post, event.cookie, event.server, event.session);
	populateBacktrace(event.backtrace);
	populateDescription(event.type, event.description);
}