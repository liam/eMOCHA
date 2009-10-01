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

	include 'include/config.php';
	include 'include/file.php';
	include 'include/db.php';

	connectToDB($Config['DB']['MAIN']);
	
	// File where to store a timestamp. Time when last file system scan looking
	// for changed files took place
	define ('LASTFSCHECK',  'config/lastFSCheck');
	
	// File where to store a timestamp. Time when last write on database took place.
	// Phones will store a copy of this value. If the value doesn't change, phones
	// know there is nothing new to download. If it has changed, then they must
	// compare the file details to know if something must be downloaded or not.
	define ('LASTDBCHANGE', 'config/lastDBChange');
	
	// Calls to this file after these many seconds will trigger a file system
	// scan looking for changed files. Maybe once every 30 minutes? Lower is
	// better in case a file must be urgently fixed in all phones, but also
	// uses more resources. The resources used are in proportion to the number
	// of files in the folder. If the number of files is low, one could set a
	// very low value for this constant (30 seconds for example). 
	define ('FSCHECKFREQ',  10);
	
	define ('SDCARDFOLDER', 'sdcard/emocha');

	$lastFSCheck = implode(file(LASTFSCHECK));
	$timeForNewCheck = time() > ($lastFSCheck + FSCHECKFREQ);
	if ($timeForNewCheck) {
		//print "<br/>RECHECK";
		syncFilesToDB();
		writeFile(LASTFSCHECK, time());
	} 
	$lastDBChange = implode(file(LASTDBCHANGE));
	
	// TODO: add user and password checking
	
	function syncFilesToDB() {
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
		$filesDiskA = getFileList(SDCARDFOLDER, true);
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
			writeFile(LASTDBCHANGE, time());
		}	
	}

	switch($_REQUEST['cmd']) {
		case 'getSyncDetails':
			print json_encode(array(
				'lastDBChange' => $lastDBChange
			));
			break;
		case 'getFileList':
		  	$filesDBQ = query("SELECT * FROM sdcard");
		  	$filesA = array();
		  	while($rowA = mysql_fetch_assoc($filesDBQ)) {
		  		$filesA[] = Array(
		  			"path" 	=> $rowA['path'], 
		  			"ts" 	=> $rowA['ts'], 
		  			"size" 	=> $rowA['size'], 
		  			"md5" 	=> $rowA['md5']);
		  	}
			print json_encode(array(
				'files' => $filesA
			));
			// TODO: send data in json format. it's easier to parse than xml.
			// the phone requires similar code
			// will compare local sqlite database with json data and make a list
			// of files to download. service will download them, and mark them in
			// sqlite as ready. application will use sqlite to show documents, videos, etc.
			break;
		case 'getFile':
			// TODO: attempt to download a file
			break;
	}


?>