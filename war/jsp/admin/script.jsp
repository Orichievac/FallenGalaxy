<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@page language="java"%>
<%@page import="fr.fg.server.data.DataAccess"%>
<%@page import="fr.fg.server.util.Config"%>
<%@page pageEncoding="utf-8" %>
<%@page contentType="text/html; charset=utf-8"%>


<%
// Vérifie que le joueur est connecté et est un super administrateur
Integer sessionId = (Integer) session.getAttribute("id");

if (sessionId == null || DataAccess.getPlayerById(sessionId) == null || !DataAccess.getPlayerById(sessionId).hasRight(Player.SUPER_ADMINISTRATOR)) {
	request.setAttribute("error", "Vous n'avez pas les droits suffisants.");
	RequestDispatcher error = getServletContext().getRequestDispatcher(Config.getErrorServlet());
	error.forward(request, response);
}

response.setCharacterEncoding("UTF-8");

String output = "";
String script = "";
if (request.getParameter("script") != null) {
	script = request.getParameter("script");
	StringBuffer buffer = new StringBuffer();
    buffer.append("importPackage(Packages.fr.fg.server.data);\n");
    buffer.append("importPackage(Packages.fr.fg.server.data.base);\n");
    buffer.append("importPackage(Packages.fr.fg.server.dao);\n");
    buffer.append("importPackage(Packages.fr.fg.server.core);\n");
    
    buffer.append(script.replace("delete()", "remove()"));
    
    StringWriter outputWriter = new StringWriter();
	PrintWriter printWriter = new PrintWriter(outputWriter);
	
    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("JavaScript");
    engine.getContext().setWriter(printWriter);
    
    try {
	    engine.eval(buffer.toString());
    } catch (Exception e) {
    	e.printStackTrace(printWriter);
    }
    
    output = outputWriter.toString();
}
%>

<%@page import="fr.fg.server.i18n.Messages"%>
<%@page import="fr.fg.server.data.Player"%>
<%@page import="javax.script.ScriptEngineManager"%>
<%@page import="javax.script.ScriptEngine"%>
<%@page import="java.io.StringWriter"%>
<%@page import="java.io.PrintWriter"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=Messages.getString("lang")%>">
	<head>
		<title><%=Messages.getString("common.title")%></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<style type="text/css">
			<!--
				body { background-color: black; color: #ffffff; margin: 10% 20% auto 20%; font: 10px verdana, arial, helvetica, sans-serif; }
			-->
		</style>
	</head>
	<body>
		<form action="script.jsp" method="post">
			<p>
				<textarea name="script" cols="80" rows="10"><%=
					script.replace("&", "&amp;").
						replace("\"", "&quot;").
						replace("'", "&apos;").
						replace("<", "&lt;").
						replace(">", "&gt;") %></textarea>
				<br/>
				<input type="submit" value="Executer"/>
			</p>
		</form>
		<p>
			Result:<br/>
			<code>
				<%= output.replace("\n", "<br/>") %>
			</code>
		</p>
	</body>
</html>
