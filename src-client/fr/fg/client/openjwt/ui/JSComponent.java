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

import java.util.HashMap;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SourcesMouseEvents;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.SoundManager;
import fr.fg.client.openjwt.core.ToolTipManager;

public class JSComponent extends AbsolutePanel implements SourcesMouseEvents {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String SOUND_CLICK = "click";
	
	public final static String COMPONENT_ID_PREFIX = "ojwtComponent";
	
	static {
		SoundManager.getInstance().configSound(SOUND_CLICK, 90, false);
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static HashMap<String, HashMap<String, String>> defaultProperties;
	
	private static int nextComponentId = 1;
	
	private String UIClassId;
	
	private String id;
	
	private int width;
	
	private int height;
	
	private MouseListenerCollection mouseListeners;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSComponent(String UIClassId) {
		super(DOM.createElement(getUIProperty(UIClassId, OpenJWT.ELEMENT)));
		
		this.UIClassId = UIClassId;
		this.id = getComponentId();
		this.width = getUIPropertyInt(OpenJWT.DEFAULT_WIDTH);
		this.height = getUIPropertyInt(OpenJWT.DEFAULT_HEIGHT);
		initElement();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getId() {
		return id;
	}

	public int getPixelWidth() {
		return width != -1 ? width + getUIPropertyInt(
				OpenJWT.HORIZONTAL_MARGIN) : getOffsetWidth();
	}
	
	public void setPixelWidth(int width) {
		if (width < -1)
			width = 0;
		
		if (width != -1) {
			this.width = Math.max(0, width - getUIPropertyInt(
				OpenJWT.HORIZONTAL_MARGIN));
		} else if (getUIPropertyInt(OpenJWT.DEFAULT_WIDTH) != -1) {
			this.width = Math.max(0, getUIPropertyInt(OpenJWT.DEFAULT_WIDTH) -
				getUIPropertyInt(OpenJWT.HORIZONTAL_MARGIN));
		} else {
			this.width = -1;
		}
		
		if (this.width != -1)
			setWidth(this.width + "px");
		else
			setWidth("");
	}

	public int getPixelHeight() {
		return height != -1 ? height + getUIPropertyInt(
				OpenJWT.VERTICAL_MARGIN) : getOffsetHeight();
	}
	
	public void setPixelHeight(int height) {
		if (height < -1)
			height = 0;
		
		if (height != -1) {
			this.height = Math.max(0, height - getUIPropertyInt(
					OpenJWT.VERTICAL_MARGIN));
		} else if (getUIPropertyInt(OpenJWT.DEFAULT_HEIGHT) != -1) {
			this.height = Math.max(0, getUIPropertyInt(OpenJWT.DEFAULT_HEIGHT) -
				getUIPropertyInt(OpenJWT.VERTICAL_MARGIN));
		} else {
			this.height = -1;
		}
		
		if (this.height != -1) {
			setHeight(this.height + "px");
			if (getUIPropertyBoolean(OpenJWT.LINE_HEIGHT))
				DOM.setStyleAttribute(getElement(), "lineHeight", this.height + "px");
		} else {
			setHeight("");
			if (getUIPropertyBoolean(OpenJWT.LINE_HEIGHT))
				DOM.setStyleAttribute(getElement(), "lineHeight", "");
		}
	}
	
	public void setPixelSize(int width, int height) {
		setPixelWidth(width);
		setPixelHeight(height);
	}
	
	public String getToolTipText() {
		return ToolTipManager.getToolTipText(getElement());
	}
	
	public void setToolTipText(String toolTipText) {
		if (toolTipText == null)
			ToolTipManager.getInstance().unregister(getElement());
		else
			ToolTipManager.getInstance().register(getElement(), toolTipText);
	}

	public void setToolTipText(String toolTipText, int toolTipWidth) {
		if (toolTipText == null)
			ToolTipManager.getInstance().unregister(getElement());
		else
			ToolTipManager.getInstance().register(
					getElement(), toolTipText, toolTipWidth);
	}
	
	public static String getComponentId() {
		return COMPONENT_ID_PREFIX + nextComponentId++;
	}

	public void addMouseListener(MouseListener listener) {
		if (mouseListeners == null) {
			sinkEvents(Event.MOUSEEVENTS);
			mouseListeners = new MouseListenerCollection();
		}
		mouseListeners.add(listener);
	}
	
	public void removeMouseListener(MouseListener listener) {
		if (mouseListeners != null)
			mouseListeners.remove(listener);
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
			if (mouseListeners != null)
				mouseListeners.fireMouseEvent(this, event);
			break;
		case Event.ONMOUSEUP:
			if (mouseListeners != null)
				mouseListeners.fireMouseEvent(this, event);
			break;
		case Event.ONMOUSEOVER:
			if (mouseListeners != null)
				mouseListeners.fireMouseEvent(this, event);
			break;
		case Event.ONMOUSEOUT:
			if (mouseListeners != null)
				mouseListeners.fireMouseEvent(this, event);
			break;
		case Event.ONMOUSEMOVE:
			if (mouseListeners != null)
				mouseListeners.fireMouseEvent(this, event);
			break;
		}
	}
	
	public static String getUIProperty(String UIClassId, String key) {
		String property = OpenJWT.getUIProperty(UIClassId, key);
		
		if (property == null)
			property = getDefaultProperty(UIClassId, key);
		
		return property;
	}
	
	public static int getUIPropertyInt(String UIClassId, String key) {
		String property = OpenJWT.getUIProperty(UIClassId, key);
		
		if (property == null)
			property = getDefaultProperty(UIClassId, key);
		
		return property != null ? Integer.parseInt(property) : null;
	}

	public static boolean getUIPropertyBoolean(String UIClassId, String key) {
		String property = OpenJWT.getUIProperty(UIClassId, key);
		
		if (property == null)
			property = getDefaultProperty(UIClassId, key);
		
		return property != null ? Boolean.parseBoolean(property) : null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	protected void initElement() {
		getElement().setId(id);
		getElement().setAttribute("unselectable", "on");
		setStyleName(getUIProperty(OpenJWT.CSS_CLASS));
		if (width != -1)
			setPixelWidth(width);
		if (height != -1)
			setPixelHeight(height);
	}
	
	protected Element getSubElementById(String subId) {
		Element subElement = OpenJWT.getElementById(
				getId() + subId, getElement());
		if (subElement == null)
			subElement = getElement();
		return subElement;
	}
	
	protected void loadInnerHTML(String[] subIdList) {
		String innerHTML = getUIProperty(OpenJWT.INNER_HTML);
		
		for (int i = 0; i < subIdList.length; i++)
			innerHTML = innerHTML.replaceAll("\\$\\{" + subIdList[i] + "\\}",
					getId() + subIdList[i]);
		
		DOM.setInnerHTML(getElement(), innerHTML);
	}
	
	protected static String getDefaultProperty(String UIClassId, String key) {
		HashMap<String, String> properties = defaultProperties.get(UIClassId);
		
		if (properties == null)
			return null;
		
		return properties.get(key);
	}
	
	protected static void setDefaultProperty(String UIClassId, String key, String value) {
		if (defaultProperties == null)
			defaultProperties = new HashMap<String, HashMap<String,String>>();
		
		HashMap<String, String> properties = defaultProperties.get(UIClassId);
		
		if (properties == null) {
			properties = new HashMap<String, String>();
			defaultProperties.put(UIClassId, properties);
		}
		
		properties.put(key, value);
	}

	protected String getUIProperty(String key) {
		return getUIProperty(UIClassId, key);
	}
	
	protected int getUIPropertyInt(String key) {
		return getUIPropertyInt(UIClassId, key);
	}
	
	protected boolean getUIPropertyBoolean(String key) {
		return getUIPropertyBoolean(UIClassId, key);
	}
}
