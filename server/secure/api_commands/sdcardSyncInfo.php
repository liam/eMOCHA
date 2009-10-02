<?php
 
	$lastDBChange = implode(file($Config['PATH']['LASTDBCHANGE']));

	$responseA['lastDBChange'] = $lastDBChange; 
?>
