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

package fr.fg.server.data;

import fr.fg.server.data.base.SpaceStationBase;
import fr.fg.server.util.Utilities;

public class SpaceStation extends SpaceStationBase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int[][] COST_LEVELS = {
		{    100000,     100000,     100000, 0,     150000},
		{    500000,     500000,     500000, 0,     750000},
		{   3300000,    3300000,    3300000, 0,    5000000},
		{  22000000,   22000000,   22000000, 0,   34000000},
		{ 150000000,  150000000,  150000000, 0,  225000000},
		{1000000000, 1000000000, 1000000000, 0, 1500000000},
	};
	
	public final static int[] INFLUENCE_LEVELS = {0, 1, 5, 25, 125, 625};
	
	public final static int[] HULL_LEVELS = {100, 600, 4000, 30000, 240000, 2000000};
	
	public final static int XP_REWARD = 60;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public SpaceStation() {
		// Nécessaire pour la construction par réflection
	}
	
	public SpaceStation(String name, int x, int y, int idAlly, int idArea) {
		setName(name);
		setLevel(0);
		setHull(HULL_LEVELS[0]);
		setX(x);
		setY(y);
		setCredits(0);
		setResource0(0);
		setResource1(0);
		setResource2(0);
		setResource3(0);
		setDate(Utilities.now());
		setIdAlly(idAlly);
		setIdArea(idArea);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Ally getAlly() {
		return DataAccess.getAllyById(getIdAlly());
	}

	public Area getArea() {
		return DataAccess.getAreaById(getIdArea());
	}
	
	public long getResource(int type) {
		switch (type) {
		case 0:
			return getResource0();
		case 1:
			return getResource1();
		case 2:
			return getResource2();
		case 3:
			return getResource3();
		}
		throw new IllegalArgumentException(
				"Invalid resource index: '" + type + "'.");
	}
	
	public double[] getResources() {
		return new double[]{
			getResource0(),
			getResource1(),
			getResource2(),
			getResource3()
		};
	}
	
	public long getValue() {
		long value = 0;
		
		for (int i = 0; i <= getLevel(); i++) {
			for (int j = 0; j < COST_LEVELS[i].length; j++)
				value += COST_LEVELS[i][j];
		}
		
		return value;
	}
	
	public void setResource(long resources, int type) {
		switch (type) {
		case 0:
			setResource0(resources);
			break;
		case 1:
			setResource1(resources);
			break;
		case 2:
			setResource2(resources);
			break;
		case 3:
			setResource3(resources);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid resource index: '" + type + "'.");
		}
	}
	
	public void setResources(long[] resources) {
		for (int i = 0; i < resources.length; i++)
			setResource(resources[i], i);
	}
	
    // Renvoie true si le point (x, y) est sur la station
    // NB : Penser à vérifier que les objets sont dans le meme secteur !
    public boolean contains(int x, int y) {
    	int dx = getX() - x;
    	int dy = getY() - y;
    	
    	return dx * dx + dy * dy <= GameConstants.SPACE_STATION_RADIUS *
    		GameConstants.SPACE_STATION_RADIUS;
    }
    
	public double getProductionModifier() {
		Sector sector = getArea().getSector();
		
		double influence = sector.getAllyInfluence(getIdAlly());
		
		return Math.min(1.5, 1 +
			influence * sector.getStrategicValue() / 100);
	}
	
	public boolean tryLevelUp() {
		if (getLevel() == 5)
			return false;
		
		boolean levelUp = true;
		
		// Teste si la station spatiale gagne un niveau
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			if (getResource(i) < SpaceStation.COST_LEVELS[getLevel() + 1][i]) {
				levelUp = false;
				break;
			}
		
		if (getCredits() < SpaceStation.COST_LEVELS[getLevel() + 1][4])
			levelUp = false;
		
		if (levelUp) {
			// Monte la station de niveau
			setLevel(getLevel() + 1);
			setHull(SpaceStation.HULL_LEVELS[getLevel()]);
			
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
				if (getLevel() == 5)
					setResource(0, i);
				else
					setResource(getResource(i) - SpaceStation.COST_LEVELS[getLevel()][i], i);
			}
			
			if (getLevel() == 5)
				setCredits(0);
			else
				setCredits(getCredits() - SpaceStation.COST_LEVELS[getLevel()][4]);
		}
		
		return levelUp;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
