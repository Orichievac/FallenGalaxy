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

package fr.fg.client.core.ally;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSRowLayout;

public class NoAllyDialog extends JSDialog implements ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSButton createAllyBt, applyBt;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public NoAllyDialog() {
		super("Alliance", false, true, true);
		
		HTMLPanel descriptionPanel = new HTMLPanel(
			"<div class=\"center\">" +
			"Vous n'avez pas d'alliance.<br/><br/>" +
			"Vous pouvez fonder une nouvelle " +
			"alliance ou postuler à une alliance existante.</div>");
		descriptionPanel.setWidth("260px");
		descriptionPanel.getElement().getStyle().setProperty("padding", "20px");
		
		// Boutons pour créer / postuler à une alliance
		createAllyBt = new JSButton("Fonder une alliance");
		createAllyBt.setPixelWidth(150);
		createAllyBt.addClickListener(this);
		
		applyBt = new JSButton("Postuler");
		applyBt.setPixelWidth(150);
		applyBt.addClickListener(this);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(descriptionPanel);
		layout.addRow();
		layout.addComponent(createAllyBt);
		layout.addComponent(applyBt);
		
		setComponent(layout);
		centerOnScreen();
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void onClick(Widget sender) {
		if (sender == createAllyBt) {
			CreateAllyDialog dialog = new CreateAllyDialog();
			dialog.setVisible(true);
		} else if (sender == applyBt) {
			ApplyDialog dialog = new ApplyDialog();
			dialog.setVisible(true);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
