<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@page language="java"%>
<%@page pageEncoding="utf-8" %>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="fr.fg.server.data.Player"%>
<%@page import="fr.fg.server.util.Config"%>
<%@page import="java.util.Calendar"%>
<%@page import="fr.fg.server.i18n.Messages"%>
<%@page import="fr.fg.server.data.DataAccess"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>

<%
// Vérifie que le joueur est connecté et est un super administrateur
Integer sessionId = (Integer) session.getAttribute("id");

if (sessionId == null || DataAccess.getPlayerById(sessionId) == null || !DataAccess.getPlayerById(sessionId).hasRight(Player.SUPER_ADMINISTRATOR)) {
	request.setAttribute("error", "Vous n'avez pas les droits suffisants.");
	RequestDispatcher error = getServletContext().getRequestDispatcher(Config.getErrorServlet());
	error.forward(request, response);
}

// Charge les paramètres de la requête
Calendar calendar = Calendar.getInstance();

int day   = parseInt(request.getParameter("day"),     1, 31, calendar.get(Calendar.DAY_OF_MONTH));
int month = parseInt(request.getParameter("month"),   1, 12, calendar.get(Calendar.MONTH) + 1);
int year  = parseInt(request.getParameter("year"), 2000, 2100, calendar.get(Calendar.YEAR));
Level minLevel = request.getParameter("level") != null ? Level.toLevel(request.getParameter("level"), Level.ALL) : Level.ALL;
int minHour    = 						 parseInt(request.getParameter("minhh"), -1, 23, -1);
int minMinutes = minHour 	== -1 ? -1 : parseInt(request.getParameter("minmm"), -1, 59, -1);
int minSeconds = minMinutes == -1 ? -1 : parseInt(request.getParameter("minss"), -1, 59, -1);
int maxHour    = 						 parseInt(request.getParameter("maxhh"), -1, 23, -1);
int maxMinutes = maxHour 	== -1 ? -1 : parseInt(request.getParameter("maxmm"), -1, 59, -1);
int maxSeconds = maxMinutes == -1 ? -1 : parseInt(request.getParameter("maxss"), -1, 59, -1);
String filter = request.getParameter("filter") != null ? request.getParameter("filter") : "";

response.setCharacterEncoding("UTF-8");
%>

<%!
// Convertit une chaîne de caractères en entier.
// Si la conversion échoue, la valeur par défaut est renvoyée (defaultValue).
// Si l'entier est hors de l'intervalle défini (minValue / maxValue), l'entier
// est ramené aux bornes de l'intervalle.
public int parseInt(String value, int minValue, int maxValue, int defaultValue) {
	if (value == null)
		return defaultValue;
	try {
		int parsedValue = Integer.parseInt(value);
		if (parsedValue < minValue)
			return minValue;
		if (parsedValue > maxValue)
			return maxValue;
		return parsedValue;
	} catch (Exception e) {
		return defaultValue;
	}
}

public String formatTime(int time) {
	if (time == -1)
		return "--";
	else if (time < 10)
		return "0" + time;
	else
		return String.valueOf(time);
}

public String searchLogs(int day, int month, int year,
		Level minLevel, int minHour, int maxHour,
		int minMinutes, int maxMinutes, int minSeconds,
		int maxSeconds, String filter) throws Exception {
	// Teste si le jour est aujourd'hui
	Calendar calendar = Calendar.getInstance();
	boolean today =
		day == calendar.get(Calendar.DAY_OF_MONTH) &&
		month == calendar.get(Calendar.MONTH) + 1 &&
		year == calendar.get(Calendar.YEAR);
	
	String fileName = Config.getServerLogFile() +
		(today ? "" : "." + year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day);
	File file = new File(fileName);
	LoggingSystem.getServerLogger().info(fileName);
	// Vérifie que le fichier de log existe
	if (!file.exists())
		return "<table cellspacing=\"0\"><tr><td><b>Log file does not exists!</b></td></tr></table>\n";
	
	BufferedReader reader = new BufferedReader(new FileReader(file));
	
	StringBuffer output = new StringBuffer();
	output.append("<table cellspacing=\"0\">\n");
	
	String line;
	int linesCount = 0;
	
	// Applique les critères de filtre sur chaque ligne des logs
	while ((line = reader.readLine()) != null) {
		if (line.length() < 15)
			continue;
		
		Level level = Level.toLevel(line.substring(0, 5).trim());
		int hour = Integer.parseInt(line.substring(6, 8));
		int minutes = Integer.parseInt(line.substring(9, 11));
		int seconds = Integer.parseInt(line.substring(12, 14));
		String log = line.substring(15);
		
		if ((minLevel != Level.ALL && !level.isGreaterOrEqual(minLevel)) ||
				(minHour    != -1 &&  minHour > hour) ||
				(minMinutes != -1 && (minHour == hour && minMinutes > minutes)) ||
				(minSeconds != -1 && (minHour == hour && minMinutes == minutes && minSeconds > seconds)) ||
				(maxHour    != -1 &&  maxHour < hour) ||
				(maxMinutes != -1 && (maxHour == hour && maxMinutes < minutes)) ||
				(maxSeconds != -1 && (maxHour == hour && maxMinutes == minutes && maxSeconds < seconds)) ||
				(!filter.equals("") && !log.contains(filter)))
			continue;
		
		if (linesCount < 500) {
			line = "<span class=\"logHeader\">" + line.substring(0, 15).replace(" ", "&nbsp;") + "</span>";
			log = log.replace("\n", "<br/>").replace(" ", "&nbsp;");
			
			if (!filter.equals(""))
				line += log.replace(filter, "<span class=\"highlight\">" +
					filter + "</span>");
			else
				line += log;
			
			output.append("<tr class=\"" + (linesCount % 2 == 0 ? "even" : "odd") +
				(level.isGreaterOrEqual(Level.WARN) ? " errorLog" : "") +
				"\"><td>" + line + "</td></tr>\n");
		}
		
		linesCount++;
	}
	
	if (linesCount > 500)
		output.append("<tr><td><b>" + (linesCount - 500) +  " other lines were " +
			"found in the log. These lines are not displayed.</b></td></tr>\n");
	
	if (linesCount == 0)
		output.append("<tr><td><b>No lines founds in log.</b></td></tr>"); 
	
	output.append("</table>\n");
	
	return output.toString();
}
%>


