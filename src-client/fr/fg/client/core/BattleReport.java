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
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.animation.WidthUpdater;
import fr.fg.client.data.AbilityData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.FleetData;
import fr.fg.client.data.GroupData;
import fr.fg.client.data.ReportActionAbilityData;
import fr.fg.client.data.ReportActionData;
import fr.fg.client.data.ReportDamageData;
import fr.fg.client.data.ReportData;
import fr.fg.client.data.ReportSlotData;
import fr.fg.client.data.ReportSlotStateData;
import fr.fg.client.data.ShipData;
import fr.fg.client.data.Sounds;
import fr.fg.client.data.WeaponGroupData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.BaseWidget;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.EventManager;
import fr.fg.client.openjwt.core.SoundManager;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;
import fr.fg.client.openjwt.ui.DialogListener;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSRowLayout;

public class BattleReport implements ClickListener, DialogListener,
		ActionCallback, EventPreview {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int DEFAULT_SHOTS_SPEED = 10;
	
	private final static int[] SHIPS_POSITION = {2, 3, 1, 4, 0};
	
	// Effets spéciaux sur les tirs
	private final static int
		EFFECT_STARS = 1 << 0,
		EFFECT_IMPACT = 1 << 1,
		EFFECT_FRAG = 1 << 2,
		EFFECT_PARTICLES = 1 << 3;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private HashMap<Integer, ReportData> reports;
	
	private boolean standalone;
	
	private int currentReportId;
	
	private Slot[] fleetsSlots;
	
	private SlotToolTip[] fleetsToolTips;
	
	private AbsolutePanel environmentPanel, battlePanel, effectsPanel,
		toolTipPanel, fleetTactics[];
	
	private JSButton exitBt, restartBt;
	
	private JSDialog roundsDialog;
	
	private ArrayList<Shot> shots;
	
	private ArrayList<Effect> effects;
	
	private ArrayList<FloatingText> floatingTexts;
	
	private ArrayList<Destruction> destructions;

	private TimeShift timeShift;
	
	private ArrayList<Sound> sounds;
	
	private BattleUpdater battleUpdater;
	
	private int currentAction;
	
	private int[] slotsPosition;
	
	private Action downloadAction;
	
	private Ability[][] abilities;
	
	private PowerBar[] powerBars;
	private XpView[] xpViews;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BattleReport(boolean standalone) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		// Charge les sons des combats
		SoundManager.getInstance().loadSound(Sounds.SMALL_SHOT1);
		SoundManager.getInstance().loadSound(Sounds.SMALL_SHOT2);
		SoundManager.getInstance().loadSound(Sounds.IMPACT);
		SoundManager.getInstance().loadSound(Sounds.EXPLOSION);
		SoundManager.getInstance().loadSound(Sounds.AVERAGE_SHOT1);
		SoundManager.getInstance().loadSound(Sounds.AVERAGE_SHOT2);
		SoundManager.getInstance().loadSound(Sounds.AVERAGE_SHOT2);
		
		this.standalone = standalone;
		this.reports = new HashMap<Integer, ReportData>();
		this.currentReportId = -1;
		this.shots = new ArrayList<Shot>();
		this.effects = new ArrayList<Effect>();
		this.floatingTexts = new ArrayList<FloatingText>();
		this.sounds = new ArrayList<Sound>();
		this.destructions = new ArrayList<Destruction>();
		this.timeShift = null;
		this.xpViews = new XpView[]{new XpView(false), new XpView(true)};
		
		// Tactique employée par chaque flotte
		fleetTactics = new AbsolutePanel[2];
		
		fleetTactics[0] = new AbsolutePanel();
		fleetTactics[0].setStyleName("bottomFleetTactics");
		
		fleetTactics[1] = new AbsolutePanel();
		fleetTactics[1].setStyleName("topFleetTactics");
		
		// Environnment de la bataille
		environmentPanel = new AbsolutePanel();
		environmentPanel.setStyleName("environmentPanel");
		
		// Champ de bataille
		battlePanel = new AbsolutePanel();
		battlePanel.setStyleName("battlePanel");
		
		fleetsSlots = new Slot[10];
		
		// Flotte en haut de l'écran
		for (int i = 0; i < 5; i++) {
			Slot slot = new Slot(5 + i);
			fleetsSlots[5 + i] = slot;
			battlePanel.add(slot, 0, 0);
		}
		
		// Flotte en bas de l'écran
		for (int i = 0; i < 5; i++) {
			Slot slot = new Slot(i);
			fleetsSlots[i] = slot;
			battlePanel.add(slot, 0, 0);
		}
		
		// Tooltips
		toolTipPanel = new AbsolutePanel();
		toolTipPanel.setStyleName("toolTipPanel");
		fleetsToolTips = new SlotToolTip[10];
		
		// Flotte en haut de l'écran
		for (int i = 0; i < 5; i++) {
			SlotToolTip toolTip = new SlotToolTip(5 + i);
			fleetsToolTips[5 + i] = toolTip;
			toolTipPanel.add(toolTip, 0, 0);
		}
		
		// Flotte en bas de l'écran
		for (int i = 0; i < 5; i++) {
			SlotToolTip toolTip = new SlotToolTip(i);
			fleetsToolTips[i] = toolTip;
			toolTipPanel.add(toolTip, 0, 0);
		}
		
		// Zone où les explosions et les dégâts sont affichés
		effectsPanel = new AbsolutePanel();
		effectsPanel.addStyleName("effectsArea");
		
		RootPanel.get().sinkEvents(Event.KEYEVENTS);
		toolTipPanel.sinkEvents(Event.KEYEVENTS);
		fleetTactics[0].sinkEvents(Event.KEYEVENTS);
		fleetTactics[1].sinkEvents(Event.KEYEVENTS);
		
		// Round précédent
		if (!standalone) {
			exitBt = new JSButton("Quitter");
			exitBt.setPixelWidth(120);
			exitBt.addClickListener(this);

			restartBt = new JSButton("Revoir");
			restartBt.setPixelWidth(120);
			restartBt.addClickListener(this);
			
			roundsDialog = new JSDialog(messages.battleTitle(), false, true, false);
			
			// Mise en forme des boutons
			JSRowLayout layout = new JSRowLayout();			
			layout.addComponent(exitBt);
			layout.addComponent(restartBt);
			
			roundsDialog.setComponent(layout);
			roundsDialog.setLocation(10, 10, false);
		}
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void onClick(Widget sender) {
		if (sender == exitBt) {
			dialogClosed(roundsDialog);
		}
		if (sender == restartBt){
			setAction(0, reports.get(currentReportId));
		}
	}
	
	public void dialogClosed(Widget sender) {
		clearReport();
		
		roundsDialog.setVisible(false);
		
		Client.getInstance().setFullScreenMode(false);
		
		Client.getInstance().getAreaContainer().getMap().setVisible(true);
		Client.getInstance().getAreaContainer().setNebula(
			Client.getInstance().getAreaContainer().getArea().getNebula(), false);
	}
	
	public void cacheReport(ReportData report) {
		reports.put(report.getId(), report);
	}
	
	public void showReport(int id) {
		if (reports.containsKey(id)) {
			currentReportId = id;
			
			ReportData report = reports.get(id);
			
			// Son lorsque les rapports de combat sont visualisés en standalone
			if (standalone && report.hasGeneralVolume() && report.hasSoundVolume()) {
				SoundManager.getInstance().setGeneralVolume(report.getGeneralVolume());
				SoundManager.getInstance().setSoundVolume(report.getSoundVolume());
				Config.setGraphicsQuality(report.getGraphicsQuality());
			}
			
			if (this.powerBars == null)
				this.powerBars = new PowerBar[]{new PowerBar(false), new PowerBar(true)};
			
			buildEnvironment(report);
			buildTactics(report);
			
			// Charge les flottes
			for (int i = 0; i < 10; i++)
				fleetsSlots[i].load(report);
			for (int i = 0; i < 10; i++)
				fleetsToolTips[i].load(report);
			
			setAction(-1, report);
			    
			Client.getInstance().setFullScreenMode(true);
			
			Client.getInstance().getAreaContainer().getMap().setVisible(false);
			Client.getInstance().getAreaContainer().setNebula(report.getNebula(), true);
			Client.getInstance().getAreaContainer().setVisible(true);

			Client.getInstance().getFullScreenPanel().add(environmentPanel);
			Client.getInstance().getFullScreenPanel().add(battlePanel);
			Client.getInstance().getFullScreenPanel().add(effectsPanel);
			Client.getInstance().getFullScreenPanel().add(toolTipPanel);
			Client.getInstance().getFullScreenPanel().add(fleetTactics[0]);
			Client.getInstance().getFullScreenPanel().add(fleetTactics[1]);
			Client.getInstance().getFullScreenPanel().add(powerBars[0]);
			Client.getInstance().getFullScreenPanel().add(powerBars[1]);
			Client.getInstance().getFullScreenPanel().add(xpViews[0]);
			Client.getInstance().getFullScreenPanel().add(xpViews[1]);
			
			EventManager.addEventHook(this);
			
			if (roundsDialog != null)
				roundsDialog.setVisible(true);
		} else {
			if (downloadAction != null && downloadAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("report", String.valueOf(id));
			
			downloadAction = new Action("getreport", params, this);
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		downloadAction = null;
		cacheReport(data.getReport());
		showReport(data.getReport().getId());
	}
	
	public boolean onEventPreview(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONKEYDOWN:
			if (event.getKeyCode() == 32) {
				if (battleUpdater != null)
					battleUpdater.setPaused(!battleUpdater.isPaused());
				event.preventDefault();
				event.cancelBubble(true);
			}
			break;
		}
		return true;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void clearReport() {
		EventManager.removeEventHook(this);
		
		if (battleUpdater != null) {
			TimerManager.unregister(battleUpdater);
			battleUpdater = null;
		}
		
		// Supprime les objets des rounds précédents
		shots.clear();
		effects.clear();
		floatingTexts.clear();
		sounds.clear();
		destructions.clear();
		effectsPanel.clear();
		timeShift = null;
	}
	
	private void setAction(int action, ReportData report) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		clearReport();
		
		currentAction = action;
		
		String message = null;
		if (action == -1) {
			message = messages.battleStartMessage();
		} else if (action == report.getActionsCount()) {
			if (report.isRetreat())
				message = messages.battleRetreatMessage();
			else
				message = messages.battleEndMessage();
		}
		
		if (message != null) {
			FloatingText text = new FloatingText(0,
				"<div class=\"floatingText\">" + message + "</div>", 0, 300, 500);
			
			effectsPanel.add(text);
			floatingTexts.add(text);
		}
		
		slotsPosition = buildFleets(report, action);
		
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < abilities[i].length; j++) {
				abilities[i][j].setSelected(false);
			}
		
		// Génère l'animation de l'action en cours
		int length = 50;
		if (action >= 0 && action < report.getActionsCount()) {
			ReportActionData reportAction = report.getActionsAt(action);
			
			abilities[reportAction.getSlotIndex() < 5 ? 0 : 1]
					[reportAction.getActionIndex()].setSelected(true);
				
			length = buildAction(report, action, slotsPosition);
			
			// Slots détruits
			for (int i = 0; i < fleetsSlots.length; i++) {
				if (fleetsSlots[i].getCount(action + 1) == 0 &&
						fleetsSlots[i].getCount(action) > 0) {
					// Explosion principale
					Effect destruction = new Effect(length, getSlotX(i),
							getSlotY(i, action), Effect.TYPE_DESTRUCTION);
					
					effectsPanel.add(destruction);
					effects.add(destruction);
					
					// Explosions secondaires
					for (int j = 0; j < 6; j++) {
						Effect explosion = new Effect(
							length + 1 + (int) (Math.random() * 14),
							getSlotX(i) + (int) (Math.random() * 51 - 25),
							getSlotY(i, action) + (int) (Math.random() * 51 - 25),
							Effect.TYPE_EXPLOSION);
						
						effectsPanel.add(explosion);
						effects.add(explosion);
					}
					
					destructions.add(new Destruction(length + 3, i));
					
					// Sons
					sounds.add(new Sound(length - 2, Sounds.IMPACT));
					sounds.add(new Sound(length,     Sounds.EXPLOSION));
					sounds.add(new Sound(length + 3, Sounds.IMPACT));
					sounds.add(new Sound(length + 5, Sounds.IMPACT));
					
					length += 15;
				}
			}
		}
		
		// Calcule la puissance des flottes et l'XP gagnée à la fin du round
		int[] initialPower = {0, 0};
		int[] currentPower = {0, 0};
		
		for (int i = 0; i < fleetsSlots.length; i++) {
			int shipId = fleetsSlots[i].getShipId();
			if (shipId != 0) {
				initialPower[i / 5] += (int) (fleetsSlots[i].getCount(-1) *
					ShipData.CLASSES_POWER[ShipData.SHIPS[shipId].getShipClass()]);
				currentPower[i / 5] += (int) (fleetsSlots[i].getCount(action) *
					ShipData.CLASSES_POWER[ShipData.SHIPS[shipId].getShipClass()]);
			}
		}
		
		int[] initialPowerLevel = {
			GroupData.getLevelAtPower(initialPower[0]),
			GroupData.getLevelAtPower(initialPower[1]),
		};
		
		for (int i = 0; i < 2; i++) 
			powerBars[i].setPower(currentPower[i]);
			
			
			int xpFactorDef = report.getDefenderBeforeXpFactor();
			int xpFactorAtt = report.getAttackerBeforeXpFactor();
			
			
			xpViews[1].setXp((int) Math.floor((initialPower[0] - currentPower[0]) * xpFactorDef * .02 * 0.01), xpFactorDef);
			
			xpViews[0].setXp((int) Math.floor((initialPower[1] - currentPower[1]) * xpFactorAtt * .02 * 0.01), xpFactorAtt);
			
			
//			for (int i = 0; i < 2; i++) {
//				powerBars[i].setPower(currentPower[i]);
//				
//				int j = i == 1 ? 0 : 1;
//				double xpFactor = FleetData.getXpFactor(initialPowerLevel[i], initialPowerLevel[j]);
//				xpViews[i].setXp((int) Math.floor((initialPower[j] - currentPower[j]) * xpFactor * .02), xpFactor);
//			}
//			
			
		
		
		// Lance l'animation
		if (action <= report.getActionsCount()) {
			battleUpdater = new BattleUpdater(length);
			TimerManager.register(battleUpdater);
		}
	}
	
	private void buildEnvironment(ReportData report) {
		environmentPanel.getElement().setInnerHTML("");
		
		boolean systemVisible = false;
		
		for (int player = 0; player < 2; player++) {
			String[] environments;
			int offset;
			
			if (player == 0) {
				environments = report.getAttackerEnvironment().split(",");
				offset = 400;
			} else {
				environments = report.getDefenderEnvironment().split(",");
				offset = 100;
			}
			
			for (String environment : environments) {
				if (environment.startsWith("asteroid")) {
					if (environment.equals("asteroid")) {
						for (int i = 0; i < 2; i++) {
							int[] x = {185, 310};
							int[] y = {offset - 5, offset + 10};
							
							Element asteroid = DOM.createDiv();
							asteroid.setClassName("asteroid env_" + "normal");
							asteroid.getStyle().setProperty("left", x[i] + "px");
							asteroid.getStyle().setProperty("top",  y[i] + "px");
							
							environmentPanel.getElement().appendChild(asteroid);
						}
					} else {
						for (int i = 0; i < 3; i++) {
							int[] x = {125, 250, 370};
							int[] y = {offset - 5, offset + 5, offset - 15};
							
							Element asteroid = DOM.createDiv();
							asteroid.setClassName("asteroid " + (i == 1 ? environment : "") + " env_" + "normal");
							asteroid.getStyle().setProperty("left", x[i] + "px");
							asteroid.getStyle().setProperty("top",  y[i] + "px");
							
							environmentPanel.getElement().appendChild(asteroid);
						}
					}
				} else if (!systemVisible && environment.startsWith("system") &&
						(player == 0 || !report.getAttackerEnvironment().contains("system"))) {
					Integer starImage = Integer.parseInt(
							environment.substring("system".length()));
					
					Element system = DOM.createDiv();
					system.setClassName("system");
					system.getStyle().setProperty("backgroundPosition",
						-120 * (starImage - 1) + "px 0");
					
					environmentPanel.getElement().appendChild(system);
					
					systemVisible = true;
				} else if (environment.equals("spaceStation") &&
						(player == 0 || !report.getAttackerEnvironment().contains("spaceStation"))) {
					Element spaceStation = DOM.createDiv();
					spaceStation.setClassName("spaceStation");
					spaceStation.setInnerHTML("<div class=\"shield\"></div>");
					
					environmentPanel.getElement().appendChild(spaceStation);
				}
			}
		}
	}
	
	// Affiche la tactique employée par chaque flotte
	private void buildTactics(ReportData report) {
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		fleetTactics[0].clear();
		fleetTactics[1].clear();
		
		fleetTactics[0].add(new Ability(-1, -2, null));
		fleetTactics[1].add(new Ability(-1, -2, null));
		
		int length;
		
		if (report.getActionsCount() > 0) {
			length = report.getActionsAt(report.getActionsCount() - 1).getActionIndex();
			length = (5 * (1 + length / 5));
		} else {
			length = 0;
		}
		
		abilities = new Ability[2][length];
		
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < 2; j++) {
				int action = -1;
				int type = 0;
				String tooltip = null;
				
				for (int k = 0; k < report.getActionsCount(); k++) {
					ReportActionData reportAction = report.getActionsAt(k);
					
					if (reportAction.getActionIndex() == i &&
							((j == 0 && reportAction.getSlotIndex() < 5) ||
							 (j == 1 && reportAction.getSlotIndex() >= 5))) {
						int shipId = report.getSlotAt(j,
								reportAction.getSlotIndex() -
								(j == 1 ? 5 : 0)).getId();
						ShipData ship = ShipData.SHIPS[shipId];
						int ability = reportAction.getActionAbilityAt(0).getAbility();
						
						action = k;
						type = ability == -1 ? -1 :
							ship.getAbilities()[ability].getType();
						
						
						tooltip = "<div unselectable=\"on\" class=\"title\">" +
							dynamicMessages.getString("ship" + shipId) + "</div>" +
							"<div unselectable=\"on\" class=\"small\" style=\"font-weight: bold;\">";
						
						if (ability == -1) {
							tooltip += "Tir";
						} else {
							tooltip += ship.getAbilities()[ability].getName();
						}
						
						tooltip += "</div><div unselectable=\"on\" class=\"x-small\">";
						
						if (ability == -1) {
							// Armement du vaisseau
							for (WeaponGroupData weaponGroup : ship.getWeapons()) {
								tooltip += "<div unselectable=\"on\">" + weaponGroup.getCount() + "x " + dynamicMessages.getString( //$NON-NLS-1$ //$NON-NLS-2$
									"weapon" + weaponGroup.getIdWeapon()) + " " + //$NON-NLS-1$ //$NON-NLS-2$
									"<img unselectable=\"on\" class=\"stat s-damage\" src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
									"images/misc/blank.gif\"/> <b unselectable=\"on\">" + weaponGroup.getWeapon().getDamageMin() + //$NON-NLS-1$
									"-" + weaponGroup.getWeapon().getDamageMax() + "</b></div>"; //$NON-NLS-1$ //$NON-NLS-2$
							}
						} else {
							tooltip += ship.getAbilities()[ability].getDesc(shipId);
						}
						break;
					}
				}
				
				Ability ability = new Ability(action, type, tooltip);
				abilities[j][i] = ability;
				fleetTactics[j].add(ability);
			}
		}
	}
	
	// Affiche les vaisseaux et tooltip des deux flottes
	private int[] buildFleets(ReportData report, int action) {
		int[] slotsPosition = new int[10];
		
		// Positionne les slots des deux flottes
		for (int i = 0; i < 10; i++) {
			Slot slot = fleetsSlots[i];
			
			OpenJWT.setElementOpacity(slot.getElement(), 1);
			
			// Compte le nombre de slots sur la meme rangée que le vaisseau
			int count = 0;
			if (slot.getShipId() != 0) {
				for (int k = 5 * (i / 5); k < i; k++) {
					Slot slot2 = fleetsSlots[k];
					
					if (slot2.getShipId() != 0 && slot2.getCount(action) > 0 &&
							slot2.isFront(action) == slot.isFront(action))
						count++;
				}
				slotsPosition[i] = SHIPS_POSITION[count];
				
				battlePanel.setWidgetPosition(slot,
						slotsPosition[i] * 100,
						i < 5 ? (slot.isFront(action) ? 400 : 500) :
							(slot.isFront(action) ? 100 : 0));
				toolTipPanel.setWidgetPosition(fleetsToolTips[i],
						slotsPosition[i] * 100,
						i < 5 ? (slot.isFront(action) ? 400 : 500) :
							(slot.isFront(action) ? 100 : 0));
			}
		}
		
		for (Slot slot : fleetsSlots)
			slot.setAction(action);
		for (SlotToolTip slotToolTip : fleetsToolTips)
			slotToolTip.setAction(action);
		
		return slotsPosition;
	}
	
	private int buildAction(ReportData report, int action, int[] slotsPosition) {
		// Buffs des actions précédentes
		for (int i = 0; i <= action; i++) {
			ReportActionData reportAction = report.getActionsAt(i);
			ReportActionAbilityData lastReportActionAbility = reportAction.getActionAbilityAt(
					reportAction.getActionAbilitiesCount() - 1);
			
			if (lastReportActionAbility.getAbility() != -1 &&
					(reportAction.getModifiers() & ReportActionData.INHIBITED) == 0) {
				ShipData ship = ShipData.SHIPS[lastReportActionAbility.getSlotId()];
				AbilityData ability = ship.getAbilities()[lastReportActionAbility.getAbility()];
				
				switch (ability.getType()) {
				case AbilityData.TYPE_RETRIBUTION:
					// Vengeance
					int actionIndex = report.getActionsAt(action).getActionIndex();
					int fleetOffset = reportAction.getSlotIndex() / 5;
					int retributionEnd = reportAction.getActionIndex() +
						ability.getRetributionLength();
					
					if ((fleetOffset == 0 && actionIndex <= retributionEnd) ||
						(fleetOffset == 1 && (actionIndex < retributionEnd ||
						(actionIndex == retributionEnd &&
						report.getActionsAt(action).getSlotIndex() / 5 == fleetOffset)))) {
						
						Effect buff = new Effect(0,
							getSlotX(reportAction.getSlotIndex()),
							getSlotY(reportAction.getSlotIndex(), action),
							Effect.TYPE_RETRIBUTION);
						
						effectsPanel.add(buff);
						effects.add(buff);
					}
					break;
				}
			}
		}
		
		ReportActionData reportAction = report.getActionsAt(action);
		
		int slotIndex = reportAction.getSlotIndex();
		int fleetIndex = slotIndex < 5 ? 0 : 5;
		int otherFleetIndex = slotIndex < 5 ? 5 : 0;
		
		Slot slot = fleetsSlots[slotIndex];
		
		// Détermine si l'action a été inhibée
		if ((reportAction.getModifiers() & ReportActionData.INHIBITED) != 0) {
			for (int j = 0; j < 6; j++) {
				Effect effect = new Effect(j * 5 + 2,
					getSlotX(reportAction.getSlotIndex()),
					getSlotY(reportAction.getSlotIndex(), action),
					Effect.TYPE_TAUNT);
				
				effectsPanel.add(effect);
				effects.add(effect);
			}
			
			FloatingText text = new FloatingText(14,
				"<div class=\"floatingText\" style=\"" +
				"font-size: 10px !important\">Inhibé !</div>",
				getSlotX(slotIndex) - 100,
				getSlotY(slotIndex, action), 200);
			
			effectsPanel.add(text);
			floatingTexts.add(text);
			
			return 35;
		}
		
		AbilityData ability = null;
		ReportActionAbilityData lastReportActionAbility = reportAction.getActionAbilityAt(
				reportAction.getActionAbilitiesCount() - 1);
		
		if (lastReportActionAbility.getAbility() != -1)
			ability = ShipData.SHIPS[lastReportActionAbility.getSlotId()].getAbilities(
					)[lastReportActionAbility.getAbility()];
		
		if (ability == null) {
			// Tir normal
			FloatingText text = new FloatingText(0,
				"<div unselectable=\"on\" class=\"floatingText ability-shot\">" +
				"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"class=\"miniAbility\" style=\"vertical-align: middle; " +
				"background-position: 0 -125px;\"/> Tir !</div>",
				getSlotX(slotIndex) - 100, getSlotY(slotIndex, action), 200);
			
			effectsPanel.add(text);
			floatingTexts.add(text);
			
			buildAttack(reportAction, slotIndex, action);
			
			return 65;
		} else {
			// Capacité
			FloatingText text = new FloatingText(0,
				"<div unselectable=\"on\" class=\"floatingText ability-" +
				ability.getCategory() + "\"><img src=\"" +
				Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"class=\"miniAbility\" style=\"vertical-align: middle; " +
				"background-position: -" + (16 *
				AbilityData.GRAPHICS[ability.getType()]) + "px -125px;\"/> " +
				ability.getName() + "</div>",
				getSlotX(slotIndex) - 100, getSlotY(slotIndex, action), 200);
			
			effectsPanel.add(text);
			floatingTexts.add(text);
			
			switch (ability.getType()) {
			case AbilityData.TYPE_INHIBITOR_FIELD:
				// Champ inhibiteur
				for (int j = 0; j < 6; j++) {
					Effect effect = new Effect(j * 5 + 2,
						getSlotX(reportAction.getSlotIndex()),
						getSlotY(reportAction.getSlotIndex(), action),
						Effect.TYPE_TAUNT);
					
					effectsPanel.add(effect);
					effects.add(effect);
				}
				
				return 35;
			case AbilityData.TYPE_ACTIVE_LEECH:
			case AbilityData.TYPE_RAGE:
				// Vol de vaisseaux
				int length = 65;
				
				for (int i = 0; i < reportAction.getDamageCount(); i++) {
					ReportDamageData reportDamage = reportAction.getDamageAt(i);
					
					if (5 * (reportDamage.getTargetSlot() / 5) == otherFleetIndex) {
						int modifiers = reportDamage.getAmount() > 0 ? EFFECT_IMPACT : 0;
						buildShots(slotIndex, reportDamage.getTargetSlot(),
								slotsPosition, modifiers, 0, action);
						buildDamage(reportDamage, 29, slotsPosition, action, true);
					} else if (slotIndex == reportDamage.getTargetSlot() &&
							reportDamage.getAmount() < 0) {
						Effect regeneration = new Effect(60,
							getSlotX(slotIndex), getSlotY(slotIndex, action) + 3,
							Effect.TYPE_REGENERATION);
						
						effectsPanel.add(regeneration);
						effects.add(regeneration);
						
						// Nombre de vaisseaux regénérés
						text = new FloatingText(65,
							"<div class=\"floatingText\" " +
							"style=\"color: #59cdff;\">+" +
							-reportDamage.getKills() +
							"<img class=\"stat s-struct\" src=\"" +
							Config.getMediaUrl() +
							"images/misc/blank.gif\"/></div>",
							getSlotX(slotIndex) - 100,
							getSlotY(slotIndex, action), 200);
						
						effectsPanel.add(text);
						floatingTexts.add(text);
						
						length += 30;
					} else if (slotIndex == reportDamage.getTargetSlot() &&
							reportDamage.getAmount() > 0) {
						// Animation - impact
						buildImpacts(slotIndex, slotsPosition, 0, action);
						buildDamage(reportDamage, 29, slotsPosition, action, false);
					}
				}
				
				return length;
			case AbilityData.TYPE_TAUNT:
				// Persecution - force l'attaque
				for (int i = 0; i < 6; i++) {
					Effect effect = new Effect(i * 5 + 2,
						getSlotX(reportAction.getSlotIndex()),
						getSlotY(reportAction.getSlotIndex(), action),
						Effect.TYPE_TAUNT);
					
					effectsPanel.add(effect);
					effects.add(effect);
				}
				
				text = new FloatingText(14,
					"<div class=\"floatingText\" style=\"" +
					"font-size: 10px !important\">" +
					"Cible obligatoire !</div>",
					getSlotX(slotIndex) - 100,
					getSlotY(slotIndex, action), 200);
				
				effectsPanel.add(text);
				floatingTexts.add(text);
				
				return 35;
			case AbilityData.TYPE_DAMAGE_RETURN_AURA:
				// Pénitence - renvoi de dégâts
				for (int j = fleetIndex; j < fleetIndex + 5; j++)  {
					if (fleetsSlots[j].getCount(action) > 0 &&
							fleetsSlots[j].isFront(action) == slot.isFront(action)) {
						for (int i = 0; i < 10; i += 2) {
							Effect buff = new Effect(i,
								getSlotX(j) + (int) (Math.random() * 41 - 20),
								getSlotY(j, action) + 7 + (int) (Math.random() * 11 - 5),
								Math.random() < .5 ?
								Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
							
							effectsPanel.add(buff);
							effects.add(buff);
						}
						
						for (int i = 10; i < 35; i += 3) {
							Effect buff = new Effect(i,
								getSlotX(j) + (int) (Math.random() * 41 - 20),
								getSlotY(j, action) + 7 + (int) (Math.random() * 11 - 5),
								Math.random() < .5 ?
								Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
							
							effectsPanel.add(buff);
							effects.add(buff);
						}
						
						text = new FloatingText(14 + (int) (Math.random() * 5 - 2),
							"<div class=\"floatingText\" style=\"" +
							"font-size: 10px !important\">" +
							"Renvoi dégâts<br/>+" + (int) Math.round(100 *
							ability.getDamageReturnAuraValue()) + "%</div>",
							getSlotX(j) - 100, getSlotY(j, action), 200);
						
						effectsPanel.add(text);
						floatingTexts.add(text);
					}
				}
				
				return 35;
			case AbilityData.TYPE_PROXIMITY_CHARGE:
				// Charge de proximité - renvoi de dégâts
						for (int i = 0; i < 10; i += 2) {
							Effect buff = new Effect(i,
								getSlotX(reportAction.getSlotIndex()) + (int) (Math.random() * 41 - 20),
								getSlotY(reportAction.getSlotIndex(), action) + 7 + (int) (Math.random() * 11 - 5),
								Math.random() < .5 ?
								Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
							
							effectsPanel.add(buff);
							effects.add(buff);
						}
						
						for (int i = 10; i < 35; i += 3) {
							Effect buff = new Effect(i,
								getSlotX(reportAction.getSlotIndex()) + (int) (Math.random() * 41 - 20),
								getSlotY(reportAction.getSlotIndex(), action) + 7 + (int) (Math.random() * 11 - 5),
								Math.random() < .5 ?
								Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
							
							effectsPanel.add(buff);
							effects.add(buff);
						}
						
						text = new FloatingText(14 + (int) (Math.random() * 5 - 2),
							"<div class=\"floatingText\" style=\"" +
							"font-size: 10px !important\">" +
							"Renvoi dégâts<br/>+" + (int) Math.round(100 *
							ability.getDamageReturnAuraValue()) + "%</div>",
							getSlotX(reportAction.getSlotIndex()) - 100, getSlotY(reportAction.getSlotIndex(), action), 200);
						
						effectsPanel.add(text);
						floatingTexts.add(text);
					

				return 35;
			case AbilityData.TYPE_RAPID_FIRE_V2:
			case AbilityData.TYPE_RAPID_FIRE:
			case AbilityData.TYPE_DEVASTATE:
			case AbilityData.TYPE_PARTICLE_PROJECTION:
			case AbilityData.TYPE_OVERHEAT:
				buildAttack(reportAction, slotIndex, action);
				return 65;
			case AbilityData.TYPE_RIFT:
				for (int j = 0; j < 10; j++)  {
					if (fleetsSlots[j].getCount(action) > 0) {
						if (fleetsToolTips[j].damageModifiers[action] > 1 ||
								fleetsToolTips[j].hullModifiers[action] > 1 ||
								fleetsToolTips[j].protectionModifiers[action] > 0) {
							for (int i = 0; i < 10; i += 2) {
								Effect buff = new Effect(i,
									getSlotX(j) + (int) (Math.random() * 41 - 20),
									getSlotY(j, action) + 7 + (int) (Math.random() * 11 - 5),
									Math.random() < .5 ?
									Effect.TYPE_DEBUFF_1 : Effect.TYPE_DEBUFF_2);
								
								effectsPanel.add(buff);
								effects.add(buff);
							}
							
							for (int i = 10; i < 35; i += 3) {
								Effect buff = new Effect(i,
									getSlotX(j) + (int) (Math.random() * 41 - 20),
									getSlotY(j, action) + 7 + (int) (Math.random() * 11 - 5),
									Math.random() < .5 ?
									Effect.TYPE_DEBUFF_1 : Effect.TYPE_DEBUFF_2);
								
								effectsPanel.add(buff);
								effects.add(buff);
							}
						}
						
						if (fleetsToolTips[j].damageModifiers[action] < 1 ||
								fleetsToolTips[j].hullModifiers[action] < 1 ||
								fleetsToolTips[j].protectionModifiers[action] < 0) {
							for (int i = 0; i < 10; i += 2) {
								Effect debuff = new Effect(i,
									getSlotX(j) + (int) (Math.random() * 41 - 20),
									getSlotY(j, action) + 7 + (int) (Math.random() * 11 - 5),
									Math.random() < .5 ?
									Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
								
								effectsPanel.add(debuff);
								effects.add(debuff);
							}
							
							for (int i = 10; i < 35; i += 3) {
								Effect debuff = new Effect(i,
									getSlotX(j) + (int) (Math.random() * 41 - 20),
									getSlotY(j, action) + 7 + (int) (Math.random() * 11 - 5),
									Math.random() < .5 ?
									Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
								
								effectsPanel.add(debuff);
								effects.add(debuff);
							}
						}
					}
				}
				
				return 35;
			case AbilityData.TYPE_BARRAGE:
				// Tir de barrage
				int targetClass = ability.getBarrageClassTarget();
				ArrayList<Integer> targets = new ArrayList<Integer>();
				
				for (int i = otherFleetIndex; i < otherFleetIndex + 5; i++) {
					int shipId = fleetsSlots[i].getShipId();
					if (shipId != 0 && fleetsSlots[i].getCount(action) > 0 &&
							ShipData.SHIPS[shipId].getShipClass() == targetClass)
						targets.add(i);
				}
				
				if (targets.size() > 0) {
					// Tirs sur les vaisseaux affectés
					for (Integer target : targets) {
						buildShots(slotIndex, target,
								slotsPosition, EFFECT_FRAG, 0, action);
						
						text = new FloatingText(45 + (int) (Math.random() * 5 - 2),
							"<div class=\"floatingText\" style=\"" +
							"font-size: 10px !important\">" + 
							"Dégâts -" + (int) Math.round(100 * (1 -
							(slot.isFront(action) ? ability.getBarrageFrontMalus() :
							ability.getBarrageBackMalus()))) + "%</div>",
							getSlotX(target) - 100, getSlotY(target, action), 200);
						
						effectsPanel.add(text);
						floatingTexts.add(text);
					}
					
					return 70;
				} else {
					// Aucun vaisseau affecté
					text = new FloatingText(10,
						"<div class=\"floatingText\" style=\"color: red;\">" + 
						"Sans effet" + "</div>",
						getSlotX(slotIndex) - 100,
						getSlotY(slotIndex, action), 200);
					
					effectsPanel.add(text);
					floatingTexts.add(text);
					
					return 40;
				}
			case AbilityData.TYPE_BARRAGE_V2:
				// Tir de barrage V2
				
				for (int i = otherFleetIndex; i < otherFleetIndex + 5; i++) {
					int shipId = fleetsSlots[i].getShipId();
					if (shipId != 0 && fleetsSlots[i].getCount(action) > 0){
					// Tirs sur les vaisseaux affectés
						buildShots(slotIndex, i,
								slotsPosition, EFFECT_FRAG, 0, action);
						
						text = new FloatingText(45 + (int) (Math.random() * 5 - 2),
							"<div class=\"floatingText\" style=\"" +
							"font-size: 10px !important\">" + 
							"Dégâts -" + (int) Math.round(100 * (1 -
							(slot.isFront(action) ? ability.getBarrageV2FrontMalus() :
							ability.getBarrageV2BackMalus()))) + "%</div>",
							getSlotX(i) - 100, getSlotY(i, action), 200);
						
						effectsPanel.add(text);
						floatingTexts.add(text);
					}
				}
					return 70;
					
			case AbilityData.TYPE_HULL_ENERGY_TRANSFER:
			case AbilityData.TYPE_DAMAGE_ENERGY_TRANSFER:
				// Transfert d'énergie
				for (int i = 0; i < 10; i += 2) {
					Effect buff = new Effect(i,
						getSlotX(slotIndex) + (int) (Math.random() * 41 - 20),
						getSlotY(slotIndex, action) + 7 + (int) (Math.random() * 11 - 5),
						Math.random() < .5 ?
						Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
					
					effectsPanel.add(buff);
					effects.add(buff);
					
					buff = new Effect(i,
						getSlotX(slotIndex) + (int) (Math.random() * 41 - 20),
						getSlotY(slotIndex, action) + 7 + (int) (Math.random() * 11 - 5),
						Math.random() < .5 ?
						Effect.TYPE_DEBUFF_1 : Effect.TYPE_DEBUFF_2);
					
					effectsPanel.add(buff);
					effects.add(buff);
				}
				
				return 30;
			case AbilityData.TYPE_FOCUSED_FIRE:
				// Tir concentré
				for (int i = 0; i < 10; i += 2) {
					Effect buff = new Effect(i,
						getSlotX(slotIndex) + (int) (Math.random() * 41 - 20),
						getSlotY(slotIndex, action) + 7 + (int) (Math.random() * 11 - 5),
						Math.random() < .5 ?
						Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
					
					effectsPanel.add(buff);
					effects.add(buff);
				}
				
				for (int i = 10; i < 35; i += 3) {
					Effect buff = new Effect(i,
						getSlotX(slotIndex) + (int) (Math.random() * 41 - 20),
						getSlotY(slotIndex, action) + 7 + (int) (Math.random() * 11 - 5),
						Math.random() < .5 ?
						Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
					
					effectsPanel.add(buff);
					effects.add(buff);
				}
				
				text = new FloatingText(14,
					"<div class=\"floatingText\" style=\"" +
					"font-size: 10px !important\">" +
					"Dégâts +" + (int) Math.round(100 * (
					ability.getFocusFireBonus() - 1)) + "%</div>",
					getSlotX(slotIndex) - 100, getSlotY(slotIndex, action), 200);
				
				effectsPanel.add(text);
				floatingTexts.add(text);
				
				return 35;
			case AbilityData.TYPE_ALL_RESISTANCE:
			case AbilityData.TYPE_RESISTANCE:
				for (int i = 0; i < 10; i += 2) {
					Effect buff = new Effect(i,
						getSlotX(slotIndex) + (int) (Math.random() * 41 - 20),
						getSlotY(slotIndex, action) + 7 + (int) (Math.random() * 11 - 5),
						Math.random() < .5 ?
						Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
					
					effectsPanel.add(buff);
					effects.add(buff);
				}
				
				for (int i = 10; i < 35; i += 3) {
					Effect buff = new Effect(i,
						getSlotX(slotIndex) + (int) (Math.random() * 41 - 20),
						getSlotY(slotIndex, action) + 7 + (int) (Math.random() * 11 - 5),
						Math.random() < .5 ?
						Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
					
					effectsPanel.add(buff);
					effects.add(buff);
				}
				
				return 35;
			case AbilityData.TYPE_FRAG_CHARGE:
				// Charges à fragmentation
				for (int j = fleetIndex; j < fleetIndex + 5; j++)  {
					if (fleetsSlots[j].getCount(action) > 0 &&
							fleetsSlots[j].isFront(action) == slot.isFront(action)) {
						for (int i = 0; i < 10; i += 2) {
							Effect buff = new Effect(i,
								getSlotX(j) + (int) (Math.random() * 41 - 20),
								getSlotY(j, action) + 7 + (int) (Math.random() * 11 - 5),
								Math.random() < .5 ?
								Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
							
							effectsPanel.add(buff);
							effects.add(buff);
						}
						
						for (int i = 10; i < 35; i += 3) {
							Effect buff = new Effect(i,
								getSlotX(j) + (int) (Math.random() * 41 - 20),
								getSlotY(j, action) + 7 + (int) (Math.random() * 11 - 5),
								Math.random() < .5 ?
								Effect.TYPE_BUFF_1 : Effect.TYPE_BUFF_2);
							
							effectsPanel.add(buff);
							effects.add(buff);
						}
						
						text = new FloatingText(14 + (int) (Math.random() * 5 - 2),
							"<div class=\"floatingText\" style=\"" +
							"font-size: 10px !important\">" +
							"Dégâts +" + (int) Math.round(100 * (
							ability.getFragChargeBonus() - 1)) + "%</div>",
							getSlotX(j) - 100, getSlotY(j, action), 200);
						
						effectsPanel.add(text);
						floatingTexts.add(text);
					}
				}
				
				return 35;
			case AbilityData.TYPE_FORCE_FIELD:
				// Champ de force
				for (int j = fleetIndex; j < fleetIndex + 5; j++)  {
					if (fleetsSlots[j].getCount(action) > 0) {
						Effect forceField = new Effect(0,
							getSlotX(j), getSlotY(j, action) + 3,
							Effect.TYPE_FORCE_FIELD);
						
						effectsPanel.add(forceField);
						effects.add(forceField);
						
						long sourceHull = slot.getCount(action) *
							ShipData.SHIPS[slot.getShipId()].getHull();
						long targetHull = fleetsSlots[j].getCount(action) *
							ShipData.SHIPS[fleetsSlots[j].getShipId()].getHull();
						
						int modifier;
						
						if (sourceHull >= targetHull)
							modifier = ability.getForceFieldHighProtectionModifier();
						else
							modifier = ability.getForceFieldLowProtectionModifier();
						
						text = new FloatingText(10 + (int) (Math.random() * 5 - 2),
							"<div class=\"floatingText\" style=\"" +
							"font-size: 10px !important\">+" + modifier +
							" <img class=\"stat s-shield\" src=\"" +
							Config.getMediaUrl() + "images/misc/blank.gif\"/></div>",
							getSlotX(j) - 100, getSlotY(j, action), 200);
						
						effectsPanel.add(text);
						floatingTexts.add(text);
					}
				}
				return 35;
			case AbilityData.TYPE_DEFLECTOR:
				// Déflecteur
				for (int j = fleetIndex; j < fleetIndex + 5; j++)  {
					if (fleetsSlots[j].getCount(action) > 0 &&
							ShipData.SHIPS[fleetsSlots[j].getShipId()].getShipClass() ==
								ability.getDeflectorClassesTarget()) {
						Effect forceField = new Effect(0,
							getSlotX(j), getSlotY(j, action) + 3,
							Effect.TYPE_FORCE_FIELD);
						
						effectsPanel.add(forceField);
						effects.add(forceField);
						
						text = new FloatingText(10 + (int) (Math.random() * 5 - 2),
							"<div class=\"floatingText\" style=\"" +
							"font-size: 10px !important\">+"+ability.getDeflectorProtectionModifier()+" " +
							"<img class=\"stat s-shield\" src=\"" +
							Config.getMediaUrl() + "images/misc/blank.gif\"/></div>",
							getSlotX(j) - 100, getSlotY(j, action), 200);
						
						effectsPanel.add(text);
						floatingTexts.add(text);
					}
				}
				return 35;
			case AbilityData.TYPE_TIME_SHIFT:
				// Faille temporelle
				timeShift = new TimeShift(0, reportAction.getSlotIndex());
				return 28;
			case AbilityData.TYPE_RECUP:
			case AbilityData.TYPE_DAMAGE_REPARTITION:
				// Récupération
				
				if (reportAction.getDamageCount() > 0) {
					//for (int j = fleetIndex; j < fleetIndex + 5; j++)  {
					for(int j =0; j < reportAction.getDamageCount();j++){
					ReportDamageData reportDamage = reportAction.getDamageAt(j);
					//if(reportDamage.getHullDamage()>0 && reportDamage.getTargetSlot()==j){
					Effect regeneration = new Effect(0,
						getSlotX(reportDamage.getTargetSlot()),
						getSlotY(reportDamage.getTargetSlot(), action) + 3,
						Effect.TYPE_REGENERATION);
					
					effectsPanel.add(regeneration);
					effects.add(regeneration);
					
					// Nombre de vaisseaux regénérés
					text = new FloatingText(5,
						"<div class=\"floatingText\" " +
						"style=\"color: #59cdff;\">+" +
						-reportDamage.getKills() +
						"<img class=\"stat s-struct\" src=\"" +
						Config.getMediaUrl() +
						"images/misc/blank.gif\"/></div>",
						getSlotX(reportDamage.getTargetSlot()) - 100,
						getSlotY(reportDamage.getTargetSlot(), action), 200);
					
					effectsPanel.add(text);
					floatingTexts.add(text);
					}
				}
				if (reportAction.getDamageCount() > 0) {
					return 35;
				}
				else
				{
					return 15;
				}
			case AbilityData.TYPE_REPAIR:
				// Réparation
				if (reportAction.getDamageCount() > 0) {
					ReportDamageData reportDamage = reportAction.getDamageAt(0);
					
					Effect regeneration = new Effect(0,
						getSlotX(reportDamage.getTargetSlot()),
						getSlotY(reportDamage.getTargetSlot(), action) + 3,
						Effect.TYPE_REGENERATION);
					
					effectsPanel.add(regeneration);
					effects.add(regeneration);
					
					// Nombre de vaisseaux regénérés
					text = new FloatingText(5,
						"<div class=\"floatingText\" " +
						"style=\"color: #59cdff;\">+" +
						-reportDamage.getKills() +
						"<img class=\"stat s-struct\" src=\"" +
						Config.getMediaUrl() +
						"images/misc/blank.gif\"/></div>",
						getSlotX(reportDamage.getTargetSlot()) - 100,
						getSlotY(reportDamage.getTargetSlot(), action), 200);
					
					effectsPanel.add(text);
					floatingTexts.add(text);
					
					return 35;
				} else {
					return 15;
				}
			case AbilityData.TYPE_REPERCUTE:
				// répercussion
				if (reportAction.getDamageCount() > 0) {
					ReportDamageData reportDamage = reportAction.getDamageAt(0);
					
					Effect regeneration = new Effect(0,
						getSlotX(reportDamage.getTargetSlot()),
						getSlotY(reportDamage.getTargetSlot(), action) + 3,
						Effect.TYPE_REGENERATION);
					
					effectsPanel.add(regeneration);
					effects.add(regeneration);
					
					// Nombre de vaisseaux détruit
					text = new FloatingText(5,
						"<div class=\"floatingText\" " +
						"style=\"color: red;\">-" +
						reportDamage.getKills() +
						"<img class=\"stat s-death\" src=\"" + Config.getMediaUrl() +
						"images/misc/blank.gif\"/></div>",
						getSlotX(reportDamage.getTargetSlot()) - 100,
						getSlotY(reportDamage.getTargetSlot(), action), 200);
					
					effectsPanel.add(text);
					floatingTexts.add(text);
					
					return 35;
				} else {
					return 15;
				}
			case AbilityData.TYPE_RETRIBUTION:
				// Vengeance
				text = new FloatingText(14,
					"<div class=\"floatingText\" style=\"" +
					"font-size: 10px !important\">" +
					"Renvoi de dégâts !</div>",
					getSlotX(slotIndex) - 100,
					getSlotY(slotIndex, action), 200);
				
				effectsPanel.add(text);
				floatingTexts.add(text);
				
				return 35;
			case AbilityData.TYPE_SACRIFICE:
				// Sacrifice
				if (slot.isFront(action)) {
					for (int j = otherFleetIndex; j < otherFleetIndex + 5; j++)  {
						if (fleetsSlots[j].getCount(action) > 0 &&
								fleetsSlots[j].isFront(action)) {
							long attackingHull = slot.getCount(action) *
								ShipData.SHIPS[slot.getShipId()].getHull();
							long defendingHull = fleetsSlots[j].getCount(action) *
								ShipData.SHIPS[fleetsSlots[j].getShipId()].getHull();
							
							int modifier;
							
							if (attackingHull >= defendingHull)
								modifier = ability.getSacrificeHighProtectionModifier();
							else
								modifier = ability.getSacrificeLowProtectionModifier();
							
							text = new FloatingText(35 + (int) (Math.random() * 5 - 2),
								"<div class=\"floatingText\" style=\"" +
								"font-size: 10px !important\">" + modifier +
								" <img class=\"stat s-shield\" src=\"" +
								Config.getMediaUrl() + "images/misc/blank.gif\"/></div>",
								getSlotX(j) - 100, getSlotY(j, action), 200);
							
							effectsPanel.add(text);
							floatingTexts.add(text);
						}
					}
					
					text = new FloatingText(35 + (int) (Math.random() * 5 - 2),
						"<div class=\"floatingText\" style=\"" +
						"font-size: 10px !important\">" +
						ability.getSacrificeSelfProtectionModifier() +
						" <img class=\"stat s-shield\" src=\"" +
						Config.getMediaUrl() + "images/misc/blank.gif\"/></div>",
						getSlotX(slotIndex) - 100, getSlotY(slotIndex, action), 200);
					
					effectsPanel.add(text);
					floatingTexts.add(text);
					
					if (reportAction.getDamageCount() > 0) {
						text = new FloatingText(16,
							"<div class=\"floatingText\">" +
							reportAction.getDamageAt(0).getKills() +
							"<img class=\"stat s-death\" src=\"" + Config.getMediaUrl() +
							"images/misc/blank.gif\"/></div>",
							getSlotX(slotIndex) - 100, getSlotY(slotIndex, action), 200);
						
						effectsPanel.add(text);
						floatingTexts.add(text);
					}
					
					buildImpacts(slotIndex, slotsPosition, 0, action);
					return 65;
				} else {
					return 25;
				}
			case AbilityData.TYPE_CONFUSION:
				// Confusion
				if (reportAction.getDamageCount() > 0) {
					// Tirs sur le vaisseau affecté
					ReportDamageData reportDamage = reportAction.getDamageAt(0);
					
					buildShots(slotIndex, reportDamage.getTargetSlot(),
							slotsPosition, EFFECT_FRAG, 0, action);
					
					text = new FloatingText(45 + (int) (Math.random() * 5 - 2),
						"<div class=\"floatingText\" style=\"" +
						"font-size: 10px !important\">Dégâts minimum !</div>",
						getSlotX(reportDamage.getTargetSlot()) - 100,
						getSlotY(reportDamage.getTargetSlot(), action), 200);
					
					effectsPanel.add(text);
					floatingTexts.add(text);
					
					return 70;
				} else {
					// Aucun vaisseau affecté
					text = new FloatingText(10,
						"<div class=\"floatingText\" style=\"color: red;\">" + 
						"Sans effet" + "</div>",
						getSlotX(slotIndex) - 100,
						getSlotY(slotIndex, action), 200);
					
					effectsPanel.add(text);
					floatingTexts.add(text);
					
					return 40;
				}
			}
		}
		
		return 15;
	}
	
	private void buildShots(int fromSlot, int targetSlot, int[] slotsPosition,
			int animationEffects, int frameOffset, int action) {
		// Position du vaisseau qui tire
		boolean isTopFleet = fromSlot >= 5;
		Slot slot = fleetsSlots[fromSlot];
		int fromPosition = slotsPosition[fromSlot];
		int fromX = fromPosition * 100 + 50;
		int fromY = (isTopFleet ? 0 : 200) + 200 +
			(slot.isFront(action) ? 0 : (isTopFleet ? -100 : 100));
		
		// Calcule la position de la cible
		int targetPosition = slotsPosition[targetSlot];
		boolean targetSlotFront = fleetsSlots[targetSlot].isFront(action);
		int targetX = targetPosition * 100 + 50;
		int targetY = (isTopFleet ? 300 : 0) + 150 +
				(targetSlotFront ? 0 : (isTopFleet ? 100 : -100));
		
		// Génère les animations
		for (int k = 0; k < 10; k++) {
			int frame = (int) (4 * k +
					Math.round(Math.random() * 5) + frameOffset);
			int angle = targetPosition - fromPosition + 4;
			
			int offsetX;
			if (fromPosition > targetPosition)
				offsetX = (int) Math.round(Math.random() * -10) - 2;
			else if (fromPosition < targetPosition)
				offsetX = (int) Math.round(Math.random() *  10) + 2;
			else
				offsetX = (int) Math.round(Math.random() * 10) - 5;
			int offsetY = (int) Math.round(Math.random() * 21) - 10;
			
			// Sons des tirs
			for (int l = 0; l < 2; l++)
				switch (ShipData.SHIPS[slot.getShipId()].getShipClass()) {
				case ShipData.FIGHTER:
				case ShipData.CORVETTE:
					sounds.add(new Sound(frame + l, Math.random() < .5 ?
							Sounds.SMALL_SHOT1 : Sounds.SMALL_SHOT2));
					break;
				case ShipData.FRIGATE:
				case ShipData.DESTROYER:
					sounds.add(new Sound(frame + l, Math.random() < .5 ?
							Sounds.AVERAGE_SHOT1 : Sounds.AVERAGE_SHOT2));
					break;
				}
			
			Shot shot = new Shot(frame,
				fromX + offsetX,
				fromY + offsetY,
				targetX + offsetX,
				targetY + offsetY + (isTopFleet ? -50 : 50),
				angle,
				ShipData.SHIPS[slot.getShipId()].getShipClass());
			
			effectsPanel.add(shot);
			shots.add(shot);
			
			// Animation - étoiles
			if ((animationEffects & EFFECT_STARS) != 0) {
				for (int l = 0; l < DEFAULT_SHOTS_SPEED; l++) {
					double percent = l / (double) DEFAULT_SHOTS_SPEED;
					
					Effect specialShot = new Effect(frame + l,
						(int) Math.round(percent * shot.getEndX() +
						(1 - percent) * shot.getStartX() + Math.random() * 12 - 6),
						(int) Math.round(percent * shot.getEndY() +
						(1 - percent) * shot.getStartY() + Math.random() * 12 - 6),
						Effect.TYPE_SPECIAL_SHOT_1 + (int) (Math.random() * 2) + (isTopFleet ? 0 : 2));
					
					effectsPanel.add(specialShot);
					effects.add(specialShot);
				}
			}
			
			// Animation - particules
			if ((animationEffects & EFFECT_PARTICLES) != 0) {
				for (int l = 0; l < DEFAULT_SHOTS_SPEED; l++) {
					double percent = l / (double) DEFAULT_SHOTS_SPEED;
					
					Effect specialShot = new Effect(frame + l,
						(int) Math.round(percent * shot.getEndX() +
						(1 - percent) * shot.getStartX() + Math.random() * 12 - 6),
						(int) Math.round(percent * shot.getEndY() +
						(1 - percent) * shot.getStartY() + Math.random() * 12 - 6),
						Effect.TYPE_PARTICLE_1 + (int) (Math.random() * 3) + (isTopFleet ? 0 : 3));
					
					effectsPanel.add(specialShot);
					effects.add(specialShot);
				}
			}
			
			// Animation - fragmentation
			if ((animationEffects & EFFECT_FRAG) != 0) {
				Effect specialShot = new Effect(frame + DEFAULT_SHOTS_SPEED,
					targetX + offsetX,
					targetY + offsetY + (isTopFleet ? -40 : 40),
					Effect.TYPE_WEAPON_BONUS_1 + (isTopFleet ? 1 : 0));
				
				effectsPanel.add(specialShot);
				effects.add(specialShot);
			}
			
			// Animation - impact
			if ((animationEffects & EFFECT_IMPACT) != 0) {
				Effect explosion = new Effect(
					frame + DEFAULT_SHOTS_SPEED,
					(int) (targetX + Math.round(Math.random() * 31) - 15),
					(int) (targetY + (isTopFleet ? -25 : 25) +
							Math.round(Math.random() * 31) - 15),
					Effect.TYPE_EXPLOSION);
				
				effectsPanel.add(explosion);
				effects.add(explosion);
				
				if (Math.random() < .3 || k % 4 == 0)
					sounds.add(new Sound(frame + DEFAULT_SHOTS_SPEED, Sounds.IMPACT));
			}
		}
	}
	
	private void buildImpacts(int targetSlot, int[] slotsPosition,
			int frameOffset, int action) {
		// Calcule la position de la cible des impacts
		boolean isTopFleet = targetSlot < 5;
		boolean targetSlotFront = fleetsSlots[targetSlot].isFront(action);
		int targetPosition = slotsPosition[targetSlot];
		int targetX = targetPosition * 100 + 50;
		int targetY = (isTopFleet ? 300 : 0) + 150 +
				(targetSlotFront ? 0 : (isTopFleet ? 100 : -100));
		
		// Impact
		for (int k = 0; k < 10; k++) {
			int frame = (int) (4 * k +
					Math.round(Math.random() * 5) + frameOffset);
			
			Effect explosion = new Effect(
				frame + DEFAULT_SHOTS_SPEED,
				(int) (targetX + Math.round(Math.random() * 31) - 15),
				(int) (targetY + (isTopFleet ? -25 : 25) +
						Math.round(Math.random() * 31) - 15),
				Effect.TYPE_EXPLOSION);
			
			effectsPanel.add(explosion);
			effects.add(explosion);
			
			if (Math.random() < .3 || k % 4 == 0)
				sounds.add(new Sound(frame + DEFAULT_SHOTS_SPEED, Sounds.IMPACT));
		}
	}
	
	private void buildAttack(ReportActionData reportAction, int slotIndex,
			int action) {
		int otherFleetIndex = slotIndex < 5 ? 5 : 0;
		
		for (int i = 0; i < reportAction.getDamageCount(); i++) {
			ReportDamageData reportDamage = reportAction.getDamageAt(i);
			
			if (5 * (reportDamage.getTargetSlot() / 5) == otherFleetIndex) {
				int modifiers = 0;
				if (reportDamage.getAmount() > 0)
					modifiers |= EFFECT_IMPACT;
				if ((reportDamage.getModifiers() & ReportDamageData.CRITICAL_HIT) != 0)
					modifiers |= EFFECT_STARS;
				if ((reportDamage.getModifiers() & ReportDamageData.PARTICLES) != 0)
					modifiers |= EFFECT_PARTICLES;
				
				buildShots(slotIndex, reportDamage.getTargetSlot(),
						slotsPosition, modifiers, 0, action);
				buildDamage(reportDamage, 29, slotsPosition, action, true);
			} else {
				// Animation - impact
				buildImpacts(reportDamage.getTargetSlot(), slotsPosition,
						0, action);
				buildDamage(reportDamage, 29, slotsPosition, action, false);
			}
		}
	}
	
	private void buildDamage(ReportDamageData reportDamage, int frame,
			int[] slotsPosition, int action, boolean miss) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		int targetSlot = reportDamage.getTargetSlot();
		boolean isTopFleet = targetSlot < 5;
		int targetX = getSlotX(targetSlot);
		int targetY = getSlotY(targetSlot, action);
		
		int damage = reportDamage.getAmount();
		
		int offset = 0;
		
		// Texte - Coup critique
		if ((reportDamage.getModifiers() & ReportDamageData.CRITICAL_HIT) != 0) {
			FloatingText text = new FloatingText(frame + offset,
				"<div class=\"floatingText\" style=\"font-size: 10px !important;\">" +
				messages.criticalHitShot() + "</div>",
				targetX - 95, targetY + (isTopFleet ? -20 : 20), 200);
			
			effectsPanel.add(text);
			floatingTexts.add(text);
			
			offset -= 8;
		}
		
		// Texte - Esquive
		if ((reportDamage.getModifiers() & ReportDamageData.DODGED) != 0) {
			FloatingText text = new FloatingText(frame + offset,
				"<div class=\"floatingText\" style=\"font-size: 10px !important;\">" + messages.dodged() + "</div>",
				 targetX - 95, targetY + (isTopFleet ? -20 : 20), 200);
			
			effectsPanel.add(text);
			floatingTexts.add(text);
			
			offset -= 8;
		}
		
		// Texte - Sublimation
		if ((reportDamage.getModifiers() & ReportDamageData.SUBLIMATION) != 0) {
			FloatingText text = new FloatingText(frame + offset,
				"<div class=\"floatingText\" style=\"" +
				"font-size: 10px !important;\">Sublimation</div>",
				targetX - 95, targetY + (isTopFleet ? -20 : 20), 200);
			
			effectsPanel.add(text);
			floatingTexts.add(text);
			
			offset -= 8;
		}
		
		// Texte - Déphasage
		if ((reportDamage.getModifiers() & ReportDamageData.PHASED) != 0) {
			FloatingText text = new FloatingText(frame + offset,
				"<div class=\"floatingText\" style=\"" +
				"font-size: 10px !important;\">" + messages.phased() + "</div>",
				targetX - 95, targetY + (isTopFleet ? -20 : 20), 200);
			
			effectsPanel.add(text);
			floatingTexts.add(text);
			
			offset -= 8;
		}

		// Ressources volées
		for (int i = 0; i < reportDamage.getStealedResourcesCount(); i++) {
			if ((long) reportDamage.getStealedResourceAt(i) > 0) {
				FloatingText text = new FloatingText(frame + offset,
					"<div class=\"floatingText\" style=\"" +
					"font-size: 10px !important;\">-" +
					Formatter.formatNumber((long) reportDamage.getStealedResourceAt(i), true) +
					" <img src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" class=\"resource r" + i + "\"/></div>",
					targetX - 95, targetY + (isTopFleet ? -20 : 20), 200);
				
				effectsPanel.add(text);
				floatingTexts.add(text);
				
				offset -= 8;
			}
		}
		
		if (damage > 0) {
			// Affiche les dégats
			int kills = reportDamage.getKills();
			double targetHullModifier =
				fleetsToolTips[targetSlot].getHullModifier(action);
			
			FloatingText text = new FloatingText(frame + 8,
				"<div class=\"floatingText\" style=\"" +
				"font-size: 10px !important;\">" +
				(int) Math.floor(damage * targetHullModifier) +
				"<img class=\"stat s-damage\" src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\"/></div>",
				targetX - 103, targetY + (isTopFleet ? -20 : 20), 200);
			
			effectsPanel.add(text);
			floatingTexts.add(text);
			
			// Affiche le nombre de vaisseaux détruits
			if (kills > 0) {
				text = new FloatingText(frame + 16,
					"<div class=\"floatingText\">" + kills +
					"<img class=\"stat s-death\" src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\"/></div>",
					targetX - 97, targetY + (isTopFleet ? -20 : 20), 200);
				
				effectsPanel.add(text);
				floatingTexts.add(text);
			}
		} else {
			// Affiche un texte "échec" du tir
			FloatingText text = new FloatingText(frame + 16,
				"<div class=\"floatingText\">" +
				(miss ? messages.miss() : "Sans effet") + "</div>",
				targetX - 100, targetY + (isTopFleet ? -20 : 20), 200);
			
			effectsPanel.add(text);
			floatingTexts.add(text);
		}
	}
	
	private int getSlotX(int slotIndex) {
		return slotsPosition[slotIndex] * 100 + 50;
	}
	
	private int getSlotY(int slotIndex, int action) {
		return slotIndex < 5 ?
			(fleetsSlots[slotIndex].isFront(action) ? 425 : 525) :
			(fleetsSlots[slotIndex].isFront(action) ? 175 : 75);
	}
	
	private long[] computeCount(ReportData report, int position) {
		ReportSlotData reportSlot = report.getSlotAt(
			position < 5 ? 0 : 1, position < 5 ? position : position - 5);
		
		long[] count = new long[report.getActionsCount() + 1];
		
		// Calcule l'évolution du nombre de vaisseaux sur le slot au cours
		// du combat
		if (reportSlot.getId() != 0) {
			count[0] = reportSlot.getCount();
			
			for (int i = 0; i < report.getActionsCount(); i++) {
				ReportActionData reportAction = report.getActionsAt(i);
				count[i + 1] = count[i];
				
				for (int j = 0; j < reportAction.getDamageCount(); j++) {
					ReportDamageData reportDamage =
						reportAction.getDamageAt(j);
					
					if (reportDamage.getTargetSlot() == position) {
						count[i + 1] -= reportDamage.getKills();
						count[i + 1] = Math.max(count[i + 1], 0);
					}
				}
			}
		}
		
		return count;
	}
	
	private int[] computeHull(ReportData report, int position) {
		ReportSlotData reportSlot = report.getSlotAt(
			position < 5 ? 0 : 1, position < 5 ? position : position - 5);
		
		int[] hull = new int[report.getActionsCount() + 1];
		
		// Calcule l'évolution du nombre de vaisseaux sur le slot au cours
		// du combat
		if (reportSlot.getId() != 0) {
			int baseHull = ShipData.SHIPS[reportSlot.getId()].getHull();
			hull[0] = baseHull;
			
			for (int i = 0; i < report.getActionsCount(); i++) {
				ReportActionData reportAction = report.getActionsAt(i);
				hull[i + 1] = hull[i];
				
				for (int j = 0; j < reportAction.getDamageCount(); j++) {
					ReportDamageData reportDamage =
						reportAction.getDamageAt(j);
					
					if (reportDamage.getTargetSlot() == position)
						hull[i + 1] = baseHull - reportDamage.getHullDamage();
				}
			}
		}
		
		return hull;
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class XpView extends BaseWidget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private OutlineText xpValue;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public XpView(boolean top) {
			Element element = getElement();
			element.setClassName("xpView " + (top ? "top" : "bottom"));
			element.setAttribute("unselectable", "on");
			
			xpValue = TextManager.getText("");
			getElement().appendChild(xpValue.getElement());
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void setXp(int xp, double xpFactor) {
			xpValue.setText("<div class=\"xpLabel\" unselectable=\"on\">" +
				Utilities.getXpImage() + "&nbsp;&nbsp;" + xp + "</div>");
			
			StaticMessages messages =
				(StaticMessages) GWT.create(StaticMessages.class);
			
			setToolTipText("<div class=\"title\">" + messages.experience() + "</div>" +
				"<div class=\"justify\">L'expérience gagnée en combat " +
				"dépend du nombre de vaisseaux que vous détruisez et de " +
				"la puissance des flottes qui s'affrontent. Attaquer des " +
				"flottes de puissance moindre rapporte moins d'XP, et " +
				"inversement.</div><div class=\"emphasize\">Coefficient XP : " +
				(int) (xpFactor) + "%</div>", 180);
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class PowerBar extends BaseWidget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private Element powerBarContainer, currentPower, side;
		
		private OutlineText powerValue;
		
		private WidthUpdater updater;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public PowerBar(boolean top) {
			Element element = getElement();
			element.setClassName("powerBar " + (top ? "top" : "bottom"));
			element.setAttribute("unselectable", "on");
			
			powerBarContainer = DOM.createDiv();
			powerBarContainer.setClassName("container");
			powerBarContainer.setAttribute("unselectable", "on");
			element.appendChild(powerBarContainer);
			
			currentPower = DOM.createDiv();
			currentPower.setClassName("currentProgress");
			currentPower.setAttribute("unselectable", "on");
			powerBarContainer.appendChild(currentPower);
			
			side = DOM.createDiv();
			side.setClassName("side");
			side.setAttribute("unselectable", "on");
			side.setAttribute("style", "display:none;");
			if( top ){
				side.setInnerHTML("<b>Defenseur</b>");
			}else{
				side.setInnerHTML("<b>Attaquant</b>");
			}
			element.appendChild(side);
			
			powerValue = TextManager.getText("");
			powerBarContainer.appendChild(powerValue.getElement());
			
			if (Config.getGraphicsQuality() > Config.VALUE_QUALITY_LOW) {
				updater = new WidthUpdater(currentPower, 0);
				TimerManager.register(updater);
			}
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void setPower(int power) {
			int powerLevel = GroupData.getLevelAtPower(power);
			int currentPowerLevel = GroupData.getPowerAtLevel(powerLevel);
			int nextPowerLevel = GroupData.getPowerAtLevel(powerLevel + 1) - 1;
			
			StaticMessages messages =
				(StaticMessages) GWT.create(StaticMessages.class);
			
			setToolTipText(null);
			setToolTipText("<div class=\"title\">Puissance</div>" +
				"<div><b>Vaisseaux : <span class=\"emphasize\">" +
				Formatter.formatNumber(power) + "</span> / " +
				Formatter.formatNumber(nextPowerLevel) + "</b></div>" +
				"<div class=\"justify\">" + messages.swapPowerHelp() + "</div>", 200);
			
			setCurrentProgress((power - currentPowerLevel) /
					(double) (nextPowerLevel - currentPowerLevel));
			
			powerValue.setText("<div class=\"barLabel\" unselectable=\"on\">" +
				"Puissance " + powerLevel + "<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"stat s-power\"/></div>");
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
		
		private void setCurrentProgress(double width) {
			if (updater != null)
				updater.setTargetWidth(width);
			else
				currentPower.getStyle().setProperty(
						"width", Math.floor(100 * width) + "%");
		}
	}
	
	private class Slot extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int position;
		
		private int shipId;
		
		private boolean[] front;
		
		private long[] count;
		
		private boolean hyperspace;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Slot(int position) {
			setElement(DOM.createDiv());
			
			this.position = position;
			
			if (position < 5)
				addStyleName("bottomFleet");
			else
				addStyleName("topFleet");
		}

		// ----------------------------------------------------- METHODES -- //
		
		public void load(ReportData report) {
			ReportSlotData reportSlot = report.getSlotAt(
					position < 5 ? 0 : 1, position < 5 ? position : position - 5);
			
			hyperspace = (position < 5 ? report.getAttackerEnvironment() :
				report.getDefenderEnvironment()).contains("hyperspace");
			shipId = reportSlot.getId();
			count = computeCount(report, position);
			
			front = new boolean[report.getActionsCount() + 1];
			front[0] = reportSlot.isFront();
			for (int i = 0; i < report.getActionsCount(); i++)
				front[i + 1] = report.getActionsAt(i).isFrontSlot(position);
		}
		
		public int getShipId() {
			return shipId;
		}
		
		public long getCount(int action) {
			return count[action == -1 ? 0 : action];
		}

		public boolean isFront(int action) {
			return front[action == -1 ? 0 : action];
		}
		
		public void setAction(int action) {
			if (action == -1)
				action = 0;
			
			DynamicMessages dynamicMessages =
				(DynamicMessages) GWT.create(DynamicMessages.class);
			
			// Affiche le vaisseau
			if (shipId != 0 && count[action] > 0) {
				OutlineText label = TextManager.getText(
					"<div class=\"spaceship-label\"><span class=\"type\">" +
					dynamicMessages.getString("ships" + shipId) + "</span>" +
					"<br/>" + count[action] + "</div>");
				label.getElement().getStyle().setProperty("position", "absolute");
				label.getElement().getStyle().setProperty("top", (position < 5 ? 54 : 22) + "px");
				label.setWidth(100);
				
				getElement().setInnerHTML((hyperspace ?
					"<div class=\"hyperspace hyperspaceIn\" style=\"" +
					"margin: " + (position < 5 ? -2 : 48) +
					"px 0 0 23px;\"></div>" : "") +
					"<div class=\"spaceship\" " +
					"style=\"background-position: -" +
					(shipId * 50) + "px 0\"></div>");
				
				getElement().appendChild(label.getElement());
				setVisible(true);
			} else {
				getElement().setInnerHTML("");
				setVisible(false);
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}

	private class SlotToolTip extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int position;

		private int shipId;
		
		private int availableAbilities;
		
		private long[] count;
		
		private int[] hull;
		
		private int[] protectionModifiers;
		
		private double[] damageModifiers;
		
		private double[] hullModifiers;
		
		private double damageFactor;
		
		private String[] extraTexts;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public SlotToolTip(int position) {
			setElement(DOM.createDiv());
			
			this.position = position;
			
			addStyleName("fleetToolTip");
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void load(ReportData report) {
			ReportSlotData reportSlot = report.getSlotAt(
					position < 5 ? 0 : 1, position < 5 ? position : position - 5);
			
			damageFactor = (position < 5 ?
					report.getAttackerDamageFactor() :
					report.getDefenderDamageFactor());
			
			shipId = reportSlot.getId();
			count = computeCount(report, position);
			hull = computeHull(report, position);
			availableAbilities = reportSlot.getAvailableAbilities();
			protectionModifiers = new int[report.getActionsCount() + 1];
			damageModifiers = new double[report.getActionsCount() + 1];
			hullModifiers = new double[report.getActionsCount() + 1];
			extraTexts = new String[report.getActionsCount() + 1];
			
			for (int i = 0; i < extraTexts.length; i++)
				extraTexts[i] = "";
			
			if (shipId != 0) {
				protectionModifiers[report.getActionsCount()] = 0;
				damageModifiers[report.getActionsCount()] = 1 * damageFactor;
				hullModifiers[report.getActionsCount()] = 1;
				
				for (int i = 0; i < report.getActionsCount(); i++) {
					ReportActionData reportAction = report.getActionsAt(i);
					
					if (count[i] > 0) {
						for (int j = 0; j < reportAction.getSlotStatesCount(); j++) {
							ReportSlotStateData reportSlotState =
								reportAction.getSlotStateAt(j);
							
							if (reportSlotState.getSlotIndex() == position) {
								protectionModifiers[i] = reportSlotState.getProtectionModifier();
								damageModifiers[i] = reportSlotState.getDamageModifier() * damageFactor;
								hullModifiers[i] = reportSlotState.getHullModifier();
								break;
							}
						}
					}
				}
			}
		}
		
		public void setAction(int action) {
			if (shipId != 0 && count[action == -1 ? 0 : action] > 0) {
				ToolTipManager.getInstance().register(
					getElement(), ShipData.getDesc(shipId, 1,
						availableAbilities,
						ShipData.SHOW_PASSIVE_ABILITIES,
						action == -1 ? ShipData.SHIPS[shipId].getHull() : hull[action],
						action == -1 ? 0 : protectionModifiers[action],
						action == -1 ? 1 * damageFactor : damageModifiers[action],
						action == -1 ? 1 : hullModifiers[action]) +
						extraTexts[action == -1 ? 0 : action], 200);
				setVisible(true);
			} else {
				ToolTipManager.getInstance().unregister(getElement());
				setVisible(false);
			}
		}
		
		public double getHullModifier(int action) {
			return hullModifiers[action == -1 ? 0 : action];
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class Shot extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int frame;
		
		private int startX, startY, endX, endY;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Shot(int frame, int startX, int startY, int endX, int endY,
				int angle, int shipClass) {
			setElement(DOM.createDiv());
			setStyleName("shoot top");
			getElement().getStyle().setProperty("backgroundPosition",
				"-" + (angle * 30) + "px " +
				"-" + (1320 + (startY < endY ? 90 : 0) + ((shipClass - 1) / 2) * 30) + "px");
			setVisible(false);
			
			this.frame = frame;
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
		}

		// ----------------------------------------------------- METHODES -- //
		
		public int getFrame() {
			return frame;
		}
		
		public int getStartX() {
			return startX;
		}

		public int getStartY() {
			return startY;
		}
		
		public int getEndX() {
			return endX;
		}

		public int getEndY() {
			return endY;
		}

		// --------------------------------------------- METHODES PRIVEES -- //
	}

	private class TimeShift {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //

		private int frame;
		
		private int slotIndex;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public TimeShift(int frame, int slotIndex) {
			this.frame = frame;
			this.slotIndex = slotIndex;
		}

		// ----------------------------------------------------- METHODES -- //
		
		public int getFrame() {
			return frame;
		}

		public int getSlotIndex() {
			return slotIndex;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class Effect extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		
		public final static int
			TYPE_EXPLOSION		=  1,
			TYPE_DESTRUCTION	=  2,
			TYPE_SPECIAL_SHOT_1	=  3,
			TYPE_SPECIAL_SHOT_2	=  4,
			TYPE_SPECIAL_SHOT_3	=  5,
			TYPE_SPECIAL_SHOT_4	=  6,
			TYPE_REGENERATION	=  7,
			TYPE_FORCE_FIELD	=  8,
			TYPE_WEAPON_BONUS_1	=  9,
			TYPE_WEAPON_BONUS_2	= 10,
			TYPE_BUFF_1			= 11,
			TYPE_BUFF_2			= 12,
			TYPE_DEBUFF_1		= 13,
			TYPE_DEBUFF_2		= 14,
			TYPE_RETRIBUTION	= 15,
			TYPE_TAUNT			= 16,
			TYPE_PARTICLE_1		= 17,
			TYPE_PARTICLE_2		= 18,
			TYPE_PARTICLE_3		= 19,
			TYPE_PARTICLE_4		= 20,
			TYPE_PARTICLE_5		= 21,
			TYPE_PARTICLE_6		= 22;
		
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int frame;
		
		private int type;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Effect(int frame, int x, int y, int type) {
			setElement(DOM.createDiv());
			setStyleName("effect");
			getElement().getStyle().setProperty("left", x + "px");
			getElement().getStyle().setProperty("top",  y + "px");
			setVisible(false);
			
			this.frame = frame;
			this.type = type;
		}

		// ----------------------------------------------------- METHODES -- //
		
		public int getFrame() {
			return frame;
		}
		
		public int getType() {
			return type;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class FloatingText extends SimplePanel {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int frame;
		
		private int x, y;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public FloatingText(int frame, String text, int x, int y, int width) {
			this.frame = frame;
			getElement().getStyle().setProperty("position", "absolute");
			OutlineText outlineText = TextManager.getText(text);
			add(outlineText);
			setLocation(x, y);
			outlineText.setWidth(width);
			setVisible(false);
		}

		// ----------------------------------------------------- METHODES -- //
		
		public int getFrame() {
			return frame;
		}
		
		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
		
		public void setLocation(int x, int y) {
			this.x = x;
			this.y = y;
			getElement().getStyle().setProperty("left", x + "px");
			getElement().getStyle().setProperty("top", y + "px");
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}

	private class Sound {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //

		private int frame;
		
		private String soundName;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Sound(int frame, String soundName) {
			this.frame = frame;
			this.soundName = soundName;
		}

		// ----------------------------------------------------- METHODES -- //
		
		public int getFrame() {
			return frame;
		}

		public String getSound() {
			return soundName;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class Destruction {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //

		private int frame;
		
		private int slotIndex;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Destruction(int frame, int slotIndex) {
			this.frame = frame;
			this.slotIndex = slotIndex;
		}

		// ----------------------------------------------------- METHODES -- //
		
		public int getFrame() {
			return frame;
		}

		public int getSlotIndex() {
			return slotIndex;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class Ability extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int action;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Ability(int action, int ability, String toolTip) {
			this.action = action;
			
			setElement(DOM.createDiv());
			setStylePrimaryName("action");
			
			if (action != -1) {
				int graphics = ability == -1 ? 0 : AbilityData.GRAPHICS[ability];
				getElement().setInnerHTML("<div id=\"a"+ action +"\" class=\"actionAbility\" " +
					"style=\"background-position: -" +
					(graphics * 25) + "px -100px;\">" +
					"<div class=\"selection\"></div></div>");
				
				ToolTipManager.getInstance().register(
						getElement(), toolTip, 220);
			}
			
			sinkEvents(Event.ONCLICK | Event.KEYEVENTS);
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void setSelected(boolean selected) {
			if (selected)
				addStyleDependentName("selected");
			else
				removeStyleDependentName("selected");
		}
		
		@Override
		public void onBrowserEvent(Event event) {
			switch (event.getTypeInt()) {
			case Event.ONCLICK:
				if (action != -1)
					setAction(action, reports.get(currentReportId));
				break;
			case Event.ONKEYDOWN:
				onEventPreview(event);
				break;
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class BattleUpdater implements TimerHandler {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int length;
		
		private int frame;
		
		private boolean pause;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public BattleUpdater(int length) {
			this.length = length;
			this.frame = 0;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public boolean isPaused() {
			return pause;
		}
		
		public void setPaused(boolean pause) {
			this.pause = pause;
		}
		
		public boolean isFinished() {
			return false;
		}
		
		public void update(int interpolation) {
			if (pause)
				return;
			
			// Mise à jour de l'animation des tirs
			for (int i = 0; i < shots.size(); i++) {
				Shot shot = shots.get(i);
				
				if (frame == shot.getFrame()) {
					shot.getElement().getStyle().setProperty("left", shot.getStartX() + "px");
					shot.getElement().getStyle().setProperty("top",  shot.getStartY() + "px");
					shot.setVisible(true);
				} else if (frame > shot.getFrame() && shot.isVisible()) {
					double percent = (frame - shot.getFrame()) / (double) DEFAULT_SHOTS_SPEED;
					
					if (percent >= 1) {
						shot.setVisible(false);
					} else {
						shot.getElement().getStyle().setProperty("left", (int) Math.round(
								(1 - percent) * shot.getStartX() + percent * shot.getEndX()) + "px");
						shot.getElement().getStyle().setProperty("top",  (int) Math.round(
								(1 - percent) * shot.getStartY() + percent * shot.getEndY()) + "px");
					}
				}
			}
			
			// Mise à jour des animations (explosions...)
			for (Effect effect : effects) {
				int length = 0;
				int offsetX = 0, offsetY = 0;
				int df = frame - effect.getFrame();
				int size = 0;
				
				switch (effect.getType()) {
				case Effect.TYPE_EXPLOSION:
					length = 12;
					offsetX = df * 32;
					offsetY = 450;
					size = 32;
					break;
				case Effect.TYPE_DESTRUCTION:
					length = 12;
					offsetX = (df % 5) * 100;
					offsetY = 150 + (df / 5) * 100;
					size = 100;
					break;
				case Effect.TYPE_SPECIAL_SHOT_1:
				case Effect.TYPE_SPECIAL_SHOT_2:
				case Effect.TYPE_SPECIAL_SHOT_3:
				case Effect.TYPE_SPECIAL_SHOT_4:
					length = 2;
					offsetX = 200 + (effect.getType() - Effect.TYPE_SPECIAL_SHOT_1) * 13;
					offsetY = 430;
					size = 13;
					break;
				case Effect.TYPE_PARTICLE_1:
				case Effect.TYPE_PARTICLE_2:
				case Effect.TYPE_PARTICLE_3:
				case Effect.TYPE_PARTICLE_4:
				case Effect.TYPE_PARTICLE_5:
				case Effect.TYPE_PARTICLE_6:
					length = 1;
					offsetX = 252 + (effect.getType() - Effect.TYPE_PARTICLE_1) * 20;
					offsetY = 430;
					size = 20;
					break;
				case Effect.TYPE_REGENERATION:
					length = 11;
					offsetX = (df % 6) * 75;
					offsetY = (df / 6) * 75;
					size = 75;
					break;
				case Effect.TYPE_FORCE_FIELD:
					length = 2000;
					offsetX = 350;
					offsetY = 350;
					size = 80;
					break;
				case Effect.TYPE_WEAPON_BONUS_1:
				case Effect.TYPE_WEAPON_BONUS_2:
					length = 3;
					offsetX = 200 + df * 40;
					offsetY = 350 + (effect.getType() - Effect.TYPE_WEAPON_BONUS_1) * 40;
					size = 40;
					break;
				case Effect.TYPE_BUFF_1:
				case Effect.TYPE_BUFF_2:
					length = 4;
					offsetX = 500;
					offsetY = df * 9 + 90 * (effect.getType() - Effect.TYPE_BUFF_1);
					size = 60;
					break;
				case Effect.TYPE_DEBUFF_1:
				case Effect.TYPE_DEBUFF_2:
					length = 4;
					offsetX = 500;
					offsetY = -df * 9 + 216 + 90 * (effect.getType() - Effect.TYPE_DEBUFF_1);
					size = 60;
					break;
				case Effect.TYPE_RETRIBUTION:
					length = 100;
					offsetX = 430;
					offsetY = 350;
					size = 70;
					break;
				case Effect.TYPE_TAUNT:
					length = 5;
					offsetX = df * 70;
					offsetY = 482;
					size = 70;
					break;
				}
				
				if (frame >= effect.getFrame() && frame < effect.getFrame() + length) {
					// Mise à jour de l'animation
					effect.getElement().getStyle().setProperty("backgroundPosition",
							"-" + offsetX + "px -" + offsetY + "px");
					
					if (frame == effect.getFrame()) {
						effect.setSize(size + "px", size + "px");
						effect.getElement().getStyle().setProperty("margin",
								(-size / 2) + "px 0 0 " + (-size / 2) + "px");
						effect.setVisible(true);
					}
					
					// Secoue l'écran lorsqu'un vaisseau est détruit
					if (effect.getType() == Effect.TYPE_DESTRUCTION) {
						String offset = frame > effect.getFrame() + length / 2 ?
								(int) (Math.random() * 5  - 2) + "px 0 0 " + (int) (Math.random() * 5  - 2) + "px" :
								(int) (Math.random() * 11 - 5) + "px 0 0 " + (int) (Math.random() * 11 - 5) + "px";
						
						Client.getInstance().getFullScreenPanel().getElement().getStyle().setProperty("margin", offset);
						Client.getInstance().getAreaContainer().getElement().getStyle().setProperty("margin", offset);
					}
				} else if (frame == effect.getFrame() + length) {
					// Fin de l'animation
					effect.setVisible(false);
					
					if (effect.getType() == Effect.TYPE_DESTRUCTION) {
						Client.getInstance().getFullScreenPanel().getElement().getStyle().setProperty("margin", "");
						Client.getInstance().getAreaContainer().getElement().getStyle().setProperty("margin", "");
					}
				}
			}

			// Mise à jour de l'animation des textes
			for (FloatingText floatingText : floatingTexts) {
				if (frame == floatingText.getFrame()) {
					floatingText.setVisible(true);
				} else if (frame >= floatingText.getFrame() && frame < floatingText.getFrame() + 25) {
					floatingText.setLocation(
						floatingText.getX(), floatingText.getY() - 2);
				} else if (frame == floatingText.getFrame() + 25) {
					floatingText.setVisible(false);
				}
			}
			
			// Mise à jour des sons
			for (Sound sound : sounds) {
				if (frame == sound.getFrame()) {
					SoundManager.getInstance().playSound(sound.getSound());
				}
			}
			
			// Mise à jour de l'affichage des vaisseaux détruits
			for (Destruction destruction : destructions) {
				if (frame == destruction.getFrame()) {
					fleetsSlots[destruction.getSlotIndex()].getElement().setInnerHTML("");
					ToolTipManager.getInstance().unregister(
							fleetsToolTips[destruction.getSlotIndex()].getElement());
				}
			}

			// Faille temporelle
			if (timeShift != null && frame >= timeShift.getFrame() &&
					frame < timeShift.getFrame() + 25) {
				int dframe = frame - timeShift.getFrame();
				
				if (dframe <= 10) {
					OpenJWT.setElementOpacity(
						fleetsSlots[timeShift.getSlotIndex()].getElement(),
						1 - dframe / 10.);
				} else if (dframe > 15) {
					OpenJWT.setElementOpacity(
						fleetsSlots[timeShift.getSlotIndex()].getElement(),
						(dframe - 15) / 10.);
				}
				
				if (dframe == 10)
					fleetsSlots[timeShift.getSlotIndex()].setAction(currentAction + 1);
			}
			
			frame++;
			
			// Round suivant si le round est fini
			if (frame == length) {
				ReportData report = reports.get(currentReportId);
				
				if (currentAction < report.getActionsCount()) {
					setAction(currentAction + 1, report);
				}
			}
		}
		
		public void destroy() {
			// Sans effet
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
