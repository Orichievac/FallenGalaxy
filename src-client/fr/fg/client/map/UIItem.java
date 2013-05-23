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

package fr.fg.client.map;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.BaseWidget;
import fr.fg.client.openjwt.core.Point;

public abstract class UIItem extends BaseWidget {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	protected UIItemRenderingHints hints;
	
	private boolean culled;
	
	private Widget parent;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public UIItem(UIItemRenderingHints hints) {
		this.hints = hints;
		this.culled = false;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public UIItemRenderingHints getRenderingHints() {
		return hints;
	}
	
	public final void setRenderingHints(UIItemRenderingHints hints) {
		this.hints = hints;
		onRenderingHintsUpdate();
	}
	
	public void onRenderingHintsUpdate() {
		// A redéfinir au besoin
	}
	
	public void onDataUpdate(Object newData) {
		// A redéfinir au besoin
	}
	
	public void onCullStateUpdate(boolean culled) {
		// A redéfinir au besoin
	}
	
	public void onDestroy() {
		// A redéfinir au besoin
	}
	
	public final void destroy() {
		parent = null;
		hints = null;
		
		onDestroy();
	}
	
	public boolean isDestroyed() {
		return hints == null;
	}
	
	public void updateCulledState() {
		Point location = getLocation();
		Point view = hints.getMap().getView();
		
		if (culled) {
			int offset = 250 + (int) (250 / hints.getZoom());
			
			if (!(view.getX() > location.getX() + offset ||
					view.getY() > location.getY() + offset ||
					view.getX() + OpenJWT.getClientWidth()  / hints.getZoom() < location.getX() - offset ||
					view.getY() + OpenJWT.getClientHeight() / hints.getZoom() < location.getY() - offset)) {
				setCulled(false);
			}
		} else {
			int offset = 500 + (int) (250 / hints.getZoom());
			
			if (view.getX() > location.getX() + offset ||
					view.getY() > location.getY() + offset ||
					view.getX() + OpenJWT.getClientWidth()  / hints.getZoom() < location.getX() - offset ||
					view.getY() + OpenJWT.getClientHeight() / hints.getZoom() < location.getY() - offset) {
				setCulled(true);
			}
		}
	}
	
	public final void setCulled(boolean culled) {
		if (this.culled != culled) {
			this.culled = culled;
			
			if (culled) {
				parent = getParent();
				removeFromParent();
			} else {
				if (parent != null) {
					((HasWidgets) parent).add(this);
					parent = null;
					onRenderingHintsUpdate();
				}
			}
			
			onCullStateUpdate(culled);
		}
	}
	
	public boolean isCulled() {
		return culled;
	}
	
	public abstract Point getLocation();
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
