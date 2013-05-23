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

package fr.fg.server.task;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.tools.ant.util.Watchdog;

import fr.fg.server.core.UpdateManager;
import fr.fg.server.data.GameConstants;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.MaxExecutionTimeWatchdog;
import fr.fg.server.util.Utilities;

public class TaskManager {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private boolean started;
	
	private ScheduledThreadPoolExecutor executor;
	
	private TaskExecutor minutelyTasks, hourlyTasks, dailyTasks;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public TaskManager() {
		this.started = false;
		
		this.minutelyTasks	= new TaskExecutor(60);
		this.hourlyTasks	= new TaskExecutor(3600);
		this.dailyTasks		= new TaskExecutor(24 * 3600);
		
		minutelyTasks.setName("ThreadExecutor Task Minutely");
		hourlyTasks.setName("ThreadExecutor Task Hourly");
		dailyTasks.setName("ThreadExecutor Task Daily");
		
		String packageName = getClass().getPackage().getName();
		
		// Charge les tâches éxécutées toutes les minutes
		Class<?>[] classes = Utilities.getClasses(packageName + ".minutely");
		
		if (classes != null) {
			for (Class<?> c : classes) {
				try {
					this.minutelyTasks.addTask((Runnable) c.newInstance());
					LoggingSystem.getServerLogger().trace(
							"Minutely task registered: '" + c.getName() + "'.");
				} catch (InstantiationException e) {
					// Ignoré
				} catch (Exception e) {
					LoggingSystem.getServerLogger().error("Could not " +
							"load task class: '" + c.getName() + "'.", e);
				}
			}
		} else {
			LoggingSystem.getServerLogger().error("Could not load " +
					"package classes from: '" + packageName + ".minutely'.");
		}
		
		// Charge les tâches éxécutées toutes les heures
		classes = Utilities.getClasses(packageName + ".hourly");
		
		if (classes != null) {
			for (Class<?> c : classes) {
				try {
					this.hourlyTasks.addTask((Runnable) c.newInstance());
					LoggingSystem.getServerLogger().trace(
							"Hourly task registered: '" + c.getName() + "'.");
				} catch (InstantiationException e) {
					// Ignoré
				} catch (Exception e) {
					LoggingSystem.getServerLogger().error("Could not " +
							"load task class: '" + c.getName() + "'.", e);
				}
			}
		} else {
			LoggingSystem.getServerLogger().error("Could not load " +
					"package classes from: '" + packageName + ".hourly'.");
		}
		
		// Charge les tâches quotidiennes
		classes = Utilities.getClasses(packageName + ".daily");
		
		if (classes != null) {
			for (Class<?> c : classes) {
				try {
					this.dailyTasks.addTask((Runnable) c.newInstance());
					LoggingSystem.getServerLogger().trace(
							"Daily task registered: '" + c.getName() + "'.");
				} catch (InstantiationException e) {
					// Ignoré
				} catch (Exception e) {
					LoggingSystem.getServerLogger().error("Could not " +
							"load task class: '" + c.getName() + "'.", e);
				}
			}
		} else {
			LoggingSystem.getServerLogger().error("Could not load " +
					"package classes from: '" + packageName + ".daily'.");
		}
		
		LoggingSystem.getServerLogger().info("Tasks loaded (" + (
			hourlyTasks.tasksQueue.size() + dailyTasks.tasksQueue.size() +
			minutelyTasks.tasksQueue.size()) + " tasks found).");
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public synchronized boolean isStarted() {
		return started;
	}
	
	public synchronized void start() {
		if (started)
			return;
		
		this.executor = new ScheduledThreadPoolExecutor(3);
		this.executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		this.executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		
		long now = Utilities.now() * 1000;
		Calendar reference = Calendar.getInstance();
		reference.setTimeInMillis(now);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now);
		
		// Taches éxécutées toutes les minutes
		calendar.set(
				reference.get(Calendar.YEAR),
				reference.get(Calendar.MONTH),
				reference.get(Calendar.DAY_OF_MONTH),
				reference.get(Calendar.HOUR_OF_DAY),
				reference.get(Calendar.MINUTE) + 1, 0);
		this.executor.scheduleAtFixedRate(this.minutelyTasks,
				(int) (calendar.getTimeInMillis() -
						reference.getTimeInMillis()) / 1000,
				Math.max(10, 60 / GameConstants.TIME_UNIT), TimeUnit.SECONDS);
		
		// Taches éxécutées toutes les heures (éxécutées tous les heures à
		// 00:00)
		calendar.set(
				reference.get(Calendar.YEAR),
				reference.get(Calendar.MONTH),
				reference.get(Calendar.DAY_OF_MONTH),
				reference.get(Calendar.HOUR_OF_DAY) + 1, 0, 0);
		this.executor.scheduleAtFixedRate(this.hourlyTasks,
				(int) (calendar.getTimeInMillis() -
						reference.getTimeInMillis()) / 1000,
				Math.max(60, 3600 / GameConstants.TIME_UNIT), TimeUnit.SECONDS);
		
		// Taches quotidiennes (éxécutées tous les jours à 00:00:00)
		calendar.set(
				reference.get(Calendar.YEAR),
				reference.get(Calendar.MONTH),
				reference.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
		this.executor.scheduleAtFixedRate(this.dailyTasks,
				(int) (calendar.getTimeInMillis() -
						reference.getTimeInMillis()) / 1000,
				Math.max(360, 3600 * 24 / GameConstants.TIME_UNIT), TimeUnit.SECONDS);
		
		this.started = true;
	}
	
	public synchronized void stop() {
		if (!started)
			return;
		
		this.executor.remove(this.minutelyTasks);
		this.executor.remove(this.hourlyTasks);
		this.executor.remove(this.dailyTasks);
		this.executor.shutdown();
		this.executor = null;
		this.started = false;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	// -------------------------------------------------- CLASSES PRIVEES -- //

	private class TaskExecutor extends Thread {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //

		private List<Runnable> tasksQueue;
		
		private int interval;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public TaskExecutor(int interval) {
			this.tasksQueue =
				Collections.synchronizedList(new LinkedList<Runnable>());
			this.interval = interval;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void addTask(Runnable task) {
			this.tasksQueue.add(task);
		}
		
		public void run() {
			try {
				LoggingSystem.getServerLogger().trace("Task Name: "+this.getName());
				synchronized (tasksQueue) {
					for (Runnable task : tasksQueue) {
						try {
							int maxExecutionTime;
							if (interval <= 60)
								maxExecutionTime = 10;
							else if (interval <= 3600)
								maxExecutionTime = 60;
							else
								maxExecutionTime = 120;
							Watchdog watchdog = new Watchdog(
								maxExecutionTime * 1000);
							watchdog.addTimeoutObserver(
								new MaxExecutionTimeWatchdog(
									Thread.currentThread(), "[Task: " +
									task.getClass() + "]", false));
							watchdog.start();
							
							task.run();
							
							watchdog.stop();
						} catch (Exception e) {
							LoggingSystem.getServerLogger().error(
									"Could not execute task '" +
									task.getClass().getName() + "'.", e);
						}
					}
				}
				
				// Déclenche les mises à jour en attente sur le thread
				UpdateManager.getInstance().flushUpdates();
			} catch (Exception e) {
				LoggingSystem.getServerLogger().error("Task execution failed.", e);
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
