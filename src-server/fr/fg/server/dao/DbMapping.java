/*
Copyright 2010 Jeremie Gottero

This file is part of Fallen Galaxy.

Fallen Galaxy is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Fallen Galaxy is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Fallen Galaxy. If not, see <http://www.gnu.org/licenses/>.
*/

package fr.fg.server.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.fg.server.dao.db.Table;

public class DbMapping {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String basePackage;
	
	private Map<Class<? extends PersistentData>, Table> tables;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DbMapping() {
		this.tables = Collections.synchronizedMap(
				new HashMap<Class<? extends PersistentData>, Table>());
		this.basePackage = "";
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Collection<Table> getTables() {
		return tables.values();
	}
	
	public void addTable(Table table) {
		tables.put(table.getMappedClass(), table);
	}
	
	public Table getTable(Class<? extends PersistentData> mappedClass) {
		return tables.get(mappedClass);
	}
	
	public String getBasePackage() {
		return basePackage;
	}
	
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
