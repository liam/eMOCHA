<?

/*
<?xml version=\'1.0\' ?>
<patient xlmns=\"\">
<patient_name>Mike 01</patient_name>
<patient_sex>m</patient_sex>
<patient_age>34</patient_age>
<patient_temp>39.2</patient_temp>
<patient_tbc>n</patient_tbc>
<patient_hiv>y</patient_hiv>
<patient_location>2.003333333333333,1.0016666666666665</patient_location>
</patient>
*/

	include 'include/db.php';
	include 'include/config.php';

	connectToDB($Config['DB']['MAIN']);
		
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<title>eMOCHA</title>
	<link rel="stylesheet" type="text/css" href="css/main.css">
	<link type="text/css" href="css/ui-lightness/jquery-ui-1.7.2.custom.css" rel="stylesheet" />	
	<LINK REL="SHORTCUT ICON" HREF="favicon.ico">
</head>
<body onload="initialize()" onunload="GUnload()">
	
	<div id="content">
	
	<TABLE id="header" class="fullWidth">
		<tr>
			<td id="banner" class="ui-corner-all">
				<img src="i/banner_cut.gif">
				<img src="i/eMOCHA_logo.png" />
			</td>
			<td id="login" class="ui-corner-all">
				<form>
					Username: <input type="text" /><br/>
					Password: <input type="password" /><br/>
					<input type="submit" value="Sign in" />
				</form>
			</td>
			<td id="status" class="ui-corner-all">
				alarm 
				<br/>status
			</td>
		</tr>
	</table>

	<div id="tabs" class="ui-tabs ui-widget ui-widget-content ui-corner-all">
		<ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
			<li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active"><a href="?section=main">Main</a></li>
			<li class="ui-state-default ui-corner-top"><a href="?section=edu">Edu</a></li>
			<li class="ui-state-default ui-corner-top"><a href="?section=telemed">TeleMed</a></li>
			<li class="ui-state-default ui-corner-top"><a href="?section=stats">Stats</a></li>
			<li class="ui-state-default ui-corner-top"><a href="?section=handsets">Handsets</a></li>
		</ul>

		<TABLE id="mid" class="fullWidth">
			<tr>
				<td id="menu">
					<ul>
						<li><a href="?section=map">Map</a>
						<li><a href="?section=alarm">Alarms</a>
						<li><a href="?section=stats">Stats</a>
						<li><a href="?section=messages">Messages</a>
						<li><a href="?section=settings">Settings</a>
						<li><a href="?section=phones">Phones</a>
					</ul>
				</td>
				<td id="map">
					<table>
						<tr>
							<td>
								<div id="map_canvas"></div>
							</td>
							<td>
								<form name="form1" action="index.php" method="post">
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
								</form>
							</td>
						</tr>
					</table>
	
				</td>
			</tr>
		</TABLE>
	</div>
		
	<!--
	<TABLE id="bottom" class="fullWidth">
		<tr>
			<td id="graphs">
				<table class="darkHeader">
					<tr>
						<th colspan="2">Patient / Cluster stats graphs</th>
					</tr>
					<tr>
						<td>Var1</td>
						<td>values values values values values values values values values values </td>
					</tr>
					<tr>
						<td>Var2</td>
						<td>values values values values values values values values values values </td>
					</tr>
				</table>
			</td>
			<td id="msg">
				<table class="darkHeader">
					<tr>
						<th>Message</th>
					</tr>
					<tr>
						<td>Dev 153 need more batteries!</td>
					</tr>
				</table>
			</td>		
		</tr>
	</TABLE>
	-->

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
<? 
	switch($_REQUEST['section']) {
	  case 'phones':
		$q = mysql_query('SELECT ID, last_connect_ts, gps, comments FROM phone WHERE LOCATE(":", gps) > 0');
		while($row = mysql_fetch_assoc($q)) {		
		  $loc = explode(':', $row['gps']);
		  print sprintf('tMarker=new GMarker(new GLatLng(%s,%s)); tMarker.PID=%d; tMarkers.push(tMarker);'."\n", 
			  $loc[0], $loc[1], $row['ID']);
		  
		  print sprintf('pPatientData[%d]="<b>%s</b><br/>'.
			  '%s";'."\n",
			  $row['ID'], 
			  $row['comments'], 
			  date('d-m-Y h:i', (3600*3)+$row['last_connect_ts'])
		  );
		}
		break;

	  default:
		$q = mysql_query('SELECT * FROM form_data ORDER BY ts');
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
			
			if ($showit) {
				print sprintf('tMarker=new GMarker(new GLatLng(%s)); tMarker.PID=%d; tMarkers.push(tMarker);'."\n", 
					$xml->patient_location, $row['ID']);
				
				print sprintf('pPatientData[%d]="<b>%s</b><br/>'.
					'%s year old %s<br/>'.
					'Temp: %sºC<br/>'.
					'%s %s<br/>%s";'."\n",
					$row['ID'], 
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
	}
?>
				    var markerCluster = new MarkerClusterer(pMap, tMarkers);
			  }	

	    }

	    </script>

	</div>
	
	<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;sensor=false&amp;key=<? include 'config/google_maps_key'; ?>" type="text/javascript"></script>
	<script src="js/jquery-1.3.2.min.js" type="text/javascript"></script>
	<script src="js/jquery-ui-1.7.2.custom.min.js" type="text/javascript"></script>
	<script src="js/markerclusterer_packed.js" type="text/javascript"></script>
</body>
</html>
