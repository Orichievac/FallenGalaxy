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

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;

import fr.fg.client.openjwt.core.SoundManager;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.ui.JSComponent;

public class ControlPanel extends FlowPanel {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int ROWS_COUNT = 2, COLUMNS_COUNT = 6;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	protected FlowPanel[][] controls;
	
	protected ControlHandler[][] handlers;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ControlPanel() {
		getElement().setId("controlPanel");
		getElement().setAttribute("unselectable", "on");
		
		controls = new FlowPanel[COLUMNS_COUNT][ROWS_COUNT];
		handlers = new ControlHandler[COLUMNS_COUNT][ROWS_COUNT];
		
		// Construit les boutons du panel
		for (int j = 0; j < ROWS_COUNT; j++) {
			FlowPanel row = new FlowPanel();
			row.addStyleName("controlRow");
			add(row);
			
			for (int i = 0; i < COLUMNS_COUNT; i++) {
				FlowPanel control = new FlowPanel();
				control.getElement().setId("control" + i + j);
				control.setStyleName("control");
				row.add(control);
				
//				Element progress = DOM.createDiv();
//				progress.setClassName("progress");
//				control.getElement().appendChild(progress);
//				
//				BackgroundUpdater updater = new BackgroundUpdater(progress, new Point(0, 0), new Point(1600, 0), new Point(50, 0));
//				TimerManager.register(updater);
				
				controls[i][j] = control;
				handlers[i][j] = null;
			}
		}
		
		sinkEvents(Event.ONMOUSEUP);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setControl(int x, int y, String className, String content,
			ControlHandler handler, String tooltip) {
		controls[x][y].setStyleName("control " + className);
		controls[x][y].getElement().setInnerHTML(content);
		handlers[x][y] = handler;
		
		if (tooltip != null)
			ToolTipManager.getInstance().register(controls[x][y].getElement(), tooltip, 200);
		else
			ToolTipManager.getInstance().unregister(controls[x][y].getElement());
	}
	
	public void clearControls() {
		for (int j = 0; j < ROWS_COUNT; j++)
			for (int i = 0; i < COLUMNS_COUNT; i++) {
				controls[i][j].setStyleName("control");
				controls[i][j].getElement().setInnerHTML("");
				handlers[i][j] = null;
				ToolTipManager.getInstance().unregister(controls[i][j].getElement());
			}
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEUP:
			for (int j = 0; j < ROWS_COUNT; j++)
				for (int i = 0; i < COLUMNS_COUNT; i++) {
					if (controls[i][j].getElement().isOrHasChild(event.getTarget())) {
						if (this.handlers[i][j] != null) {
							SoundManager.getInstance().playSound(
									JSComponent.SOUND_CLICK);
							this.handlers[i][j].actionPerformed(
									i, j, event.getButton());
						}
						break;
					}
				}
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	// ----------------------------------------------- CLASSES IMBRIQUEES -- //
	
	public interface ControlHandler {
		// --------------------------------------------------- CONSTANTES -- //
		// ----------------------------------------------------- METHODES -- //
		
		public void actionPerformed(int x, int y, int button);
	}
}
