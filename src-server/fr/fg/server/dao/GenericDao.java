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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.fg.server.dao.db.Column;
import fr.fg.server.dao.db.ColumnType;
import fr.fg.server.dao.db.Index;
import fr.fg.server.dao.db.PrimaryKey;
import fr.fg.server.dao.db.Table;
import fr.fg.server.util.DBManager;

public class GenericDao implements AccessObject {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Table table;
	
	private QueriesPool pool;
	
	private Connection db;
	
	private PreparedStatement
		selectAllStatement,
		insertStatement,
		deleteStatement,
		updateStatement,
		selectNextIdStatement;
	
	private Map<String, PreparedStatement> selectByKeyStatements;
	
	private Object nextId;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public GenericDao(Table table, QueriesPool pool) {
		this.table = table;
		this.pool = pool;
		this.selectByKeyStatements = new HashMap<String, PreparedStatement>();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public synchronized void setConnection(Connection db) {
		if (this.db != null)
			closeQueries();
		
		this.db = db;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized List<? extends PersistentData> getAll()
			throws Exception {
		// Vérifie qu'une connexion a été définie
		if (db == null)
			throw new IllegalStateException("A database connection must be " +
					"defined with setConnection.");
		
		PreparedStatement selectAllStatement = getSelectAllQuery();
		
		ResultSet result = selectAllStatement.executeQuery();
		
		List objects = new ArrayList();
		
		while (result.next()) {
			Object instance = table.getMappedClass().newInstance();
			
			for (Column column : table.getColumns()) {
				ColumnType type = column.getType();
				Object value = null;
				
				if (type == ColumnType.INT || type == ColumnType.TIMESTAMP) {
					// Integer / Timestamp
					value = result.getInt(column.getName());
				} else if (type == ColumnType.LONG) {
					// Long
					value = result.getLong(column.getName());
				} else if (type == ColumnType.STRING) {
					// String
					value = result.getString(column.getName());
				} else if (type == ColumnType.FLOAT) {
					// Float
					value = result.getFloat(column.getName());
				} else if (type == ColumnType.BOOLEAN) {
					// Boolean
					value = result.getBoolean(column.getName());
				} else if (type == ColumnType.SHORT) {
					// Short
					value = result.getShort(column.getName());
				} else if (type == ColumnType.DOUBLE) {
					// Double
					value = result.getDouble(column.getName());
				} else if (type == ColumnType.BYTE) {
					// Byte
					value = result.getByte(column.getName());
				}
				
				// Valeur NULL
				if (result.wasNull())
					value = null;
				
				// Affecte la valeur à l'instance
				column.getSetAccessor().invoke(instance, value);
			}
			
			((PersistentData) instance).setEditable(false);
			
			objects.add(instance);
		}
		
		try {
			result.close();
		} catch (Exception e) {
			// Ignoré
		}
		
		return objects;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized List<? extends PersistentData> getObjectsById(
			String key, Object value) throws Exception {
		// Vérifie qu'une connexion a été définie
		if (db == null)
			throw new IllegalStateException("A database connection must be " +
					"defined with setConnection.");
		
		Index index = table.getIndexByColumn(key);
		if (index == null)
			throw new IllegalArgumentException(
					"Undefined or not an index column: '" + key + "'.");
		Column keyColumn = table.getColumnByName(index.getColumn());
		
		PreparedStatement selectByKeyStatement = getSelectByKeyStatement(key);
		ColumnType type = keyColumn.getType();
		
		if (type == ColumnType.INT || type == ColumnType.TIMESTAMP) {
			// Integer / Timestamp
			if (value == null)
				selectByKeyStatement.setNull(1, Types.INTEGER);
			else
				selectByKeyStatement.setInt(1, (Integer) value);
		} else if (type == ColumnType.LONG) {
			// Long
			if (value == null)
				selectByKeyStatement.setNull(1, Types.BIGINT);
			else
				selectByKeyStatement.setLong(1, (Long) value);
		} else if (type == ColumnType.STRING) {
			// String
			if (value == null)
				selectByKeyStatement.setNull(1, Types.VARCHAR);
			else
				selectByKeyStatement.setString(1, (String) value);
		} else if (type == ColumnType.FLOAT) {
			// Float
			if (value == null)
				selectByKeyStatement.setNull(1, Types.FLOAT);
			else
				selectByKeyStatement.setFloat(1, (Float) value);
		} else if (type == ColumnType.BOOLEAN) {
			// Boolean
			if (value == null)
				selectByKeyStatement.setNull(1, Types.BOOLEAN);
			else
				selectByKeyStatement.setBoolean(1, (Boolean) value);
		} else if (type == ColumnType.SHORT) {
			// Short
			if (value == null)
				selectByKeyStatement.setNull(1, Types.SMALLINT);
			else
				selectByKeyStatement.setShort(1, (Short) value);
		} else if (type == ColumnType.DOUBLE) {
			// Double
			if (value == null)
				selectByKeyStatement.setNull(1, Types.DOUBLE);
			else
				selectByKeyStatement.setDouble(1, (Double) value);
		} else if (type == ColumnType.BYTE) {
			// Byte
			if (value == null)
				selectByKeyStatement.setNull(1, Types.TINYINT);
			else
				selectByKeyStatement.setByte(1, (Byte) value);
		}
		
		ResultSet result = selectByKeyStatement.executeQuery();
		
		List objects = new ArrayList();
		
		while (result.next()) {
			Object instance = table.getMappedClass().newInstance();
			
			for (Column column : table.getColumns()) {
				type = column.getType();
				value = null;
				
				if (type == ColumnType.INT || type == ColumnType.TIMESTAMP) {
					// Integer / Timestamp
					value = result.getInt(column.getName());
				} else if (type == ColumnType.LONG) {
					// Long
					value = result.getLong(column.getName());
				} else if (type == ColumnType.STRING) {
					// String
					value = result.getString(column.getName());
				} else if (type == ColumnType.FLOAT) {
					// Float
					value = result.getFloat(column.getName());
				} else if (type == ColumnType.BOOLEAN) {
					// Boolean
					value = result.getBoolean(column.getName());
				} else if (type == ColumnType.SHORT) {
					// Short
					value = result.getShort(column.getName());
				} else if (type == ColumnType.DOUBLE) {
					// Double
					value = result.getDouble(column.getName());
				} else if (type == ColumnType.BYTE) {
					// Byte
					value = result.getByte(column.getName());
				}
				
				// Valeur NULL
				if (result.wasNull())
					value = null;
				
				// Affecte la valeur à l'instance
				column.getSetAccessor().invoke(instance, value);
			}
			
			((PersistentData) instance).setEditable(false);
			
			objects.add(instance);
		}
		
		try {
			result.close();
		} catch (Exception e) {
			// Ignoré
		}
		
		return objects;
	}
	
	public synchronized PersistentData getObjectById(String key, Object value)
			throws Exception {
		List<? extends PersistentData> objects = getObjectsById(key, value);
		
		if (objects.size() > 0)
			return objects.get(0);
		return null;
	}
	
	// FIXME jgottero éxécuter directement les requetes sans passer par le pool si
	// il n'y pas de CAO (mettre un booléen dans le constructeur ?), afin que le
	// DataLayer soit toujours synchro
	public synchronized boolean create(PersistentData data) throws Exception {
		if (data.isEditable() && data.getOriginalCopy() != null)
			return update(data);
		
		data.setEditable(false);
		
		if (pool != null) {
			pool.queueCreateQuery(this, data);
			return true;
		} else {
			return execCreate(data);
		}
	}

	public synchronized boolean update(PersistentData data) throws Exception {
		if (data.getOriginalCopy() == null) {
			if (data.isEditable())
				return create(data);
			return false;
		}
		
		data.setEditable(false);
		
		if (pool != null) {
			pool.queueUpdateQuery(this, data);
			return true;
		} else {
			return execUpdate(data);
		}
	}

	public synchronized boolean delete(PersistentData data) throws Exception {
		if (data.isEditable())
			throw new IllegalStateException("Could not delete " +
					"desynchronized data. Data must be saved first.");
		data.setEditable(true);
		
		if (pool != null) {
			pool.queueDeleteQuery(this, data);
			return true;
		} else {
			return execDelete(data);
		}
	}
	
	public synchronized boolean truncate() throws Exception {
		boolean success = true;
		
		for (PersistentData data : getAll())
			if (!DataLayer.getInstance().delete(data))
				success = false;
		
		resetNextId();
		
		return success;
	}
	
	public void resetNextId() {
		Iterator<PrimaryKey> i = table.getPrimaryKeys().iterator();
		Column keyColumn = table.getColumnByName(i.next().getName());
		
		// Cast l'identifiant dans le bon type
		Object nextId;
		ColumnType type = keyColumn.getType();
		
		if (type == ColumnType.INT || type == ColumnType.TIMESTAMP) {
			nextId = (int) 0;
		} else if (type == ColumnType.SHORT) {
			nextId = (short) 0;
		} else if (type == ColumnType.BYTE) {
			nextId = (byte) 0;
		} else if (type == ColumnType.FLOAT) {
			nextId = (float) 0;
		} else if (type == ColumnType.DOUBLE) {
			nextId = (double) 0;
		} else {
			nextId = 0;
		}
		
		this.nextId = nextId;
	}
	
	public synchronized boolean execCreate(PersistentData data) throws Exception {
		// Vérifie qu'une connexion a été définie
		if (db == null)
			throw new IllegalStateException("A database connection must be " +
					"defined with setConnection.");
		
		PreparedStatement insertStatement = getInsertStatement();
		
		int parameter = 1;
		for (Column column : table.getColumns()) {
			ColumnType type = column.getType();
			Object value = column.getGetAccessor().invoke(data);
			
			if (type == ColumnType.INT || type == ColumnType.TIMESTAMP) {
				// Integer / Timestamp
				if (value == null)
					insertStatement.setNull(parameter, Types.INTEGER);
				else
					insertStatement.setInt(parameter, (Integer) value);
			} else if (type == ColumnType.LONG) {
				// Long
				if (value == null)
					insertStatement.setNull(parameter, Types.BIGINT);
				else
					insertStatement.setLong(parameter, (Long) value);
			} else if (type == ColumnType.STRING) {
				// String
				if (value == null)
					insertStatement.setNull(parameter, Types.VARCHAR);
				else
					insertStatement.setString(parameter, (String) value);
			} else if (type == ColumnType.FLOAT) {
				// Float
				if (value == null)
					insertStatement.setNull(parameter, Types.FLOAT);
				else
					insertStatement.setFloat(parameter, (Float) value);
			} else if (type == ColumnType.BOOLEAN) {
				// Boolean
				if (value == null)
					insertStatement.setNull(parameter, Types.BOOLEAN);
				else
					insertStatement.setBoolean(parameter, (Boolean) value);
			} else if (type == ColumnType.SHORT) {
				// Short
				if (value == null)
					insertStatement.setNull(parameter, Types.SMALLINT);
				else
					insertStatement.setShort(parameter, (Short) value);
			} else if (type == ColumnType.DOUBLE) {
				// Double
				if (value == null)
					insertStatement.setNull(parameter, Types.DOUBLE);
				else
					insertStatement.setDouble(parameter, (Double) value);
			} else if (type == ColumnType.BYTE) {
				// Byte
				if (value == null)
					insertStatement.setNull(parameter, Types.TINYINT);
				else
					insertStatement.setByte(parameter, (Byte) value);
			}
			
			parameter++;
		}
		
		return insertStatement.executeUpdate() > 0;
	}

	public synchronized boolean execUpdate(PersistentData data) throws Exception {
		// Vérifie qu'une connexion a été définie
		if (db == null)
			throw new IllegalStateException("A database connection must be " +
					"defined with setConnection.");

		PreparedStatement updateStatement = getUpdateStatement();
		
		int parameter = 1;
		
		// 1e passe = champs hors clés primaires
		// 2e passe = clés primaires
		for (int pass = 0; pass < 2; pass++) {
			for (Column column : table.getColumns()) {
				if ((pass == 0 && table.isPrimaryKey(column.getName())) ||
						(pass == 1 && !table.isPrimaryKey(column.getName())))
					continue;
				
				ColumnType type = column.getType();
				Object value = column.getGetAccessor().invoke(data);
				
				if (type == ColumnType.INT || type == ColumnType.TIMESTAMP) {
					// Integer / Timestamp
					if (value == null)
						updateStatement.setNull(parameter, Types.INTEGER);
					else
						updateStatement.setInt(parameter, (Integer) value);
				} else if (type == ColumnType.LONG) {
					// Long
					if (value == null)
						updateStatement.setNull(parameter, Types.BIGINT);
					else
						updateStatement.setLong(parameter, (Long) value);
				} else if (type == ColumnType.STRING) {
					// String
					if (value == null)
						updateStatement.setNull(parameter, Types.VARCHAR);
					else
						updateStatement.setString(parameter, (String) value);
				} else if (type == ColumnType.FLOAT) {
					// Float
					if (value == null)
						updateStatement.setNull(parameter, Types.FLOAT);
					else
						updateStatement.setFloat(parameter, (Float) value);
				} else if (type == ColumnType.BOOLEAN) {
					// Boolean
					if (value == null)
						updateStatement.setNull(parameter, Types.BOOLEAN);
					else
						updateStatement.setBoolean(parameter, (Boolean) value);
				} else if (type == ColumnType.SHORT) {
					// Short
					if (value == null)
						updateStatement.setNull(parameter, Types.SMALLINT);
					else
						updateStatement.setShort(parameter, (Short) value);
				} else if (type == ColumnType.DOUBLE) {
					// Double
					if (value == null)
						updateStatement.setNull(parameter, Types.DOUBLE);
					else
						updateStatement.setDouble(parameter, (Double) value);
				} else if (type == ColumnType.BYTE) {
					// Byte
					if (value == null)
						updateStatement.setNull(parameter, Types.TINYINT);
					else
						updateStatement.setByte(parameter, (Byte) value);
				}
				
				parameter++;
			}
		}
		
		return updateStatement.executeUpdate() > 0;
	}
	
	public synchronized boolean execDelete(PersistentData data) throws Exception {
		// Vérifie qu'une connexion a été définie
		if (db == null)
			throw new IllegalStateException("A database connection must be " +
					"defined with setConnection.");
		
		PreparedStatement deleteStatement = getDeleteStatement();
		
		int parameter = 1;
		
		for (PrimaryKey primaryKey : table.getPrimaryKeys()) {
			Column column = table.getColumnByName(primaryKey.getName());
			ColumnType type = column.getType();
			Object value = column.getGetAccessor().invoke(data);
			
			if (type == ColumnType.INT || type == ColumnType.TIMESTAMP) {
				// Integer
				if (value == null)
					deleteStatement.setNull(parameter, Types.INTEGER);
				else
					deleteStatement.setInt(parameter, (Integer) value);
			} else if (type == ColumnType.LONG) {
				// Long
				if (value == null)
					deleteStatement.setNull(parameter, Types.BIGINT);
				else
					deleteStatement.setLong(parameter, (Long) value);
			} else if (type == ColumnType.STRING) {
				// String
				if (value == null)
					deleteStatement.setNull(parameter, Types.VARCHAR);
				else
					deleteStatement.setString(parameter, (String) value);
			} else if (type == ColumnType.FLOAT) {
				// Float
				if (value == null)
					deleteStatement.setNull(parameter, Types.FLOAT);
				else
					deleteStatement.setFloat(parameter, (Float) value);
			} else if (type == ColumnType.BOOLEAN) {
				// Boolean
				if (value == null)
					deleteStatement.setNull(parameter, Types.BOOLEAN);
				else
					deleteStatement.setBoolean(parameter, (Boolean) value);
			} else if (type == ColumnType.SHORT) {
				// Short
				if (value == null)
					deleteStatement.setNull(parameter, Types.SMALLINT);
				else
					deleteStatement.setShort(parameter, (Short) value);
			} else if (type == ColumnType.DOUBLE) {
				// Double
				if (value == null)
					deleteStatement.setNull(parameter, Types.DOUBLE);
				else
					deleteStatement.setDouble(parameter, (Double) value);
			} else if (type == ColumnType.BYTE) {
				// Byte
				if (value == null)
					deleteStatement.setNull(parameter, Types.TINYINT);
				else
					deleteStatement.setByte(parameter, (Byte) value);
			}
			
			parameter++;
		}
		
		return deleteStatement.executeUpdate() > 0;
	}

	public synchronized Object getNextId() throws Exception {
		if (table.getPrimaryKeys().size() != 1)
			throw new UnsupportedOperationException("Next id value can only " +
					"be retrieved for tables with an unique primary key.");
		
		if (nextId == null) {
			// Vérifie qu'une connexion a été définie
			if (db == null)
				throw new IllegalStateException("A database connection must be " +
						"defined with setConnection.");
			
			// Récupère la valeur du prochain identifiant inséré dans la table
			Iterator<PrimaryKey> i = table.getPrimaryKeys().iterator();
			Column keyColumn = table.getColumnByName(i.next().getName());
			
			PreparedStatement selectNextIdStatement = getSelectNextIdStatement();
			ResultSet result = selectNextIdStatement.executeQuery();
			result.next();
			long max = result.getLong(1);
			
			// Cast l'identifiant dans le bon type
			Object nextId;
			ColumnType type = keyColumn.getType();
			
			if (type == ColumnType.INT || type == ColumnType.TIMESTAMP) {
				nextId = (int) max + 1;
			} else if (type == ColumnType.SHORT) {
				nextId = (short) max + 1;
			} else if (type == ColumnType.FLOAT) {
				nextId = (float) max + 1;
			} else if (type == ColumnType.DOUBLE) {
				nextId = (double) max + 1;
			} else if (type == ColumnType.BYTE) {
				nextId = (byte) max + 1;
			} else {
				nextId = max + 1;
			}
			
			try {
				result.close();
			} catch (Exception e) {
				// Ignoré
			}
			
			this.nextId = nextId;
		} else {
			Iterator<PrimaryKey> i = table.getPrimaryKeys().iterator();
			Column keyColumn = table.getColumnByName(i.next().getName());
			ColumnType type = keyColumn.getType();
			
			// Cast l'identifiant dans le bon type
			if (type == ColumnType.INT || type == ColumnType.TIMESTAMP) {
				nextId = (Integer) nextId + 1;
			} else if (type == ColumnType.SHORT) {
				nextId = (Short) nextId + 1;
			} else if (type == ColumnType.FLOAT) {
				nextId = (Float) nextId + 1;
			} else if (type == ColumnType.BYTE) {
				nextId = (Byte) nextId + 1;
			} else if (type == ColumnType.DOUBLE) {
				nextId = (Double) nextId + 1;
			} else {
				nextId = (Long) nextId + 1;
			}
		}
		return nextId;
	}
	
	public synchronized PreparedStatement getSelectAllQuery()
			throws SQLException {
		if (selectAllStatement == null) {
			String selectAllQuery = DBManager.parseQuery(
				"SELECT * " +
				"FROM %prefix%" + table.getName()
			);
			
			selectAllStatement = db.prepareStatement(selectAllQuery);
		}
		
		return selectAllStatement;
	}

	public synchronized PreparedStatement getSelectByKeyStatement(String key)
			throws SQLException {
		PreparedStatement statement = selectByKeyStatements.get(key);
		
		if (statement == null) {
			Index index = table.getIndexByColumn(key);
			if (index == null)
				throw new IllegalArgumentException(
						"Undefined or not an index column: '" + key + "'.");
			
			String selectByKeyQuery = DBManager.parseQuery(
				"SELECT * " +
				"FROM %prefix%" + table.getName() + " " +
				"WHERE " + index.getColumn() + " = ?"
			);
			
			statement = db.prepareStatement(selectByKeyQuery);
			selectByKeyStatements.put(key, statement);
		}
		
		return statement;
	}
	
	public synchronized PreparedStatement getSelectNextIdStatement()
			throws SQLException {
		if (selectNextIdStatement == null) {
			if (table.getPrimaryKeys().size() != 1)
				throw new UnsupportedOperationException("Next id value can only " +
						"be retrieved for tables with an unique primary key.");
			
			// Récupère la valeur du prochain identifiant inséré dans la table
			Iterator<PrimaryKey> i = table.getPrimaryKeys().iterator();
			Column keyColumn = table.getColumnByName(i.next().getName());
			String selectNextIdQuery = DBManager.parseQuery(
					"SELECT MAX(" + keyColumn.getName() + ") " +
					"FROM %prefix%" + table.getName()
			);
			
			selectNextIdStatement = db.prepareStatement(selectNextIdQuery);
		}

		return selectNextIdStatement;
	}
	
	public synchronized PreparedStatement getInsertStatement()
			throws SQLException {
		if (insertStatement == null) {
			// Colonnes
			String cols = "";
			for (int i = 0; i < table.getColumns().size(); i++) {
				if (i != 0)
					cols += ", ";
				cols += "?";
			}
			
			String insertQuery = DBManager.parseQuery(
				"INSERT INTO %prefix%" + table.getName() + " " +
				"VALUES (" + cols + ")"
			);
			
			insertStatement = db.prepareStatement(insertQuery);
		}
		
		return insertStatement;
	}
	
	public synchronized PreparedStatement getDeleteStatement()
			throws SQLException {
		if (deleteStatement == null) {
			// Primary keys
			String keys = "";
			boolean first = true;
			for (PrimaryKey primaryKey : table.getPrimaryKeys()) {
				if (first)
					first = false;
				else
					keys += " AND ";
				keys += primaryKey.getName() + " = ?";
			}
			String deleteQuery = DBManager.parseQuery(
				"DELETE FROM %prefix%" + table.getName() + " " +
				"WHERE " + keys
			);
			
			deleteStatement = db.prepareStatement(deleteQuery);
		}
		
		return deleteStatement;
	}
	
	public synchronized PreparedStatement getUpdateStatement()
			throws SQLException {
		if (updateStatement == null) {
			// Colonnes
			String cols = "";
			boolean first = true;
			for (Column col : table.getColumns()) {
				if (!table.isPrimaryKey(col.getName())) {
					if (first)
						first = false;
					else
						cols += ", ";
					cols += col.getName() + " = ?";
				}
			}
			// Primary keys
			String keys = "";
			first = true;
			for (Column col : table.getColumns()) {
				if (!table.isPrimaryKey(col.getName()))
					continue;
				if (first)
					first = false;
				else
					keys += " AND ";
				keys += col.getName() + " = ?";
			}
			
			String updateQuery = DBManager.parseQuery(
				"UPDATE %prefix%" + table.getName() + " " +
				"SET " + cols + " " +
				"WHERE " + keys
			);
			
			updateStatement = db.prepareStatement(updateQuery);
		}
		
		return updateStatement;
	}

	public synchronized void closeQueries() {
		try {
			if (selectAllStatement != null)
				selectAllStatement.close();
		} catch (SQLException e) {
			// Ignoré
		}
		try {
			if (insertStatement != null)
				insertStatement.close();
		} catch (SQLException e) {
			// Ignoré
		}
		try {
			if (deleteStatement != null)
				deleteStatement.close();
		} catch (SQLException e) {
			// Ignoré
		}
		try {
			if (updateStatement != null)
				updateStatement.close();
		} catch (SQLException e) {
			// Ignoré
		}
		try {
			if (selectNextIdStatement != null)
				selectNextIdStatement.close();
		} catch (SQLException e) {
			// Ignoré
		}
		for (PreparedStatement statement : selectByKeyStatements.values())
			try {
				statement.close();
			} catch (SQLException e) {
				// Ignoré
			}
		selectAllStatement = null;
		insertStatement = null;
		deleteStatement = null;
		updateStatement = null;
		selectNextIdStatement = null;
		selectByKeyStatements.clear();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
