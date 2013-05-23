<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@page import="fr.fg.server.dao.db.ColumnType"%>
<%@page import="fr.fg.server.dao.DbMapping"%>
<%@page import="fr.fg.server.dao.db.Table"%>
<%@page import="fr.fg.server.dao.db.Index"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.LinkedList"%>
<%@page import="fr.fg.server.dao.db.Column"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="fr.fg.server.dao.PersistentData"%>
<%@page import="fr.fg.server.dao.db.ForeignKey"%>
<%@page import="fr.fg.server.data.DataAccess"%>
<%@page import="fr.fg.server.dao.db.PrimaryKey"%>
<%@page import="java.util.Iterator"%>
<%@page import="fr.fg.server.data.Treaty"%>
<%@page import="fr.fg.server.data.Player"%>

<%
// Vérifie que le joueur est connecté et est un super administrateur
Integer sessionId = (Integer) session.getAttribute("id");

if (sessionId == null || DataAccess.getPlayerById(sessionId) == null || !DataAccess.getPlayerById(sessionId).hasRight(Player.SUPER_ADMINISTRATOR)) {
	request.setAttribute("error", "Vous n'avez pas les droits suffisants.");
	RequestDispatcher error = getServletContext().getRequestDispatcher(Config.getErrorServlet());
	error.forward(request, response);
}

response.setCharacterEncoding("UTF-8");
%>

<%!
public Object getValue(String data, ColumnType type) {
	Object value = null;
	
	if (type == ColumnType.INT || type == ColumnType.TIMESTAMP) {
		// Integer / Timestamp
		value = Integer.parseInt(data);
	} else if (type == ColumnType.LONG) {
		// Long
		value = Long.parseLong(data);
	} else if (type == ColumnType.STRING) {
		// String
		value = data.replace("&", "&amp;").
			replace("\"", "&quot;").
			replace("'", "&apos;").
			replace("<", "&lt;").
			replace(">", "&gt;");
	} else if (type == ColumnType.FLOAT) {
		// Float
		value = Float.parseFloat(data);
	} else if (type == ColumnType.BOOLEAN) {
		// Boolean
		value = Boolean.parseBoolean(data);
	} else if (type == ColumnType.SHORT) {
		// Short
		value = Short.parseShort(data);
	} else if (type == ColumnType.DOUBLE) {
		// Double
		value = Double.parseDouble(data);
	} else if (type == ColumnType.BYTE) {
		// Byte
		value = Byte.parseByte(data);
	}
	
	return value;
}
%>

<%@page import="fr.fg.server.util.Config"%>
<%@page import="fr.fg.server.dao.DataLayer"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Fallen Galaxy :: FgMyAdmin</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
function tableChanged() {
	currentTableIndex = document.getElementById('tables').selectedIndex;
	currentKeyIndex = 0;
	updateKeys();
}

function init() {
	document.getElementById('tables').innerHTML = '';
	for (var i = 0; i < tables.length; i++) {
		var tableOption = document.createElement('option');
		tableOption.value = tables[i].name;
		tableOption.innerHTML = tables[i].name;
		if (currentTableIndex == i) {
			tableOption.selected = 'selected';
			updateKeys();
		}
		document.getElementById('tables').appendChild(tableOption);
	}
}

function showItems(table, key, value) {
	var tables = document.getElementById('tables');
	
	for (var i = 0; i < tables.options.length; i++) {
		
		if (tables.options[i].value == table) {
			tables.options[i].selected = 'selected';
			break;
		}
	}
	
	var keys = document.getElementById('keys');
	
	for (var i = 0; i < keys.options.length; i++) {
		
		if (keys.options[i].value == key) {
			keys.options[i].selected = 'selected';
			break;
		}
	}
	
	document.getElementById('value').value = value;
	
	document.getElementById('form').submit();
}

function updateKeys() {
	document.getElementById('keys').innerHTML = '';
	for (var j = 0; j < tables[currentTableIndex].keys.length; j++) {
		var keyOption = document.createElement('option');
		keyOption.value = tables[currentTableIndex].keys[j];
		keyOption.innerHTML = tables[currentTableIndex].keys[j];
		if (currentKeyIndex == j)
			keyOption.selected = 'selected';
		document.getElementById('keys').appendChild(keyOption);
	}
}

