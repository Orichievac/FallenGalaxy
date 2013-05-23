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

package fr.fg.client.core.settings;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSPasswordField;
import fr.fg.client.openjwt.ui.JSRowLayout;

public class PasswordDialog extends JSDialog implements ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSPasswordField passwordField, newPasswordField,
		confirmPasswordField;
	
	private JSButton okBt, cancelBt;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public PasswordDialog() {
		super("Mot de passe", true, true, true);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		// Mot de passe
		JSLabel passwordLabel = new JSLabel("&nbsp;Mot de passe");
		passwordLabel.setPixelWidth(100);
		
		passwordField = new JSPasswordField();
		passwordField.setPixelWidth(160);
		
		// Nouveau mot de passe
		JSLabel newPasswordLabel = new JSLabel("&nbsp;Nouveau");
		newPasswordLabel.setPixelWidth(100);
		
		newPasswordField = new JSPasswordField();
		newPasswordField.setPixelWidth(160);

		// Confirmation nouveau mot de passe
		JSLabel confirmPasswordLabel = new JSLabel("&nbsp;Confirmez");
		confirmPasswordLabel.setPixelWidth(100);
		
		confirmPasswordField = new JSPasswordField();
		confirmPasswordField.setPixelWidth(160);
		
		// Boutons OK / Annuler
		okBt = new JSButton(messages.ok());
		okBt.setPixelWidth(100);
		okBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(passwordLabel);
		layout.addComponent(passwordField);
		layout.addRow();
		layout.addComponent(newPasswordLabel);
		layout.addComponent(newPasswordField);
		layout.addRow();
		layout.addComponent(confirmPasswordLabel);
		layout.addComponent(confirmPasswordField);
		layout.addRowSeparator(5);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(okBt);
		layout.addComponent(cancelBt);
		
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
		setComponent(layout);
		centerOnScreen();
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			String newPassword = newPasswordField.getText();
			String confirmPassword = confirmPasswordField.getText();
			
			if (!newPassword.equals(confirmPassword)) {
				JSOptionPane.showMessageDialog(
					"Le nouveau mot de passe ne correspond pas dans les deux champs.",
					"Mot de passe invalide", JSOptionPane.OK_OPTION,
					JSOptionPane.ERROR_MESSAGE, null);
				return;
			}
			
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("password", passwordField.getText());
			params.put("newpassword", newPassword);
			
			currentAction = new Action("setpassword", params, this);
		} else if (sender == cancelBt) {
			setVisible(false);
			currentAction = null;
		}
	}

	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		setVisible(false);
		currentAction = null;
		JSOptionPane.showMessageDialog(
			"Votre mot de passe a été modifié.",
			"Changement mot de passe", JSOptionPane.OK_OPTION,
			JSOptionPane.INFORMATION_MESSAGE, null);

	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
