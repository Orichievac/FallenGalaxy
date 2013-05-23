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

package fr.fg.client.core.ally;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.OptionPaneListener;

public class ApplicantDialog extends JSDialog implements ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSButton cancelApplyBt;
	
	private HTMLPanel descriptionPanel;
	
	private String allyName;
	
	private boolean vote;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ApplicantDialog() {
		super("Candidature", true, true, true);
		
		descriptionPanel = new HTMLPanel("");
		descriptionPanel.setWidth("290px");
		descriptionPanel.getElement().getStyle().setProperty("padding", "20px 5px");
		
		cancelApplyBt = new JSButton("Retirer ma candidature");
		cancelApplyBt.setPixelWidth(200);
		cancelApplyBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(descriptionPanel);
		layout.addRow();
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(cancelApplyBt);
		
		setComponent(layout);
		centerOnScreen();
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void setParameters(String allyName, boolean vote) {
		if (allyName.equals(this.allyName) && this.vote == vote)
			return;
		
		this.allyName = allyName;
		this.vote = vote;
		
		String text = vote ?
			"Votre candidature est en train d'être votée." :
			"Votre candidature est en train d'être examinée.";
		
		descriptionPanel.getElement().setInnerHTML(
			"<div class=\"center\" unselectable=\"on\">" +
			"Vous avez déposé une candidature pour entrer dans " +
			"l'alliance <b>" + allyName + "</b>.<br/>" + text + "</div>");
	}
	
	public void onClick(Widget sender) {
		if (sender == cancelApplyBt) {
			JSOptionPane.showMessageDialog(
				"Voulez-vous retirer votre candidature ?", "Confirmation",
				JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.YES_OPTION) {
							new Action("allies/cancelapply",
								Action.NO_PARAMETERS,
								UpdateManager.UPDATE_CALLBACK);
						}
					}
				});
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
