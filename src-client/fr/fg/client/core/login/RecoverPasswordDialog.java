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

package fr.fg.client.core.login;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.ui.DialogListener;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.OptionPaneListener;

public class RecoverPasswordDialog extends JSDialog implements ClickListener,
		ActionCallback, OptionPaneListener, DialogListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSButton okBt, cancelBt;
	
	private JSTextField emailField;
	
	private Action currentAction;
	
	private LoginDialog loginDialog;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public RecoverPasswordDialog(LoginDialog loginDialog) {
		super(((StaticMessages) GWT.create(
				StaticMessages.class)).recoverPassword(), true, true, true);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		this.loginDialog = loginDialog;
		
		HTMLPanel recoverLabel = new HTMLPanel("<div unselectable=\"on\" " +
			"class=\"justify\" style=\"padding: 4px;\">" +
			messages.recoverPasswordHelp() + "</div>");
		recoverLabel.setPixelSize(292, 50);
		OpenJWT.setElementFloat(recoverLabel.getElement(), "left");
		
		// Champ email
		JSLabel emailLabel = new JSLabel("&nbsp;" + messages.email());
		emailLabel.setPixelWidth(100);
		
		emailField = new JSTextField();
		emailField.setPixelWidth(200);
		
		// Boutons OK / Annuler
		okBt = new JSButton(messages.ok());
		okBt.setPixelWidth(100);
		okBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(recoverLabel);
		layout.addRow();
		layout.addComponent(emailLabel);
		layout.addComponent(emailField);
		layout.addRowSeparator(5);
		layout.addComponent(okBt);
		layout.addComponent(cancelBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		
		setComponent(layout);
		centerOnScreen();
		addDialogListener(this);
	}

	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible)
			emailField.setFocus(true);
		else
			emailField.setFocus(false);
	}
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("email", emailField.getText());
			
			currentAction = new Action("recoverpassword", params, this);
		} else if (sender == cancelBt) {
			removeDialogListener(this);
			setVisible(false);
			currentAction = null;
			loginDialog.setVisible(true);
			loginDialog = null;
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		currentAction = null;
		
		JSOptionPane.showMessageDialog(messages.recoverEmailSent(),
				messages.recoverPassword(), JSOptionPane.OK_OPTION,
				JSOptionPane.INFORMATION_MESSAGE, this);
	}
	
	public void optionSelected(Object option) {
		loginDialog.setVisible(true);
		loginDialog = null;
	}
	
	public void dialogClosed(Widget sender) {
		// Retour à la fenêtre de login
		removeDialogListener(this);
		setVisible(false);
		currentAction = null;
		loginDialog.setVisible(true);
		loginDialog = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
