function populateEvents(params) {
	var events = getEvents();
	populateEventsList(events);
	switchEvent(events.length - 1, 0);
}

function populateEventsList(params) {
	jQuery.each(params.reverse(), function(index,element) {
		var event = '<li id="request_' + index + '">';
		
		event += '<div class="event-title">' +
				 '<div class="studio-icon"></div>' + element.url + '</div><ul>';
		
		jQuery.each(element.events, function(eventIndex, eventElement) {
			event += '<li><div class="event-type" id="' + getEventTypeIcon(eventElement.type) + '"></div>' + 
					 '<div class="event-type" id="debug-small"></div>' +
					 '<div class="event-type-desc" id="event_' + eventIndex + '" onClick="switchEvent(' + index + ', ' + eventIndex + ')">"' +  eventElement.name + '</div></li>'; 
		});
		
		event += '</ul></li>';
		
		if (index == 0) {
			$('.outer-west ul').html(event);
		} else {
			$('.outer-west ul li:first').before(event);
		}
	});
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

function setEvents(events) {
	window.events = events;
}

function getEvents() {
	return window.events;
}

function switchEvent(index, eventIndex) {
	var events = getEvents();
	event = events[index].events[eventIndex];
	
	jQuery.each($('#events-list > li'), function(index, element) {
		$(element).removeClass('selected-request');
	});
	
	jQuery.each($('.event-type-desc'), function(index, element) {
		$(element).removeClass('selected-event');
	});
	
	$('#request_' + index).addClass('selected-request');
	$('#request_' + index + ' #event_' + eventIndex).addClass('selected-event');
	
	populateSuperglobals(event.get, event.post, event.cookie, event.server, event.session);
}