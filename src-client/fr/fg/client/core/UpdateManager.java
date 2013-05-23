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

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.selection.SelectionManager;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.EffectData;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.PlayerGeneratorData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.UpdateData;
import fr.fg.client.openjwt.ui.JSOptionPane;

public class UpdateManager extends ActionCallbackAdapter {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static ActionCallback UPDATE_CALLBACK = new ActionCallbackAdapter() {
		public void onSuccess(AnswerData data) {
			process(data);
		}
	};
	
	public final static ActionCallbackAdapter UNSELECT_AND_UPDATE_CALLBACK = new ActionCallbackAdapter() {
		public void onSuccess(AnswerData data) {
			SelectionManager.getInstance().setNoSelection();
			process(data);
		}
	};
	
	private final static HashMap<String, String> EMPTY_HASH_MAP =
		new HashMap<String, String>();
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private final static UpdateManager instance = new UpdateManager();
	
	private HashMap<Integer, Long> updateDatesByType;
	
	private boolean active;
	
	private int errorsCount;
	
	private boolean running;
	
	private Action currentAction;
	
	// ----------------------------------------------------- CONSTRUCTEURS --//
	
	private UpdateManager() {
		this.active = false;
		this.running = false;
		this.errorsCount = 0;
		this.updateDatesByType = new HashMap<Integer, Long>();
	}
	
	// ---------------------------------------------------------- METHODES --//
	
	public static void start() {
		instance.active = true;
		if (!instance.running) {
			instance.running = true;
			instance.update();
		}
	}
	
	public static void stop() {
		instance.active = false;
		if (instance.currentAction != null && instance.currentAction.isPending())
			new Action("killpolling", Action.NO_PARAMETERS);
	}
	
	private void update() {
		currentAction = new Action("polling", EMPTY_HASH_MAP, this);
	}
	
	public void onFailure(String error) {
		if (instance.active) {
			instance.errorsCount++;
			
			if (instance.errorsCount > 3) {
				super.onFailure(error);
				
				// Recharge la page
				Window.Location.reload();
			} else {
				Timer t = new Timer() {
					public void run() {
						instance.update();
					}
				};
				t.schedule(200);
			}
		}
	}
	
	public void onSuccess(AnswerData data) {
		if (instance.active) {
			try {
				UpdateManager.process(data);
			} catch (Exception e) {
				Utilities.log("Processing exception.", e);
			}
			instance.update();
		} else {
			instance.running = false;
		}
	}
	
