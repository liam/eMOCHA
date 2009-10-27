<?

 /*-------------------------------------------------
    DESPLEGABLE
   ------------------------------------------------*/
  function html_select ($array, $name, $selected = '', $extra = '', $label = '', $useStyle = false)  {
    if (!is_array($array))
      return;

    $str = html_label_up($label).
      "<select name='$name' id='$name' $extra>";

    foreach ($array AS $key => $value)
      $str .= sprintf("<option value='%s' %s %s>%s</option>",
        $key,
        $useStyle ? "class='$key'" : '',
        $key == $selected ? 'selected' : '', // in($key, $selected)
        htmlentities($value)
      );

    $str .= "</select>".
      html_label_dn($label);

    return $str;
  }

 /*------------------------------------------------
    RADIO
   ------------------------------------------------*/
  function html_radio ($name, $value='', $checked=0, $extra='') {
    return '<input type="radio" name="'.$name.'" value="'.$value.'"'.($checked ? ' checked' : '').' '.$extra.'>';
  }

 /*------------------------------------------------
    BUTTON
   ------------------------------------------------*/
  function html_button ($text, $class, $onclick, $title='') {
    return sprintf('<input type="button" value="%s" class="%s" title="%s" onClick="%s">', $text, $class, $title, $onclick);
  }

 /*------------------------------------------------
    SUBMIT
   ------------------------------------------------*/
  function html_submit ($text, $class, $onclick, $title='') {
    return sprintf('<input type="submit" value="%s" class="%s" title="%s" onClick="%s">', $text, $class, $title, $onclick);
  }

 /*------------------------------------------------
    CHECKBOX
   ------------------------------------------------*/
  function html_checkbox ($name, $checked=0, $text='', $extra='', $value = 1)  {
    return '<input type="checkbox" name="'.$name.'" value="'.$value.'"'.($checked ? ' checked' : '')." $extra>".htmlentities($text);
  }

 /*------------------------------------------------
    HIDDEN
   ------------------------------------------------*/
  function html_hidden ($dataA)  {
    $str = '';
    foreach ($dataA AS $name => $value)
      $str .= '<input type="hidden" name="'.$name.'" value="'.htmlentities($value).'">';
    return $str;
  }

 /*------------------------------------------------
    TEXT
   ------------------------------------------------*/
  function html_text ($name, $value='', $size = 8, $maxlength = 8, $extra = '', $label = '') {
    return html_label_up($label).
      '<input type="text" name="'.$name.'" size="'.$size.'" maxlength="'.$maxlength.'" value="'.htmlentities($value).'" '.$extra.'>'.
      html_label_dn($label);
  }

 /*-------------------------------------------------
	CSS SELECT
   -------------------------------------------------*/
	function html_selectbox($name, $options, $selected = "", $class = "") {
		$return = '<select name="'.$name.'" id="'.$name.'" class="'.$class.'">';
		foreach ($options as $option) {
			$return .= '<option value="'.$option['value'].'"'. ($selected == $option['value'] ? ' selected="selected"' : '') .'>'.$option['label'].'</option>';
		}
		
		$return .= '</select>';
		
		return $return;
	}

 /*------------------------------------------------
    PASSWORD
   ------------------------------------------------*/
  function html_password ($name, $value='', $size=8, $maxlength=8, $extra='', $label = '')  {
    return html_label_up($label).
      '<input type="password" name="'.$name.'" size="'.$size.'" maxlength="'.$maxlength.'" value="'.htmlentities($value).'" '.$extra.'>'.
      html_label_dn($label);
  }

 /*------------------------------------------------
    TEXTAREA
   ------------------------------------------------*/
  function html_textarea ($name, $value='', $cols=30, $rows=3, $extra='', $label = '')  {
    return html_label_up($label).
      '<textarea name="'.$name.'" wrap="VIRTUAL" '.$extra.' cols="'.$cols.'" rows="'.$rows.'">'.$value.'</textarea>'.
      html_label_dn($label);
  }

 /*------------------------------------------------
    TABLE
   ------------------------------------------------*/
  function html_table ($class, $dataA) {
    if($dataA) {
      $maxcols = 0;
      foreach ($dataA AS $rowA)
        if (count($rowA) - 1 > $maxcols)
          $maxcols = count($rowA) - 1;

      $str = "\n<table class='$class'>";
      foreach ($dataA AS $rowA) {
        $str .= "\n<tr class='$rowA[0]'>";
        for ($i = 1; $i < count($rowA); $i++) {
          $colspan = ($i == count($rowA) - 1 && $i < $maxcols) ? ' colspan='.($maxcols-$i+1) : '';
          $str .= "<td$colspan>$rowA[$i]</td>";
        }
        $str .= "</tr>";
      }
      $str .= "\n</table>";
      return $str;
    }
  }

 /*------------------------------------------------
    LABELS
   ------------------------------------------------*/
  function html_label_up ($label) {
    if ($label) {
          if ($label[0] == '^') return htmlentities(substr($label, 1)).'<br>';
      elseif ($label[0] == '<') return htmlentities(substr($label, 1));
    }
  }

  function html_label_dn ($label) {
    if ($label) {
          if ($label[0] == '_') return '<br>'.htmlentities(substr($label, 1));
      elseif ($label[0] == '>') return htmlentities(substr($label, 1));
    }
  }

 /*------------------------------------------------
    SWF
   ------------------------------------------------*/
  function html_swf($src, $w = 100, $h = 100, $bgcolor = '#FFFFFF', $id = '') {
    $imgsrc = preg_replace('/(\.swf.+)$/', '.swf.gif', $src);
    $str = <<<FIN
<OBJECT TYPE="application/x-shockwave-flash" DATA="$src" WIDTH="$w" HEIGHT="$h" ID="$id">
  <PARAM NAME=movie VALUE="$src" />
  <PARAM NAME=quality VALUE="high" />
  <PARAM NAME=bgcolor VALUE="$bgcolor" />
  <PARAM NAME=menu VALUE="false" />
  <IMG SRC="$imgsrc" WIDTH="$w" HEIGHT="$h" ALT="You need to download a new flash plugin" />
</OBJECT>
FIN;
/*
    $str = "<OBJECT classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' ".
      "codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0' ".
      "WIDTH=$w HEIGHT=$h ID='$id'>".
      "<PARAM NAME=movie VALUE='$src'> <PARAM NAME=quality VALUE=high> <PARAM NAME=bgcolor VALUE=$bgcolor> <PARAM NAME=menu VALUE=false>".
 			"<EMBED src='$src' menu=false quality=high bgcolor=$bgcolor NAME='$id' WIDTH=$w HEIGHT=$h TYPE='application/x-shockwave-flash' ".
 			"PLUGINSPAGE='http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash'></EMBED>".
		  "</OBJECT>";
*/
		return $str;
  }

 /*------------------------------------------------
    AREAHEADER
   ------------------------------------------------*/
  function html_areaheader($content) {
    $str = <<<FIN
<table border="0" cellpadding="1" cellspacing="4">
  <tr><td background="i/dots1.gif">
    <table width="420" border="0" cellpadding="5" cellspacing="0" bgcolor="#CCCCCC">
      <tr><td>&nbsp; $content</td></tr>
    </table>
  </td></tr>
</table>

FIN;
    return $str;
  }

 /*------------------------------------------------
    DECOTABLE
   ------------------------------------------------*/
  function html_decotable($content) {
    $str = <<<FIN
<table border="0" cellpadding="1" cellspacing="4">
  <tr><td background="i/dots2.gif">
    <table border="0" cellpadding="10" cellspacing="0" bgcolor="#FFFFFF">
      <tr><td>$content</td></tr>
    </table>
  </td></tr>
</table>

FIN;
    return $str;
  }

 /*------------------------------------------------
    INCLUDE
   ------------------------------------------------*/
  function html_include($path) {
    if ($filecontent = @file($path))
      if (preg_match("/\<body.*?\>(.*)\<\/body\>/is", implode('', $filecontent), $found))
        return $found[1];
  }
?>