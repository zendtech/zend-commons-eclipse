<?php

ini_set ( "display_erros", 1 );
ini_set ( "error_reporting", E_ALL );

set_include_path ( get_include_path () . PATH_SEPARATOR . dirname ( __FILE__ ) );

$dbHost = get_cfg_var("zend_developer_cloud.db.host");
if (! $dbHost) {
	echo ("ZS_DB_HOST env var undefined");
	exit ( 1 );
}
$dbUsername = get_cfg_var ( "zend_developer_cloud.db.username" );
if (! $dbUsername) {
	echo ("ZS_DB_USERNAME env var undefined");
	exit ( 1 );
}
$dbPassword = get_cfg_var ( "zend_developer_cloud.db.password" );
if (! $dbPassword) {
	echo ("ZS_DB_PASSWORD env var undefined");
	exit ( 1 );
}

$dbName = get_cfg_var ( "zend_developer_cloud.db.name" );
if (! $dbName) {
	echo ("ZS_DB_NAME env var undefined");
	exit ( 1 );
}
