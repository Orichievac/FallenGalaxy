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

package fr.fg.client.core.impl;

import fr.fg.client.openjwt.core.BaseWidget;

public class AreaContainerImpl {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private BaseWidget nearStarField, farStarField;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public BaseWidget getNearStarField() {
		return nearStarField;
	}

	public BaseWidget getFarStarField() {
		return farStarField;
	}
	
	public void createStarFields() {
		nearStarField = new BaseWidget();
		nearStarField.getElement().setId("nearStarfield");
		farStarField = new BaseWidget();
		farStarField.getElement().setId("farStarfield");
		
		farStarField.getElement().setAttribute("unselectable", "on");
		nearStarField.getElement().setAttribute("unselectable", "on");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
