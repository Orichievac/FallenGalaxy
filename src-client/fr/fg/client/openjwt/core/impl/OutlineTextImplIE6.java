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

public class OutlineTextImplIE6 extends OutlineTextImpl {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public String createContent(boolean preventSelection, String decoration,
			Point decorationOffset, boolean optimized) {
		if (!optimized)
			return super.createContent(preventSelection,
					decoration, decorationOffset, optimized);
		
		if (decoration.equals(DECORATION_OUTLINE)) {
			return "<div class=\"content\" style=\"position: static; " +
					"width: 100%; filter: dropShadow(color=black," +
					"offX=" + (decorationOffset.getX() - 1) + "," +
					"offY=" + decorationOffset.getY() + ");\" unselectable=\"on\">" +
				"<div style=\"width: 100%; filter: dropShadow(color=black," +
					"offX=" + (decorationOffset.getX() + 1) + "," +
					"offY=" + decorationOffset.getY() + ");\" unselectable=\"on\">" +
				"<div style=\"width: 100%; filter: dropShadow(color=black," +
					"offX=" + decorationOffset.getX() + "," +
					"offY=" + (decorationOffset.getY() + 1) + ");\" unselectable=\"on\">" +
				"<div style=\"width: 100%; filter: dropShadow(color=black," +
					"offX=" + decorationOffset.getX() + "," +
					"offY=" + (decorationOffset.getY() - 1) + ");\" " +
					(preventSelection ? "unselectable=\"on\"" : "") + ">" +
				"</div></div></div>" +
				"</div>";
		} else if (decoration.equals(DECORATION_SHADOW)) {
			return "<div class=\"content\" style=\"position: static; " +
				"filter: dropShadow(color=black," +
				"offX=" + (decorationOffset.getX() - 1) + "," +
				"offY=" + decorationOffset.getY() + ");\" " +
				(preventSelection ? "unselectable=\"on\"" : "") + ">" +
				"</div>";
		} else {
			return "<div class=\"content\" " + (preventSelection ?
					"unselectable=\"on\"" : "") + "></div>";
		}
	}
	
	public int getOutlinesCount(String decoration, boolean optimized) {
		if (!optimized)
			return super.getOutlinesCount(decoration, optimized);
		
		return 0;
	}
	
	public void setText(Element element, String text, String decoration, boolean optimized) {
		if (!optimized) {
			super.setText(element, text, decoration, optimized);
			return;
		}
		
		if (text == null)
			text = "";
		
		if (decoration.equals(DECORATION_OUTLINE)) {
			DOM.getChild(element, 0).getFirstChildElement().getFirstChildElement(
				).getFirstChildElement().setInnerHTML(text);
		} else {
			DOM.getChild(element, 0).setInnerHTML(text);
		}
	}
	
	public void setWidth(Element element, int width, String decoration, boolean optimized) {
		if (!optimized) {
			super.setWidth(element, width, decoration, optimized);
			return;
		}
		
		String widthValue = width != -1 ? width + "px" : "";
		
		DOM.getChild(element, 0).getStyle().setProperty("width", widthValue);
		if (decoration.equals(DECORATION_OUTLINE)) {
			DOM.getChild(element, 0).getFirstChildElement().getStyle().setProperty("width", widthValue);
			DOM.getChild(element, 0).getFirstChildElement().getFirstChildElement(
				).getStyle().setProperty("width", widthValue);
			DOM.getChild(element, 0).getFirstChildElement().getFirstChildElement(
				).getFirstChildElement().getStyle().setProperty("width", widthValue);
		}
	}
	
	public void setMaxWidth(Element element, int maxWidth, String decoration, boolean optimized) {
		if (!optimized) {
			super.setMaxWidth(element, maxWidth, decoration, optimized);
			return;
		}
		
		String maxWidthValue = maxWidth != -1 ? maxWidth + "px" : "";
		
		DOM.getChild(element, 0).getStyle().setProperty("maxWidth", maxWidthValue);
		if (decoration.equals(DECORATION_OUTLINE)) {
			DOM.getChild(element, 0).getFirstChildElement().getStyle().setProperty("width", maxWidthValue);
			DOM.getChild(element, 0).getFirstChildElement().getFirstChildElement(
				).getStyle().setProperty("width", maxWidthValue);
			DOM.getChild(element, 0).getFirstChildElement().getFirstChildElement(
				).getFirstChildElement().getStyle().setProperty("width", maxWidthValue);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
