<?

	/*
		return some HTML pointing to a thumbnail for the give source file.
		* if the source file is an image, send an IMG tag pointing to a thumbnail.
		* if there is no thumbnail yet, create it.
		* if the source is a zip file, send a list of contained files.
	*/
	function getHTMLThumb($source) {
		$info = pathinfo($source);

		switch($info['extension']) {
			case 'zip':
				$zip = new ZipArchive;
				$fp = $zip->open($source);
				if ($fp === TRUE) {
					$i = 0;
					$result = '<small>';
					do {
						$zipInfo = $zip->statIndex($i++);
						if ($zipInfo) {
							$result .= $zipInfo['name'].'<br/>';
						}
					} while ($zipInfo);
					$zip->close();
					$result .= '</small>';
					return $result;
				} else {
					return '(error reading zip file)';
				}
				break;

			case 'gif':
			case 'jpg':
			case 'jpeg':
			case 'png':
				$thumb = $info['dirname'].'/.'.$info['basename'].'.jpg';
				if (is_file($thumb)) {
					return "<img src='$thumb' />";
				} else {
					if (FixNSaveImage($source, $thumb, 120, 120, 0, 80)) {
						return "<img src='$thumb' />";
					} else {
						return '(error creating thumbnail)';
					}
				}
				break;
			default:
				return '(unknown type)';
				break;
		}
	}

  /* Funcion para copiar/convertir y guardar una imagen.
     30.03.2004 version que no necesita programas externos (convert)
	 04.09.2007 function_exists() call_user_func()

     FixNSaveImage(
       $rutaImagenOrigen,      // '/tmp/imagen.gif'
       $rutaImagenDestuno,     // '/idb/imagen.jpg'
       $ancho,                 // pixels
       $alto,                  // pixels
       $forzar,                 // entero:
             No forzar:
               0 - Reduce la imagen para que no sobrepase width/height, pero NO sabemos que tamaño saldrá (por defecto)
             Forzar tamaño a width x height, sin deformar, empleando el máximo espacio disponible:
               1 - Reduce o amplia enfocando arriba o a la izquierda.
               2 - Reduce o amplia enfocando al centro.
               3 - Reduce o amplia enfocando abajo o a la derecha.
       $calidad,                // 0-100, por defecto 80 (solo para jpegs)
       $funcionPostproceso      // nombre de la funcion que procesara la imagen
     );
  */

  function FixNSaveImage($in, $out, $width, $height, $force = 0, $quality = 99, $fixWith = '') {
    $sizeA = calcSizes($in, $width, $height, $force);

    $destImage = imageCreateTrueColor($sizeA['dstW'], $sizeA['dstH']);
    // should be imageCreateTrueColor, but only available with GD2 | imageCreate

    switch($sizeA[2]) {
      case 1:        $srcImage = imagecreatefromgif($in);        break;
      case 2:        $srcImage = imagecreatefromjpeg($in);       break;
      case 3:        $srcImage = imagecreatefrompng($in);        break;
      default:       return;
    }

    // should be imageCopyResampled, but only available with GD2 | imageCopyResized
    imageCopyResampled($destImage, $srcImage, 0, 0,
                                              $sizeA['srcX'], $sizeA['srcY'],
                                              $sizeA['dstW'], $sizeA['dstH'],
                                              $sizeA['srcW'], $sizeA['srcH']);

    if($fixWith != '' && function_exists('fixImage')) {
			call_user_func('fixImage', $fixWith, $destImage);  // fixImage('addGreenBorders', $im);
		}

    switch(strtolower(substr($out, -4))) {
      case '.gif': imagegif($destImage, $out); break;
      case '.jpg': imagejpeg($destImage, $out, $quality); break;
      case '.png': imagepng($destImage, $out); break;
    }

    if(!is_file($out)) {
      return;
	}

    return $out; // returns path to new image
  }

  // CALC SIZE (depending on force)
  function calcSizes($imgPath, $reqW, $reqH, $force) {
	  if (!defined("WIDTH")) {
      define("WIDTH", 0);
    }
    if (!defined("HEIGHT")) {
      define("HEIGHT", 1);
    }
    $srcImgA = getimagesize($imgPath);

    $propW = $reqW / $srcImgA[WIDTH];         // 1 = ancho exacto   >1 = estrecha   <1 = ancha
    $propH = $reqH / $srcImgA[HEIGHT];         // 1 = alto  exacto   >1 = baja       <1 = alta

    $p = $propH > $propW;  // bool : más demasiado-bajo que demasiado-estrecho

    if ($force == 0) {
      $srcImgA['srcX'] = 0;     $srcImgA['srcY'] = 0;    $srcImgA['srcW'] = $srcImgA[0];    $srcImgA['srcH'] = $srcImgA[1];
      $minprop = min($propW, $propH, 1); // por donde se pasa más
      if ($minprop > 1) {
        $minprop = 1; // si es demasiado pequeña, no agrandarla, dejarla igual
      }
      $srcImgA['dstW'] = $srcImgA[WIDTH] * $minprop;
      $srcImgA['dstH'] = $srcImgA[HEIGHT] * $minprop;
    } else {
      $dstAspect = $reqH / $reqW;
      if ($p) {
        $srcImgA['srcY'] = 0;
        $srcImgA['srcW'] = $srcImgA[HEIGHT] / $dstAspect;
        $srcImgA['srcH'] = $srcImgA[HEIGHT];
        switch ($force) {
          case 1: // FORCE TOP LEFT
            $srcImgA['srcX'] = 0;
            break;
          case 2: // FORCE CENTER
            $srcImgA['srcX'] = floor(($srcImgA[WIDTH]-$srcImgA['srcW'])/2);
            break;
          case 3: // FORCE BOTTOM RIGHT
            $srcImgA['srcX'] = $srcImgA[WIDTH]-$srcImgA['srcW'];
            break;
        }
      } else {
        $srcImgA['srcW'] = $srcImgA[WIDTH];
        $srcImgA['srcH'] = $srcImgA[WIDTH] * $dstAspect;
        $srcImgA['srcX'] = 0;
        switch ($force) {
          case 1: // FORCE TOP LEFT
            $srcImgA['srcY'] = 0;
            break;
          case 2: // FORCE CENTER
            $srcImgA['srcY'] = floor(($srcImgA[HEIGHT]-$srcImgA['srcH'])/2);
            break;
          case 3: // FORCE BOTTOM RIGHT
            $srcImgA['srcY'] = $srcImgA[HEIGHT]-$srcImgA['srcH'];
            break;
        }
      }
      $srcImgA['dstW'] = $reqW;
      $srcImgA['dstH'] = $reqH;
    }

    return $srcImgA;
  }

?>