<? 
	$markerDataJS = '';
	$q = mysql_query('SELECT * FROM uploaded_data ORDER BY ts');
	while($row = mysql_fetch_assoc($q)) {		
		$xml = simplexml_load_string(stripslashes($row['xml']));
		// print '<h1>'.$xml->patient_name.'</h1>';
		// $locA = explode(',', $xml->patient_location);
		// print implode('<br/>', $locA);

		$showit = true;
		
		$ageMin  = min($_POST['age_min'],  $_POST['age_max']); 
		$ageMax  = max($_POST['age_min'],  $_POST['age_max']);
		$tempMin = min($_POST['temp_min'], $_POST['temp_max']);
		$tempMax = max($_POST['temp_min'], $_POST['temp_max']);
		
		// filtering rules
		if ($_POST['gender']) {
			$showit = $showit && ($_POST['gender'] == strtolower($xml->patient_sex));
		} 
		if ($_POST['tb']) {
			$showit = $showit && ($_POST['tb'] == $xml->patient_tbc);
		}
		if ($_POST['hiv']) {
			$showit = $showit && ($_POST['hiv'] == $xml->patient_hiv);
		}
		if ($_POST['age_min']) {
			$showit = $showit && ($ageMin <= $xml->patient_age);
		}
		if ($_POST['age_max']) {
			$showit = $showit && ($ageMax >= $xml->patient_age);
		}
		if ($_POST['temp_min']) {
			$showit = $showit && ($tempMin <= $xml->patient_temp);
		}
		if ($_POST['temp_max']) {
			$showit = $showit && ($tempMax >= $xml->patient_temp);
		}
		
		if ($showit && strlen($xml->patient_location) > 10) {
			if ( $row['last_connect_ts'] > time() - 7200) {
				$icon = 'recentIcon';
  			} else {
  				$icon = 'oldIcon';
			  }
		  			
			$markerDataJS .= sprintf('tMarker=new GMarker(new GLatLng(%s), { icon:%s }); tMarker.PID=%d; tMarkers.push(tMarker);'."\n", 
				str_replace(' ', ',', $xml->patient_location), $icon, $row['ID']);
							
			if (strlen($xml->patient_image) > 5) {
				$url = sprintf("sdcard/%s/sdcard/odk/instances/%s/%s", $row['usrID'], $row['folderName'], $xml->patient_image);
				$img = sprintf("<img onClick='jQuery.slimbox(\\\"%s\\\", \\\"%s\\\");' src='%s' style='float:right;' width='60'>", 
				$url, $xml->patient_name, $url);				
			} else {
				$img = '';
			}
			
			$markerDataJS .= sprintf('pPatientData[%d]="%s<b>%s</b><br/>'.
				'%s year old %s<br/>'.
				'Temp: %sºC<br/>'.
				'%s %s<br/>%s";'."\n",
				$row['ID'], 
				$img,
				$xml->patient_name, 
				$xml->patient_age,
				$xml->patient_sex == 'm' ? 'male' : 'female',
				$xml->patient_temp,
				$xml->patient_tbc == 'y' ? 'TB ' : '',
				$xml->patient_hiv == 'y' ? 'HIV ' : '',
				date('d-m-Y h:i', (3600*3)+$row['ts'])
			);
		}
	} 
?>


<script type="text/javascript">

	var pMap;
	var pPatientData = [];

	function showMarkerDetails(tMarker) {
		// send PID to ajax.php, which will return html and image
		tMarker.openInfoWindowHtml(pPatientData[tMarker.PID]);				
	}

    function initialize() {
		if(GBrowserIsCompatible()) {
		    pMap = new GMap2(document.getElementById("map_canvas"));
		    pMap.setCenter(new GLatLng(-0.326, 32.6424), 1);
		    //map.addControl(new GLargeMapControl());
				pMap.setUIToDefault();

				GEvent.addListener(pMap, "click", function(e) {
					if (e instanceof GMarker) {
						showMarkerDetails(e);
					}				
				});

			var recentIcon = new GIcon();
			recentIcon.image = "http://labs.google.com/ridefinder/images/mm_20_green.png";
			recentIcon.shadow = "http://labs.google.com/ridefinder/images/mm_20_shadow.png";
			recentIcon.iconSize = new GSize(12, 20);
			recentIcon.shadowSize = new GSize(22, 20);
			recentIcon.iconAnchor = new GPoint(6, 20);
			recentIcon.infoWindowAnchor = new GPoint(5, 1);

			var oldIcon = new GIcon();
			oldIcon.image = "http://labs.google.com/ridefinder/images/mm_20_orange.png";
			oldIcon.shadow = "http://labs.google.com/ridefinder/images/mm_20_shadow.png";
			oldIcon.iconSize = new GSize(12, 20);
			oldIcon.shadowSize = new GSize(22, 20);
			oldIcon.iconAnchor = new GPoint(6, 20);
			oldIcon.infoWindowAnchor = new GPoint(5, 1);
			
		    var tMarkers = [];
			var tMarker;
			<? print $markerDataJS; ?>
		    var markerCluster = new MarkerClusterer(pMap, tMarkers);			    
		}	
    }

</script>

<table>
	<tr>
		<td>
			<div id="map_canvas"></div>
		</td>
		<td>
			Gender<br/>
			<input type="radio" name="gender" value="m" />Male<br/>
			<input type="radio" name="gender" value="f" />Female<br/>
			<input type="radio" name="gender" value="" />Both<br/>
			<br/>
			TB<br/>
			<input type="radio" name="tb" value="y" />yes<br/>
			<input type="radio" name="tb" value="n" />no<br/>
			<input type="radio" name="tb" value="" />both<br/>
			<br/>
			HIV<br/>
			<input type="radio" name="hiv" value="y" />yes<br/>
			<input type="radio" name="hiv" value="n" />no<br/>
			<input type="radio" name="hiv" value="" />both<br/>
			<br/>
			Age between<br/>
			<input type="text" name="age_min" class="minmax" /> and
			<input type="text" name="age_max" class="minmax" /><br/>
			<br/>
			Temp between<br/>
			<input type="text" name="temp_min" class="minmax" /> and 
			<input type="text" name="temp_max" class="minmax" /><br/>
			<br/>
			<input type="submit" value="search" />
		</td>
	</tr>
</table>

<script src="js/slimbox2.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="css/slimbox2.css">