var tables = [
<%
DbMapping mapping = DataLayer.getInstance().getDbMapping();

Table currentTable = null;
Index currentKey = null;
int currentTableIndex = 0;
int currentKeyIndex = 0;

// Liste des tables
List<Table> tables = new ArrayList<Table>(mapping.getTables());
Collections.sort(tables);

String currentTableName = request.getParameter("table") != null ?
		request.getParameter("table") : tables.get(0).getName();
String currentKeyName = request.getParameter("key") != null ?
		request.getParameter("key") : tables.get(0).getIndexes().iterator().next().getColumn();
String currentValue = request.getParameter("value") != null ?
		request.getParameter("value") : "";

int i = 0;
for (Table table : tables) {
	out.print("{name : '" + table.getName() + "', keys : ['all', ");
	
	if (table.getName().equals(currentTableName)) {
		currentTableIndex = i;
		currentTable = table;
	}
	
	// Liste des indexes
	List<Index> indexes = new ArrayList<Index>(table.getIndexes());
	Collections.sort(indexes);
	
	int j = 0;
	for (Index index : indexes) {
		if (table.getName().equals(currentTableName) &&
				index.getColumn().equals(currentKeyName)) {
			currentKeyIndex = j + 1;
			currentKey = index;
		}
		
		out.print("'" + index.getColumn() + "', ");
		j++;
	}
	
	out.print("]}, ");
	i++;
}
%>];
var currentTableIndex = <%=currentTableIndex%>;
var currentKeyIndex = <%=currentKeyIndex%>;
		</script>
		<style type="text/css">
