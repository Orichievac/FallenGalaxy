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

package fr.fg.client.animation;

import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.core.ResourcesManager;
import fr.fg.client.openjwt.animation.TimerHandler;

public class BuildingUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Widget widget;
	
	private int[] cost;
	
	private int systemId;
	
	private ResourcesManager resourcesManager;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BuildingUpdater(Widget widget, int[] cost,
			ResourcesManager resourcesManager, int systemId) {
		this.widget = widget;
		this.cost = cost;
		this.resourcesManager = resourcesManager;
		
		update(0);
	}

	// --------------------------------------------------------- METHODES -- //
	
	public boolean isDisabled() {
		for (int i = 0; i < 4; i++)
			if (cost[i] > resourcesManager.getCurrentResource(systemId, i))
				return true;
		
		if (cost[4] > resourcesManager.getCurrentCredits())
			return true;
		return false;
	}
	
	public boolean isFinished() {
		return false;
	}
	
	public void update(int interpolation) {
		if (isDisabled())
			widget.addStyleName("disabled"); //$NON-NLS-1$
		else
			widget.removeStyleName("disabled"); //$NON-NLS-1$
	}
	
	public void destroy() {
		widget = null;
		cost = null;
		resourcesManager = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
