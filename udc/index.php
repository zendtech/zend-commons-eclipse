<?php

/*
 * This function answers true if the User-Agent field in the HTTP Header
 * indicates that the source of the information is an Eclipse instance
 * running the UDC. It answers false otherwise.
 */
function is_udc_agent() {
	if (!array_key_exists('HTTP_USER_AGENT', $_SERVER)) return false;
	$agent = $_SERVER['HTTP_USER_AGENT'];
	return ereg('Zend UDC/.*', $agent);
}

// If the UDC is not providing the data, then redirect to the UDC landing page.
if (!is_udc_agent()) {
	header("Location: http://www.eclipse.org/org/usagedata");
	return;
}

/*
 * Uncomment this section to effectively shutdown the gathering of
 * usage data. A 500 error is returned to the requestor who should retain
 * their data for a subsequent upload.
 */
//header("HTTP/1.0 500 Server is down for maintenance.");
//return;

require_once('classes/UploadManager.class.php');
	
$manager = new UploadManager();
$manager->open();
try {
	$result = $manager->handle_uploads();
} catch (FatalUploadException $e) {
	header("HTTP/1.0 500 A fatal exception occurred while processing this request.");
} catch (UploadException $e) {
	// Do nothing (for now)
}

// Dump anything that has been written to the log.
echo $manager->messages;

$manager->close();
    
?>
