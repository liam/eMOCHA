<?
	ini_set('display_errors', 1);
	ini_set('error_reporting', E_ALL & ~E_NOTICE);

	session_start();

	include_once "include/config.php";
	include_once "include/consts.php";
	include_once "include/db.php";
	include_once "include/html.php";
	include_once "include/file.php";
	include_once "include/utils.php";
	include_once "include/buildMenu.php";

	connectToDB($Config['DB']['MAIN']);

	switch($_REQUEST['cmd']) {
		case 'setApp':
			$_SESSION['app'] = $_REQUEST['param1'];
			break;
		case 'logout':
			query("DELETE FROM sys_session WHERE session_id='".session_id()."'");
			break;
		case 'login':
			$userA = queryRow("SELECT usr, groups ".
				"FROM sys_user ".
				"WHERE usr='$_REQUEST[param1]' AND pwd=PASSWORD('$_REQUEST[param2]')");

			if ($userA['usr']) {
				$menuPartsA = buildMenuHTML($userA['groups']);
				query("DELETE FROM sys_session WHERE usr='$userA[usr]'");
				query("INSERT INTO sys_session ".
					"SET usr='$userA[usr]', menuHTML='$menuPartsA[menu]', submenuHTML='$menuPartsA[submenu]', ts=UNIX_TIMESTAMP(), session_id='".session_id()."'");
			} else {
				//print "wrong pass ";
			}
			break;
	}

	$userA = queryRow("SELECT S.ts, S.menuHTML, S.submenuHTML, U.alias, U.groups, U.usr ".
		"FROM sys_session S ".
		"LEFT JOIN sys_user U ON U.usr=S.usr ".
		"WHERE S.session_id='".session_id()."'");

	if (!isset($_SESSION['app'])) {
		$_SESSION['app'] = 'default';
	}

	$Config['PATH']['CURRENT'] = "apps/$_SESSION[app]/";

	// TODO: validate if current user can target current app ajax.php
	if (isset($_POST['ajaxPath']) && $userA['alias']) {
		include "include/ajax.php";
		include "$_POST[ajaxPath]ajax.php";
		exit();
	}
			
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<title>eMOCHA</title>
	<link rel="stylesheet" type="text/css" href="css/main.css">
	<!--link rel="stylesheet" type="text/css" href="css/admin.css"-->
	<link type="text/css" href="css/custom-theme/jquery-ui-1.7.2.custom.css" rel="stylesheet" />	
	<LINK REL="SHORTCUT ICON" HREF="favicon.ico">
	<script src="js/jquery-1.3.2.min.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
		    $("#tabs").tabs({
		    	event: 'mouseover'
		    });
		    //select tab on start up
		    <? if ($_REQUEST['sec']) { ?>
			$("#tabs").tabs('select', <? print $_REQUEST['sec']; ?>);
			<? } ?>		    		    
		});
	</script>
</head>
<body onload="initialize()" onunload="GUnload()">
<form method="post" name="form1" action="index.php" enctype="multipart/form-data">
	
	<div id="content">
	
	<table id="header" class="fullWidth">
		<tr>
			<td id="banner" class="ui-corner-all">
				<img src="images/banner_cut.gif">
				<img src="images/eMOCHA_logo.png" />
			</td>
			<td id="login" class="ui-corner-all">	
				<?
					if ($userA['alias']) {
						print "Hello $userA[alias]! ".
							'<span id="send_logout" class="activelink link">Log out</span>.<br/>';
					} else {
						print 'Username: '.html_text('usr_name', 'user', 8, 32, 'id="usr_name"').'<br/>';
						print 'Password: '.html_password('usr_pwd', 'pass', 8, 32, 'id="usr_pwd"').'<br/>';
						print '<input type="button" onclick="login()" value="Sign in" />';
					}
				?>							
			</td>
			<td id="status" class="ui-corner-all">Welcome</td>
		</tr>
	</table>

	<div id="tabs" class="ui-tabs ui-widget ui-widget-content ui-corner-all">
		<ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
			<? print $userA['menuHTML']; ?>
		</ul>

		<TABLE id="mid" class="fullWidth">
			<tr>
				<td id="menu" class="ui-corner-all">
					<? print $userA['submenuHTML']; ?>
				</td>
				<td id="map">
					<? if ($userA['usr']) { ?>
					<div id="app">
						<img id="loading" src="images/ajax-loader.gif" style="display:none;">
						<?
							$appPath = $Config['PATH']['CURRENT'].'app.php';
							if (is_file($appPath)) {
								include $appPath;
							} else {
								print "<br/>$appPath requested, but does not exist!";
							}
					
							// if the app has a style.css file, load it
							if (is_file($Config['PATH']['CURRENT'].'style.css')) {
								print sprintf('<link rel="stylesheet" type="text/css" href="%sstyle.css">', $Config['PATH']['CURRENT']);
							}
						?>
					</div>
					<? } ?>
				</td>
			</tr>
		</TABLE>
	</div>
		
	<table id="bottom" class="fullWidth">
		<tr>
			<td id="graphs" class="ui-corner-all">(a)</td>
			<td id="msg" class="ui-corner-all">(b)</td>
		</tr>
	</table>

	</div>
	
	<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;sensor=false&amp;key=<? include 'config/google_maps_key'; ?>" type="text/javascript"></script>
	<script src="js/jquery-ui-1.7.2.custom.min.js" type="text/javascript"></script>
	<script src="js/markerclusterer_packed.js" type="text/javascript"></script>
	<script src="js/text/helpers.js" type="text/javascript"></script>
	<script src="js/main.js" type="text/javascript"></script>

	<input type="hidden" name="cmd" value="">
	<input type="hidden" name="param1" value="">
	<input type="hidden" name="param2" value="">
	<input type="hidden" name="sec" value="">					
</form>
</body>
</html>
