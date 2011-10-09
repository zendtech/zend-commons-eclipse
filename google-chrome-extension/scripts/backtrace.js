function populateBacktrace(errorType, errorMessage, backtrace) {
	$('.stack-trace-error .error-type').html(errorType);
	$('.stack-trace-error .error-message').html(errorMessage);
	$('.stack-trace-list li').remove();
	
	$.each(backtrace, function(index, element) {
		var listItem = '<li><span class="method-name no-wrap">'+element.method+'</span> <span class="filename no-wrap">'+element.filename+'</span></li>';
		if ($('.stack-trace-list li').length) {
			$('.stack-trace-list li:last').after(listItem);
		} else {
			$('.stack-trace-list').html(listItem);
		}
	});
}