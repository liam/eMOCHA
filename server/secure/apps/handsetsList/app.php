<table id="video_thumb_table">
<?php 

	$q = query("SELECT * FROM phone ORDER BY last_connect_ts");
	
	while($row = mysql_fetch_assoc($q)) {
			$time = date('d-m-Y H:j:s', $row['last_connect_ts']);			
			print "<tr>";
			print "<td><b>$row[comments]</b>";
			print "<td>$row[imei]</td>";
			print "<td>$time</td>";
			print "</tr>";
	}
	
?>
</table>
	