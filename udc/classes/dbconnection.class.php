<?

require_once ("db_config.php");

   	
$dbh = mysql_connect($dbHost, $dbUsername, $dbPassword);
$dbs = mysql_select_db($dbName, $dbh);

class DBConnection {
		#*****************************************************************************
        #
        # dbconnection.class.php
        #
        # Author:       Denis Roy
        # Date:         2004-08-05
        #
        # Description: Functions and modules related to the MySQL database connection
        #
		#*****************************************************************************


 	function connect() {
		global $dbh;
		return $dbh;
	}

	function disconnect() {
	}
}
?>
