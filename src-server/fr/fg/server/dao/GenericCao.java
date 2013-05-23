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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.fg.server.dao.db.Column;
import fr.fg.server.dao.db.Index;
import fr.fg.server.dao.db.Table;
import fr.fg.server.util.Utilities;

public class GenericCao implements AccessObject {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Table table;

	private GenericDao dao;
	
	@SuppressWarnings("unchecked")
	volatile private List cache, unmodifiableCache;
	
	volatile private Map<Index, Map<Object, PersistentData>> uniqueIndexes;
	
	volatile private Map<Index, Map<Object, List<? extends PersistentData>>>
		multipleIndexes;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	@SuppressWarnings("unchecked")
	public GenericCao(Table table, GenericDao dao) throws Exception {
		this.table = table;
		this.dao = dao;
		this.cache = dao.getAll();
		this.unmodifiableCache = Collections.unmodifiableList(cache);
		this.uniqueIndexes = Collections.synchronizedMap(
				new HashMap<Index, Map<Object, PersistentData>>());
		this.multipleIndexes = Collections.synchronizedMap(
				new HashMap<Index, Map<Object,
					List<? extends PersistentData>>>());
		
		// Construit les index
		for (Index i : table.getIndexes()) {
			Column c = table.getColumnByName(i.getColumn());
			
			if (i.getType() == Index.UNIQUE) {
				// Index unique (une valeur par clé)
				Map<Object, PersistentData> map = Collections.synchronizedMap(
						new HashMap<Object, PersistentData>());
				this.uniqueIndexes.put(i, map);
				
				for (Object o : cache) {
					Object key = c.getGetAccessor().invoke(o);
					if (key instanceof String)
						key = Utilities.formatString((String) key);
					
					map.put(key, (PersistentData) o);
				}
			} else {
				// Index multiple (plusieurs valeur par clé)
				Map<Object, List<? extends PersistentData>> map =
					Collections.synchronizedMap(
						new HashMap<Object, List<? extends PersistentData>>());
				this.multipleIndexes.put(i, map);
				
				for (Object o : cache) {
					Object key = c.getGetAccessor().invoke(o);
					if (key instanceof String)
						key = Utilities.formatString((String) key);
					
					List values = map.get(key);
					
					if (values == null) {
						values = Collections.synchronizedList(
								new ArrayList());
						map.put(key, values);
					}
					
					values.add(o);
				}
			}
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@SuppressWarnings("unchecked")
	public List<? extends PersistentData> getAll() {
		return unmodifiableCache;
	}
	
	public PersistentData getObjectById(String indexName, Object key) {
		Index index = table.getIndexByColumn(indexName);
		
		if (index == null || index.getType() != Index.UNIQUE)
			throw new IllegalArgumentException(indexName + " is not an " +
					"unique index for table " + table.getName() + ".");
		
		// Recherche insensible à la casse
		if (key instanceof String)
			key = Utilities.formatString((String) key);
		
		return uniqueIndexes.get(index).get(key);
	}
	
	@SuppressWarnings("unchecked")
	public List<? extends PersistentData> getObjectsById(String indexName, Object key) {
		Index index = table.getIndexByColumn(indexName);
		
		if (index == null || index.getType() != Index.MULTIPLE)
			throw new IllegalArgumentException(indexName + " is not a " +
					"multiple index for table " + table.getName() + ".");
		
		// Recherche insensible à la casse
		if (key instanceof String)
			key = Utilities.formatString((String) key);
		
		List<? extends PersistentData> values =
			multipleIndexes.get(index).get(key);
		if (values == null) {
			values = Collections.synchronizedList(new ArrayList());
			multipleIndexes.get(index).put(key, values);
		}
		
		// REMIND jgottero rendre la liste unmodifiable de manière optimisée
		// (wrapper créé une fois pour toutes ds le CAO) / Attention aux locks
		// (poser des locks sur la UList sur les add/remove) => synchronizedList
		// devient inutile
		return values;
	}
	
	@SuppressWarnings("unchecked")
	public boolean create(PersistentData data) throws Exception {
		if (data.isEditable() && data.getOriginalCopy() != null)
			return update(data);
		
		// Met à jour le cache et les index
		synchronized (this.unmodifiableCache) {
			cache.add(data);
		}
		
		for (Index i : table.getIndexes()) {
			Column c = table.getColumnByName(i.getColumn());
			Object key = c.getGetAccessor().invoke(data);
			if (key instanceof String)
				key = Utilities.formatString((String) key);
			
			if (i.getType() == Index.UNIQUE) {
				// Index unique (une valeur par clé)
				this.uniqueIndexes.get(i).put(key, data);
			} else {
				// Index multiple (plusieurs valeur par clé)
				Map<Object, List<? extends PersistentData>> map =
					this.multipleIndexes.get(i);
				List values = map.get(key);
				
				if (values == null) {
					values = Collections.synchronizedList(new ArrayList());
					map.put(key, values);
				}
				
				values.add(data);
			}
		}
		
		dao.create(data);

		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean update(PersistentData data) throws Exception {
		PersistentData originalData = data.getOriginalCopy();
		if (originalData == null) {
			if (data.isEditable())
				return create(data);
			return false;
		}
		
		synchronized (this.unmodifiableCache) {
			cache.remove(originalData);
			originalData.setEditable(true);
			originalData.setOriginalCopy(data);
			cache.add(data);
		}
		
		for (Index i : table.getIndexes()) {
			Column c = table.getColumnByName(i.getColumn());
			Object originalKey = c.getGetAccessor().invoke(originalData);
			Object key = c.getGetAccessor().invoke(data);
			if (originalKey instanceof String)
				originalKey = Utilities.formatString((String) originalKey);
			if (key instanceof String)
				key = Utilities.formatString(((String) key));
			
			if (i.getType() == Index.UNIQUE) {
				Map<Object, PersistentData> map = this.uniqueIndexes.get(i);
				
				map.remove(originalKey);
				map.put(key, data);
			} else {
				Map<Object, List<? extends PersistentData>> map =
					this.multipleIndexes.get(i);
				List values = map.get(key);
				
				if (values == null) {
					values = Collections.synchronizedList(new ArrayList());
					map.put(key, values);
				}
				
				map.get(originalKey).remove(originalData);
				values.add(data);
			}
		}
		
		dao.update(data);
		
		return true;
	}
	
	public boolean delete(PersistentData data) throws Exception {
		if (data.isEditable())
			throw new IllegalStateException("Could not delete " +
					"desynchronized data. Data must be saved first.");
		
		// Supprime l'objet du cache et des index
		cache.remove(data);
		
		for (Index i : table.getIndexes()) {
			Column c = table.getColumnByName(i.getColumn());
			Object key = c.getGetAccessor().invoke(data);
			if (key instanceof String)
				key = Utilities.formatString(((String) key));
			
			if (i.getType() == Index.UNIQUE)
				this.uniqueIndexes.get(i).remove(key);
			else
				this.multipleIndexes.get(i).get(key).remove(data);
		}
		
		dao.delete(data);
		
		return true;
	}
	
	public Object getNextId() throws Exception {
		return dao.getNextId();
	}
	
	@SuppressWarnings("unchecked")
	public boolean truncate() throws Exception {
		boolean success = true;
		
		synchronized (unmodifiableCache) {
			List<? extends PersistentData> cacheCopy =
				new ArrayList<PersistentData>(cache);
			
			for (PersistentData data : cacheCopy)
				if (!DataLayer.getInstance().delete(data))
					success = false;
			
			dao.resetNextId();
		}
		
		return success;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
