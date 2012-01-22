<?php

$dbHost = get_cfg_var("zend_developer_cloud.db.host");
$dbUsername = get_cfg_var ( "zend_developer_cloud.db.username" );
$dbPassword = get_cfg_var ( "zend_developer_cloud.db.password" );
$dbName = get_cfg_var ( "zend_developer_cloud.db.name" );

$link = mysql_connect ( $dbHost, $dbUsername, $dbPassword );
mysql_select_db ( $dbName, $link );

$queries = explode ( ";", file_get_contents ( dirname ( __FILE__ ) . "/create_tables.sql" ) );

foreach ( $queries as $id => $query ) {
	$query = trim($query);
	if ($query != '') {
		
		$result = mysql_query ( $query . ";", $link );
		if (! $result) {
			echo ("Invalid query [$query]: " . mysql_error ());
			die ( 1 );
		}
		;
	
	}
}

mysql_close ( $link );