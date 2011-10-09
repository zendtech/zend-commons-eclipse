chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
	if (request.method == "refreshPopupContent") {
		refreshPopupContent();
	} else {
		sendResponse({}); // snub them.
	}
});

function login(f) {
	// prepare args
	var u = f.username.value;
	var p = f.password.value;
	
	localStorage["username"] = u;
	
	// authenticate
	authenticate(u, p, authenticate_success, authenticate_error);

	return false;
}

function authenticate_success(sessionid) {
	localStorage['phpcloudsess'] = sessionid;

	var select = $('#login-form-message')[0];
	select.style.display = 'none';

	list(list_success, list_error);
}

function authenticate_error(status) {
	var select = $('#login-form-message')[0];
	select.style.display = 'inline';
	
	var select = $('#login-form-welcome-title')[0];
	select.style.display = 'none';
	signout();
}

function list_success(containers) {
	localStorage['containers_length'] = containers.length;
	for ( var i = 0; i < containers.length; i++) {
		localStorage['containers' + i] = containers[i].name;
	}
	refreshPopupContent();
	window.close();
}

function list_error(status) {
	authenticate_error();
}

function refreshPopupContent() {
	if (localStorage["username"] == undefined) {
		if ($('#mini_bar_welcome').length > 0) {
			$('#mini_bar_welcome')[0].style.display = 'none';
		}
		if ($('#mini_bar_login').length > 0) {
			$('#mini_bar_login')[0].style.display = 'inline';
		}
		$('#settings-button').css('display', 'none'); 
		$('#logout-button').css('display', 'none');
		$('#mini_bar_header').width(350);
		document.body.style.width="360px";
	} else {
		if ($('#mini_bar_welcome').length > 0) {
			$('#mini_bar_welcome')[0].style.display = 'inline';
		}
		if ($('#mini_bar_login').length > 0) {
			$('#mini_bar_login')[0].style.display = 'none';
		}
		$('#settings-button').css('display', 'block'); 
		$('#logout-button').css('display', 'block');
		$('#mini_bar_header').width(490);
		document.body.style.width="500px";
	}
}

function signout() {
	chrome.extension.sendRequest({
		method : "signout"
	}, function(response) {
		refreshPopupContent();
	});
	return false;
}
