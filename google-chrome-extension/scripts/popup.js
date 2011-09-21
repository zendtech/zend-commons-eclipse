function checkWelcome() {
	if (localStorage["username"] == undefined) {
		$('#welcome')[0].style.display = 'none';
		$('#loginform')[0].style.display = 'inline';
	} else {
		$('#welcome')[0].innerHTML = "Welcome, " + localStorage["username"] + "!<br/><a href='javascript:signout();'>sign out</a>";
		$('#welcome')[0].style.display = 'inline';
		$('#loginform')[0].style.display = 'none';
	}
}

function signout() {
	delete localStorage["username"];
	delete localStorage['projectxsess'];
	setSessionId(null);
	checkWelcome();
	return false;
}

function login(f) {

	// prepare args
	var u = f.username.value;
	var p = f.password.value;

	localStorage["username"] = u;

	// authenticate
	authenticate(u, p, authenticate_success, authenticate_fail);

	return false;
}

function authenticate_success(sessionid) {
	localStorage['projectxsess'] = sessionid;

	var select = $('#form-message')[0];
	select.style.display = 'none';

	list(list_success, list_fail);
}

function authenticate_fail() {
	delete localStorage.projectxsess;

	var select = $('#form-message')[0];
	select.style.display = 'inline';
}

function list_success(name) {
	localStorage['container'] = name;
	checkWelcome();
	window.close();
}

function list_fail() {
	authenticate_fail();
	delete localStorage.container;
}
