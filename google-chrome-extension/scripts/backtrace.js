function populateBacktrace(backtrace) {
	$('.stack-trace-list li').remove();
	
	$.each(backtrace, function(index, element) {
		var listItem = '<li><span class="method-name no-wrap">'+(element['class']?element['class']+'.':'')+element.method+'()</span> <span class="filename no-wrap">'+element.filename+':'+element.line+'</span></li>';
		if ($('.stack-trace-list li').length) {
			$('.stack-trace-list li:last').after(listItem);
		} else {
			$('.stack-trace-list').html(listItem);
		}
	});
}