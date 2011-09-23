var org = org || {};
org.zend = org.zend = {

	isValidDomain : function(domain) {
		return domain == "devpaas.zend.com" || domain == "projectx.zend.com"
				|| domain == "phpcloud.com";
	},

	getMonitorRequestId : function(name) {
		prefix = "ZENDMONITORURL";
		length = prefix.length;

		// is valid request id
		var arrcookies = document.cookie.split(";");
		for ( var i = 0; i < arrcookies.length; i++) {
			name = arrcookies[i].substr(0, arrcookies[i].indexOf("="));
			if (name.length > length && name.substr(0, length) == prefix) {
				return name.substr(length);
			}
		}
		return null;
	},

	summary_success : function(response) {
		if (response.requestSummary["events-count"] != '0') {
			for ( var ev in response.requestSummary.events) {
				events.push(response.requestSummary.events[ev]);
			}
			if (hasFader()) {
				addSlides();
				all_events = all_events.concat(events);
				events = [];
				updateSummary(all_events);
			}
			chrome.extension.sendRequest({
				method : "showNotifications"
			}, function(response) {
			});
		}
	},

	summary_error : function(message) {
		chrome.extension.sendRequest({
			method : "signout"
		}, function(response) {
			chrome.extension.sendRequest({
				method : "refreshPopupContent"
			}, function(response) {
			});
		});
	}
};

// search for the magic cookie....
domain = window.location.hostname;
dot = domain.indexOf('.');
if (dot != -1 && org.zend.isValidDomain(domain.substr(dot + 1))) {
	// is container name included in the list of containers
	containerName = domain.substr(0, dot);
	chrome.extension.sendRequest({
		method : "isValidContainer",
		key : containerName
	}, function(response) {
		if (response.data != -1) {
			requestId = org.zend.getMonitorRequestId(document.cookie);
			if (requestId != null) {
				requestSummary(containerName, requestId,
						org.zend.summary_success, org.zend.summary_error);
			}
		}
	});
}

var events = [];
var all_events = [];
var slideshow;

chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
	if (request.details == "openEvents") {
		if (!hasFader()) {
			createFader();
		}

	}
	if (hasFader()) {
		addSlides();
		all_events = all_events.concat(events);
		events = [];
		updateSummary(all_events);
	}
});

function updateSummary(events) {
	$("#zend_ex_summary").html("total " + events.length + " events.");
}

function addSlides() {
	var slidesUl = $("#slides");
	for ( var i = 0; i < events.length; i++) {
		// todo: prettify
		var slideLi = $("<li>").html(
				'<h1>' + events[i].type + '</h1><p>' + events[i].description
						+ '</p>' + '<a href=' + events[i]['debug-url']
						+ '>Debug Event</a><br/>' + '<a href='
						+ events['code-tracing']
						+ '>Open Code Tracing Snapshot</a>' + '<p>'
						+ events[i].severity + '</p>');
		slideLi.addClass("zend_content");
		slidesUl.append(slideLi);

		slideshow.l++;
	}

	var paginationUl = $("#pagination");
	for ( var i = 0; i < events.length; i++) {
		j = i + 1;
		var paginationLi = $("<li>").html(j);
		var aff = (function(n) {
			return function() {
				slideshow.pos(n);
			};
		})(j);
		paginationLi.click(aff);
		paginationUl.append(paginationLi);
	}
}

function hasFader() {
	return document.getElementById("wrapper") != undefined;
}

function createFader() {
	// slides
	var divSlides = getSlidesDiv();

	// pagination
	var divPagination = getPaginationDiv();

	// wrapper
	var div = $(document.createElement('div'));
	div.attr("id", "wrapper");
	div.append(divSlides);
	div.append(divPagination);

	// add wrapper
	$("body").prepend(div);

	// <div id="fade" class="black_overlay"></div>
	var fadeDiv = $(document.createElement('div'));
	fadeDiv.attr("id", "fade");
	fadeDiv.attr("class", "black_overlay");
	$("body").prepend(fadeDiv);

	// init slideshow
	slideshow = new TINY.fader.fade('slideshow', {
		id : 'slides',
		auto : 0,
		resume : false,
		navid : 'pagination',
		activeclass : 'current',
		visible : true,
		position : 0
	});
}

function getSlidesDiv() {
	// slides div

	var divLeft = $(document.createElement('div'));
	divLeft.attr("class", "sliderbutton");
	divLeft.html('<img src="' + chrome.extension.getURL("images/left.gif")
			+ '" width="32" height="38" alt="Previous" />');
	$("img", divLeft).click(function() {
		slideshow.move(-1);
	});

	var divRight = $(document.createElement('div'));
	divRight.attr("class", "sliderbutton");
	divRight.html('<img src="' + chrome.extension.getURL("images/right.gif")
			+ '" width="32" height="38" alt="Next" />');
	$("img", divRight).click(function() {
		slideshow.move(1);
	});

	var divCenter = $(document.createElement('div'));
	divCenter.attr("id", "slideshow");

	var slidesUl = $(document.createElement('ul'));
	slidesUl.attr("id", "slides");

	// build summary page
	var slideLi = $("<li>").html(
			'<h1>Summary</h1><div id="zend_ex_summary"></div>');
	slideLi.addClass("zend_content");
	slidesUl.append(slideLi);
	divCenter.append(slidesUl);

	var divSlides = $(document.createElement('div'));
	divSlides.append(divLeft);
	divSlides.append(divCenter);
	divSlides.append(divRight);
	return divSlides;
}

function getPaginationDiv() {

	var paginationUl = $(document.createElement('ul'));
	paginationUl.attr("id", "pagination");
	paginationUl.attr("class", "pagination");

	// build summary page
	var paginationLi = $("<li>");
	paginationLi.click(function() {
		slideshow.pos(0);
	});
	paginationLi.html("Summary");
	paginationUl.append(paginationLi);

	return paginationUl;
}
