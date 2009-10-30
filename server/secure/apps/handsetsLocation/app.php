<?
		$markerDataJS = ''; 
		$q = mysql_query('SELECT ID, last_connect_ts, gps, comments '.
			'FROM phone WHERE LOCATE(":", gps) > 0');
		while($row = mysql_fetch_assoc($q)) {		
		  $loc = explode(':', $row['gps']);
		  
		  if ( $row['last_connect_ts'] > time() - 7200) {
		  	$icon = 'recentIcon';
		  } else {
		  	$icon = 'oldIcon';
		  }
		  
		  $markerDataJS .= sprintf(
			'tMarker=new GMarker(new GLatLng(%s,%s), { icon:%s }); '.
		  	'tMarker.PID=%d; '.
		  	'tMarkers.push(tMarker);'."\n", 
			  $loc[0], $loc[1], $icon, $row['ID']);
		  
		  $markerDataJS .= sprintf(
			'pPatientData[%d]="<b>%s</b><br/>%s";'."\n",
			  $row['ID'], 
			  $row['comments'], 
			  date('d-m-Y H:i', (3600*0)+$row['last_connect_ts'])
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

<div id="map_canvas"></div>
