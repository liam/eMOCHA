<?

	function connectToDB($settingsA) {

		$tCurrentPath = dirname(__FILE__).DIRECTORY_SEPARATOR;

		$db = @mysql_connect($settingsA['URL'], $settingsA['USR'], $settingsA['PWD']);
		if ($db) {
			@mysql_select_db ($settingsA['DB']);
			@mysql_query("SET CHARACTER SET utf8");
			@mysql_query("SET NAMES utf8");
		} else {
			include $tCurrentPath.'db_error.html';
			exit();
		}
	}

	// most of this file was created in the 20th century ...

	if (!isset($DEBUG)) $DEBUG = 'OFF';

	/* _________________________________________________________________________________________
		 sustituye a mysql_query para que las consultas se registren en el debug.
	*/
	function query($q)
	{
		global $DebugMainLayer;

	    $args = func_get_args();
	    if (count($args) > 1) {
	    	array_shift($args);
		    $args = array_map('mysql_real_escape_string', $args);
	    	array_unshift($args, $q);
	    	$q = call_user_func_array('sprintf', $args);	
		}

		$myQ = @mysql_query($q);

		if (isset($GLOBALS['DEBUG']) && $GLOBALS['DEBUG'] != 'OFF')
		{
			$parts = explode(' ', trim($q));
			$cmd = strtoupper($parts[0]);

			if ($myQ == false)
				$cmd = 'ERROR';

			switch ($cmd)
			{
				case 'SELECT':
					$numr = @mysql_num_rows($myQ);
					$numf = @mysql_num_fields($myQ);

					if ($numr)
						$style = 'Ok';
					else
						$style = 'Empty';
					DebugAdd($style,$q,"$numf x $numr mysql result");

					$DebugMainLayer .= "\n<table class='ST' border=1 cellpadding=2 cellspacing=1 width='100%'>";
					$DebugMainLayer .= "\n<tr>";
					if ($numr)
					{
						for($n = 0; $n < $numf; $n++)
							$DebugMainLayer .= '<td><B>&nbsp;'.mysql_field_name($myQ, $n).'</B></td>';
						$DebugMainLayer .= '</tr>';

						$ci = $rownum = 0;
						while (($row = mysql_fetch_row($myQ)) && ($rownum++ < 10))
						{
							$DebugMainLayer .= '<tr class="ST'.($ci++ % 2).'">';
							foreach($row AS $val) {
                $val = strlen($val) > 100 ? substr($val, 0, 100).'...' : $val;
								$DebugMainLayer .= '<td>&nbsp;'.$val.'</td>';
              }
							$DebugMainLayer .= '</tr>';
						}

						if ($rownum >= 10)
							$DebugMainLayer .= '<tr><td colspan="'.$numf.'">(sigue)</td></tr>';
					}
					$DebugMainLayer .= '</table>';
				break; // SELECT


				case 'INSERT':
				case 'UPDATE':
				case 'DELETE':
					$affr = @mysql_affected_rows();
					DebugAdd($affr ? 'Ok' : 'Empty',$q,"$affr affected rows");
					break;

				case 'ERROR':
					DebugAdd('Error',$q,@mysql_error());
					break;

				default:
					DebugAdd('Ok',$q,'unknown command OK');
					break;
			}

			@mysql_data_seek($myQ, 0);
		}
		return $myQ;
	}

	/* _________________________________________________________________________________________
		 devuelve el elemento (0,0) de una consulta, o nada si no obtiene ningn elemento.
		 $nombre = query1("SELECT nombre FROM usuarios WHERE id=7");
	*/
	function query1($q)
	{
		$qry = query($q);

		if($row = @mysql_fetch_row($qry))
			return $row[0];
		else
			return;
	}

	/* _________________________________________________________________________________________
		 devuelve la primera fila de una consulta en un array asociativo, o nada si no obtiene filas.
		 $usuario = queryRow("SELECT * FROM usuarios WHERE id=7");
		 print $usuario[nombre].' '.$usuario[apellidos];
	*/
	function queryRow($q)
	{
		$qry = query($q);

		if (@mysql_num_rows($qry))
			return @mysql_fetch_array($qry);
		else
			return;
	}

  /*
     Devuelve un array de arrays (dos dimensiones)
     $dataA = queryArrArr("SELECT id, nombre, tel FROM usrs");
     $dataA[$row] = array('id' => 3, 'nombre' => 'pedro', 'tel' => '912942');
     $dataA[$row]['nombre'] = 'pedro';
     count($dataA) indica el numero de rows.
     Indexado de manera automatica numericamente.
  */
  function queryArrArr($q) {
    $qry = query($q);

    if (@mysql_num_rows($qry) < 1)      return;

    for ($i = 0; $i < mysql_num_rows($qry); $i++) {
      mysql_data_seek($qry, $i);
      $pA[$i] = mysql_fetch_assoc($qry);
    }
    return $pA;
  }


  /*
  	 Devuelve un array asociativo en el que la primera columna es el $key
  	 y la segunda el $value. Permite un array inicial
  	 $nombresA = queryArray("SELECT id,concat(nombre,' ',apellidos) FROM usuarios", array(0 => '[selecciona un usuario]'));
  	 html_select($nombreA,nombres,$nombre);
	*/
	function queryArray($q, $defaultValueArray = 'empty')
	{
		$qry = query($q);

		if (is_array($defaultValueArray))
			$arr = $defaultValueArray;

		while($row = @mysql_fetch_row($qry))
			$arr[$row[0]] = $row[1];

    if(isset($arr)) {
			return $arr;
		}
	}

	/* _________________________________________________________________________________________
  	 Devuelve un array de dos o tres dimensiones

  	 Ejemplo 1________
		 $usrinfo = queryArrays("SELECT id, nombre, apellidos FROM usuarios");
		 print $usrinfo[nombre][3]; //nombre del usuario id=3
		 print $usrinfo[apellidos][3]; // apellidos de usuario id=3
		 foreach ($usrinfo[nombre] AS $id => $nombre) //para recorrer los nombres

  	 Ejemplo 2________Crear una tabla que permita mostrar todos los eventos en un proyecto
  	 Emplea dos indices: id del evento(unico) y proyecto al que pertenece(repetido)
  	 id en realidad no nos interesa, solo lo cojemos para indexar el array.

		 $evntpro = queryArrays("SELECT proyecto,id,  log,presupuesto FROM eventos", 2);
		 foreach($evntpro[3] AS $id => $epA)
		   print "PROYECTO 3: $epA[log] $epA[presupuesto] <br>"; // imprime log y presupuesto de los eventos con proyecto=3

  	 print $evntpro[3][5][log]; //imprime el log del proyecto 3 (con el id=5). Este uso no es nada util
  	 ya que obliga a conocer el id del evento.
	*/
	function queryArrays($q, $idxs = 1)
	{
		$qry = query($q);
		$rows = @mysql_num_fields($qry);
		for ($i = 1; $i < $rows; $i++)
			$fname[$i] = mysql_field_name($qry, $i);

		switch ($idxs)
		{
			case 1:
				while($row = @mysql_fetch_row($qry))
					for ($i = 1; $i < $rows; $i++)
						$arr[$fname[$i]][$row[0]] = $row[$i];
				break;
			case 2:
				while($row = @mysql_fetch_row($qry))
					for ($i = 2; $i < $rows; $i++)
						$arr[$row[0]][$row[1]][$fname[$i]] = $row[$i];
				break;
		}

    return $arr;
	}

	/* _________________________________________________________________________________________
		 Para codificar un string con un salt
	*/
	function encode($string, $code)
	{
		return rawurlencode(query1("SELECT encode('$string','$code')"));
	}

	/* _________________________________________________________________________________________
		 Para decodificar un string con un salt
	*/
	function decode($string, $code)
	{
		return query1("SELECT decode('".rawurldecode($string)."','$code')");
	}

	/* _________________________________________________________________________________________
		 Calcula el string para el Limit de un SELECT segn la p�ina actual y el epp (elements Per page)
	*/
	function makeLimitString($currentPage, $epp)
	{
		return 'LIMIT '.(($currentPage-1)*$epp).','.$epp;
	}

	/* _________________________________________________________________________________________
		 Guarda en $DebugDB.$DebugTable la informaci� de depuraci�
	*/
	function Debug()
	{
		global $_POST, $_GET, $_SERVER, $_FILES, $_REQUEST; // PHP 4.1
		global $DebugMainLayer, $DebugHREF, $DebugTable;

		if ($GLOBALS['DEBUG'] != 'OFF')
		{
			printt('$_GET',$_GET);
			printt('$_POST',$_POST);
			printt('$_FILES',$_FILES);

			$scriptname = $_SERVER['SCRIPT_NAME'];
			$html = addslashes($DebugMainLayer);
			mysql_query("DELETE FROM $DebugTable WHERE scriptname='$scriptname' OR ts<UNIX_TIMESTAMP()-86400*14");
			mysql_query("INSERT INTO $DebugTable SET scriptname='$scriptname', ts=UNIX_TIMESTAMP(), html='$html'");

			if ($GLOBALS['DEBUG'] == 'NOISY')
			{
				print '<DIV id="Layer1" style="position:absolute; z-index:1; background-color: #FF3333; layer-background-color: #FF3333; border: 1px solid orange; width: 10px; height: 10px; left: 1px; top: 1px; filter: Alpha(Opacity=70)">';
				print "<a href='$DebugHREF?scriptname=$scriptname' target='debugwindow'>&nbsp;&nbsp;&nbsp;&nbsp;</a>";
				print '</DIV>';
			}
		}
	}

	/* _________________________________________________________________________________________
		 Depura una variable
	*/
	function printt($Name, $Var)
	{
		global $DebugMainLayer;

		if ($GLOBALS['DEBUG'] != 'OFF'):
	    switch(gettype($Var)):
				case 'array':
					$Count = count($Var);
					DebugAdd($Count ? 'Ok' : 'Empty',$Name.'[]',"$Count element array");
					if($Count):
						$DebugMainLayer .= "\n<table class='ST' border=1 cellpadding=2 cellspacing=1 width='100%'>";
						$DebugMainLayer .= '<tr><td><B>Variable</B></td><td><B>Value</B></td></tr>';
						foreach ($Var AS $var => $val)
						{
							$i = ($i + 1) % 2;
							$class = $val ? 'Ok' : 'Empty';
							if (is_string($val)) $val = "'$val'";
							$DebugMainLayer .= "<tr class='ST$i'><td class='$class'>${Name}[$var] =</td><td class='$class'>$val</td></tr>";
						}
						$DebugMainLayer .= '</table>';
					endif; // $Count
					@reset ($Var);
					break;
				case 'string':
					DebugAdd($Var ? 'Ok' : 'Empty',"$Name = '$Var'",strlen($Var)." char string");
					break;
				case 'integer':
					DebugAdd($Var ? 'Ok' : 'Empty',"$Name = $Var",'integer');
					break;
				case 'double':
					DebugAdd($Var ? 'Ok' : 'Empty',"$Name = $Var",'double');
					break;
				default:
					DebugAdd($Var ? 'Ok' : 'Empty',"$Name = $Var",'unknown type');
			endswitch;
		endif;
	}

	/* _________________________________________________________________________________________
		 Funci� interna!
		 A�de informaci� a $DebugMainLayer, luego se guarda con DebugSave()
	*/
	function DebugAdd($Style,$Query,$Text)
	{
		global $DebugMainLayer;
		static $bool;

    $bgcolor = ($bool++ % 2) ? 'white': '#eeeeee';
		$DebugMainLayer .= '<table class="HD" border="1" cellpadding="4" cellspacing="4" width="90%">';
		$DebugMainLayer .= "<tr bgcolor='$bgcolor'><td class='$Style' width='50%'>$Query</td><td nowrap class='$Style'>$Text</td></tr>";
		$DebugMainLayer .= '</table>';
	}
?>