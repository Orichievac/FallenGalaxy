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

package fr.fg.client.map.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import fr.fg.client.core.Client;
import fr.fg.client.data.GateData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.core.Config;

public class GateItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private GateData gateData;
	
	private boolean jumpRadiusVisible;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public GateItem(GateData gateData, UIItemRenderingHints hints) {
		super(gateData.getX(), gateData.getY(), hints);
		
		this.gateData = gateData;
		this.jumpRadiusVisible = false;
		
		setStylePrimaryName("gate"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		setToolTipText("<div class=\"title\">" + messages.gate() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.gateDesc() + "</div>", 200); //$NON-NLS-1$ //$NON-NLS-2$
		
		if (Config.getGraphicsQuality() >= Config.VALUE_QUALITY_MAXIMUM) {
			int size = (int) (120 * hints.getZoom());
			int margin = 0;
			boolean display = hints.getZoom() >= .3;
			
			getElement().setInnerHTML(
				"<div style=\"position: relative;\">" +
				"<object id=\"gate" + gateData.getId() + "_object\" " +
						"classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-4moule3540000\" " +
						"codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=10,0,0,0\" " +
						"width=\"" + size + "\" height=\"" + size + "\" " +
						"style=\"position: absolute; margin: " + margin + "px;" +
						(display ? "" : " display: none;") + "\">" +
					"<param name=\"allowScriptAccess\" value=\"allDomain\"/>" +
					"<param name=\"allowFullScreen\" value=\"false\"/>" +
					"<param name=\"movie\" value=\"" + Config.getMediaUrl() + "swf/Gate.swf\"/>" +
					"<param name=\"quality\" value=\"high\" />" +
					"<param name=\"wmode\" value=\"transparent\" />" +
					"<param name=\"bgcolor\" value=\"#ffffff\" />" +
					"<embed id=\"gate" + gateData.getId() + "_embed\" " + 
						"src=\"" + Config.getMediaUrl() + "swf/Gate.swf\" " +
						"quality=\"high\" wmode=\"transparent\" bgcolor=\"#ffffff\" " +
						"width=\"" + size + "\" height=\"" + size + "\" " +
						"name=\"Gate\" " +
						"allowScriptAccess=\"allDomain\" " +
						"allowFullScreen=\"false\" " +
						"type=\"application/x-shockwave-flash\" " +
						"pluginspage=\"http://www.adobe.com/go/getflashplayer_fr\"" +
						(display ? "" : " style=\"display: none;\"") + "/>" +
				"</object>" +
				"<div class=\"blocker\" style=\"background-image: url('" +
					Config.getMediaUrl() + "images/misc/blank.gif');\"></div>" +
				"</div>"
			);
		}
		
		sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		
		if (jumpRadiusVisible)
			updateCanvas();
		
		if (Config.getGraphicsQuality() >= Config.VALUE_QUALITY_MAXIMUM) {
			int size = (int) (120 * hints.getZoom());
			int margin = 0;
			boolean display = hints.getZoom() >= .3;
			
			String id = "gate" + gateData.getId();
			Element element = DOM.getElementById(id + "_object");
			
			if (element != null) {
				element.setAttribute("width", String.valueOf(size));
				element.setAttribute("height", String.valueOf(size));
				element.getStyle().setProperty("margin", margin + "px");
				element.getStyle().setProperty("display", display ? "" : "none");
			}
			
			element = DOM.getElementById(id + "_embed");
			
			if (element != null) {
				element.setAttribute("width", String.valueOf(size));
				element.setAttribute("height", String.valueOf(size));
				element.getStyle().setProperty("display", display ? "" : "none");
			}
		}
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEOVER:
			jumpRadiusVisible = true;
			updateCanvas();
			break;
		case Event.ONMOUSEOUT:
			jumpRadiusVisible = false;
			Client.getInstance().getAreaContainer().hideCanvas();
			break;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		gateData = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateCanvas() {
		Client.getInstance().getAreaContainer().drawCircle(
			gateData.getX(), gateData.getY(),
			GateData.HYPERGATE_JUMP_RADIUS, "#00a9d6");
	}
}
