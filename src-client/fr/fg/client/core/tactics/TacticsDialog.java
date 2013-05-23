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
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.Client;
import fr.fg.client.core.ResearchManager;
import fr.fg.client.core.Tutorial;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.data.AbilityData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.ShipData;
import fr.fg.client.data.SlotInfoData;
import fr.fg.client.data.WeaponGroupData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.DialogListener;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSList;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.OptionPaneListener;
import fr.fg.client.openjwt.ui.SelectionListener;

public class TacticsDialog extends JSDialog implements SelectionListener,
		ClickListener, TacticsListener, DialogListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSList actionsList;
	
	private PlayerFleetData currentFleet;
	
	private JSButton okBt, cancelBt, exportBt, importBt, saveBt, loadBt;
	
	private ArrayList<Integer> shipsId, abilities;
	
	private ArrayList<Long> shipsCount;
	
	private boolean updatingSelection;
	
	private Action currentAction;
	
	private FleetScheme scheme;
	
	private FleetTactics tactics;
	
	private int[][] actions;
	
	private HashMap<Integer, Integer> shipsAvailableAbilities;
	
	private ResearchManager researchManager;
	
	private PlayerTacticsDialog playerTacticsDialog;

	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public TacticsDialog(ResearchManager researchManager,
			PlayerTacticsDialog playerTacticsDialog) {
		super("Tactique", true, true, true);
		
		this.researchManager = researchManager;
		this.playerTacticsDialog = playerTacticsDialog;
		this.playerTacticsDialog.addDialogListener(this);
		
		actions = new int[2][];
		actions[FleetTactics.VIEW_SKIRMISH] = new int[5];
		actions[FleetTactics.VIEW_BATTLE] = new int[15];
		
		shipsAvailableAbilities = new HashMap<Integer, Integer>();
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		actionsList = new JSList();
		actionsList.setPixelSize(510, 172);
		actionsList.addStyleName("actions");
		actionsList.addSelectionListener(this);
		
		JSLabel[] separators = new JSLabel[4];
		for (int i = 0; i < 4; i++) {
			separators[i] = new JSLabel();
			separators[i].setPixelSize(10, 101);
		}
		
		JSLabel scheduledActionsHeader = new JSLabel("<b unselectable=\"on\">Séquence d'actions</b>");
		scheduledActionsHeader.setPixelWidth(270);
		
		scheme = new FleetScheme(true);
		
		tactics = new FleetTactics();
		tactics.addTacticsListener(this);
		
		JSLabel actionsHeader = new JSLabel("<b unselectable=\"on\">Actions disponibles</b>");
		actionsHeader.setPixelWidth(510);
		
		exportBt = new JSButton();
		exportBt.setPixelWidth(31);
		exportBt.addStyleName("iconExport");
		exportBt.addClickListener(this);
		exportBt.setToolTipText("<div class=\"title\">Exporter tactique</div>" +
			"<div>Génère un code pour pouvoir partager votre tactique.</div>", 200);
		
		importBt = new JSButton();
		importBt.setPixelWidth(31);
		importBt.addStyleName("iconImport");
		importBt.addClickListener(this);
		importBt.setToolTipText("<div class=\"title\">Importer tactique</div>" +
			"<div>Chargez une tactique à partir d'un code.</div>", 200);
		importBt.getElement().getStyle().setProperty("visibility",
			Settings.isPremium() ? "" : "hidden");
		
		saveBt = new JSButton();
		saveBt.setPixelWidth(31);
		saveBt.addStyleName("iconSave");
		saveBt.addClickListener(this);
		saveBt.setToolTipText("<div class=\"title\">Sauvegarder tactique</div>" +
			"<div>Enregistre la tactique dans votre liste de tactiques afin " +
			"que vous puissiez l'appliquer à d'autres flottes.</div>", 200);
		
		loadBt = new JSButton("Charger");
		loadBt.setPixelWidth(100);
		loadBt.addClickListener(this);
		loadBt.setToolTipText("<div class=\"title\">Charger tactique</div>" +
			"<div>Charge une tactique enregistrée compatible avec les " +
			"vaisseaux de votre flotte.</div>", 200);
		loadBt.getElement().getStyle().setProperty("visibility",
			Settings.isPremium() ? "" : "hidden");
		
		// Boutons OK / Annuler
		okBt = new JSButton(messages.ok());
		okBt.setWidth(100 + "px"); //$NON-NLS-1$
		okBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setWidth(100 + "px"); //$NON-NLS-1$
		cancelBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(scheme);
		layout.addComponent(JSRowLayout.createHorizontalSeparator(47));
		layout.addComponent(loadBt);
		layout.addComponent(saveBt);
		layout.addComponent(importBt);
		layout.addComponent(exportBt);
		layout.addRowSeparator(10);
		layout.addComponent(tactics);
		layout.addRowSeparator(10);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(actionsHeader);
		layout.addRow();
		layout.addComponent(actionsList);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addRowSeparator(10);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(okBt);
		layout.addComponent(cancelBt);
		
		setComponent(layout);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void show(PlayerFleetData fleet) {
		updatingSelection = false;
		currentFleet = fleet;
		
		shipsId = new ArrayList<Integer>();
		abilities = new ArrayList<Integer>();
		shipsCount = new ArrayList<Long>();
		
		for (int i = 0; i < fleet.getSlotsCount(); i++) {
			SlotInfoData slot = fleet.getSlotAt(i);
			scheme.setShip(i, slot.getId(),
				(long) slot.getCount(), slot.isFront());
		}
		
		shipsAvailableAbilities.clear();
		
		shipsId.add(-1);
		abilities.add(-1);
		shipsCount.add(0l);
		
		for (int i = 0; i < fleet.getSlotsCount(); i++) {
			SlotInfoData slot = fleet.getSlotAt(i);
			
			if (slot.getId() != 0) {
				ShipData ship = ShipData.SHIPS[slot.getId()];
				int availableAbilities = researchManager.getShipAvailableAbilities(ship);
				shipsAvailableAbilities.put(slot.getId(), availableAbilities);
				
				if (ship.getWeapons().length > 0) {
					shipsId.add(slot.getId());
					abilities.add(-1);
					shipsCount.add((long) slot.getCount());
				}
				
				for (int j = 0; j < ship.getAbilities().length; j++) {
					if (!ship.getAbilities()[j].isPassive() &&
							(availableAbilities & (1 << j)) != 0) {
						shipsId.add(slot.getId());
						abilities.add(j);
						shipsCount.add((long) slot.getCount());
					}
				}
			}
		}
		
		// Charge la tactique employée en escarmouche
		for (int i = 0; i < fleet.getSkirmishActionSlotsCount(); i++) {
			int slot = fleet.getSkirmishActionSlotAt(i);
			
			if (slot == -1) {
				setAction(FleetTactics.VIEW_SKIRMISH, i, 0);
				tactics.setNoAction(FleetTactics.VIEW_SKIRMISH, i);
			} else {
				for (int j = 0; j < shipsId.size(); j++)
					if (shipsId.get(j) == fleet.getSlotAt(slot).getId()) {
						int ability = fleet.getSkirmishActionAbilityAt(i);
						int action = j;
						
						if (ability == -1) {
							setAction(FleetTactics.VIEW_SKIRMISH, i, j);
						} else {
							ShipData ship = ShipData.SHIPS[fleet.getSlotAt(slot).getId()];
							int count = ship.getWeapons().length > 0 ? 0 : -1;
							int availableAbilities = researchManager.getShipAvailableAbilities(ship);
							
							for (int k = 0; k < ship.getAbilities().length; k++) {
								if (!ship.getAbilities()[k].isPassive() &&
										(availableAbilities & (1 << k)) != 0)
									count++;
								
								if (k == ability) {
									action = j + count;
									setAction(FleetTactics.VIEW_SKIRMISH, i, action);
									break;
								}
							}
						}
						
						tactics.setAction(FleetTactics.VIEW_SKIRMISH, i, shipsId.get(action),
								shipsCount.get(action), abilities.get(action));
						break;
					}
			}
		}
		
		// Charge la tactique employée en bataille
		for (int i = 0; i < fleet.getBattleActionSlotsCount(); i++) {
			int slot = fleet.getBattleActionSlotAt(i);
			
			if (slot == -1) {
				setAction(FleetTactics.VIEW_BATTLE, i, 0);
				tactics.setNoAction(FleetTactics.VIEW_BATTLE, i);
			} else {
				for (int j = 0; j < shipsId.size(); j++)
					if (shipsId.get(j) == fleet.getSlotAt(slot).getId()) {
						int ability = fleet.getBattleActionAbilityAt(i);
						int action = j;
						
						if (ability == -1) {
							setAction(FleetTactics.VIEW_BATTLE, i, j);
						} else {
							ShipData ship = ShipData.SHIPS[fleet.getSlotAt(slot).getId()];
							int count = ship.getWeapons().length > 0 ? 0 : -1;
							int availableAbilities = researchManager.getShipAvailableAbilities(ship);
							
							for (int k = 0; k < ship.getAbilities().length; k++) {
								if (!ship.getAbilities()[k].isPassive() &&
										(availableAbilities & (1 << k)) != 0)
									count++;
								
								if (k == ability) {
									action = j + count;
									setAction(FleetTactics.VIEW_BATTLE, i, j + count);
									break;
								}
							}
						}
						
						tactics.setAction(FleetTactics.VIEW_BATTLE, i, shipsId.get(action),
								shipsCount.get(action), abilities.get(action));
						break;
					}
			}
		}
		
		tactics.setView(FleetTactics.VIEW_SKIRMISH);
		
		playerTacticsDialog.setFleet(scheme, tactics);
		loadBt.getElement().getStyle().setProperty("visibility",
			playerTacticsDialog.getCompatibleTacticsCount() > 0 ? "" : "hidden");
		
		setVisible(true);
		Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_TACTIC);
		actionsList.update();
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (updatingSelection)
			return;
		
		if (newValue == -1)
			return;
		
		if (sender == actionsList) {
			updatingSelection = true;
			
			if (actionsList.getSelectedItem().toString().contains("cooldown disabled")) {
				if (oldValue != -1)
					actionsList.setSelectedIndex(oldValue);
				updatingSelection = false;
				return;
			}
			
			int step = tactics.getSelectedActionIndex();
			setAction(tactics.getView(), step, newValue);
			
			scheduleActions(tactics.getView(), step + 1);
			tactics.setAction(tactics.getView(), step, shipsId.get(newValue),
					shipsCount.get(newValue), abilities.get(newValue));
			
			updatingSelection = false;
			
			if (tactics.getSelectedActionIndex() < getCurrentViewActionsCount() - 1) {
				int offset = 1;
				while (tactics.getSelectedActionIndex() + offset < getCurrentViewActionsCount()) {
					boolean available = false;
					int nextStep = tactics.getSelectedActionIndex() + offset;
					
					for (int i = 0; i < shipsId.size(); i++) {
						int shipId = shipsId.get(i);
						
						if (shipId != -1 && getShipUnavailabilityTime(
								tactics.getView(), shipId, nextStep) == 0) {
							available = true;
							break;
						}
					}
					
					if (available) {
						tactics.setSelectedActionIndex(tactics.getSelectedActionIndex() + offset);
						break;
					} else {
						offset++;
					}
				}
			}
		}
	}
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("id", String.valueOf(currentFleet.getId()));
			
			for (int i = 0; i < 5; i++)
				params.put("slot" + i + "_front", String.valueOf(scheme.isFront(i)));
			
			for (int i = 0; i < getActionsCount(FleetTactics.VIEW_SKIRMISH); i++) {
				if (getAction(FleetTactics.VIEW_SKIRMISH, i) == 0) {
					params.put("skirmish_action" + i + "_slot", "-1");
				} else {
					for (int j = 0; j < currentFleet.getSlotsCount(); j++) {
						if (currentFleet.getSlotAt(j).getId() ==
								shipsId.get(getAction(FleetTactics.VIEW_SKIRMISH, i))) {
							params.put("skirmish_action" + i + "_slot", String.valueOf(j));
						}
					}
				}
			}
			
			for (int i = 0; i < getActionsCount(FleetTactics.VIEW_SKIRMISH); i++)
				params.put("skirmish_action" + i + "_ability", String.valueOf(
					getAction(FleetTactics.VIEW_SKIRMISH, i) == 0 ? -1 :
						abilities.get(getAction(FleetTactics.VIEW_SKIRMISH, i))));
			
			for (int i = 0; i < getActionsCount(FleetTactics.VIEW_BATTLE); i++) {
				if (getAction(FleetTactics.VIEW_BATTLE, i) == 0) {
					params.put("battle_action" + i + "_slot", "-1");
				} else {
					for (int j = 0; j < currentFleet.getSlotsCount(); j++) {
						if (currentFleet.getSlotAt(j).getId() ==
								shipsId.get(getAction(FleetTactics.VIEW_BATTLE, i))) {
							params.put("battle_action" + i + "_slot", String.valueOf(j));
						}
					}
				}
			}
			
			for (int i = 0; i < getActionsCount(FleetTactics.VIEW_BATTLE); i++)
				params.put("battle_action" + i + "_ability", String.valueOf(
					getAction(FleetTactics.VIEW_BATTLE, i) == 0 ? -1 :
					abilities.get(getAction(FleetTactics.VIEW_BATTLE, i))));
			
			if (currentAction != null && currentAction.isPending())
				return;
			
			currentAction = new fr.fg.client.ajax.Action(
					"settactics", params, new ActionCallbackAdapter() {
				public void onSuccess(AnswerData data) {
					setVisible(false);
					UpdateManager.UPDATE_CALLBACK.onSuccess(data);
				}
			});
		} else if (sender == cancelBt) {
			setVisible(false);
		} else if (sender == exportBt) {
			JSOptionPane.showInputDialog("Copiez / collez le texte suivant " +
				"sur le chat ou dans un message pour partager votre tactique.",
				"Tactique", JSOptionPane.OK_OPTION,
				JSOptionPane.INFORMATION_MESSAGE, null,
				TacticsTools.hashCode(scheme, tactics));
		} else if (sender == importBt) {
			JSOptionPane.showInputDialog("Collez le code d'une tactique " +
				"pour la charger.", "Tactique", JSOptionPane.OK_OPTION,
				JSOptionPane.INFORMATION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if (option != null) {
							TacticsTools.load((String) option, scheme, tactics, true,
									Client.getInstance().getResearchManager());
							updateActions();
						}
					}
			}, "");
		} else if (sender == saveBt) {
			// Enregistre la tactique
			if (currentAction != null && currentAction.isPending())
				return;
			
			JSOptionPane.showInputDialog("Indiquez un nom pour la tactique (facultatif) :",
				"Tactique", JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if (option != null) {
							String hashCode = TacticsTools.hashCode(scheme, tactics);
	
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("name", (String) option); // Supprime les [[ ]]
							params.put("hash", hashCode.substring(2, hashCode.length() - 2)); // Supprime les [[ ]]
							for(int i = 0; i < 5; i++){
							params.put("slot_id_" + i, String.valueOf(scheme.getShipId(i)));
							params.put("tactic_slot" + i + "_count", String.valueOf(scheme.getCount(i)));
							}
							currentAction = new Action("tactics/save", params, new ActionCallbackAdapter() {
								@Override
								public void onSuccess(AnswerData data) {
									playerTacticsDialog.setPlayerTactics(data.getTactics());
									
									loadBt.getElement().getStyle().setProperty("visibility",
										playerTacticsDialog.getCompatibleTacticsCount() > 0 ? "" : "hidden");
								}
								
							});
							
					}
			}
		}, "");
		} else if (sender == loadBt) {
			// Charge une tactique enregistrée
			playerTacticsDialog.setVisible(true);
		}
	}
	
	public void dialogClosed(Widget sender) {
		if (sender == playerTacticsDialog) {
			updateActions();
		}
	}
	
	public void onActionSelected(int newIndex, int oldIndex) {
		updatingSelection = true;
		
		updateActionsDisplay(newIndex);
		actionsList.setSelectedIndex(getAction(
				tactics.getView(), newIndex));
		
		updatingSelection = false;
	}
	
	public void onViewChange(int newView, int oldView) {
		// Sans effet
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	// Recalcule les actions quand elles ont été chargées 
	private void updateActions() {
		// Charge la tactique employée en escarmouche
		for (int i = 0; i < getActionsCount(FleetTactics.VIEW_SKIRMISH); i++) {
			int shipId = tactics.getShipId(FleetTactics.VIEW_SKIRMISH, i);
			
			if (shipId == -1) {
				setAction(FleetTactics.VIEW_SKIRMISH, i, 0);
			} else {
				for (int j = 0; j < shipsId.size(); j++)
					if (shipsId.get(j) == shipId) {
						int ability = tactics.getAbility(FleetTactics.VIEW_SKIRMISH, i);
						int action = j;
						
						if (ability == -1) {
							setAction(FleetTactics.VIEW_SKIRMISH, i, j);
						} else {
							ShipData ship = ShipData.SHIPS[shipId];
							int count = ship.getWeapons().length > 0 ? 0 : -1;
							
							for (int k = 0; k < ship.getAbilities().length; k++) {
								if (!ship.getAbilities()[k].isPassive())
									count++;
								
								if (k == ability) {
									action = j + count;
									setAction(FleetTactics.VIEW_SKIRMISH, i, action);
									break;
								}
							}
						}
						break;
					}
			}
		}
		
		for (int i = 0; i < getActionsCount(FleetTactics.VIEW_BATTLE); i++) {
			int shipId = tactics.getShipId(FleetTactics.VIEW_BATTLE, i);
			
			if (shipId == -1) {
				setAction(FleetTactics.VIEW_BATTLE, i, 0);
			} else {
				for (int j = 0; j < shipsId.size(); j++)
					if (shipsId.get(j) == shipId) {
						int ability = tactics.getAbility(FleetTactics.VIEW_BATTLE, i);
						int action = j;
						
						if (ability == -1) {
							setAction(FleetTactics.VIEW_BATTLE, i, j);
						} else {
							ShipData ship = ShipData.SHIPS[shipId];
							int count = ship.getWeapons().length > 0 ? 0 : -1;
							
							for (int k = 0; k < ship.getAbilities().length; k++) {
								if (!ship.getAbilities()[k].isPassive())
									count++;
								
								if (k == ability) {
									action = j + count;
									setAction(FleetTactics.VIEW_BATTLE, i, action);
									break;
								}
							}
						}
						break;
					}
			}
		}
	}
	
	// Planification automatique des actions
	private void scheduleActions(int view, int from) {
		// Recherche le moment à partir duquel il faut replanifier les actions
		start:for (int i = from; i < getActionsCount(view); i++) {
			int action = getAction(view, i);
			
			if (action == 0) {
				for (int j = 0; j < shipsId.size(); j++) {
					int shipId = shipsId.get(j);
					
					if (shipId != -1 &&
							getShipUnavailabilityTime(view, shipId, i) == 0) {
						from = i;
						break start;
					}
				}
			} else {
				from = i;
				break start;
			}
		}
		
		// Regénère les actions
		for (int i = from; i < getActionsCount(view); i++) {
			int action = 0;
			
			for (int j = 0; j < shipsId.size(); j++) {
				int shipId = shipsId.get(j);
				
				if (shipId == -1 || getShipUnavailabilityTime(view, shipId, i) > 0)
					continue;
				
				// Recopie la dernière action du vaisseau
				action = j;
				
				for (int k = from - 1; k >= 0; k--) {
					if (getAction(view, k) != 0 &&
							shipsId.get(getAction(view, k)) == shipId) {
						action = getAction(view, k);
						break;
					}
				}
				
				// Vérifie que l'action n'est pas trop longue en fin de combat
				int ability = abilities.get(action);
				if (ability != -1 && i + ShipData.SHIPS[shipId].getAbilities(
						)[ability].getCooldown() >= getActionsCount(view) + 4) {
					action = 0;
					for (int k = 0; k < shipsId.size(); k++)
						if (shipsId.get(k) == shipId) {
							ability = abilities.get(k);
							
							if (i + (ability == -1 ? 4 :
								ShipData.SHIPS[shipId].getAbilities(
									)[ability].getCooldown()) <
									getActionsCount(view) + 4) {
								action = k;
								break;
							}
						}
				}
				break;
			}
			setAction(view, i, action);
			
			if (action == 0)
				tactics.setNoAction(view, i);
			else
				tactics.setAction(view, i, shipsId.get(action),
					shipsCount.get(action), abilities.get(action));
		}
		
		// Vérifie que la dernière action d'une séquence de tir concentrés et
		// un tir
		actions:for (int i = getActionsCount(view) - 1; i >= 0; i--) {
			int action = getAction(view, i);
			
			if (action != 0) {
				int shipId = shipsId.get(getAction(view, i));
				ShipData ship = ShipData.SHIPS[shipId];
				int ability = abilities.get(getAction(view, i));
				
				if (ability != -1 && ship.getAbilities()[ability].getType() ==
						AbilityData.TYPE_FOCUSED_FIRE) {
					// Recherche s'il y a une action autre qu'un tir concentré
					// après le tir
					for (int j = i + 1; j < getActionsCount(view); j++)
						if (getAction(view, j) != 0) {
							if (shipsId.get(getAction(view, j)) == shipId &&
									action != getAction(view, j))
								continue actions;
						}
					
					// Remplace le tir concentré par un tir normal
					for (int j = 0; j < shipsId.size(); j++)
						if (shipsId.get(j) == shipId &&
								abilities.get(j) == -1) {
							setAction(view, i, j);
							
							if (action == 0)
								tactics.setNoAction(view, i);
							else
								tactics.setAction(view, i, shipsId.get(j),
									shipsCount.get(j), abilities.get(j));
							continue actions;
						}
				}
			}
		}
	}
	
	private int getActionsCount(int view) {
		return actions[view].length;
	}
	
	private int getCurrentViewActionsCount() {
		return actions[tactics.getView()].length;
	}
	
	private int getAction(int view, int index) {
		return actions[view][index];
	}
	
	private void setAction(int view, int index, int action) {
		actions[view][index] = action;
	}
	
	private void updateActionsDisplay(int step) {
		ArrayList<String> actions = new ArrayList<String>();
		
		for (int i = 0; i < shipsId.size(); i++) {
			int shipId = shipsId.get(i);
			int ability = abilities.get(i);
			
			if (shipId == -1) {
				actions.add("<div unselectable=\"on\" class=\"title\" style=\"padding: 5px 0 5px 58px;\">Aucune action</div>");
			} else {
				int unavailability = getShipUnavailabilityTime(tactics.getView(), shipId, step);
				int cooldown = ability == -1 ? 4 :
					ShipData.SHIPS[shipId].getAbilities()[ability].getCooldown();
				
				if (unavailability > 0)
					actions.add(createAction(shipId, shipsCount.get(i), ability, -unavailability));
				else if (step + cooldown >= (tactics.getView() == FleetTactics.VIEW_SKIRMISH ? 5 : 15) + 4)
					actions.add(createAction(shipId, shipsCount.get(i), ability, "Action trop longue"));
				else
					actions.add(createAction(shipId, shipsCount.get(i), ability));
			}
		}
		
		actionsList.setItems(actions);
	}
	
	private int getShipUnavailabilityTime(int view, int shipId, int step) {
		// Vérifie que le vaisseau est disponible
		for (int k = 0; k < step; k++) {
			int action = getAction(view, k);
			
			if (action != 0) {
				int actionShipId = shipsId.get(action);
				
				if (actionShipId == shipId) {
					ShipData ship = ShipData.SHIPS[shipId];
					
					int ability = abilities.get(action);
					int cooldown = ability == -1 ? 4 :
						ship.getAbilities()[ability].getCooldown();
					
					if (k + cooldown >= step)
						return 1 + k + cooldown - step;
				}
			}
		}
		
		return 0;
	}
	
	private String createAction(int shipId, long count, int ability) {
		return createAction(shipId, count, ability, ability == -1 ?
			4 : ShipData.SHIPS[shipId].getAbilities()[ability].getCooldown());
	}
	
	private String createAction(int shipId, long count, int ability, int cooldown) {
		return createAction(shipId, count, ability, cooldown, null);
	}
	
	private String createAction(int shipId, long count, int ability, String reason) {
		return createAction(shipId, count, ability, 0, reason);
	}
	
	private String createAction(int shipId, long count, int ability, int cooldown, String reason) {
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		ShipData ship = ShipData.SHIPS[shipId];
		
		String content = "";
		
		if (reason != null) {
			content += "<div unselectable=\"on\" class=\"cooldown disabled\">" +
				"<img unselectable=\"on\" class=\"stat s-cooldown-disabled\" src=\"" +
				Config.getMediaUrl() + "images/misc/blank.gif\"/> " + reason + "</div>";
		} else {
			if (cooldown > 0)
				content += "<div unselectable=\"on\" class=\"cooldown\">" +
					"<img unselectable=\"on\" class=\"stat s-cooldown\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/> " + cooldown + "</div>";
			else if (cooldown < 0)
				content += "<div unselectable=\"on\" class=\"cooldown disabled\">" +
					"<img unselectable=\"on\" class=\"stat s-cooldown-disabled\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\"/> " + -cooldown + "</div>";
		}
		
		String abilityIcon = "<div unselectable=\"on\" class=\"ability\" style=\"" +
			"background-position: -" + (50 * (ability == -1 ? 0 :
			AbilityData.GRAPHICS[ship.getAbilities()[ability].getType()])) +
			"px -" + (cooldown > 0 ? 0 : 50) + "px;\">" +
			"</div>";
		
		content += "<table unselectable=\"on\" class=\"action" +
			(cooldown <= 0 ? " unavailable" : "") + "\" cellspacing=\"0\">" +
			"<tr unselectable=\"on\">" +
			"<td unselectable=\"on\" valign=\"center\">" + abilityIcon + "</td>" +
			"<td unselectable=\"on\" valign=\"center\" style=\"padding-left: 5px;\">" +
			"<div unselectable=\"on\" class=\"title\">" +
				dynamicMessages.getString("ship" + shipId) + "</div>" +
			"<div unselectable=\"on\" class=\"small\" style=\"font-weight: bold;\">";
		
		if (ability == -1) {
			content += "Tir";
		} else {
			content += ship.getAbilities()[ability].getName();
		}
		
		content += "</div><div unselectable=\"on\" class=\"x-small\">";
		
		if (ability == -1) {
			// Armement du vaisseau
			for (WeaponGroupData weaponGroup : ship.getWeapons()) {
				content += "<div unselectable=\"on\">" + weaponGroup.getCount() + "x " + dynamicMessages.getString( //$NON-NLS-1$ //$NON-NLS-2$
					"weapon" + weaponGroup.getIdWeapon()) + " " + //$NON-NLS-1$ //$NON-NLS-2$
					"<img unselectable=\"on\" class=\"stat s-damage\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
					"images/misc/blank.gif\"/> <b unselectable=\"on\">" + weaponGroup.getWeapon().getDamageMin() + //$NON-NLS-1$
					"-" + weaponGroup.getWeapon().getDamageMax() + "</b></div>"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			content += ship.getAbilities()[ability].getDesc(shipId);
		}
		
		content += "</div></td></tr></table>";
		
		return content;
	}
}
