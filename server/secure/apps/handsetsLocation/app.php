<?
		$markerDataJS = ''; 
		$q = mysql_query('SELECT ID, last_connect_ts, gps, comments '.
			'FROM phone WHERE LOCATE(":", gps) > 0');
		while($row = mysql_fetch_assoc($q)) {		
		  $loc = explode(':', $row['gps']);
		  $markerDataJS .= sprintf(
			'tMarker=new GMarker(new GLatLng(%s,%s)); '.
		  	'tMarker.PID=%d; '.
		  	'tMarkers.push(tMarker);'."\n", 
			  $loc[0], $loc[1], $row['ID']);
		  
		  $markerDataJS .= sprintf(
			'pPatientData[%d]="<b>%s</b><br/>%s";'."\n",
			  $row['ID'], 
			  $row['comments'], 
			  date('d-m-Y h:i', (3600*3)+$row['last_connect_ts'])
		  );
		}
?>

<script type="text/javascript">

	var pMap;
	var pPatientData = [];

	function showMarkerDetails(tMarker) {
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

		    var tMarkers = [];
			var tMarker;
			<? print $markerDataJS; ?>
		    var markerCluster = new MarkerClusterer(pMap, tMarkers);			    
		}	
    }

</script>

<div id="map_canvas"></div>
