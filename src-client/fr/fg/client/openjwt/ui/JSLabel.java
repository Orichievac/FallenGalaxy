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

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class JSLabel extends JSComponent {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "Label";
	
	public final static String
		ALIGN_LEFT = "left",
		ALIGN_CENTER = "center",
		ALIGN_RIGHT = "right";
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,			"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,			"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,		"true");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,			"label");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION,		"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION_OFFSET_X,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION_OFFSET_Y,	"0");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String text;
	
	private String alignment;
	
	private OutlineText outlineText;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSLabel() {
		this("");
	}
	
	public JSLabel(String text) {
		super(UI_CLASS_ID);
		
		loadInnerHTML(new String[]{"text"});
		setText(text);
		setAlignment(ALIGN_LEFT);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
		
		String decoration = getUIProperty(OpenJWT.FX_DECORATION);
		Element textElement = getSubElementById("text");
		
		if (decoration.equals("")) {
			textElement.setInnerHTML(text);
		} else {
			outlineText = TextManager.getText(text, decoration, new Point(
				getUIPropertyInt(OpenJWT.FX_DECORATION_OFFSET_X),
				getUIPropertyInt(OpenJWT.FX_DECORATION_OFFSET_Y)), true);
			
			textElement.setInnerHTML("");
			textElement.appendChild(outlineText.getElement());
			
			if (getPixelWidth() > 0)
				outlineText.setWidth(getPixelWidth());
		}
	}
	
	public void setAlignment(String alignment) {
		if (this.alignment != null)
			removeStyleName(this.alignment);
		
		this.alignment = alignment;
		addStyleName(alignment);
	}
	
	public void setPixelWidth(int width) {
		super.setPixelWidth(width);
		
		if (outlineText != null)
			outlineText.setWidth(width);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
