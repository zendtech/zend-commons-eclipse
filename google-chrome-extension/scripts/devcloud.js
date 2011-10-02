/**
 * Authenticate to the Zend Developer Cloud service given the username and
 * password. Once authentication is validated, a session id is retrieved and
 * assigned as cookie
 * 
 * @param u
 * @param p
 * @param _success
 * @param _error
 */
function authenticate(u, p, _success, _error) {

	var params = 'username=' + encodeURIComponent(u) + '&password='
			+ encodeURIComponent(p);

	$.ajax({
		type : "POST",
		url : "https://projectx.zend.com/user/login?format=json",
		data : params,
		contentType : "application/x-www-form-urlencoded",
		success : function(data, textStatus) {
			try {
				resp = JSON.parse(data);
			} catch (ex) {
				_error('error parsing json');
				return;
			}
			setSessionId(resp.session.projectxsess);
			_success(resp.session.projectxsess);
		},
		error : function(jqxhr, textStatus, errorThrown) {
			_error(jqxhr.response);
		}
	});
}

/**
 * Lists all containers for a given user TODO *all*
 * 
 * @param _success
 * @param _error
 */
function list(_success, _error) {
	var params = '/container/list?format=json';

	$.ajax({
		type : "POST",
		url : "https://projectx.zend.com/container/list?format=json",
		data : params,
		contentType : "application/x-www-form-urlencoded",
		success : function(data, textStatus) {
			try {
				resp = JSON.parse(data);
			} catch (ex) {
				_error('error parsing json');
				return;
			}
			_success(resp.containers);
		},
		error : function(jqxhr, textStatus, errorThrown) {
			_error(textStatus);
		}
	});
}

/**
 * Retrieve information about a particular request's events and code tracing.
 * The requestUid identifier is provided in a cookie that is set in the response
 * to the particular request.
 * 
 * @param containerName
 * @param requestUid
 * @param _success
 * @param _error
 */
function requestSummary(containerName, requestUid, _success, _error) {
	var params = 'containerName/' + containerName + '/requestUid/' + requestUid
			+ '?format=json';

	$.ajax({
		type : "GET",
		url : "https://projectx.zend.com/monitor/get-request-summary/" + params,
		success : function(data, textStatus) {
			try {
				resp = JSON.parse(data);
			} catch (ex) {
				_error('error parsing json');
				return;
			}
			if (resp.status == 'Success') {
				_success(resp.response);
			} else {
				_error(resp.response.message);
			}
		},
		error : function(jqxhr, textStatus, errorThrown) {
			_error(textStatus);
		}
	});
}

/**
 * Download the amf file specified by codetracing identifier
 * 
 * @param containerName
 * @param amf
 * @param _success
 * @param _error
 */
function downloadAmf(containerName, amf, _success, _error) {
	var params = 'containerName/' + containerName + '/amf/' + amf
			+ '?format=json';

	$.ajax({
		type : "GET",
		url : "https://projectx.zend.com/monitor/download-amf/" + params,
		success : function(data, textStatus) {
			try {
				resp = JSON.parse(data);
			} catch (ex) {
				_error('error parsing json');
				return;
			}
			if (response.status == 'Success') {
				_success(response.response);
			} else {
				_error(response.response.message);
			}
		},
		error : function(jqxhr, textStatus, errorThrown) {
			_error(textStatus);
		}
	});
}

/**
 * Start a debug session for specific issue
 * 
 * @param containerName
 * @param amf
 * @param _success
 * @param _error
 */
function startDebug(containerName, issueId, eventGroupId, _success, _error) {
	var params = 'containerName/' + containerName + '/issueId/' + issueId
			+ '/eventGroupId/' + eventGroupId + '?format=json';

	$.ajax({
		type : "GET",
		url : "https://projectx.zend.com/monitor/start-debug/" + params,
		success : function(data, textStatus) {
			try {
				resp = JSON.parse(data);
			} catch (ex) {
				_error('error parsing json');
				return;
			}
			if (resp.status == 'Success') {
				_success(resp.response);
			} else {
				_error(resp.response.message);
			}
		},
		error : function(jqxhr, textStatus, errorThrown) {
			_error(textStatus);
		}
	});
}

function setSessionId(sessionid) {
	var today = new Date();
	var expire = new Date();
	if (sessionid != null) {
		expire.setTime(today.getTime() + 3600000 * 24);
	}
	document.cookie = "projectxsess=" + escape(sessionid) + ";expires="
			+ expire.toGMTString();
}
