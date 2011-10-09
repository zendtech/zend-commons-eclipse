function populateEvents(params) {
	var params = [
	             {title:'mypass/user/login', 
				  events: [
				           {name: 'Fatal PHP Error', type: 'critical'},
				           {name: 'Event X', type: 'warning'},
				           {name: 'Event Y', type: 'normal'}
				  ]
				 },
				 {title:'mypass/user/login2', 
					  events: [
				           {name: 'Fatal PHP Error', type: 'critical'},
				           {name: 'Event X', type: 'warning'},
				           {name: 'Event Y', type: 'normal'}
				  ]
				 }
		];
	
	populateEventsList(params);
}

function populateEventsList(params) {
	jQuery.each(params.reverse(), function(index,element) {
		var event = '';
		if (index == params.length - 1) {
			event = '<li id="selected-event">';
		} else {
			event = '<li>';
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