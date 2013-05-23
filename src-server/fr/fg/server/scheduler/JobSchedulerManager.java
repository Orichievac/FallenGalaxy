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

package fr.fg.server.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.tools.ant.util.Watchdog;

import fr.fg.server.core.UpdateManager;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.MaxExecutionTimeWatchdog;
import fr.fg.server.util.Utilities;

public class JobSchedulerManager implements Runnable {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private boolean started;
	
	private final static JobSchedulerManager instance = new JobSchedulerManager();
	
	private List<JobScheduler<?>> schedulers;
	
	private ScheduledThreadPoolExecutor executor;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private JobSchedulerManager() {
		LoggingSystem.getServerLogger().info("Loading schedulers");
		
		this.started = false;
		this.schedulers = Collections.synchronizedList(
			new ArrayList<JobScheduler<?>>());
		
		String packageName = getClass().getPackage().getName();
		
		// Charge les planificateurs des tâches
		Class<?>[] classes = Utilities.getClasses(packageName + ".impl");
		
		if (classes != null) {
			for (Class<?> c : classes) {
				try {
					this.schedulers.add((JobScheduler<?>) c.newInstance());
					LoggingSystem.getServerLogger().trace(
						"Job scheduler registered: '" + c.getName() + "'.");
				} catch (InstantiationException e) {
					// Ignoré
				} catch (Exception e) {
					LoggingSystem.getServerLogger().error("Could not " +
						"load job scheduler: '" + c.getName() + "'.", e);
				}
			}
		} else {
			LoggingSystem.getServerLogger().error("Could not load " +
					"package classes from: '" + packageName + ".impl'.");
		}
		
		LoggingSystem.getServerLogger().info("Schedulers loaded (" +
			schedulers.size() + " schedulers found).");
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public synchronized boolean isStarted() {
		return started;
	}
	
	public synchronized void start() {
		if (started)
			return;
		
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		this.executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		this.executor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
		
		this.started = true;
		
		LoggingSystem.getServerLogger().info("Job scheduler started.");
	}

	public synchronized void stop() {
		if (!started)
			return;
		
		this.executor.remove(this);
		this.executor.shutdown();
		this.executor = null;
		this.started = false;
		
		LoggingSystem.getServerLogger().info("Job scheduler stopped.");
	}

	public void run() {
		String threadName = Thread.currentThread().getName();
		if (!threadName.contains("JobScheduler"))
			Thread.currentThread().setName("JobScheduler-" + threadName);
		
		synchronized (schedulers) {
			for (JobScheduler<?> scheduler : schedulers) {
				try {
					Watchdog watchdog = new Watchdog(40000);
					watchdog.addTimeoutObserver(
						new MaxExecutionTimeWatchdog(
							Thread.currentThread(), "[Job scheduler: " +
							scheduler.getClass() + "]", true));
					watchdog.start();
					
					scheduler.execute();
					
					watchdog.stop();
				} catch (Exception e) {
					LoggingSystem.getServerLogger().warn(
						"Could not execute scheduler.", e);
				}
			}
		}
		
		UpdateManager.getInstance().flushUpdates();
	}
	
	public static JobSchedulerManager getInstance() {
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
