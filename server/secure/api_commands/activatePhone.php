<?php

	$imei = preg_replace('/\W/', '', $_REQUEST['imei']); 

	if (strlen($imei) > 14) {
		query("INSERT INTO phone SET ".
			"imei='$imei', imei_md5=MD5('$imei'), validated=0, last_connect_ts=UNIX_TIMESTAMP()");
		$responseA['msg'] = 'Phone activation sent. The administrator will contact you.';
	} else {
		$responseA['msg'] = 'Server says IMEI code not received!';		
	}
?>
