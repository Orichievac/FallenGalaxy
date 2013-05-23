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

package fr.fg.client.openjwt.animation;

public abstract class PeriodicAnimation extends Animation {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private double period;
	
	private double totalElapsedTime;
	
	private double elapsedTimeSinceLastUpdate;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public PeriodicAnimation(double period) {
		this.period = period;
		setLoop(true);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	protected double interpolate(double elapsedTime, double duration) {
		if (elapsedTime >= duration)
	        return 1.0;
		
        return elapsedTime / (double) duration;
	}
	
	protected final void onUpdate(double progress, double elapsedTime) {
		if ((int) Math.floor(totalElapsedTime / period) !=
				(int) Math.floor((totalElapsedTime + elapsedTime) / period)) {
			onPeriodicUpdate(elapsedTimeSinceLastUpdate);
			elapsedTimeSinceLastUpdate = 0;
		}
		
		totalElapsedTime += elapsedTime;
		elapsedTimeSinceLastUpdate += elapsedTime;
	}
	
	protected abstract void onPeriodicUpdate(double elapsedTime);
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}