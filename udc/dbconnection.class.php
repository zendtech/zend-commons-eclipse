<?
$MysqlUrl           = "localhost";
$MysqlUser          = "udc_user";
$MysqlPassword      = "udc_password";
$MysqlDatabase      = "udc";
   	
$dbh = mysql_connect($MysqlUrl, $MysqlUser, $MysqlPassword);
$dbs = mysql_select_db($MysqlDatabase, $dbh);

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
