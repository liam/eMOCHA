<h1>Your files</h1>
<?

	//include_once 'include/file.php';
	//include_once 'include/image.php';

	$path = "upload/$userA[usr]/";

	$filesA = scan_dir($path, '\w+.*');
?>
<table>
	<tr>
		<th>Name</th>
		<th>Size</th>
		<th>Preview</th>
		<th>Operation</th>
	</tr>
<?
	if (is_array($filesA) && count($filesA)>0) {
		foreach($filesA AS $file) {
			$size = number_format(filesize($path.$file), 0, ',', '.');
?>
	<tr id="row_<?=$file;?>">
		<td><?=$file;?></td>
		<td><?=$size;?></td>
		<td><? print getHTMLThumb($path.$file); ?></td>
		<td><input type="button" class="delete" id="but_<?=$file;?>" value="delete" /></td>
	</tr>
<?
		}
	}
?>
	<tr id="placeHolder"></tr>
</table>

<h1>Upload new files</h1>
<input id="upfile" type="file" size="40" name="upfile" />
<input id="but_upload" type="button" value="upload" />

<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript">
// <![CDATA[

	$("INPUT.delete").bind("click", requestDelete);
	$("#but_upload").bind("click", requestUpload);

	function requestUpload() {
		$("#loading").ajaxStart(function() {
			$(this).show();
		}).ajaxComplete(function(){
			$(this).hide();
		});
		$.ajaxFileUpload({
			url:			'index.php',
			secureuri:		false,
			fileElementId:	'upfile',
			dataType: 		'json',
			success: function (tData, status) {
				onCMD(tData);
			}, error: function (tData, status, e) {
				onCMD(tData);
				//alert(e);
			}
		});
	}

	function requestDelete(e) {
		if(e.shiftKey || confirm('Are you sure? Forever?')) {
			var path = this.id.substr(1 + this.id.indexOf('_'));
			sendAjaxCMD("deleteFile", {
				filename:path
			});
		}
	}

	function onCMD(tData) {
		switch(tData.command) {
			case "deleteFile":
				if (tData.status == "OK") {
					$(fixID("row_" + tData.filename)).remove();
				}
				break;
			case "uploadFile":
				if (tData.status == "OK") {
					var ID = "row_" + tData.name;
					$("#placeHolder").before('<tr id="' + ID + '"><td>' + tData.name +
						'</td><td>' + tData.size + '</td><td>' + unescape(tData.html) + '</td>' +
						'<td><input type="button" class="delete" id="but_' + tData.name + '" value="delete" /></td></tr>');

					$("INPUT.delete").bind("click", requestDelete);
				}
				break;
		}
		setStatus(tData.status, tData.msg);
	}
// ]]>
</script>