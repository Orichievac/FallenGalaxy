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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.core.ToolTipManager;

public abstract class ToolTipTextUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static int nextId = 0;
	
	private Element toolTipElement;
	
	private String toolTipElementId, id;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ToolTipTextUpdater(Element toolTipElement, String id) {
		this.toolTipElement = toolTipElement;
		this.id = id;
	}

	public ToolTipTextUpdater(String toolTipElementId, String id) {
		this.toolTipElementId = toolTipElementId;
		this.id = id;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void destroy() {
		toolTipElement = null;
		toolTipElementId = null;
		id = null;
	}
	
	public static String generateId() {
		return "time" + (int) Math.floor(++nextId); //$NON-NLS-1$
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	protected void setInnerHTML(String html) {
		Element timedElement = DOM.getElementById(id);
		
		if (timedElement != null) {
			timedElement.setInnerHTML(html);
			
			for (int i = 0; i < 4; i++) {
				Element outlineElement = DOM.getElementById(id + "_" + i); //$NON-NLS-1$
				if (outlineElement != null)
					outlineElement.setInnerHTML(html);
			}
		}
		
		Element toolTipElement = toolTipElementId != null ?
				DOM.getElementById(toolTipElementId) : this.toolTipElement;
		
		if (toolTipElement != null && ToolTipManager.getToolTipText(toolTipElement) != null) {
			ToolTipManager.setToolTipText(toolTipElement,
					ToolTipManager.getToolTipText(toolTipElement).replaceAll(
					"(<[^>]+id=\"" + id + "\"[^>]*>)[^<]*<", "$1" + html + "<")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}
}
