<table id="video_thumb_table">
<?php 

	$folderPath = "sdcard/emocha/training/lectures/";
	$filesA = getFileList($folderPath, false);
	
	
	foreach($filesA AS $fileA) {
		if (substr($fileA['filename'], -4) == '.mp4') {
			$time = date('d-m-Y H:j:s', $fileA['ts']);
			$plainName = substr($fileA['filename'], 0, -4);
			
			$size = number_format(filesize($folderPath.$fileA['filename']));
			$img = $folderPath.$plainName.'.jpg';
			if (!is_file($img)) {
				$img = '';
			}
			
			print "<tr>";
			print "<td><img src=\"$img\"></td>";
			print "<td><b>$plainName</b><br/>$time<br/>$size bytes</td>";
			print "</tr>";
		}
	}
	
?>
</table>
	