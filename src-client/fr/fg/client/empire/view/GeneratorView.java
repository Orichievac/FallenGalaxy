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

package fr.fg.client.empire.view;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import fr.fg.client.core.Client;
import fr.fg.client.core.Utilities;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.PlayerGeneratorData;
import fr.fg.client.empire.View;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class GeneratorView extends View {
	// --------------------------------------------------- CONSTANTES -- //
	// ---------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	
	private int x, y;
	
	private int idArea;
	
	// ------------------------------------------------ CONSTRUCTEURS -- //
	
	public GeneratorView(PlayerGeneratorData generatorData, long lastUpdate) {
		this.id = generatorData.getId();
		this.idArea = generatorData.getArea().getId();
		this.x = generatorData.getX();
		this.y = generatorData.getY();
		
		setStyleName("view generator");
		getElement().setAttribute("unselectable", "on");
		
		Element graphics = DOM.createDiv();
		graphics.setAttribute("unselectable", "on");
		graphics.setClassName("graphics");
		graphics.getStyle().setProperty("backgroundPosition",
			"-150px -" + (30 * generatorData.getGraphics()) + "px");
		getElement().appendChild(graphics);
		
		if (generatorData.getShortcut() != -1) {
			OutlineText text = TextManager.getText(String.valueOf(generatorData.getShortcut()));
			
			Element shortcut = DOM.createDiv();
			shortcut.setClassName("shortcut");
			shortcut.setAttribute("unselectable", "on");
			shortcut.appendChild(text.getElement());
			
			getElement().appendChild(shortcut);
		}
		
		String[] colors = {"#ff0000", "#ff7201", "#ffd800", "#ceff01", "#00ff00"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		double[] thresholds = {.2, .4, .6, .8, 1};
		
		double coef = generatorData.getHull() / (double) generatorData.getMaxHull();
		String color = "";
		for (int i = 0; i < thresholds.length; i++)
			if (coef <= thresholds[i]) {
				color = colors[i];
				break;
			}
		
		setToolTipText(
			"<div class=\"owner-player\"><b>" + generatorData.getName() + "</b></div>" +
			"<div>Niveau : <b>" + generatorData.getLevel() + "</b></div>" +
			"<div>Points de structure : <b style=\"color: " + color + ";\">" +
			Formatter.formatNumber(Math.round(generatorData.getHull())) + "</b> / " +
			"<b>" + Formatter.formatNumber(Math.round(generatorData.getMaxHull())) + "</b></div>" +
			"<div>Energie : <b>" + generatorData.getUsedEnergy() + " / " +
			generatorData.getMaxEnergy() + "</b> " +
			Utilities.getEnergyImage() + "</div>", 200);
		
		sinkEvents(Event.ONCLICK | Event.ONDBLCLICK);
	}
	
	// ----------------------------------------------------- METHODES -- //

	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			Client.getInstance().getAreaContainer().setIdArea(idArea, new Point(x, y));
			break;
		case Event.ONDBLCLICK:
			IndexedAreaData area = Client.getInstance().getAreaContainer().getArea();
			if (area.getId() == idArea)
				SelectionManager.getInstance().selectStructure(id);
			break;
		}
	}
	
	// --------------------------------------------- METHODES PRIVEES -- //
}
