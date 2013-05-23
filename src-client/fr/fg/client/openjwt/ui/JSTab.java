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
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.ClassNameUpdater;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.SoundManager;

class JSTab extends JSComponent {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "Tab";
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,			"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,			"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,		"true");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,			"tab");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ClassNameUpdater updater;
	
	private String label;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSTab(String label) {
		super(UI_CLASS_ID);
		
		this.label = label;
		loadInnerHTML(new String[]{"label"});
		getSubElementById("label").setInnerHTML(label);
		sinkEvents(Event.ONCLICK | Event.ONMOUSEDOWN);
		if (Config.getGraphicsQuality() >=
				Config.VALUE_QUALITY_AVERAGE) {
			sinkEvents(Event.ONMOUSEOUT | Event.ONMOUSEOVER);
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
		getSubElementById("label").setInnerHTML(label);
	}
	
	public void onUnload() {
		super.onUnload();
		if (this.updater != null) {
			TimerManager.unregister(updater);
			this.updater = null;
		}
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONCLICK:
			Widget parent = getParent();
			int index = DOM.getChildIndex(parent.getElement(), getElement());
			
			while (!(parent instanceof JSTabbedPane))
				parent = parent.getParent();
			JSTabbedPane pane = (JSTabbedPane) parent;
			pane.setSelectedIndex(index);
			break;
		case Event.ONMOUSEOUT:
			if (event.getToElement() != null &&
					!getElement().isOrHasChild(event.getToElement())) {
				if (updater != null) {
					if (updater.isFinished()) {
						updater = new ClassNameUpdater(this, "state", 4, 20);
						TimerManager.register(updater);
					}
					updater.setTargetClass(0, true);
				} else {
					updater = new ClassNameUpdater(this, "state", 4, 20);
					updater.setTargetClass(0, true);
					TimerManager.register(updater);
				}
			}
			break;
		case Event.ONMOUSEOVER:
			if (event.getFromElement() != null &&
					!getElement().isOrHasChild(event.getFromElement()) &&
					Config.getGraphicsQuality() >=
					Config.VALUE_QUALITY_AVERAGE) {
				if (updater != null) {
					if (updater.isFinished()) {
						updater = new ClassNameUpdater(this, "state", 0, 20);
						TimerManager.register(updater);
					}
					updater.setTargetClass(4, true);
				} else {
					updater = new ClassNameUpdater(this, "state", 0, 20);
					updater.setTargetClass(4, true);
					TimerManager.register(updater);
				}
				updater.setTargetClass(4, true);
			}
			break;
		case Event.ONMOUSEDOWN:
			SoundManager.getInstance().playSound(SOUND_CLICK);
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
