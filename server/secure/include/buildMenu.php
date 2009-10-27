<?

	function buildMenuHTML($tGroups) {
		$tGroupsA = explode(',', $tGroups);
		if (count($tGroupsA) > 0) {
			$tCondA = Array();
			foreach($tGroupsA AS $tGroup) {
				$tCondA[] = "FIND_IN_SET('$tGroup', groups)>0";
			}
			$tCond = implode(' OR ', $tCondA);

			$tMenu = '';
			$tSubmenu = '';
			$tMenuA = queryArray("SELECT ID, name FROM sys_menu WHERE $tCond ORDER BY `order`");
			foreach($tMenuA AS $tParentID => $tName) {
				$tMenu .= sprintf('<li class="ui-corner-top ui-state-default"><a href="#tabs-%s">%s</a></li>'.BR, $tParentID, $tName);

				$tSubm = '';
				$tSubmenuA 	= queryArray("SELECT ID, name FROM sys_submenu WHERE ($tCond) AND parentID='$tParentID' ORDER BY `order`");
				if (is_array($tSubmenuA)) {
					foreach($tSubmenuA AS $tID => $tName) {
						$tSubm .= sprintf('<input type="button" id="send_setApp_%s" class="activelink ui-corner-all ui-state-default" value="%s" /><br /> '.BR, $tID, $tName);
					}
				}

				$tSubmenu .= sprintf('<div id="tabs-%s">%s</div>'.BR, $tParentID, $tSubm);
			}
			return Array(
				'menu' => $tMenu, 
				'submenu' => $tSubmenu
			);
		}
	}

?>