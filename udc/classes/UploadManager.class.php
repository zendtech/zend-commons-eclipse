<?php
/*******************************************************************************
 * Copyright (c) 2009, 2012 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    The Eclipse Foundation - initial API and implementation
 *******************************************************************************/

/**
 * This class does the upload heavy lifting.
 * Usage is along the lines of:
 * <ul>
 * <li>$uploadManager = new UploadManager();</li>
 * <li>$uploadManager->open();</li>
 * <li>$uploadManager->handle_uploads();</li>
 * <li>$uploadManager->close(); </li>
 * </ul>
 * <p>
 * The instance can additionally be asked to provide any messages that have been generated during
 * execution: $messages = $uploadManager->messages; Messages are only generated
 * if the instance has logging turned on. All configuration of the instance is
 * done via the HTTP request header (see the #initialize() function). Thoughts
 * about handling errors: First, we need to do a better job. We currently do not
 * employ any transaction support. This is something that we should consider
 * moving forward.
 * </p>
 */

class UploadManager {
	// **PRIVATE** The connection to the database.
	var $connection;
	
	// **PRIVATE** Identity of the user (more accurately, the identity of the
	// user's workstation).
	// This value is a UUID.
	var $userId;
	
	// **PRIVATE** Identity of the workspace. This value is a UUID.
	var $workspaceId;
	
	// **PRIVATE** Are we logging?
	var $logging = false;
	
	// **PRIVATE** Keep log messages in a buffer so that we can log activity and
	// still have the
	// ability to modify the headers of the response (once you write to stdout,
	// you can't modify the headers).
	var $messages = '';
	
	/**
	 * Open the instance.
	 * The instance is self-populating. That is, it populates
	 * itself from the information contained in the HTTP Request header. @return
	 * void.
	 */
	public function open() {
		$this->initialize ();
		$this->connect ();
		$this->configure ();
	}
	
	function close() {
		$this->disconnect ();
	}
	
	/**
	 * This is where it all happens.
	 * This function finds all the uploaded files
	 * and steps through them one-at-a-time, collecting the information
	 * contained in them and pushing it into the database.
	 *
	 * @throws FatalUploadException if the instance has not been configured
	 *         properly.
	 */
	function handle_uploads() {
		// First, check to make sure that the user agent is set properly.
		if (! ereg ( 'Zend UDC/.*', $this->userAgent )) {
			$this->log ( "Invalid User-Agent: $this->userAgent" );
			throw new FatalUploadException ();
		}
		$this->log ( "Handling uploads" );
		
		// The userId and workspaceId must be set
		// @see initialize
		if ($this->userId == null) {
			$this->log ( "No userId provided." );
			throw new FatalUploadException ();
		}
		if ($this->workspaceId == null) {
			$this->log ( "No workspaceId provided." );
			throw new FatalUploadException ();
		}
		
		$id = $this->get_profile_id ( $this->userId, $this->workspaceId );
		
		$upload_id = $this->get_upload_id ( $id );
		if ($upload_id === null) {
			$this->log ( "Can't obtain an uploadId." );
			throw new FatalUploadException ();
		}
		
		foreach ( $_FILES ["uploads"] ["error"] as $index => $error ) {
			if ($error == UPLOAD_ERR_OK) {
				$tmp_name = $_FILES ["uploads"] ["tmp_name"] [$index];
				$name = $_FILES ["uploads"] ["name"] [$index];
				$size = $_FILES ["uploads"] ["size"] [$index];
				
				try {
					$this->handle_file ( $upload_id, $tmp_name );
					echo "file-ok:$name\n";
				} catch ( UploadException $e ) {
					$this->log ( $e );
					echo "file-err:$name\n";
				}
			} else {
				$this->log ( "Failed to access file $index with error code $error." );
				echo "file-err:$name\n";
			}
		}
	}
	
	private function connect() {
		if ($this->connection)
			return; // Don't reopen
		
		require_once ("classes/dbconnection.class.php");
		
		$this->connection = new DBConnection ();
		$this->connection->connect ();
	}
	
