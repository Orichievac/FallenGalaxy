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

public class OutlineTextImpl {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		DECORATION_OUTLINE = "outline",
		DECORATION_SHADOW = "shadow";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public String createContent(boolean preventSelection, String decoration,
			Point decorationOffset, boolean optimized) {
		if (decoration.equals(DECORATION_OUTLINE)) {
			return "<div class=\"outline n\" unselectable=\"on\" style=\"margin: " +
					(decorationOffset.getY() - 1) + "px 0 1px " +
					 decorationOffset.getX() + "px;\"></div>" +
				"<div class=\"outline w\" unselectable=\"on\" style=\"margin: " +
					 decorationOffset.getY() + "px 1px 0 " +
					(decorationOffset.getX() - 1) + "px;\"></div>" +
				"<div class=\"outline e\" unselectable=\"on\" style=\"margin: " +
					 decorationOffset.getY() + "px -1px 0 " +
					(decorationOffset.getX() + 1) + "px;\"></div>" +
				"<div class=\"outline s\" unselectable=\"on\" style=\"margin: " +
					(decorationOffset.getY() + 1) + "px 0 -1px " +
					 decorationOffset.getX() + "px;\"></div>" +
				"<div class=\"content\" " + (preventSelection ?
					"unselectable=\"on\"" : "") + "></div>" +
				"<div style=\"visibility: hidden;\"></div>";
		} else if (decoration.equals(DECORATION_SHADOW)) {
			return "<div class=\"outline shadow\" unselectable=\"on\" style=\"margin: " +
					(decorationOffset.getY() + 1) + "px 0 0 " +
					(decorationOffset.getX() + 1) + "px;\"></div>" +
				"<div class=\"content\" " + (preventSelection ?
					"unselectable=\"on\"" : "") + "></div>" +
				"<div style=\"visibility: hidden;\"></div>";
		} else {
			return "<div class=\"content\" " + (preventSelection ?
					"unselectable=\"on\"" : "") + "></div>" +
				"<div style=\"visibility: hidden;\"></div>";
		}
	}
	
	public int getOutlinesCount(String decoration, boolean optimized) {
		if (decoration.equals(DECORATION_OUTLINE))
			return 4;
		else if (decoration.equals(DECORATION_SHADOW))
			return 1;
		else
			return 0;
	}
	
	public void setText(Element element, String text, String decoration, boolean optimized) {
		if (text == null)
			text = "";
		
		int i;
		for (i = 0; i < getOutlinesCount(decoration, optimized); i++)
			// Ajoute un suffixe de la forme _i à la fin des id pour éviter
			// les doublons
			DOM.getChild(element, i).setInnerHTML(
				text.replaceAll("(<[^>]+id=\")([a-zA-Z0-9-]+)\"", "$1$2_" + i + "\""));
		DOM.getChild(element, i).setInnerHTML(text);
		DOM.getChild(element, i + 1).setInnerHTML(text);
	}
	
	public void setWidth(Element element, int width, String decoration, boolean optimized) {
		String widthValue = width != -1 ? width + "px" : "";
		
		int i;
		for (i = 0; i < getOutlinesCount(decoration, optimized); i++)
			DOM.getChild(element, i).getStyle().setProperty("width", widthValue);
		DOM.getChild(element, i).getStyle().setProperty("width", widthValue);
		DOM.getChild(element, i + 1).getStyle().setProperty("width", widthValue);
	}
	
	public void setMaxWidth(Element element, int maxWidth, String decoration, boolean optimized) {
		String maxWidthValue = maxWidth != -1 ? maxWidth + "px" : "";
		
		int i;
		for (i = 0; i < getOutlinesCount(decoration, optimized); i++)
			DOM.getChild(element, i).getStyle().setProperty("maxWidth", maxWidthValue);
		DOM.getChild(element, i).getStyle().setProperty("maxWidth", maxWidthValue);
		DOM.getChild(element, i + 1).getStyle().setProperty("maxWidth", maxWidthValue);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
