function populateSuperglobals(getParams, postParams, cookieParams, serverParams, sessionParams) {
	$('.super-global-table').hide();
	$('.super-global-table tbody tr').remove();
	$('label.has-content-label').hide();
	$('a.toggler').hide();
	$('label.no-content-label').show();
	
	populateTable('#get-super-global', getParams);
	populateTable('#post-super-global', postParams);
	populateTable('#cookie-super-global', cookieParams);
	populateTable('#server-super-global', serverParams);
	populateTable('#session-super-global', sessionParams);
}

function populateTable(container, params) {
	jQuery.each(params, function(index,element) {
		if ($(container + ' table tbody tr:last').length) {
			$(container + ' table tbody tr:last').after('<tr><td>' + index + '</td><td>'+ element +'</td></tr>');
		} else {
			$(container + ' table tbody').html('<tr><td>' + index + '</td><td>'+ element +'</td></tr>');
		}
		$(container + ' table').show();
		$(container + ' label.no-content-label').hide();
		$(container + ' label.has-content-label').show();
		$(container + ' a.toggler').show();
	});
}