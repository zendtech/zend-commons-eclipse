function populateEvents(params) {
	var events = getEvents();
	populateEventsList(events);
	switchEvent(events.length - 1);
}

function populateEventsList(params) {
	jQuery.each(params.reverse(), function(index,element) {
		var event = '';
		if (index == params.length - 1) {
			event = '<li id="selected-event" onClick="switchEvent(' + index + ')">';
		} else {
			event = '<li onClick="switchEvent(' + index + ')">';
		}
		
		event += '<div class="event-title">' +
				 '<div class="studio-icon"></div>' + element.title + '</div><ul>';
		
		jQuery.each(element.events, function(eventIndex, eventElement) {
			event += '<li><div class="event-type" id="' + getEventTypeIcon(eventElement.type) + '"></div>' + 
					 '<div class="event-type" id="debug-small"></div>' + eventElement.name + '</li>'; 
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

function switchEvent(index) {
	var events = getEvents();
	console.log(index);
	event = events[index];
	
	populateSuperglobals(event.get, event.post, event.cookie, event.server, event.session);
}