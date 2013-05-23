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
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.JSTextPane;

public class WriteMessageDialog extends JSDialog implements ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		TYPE_MESSAGE = 1 << 0,
		TYPE_NEWS = 1 << 1;
	
	public final static int
		OPTION_RECEIVER = 1 << 0,
		OPTION_TITLE = 1 << 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type, parent;
	
	private long lastUpdate;
	
	private JSButton sendBt, cancelBt;
	
	private JSTextField receiverField, titleField;
	
	private JSTextPane contentPane;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public WriteMessageDialog(int type, long lastUpdate, int options) {
		this(type, lastUpdate, options, "", "", 0);
	}

	public WriteMessageDialog(int type, long lastUpdate, int options,
			String title, int parent) {
		this(type, lastUpdate, options, "", title, parent);
	}
	
	public WriteMessageDialog(int type, long lastUpdate, int options,
			String receiver, String title) {
		this(type, lastUpdate, options, receiver, title, 0);
	}
	
	public WriteMessageDialog(int type, long lastUpdate, int options,
			String receiver, String title, String content) {
		this(type, lastUpdate, options, receiver, title, content, 0);
	}

	private WriteMessageDialog(int type, long lastUpdate, int options,
			String receiver, String title, int parent) {
		this(type, lastUpdate, options, receiver, title, "", parent);
	}
	
	private WriteMessageDialog(int type, long lastUpdate, int options,
			String receiver, String title, String content, int parent) {
		super("Nouveau message", true, true, true);
		
		this.type = type;
		this.lastUpdate = lastUpdate;
		this.parent = parent;
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		// Ecriture d'un message
		JSLabel receiverLabel = new JSLabel("&nbsp;" + messages.receiver());
		receiverLabel.setPixelWidth(100);
		
		receiverField = new JSTextField(receiver);
		receiverField.setPixelWidth(250);
		receiverField.setMaxLength(20);
		
		JSLabel titleLabel = new JSLabel("&nbsp;" + messages.title());
		titleLabel.setPixelWidth(100);
		
		titleField = new JSTextField(title);
		titleField.setPixelWidth(250);
		titleField.setMaxLength(40);
		
		contentPane = new JSTextPane();
		contentPane.setPixelSize(350, 250);
		
		if (content.length() > 0)
			contentPane.setHTML(content);
		
		sendBt = new JSButton(messages.send());
		sendBt.setPixelWidth(100);
		sendBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		// Mise en forme des composants
		JSRowLayout layout = new JSRowLayout();
		if ((options & OPTION_RECEIVER) != 0) {
			layout.addComponent(receiverLabel);
			layout.addComponent(receiverField);
			layout.addRow();
		}
		if ((options & OPTION_TITLE) != 0) {
			layout.addComponent(titleLabel);
			layout.addComponent(titleField);
			layout.addRowSeparator(10);
		}
		layout.addComponent(contentPane);
		layout.addRowSeparator(5);
		layout.addComponent(sendBt);
		layout.addComponent(cancelBt);
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
		setComponent(layout);
		centerOnScreen();
	}

	// --------------------------------------------------------- METHODES -- //
	
	public void onClick(Widget sender) {
		if (sender == sendBt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			switch (type) {
			case TYPE_MESSAGE:
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("player", receiverField.getText());
				params.put("title", titleField.getText());
				//params.put("update", String.valueOf(lastUpdate));
				params.put("update", String.valueOf(0));//Permet d'obtenir toutes les news
				params.put("content", contentPane.getHTML());
				
				currentAction = new Action("messages/add", params, this);
				break;
			case TYPE_NEWS:
				params = new HashMap<String, String>();
				params.put("title", titleField.getText());
				params.put("content", contentPane.getHTML());
				//params.put("update", String.valueOf(lastUpdate));
				params.put("update", String.valueOf(0)); 
				params.put("parent", String.valueOf(parent));
				
				currentAction = new Action("allies/addnews", params, this);
				break;
			}
		} else if (sender == cancelBt) {
			currentAction = null;
			setVisible(false);
		}
	}

	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		currentAction = null;
		setVisible(false);
		
		UpdateManager.UPDATE_CALLBACK.onSuccess(data);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
