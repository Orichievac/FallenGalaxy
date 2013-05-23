/*
Copyright 2010 Thierry Chevalier

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

import java.util.HashMap;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.JSTextPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;

public class BugReportDialog extends JSDialog implements ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	private JSRowLayout mainLayout;
	private JSLabel titleLabel;
	private JSTextField title;
	private JSTextPane description;
	private JSButton confirmBt, cancelBt;
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	public BugReportDialog() {
		super("Signaler un bug",true,true,true);

	}
	// --------------------------------------------------------- METHODES -- //
	public void setVisible(boolean visible) {
		if(isVisible() == visible) {
			return;
		}
		
		if(visible) {
			mainLayout = new JSRowLayout();
			
			titleLabel = new JSLabel("Sujet: ");
			titleLabel.setPixelWidth(80);
			
			title = new JSTextField();
			title.setPixelWidth(280);
			
			description = new JSTextPane();
			description.setPixelSize(360, 180);
			
			confirmBt = new JSButton("Envoyer");
			confirmBt.setPixelWidth(180);
			confirmBt.addClickListener(this);
			
			cancelBt = new JSButton("Annuler");
			cancelBt.setPixelWidth(180);
			cancelBt.addClickListener(this);
			
			mainLayout.addComponent(titleLabel);
			mainLayout.addComponent(title);
			mainLayout.addRow();
			mainLayout.addComponent(description);
			mainLayout.addRow();
			mainLayout.addComponent(confirmBt);
			mainLayout.addComponent(cancelBt);
			
			setComponent(mainLayout);
			centerOnScreen();
		}
		else {
			this.remove(mainLayout);
			titleLabel = null;
			title = null;
			description = null;
			confirmBt = null;
			cancelBt = null;
			mainLayout = null;
			this.removeFromParent();
		}
		
		super.setVisible(visible);
	}
	
	public void onClick(Widget sender) {
		if(sender == confirmBt) {
			sendBugReport();
		} else if(sender == cancelBt) {
			setVisible(false);
		}
		
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
	private void sendBugReport() {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("subject", title.getText());
		params.put("content", description.getHTML());
		new Action("sendbugreport", params, new ActionCallback() {
			public void onSuccess(AnswerData data) {
				JSOptionPane.showMessageDialog("Merci d'avoir signalé un bug!",
						"Signaler un bug", JSOptionPane.OK_OPTION,
						JSOptionPane.INFORMATION_MESSAGE ,new OptionPaneListener() {
							public void optionSelected(Object option) {
								setVisible(false);
							}
						});
			}
	
			public void onFailure(String error) {
				ActionCallbackAdapter.onFailureDefaultBehavior(error);
				
			}
		});
	}
}
