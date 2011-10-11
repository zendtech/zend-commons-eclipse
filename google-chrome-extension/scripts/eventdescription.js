function populateDescription(errorType, errorMessage) {
	$('.event-description').show();
	$('.event-description .error-type').html(errorType);
	$('.event-description .description').html(errorMessage);
}

function resetDescription() {
	$('.event-description').hide();
}