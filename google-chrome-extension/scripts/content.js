var result;
var slideshow;

chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
	if (request.details == "now") {
		addFader();
	} else {
		result = request;
	}
});

function addFader() {

	// slides
	divSlides = getSlidesDiv();
	
	// pagination
	divPagination = getPaginationDiv();
	
	// wrapper
	div = $(document.createElement('div'));
	div.attr("id", "wrapper");
	div.append(divSlides);
	div.append(divPagination);
	
	// add wrapper
	$("body").attr('style', 'font:12px Verdana,Arial; color:#555; background:#222 url(' + chrome.extension.getURL("images/bg.jpg")  + ') 50% 0 no-repeat');
	$("body").prepend(div);
	
	// init slideshow
	slideshow = new TINY.fader.fade('slideshow', {
		id : 'slides',
		auto : 1,
		resume : false,
		navid : 'pagination',
		activeclass : 'current',
		visible : true,
		position : 0
	});
}

function getSlidesDiv() {
	// slides div
	
	divLeft = $(document.createElement('div'));
	divLeft.attr("class", "sliderbutton");
	divLeft.html('<img src="' + chrome.extension.getURL("images/left.gif") + '" width="32" height="38" alt="Previous" onclick="slideshow.move(-1)" />');

	divRight = $(document.createElement('div'));
	divRight.attr("class", "sliderbutton");
	divRight.html('<img src="' + chrome.extension.getURL("images/right.gif") + '" width="32" height="38" alt="Next" onclick="slideshow.move(1)" />');
	
	divCenter = $(document.createElement('div'));
	divCenter.attr("id", "slideshow");
	
	slidesUl = $(document.createElement('ul'));
	slidesUl.attr("id", "slides");

	// build summary page
	slideLi = $("<li>").html('<h1>Summary</h1>');
	slideLi.attr("id", "content");
	slidesUl.append(slideLi);
	
	for ( var i = 0; i < result.events.length; i++) {
		// todo: prettify
		slideLi = $("<li>").html('<h1>' + result.events[i].type +  '</h1><p>' + result.events[i].description +  '</p>' + '<a href=' + result.events[i]['debug-url'] + '>Debug Event</a><br/>' + '<a href=' + result.events['code-tracing'] + '>Open Code Tracing Snapshot</a>' + '<p>' + result.events[i].severity + '</p>');
		slideLi.attr("id", "content");
		slidesUl.append(slideLi);
	}
	divCenter.append(slidesUl);
	
	divSlides = $(document.createElement('div'));
	divSlides.append(divLeft);
	divSlides.append(divCenter);
	divSlides.append(divRight);
	return divSlides;
}

function getPaginationDiv() {
	
	paginationUl = $(document.createElement('ul'));
	paginationUl.attr("id", "pagination");
	paginationUl.attr("class", "pagination");

	// build summary page
	paginationLi = $("<li>");
	paginationLi.attr("onclick", "slideshow.pos(0)");
	paginationLi.html("Summary");
	paginationUl.append(paginationLi);
	
	for ( var i = 0; i < result.events.length; i++) {
		j = i + 1;
		paginationLi = $("<li>").html(j);
		paginationLi.attr("onclick", "slideshow.pos(" + j + ")");
		paginationUl.append(paginationLi);
	}
	return paginationUl;
}