body { font: 12px verdana, arial, helvetica, sans-serif; margin: 0; }
div.header { background-color: black; padding: 8px; color: white; }
div.header .field { background-color: #ceff01; }
tr th { background-color: gray; padding: 4px; }
div.values input { margin: 0; padding: 0; border: 0; }
tr.odd, tr.odd input { background-color: #e8e8e8; }
tr.even, tr.even input { background-color: #cecece; }
tr:hover, tr:hover input { background-color: #ceff01 !important; }
table td { padding: 2px 2px; border-right: 1px solid gray; }
		</style>
	</head>
	<body onload="init()">
		<form id="form" action="fgmyadmin.jsp" method="get">
			<div class="header">
				Table
				<select id="tables" name="table" class="field" onchange="tableChanged()"></select>
				Index
				<select id="keys" name="key" class="field"></select>
				Valeur
				<input id="value" name="value" class="field" type="text" style="width: 200px;" value="<%=currentValue.replaceAll("\"", "&quot;")%>"/>
				<input type="submit" value="Afficher"/>
			</div>
		</form>
		<div class="values">
			<table cellspacing="0">
				<tr>
					<th><img src="icon_save.gif"/><img src="icon_delete.gif"/></th>
<%
for (Column column : currentTable.getColumns()) {
%>
					<th><%=column.getName()%></th>
<%
}

String action = request.getParameter("action");

// Action de mise à jour ou de suppression d'un objet
if (action != null) {
	PersistentData item = null;
	
	try {
		Object key = null;
		
		if (currentTable.getPrimaryKeys().size() == 1) {
			// La clé primaire est unique et l'objet peut etre accédé directement
			String name = currentTable.getPrimaryKeys().iterator().next().getName();
			ColumnType type = currentTable.getColumnByName(name).getType();
			String parameter = request.getParameter("field-" + name);

			key = getValue(parameter, type);
			
			item = DataLayer.getInstance().getObjectById(
					currentTable.getMappedClass(), name, key);
		} else if (currentTable.getPrimaryKeys().size() > 1) {
			// La clé primaire est multiple, et il faut croiser les
			// index pour récupérer l'élément 
			List<?> items = null;
			
			for (PrimaryKey primaryKey : currentTable.getPrimaryKeys()) {
				String name = primaryKey.getName();
				ColumnType type = currentTable.getColumnByName(name).getType();
				String parameter = request.getParameter("field-" + name);
				
				key = getValue(parameter, type);
				
				if (items == null) {
					items = new ArrayList(DataLayer.getInstance().getObjectsById(
							currentTable.getMappedClass(), name, key));
				} else {
					// Intersection entre les deux listes
					List<?> newItems = DataLayer.getInstance().getObjectsById(
							currentTable.getMappedClass(), name, key);
					items.retainAll(newItems);
				}
			}
			
			if (items != null && items.size() == 1)
				item = (PersistentData) items.get(0);
		}
	} catch (Exception e) {
		// Ignoré
	}
	
	if (action.equals("update")) {
		// Mise à jour de l'objet
		synchronized (item.getLock()) {
			PersistentData newItem = DataAccess.getEditable(item);
			for (Column column : currentTable.getColumns()) {
				if (currentTable.isPrimaryKey(column.getName()))
					continue;
				ColumnType type = column.getType();
				String parameter = request.getParameter("field-" + column.getName());
				
				Object value = getValue(parameter, type);
				
				column.getSetAccessor().invoke(newItem, value);
			}
			DataAccess.save(newItem);
		}
	} else if (action.equals("delete")) {
		DataAccess.delete(item);
	}
}

%>
				</tr>
<%
List items;
if (currentKeyName.equals("all")) {
	items = DataLayer.getInstance().getAll(currentTable.getMappedClass());
} else {
	ColumnType type = currentTable.getColumnByName(currentKey.getColumn()).getType();
	Object key = null;
	
	try {
		key = getValue(currentValue, type);
		
		if (currentKey.getType() == Index.MULTIPLE) {
			items = DataLayer.getInstance().getObjectsById(
					currentTable.getMappedClass(), currentKey.getColumn(), key);
		} else {
			items = new ArrayList<PersistentData>();
			Object item = DataLayer.getInstance().getObjectById(
					currentTable.getMappedClass(), currentKey.getColumn(), key);
			if (item != null)
				items.add(item);
		}
	} catch (Exception e) {
		items = new ArrayList<PersistentData>();
	}
}

int parity = 1;
int id = 1;
for (Object item : items) {
	String update = "";
	
	for (Column column : currentTable.getColumns()) {
		if (currentTable.isPrimaryKey(column.getName())) {
			update = " onclick=\"\"";
		}
	}
%>
				<tr class="<%=parity == 0 ? "even" : "odd"%>">
					<form id="f<%=id%>" action="fgmyadmin.jsp" method="post">
						<td>
							<div style="width: 40px;">
								<a onclick="document.getElementById('a<%=id%>').value='update';document.getElementById('f<%=id%>').submit()"><img src="icon_save.gif"/></a>
								<a onclick="if(confirm('Really?')){document.getElementById('a<%=id%>').value='delete';document.getElementById('f<%=id%>').submit()}"><img src="icon_delete.gif"/></a>
								<input id="a<%=id%>" name="action" type="hidden" value="update"/>
								<input name="table" type="hidden" value="<%=currentTableName%>"/>
								<input name="key"   type="hidden" value="<%=currentKeyName%>"/>
								<input name="value" type="hidden" value="<%=currentValue%>"/>
							</div>
						</td>
<%
	for (Column column : currentTable.getColumns()) {
		String link = null;
		String value = String.valueOf(column.getGetAccessor().invoke(item));
		
		for (Table table : mapping.getTables()) {
			for (ForeignKey foreignKey : table.getForeignKeys()) {
				if (foreignKey.getColumn().getName().equals(column.getName())) {
					link = "<a onclick=\"showItems('" + foreignKey.getReferencedTable() + "', '" + foreignKey.getReferencedColumn() + "', '" + value.replaceAll("'", "\\\'") + "');\"><img src=\"icon_arrow.png\"/></a>";
					break;
				}
			}
		}
		
		if (link == null) {
%>
						<td><input name="field-<%=column.getName()%>" type="text" value="<%=value%>" style="width: 80px;"/></td>
<%
		} else {
%>
						<td><div style="width: 94px;"><input name="field-<%=column.getName()%>" type="text" value="<%=value%>" style="width: 80px;"/><%=link%></div></td>
<%
		}
	}
%>
					</form>
				</tr>
<%
	parity = (parity + 1) % 2;
	id++;
}
%>
			</table>
		</div>
	</body>
</html>
