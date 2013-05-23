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
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.fg.server.dao.db.Column;
import fr.fg.server.dao.db.ForeignKey;
import fr.fg.server.dao.db.Index;
import fr.fg.server.dao.db.PrimaryKey;
import fr.fg.server.dao.db.Table;
import fr.fg.server.util.Config;
import fr.fg.server.util.DBManager;
import fr.fg.server.util.LoggingSystem;

public class DataLayer {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static DataLayer instance = new DataLayer();
	
	private Connection db;
	
	private DbMapping dbMapping;

	private QueriesPool pool;
	
	private Map<Class<? extends PersistentData>, GenericCao> caoMap;

	private Map<Class<? extends PersistentData>, GenericDao> daoMap;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private DataLayer() {
		this.pool = new QueriesPool();
		this.caoMap = Collections.synchronizedMap(
				new HashMap<Class<? extends PersistentData>, GenericCao>());
		this.daoMap = Collections.synchronizedMap(
				new HashMap<Class<? extends PersistentData>, GenericDao>());
		try {
			// Charge le fichier de mapping classes java / tables DB
			dbMapping = DbMappingParser.parse(DataLayer.class.getClassLoader(
					).getResource(Config.getDbMapping()));
			
			LoggingSystem.getServerLogger().info("Database mapping loaded (" +
					dbMapping.getTables().size() + " tables found).");
		} catch (Exception e) {
			// Echec du chargement du fichier
			LoggingSystem.getServerLogger().error(
					"Could not load database mapping.", e);
			dbMapping = new DbMapping();
		}
		
		if (Config.isAOPreloaded()) {
			for (Table table : dbMapping.getTables())
				if (table.isCached())
					getAll(table.getMappedClass());
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public DbMapping getDbMapping() {
		return dbMapping;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PersistentData> T getEditable(T t) {
		try {
			// TODO jgottero cloner l'objet en cache
			return (T) t.clone();
		} catch (CloneNotSupportedException e) {
			LoggingSystem.getServerLogger().warn("Failed to clone " +
					"object '" + t + "'.", e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PersistentData> List<T> getAll(Class<T> t) {
		AccessObject ao = getAccessObject(t);
		
		if (ao != null)
			try {
				return (List<T>) ao.getAll();
			} catch (Exception e) {
				LoggingSystem.getServerLogger().warn("Could not " +
						"retrieve data for class: '" + t.getName() + "'.", e);
			}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends PersistentData> T getObjectById(
			Class<T> t, String index, Object key) {
		AccessObject ao = getAccessObject(t);
		
		if (ao != null)
			try {
				return (T) ao.getObjectById(index, key);
			} catch (Exception e) {
				LoggingSystem.getServerLogger().warn("Could not " +
						"retrieve data for class: '" + t.getName() + "'.", e);
			}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends PersistentData> List<T> getObjectsById(
			Class<T> t, String index, Object key) {
		AccessObject ao = getAccessObject(t);
		
		if (ao != null)
			try {
				return (List<T>) ao.getObjectsById(index, key);
			} catch (Exception e) {
				LoggingSystem.getServerLogger().warn("Could not " +
						"retrieve data for class: '" + t.getName() + "'.", e);
			}
		return null;
	}
	
	protected boolean save(PersistentData data) {
		AccessObject ao = getAccessObject(data.getClass());
		
		if (ao != null)
			try {
				if (data.getOriginalCopy() == null) {
					if (data.isEditable()) {
						Table table = dbMapping.getTable(data.getClass());
						
						// Génère les clés primaires avec un generator de type increment
						for (PrimaryKey primaryKey : table.getPrimaryKeys()) {
							if (primaryKey.getGenerator() == PrimaryKey.GENERATOR_INCREMENT) {
								Column keyColumn = table.getColumnByName(primaryKey.getName());
								keyColumn.getSetAccessor().invoke(data, ao.getNextId());
							}
						}
						return ao.create(data);
					}
				} else {
					return ao.update(data);
				}
			} catch (Exception e) {
				LoggingSystem.getServerLogger().warn(
						"Could not save object: '" + data + "'.", e);
			}
		return false;
	}
	
	protected boolean delete(PersistentData data) {
		AccessObject ao = getAccessObject(data.getClass());
		
		if (ao != null)
			try {
				// Synchronise la donnée avant de la supprimer
				if (data.isEditable()) {
					if (!save(data))
						return false;
				}
				
				Table table = dbMapping.getTable(data.getClass());
				
				// Contraintes de clés étrangères
				for (ForeignKey foreignKey : table.getForeignKeys()) {
					Column foreignKeyColumn = foreignKey.getColumn();
					Index foreignKeyIndex = foreignKey.getTable(
							).getIndexByColumn(foreignKey.getName());
					Column keyColumn = table.getColumnByName(
							foreignKey.getReferencedColumn());
					
					if (foreignKeyIndex.getType() == Index.UNIQUE) {
						// Référence unique
						Object key = keyColumn.getGetAccessor().invoke(data);
						PersistentData target = getObjectById(
								foreignKey.getTable().getMappedClass(),
								foreignKey.getName(), key);
						
						if (target != null && target != data) { // NB : évite les récursions infinies
							PersistentData editableTarget = getEditable(target);
							
							switch (foreignKey.getAction()) {
							case ForeignKey.SET_NULL:
								foreignKeyColumn.getSetAccessor().invoke(editableTarget,
										foreignKeyColumn.getGetAccessor().invoke(
												editableTarget).getClass().cast(null));
								save(editableTarget);
								break;
							case ForeignKey.SET_ZERO:
								foreignKeyColumn.getSetAccessor().invoke(editableTarget, 0);
								save(editableTarget);
								break;
							case ForeignKey.CASCADE:
								delete(target);
								break;
							case ForeignKey.RESTRICT:
								throw new RestrictedDataException(foreignKey, key);
							}
						}
					} else {
						// Références multiples
						Object key = keyColumn.getGetAccessor().invoke(data);
						List<? extends PersistentData> targets = getObjectsById(
								foreignKey.getTable().getMappedClass(),
								foreignKey.getName(), key);
						
						// Boucle inversée car des éléments peuvent etre supprimés
						for (int i = targets.size() - 1; i >= 0; i--) {
							if (i >= targets.size())
								continue;
							
							PersistentData target = targets.get(i);
							if (target == data) // NB : évite les récursions infinies
								continue;
							
							PersistentData newTarget = DataLayer.getInstance().getEditable(target);
							
							switch (foreignKey.getAction()) {
							case ForeignKey.SET_NULL:
								foreignKeyColumn.getSetAccessor().invoke(newTarget,
										foreignKeyColumn.getGetAccessor().invoke(
												newTarget).getClass().cast(null));
								save(newTarget);
								break;
							case ForeignKey.SET_ZERO:
								foreignKeyColumn.getSetAccessor().invoke(newTarget, 0);
								save(newTarget);
								break;
							case ForeignKey.CASCADE:
								delete(target);
								break;
							case ForeignKey.RESTRICT:
								throw new RestrictedDataException(foreignKey, key);
							}
						}
					}
				}
				
				return ao.delete(data);
			} catch (Exception e) {
				LoggingSystem.getServerLogger().warn(
						"Could not delete object: '" + data + "'.", e);
			}
		return false;
	}
	
	public boolean truncate(Class<? extends PersistentData> c) {
		AccessObject ao = getAccessObject(c);
		
		try {
			return ao.truncate();
		} catch (Exception e) {
			LoggingSystem.getServerLogger().warn(
					"Could not truncate table for class: '" + c + "'.", e);
		}
		
		flush();
		
		return false;
	}
	
    public synchronized void flush() {
    	pool.flush();
    }

    public synchronized void release() {
		this.caoMap = Collections.synchronizedMap(
				new HashMap<Class<? extends PersistentData>, GenericCao>());
		this.daoMap = Collections.synchronizedMap(
				new HashMap<Class<? extends PersistentData>, GenericDao>());
		if (this.db != null)
			try {
				this.db.close();
			} catch (SQLException e) {
				// Ignoré
			}
		this.db = null;
    }
    
	public static DataLayer getInstance() {
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private AccessObject getAccessObject(Class<? extends PersistentData> c) {
		Table table = dbMapping.getTable(c);
		
		if (table != null) {
			if (table.isCached())
				return getCao(c);
			else
				return getDao(c);
		}
		return null;
	}
	
	private synchronized GenericCao getCao(Class<? extends PersistentData> c) {
		GenericCao cao = caoMap.get(c);
		
		if (cao == null) {
			Table table = dbMapping.getTable(c);
			
			if (table != null) {
				try {
					// Construit le CAO pour accèder au cache de la table
					cao = new GenericCao(table, getDao(c));
					caoMap.put(c, cao);
					
					LoggingSystem.getServerLogger().trace("CAO created for table: '" +
							table.getName() + "'.");
				} catch (Exception e) {
					LoggingSystem.getServerLogger().error("Could not " +
							"load CAO for class '" + c + "'.", e);
				}
			} else {
				LoggingSystem.getServerLogger().warn("No DB table found " +
						"matching class '" + c + "'.");
			}
		}
		
		return cao;
	}

	private synchronized GenericDao getDao(Class<? extends PersistentData> c) {
		GenericDao dao = daoMap.get(c);
		
		if (dao == null) {
			Table table = dbMapping.getTable(c);
			
			if (table != null) {
				if (this.db == null)
					this.db = DBManager.getConnection();
				
				// Construit le DAO pour accèder à la table
				dao = new GenericDao(table, table.isCached() ? pool : null);
				dao.setConnection(db);
				daoMap.put(c, dao);
				
				LoggingSystem.getServerLogger().trace("DAO created for table: '" +
						table.getName() + "'.");
			} else {
				LoggingSystem.getServerLogger().warn("No DB table found " +
						"matching class '" + c + "'.");
			}
		}
		
		return dao;
	}
}
