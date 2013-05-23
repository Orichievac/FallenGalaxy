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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.ResearchManager;
import fr.fg.client.core.SelectionTools;
import fr.fg.client.data.AbilityData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.GroupData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.ShipData;
import fr.fg.client.data.TacticData;
import fr.fg.client.data.TacticsData;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSList;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.OptionPaneListener;
import fr.fg.client.openjwt.ui.SelectionListener;

public class PlayerTacticsDialog extends JSDialog implements ClickListener, SelectionListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int VIEW_COMPATIBLE = 0, VIEW_ALL = 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSButton okBt, cancelBt, exportBt, renameBt, deleteBt, replenishmentBt;
	
	private JSComboBox filterComboBox;
	
	private JSList tacticsList;
	
	private ArrayList<TacticData> playerTactics;
	
	private FleetScheme scheme;
	
	private FleetTactics tactics;
	
	private ResearchManager researchManager;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public PlayerTacticsDialog(ResearchManager researchManager) {
		super("Tactiques enregistrées", true, true, true);
		
		StaticMessages messages = GWT.create(StaticMessages.class);
		
		this.researchManager = researchManager;
		this.playerTactics = new ArrayList<TacticData>();
		this.scheme = null;
		
		ArrayList<String> filters = new ArrayList<String>();
		filters.add("Compatibles avec la flotte");
		filters.add("Toutes les tactiques");
		
		filterComboBox = new JSComboBox();
		filterComboBox.setItems(filters);
		filterComboBox.setPixelWidth(200);
		filterComboBox.addSelectionListener(this);
		
		tacticsList = new JSList();
		tacticsList.setPixelSize(420, 300);
		tacticsList.addSelectionListener(this);
		
		exportBt = new JSButton();
		exportBt.setPixelWidth(31);
		exportBt.addStyleName("iconExport");
		exportBt.addClickListener(this);
		exportBt.setToolTipText("<div class=\"title\">Exporter tactique</div>" +
				"<div>Génère un code pour pouvoir partager votre tactique.</div>", 200);
		exportBt.getElement().getStyle().setProperty("visibility", "hidden");
		
		replenishmentBt = new JSButton();
		replenishmentBt.setPixelWidth(31);
		replenishmentBt.addStyleName("iconReplenishment");
		replenishmentBt.addClickListener(this);
		replenishmentBt.setToolTipText("<div class=\"title\">Réapprovsionnement en vaisseaux et tactique</div>" +
		"<div>Réapprivsionne votre/vos flotte(s) sélectionnée(s) selon une tactique définie.</div>", 200);
		replenishmentBt.getElement().getStyle().setProperty("visibility", "hidden");
		
		deleteBt = new JSButton();
		deleteBt.setPixelWidth(31);
		deleteBt.addStyleName("iconDelete");
		deleteBt.addClickListener(this);
		deleteBt.setToolTipText("<div class=\"title\">Supprimer tactique</div>");
		deleteBt.getElement().getStyle().setProperty("visibility", "hidden");
		
		renameBt = new JSButton("Renommer");
		renameBt.setPixelWidth(100);
		renameBt.addClickListener(this);
		renameBt.getElement().getStyle().setProperty("visibility", "hidden");
		
		// Boutons OK / Annuler
		okBt = new JSButton(messages.ok());
		okBt.setWidth(100 + "px"); //$NON-NLS-1$
		okBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setWidth(100 + "px"); //$NON-NLS-1$
		cancelBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(filterComboBox);
		layout.addComponent(JSRowLayout.createHorizontalSeparator(
			420 - filterComboBox.getPixelWidth() - renameBt.getPixelWidth() -
			deleteBt.getPixelWidth() - exportBt.getPixelWidth() - replenishmentBt.getPixelWidth()));
		layout.addComponent(renameBt);
		layout.addComponent(deleteBt);
		layout.addComponent(exportBt);
		layout.addComponent(replenishmentBt);
		layout.addRow();
		layout.addComponent(tacticsList);
		layout.addRowSeparator(5);
		layout.addComponent(okBt);
		layout.addComponent(cancelBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
			
		setComponent(layout);
		centerOnScreen();
		
		updateUI();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public int getCompatibleTacticsCount() {
		if (scheme == null || tactics == null)
			return 0;
		
		int count = 0;
		
		for (TacticData tactic : playerTactics)
			if (TacticsTools.isCompatible("[[" + tactic.getHash() + "]]", scheme, researchManager))
				count++;
		
		return count;
	}
	
	public void setPlayerTactics(TacticsData tacticsData) {
		ArrayList<TacticData> tactics = new ArrayList<TacticData>();
		
		for (int i = 0; i < tacticsData.getTacticsCount(); i++)
			tactics.add(tacticsData.getTacticAt(i));
		
		Collections.sort(tactics, new Comparator<TacticData>() {
			public int compare(TacticData t1, TacticData t2) {
				if (t1.getName().equals(t2.getName()))
					return t1.getHash().compareToIgnoreCase(t2.getHash());
				return t1.getName().compareToIgnoreCase(t2.getName());
			}
		});
		
		this.playerTactics = tactics;
		
		updateUI();
	}
	
	public void setFleet(FleetScheme scheme, FleetTactics tactics) {
		this.scheme = scheme;
		this.tactics = tactics;
		
		setView(VIEW_COMPATIBLE);
		
		return;
	}
	
	public void onClick(Widget sender) {
		
		if (sender == okBt) {
			if (tacticsList.getSelectedIndex() == -1) {
				JSOptionPane.showMessageDialog(
					"Sélectionnez une tactique, puis cliquez sur OK.",
					"Erreur", JSOptionPane.OK_OPTION,
					JSOptionPane.WARNING_MESSAGE, null);
				return;
			}
			
			String hash = ((PlayerTactic) tacticsList.getSelectedItem()).getHash();
			
			if (TacticsTools.load(hash, scheme, tactics, true, researchManager))
				setVisible(false);
		} else if (sender == cancelBt) {
			setVisible(false);
		} else if (sender == exportBt) {
			FleetScheme fleetScheme = new FleetScheme(false);
			FleetTactics fleetTactics = new FleetTactics();
			
			TacticsTools.load(((PlayerTactic) tacticsList.getSelectedItem()).getHash(),
					fleetScheme, fleetTactics, false, null);
			
			JSOptionPane.showInputDialog("Copiez / collez le texte suivant " +
				"sur le chat ou dans un message pour partager votre tactique.",
				"Tactique", JSOptionPane.OK_OPTION,
				JSOptionPane.INFORMATION_MESSAGE, null,
				TacticsTools.hashCode(fleetScheme, fleetTactics));
		} else if (sender == renameBt) {
			JSOptionPane.showInputDialog("Indiquez le nom de la tactique :",
				"Tactique", JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if (option != null) {
							if (currentAction != null && currentAction.isPending())
								return;

							String hashCode = ((PlayerTactic) tacticsList.getSelectedItem()).getHash();
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("hash", hashCode.substring(2, hashCode.length() - 2));
							params.put("name", (String) option);
							
							currentAction = new Action("tactics/save", params, new ActionCallbackAdapter() {
								@Override
								public void onSuccess(AnswerData data) {
									setPlayerTactics(data.getTactics());
								}
							});
						}
					}
			}, ((PlayerTactic) tacticsList.getSelectedItem()).getName());
		} else if (sender == deleteBt) {
			JSOptionPane.showMessageDialog("Voulez-vous supprimer cette tactique ?",
				"Tactique", JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.YES_OPTION) {
							if (currentAction != null && currentAction.isPending())
								return;
							
							String hashCode = ((PlayerTactic) tacticsList.getSelectedItem()).getHash();
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("hash", hashCode.substring(2, hashCode.length() - 2));
							
							currentAction = new Action("tactics/delete", params, new ActionCallbackAdapter() {
								@Override
								public void onSuccess(AnswerData data) {
									setPlayerTactics(data.getTactics());
								}
							});
						}
					}
			});
		}
		else if (sender == replenishmentBt) {
			JSOptionPane.showMessageDialog("Voulez-vous réapprovisionner les flottes sélectionnées avec cette tactique ?",
				"Réapprovisionnement", JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.YES_OPTION) {
							if (currentAction != null && currentAction.isPending())
								return;
							
			    FleetScheme fleetScheme = new FleetScheme(false);	
			    FleetTactics fleetTactics = new FleetTactics();
			    
			    TacticsTools.load(((PlayerTactic) tacticsList.getSelectedItem()).getHash(),
				fleetScheme, fleetTactics, false, null);
			    
			    PlayerFleetData[] selectedFleetsData = getSelectedFleets();
			    String hashCode = ((PlayerTactic) tacticsList.getSelectedItem()).getHash();
				HashMap<String, String> params = new HashMap<String, String>();	

				for (int i = 0; i < selectedFleetsData.length; i++){
					
				if(!selectedFleetsData[i].isOverSystem() && !selectedFleetsData[i].getSystemTreaty().equals("player")){
					JSOptionPane.showMessageDialog((selectedFleetsData.length == 1 ? 
							"La flotte sélectionnée n'est pas sur un de vos systèmes" : "Les flottes sélectionnées ne sont" +
							"pas sur un de vos systèmes"),
							"Action invalide", JSOptionPane.OK_OPTION,
							JSOptionPane.WARNING_MESSAGE, null);
				}
				else{
params.put("fleet" + i, String.valueOf(selectedFleetsData[i].getId())); //Récupère les id's des flottes sélectionnées
params.put("system", String.valueOf(selectedFleetsData[i].getSystem())); //Récupère l'id du système de où les flottes sont posées
params.put("hash", hashCode.substring(2, hashCode.length() - 2)); // Met le hash du code de la tactique aux flottes
				}
				}

for(int j = 0; j < 5; j++){
params.put("slot_id_" + j, String.valueOf(fleetScheme.getShipId(j)));
params.put("tactic_slot" + j + "_count", String.valueOf(fleetScheme.getCount(j)));
	
}


							currentAction = new Action("tactics/settactic", params, new ActionCallbackAdapter() {
								@Override
								public void onSuccess(AnswerData data) {

									setVisible(false);
									return;
								}
							});
						
						}
					}
			});
		}
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (sender == filterComboBox) {
			updateUI();
		} else if (sender == tacticsList) {
			String visibility = newValue == -1 ? "hidden" : "";
			exportBt.getElement().getStyle().setProperty("visibility", visibility);
			deleteBt.getElement().getStyle().setProperty("visibility", visibility);
			renameBt.getElement().getStyle().setProperty("visibility", visibility);
			replenishmentBt.getElement().getStyle().setProperty("visibility", visibility);
		}
	}
	
	public void setView(int view) {
		filterComboBox.setSelectedIndex(view);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private PlayerFleetData[] getSelectedFleets() {
		return SelectionTools.getSelectedFleets();
	}
	
	
	
	private void updateUI() {
		switch (filterComboBox.getSelectedIndex()) {
		case VIEW_COMPATIBLE:
			// Affiche les tactiques compatibles avec la flotte
			ArrayList<PlayerTactic> compatibleTactics = new ArrayList<PlayerTactic>();
			
			if (scheme != null) {
				for (TacticData tactic : playerTactics)
					if (TacticsTools.isCompatible("[[" + tactic.getHash() + "]]", scheme, researchManager))
						compatibleTactics.add(new PlayerTactic(tactic.getName(), "[[" + tactic.getHash() + "]]"));
			}
			
			tacticsList.setItems(compatibleTactics);
			break;
		case VIEW_ALL:
			// Affiche toutes les tactiques
			ArrayList<PlayerTactic> allTactics = new ArrayList<PlayerTactic>();
			
			for (TacticData tactic : playerTactics) {
				if (TacticsTools.isValidHashCode("[[" + tactic.getHash() + "]]"))
					allTactics.add(new PlayerTactic(tactic.getName(), "[[" + tactic.getHash() + "]]"));
			}

			tacticsList.setItems(allTactics);
			break;
		}
	}
	
	private static class PlayerTactic {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private static FleetScheme fleetScheme = new FleetScheme(false);
		
		private static FleetTactics fleetTactics = new FleetTactics();
		
		private String html;
		
		private String name;
		
		private String hash;

		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public PlayerTactic(String name, String hash) {
			this.name = name;
			this.hash = hash;

			
			TacticsTools.load(hash, fleetScheme, fleetTactics, false, null);
			
			StringBuffer buffer = new StringBuffer();

				
			buffer.append("<div style=\"float: left; font-weight: bold; margin: 3px;\" unselectable=\"on\">" +
				GroupData.getLevelAtPower(fleetScheme.getPower()) + "<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"stat s-power\" unselectable=\"on\"/>" +
				(name.length() > 0 ? " - " + name : "") + "</div>");
			
			buffer.append("<div style=\"clear: both; font-size: 1px;\"></div>");
	
			for (int i = 0; i < 5; i++)
				if (fleetScheme.getShipId(i) != 0)
					
					buffer.append("<div class=\"control\">" +
						"<div unselectable=\"on\" class=\"" +
						(fleetScheme.isFront(i) ? "frontLine" : "backLine") + "\"></div>" +
						"<div unselectable=\"on\" class=\"spaceship\" style=\"" +
						"background-position: -" + (fleetScheme.getShipId(i) * 50) + "px 0\">" +
						Formatter.formatNumber(fleetScheme.getCount(i), true) + "</div></div>");
			 
				else
					buffer.append("<div class=\"control\" unselectable=\"on\"></div>");
			
			buffer.append("<div style=\"clear: both; font-size: 1px; height: 1px;\"></div>");
			
			for (int i = 0; i < 5; i++)
				buffer.append(getAbility(FleetTactics.VIEW_SKIRMISH, i));
			
			buffer.append("<div style=\"clear: both; font-size: 1px; height: 1px;\"></div>");
			
			for (int i = 0; i < 15; i++)
				buffer.append(getAbility(FleetTactics.VIEW_BATTLE, i));
			
			buffer.append("<div style=\"clear: both; font-size: 1px;\"></div>");
			
			buffer.append("<div style=\"clear: both; font-size: 1px;\"></div>");
				
			buffer.append("<div style=\"clear: both; font-size: 1px;\"></div>");
			html = buffer.toString();
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public String getName() {
			return name;
		}
		
		public String getHash() {
			return hash;
		}
		
		@Override
		public String toString() {
			return html;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
		
		private String getAbility(int view, int index) {
			int shipId = fleetTactics.getShipId(view, index);
			
			if (shipId == -1) {
				return "<div class=\"actionAbility noAction\" " +
					"style=\"float: left; margin-left: 1px;\"></div>";
			} else {
				ShipData ship = ShipData.SHIPS[shipId];
				int ability = fleetTactics.getAbility(view, index);
				
				return "<div class=\"actionAbility\" style=\"float: left; " +
					"background-position: -" + ((ability == -1 ? 0 :
					AbilityData.GRAPHICS[ship.getAbilities()[ability].getType()]) * 25) +
					"px -100px; margin-left: 1px;\"></div>";
			}
			
		}
				
	}
}