<%@page import="fr.fg.server.util.LoggingSystem"%><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=Messages.getString("lang")%>">
	<head>
		<title><%=Messages.getString("common.title")%></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<style type="text/css">
body { font: 11px verdana, arial, helvetica, sans-serif; margin: 0; }
div.header { background-color: black; padding: 8px; color: white; }
div.header .field { background-color: #ceff01; }
tr th { background-color: gray; padding: 4px; }
div.values input { margin: 0; padding: 0; border: 0; }
tr.odd, tr.odd input { background-color: #e8e8e8; }
tr.even, tr.even input { background-color: #cecece; }
tr:hover, tr:hover input { background-color: #ceff01 !important; }
table td { padding: 2px 10px; border-right: 1px solid gray; }
.highlight { background-color: #ffffa0; }
.logHeader { font-family: monospace; font-size: 11px; }
.errorLog { color: red; font-weight: bold; }
		</style>
	</head>
	<body>
		<form id="form" action="serverlog.jsp" method="get">
			<div class="header">
				Date (dd/mm/yyyy)
				<input name="day"   class="field" type="text" style="width: 20px;" maxlength="2" value="<%=day%>"/> /
				<input name="month" class="field" type="text" style="width: 20px;" maxlength="2" value="<%=month%>"/> /
				<input name="year"  class="field" type="text" style="width: 40px;" maxlength="4" value="<%=year%>"/>
				Min. log level
				<select name="level" class="field">
<%
Level[] LEVELS = {Level.ALL, Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR};
for (int i = 0; i < LEVELS.length; i++) {
%>
					<option value="<%=LEVELS[i].toString()%>"<%=minLevel.equals(LEVELS[i]) ? "selected=\"selected\"" : ""%>><%=LEVELS[i].toString()%></option>
<%
}
%>
				</select>
				Min. HH:MM:SS
				<input name="minhh" class="field" type="text" style="width: 20px;" maxlength="2" value="<%=minHour == -1 ? "" : minHour%>"/> :
				<input name="minmm" class="field" type="text" style="width: 20px;" maxlength="2" value="<%=minMinutes == -1 ? "" : minMinutes%>"/> :
				<input name="minss" class="field" type="text" style="width: 20px;" maxlength="2" value="<%=minSeconds == -1 ? "" : minSeconds%>"/>
				Max. HH:MM:SS
				<input name="maxhh" class="field" type="text" style="width: 20px;" maxlength="2" value="<%=maxHour == -1 ? "" : maxHour%>"/> :
				<input name="maxmm" class="field" type="text" style="width: 20px;" maxlength="2" value="<%=maxMinutes == -1 ? "" : maxMinutes%>"/> :
				<input name="maxss" class="field" type="text" style="width: 20px;" maxlength="2" value="<%=maxSeconds == -1 ? "" : maxSeconds%>"/>
				Contains text
				<input name="filter" class="field" type="text" style="width: 150px;" value="<%=filter.replaceAll("\"", "&quot;")%>"/>
				<input type="submit" value="Search"/>
			</div>
		</form>
		
		<%=searchLogs(day, month, year, minLevel, minHour, maxHour, minMinutes, maxMinutes, minSeconds, maxSeconds, filter)%>
	</body>
</html>