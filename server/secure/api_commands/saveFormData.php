<?
	
	$xml = mysql_real_escape_string($_REQUEST['xml']);
	
	if (strlen($xml) > 10) {
		mysql_query("INSERT INTO form_data SET ts=UNIX_TIMESTAMP(), xml='$xml'");
	
		if (mysql_errno() != 0) {
			sendError(mysql_error());
		}
	} else {
		sendError("no data received");
	}

?>