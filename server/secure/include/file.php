<?

function getFileList($tDir, $tRecursive = FALSE) {
	if (is_dir($tDir)) {
		for ($tList = array(), $tHandle = opendir($tDir); (FALSE !== ($tFile = readdir($tHandle)));) {
			if (($tFile != '.' && $tFile != '..') && (file_exists($tPath = "$tDir/$tFile"))) {
				if (is_dir($tPath) && ($tRecursive)) {
					$tList = array_merge($tList, getFileList($tPath, TRUE));
				} else {
					// 1. Files and Directories
					$tEntry = array(
						'filename' => $tFile, 
						'dirpath'  => $tDir,
						'path'     => $tPath,
						'ts'       => filemtime($tPath)
					);
					
					do if (!is_dir($tPath)) {
						// 2. Files 
						$tEntry['size'] = filesize($tPath);
						break;
					} else {
						// 3. Directories
						break;
					} while (FALSE);
					$tList[] = $tEntry;
				}
			}
		}
		closedir($tHandle);
		return $tList;
	} else return FALSE;
}

function writeFile($tFilename, $tContent) {
	$tFP = fopen($tFilename, 'w');
	fputs ($tFP, $tContent);
	fclose($tFP);		
}
?>