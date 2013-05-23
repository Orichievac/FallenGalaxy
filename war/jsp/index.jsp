<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@page import="fr.fg.server.i18n.Messages"%>
<%@page import="fr.fg.server.util.Config"%>
<%@page import="fr.fg.server.data.Player"%>
<%@page import="fr.fg.server.data.DataAccess"%>
<%@page import="fr.fg.server.data.GameConstants"%>

<%@page language="java"%>
<%@page pageEncoding="utf-8" %>
<%@page contentType="text/html; charset=utf-8"%>


<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title><%= Messages.getString("common.title") %></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<style type="text/css">
			body { background-color: black; }
			
			#preloading {
				position: absolute;
				top: 50%;
				left: 50%;
				width: 250px;
				margin: -25px 0 0 -125px;
				font-size: 16px;
				font-family: Lucida Grande, Bitstream Vera Sans, Verdana, sans-serif;
				vertical-align: middle;
				background-color: #202020;
				border: 1px solid #404040;
				padding: 10px;
				color: white;
				-webkit-border-radius: 4px;
				-moz-border-radius: 4px;
			}
		</style>
<%
	// Récupère la skin du joueur
Player player = null;
if (session.getAttribute("id") != null)
	player = DataAccess.getPlayerById((Integer) session.getAttribute("id"));

String playerSkin = GameConstants.THEMES[0];

if (player != null) {
	playerSkin = player.getSettingsTheme();
} 	//else {
	//if (request.getCookies() != null) {
		//for (Cookie cookie : request.getCookies()) {
			//if (cookie.getName().equals("theme"))
				//playerSkin = cookie.getValue();
		//}
	//}
	//}

%>
		<!-- Style -->
		<link rel="stylesheet" type="text/css" href="<%= Config.getMediaURL() %>style/imports/base.css"/>
		<link rel="stylesheet" type="text/css" href="<%= playerSkin %>/style.css"/>
		<!--[if IE]>
		<link rel="stylesheet" type="text/css" href="<%= playerSkin %>/cursors.css"/>
		<![endif]-->
		<!-- IPhone -->
		<!--[if !IE]>
		<script>
			window.scrollTo(0, 1);
		</script>
		<![endif]-->
	</head>
	<body>
		<div id="console" style="width: 400px; height: 200px; overflow: scroll; z-index: 100000; position: absolute; top: 0; left: 0; font-size: 9px; display: none;"></div>
		<div id="preloading">
			<div style="font-weight: bold;">Fallen Galaxy</div>
			<div id="preloadingStatus" style="color: #808080;"><%= Messages.getString("common.initialization") %></div>
		</div>
		<script type="text/javascript">
			var config = {
				serverUrl : '<%= Config.getServerURL() %>',
				mediaUrl : '<%= Config.getMediaURL() %>',
				theme : '<%= playerSkin %>',
				debug : false
			};
			document.oncontextmenu = function() { return false; };
		</script>
		<script type="text/javascript" src="<%= Config.getMediaURL() %>client.nocache.js"></script>
	</body>
</html>
