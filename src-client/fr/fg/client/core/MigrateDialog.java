/*
Copyright 2010 Nicolas Bosc

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

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.PlayerSystemsData;
import fr.fg.client.data.StarSystemData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSList;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.OptionPaneListener;
import fr.fg.client.openjwt.ui.SelectionListener;

public class MigrateDialog extends JSDialog implements ActionCallback,
		SelectionListener, ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private PlayerSystemsData systemsData; //Liste des systèmes du joueur
	
	private Action currentAction;
	
	private JSList systemsList;
	
	private StarSystemData starSystemData; //Système sur lequel est la flotte
	
	private JSRowLayout mainLayout;
	
	private JSButton okBt2;
	
	private PlayerFleetData fleet;
	
	private long systemMigrationCost;
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public MigrateDialog(StarSystemData starSystemData, PlayerFleetData fleet, long systemMigrationCost) {
		super("Migration", false, true, true);
		
		this.starSystemData=starSystemData;
		this.systemMigrationCost=systemMigrationCost;
		this.fleet=fleet;
		StaticMessages messages = GWT.create(StaticMessages.class);

		systemsList = new JSList();
		systemsList.setPixelSize(400, 260);
		systemsList.addSelectionListener(this);
		
		// Bouton OK	
		okBt2 = new JSButton(messages.ok());
		okBt2.setPixelWidth(100);
		okBt2.addClickListener(this);
		okBt2.setVisible(false);
		
		
		mainLayout = new JSRowLayout();
		mainLayout.setPixelSize(400, 280);
		mainLayout.addComponent(systemsList);
		mainLayout.addRowSeparator(2);
		mainLayout.addComponent(okBt2);
		
		setComponent(mainLayout);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible) {
			systemsList.removeSelectionListener(this);
			currentAction = null;
		}
		
		if (visible) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			currentAction = new Action("getsystems", Action.NO_PARAMETERS, this);
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		this.systemsData = data.getPlayerSystems();
		updateUI();
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if(sender == systemsList)
		{
			okBt2.setVisible(true);
		}
		
		mainLayout.update();
		centerOnScreen();
			
	}
	
	public void onClick(Widget sender) {
		if ( sender == okBt2) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			SystemUI systemUI = (SystemUI) systemsList.getSelectedItem();
			
			JSOptionPane.showMessageDialog("Vous risquez de perdre des bâtiments si le système " +
					"cible n'a pas la même configuration que le système à migrer.<br/><br/>" +
					"Voulez-vous migrer le " +
					"système "+ systemUI.systemData.getName() +" pour " +
					systemMigrationCost + "&nbsp;" +
					"<img class=\"resource credits\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\" " +
					"unselectable=\"on\"/>&nbsp;?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.YES_OPTION) {
								
								SystemUI systemUI = (SystemUI) systemsList.getSelectedItem();
								
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("fleet", String.valueOf(fleet.getId()));
								
								params.put("system", String.valueOf(systemUI.systemData.getId()));
								
								new Action("migration", params,
									UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
								
								setVisible(false);
							}
						}
					}
				);
			}
		}

	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateUI() {
		ArrayList<SystemUI> systemsUI = new ArrayList<SystemUI>();
		

			for (int i = 0; i < systemsData.getSystemsCount(); i++)
				// Si le quadrant actuel est plus élevé que le quadrant où se situe
				// un système du joueur, alors on autorise la migration
				if(systemsData.getSystemAt(i).getArea().getSector().getType()<
						fleet.getArea().getSector().getType())
					systemsUI.add(new SystemUI(systemsData.getSystemAt(i), starSystemData));
			
			mainLayout.clear();
			mainLayout.addComponent(new JSLabel("<span style=\"margin-left:20px;color:red;\"><b>Systèmes de quadrants inférieurs disponibles:</b></span> &nbsp;"));
			mainLayout.addRowSeparator(1);
			systemsList.setItems(systemsUI);
			mainLayout.addComponent(systemsList);
			mainLayout.addRowSeparator(1);
			mainLayout.addComponent(okBt2);

		mainLayout.update();
	}

	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class SystemUI {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private PlayerStarSystemData systemData;
		
		private StarSystemData starSystemData;

		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public SystemUI(PlayerStarSystemData systemData, StarSystemData starSystemData) {
			this.systemData = systemData;
			
			this.starSystemData=starSystemData;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		@Override
		public String toString() {
			return "<div class=\"migration\">" +
				"<table cellspacing=\"0\" style=\"width: 100%;\"><tr>" +
				"<td><div class=\"star\" style=\"background-position : -" + (systemData.getStarImage() - 1) * 30 + "px -225px;\">"+"&nbsp;</div></td>"+
				"<td style=\"width: 60%;\">" + systemData.getName() + "</td>" +
				"</tr></div>";
			
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
