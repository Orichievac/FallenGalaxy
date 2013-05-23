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

import com.google.gwt.user.client.Event;

import fr.fg.client.empire.Tree;
import fr.fg.client.empire.View;

public class ScrollView extends View {
	// --------------------------------------------------- CONSTANTES -- //
	// ---------------------------------------------------- ATTRIBUTS -- //
	
	private Tree tree;
	
	private boolean scrollUp;
	
	// ------------------------------------------------ CONSTRUCTEURS -- //
	
	public ScrollView(Tree tree, boolean scrollUp) {
		this.tree = tree;
		this.scrollUp = scrollUp;
		
		setStyleName("view empireScroll empireScroll" + (scrollUp ? "Up" : "Down"));
		getElement().setAttribute("unselectable", "on");
		
		getElement().setInnerHTML("<div class=\"content\" unselectable=\"on\"></div>");
		
		sinkEvents(Event.ONCLICK);
	}
	
	// ----------------------------------------------------- METHODES -- //

	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			if (scrollUp)
				tree.setRowOffset(tree.getRowOffset() - tree.getMaxRowsCount() - 2);
			else
				tree.setRowOffset(tree.getRowOffset() + tree.getMaxRowsCount() - 2);
			break;
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		tree = null;
	}
	
	// --------------------------------------------- METHODES PRIVEES -- //
}
