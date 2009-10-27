<?
	$responseO = new Response($_POST['command']);

	switch($_POST['command']) {
		case 'addPhone':
			if (strlen($_POST['imei']) == 15) {
				$sql = sprintf('INSERT INTO phone SET imei="%s"',
					$_POST['imei']
				);
				if(query($sql)) {
					$responseO->setStatus('OK', "Item created succesfully");
					$responseO->set(array(
						'newID' => mysql_insert_id(),
						'imei' => $_POST['imei']
					));
				} elseif (mysql_errno() == 1062) {
					$responseO->setStatus('ERR', "Phone with IMEI $_POST[imei] already exists in the database. No need to add it again.");
				} else {					
					$responseO->setStatus('ERR', "Error creating item: <code>$sql</code>");
				}
			}
		
			break;
	}

	print $responseO->get();

?>