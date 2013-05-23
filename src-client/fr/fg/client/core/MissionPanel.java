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
import java.util.List;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;

import fr.fg.client.animation.ToolTipTextUpdater;
import fr.fg.client.animation.ToolTipTimeUpdater;
import fr.fg.client.data.ContractStateData;
import fr.fg.client.data.ContractsStateData;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class MissionPanel extends AbsolutePanel {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ContractStateData[] missionStates;
	
	private OutlineText[] missionTexts;
	
	private List<TimerHandler> updaters;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public MissionPanel() {
		getElement().setId("missionPanel");
		
		this.missionStates = new ContractStateData[0];
		this.missionTexts = new OutlineText[0];
		this.updaters = new ArrayList<TimerHandler>();
		
		sinkEvents(Event.ONCLICK);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setMissionStates(ContractsStateData contractsState) {
		clear();
		
		for (TimerHandler updater : updaters)
			TimerManager.unregister(updater);
		updaters.clear();
		
		this.missionStates = new ContractStateData[contractsState.getContractsStateCount()];
		this.missionTexts = new OutlineText[contractsState.getContractsStateCount()];
		for (int i = 0; i < contractsState.getContractsStateCount(); i++) {
			ContractStateData missionState = contractsState.getContractStateAt(i);
			
			String text = "<div class=\"mission mission-" +
					missionState.getStatus() +
					"\" unselectable=\"on\">" +
				"<div class=\"missionName\" unselectable=\"on\">" + missionState.getName() + "</div>";
			
			// Parse les comptes à rebours
			String description = missionState.getDescription();
			String countdown = "%countdown:";
			
			int index = description.indexOf(countdown);
			while (index != -1) {
				int end = description.indexOf("%", index + 1);
				
				try {
					int value = Integer.parseInt(
							description.substring(index + countdown.length(), end));
					
					String id = ToolTipTextUpdater.generateId();
					ToolTipTimeUpdater updater =
						new ToolTipTimeUpdater(getElement(), id, value);
					updaters.add(updater);
					TimerManager.register(updater);
					
					description = description.substring(0, index) +
						"<span id=\"" + id + "\">" +
						Formatter.formatDate(Math.ceil(value)) + "</span>" +
						description.substring(end + 1);
				} catch (Exception e) {
					Utilities.log("Countdown parsing exception.", e);
					break;
				}
				
				index = description.indexOf(countdown);
			}
			
			text += "<div class=\"missionDescription\" unselectable=\"on\">" +
					description + "</div></div>";
			
			OutlineText missionText = TextManager.getText(text);
			add(missionText);
			
			this.missionStates[i] = missionState;
			this.missionTexts[i] = missionText;
		}
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			for (int i = 0; i < missionTexts.length; i++) {
				if (missionTexts[i].getElement().isOrHasChild(event.getTarget())) {
					ContractStateData missionState = missionStates[i];
					
					if (missionState.getIdArea() != 0) {
						if (missionState.isLocationDefined())
							Client.getInstance().getAreaContainer().setIdArea(
								missionState.getIdArea(), new Point(
									missionState.getX(), missionState.getY()
								));
						else
							Client.getInstance().getAreaContainer().setIdArea(
								missionState.getIdArea());
					}
					break;
				}
			}
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
