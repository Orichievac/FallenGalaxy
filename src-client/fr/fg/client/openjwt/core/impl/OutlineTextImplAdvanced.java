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

package fr.fg.client.openjwt.core.impl;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import fr.fg.client.openjwt.core.Point;

public class OutlineTextImplAdvanced extends OutlineTextImpl {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public String createContent(boolean preventSelection, String decoration,
			Point decorationOffset, boolean optimized) {
		if (decoration.equals(DECORATION_OUTLINE) || decoration.equals(DECORATION_SHADOW))
			return "<div class=\"content advancedOutline advancedOutline-" + decoration + "\" " +
				(preventSelection ? "unselectable=\"on\"" : "") + "></div>";
		else
			return "<div class=\"content advancedOutline\" " +
				(preventSelection ? "unselectable=\"on\"" : "") + "></div>";
	}
	
	public int getOutlinesCount(String decoration, boolean optimized) {
		return 0;
	}
	
	public void setText(Element element, String text, String decoration, boolean optimized) {
		if (text == null)
			text = "";
		
		DOM.getChild(element, 0).setInnerHTML(text);
	}
	
	public void setWidth(Element element, int width, String decoration, boolean optimized) {
		String widthValue = width != -1 ? width + "px" : "";
		
		DOM.getChild(element, 0).getStyle().setProperty("width", widthValue);
	}
	
	public void setMaxWidth(Element element, int maxWidth, String decoration, boolean optimized) {
		String maxWidthValue = maxWidth != -1 ? maxWidth + "px" : "";
		
		DOM.getChild(element, 0).getStyle().setProperty("maxWidth", maxWidthValue);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
