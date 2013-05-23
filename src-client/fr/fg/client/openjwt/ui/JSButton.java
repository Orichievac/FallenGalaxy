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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.SourcesClickEvents;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.ClassNameUpdater;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.SoundManager;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class JSButton extends JSComponent implements SourcesClickEvents  {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "Button";
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,				"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,				"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,			"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,			"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,			"true");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,		"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,		"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HORIZONTAL_MARGIN,"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,				"button");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION,			"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION_OFFSET_X,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION_OFFSET_Y,	"0");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ClickListenerCollection clickListeners;
	
	private ClassNameUpdater updater;
	
	private String label;
	
	private boolean selected;
	
	private boolean active;
	
	private OutlineText outlineLabel;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSButton() {
		this("&nbsp;");
	}
	
	public JSButton(String label) {
		super(UI_CLASS_ID);
		this.selected = false;
		this.active = false;
		loadInnerHTML(new String[]{"label"});
		setLabel(label);
		sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		if (selected && !this.selected)
			addStyleDependentName("selected");
		else if (!selected && this.selected)
			removeStyleDependentName("selected");
		this.selected = selected;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
		
		String decoration = getUIProperty(OpenJWT.FX_DECORATION);
		Element labelElement = getSubElementById("label");
		
		if (decoration.equals("")) {
			labelElement.setInnerHTML(label);
		} else {
			outlineLabel = TextManager.getText(label, decoration, new Point(
				getUIPropertyInt(OpenJWT.FX_DECORATION_OFFSET_X),
				getUIPropertyInt(OpenJWT.FX_DECORATION_OFFSET_Y)), true);
			
			labelElement.setInnerHTML("");
			labelElement.appendChild(outlineLabel.getElement());
			
			int width = getPixelWidth();
			if (width != 0)
				outlineLabel.setWidth(Math.max(0, width - Math.max(
					getUIPropertyInt(OpenJWT.INNER_HORIZONTAL_MARGIN),
					getUIPropertyInt(OpenJWT.HORIZONTAL_MARGIN))));
		}
	}
	
	public void setPixelWidth(int width) {
		super.setPixelWidth(width);
		
		if (outlineLabel != null)
			outlineLabel.setWidth(Math.max(0, width - Math.max(
				getUIPropertyInt(OpenJWT.INNER_HORIZONTAL_MARGIN),
				getUIPropertyInt(OpenJWT.HORIZONTAL_MARGIN))));
	}
	
	public void addClickListener(ClickListener listener) {
		if (clickListeners == null)
			clickListeners = new ClickListenerCollection();
		clickListeners.add(listener);
	}
	
	public void removeClickListener(ClickListener listener) {
		if (clickListeners != null)
			clickListeners.remove(listener);
	}

	public void doClick() {
		if (clickListeners != null)
			clickListeners.fireClick(this);
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
		super.onBrowserEvent(event);
		
		switch (DOM.eventGetType(event)) {
		case Event.ONCLICK:
			if (clickListeners != null)
				clickListeners.fireClick(this);
			break;
		case Event.ONMOUSEDOWN:
			SoundManager.getInstance().playSound(SOUND_CLICK);
			addStyleName("buttonActive");
			active = true;
			break;
		case Event.ONMOUSEUP:
			if (active) {
				removeStyleName("buttonActive");
				active = false;
			}
			break;
		case Event.ONMOUSEOVER:
			if (Config.getGraphicsQuality() >=
					Config.VALUE_QUALITY_AVERAGE) {
				if (updater != null) {
					if (updater.isFinished()) {
						updater = new ClassNameUpdater(this, "state", 0, 10);
						TimerManager.register(updater);
					}
					updater.setTargetClass(4, true);
					updater.setIncrement(20);
				} else {
					updater = new ClassNameUpdater(this, "state", 0, 10);
					TimerManager.register(updater);
					updater.setTargetClass(4, true);
					updater.setIncrement(20);
				}
			}
			break;
		case Event.ONMOUSEOUT:
			if (updater != null) {
				if (updater.isFinished()) {
					updater = new ClassNameUpdater(this, "state", 4, 10);
					TimerManager.register(updater);
				}
				updater.setTargetClass(0, true);
				updater.setIncrement(10);
			}
			
			if (active) {
				removeStyleName("buttonActive");
				active = false;
			}
			
			break;
		}
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
