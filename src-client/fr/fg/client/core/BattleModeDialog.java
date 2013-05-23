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

package fr.fg.client.core;

import java.util.HashMap;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;

public class BattleModeDialog extends JSDialog implements ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int playerFleetId, enemyFleetId;
	
	private JSButton tacticsBt, skirmishBt, battleBt;
	
	private boolean bombing;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BattleModeDialog(int playerFleetId, int enemyFleetId, boolean bombing) {
		super("Combat", true, true, true);
		
		this.playerFleetId = playerFleetId;
		this.enemyFleetId = enemyFleetId;
		this.bombing = bombing;
		
		JSLabel titleLabel = new JSLabel("<span class=\"title\" style=\"font-size: 14px;\">&nbsp;&nbsp;Mode de combat</span>");
		
		// Description des modes de combat
		HTMLPanel skirmishDescription = new HTMLPanel("<b unselectable=\"on\">Escarmouche :</b> exécute les 5 actions que vous avez défini pour le mode escarmouche, immobilise votre flotte 2h.");
		skirmishDescription.setWidth("290px");
		skirmishDescription.getElement().setAttribute("unselectable", "on");
		skirmishDescription.addStyleName("justify");
		skirmishDescription.getElement().getStyle().setProperty("padding", "5px");
		OpenJWT.setElementFloat(skirmishDescription.getElement(), "left");
		
		HTMLPanel battleDescription = new HTMLPanel("<b unselectable=\"on\">Bataille :</b> exécute les 15 actions que vous avez défini pour le mode bataille, immobilise votre flotte 4h.");
		battleDescription.setWidth("290px");
		battleDescription.getElement().setAttribute("unselectable", "on");
		battleDescription.addStyleName("justify");
		battleDescription.getElement().getStyle().setProperty("padding", "5px");
		OpenJWT.setElementFloat(battleDescription.getElement(), "left");
		
		// Choix du mode de combat
		tacticsBt = new JSButton("Voir les options tactiques");
		tacticsBt.setPixelWidth(220);
		tacticsBt.addClickListener(this);
		
		skirmishBt = new JSButton("Escarmouche");
		skirmishBt.setPixelWidth(150);
		skirmishBt.addClickListener(this);
		
		battleBt = new JSButton("Bataille");
		battleBt.setPixelWidth(150);
		battleBt.addClickListener(this);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		layout.addRowSeparator(8);
		layout.addComponent(titleLabel);
		layout.addRowSeparator(12);
		layout.addComponent(skirmishDescription);
		layout.addRowSeparator(5);
		layout.addComponent(battleDescription);
		layout.addRowSeparator(10);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(tacticsBt);
		layout.addRowSeparator(15);
		layout.addComponent(skirmishBt);
		layout.addComponent(battleBt);
		
		layout.getElement().getStyle().setProperty("background",
				"url('" + Config.getMediaUrl() + "images/misc/battle.png') 0 0 no-repeat");
		
		setComponent(layout);
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
		centerOnScreen();
	}

	public void onClick(Widget sender) {
		if (sender == tacticsBt) {
			PlayerFleetData playerFleet = Client.getInstance(
					).getEmpireView().getFleetById(playerFleetId);
			
			if (playerFleet != null)
				Client.getInstance().getTacticsDialog().show(playerFleet);
		} else if (sender == skirmishBt || sender == battleBt) {
			setVisible(false);
			
			// Combat !
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("fleet", String.valueOf(playerFleetId));
			params.put("enemy", String.valueOf(enemyFleetId));
			params.put("mode", sender == skirmishBt ? "0" : "1");
			params.put("bombing", String.valueOf(bombing));
			
			new Action("battle", params, new ActionCallbackAdapter() {
				public void onSuccess(AnswerData data) {
					SelectionManager.getInstance().setNoSelection();
					
					if (data.getReport().isDelude()) {
						// Leurre
						JSOptionPane.showMessageDialog("La flotte était un leurre !",
							"Leurre", JSOptionPane.OK_OPTION,
							JSOptionPane.INFORMATION_MESSAGE, null);
					} else {
						// Affiche la bataille
						BattleReport battleReport = Client.getInstance().getBattleReport();
						battleReport.cacheReport(data.getReport());
						battleReport.showReport(data.getReport().getId());
					}
				};
			});
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
