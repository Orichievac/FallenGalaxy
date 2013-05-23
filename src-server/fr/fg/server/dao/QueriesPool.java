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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import fr.fg.server.util.LoggingSystem;

public class QueriesPool {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int
		OPERATION_CREATE = 1,
		OPERATION_DELETE = 2,
		OPERATION_UPDATE = 3;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private QueryExecutor executor;
	
	private List<Query> queriesQueue;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //

	public QueriesPool() {
		this.queriesQueue = Collections.synchronizedList(
				new LinkedList<Query>());
		this.executor = new QueryExecutor();
		this.executor.start();
				
	}

	// --------------------------------------------------------- METHODES -- //

	public void queueCreateQuery(GenericDao dao, PersistentData data) {
		queriesQueue.add(new Query(dao, data, OPERATION_CREATE));
	}
	
	public void queueUpdateQuery(GenericDao dao, PersistentData data) {
		queriesQueue.add(new Query(dao, data, OPERATION_UPDATE));
	}
	
	public void queueDeleteQuery(GenericDao dao, PersistentData data) {
		queriesQueue.add(new Query(dao, data, OPERATION_DELETE));
	}
	
    public void flush() {
    	synchronized (queriesQueue) {
        	while (queriesQueue.size() > 0)
            	execute(queriesQueue.remove(0));
    	}
    }
    
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void execute(Query query) {
		GenericDao dao = query.dao;
		try {
			switch (query.operation) {
			case OPERATION_CREATE:
				if (!dao.execCreate(query.data)) {
					LoggingSystem.getServerLogger().warn("SQL query '" +
						dao.getInsertStatement() + "' affected no row.");
					LoggingSystem.getServerLogger().warn("Caused by:",
							query.throwable);
				}
				break;
			case OPERATION_UPDATE:
				if (!dao.execUpdate(query.data)) {
					LoggingSystem.getServerLogger().warn("SQL query '" +
						dao.getUpdateStatement() + "' affected no row.");
					LoggingSystem.getServerLogger().warn("Caused by:",
							query.throwable);
				}
				break;
			case OPERATION_DELETE:
				if (!dao.execDelete(query.data)) {
					LoggingSystem.getServerLogger().warn("SQL query '" +
							dao.getDeleteStatement() + "' affected no row.");
					LoggingSystem.getServerLogger().warn("Caused by:",
							query.throwable);
				}
				break;
			}
		} catch (Exception e) {
			String request = "???";
			try {
				switch (query.operation) {
				case OPERATION_CREATE:
					request = dao.getInsertStatement().toString();
					break;
				case OPERATION_UPDATE:
					request = dao.getUpdateStatement().toString();
					break;
				case OPERATION_DELETE:
					request = dao.getDeleteStatement().toString();
					break;
				}
			} catch (Exception e2) {
				// Ignoré
			}
			LoggingSystem.getServerLogger().warn("Database " +
					"operation failed. SQL query: '" + request + "'.", e);
			LoggingSystem.getServerLogger().warn("Caused by:",
					query.throwable);
		}
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //

	private class Query {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private GenericDao dao;
		private PersistentData data;
		private int operation;
		private Throwable throwable;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Query(GenericDao dao, PersistentData data, int operation) {
			this.dao = dao;
			this.data = data;
			this.operation = operation;
			this.throwable = new Throwable();
		}
		
		// ----------------------------------------------------- METHODES -- //
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class QueryExecutor extends Thread {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public QueryExecutor() {
			setName("QueryExecutor");
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void run() {
			while (!isInterrupted()) {
				Query query = null;
				
				do {
					synchronized (queriesQueue) {
						if (queriesQueue.size() > 0)
							query = queriesQueue.remove(0);
						else
							query = null;
					}
					
					if (query != null)
						execute(query);
				} while (query != null);
				
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// Ignoré
				}
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
