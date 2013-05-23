<%@page language="java"%>
<%@page import="fr.fg.server.i18n.Messages"%>
<%@page pageEncoding="utf-8" %>
<%@page contentType="text/html; charset=utf-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=Messages.getString("lang")%>">
	<head>
		<title><%=Messages.getString("common.title")%></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<style type="text/css">
			<!--
				body { background-color: black; color: #ffffff; margin: 10% 20% auto 20%; font: 10px verdana, arial, helvetica, sans-serif; }
				#errorbox { border: 1px solid #c00000; }
				h2 { margin: 0; color: #ffffff; background-color: #c00000; font-size: 12px; padding: 5px 4px; }
				#errorbox div { padding: 6px 5px; background-color: #202020; }
			-->
		</style>
	</head>
	<body>
		<div id="errorbox">
			<h2>Une erreur s'est produite</h2>
			<div>
				Erreur : <strong><%=request.getAttribute("error") != null ? request.getAttribute("error") : "???"%></strong>
			</div>
		</div>
	</body>
</html>
