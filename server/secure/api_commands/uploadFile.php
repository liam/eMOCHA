<?
	
	foreach($_FILES AS $fileID => $fileA) {
		$fileNum = substr($fileID, 4);
		$pathInSdcard = $_POST["path$fileNum"];		
		$newFilePath = 'sdcard/'.$usrID.$pathInSdcard;		
		$dirname = dirname($newFilePath);

		if (!is_dir($dirname)) {
			if (!mkdir($dirname, 0755, true)) {
				sendError("Can not create user directory");
			}
		}
		
		copy($fileA['tmp_name'], $newFilePath);
	}

//	print_r($_FILES);
//	print_r($_POST);
 
/*
Array (
    [file0] => Array (
            [name] => mHealth_2009-09-18_17-51-35.xml
            [type] => application/octet-stream
            [tmp_name] => /tmp/phpNlmCUB
            [error] => 0
            [size] => 286
        )
)

Array (
    [usr] => fbbee129c5cce1ab58c097624fb1b0ee
    [pwd] => 987654
    [cmd] => uploadFile
    [path0] => /sdcard/odk/instances/mHealth_2009-09-18_17-51-35/mHealth_2009-09-18_17-51-35.xml
)
 */ 	
?>