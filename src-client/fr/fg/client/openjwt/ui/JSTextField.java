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

package fr.fg.client.openjwt.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerCollection;
import com.google.gwt.user.client.ui.SourcesKeyboardEvents;

import fr.fg.client.openjwt.OpenJWT;

public class JSTextField extends JSComponent implements SourcesKeyboardEvents {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "TextField";
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,			"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,			"<input id=\"${text}\" type=\"text\" value=\"\"/>");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,		"false");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,			"textfield");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private KeyboardListenerCollection keyboardListeners;
	
	private boolean editable;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSTextField() {
		this("", UI_CLASS_ID);
	}
	
	public JSTextField(String text) {
		this(text, UI_CLASS_ID);
	}
	
	public JSTextField(String text, String UIClassId) {
		super(UIClassId);
		
		this.editable = true;
		loadInnerHTML(new String[]{"text"});
		setText(text);
		
		DOM.sinkEvents(getSubElementById("text"), Event.FOCUSEVENTS);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getText() {
		return getSubElementById("text").getPropertyString("value");
	}
	
	public void setText(String text) {
		getSubElementById("text").setPropertyString("value", text);
	}
	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		if (editable)
			getSubElementById("text").removeAttribute("readonly");
		else
			getSubElementById("text").setAttribute("readonly", "readonly");
	}
	
	public String getName() {
		return DOM.getElementAttribute(getSubElementById("text"), "name");
	}
	
	public void setName(String name) {
		DOM.setElementAttribute(getSubElementById("text"), "name", name);
	}

	public String getMaxLength() {
		return DOM.getElementAttribute(getSubElementById("maxlength"), "name");
	}
	
	public void setMaxLength(int maxLength) {
		DOM.setElementAttribute(getSubElementById("text"),
				"maxlength", String.valueOf(maxLength));
	}
	
	public void setFocus(boolean focused) {
		if (focused)
			OpenJWT.focus(getSubElementById("text"));
		else
			OpenJWT.blur(getSubElementById("text"));
	}

	public void select() {
		OpenJWT.select(getSubElementById("text"));
	}
	
	public void addKeyboardListener(KeyboardListener listener) {
		if (keyboardListeners == null) {
			keyboardListeners = new KeyboardListenerCollection();
			DOM.sinkEvents(getSubElementById("text"), Event.KEYEVENTS);
		}
		keyboardListeners.add(listener);
	}
	
	public void removeKeyboardListener(KeyboardListener listener) {
		if (keyboardListeners != null) {
			keyboardListeners.remove(listener);
		}
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		
		switch (DOM.eventGetType(event)) {
		case Event.ONKEYDOWN:
		case Event.ONKEYUP:
		case Event.ONKEYPRESS:
			if (keyboardListeners != null)
				keyboardListeners.fireKeyboardEvent(this, event);
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
