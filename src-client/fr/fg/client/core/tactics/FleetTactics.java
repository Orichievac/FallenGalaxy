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

import java.util.Arrays;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComponent;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;

public class FleetTactics extends JSRowLayout implements ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int VIEW_SKIRMISH = 0, VIEW_BATTLE = 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int view;
	
	private JSButton previousActionBt, nextActionBt, skirmishBt, battleBt;
	
	private AbsolutePanel actionsContainer;
	
	private TacticsAction[] actionsList;
	
	private ActionsContainerScrollUpdater scrollUpdater;
	
	private int[][] shipsId;
	
	private long[][] shipsCount;
	
	private int[][] shipsAbilities;
	
	private int selectedIndex;
	
	private TacticsListenerCollection listeners;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FleetTactics() {
		super();
		
		view = VIEW_SKIRMISH;
		
		shipsId = new int[2][];
		shipsId[FleetTactics.VIEW_SKIRMISH] = new int[5];
		shipsId[FleetTactics.VIEW_BATTLE] = new int[15];
		
		Arrays.fill(shipsId[0], -1);
		Arrays.fill(shipsId[1], -1);
		
		shipsCount = new long[2][];
		shipsCount[FleetTactics.VIEW_SKIRMISH] = new long[5];
		shipsCount[FleetTactics.VIEW_BATTLE] = new long[15];
		
		shipsAbilities = new int[2][];
		shipsAbilities[FleetTactics.VIEW_SKIRMISH] = new int[5];
		shipsAbilities[FleetTactics.VIEW_BATTLE] = new int[15];
		
		Arrays.fill(shipsAbilities[0], -1);
		Arrays.fill(shipsAbilities[1], -1);
		
		JSLabel actionsHeader = new JSLabel("<b unselectable=\"on\">Séquence d'actions</b>");
		actionsHeader.setPixelWidth(290);
		
		skirmishBt = new JSButton("Escarmouche");
		skirmishBt.setPixelWidth(110);
		skirmishBt.addClickListener(this);
		
		battleBt = new JSButton("Bataille");
		battleBt.setPixelWidth(110);
		battleBt.addClickListener(this);
		
		// Boutons précédent / suivant
		previousActionBt = new JSButton();
		previousActionBt.addStyleName("iconLeft");
		previousActionBt.setPixelWidth(JSComponent.getUIPropertyInt(
			JSButton.UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT));
		previousActionBt.addClickListener(this);
		previousActionBt.getElement().getStyle().setProperty("marginTop", "10px");
		
		nextActionBt = new JSButton();
		nextActionBt.addStyleName("iconRight");
		nextActionBt.setPixelWidth(JSComponent.getUIPropertyInt(
			JSButton.UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT));
		nextActionBt.addClickListener(this);
		nextActionBt.getElement().getStyle().setProperty("marginTop", "10px");
		
		actionsContainer = new AbsolutePanel();
		actionsContainer.setPixelSize(510, 101);
		OpenJWT.setElementFloat(actionsContainer.getElement(), "left");
		
		actionsList = new TacticsAction[15];
		for (int i = 0; i < actionsList.length; i++) {
			actionsList[i] = new TacticsAction(i) {
				@Override
				public void onBrowserEvent(Event event) {
					switch (event.getTypeInt()) {
					case Event.ONCLICK:
						setSelectedActionIndex(index);
						break;
					}
				}
			};
			actionsContainer.add(actionsList[i], i * 51, 0);
		}
		
		scrollUpdater = new ActionsContainerScrollUpdater(
				actionsContainer, actionsList);
		
		JSLabel[] separators = new JSLabel[4];
		for (int i = 0; i < 4; i++) {
			separators[i] = new JSLabel();
			separators[i].setPixelSize(10, 101);
		}
		
		addComponent(actionsHeader);
		addComponent(skirmishBt);
		addComponent(battleBt);
		setRowAlignment(JSRowLayout.ALIGN_CENTER);
		addRow();
		addComponent(separators[0]);
		addComponent(previousActionBt);
		addComponent(separators[1]);
		addComponent(actionsContainer);
		addComponent(separators[2]);
		addComponent(nextActionBt);
		addComponent(separators[3]);
		
		setView(VIEW_SKIRMISH);
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void addTacticsListener(TacticsListener listener) {
		if (listeners == null)
			listeners = new TacticsListenerCollection();
		listeners.add(listener);
	}
	
	public void removeTacticsListener(TacticsListener listener) {
		if (listeners == null)
			listeners = new TacticsListenerCollection();
		listeners.remove(listener);
	}
	
	public void onClick(Widget sender) {
		if (sender == previousActionBt) {
			scrollRight();
		} else if (sender == nextActionBt) {
			scrollLeft();
		} else if (sender == skirmishBt) {
			setView(VIEW_SKIRMISH);
		} else if (sender == battleBt) {
			setView(VIEW_BATTLE);
		}
	}
	
	public int getView() {
		return view;
	}
	
	public void setView(int view) {
		int oldView = this.view;
		this.view = view;
		
		skirmishBt.setSelected(view == VIEW_SKIRMISH);
		battleBt.setSelected(view == VIEW_BATTLE);
		
		for (int i = 0; i < 10; i++) {
			actionsList[i].getElement().getStyle().setProperty(
				"visibility", view == VIEW_SKIRMISH && i >= 5 ? "hidden" : "");
		}
		
		if (view == VIEW_SKIRMISH) {
			previousActionBt.getElement().getStyle().setProperty(
					"visibility", "hidden");
			nextActionBt.getElement().getStyle().setProperty(
					"visibility", "hidden");
		} else if (view == VIEW_BATTLE) {
			previousActionBt.getElement().getStyle().setProperty(
					"visibility", "hidden");
			nextActionBt.getElement().getStyle().setProperty(
					"visibility", "");
		}
		
		scrollUpdater.setCurrentOffset(0);
		
		for (int i = 0; i < (view == FleetTactics.VIEW_SKIRMISH ? 5 : 15); i++) {
			actionsList[i].setAction(shipsId[view][i],
					shipsCount[view][i], shipsAbilities[view][i]);
		}
		
		if (listeners != null)
			listeners.fireViewChange(view, oldView);
		
		setSelectedActionIndex(0);
	}
	
	public void scrollLeft() {
		previousActionBt.getElement().getStyle().setProperty(
				"visibility", "");
		nextActionBt.getElement().getStyle().setProperty(
				"visibility", "hidden");
		
		scrollUpdater.setTargetOffset(51 * 5);
		TimerManager.register(scrollUpdater);
	}
	
	public void scrollRight() {
		previousActionBt.getElement().getStyle().setProperty(
				"visibility", "hidden");
		nextActionBt.getElement().getStyle().setProperty(
				"visibility", "");
		
		scrollUpdater.setTargetOffset(0);
		TimerManager.register(scrollUpdater);
	}
	
	public void setNoAction(int view, int index) {
		shipsId[view][index] = -1;
		shipsCount[view][index] = 0;
		shipsAbilities[view][index] = -1;
		actionsList[index].setNoAction();
	}
	
	public int getShipId(int view, int index) {
		return shipsId[view][index];
	}
	
	public long getCount(int view, int index) {
		return shipsCount[view][index];
	}
	
	public int getAbility(int view, int index) {
		return shipsAbilities[view][index];
	}
	
	public void setAction(int view, int index, int shipId, long count, int ability) {
		shipsId[view][index] = shipId;
		shipsCount[view][index] = count;
		shipsAbilities[view][index] = ability;
		actionsList[index].setAction(shipId, count, ability);
	}
	
	public int getSelectedActionIndex() {
		return selectedIndex;
	}
	
	public void setSelectedActionIndex(int index) {
		int oldIndex = selectedIndex;
		selectedIndex = index;
		
		for (int i = 0; i < actionsList.length; i++) {
			if (i == index)
				actionsList[i].setSelected(true);
			else
				actionsList[i].setSelected(false);
		}
		
		if (selectedIndex >= 10)
			scrollLeft();
		
		if (listeners != null)
			listeners.fireActionSelected(index, oldIndex);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private class ActionsContainerScrollUpdater implements TimerHandler {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int targetOffset, currentOffset;
		
		private AbsolutePanel actionsContainer;
		
		private TacticsAction[] actionsList;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public ActionsContainerScrollUpdater(AbsolutePanel actionsContainer,
				TacticsAction[] actionsList) {
			this.currentOffset = 0;
			this.targetOffset = 0;
			this.actionsContainer = actionsContainer;
			this.actionsList = actionsList;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void setTargetOffset(int offset) {
			this.targetOffset = offset;
		}

		public void setCurrentOffset(int offset) {
			this.currentOffset = offset;
			this.targetOffset = offset;
			
			updateOffset();
		}
		
		public boolean isFinished() {
			return currentOffset == targetOffset;
		}
		
		public void update(int interpolation) {
			if (currentOffset != targetOffset) {
				if (currentOffset > targetOffset)
					currentOffset = Math.max(currentOffset - 76, targetOffset);
				else
					currentOffset = Math.min(currentOffset + 76, targetOffset);
				
				updateOffset();
			}
		}
		
		public void destroy() {
			// Non utilisé
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
		
		private void updateOffset() {
			for (int i = 0; i < actionsList.length; i++) {
				actionsContainer.setWidgetPosition(
						actionsList[i], i * 51 - currentOffset, 0);
			}
		}
	}
}
