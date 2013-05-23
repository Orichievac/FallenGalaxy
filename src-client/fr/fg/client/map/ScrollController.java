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

package fr.fg.client.map;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.EventManager;
import fr.fg.client.openjwt.core.Point;

public class ScrollController implements EventPreview {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int
		KEY_LEFT  = 1 << 0,
		KEY_RIGHT = 1 << 1,
		KEY_UP    = 1 << 2,
		KEY_DOWN  = 1 << 3;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Map map;
	
	private Widget container;
	
	private Point cursor;
	
	private boolean mouseDown;
	
	private int keys;
	
	private boolean enabled;
	
	private KeyboardHandler handler;
	
	private Point currentMovement;
	
	private Point viewMovement;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ScrollController(Map map, Widget container) {
		this.map = map;
		this.container = container;
		this.cursor = new Point();
		this.mouseDown = false;
		this.keys = 0; // Touches directionnelles enfoncées
		this.enabled = true;
		this.currentMovement = new Point();
		this.viewMovement = new Point();
		this.handler = new KeyboardHandler();
		
		EventManager.addEventHook(this);
		TimerManager.register(handler);
		
		RootPanel.get().sinkEvents(Event.KEYEVENTS | Event.MOUSEEVENTS);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			
			if (enabled) {
				EventManager.addEventHook(this);
				TimerManager.register(handler);
			} else {
				EventManager.removeEventHook(this);
				TimerManager.unregister(handler);
				mouseDown = false;
				keys = 0;
			}
		}
	}
	
	public boolean onEventPreview(Event event) {
		// Ignore les évènements si le controleur est désactive ou sur les
		// textfields
		Element target = DOM.eventGetTarget(event);
		
		if (!enabled || (target != null && DOM.getElementProperty(
				target, "nodeName").toLowerCase().equals("input"))) //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		
		switch (DOM.eventGetType(event)) {
		case Event.ONKEYDOWN:
			int keyCode = DOM.eventGetKeyCode(event);
			
			if (keyCode >= 32 && keyCode <= 40) {
				switch (keyCode) {
				case EventManager.KEY_LEFT:
					this.keys |= KEY_LEFT;
					break;
				case EventManager.KEY_UP:
					this.keys |= KEY_UP;
					break;
				case EventManager.KEY_RIGHT:
					this.keys |= KEY_RIGHT;
					break;
				case EventManager.KEY_DOWN:
					this.keys |= KEY_DOWN;
					break;
				}
				
				// Bloque l'effet des touches de déplacement
				event.cancelBubble(true);
				event.preventDefault();
			}
			break;
		case Event.ONKEYUP:
			keyCode = DOM.eventGetKeyCode(event);
			
			if (keyCode >= 37 && keyCode <= 40) {
				switch (keyCode) {
				case EventManager.KEY_LEFT:
					this.keys -= KEY_LEFT;
					break;
				case EventManager.KEY_UP:
					this.keys -= KEY_UP;
					break;
				case EventManager.KEY_RIGHT:
					this.keys -= KEY_RIGHT;
					break;
				case EventManager.KEY_DOWN:
					this.keys -= KEY_DOWN;
					break;
				}
				
				event.cancelBubble(true);
				event.preventDefault();
			}
			break;
		case Event.ONKEYPRESS:
			keyCode = DOM.eventGetKeyCode(event);
			
			if (keyCode >= 37 && keyCode <= 40) {
				event.cancelBubble(true);
				event.preventDefault();
			}
			break;
		case Event.ONMOUSEDOWN:
			if (DOM.isOrHasChild(container.getElement(),
					DOM.eventGetTarget(event))) {
				if (DOM.eventGetButton(event) == Event.BUTTON_LEFT) {
					cursor = new Point(
						OpenJWT.eventGetPointerX(event),
						OpenJWT.eventGetPointerY(event)
					);
					viewMovement.setX(0);
					viewMovement.setY(0);
					mouseDown = true;
					
					event.preventDefault();
					event.cancelBubble(true);
					
					OpenJWT.focus(RootPanel.getBodyElement());
				}
			}
			break;
		case Event.ONMOUSEMOVE:
			if (mouseDown) {
				Point newCursor = new Point(
					OpenJWT.eventGetPointerX(event),
					OpenJWT.eventGetPointerY(event)
				);
				
				Point offset = new Point(cursor);
				offset.subtract(newCursor);
				offset.setX((int) (offset.getX() / map.getZoom()));
				offset.setY((int) (offset.getY() / map.getZoom()));
				
				viewMovement.add(offset);
				
				cursor = newCursor;
				
				event.preventDefault();
				event.cancelBubble(true);
				
				OpenJWT.focus(RootPanel.getBodyElement());
			}
			break;
		case Event.ONMOUSEUP:
			if (mouseDown) {
				mouseDown = false;
				viewMovement.setX(0);
				viewMovement.setY(0);
				
				event.preventDefault();
				event.cancelBubble(true);
				
				OpenJWT.focus(RootPanel.getBodyElement());
			}
			break;
		}
		
		return true;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateKeyboard(int interpolation) {
		Point movement = new Point();
		
		int delta = (int) Math.round(600 * interpolation / 1000.);
		
		if ((keys & KEY_LEFT)  != 0)
			movement.addX(-delta);
		if ((keys & KEY_UP)    != 0)
			movement.addY(-delta);
		if ((keys & KEY_RIGHT) != 0)
			movement.addX(delta);
		if ((keys & KEY_DOWN)  != 0)
			movement.addY(delta);
		
		viewMovement.add(movement);
		
		if (Math.abs(viewMovement.getX()) > Math.abs(currentMovement.getX()))
			currentMovement.setX(viewMovement.getX());
		if (Math.abs(viewMovement.getY()) > Math.abs(currentMovement.getY()))
			currentMovement.setY(viewMovement.getY());
		
		if (Math.abs(viewMovement.getX()) < Math.abs(currentMovement.getX()))
			currentMovement.setX(currentMovement.getX() / 2);
		if (Math.abs(viewMovement.getY()) < Math.abs(currentMovement.getY()))
			currentMovement.setY(currentMovement.getY() / 2);
		
		if (currentMovement.getX() != 0 || currentMovement.getY() != 0)
			map.setView(new Point(currentMovement).add(map.getView()));
		
		viewMovement.setX(0);
		viewMovement.setY(0);
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class KeyboardHandler implements TimerHandler {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		// ----------------------------------------------------- METHODES -- //
		
		public void update(int interpolation) {
			updateKeyboard(interpolation);
		}
		
		public boolean isFinished() {
			return false;
		}
		
		public void destroy() {
			// Sans effet
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
