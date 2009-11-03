<table id="video_thumb_table">
<?php 

	$folderPath = "sdcard/emocha/training/library/";
	$filesA = getFileList($folderPath, false);
	
	foreach($filesA AS $fileA) {
		$filePath = $folderPath.$fileA['filename'];
		$infoA = pathinfo($filePath);
		if ($infoA['extension'] == 'html') {
			$time = date('d-m-Y H:j:s', $fileA['ts']);			
			$size = number_format(filesize($folderPath.$fileA['filename']));
			
			print "<tr>";
			print "<td><b>$infoA[filename]</b>";
			print "<td>$time</td>";
			print "<td>$size bytes</td>";
			print "</tr>";
		}
	}
	
?>
</table>
	