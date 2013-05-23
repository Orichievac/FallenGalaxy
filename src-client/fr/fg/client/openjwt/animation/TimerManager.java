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

package fr.fg.client.openjwt.animation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.user.client.Timer;

public class TimerManager extends Timer {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int 
		SECOND_UNIT = 1000,
		MINUTE_UNIT = 60000;
	
	public final static int BASE_TIME_UNIT = 100;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static TimerManager instance = new TimerManager();
	
	private ArrayList<TimerHandler> baseUnitHandlers;
	
	private ArrayList<TimerHandler> secondUnitHandlers,
		firstPassSecondUnitHandlers, minuteUnitHandlers,
		firstPassMinuteUnitHandlers;
	
	private HashMap<TimerHandler, Double> secondUnitHandlersRegisterDate,
		minuteUnitHandlersRegisterDate;
	
	private ArrayList<CallbackHandler> callbackHandlers;
	
	private double lastUpdate, lastSecondUpdate, lastMinuteUpdate;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private TimerManager() {
		this.baseUnitHandlers = new ArrayList<TimerHandler>();
		this.secondUnitHandlers = new ArrayList<TimerHandler>();
		this.firstPassSecondUnitHandlers = new ArrayList<TimerHandler>();
		this.secondUnitHandlersRegisterDate = new HashMap<TimerHandler, Double>();
		this.minuteUnitHandlers = new ArrayList<TimerHandler>();
		this.firstPassMinuteUnitHandlers = new ArrayList<TimerHandler>();
		this.minuteUnitHandlersRegisterDate = new HashMap<TimerHandler, Double>();
		this.callbackHandlers = new ArrayList<CallbackHandler>();
		this.lastUpdate = new Date().getTime();
		this.lastSecondUpdate = lastUpdate;
		this.lastMinuteUpdate = lastUpdate;
		scheduleRepeating(BASE_TIME_UNIT);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static ArrayList<TimerHandler> getBaseUnitHandlers() {
		return instance.baseUnitHandlers;
	}
	
	public static ArrayList<TimerHandler> getSecondUnitHandlers() {
		return instance.secondUnitHandlers;
	}
	
	public static ArrayList<TimerHandler> getMinuteUnitHandlers() {
		return instance.minuteUnitHandlers;
	}
	
	public static void registerCallback(CallbackHandler callbackHandler) {
		for (int i = 0; i < instance.callbackHandlers.size(); i++)
			if (instance.callbackHandlers.get(i).getTriggerTime() >
					callbackHandler.getTriggerTime()) {
				instance.callbackHandlers.add(i, callbackHandler);
				return;
			}
		
		instance.callbackHandlers.add(callbackHandler);
	}
	
	public static void unregisterCallback(CallbackHandler callbackHandler) {
		instance.callbackHandlers.remove(callbackHandler);
	}
	
	public static void register(TimerHandler handler) {
		register(handler, BASE_TIME_UNIT);
	}
	
	public static void register(TimerHandler handler, int timeUnit) {
		if (handler.isFinished())
			return;
		
		switch (timeUnit) {
		case BASE_TIME_UNIT:
			for (TimerHandler existingHandler : instance.baseUnitHandlers)
				if (existingHandler == handler)
					return;
			
			instance.baseUnitHandlers.add(handler);
			break;
		case SECOND_UNIT:
			for (TimerHandler existingHandler : instance.secondUnitHandlers)
				if (existingHandler == handler)
					return;
			
			instance.firstPassSecondUnitHandlers.add(handler);
			instance.secondUnitHandlersRegisterDate.put(handler, (double) new Date().getTime());
			break;
		case MINUTE_UNIT:
			for (TimerHandler existingHandler : instance.minuteUnitHandlers)
				if (existingHandler == handler)
					return;
			
			instance.firstPassMinuteUnitHandlers.add(handler);
			instance.minuteUnitHandlersRegisterDate.put(handler, (double) new Date().getTime());
			break;
		}
	}
	
	public static void unregister(TimerHandler handler) {
		if (handler.isFinished())
			return;
		
		for (int i = 0; i < instance.baseUnitHandlers.size(); i++)
			if (instance.baseUnitHandlers.get(i) == handler) {
				instance.baseUnitHandlers.remove(i);
				return;
			}
		
		for (int i = 0; i < instance.secondUnitHandlers.size(); i++)
			if (instance.secondUnitHandlers.get(i) == handler) {
				instance.secondUnitHandlers.remove(i);
				return;
			}
		
		for (int i = 0; i < instance.firstPassSecondUnitHandlers.size(); i++)
			if (instance.firstPassSecondUnitHandlers.get(i) == handler) {
				instance.firstPassSecondUnitHandlers.remove(i);
				instance.secondUnitHandlersRegisterDate.remove(handler);
				return;
			}
		
		for (int i = 0; i < instance.minuteUnitHandlers.size(); i++)
			if (instance.minuteUnitHandlers.get(i) == handler) {
				instance.minuteUnitHandlers.remove(i);
				return;
			}
		
		for (int i = 0; i < instance.firstPassMinuteUnitHandlers.size(); i++)
			if (instance.firstPassMinuteUnitHandlers.get(i) == handler) {
				instance.firstPassMinuteUnitHandlers.remove(i);
				instance.minuteUnitHandlersRegisterDate.remove(handler);
				return;
			}
	}
	
	@Override
	public void run() {
		double currentTime = new Date().getTime();
		
		// Execute les callbacks
		while (callbackHandlers.size() > 0 &&
				callbackHandlers.get(0).getTriggerTime() <= currentTime) {
			CallbackHandler callbackHandler = callbackHandlers.remove(0);
			callbackHandler.getCallback().run();
			callbackHandler.setFinished(true);
		}
		
		if (currentTime - lastSecondUpdate >= 1000) {
			int interpolation = (int) (currentTime - lastSecondUpdate);
			lastSecondUpdate = currentTime;
			
			for (int i = secondUnitHandlers.size() - 1; i >= 0; i--) {
				TimerHandler handler = secondUnitHandlers.get(i);
				handler.update(interpolation);
				
				if (handler.isFinished()) {
					secondUnitHandlers.remove(i);
					handler.destroy();
				}
			}
			
			for (int i = firstPassSecondUnitHandlers.size() - 1; i >= 0; i--) {
				TimerHandler handler = firstPassSecondUnitHandlers.get(i);
				
				interpolation = (int) (currentTime - secondUnitHandlersRegisterDate.get(handler));
				
				handler.update(interpolation);
				
				firstPassSecondUnitHandlers.remove(i);
				secondUnitHandlersRegisterDate.remove(handler);
				
				if (!handler.isFinished())
					secondUnitHandlers.add(handler);
				else
					handler.destroy();
			}
		}
		
		if (currentTime - lastMinuteUpdate >= 60000) {
			int interpolation = (int) (currentTime - lastMinuteUpdate);
			lastMinuteUpdate = currentTime;
			
			for (int i = minuteUnitHandlers.size() - 1; i >= 0; i--) {
				TimerHandler handler = minuteUnitHandlers.get(i);
				handler.update(interpolation);
				
				if (handler.isFinished()) {
					minuteUnitHandlers.remove(i);
					handler.destroy();
				}
			}
			
			for (int i = firstPassMinuteUnitHandlers.size() - 1; i >= 0; i--) {
				TimerHandler handler = firstPassMinuteUnitHandlers.get(i);
				
				interpolation = (int) (currentTime - minuteUnitHandlersRegisterDate.get(handler));
				
				handler.update(interpolation);
				
				firstPassMinuteUnitHandlers.remove(i);
				minuteUnitHandlersRegisterDate.remove(handler);
				
				if (!handler.isFinished())
					minuteUnitHandlers.add(handler);
				else
					handler.destroy();
			}
		}
		
		int interpolation = (int) (currentTime - lastUpdate);
		lastUpdate = currentTime;
		for (int i = baseUnitHandlers.size() - 1; i >= 0; i--) {
			TimerHandler handler = baseUnitHandlers.get(i);
			handler.update(interpolation);
			
			if (handler.isFinished()) {
				baseUnitHandlers.remove(i);
				handler.destroy();
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
