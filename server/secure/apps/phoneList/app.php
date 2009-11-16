<?

	// make a list of some kind from DB

	if ($_REQUEST['cmd'] == 'addPhone') {
		print_r($_REQUEST);
	}

	$list = '';
	$q = query("SELECT * FROM phone ORDER BY imei");
	while($row = mysql_fetch_assoc($q)) {
		$list .= sprintf('<tr><td id="onClickItem_%s" class="activelink link">%s</td><td>%s</td><td>%s</td><td>%s</td></tr>',
			$row['ID'],
			$row['imei'],
			$row['last_connect_ts'],
			$row['active'],
			nl2br(htmlentities($row['comments'])));
	}

?>

<h1>Available phones</h1>

This tool lets you manage the list of active phones that can communicate with the server. 
It can be used to add new phones, or to disable a phone in case it has been lost.

<table id="users" class="ui-widget ui-widget-content">
	<thead>
		<tr class="ui-widget-header ">
			<th>IMEI</th>
			<th>Last connection</th>
			<th>Active</th>
			<th>Comments</th>
		</tr>
	</thead>
	<tbody>
		<?=$list;?>
	</tbody>
</table>

<input id="addPhone" type="button" value="add new phone" />


<div id="dialog" title="Add / Edit phone information">
	<label for="f_imei">IMEI</label>
	<input type="text" name="f_imei" id="f_imei" class="text ui-widget-content ui-corner-all" />

	<label for="f_pwd">Password</label>
	<input type="text" name="f_pwd" id="f_pwd" class="text ui-widget-content ui-corner-all" />
	
	<label for="f_comments">Comments</label>
	<textarea name="f_comments" id="f_comments" class="text ui-widget-content ui-corner-all"></textarea>

	<input type="checkbox" name="f_active" id="f_active" />
	<label for="f_active">Active (can send data to server)</label>
</div>

<div id="header"></div>
<script type="text/javascript">
// <![CDATA[
	$(function() {
		$('#addPhone').click(function() {
			$('#dialog').dialog('open');
			$('#f_imei').val('');
		})
		//$("#addPhone").bind("click", addPhone);
		
		$('#dialog').dialog({
			autoOpen:false,
			modal: true,
			width: 350,
			buttons: {
				'Cancel' : function() {
					$(this).dialog('close');					
				},
				'Save' : function() {
					send("addPhone");
				}
			}
		});
	});

	function onClickItem(tID) {
		$('#dialog').dialog('open');
		$('#f_imei').val($("#onClickItem_" + tID).text());
	}

	function addPhone() {
		var tIMEI = $("#f_newimei").val();
		
		if (tIMEI.length != 15) {
			$("#f_newimei").addClass("err").focus();
			alert("To add a new phone please enter a 15 digit IMEI code");
		} else {
			$("#f_newimei").removeClass("err");
			sendAjaxCMD("addPhone", {
				imei: tIMEI
			});
		}
	}

	function onCMD(tData) {
		switch(tData.command) {

			case "addPhone":
				if (tData.status == "OK") {
					$('#app_list').append('<span id="onClickItem_' + tData.newID + '" class="link">' + tData.imei + '</span><br/>');
					$("#f_newimei").val('');
				}
				break;
		}
		setStatus(tData.status, tData.msg);
	}
	

// ]]>
</script>