	private static void process(AnswerData data) {
		for (int i = 0; i < data.getUpdatesCount(); i++) {
			UpdateData update = data.getUpdateAt(i);
			
			
			// Controle pour éviter d'appliquer une mise à jour plus ancienne
			// sur une mise à jour récente
			switch (update.getType()) {
			case UpdateData.UPDATE_AREA:
			case UpdateData.UPDATE_PLAYER_FLEETS:
			case UpdateData.UPDATE_PLAYER_SYSTEMS:
			case UpdateData.UPDATE_CONTRACTS_STATE:
			case UpdateData.UPDATE_XP:
				if (instance.updateDatesByType.containsKey(update.getType())) {
					long date = instance.updateDatesByType.get(update.getType());
					
					if (date > update.getDate())
						continue;
				}
				
				instance.updateDatesByType.put(update.getType(), (long) update.getDate());
				break;
			}
			
			switch (update.getType()) {
			case UpdateData.UPDATE_CHAT:
				Client.getInstance().getChat().addMessage(
						update.getChatMessage());
				break;
			case UpdateData.UPDATE_AREA:
				IndexedAreaData areaData = new IndexedAreaData(update.getArea());
				Client.getInstance().getAreaContainer().updateArea(areaData);
				SelectionManager.getInstance().updateSelection(areaData);
				
				if (SelectionManager.getInstance().isSpaceStationSelected() ||
						SelectionManager.getInstance().isStructureSelected()) {
					Client.getInstance().getEmpireControlPanel().updateControls();
					Client.getInstance().getNamePanel().update();
					Client.getInstance().getSelectionInfo().update();
					Client.getInstance().getProgressBar().update();
				}
				Client.getInstance().getActionManager().updateSelection();
				break;
			case UpdateData.UPDATE_PLAYER_FLEETS:
				ArrayList<PlayerFleetData> fleets = new ArrayList<PlayerFleetData>();
				for (int j = 0; j < update.getPlayerFleets().getFleetsCount(); j++)
					fleets.add(update.getPlayerFleets().getFleetAt(j));
				
				Client.getInstance().getEmpireView().updateFleets(fleets);
				SelectionManager.getInstance().updateSelection(update.getPlayerFleets());
				
				if (SelectionManager.getInstance().isFleetSelected()) {
					Client.getInstance().getEmpireControlPanel().updateControls();
					Client.getInstance().getNamePanel().update();
					Client.getInstance().getSelectionInfo().update();
					Client.getInstance().getProgressBar().update();
				}
				break;
			case UpdateData.UPDATE_PLAYER_SYSTEMS:
				ArrayList<PlayerStarSystemData> systems =
					new ArrayList<PlayerStarSystemData>();
				for (int j = 0; j < update.getPlayerSystems().getSystemsCount(); j++)
					systems.add(update.getPlayerSystems().getSystemAt(j));
				
				Client.getInstance().getEmpireView().setSystems(systems);
				Client.getInstance().getResourcesManager().setSystems(systems);
				SelectionManager.getInstance().updateSelection(update.getPlayerSystems());
				
				if (SelectionManager.getInstance().isSystemSelected()) {
					Client.getInstance().getEmpireControlPanel().updateControls();
					Client.getInstance().getNamePanel().update();
					Client.getInstance().getSelectionInfo().update();
					Client.getInstance().getProgressBar().update();
				}
				break;
			case UpdateData.UPDATE_PLAYER_GENERATORS:
				ArrayList<PlayerGeneratorData> generators =
					new ArrayList<PlayerGeneratorData>();
				for (int j = 0; j < update.getPlayerGenerators().getGeneratorsCount(); j++)
					generators.add(update.getPlayerGenerators().getGeneratorAt(j));
				
				Client.getInstance().getEmpireView().setGenerators(generators);
				
				Client.getInstance().getProgressBar().setPlayerGeneratorsCount(update.getPlayerGenerators().getGeneratorsCount());
				
				break;
			case UpdateData.UPDATE_NEW_MESSAGE:
				Client.getInstance().getMessenger().setUnreadMessages(
					Client.getInstance().getMessenger().getUnreadMessages() + 1);
				break;
			case UpdateData.UPDATE_NEW_NEWS:
				Client.getInstance().getAllyDialog().setUnreadNews(
					Client.getInstance().getAllyDialog().getUnreadNews() + 1);
				break;
			case UpdateData.UPDATE_CONTRACTS_STATE:
				Client.getInstance().getMissionPanel().setMissionStates(
					update.getContractsState());
				break;
			case UpdateData.UPDATE_PLAYER_CONTRACTS:
				Client.getInstance().getContractDialog().setContracts(
					update.getPlayerContracts());
				break;
			case UpdateData.UPDATE_XP:
				Client.getInstance().getProgressBar().setPlayerData(
					(long) update.getXp().getXp(),
					update.getXp().getColonizationPoints());
				Client.getInstance().getAdvancementDialog().updateUI();
				break;
			case UpdateData.UPDATE_NEW_EVENTS:
				Client.getInstance().getEventsDialog().setNewEvents(true);
				break;
			case UpdateData.UPDATE_CHAT_CHANNELS:
				Client.getInstance().getChat().setChannels(update.getChatChannels());
				break;
			case UpdateData.UPDATE_ALLY:
				Client.getInstance().getAllyDialog().setAlly(update.getAlly());
				break;
			case UpdateData.UPDATE_SERVER_SHUTDOWN:
				Client.getInstance().setServerShutdown(
					update.getServerShutdownRemainingTime());
				break;
			case UpdateData.UPDATE_INFORMATION:
				JSOptionPane.showMessageDialog(update.getInformation(),
					"Information", JSOptionPane.OK_OPTION,
					JSOptionPane.INFORMATION_MESSAGE, null);
				break;
			case UpdateData.UPDATE_ADVANCEMENTS:
				Client.getInstance().getAdvancementDialog(
					).setAdvancements(update.getAdvancements());
				break;
			case UpdateData.UPDATE_EFFECT:
				EffectData effect = update.getEffect();
				IndexedAreaData area = Client.getInstance().getAreaContainer().getArea();
				
				if (area.getId() == effect.getIdArea())
					Client.getInstance().getAreaContainer().getMap().addItem(effect, EffectData.CLASS_NAME);
				break;
			case UpdateData.UPDATE_PRODUCTS:
				Client.getInstance().getProductsManager().setProducts(update.getProducts());
				break;
			case UpdateData.UPDATE_PLAYER_FLEET:
				Client.getInstance().getEmpireView().updateFleet(update.getPlayerFleet());
				
				if (SelectionManager.getInstance().isFleetSelected()) {
					Client.getInstance().getEmpireControlPanel().updateControls();
					Client.getInstance().getNamePanel().update();
					Client.getInstance().getSelectionInfo().update();
					Client.getInstance().getProgressBar().update();
				}
				break;
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