	private function disconnect() {
		if (! $this->connection)
			return;
		try {
			$this->execute_sql ( 'unlock tables' );
		} catch ( UploadException $e ) {
			// Ignore
		}
		$this->connection->disconnect ();
	}
	
	/**
	 * This function initializes the instance by pulling some information out of
	 * the HTTP Header.
	 * Note that initialization happens before we get a
	 * connection to the database.
	 */
	private function initialize() {
		$this->userAgent = $this->get_http_header ( 'HTTP_USER_AGENT' );
		$this->userId = $this->get_http_header ( 'HTTP_USERID' );
		$this->workspaceId = $this->get_http_header ( 'HTTP_WORKSPACEID' );
		$this->time = $this->get_http_header ( 'HTTP_TIME' );
		
		$this->logging = strcasecmp ( $this->get_http_header ( 'HTTP_LOGGING' ), 'true' ) == 0;
	}
	
	/**
	 * This function locks the tables into which we intend to write.
	 * Note that configure() is called after we have already obtained
	 * a connection to the database.
	 */
	private function configure() {
		$sql = "lock tables usagedata_upload WRITE, usagedata_profile WRITE, usagedata_record WRITE";
		$this->execute_sql ( $sql ); // Will throw exception on failure.
	}
	
	/**
	 * Convenience method to obtain information from the HTTP Header without
	 * resulting in warning messages being written in the event that the
	 * header key is not available.
	 *
	 * @param $key name
	 *       	 of the header.
	 * @return a string value.
	 */
	private function get_http_header($key) {
		if (! array_key_exists ( $key, $_SERVER ))
			return null;
		return $_SERVER [$key];
	}
	
	/**
	 * This function obtains the profileId for the provided userId and
	 * workspaceId.
	 * If we have already encountered a user with the provided
	 * values, that user's profileId is returned. Otherwise, a new profile row
	 * is created and the id resulting from the insert is returned. Note that
	 * this function assumes that the usagedata_profile table has been locked
	 * for reads.
	 *
	 * @param $userId user
	 *       	 id (UUID) representing the user who initiated the upload.
	 * @param $workspaceId workspace
	 *       	 id (UUID) of the workspace.
	 *       	 function
	 * @return The primary key (int) of the existing, or newly created row in
	 *         the
	 *         usagedata_profile table.
	 */
	private function get_profile_id($userId, $workspaceId) {
		$userId = mysql_real_escape_string ( $userId );
		$workspaceId = mysql_real_escape_string ( $workspaceId );
		$sql = "select id from usagedata_profile where userid = '$userId' and workspaceid = '$workspaceId'";
		try {
			$result = $this->execute_sql ( $sql );
		} catch ( UploadException $e ) {
			throw new FatalUploadException ( $e->getMessage () );
		}
		$row = mysql_fetch_row ( $result );
		if ($row) {
			$id = $row [0];
			$this->log ( "Found profile id $id for $userId/$workspaceId." );
			return $id;
		} else {
			$sql = "insert into usagedata_profile (userId, workspaceId) values ('$userId', '$workspaceId')";
			try {
				$id = $this->execute_insert ( $sql );
			} catch ( UploadException $e ) {
				throw new FatalUploadException ( $e->getMessage () );
			}
			$this->log ( "Created profile id $id for $userId/$workspaceId." );
			return $id;
		}
	}
	
	/**
	 * This function creates an entry in the usagedata_uploads table to
	 * represent this upload operation.
	 * Note that this function assumes that the
	 * usagedata_upload table has been locked for reads. PRIVATE: This function
	 * is not intended as API.
	 *
	 * @param $profile_id Profile
	 *       	 id (PK from the usagedata_profile table) of the uploader.
	 * @return the primary key (int) of the row created to represent the upload.
	 */
	private function get_upload_id($profile_id) {
		$sql = "insert into usagedata_upload (profileId) values ('$profile_id')";
		$id = $this->execute_insert ( $sql );
		
		$this->log ( "Created upload id $id for profile $profile_id." );
		return $id;
	}
	
