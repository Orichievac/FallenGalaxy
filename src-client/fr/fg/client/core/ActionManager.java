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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.RootPanel;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.animation.DelayedActionUpdater;
import fr.fg.client.core.selection.Selection;
import fr.fg.client.core.selection.SelectionListener;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.AsteroidsData;
import fr.fg.client.data.BankData;
import fr.fg.client.data.BlackHoleData;
import fr.fg.client.data.DialogData;
import fr.fg.client.data.DoodadData;
import fr.fg.client.data.FakeStructureData;
import fr.fg.client.data.FakeWardData;
import fr.fg.client.data.FleetData;
import fr.fg.client.data.GateData;
import fr.fg.client.data.GravityWellData;
import fr.fg.client.data.GroupData;
import fr.fg.client.data.HyperspaceSignatureData;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.ItemInfoData;
import fr.fg.client.data.LotteryData;
import fr.fg.client.data.MarkerData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.ProductData;
import fr.fg.client.data.SkillData;
import fr.fg.client.data.Sounds;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StarSystemData;
import fr.fg.client.data.StructureData;
import fr.fg.client.data.StructureSkillData;
import fr.fg.client.data.TradeCenterData;
import fr.fg.client.data.WardData;
import fr.fg.client.map.UIItem;
import fr.fg.client.map.impl.AreaMap;
import fr.fg.client.map.item.BankItem;
import fr.fg.client.map.item.LotteryItem;
import fr.fg.client.map.item.StarSystemItem;
import fr.fg.client.map.item.TradeCenterItem;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.Callback;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.BaseWidget;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Dimension;
import fr.fg.client.openjwt.core.EventManager;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.SoundManager;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.ui.JSComponent;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;


