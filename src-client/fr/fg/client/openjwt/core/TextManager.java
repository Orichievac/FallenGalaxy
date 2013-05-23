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

package fr.fg.client.openjwt.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.core.impl.OutlineTextImpl;

public class TextManager {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		DECORATION_OUTLINE = OutlineTextImpl.DECORATION_OUTLINE,
		DECORATION_SHADOW = OutlineTextImpl.DECORATION_SHADOW;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public static OutlineText getText(String text) {
		return getText(text, DECORATION_OUTLINE, new Point(0, 0), true);
	}
	
	public static OutlineText getText(String text, boolean preventSelection) {
		return getText(text, DECORATION_OUTLINE, new Point(0, 0), preventSelection, false);
	}
	
	public static OutlineText getText(String text, boolean preventSelection, boolean optimized) {
		return getText(text, DECORATION_OUTLINE, new Point(0, 0), preventSelection, optimized);
	}
	
	public static OutlineText getText(String text, String decoration) {
		return getText(text, decoration, new Point(0, 0), true, false);
	}
	
	public static OutlineText getText(String text, String decoration,
			Point decorationOffset) {
		return getText(text, decoration, decorationOffset, true, false);
	}
	
	public static OutlineText getText(String text, String decoration,
			boolean preventSelection) {
		return getText(text, decoration, new Point(0, 0), preventSelection, false);
	}
	
	public static OutlineText getText(String text, String decoration,
			Point decorationOffset, boolean preventSelection) {
		return getText(text, decoration, decorationOffset, preventSelection, false);
	}
	
	public static OutlineText getText(String text, String decoration,
			Point decorationOffset, boolean preventSelection, boolean optimized) {
		OutlineText outlineText = new OutlineText(decoration, decorationOffset,
				preventSelection, optimized);
		outlineText.setText(text);
		
		return outlineText;
	}
	
	public static void attachText(Panel parent, OutlineText outlineText) {
		if (parent != null)
			parent.add(outlineText);
		else
			RootPanel.get().add(outlineText);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	public static class OutlineText extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private final static OutlineTextImpl impl = GWT.create(OutlineTextImpl.class);
		
		private boolean optimized;
		
		private String decoration;
		
		private Point decorationOffset;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public OutlineText(String decoration, Point decorationOffset,
				boolean preventSelection, boolean optimized) {
			this.decoration = decoration;
			this.decorationOffset = decorationOffset;
			this.optimized = optimized;
			
			setElement(DOM.createDiv());
			addStyleName("outlineText");
			getElement().setAttribute("unselectable", "on");
			getElement().setInnerHTML(createContent(preventSelection));
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void setText(String text) {
			impl.setText(getElement(), text, decoration, optimized);
		}
		
		public void setWidth(int width) {
			impl.setWidth(getElement(), width, decoration, optimized);
		}
		
		public void setMaxWidth(int maxWidth) {
			impl.setMaxWidth(getElement(), maxWidth, decoration, optimized);
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
		
		private String createContent(boolean preventSelection) {
			return impl.createContent(preventSelection,
					decoration, decorationOffset, optimized);
		}
	}
}
