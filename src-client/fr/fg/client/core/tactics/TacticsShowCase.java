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

package fr.fg.client.core.tactics;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;

public class TacticsShowCase extends JSDialog implements ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static TacticsShowCase instance = new TacticsShowCase();
	
	private JSButton okBt, exportBt;
	
	private FleetScheme scheme;
	
	private FleetTactics tactics;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private TacticsShowCase() {
		super("Tactique", false, true, true);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		scheme = new FleetScheme(false);
		tactics = new FleetTactics();
		
		exportBt = new JSButton();
		exportBt.setPixelWidth(31);
		exportBt.addStyleName("iconExport");
		exportBt.addClickListener(this);
		exportBt.setToolTipText("<div class=\"title\">Exporter tactique</div>" +
			"<div>Génère un code pour pouvoir partager votre tactique.</div>", 200);
		
		// Bouton OK
		okBt = new JSButton(messages.ok());
		okBt.setWidth(100 + "px"); //$NON-NLS-1$
		okBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(scheme);
		layout.addComponent(JSRowLayout.createHorizontalSeparator(209));
		layout.addComponent(exportBt);
		layout.addRowSeparator(10);
		layout.addComponent(tactics);
		layout.addRowSeparator(10);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(okBt);
		
		setComponent(layout);
		centerOnScreen();
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			setVisible(false);
		} else if (sender == exportBt) {
			JSOptionPane.showInputDialog("Copiez / collez le texte suivant " +
				"sur le chat ou dans un message pour partager votre tactique.",
				"Tactique", JSOptionPane.OK_OPTION,
				JSOptionPane.INFORMATION_MESSAGE, null,
				TacticsTools.hashCode(scheme, tactics));
		}
	}
	
	public static void show(String hashCode) {
		try {
			TacticsTools.load(hashCode, instance.scheme, instance.tactics, false, null);
			instance.setVisible(true);
		} catch (Exception e) {
			JSOptionPane.showMessageDialog("Tactique invalide.", "Erreur",
				JSOptionPane.OK_OPTION, JSOptionPane.ERROR_MESSAGE, null);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
