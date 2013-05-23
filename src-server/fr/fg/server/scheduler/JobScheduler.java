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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

abstract public class JobScheduler<E> {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Map<E, Job<E>> elements;
	
	private List<Job<E>> jobs;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JobScheduler() {
		this.jobs = Collections.synchronizedList(new LinkedList<Job<E>>());
		this.elements = Collections.synchronizedMap(new HashMap<E, Job<E>>());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addJob(E e, long time) {
		if (elements.containsKey(e))
			jobs.remove(elements.get(e));
		
		Job<E> newJob = new Job<E>(e, time);
		elements.put(e, newJob);
		
		synchronized (jobs) {
			Iterator<Job<E>> iterator = jobs.iterator();
			int i = 0;
			
			while (iterator.hasNext()) {
				Job<E> job = iterator.next();
				
				if (job.time == time && job.e.equals(e))
					return;
				
				if (job.time > time) {
					jobs.add(i, newJob);
					return;
				}
				
				i++;
			}
			
			jobs.add(i, newJob);
		}
	}
	
	public void execute() {
		ArrayList<Job<E>> jobs = new ArrayList<Job<E>>(this.jobs);
		long now = Utilities.now();
		ArrayList<E> elements = new ArrayList<E>();
		
		Iterator<Job<E>> iterator = jobs.iterator();
		
		while (iterator.hasNext()) {
			Job<E> job = iterator.next();
			
			if (job.time > now)
				break;
			
			this.jobs.remove(job);
			this.elements.remove(job.e);
			elements.add(job.e);
		}
		
		if (elements.size() > 0)
			for (E e : elements) {
				try {
					process(e, now);
				} catch (Exception ex) {
					LoggingSystem.getServerLogger().warn(
						"Could not process element.", ex);
				}
			}
	}
	
	public abstract void process(E e, long time);
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static class Job<E> {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private long time;
		
		private E e;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Job(E e, long time) {
			this.e = e;
			this.time = time;
		}
		
		// ----------------------------------------------------- METHODES -- //
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
