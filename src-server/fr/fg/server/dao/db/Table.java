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

package fr.fg.server.dao.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.fg.server.dao.PersistentData;


public class Table implements Comparable<Table> {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String name;
	
	private Class<? extends PersistentData> mappedClass;
	
	private boolean cached;
	
	private List<Column> columns;
	
	private Map<String, Column> columnsByName;
	
	private Set<PrimaryKey> primaryKeys;
	
	private Map<String, PrimaryKey> primaryKeysByName;
	
	private Set<Index> indexes;
	
	private List<ForeignKey> foreignKeys;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Table(String name, Class<? extends PersistentData> mappedClass,
			boolean cached) {
		this.name = name;
		this.mappedClass = mappedClass;
		this.cached = cached;
		this.columns = Collections.synchronizedList(
				new ArrayList<Column>());
		this.columnsByName = Collections.synchronizedMap(
				new HashMap<String, Column>());
		this.primaryKeys = Collections.synchronizedSet(
				new HashSet<PrimaryKey>());
		this.primaryKeysByName = Collections.synchronizedMap(
				new HashMap<String, PrimaryKey>());
		this.indexes = Collections.synchronizedSet(
				new HashSet<Index>());
		this.foreignKeys = Collections.synchronizedList(
				new LinkedList<ForeignKey>());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isCached() {
		return cached;
	}
	
	public void setCached(boolean cached) {
		this.cached = cached;
	}
	
	public List<Column> getColumns() {
		return columns;
	}
	
	public void setColumns(List<Column> columns) {
		this.columns.clear();
		this.columnsByName.clear();
		for (Column column : columns)
			addColumn(column);
	}
	
	public void addColumn(Column column) {
		this.columns.add(column);
		this.columnsByName.put(column.getName().toLowerCase(), column);
		column.setParentTable(this);
	}
	
	public Column getColumnByName(String name) {
		return columnsByName.get(name.toLowerCase());
	}
	
	public Set<PrimaryKey> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(Set<PrimaryKey> primaryKeys) {
		this.primaryKeys.clear();
		this.primaryKeysByName.clear();
		for (PrimaryKey primaryKey : primaryKeys)
			addPrimaryKey(primaryKey);
	}

	public void addPrimaryKey(PrimaryKey primaryKey) {
		primaryKeys.add(primaryKey);
		primaryKeysByName.put(primaryKey.getName().toLowerCase(), primaryKey);
	}
	
	public boolean isPrimaryKey(String column) {
		return primaryKeysByName.get(column.toLowerCase()) != null;
	}
	
	public Set<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(Set<Index> indexes) {
		this.indexes = indexes;
	}
	
	public void addIndex(Index index) {
		this.indexes.add(index);
	}
	
	public Index getIndexByColumn(String column) {
		for (Index i : indexes)
			if (i.getColumn().equalsIgnoreCase(column))
				return i;
		return null;
	}
	
	public void addForeignKey(ForeignKey foreignKey) {
		this.foreignKeys.add(foreignKey);
	}
	
	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}
	
	public Class<? extends PersistentData> getMappedClass() {
		return mappedClass;
	}

	public void setMappedClass(Class<? extends PersistentData> mappedClass) {
		this.mappedClass = mappedClass;
	}
	
	public int compareTo(Table t) {
		return getName().compareToIgnoreCase(t.getName());
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
