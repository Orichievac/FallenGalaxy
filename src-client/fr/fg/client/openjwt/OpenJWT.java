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

package fr.fg.client.openjwt;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;

public final class OpenJWT {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		ELEMENT = "element",
		INNER_HTML = "innerHtml",
		DEFAULT_WIDTH = "width",
		DEFAULT_HEIGHT = "height",
		LINE_HEIGHT = "lineHeight",
		HORIZONTAL_MARGIN = "horizontalMargin",
		VERTICAL_MARGIN = "verticalMargin",
		INNER_HORIZONTAL_MARGIN = "innerHorizontalMargin",
		INNER_VERTICAL_MARGIN = "innerVerticalMargin",
		CSS_CLASS = "cssClass",
		FX_DECORATION = "fx.decoration",
		FX_DECORATION_OFFSET_X = "fx.decoration.offsetX",
		FX_DECORATION_OFFSET_Y = "fx.decoration.offsetY",
		SCROLL_UP_BUTTON_WIDTH = "scrollUpButtonWidth",
		SCROLL_UP_BUTTON_HEIGHT = "scrollUpButtonHeight",
		SCROLL_DOWN_BUTTON_WIDTH = "scrollDownButtonWidth",
		SCROLL_DOWN_BUTTON_HEIGHT = "scrollDownButtonHeight",
		SCROLL_BUBBLE_MIN_HEIGHT = "scrollBubbleMinHeight",
		SCROLL_BUBBLE_WIDTH = "scrollBubbleWidth";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static int clientWidth, clientHeight;
	
	private static XmlStyle style;
	
	static {
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		
		Window.addWindowResizeListener(new WindowResizeListener() {
			public void onWindowResized(int width, int height) {
				clientWidth = Window.getClientWidth();
				clientHeight = Window.getClientHeight();
			}
		});
	}
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public final static boolean isStyleReady() {
		return style != null && style.isReady();
	}
	
	public final static void loadStyle(String url) {
		style = XmlStyle.load(url);
	}
	
	public final static int getClientWidth() {
		return clientWidth;
	}
	
	public final static int getClientHeight() {
		return clientHeight;
	}

	public final static native void select(com.google.gwt.dom.client.Element element) /*-{
		element.select();
	}-*/;

	public final static native void focus(com.google.gwt.dom.client.Element element) /*-{
		element.focus();
	}-*/;

	public final static native void blur(com.google.gwt.dom.client.Element element) /*-{
		element.blur();
	}-*/;
	
	public final static native int eventGetPointerX(Event event) /*-{
		return event.pageX || (event.clientX + 
			(document.documentElement.scrollLeft || document.body.scrollLeft));
	}-*/;
	
	public final static native int eventGetPointerY(Event event) /*-{
		return event.pageY || (event.clientY + 
			(document.documentElement.scrollTop || document.body.scrollTop));
	}-*/;
	
	public final static Element getElementById(String id, Element parent) {
		if (parent == null)
			return null;
		
		if (parent.getId() != null && parent.getId().equals(id))
			return parent;
		
		int count = parent.getChildNodes().getLength();
		
		for (int i = 0; i < count; i++) {
			Element element = getElementById(id, DOM.getChild(parent, i));
			if (element != null)
				return element;
		}
		
		return null;
	}
	
	public final static void setElementOpacity(Element element, double opacity) {
		if (opacity < 0.00001)
            opacity = 0.0;
        if (opacity > 1.0)
            opacity = 1.0;
        
		element.getStyle().setProperty("opacity", String.valueOf(opacity));
		element.getStyle().setProperty("mozOpacity", String.valueOf(opacity));
		if (opacity == 1) {
			element.getStyle().setProperty("filter", "");
		} else {
			element.getStyle().setProperty("filter",
				"progid:DXImageTransform.Microsoft.Alpha(opacity=" + (int) Math.round(opacity * 100) + ")");
		}
	}
	
	public final static void setElementFloat(Element element, String cssFloat) {
		element.getStyle().setProperty("cssFloat", cssFloat);
		element.getStyle().setProperty("styleFloat", cssFloat);
	}
	
	public final static String getUIProperty(String UIClassId, String key) {
		return style != null && style.isReady() ? style.getProperty(UIClassId, key) : null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
