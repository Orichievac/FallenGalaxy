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

package fr.fg.client.map.miniitem;

import fr.fg.client.data.AsteroidsData;
import fr.fg.client.map.UIMiniItemRenderingHints;

public class AsteroidsMiniItem extends AnimatedMiniItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private AsteroidsData asteroidsData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AsteroidsMiniItem(AsteroidsData asteroidsData,
			UIMiniItemRenderingHints hints) {
		super(asteroidsData.getX(), asteroidsData.getY(), hints);
		
		this.asteroidsData = asteroidsData;
		
		setStyleName("asteroid" + (asteroidsData.getType().equals(
			"asteroid") ? "" : " " + asteroidsData.getType())); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		boolean hide =
			asteroidsData.getType().equals("asteroid") || 
			asteroidsData.getType().equals("asteroid_dense");
		getElement().getStyle().setProperty("display", hide ? "none" : "");
				
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDataUpdate(Object newData) {
		AsteroidsData newAsteroidsData = (AsteroidsData) newData;
		
		if (asteroidsData.getX() != newAsteroidsData.getX() ||
			asteroidsData.getY() != newAsteroidsData.getY())
			setLocation(newAsteroidsData.getX(), newAsteroidsData.getY());
		
		if (!asteroidsData.getType().equals(newAsteroidsData.getType())) {
			setStyleName("asteroid" + (newAsteroidsData.getType().equals(
				"asteroid") ? "" : " " + newAsteroidsData.getType()));
			
			boolean hide =
				newAsteroidsData.getType().equals("asteroid") || 
				newAsteroidsData.getType().equals("asteroid_dense");
			getElement().getStyle().setProperty("display", hide ? "none" : "");
		}
		
		asteroidsData = newAsteroidsData;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		asteroidsData = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
