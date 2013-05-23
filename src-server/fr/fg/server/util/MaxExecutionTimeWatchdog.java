/*
Copyright 2011 Jeremie Gottero

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

package fr.fg.server.util;

import java.lang.Thread.State;
import java.util.Map;

import org.apache.tools.ant.util.TimeoutObserver;
import org.apache.tools.ant.util.Watchdog;

public class MaxExecutionTimeWatchdog implements TimeoutObserver {
	// --------------------------------------------------- CONSTANTES -- //
	// ---------------------------------------------------- ATTRIBUTS -- //
	
	private Thread thread;
	
	private String requestParameters;
	
	private boolean fullDump;
	
	// ------------------------------------------------ CONSTRUCTEURS -- //
	
	public MaxExecutionTimeWatchdog(Thread thread,
			String requestParameters, boolean fullDump) {
		this.thread = thread;
		this.requestParameters = requestParameters;
		this.fullDump = fullDump;
	}
	
	// ----------------------------------------------------- METHODES -- //
	
	public void timeoutOccured(Watchdog watchdog) {
		LoggingSystem.getServerLogger().error("Blocked thread detected [" +
				thread.getName() + "] - " + requestParameters + ".");
		
		// Dump l'état des threads
		Map<Thread, StackTraceElement[]> allTraces = Thread.getAllStackTraces();
		StringBuffer buffer = new StringBuffer();
		
		for (Thread thread : allTraces.keySet()) {
			if (thread.getId() != this.thread.getId())
				continue;
			
			StackTraceElement[] trace = allTraces.get(thread);
			buffer.append("Dumping blocked thread stack trace: ");
			buffer.append(thread.getName());
			buffer.append(" [State=");
			buffer.append(thread.getState());
			buffer.append("]\n");
			
	        for (int i = 0; i < trace.length; i++) {
	        	buffer.append("\tat ");
	        	buffer.append(trace[i]);
	        	buffer.append("\n");
	        }
		}
		
		for (Thread thread : allTraces.keySet()) {
			if (!fullDump || thread.getId() == this.thread.getId())
				continue;
			
			if (thread.getState().equals(State.TIMED_WAITING) ||
					thread.getState().equals(State.WAITING) ||
					thread.getState().equals(State.RUNNABLE))
				continue;
			
			StackTraceElement[] trace = allTraces.get(thread);
			buffer.append("Dumping thread stack trace: ");
			buffer.append(thread.getName());
			buffer.append(" [State=");
			buffer.append(thread.getState());
			buffer.append("]\n");
			
	        for (int i = 0; i < trace.length; i++) {
	        	buffer.append("\tat ");
	        	buffer.append(trace[i]);
	        	buffer.append("\n");
	        }
		}
		
		buffer.append("End of threads dump.\n");
		
		LoggingSystem.getServerLogger().info(buffer.toString());
	}
	
	// --------------------------------------------- METHODES PRIVEES -- //
}