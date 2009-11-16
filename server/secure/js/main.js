// for AS3 programmers ;)
function trace(tStr) {
	alert(tStr);
}
function send(cmd, param1, param2) {
	var F = document.form1;
	F.cmd.value    = cmd;
	F.param1.value = param1;
	F.param2.value = param2;
	F.sec.value    = $('#tabs').tabs('option', 'selected');
	F.submit();
}
function sendToFlash(tTarget, tData) {
	var swf = document.getElementById(tTarget);
	swf.fromJS(tData);
}
function sendAjaxCMD(tCMD, tObj) {
	if(tObj == undefined) {
		tObj = { }
	}
	tObj.ajaxPath =	Config.path.current;
	tObj.command  = tCMD;
	$.post("index.php", tObj, onCMD, "json");
}
function setHeader(tMsg) {
	$("#header").text(tMsg);
}
function setStatus(tStatus, tMsg) {
	$("#status").removeClass("st_OK st_ERR").addClass("st_" + tStatus).text(tMsg || ".");
}
function displayFlash(tSWF, tDIV, tID, tWidth, tHeight) {
	var attributes = {
		id:   tID,
		name: tID
	};

	swfobject.embedSWF(Config.path.current + tSWF + ".swf", tDIV, tWidth, tHeight, "9.0.0", null, null, null, attributes);
}
function fixID(tID) {
	return '#' + tID.replace(":","\\:").replace(".","\\.");
}


function login() {
	var tUser = $("#usr_name").val();
	var tPwd  = $("#usr_pwd").val();
	if (tUser != "user" && tPwd != "pass") {
		send('login', tUser, tPwd);
	} else {
		$("#user").css("background-color", "#F00");
		$("#user").oneTime(500, function() {
			$("#user").css("background-color", "EEE");
		});
	}
}
function hideMenu(e) {
	if ($(e.target).parent().attr("id") != "menu") {
		$('.subMenu').hide();
		$("BODY").unbind("click", hideMenu);
	}
}

// On page load:
$(function() {
	// clicked something with class=".activelink"
	// the ID contains the name of a function
	// and the parameters to send to that function,
	// separated by underscore: id="send_setApp_furnitureRegPoint" class=".link"
	$('.activelink').click(function() {
		var tParts = this.id.split("_");
		new Function(tParts.shift() + "('" + tParts.join("','") + "')")();
	});
	$('DIV.activechildren').click(function(ev) {
	    if ((this != ev.target) && ev.target.id) {
		    var tParts = ev.target.id.split("_");
		    new Function(tParts.shift() + "('" + tParts.join("','") + "')")();
        }
    });

	$('#menu SPAN').mouseup(function(e) {
		var tParts  = this.id.split("_");
		var tOffset = $(e.target).offset();
		$('.subMenu').hide();
		$("#submenu_" + tParts[1]).show().css("left", tOffset.left);
		$("BODY").bind("click", hideMenu);
	});

	if ($('#usr_name').length) {
		$('#usr_name').focus(function(e) {
			var t = $(this).val();
			if (t == "user") {
				$(this).val("");
			}
		});
		$('#usr_name').blur(function(e) {
			var t = $(this).val();
			if (t == "") {
				$(this).val("user");
			}
		});
		$('#usr_name').keypress(function(e) {
			if (e.which == 13) {
				login();
			}
		});

		$('#usr_pwd').focus(function(e) {
			var t = $(this).val();
			if (t == "pass") {
				$(this).val("");
			}
		});
		$('#usr_pwd').blur(function(e) {
			var t = $(this).val();
			if (t == "") {
				$(this).val("pass");
			}
		});
		$('#usr_pwd').keypress(function(e) {
			if (e.which == 13) {
				login();
			}
		});

		$('#usr_name').trigger("focus");
		
	}

});
