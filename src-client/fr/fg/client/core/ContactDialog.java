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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.ContactData;
import fr.fg.client.data.ContactsData;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSList;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.SelectionListener;

public class ContactDialog extends JSDialog implements SelectionListener,
		KeyboardListener, ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		VIEW_FRIENDS = 0,
		VIEW_REQUESTS = 1,
		VIEW_IGNORE = 2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSList contactsList;
	
	private JSTextField nameField;
	
	private JSButton actionBt;
	
	private Action currentAction;
	
	private ArrayList<ContactUI> friendsList, requestsList, ignoreList;
	
	private JSTabbedPane tabbedPane;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ContactDialog() {
		super("Contacts", false, true, true);
		
		this.friendsList = new ArrayList<ContactUI>();
		this.requestsList = new ArrayList<ContactUI>();
		this.ignoreList = new ArrayList<ContactUI>();
		
		tabbedPane = new JSTabbedPane();
		tabbedPane.addTab("Amis");
		tabbedPane.addTab("Demandes");
		tabbedPane.addTab("Ignorés");
		tabbedPane.setPixelWidth(220);
		tabbedPane.addSelectionListener(this);
		
		contactsList = new JSList();
		contactsList.setPixelSize(220, 250);
		contactsList.addSelectionListener(this);
		
		nameField = new JSTextField();
		nameField.setPixelWidth(189);
		nameField.addKeyboardListener(this);
		
		actionBt = new JSButton();
		actionBt.setPixelWidth(31);
		actionBt.addStyleName("iconAdd");
		actionBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(tabbedPane);
		layout.addRowSeparator(3);
		layout.addComponent(contactsList);
		layout.addRow();
		layout.addComponent(nameField);
		layout.addComponent(actionBt);
		
		setComponent(layout);
		centerOnScreen();
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			currentAction = new Action("contact/get", Action.NO_PARAMETERS, this);
			Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_CONTACT);
		}
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (sender == contactsList) {
			if (newValue != -1) {
				nameField.setText(((ContactUI)
						contactsList.getSelectedItem()).data.getName());
				switch (tabbedPane.getSelectedIndex()) {
				case VIEW_FRIENDS:
				case VIEW_IGNORE:
					actionBt.removeStyleName("iconAdd");
					actionBt.addStyleName("iconDelete");
					break;
				case VIEW_REQUESTS:
					actionBt.addStyleName("iconAdd");
					actionBt.removeStyleName("iconDelete");
					break;
				}
			} else {
				actionBt.addStyleName("iconAdd");
				actionBt.removeStyleName("iconDelete");
			}
		} else if (sender == tabbedPane) {
			switch (newValue) {
			case VIEW_FRIENDS:
				contactsList.setItems(friendsList);
				nameField.setEditable(true);
				break;
			case VIEW_REQUESTS:
				contactsList.setItems(requestsList);
				nameField.setEditable(false);
				break;
			case VIEW_IGNORE:
				contactsList.setItems(ignoreList);
				nameField.setEditable(true);
				break;
			}
			nameField.setText("");
		}
	}

	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		// Non utilisé
	}

	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		// Non utilisé
	}

	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		switch (keyCode) {
		case 10:
		case 13:
			doAction();
			break;
		default:
			updateSelectedName();
			break;
		}
	}

	public void onClick(Widget sender) {
		if (sender == actionBt) {
			doAction();
		}
	}

	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		ContactsData contacts = data.getContacts();
		
		ArrayList<ContactUI> friendsList = new ArrayList<ContactUI>();
		ArrayList<ContactUI> requestsList = new ArrayList<ContactUI>();
		ArrayList<ContactUI> ignoreList = new ArrayList<ContactUI>();
		
		for (int i = 0; i < contacts.getContactsCount(); i++) {
			ContactData contact = contacts.getContactAt(i);
			
			if (contact.getType().equals("friend"))
				friendsList.add(new ContactUI(contact));
			else if (contact.getType().equals("request"))
				requestsList.add(new ContactUI(contact));
			else
				ignoreList.add(new ContactUI(contact));
		}
		
		Collections.sort(friendsList, new Comparator<ContactUI>() {
			public int compare(ContactUI c1, ContactUI c2) {
				if (c1.data.isConnected() && !c2.data.isConnected())
					return -1;
				if (!c1.data.isConnected() && c2.data.isConnected())
					return 1;
				return c1.data.getName().compareToIgnoreCase(c2.data.getName());
			}
		});
		Collections.sort(requestsList, new Comparator<ContactUI>() {
			public int compare(ContactUI c1, ContactUI c2) {
				if (c1.data.isConnected() && !c2.data.isConnected())
					return -1;
				if (!c1.data.isConnected() && c2.data.isConnected())
					return 1;
				return c1.data.getName().compareToIgnoreCase(c2.data.getName());
			}
		});
		Collections.sort(ignoreList, new Comparator<ContactUI>() {
			public int compare(ContactUI c1, ContactUI c2) {
				if (c1.data.isConnected() && !c2.data.isConnected())
					return -1;
				if (!c1.data.isConnected() && c2.data.isConnected())
					return 1;
				return c1.data.getName().compareToIgnoreCase(c2.data.getName());
			}
		});
		
		this.friendsList = friendsList;
		this.requestsList = requestsList;
		this.ignoreList = ignoreList;
		
		String name = nameField.getText();
		
		switch (tabbedPane.getSelectedIndex()) {
		case VIEW_FRIENDS:
			contactsList.setItems(friendsList);
			break;
		case VIEW_REQUESTS:
			contactsList.setItems(requestsList);
			break;
		case VIEW_IGNORE:
			contactsList.setItems(ignoreList);
			break;
		}
		
		nameField.setText(name);
		updateSelectedName();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void doAction() {
		String type = "";
		
		switch (tabbedPane.getSelectedIndex()) {
		case VIEW_FRIENDS:
		case VIEW_REQUESTS:
			type = "friend";
			break;
		case VIEW_IGNORE:
			type = "ignore";
			break;
		}
		
		if (actionBt.getStyleName().contains("iconAdd")) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("player", nameField.getText());
			params.put("type", type);
			
			nameField.setText("");
			new Action("contact/add", params, this);
		} else {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("player", nameField.getText());
			params.put("type", type);

			nameField.setText("");
			new Action("contact/delete", params, this);
		}
	}
	
	private void updateSelectedName() {
		String name = nameField.getText();
		
		switch (tabbedPane.getSelectedIndex()) {
		case VIEW_FRIENDS:
			for (int i = 0; i < friendsList.size(); i++) {
				ContactUI contact = friendsList.get(i);
				
				if (contact.data.getName().equalsIgnoreCase(name)) {
					contactsList.setSelectedIndex(i);
					actionBt.removeStyleName("iconAdd");
					actionBt.addStyleName("iconDelete");
					return;
				}
			}
			break;
		case VIEW_REQUESTS:
			for (int i = 0; i < requestsList.size(); i++) {
				ContactUI contact = requestsList.get(i);
				
				if (contact.data.getName().equalsIgnoreCase(name)) {
					contactsList.setSelectedIndex(i);
					actionBt.removeStyleName("iconAdd");
					actionBt.addStyleName("iconDelete");
					return;
				}
			}
			break;
		case VIEW_IGNORE:
			for (int i = 0; i < ignoreList.size(); i++) {
				ContactUI contact = ignoreList.get(i);
				
				if (contact.data.getName().equalsIgnoreCase(name)) {
					contactsList.setSelectedIndex(i);
					actionBt.removeStyleName("iconAdd");
					actionBt.addStyleName("iconDelete");
					return;
				}
			}
			break;
		}
		
		actionBt.addStyleName("iconAdd");
		actionBt.removeStyleName("iconDelete");
		contactsList.setSelectedIndex(-1);
	}
	
	private class ContactUI {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private ContactData data;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public ContactUI(ContactData data) {
			this.data = data;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public String toString() {
			String color;
			if (!data.getType().equals("ignore")) {
				if (data.isConnected())
					color = "#00c000";
				else
					color = "#808080";
			} else {
				color = "red";
			}
			
			return "<div style=\"padding: 2px; color: " + color + ";\">" +
				(data.hasAllyTag() ? "[" + data.getAllyTag() + "] " : "") +
				data.getName() + (data.isMutual() ? " (ami mutuel)" : "") + "</div>";
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
