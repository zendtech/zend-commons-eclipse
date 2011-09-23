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
	localStorage['projectxsess'] = sessionid;

	var select = $('#form-message')[0];
	select.style.display = 'none';

	list(list_success, list_error);
}

function authenticate_error(status) {
	var select = $('#form-message')[0];
	select.style.display = 'inline';
	signout();
}

function list_success(containers) {
	localStorage['containers_length'] = containers.length;
	for ( var i = 0; i < containers.length; i++) {
		localStorage['containers' + i] = containers[0].name;
	}
	refreshPopupContent();
	window.close();
}

function list_error(status) {
	authenticate_error();
}

function refreshPopupContent() {
	if (localStorage["username"] == undefined) {
		$('#welcome')[0].style.display = 'none';
		$('#loginform')[0].style.display = 'inline';
	} else {
		$('#welcome')[0].innerHTML = "Welcome, " + localStorage["username"]
				+ "!<br/><a href='javascript:signout();'>sign out</a>";
		$('#welcome')[0].style.display = 'inline';
		$('#loginform')[0].style.display = 'none';
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
