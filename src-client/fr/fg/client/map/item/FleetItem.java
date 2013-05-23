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

package fr.fg.client.map.item;

import com.allen_sauer.gwt.voices.client.Sound;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

import fr.fg.client.animation.BackgroundUpdater;
import fr.fg.client.animation.LoopClassNameUpdater;
import fr.fg.client.animation.ToolTipTextUpdater;
import fr.fg.client.animation.ToolTipTimeUpdater;
import fr.fg.client.core.Client;
import fr.fg.client.core.ProgressBar;
import fr.fg.client.core.Utilities;
import fr.fg.client.data.AbilityData;
import fr.fg.client.data.FleetData;
import fr.fg.client.data.GroupData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.ShipInfoData;
import fr.fg.client.data.SkillData;
import fr.fg.client.data.SlotInfoData;
import fr.fg.client.data.Sounds;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.OpacityUpdater;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.SoundManager;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.ToolTipListener;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class FleetItem extends AnimatedItem implements EventListener, ToolTipListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static String
		OFFENSIVE_LINK_ID = "linkOffensive", //$NON-NLS-1$
		DEFENSIVE_LINK_ID = "linkDefensive"; //$NON-NLS-1$

	static {
		SoundManager.getInstance().loadSound(Sounds.ENGINE);
		SoundManager.getInstance().loadSound(Sounds.HYPERSPACE);
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static EngineSoundUpdater engineSoundUpdater, hyperspaceSoundUpdater;
	
	private FleetData fleetData;
	
	private Element offensiveLink, defensiveLink, graphics, owner,
		fleetOutline, hyperspace, npcAction, hullBar, currentFleet;
	
	private OutlineText powerLevel;
	
	private BackgroundUpdater
		offensiveLinkUpdaterSelf, offensiveLinkUpdaterTarget,
		defensiveLinkUpdaterSelf, defensiveLinkUpdaterTarget;
	
	private ToolTipTimeUpdater movementReloadUpdater, hyperspaceUpdater,
		jumpReloadUpdater, colonizationUpdater,migrationUpdater;
	
	private LoopClassNameUpdater fleetOutlineUpdater;
	
	private OpacityUpdater hyperspaceOpacityUpdater;
	
	private boolean selected;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FleetItem(FleetData fleetData, UIItemRenderingHints hints) {
		super(fleetData.getX(), fleetData.getY(), hints);
		
		this.fleetData = fleetData;
		
		// Element flotte
		Element fleet = getElement();
		fleet.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		setStylePrimaryName("fleet"); //$NON-NLS-1$
		
		// Flotte qui entre / sort d'hyperespace
		hyperspace = DOM.createDiv();
		hyperspace.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		hyperspace.setPropertyString("className", "hyperspace"); //$NON-NLS-1$ //$NON-NLS-2$
		fleet.appendChild(hyperspace);
		
		// Lien offensif
		offensiveLink = DOM.createDiv();
		offensiveLink.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		offensiveLink.setClassName("link");
		fleet.appendChild(offensiveLink);
		
		// Lien défensif
		defensiveLink = DOM.createDiv();
		defensiveLink.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		defensiveLink.setClassName("link");
		fleet.appendChild(defensiveLink);
		
		// Cercle autour de la flotte
		fleetOutline = DOM.createDiv();
		fleetOutline.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		fleet.appendChild(fleetOutline);
		
		// Barre de puissance
		hullBar = DOM.createDiv();
		hullBar.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		hullBar.setClassName("hullBar");
		hullBar.getStyle().setProperty("display", "none");
		fleet.appendChild(hullBar);

		
		// Barre de puissance de la flotte actuelle
		currentFleet = DOM.createDiv();
		currentFleet.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		currentFleet.setClassName("currentFleet");
		currentFleet.getStyle().setProperty("display", "none");
		hullBar.appendChild(currentFleet);
		
		// Image de la flotte
		graphics = DOM.createDiv();
		graphics.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		graphics.setPropertyString("className", "graphics"); //$NON-NLS-1$ //$NON-NLS-2$
		fleet.appendChild(graphics);
		
		// Nom du propriétaire de la flotte
		owner = DOM.createDiv();
		owner.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		fleet.appendChild(owner);

		// Action flotte PNJ
		npcAction = DOM.createDiv();
		npcAction.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		fleet.appendChild(npcAction);
		
		// Puissance de la flotte
		powerLevel = TextManager.getText("");
		powerLevel.setVisible(false);
		fleet.appendChild(powerLevel.getElement());
		
		updateData(fleetData, false);
		updateRendering();
		
		sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setHullBarVisible(boolean visible) {
		hullBar.getStyle().setProperty("display", visible ? "" : "block");
	}
	
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		ToolTipManager.getInstance().addToolTipListener(this);
	}
	
	@Override
	protected void onUnload() {
		super.onLoad();
		
		ToolTipManager.getInstance().removeToolTipListener(this);
	}
	
	public void onToolTipOpening(com.google.gwt.dom.client.Element element) {
		if (element != getElement())
			return;
		
		if (movementReloadUpdater != null) {
			movementReloadUpdater.synchronize();
			TimerManager.register(movementReloadUpdater, TimerManager.SECOND_UNIT);
		}
		if (hyperspaceUpdater != null) {
			hyperspaceUpdater.synchronize();
			TimerManager.register(hyperspaceUpdater, TimerManager.SECOND_UNIT);
		}
		if (jumpReloadUpdater != null) {
			jumpReloadUpdater.synchronize();
			TimerManager.register(jumpReloadUpdater, TimerManager.SECOND_UNIT);
		}
		if (colonizationUpdater != null) {
			colonizationUpdater.synchronize();
			TimerManager.register(colonizationUpdater, TimerManager.SECOND_UNIT);
		}
		if (migrationUpdater != null) {
			migrationUpdater.synchronize();
			TimerManager.register(migrationUpdater, TimerManager.SECOND_UNIT);
		}
	}
	
	public void onToolTipClosed(com.google.gwt.dom.client.Element element) {
		if (element != getElement())
			return;
		
		if (movementReloadUpdater != null)
			TimerManager.unregister(movementReloadUpdater);
		if (hyperspaceUpdater != null)
			TimerManager.unregister(hyperspaceUpdater);
		if (jumpReloadUpdater != null)
			TimerManager.unregister(jumpReloadUpdater);
		if (colonizationUpdater != null)
			TimerManager.unregister(colonizationUpdater);
		if (migrationUpdater != null)
			TimerManager.unregister(migrationUpdater);
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		
		// Cercle indiquant le propriétaire de la flotte
		fleetOutline.setPropertyString("className", "fleetOutline " + //$NON-NLS-1$ //$NON-NLS-2$
			"fleetOutline-" + (selected ? "selected" : //$NON-NLS-1$ //$NON-NLS-2$
			(fleetData.isPirateFleet() ? "enemy" :
			!fleetData.getNpcAction().equals("none") || (
				fleetData.isNeutralFleet() &&
				fleetData.isAi() && !fleetData.isReserved()) ?
			"npc" : ((fleetData.isAlliedFleet() && !fleetData.isAllyFleet())?
					"allied" : fleetData.getTreaty())
			))); //$NON-NLS-1$
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEOVER:
			if (event.getFromElement() == null ||
					getElement().isOrHasChild(event.getFromElement()))
				return;
			
			showFleetOutline(true);
			startLinksAnimation();
			break;
		case Event.ONMOUSEOUT:
			if (event.getToElement() == null ||
					getElement().isOrHasChild(event.getToElement()))
				return;
			
			showFleetOutline(false);
			stopLinksAnimation();
			break;
		}
	}
	
	public void showFleetOutline(boolean show) {
		if (fleetData.isPlayerFleet() &&
				fleetOutlineUpdater != null) {
			if (show) {
				fleetOutlineUpdater.loopTargetClass(0, 3);
				TimerManager.register(fleetOutlineUpdater);
			} else {
				fleetOutlineUpdater.setTargetClass(0);
				fleetOutlineUpdater.setCurrentClass(0);
				TimerManager.unregister(fleetOutlineUpdater);
			}
		}
	}
	
	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		updateRendering();
	}
	
	@Override
	public void onDataUpdate(Object newData) {
		FleetData newFleetData = (FleetData) newData;
		
		if (fleetData.getX() != newFleetData.getX() ||
			fleetData.getY() != newFleetData.getY()) {
			
			setLocation(newFleetData.getX(), newFleetData.getY(), true);
			
			if (engineSoundUpdater == null || engineSoundUpdater.isFinished()) {
				Sound sound = SoundManager.getInstance().getSound(Sounds.ENGINE);
				if (sound != null) {
					engineSoundUpdater = new EngineSoundUpdater(this, sound, 0);
					TimerManager.register(engineSoundUpdater);
				}
			}
		}
		
		boolean updateRendering =
			fleetData.getSkin() != newFleetData.getSkin() ||
			fleetData.isDelude() != newFleetData.isDelude() ||
			fleetData.isImmobilized() != newFleetData.isImmobilized();
		
		boolean updateData = false;
		
		// Cherche les modifications dans les estimations des
		// quantités de classes de vaisseaux
		if (fleetData.hasClasses() && newFleetData.hasClasses()) {
			if (fleetData.getClassesCount() != newFleetData.getClassesCount()) {
				updateData = true;
			} else {
				for (int k = 0; k < fleetData.getClassesCount(); k++)
					if (fleetData.getClassAt(k) !=
							newFleetData.getClassAt(k)) {
						updateData = true;
						break;
					}
			}
		}
		
		// Cherche les modifications dans les estimations des
		// quantités de vaisseaux
		if (fleetData.hasShips() && newFleetData.hasShips()) {
			if (fleetData.getShipsCount() != newFleetData.getShipsCount()) {
				updateData = true;
			} else {
				for (int k = 0; k < fleetData.getShipsCount(); k++)
					if (fleetData.getShipAt(k).getId() != newFleetData.getShipAt(k).getId() ||
						fleetData.getShipAt(k).getClasses() != newFleetData.getShipAt(k).getClasses()) {
						updateData = true;
						break;
					}
			}
		}
		
		// Cherche les modifications dans les tactiques
		if (fleetData.getSkirmishAbilitiesCount() == newFleetData.getSkirmishAbilitiesCount()) {
			for (int i = 0; i < fleetData.getSkirmishAbilitiesCount(); i++)
				if (fleetData.getSkirmishAbilityAt(i) != newFleetData.getSkirmishAbilityAt(i)) {
					updateData = true;
					break;
				}
		}
		
		if (fleetData.getBattleAbilitiesCount() == newFleetData.getBattleAbilitiesCount()) {
			for (int i = 0; i < fleetData.getBattleAbilitiesCount(); i++)
				if (fleetData.getBattleAbilityAt(i) != newFleetData.getBattleAbilityAt(i)) {
					updateData = true;
					break;
				}
		}
		
		if (fleetData.isPlayerFleet() &&
				newFleetData.isPlayerFleet()) {
			if (fleetData.getOffensiveLinkedFleetId() != 0 &&
					newFleetData.getOffensiveLinkedFleetId() != 0) {
				for (int i = 0; i < fleetData.getSlotsCount(); i++)
					if (fleetData.getOffensiveLinkedCount(i) !=
							newFleetData.getOffensiveLinkedCount(i)) {
						updateData = true;
						break;
					}
			}
			
			if (fleetData.getDefensiveLinkedFleetId() != 0 &&
					newFleetData.getDefensiveLinkedFleetId() != 0) {
				for (int i = 0; i < fleetData.getSlotsCount(); i++)
					if (fleetData.getDefensiveLinkedCount(i) !=
							newFleetData.getDefensiveLinkedCount(i)) {
						updateData = true;
						break;
					}
			}
		}

		boolean updateHyperspace = false;
		if (fleetData.getStartJumpRemainingTime() == 0) {
			if (newFleetData.getStartJumpRemainingTime() != 0)
				updateHyperspace = true;
		} else if (fleetData.getStartJumpRemainingTime() > 0) {
			if (newFleetData.getStartJumpRemainingTime() <= 0)
				updateHyperspace = true;
		} else if (fleetData.getStartJumpRemainingTime() < 0) {
			if (newFleetData.getStartJumpRemainingTime() >= 0)
				updateHyperspace = true;
		}
		
		if (updateData || updateHyperspace ||
			fleetData.getVersion() != newFleetData.getVersion() ||
			fleetData.isConnected() != newFleetData.isConnected() ||
			fleetData.isAway() != newFleetData.isAway() ||
			!fleetData.getTreaty().equals(newFleetData.getTreaty()) ||
			fleetData.isConnected() != newFleetData.isConnected() ||
			!fleetData.getAlly().equals(newFleetData.getAlly()) ||
			!fleetData.getAllyTag().equals(newFleetData.getAllyTag()) ||
			fleetData.getSkirmishAbilitiesCount() != newFleetData.getSkirmishAbilitiesCount() ||
			fleetData.getBattleAbilitiesCount() != newFleetData.getBattleAbilitiesCount() ||
			(!fleetData.hasClasses() &&  newFleetData.hasClasses()) ||
			( fleetData.hasClasses() && !newFleetData.hasClasses()) ||
			(!fleetData.hasSlots()   &&  newFleetData.hasSlots()) ||
			( fleetData.hasSlots()   && !newFleetData.hasSlots()) ||
			(!fleetData.hasShips()   &&  newFleetData.hasShips()) ||
			( fleetData.hasShips()   && !newFleetData.hasShips()) ||
			!fleetData.getNpcAction().equals(newFleetData.getNpcAction()) ||
			fleetData.isReserved() != newFleetData.isReserved()) {
			updateData(newFleetData, updateHyperspace);
		}
		
		fleetData = newFleetData;
		
		if (updateRendering)
			onRenderingHintsUpdate();
	}
	
	public void setPowerLevelVisible(boolean visible) {
		powerLevel.setVisible(visible);
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		stopTimers();
		
		if (engineSoundUpdater != null && engineSoundUpdater.fleetItem == this) {
			TimerManager.unregister(engineSoundUpdater);
			engineSoundUpdater.destroy();
			engineSoundUpdater = null;
		}
		
		if (hyperspaceSoundUpdater != null && hyperspaceSoundUpdater.fleetItem == this) {
			TimerManager.unregister(hyperspaceSoundUpdater);
			hyperspaceSoundUpdater.destroy();
			hyperspaceSoundUpdater = null;
		}
		
		fleetData = null;
		offensiveLink = null;
		defensiveLink = null;
		graphics = null;
		owner = null;
		fleetOutline = null;
		hyperspace = null;
		npcAction = null;
		powerLevel = null;
		hullBar = null;
	}
	
	public static String getQualifier(int qualifier, String qualified) {
		StaticMessages staticMessages = GWT.create(StaticMessages.class);
		
		switch (qualifier) {
		case 1:
			return staticMessages.qualifier1(qualified);
		case 2:
			return staticMessages.qualifier2(qualified);
		case 3:
			return staticMessages.qualifier3(qualified);
		case 4:
			return staticMessages.qualifier4(qualified);
		case 5:
			return staticMessages.qualifier5(qualified);
		case 6:
			return staticMessages.qualifier6(qualified);
		case 7:
			return staticMessages.qualifier7(qualified);
		case 8:
			return staticMessages.qualifier8(qualified);
		case 9:
			return staticMessages.qualifier9(qualified);
		default:
			return ""; //$NON-NLS-1$
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void startTimers() {
		if (hyperspaceOpacityUpdater != null)
			TimerManager.register(hyperspaceOpacityUpdater);
	}
	
	private void stopTimers() {
		if (offensiveLinkUpdaterSelf != null) {
			TimerManager.unregister(offensiveLinkUpdaterSelf);
			offensiveLinkUpdaterSelf.destroy();
			offensiveLinkUpdaterSelf = null;
		}
		
		if (offensiveLinkUpdaterTarget != null) {
			TimerManager.unregister(offensiveLinkUpdaterTarget);
			offensiveLinkUpdaterTarget.destroy();
			offensiveLinkUpdaterTarget = null;
		}
		
		if (defensiveLinkUpdaterSelf != null) {
			TimerManager.unregister(defensiveLinkUpdaterSelf);
			defensiveLinkUpdaterSelf.destroy();
			defensiveLinkUpdaterSelf = null;
		}
		
		if (defensiveLinkUpdaterTarget != null) {
			TimerManager.unregister(defensiveLinkUpdaterTarget);
			defensiveLinkUpdaterTarget.destroy();
			defensiveLinkUpdaterTarget = null;
		}
		
		if (movementReloadUpdater != null) {
			TimerManager.unregister(movementReloadUpdater);
			movementReloadUpdater.destroy();
			movementReloadUpdater = null;
		}
		
		if (hyperspaceUpdater != null) {
			TimerManager.unregister(hyperspaceUpdater);
			hyperspaceUpdater.destroy();
			hyperspaceUpdater = null;
		}
		
		if (jumpReloadUpdater != null) {
			TimerManager.unregister(jumpReloadUpdater);
			jumpReloadUpdater.destroy();
			jumpReloadUpdater = null;
		}
		
		if (colonizationUpdater != null) {
			TimerManager.unregister(colonizationUpdater);
			colonizationUpdater.destroy();
			colonizationUpdater = null;
		}
		
		if (migrationUpdater != null) {
			TimerManager.unregister(migrationUpdater);
			migrationUpdater.destroy();
			migrationUpdater = null;
		}
		
		if (fleetOutlineUpdater != null) {
			TimerManager.unregister(fleetOutlineUpdater);
			fleetOutlineUpdater.destroy();
			fleetOutlineUpdater = null;
		}
		
		if (hyperspaceOpacityUpdater != null) {
			TimerManager.unregister(hyperspaceOpacityUpdater);
			hyperspaceOpacityUpdater.destroy();
			hyperspaceOpacityUpdater = null;
		}
	}
	
	private void startLinksAnimation() {
		if (fleetData.getOffensiveLinkedFleetId() != 0) {
			// Active l'animation du lien offensif sur la flotte actuelle
			int startY = hints.getZoom() == 1 ? 851 : 905;
			int offsetY = hints.getZoom() == 1 ? 54 : 27;
			
			offensiveLinkUpdaterSelf = new BackgroundUpdater(offensiveLink,
					new Point(0, startY),
					new Point((int) (432 * hints.getZoom()), startY),
					new Point(offsetY, 0));
			TimerManager.register(offensiveLinkUpdaterSelf);
			
			// Active l'animation du lien offensif sur l'autre flotte ciblée
			// par le lien
			Element targetLink = DOM.getElementById(OFFENSIVE_LINK_ID +
					fleetData.getOffensiveLinkedFleetId());
			if (targetLink != null) {
				offensiveLinkUpdaterTarget = new BackgroundUpdater(targetLink,
						new Point(0, startY),
						new Point((int) (432 * hints.getZoom()), startY),
						new Point(offsetY, 0));
				TimerManager.register(offensiveLinkUpdaterTarget);
			}
		}
		
		if (fleetData.getDefensiveLinkedFleetId() != 0) {
			// Active l'animation du lien défensif sur la flotte actuelle
			int startY = hints.getZoom() == 1 ? 932 : 986;
			int offsetY = hints.getZoom() == 1 ? 54 : 27;
			
			defensiveLinkUpdaterSelf = new BackgroundUpdater(defensiveLink,
					new Point(0, startY),
					new Point((int) (432 * hints.getZoom()), startY),
					new Point(offsetY, 0));
			TimerManager.register(defensiveLinkUpdaterSelf);
			
			// Active l'animation du lien défensif sur l'autre flotte ciblée
			// par le lien
			Element targetLink = DOM.getElementById(DEFENSIVE_LINK_ID +
					fleetData.getDefensiveLinkedFleetId());
			if (targetLink != null) {
				defensiveLinkUpdaterTarget = new BackgroundUpdater(targetLink,
						new Point(0, startY),
						new Point((int) (432 * hints.getZoom()), startY),
						new Point(offsetY, 0));
				TimerManager.register(defensiveLinkUpdaterTarget);
			}
		}
	}
	
	private void stopLinksAnimation() {
		if (fleetData.getOffensiveLinkedFleetId() != 0 && offensiveLinkUpdaterSelf != null) {
			// Désactive l'animation du lien offensif sur la flotte actuelle
			TimerManager.unregister(offensiveLinkUpdaterSelf);
			offensiveLink.getStyle().setProperty("backgroundPosition",
					"-" + offensiveLinkUpdaterSelf.getStart().getX() + "px -" +
					offensiveLinkUpdaterSelf.getStart().getY() + "px"); //$NON-NLS-1$
			offensiveLinkUpdaterSelf.destroy();
			offensiveLinkUpdaterSelf = null;
			
			// Désactive l'animation du lien offensif sur l'autre flotte
			// ciblée par le lien
			if (offensiveLinkUpdaterTarget != null) {
				Element targetLink = DOM.getElementById(OFFENSIVE_LINK_ID +
						fleetData.getOffensiveLinkedFleetId());
				
				TimerManager.unregister(offensiveLinkUpdaterTarget);
				targetLink.getStyle().setProperty("backgroundPosition",
						"-" + offensiveLinkUpdaterTarget.getStart().getX() + "px -" +
						offensiveLinkUpdaterTarget.getStart().getY() + "px"); //$NON-NLS-1$
				offensiveLinkUpdaterTarget.destroy();
				offensiveLinkUpdaterTarget = null;
			}
		}
		
		if (fleetData.getDefensiveLinkedFleetId() != 0 && defensiveLinkUpdaterSelf != null) {
			// Désactive l'animation du lien défensif sur la flotte actuelle
			TimerManager.unregister(defensiveLinkUpdaterSelf);
			defensiveLink.getStyle().setProperty("backgroundPosition",
					"-" + defensiveLinkUpdaterSelf.getStart().getX() + "px -" +
					defensiveLinkUpdaterSelf.getStart().getY() + "px"); //$NON-NLS-1$
			defensiveLinkUpdaterSelf.destroy();
			defensiveLinkUpdaterSelf = null;
			
			// Désactive l'animation du lien défensif sur l'autre flotte
			// ciblée par le lien
			if (defensiveLinkUpdaterTarget != null) {
				Element targetLink = DOM.getElementById(DEFENSIVE_LINK_ID +
						fleetData.getDefensiveLinkedFleetId());
				
				TimerManager.unregister(defensiveLinkUpdaterTarget);
				targetLink.getStyle().setProperty("backgroundPosition",
						"-" + defensiveLinkUpdaterTarget.getStart().getX() + "px -" +
						defensiveLinkUpdaterTarget.getStart().getY() + "px"); //$NON-NLS-1$
				defensiveLinkUpdaterTarget.destroy();
				defensiveLinkUpdaterTarget = null;
			}
		}
	}
	
	private void updateData(FleetData fleetData, boolean updateHyperspace) {
		stopTimers();
		
		if (fleetData.isStealth())
			OpenJWT.setElementOpacity(getElement(), .5);
		else
			OpenJWT.setElementOpacity(getElement(), 1);
		
		if (fleetData.isPlayerFleet())
			fleetOutlineUpdater = new LoopClassNameUpdater(this, "fleet-over", 0, 10);
		
		setToolTipText(buildToolTipText(fleetData));
		
		// Cercle indiquant le propriétaire de la flotte
		fleetOutline.setPropertyString("className", "fleetOutline " + //$NON-NLS-1$ //$NON-NLS-2$
			"fleetOutline-" + (selected ? "selected" : //$NON-NLS-1$ //$NON-NLS-2$
			(fleetData.getTreaty().equals("pirate") ? "enemy" :
			!fleetData.getNpcAction().equals("none") || (
				fleetData.getTreaty().equals("neutral") &&
				fleetData.isAi() && !fleetData.isReserved()) ?
			"npc" : ((fleetData.isAlliedFleet() && !fleetData.isAllyFleet())?
					"allied" : fleetData.getTreaty())))); //$NON-NLS-1$
		
		// Entrée / sortie d'hyperespace
		if (fleetData.getStartJumpRemainingTime() != 0)
			hyperspace.setClassName("hyperspace hyperspaceIn");
		else if (fleetData.getEndJumpRemainingTime() != 0)
			hyperspace.setClassName("hyperspace hyperspaceOut");
		
		if (updateHyperspace) {
			hyperspace.getStyle().setProperty("display", "");
					
			int target = fleetData.getStartJumpRemainingTime() == 0 ? 0 : 1;
			hyperspaceOpacityUpdater = new OpacityUpdater(hyperspace,
					target == 0 ? 1 : 0, .25,
					OpacityUpdater.HIDE_WHEN_TRANSPARENT);
			hyperspaceOpacityUpdater.setTargetOpacity(target, true);
			
			if (target == 1 && (hyperspaceSoundUpdater == null || hyperspaceSoundUpdater.isFinished())) {
				Sound sound = SoundManager.getInstance().getSound(Sounds.HYPERSPACE);
				if (sound != null) {
					hyperspaceSoundUpdater = new EngineSoundUpdater(this, sound, 4);
					TimerManager.register(hyperspaceSoundUpdater);
				}
			}
		} else {
			hyperspace.getStyle().setProperty("display",
				fleetData.getStartJumpRemainingTime() != 0 ||
				fleetData.getEndJumpRemainingTime() != 0 ? "" : "none");
			OpenJWT.setElementOpacity(hyperspace, 1);
		}
		
		// Action PNJ
		if (fleetData.getNpcAction().equals("none")) {
			npcAction.getStyle().setProperty("display", "none");
		} else {
			if (fleetData.getNpcAction().equals("mission"))
				npcAction.setClassName("npcMission");
			else
				npcAction.setClassName("npcTalk");
			npcAction.getStyle().setProperty("display", "");
		}
		
		// Liens offensifs / défensifs
		offensiveLink.getStyle().setProperty("display",
				fleetData.getOffensiveLinkedFleetId() != 0 ? "" : "none");
		defensiveLink.getStyle().setProperty("display",
				fleetData.getDefensiveLinkedFleetId() != 0 ? "" : "none");
		
		if (fleetData.getOffensiveLinkedFleetId() != 0)
			offensiveLink.setId(OFFENSIVE_LINK_ID + fleetData.getId());
		if (fleetData.getDefensiveLinkedFleetId() != 0)
			defensiveLink.setId(DEFENSIVE_LINK_ID + fleetData.getId());
		
		// Propriétaire de la flotte
		boolean pirate = fleetData.getTreaty().equals("pirate") || fleetData.isPirate(); //$NON-NLS-1$
		
		OutlineText text = TextManager.getText(
				fleetData.getOwner() + (fleetData.isAi() ? "&nbsp;<img src=\"" +
						Config.getMediaUrl() + "images/misc/blank.gif\" class=\"ai\"/>" : "") +
						(fleetData.isAi() ? "" : (fleetData.isConnected() ?
							(fleetData.isAway() ? Utilities.getAwayImage() : Utilities.getOnlineImage()) :
							Utilities.getOfflineImage())) +
						(!fleetData.isAi() && pirate ? "&nbsp;<img src=\"" +
						Config.getMediaUrl() + "images/misc/blank.gif\" class=\"pirate\" " +
						"style=\"margin-top: -4px;\"/>" : ""));
		owner.setInnerHTML("");
		owner.appendChild(text.getElement());
		owner.setPropertyString("className", "owner owner-" + ((fleetData.isAlliedFleet() && !fleetData.isAllyFleet())?
				"allied" : fleetData.getTreaty())); //$NON-NLS-1$ //$NON-NLS-2$
		
		powerLevel.setText("<div unselectable=\"on\" class=\"fleetPower\">" +
			fleetData.getPowerLevel() + "<img src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" class=\"stat s-power\" " +
			"unselectable=\"on\"/></div>");
		
		startTimers();
	}
	
	private void updateRendering() {
		int offsetY;
		if (!fleetData.isDelude())
			if (fleetData.isImmobilized() && fleetData.getStartJumpRemainingTime() == 0 &&
					fleetData.getEndJumpRemainingTime() == 0)
				offsetY = hints.getZoom() == 1 ? 1371 : 1411;
			else
				offsetY = hints.getZoom() == 1 ? 791 : 831;
		else
			offsetY = hints.getZoom() == 1 ? 2154 : 2194;
		
		DOM.setStyleAttribute(graphics, "backgroundPosition", "-" + //$NON-NLS-1$ //$NON-NLS-2$
				(int) Math.floor(hints.getZoom() * 40 * fleetData.getSkin()) + "px -" + //$NON-NLS-1$
				offsetY + "px"); //$NON-NLS-1$
		
		int y = hints.getZoom() == 1 ? 851 : 905;
		offensiveLink.getStyle().setProperty("backgroundPosition",
				"0 -" + y + "px"); //$NON-NLS-1$
		
		y = hints.getZoom() == 1 ? 932 : 986;
		defensiveLink.getStyle().setProperty("backgroundPosition",
				"0 -" + y + "px"); //$NON-NLS-1$
		
		if (offensiveLinkUpdaterSelf != null || defensiveLinkUpdaterSelf != null) {
			stopLinksAnimation();
			startLinksAnimation();
		}
	}
	
	private String buildToolTipText(FleetData fleetData) {
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		StaticMessages staticMessages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		String tooltip;
		
		int deludeLevel = (fleetData.isPlayerFleet() || fleetData.isAllyFleet()) &&
			fleetData.getSkillAt(3).getType() == 15 ?
			fleetData.getSkillAt(3).getLevel() : -1;
		
		if (fleetData.isPirateFleet()) {
			// Flotte pirate joueur
			tooltip = "<div class=\"owner-enemy\"><b>" + //$NON-NLS-1$
				staticMessages.pirateFleet() + "</b>" +
				(fleetData.isAi() ? " <img src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
				"images/misc/blank.gif\" class=\"ai\"/>" : "") +
				"</div>"; //$NON-NLS-1$
		} else {
			// Flotte normale
			tooltip =
				"<div class=\"owner-" + fleetData.getPact() + "\"><b>" + //$NON-NLS-1$ //$NON-NLS-2$
				(fleetData.hasAllyTag() ? "[" + fleetData.getAllyTag() + "] " : "") +
				fleetData.getOwner() + "</b>" + //$NON-NLS-1$
				(fleetData.isAi() ? " <img src=\"" + Config.getMediaUrl() + //$NON-NLS-1$
						"images/misc/blank.gif\" class=\"ai\"/>" : (fleetData.isConnected() ? (fleetData.isAway() ? Utilities.getAwayImage() :
							Utilities.getOnlineImage()) : Utilities.getOfflineImage())) + //$NON-NLS-1$ //$NON-NLS-2$
				"</div>"; //$NON-NLS-1$
		
		}
		if(fleetData.isPlayerFleet()){
		double levelPlayer = Client.getInstance().getProgressBar().getPlayerLevel();
		int coef = fleetData.getPowerLevel();
		currentFleet.getStyle().setProperty("width",
				30 * coef / levelPlayer + "px");
		}
		else{
			currentFleet.getStyle().setProperty("display", "none");
			hullBar.getStyle().setProperty("display", "none");	
			}
		
		// Affiche la puissance de la flotte
		String power = "<div>" + staticMessages.fleetPower(
			"<b class=\"owner-" + (fleetData.getPact().equals("pirate")? "enemy": fleetData.getPact())  + "\">" +
			fleetData.getPowerLevel() + "</b> <img src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\" " +
			"class=\"stat s-power\" unselectable=\"on\"/>") + "</div>";
		
		if (!fleetData.isPlayerFleet()) { //$NON-NLS-1$
			// Nom de flotte
			if (!fleetData.isPirateFleet()) //$NON-NLS-1$
				tooltip += "<div><b>" + fleetData.getName() + "</b></div>"; //$NON-NLS-1$ //$NON-NLS-2$
			
			if (fleetData.isDelude())
				tooltip += "<div style=\"font-weight: bold; color: #ffa205;\">Leurre</div>";
			
			// Affiche la puissance de la flotte
			tooltip += power;
			
			// Furtivité
			if (fleetData.isStealth())
				tooltip += "<div style=\"color: red;\">Mode furtif</div>";
			
			if (fleetData.isReserved())
				tooltip+= "<div style=\"color: red;\">Flotte réservée pour<br/>un autre joueur.</div>";
			
			// Entrée en hyperespace en cours
			if (fleetData.getStartJumpRemainingTime() == -1)
				tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
						staticMessages.enteringHyperspace() + "</div>"; //$NON-NLS-1$
			
			// Sortie d'hyperespace en cours
			if (fleetData.getEndJumpRemainingTime() == -1)
				tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
						staticMessages.leavingHyperspace() + "</div>"; //$NON-NLS-1$
			
			// Capture de système en cours
			if (fleetData.isCapturing())
				tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
						staticMessages.systemCapture() + "</div>"; //$NON-NLS-1$
		} else {
			tooltip += "<div><b>" + fleetData.getName() + "</b></div>"; //$NON-NLS-1$ //$NON-NLS-2$
			
			if (fleetData.isDelude())
				tooltip += "<div style=\"font-weight: bold; color: #ffa205;\">Leurre</div>";
			
			// Affiche la puissance de la flotte
			tooltip += power;
			
			// Mouvement automatique en cours
			if (fleetData.isScheduledMove())
				tooltip += "<div style=\"color: red;\">Déplacement auto<br/>en cours...</div>";
			
			if (fleetData.getStartJumpRemainingTime() != 0) {
				// Entrée en hyperespace en cours
				String id = ToolTipTextUpdater.generateId();
				hyperspaceUpdater = new ToolTipTimeUpdater(getElement(), id,
						fleetData.getStartJumpRemainingTime());
				
				tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
						staticMessages.enteringHyperspace("<b id=\"" + id + "\"></b>") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				
				// Secteur d'arrivée du saut
				if (fleetData.getJumpTarget().length() > 0)
					tooltip += "<div style=\"color: red;\">Destination <b>" +
						fleetData.getJumpTarget() + "</b></div>";
			} else if (fleetData.getEndJumpRemainingTime() != 0) {
				// Sortie d'hyperespace en cours
				String id = ToolTipTextUpdater.generateId();
				hyperspaceUpdater = new ToolTipTimeUpdater(getElement(), id,
						fleetData.getEndJumpRemainingTime());
				
				tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
						staticMessages.leavingHyperspace("<b id=\"" + id + "\"></b>") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else if (!fleetData.isDelude() || deludeLevel > 0) {
				// Mouvement restant
				tooltip += "<div>" + staticMessages.movement( //$NON-NLS-1$
						"<b>" + fleetData.getMovement() + "/" + //$NON-NLS-1$ //$NON-NLS-2$
						fleetData.getMovementMax() + "</b>") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
				
				// Rechargement du mouvement
				if (fleetData.getMovement() < fleetData.getMovementMax() &&
						fleetData.getColonizationRemainingTime() == 0 && !fleetData.isMigrating()) {
					String id = ToolTipTextUpdater.generateId();
					movementReloadUpdater = new ToolTipTimeUpdater(getElement(), id,
							fleetData.getMovementReloadRemainingTime());
					
					tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
						staticMessages.maxMovement("<b id=\"" + id + "\"></b>") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
			
			// Furtivité
			if (fleetData.isStealth())
				tooltip += "<div style=\"color: red;\">Mode furtif</div>";
			
			// Liens offensifs / défensifs
			if (fleetData.getOffensiveLinkedFleetId() != 0)
				tooltip += "<div style=\"color: #fa941d;\">" +
					staticMessages.offensiveLink() + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
			if (fleetData.getDefensiveLinkedFleetId() != 0)
				tooltip += "<div style=\"color: #2ba7dc;\">" +
					staticMessages.defensiveLink() + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
			
			// Rechargement de l'hyperpropulsion
			if (fleetData.getJumpReloadRemainingTime() != 0) {
				String id = ToolTipTextUpdater.generateId();
				jumpReloadUpdater = new ToolTipTimeUpdater(
						getElement(), id, fleetData.getJumpReloadRemainingTime());
				
				tooltip += "<div style=\"color: red;\">" + //$NON-NLS-1$
					staticMessages.jumpReload("<b id=\"" + id + "\"></b>") + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
			// Colonisation / capture encours
			if (fleetData.getColonizationRemainingTime() != 0) {
				String id = ToolTipTextUpdater.generateId();
				colonizationUpdater = new ToolTipTimeUpdater(getElement(), id,
						fleetData.getColonizationRemainingTime());
				
				tooltip += "<div style=\"color: red;\">" + (fleetData.isCapturing() ? //$NON-NLS-1$
						staticMessages.systemCapture("<b id=\"" + id + "\"></b>") : //$NON-NLS-1$ //$NON-NLS-2$
						staticMessages.colonization("<b id=\"" + id + "\"></b>")) + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
			// Migration en cours
			if (fleetData.isMigrating() == true) {
				String id = ToolTipTextUpdater.generateId();
				migrationUpdater = new ToolTipTimeUpdater(getElement(), id,
						fleetData.getMovementReloadRemainingTime());
				
				tooltip += "<div style=\"color: red;\">" +
				staticMessages.systemMigration("<b id=\"" + id + "\"></b>")+"</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
						
				}
		}
		
		// Vaisseaux dans la flotte
		if (fleetData.hasSlots()) {
			for (int i = 0; i < fleetData.getSlotsCount(); i++) {
				SlotInfoData slotData = fleetData.getSlotAt(i);
				
				if (slotData.getId() != 0) {
					String offensiveLinkedCount = "";
					if (fleetData.isPlayerFleet() &&
							fleetData.getOffensiveLinkedFleetId() != 0 &&
							fleetData.getOffensiveLinkedCount(i) > 0)
						offensiveLinkedCount = "&nbsp;<span style=\"color: #fa941d;\">+" +
							Formatter.formatNumber(fleetData.getOffensiveLinkedCount(i), true) + "</span>";
					
					String defensiveLinkedCount = "";
					if (fleetData.isPlayerFleet() &&
							fleetData.getDefensiveLinkedFleetId() != 0 &&
							fleetData.getDefensiveLinkedCount(i) > 0)
						defensiveLinkedCount = "&nbsp;<span style=\"color: #2ba7dc;\">+" +
							Formatter.formatNumber(fleetData.getDefensiveLinkedCount(i), true) + "</span>";
					
					String position = "";
					if (fleetData.isPlayerFleet())
						position = "&nbsp;<span style=\"color: " +
							(slotData.isFront() ? "#7aff01" : "#ff7901") + "\">" +
							(slotData.isFront() ? "▲" : "▼")+ "</span>";
					
					tooltip += "<div><span class=\"owner-" + fleetData.getPact() + "\">" + //$NON-NLS-1$ //$NON-NLS-2$
						Formatter.formatNumber(slotData.getCount(), true) + "</span>&nbsp;" + //$NON-NLS-1$
						dynamicMessages.getString("ship" + //$NON-NLS-1$
						(slotData.getCount() > 1 ? "s" : "") +
						slotData.getId()) + position + offensiveLinkedCount +
						defensiveLinkedCount + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		}
		
		// Estimation du nombre de vaisseaux
		if (fleetData.hasShips()) {
			for (int i = 0; i < fleetData.getShipsCount(); i++) {
				ShipInfoData shipData = fleetData.getShipAt(i);
				
				if (shipData.getId() > 0)
					tooltip += "<div>" + getQualifier(shipData.getClasses(), //$NON-NLS-1$
							dynamicMessages.getString("ships" + //$NON-NLS-1$
									shipData.getId()).toLowerCase()) + "</div>"; //$NON-NLS-1$
			}
		}
		
		// Estimation du nombre de vaisseaux par classes de vaisseaux
		if (fleetData.hasClasses()) {
			for (int i = 0; i < fleetData.getClassesCount(); i++) {
				int classData = fleetData.getClassAt(i);
				
				if (classData > 0)
					tooltip += "<div>" + getQualifier(classData, //$NON-NLS-1$
							dynamicMessages.getString("shipClasses" + //$NON-NLS-1$
									(i + 1))) + "</div>"; //$NON-NLS-1$
			}
		}
		
		if ((fleetData.getTreaty().equals("player") ||
				fleetData.isAllyFleet()) &&
				!fleetData.isDelude()) { //$NON-NLS-1$
			// Niveau et expérience
			int level = fleetData.getFleetLevel();
			double levelXp = FleetData.getFleetLevelXp(level);
			int width;
			
			if (level < 15) {
				double nextLevelXp = FleetData.getFleetLevelXp(level + 1);
				width = (int) Math.floor((fleetData.getXp() - levelXp) * 80 /
					(nextLevelXp - levelXp));
			} else {
				width = 80;
			}
			
			tooltip += "<div class=\"xp-mini\" style=\"margin: 5px 0 2px 10px\">" + //$NON-NLS-1$
				"<div class=\"current-xp-mini\" style=\"width: " + //$NON-NLS-1$
				width + "px\"></div>" + //$NON-NLS-1$
				"<div class=\"name\">" + staticMessages.level(level) + "</div></div>"; //$NON-NLS-1$ //$NON-NLS-2$
			
			// Compétences
			String skills = ""; //$NON-NLS-1$
			int skillsCount = 0;
			int skillPoints = 0;
			for (int i = 0; i < fleetData.getSkillsCount(); i++) {
				SkillData skillData = fleetData.getSkillAt(i);
				
				if (skillData.getType() != 0) {
					skills += "<div class=\"miniSkill" + (skillData.getType() > 10 ? " ultimate" : "") + "\" style=\"background-position: -" + //$NON-NLS-1$
							(20 * (skillData.getType() > 10 ? skillData.getType() - 11 : skillData.getType())) +
							"px -" + (skillData.getType() > 10 ? 1268 : 771) + "px\">" + //$NON-NLS-1$
							(skillData.getLevel() + 1) + "</div>"; //$NON-NLS-1$
					skillsCount++;
					skillPoints += skillData.getLevel() + 1;
				}
			}
			if (skillPoints < level) {
				skills += "<div class=\"miniSkillPoint\">" + (level - skillPoints) + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
				skillsCount++;
			}
			tooltip += "<div style=\"width: " + (20 * skillsCount) + "px; margin: 0 0 0 " + //$NON-NLS-1$ //$NON-NLS-2$
				((100 - 20 * skillsCount) / 2) + "px;\">" + skills + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (fleetData.getSkirmishAbilitiesCount() > 0) {
			tooltip += "<div><div style=\"float: left; padding-top: 2px; color: #ffcc00;\">" +
				"Escarmouche&nbsp;</div>";
			for (int i = 0; i < fleetData.getSkirmishAbilitiesCount(); i++) {
				int ability = fleetData.getSkirmishAbilityAt(i);
				
				if (ability != -1) {
					tooltip += "<div class=\"miniActionAbility\" style=\"float: left; " +
						"background-position: -" + (AbilityData.GRAPHICS[ability] * 16) +
						"px -125px; margin-left: 1px;\"></div>";
				} else {
					tooltip += "<div class=\"miniActionAbility noAction\" " +
						"style=\"float: left; margin-left: 1px;\"></div>";
				}
			}
			tooltip += "</div>";
		}
		
		if (fleetData.getBattleAbilitiesCount() > 0) {
			tooltip += "<div style=\"clear: both;\">" +
				"<div style=\"float: left; padding-top: 3px; color: #ffcc00;\">" +
				"Bataille&nbsp;</div>";
			
			for (int i = 0; i < fleetData.getBattleAbilitiesCount(); i++) {
				int ability = fleetData.getBattleAbilityAt(i);
				
				if (ability != -1) {
					tooltip += "<div class=\"miniActionAbility\" style=\"float: left; " +
						"background-position: -" + (AbilityData.GRAPHICS[ability] * 16) +
						"px -125px; margin: 1px 0 0 1px;\"></div>";
				} else {
					tooltip += "<div class=\"miniActionAbility noAction\" " +
						"style=\"float: left; margin: 1px 0 0 1px;\"></div>";
				}
			}
			tooltip += "</div>";
		}
		
		tooltip += "<div style=\"clear: both; font-size: 1px; height: 1px;\"></div>";
		
		return tooltip;
	}
	
	private static class EngineSoundUpdater implements TimerHandler {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private FleetItem fleetItem;
		
		private Sound sound;
		
		private int initialVolume;
		
		private boolean play;
		
		private int frame;
		
		private int length;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public EngineSoundUpdater(FleetItem fleetItem, Sound sound, int length) {
			this.fleetItem = fleetItem;
			this.sound = sound;
			this.play = false;
			this.initialVolume = sound.getVolume();
			this.length = length * 1000;
			this.frame = 0;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void destroy() {
			fleetItem = null;
			if (sound != null) {
				sound.stop();
				sound.setVolume(initialVolume);
				sound.setBalance(0);
				sound = null;
			}
		}
		
		public boolean isFinished() {
			if (fleetItem == null)
				return true;
			else if (length == 0)
				return !fleetItem.isAnimated();
			else
				return frame > length;
		}
		
		public void update(int interpolation) {
			if (fleetItem != null) {
				Point location = fleetItem.getLocation();
				UIItemRenderingHints hints = fleetItem.getRenderingHints();
				Point view = hints.getMap().getView();
				frame += interpolation;
				
				int offset = 250 + (int) (250 / hints.getZoom());
				
				int volume;
				int balance;
				
				if (view.getX() >= location.getX() + offset ||
						view.getY() >= location.getY() + offset ||
						view.getX() + OpenJWT.getClientWidth()  / hints.getZoom() <= location.getX() - offset ||
						view.getY() + OpenJWT.getClientHeight() / hints.getZoom() <= location.getY() - offset) {
					// Flotte loin d'être visible
					volume = 0;
					balance = 0;
				} else if (view.getX() <= location.getX() &&
						view.getY() <= location.getY() &&
						view.getX() + OpenJWT.getClientWidth()  / hints.getZoom() >= location.getX() &&
						view.getY() + OpenJWT.getClientHeight() / hints.getZoom() >= location.getY()) {
					// Flotte visible
					volume = 100;
					balance = (int) Math.round(-100 + (200 * (location.getX() -
						view.getX())) / (OpenJWT.getClientWidth()  / hints.getZoom()));
				} else {
					// Flotte presque visible
					int volumeX, volumeY;
					
					if (view.getX() <= location.getX() &&
							view.getX() + OpenJWT.getClientWidth()  / hints.getZoom() >= location.getX()) {
						// Flotte au dessus / en dessous de l'écran
						volumeX = 100;
						balance = 0;
					} else if (view.getX() > location.getX()) {
						// Flotte à gauche de l'écran
						volumeX = (100 * (offset - view.getX() + location.getX())) / offset;
						balance = -100;
					} else {
						// Flotte à droite de l'écran
						volumeX = (int) Math.round(100 * (offset - location.getX() + view.getX() + OpenJWT.getClientWidth() / hints.getZoom())) / offset;
						balance = 100;
					}
					
					if (view.getY() <= location.getY() &&
							view.getY() + OpenJWT.getClientHeight()  / hints.getZoom() >= location.getX()) {
						// Flotte au dessus / en dessous de l'écran
						volumeY = 100;
					} else if (view.getY() > location.getY()) {
						// Flotte à gauche de l'écran
						volumeY = (100 * (offset - view.getY() + location.getY())) / offset;
					} else {
						// Flotte à droite de l'écran
						volumeY = (int) Math.round(100 * (offset - location.getY() + view.getY() + OpenJWT.getClientHeight() / hints.getZoom())) / offset;
					}
					
					volume = Math.min(volumeX, volumeY);
				}
				
				sound.setVolume(initialVolume * volume / 100);
				sound.setBalance(balance);
				
				if (!play) {
					play = true;
					frame = 0;
					sound.play();
				}
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
