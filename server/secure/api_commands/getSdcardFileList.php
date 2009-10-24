<?php
	/* This php script is queried by each phone to find out 
	 * if there are pending updates on on the sdcard.
	 
      - DONE: scan the sdcard folder
      - DONE: make a list of path/filename size date
      - DONE: Sync: make database look like file system (DELETE, INSERT, UPDATE)      
      - DONE: if path/filename exists, compare size and date, if different do md5 again
      - DONE: delete from database files that don't exist on disk anymore.
      
      - phone asks for this list of files from time to time. 
      - if the list has not been regenerated during the last XX minutes, refresh list.

      - phone will store a copy of this list in it's own database.
      - phone will have it's own timestamp of files in the database, so it can compares files
        on disk with files on database. if local time and local size differ from database, 
        rebuild local md5.
      - compare md5 from serverMD5 and localMD5. 
        if different download file from https using php usr+pwd, 
        get MD5 when done, if same MD5 set READY=true
        
      - files on the phone should be dowloaded to a temporary folder until they are
        complete, then moved to the final destination 
	*/
		
	$lastFSCheck = implode(file($Config['PATH']['LASTFSCHECK']));
	$timeForNewCheck = time() > ($lastFSCheck + $Config['TIME']['FSCHECKFREQ']);
	if ($timeForNewCheck) {
		//print "<br/>RECHECK";
		syncFilesToDB();
		writeFile($Config['PATH']['LASTFSCHECK'], time());
	} 
	function syncFilesToDB() {
		global $Config;
		
		$dbChanged = false;
	  	// create an array with all files in the database
	  	$filesDBQ = query("SELECT * FROM sdcard");
	  	$filesDBA = Array();  	
	  	while($rowA = mysql_fetch_assoc($filesDBQ)) {
	  		$filesDBA[$rowA['path']] = Array(
	  			'ts'     => $rowA['ts'],
	  			'size'   => $rowA['size'],
	  			'md5'    => $rowA['md5'],
	  			'ondisk' => false
	  		);
	  	};  	
		// create an array with all files in the disk
		$filesDiskA = getFileList($Config['PATH']['SDCARDFOLDER'], true);
		foreach($filesDiskA as $fileInDisk) {
			$pathInDisk    = $fileInDisk['path'];
			$fileFoundInDB = isset($filesDBA[$pathInDisk]);
			if ($fileFoundInDB) {
				$filesDBA[$pathInDisk]['ondisk'] = true;
				$tsChanged   = $fileInDisk['ts']   != $filesDBA[$pathInDisk]['ts']; 
				$sizeChanged = $fileInDisk['size'] != $filesDBA[$pathInDisk]['size']; 
				if ($tsChanged || $sizeChanged) {
					$md5 = md5_file($pathInDisk);
					query("UPDATE sdcard SET ts='%d', size='%d', md5='%s' WHERE path='%s'",
						$fileInDisk['ts'], 
						$fileInDisk['size'],
						$md5,
						$pathInDisk
					);
					$dbChanged = true;
					//print "<br/>file changed: $pathInDisk";
				} 
			} else {
				$md5 = md5_file($pathInDisk);
				query("INSERT INTO sdcard SET path='%s', ts='%d', size='%d', md5='%s'",
					$pathInDisk,
					$fileInDisk['ts'], 
					$fileInDisk['size'],
					$md5
				);
				$dbChanged = true;
				//print "<br/>new file: $pathInDisk";			
			}
		}
		foreach($filesDBA AS $pathInDB => $fileDetails) {
			if (!$fileDetails['ondisk']) {
				query("DELETE FROM sdcard WHERE path='%s'", $pathInDB);
				$dbChanged = true;
				//print "<br/>Delete from DB: $pathInDB";
			} 
		}
		if ($dbChanged) {
			writeFile($Config['PATH']['LASTDBCHANGE'], time());
		}	
	}

	// we return the last server update timestamp
	// if that timestamp is the same that was requested by the phone
	// it means there were no changes in the database, so the phone
	// must take no further action.
	// if the timestamp is different then the phone must compare the
	// returned list with the files found in the sdcard, and probably
	// download some new / updated files.
	$lastDBChange = implode(file($Config['PATH']['LASTDBCHANGE']));
  	$responseA['last_server_upd'] = $lastDBChange;

	if ($lastDBChange != $_REQUEST['last_server_upd']) {
	  	$filesDBQ = query("SELECT * FROM sdcard");
	  	$filesA = array();
	  	while($rowA = mysql_fetch_assoc($filesDBQ)) {
	  		$filesA[] = Array(
	  			"path" 	=> $rowA['path'], 
	  			"ts" 	=> $rowA['ts'], 
	  			"size" 	=> $rowA['size'], 
	  			"md5" 	=> $rowA['md5']);
	  	}
	  	$responseA['files'] = $filesA;
	} 
	
?>
