/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.animation.BackgroundUpdater;
import fr.fg.client.animation.CountdownUpdater;
import fr.fg.client.animation.ToolTipTextUpdater;
import fr.fg.client.animation.ToolTipTimeUpdater;
import fr.fg.client.core.ally.AllyDialog;
import fr.fg.client.core.LotteryDialog;
import fr.fg.client.core.selection.Selection;
import fr.fg.client.core.selection.SelectionListener;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.AdvancementData;
import fr.fg.client.data.AllyData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.BankData;
import fr.fg.client.data.BuildData;
import fr.fg.client.data.BuildingData;
import fr.fg.client.data.DoodadData;
import fr.fg.client.data.FleetData;
import fr.fg.client.data.GateData;
import fr.fg.client.data.GroupData;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.ItemInfoData;
import fr.fg.client.data.LotteryData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.PlayerGeneratorData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.ProductData;
import fr.fg.client.data.ShipData;
import fr.fg.client.data.SkillData;
import fr.fg.client.data.SlotInfoData;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StarSystemData;
import fr.fg.client.data.StructureData;
import fr.fg.client.data.StructureModuleData;
import fr.fg.client.data.StructureSkillData;
import fr.fg.client.data.TradeCenterData;
import fr.fg.client.data.WardData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Dimension;
import fr.fg.client.openjwt.core.EventManager;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.core.ActionManager;
import fr.fg.client.core.BuildBuildingDialog;
import fr.fg.client.core.BuildShipDialog;
import fr.fg.client.core.Client;
import fr.fg.client.core.ControlPanel;
import fr.fg.client.core.EmpireControlPanel;
import fr.fg.client.core.GalaxyMap;
import fr.fg.client.core.ProbeReportDialog;
import fr.fg.client.core.SelectionTools;
import fr.fg.client.core.SwapDialog;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.core.Utilities;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.openjwt.core.SoundManager;


