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
import com.google.gwt.user.client.Event;
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
import fr.fg.client.openjwt.ui.JSComponent;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSPasswordField;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSScrollPane;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.OptionPaneListener;

public class RegisterDialog extends JSDialog implements ClickListener,
		ActionCallback, OptionPaneListener, DialogListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSButton okBt, cancelBt, licenseBt;
	
	private JSTextField loginField, emailField, birthdayField, sponsorField;
	
	private JSPasswordField passwordField, confirmPasswordField;
	
	private JSLabel licenseLabel;
	
	private Action currentAction;
	
	private LoginDialog loginDialog;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public RegisterDialog(LoginDialog loginDialog) {
		this(loginDialog, "");
	}
	
	public RegisterDialog(LoginDialog loginDialog, String sponsor) {
		super("Inscription", true, true, true);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		this.loginDialog = loginDialog;
		
		// Login
		JSLabel loginLabel = new JSLabel("&nbsp;" + messages.login());
		loginLabel.setPixelWidth(130);
		
		loginField = new JSTextField();
		loginField.setPixelWidth(210);
		loginField.setToolTipText("<div class=\"justify\">" +
				messages.loginToolTip() + "</div>", 160);
		loginField.setMaxLength(20);
		
		// Mot de passe
		JSLabel passwordLabel = new JSLabel("&nbsp;" + messages.password());
		passwordLabel.setPixelWidth(130);
		
		passwordField = new JSPasswordField();
		passwordField.setPixelWidth(210);
		passwordField.setToolTipText("<div class=\"justify\">" +
				messages.passwordToolTip() + "</div>", 160);
		passwordField.setMaxLength(50);
		
		// Confirmation du mot de passe
		JSLabel confirmPasswordLabel = new JSLabel(
				"&nbsp;" + messages.confirmPassword());
		confirmPasswordLabel.setPixelWidth(130);
		
		confirmPasswordField = new JSPasswordField();
		confirmPasswordField.setPixelWidth(210);
		confirmPasswordField.setMaxLength(50);
		
		// E-mail
		JSLabel emailLabel = new JSLabel("&nbsp;" + messages.email());
		emailLabel.setPixelWidth(130);
		
		emailField = new JSTextField();
		emailField.setPixelWidth(210);
		emailField.setToolTipText("<div class=\"justify\">" +
				messages.emailToolTip() + "</div>", 160);
		
		// Age
		JSLabel birthdayLabel = new JSLabel("&nbsp;" + messages.birthday());
		birthdayLabel.setPixelWidth(130);
		
		birthdayField = new JSTextField(messages.birthdayFormat());
		birthdayField.setPixelWidth(210);
		birthdayField.addStyleName("textFieldHelp");
		birthdayField.setToolTipText("<div class=\"justify\">" +
				messages.birthdayToolTip() + "</div>", 160);
		birthdayField.setMaxLength(10);
		
		JSLabel sponsorLabel = new JSLabel("&nbsp;" + "Parrain" );
		sponsorLabel.setPixelWidth(130);
		
		sponsorField = new JSTextField();
		sponsorField.setPixelWidth(210);
		sponsorField.setText(sponsor);
		sponsorField.setToolTipText("<div class=\"justify\">" +
				"Entrez le pseudo du joueur qui vous a fait découvrir le jeu, le cas échéant." + "</div>", 160);
		
		// Licence
		licenseBt = new JSButton();
		licenseBt.setPixelWidth(JSComponent.getUIPropertyInt(
			JSButton.UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT));
		licenseBt.addClickListener(this);
		licenseBt.addStyleName("checkBox");
		
		licenseLabel = new JSLabel("&nbsp;" + messages.agreeTos(
				"<a>" + messages.termsOfService().toLowerCase() + "</a>"));
		licenseLabel.setPixelWidth(340 - licenseBt.getPixelWidth() - 10);
		
		// Boutons ok & annuler
		okBt = new JSButton(messages.ok());
		okBt.setPixelWidth(100);
		okBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(loginLabel);
		layout.addComponent(loginField);
		layout.addRow();
		layout.addComponent(passwordLabel);
		layout.addComponent(passwordField);
		layout.addRow();
		layout.addComponent(confirmPasswordLabel);
		layout.addComponent(confirmPasswordField);
		layout.addRow();
		layout.addComponent(emailLabel);
		layout.addComponent(emailField);
		layout.addRow();
		layout.addComponent(birthdayLabel);
		layout.addComponent(birthdayField);
		layout.addRow();
		layout.addComponent(sponsorLabel);
		layout.addComponent(sponsorField);
		layout.addRowSeparator(10);
		layout.addComponent(licenseBt);
		layout.addComponent(licenseLabel);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addRowSeparator(10);
		layout.addComponent(okBt);
		layout.addComponent(cancelBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		
		setComponent(layout);
		centerOnScreen();
		addDialogListener(this);
		
		sinkEvents(Event.ONCLICK | Event.ONKEYDOWN);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		switch (event.getTypeInt()) {
		case Event.ONCLICK:
			if (licenseLabel.getElement().isOrHasChild(event.getTarget()) &&
					event.getTarget().getNodeName().equalsIgnoreCase("a")) {
				final JSDialog licenseDialog = new JSDialog(
						messages.termsOfService(), true, true, true);
				
				HTMLPanel licensePanel = new HTMLPanel(
					"<div class=\"justify\">" + messages.license() + "</div>");
				
				JSScrollPane scrollPane = new JSScrollPane();
				scrollPane.setView(licensePanel);
				scrollPane.setPixelSize(400, 300);
				
				JSButton closeBt = new JSButton(messages.close());
				closeBt.setPixelWidth(100);
				closeBt.addClickListener(new ClickListener() {
					public void onClick(Widget sender) {
						licenseDialog.setVisible(false);
					}
				});
				
				JSRowLayout layout = new JSRowLayout();
				layout.addComponent(scrollPane);
				layout.addRow();
				layout.addComponent(closeBt);
				layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
				
				licenseDialog.setComponent(layout);
				licenseDialog.centerOnScreen();
				licenseDialog.setDefaultCloseOperation(DESTROY_ON_CLOSE);
				licenseDialog.setVisible(true);
			}
			
			if (birthdayField.getElement().isOrHasChild(event.getTarget())) {
				if (birthdayField.getText().equals(messages.birthdayFormat())) {
					birthdayField.setText("");
					birthdayField.removeStyleName("textFieldHelp");
				}
			}
			break;
		case Event.ONKEYDOWN:
			if (emailField.getElement().isOrHasChild(event.getTarget())) {
				if (birthdayField.getText().equals(messages.birthdayFormat()) &&
						event.getKeyCode() == 9) {
					birthdayField.setText("");
					birthdayField.removeStyleName("textFieldHelp");
				}
			}
			break;
		}
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible) {
			StaticMessages messages =
				(StaticMessages) GWT.create(StaticMessages.class);
			
			loginField.setText("");
			passwordField.setText("");
			confirmPasswordField.setText("");
			emailField.setText("");
			birthdayField.setText(messages.birthdayFormat());
			birthdayField.addStyleName("textFieldHelp");
		} else {
			loginField.setFocus(true);
		}
	}
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			StaticMessages messages =
				(StaticMessages) GWT.create(StaticMessages.class);
			
			if (!passwordField.getText().equals(confirmPasswordField.getText())) {
				JSOptionPane.showMessageDialog(messages.passwordNoMatch(),
						messages.error(), JSOptionPane.OK_OPTION,
						JSOptionPane.ERROR_MESSAGE, null);
				return;
			}
			
			if (!licenseBt.getStyleName().contains("checked")) {
				JSOptionPane.showMessageDialog(messages.checkTermsOfService(),
						messages.error(), JSOptionPane.OK_OPTION,
						JSOptionPane.ERROR_MESSAGE, null);
				return;
			}
			
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("login", loginField.getText());
			params.put("password", passwordField.getText());
			params.put("email", emailField.getText());
			params.put("sponsor", sponsorField.getText());
			params.put("birthday", birthdayField.getText().equals(
					messages.birthdayFormat()) ? "" : birthdayField.getText());
			currentAction = new Action("register", params, this);
		} else if (sender == cancelBt) {
			removeDialogListener(this);
			setVisible(false);
			currentAction = null;
			loginDialog.setVisible(true);
			loginDialog = null;
		} else if (sender == licenseBt) {
			if (licenseBt.getStyleName().contains("checked"))
				licenseBt.removeStyleName("checked");
			else
				licenseBt.addStyleName("checked");
		}
	}

	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		// Inscription réussie
		removeDialogListener(this);
		setVisible(false);

		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		JSOptionPane.showMessageDialog(messages.emailSent(),
				messages.register(), JSOptionPane.OK_OPTION,
				JSOptionPane.INFORMATION_MESSAGE, this);
	}
	
	public void optionSelected(Object option) {
		// Retour à la fenêtre de login
		setVisible(false);
		currentAction = null;
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