	/**
	 * This function handles a single uploaded file by using the CSV APIs to
	 * walk through the file contents, extracting the data.
	 * The extracted data
	 * (an array of arrays mapping column headers to values) is then forwarded
	 * to the insert_records function which inserts the values into the
	 * database.
	 */
	private function handle_file($upload_id, &$file_name) {
		$this->log ( "Handling the file $file_name" );
		$handle = fopen ( $file_name, "r" );
		$headers = fgetcsv ( $handle );
		if (! $headers)
			throw new DataMissingException ();
		
		if ($this->logging) {
			$message = "Found headers: ";
			foreach ( $headers as $header ) {
				$message .= "$header, ";
			}
			$this->log ( $message );
		}
		
		$records = array ();
		while ( ($record = fgetcsv ( $handle )) !== FALSE ) {
			$count = count ( $record );
			$this->log ( "Found a record with $count entries." );
			
			try {
				$record = $this->extract_record_from_array ( $headers, $record );
			} catch ( DataMissingException $e ) {
				// Try to recover.
			}
			if ($record)
				$records [] = $record;
		}
		fclose ( $handle );
		
		$this->insert_records ( $upload_id, $records );
	}
	
	/**
	 * Get an array containing the names of the headers in the given line.
	 * Note
	 * that we assume that there are no extra spaces in or around the names of
	 * the headers. Also note that we assume that there are no special
	 * characters, quotes, etc.
	 */
	private function get_headers($line) {
		$this->log ( "Getting headers from $line" );
		
		return split ( ",", $line );
	}
	
	private function extract_record_from_array(&$headers, &$entries) {
		if ($this->logging) {
			$message = "Extracting record from ";
			foreach ( $entries as $entry ) {
				$message .= "$entry, ";
			}
			$this->log ( $message );
		}
		
		reset ( $headers );
		reset ( $entries );
		
		$record = array ();
		while ( $header = current ( $headers ) ) {
			if (current ( $entries ) === false)
				throw new DataMissingException ();
			$record [$header] = mysql_real_escape_string ( current ( $entries ) );
			next ( $headers );
			next ( $entries );
		}
		return $record;
	}
	
	/**
	 * This function inserts the array of $records into the database.
	 * The given
	 * $upload_id is inserted with each record. If an exception occurs while
	 * writing any row, the method an any unwritten records are skipped, and
	 * the exception is rethrown.
	 */
	private function insert_records($upload_id, &$records) {
		try {
			foreach ( $records as &$record ) {
				$this->insert_record ( $upload_id, $record );
			}
		} catch ( SqlException $e ) {
			throw $e;
		}
	}
	
	private function insert_record($upload_id, &$record) {
		$column_list = $this->get_columns ();
		$columns = split ( ',', $column_list );
		
		$values = '';
		while ( $column = current ( $columns ) ) {
			if (! array_key_exists ( $column, $record )) {
				$this->log ( "Missing value for '$column' column." );
				return;
			}
			$value = $record [$column];
			$values .= ",'$value'";
			next ( $columns );
		}
		$sql = "insert into usagedata_record (uploadId, $column_list) values ('$upload_id' $values)";
		
		try {
			$this->execute_sql ( $sql );
		} catch ( SqlException $e ) {
			throw $e;
		}
	}
	
	private function get_columns() {
		return "what,kind,bundleId,bundleVersion,description,time";
	}
	
	private function execute_insert($sql) {
		try {
			$this->execute_sql ( $sql );
		} catch ( SqlException $e ) {
			throw $e;
		}
		return mysql_insert_id ();
	}
	
	private function execute_sql($sql) {
		$this->log ( "Executing: $sql" );
		$result = mysql_query ( $sql );
		if ($error = mysql_error ()) {
			$this->log ( "SQL Error: $error" );
			throw new SqlException ( $error );
		}
		return $result;
	}
	
	private function log($message) {
		if ($this->logging)
			$this->messages .= "log: $message\n";
	}

}

class UploadException extends Exception {
}

class FatalUploadException extends UploadException {
}

class SqlException extends UploadException {
}

class DataMissingException extends UploadException {
}
?>
