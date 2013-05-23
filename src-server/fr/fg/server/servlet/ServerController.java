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

package fr.fg.server.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import fr.fg.server.admin.AdminScriptManager;
import fr.fg.server.contract.ContractManager;
import fr.fg.server.core.AchievementManager;
import fr.fg.server.core.ConnectionManager;
import fr.fg.server.core.TerritoryManager;
import fr.fg.server.data.DataAccess;
import fr.fg.server.scheduler.JobSchedulerManager;
import fr.fg.server.task.TaskManager;
import fr.fg.server.util.LoggingSystem;

public class ServerController implements ServletContextListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static long shutdownDate;
	
	private ServerController instance;
	
	private boolean started = false;
	
	private TaskManager taskManager;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ServerController() {
		this.instance = this;
		this.taskManager = new TaskManager();
		
		// Enregistre un thread pour arreter le serveur proprement
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LoggingSystem.getServerLogger().info(
						"Graceful server shutdown started.");
				instance.contextDestroyed(null);
			}
		});
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static long getShutdownDate() {
		return shutdownDate;
	}

	public static void setShutdownDate(long date) {
		shutdownDate = date;
	}
	
	public synchronized void contextInitialized(ServletContextEvent event) {
		if (isStarted())
			return;
		
		LoggingSystem.getServerLogger().info("Starting server...");
		
		// Charge les missions
		ContractManager.getInstance();
		
		//Charge les Scripts Administrateur
		AdminScriptManager.getInstance();
		
		// Démarre les gestionnaires de tâches
		taskManager.start();
		JobSchedulerManager.getInstance().start();
		
		AchievementManager.start();
		
		// Met à jour le cache des serveurs
		try {
			Servers.updateServersCache();
		} catch (Exception e) {
			LoggingSystem.getServerLogger().warn(
					"Could not create servers cache.", e);
		}
		
		TerritoryManager.getInstance().updateAllTerritoriesMap();
		
		started = true;
		shutdownDate = -1;
		LoggingSystem.getServerLogger().info("Server started.");
	}
	
	public synchronized void contextDestroyed(ServletContextEvent event) {
		if (!isStarted())
			return;
		
		LoggingSystem.getServerLogger().info("Stopping server...");
		
		started = false;
		shutdownDate = -1;
		
		// Déconnecte tous les joueurs
		ConnectionManager.getInstance().disconnectAll();
		
		AchievementManager.stop();
		
		// Stoppe les gestionnaires de tâches
		taskManager.stop();
		JobSchedulerManager.getInstance().stop();
		
		// Arrête les watchdogs qui surveillent les requetes
		ActionServlet.stopWatchdogs();
		
		// Flush les données non enregistrées en DB
		DataAccess.flush();
		
		// Rend les ressources utilisées par le cache de données
		DataAccess.release();
		
		// Attend 1 secondes que le serveur soit arrêté
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// Ignoré
		}
		
		LoggingSystem.getServerLogger().info("Server stopped.");
	}
	
	public synchronized boolean isStarted() {
		return started;
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
