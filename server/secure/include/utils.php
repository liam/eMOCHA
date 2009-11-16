<?

 /*------------------------------------------------
    convierte un string en array trozeando por
    las no palabras
   ------------------------------------------------*/
  function str2array($str) {
    $arrA = preg_split('/[\W]+/', $str, -1, PREG_SPLIT_NO_EMPTY);
    return count($arrA) ? $arrA : array();
  }

 /*------------------------------------------------
    Comprueba si hay una palabra clave común
    en dos strings
      if (in('[verde][rojo][azul]', 'naranja'):
      if (in('opt,fix', 'ahn deu fix'));
   ------------------------------------------------*/
	function in($required, $privileges) {
		if ($required == '') return 1;
		$A = str2array($required);
		$B = str2array($privileges);
		return count(array_intersect($A,$B));
	}

?>