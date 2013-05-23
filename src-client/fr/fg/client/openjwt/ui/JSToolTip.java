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

public class JSToolTip extends JSComponent {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "ToolTip";
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,			"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,			"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,		"false");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,			"tooltip");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION,		"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION_OFFSET_X,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION_OFFSET_Y,	"0");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String text;
	
	private OutlineText outlineText;
	
	private int width;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSToolTip() {
		this(null);
	}
	
	public JSToolTip(String text) {
		this(text, -1);
	}
	
	public JSToolTip(String text, int width) {
		super(UI_CLASS_ID);
		
		loadInnerHTML(new String[]{"text"});
		setPixelWidth(width);
		setText(text);
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
			
			if (this.width != -1)
				setPixelWidth(getPixelWidth());
		}
	}
	
	@Override
	public int getPixelWidth() {
		return this.width != -1 ? this.width +
			getUIPropertyInt(OpenJWT.HORIZONTAL_MARGIN) : getOffsetWidth();
	}
	
	@Override
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
		
		if (this.width != -1) {
			if (outlineText != null)
				outlineText.setMaxWidth(this.width);
			else
				getSubElementById("text").getStyle().setProperty("maxWidth", this.width + "px");
		} else {
			if (outlineText != null)
				outlineText.setMaxWidth(-1);
			else
				getSubElementById("text").getStyle().setProperty("maxWidth", "");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
