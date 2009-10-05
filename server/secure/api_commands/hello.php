<?

	$vars = '';
	foreach($_REQUEST AS $k =>  $v) {
		$vars .= "$k:$v, ";
	}

	$responseA['msg'] = 'Hello world :) VARS = '.$vars;

?>