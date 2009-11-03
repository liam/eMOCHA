<table id="video_thumb_table">
<?php 

	$folderPath = "sdcard/emocha/training/courses/";
	$filesA = getFileList($folderPath, false);
	
	
	foreach($filesA AS $fileA) {
		$filePath = $folderPath.$fileA['filename'];
		$infoA = pathinfo($filePath);
		if ($infoA['extension'] == 'mp4') {
			$time = date('d-m-Y H:j:s', $fileA['ts']);			
			$size = number_format(filesize($filePath));
			$img = $folderPath.$infoA['filename'].'.jpg';
			if (!is_file($img)) {
				$img = '';
			}
			
			print "<tr>";
			print "<td><img src=\"$img\"></td>";
			print "<td><b>$infoA[filename]</b><br/>$time<br/>$size bytes</td>";
			print "</tr>";
		}
	}
	
?>
</table>
	