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

package fr.fg.client.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.data.ProbeReportData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSRowLayout;

public class ProbeReportDialog extends JSDialog implements ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSButton okBt;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ProbeReportDialog(ProbeReportData data) {
		super("Sonde de " + data.getSystemName(), true, true, true);
		
		StaticMessages messages = GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		String report = "<table style=\"width: 100%;\" cellspacing=\"0\">" +
			"<tr class=\"odd\">" +
			"<td style=\"padding: 4px;\">Espace disponible</td>" +
			"<td style=\"padding: 4px; text-align: right\">" + data.getAvailableSpace() + "&nbsp;<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"resource credits\"/>&nbsp;</td>" +
			"</tr>";
		
		for (int i = 0; i < data.getResourcesCount(); i++) {
			report += "<tr class=\"" + (i % 2 == 1 ? "odd" : "even") + "\" style=\"padding: 4px;\">" +
				"<td style=\"padding: 4px;\">" + dynamicMessages.getString("resource" + i) + "</td>" +
				"<td style=\"padding: 4px; text-align: right\">" + data.getResourceAt(i) + "&nbsp;<img src=\"" + Config.getMediaUrl() +
					"images/misc/blank.gif\" class=\"resource r" + i + "\"/>&nbsp;</td>" +
				"</tr>";
		}
		
		report += "</table>";
		
		HTMLPanel reportPanel = new HTMLPanel(report);
		reportPanel.setWidth("300px");
		
		// Bouton OK
		okBt = new JSButton(messages.ok());
		okBt.setPixelWidth(100);
		okBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(reportPanel);
		layout.addRowSeparator(10);
		layout.addComponent(okBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		
		setComponent(layout);
		centerOnScreen();
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			setVisible(false);
		}
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible) {
			okBt.removeClickListener(this);
			okBt = null;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