public class EmpireControlPanel extends ControlPanel implements EventPreview,
		SelectionListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int
		NO_VIEW							 = 0,
		VIEW_FLEET_CONTROLS			 	 = 1,
		VIEW_FLEET_CONTENT			 	 = 2,
		VIEW_FLEET_SKILLS_SELECTION 	 = 3,
		VIEW_FLEET_STRUCTURE_ITEMS		 = 4,
		VIEW_FLEET_TAG_SELECTION		 = 5,
		VIEW_SYSTEM_CONTROLS			 = 6,
		VIEW_SYSTEM_BUILDINGS_CONTENT	 = 7,
		VIEW_SYSTEM_SHIPS_CONTENT	 	 = 15,
		VIEW_FLEET_ENGINEER_WARDS		 = 16,
		VIEW_SPACE_STATION_CONTROLS		 = 17,
		VIEW_FLEET_PYROTECHNIST_WARDS	 = 18,
		VIEW_STRUCTURE_CONTROLS			 = 19,
		VIEW_STRUCTURE_SHIPS_CONTENT	 = 20,
		VIEW_STRUCTURE_DECKS			 = 21,
		VIEW_FLEET_ENGINEER_STRUCTURE	 = 22;
		
	
	private final static String[] BUILDINGS_ORDER = {
    	"civilian_infrastructures",
    	"corporations",
    	"trade_port",
    	"extractor_center",
    	"refinery",
    	"exploitation0",
    	"exploitation1",
    	"exploitation2",
    	"exploitation3",
    	"storehouse",
    	"factory",
    	"spaceship_yard",
    	"defensive_deck",
    	"laboratory",
    	"research_center"
	};
	
	private final static String[] FLEET_NUMBER = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ControlHandler showFleetContentHandler, hideControlsHandler,
		colonizeHandler, transferHandler, showFleetTagSelectionHandler,
		probeHandler, swapHandler, showFleetSkillsSelection0Handler,
		showFleetSkillsSelection1Handler, showFleetControlsHandler,
		showSystemControlsHandler, showSystemContent0Handler,
		showSystemContent1Handler, showSystemContent2Handler, buyFleetHandler,
		hyperspaceHandler, buildHandler, cancelHyperspaceHandler,
		tacticsHandler, buildShipsSystemHandler, offensiveLinkHandler,
		defensiveLinkHandler, miningHandler, showSystemShipsHandler,
		tradeHandler, trainHandler, engineerHandler, buildWardHandler,
		destroyWardHandler, selectionHandler, destroyHandler,
		destroySpaceStationHandler, mountStructureHandler,
		swapSpaceStationHandler, fundSpaceStationHandler, pickUpDoodadHandler,
		empHandler, leaveSystemHandler, cancelColonizationHandler, bankHandler, lotteryHandler,
		stealthHandler, pyrotechnistHandler, buildMineHandler, bombingHandler,
		deludeHandler, pushFleetsHandler, defuseHandler, buildStructure,
		swapStructureHandler, upgradeModuleHandler, destroyStructureHandler,
		attackStructureHandler, castStructureSkillHandler,
		dismountStructureHandler, showFleetStructureItems,
		deactivateStructureHandler, activateStructureHandler, repairHandler,
		buyStructureFleetHandler, showStructureControlsHandler,
		showStructureShipsHandler, showStructureDecksHandler,
		buildShipsHandler, cancelDismountHandler, engineerStructureHandler,
		engineerStructureNextHandler, migrationHandler, cancelMigrationHandler,
		buildStructureNext, reloadingHandler;
	
	private ArrayList<TimerHandler> localUpdaters;
	
	private int currentView;
	
	private int currentTab;
	
	private Action currentAction;
	
	private boolean canvasVisible;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public EmpireControlPanel() {
		this.currentView = NO_VIEW;
		this.localUpdaters = new ArrayList<TimerHandler>();
		this.canvasVisible = false;
		
		this.showFleetControlsHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showFleetControls();
//				Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_FLEET);
			}
		};
		this.showFleetContentHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showFleetContent();
			}
		};
		this.showFleetTagSelectionHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showFleetTagSelection();
			}
		};
		this.showFleetSkillsSelection0Handler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showFleetSkillsSelection(0);
			}
		};
		this.showFleetSkillsSelection1Handler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showFleetSkillsSelection(1);
			}
		};
		this.hideControlsHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				SelectionManager.getInstance().setNoSelection();
			}
		};
		this.cancelDismountHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet == null)
					return;
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("fleet", String.valueOf(fleet.getId()));
				
				currentAction = new Action("structure/canceldismount", params,
					UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
			}
		};
		this.showStructureControlsHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showStructureControls();
			}
		};
		this.showStructureShipsHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showStructureShipsContent();
			}
		};
		this.showStructureDecksHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showStructureDecks();
			}
		};
		this.tacticsHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet == null)
					return;
				
				Client.getInstance().getTacticsDialog().show(fleet);
			}
		};
		this.reloadingHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet == null)
					return;
				Client.getInstance().getPlayerTacticsDialog().setVisible(true);
               }
				
				
			};
		
		this.colonizeHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet == null)
					return;
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("fleet", String.valueOf(fleet.getId()));
				
				new Action("colonizeorcapture", params,
					UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
			}
		};
		
		this.migrationHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet == null)
					return;
				
				MigrateDialog dialog = new MigrateDialog(getSystemUnderFleet(fleet), fleet, getSystemMigrationCost());
					dialog.setVisible(true);
					
			}
		};
		
		this.cancelMigrationHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet == null)
					return;
				
				HashMap<String, String> params = new HashMap<String, String>();
					params.put("fleet", String.valueOf(fleet.getId()));
				
				currentAction = new Action("cancelmigration", params, UpdateManager.UPDATE_CALLBACK);
				
				SelectionManager.getInstance().setNoSelection();
			
			}
		};
		
		
		this.pushFleetsHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("system", String.valueOf(getSelectedSystem().getId()));
				
				new Action("pushfleets", params, new ActionCallbackAdapter() {
					public void onSuccess(AnswerData data) {
						JSOptionPane.showMessageDialog(
							"Toutes les flottes neutres ont été " +
							"éjectées de votre système.", "Information",
							JSOptionPane.OK_OPTION,
							JSOptionPane.INFORMATION_MESSAGE, null);
						
						UpdateManager.UPDATE_CALLBACK.onSuccess(data);
					}
				});
			}
		};
		this.defuseHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				Client.getInstance().getActionManager().setAction(
						ActionManager.ACTION_DEFUSE);
			}
		};
		this.transferHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet == null)
					return;
				
				Client.getInstance().getSwapDialog().show(new GroupData[]{
					new GroupData(Client.getInstance().getEmpireView().getSystemById(fleet.getSystem()),
							Client.getInstance().getResourcesManager(),
							Client.getInstance().getEmpireView().getLastSystemsUpdate()),
					new GroupData(fleet, Client.getInstance().getProgressBar().getPlayerLevel())
				}, SwapDialog.MODE_SYSTEM_TO_FLEET);
			}
		};
		this.probeHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleetData = getFirstSelectedFleet();
				if (fleetData == null)
					return;

				if (isSkillReloaded(fleetData, getSkillByPosition(fleetData, x))) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("fleet", String.valueOf(fleetData.getId()));
					
					new Action("probesystem", params, new ActionCallbackAdapter() {
						@Override
						public void onSuccess(AnswerData data) {
							SelectionManager.getInstance().setNoSelection();
							
							ProbeReportDialog reportDialog =
								new ProbeReportDialog(data.getProbeReport());
							reportDialog.setVisible(true);
						}
					});
				} else {
					JSOptionPane.showMessageDialog(
						"La compétence est en train de se recharger.",
						"Action invalide", JSOptionPane.OK_OPTION,
						JSOptionPane.WARNING_MESSAGE, null);
				}
			}
		};
		this.swapHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleetData = getFirstSelectedFleet();
				
				if (fleetData != null && isSkillReloaded(
						fleetData, getSkillByPosition(fleetData, x)))
					Client.getInstance().getActionManager().setAction(
						ActionManager.ACTION_SWAP);
				else
					JSOptionPane.showMessageDialog(
						"La compétence est en train de se recharger.",
						"Action invalide", JSOptionPane.OK_OPTION,
						JSOptionPane.WARNING_MESSAGE, null);
			}
		};
		this.empHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleetData = getFirstSelectedFleet();
				
				if (fleetData != null && isSkillReloaded(
						fleetData, getSkillByPosition(fleetData, x)))
					Client.getInstance().getActionManager().setAction(
						ActionManager.ACTION_EMP);
				else
					JSOptionPane.showMessageDialog(
						"La compétence est en train de se recharger.",
						"Action invalide", JSOptionPane.OK_OPTION,
						JSOptionPane.WARNING_MESSAGE, null);
			}
		};
		this.stealthHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleetData = getFirstSelectedFleet();
				if (fleetData == null)
					return;
				
				if (isSkillReloaded(fleetData, getSkillByPosition(fleetData, x))) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("fleet", String.valueOf(fleetData.getId()));
					
					new Action("skill/switchstealth", params,
						UpdateManager.UPDATE_CALLBACK);
				} else {
					JSOptionPane.showMessageDialog(
						"La compétence est en train de se recharger.",
						"Action invalide", JSOptionPane.OK_OPTION,
						JSOptionPane.WARNING_MESSAGE, null);
				}
			}
		};
		this.deludeHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleetData = getFirstSelectedFleet();
				if (fleetData == null)
					return;

				if (isSkillReloaded(fleetData, getSkillByPosition(fleetData, x))) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("fleet", String.valueOf(fleetData.getId()));
					
					new Action("skill/createdelude", params,
						UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
				} else {
					JSOptionPane.showMessageDialog(
						"La compétence est en train de se recharger.",
						"Action invalide", JSOptionPane.OK_OPTION,
						JSOptionPane.WARNING_MESSAGE, null);
				}
			}
		};
		this.bombingHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				Client.getInstance().getActionManager().setAction(
					ActionManager.ACTION_BOMBING);
			}
		};
		this.showSystemControlsHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showSystemControls();
			}
		};
		this.showSystemContent0Handler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showSystemBuildingsContent(0);
			}
		};
		this.showSystemContent1Handler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showSystemBuildingsContent(1);
			}
		};
		this.showSystemContent2Handler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showSystemBuildingsContent(2);
			}
		};
		this.buyFleetHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				JSOptionPane.showMessageDialog("Voulez-vous acheter une " +
					"flotte pour " + getFleetCost(Client.getInstance().getProductsManager()) + "&nbsp;" +
					"<img class=\"resource credits\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\" " +
					"unselectable=\"on\"/>&nbsp;?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.YES_OPTION) {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("system", String.valueOf(getSelectedSystem().getId()));
								
								//new Action("buyfleet", params, UpdateManager.UPDATE_CALLBACK);
								new Action("buyfleet", params, new ActionCallbackAdapter() {
									@Override
									public void onSuccess(AnswerData data) {
										// Fenêtre de transfert automatique
										PlayerFleetData fleet = data.getFleetData();
										if (fleet == null)
											return;
										
										Client.getInstance().getSwapDialog().show(new GroupData[]{
											new GroupData(Client.getInstance().getEmpireView().getSystemById(fleet.getSystem()),
													Client.getInstance().getResourcesManager(),
													Client.getInstance().getEmpireView().getLastSystemsUpdate()),
											new GroupData(fleet, Client.getInstance().getProgressBar().getPlayerLevel())
										}, SwapDialog.MODE_SYSTEM_TO_FLEET);
									}
								});

							}
						}
					});
			}
		};
		this.hyperspaceHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				Client.getInstance().getGalaxyMap().show(GalaxyMap.MODE_HYPERSPACE);
			}
		};
		this.cancelHyperspaceHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				PlayerFleetData[] selectedFleetsData = getSelectedFleets();
				if (selectedFleetsData.length == 0)
					return;
				
				HashMap<String, String> params = new HashMap<String, String>();
				for (int i = 0; i < selectedFleetsData.length; i++)
					params.put("fleet" + i, String.valueOf(selectedFleetsData[i].getId()));
				
				currentAction = new Action("canceljump", params, UpdateManager.UPDATE_CALLBACK);
				
				SelectionManager.getInstance().setNoSelection();
			}
		};
		this.buildHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerStarSystemData systemData = getSelectedSystem();
				if (systemData == null)
					return;
				
				final int buildSlot = x - 1;
				
				if (button == Event.BUTTON_RIGHT) {
					if (currentAction != null && currentAction.isPending())
						return;
					if(buildSlot>=getSelectedSystem().getBuildsCount()) {
						return;
					}
					JSOptionPane.showMessageDialog("Voulez-vous annuler la " +
						"construction du bâtiment (les ressources vous " +
						"seront restituées) ?", "Construction",
						JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
						JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
							public void optionSelected(Object option) {
								if ((Integer) option == JSOptionPane.YES_OPTION) {
									// Annule la construction en cours
									PlayerStarSystemData systemData = getSelectedSystem();
									if (systemData == null)
										return;
									
									HashMap<String, String> params = new HashMap<String, String>();
									params.put("system", String.valueOf(systemData.getId()));
									switch(buildSlot) {
									// 1er slot
									case 0:
									if (systemData.getBuildsCount() > 1) {
										params.put("build_id", String.valueOf(systemData.getBuildAt(1).getType()));
										params.put("build_level", String.valueOf(systemData.getBuildAt(1).getLevel()));
										if(systemData.getBuildsCount() > 2) {
											params.put("queue_id", String.valueOf(systemData.getBuildAt(2).getType()));
											params.put("queue_level", String.valueOf(systemData.getBuildAt(2).getLevel()));
											params.put("third_id","");
											params.put("third_level","0");
										}
										else
										{
											params.put("queue_id", "");
											params.put("queue_level", "0");
											params.put("third_id","");
											params.put("third_level","0");
										}
									} else {
										params.put("build_id", "");
										params.put("build_level", "0");
										params.put("queue_id", "");
										params.put("queue_level", "0");
										params.put("third_id","");
										params.put("third_level","0");
									}
									break;
									case 1:
									// Slot en attente
									if (systemData.getBuildsCount() > 0) {
										params.put("build_id", systemData.getBuildAt(0).getType());
										params.put("build_level", String.valueOf(systemData.getBuildAt(0).getLevel()));
										params.put("third_id","");
										params.put("third_level","0");
										if(systemData.getBuildsCount() > 2) {
											params.put("queue_id", systemData.getBuildAt(2).getType());
											params.put("queue_level", String.valueOf(systemData.getBuildAt(2).getLevel()));
										}
										else
										{
											params.put("queue_id", "");
											params.put("queue_level", "0");
										}
									} else {
										params.put("build_id", "");
										params.put("build_level", "0");
										params.put("queue_id", "");
										params.put("queue_level", "0");
										params.put("third_id","");
										params.put("third_level","0");
										
									}
									break;
									case 2:
									if (systemData.getBuildsCount() > 2) {
										params.put("build_id", systemData.getBuildAt(0).getType());
										params.put("build_level", String.valueOf(systemData.getBuildAt(0).getLevel()));
										params.put("queue_id", systemData.getBuildAt(1).getType());
										params.put("queue_level", String.valueOf(systemData.getBuildAt(1).getLevel()));
										params.put("third_id","");
										params.put("third_level","0");
										
									}
									break;
								}
									
									currentAction = new Action("systems/build", params,
											UpdateManager.UPDATE_CALLBACK);
								}
							}
					});
				} else {
					BuildBuildingDialog dialog = new BuildBuildingDialog(
						Client.getInstance().getResourcesManager(),
						Client.getInstance().getResearchManager());
					dialog.show(systemData, buildSlot);
				}
			}
		};
		this.pickUpDoodadHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet != null) {
					HashMap<String, String> params =
						new HashMap<String, String>();
					params.put("id", String.valueOf(fleet.getId()));
					
					currentAction = new Action("pickupdoodad", params,
						UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
				}
			}
		};
		this.buildShipsSystemHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerStarSystemData systemData = getSelectedSystem();
				if (systemData == null)
					return;
				
				int slot = -1;
				int index = 1;
				for (int i = 0; i < systemData.getBuildSlotsCount(); i++) {
					if (systemData.getBuildSlotAt(i).getId() != 0) {
						if (index == x)
							slot = index - 1;
						index++;
					}
				}
				
				final int buildSlot = slot;
				
				if (button == Event.BUTTON_RIGHT) {
					if (currentAction != null && currentAction.isPending())
						return;
					if(buildSlot==-1) {
						return;
					}
					JSOptionPane.showMessageDialog("Voulez-vous annuler la " +
						"construction des vaisseaux (les ressources vous " +
						"seront restituées) ?", "Construction",
						JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
						JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
							public void optionSelected(Object option) {
								if ((Integer) option == JSOptionPane.YES_OPTION) {
									HashMap<String, String> params = new HashMap<String, String>();
									params.put("system", String.valueOf(getSelectedSystem().getId()));
									params.put("slot", String.valueOf(buildSlot));
									params.put("id", "0");
									params.put("count", "0");
									
									currentAction = new Action("systems/buildships", params,
											UpdateManager.UPDATE_CALLBACK);
								}
							}
					});
				} else {
					BuildShipDialog dialog = new BuildShipDialog(
						Client.getInstance().getResourcesManager(),
						Client.getInstance().getResearchManager(),
						Client.getInstance().getProductsManager());
					dialog.show(systemData, slot);
				}
			}
		};
		this.buildShipsHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				StructureData structureData = getSelectedStructure();
				if (structureData == null)
					return;
				
				int slot = -1;
				int index = 1;
				for (int i = 0; i < structureData.getBuildSlotsCount(); i++) {
					if (structureData.getBuildSlotAt(i).getId() != 0) {
						if (index == x)
							slot = index - 1;
						index++;
					}
				}
				
				final int buildSlot = slot;
				
				if (button == Event.BUTTON_RIGHT) {
					if (currentAction != null && currentAction.isPending())
						return;
					
					JSOptionPane.showMessageDialog("Voulez-vous annuler la " +
						"construction des vaisseaux (les ressources vous " +
						"seront restituées) ?", "Construction",
						JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
						JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
							public void optionSelected(Object option) {
								if ((Integer) option == JSOptionPane.YES_OPTION) {
									HashMap<String, String> params = new HashMap<String, String>();
									params.put("structure", String.valueOf(getSelectedStructure().getId()));
									params.put("slot", String.valueOf(buildSlot));
									params.put("id", "0");
									params.put("count", "0");
									
									currentAction = new Action("structure/buildships", params,
											UpdateManager.UPDATE_CALLBACK);
								}
							}
					});
				} else {
					BuildShipDialog dialog = new BuildShipDialog(
						Client.getInstance().getResourcesManager(),
						Client.getInstance().getResearchManager(),
						Client.getInstance().getProductsManager());
					dialog.show(structureData, slot);
				}
			}
		};
		this.offensiveLinkHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				// Création d'un lien offensif
				Client.getInstance().getActionManager().setAction(
						ActionManager.ACTION_OFFENSIVE_LINK);
			}
		};
		this.defensiveLinkHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				// Création d'un lien défensif
				Client.getInstance().getActionManager().setAction(
						ActionManager.ACTION_DEFENSIVE_LINK);
			}
		};
		this.miningHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet == null)
					return;
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("fleet", String.valueOf(fleet.getId()));
				
				currentAction = new Action("skill/mining", params,
					UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
			}
		};
		this.showSystemShipsHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showSystemShipsContent();
			}
		};
		this.tradeHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet != null)
					Client.getInstance().getTradeDialog().show(fleet);
			}
		};
		this.bankHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet != null)
					Client.getInstance().getBankDialog().show(fleet);
			}
		};
		this.lotteryHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet != null){
					final int idFleet = fleet.getId();
					HashMap<String, String> params = new HashMap<String, String>();
                params.put("fleet", String.valueOf(idFleet));
               
                new Action("lottery/getinfo", params, new ActionCallbackAdapter() {
                        @Override
                        public void onSuccess(AnswerData data) {
                                LotteryDialog lottery = new LotteryDialog(idFleet, data.getLotteryInfoData());
                                lottery.setVisible(true);
                        }
                });
			
		}
			}
		};
		this.trainHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet != null) {
					JSOptionPane.showMessageDialog("Voulez-vous entraîner " +
						"la flotte au niveau " + (fleet.getFleetLevel() + 1) +
						" pour " + FleetData.TRAINING_COST[fleet.getFleetLevel()] +
						"&nbsp;<img class=\"resource credits\" src=\"" + Config.getMediaUrl() +
						"images/misc/blank.gif\" unselectable=\"on\"/> ?",
						"Entrainement",
						JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
						JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
							public void optionSelected(Object option) {
								if ((Integer) option == JSOptionPane.YES_OPTION) {
									PlayerFleetData fleet = getFirstSelectedFleet();
									
									if (fleet != null) {
										HashMap<String, String> params =
											new HashMap<String, String>();
										params.put("id", String.valueOf(fleet.getId()));
										
										currentAction = new Action("trainfleet", params,
												UpdateManager.UPDATE_CALLBACK);
									}
								}
							}
					});
				}
			}
		};
		this.engineerHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showFleetEngineerStructures();
			}
		};
		this.engineerStructureHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showFleetEngineerStructuresSolo();
			}
		};
		this.engineerStructureNextHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showFleetEngineerStructuresNext();
			}
		};
		
		
		this.pyrotechnistHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showFleetPyrotechnistStructures();
			}
		};
		this.buildWardHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				String[] types = {WardData.TYPE_OBSERVER, WardData.TYPE_SENTRY,
					WardData.TYPE_OBSERVER_INVISIBLE, WardData.TYPE_SENTRY_INVISIBLE};
				
				Client.getInstance().getActionManager().setAction(
					ActionManager.ACTION_SETUP_WARD, types[x - 1]);
			}
		};
		this.buildMineHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				String[] types = {WardData.TYPE_STUN, WardData.TYPE_MINE,
					WardData.TYPE_STUN_INVISIBLE, WardData.TYPE_MINE_INVISIBLE};
				
				Client.getInstance().getActionManager().setAction(
					ActionManager.ACTION_SETUP_WARD, types[x]);
			}
		};
		this.destroyWardHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				JSOptionPane.showMessageDialog("Voulez-vous saboter la " +
					"structure ?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.YES_OPTION) {
							PlayerFleetData fleet = getFirstSelectedFleet();
							
							if (fleet != null) {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("fleet", String.valueOf(fleet.getId()));
								
								currentAction = new Action("skill/destroyward", params,
									UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
							}
						}
					}
				});
			}
		};
		this.selectionHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				for (int i = 0; i < 5; i++)
					for (int j = 0; j < 2; j++)
						controls[i][j].removeStyleName("control-selected");
				controls[x][y].addStyleName("control-selected");
			}
		};
		this.destroyHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				final long lastSystemsUpdate = Client.getInstance(
						).getEmpireView().getLastSystemsUpdate();
				final long lastAreaUpdate = Client.getInstance(
					).getAreaContainer().getLastAreaUpdate();
				int selectedX = -1, selectedY = -1;
				
				for (int i = 0; i < 5; i++)
					for (int j = 0; j < 2; j++)
						if (controls[i][j].getStyleName().contains("control-selected")) {
							selectedX = i;
							selectedY = j;
							break;
						}
				
				DynamicMessages messages = GWT.create(DynamicMessages.class);
				
				if (selectedX != -1) {
					String message = "";
					String quantity = "";
					
					switch (currentView) {
					case VIEW_FLEET_CONTENT:
						// Destruction de vaisseaux / ressources dans une flotte
						PlayerFleetData fleetData = getFirstSelectedFleet();
						if (fleetData == null)
							return;
						
						if (selectedY == 0) {
							int slotIndex = 0;
							for (int k = 0; k < fleetData.getSlotsCount(); k++) {
								SlotInfoData slotData = fleetData.getSlotAt(k);
								if (slotData.getId() != 0) {
									if (slotIndex == selectedX) {
										message = "Indiquez la quantité de " +
											messages.getString("ships" + slotData.getId()) +
											" que vous souhaitez détruire (la destruction " +
											"est irréversible) :";
										
										quantity = String.valueOf((long)
											fleetData.getSlotAt(k).getCount());
										break;
									}
									
									slotIndex++;
								}
							}
						} else {
							int itemIndex = 0;
							for (int k = 0; k < fleetData.getItemsCount(); k++) {
								ItemInfoData item = fleetData.getItemAt(k);
								if (item.getType() != ItemInfoData.TYPE_NONE) {
									if (itemIndex == selectedX) {
										switch (item.getType()) {
										case ItemInfoData.TYPE_RESOURCE:
											message = "Indiquez la quantité de <img src=\"" +
												Config.getMediaUrl() + "images/misc/blank.gif\"" +
												" class=\"resource r" + k + "\"/> que vous " +
												"souhaitez détruire (la destruction " +
												"est irréversible) :";
												break;
										case ItemInfoData.TYPE_STRUCTURE:
											message = "Indiquez le nombre de structures que " +
												"vous souhaitez détruire (la destruction " +
												"est irréversible) :";
											break;
										case ItemInfoData.TYPE_STUFF:
											message = "Indiquez le nombre d'objets que " +
												"vous souhaitez détruire (la destruction " +
												"est irréversible) :";
											break;
										}
										
										quantity = String.valueOf((long) item.getCount());
										break;
									}
									
									itemIndex++;
								}
							}
						}
						break;
					case VIEW_SYSTEM_SHIPS_CONTENT:
						// Destruction de vaisseaux
						PlayerStarSystemData systemData = getSelectedSystem();
						if (systemData == null)
							return;
						
						int slotIndex = 0;
						for (int i = 0; i < systemData.getSlotsCount(); i++) {
							int slotId = systemData.getInterpolatedSlotIdAt(i, lastSystemsUpdate);
							
							if (slotId != 0) {
								if (slotIndex == selectedX + 5 * selectedY) {
									message = "Indiquez la quantité de " +
										messages.getString("ships" + slotId) +
										" que vous souhaitez détruire (la destruction " +
										"est irréversible) :";
									
									quantity = String.valueOf((long)
										systemData.getSlotAt(i).getCount());
									break;
								}
								
								slotIndex++;
							}
						}
						break;
					case VIEW_SYSTEM_BUILDINGS_CONTENT:
						// Destruction de bâtiments
						message = "La destruction est irréversible. " +
							"Êtes-vous sur de vouloir détruire ce " +
							"bâtiment ? Vous récupérerez 50% du coût " +
							"de la dernière amélioration du bâtiment.";
						break;
					case VIEW_STRUCTURE_SHIPS_CONTENT:
						// Destruction de vaisseaux
						StructureData structureData = getSelectedStructure();
						if (structureData == null)
							return;
						
						switch (structureData.getType()) {
						case StructureData.TYPE_SPACESHIP_YARD:
							slotIndex = 0;
							for (int i = 0; i < structureData.getSlotsCount(); i++) {
								int slotId = structureData.getInterpolatedSlotIdAt(i, lastAreaUpdate);
								
								if (slotId != 0) {
									if (slotIndex == selectedX + 5 * selectedY) {
										message = "Indiquez la quantité de " +
											messages.getString("ships" + slotId) +
											" que vous souhaitez détruire (la destruction " +
											"est irréversible) :";
										
										quantity = String.valueOf((long)
												structureData.getSlotAt(i).getCount());
										break;
									}
									
									slotIndex++;
								}
							}
							break;
						}
						break;
					}
					
					if (currentView == VIEW_SYSTEM_BUILDINGS_CONTENT) {
						// Destruction de bâtiments
						JSOptionPane.showMessageDialog(message, "Destruction",
							JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
							JSOptionPane.QUESTION_MESSAGE,
							new OptionPaneListener() {
								public void optionSelected(Object option) {
									if ((Integer) option != JSOptionPane.YES_OPTION)
										return;
									
									int selectedX = -1, selectedY = -1;

									for (int i = 0; i < 5; i++)
										for (int j = 0; j < 2; j++)
											if (controls[i][j].getStyleName().contains("control-selected")) {
												selectedX = i;
												selectedY = j;
												break;
											}
									
									if (selectedX != -1) {
										switch (currentView) {
										case VIEW_SYSTEM_BUILDINGS_CONTENT:
											PlayerStarSystemData systemData = getSelectedSystem();
											if (systemData == null)
												return;
											
											int buildingIndex = 0;
											buildings:for (int i = 0; i < BUILDINGS_ORDER.length; i++) {
												String type = BUILDINGS_ORDER[i];
												
												for (int level = 0; level < 5; level++) {
													int count = systemData.getBuildingsCount(type, level);
													
													if (count > 0) {
														if (buildingIndex == (currentTab + 1) * 9) {
															break buildings;
														}
														
														if (buildingIndex >= currentTab * 9) {
															int x = (buildingIndex % 9) % 5;
															int y = buildingIndex - currentTab * 9 < 5 ? 0 : 1;
															
															if (x == selectedX && y == selectedY) {
																HashMap<String, String> params = new HashMap<String, String>();
																params.put("system", String.valueOf(systemData.getId()));
																params.put("id", type);
																params.put("level", String.valueOf(level));
																
																currentAction = new Action("systems/destroy",
																	params, UpdateManager.UPDATE_CALLBACK);
																return;
															}
														}
														buildingIndex++;
													}
												}
											}
											
											break;
										}
										return;
									}
								}
							});
					} else {
						// Destruction de vaisseaux / ressources
						JSOptionPane.showInputDialog(message, "Destruction",
							JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
							JSOptionPane.QUESTION_MESSAGE,
							new OptionPaneListener() {
								public void optionSelected(Object option) {
									if (option == null)
										return;
									
									long count = Utilities.parseNumber((String) option);
									
									if (count < 1) {
										JSOptionPane.showMessageDialog(
											"Quantité invalide.", "Erreur",
											JSOptionPane.OK_OPTION,
											JSOptionPane.INFORMATION_MESSAGE, null);
										return;
									}
									
									int selectedX = -1, selectedY = -1;
									
									for (int i = 0; i < 5; i++)
										for (int j = 0; j < 2; j++)
											if (controls[i][j].getStyleName().contains("control-selected")) {
												selectedX = i;
												selectedY = j;
												break;
											}
									
									if (selectedX != -1) {
										switch (currentView) {
										case VIEW_FLEET_CONTENT:
											PlayerFleetData fleetData = getFirstSelectedFleet();
											if (fleetData == null)
												return;
											
											if (selectedY == 0) {
												// Destruction de vaisseaux
												int slotIndex = 0;
												for (int k = 0; k < fleetData.getSlotsCount(); k++) {
													SlotInfoData slotData = fleetData.getSlotAt(k);
													if (slotData.getId() != 0) {
														if (slotIndex == selectedX) {
															int slotsCount = 0;
															for (int i = 0; i < fleetData.getSlotsCount(); i++) {
																SlotInfoData slotData2 = fleetData.getSlotAt(i);
																if (slotData2.getId() != 0)
																	slotsCount++;
															}
															
															if (count >= (long) slotData.getCount() && slotsCount == 1) {
																// Demande une confirmation si tous
																// les vaisseaux sont détruits
																final int indexSave = k;
																final long countSave = count;
																JSOptionPane.showMessageDialog(
																	"Si vous détruisez ces vaisseaux, " +
																	"votre flotte sera détruite ! Êtes-vous sur " +
																	"de vouloir les détruire ?",
																	"Confirmation", JSOptionPane.OK_OPTION |
																	JSOptionPane.CANCEL_OPTION,
																	JSOptionPane.QUESTION_MESSAGE,
																	new OptionPaneListener() {
																		public void optionSelected(Object option) {
																			if ((Integer) option != JSOptionPane.OK_OPTION)
																				return;
																			
																			PlayerFleetData fleetData = getFirstSelectedFleet();
																			if (fleetData == null)
																				return;
																			
																			HashMap<String, String> params = new HashMap<String, String>();
																			params.put("id", String.valueOf(fleetData.getId()));
																			params.put("index", String.valueOf(indexSave));
																			params.put("count", String.valueOf(countSave));
																			params.put("type", "ship");
																			
																			currentAction = new Action("fleet/destroystuff",
																				params, UpdateManager.UPDATE_CALLBACK);																		}
																	});
															} else {
																HashMap<String, String> params = new HashMap<String, String>();
																params.put("id", String.valueOf(fleetData.getId()));
																params.put("index", String.valueOf(k));
																params.put("count", String.valueOf(count));
																params.put("type", "ship");
																
																currentAction = new Action("fleet/destroystuff",
																	params, UpdateManager.UPDATE_CALLBACK);
															}
															return;
														}
														
														slotIndex++;
													}
												}
											} else {
												// Destruction d'items
												int itemIndex = 0;
												for (int k = 0; k < fleetData.getItemsCount(); k++) {
													ItemInfoData item = fleetData.getItemAt(k);
													if (item.getType() != ItemInfoData.TYPE_NONE) {
														if (itemIndex == selectedX) {
															HashMap<String, String> params = new HashMap<String, String>();
															params.put("id", String.valueOf(fleetData.getId()));
															params.put("index", String.valueOf(k));
															params.put("count", String.valueOf(count));
															params.put("type", "resource");
															
															currentAction = new Action("fleet/destroystuff",
																params, UpdateManager.UPDATE_CALLBACK);
															return;
														}
														
														itemIndex++;
													}
												}
											}
											break;
										case VIEW_SYSTEM_SHIPS_CONTENT:
											PlayerStarSystemData systemData = getSelectedSystem();
											if (systemData == null)
												return;
											
											// Destruction de vaisseaux
											int slotIndex = 0;
											
											for (int i = 0; i < systemData.getSlotsCount(); i++) {
												int slotId = systemData.getInterpolatedSlotIdAt(i, lastSystemsUpdate);
												
												if (slotId != 0) {
													if (slotIndex == selectedX + 5 * selectedY) {
														HashMap<String, String> params = new HashMap<String, String>();
														params.put("id", String.valueOf(systemData.getId()));
														params.put("index", String.valueOf(i));
														params.put("count", String.valueOf(count));
														params.put("type", "ship");
														
														currentAction = new Action("systems/destroystuff",
															params, UpdateManager.UPDATE_CALLBACK);
														return;
													}
													
													slotIndex++;
												}
											}
											break;
										case VIEW_STRUCTURE_SHIPS_CONTENT:
											StructureData structureData = getSelectedStructure();
											if (structureData == null)
												return;
											
											// Destruction de vaisseaux
											slotIndex = 0;
											
											for (int i = 0; i < structureData.getSlotsCount(); i++) {
												int slotId = structureData.getInterpolatedSlotIdAt(i, lastSystemsUpdate);
												
												if (slotId != 0) {
													if (slotIndex == selectedX + 5 * selectedY) {
														HashMap<String, String> params = new HashMap<String, String>();
														params.put("id", String.valueOf(structureData.getId()));
														params.put("index", String.valueOf(i));
														params.put("count", String.valueOf(count));
														params.put("type", "ship");
														
														currentAction = new Action("structure/destroystuff",
															params, UpdateManager.UPDATE_CALLBACK);
														return;
													}
													
													slotIndex++;
												}
											}
											break;
										}
										return;
									}
								}
							}, quantity);
					}
				} else {
					// Pas d'objet sélectionné
					switch (currentView) {
					case VIEW_FLEET_CONTENT:
						JSOptionPane.showMessageDialog(
							"Sélectionnez un vaisseau ou une ressources, puis " +
							"cliquez sur détruire.", "Destruction",
							JSOptionPane.OK_OPTION,
							JSOptionPane.INFORMATION_MESSAGE,
							null);
						break;
					case VIEW_SYSTEM_SHIPS_CONTENT:
						JSOptionPane.showMessageDialog(
							"Sélectionnez un vaisseau, puis " +
							"cliquez sur détruire.", "Destruction",
							JSOptionPane.OK_OPTION,
							JSOptionPane.INFORMATION_MESSAGE,
							null);
						break;
					case VIEW_SYSTEM_BUILDINGS_CONTENT:
						JSOptionPane.showMessageDialog(
							"Sélectionnez un bâtiment, puis " +
							"cliquez sur détruire.", "Destruction",
							JSOptionPane.OK_OPTION,
							JSOptionPane.INFORMATION_MESSAGE,
							null);
						break;
					}
				}
			}
		};
		this.destroySpaceStationHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				PlayerFleetData fleetData = getFirstSelectedFleet();
				IndexedAreaData areaData = Client.getInstance().getAreaContainer().getArea();
				SpaceStationData overSpaceStationData = null;
				
				for (int i = 0; i < areaData.getSpaceStationsCount(); i++) {
					SpaceStationData spaceStationData = areaData.getSpaceStationAt(i);
					int dx = spaceStationData.getX() - fleetData.getX();
					int dy = spaceStationData.getY() - fleetData.getY();
					double radius = SpaceStationData.SPACE_STATION_RADIUS;
					
					if (dx * dx + dy * dy <= radius * radius) {
						overSpaceStationData = spaceStationData;
						break;
					}
				}
				
				if (overSpaceStationData.getTreaty().equals("ally")) {
					JSOptionPane.showInputDialog("Confirmez votre mot de passe " +
						"pour déclencher l'auto-destruction de la station :", "Confirmation",
						JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
						JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
							public void optionSelected(Object option) {
								if (option != null) {
									PlayerFleetData fleet = getFirstSelectedFleet();
									
									if (fleet != null) {
										HashMap<String, String> params = new HashMap<String, String>();
										params.put("fleet", String.valueOf(fleet.getId()));
										params.put("password", (String) option);
										
										currentAction = new Action("skill/destroyspacestation", params,
											UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
									}
								}
							}
					}, "", true);
				} else {
					JSOptionPane.showMessageDialog("Voulez-vous saboter la " +
							"station spatiale ?", "Confirmation",
							JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
							JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
								public void optionSelected(Object option) {
									if ((Integer) option == JSOptionPane.YES_OPTION) {
										PlayerFleetData fleet = getFirstSelectedFleet();
										
										if (fleet != null) {
											HashMap<String, String> params = new HashMap<String, String>();
											params.put("fleet", String.valueOf(fleet.getId()));
											
											currentAction = new Action("skill/destroyspacestation", params,
												UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
										}
									}
								}
						});
				}
			}
		};
		this.swapSpaceStationHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleetData = getFirstSelectedFleet();
				IndexedAreaData areaData = Client.getInstance().getAreaContainer().getArea();
				SpaceStationData overSpaceStationData = null;
				
				for (int i = 0; i < areaData.getSpaceStationsCount(); i++) {
					SpaceStationData spaceStationData = areaData.getSpaceStationAt(i);
					int dx = spaceStationData.getX() - fleetData.getX();
					int dy = spaceStationData.getY() - fleetData.getY();
					double radius = SpaceStationData.SPACE_STATION_RADIUS;
					
					if (dx * dx + dy * dy <= radius * radius) {
						overSpaceStationData = spaceStationData;
						break;
					}
				}
				
				Client.getInstance().getSwapDialog().show(new GroupData[]{
					new GroupData(overSpaceStationData),
					new GroupData(fleetData, Client.getInstance().getProgressBar().getPlayerLevel())
				}, SwapDialog.MODE_FLEET_TO_STATION);
			}
		};
		this.swapStructureHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				PlayerFleetData fleetData = getFirstSelectedFleet();
				StructureData overStructure = getStructureUnderFleet(fleetData);
				
				Client.getInstance().getSwapDialog().show(new GroupData[]{
					new GroupData(overStructure, Client.getInstance(
						).getAreaContainer().getLastAreaUpdate()),
					new GroupData(fleetData, Client.getInstance().getProgressBar().getPlayerLevel())
				}, overStructure.getType() == StructureData.TYPE_STOREHOUSE ?
					SwapDialog.MODE_FLEET_TO_STOREHOUSE :
					SwapDialog.MODE_FLEET_TO_SPACESHIP_YARD);
			}
		};
		this.fundSpaceStationHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				JSOptionPane.showInputDialog("Indiquez la quantité de " +
					"crédits que vous souhaitez transférer sur la station " +
					"(" + Formatter.formatNumber(Client.getInstance(
					).getResourcesManager().getCurrentCredits(), true) +
					"&nbsp;<img src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" class=\"resource credits\"/> " +
					"maximum). Une fois transférés, les crédits ne peuvent " +
					"plus être récupérés.", "Transfert crédits",
					JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
					JSOptionPane.QUESTION_MESSAGE,
					new OptionPaneListener() {
						public void optionSelected(Object option) {
							if (option != null) {
								long credits = Utilities.parseNumber((String) option);
								
								if (credits == -1) {
									JSOptionPane.showMessageDialog(
										"Quantité de crédits invalide.",
										"Erreur", JSOptionPane.OK_OPTION,
										JSOptionPane.WARNING_MESSAGE, null);
								} else {
									HashMap<String, String> params = new HashMap<String, String>();
									params.put("station", String.valueOf(getSelectedSpaceStation().getId()));
									params.put("credits", String.valueOf(credits));
									
									currentAction = new Action("fundstation",
										params, UpdateManager.UPDATE_CALLBACK);
								}
							}
						}
				}, "");
			}
		};
		this.leaveSystemHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				JSOptionPane.showInputDialog("Confirmez votre mot de passe " +
					"pour abandonner le système :", "Abandon système",
					JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if (option != null && getSelectedSystem() != null) {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("system", String.valueOf(getSelectedSystem().getId()));
								params.put("password", (String) option);
								 
								currentAction = new Action("systems/leave", params,
									UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
							}
						}
				}, "", true);
			}
		};
		this.cancelColonizationHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				PlayerFleetData fleet = getFirstSelectedFleet();
				if (fleet == null)
					return;
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("fleet", String.valueOf(fleet.getId()));
				
				currentAction = new Action("cancelcolonization", params,
					UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
			}
		};
		this.buildStructure = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				Client.getInstance().getActionManager().setAction(
					ActionManager.ACTION_SETUP_STRUCTURE, (x + y * 5));
			}
		};
		this.buildStructureNext = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				x=x+10;
				Client.getInstance().getActionManager().setAction(
					ActionManager.ACTION_SETUP_STRUCTURE, (x + y * 5));
			}
		};
		// TODO bloque l'amélioration si recherche pas faite
		this.upgradeModuleHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				StructureData structure = getSelectedStructure();
				if (structure == null)
					return;
				
				int moduleType;
				if (currentView == VIEW_STRUCTURE_DECKS)
					moduleType = StructureModuleData.TYPE_DECK_FIGHTER + x;
				else
					moduleType = StructureData.getValidModules(structure.getType())[x];
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("structure", String.valueOf(structure.getId()));
				params.put("type", String.valueOf(moduleType));
				
				currentAction = new Action("structure/upgrademodule", params,
					UpdateManager.UPDATE_CALLBACK);
			}
		};
		this.destroyStructureHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				JSOptionPane.showMessageDialog("Voulez-vous enclencher " +
					"l'auto-destruction de la structure ?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.YES_OPTION) {
							StructureData structure = getSelectedStructure();
							
							if (structure != null) {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("structure", String.valueOf(structure.getId()));
								
								currentAction = new Action("structure/destroy", params,
									UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
							}
						}
					}
				});
			}
		};
		this.attackStructureHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				JSOptionPane.showMessageDialog("Voulez-vous bombarder la " +
					"structure ?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.YES_OPTION) {
								PlayerFleetData fleet = getFirstSelectedFleet();
								
								if (fleet != null) {
									HashMap<String, String> params = new HashMap<String, String>();
									params.put("fleet", String.valueOf(fleet.getId()));
									
									currentAction = new Action("structure/attack", params,
										UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
								}
							}
						}
				});
			}
		};
		this.castStructureSkillHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				StructureData structure = getSelectedStructure();
				
				switch (structure.getSkillAt(x).getType()) {
				case StructureSkillData.TYPE_STASIS:
					Client.getInstance().getActionManager(
						).setAction(ActionManager.ACTION_STASIS);
					break;
				}
			}
		};
		this.dismountStructureHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				PlayerFleetData fleetData = getFirstSelectedFleet();
				StructureData overStructure = getStructureUnderFleet(fleetData);
				
				if (overStructure != null) {
					JSOptionPane.showMessageDialog("Voulez-vous démonter la " +
						"structure pour " + Formatter.formatNumber(overStructure.getDismountCost(), true) +
						"&nbsp;" + Utilities.getCreditsImage() + " ?", "Confirmation",
						JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
						JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
							public void optionSelected(Object option) {
								if ((Integer) option == JSOptionPane.YES_OPTION) {
									PlayerFleetData fleet = getFirstSelectedFleet();
									
									if (fleet != null) {
										HashMap<String, String> params = new HashMap<String, String>();
										params.put("fleet", String.valueOf(fleet.getId()));
										
										currentAction = new Action("structure/dismount", params,
											UpdateManager.UNSELECT_AND_UPDATE_CALLBACK);
									}
								}
							}
					});
				}
			}
		};
		this.mountStructureHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				PlayerFleetData fleetData = getFirstSelectedFleet();
				int index = 0;
				
				for (int i = 0; i < fleetData.getItemsCount(); i++)
					if (fleetData.getItemAt(i).getType() == ItemInfoData.TYPE_STRUCTURE) {
						if (index == x) {
							Client.getInstance().getActionManager().setAction(
								ActionManager.ACTION_MOUNT_STRUCTURE, i);
							break;
						}
						index++;
					}
				
			}
		};
		this.showFleetStructureItems = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				showFleetStructureItems();
			}
		};
		this.deactivateStructureHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				JSOptionPane.showMessageDialog("Voulez-vous désactiver " +
					"la structure ?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.YES_OPTION) {
							StructureData structure = getSelectedStructure();
							
							if (structure != null) {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("structure", String.valueOf(structure.getId()));
								params.put("activate", "false");
								
								currentAction = new Action("structure/activate", params,
									UpdateManager.UPDATE_CALLBACK);
							}
						}
					}
				});
				
			}
		};
		this.activateStructureHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				JSOptionPane.showMessageDialog("Voulez-vous activer " +
					"la structure ?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.YES_OPTION) {
							StructureData structure = getSelectedStructure();
							
							if (structure != null) {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("structure", String.valueOf(structure.getId()));
								params.put("activate", "true");
								
								currentAction = new Action("structure/activate", params,
									UpdateManager.UPDATE_CALLBACK);
							}
						}
					}
				});
			}
		};
		this.repairHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				if (currentAction != null && currentAction.isPending())
					return;
				
				JSOptionPane.showMessageDialog("Voulez-vous réparer " +
					"la structure ?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.YES_OPTION) {
							PlayerFleetData fleetData = getFirstSelectedFleet();
							
							if (fleetData != null) {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("fleet", String.valueOf(fleetData.getId()));
								
								currentAction = new Action("structure/repair", params,
									UpdateManager.UPDATE_CALLBACK);
							}
						}
					}
				});
			}
		};
		this.buyStructureFleetHandler = new ControlHandler() {
			public void actionPerformed(int x, int y, int button) {
				JSOptionPane.showMessageDialog("Voulez-vous acheter une " +
					"flotte pour " + getFleetCost(Client.getInstance().getProductsManager()) + "&nbsp;" +
					"<img class=\"resource credits\" src=\"" +
					Config.getMediaUrl() + "images/misc/blank.gif\" " +
					"unselectable=\"on\"/>&nbsp;?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.YES_OPTION) {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("structure", String.valueOf(getSelectedStructure().getId()));
								
								new Action("structure/buyfleet", params, UpdateManager.UPDATE_CALLBACK);
							}
						/*	if ((Integer) option == JSOptionPane.YES_OPTION) {
								StructureData structureData = getSelectedStructure();
								
								Client.getInstance().getSwapDialog().show(new GroupData[]{
									new GroupData(structureData, Client.getInstance(
										).getAreaContainer().getLastAreaUpdate()),
									new GroupData(Client.getInstance().getProgressBar().getPlayerLevel())
								}, SwapDialog.MODE_BUY_FLEET_ON_SPACESHIP_YARD);
							}*/
						}
				});
			}
		};
		
		sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
		
		EventManager.addEventHook(this);
		SelectionManager.getInstance().addSelectionListener(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void updateControls() {
		switch (currentView) {
		case VIEW_FLEET_CONTENT:
			showFleetContent();
			break;
		case VIEW_FLEET_CONTROLS:
			showFleetControls();
			break;
		case VIEW_FLEET_SKILLS_SELECTION:
			showFleetSkillsSelection(currentTab);
			break;
		case VIEW_FLEET_TAG_SELECTION:
			showFleetTagSelection();
			break;
		case VIEW_FLEET_ENGINEER_WARDS:
			showFleetEngineerStructures();
			break;
		case VIEW_FLEET_PYROTECHNIST_WARDS:
			showFleetPyrotechnistStructures();
			break;
		case VIEW_SYSTEM_BUILDINGS_CONTENT:
			showSystemBuildingsContent(currentTab);
			break;
		case VIEW_SYSTEM_CONTROLS:
			showSystemControls();
			break;
		case VIEW_SYSTEM_SHIPS_CONTENT:
			showSystemShipsContent();
			break;
		case VIEW_SPACE_STATION_CONTROLS:
			showSpaceStationControls();
			break;
		case VIEW_STRUCTURE_CONTROLS:
			showStructureControls();
			break;
		case VIEW_STRUCTURE_SHIPS_CONTENT:
			showStructureShipsContent();
			break;
		case VIEW_STRUCTURE_DECKS:
			showStructureDecks();
		}
	}
	
	public void selectionChanged(Selection newSelection, Selection oldSelection) {
		if (oldSelection.getType() != Selection.TYPE_NONE) {
			clearControls();
			
			this.currentView = NO_VIEW;
			
			for (TimerHandler updater : localUpdaters) {
				TimerManager.unregister(updater);
				updater.destroy();
			}
			localUpdaters.clear();
			
			if (canvasVisible) {
				Client.getInstance().getAreaContainer().hideCanvas();
				canvasVisible = false;
			}
			
			if (newSelection.getType() != oldSelection.getType()) {
				Client.getInstance().getSelectionInfo().setVisible(false);
			}
		}
		
		switch (newSelection.getType()) {
		case Selection.TYPE_FLEET:
			// Sélection d'une ou plusieurs flottes
			showFleetControls();
			break;
		case Selection.TYPE_SYSTEM:
			// Sélection d'un système
			showSystemControls();
			break;
		case Selection.TYPE_SPACE_STATION:
			// Selection d'une station spatiale
			showSpaceStationControls();
			break;
		case Selection.TYPE_STRUCTURE:
			// Sélection d'une structure
			showStructureControls();
			break;
		}
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		
		switch (event.getTypeInt()) {
		case Event.ONMOUSEOVER:
			if (currentView == VIEW_FLEET_CONTROLS) {
				PlayerFleetData[] fleetsData = getSelectedFleets();
				if (fleetsData.length != 1)
					return;
				
				for (int i = 0; i < fleetsData[0].getSkillsCount(); i++)
					if (fleetsData[0].getSkillAt(i).getType() == 1 ||
							fleetsData[0].getSkillAt(i).getType() == 2) {
						FlowPanel control = controls[i][1];
						if (event.getFromElement() == null ||
								control.getElement().isOrHasChild(event.getFromElement()))
							continue;
						
						if (control.getElement().isOrHasChild(event.getTarget())) {
							canvasVisible = true;
							Client.getInstance().getAreaContainer().drawCircle(
								fleetsData[0].getX(), fleetsData[0].getY(),
								fleetsData[0].getSkillAt(i).getType() == 1 ?
								SkillData.SPY_RANGE : SkillData.TRACKER_RANGE, "#3270b1");
						}
					}
			}
			break;
		case Event.ONMOUSEOUT:
			if (currentView == VIEW_FLEET_CONTROLS) {
				PlayerFleetData[] fleetsData = getSelectedFleets();
				if (fleetsData.length != 1)
					return;
				
				for (int i = 0; i < fleetsData[0].getSkillsCount(); i++)
					if (fleetsData[0].getSkillAt(i).getType() == 1 ||
							fleetsData[0].getSkillAt(i).getType() == 2) {
						FlowPanel control = controls[i][1];
						if (event.getToElement() == null ||
								control.getElement().isOrHasChild(event.getToElement()))
							continue;
						
						if (control.getElement().isOrHasChild(event.getTarget())) {
							Client.getInstance().getAreaContainer().hideCanvas();
							canvasVisible = false;
						}
					}
			}
			break;
		}
	}
	
	public boolean onEventPreview(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONKEYDOWN:
			Element target = DOM.eventGetTarget(event);
			
			int keyCode = event.getKeyCode();
			
			if ((currentAction != null && currentAction.isPending()) ||
					(target != null && DOM.getElementProperty(
						target, "nodeName").toLowerCase().equals("input"))) //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			
			// combo de touche pour reset le theme CTRL/META/SHIFT + G
			if(keyCode == 71 ){
				
				
				if (event.getCtrlKey() || event.getMetaKey() || event.getShiftKey()) {
					new Action("killpolling", Action.NO_PARAMETERS, new ActionCallback() {
						
					public void onSuccess(AnswerData data) {

						JSOptionPane.showMessageDialog(
								"Voulez vous vraiment avoir le thème de base?", "Confirmation",
								JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
								JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
									public void optionSelected(Object option) {
										if ((Integer) option == JSOptionPane.OK_OPTION) {
											Cookies.setCookie("theme",
													Config.getMediaUrl()+"style/FallenCraft2Red",
													new Date(1000l * Utilities.getCurrentTime() + 31536000000l));
											HashMap<String, String> params = new HashMap<String, String>();
											params.put("grid", String.valueOf(Settings.isGridVisible()));
											params.put("brightness", String.valueOf(Settings.getBrightness()));
											params.put("fleetsSkin", String.valueOf(Settings.getFleetsSkin()));
											params.put("generalvol", String.valueOf(SoundManager.getInstance().getGeneralVolume()));
											params.put("musicvol", String.valueOf(SoundManager.getInstance().getMusicVolume()));
											params.put("soundvol", String.valueOf(SoundManager.getInstance().getSoundVolume()));
											params.put("graphics", String.valueOf(0));
											params.put("censorship", String.valueOf(Settings.isCensorshipActive()));
											params.put("optimConnect", String.valueOf(Settings.isConnectionOptimized()));
											
											params.put("theme", Config.getMediaUrl()+"style/FallenCraft2Red");
											
											currentAction = new Action("setsettings", params, UpdateManager.UPDATE_CALLBACK);
											Window.Location.reload();
													

										}
									}
								}
								);
				}
					

						public void onFailure(String error) {
							ActionCallbackAdapter.onFailureDefaultBehavior(error);
						}
					
				});
					event.preventDefault();
					event.cancelBubble(true);
			}
				
			}
			else
			if ((keyCode >= 48 && keyCode <= 57) ||
					(keyCode >= 96 && keyCode <= 105)) {
				int shortcut = keyCode - (keyCode <= 57 ? 48 : 96);
				
				event.cancelBubble(true);
				event.preventDefault();
				
				if (event.getCtrlKey() || event.getMetaKey() || event.getShiftKey()) {
					// Affectation de raccourci
					PlayerFleetData[] fleetsData = getSelectedFleets();
					PlayerStarSystemData systemData = getSelectedSystem();
					StructureData structureData = getSelectedStructure();
					PlayerGeneratorData generatorData = null;
					
		
					if(structureData!=null)
					if (structureData.getType() == StructureData.TYPE_GENERATOR)
						generatorData = Client.getInstance().getEmpireView(
							).getGeneratorById(structureData.getId());
					

					if ((fleetsData == null || fleetsData.length != 1) &&
							systemData == null && generatorData == null)
						return true;
					

					
					if (fleetsData != null && fleetsData.length == 1 && fleetsData[0].getShortcut() == shortcut)
						shortcut = -1;
					if (systemData != null && systemData.getShortcut() == shortcut)
						shortcut = -1;
					if (generatorData != null && generatorData.getShortcut() == shortcut)
						shortcut = -1;
					

					
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("shortcut", String.valueOf(shortcut));
					
					if (fleetsData != null && fleetsData.length == 1) {
						params.put("id", String.valueOf(fleetsData[0].getId()));
						params.put("type", "fleet");
					} else if (systemData != null) {
						params.put("id", String.valueOf(systemData.getId()));
						params.put("type", "system");
					} else if (generatorData != null) {
						params.put("id", String.valueOf(generatorData.getId()));
						params.put("type", "structure");
					}
					
					currentAction = new Action("setshortcut", params,
						UpdateManager.UPDATE_CALLBACK);
				} else {
					ArrayList<PlayerFleetData> fleets =
						Client.getInstance().getEmpireView().getFleets();
					
					for (PlayerFleetData fleet : fleets)
						if (fleet.getShortcut() == shortcut) {
							int idArea = Client.getInstance().getAreaContainer().getArea().getId();
							
							Client.getInstance().getAreaContainer().setIdArea(
								fleet.getArea().getId(), new Point(fleet.getX(), fleet.getY()));
							
							if (idArea == fleet.getArea().getId())
								SelectionManager.getInstance().selectFleet(fleet.getId());
							return true;
						}
					
					ArrayList<PlayerStarSystemData> systems =
						Client.getInstance().getEmpireView().getSystems();
					
					for (PlayerStarSystemData system : systems)
						if (system.getShortcut() == shortcut) {
							int idArea = Client.getInstance().getAreaContainer().getArea().getId();
							
							Client.getInstance().getAreaContainer().setIdArea(
								system.getArea().getId(), new Point(system.getX(), system.getY()));
							
							if (idArea == system.getArea().getId())
								SelectionManager.getInstance().selectSystem(system.getId());
							return true;
						}
					
					ArrayList<PlayerGeneratorData> generators =
						Client.getInstance().getEmpireView().getGenerators();
					
					for (PlayerGeneratorData generator : generators)
						if (generator.getShortcut() == shortcut) {
							int idArea = Client.getInstance().getAreaContainer().getArea().getId();
							
							Client.getInstance().getAreaContainer().setIdArea(
								generator.getArea().getId(), new Point(generator.getX(), generator.getY()));
							
							if (idArea == generator.getArea().getId())
								SelectionManager.getInstance().selectStructure(generator.getId());
							return true;
						}
				}
			}
			break;
		}
		
		return true;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private int getSkillByPosition(PlayerFleetData fleetData, int x) {
		int count = 0;
		for (int i = 0; i < fleetData.getSkillsCount(); i++) {
			if (fleetData.getSkillAt(i).getType() != 0) {
				if (count == x)
					return i;
				count++;
			}
		}
		return -1;
	}
	
	private boolean isSkillReloaded(PlayerFleetData fleetData, int index) {
		return fleetData != null && index != -1 && Math.max(0, fleetData.getSkillAt(
			index).getReloadRemainingTime() - Utilities.getCurrentTime() +
			Client.getInstance().getEmpireView().getLastFleetUpdate(fleetData.getId())) == 0;
	}
	
	private PlayerFleetData getFirstSelectedFleet() {
		long[] idSelectedFleets = SelectionManager.getInstance().getIdSelectedFleets();
		if (idSelectedFleets.length == 0)
			return null;
		else
			return Client.getInstance().getEmpireView().getFleetById((int) idSelectedFleets[0]);
	}
	
	private PlayerFleetData[] getSelectedFleets() {
		return SelectionTools.getSelectedFleets();
	}
	
	private PlayerStarSystemData getSelectedSystem() {
		return SelectionTools.getSelectedSystem();
	}
	
	private StructureData getSelectedStructure() {
		return SelectionTools.getSelectedStructure();
	}
	
	private SpaceStationData getSelectedSpaceStation() {
		return SelectionTools.getSelectedSpaceStation();
	}
	
	private StructureData getStructureUnderFleet(PlayerFleetData fleetData) {
		IndexedAreaData areaData = Client.getInstance().getAreaContainer().getArea();
		
		for (int i = 0; i < areaData.getStructuresCount(); i++) {
			StructureData structureData = areaData.getStructureAt(i);
			Dimension size = structureData.getSize();
			
			if (fleetData.getX() >= structureData.getX() - size.getWidth() / 2 &&
					fleetData.getX() < structureData.getX() - size.getWidth() / 2 + size.getWidth() &&
					fleetData.getY() >= structureData.getY() - size.getHeight() / 2 &&
					fleetData.getY() < structureData.getY() - size.getHeight() / 2 + size.getHeight()) {
				return structureData;
			}
		}
		
		return null;
	}
	
	
	private StarSystemData getSystemUnderFleet(PlayerFleetData fleetData) {
		IndexedAreaData areaData = Client.getInstance().getAreaContainer().getArea();
		
		for (int i = 0; i < areaData.getSystemsCount(); i++) {
			StarSystemData systemData = areaData.getSystemAt(i);
			
			int dx = systemData.getX() - fleetData.getX();
	    	int dy = systemData.getY() - fleetData.getY();
			
			if (dx * dx + dy * dy <= 5*5) {
				return systemData;
			}
		}
		
		return null;
	}
	
	private void showFleetControls() {
		PlayerFleetData[] fleetsData = getSelectedFleets();
		
		this.currentView = VIEW_FLEET_CONTROLS;
		clearControls();
		
		for (TimerHandler updater : localUpdaters) {
			TimerManager.unregister(updater);
			updater.destroy();
		}
		localUpdaters.clear();
		
		int fleetLevel = fleetsData[0].getFleetLevel();
		
		StaticMessages messages = GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		// Détermine si la flotte est suffisament proche d'une porte de saut
		IndexedAreaData areaData = Client.getInstance().getAreaContainer().getArea();
		
		boolean overTradeCenter = false;
		if (fleetsData.length == 1) {
			for (int i = 0; i < areaData.getTradeCentersCount(); i++) {
				int dx = areaData.getTradeCenterAt(i).getX() - fleetsData[0].getX();
				int dy = areaData.getTradeCenterAt(i).getY() - fleetsData[0].getY();
				
				if (dx * dx + dy * dy < TradeCenterData.TRADE_CENTER_RADIUS *
						TradeCenterData.TRADE_CENTER_RADIUS) {
					overTradeCenter = true;
					break;
				}
			}
		}
		
		boolean overBank = false;
		if (fleetsData.length == 1) {
			for (int i = 0; i < areaData.getBanksCount(); i++) {
				int dx = areaData.getBankAt(i).getX() - fleetsData[0].getX();
				int dy = areaData.getBankAt(i).getY() - fleetsData[0].getY();
				
				if (dx * dx + dy * dy < BankData.BANK_RADIUS *
						BankData.BANK_RADIUS) {
					overBank = true;
					break;
				}
			}
		}
		
		boolean overLottery = false;
		if (fleetsData.length == 1) {
			Utilities.log(">" + areaData.getLotterysCount());
			for (int i = 0; i < areaData.getLotterysCount(); i++) {
				int dx = areaData.getLotteryAt(i).getX() - fleetsData[0].getX();
				int dy = areaData.getLotteryAt(i).getY() - fleetsData[0].getY();
				Utilities.log(dx + " " + dy);
				if (dx * dx + dy * dy < LotteryData.LOTTERY_RADIUS *
						LotteryData.LOTTERY_RADIUS) {
					overLottery = true;
					break;
				}
			}
		}
		
		boolean pirate = false;
		if (fleetsData.length == 1) {
			for (int i = 0; i < fleetsData[0].getSkillsCount(); i++)
				if (fleetsData[0].getSkillAt(i).getType() == 10) {
					pirate = true;
					break;
				}
		}
		
		boolean overWard = false;
		if (fleetsData.length == 1) {
			for (int i = 0; i < areaData.getWardsCount(); i++) {
				WardData wardData = areaData.getWardAt(i);
				
				if (wardData.getX() == fleetsData[0].getX() &&
						wardData.getY() == fleetsData[0].getY() && (
						(wardData.getType().startsWith(WardData.TYPE_OBSERVER) ||
						wardData.getType().startsWith(WardData.TYPE_SENTRY) ||
						wardData.getTreaty().equals("player")) &&
						(wardData.getTreaty().equals("player") ||
						wardData.getTreaty().equals("enemy") || (
						wardData.getTreaty().equals("neutral") && pirate)))) {
					overWard = true;
					break;
				}
			}
		}
		
		SpaceStationData overSpaceStation = null;
		if (fleetsData.length == 1) {
			for (int i = 0; i < areaData.getSpaceStationsCount(); i++) {
				SpaceStationData spaceStationData = areaData.getSpaceStationAt(i);
				int dx = spaceStationData.getX() - fleetsData[0].getX();
				int dy = spaceStationData.getY() - fleetsData[0].getY();
				double radius = SpaceStationData.SPACE_STATION_RADIUS;
				
				if (dx * dx + dy * dy <= radius * radius) {
					overSpaceStation = spaceStationData;
					break;
				}
			}
		}
		
		StructureData overStructure = null;
		if (fleetsData.length == 1)
			overStructure = getStructureUnderFleet(fleetsData[0]);
		
		DoodadData overDoodad = null;
		if (fleetsData.length == 1) {
			for (int i = 0; i < areaData.getDoodadsCount(); i++) {
				DoodadData doodadData = areaData.getDoodadAt(i);
				
				if (doodadData.getX() == fleetsData[0].getX() &&
						doodadData.getY() == fleetsData[0].getY()) {
					overDoodad = doodadData;
					break;
				}
			}
		}
		
		int lastUpdate = (int) (Utilities.getCurrentTime() -
			Client.getInstance().getEmpireView().getLastFleetUpdate(fleetsData[0].getId()));
		
		// Boutons d'action
		int index = 0;
		if (fleetsData.length == 1) {
			setControl(index++, 0, "content", "", showFleetContentHandler,
			"<div class=\"title\">Contenu de la flotte</div><div class=\"justify\">Affiche les vaisseaux et ressources contenus dans la flotte.</div>");
		}
		
		// Tactique
		if (fleetsData.length == 1 && !fleetsData[0].isDelude()) {
			setControl(index++, 0, "tactics", "", tacticsHandler,
				"<div class=\"title\">Tactique</div><div class=\"justify\">Définissez quelles actions vos vaisseaux doivent entreprendre pendant les combats.</div>");
		}
	
		boolean showJump = true, showCancelJump = true;
		
		for (PlayerFleetData fleetData : fleetsData) {
			int startJumpRemainingTime = Math.max(0,
				fleetData.getStartJumpRemainingTime() - lastUpdate);
			int endJumpRemainingTime = Math.max(0,
				fleetData.getEndJumpRemainingTime() - lastUpdate);
			boolean inHyperspace = startJumpRemainingTime > 0 ||
				endJumpRemainingTime > 0;
			boolean nearHypergate = false;
			
			for (int i = 0; i < areaData.getGatesCount(); i++) {
				int dx = areaData.getGateAt(i).getX() - fleetData.getX();
				int dy = areaData.getGateAt(i).getY() - fleetData.getY();
				
				if (dx * dx + dy * dy <= GateData.HYPERGATE_JUMP_RADIUS *
						GateData.HYPERGATE_JUMP_RADIUS) {
					nearHypergate = true;
					break;
				}
			}
			
			// Saut hyperspatial
			if (!inHyperspace || startJumpRemainingTime <= 0)
				showCancelJump = false;
			if (fleetData.isDelude() || !nearHypergate || inHyperspace)
				showJump = false;
		}
		
		// Boutons pour sauter / annuler un saut en hyperespace
		if (showJump)
			setControl(index++, 0, "hyperspace", "", hyperspaceHandler,
				"<div class=\"title\">Saut hyperspatial</div><div class=\"justify\">Voyagez vers un secteur proche en hyperespace.<br/>La flotte droit être à proximité d'un relai hyperspatial pour pouvoir sauter.</div>");
		if (showCancelJump)
			setControl(index++, 0, "cancelHyperspace", "", cancelHyperspaceHandler,
				"<div class=\"title\">Annuler saut hyperspatial</div><div class=\"justify\">Annule le voyage hyperspatial vers un autre secteur.</div>");
		
		if (fleetsData.length == 1 && !fleetsData[0].isDelude()) {
			// Transfert de vaisseaux avec un système
			if (fleetsData[0].isOverSystem() && fleetsData[0].getSystemTreaty().equals("player"))
				setControl(index++, 0, "transfer", "", transferHandler,
					"<div class=\"title\">Transfert</div><div class=\"justify\">Transfert de vaisseaux / ressources entre la flotte et un système.<br>La flotte doit être stationnée sur l'un de vos systèmes.</div>");
			
			
			
			// Détruire une structure
			if (overWard)
				setControl(index++, 0, "destroyWard", "", destroyWardHandler,
					"<div class=\"title\">Saboter structure</div><div class=\"justify\">Détruit la structure sur laquelle la flotte se trouve.</div><div class=\"emphasize\"><img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"clock\"/> 1h</div>");
			
			// Endommager une station spatiale
			if (overSpaceStation != null) {
				AllyDialog allyDialog = Client.getInstance().getAllyDialog();
				
				if (overSpaceStation.getTreaty().equals("ally"))
					setControl(index++, 0, "spaceStation", "", swapSpaceStationHandler,
						"<div class=\"title\">Transfert</div><div class=\"justify\">Transfert de ressources vers la station spatiale. Une fois transférées, les ressources ne peuvent plus être récupérées.</div>");
				if ((overSpaceStation.getTreaty().equals("ally") &&
						allyDialog.getPlayerRank() >=
						allyDialog.getAlly().getRequiredRank(AllyData.RIGHT_MANAGE_STATIONS)) ||
						overSpaceStation.getTreaty().equals("enemy") || (
						overSpaceStation.getTreaty().equals("neutral") && pirate))
					setControl(index++, 0, "destroyWard", "", destroySpaceStationHandler,
						"<div class=\"title\">Saboter station spatiale</div><div class=\"justify\">" + (overSpaceStation.getTreaty().equals("ally") ? "Détruit" : "Endommage") + " la station spatiale sur laquelle la flotte se trouve.</div><div class=\"emphasize\"><img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"clock\"/> 2h</div>");
			}
			
			// Actions sur les structures
			if (overStructure != null) {
				if (overStructure.getTreaty().equals("player")) {
					switch (overStructure.getType()) {
					case StructureData.TYPE_STOREHOUSE:
						setControl(index++, 0, "default", "<div class=\"structure\" " +
							"style=\"background-position: -" + (overStructure.getType() * 50) +
							"px -100px;\"></div>", swapStructureHandler,
							"<div class=\"title\">Transfert ressources</div>" +
							"<div class=\"justify\">Déposez ou retirez des " +
							"ressources sur le silo. Les ressources que vous " +
							"déposez dans le silo peuvent être utilisées " +
							"sur toutes vos structures dans le secteur.</div>");
						break;
					case StructureData.TYPE_SPACESHIP_YARD:
						setControl(index++, 0, "default", "<div class=\"structure\" " +
							"style=\"background-position: -" + (overStructure.getType() * 50) +
							"px -100px;\"></div>", swapStructureHandler,
							"<div class=\"title\">Transfert vaisseaux</div>" +
							"<div class=\"justify\">Déposez ou retirez des " +
							"vaisseaux sur le chantier spatial.</div>");
						setControl(index++, 0, "reloading", "<div class=\"reloading\" " +
								"style=\"background-position: -" + (overStructure.getType() * 50) +
								"px -100px;\"></div>", reloadingHandler ,
								"<div class=\"title\">Rechargement automatique</div>" +
										"<div class=\"justify\">Rechargez vos flottes avec une tactique définie</div>");
						break;
					}
					
					if (fleetsData[0].getSkillLevel(7) >= 0) {
						setControl(index++, 0, "dismount", "<div class=\"structure\" " +
							"style=\"background-position: -" + (overStructure.getType() * 50) +
							"px -100px;\"></div>", dismountStructureHandler,
							"<div class=\"title\">Démonter structure</div>" +
							"<div class=\"justify\">Démonte la structure pour " +
							"l'assembler ailleurs. La flotte doit avoir de l'espace " +
							"libre pour pouvoir transporter la structure, et la" +
							"structure doit être désactivée.</div>" +
							"<div class=\"emphasize\">" + Utilities.getCreditsImage() + " " +
							Formatter.formatNumber(overStructure.getDismountCost(), true) + " " +
							Utilities.getClockImage() + " " + (3 + overStructure.getLevel()) + "h</div>" +
							"<div class=\"emphasize\">Capacité requise : " +
							Formatter.formatNumber(overStructure.getWeight(), true) + "</div>");
					}
					if (fleetsData[0].getMovementReloadRemainingTime() > 100)
						setControl(index++, 0, "cancel","<div class=\"structure\" " +
								"style=\"background-position: -" + (overStructure.getType() * 50) +
								"px -100px;\"></div>" , cancelDismountHandler,
							"<div class=\"title\">Annuler démontage</div><div class=\"justify\">Annule le démontage de la structure.</div>");
					
				} else if (overStructure.getTreaty().equals("enemy") ||
						(overStructure.getTreaty().equals("neutral") && pirate)) {
					setControl(index++, 0, "destroyWard", "", attackStructureHandler,
						"<div class=\"title\">Bombarder structure</div>" +
						"<div class=\"justify\">Endommage la structure sur " +
						"laquelle votre flotte se trouve. Le bombardement " +
						"immobilise votre flotte 4h. Au bout des 4h, la " +
						"structure est endommagée d'autant de points de " +
						"structure que la puissance de votre flotte. Si " +
						"votre flotte est détruite avant la fin des 4h, " +
						"la structure n'est pas endommagée.</div>" +
						"<div class=\"emphasize\"><img src=\"" +
						Config.getMediaUrl() + "images/misc/blank.gif\" " +
						"class=\"clock\"/> 4h</div>");
				}
			}
			
			// Colonisation / capture
			if (fleetsData[0].isOverSystem() && (fleetsData[0].getSystemTreaty().equals("enemy") ||
					fleetsData[0].getSystemTreaty().equals("uninhabited")) &&
					fleetsData[0].getColonizationRemainingTime() == 0)
				setControl(index++, 0, "colonize", "", colonizeHandler,
					"<div class=\"title\">Colonisation / Capture</div><div class=\"justify\">Colonise un système ou capture un système ennemi.<br>Pour coloniser, la flotte doit être stationnée sur un système inoccupé et vous devez avoir suffisament de points de colonisation.<br/>Pour capturer, la flotte doit être stationnée sur un système ennemi.</div>");
			
			if (fleetsData[0].getColonizationRemainingTime() > 0)
				setControl(index++, 0, "cancelColonization", "", cancelColonizationHandler,
					"<div class=\"title\">Annuler colonisation</div><div class=\"justify\">Annule la colonisation en cours. Les points de colonisation utilisés sont restitués.</div>");
			
			// Migration
			if (fleetsData[0].isOverSystem() && (fleetsData[0].getSystemTreaty().equals("uninhabited")) && (fleetsData[0].isMigrating()==false))
				setControl(index++, 0, "migration", "", migrationHandler,
					"<div class=\"title\">Migration</div><div class=\"justify\">Migre un système.<br/>Un de vos systèmes sera déplacé à cet endroit précis.<br/> Vous risquez de perdre des bâtiments si la configuration du système ciblé ne permet pas de tout déplacer.<br/>Les bâtiments et vaisseaux en cours de production seront perdus.</div>");
			
			// Anulation de la migration
			if (fleetsData[0].isOverSystem() && (fleetsData[0].isMigrating()==true))
				setControl(index++, 0, "cancelMigration", "", cancelMigrationHandler,
					"<div class=\"title\">Annuler la Migration</div><div class=\"justify\">Annule la migration en cours. Les crédits utilisés sont restitués.</div>");
			
			// Commerce
			if (overTradeCenter)
				setControl(index++, 0, "trade", "", tradeHandler,
					"<div class=\"title\">Commerce</div><div class=\"justify\">Achat / vente de ressources.</div>");
			
			// Banque
			if (overBank)
				setControl(index++, 0, "bank", "", bankHandler,
					"<div class=\"title\">Banque</div><div class=\"justify\">Déposez vos ressources dans la banque pour gagner des intérêts.</div>");
			
			// Loterie
						if (overLottery)
							setControl(index++, 0, "lottery", "", lotteryHandler,
								"<div class=\"title\">Loterie</div><div class=\"justify\">Laissez-vous tenter par l'Eden : jouez et gagnez des cadeaux divins !</div>");
			
			if (overDoodad != null)
				setControl(index++, 0, "default", "<div class=\"doodad\" style=\"background-position: -" + (overDoodad.getType() * 60 + 5) + "px -446px;\"></div>", pickUpDoodadHandler, "<div class=\"title\">Examiner le bidule</div><div class=\"justify\">Analyser l'objet inconnu sur lequel la flotte se trouve... À vos risques et périls !</div><div class=\"emphasize\">" + messages.premium() + "</div>");
			
		}
		
		
		// Rechargement automatique en vaisseaux	
		for (int i = 0; i < 9; i++)
		if (fleetsData.length == i && !fleetsData[0].isDelude() && fleetsData[0].isOverSystem() 
		    && fleetsData[0].getSystemTreaty().equals("player")) {
			setControl(index++, 0, "reloading", "", reloadingHandler,
				"<div class=\"title\">Réapprovisionnement</div><div class=\"justify\">Rechargez vos flottes automatiquement avec une tactique définie.</div>");
		}
		
		
		setControl(5, 0, "default", "<div class=\"tag\" style=\"background-position: -" + (fleetsData[0].getTag() * 30) + "px -1153px;\"></div>", fleetsData[0].getTag() != 8 ? showFleetTagSelectionHandler : null,
			"<div class=\"title\">Rôle " + dynamicMessages.getString("fleetTag" + fleetsData[0].getTag()) + "</div><div class=\"justify\">Fonction de la flotte, parmi offensif, défensif, commerce, exploration, pirate, mineur, ingénieur ou artificier. Le rôle sert uniquement a mieux organiser les flottes.</div>");
		setControl(5, 1, "cancel", "", hideControlsHandler,
			"<div class=\"title\">Déselectionne la flotte</div>");
		
		// Compétences
		if (fleetsData.length == 1 && !fleetsData[0].isDelude()) {
			// Entrainement
			int maxTrainingLevel = Math.min(15, 3 + Client.getInstance(
				).getAdvancementDialog().getAdvancementLevel(
					AdvancementData.TYPE_TRAINING_MAX_LEVEL));
			if (fleetsData[0].getFleetLevel() < maxTrainingLevel) {
				setControl(4, 1, "training", "", trainHandler,
					"<div class=\"title\">Entraînement</div>" +
					"<div class=\"justify\">Augmente le niveau de la flotte de 1 pour " +
					FleetData.TRAINING_COST[fleetsData[0].getFleetLevel()] + "&nbsp;" +
					"<img class=\"resource credits\" src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" unselectable=\"on\"/>. " +
					"La flotte peut être entraînée jusqu'au niveau " +
					maxTrainingLevel + ".</div>");
			}
			
			int skillsCount = 0;
			int skillPoints = 0;
			for (int i = 0; i < fleetsData[0].getSkillsCount(); i++)
				if (fleetsData[0].getSkillAt(i).getType() != 0) {
					ControlHandler handler = null;
					int id = fleetsData[0].getSkillAt(i).getType();
					
					if (id == 1)
						handler = probeHandler;
					else if (id == 3)
						handler = bombingHandler;
					else if (id == 4)
						handler = offensiveLinkHandler;
					else if (id == 5)
						handler = defensiveLinkHandler;
					else if (id == 7)
						handler = engineerHandler;
					else if (id == 8)
						handler = pyrotechnistHandler;
					else if (id == 9)
						handler = miningHandler;
					else if (id == 12)
						handler = swapHandler;
					else if (id == 13)
						handler = stealthHandler;
					else if (id == 14)
						handler = empHandler;
					else if (id == 15)
						handler = deludeHandler;
					
					long offset = Utilities.getCurrentTime() -
						Client.getInstance().getEmpireView().getLastFleetUpdate(fleetsData[0].getId());
					
					setSkillControl(skillsCount++, 1, id,
						fleetsData[0].getSkillAt(i).getLevel(), true,
						Math.max(0, fleetsData[0].getSkillAt(i).getReloadRemainingTime() - offset),
						(long) fleetsData[0].getSkillAt(i).getLastUseTime() + offset, handler);
					skillPoints += fleetsData[0].getSkillAt(i).getLevel() + 1;
				}
			if (skillPoints < fleetLevel) {
				int points = (fleetLevel - skillPoints);
				String content = "<div class=\"skillLevel\">" + points + "</div>" +
					"<div class=\"skillPoint\"></div>";
				String tooltip = "<div class=\"title\">" + points + " point" +
					(points > 1 ? "s" : "") + " de compétence disponible" +
					(points > 1 ? "s" : "") + "</div><div class=\"justify\">" +
					"Cliquer pour " + (points > 1 ? "les " : "l'") + "affecter.</div>";
				
				setControl(skillsCount++, 1, "", content,
					showFleetSkillsSelection0Handler, tooltip);
			}
		}
	}
	
	private void showFleetContent() {
		int selectedX = -1, selectedY = -1;
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 2; j++)
				if (controls[i][j].getStyleName().contains("control-selected")) {
					selectedX = i;
					selectedY = j;
					break;
				}
		
		PlayerFleetData fleetData = getFirstSelectedFleet();
		
		this.currentView = VIEW_FLEET_CONTENT;
		clearControls();
		
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		int slotIndex = 0;
		for (int i = 0; i < fleetData.getSlotsCount(); i++) {
			SlotInfoData slotData = fleetData.getSlotAt(i);
			if (slotData.getId() != 0) {
				String count = Formatter.formatNumber(slotData.getCount(), true);
				
				String content = "<div class=\"spaceship\" style=\"" +
					"background-position: -" + (slotData.getId() * 50) + "px 0\">" +
					count + "</div><div class=\"selection\"></div>";
				
				setControl(slotIndex, 0, "", content, selectionHandler,
					ShipData.getDesc(slotData.getId(), (long) slotData.getCount(),
					Client.getInstance().getResearchManager(
						).getShipAvailableAbilities(slotData.getId())));
				
				if (selectedX == slotIndex && selectedY == 0)
					controls[selectedX][selectedY].addStyleName("control-selected");
					
				slotIndex++;
			}
		}
		
		int itemIndex = 0;
		for (int i = 0; i < fleetData.getItemsCount(); i++) {
			ItemInfoData item = fleetData.getItemAt(i);
			if (item.getType() != ItemInfoData.TYPE_NONE) {
				String count = Formatter.formatNumber(item.getCount(), true);
				
				switch (item.getType()) {
				case ItemInfoData.TYPE_RESOURCE:
					setControl(itemIndex, 1, "resource r" + item.getId(), count +
						"<div class=\"selection\"></div>", selectionHandler,
						"<div class=\"title\">" + Formatter.formatNumber(item.getCount()) + " " +
						dynamicMessages.getString("resource" + item.getId()) + "</div>");
					break;
				case ItemInfoData.TYPE_STUFF:
					// TODO
					break;
				case ItemInfoData.TYPE_STRUCTURE:
					setControl(itemIndex, 1, "", "<div class=\"structure\" " +
						"style=\"background-position: -" + (50 * item.getStructureType()) +
						"px -100px\">1</div><div class=\"selection\"></div>", selectionHandler,
						"<div class=\"title\">" + dynamicMessages.getString("structure" +
						item.getStructureType()) + " niv. " + item.getStructureLevel() + "</div>" +
						"<div class=\"justify\">" + dynamicMessages.getString("structure" +
						item.getStructureType() + "Desc") + "</div>");
					break;
				}
				
				if (selectedX == itemIndex && selectedY == 1)
					controls[selectedX][selectedY].addStyleName("control-selected");
				
				itemIndex++;
			}
		}
		
		setControl(5, 0, "selfdestruct", "", destroyHandler,
			"<div class=\"title\">Détruire vaisseaux / ressources</div>" +
			"<div class=\"justify\">Sélectionnez un vaisseau ou une " +
			"ressource, puis cliquez sur ce bouton pour ordonner sa " +
			"destruction. Attention, cette opération est irréversible " +
			"et vous ne récuperez pas de ressources pour la destruction " +
			"des vaisseaux !<br/><span class=\"emphasize\">Si vous " +
			"supprimez tous les vaisseaux d'une flotte, celle-ci sera " +
			"détruite.</span></div>");
		setControl(5, 1, "cancel", "", showFleetControlsHandler,
			"<div class=\"title\">Retour</div>");
	}
	
	private void showFleetSkillsSelection(int tab) {
		PlayerFleetData fleetData = getFirstSelectedFleet();
		
		this.currentView = VIEW_FLEET_SKILLS_SELECTION;
		this.currentTab = tab;
		
		clearControls();
		
		// Sauvegarde la position des compétences
		final int[] skillsList = new int[10];
		
		if (tab == 0) {
			// Compétences basiques
			int skillIndex = 0;
			for (int i = 1; i <= SkillData.BASIC_SKILLS_COUNT; i++) {
				// Recherche le niveau de la flotte dans la compétence
				int level = 0;
				for (int j = 0; j < fleetData.getSkillsCount(); j++)
					if (i == fleetData.getSkillAt(j).getType()) {
						level = fleetData.getSkillAt(j).getLevel() + 1;
						break;
					}
				
				// Affiche la compétence si la flotte ne l'a pas au niveau
				// maximal
				if (level < 4)
					setSkillControl(skillIndex % 5,
						(int) Math.floor(skillIndex / 5), i, level, false, 0, 0, new ControlHandler() {
							public void actionPerformed(int x, int y, int button) {
								PlayerFleetData fleetData = getFirstSelectedFleet();
								if (fleetData == null)
									return;
								
								HashMap<String, String> params =
									new HashMap<String, String>();
								params.put("fleet", String.valueOf(fleetData.getId()));
								params.put("skill", String.valueOf(skillsList[x + y * 5]));
								
								new Action("setfleetskill", params,
										UpdateManager.UPDATE_CALLBACK);
								showFleetControls();
							}
						});
				
				skillsList[skillIndex] = i;
				skillIndex++;
			}
			
			this.setControl(5, 0, "next", "",
				showFleetSkillsSelection1Handler,
				"<div class=\"title\">Compétences ultimes</div><div class=\"justify\">" +
				"Accessibles à partir du niveau 6.</div>");
		} else if (tab == 1) {
			// Compétences ultimes
			int skillIndex = 0;
			for (int i = 11; i <= SkillData.ULTIMATE_SKILLS_COUNT + 10; i++) {
				// Recherche le niveau de la flotte dans la compétence
				int level = 0;
				for (int j = 0; j < fleetData.getSkillsCount(); j++)
					if (i == fleetData.getSkillAt(j).getType()) {
						level = fleetData.getSkillAt(j).getLevel() + 1;
						break;
					}
				
				// Affiche la compétence si la flotte ne l'a pas au niveau
				// maximal
				if (level < 3)
					setSkillControl(skillIndex % 5,
						(int) Math.floor(skillIndex / 5), i, level, false, 0, 0, new ControlHandler() {
							public void actionPerformed(int x, int y, int button) {
								PlayerFleetData fleetData = getFirstSelectedFleet();
								if (fleetData == null)
									return;
								
								HashMap<String, String> params =
									new HashMap<String, String>();
								params.put("fleet", String.valueOf(fleetData.getId()));
								params.put("skill", String.valueOf(skillsList[x + y * 5]));
								
								new Action("setfleetskill", params,
										UpdateManager.UPDATE_CALLBACK);
								showFleetControls();
							}
						});
				
				skillsList[skillIndex] = i;
				skillIndex++;
			}
			
			setControl(5, 0, "next", "",
				showFleetSkillsSelection0Handler,
				"<div class=\"title\">Compétences basiques</div>");
		}
		
		setControl(5, 1, "cancel", "", showFleetControlsHandler,
			"<div class=\"title\">Retour</div>");
	}
	
	private void showFleetTagSelection() {
		this.currentView = VIEW_FLEET_TAG_SELECTION;
		clearControls();
		
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		for (int i = 0; i < 8; i++) {
			String content = "<div class=\"tag\" style=\"" +
				"background-position: -" + (i * 30) + "px -1153px;\"></div>";
			
			setControl(i % 5, i / 5, "default", content, new ControlHandler() {
				public void actionPerformed(int x, int y, int button) {
					PlayerFleetData[] fleetsData = getSelectedFleets();
					if (fleetsData.length == 0)
						return;
					
					HashMap<String, String> params =
						new HashMap<String, String>();
					for (int i = 0; i < fleetsData.length; i++)
						params.put("fleet" + i, String.valueOf(fleetsData[i].getId()));
					params.put("tag", String.valueOf(x + y * 5));
					
					new Action("setfleetstag", params, UpdateManager.UPDATE_CALLBACK);
					showFleetControls();
				}
			}, "<div class=\"title\">" + dynamicMessages.getString(
					"fleetTag" + i) + "</div>");
		}
		
		setControl(5, 1, "cancel", "", showFleetControlsHandler,
			"<div class=\"title\">Retour</div>");
	}
	
	
	private void showFleetEngineerStructuresSolo() {
		this.currentView = VIEW_FLEET_ENGINEER_STRUCTURE;
		clearControls();
		
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		int index = 0;
		for (int structure = 0; structure <= 9; structure++) {
			int[] cost = StructureData.getBaseCost(structure);
			
			String resources = "";
			for (int i = 0; i < cost.length; i++) {
				if (cost[i] > 0)
					resources += "<img src=\"" + Config.getMediaUrl() +
						"images/misc/blank.gif\" class=\"resource " +
						(i == 4 ? "credits" : "r" + i) + "\"/>" + "&nbsp;" +
						Formatter.formatNumber(cost[i], true) + " ";
			}
			
		
		setControl(index % 5, index / 5, "default", "<div class=\"structure\" " +
				"style=\"background-position: -" + (structure * 50) + "px -100px;\">", buildStructure,
				"<div class=\"title\">" + dynamicMessages.getString("structure" + structure) + "</div>" +
				"<div class=\"justify\">" + dynamicMessages.getString("structure" + structure + "Desc") + "</div>" +
				"<div class=\"emphasize\">" + resources +
				"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"class=\"clock\"/> 1h " + Utilities.getEnergyImage() + " " +
				StructureData.getEnergyConsumption(structure) + "</div>");
			index++;
		}
		setControl(5, 1, "cancel", "", showFleetControlsHandler,
		"<div class=\"title\">Retour</div>");
		setControl(5, 0, "next", "", engineerStructureNextHandler,
		"<div class=\"title\">Structures suivantes</div>");
	}
	
	
	private void showFleetEngineerStructuresNext() {
		this.currentView = VIEW_FLEET_ENGINEER_STRUCTURE;
		clearControls();
		
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		int index = 0;
		for (int structure = 0; structure <= 2; structure++) { // TODO
			int[] cost = StructureData.getBaseCost(structure+10);
			
			String resources = "";
			for (int i = 0; i < cost.length; i++) {
				if (cost[i] > 0)
					resources += "<img src=\"" + Config.getMediaUrl() +
						"images/misc/blank.gif\" class=\"resource " +
						(i == 4 ? "credits" : "r" + i) + "\"/>" + "&nbsp;" +
						Formatter.formatNumber(cost[i], true) + " ";
			}
			
			
		setControl(index % 5, index / 5, "default", "<div class=\"structure\" " +
				"style=\"background-position: -" + ((structure+10) * 50) + "px -100px;\">", buildStructureNext,
				"<div class=\"title\">" + dynamicMessages.getString("structure" + (structure+10)) + "</div>" +
				"<div class=\"justify\">" + dynamicMessages.getString("structure" + (structure+10) + "Desc") + "</div>" +
				"<div class=\"emphasize\">" + resources +
				"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"class=\"clock\"/> 1h " + Utilities.getEnergyImage() + " " +
				StructureData.getEnergyConsumption(structure+10) + "</div>");
			index++;
		}
		setControl(5, 1, "cancel", "", showFleetControlsHandler,
		"<div class=\"title\">Retour</div>");
		setControl(5, 0, "next", "", engineerStructureHandler,
		"<div class=\"title\">Structures précédentes</div>");
	}
	
	
	private void showFleetEngineerStructures() {
		PlayerFleetData fleetData = getFirstSelectedFleet();
		
		this.currentView = VIEW_FLEET_ENGINEER_WARDS;
		clearControls();
		
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		for (int structure = 0; structure <= 5; structure++) {
			int[] cost = StructureData.getBaseCost(structure);
			
			String resources = "";
			for (int i = 0; i < cost.length; i++) {
				if (cost[i] > 0)
					resources += "<img src=\"" + Config.getMediaUrl() +
						"images/misc/blank.gif\" class=\"resource " +
						(i == 4 ? "credits" : "r" + i) + "\"/>" + "&nbsp;" +
						Formatter.formatNumber(cost[i], true) + " ";
			}
			setControl(0,1, "buildStruct", "",
				engineerStructureHandler, "Créer une structure");
		}
		
		for (int i = 0; i < fleetData.getItemsCount(); i++)
			if (fleetData.getItemAt(i).getType() == ItemInfoData.TYPE_STRUCTURE) {
				setControl(5, 0, "mount", "", showFleetStructureItems,
					"<div class=\"title\">Assembler une structure</div>" +
					"<div class=\"justify\">Construction d'une structure " +
					"parmi celles que la flotte transporte.</div>");
				break;
			}
		
		for (int i = 0; i < fleetData.getSkillsCount(); i++) {
			if (fleetData.getSkillAt(i).getType() == 7) {
				int level = fleetData.getSkillAt(i).getLevel();
				
				String[] wardTypes = {
					WardData.TYPE_OBSERVER,
					WardData.TYPE_SENTRY,
					WardData.TYPE_OBSERVER_INVISIBLE,
					WardData.TYPE_SENTRY_INVISIBLE
				};
				String[] wardDesc = {
					"Observer", "Sentry",
					"ObserverInvisible", "SentryInvisible"
				};
				
				for (int j = 0; j <= level; j++) {
					setControl(j + 1, 0, "default", 
						"<div class=\"ward ward-" + wardTypes[j].replace("_", "-") + "\">",
						buildWardHandler, "<div class=\"title\">" +
						dynamicMessages.getString("ward" + wardDesc[j]) +
						"</div><div class=\"justify\">" +
						dynamicMessages.getString("ward" + wardDesc[j] + "Desc") +
						"</div><div class=\"emphasize\"><img src=\"" +
						Config.getMediaUrl() + "images/misc/blank.gif\" " +
						"class=\"clock\"/> 1h</div>");
				}
				break;
			}
		}
		
		String repairText = "<div class=\"title\">Réparer structure</div>" +
			"<div class=\"justify\">Répare la structure sur laquelle " +
			"votre flotte se trouve. La réparation immobilise votre " +
			"flotte 4h. Au bout des 4h, la structure est réparée de " +
			"<span class=\"emphasize\">" + (25 * (1 + fleetData.getSkillLevel(7))) +
			"%</span> de la puissance de votre flotte (minimum 1). Si " +
			"votre flotte est détruite avant la fin des 4h, la structure " +
			"n'est pas réparée.</div><div class=\"emphasize\">" +
			"<div style=\"float: right;\">4h " + Utilities.getClockImage() +
			"</div>Cliquez pour réparer la structure</div>";
		StructureData overStructure = getStructureUnderFleet(fleetData);
		
		if (overStructure == null || overStructure.getTreaty().equals("enemy") ||
				overStructure.getTreaty().equals("neutral") ||
				overStructure.getHull() >= overStructure.getMaxHull())
			setControl(0, 0, "repair disabled", "", null, repairText);
		else
			setControl(0, 0, "repair", "", repairHandler, repairText);
		
		setControl(5, 1, "cancel", "", showFleetControlsHandler,
			"<div class=\"title\">Retour</div>");
		
		Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_STRUCTURE);
	}
	
	private void showFleetStructureItems() {
		PlayerFleetData fleetData = getFirstSelectedFleet();
		
		this.currentView = VIEW_FLEET_STRUCTURE_ITEMS;
		clearControls();
		
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		int index = 0;
		for (int i = 0; i < fleetData.getItemsCount(); i++) {
			ItemInfoData item = fleetData.getItemAt(i);
			if (item.getType() == ItemInfoData.TYPE_STRUCTURE) {
				setControl(index++, 0, "mount", "<div class=\"structure\" " +
					"style=\"background-position: -" + (item.getStructureType() * 50) +
					"px -100px;\"></div>", mountStructureHandler,
					"<div class=\"title\">Assembler " + dynamicMessages.getString(
					"structure" + item.getStructureType()) + " niv. " +
					item.getStructureLevel() + "</div>" +
					"<div class=\"justify\">" + dynamicMessages.getString(
					"structure" + item.getStructureType() + "Desc") + "</div>" +
					"<div class=\"emphasize\">" + Utilities.getClockImage() + " " +
					(6 + item.getStructureLevel() / 2) + "h</div>");
			}
		}
		
		setControl(5, 1, "cancel", "", engineerHandler,
			"<div class=\"title\">Retour</div>");
	}
	
	private void showFleetPyrotechnistStructures() {
		PlayerFleetData fleetData = getFirstSelectedFleet();
		
		this.currentView = VIEW_FLEET_PYROTECHNIST_WARDS;
		clearControls();
		
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		for (int i = 0; i < fleetData.getSkillsCount(); i++) {
			if (fleetData.getSkillAt(i).getType() == 8) {
				int level = fleetData.getSkillAt(i).getLevel();
				
				String[] wardTypes = {
					WardData.TYPE_STUN,
					WardData.TYPE_MINE,
					WardData.TYPE_STUN_INVISIBLE,
					WardData.TYPE_MINE_INVISIBLE
				};
				String[] wardDesc = {
					"Stun", "Mine",
					"StunInvisible", "MineInvisible"
				};
				
				for (int j = 0; j <= level; j++) {
					setControl(j, 0, "default", 
						"<div class=\"ward ward-" + wardTypes[j].replace("_", "-") + "\">",
						buildMineHandler, "<div class=\"title\">" +
						dynamicMessages.getString("ward" + wardDesc[j]) +
						"</div><div class=\"justify\">" +
						dynamicMessages.getString("ward" + wardDesc[j] + "Desc") +
						"</div><div class=\"emphasize\"><img src=\"" +
						Config.getMediaUrl() + "images/misc/blank.gif\" " +
						"class=\"clock\"/> 3h</div>");
				}
				
				if (level > 0) {
					setControl(0, 1, "defuse", "", defuseHandler,
					"<div class=\"title\">Désamorçage</div>" +
					"<div class=\"justify\">Désamorce une charge électromagnétique ou explosive à moins de <span class=\"emphasize\">10</span> cases de distance. La puissance de la charge doit être inférieure ou égale à la puissance de votre flotte.</div>" +
					"<div class=\"emphasize\"><div style=\"float: right;\">1h <img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"clock\"/></div>Cliquez puis sélectionnez une charge</div>");
				}
				
				break;
			}
		}
		
		setControl(5, 1, "cancel", "", showFleetControlsHandler,
			"<div class=\"title\">Retour</div>");
	}
	
	private void setSkillControl(int x, int y, int skill, int level,
			boolean action, long reloadRemainingTime, long lastUseTime,
			ControlHandler handler) {
		PlayerFleetData fleetData = getFirstSelectedFleet();
		
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		StaticMessages staticMessages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		String actionDescription = "";
		if (action) {
			if (skill == 1) {
				actionDescription = "<div style=\"float: right;\">1h <img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"clock\"/></div>Cliquez pour sonder un système inoccupé.";
			} else if (skill == 3) {
				actionDescription = "Cliquez puis sélectionnez une flotte pour l'attaquer.";
			} else if (skill == 4) {
				actionDescription = "Cliquez puis sélectionnez une flotte pour activer la compétence.";
			} else if (skill == 5) {
				actionDescription = "Cliquez puis sélectionnez une flotte pour activer la compétence.";
			} else if (skill == 7) {
				actionDescription = "Cliquez pour afficher les options.";
			} else if (skill == 8) {
				actionDescription = "Cliquez pour afficher les options.";
			} else if (skill == 9) {
				int hours = (int) Math.floor(6 * Math.pow(.95, Client.getInstance().getAdvancementDialog().getAdvancementLevel(AdvancementData.TYPE_MINING_UPGRADE)));
				int mins = (int) Math.ceil(6 * 60 * Math.pow(.95, Client.getInstance().getAdvancementDialog().getAdvancementLevel(AdvancementData.TYPE_MINING_UPGRADE))) - hours * 60;
				actionDescription = "<div style=\"float: right; padding-left: 5px;\">" + hours + "h" + (mins > 0 ? mins : "") + " <img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"clock\"/></div>Cliquez pour activer la compétence.";
			} else if (skill == 12) {
				actionDescription = "<div style=\"float: right; padding-left: 5px;\">3h <img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"clock\"/></div>Cliquez puis sélectionnez une flotte pour échanger de position";
			} else if (skill == 13) {
				actionDescription = (fleetData.isStealth() ? "" : "<div style=\"float: right; padding-left: 5px;\">1h <img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"clock\"/></div>") + "Cliquez pour " + (fleetData.isStealth() ? "sortir du" : "passer en") + " mode furtif";
			} else if (skill == 14) {
				actionDescription = "<div style=\"float: right; padding-left: 5px;\">3h <img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"clock\"/></div>Cliquez puis sélectionnez une flotte pour l'immobiliser";
			} else if (skill == 15) {
				actionDescription = "<div style=\"float: right; padding-left: 5px;\">1h <img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"clock\"/></div><div style=\"float: right; padding-left: 5px; clear: both;\">" + ((fleetData.getPowerLevel() + 1) * 5000) + " <img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"resource r3\"/></div>Cliquez pour créer un leurre";
			}
		}
		
		String content = "<div class=\"skillLevel\">";
		for (int j = 0; j <= level; j++)
			content += "<img class=\"level\" src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\"/>";
		content += "</div><div class=\"skill\" style=\"background-position: -" +
				(40 * (skill > 10 ? skill - 11 : skill)) + "px -" + (skill > 10 ? 1228 : 731) + "px\"></div>";
		
		if (reloadRemainingTime > 0)
			content += "<div class=\"progress\"></div>";
		
		String tooltip = "<div class=\"title\">" +
			dynamicMessages.getString("skill" + skill) + " " +
			staticMessages.skillLevel(level + 1) + "</div><div class=\"justify\">" +
			dynamicMessages.getString("skill" + skill + "Desc" + level) + "</div>" +
			(actionDescription.length() > 0 ? "<div class=\"emphasize\">" +
			actionDescription + "</div>" : "");
		
		setControl(x, y, "", content, handler, tooltip);
		
		if (reloadRemainingTime > 0) {
			BackgroundUpdater bgUpdater = new BackgroundUpdater(
				controls[x][y].getElement().getFirstChildElement(
					).getNextSiblingElement().getNextSiblingElement(),
				new Point(0, 0),
				new Point(1600, 0),
				new Point(50, 0),
				33 / (1000. * reloadRemainingTime));
			long length = reloadRemainingTime + lastUseTime;
			bgUpdater.setCurrentOffset(
				33. * (length - reloadRemainingTime) / length);
			bgUpdater.setMode(BackgroundUpdater.MODE_SINGLE);
			localUpdaters.add(bgUpdater);
			TimerManager.register(bgUpdater);
			
			String id = ToolTipTextUpdater.generateId();
			OutlineText reloadRemainingTimeText = TextManager.getText(
				"<span id=\"" + id + "\"></span>");
			reloadRemainingTimeText.addStyleName("buildTime");
			controls[x][y].getElement().appendChild(reloadRemainingTimeText.getElement());
			
			ToolTipTimeUpdater updater = new ToolTipTimeUpdater(
					getElement(), id, reloadRemainingTime, false, true);
			localUpdaters.add(updater);
			TimerManager.register(updater, TimerManager.SECOND_UNIT);
		}
	}
	
	private void showSystemControls() {
		PlayerStarSystemData systemData = getSelectedSystem();
		
		for (TimerHandler updater : localUpdaters) {
			TimerManager.unregister(updater);
			updater.destroy();
		}
		localUpdaters.clear();
		
		currentView = VIEW_SYSTEM_CONTROLS;
		clearControls();
		
		// Bâtiments en cours de construction
		int index = 1;
		for (int i = 0; i < systemData.getBuildsCount(); i++) {
			BuildData buildData = systemData.getBuildAt(i);
			
			// Niveau du bâtiment
			String content = "<div class=\"progress\"></div>" +
				"<div class=\"buildingLevel\">";
			for (int j = 0; j <= buildData.getLevel(); j++)
				content += "<img class=\"level\" src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\"/>";
			content += "</div>";
			
			// Nom et decription du bâtiment
			String tooltip =
				"<div class=\"title\">" + BuildingData.getName(
				buildData.getType(), buildData.getLevel()) + "</div>" +
				"<div class=\"justify\">" + BuildingData.getDesc(
				buildData.getType(), buildData.getLevel(), false) + "</div>" +
				"<div class=\"emphasize\">Clic droit pour annuler la construction.</div>";
			
			setControl(index, 0, BuildingData.getClassName(
					buildData.getType()), content, buildHandler, tooltip);
			
			if (i == 0) {
				long buildTime = (long) Math.ceil(BuildingData.getBuildTime(
						buildData.getType(), buildData.getLevel()) *
						BuildingData.getProduction(BuildingData.FACTORY, systemData));
				long buildEnd = buildData.getEnd() - Utilities.getCurrentTime() +
					Client.getInstance().getEmpireView().getLastSystemsUpdate();
				
				if (buildEnd > 0) {
					BackgroundUpdater bgUpdater = new BackgroundUpdater(
						controls[index][0].getElement().getFirstChildElement(),
						new Point(0, 0),
						new Point(1600, 0),
						new Point(50, 0),
						33 / (1000. * buildTime));
					bgUpdater.setCurrentOffset(
						33. * (buildTime - buildEnd) / buildTime);
					bgUpdater.setMode(BackgroundUpdater.MODE_SINGLE);
					localUpdaters.add(bgUpdater);
					TimerManager.register(bgUpdater);
					
					String id = ToolTipTextUpdater.generateId();
					OutlineText remainingTimeText = TextManager.getText(
						"<span id=\"" + id + "\"></span>");
					remainingTimeText.addStyleName("buildTime");
					controls[index][0].getElement().appendChild(remainingTimeText.getElement());
					
					ToolTipTimeUpdater updater = new ToolTipTimeUpdater(
							getElement(), id, buildEnd, false);
					localUpdaters.add(updater);
					TimerManager.register(updater, TimerManager.SECOND_UNIT);
				}
			}
			
			index++;
		}
		
		// Vaisseaux en cours de construction
		index = 1;
		for (int i = 0; i < systemData.getBuildSlotsCount(); i++) {
			if (systemData.getBuildSlotAt(i).getId() != 0) {
				SlotInfoData slotData = systemData.getBuildSlotAt(i);
				
				String countId = ToolTipTextUpdater.generateId();
				String content = "<div class=\"progress\"></div>" +
					"<div id=\"" + countId + "\" class=\"spaceship\" style=\"" +
					"background-position: -" + (slotData.getId() * 50) + "px 0\">" +
					Formatter.formatNumber(Math.ceil(slotData.getCount()), true) + "</div>";
				
				setControl(index, 1, "", content, buildShipsSystemHandler, ShipData.getDesc(
					slotData.getId(), (long) slotData.getCount(), ShipData.HIDE_ABILITIES) +
					"<div class=\"emphasize\">Clic droit pour annuler la construction.</div>");
				
				if (index == 1) {
					double shipProduction = BuildingData.getProduction(
						BuildingData.SPACESHIP_YARD, systemData);
					double shipBuildTime = shipProduction > 0 ?
						ShipData.SHIPS[slotData.getId()].getBuildTime() /
						shipProduction : Integer.MAX_VALUE;
					
					int totalBuildTime = (int) (shipBuildTime *
						systemData.getBuildSlotOrderedAt(i));
					int remainingBuildTime = (int) (shipBuildTime *
						slotData.getCount() - Utilities.getCurrentTime() +
						Client.getInstance().getEmpireView().getLastSystemsUpdate() -
						systemData.getLastUpdate());
					
					double progress = 1 - (remainingBuildTime / (double) totalBuildTime);
					double slotsCount = (1 - progress) * systemData.getBuildSlotOrderedAt(i);
					
					if (progress < 1) {
						BackgroundUpdater bgUpdater = new BackgroundUpdater(
							controls[index][1].getElement().getFirstChildElement(),
							new Point(0, 0),
							new Point(1600, 0),
							new Point(50, 0),
							33 / (1000. * shipBuildTime));
						bgUpdater.setCurrentOffset(
							33. * (Math.ceil(slotsCount) - slotsCount));
						bgUpdater.setMode(BackgroundUpdater.MODE_LOOP);
						localUpdaters.add(bgUpdater);
						TimerManager.register(bgUpdater);
						
						String id = ToolTipTextUpdater.generateId();
						OutlineText remainingTimeText = TextManager.getText(
							"<span id=\"" + id + "\"></span>");
						remainingTimeText.addStyleName("buildTime");
						controls[index][1].getElement().appendChild(remainingTimeText.getElement());
						
						ToolTipTimeUpdater updater = new ToolTipTimeUpdater(
								getElement(), id, remainingBuildTime, false);
						localUpdaters.add(updater);
						TimerManager.register(updater, TimerManager.SECOND_UNIT);
						
						CountdownUpdater cdUpdater = new CountdownUpdater(
							countId, slotsCount, 1. / (1000 * shipBuildTime));
						localUpdaters.add(cdUpdater);
						TimerManager.register(cdUpdater);
					}
				}
				
				index++;
			}
		}
		
		if (index < 3 || (index < 4 && Settings.isPremium())) {
			// Vérifie que le système comporte un chantier spatial
			boolean spaceshipYard = false;
			for (int i = 0; i < 5; i++) {
				if (systemData.getBuildingsCount("spaceship_yard", i) > 0) {
					spaceshipYard = true;
					break;
				}
			}
			
			setControl(index++, 1, "buildShips", "",
				spaceshipYard ? buildShipsSystemHandler : null,
				"<div class=\"title\">Construire des vaisseaux</div>" +
				"<div class=\"justify\">Vos vaisseaux seront placés en " +
				"attente sur le système, prêts à être transférés sur " +
				"une flotte.</div>" + (spaceshipYard ? "" :
				"<div class=\"justify\" style=\"color: red;\">" +
				"Construisez un chantier spatial pour pouvoir " +
				"construire des vaisseaux.</div>"));
		}
		
		int buildingsCount = 0;
		for (String type : BUILDINGS_ORDER)
			for (int level = 0; level < 5; level++)
				buildingsCount += systemData.getBuildingsCount(type, level);
		
		for (int i = 0; i < systemData.getBuildsCount(); i++)
			if (systemData.getBuildAt(i).getLevel() == 0)
				buildingsCount++;
		
		// Boutons d'action
		setControl(0, 0, "systemContent", "", showSystemContent0Handler,
			"<div class=\"title\">Bâtiments construits</div><div class=\"justify\">Affiche la liste des bâtiments construits sur le système.</div>");
		if ((!Settings.isPremium() && systemData.getBuildsCount() < 2) || (Settings.isPremium() && systemData.getBuildsCount() < 3))
			setControl(1 + systemData.getBuildsCount(), 0, "buildBuilding", "", buildHandler,
				"<div class=\"title\">Construire un bâtiment</div><div class=\"justify\">Construire un nouveau bâtiment ou améliorer d'un bâtiment existant.</div><div class=\"emphasize\"><div style=\"float: right;\">" + buildingsCount + " / " + systemData.getBuildingLand() + "</div>Limite de bâtiments</div>");
		setControl(0, 1, "shipsContent", "", showSystemShipsHandler,
			"<div class=\"title\">Vaisseaux construits</div><div class=\"justify\">Affiche les vaisseaux stationnés sur le système.</div>");
		setControl(4, 0, "pushFleets", "", pushFleetsHandler,
			"<div class=\"title\">Expulsion !</div><div class=\"justify\">Expulse hors de votre système toutes les flottes neutres qui s'y trouvent.</div>");
		if(getFleetCost(Client.getInstance().getProductsManager())>0){
		setControl(4, 1, "buyFleet", "", buyFleetHandler,
			"<div class=\"title\">Recruter une flotte</div><div class=\"justify\">Acheter une nouvelle flotte pour " + getFleetCost(Client.getInstance().getProductsManager()) + "&nbsp;" +
			"<img class=\"resource credits\" src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" unselectable=\"on\"/>.</div>");
		}
		setControl(5, 0, "leaveSystem", "", leaveSystemHandler,
			"<div class=\"title\">Abandonner le système</div><div class=\"justify\">Tous vos bâtiments, ressources et vaisseaux stockés sur le système seront perdus. En abandonnant le système, vous récupérez 5 points de colonisation. Cette opération est irréversible.</div>");
		setControl(5, 1, "cancel", "", hideControlsHandler,
			"<div class=\"title\">Déselectionne le système</div>");
		
		Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_SYSTEM);
	}
	
	private void showSystemBuildingsContent(int tab) {
		int selectedX = -1, selectedY = -1;
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 2; j++)
				if (controls[i][j].getStyleName().contains("control-selected")) {
					selectedX = i;
					selectedY = j;
					break;
				}
		
		PlayerStarSystemData systemData = getSelectedSystem();
		
		for (TimerHandler updater : localUpdaters) {
			TimerManager.unregister(updater);
			updater.destroy();
		}
		localUpdaters.clear();
		
		this.currentView = EmpireControlPanel.VIEW_SYSTEM_BUILDINGS_CONTENT;
		this.currentTab = tab;
		
		clearControls();
		
		boolean hasNextBuildings = false;
		
		int buildingIndex = 0;
		buildings:for (int i = 0; i < BUILDINGS_ORDER.length; i++) {
			String type = BUILDINGS_ORDER[i];
			
			for (int level = 0; level < 5; level++) {
				int count = systemData.getBuildingsCount(type, level);
				
				if (count > 0) {
					if (buildingIndex == (tab + 1) * 9) {
						hasNextBuildings = true;
						break buildings;
					}
					
					if (buildingIndex >= tab * 9) {
						int x = (buildingIndex % 9) % 5;
						int y = buildingIndex - tab * 9 < 5 ? 0 : 1;
						setBuilding(x, y, type, level, count, systemData);
						
						if (selectedX == x && selectedY == y)
							controls[x][y].addStyleName("control-selected");
					}
					buildingIndex++;
				}
			}
		}
		
		if (hasNextBuildings) {
			if (tab == 0)
				setControl(5, 0, "next", "", showSystemContent1Handler,
					"<div class=\"title\">Bâtiments suivants</div>");
			else if (tab == 1)
				setControl(5, 0, "next", "", showSystemContent2Handler,
					"<div class=\"title\">Bâtiments suivants</div>");
		} else {
			if (tab > 0)
				setControl(5, 0, "next", "", showSystemContent0Handler,
					"<div class=\"title\">Bâtiments suivants</div>");
		}
		
		setControl(4, 1, "selfdestruct", "", destroyHandler,
			"<div class=\"title\">Détruire bâtiment</div>" +
			"<div class=\"justify\">Sélectionnez un bâtiment, puis cliquez " +
			"sur ce bouton pour ordonner sa destruction. " +
			"Vous récupérerez 50% du coût de la dernière amélioration du bâtiment. " +
			"Attention, cette opération est irréversible ! Si vous avez " +
			"plusieurs bâtiment du même type, seul un bâtiment est détruit.</div>");
		setControl(5, 1, "cancel", "", showSystemControlsHandler,
			"<div class=\"title\">Retour</div>");
	}
	
	private void showSystemShipsContent() {
		int selectedX = -1, selectedY = -1;
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 2; j++)
				if (controls[i][j].getStyleName().contains("control-selected")) {
					selectedX = i;
					selectedY = j;
					break;
				}
		
		PlayerStarSystemData systemData = getSelectedSystem();
		
		for (TimerHandler updater : localUpdaters) {
			TimerManager.unregister(updater);
			updater.destroy();
		}
		localUpdaters.clear();
		
		this.currentView = EmpireControlPanel.VIEW_SYSTEM_SHIPS_CONTENT;
		
		clearControls();
		
		long lastUpdate = Client.getInstance().getEmpireView().getLastSystemsUpdate();
		
		int slotIndex = 0;
		for (int i = 0; i < systemData.getSlotsCount(); i++) {
			int slotId = systemData.getInterpolatedSlotIdAt(i, lastUpdate);
			long slotCount = systemData.getInterpolatedSlotCountAt(i, lastUpdate);
			
			if (slotId != 0) {
				String count = Formatter.formatNumber(slotCount, true);
				
				String content = "<div class=\"spaceship\" style=\"" +
					"background-position: -" + (slotId * 50) + "px 0\">" +
					count + "</div><div class=\"selection\"></div>";
				
				int x = slotIndex % 5;
				int y = slotIndex / 5;
				setControl(x, y, "", content,
					selectionHandler, ShipData.getDesc(slotId, slotCount,
						Client.getInstance().getResearchManager(
							).getShipAvailableAbilities(slotId)));
				
				if (selectedX == x && selectedY == y)
					controls[x][y].addStyleName("control-selected");
				
				slotIndex++;
			}
		}
		
		setControl(5, 0, "selfdestruct", "", destroyHandler,
			"<div class=\"title\">Détruire vaisseaux</div>" +
			"<div class=\"justify\">Sélectionnez un vaisseau, puis cliquez " +
			"sur ce bouton pour ordonner sa destruction. Attention, " +
			"cette opération est irréversible et vous ne récuperez pas de " +
			"ressources pour la destruction des vaisseaux !</div>");
		setControl(5, 1, "cancel", "", showSystemControlsHandler,
			"<div class=\"title\">Retour</div>");
	}
	
	private void showSpaceStationControls() {
		this.currentView = VIEW_SPACE_STATION_CONTROLS;
		
		setControl(0, 0, "spaceStation", "<img style=\"position: absolute; " +
			"top: 30px; left: 30px;\" src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" class=\"resource credits\"/>",
			fundSpaceStationHandler, "<div class=\"title\">Transfert crédits</div>" +
			"<div class=\"justify\">Transférez des crédits sur la station spatiale. Une fois " +
			"transférés, les crédits ne peuvent plus être récupérés.</div>");
		
		setControl(5, 1, "cancel", "", hideControlsHandler,
			"<div class=\"title\">Déselectionne la station spatiale.</div>");
	}
	
	private void showStructureControls() {
		this.currentView = VIEW_STRUCTURE_CONTROLS;
		clearControls();
		
		StructureData structure = getSelectedStructure();
		
		if (structure.isActivated()) {
			// Affiche la liste des modules
			int[] validModules = StructureData.getValidModules(structure.getType());
			for (int i = 0; i < validModules.length; i++) {
				int moduleType = validModules[i];
				
				if (moduleType >= StructureModuleData.TYPE_DECK_FIGHTER &&
					moduleType <= StructureModuleData.TYPE_DECK_BATTLECRUISER) {
					if (moduleType == StructureModuleData.TYPE_DECK_FIGHTER) {
						setControl(i, 0, "moduleSpaceshipDecks", "",
							showStructureDecksHandler,
							"<div class=\"title\">Plateformes</div>" +
							"<div class=\"justify\">Permet la construction " +
							"de nouvelles classes de vaisseaux. Les cargos " +
							"ne nécessitent pas de plateforme.</div>" +
							"<div class=\"emphasize\">Cliquez pour " +
							"afficher les plateformes</div>");
					}
					continue;
				}
				
				int currentLevel = 0;
				for (int j = 0; j < structure.getModulesCount(); j++)
					if (structure.getModuleAt(j).getType() == moduleType)
						currentLevel = structure.getModuleAt(j).getLevel();
				
				// Calcule le prix de l'amélioration du module
				long[] cost = structure.getModuleCost(moduleType,
					structure.getModuleLevel(moduleType) + 1);
				
				String costTooltip = "";
				for (int j = 0; j < cost.length; j++)
					if (cost[j] > 0) {
						double resources;
						if (j < 4)
							resources = structure.getResourceAt(j);
						else
							resources = Client.getInstance().getResourcesManager().getCurrentCredits();
						
						costTooltip += "<b style=\"color: " + (resources >= cost[j] ?
							"#00c000" : "red") + ";\">" + 
							Formatter.formatNumber(cost[j], true) + "</b>" +
							"&nbsp;<img src=\"" + Config.getMediaUrl() +
							"images/misc/blank.gif\" class=\"resource " +
							(j == 4 ? "credits" : "r" + j) + "\"/> ";
					}
				
				
				setControl(i, 0, "module" + moduleType, "", upgradeModuleHandler,
					StructureModuleData.getDesc(structure.getType(), moduleType, currentLevel, true) +
					"<div style=\"clear: both;\">" + costTooltip + "</div>");
				
				OutlineText text = TextManager.getText(
					"<div class=\"inset\">" + (currentLevel + 1) + "</div>");
				controls[i][0].getElement().appendChild(text.getElement());
			}
			
			switch (structure.getType()) {
			case StructureData.TYPE_STASIS_CHAMBER:
				// Stase
				long reloadRemainingTime = (long) structure.getSkillAt(0).getReloadRemainingTime();
				long lastUseTime = (long) structure.getSkillAt(0).getLastUseTime();
				setControl(0, 1, "skillStasis", reloadRemainingTime > 0 ?
					"<div class=\"progress\"></div>" : "", castStructureSkillHandler,
					"<div class=\"title\">Stase</div>" +
					"<div class=\"justify;\">Peut être utilisé sur une flotte à " +
					"moins de 25 cases. Utilisé sur une flotte amie, la flotte " +
					"regagne tout son mouvement. Utilisé sur une flotte ennemie " +
					"ou pirate, la flotte est immobilisée 4h (non cumulatif).</div>" +
					"<div class=\"emphasize\"><img src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" class=\"clock\"/> " + Formatter.formatDate(
					Math.round(StructureSkillData.STASIS_RELOAD * Math.pow(.88,
					structure.getModuleLevel(StructureModuleData.TYPE_SKILL_RELOAD))), false) + "</div>");

				if (reloadRemainingTime > 0) {
					BackgroundUpdater bgUpdater = new BackgroundUpdater(
						controls[0][1].getElement().getFirstChildElement(),
						new Point(0, 0),
						new Point(1600, 0),
						new Point(50, 0),
						33 / (1000. * reloadRemainingTime));
					long length = reloadRemainingTime + lastUseTime;
					
					bgUpdater.setCurrentOffset(
						33. * (length - reloadRemainingTime) / length);
					bgUpdater.setMode(BackgroundUpdater.MODE_SINGLE);
					localUpdaters.add(bgUpdater);
					TimerManager.register(bgUpdater);
					
					String id = ToolTipTextUpdater.generateId();
					OutlineText reloadRemainingTimeText = TextManager.getText(
						"<span id=\"" + id + "\"></span>");
					reloadRemainingTimeText.addStyleName("buildTime");
					controls[0][1].getElement().appendChild(reloadRemainingTimeText.getElement());
					
					ToolTipTimeUpdater updater = new ToolTipTimeUpdater(
							getElement(), id, reloadRemainingTime, false, true);
					localUpdaters.add(updater);
					TimerManager.register(updater, TimerManager.SECOND_UNIT);
				}
				break;
			case StructureData.TYPE_SPACESHIP_YARD:
				// Recrutement d'une nouvelle flotte
				int buyFleetRemainingTime = structure.getBuyFleetRemainingTime();
				
				setControl(4, 0, "buyFleet", buyFleetRemainingTime > 0 ?
					"<div class=\"progress\"></div>" : "", buyStructureFleetHandler,
					"<div class=\"title\">Recruter une flotte</div>" +
					"<div class=\"justify\">Le chantier " +
					"spatial doit disposer d'une case libre. " +
					"Vous devez attendre 1h après avoir recruté un " +
					"flotte pour en recruter une nouvelle.</div>" +
					"<div class=\"emphasize\">" + Utilities.getCreditsImage() +
					"&nbsp;" + Formatter.formatNumber(getFleetCost(Client.getInstance().getProductsManager())) + "</div>");
				
				if (buyFleetRemainingTime > 0) { //check baha
					BackgroundUpdater bgUpdater = new BackgroundUpdater(
						controls[0][1].getElement().getFirstChildElement(),
						new Point(0, 0),
						new Point(1600, 0),
						new Point(50, 0),
						33 / (1000. * buyFleetRemainingTime));
					long length = 3600;
					
					bgUpdater.setCurrentOffset(
						33. * (length - buyFleetRemainingTime) / length);
					bgUpdater.setMode(BackgroundUpdater.MODE_SINGLE);
					localUpdaters.add(bgUpdater);
					TimerManager.register(bgUpdater);
					
					String id = ToolTipTextUpdater.generateId();
					OutlineText reloadRemainingTimeText = TextManager.getText(
						"<span id=\"" + id + "\"></span>");
					reloadRemainingTimeText.addStyleName("buildTime");
					controls[4][0].getElement().appendChild(reloadRemainingTimeText.getElement());
					
					ToolTipTimeUpdater updater = new ToolTipTimeUpdater(
							getElement(), id, buyFleetRemainingTime, false, true);
					localUpdaters.add(updater);
					TimerManager.register(updater, TimerManager.SECOND_UNIT);
				}
				
				// Vaisseaux en cours de construction
				int index = 1;
				for (int i = 0; i < structure.getBuildSlotsCount(); i++) {
					if (structure.getBuildSlotAt(i).getId() != 0) {
						SlotInfoData slotData = structure.getBuildSlotAt(i);
						
						String countId = ToolTipTextUpdater.generateId();
						String content = "<div class=\"progress\"></div>" +
							"<div id=\"" + countId + "\" class=\"spaceship\" style=\"" +
							"background-position: -" + (slotData.getId() * 50) + "px 0\">" +
							Formatter.formatNumber(Math.ceil(slotData.getCount()), true) + "</div>";
						
						setControl(index, 1, "", content, buildShipsHandler, ShipData.getDesc(
							slotData.getId(), (long) slotData.getCount(), ShipData.HIDE_ABILITIES) +
							"<div class=\"emphasize\">Clic droit pour annuler la construction.</div>");
						
						if (index == 1) {
							double shipProduction = structure.getShipProduction();
							double shipBuildTime = shipProduction > 0 ?
								ShipData.SHIPS[slotData.getId()].getBuildTime() /
								shipProduction : Integer.MAX_VALUE;
							
							int totalBuildTime = (int) (shipBuildTime *
								structure.getBuildSlotOrderedAt(i));
							int remainingBuildTime = (int) (shipBuildTime *
								slotData.getCount() - Utilities.getCurrentTime() +
								Client.getInstance().getAreaContainer().getLastAreaUpdate() -
								structure.getLastUpdate());
							
							double progress = 1 - (remainingBuildTime / (double) totalBuildTime);
							double slotsCount = (1 - progress) * structure.getBuildSlotOrderedAt(i);
							
							if (progress < 1) {
								BackgroundUpdater bgUpdater = new BackgroundUpdater(
									controls[index][1].getElement().getFirstChildElement(),
									new Point(0, 0),
									new Point(1600, 0),
									new Point(50, 0),
									33 / (1000. * shipBuildTime));
								bgUpdater.setCurrentOffset(
									33. * (Math.ceil(slotsCount) - slotsCount));
								bgUpdater.setMode(BackgroundUpdater.MODE_LOOP);
								localUpdaters.add(bgUpdater);
								TimerManager.register(bgUpdater);
								
								String id = ToolTipTextUpdater.generateId();
								OutlineText remainingTimeText = TextManager.getText(
									"<span id=\"" + id + "\"></span>");
								remainingTimeText.addStyleName("buildTime");
								controls[index][1].getElement().appendChild(remainingTimeText.getElement());
								
								ToolTipTimeUpdater updater = new ToolTipTimeUpdater(
										getElement(), id, remainingBuildTime, false);
								localUpdaters.add(updater);
								TimerManager.register(updater, TimerManager.SECOND_UNIT);
								
								CountdownUpdater cdUpdater = new CountdownUpdater(
									countId, slotsCount, 1. / (1000 * shipBuildTime));
								localUpdaters.add(cdUpdater);
								TimerManager.register(cdUpdater);
							}
						}
						
						index++;
					}
				}
				
				if (index < 4) {
					setControl(index++, 1, "buildShips", "", buildShipsHandler,
						"<div class=\"title\">Construire des vaisseaux</div>" +
						"<div class=\"justify\">Vos vaisseaux seront placés en " +
						"attente sur le chantier spatial, prêts à être " +
						"transférés sur une flotte.</div>");
				}
				
				setControl(0, 1, "shipsContent", "", showStructureShipsHandler,
					"<div class=\"title\">Vaisseaux construits</div>" +
					"<div class=\"justify\">Affiche les vaisseaux stationnés " +
					"sur le chantier spatial.</div>");
				
				break;
			}
			
			setControl(5, 0, "deactivate", "", deactivateStructureHandler,
				"<div class=\"title\">Désactivation</div>" +
				"<div class=\"justify\">La structure ne peut plus être " +
				"utilisée, ne consomme plus de " + Utilities.getEnergyImage() +
				" et peut être démontée.</div>");
		} else {
			setControl(5, 0, "activate", "", activateStructureHandler,
				"<div class=\"title\">Activation</div>" +
				"<div class=\"justify\">Permet d'utiliser la structure. La " +
				"structure ne doit pas être en cours de démontage.</div>" +
				"<div class=\"emphasize\">Consommation : +" +
				StructureData.getEnergyConsumption(structure.getType()) +
				" " + Utilities.getEnergyImage() + "</div>");
		}
		
		setControl(4, 1, "destroyWard", "", destroyStructureHandler,
			"<div class=\"title\">Auto-destruction</div>" +
			"<div class=\"justify\">Enclenche la procédure d'auto-destruction " +
			"de la structure. Cette opération est irréversible.</div>");
		setControl(5, 1, "cancel", "", hideControlsHandler,
			"<div class=\"title\">Déselectionne la structure.</div>");
	}
	
	private void showStructureShipsContent() {
		int selectedX = -1, selectedY = -1;
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 2; j++)
				if (controls[i][j].getStyleName().contains("control-selected")) {
					selectedX = i;
					selectedY = j;
					break;
				}
		
		StructureData structureData = getSelectedStructure();
		
		this.currentView = VIEW_STRUCTURE_SHIPS_CONTENT;
		clearControls();
		
		int slotIndex = 0;
		for (int i = 0; i < structureData.getSlotsCount(); i++) {
			SlotInfoData slotData = structureData.getSlotAt(i);
			if (slotData.getId() != 0) {
				String count = Formatter.formatNumber(slotData.getCount(), true);
				
				String content = "<div class=\"spaceship\" style=\"" +
					"background-position: -" + (slotData.getId() * 50) + "px 0\">" +
					count + "</div><div class=\"selection\"></div>";
				
				setControl(slotIndex % 5, slotIndex / 5, "", content, selectionHandler,
					ShipData.getDesc(slotData.getId(), (long) slotData.getCount(),
					Client.getInstance().getResearchManager(
						).getShipAvailableAbilities(slotData.getId())));
				
				if (selectedX == slotIndex && selectedY == 0)
					controls[selectedX][selectedY].addStyleName("control-selected");
					
				slotIndex++;
			}
		}
		
		setControl(5, 0, "selfdestruct", "", destroyHandler,
			"<div class=\"title\">Détruire vaisseaux</div>" +
			"<div class=\"justify\">Sélectionnez un vaisseau, puis cliquez " +
			"sur ce bouton pour ordonner sa destruction. Attention, " +
			"cette opération est irréversible et vous ne récuperez pas de " +
			"ressources pour la destruction des vaisseaux !</div>");
		setControl(5, 1, "cancel", "", showStructureControlsHandler,
			"<div class=\"title\">Retour</div>");
	}
	
	private void showStructureDecks() {
		StructureData structure = getSelectedStructure();
		
		this.currentView = VIEW_STRUCTURE_DECKS;
		clearControls();
		
		// Affiche la liste des modules
		for (int i = 0; i < 6; i++) {
			int moduleType = StructureModuleData.TYPE_DECK_FIGHTER + i;
			int currentLevel = 0;
			for (int j = 0; j < structure.getModulesCount(); j++)
				if (structure.getModuleAt(j).getType() == moduleType)
					currentLevel = structure.getModuleAt(j).getLevel();
			
			if (currentLevel == 0) {
				// Calcule le prix de l'amélioration du module
				long[] cost = structure.getModuleCost(moduleType,
					structure.getModuleLevel(moduleType) + 1);
				
				String costTooltip = "";
				for (int j = 0; j < cost.length; j++)
					if (cost[j] > 0) {
						double resources;
						if (j < 4)
							resources = structure.getResourceAt(j);
						else
							resources = Client.getInstance().getResourcesManager().getCurrentCredits();
						
						costTooltip += "<b style=\"color: " + (resources >= cost[j] ?
							"#00c000" : "red") + ";\">" + 
							Formatter.formatNumber(cost[j], true) + "</b>" +
							"&nbsp;<img src=\"" + Config.getMediaUrl() +
							"images/misc/blank.gif\" class=\"resource " +
							(j == 4 ? "credits" : "r" + j) + "\"/> ";
					}
				
				setControl(i, 0, "module" + moduleType, "", upgradeModuleHandler,
					StructureModuleData.getDesc(structure.getType(), moduleType, currentLevel, true) +
					"<div style=\"clear: both;\">" + costTooltip + "</div>");
				
				OutlineText text = TextManager.getText(
					"<div class=\"inset\">" + (currentLevel + 1) + "</div>");
				controls[i][0].getElement().appendChild(text.getElement());
			}
		}
		
		setControl(5, 1, "cancel", "", showStructureControlsHandler,
			"<div class=\"title\">Retour</div>");
	}
	
	private void setBuilding(int x, int y, String type, int level, int count,
			PlayerStarSystemData system) {
		// Niveau du bâtiment
		String content = "<div class=\"buildingLevel\">";
		for (int i = 0; i <= level; i++)
			content += "<img class=\"level\" src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\"/>";
		content += "</div><div class=\"selection\"></div>";
		
		// Nom et decription du bâtiment
		String tooltip =
			"<div class=\"title\">" + BuildingData.getName(type, level) + "</div>" +
			"<div class=\"justify\">" + BuildingData.getDesc(type, level, false) + "</div>";
		
		setControl(x, y, BuildingData.getClassName(type),
				content, selectionHandler, tooltip);
		
		// Nombre de batiment construits
		if (count > 0) {
			OutlineText text = TextManager.getText(
				"<div class=\"inset\">" + count + "</div>");
			controls[x][y].getElement().appendChild(text.getElement());
		}
	}
	
	private long getFleetCost(ProductsManager productsManager) {
		int fleetsCount = 0;
		ArrayList<PlayerFleetData> fleetsData =
			Client.getInstance().getEmpireView().getFleets();
		
		for (PlayerFleetData fleetData : fleetsData)
			if (!fleetData.isDelude())
				fleetsCount++;
		
		double cost = 1000;
		cost *= Math.pow(1.5, Math.min(fleetsCount, 5));
		if (fleetsCount > 5)
		   cost *= Math.pow(1.4, Math.min(fleetsCount - 5, 5));
		if (fleetsCount > 10)
		   cost *= Math.pow(1.3, Math.min(fleetsCount - 10, 5));
		if (fleetsCount > 15)
		   cost *= Math.pow(1.2, Math.min(fleetsCount - 15, 5));
		if (fleetsCount > 20)
		   cost *= Math.pow(1.1, (fleetsCount - 20));
		
		cost *= Math.pow(.95, Client.getInstance().getAdvancementDialog(
			).getAdvancementLevel(AdvancementData.TYPE_FLEETS_COST));
		
		
		HashMap<Integer, Integer> products = productsManager.getProducts();
		double priceFactor = 1;
		
		if(products!=null){
		if(products.get(ProductData.PRODUCT_SULFARIDE)!=null)
			{
			priceFactor = ProductData.getProductEffect(
					ProductData.PRODUCT_SULFARIDE,
					products.get(ProductData.PRODUCT_SULFARIDE)
					);
			}
		}
		
		cost *= priceFactor;
		
		return (long) (Math.ceil(cost / 100)) * 100;
	}

	
	private long getSystemMigrationCost() {
		int lvl = Client.getInstance().getProgressBar().getPlayerLevel();
		
		double cost = 4000;
		cost*=Math.pow(lvl,2);
		
		return (long) (Math.ceil(cost / 100)) * 100;
	}
}
