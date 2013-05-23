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

import fr.fg.server.data.base.StorehouseResourcesBase;

public class StorehouseResources extends StorehouseResourcesBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public StorehouseResources() {
		// Nécessaire pour la construction par réflection
	}
	
	public StorehouseResources(int idArea, int idPlayer) {
		setIdArea(idArea);
		setIdPlayer(idPlayer);
		setResource0(0);
		setResource1(0);
		setResource2(0);
		setResource3(0);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Player getPlayer() {
		return DataAccess.getPlayerById(getIdPlayer());
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
		default:
			throw new IllegalArgumentException(
					"Invalid resource index: '" + type + "'.");
		}
	}
	
	public long getRessourceCount(){
		return getResource0()+getResource1()+getResource2()+getResource3();
	}
	
	public void addResource(long resources, int type) {
		setResource(Math.max(0, getResource(type) + resources), type);
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
	
	public void resetResources(){
		setResource0(0);
		setResource1(0);
		setResource2(0);
		setResource3(0);
	}
	
	public void setResources(long[] resources) {
		for (int i = 0; i < resources.length; i++)
			setResource(resources[i], i);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
