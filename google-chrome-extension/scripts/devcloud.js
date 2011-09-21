/**
 * @param u -
 *            username
 * @param p -
 *            password
 * @param success -
 *            callback for the case of success
 * @param fail -
 *            callback for the case of failure
 */
function authenticate(u, p, _success, _fail) {

	var params = 'username=' + encodeURIComponent(u) + '&password='
			+ encodeURIComponent(p);

	$.ajax({
		type : "POST",
		url : "https://devpaas.zend.com/user/login?format=json",
		data : params,
		contentType : "application/x-www-form-urlencoded",
		success : function(data, textStatus) {
			try {
				resp = JSON.parse(data);
			} catch (ex) {
				return;
			}
			setSessionId(resp.session.projectxsess);
			_success(resp.session.projectxsess);
		},
		error : function(jqxhr, textStatus, errorThrown) {
			_fail();
		}
	});
}

/**
 * Lists all containers in the
 * 
 * @param success
 * @param fail
 */
function list(_success, _fail) {
	var params = '/container/list?format=json';

	$.ajax({
		type : "POST",
		url : "https://devpaas.zend.com/container/list?format=json",
		data : params,
		contentType : "application/x-www-form-urlencoded",
		success : function(data, textStatus) {
			try {
				resp = JSON.parse(data);
			} catch (ex) {
				_fail();
				return;
			}
			_success(resp.containers[0].name);
		},
		error : function(jqxhr, textStatus, errorThrown) {
			_fail();
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