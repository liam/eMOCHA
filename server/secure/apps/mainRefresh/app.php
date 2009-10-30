<?

	$validUsersA = queryArray("SELECT ID, ID FROM phone WHERE validated=1");

	$inserts = 0;

	foreach($validUsersA AS $ID) {
		$folder = "sdcard/$ID";
		
		if (is_dir($folder)) { 		
			$folderPath = "sdcard/$ID/sdcard/odk/instances";
			$filesA = getFileList($folderPath, false);
			foreach($filesA AS $fileA) {
				$folderName = $fileA['filename'];
				$exists = query1("SELECT ID FROM uploaded_data WHERE usrID='$ID' AND folderName='$folderName'");
				if ($exists == '') {
					$xmlPath = "$folderPath/$folderName/$folderName.xml";
					if (is_file($xmlPath)) { 
						$xml = addslashes(implode('', file($xmlPath)));

						list($name, $date, $time) = explode('_', $folderName);
						if ($name == 'mHealth' || $name == 'eMOCHA') {
							list($yy, $mo, $dd) = explode('-', $date);
							list($hh, $mi, $ss) = explode('-', $time);
							$ts = mktime($hh, $mi, $ss, $mo, $dd, $yy);
						
							if(query("INSERT INTO uploaded_data SET usrID='$ID', folderName='$folderName', xml='$xml', ts='$ts'")) {
								$inserts++;
							}
						} else {
							// here we are skipping other forms, like quizes. do something with them!
						} 
					} else {
						// here we should delete this folder because it doesn't contain an xml?
					} 
				} 
			}
		}
	}

	print "$inserts new entries loaded.";

?>