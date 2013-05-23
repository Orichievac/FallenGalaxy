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

import com.google.gwt.user.client.Element;

import fr.fg.client.core.ResourcesManager;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.openjwt.animation.TimerHandler;

public class ResourcesUpdater extends ToolTipTextUpdater implements TimerHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		RESOURCE_0 = 0,
		RESOURCE_1 = 1,
		RESOURCE_2 = 2,
		RESOURCE_3 = 3,
		RESOURCE_CREDITS = 4;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ResourcesManager resourcesManager;
	
	private int systemId;
	
	private int resource;
	
	private int requiredValue;
	
	private boolean useSIPrefix;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ResourcesUpdater(String toolTipElementId, String id,
			ResourcesManager resourcesManager, int systemId, int resourceIndex,
			boolean useSIPrefix) {
		this(toolTipElementId, id, resourcesManager, systemId, resourceIndex, -1, useSIPrefix);
	}
	
	public ResourcesUpdater(String toolTipElementId, String id,
			ResourcesManager resourcesManager, int systemId, int resource,
			int requiredValue, boolean useSIPrefix) {
		super(toolTipElementId, id);
		
		this.resourcesManager = resourcesManager;
		this.systemId = systemId;
		this.resource = resource;
		this.requiredValue = requiredValue;
		this.useSIPrefix = useSIPrefix;
		
		setInnerHTML(getValue());
	}
	
	public ResourcesUpdater(Element toolTipElement, String id,
			ResourcesManager resourcesManager, int systemId, int resource,
			boolean useSIPrefix) {
		super(toolTipElement, id);
		
		this.resourcesManager = resourcesManager;
		this.systemId = systemId;
		this.resource = resource;
		this.requiredValue = -1;
		this.useSIPrefix = useSIPrefix;
		
		setInnerHTML(getValue());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getValue() {
		double currentValue = resource == RESOURCE_CREDITS ?
			resourcesManager.getCurrentCredits() :
			resourcesManager.getCurrentResource(systemId, resource);
		
		return requiredValue != -1 ? "<span style=\"color: " + //$NON-NLS-1$
			(currentValue > requiredValue ? "#00c000" : "red") + ";\">" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			Formatter.formatNumber(Math.floor(currentValue), useSIPrefix) + "</span>" : //$NON-NLS-1$
			Formatter.formatNumber(Math.floor(currentValue), useSIPrefix);
	}
	
	public void update(int interpolation) {
		setInnerHTML(getValue());
	}
	
	public boolean isFinished() {
		return false;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