public class ActionManager implements EventPreview, SelectionListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		NO_ACTION = 0,
		ACTION_MOVE_FLEET = 1,
		ACTION_OFFENSIVE_LINK = 2,
		ACTION_DEFENSIVE_LINK = 3,
		ACTION_EMP = 4,
		ACTION_SWAP = 5,
		ACTION_BOMBING = 6,
		ACTION_DEFUSE = 7,
		ACTION_SETUP_STRUCTURE = 8,
		ACTION_SETUP_WARD = 9,
		ACTION_STASIS = 10,
		ACTION_MOUNT_STRUCTURE = 11;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private AreaContainer areaContainer;
	
	private int currentAction;
	
	private BaseWidget cursor;
	
	private FleetData[] selectedFleetsData;
	
	private TalkListener talkListener;
	
	private int talkerSourceId, talkerTargetId;
	
	private Action lastAction;
	
	private Point lastMovedTile;
	
	private int structureItemIndex;
	
	private FakeStructureData fakeStructureData;
	
	private FakeWardData fakeWardData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ActionManager(AreaContainer areaContainer) {
		this.areaContainer = areaContainer;
		this.selectedFleetsData = new FleetData[0];
		this.fakeStructureData = null;
		this.fakeWardData = null;
		
		// Curseur indiquant où la flotte va se déplacer
		this.cursor = new BaseWidget();
		this.cursor.getElement().setPropertyString("unselectable", "on");
		this.cursor.getElement().setId("tileCursor");
		
		EventManager.addEventHook(this);
		
		this.talkListener = new TalkListener(Client.getInstance().getDialogManager());
		Client.getInstance().getDialogManager().addChoiceListener(talkListener);
		SelectionManager.getInstance().addSelectionListener(this);
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void selectionChanged(Selection newSelection, Selection oldSelection) {
		if (oldSelection.getType() != Selection.TYPE_NONE)
			setAction(NO_ACTION);
		
		if (newSelection.getType() == Selection.TYPE_FLEET) {
			// Sélectionne la ou les flottes
			this.selectedFleetsData = getSelectedFleets();
			setAction(ACTION_MOVE_FLEET);
		}
	}
	
	public int getAction() {
		return currentAction;
	}
	
	public void setAction(int action) {
		setAction(action, 0);
	}
	
	public void setAction(int action, Object arg) {
		this.currentAction = action;
		
		if (fakeStructureData != null) {
			areaContainer.getMap().removeItem(fakeStructureData, FakeStructureData.CLASS_NAME);
			fakeStructureData = null;
		}
		
		if (fakeWardData != null) {
			areaContainer.getMap().removeItem(fakeWardData, FakeWardData.CLASS_NAME);
			fakeWardData = null;
		}
		
		switch (action) {
		case NO_ACTION:
			selectedFleetsData = new FleetData[0];
			areaContainer.getMap().remove(cursor);
			break;
		case ACTION_SETUP_WARD:
			// Positionnement d'une balise
			fakeWardData = new FakeWardData((String) arg, -1000, -1000, false);
			areaContainer.getMap().addItem(
				fakeWardData, FakeWardData.CLASS_NAME);
			cursor.setStyleName("action");
			areaContainer.getMap().add(cursor, -10000, -10000);
			break;
		case ACTION_MOUNT_STRUCTURE:
			// Assemblage d'une structure
			structureItemIndex = (Integer) arg;
			ItemInfoData structureItem = Client.getInstance().getEmpireView().getFleetById(
				selectedFleetsData[0].getId()).getItemAt(structureItemIndex);
			fakeStructureData = new FakeStructureData(
				structureItem.getStructureType(), -1000, -1000, false, true);
			areaContainer.getMap().addItem(
				fakeStructureData, FakeStructureData.CLASS_NAME);
			areaContainer.getMap().add(cursor, -10000, -10000);
			break;
		case ACTION_SETUP_STRUCTURE:
			// Positionnement d'une structure
			fakeStructureData = new FakeStructureData((Integer) arg, -1000, -1000, false, true);
			areaContainer.getMap().addItem(
				fakeStructureData, FakeStructureData.CLASS_NAME);
			areaContainer.getMap().add(cursor, -10000, -10000);
			break;
		default:
			// Curseur indiquant la case où la flotte va se déplacer
			cursor.setStyleName("action");
			areaContainer.getMap().add(cursor, -10000, -10000);
			break;
		}
	}
	
	public void updateSelection() {
		if (selectedFleetsData.length == 0)
			return;
		
		selectedFleetsData = getSelectedFleets();
		
		if (lastMovedTile != null)
			onMouseMove(lastMovedTile);
	}
	
	public boolean onEventPreview(Event event) {
		if (!areaContainer.getElement().isOrHasChild(event.getTarget()))
			return true;
		
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			onClick(event);
			break;
		case Event.ONMOUSEMOVE:
			onMouseMove(getTileFromMouseEvent(event));
			break;
		case Event.ONMOUSEDOWN:
			if (event.getButton() != Event.BUTTON_LEFT) {
				// Déselectionne la flotte / le système s'il y en a un sélectionné
				SelectionManager.getInstance().setNoSelection();
				
				event.preventDefault();
				event.cancelBubble(true);
				
				OpenJWT.focus(RootPanel.getBodyElement());
			}
			break;
		case Event.ONMOUSEOUT:
			// Masque le curseur quand il sort de la minicarte
			if (currentAction != NO_ACTION) {
				Element element = areaContainer.getElement();
				
				if (!element.isOrHasChild(event.getToElement())) {
					areaContainer.getMap().setWidgetPosition(cursor, -10000, -10000);
					
					if (fakeStructureData != null) {
						fakeStructureData = new FakeStructureData(
							fakeStructureData.getType(), -10000, -10000, false, true);
						areaContainer.getMap().updateItem(fakeStructureData,
							FakeStructureData.CLASS_NAME);
					}
					
					if (fakeWardData != null) {
						fakeWardData = new FakeWardData(
							fakeWardData.getType(), -10000, -10000, false);
						areaContainer.getMap().updateItem(fakeWardData,
							FakeWardData.CLASS_NAME);
					}
				}
				
				OpenJWT.focus(RootPanel.getBodyElement());
			}
			break;
		}
		
		return true;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void setMarker(Point position) {
		IndexedAreaData areaData = areaContainer.getArea();
		
		// Recherche s'il y a un message à supprimer
		for (int i = 0; i < areaData.getMarkersCount(); i++) {
			final MarkerData marker = areaData.getMarkerAt(i);
			
			if (!marker.isContractMarker() &&
					marker.getX() == position.getX() &&
					marker.getY() == position.getY()) {
				JSOptionPane.showMessageDialog(
					"Voulez-vous supprimer ce message ?", "Message",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.YES_OPTION) {
								if (lastAction != null && lastAction.isPending())
									return;
								
								HashMap<String, String> params =
									new HashMap<String, String>();
								params.put("marker", String.valueOf(marker.getId()));
								
								lastAction = new Action("markers/delete", params);
							}
						}
				});
				return;
			}
		}
		
		// Création d'un nouveau marqueur
		MarkerDialog dialog = new MarkerDialog(
			areaContainer.getArea().getId(), position);
		dialog.setVisible(true);
	}
	
	private void moveSelectedFleet(Point position) {
		IndexedAreaData areaData = areaContainer.getArea();
		AreaMap map = areaContainer.getMap();
		
		// Mouvement sur la meme case affiche la tactique
		for (FleetData selectedFleetData : selectedFleetsData) {
			if (position.getX() == selectedFleetData.getX() &&
				position.getY() == selectedFleetData.getY() &&
				!selectedFleetData.isDelude()) {
				Client.getInstance().getTacticsDialog().show(Client.getInstance(
					).getEmpireView().getFleetById(selectedFleetData.getId()));
				return;
			}
		}
		
		// Teste si autre flotte se trouve sur cette case
		if (selectedFleetsData.length == 1) {
			FleetData selectedFleetData = selectedFleetsData[0];
			
			for (int i = 0; i < areaData.getFleetsCount(); i++) {
				FleetData fleetData = areaData.getFleetAt(i);
				
				if (fleetData.getX() == position.getX() &&
					fleetData.getY() == position.getY()) {
					
					Point nearestTile = getNearestFreeTile(fleetData, selectedFleetData);
					
					if (nearestTile != null) {
						if (!fleetData.getNpcAction().equals("none")) {
							// Attaque
							boolean mustMove = !(
								Math.abs(selectedFleetData.getX() - fleetData.getX()) <= 1 &&
								Math.abs(selectedFleetData.getY() - fleetData.getY()) <= 1);
							
							talkerSourceId = selectedFleetData.getId();
							talkerTargetId = fleetData.getId();
							
							if (mustMove) {
								UIItem item = map.getItem(selectedFleetData, FleetData.CLASS_NAME);
								
								TimerManager.register(new DelayedActionUpdater(item, nearestTile, new Callback() {
									public void run() {
										if (lastAction != null && lastAction.isPending())
											return;
										
										HashMap<String, String> params = new HashMap<String, String>();
										params.put("source", String.valueOf(talkerSourceId));
										params.put("target", String.valueOf(talkerTargetId));
										params.put("option", "-1");
										
										lastAction = new Action("talk", params, new ActionCallbackAdapter() {
											public void onSuccess(AnswerData data) {
												// Affiche le dialogue
												DialogData dialog = data.getDialog();
												
												if (!dialog.isEndOfDialog()) {
													Client.getInstance().getDialogManager().show(
														dialog.getTalker(), dialog.getContent(),
														dialog.getOptions(), dialog.getValidOptions(),
														dialog.getAvatar());
													SelectionManager.getInstance().setNoSelection();
													talkListener.setActive(true);
												}
											};
										});
									}
								}));
								
								doMoveSelectedFleet(nearestTile);
							} else {
								if (lastAction != null && lastAction.isPending())
									return;
								
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("source", String.valueOf(selectedFleetData.getId()));
								params.put("target", String.valueOf(fleetData.getId()));
								params.put("option", "-1");
								
								lastAction = new Action("talk", params, new ActionCallbackAdapter() {
									public void onSuccess(AnswerData data) {
										// Affiche le dialogue
										DialogData dialog = data.getDialog();
										
										if (!dialog.isEndOfDialog()) {
											Client.getInstance().getDialogManager().show(
												dialog.getTalker(), dialog.getContent(),
												dialog.getOptions(), dialog.getValidOptions(),
												dialog.getAvatar());
											SelectionManager.getInstance().setNoSelection();
											talkListener.setActive(true);
										}
									};
								});
							}
						} else if (fleetData.isEnemyFleet() ||
							fleetData.isPirateFleet() ||
							(selectedFleetData.isPirate() &&
							!(fleetData.isPlayerFleet() ||
							fleetData.isAllyFleet() ||
							fleetData.isAlliedFleet()))) {
							
							// Attaque
							boolean mustMove = !(
								Math.abs(selectedFleetData.getX() - fleetData.getX()) <= 1 &&
								Math.abs(selectedFleetData.getY() - fleetData.getY()) <= 1);
							
							
							if (mustMove) {
								final int fleetId1 = selectedFleetData.getId();
								final int fleetId2 = fleetData.getId();
								
								UIItem item = map.getItem(selectedFleetData, FleetData.CLASS_NAME);
								
								TimerManager.register(new DelayedActionUpdater(item, nearestTile, new Callback() {
									public void run() {
										BattleModeDialog dialog = new BattleModeDialog(
												fleetId1, fleetId2, false);
										dialog.setVisible(true);
									}
								}));
								
								doMoveSelectedFleet(nearestTile);
							} else {
								BattleModeDialog dialog = new BattleModeDialog(
										selectedFleetData.getId(), fleetData.getId(), false);
								dialog.setVisible(true);
							}
						} else if (fleetData.isPlayerFleet()) {
							// Transfert entre flottes du joueur
							boolean mustMove = !(
									Math.abs(selectedFleetData.getX() - fleetData.getX()) <= 1 &&
									Math.abs(selectedFleetData.getY() - fleetData.getY()) <= 1);
							
							if (mustMove) {
								final int fleetId1 = selectedFleetData.getId();
								final int fleetId2 = fleetData.getId();
								
								UIItem item = map.getItem(selectedFleetData, FleetData.CLASS_NAME);
								
								TimerManager.register(new DelayedActionUpdater(item, nearestTile, new Callback() {
									public void run() {
										EmpireView empireView = Client.getInstance().getEmpireView();
										
										Client.getInstance().getSwapDialog().show(new GroupData[]{
											new GroupData(empireView.getFleetById(fleetId1),
												Client.getInstance().getProgressBar().getPlayerLevel()),
											new GroupData(empireView.getFleetById(fleetId2),
												Client.getInstance().getProgressBar().getPlayerLevel()),
										}, SwapDialog.MODE_FLEET_TO_FLEET);
									}
								}));
								
								doMoveSelectedFleet(nearestTile);
							} else {
								EmpireView empireView = Client.getInstance().getEmpireView();
								
								Client.getInstance().getSwapDialog().show(new GroupData[]{
									new GroupData(empireView.getFleetById(selectedFleetData.getId()),
										Client.getInstance().getProgressBar().getPlayerLevel()),
									new GroupData(empireView.getFleetById(fleetData.getId()),
										Client.getInstance().getProgressBar().getPlayerLevel()),
								}, SwapDialog.MODE_FLEET_TO_FLEET);
							}
						} else {
							JSOptionPane.showMessageDialog(
								"La case est déjà occupée par une flotte.",
								"Déplacement impossible", JSOptionPane.OK_OPTION,
								JSOptionPane.WARNING_MESSAGE, null);
						}
					}
					
					return; 
				}
			}
		}
		
		// Vérifie que le clic s'est fait sur une case que la flotte peut
		// atteindre
		if (position.getX() >= 0 &&
				position.getY() >= 0 &&
				position.getX() < areaData.getWidth() &&
				position.getY() < areaData.getHeight()) {
			doMoveSelectedFleet(position);
		}
	}
	
	private void doMoveSelectedFleet(Point position) {
		if (lastAction != null && lastAction.isPending())
			return;
		
		// Déplace la flotte
		HashMap<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < selectedFleetsData.length; i++)
			params.put("fleet" + i, String.valueOf(selectedFleetsData[i].getId()));
		params.put("x",  String.valueOf(position.getX()));
		params.put("y",  String.valueOf(position.getY()));
		lastAction = new Action("movefleets", params, UpdateManager.UPDATE_CALLBACK);
		
		SelectionManager.getInstance().setNoSelection();
		Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_MAP);
	}
	
	private void onMouseMove(Point tile) {
		lastMovedTile = tile;
		
		switch (currentAction) {
		case NO_ACTION:
			break;
		case ACTION_SETUP_STRUCTURE:
		case ACTION_MOUNT_STRUCTURE:
			// Calcule les coordonnées du curseur
			AreaMap map = areaContainer.getMap();
			IndexedAreaData areaData = areaContainer.getArea();
			
			Dimension size = StructureData.getSize(fakeStructureData.getType());
			boolean valid = true;
			boolean activated;
			
			int cornerX = tile.getX() - size.getWidth() / 2;
			int cornerY = tile.getY() - size.getHeight() / 2;
			
			switch (fakeStructureData.getType()) {
			case StructureData.TYPE_GENERATOR:
				valid = false;
				activated = true;
				
				// Valide uniquement sur un puit gravitationnel
				for (int i = 0; i < areaData.getGravityWellsCount(); i++) {
					GravityWellData gravityWellData = areaData.getGravityWellAt(i);
					
					if (gravityWellData.getX() == tile.getX() &&
							gravityWellData.getY() == tile.getY()) {
						valid = true;
						break;
					}
				}
				
				if (valid) {
					// Détection des collisions avec les structures
					for (int i = 0; i < areaData.getStructuresCount(); i++) {
						StructureData structureData = areaData.getStructureAt(i);
						Dimension structureSize = structureData.getSize();
						int structureX = structureData.getX() - structureSize.getWidth() / 2;
						int structureY = structureData.getY() - structureSize.getHeight() / 2;
						
						if (cornerX + size.getWidth() > structureX &&
								cornerY + size.getHeight() > structureY &&
								cornerX < structureX + structureSize.getWidth() &&
								cornerY < structureY + structureSize.getHeight()) {
							valid = false;
							break;
						}
					}
				}
				break;
			default:
				if (cornerX < 0 || cornerY < 0 ||
						cornerX > areaData.getWidth() - size.getWidth() ||
						cornerY > areaData.getHeight() - size.getHeight())
					valid = false;
				
				if (valid) {
					// Détection des collisions avec les systèmes
					int radiusSq = StarSystemItem.SYSTEM_RADIUS * StarSystemItem.SYSTEM_RADIUS;
					
					systems:for (int i = 0; i < areaData.getSystemsCount(); i++) {
						StarSystemData systemData = areaData.getSystemAt(i);
						
						// Premier test pour éliminer le système s'il est trop éloigné
						int dx = systemData.getX() - tile.getX();
						int dy = systemData.getY() - tile.getY();
						int radius = StarSystemItem.SYSTEM_RADIUS + size.getWidth() + size.getHeight();
						
						if (dx * dx + dy * dy > radius * radius)
							continue;
						
				    	for (int j = cornerX ; j < cornerX + size.getWidth(); j++)
				        	for (int k = cornerY; k < cornerY + size.getHeight(); k++) {
								dx = systemData.getX() - j;
								dy = systemData.getY() - k;
								
								if (dx * dx + dy * dy <= radiusSq) {
									valid = false;
									break systems;
								}
				        	}
					}
				}
				
				if (valid) {
					// Détection des collisions avec les trous noirs
					int radiusSq = BlackHoleData.GRAVITY_RADIUS * BlackHoleData.GRAVITY_RADIUS;
					
					blackHoles:for (int i = 0; i < areaData.getBlackHolesCount(); i++) {
						BlackHoleData blackHoleData = areaData.getBlackHoleAt(i);
						
						// Premier test pour éliminer le trou noir s'il est trop éloigné
						int dx = blackHoleData.getX() - tile.getX();
						int dy = blackHoleData.getY() - tile.getY();
						int radius = BlackHoleData.GRAVITY_RADIUS + size.getWidth() + size.getHeight();
						
						if (dx * dx + dy * dy > radius * radius)
							continue;
						
				    	for (int j = cornerX ; j < cornerX + size.getWidth(); j++)
				        	for (int k = cornerY; k < cornerY + size.getHeight(); k++) {
								dx = blackHoleData.getX() - j;
								dy = blackHoleData.getY() - k;
								
								if (dx * dx + dy * dy <= radiusSq) {
									valid = false;
									break blackHoles;
								}
				        	}
					}
				}
				
				if (valid) {
					// Détection des collisions avec les portes hyperspatiales
					int radiusSq = GateData.HYPERGATE_JUMP_RADIUS * GateData.HYPERGATE_JUMP_RADIUS;
					
					gates:for (int i = 0; i < areaData.getGatesCount(); i++) {
						GateData gateData = areaData.getGateAt(i);
						
						// Premier test pour éliminer la porte si elle est trop éloignée
						int dx = gateData.getX() - tile.getX();
						int dy = gateData.getY() - tile.getY();
						int radius = GateData.HYPERGATE_JUMP_RADIUS + size.getWidth() + size.getHeight();
						
						if (dx * dx + dy * dy > radius * radius)
							continue;
						
				    	for (int j = cornerX ; j < cornerX + size.getWidth(); j++)
				        	for (int k = cornerY; k < cornerY + size.getHeight(); k++) {
								dx = gateData.getX() - j;
								dy = gateData.getY() - k;
								
								if (dx * dx + dy * dy <= radiusSq) {
									valid = false;
									break gates;
								}
				        	}
					}
				}
				
				if (valid) {
					// Détection des collisions avec les structures
					for (int i = 0; i < areaData.getStructuresCount(); i++) {
						StructureData structureData = areaData.getStructureAt(i);
						Dimension structureSize = structureData.getSize();
						int structureX = structureData.getX() - structureSize.getWidth() / 2;
						int structureY = structureData.getY() - structureSize.getHeight() / 2;
						
						if (cornerX + size.getWidth() > structureX &&
								cornerY + size.getHeight() > structureY &&
								cornerX < structureX + structureSize.getWidth() &&
								cornerY < structureY + structureSize.getHeight()) {
							valid = false;
							break;
						}
					}
				}
				
				if (valid) {
					// Détection de collisions avec les astéroïdes
					for (int i = 0; i < areaData.getAsteroidsCount(); i++) {
						AsteroidsData asteroidsData = areaData.getAsteroidsAt(i);
						int asteroidsX = asteroidsData.getX() - 1;
						int asteroidsY = asteroidsData.getY() - 1;
						
						if (cornerX + size.getWidth() > asteroidsX &&
								cornerY + size.getHeight() > asteroidsY &&
								cornerX < asteroidsX + 3 &&
								cornerY < asteroidsY + 3) {
							valid = false;
							break;
						}
					}
				}
				
				if (valid) {
					// Détection de collisions avec les puits gravitationnels
					for (int i = 0; i < areaData.getGravityWellsCount(); i++) {
						GravityWellData gravityWellData = areaData.getGravityWellAt(i);
						int gravityWellX = gravityWellData.getX() - 2;
						int gravityWellY = gravityWellData.getY() - 2;
						
						if (cornerX + size.getWidth() > gravityWellX &&
								cornerY + size.getHeight() > gravityWellY &&
								cornerX < gravityWellX + 5 &&
								cornerY < gravityWellY + 5) {
							valid = false;
							break;
						}
					}
				}
				
				if (valid) {
					// Détection de collisions avec les banques
					for (int i = 0; i < areaData.getBanksCount(); i++) {
						BankData bankData = areaData.getBankAt(i);
						int bankX = bankData.getX() - 4;
						int bankY = bankData.getY() - 4;
						
						if (cornerX + size.getWidth() > bankX &&
								cornerY + size.getHeight() > bankY &&
								cornerX < bankX + 9 &&
								cornerY < bankY + 9) {
							valid = false;
							break;
						}
					}
				}
				
				if (valid) {
					// Détection de collisions avec les loteries
					for (int i = 0; i < areaData.getLotterysCount(); i++) {
						LotteryData lotteryData = areaData.getLotteryAt(i);
						int lotteryX = lotteryData.getX() - 2;
						int lotteryY = lotteryData.getY() - 2;
						
						if (cornerX + size.getWidth() > lotteryX &&
								cornerY + size.getHeight() > lotteryY &&
								cornerX < lotteryX + 2 &&
								cornerY < lotteryY + 2) {
							valid = false;
							break;
						}
					}
				}
				
				if (valid) {
					// Détection de collisions avec les centres de commerce
					for (int i = 0; i < areaData.getTradeCentersCount(); i++) {
						TradeCenterData tradeCenterData = areaData.getTradeCenterAt(i);
						int tradeCenterX = tradeCenterData.getX() - 4;
						int tradeCenterY = tradeCenterData.getY() - 4;
						
						if (cornerX + size.getWidth() > tradeCenterX &&
								cornerY + size.getHeight() > tradeCenterY &&
								cornerX < tradeCenterX + 9 &&
								cornerY < tradeCenterY + 9) {
							valid = false;
							break;
						}
					}
				}
				
				if (valid) {
					// Détection de collisions avec les doodads
					for (int i = 0; i < areaData.getDoodadsCount(); i++) {
						DoodadData doodadData = areaData.getDoodadAt(i);
						
						if (cornerX + size.getWidth() > doodadData.getX() &&
								cornerY + size.getHeight() > doodadData.getY() &&
								cornerX < doodadData.getX() + 1 &&
								cornerY < doodadData.getY() + 1) {
							valid = false;
							break;
						}
					}
				}
				
				if (valid) {
					// Détection de collisions avec les flottes
					for (int i = 0; i < areaData.getFleetsCount(); i++) {
						FleetData fleetData = areaData.getFleetAt(i);
						
						if (fleetData.getId() != selectedFleetsData[0].getId() &&
								tile.getX() == fleetData.getX() &&
								tile.getY() == fleetData.getY()) {
							valid = false;
							break;
						}
					}
				}
				
				if (valid) {
					// Détection de collisions avec les flottes en sortie
					// d'hyperespace
					for (int i = 0; i < areaData.getHyperspaceSignaturesCount(); i++) {
						HyperspaceSignatureData hyperspaceSignatureData = areaData.getHyperspaceSignatureAt(i);
						
						if (tile.getX() == hyperspaceSignatureData.getX() &&
								tile.getY() == hyperspaceSignatureData.getY()) {
							valid = false;
							break;
						}
					}
				}
				
				activated = false;
				int requiredEnergy = StructureData.getEnergyConsumption(
						fakeStructureData.getType());
				
				// Recherche une source d'énergie à portée
				for (int i = 0; i < areaData.getStructuresCount(); i++) {
					StructureData structureData = areaData.getStructureAt(i);
					
					if ((structureData.isPlayerStructure() ||
							(structureData.isAllyStructure() &&
							 structureData.isShared())) &&
							 structureData.getMaxEnergy() -
							 structureData.getUsedEnergy() > requiredEnergy) {
						int dx = tile.getX() - structureData.getX();
						int dy = tile.getY() - structureData.getY();
						
						//Bonus de range de diffusion grace aux produits
						int radiusBonus = 0;
						ProductsManager productsManager = Client.getInstance().getProductsManager();
						HashMap<Integer, Integer> products = productsManager.getProducts();
						if(products!=null){
						if(products.get(ProductData.PRODUCT_SELENIUM)!=null) {
							
							radiusBonus = 
								(int) ProductData.getProductEffect(ProductData.PRODUCT_SELENIUM, products.get(ProductData.PRODUCT_SELENIUM));
							}
						}
						int radius = StructureData.getEnergyDiffusionRange(structureData.getType())+radiusBonus;
						
						if (dx * dx + dy * dy < radius * radius) {
							activated = true;
							break;
						}
					}
				}
				break;
			}
			
			fakeStructureData = new FakeStructureData(
				fakeStructureData.getType(), tile.getX(),
				tile.getY(), valid, activated);
			map.updateItem(fakeStructureData, FakeStructureData.CLASS_NAME);
			break;
		case ACTION_SETUP_WARD:
			// Positionnement d'une balise / charge
			map = areaContainer.getMap();
			areaData = areaContainer.getArea();
			valid = true;
			
			if (tile.getX() < 0 || tile.getY() < 0 ||
					tile.getX() >= areaData.getWidth() ||
					tile.getY() > areaData.getHeight())
				valid = false;
			
			if (valid && (fakeWardData.getType().startsWith(WardData.TYPE_MINE) ||
					fakeWardData.getType().startsWith(WardData.TYPE_STUN))) {
				// Détection des collisions avec les systèmes
				for (int i = 0; i < areaData.getSystemsCount(); i++) {
					StarSystemData systemData = areaData.getSystemAt(i);
					
					int dx = systemData.getX() - tile.getX();
					int dy = systemData.getY() - tile.getY();
					int radius = StarSystemItem.SYSTEM_RADIUS + 1 +
						(fakeWardData.getType().startsWith(WardData.TYPE_MINE) ?
							WardData.MINE_TRIGGER_RADIUS : WardData.STUN_TRIGGER_RADIUS);
					
					if (dx * dx + dy * dy <= radius * radius) {
						valid = false;
						break;
					}
				}
			}
			
			if (valid) {
				// Détection de collisions avec les balises / charges
				for (int i = 0; i < areaData.getWardsCount(); i++) {
					WardData wardData = areaData.getWardAt(i);
					
					if (tile.getX() == wardData.getX() &&
							tile.getY() == wardData.getY()) {
						valid = false;
						break;
					}
				}
			}
			
			if (valid) {
				// Détection de collisions avec les relais HE
				if (fakeWardData.getType().startsWith(WardData.TYPE_MINE) ||
						fakeWardData.getType().startsWith(WardData.TYPE_STUN)) {
					for (int i = 0; i < areaData.getGatesCount(); i++) {
						GateData gateData = areaData.getGateAt(i);
						
						int dx = gateData.getX() - tile.getX();
						int dy = gateData.getY() - tile.getY();
						int radius = GateData.HYPERGATE_JUMP_RADIUS + 1;
						
						if (dx * dx + dy * dy <= radius * radius) {
							valid = false;
							break;
						}
					}
				} else {
					for (int i = 0; i < areaData.getGatesCount(); i++) {
						GateData gateData = areaData.getGateAt(i);
						int gateX = gateData.getX() - 1;
						int gateY = gateData.getY() - 1;
						
						if (tile.getX() + 1 > gateX &&
								tile.getY() + 1 > gateY &&
								tile.getX() < gateX + 3 &&
								tile.getY() < gateY + 3) {
							valid = false;
							break;
						}
					}
				}
			}
			
			if (valid) {
				// Détection de collisions avec les flottes
				for (int i = 0; i < areaData.getFleetsCount(); i++) {
					FleetData fleetData = areaData.getFleetAt(i);
					
					if (fleetData.getId() != selectedFleetsData[0].getId() &&
							tile.getX() == fleetData.getX() &&
							tile.getY() == fleetData.getY()) {
						valid = false;
						break;
					}
				}
			}
			
			if (valid) {
				// Détection de collisions avec les flottes en sortie
				// d'hyperespace
				for (int i = 0; i < areaData.getHyperspaceSignaturesCount(); i++) {
					HyperspaceSignatureData hyperspaceSignatureData = areaData.getHyperspaceSignatureAt(i);
					
					if (tile.getX() == hyperspaceSignatureData.getX() &&
							tile.getY() == hyperspaceSignatureData.getY()) {
						valid = false;
						break;
					}
				}
			}
			
			fakeWardData = new FakeWardData(
				fakeWardData.getType(), tile.getX(), tile.getY(), valid);
			map.updateItem(fakeWardData, FakeWardData.CLASS_NAME);
			break;
		case ACTION_STASIS:
			// Déplace le curseur
			map = areaContainer.getMap();
			int x = (int) Math.floor(tile.getX() * map.getTileSize() * map.getZoom());
			int y = (int) Math.floor(tile.getY() * map.getTileSize() * map.getZoom());
			map.setWidgetPosition(cursor, x, y);
			
			// Stase
			StructureData selectedStructureData = SelectionTools.getSelectedStructure();
			areaData = areaContainer.getArea();
			boolean found = false;
			String toolTipText = null;
			
			for (int i = 0; i < areaData.getFleetsCount(); i++) {
				FleetData fleetData = areaData.getFleetAt(i);
				if (fleetData.getX() == tile.getX() &&
						fleetData.getY() == tile.getY()) {
					switch (this.currentAction) {
					case ACTION_STASIS:
						// Vérifie que la flotte est à portée de stase
						found = true;
						int dx = fleetData.getX() - selectedStructureData.getX();
						int dy = fleetData.getY() - selectedStructureData.getY();
						int radius = StructureSkillData.STASIS_RANGE;
						
						if (!fleetData.isNeutralFleet()) {
							boolean allied = fleetData.isPlayerFleet() ||
								fleetData.isAllyFleet() ||
								fleetData.isAlliedFleet();
							
							if (allied && fleetData.getMovement() >= fleetData.getMovementMax()) {
								cursor.setStyleName("unreachable");
								toolTipText = "Mouvement au maximum";
							} else if (dx * dx + dy * dy <= radius * radius) {
								cursor.setStyleName("action");
								toolTipText = allied ? "Recharger la flotte" : "Immobiliser la flotte";
							} else {
								cursor.setStyleName("unreachable");
								toolTipText = "Cible hors de portée";
							}
						} else {
							cursor.setStyleName("unreachable");
							toolTipText = "Cible invalide";
						}
						break;
					}
					
					break;
				}
			}
			
			if (!found) {
				cursor.setStyleName("unreachable");
				toolTipText = "Cible invalide";
			}
			
			ToolTipManager.getInstance().register(cursor.getElement(), toolTipText);
			break;
		default:
			// Déplace le curseur
			map = areaContainer.getMap();
			x = (int) Math.floor(tile.getX() * map.getTileSize() * map.getZoom());
			y = (int) Math.floor(tile.getY() * map.getTileSize() * map.getZoom());
			map.setWidgetPosition(cursor, x, y);
			
			// Indique si la flotte doit faire une action de déplacement avant
			// d'atteindre sa cible
			FleetData mustMoveNextToFleet = null;
			toolTipText = null;
			found = false;
			
			areaData = areaContainer.getArea();
			
			if (currentAction == ACTION_MOVE_FLEET) {
				for (FleetData selectedFleetData : selectedFleetsData) {
					// Vérifie que toutes les flottes ont du mouvement
					if (selectedFleetData.getMovement() == 0) {
						cursor.setStyleName("unreachable");
						toolTipText = "Plus de mouvement";
						found = true;
						break;
					}
					
					// Vérifie qu'aucune flotte ne saute parmi les flottes
					// sélectionnées
					if (selectedFleetData.getStartJumpRemainingTime() != 0) {
						cursor.setStyleName("unreachable");
						toolTipText = "Passage en hyperespace en cours";
						found = true;
						break;
					}
					
					// Vérifie qu'aucune flotte ne fini de sauter parmi les
					// flottes sélectionnées
					if (selectedFleetData.getEndJumpRemainingTime() != 0) {
						cursor.setStyleName("unreachable");
						toolTipText = "Sortie d'hyperespace en cours";
						found = true;
						break;
					}
					
					// Leurre immobile
					if (this.currentAction == ACTION_MOVE_FLEET) {
						int deludeLevel = selectedFleetData.getSkillAt(3).getType() == 15 ?
								selectedFleetData.getSkillAt(3).getLevel() : -1;
						
						if (selectedFleetData.isDelude() && deludeLevel <= 0) {
							cursor.setStyleName("unreachable");
							toolTipText = "<div class=\"title\">Leurre immobile</div>";
							found = true;
						}
					}	
				}
			}
			
			if (tile.getX() < 0 || tile.getY() < 0 ||
					tile.getX() >= areaData.getWidth() ||
					tile.getY() >= areaData.getHeight()) {
				cursor.setStyleName("unreachable");
				toolTipText = "Zone non navigable";
				found = true;
			}
			
			// Détermine si le curseur est sur une flotte
			if (this.currentAction != ACTION_DEFUSE && !found &&
					selectedFleetsData.length == 1) {
				FleetData selectedFleetData = selectedFleetsData[0];
				
				for (int i = 0; i < areaData.getFleetsCount(); i++) {
					FleetData fleetData = areaData.getFleetAt(i);
					if (fleetData.getX() == tile.getX() &&
							fleetData.getY() == tile.getY() &&
							fleetData.getId() != selectedFleetData.getId()) {
						found = true;
						
						// Leurre
						if (selectedFleetData.isDelude()) {
							cursor.setStyleName("unreachable");
							toolTipText = "<div class=\"title\">Action impossible avec un leurre</div>";
							break;
						}
						
						switch (this.currentAction) {
						case ACTION_MOVE_FLEET:
							// Déplacement
							if (!fleetData.getNpcAction().equals("none")) {
								cursor.setStyleName("action");
								mustMoveNextToFleet = fleetData;
								toolTipText = "<div class=\"title\">Communication</div>";
							} else if ((selectedFleetData.isPirate() &&
									!(fleetData.isPlayerFleet() ||
									fleetData.isAllyFleet() ||
									fleetData.isAlliedFleet())) ||
									fleetData.isEnemyFleet() ||
									fleetData.isPirateFleet()) {
								cursor.setStyleName("enemy");
								mustMoveNextToFleet = fleetData;
								
								toolTipText = getBattleToolTip(selectedFleetData, fleetData);
							} else if (fleetData.isPlayerFleet()) {
								cursor.setStyleName("action");
								mustMoveNextToFleet = fleetData;
								toolTipText = "<div class=\"title\">Transférer des vaisseaux / ressources</div>";
							} else {
								cursor.setStyleName("unreachable");
								toolTipText = "<div class=\"title\">Déplacement invalide</div>";
							}
							break;
						case ACTION_OFFENSIVE_LINK:
						case ACTION_DEFENSIVE_LINK:
							// Vérifie que la flotte est à portée de lien
							int dx = fleetData.getX() - selectedFleetData.getX();
							int dy = fleetData.getY() - selectedFleetData.getY();
							int radius = currentAction == ACTION_OFFENSIVE_LINK ?
									SkillData.DEFENSIVE_LINK_RANGE :
									SkillData.OFFENSIVE_LINK_RANGE;
							
							if (dx * dx + dy * dy <= radius * radius) {
								cursor.setStyleName("action");
								toolTipText = "Créer un lien";
							} else {
								cursor.setStyleName("unreachable");
								toolTipText = "Cible hors de portée";
							}
							break;
						case ACTION_EMP:
						case ACTION_SWAP:
							// Vérifie que la flotte est à portée de lien
							int skillLevel = selectedFleetData.getSkillAt(3).getLevel();
							dx = fleetData.getX() - selectedFleetData.getX();
							dy = fleetData.getY() - selectedFleetData.getY();
							radius = currentAction == ACTION_EMP ?
								SkillData.EMP_RANGE[skillLevel] :
								SkillData.SWAP_RANGE[skillLevel];
							
							if (currentAction == ACTION_EMP && !(
									fleetData.isEnemyFleet() ||
									fleetData.isPirateFleet() ||
									(selectedFleetData.isPirate() &&
									!(fleetData.isPlayerFleet() ||
									fleetData.isAllyFleet() ||
									fleetData.isAlliedFleet())))) {
								cursor.setStyleName("unreachable");
								toolTipText = "Cible invalide";
							} else {
								if (dx * dx + dy * dy <= radius * radius) {
									cursor.setStyleName("action");
									toolTipText = currentAction == ACTION_EMP ?
										"Lancer une IEM" :
										"Échanger de position";
								} else {
									cursor.setStyleName("unreachable");
									toolTipText = "Cible hors de portée";
								}
							}
							break;
						case ACTION_BOMBING:
							// Vérifie que la flotte est à portée de bombardement
							for (int j = 0; j < selectedFleetData.getSkillsCount(); j++)
								if (selectedFleetData.getSkillAt(j).getType() == 3) {
									dx = fleetData.getX() - selectedFleetData.getX();
									dy = fleetData.getY() - selectedFleetData.getY();
									radius = SkillData.BOMBING_RANGE[selectedFleetData.getSkillAt(j).getLevel()];
									
									if (!((selectedFleetData.isPirate() &&
											!(fleetData.isPlayerFleet() ||
											  fleetData.isAllyFleet() ||
											  fleetData.isAlliedFleet())) ||
											fleetData.isEnemyFleet() ||
											fleetData.isPirateFleet())) {
										cursor.setStyleName("unreachable");
										toolTipText = "Cible invalide";
									} else {
										if (dx * dx + dy * dy <= radius * radius) {
											cursor.setStyleName("enemy");
											toolTipText = getBattleToolTip(selectedFleetData, fleetData);
										} else {
											cursor.setStyleName("unreachable");
											toolTipText = "Cible hors de portée";
										}
									}
								}
							break;
						}
						
						break;
					}
				}
			}
			
			if (this.currentAction == ACTION_DEFUSE && !found &&
					selectedFleetsData.length == 1) {
				FleetData selectedFleetData = selectedFleetsData[0];
				
				// Détermine si le curseur est sur une charge
				for (int i = 0; i < areaData.getWardsCount(); i++) {
					WardData wardData = areaData.getWardAt(i);
					
					if ((wardData.getType().startsWith(WardData.TYPE_STUN) ||
							wardData.getType().startsWith(WardData.TYPE_MINE)) &&
							wardData.getX() == tile.getX() &&
							wardData.getY() == tile.getY() &&
							(wardData.isEnemyWard() ||
							(wardData.isNeutralWard() &&
							 selectedFleetData.isPirate()))) {
						int dx = wardData.getX() - selectedFleetData.getX();
						int dy = wardData.getY() - selectedFleetData.getY();
						int radius = WardData.CHARGE_DEFUSE_RADIUS;
						
						if (dx * dx + dy * dy <= radius * radius) {
							found = true;
							cursor.setStyleName("action");
							toolTipText = "Désamorcer";
							break;
						} else {
							found = true;
							cursor.setStyleName("unreachable");
							toolTipText = "Hors de portée";
						}
					}
				}
			}
			
			if (this.currentAction == ACTION_MOVE_FLEET) {
				// Détermine si le curseur est dans un système neutre
				if (!found) {
					for (int i = 0; i < areaData.getSystemsCount(); i++) {
						StarSystemData systemData = areaData.getSystemAt(i);
						int dx = systemData.getX() - tile.getX();
						int dy = systemData.getY() - tile.getY();
						int radius = StarSystemItem.SYSTEM_RADIUS;
						
						if ((systemData.isNeutralStarSystem() ||
								systemData.getTreaty().equals("unknown")) &&
								dx * dx + dy * dy <= radius * radius) {
							found = true;
							cursor.setStyleName("unreachable");
							
							if (systemData.isNeutralStarSystem())
								toolTipText = "Système neutre";
							else
								toolTipText = "Système inconnu";
							break;
						}
					}
				}
				
				// Détermine si le curseur est sur un centre de commerce
				if (!found && selectedFleetsData.length == 1 &&
						!selectedFleetsData[0].isDelude()) {
					for (int i = 0; i < areaData.getTradeCentersCount(); i++) {
						int dx = areaData.getTradeCenterAt(i).getX() - tile.getX();
						int dy = areaData.getTradeCenterAt(i).getY() - tile.getY();
						double radius = TradeCenterItem.TRADE_CENTER_RADIUS;
						
						if (dx * dx + dy * dy <= radius * radius) {
							found = true;
							cursor.setStyleName("action");
							toolTipText = "Echanger des ressources";
							break;
						}
					}
				}
				
				// Détermine si le curseur est sur une station spatiale
				if (!found && selectedFleetsData.length == 1 &&
						!selectedFleetsData[0].isDelude()) {
					for (int i = 0; i < areaData.getSpaceStationsCount(); i++) {
						int dx = areaData.getSpaceStationAt(i).getX() - tile.getX();
						int dy = areaData.getSpaceStationAt(i).getY() - tile.getY();
						double radius = SpaceStationData.SPACE_STATION_RADIUS;
						
						if (dx * dx + dy * dy <= radius * radius) {
							found = true;
							cursor.setStyleName("action");
							toolTipText = "Station spatiale";
							break;
						}
					}
				}
				
				// Détermine si le curseur est sur une banque
				if (!found && selectedFleetsData.length == 1 &&
						!selectedFleetsData[0].isDelude()) {
					for (int i = 0; i < areaData.getBanksCount(); i++) {
						int dx = areaData.getBankAt(i).getX() - tile.getX();
						int dy = areaData.getBankAt(i).getY() - tile.getY();
						double radius = BankItem.BANK_RADIUS;
						
						if (dx * dx + dy * dy <= radius * radius) {
							found = true;
							cursor.setStyleName("action");
							toolTipText = "Déposer / retirer des ressources";
							break;
						}
					}
				}
				
				// Détermine si le curseur est sur une loterie
				if (!found && selectedFleetsData.length == 1 &&
						!selectedFleetsData[0].isDelude()) {
					for (int i = 0; i < areaData.getLotterysCount(); i++) {
						int dx = areaData.getLotteryAt(i).getX() - tile.getX();
						int dy = areaData.getLotteryAt(i).getY() - tile.getY();
						double radius = LotteryItem.LOTTERY_RADIUS;
						
						if (dx * dx + dy * dy <= radius) {
							found = true;
							cursor.setStyleName("action");
							toolTipText = "Jouez à la loterie !";
							break;
						}
					}
				}
				
				// Détermine si le curseur est sur une hyperporte
				if (!found) {
					for (int i = 0; i < areaData.getGatesCount(); i++) {
						GateData gateData = areaData.getGateAt(i);
						
						if (Math.abs(gateData.getX() - tile.getX()) <= 1 &&
								Math.abs(gateData.getY() - tile.getY()) <= 1) {
							found = true;
							cursor.setStyleName("unreachable");
							toolTipText = "Relai hyperspatial";
							break;
						}
					}
				}
				
				if (!found) {
					// Vérifie que la case peut être atteinte par la flotte
					int dx = tile.getX() - selectedFleetsData[0].getX();
					int dy = tile.getY() - selectedFleetsData[0].getY();
					int distance = (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
					if (distance <= selectedFleetsData[0].getMovement() &&
							tile.getX() >= 0 &&
							tile.getY() >= 0 &&
							tile.getX() < areaData.getWidth() &&
							tile.getY() < areaData.getHeight()) {
						cursor.setStyleName("move");
						toolTipText = "<div class=\"title\">" +
							"Cliquez pour déplacer la flotte</div><div>" +
							"Mouvement restant : <b><span class=\"emphasize\">" +
							(selectedFleetsData[0].getMovement() - distance) + "</span> / " +
							selectedFleetsData[0].getMovementMax() + "</b></div>";
					} else {
						int time = (((distance - selectedFleetsData[0].getMovement() - 1) /
								selectedFleetsData[0].getMovementMax()) + 1) * 10;
						
						cursor.setStyleName("scheduledMove");
						toolTipText = "<div class=\"title\">" +
							"Déplacement automatique</div><div>" +
							"Arrivée dans <b><span class=\"emphasize\">" +
							time + "</span> sec</b></div>";
					}
				}
			} else {
				if (!found) {
					cursor.setStyleName("unreachable");
					toolTipText = "Cible invalide";
				}
			}
			
			// Affiche la distance séparant la flotte du curseur
			if (!cursor.getStyleName().equals("unreachable")) {
				switch (currentAction) {
				case ACTION_MOVE_FLEET:
					// Détermine si le curseur est sur une charge
					boolean mayTriggerCharge = false;
					if (selectedFleetsData.length > 0) {
						FleetData selectedFleetData = selectedFleetsData[0];
						
						for (int i = 0; i < areaData.getWardsCount(); i++) {
							WardData wardData = areaData.getWardAt(i);
							int dx = wardData.getX() - tile.getX();
							int dy = wardData.getY() - tile.getY();
							
							if ((wardData.getType().startsWith(WardData.TYPE_STUN) ||
									wardData.getType().startsWith(WardData.TYPE_MINE)) &&
									dx * dx + dy * dy <= WardData.MINE_TRIGGER_RADIUS *
									WardData.MINE_TRIGGER_RADIUS &&
									(wardData.isEnemyWard() ||
									(wardData.isNeutralWard() &&
									 selectedFleetData.isPirate()))) {
								mayTriggerCharge = true;
								toolTipText += "<div class=\"emphasize\">Charge à proximité !</div>";
								break;
							}
						}
					}
					
					if (mustMoveNextToFleet != null) {
						Point nearestTile = getNearestFreeTile(mustMoveNextToFleet, selectedFleetsData[0]);
						
						if (cursor.getStyleName().equals("scheduledMove") || nearestTile == null) {
							cursor.setStyleName("unreachable");
							cursor.getElement().setInnerHTML("");
							toolTipText = "Cible hors de portée";
						} else {
							int dx = nearestTile.getX() - this.selectedFleetsData[0].getX();
							int dy = nearestTile.getY() - this.selectedFleetsData[0].getY();
							int movement = (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
							
							dx = tile.getX() - nearestTile.getX();
							dy = tile.getY() - nearestTile.getY();
							movement += Math.ceil(Math.sqrt(dx * dx + dy * dy));
							
							if (movement > selectedFleetsData[0].getMovement()) {
								cursor.setStyleName("unreachable");
								cursor.getElement().setInnerHTML("");
								toolTipText = "Cible hors de portée";
							} else {
								cursor.getElement().setInnerHTML(String.valueOf(movement));
							}
						}
					} else {
						int dx = tile.getX() - selectedFleetsData[0].getX();
						int dy = tile.getY() - selectedFleetsData[0].getY();
						int length = (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
						
						if (length <= selectedFleetsData[0].getMovement())
							cursor.getElement().setInnerHTML(String.valueOf(length));
						else
							cursor.getElement().setInnerHTML((10 * (((length -
									selectedFleetsData[0].getMovement() - 1) /
									selectedFleetsData[0].getMovementMax()) + 1)) + "s");
					}
					
					if (mayTriggerCharge && !cursor.getStyleName().equals("unreachable"))
						cursor.setStyleName("enemy");
					break;
				case ACTION_OFFENSIVE_LINK:
				case ACTION_DEFENSIVE_LINK:
					cursor.getElement().setInnerHTML("Lier");
					break;
				default:
					cursor.getElement().setInnerHTML("");
					break;
				}
			} else {
				cursor.getElement().setInnerHTML("");
			}
			
			ToolTipManager.getInstance().register(cursor.getElement(), toolTipText);
			break;
		}
	}
	//TODO
	
	private void onClick(Event event) {
		// Calcule la position du clic en cases
		Point tile = getTileFromMouseEvent(event);
		
		switch (currentAction) {
		case NO_ACTION:
			if (event.getCtrlKey() || event.getShiftKey()) {
				setMarker(tile);
				return;
			}
			
			IndexedAreaData areaData = areaContainer.getArea();
			
			// Recherche s'il y a une flotte à l'endroit du clic
			if (trySelectFleet(areaData, tile))
				return;
			
			// Recherche s'il y a une structure à l'endroit du clic
			if (trySelectStructure(areaData, tile))
				return;
			
			// Recherche s'il y a un système à l'endroit du clic
			if (trySelectSystem(areaData, tile))
				return;
			
			// Recherche s'il y a une station à l'endroit du clic
			if (trySelectSpaceStation(areaData, tile))
				return;
			break;
		case ACTION_MOVE_FLEET:
			// Si une flotte est sélectionnée et que le curseur est sur une
			// case valide, on déplace la flotte
			onMouseMove(getTileFromMouseEvent(event));
			
			areaData = areaContainer.getArea();
			
			// Recherche s'il y a une flotte à l'endroit du clic
			if ((event.getCtrlKey() || event.getShiftKey())) {
				if (selectedFleetsData.length >= 9)
					return;
				
				for (int i = 0; i < areaData.getFleetsCount(); i++) {
					FleetData fleetData = areaData.getFleetAt(i);
					if (fleetData.getX() == tile.getX() &&
							fleetData.getY() == tile.getY() &&
							fleetData.isPlayerFleet()) {
						// Ajoute la flotte à la selection
						SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
						if (SelectionManager.getInstance().isFleetSelected(fleetData.getId()))
							SelectionManager.getInstance().removeSelectedFleet(fleetData.getId());
						else
							SelectionManager.getInstance().addSelectedFleet(fleetData.getId());
						onMouseMove(getTileFromMouseEvent(event));
						
						return;
					}
				}
			} else {
				if (selectedFleetsData.length == 1 &&
						tile.getX() == selectedFleetsData[0].getX() &&
						tile.getY() == selectedFleetsData[0].getY()) {
					// Recherche s'il y a une structure à l'endroit du clic
					if (trySelectStructure(areaData, tile))
						return;
					
					// Recherche s'il y a un système à l'endroit du clic
					if (trySelectSystem(areaData, tile))
						return;
					
					// Recherche s'il y a une station à l'endroit du clic
					if (trySelectSpaceStation(areaData, tile))
						return;
				}
			}
			
			if (!cursor.getStyleName().equals("unreachable")) {
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
				moveSelectedFleet(tile);
				//TODO
			} else {
				SoundManager.getInstance().playSound(Sounds.MISCLICK);
			}
			break;
		case ACTION_SETUP_STRUCTURE:
			// Construction d'une structure
			if (fakeStructureData != null && fakeStructureData.isValid()) {
				FleetData selectedFleetData = selectedFleetsData[0];
				UIItem item = areaContainer.getMap().getItem(selectedFleetData, FleetData.CLASS_NAME);
				final int structureType = fakeStructureData.getType();
				final int fleetId = selectedFleetData.getId();
				
				if (tile.getX() == selectedFleetData.getX() &&
						tile.getY() == selectedFleetData.getY()) {
					if (lastAction != null && lastAction.isPending())
						return;
					
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("fleet", String.valueOf(fleetId));
					params.put("type", String.valueOf(structureType));
					
					lastAction = new Action("structure/build", params,
						UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
				} else {
					TimerManager.register(new DelayedActionUpdater(item, new Point(tile), new Callback() {
						public void run() {
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("fleet", String.valueOf(fleetId));
							params.put("type", String.valueOf(structureType));
							
							new Action("structure/build", params,
								UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
						}
					}));
					
					doMoveSelectedFleet(new Point(tile));
				}
				
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
			} else {
				SoundManager.getInstance().playSound(Sounds.MISCLICK);
			}
			break;
		case ACTION_MOUNT_STRUCTURE:
			// Assemblage d'une structure
			if (fakeStructureData != null && fakeStructureData.isValid()) {
				FleetData selectedFleetData = selectedFleetsData[0];
				UIItem item = areaContainer.getMap().getItem(selectedFleetData, FleetData.CLASS_NAME);
				final int fleetId = selectedFleetData.getId();
				final int itemIndex = structureItemIndex;
				
				if (tile.getX() == selectedFleetData.getX() &&
						tile.getY() == selectedFleetData.getY()) {
					if (lastAction != null && lastAction.isPending())
						return;
					
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("fleet", String.valueOf(fleetId));
					params.put("item", String.valueOf(itemIndex));
					
					lastAction = new Action("structure/mount", params,
						UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
				} else {
					TimerManager.register(new DelayedActionUpdater(item, new Point(tile), new Callback() {
						public void run() {
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("fleet", String.valueOf(fleetId));
							params.put("item", String.valueOf(itemIndex));
							
							new Action("structure/mount", params,
								UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
						}
					}));
					
					doMoveSelectedFleet(new Point(tile));
				}
				
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
			} else {
				SoundManager.getInstance().playSound(Sounds.MISCLICK);
			}
			break;
		case ACTION_SETUP_WARD:
			// Construction d'une balise
			if (fakeWardData != null && fakeWardData.isValid()) {
				FleetData selectedFleetData = selectedFleetsData[0];
				UIItem item = areaContainer.getMap().getItem(selectedFleetData, FleetData.CLASS_NAME);
				final String wardType = fakeWardData.getType();
				final PlayerFleetData fleet = Client.getInstance(
					).getEmpireView().getFleetById(selectedFleetData.getId());
				
				if (tile.getX() == selectedFleetData.getX() &&
						tile.getY() == selectedFleetData.getY()) {
					BuildWardDialog dialog = new BuildWardDialog(wardType, fleet);
					dialog.setVisible(true);
				} else {
					TimerManager.register(new DelayedActionUpdater(item, new Point(tile), new Callback() {
						public void run() {
							BuildWardDialog dialog = new BuildWardDialog(wardType, fleet);
							dialog.setVisible(true);
						}
					}));
					
					doMoveSelectedFleet(new Point(tile));
				}
				
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
			} else {
				SoundManager.getInstance().playSound(Sounds.MISCLICK);
			}
			break;
		case ACTION_DEFENSIVE_LINK:
		case ACTION_OFFENSIVE_LINK:
			// Création d'un lien défensif / offensif
			if (lastAction != null && lastAction.isPending())
				return;
			
			if (!cursor.getStyleName().equals("unreachable")) {
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
				
				areaData = areaContainer.getArea();
				for (int i = 0; i < areaData.getFleetsCount(); i++) {
					FleetData fleetData = areaData.getFleetAt(i);
					if (fleetData.getX() == tile.getX() && fleetData.getY() == tile.getY()) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("fleet", String.valueOf(selectedFleetsData[0].getId()));
						params.put("target", String.valueOf(fleetData.getId()));
						
						String uri = currentAction == ACTION_DEFENSIVE_LINK ?
								"setdefensivelink" : "setoffensivelink";
						
						lastAction = new Action("skill/" + uri, params,
								new ActionCallbackAdapter() {
							public void onSuccess(AnswerData data) {
								SelectionManager.getInstance().setNoSelection();
								UpdateManager.UPDATE_CALLBACK.onSuccess(data);
							}
						});
						return;
					}
				}
			} else {
				SoundManager.getInstance().playSound(Sounds.MISCLICK);
			}
			break;
		case ACTION_SWAP:
			// Distorsion spatiale
			if (lastAction != null && lastAction.isPending())
				return;
			
			if (!cursor.getStyleName().equals("unreachable")) {
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
				
				areaData = areaContainer.getArea();
				for (int i = 0; i < areaData.getFleetsCount(); i++) {
					FleetData fleetData = areaData.getFleetAt(i);
					if (fleetData.getX() == tile.getX() && fleetData.getY() == tile.getY()) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("fleet", String.valueOf(selectedFleetsData[0].getId()));
						params.put("target", String.valueOf(fleetData.getId()));
						
						lastAction = new Action("skill/swapfleetposition", params,
								new ActionCallbackAdapter() {
							public void onSuccess(AnswerData data) {
								SelectionManager.getInstance().setNoSelection();
								UpdateManager.UPDATE_CALLBACK.onSuccess(data);
							}
						});
						return;
					}
				}
			} else {
				SoundManager.getInstance().playSound(Sounds.MISCLICK);
			}
			break;
		case ACTION_STASIS:
			// Stase
			if (lastAction != null && lastAction.isPending())
				return;
			
			if (!cursor.getStyleName().equals("unreachable")) {
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
				
				areaData = areaContainer.getArea();
				for (int i = 0; i < areaData.getFleetsCount(); i++) {
					FleetData fleetData = areaData.getFleetAt(i);
					if (fleetData.getX() == tile.getX() && fleetData.getY() == tile.getY()) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("structure", String.valueOf(
							SelectionManager.getInstance().getIdSelectedStructure()));
						params.put("target", String.valueOf(fleetData.getId()));
						
						lastAction = new Action("structure/caststasis", params,
								new ActionCallbackAdapter() {
							public void onSuccess(AnswerData data) {
								SelectionManager.getInstance().setNoSelection();
								UpdateManager.UPDATE_CALLBACK.onSuccess(data);
							}
						});
						return;
					}
				}
			} else {
				SoundManager.getInstance().playSound(Sounds.MISCLICK);
			}
			break;
		case ACTION_EMP:
			// IEM
			if (lastAction != null && lastAction.isPending())
				return;
			
			if (!cursor.getStyleName().equals("unreachable")) {
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
				
				areaData = areaContainer.getArea();
				for (int i = 0; i < areaData.getFleetsCount(); i++) {
					FleetData fleetData = areaData.getFleetAt(i);
					if (fleetData.getX() == tile.getX() && fleetData.getY() == tile.getY()) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("fleet", String.valueOf(selectedFleetsData[0].getId()));
						params.put("target", String.valueOf(fleetData.getId()));
						
						lastAction = new Action("skill/emp", params,
								new ActionCallbackAdapter() {
							public void onSuccess(AnswerData data) {
								SelectionManager.getInstance().setNoSelection();
								UpdateManager.UPDATE_CALLBACK.onSuccess(data);
							}
						});
						return;
					}
				}
			} else {
				SoundManager.getInstance().playSound(Sounds.MISCLICK);
			}
			break;
		case ACTION_BOMBING:
			if (lastAction != null && lastAction.isPending())
				return;
			
			if (!cursor.getStyleName().equals("unreachable")) {
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
				
				areaData = areaContainer.getArea();
				for (int i = 0; i < areaData.getFleetsCount(); i++) {
					FleetData fleetData = areaData.getFleetAt(i);
					if (fleetData.getX() == tile.getX() && fleetData.getY() == tile.getY()) {
						BattleModeDialog dialog = new BattleModeDialog(
								selectedFleetsData[0].getId(), fleetData.getId(), true);
						dialog.setVisible(true);
					}
				}
			} else {
				SoundManager.getInstance().playSound(Sounds.MISCLICK);
			}
			break;
		case ACTION_DEFUSE:
			if (lastAction != null && lastAction.isPending())
				return;
			
			if (!cursor.getStyleName().equals("unreachable")) {
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
				
				areaData = areaContainer.getArea();
				for (int i = 0; i < areaData.getWardsCount(); i++) {
					WardData wardData = areaData.getWardAt(i);
					if (wardData.getX() == tile.getX() && wardData.getY() == tile.getY()) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("fleet", String.valueOf(selectedFleetsData[0].getId()));
						params.put("target", String.valueOf(wardData.getId()));
						
						lastAction = new Action("skill/defuse", params,
								new ActionCallbackAdapter() {
							public void onSuccess(AnswerData data) {
								SelectionManager.getInstance().setNoSelection();
								UpdateManager.UPDATE_CALLBACK.onSuccess(data);
							}
						});
						return;
					}
				}
			} else {
				SoundManager.getInstance().playSound(Sounds.MISCLICK);
			}
			break;
		}
	}
	
	// Renvoie la case libre la plus proche de from dans les cases adjacentes
	// à target
	private Point getNearestFreeTile(FleetData target, FleetData from) {
		if (Math.abs(target.getX() - from.getX()) <= 1 &&
			Math.abs(target.getY() - from.getY()) <= 1)
			return new Point(from.getX(), from.getY());
		
		IndexedAreaData areaData = areaContainer.getArea();
		Point nearestTile = null;
		int minDist = -1;
		
		for (int x = -1; x <= 1; x++)
			coords:for (int y = -1; y <= 1; y++)
				if (x != 0 || y != 0) {
					Point tile = new Point(target.getX() + x, target.getY() + y);
					
					// Vérifie que la case n'est pas occupée par une flotte
					for (int i = 0; i < areaData.getFleetsCount(); i++) {
						FleetData fleetData = areaData.getFleetAt(i);
						if (fleetData.getX() == tile.getX() &&
							fleetData.getY() == tile.getY())
							continue coords;
					}
					
					// Vérifie que la case n'est pas dans un système neutre
					for (int i = 0; i < areaData.getSystemsCount(); i++) {
						StarSystemData systemData = areaData.getSystemAt(i);
						int dx = systemData.getX() - tile.getX();
						int dy = systemData.getY() - tile.getY();
						int radius = StarSystemItem.SYSTEM_RADIUS;
						
						if (systemData.isNeutralStarSystem() &&
								dx * dx + dy * dy <= radius * radius)
							continue coords;
					}
					
					// Vérifie que la case n'est pas dans une hyperporte
					for (int i = 0; i < areaData.getGatesCount(); i++) {
						GateData gateData = areaData.getGateAt(i);
						
						if (Math.abs(gateData.getX() - tile.getX()) <= 1 &&
							Math.abs(gateData.getY() - tile.getY()) <= 1)
							continue coords;
					}
					
					// Teste si la case est plus proche de la case from
					int dx = from.getX() - tile.getX();
					int dy = from.getY() - tile.getY();
					int dist = (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
					
					dx = tile.getX() - target.getX();
					dy = tile.getY() - target.getY();
					dist += Math.ceil(Math.sqrt(dx * dx + dy * dy));
					
					if (nearestTile == null || dist < minDist) {
						nearestTile = new Point(tile);
						minDist = dist;
					}
				}
		
		return nearestTile;
	}
	
	// Calcule la position d'un mouse event en cases
	private Point getTileFromMouseEvent(Event event) {
		AreaMap map = areaContainer.getMap();
		return new Point(
			(int) Math.floor((OpenJWT.eventGetPointerX(event) / map.getZoom() +
						map.getView().getX()) / map.getTileSize()),
			(int) Math.floor((OpenJWT.eventGetPointerY(event) / map.getZoom() +
						map.getView().getY()) / map.getTileSize()));
	}
	
	private String getBattleToolTip(FleetData attacker, FleetData defender) {
		double xpFactor = FleetData.getXpFactor(
			attacker.getPowerLevel(),
			defender.getPowerLevel());
		int xp = (int) Math.floor(xpFactor * (FleetData.getPowerLevel(
			defender.getPowerLevel() + 1) - 1) * .02);
			
		return "<div class=\"title\">Attaquer la flotte</div>" +
			"<div>Puissance de votre flotte : <span class=\"emphasize\">" +
			attacker.getPowerLevel() +
			"</span> <img src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" class=\"stat s-power\" " +
			"unselectable=\"on\"/></div>" +
			"<div>Puissance flotte ennemie : <span class=\"emphasize\">" +
			defender.getPowerLevel() + "</span> <img src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\" " +
			"class=\"stat s-power\" unselectable=\"on\"/></div>" +
			"<div>Gain XP : <span class=\"emphasize\">0 - " + xp +
			"</span> (" + (int) (100 * xpFactor) + "%)</div>";
	}
	
	private FleetData[] getSelectedFleets() {
		IndexedAreaData areaData = areaContainer.getArea();
		long[] idSelectedFleets = SelectionManager.getInstance().getIdSelectedFleets();
		FleetData[] selectedFleetsData = new FleetData[idSelectedFleets.length];
		
		for (int i = 0; i < idSelectedFleets.length; i++)
			selectedFleetsData[i] = areaData.getFleetById((int) idSelectedFleets[i]);
		
		return selectedFleetsData;
	}
	
	private boolean trySelectFleet(IndexedAreaData areaData, Point tile) {
		// Recherche s'il y a une flotte à l'endroit du clic
		for (int i = 0; i < areaData.getFleetsCount(); i++) {
			FleetData fleetData = areaData.getFleetAt(i);
			
			if (fleetData.getX() == tile.getX() && fleetData.getY() == tile.getY() &&
					fleetData.isPlayerFleet()) {
				// Sélectionne la flotte
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
				SelectionManager.getInstance().selectFleet(fleetData.getId());
				onMouseMove(tile);
				Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_FLEET);
				return true;
			}
		}
		return false;
	}
	
	private boolean trySelectStructure(IndexedAreaData areaData, Point tile) {
		// Recherche s'il y a une structure à l'endroit du clic
		for (int i = 0; i < areaData.getStructuresCount(); i++) {
			StructureData structureData = areaData.getStructureAt(i);
			Dimension size = structureData.getSize();
			
			if (tile.getX() >= structureData.getX() - size.getWidth() / 2 &&
					tile.getX() < structureData.getX() - size.getWidth() / 2 + size.getWidth() &&
					tile.getY() >= structureData.getY() - size.getHeight() / 2 &&
					tile.getY() < structureData.getY() - size.getHeight() / 2 + size.getHeight() &&
					structureData.isPlayerStructure()) {
				// Sélectionne la structure
				SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
				SelectionManager.getInstance().selectStructure(structureData.getId());
				onMouseMove(tile);
				return true;
			}
		}
		
		return false;
	}
	
	private boolean trySelectSystem(IndexedAreaData areaData, Point tile) {
		// Recherche s'il y a un système à l'endroit du clic
		for (int i = 0; i < areaData.getSystemsCount(); i++) {
			StarSystemData systemData = areaData.getSystemAt(i);
			int dx = systemData.getX() - tile.getX();
			int dy = systemData.getY() - tile.getY();
			int radius = StarSystemItem.SYSTEM_RADIUS;
			
			if (dx * dx + dy * dy <= radius * radius) {
				if (systemData.isPlayerStarSystem()) {
					SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
					SelectionManager.getInstance().selectSystem(systemData.getId());
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean trySelectSpaceStation(IndexedAreaData areaData, Point tile) {
		// Recherche s'il y a une station à l'endroit du clic
		for (int i = 0; i < areaData.getSpaceStationsCount(); i++) {
			SpaceStationData spaceStationData = areaData.getSpaceStationAt(i);
			int dx = spaceStationData.getX() - tile.getX();
			int dy = spaceStationData.getY() - tile.getY();
			double radius = SpaceStationData.SPACE_STATION_RADIUS;
			
			if (dx * dx + dy * dy <= radius * radius) {
				if (spaceStationData.isAllySpaceStation()) {
					SoundManager.getInstance().playSound(JSComponent.SOUND_CLICK);
					SelectionManager.getInstance().selectSpaceStation(spaceStationData.getId());
					return true;
				}
			}
		}
		
		return false;
	}
	
	private class TalkListener implements DialogManager.ChoiceListener {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private DialogManager dialogManager;
		
		private boolean active;
		
		private Action currentAction;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public TalkListener(DialogManager dialogManager) {
			this.dialogManager = dialogManager;
			this.active = false;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void setActive(boolean active) {
			this.active = active;
		}
		
		public void onChoice(DialogManager source, int choice) {
			if (!active)
				return;
			
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("source", String.valueOf(talkerSourceId));
			params.put("target", String.valueOf(talkerTargetId));
			params.put("option", String.valueOf(choice));
			
			currentAction = new Action("talk", params, new ActionCallbackAdapter() {
				public void onSuccess(AnswerData data) {
					// Affiche le dialogue
					DialogData dialog = data.getDialog();
					
					if (!dialog.isEndOfDialog()) {
						dialogManager.show(
							dialog.getTalker(), dialog.getContent(),
							dialog.getOptions(), dialog.getValidOptions(),
							dialog.getAvatar());
					} else {
						dialogManager.hide();
						setActive(false);
					}
				};
			});
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
