<?

	// eMOCHA server side gateway
	
	// All communications between the phones and 
	// the server will go through this file.

	// Check if using HTTPS, otherwise exit.
	if ($_SERVER['HTTPS'] != "on") { 
		sendError("HTTPS only");
	} 

	include 'include/db.php';
	include 'include/config.php';

	connectToDB($Config['DB']['MAIN']);

	if ($_REQUEST['cmd'] != 'activatePhone') {
		// Check if we received all required POST vars
		$requiredVars = Array('usr', 'pwd', 'cmd');
		foreach($requiredVars as $var) {
			if (!isset($_REQUEST[$var]) || strlen($_REQUEST[$var]) == 0) {
				sendError('required variable is missing');
			}
		}
	
		$usrID = query1("SELECT ID FROM phone ".
			"WHERE imei_md5='$_REQUEST[usr]' AND pwd=PASSWORD('$_REQUEST[pwd]') AND validated=1");
		if (!($usrID)) {
			sendError('unauthorized user '.$_REQUEST['usr'].', '.$_REQUEST['pwd']);
		}
	}
	
	$cmdPath = 'api_commands/'.preg_replace('/\W/', '', $_REQUEST['cmd']).'.php';
	if (is_file($cmdPath)) {
		$responseA = Array(
			'ok' 	=> time(),
			'cmd' 	=> $_REQUEST['cmd'] 
		);
		include $cmdPath;
		sendResponse($responseA);
	} else {
		sendError('unknown command');
	}
	
	
	function sendResponse($r) {
		print json_encode($r);
		exit();
	}
	function sendError($msg) {
		sendResponse(array('error' => $msg));
	}

?>