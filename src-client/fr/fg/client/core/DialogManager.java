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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class DialogManager extends SimplePanel {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ChoiceListenerCollection choiceListeners;
	
	private AvatarAnimationUpdater updater;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DialogManager() {
		getElement().setId("talks");
		setVisible(false);
		RootPanel.get().add(this);
		
		sinkEvents(Event.ONCLICK);
	}
	
	// --------------------------------------------------------- METHODES -- //

	public void show(String talker, String message) {
		show(talker, message, new String[0], null);
	}
	
	public void show(String talker, String message, String[] options,
			boolean[] validOptions) {
		show(talker, message, options, null, null);
	}
	
	public void show(String talker, String message, String[] options,
			boolean[] validOptions, String avatar) {
		String talk = "<div class=\"talk\" unselectable=\"on\">";
		
		// Avatar
		if (avatar != null && avatar.length() > 0) {
			talk += "<div id=\"avatar\" class=\"avatar\" style=\"" +
				"background: url('" + Config.getMediaUrl() +
				"images/avatars/" + avatar + ".png') 0 0 repeat;\"></div>";
		}
		
		// Dialogue
		talk += "<div class=\"talker\" unselectable=\"on\">" + talker + "</div>" + message;
		
		// Réponses possible
		if (options.length > 0)
			talk += "<div unselectable=\"on\">&nbsp;</div>";
		
		for (int i = 0; i < options.length; i++)
			talk += "<div id=\"answer" + i + "\" class=\"answer" +
				(validOptions[i] ? "" : " invalidAnswer") + "\" " +
				"unselectable=\"on\">" + (i + 1) + ". " + options[i] + "</div>";
		
		talk += "</div>";
		
		// Crée le texte
		OutlineText text = TextManager.getText(talk);
		
		clear();
		add(text);
		
		// Animation de l'avatar
		if (updater == null) {
			if (avatar != null) {
				updater = new AvatarAnimationUpdater("avatar");
				TimerManager.register(updater);
			}
		} else {
			if (updater != null) {
				TimerManager.unregister(updater);
				updater = null;
			}
		}
		
		setVisible(true);
	}
	
	public void hide() {
		setVisible(false);
		
		if (updater != null) {
			TimerManager.unregister(updater);
			updater = null;
		}
		
		clear();
	}
	
	public void addChoiceListener(ChoiceListener listener) {
		if (choiceListeners == null)
			choiceListeners = new ChoiceListenerCollection();
		choiceListeners.add(listener);
	}
	
	public void removeChoiceListener(ChoiceListener listener) {
		if (choiceListeners != null)
			choiceListeners.remove(listener);
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			int option = 0;
			
			while (true) {
				Element element = DOM.getElementById("answer" + option);
				
				if (element == null)
					return;
				
				if (element.isOrHasChild(event.getTarget())) {
					if (!element.getClassName().contains("invalidAnswer"))
						choiceListeners.fireChoice(this, option);
					return;
				}
				
				option++;
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	public static interface ChoiceListener {
		// --------------------------------------------------- CONSTANTES -- //
		// ----------------------------------------------------- METHODES -- //
		
		public void onChoice(DialogManager source, int choice);
	}
	
	public static class ChoiceListenerCollection extends ArrayList<ChoiceListener> {
		// --------------------------------------------------- CONSTANTES -- //

		private static final long serialVersionUID = -8939574172855840895L;
		
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		// ----------------------------------------------------- METHODES -- //
		
		public void fireChoice(DialogManager source, int choice) {
			for (ChoiceListener listener : this) {
				listener.onChoice(source, choice);
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private static class AvatarAnimationUpdater implements TimerHandler {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int scrollLength, scrollValue, scrollOffset;
		
		private String elementId;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public AvatarAnimationUpdater(String elementId) {
			this.elementId = elementId;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public boolean isFinished() {
			return false;
		}
		
		public void update(int interpolation) {
			Element avatarElement = DOM.getElementById(elementId);
			
			if (avatarElement == null)
				return;
			
			int offsetY = 0;
			
			if (Math.random() < (scrollLength > 0 ? .3 : .02)) {
				offsetY = 200;
			} else if (Math.random() < .04) {
				offsetY = 100;
			}
			
			if (scrollLength == 0 && Math.random() < .004) {
				scrollOffset = -145 + (int) (Math.random() * 10);
				scrollValue = 0;
				scrollLength = (int) (Math.random() * 6 + 8);
			} else if (scrollLength > 0) {
				scrollOffset -= 10;
				offsetY += Math.random() * 31 - 15;
				
				scrollValue++;
				if (scrollValue > scrollLength) {
					scrollValue = 0;
					scrollLength = 0;
					scrollOffset = 0;
				}
			}
			
			avatarElement.getStyle().setProperty("backgroundPosition",
					"0 " + (-scrollOffset - offsetY) + "px");
		}
		
		public void destroy() {
			elementId = null;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
