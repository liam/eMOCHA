<?
	$Config = Array();

	$current_path = dirname(__FILE__).DIRECTORY_SEPARATOR;
	$local_config_file = realpath($current_path.'config_local.php');

	// -- sdcardSync --

	// File where to store a timestamp. Time when last file system scan looking
	// for changed files took place
	$Config['PATH']['LASTFSCHECK'] 	= 'config/lastFSCheck';

	// File where to store a timestamp. Time when last write on database took place.
	// Phones will store a copy of this value. If the value doesn't change, phones
	// know there is nothing new to download. If it has changed, then they must
	// compare the file details to know if something must be downloaded or not.
	$Config['PATH']['LASTDBCHANGE'] = 'config/lastDBChange';

	// Calls to this file after these many seconds will trigger a file system
	// scan looking for changed files. Maybe once every 30 minutes? Lower is
	// better in case a file must be urgently fixed in all phones, but also
	// uses more resources. The resources used are in proportion to the number
	// of files in the folder. If the number of files is low, one could set a
	// very low value for this constant (30 seconds for example). 
	$Config['TIME']['FSCHECKFREQ'] = 10; // seconds


	$Config['PATH']['SDCARDFOLDER']	= 'sdcard/emocha';

	if(is_file($local_config_file)) {
		include($local_config_file);
	    return;
	}

	$Config['DB']['MAIN'] = Array(
  		'URL' => '',
    	'USR' => '',
    	'PWD' => '',
    	'DB'  => ''
	);

?>