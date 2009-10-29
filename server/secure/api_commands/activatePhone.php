<?php

	function getIP(){
	    if( isset( $_SERVER['HTTP_X_FORWARDED_FOR'] )) 
	    	$ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
	    else if( isset( $_SERVER ['HTTP_VIA'] ))  
	    	$ip = $_SERVER['HTTP_VIA'];
	    else if( isset( $_SERVER ['REMOTE_ADDR'] ))  
	    	$ip = $_SERVER['REMOTE_ADDR'];
	    else 
	    	$ip = null ;
	    
	    return $ip;
	}

	$imei = preg_replace('/\W/', '', $_REQUEST['imei']);
	$ip = getIP(); 

	if (strlen($imei) > 14) {
		query("INSERT INTO phone SET ".
			"imei='$imei', ".
			"imei_md5=MD5('$imei'), ".
			"validated=0, ".
			"creation_ts=UNIX_TIMESTAMP(), ".
			"creation_ip='$ip'"
		);
		$responseA['msg'] = 'Phone activation sent. The administrator will contact you.';
		
		mail($Config['EMAIL']['ACTIVATEPHONE'], 
			'Phone activation requested', 
			'ID: '.mysql_insert_id(), 
			$Config['EMAIL']['SERVER']);
	} else {
		$responseA['msg'] = 'Server says IMEI code not received!';		
	}
?>
