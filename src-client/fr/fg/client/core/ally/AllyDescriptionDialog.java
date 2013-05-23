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

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.data.AnswerData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextPane;

public class AllyDescriptionDialog extends JSDialog implements ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long lastUpdate;
	
	private JSTextPane descriptionPane;
	
	private JSButton okBt, cancelBt;
	
	private Action currentAction;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AllyDescriptionDialog(String currentDescription, long lastUpdate) {
		super("Modifier description", true, true, true);
		
		this.lastUpdate = lastUpdate;
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		descriptionPane = new JSTextPane();
		descriptionPane.setHTML(currentDescription);
		descriptionPane.setPixelSize(350, 250);
		
		okBt = new JSButton(messages.ok());
		okBt.setPixelWidth(100);
		okBt.addClickListener(this);
		
		cancelBt = new JSButton(messages.cancel());
		cancelBt.setPixelWidth(100);
		cancelBt.addClickListener(this);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(descriptionPane);
		layout.addRow();
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(okBt);
		layout.addComponent(cancelBt);
		
		setComponent(layout);
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void onClick(Widget sender) {
		if (sender == okBt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("description", descriptionPane.getHTML());
			params.put("update", String.valueOf(lastUpdate));
			
			currentAction = new Action("allies/setdescription", params, this);
		} else if (sender == cancelBt) {
			setVisible(false);
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		setVisible(false);
		UpdateManager.UPDATE_CALLBACK.onSuccess(data);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (!visible) {
			currentAction = null;
			okBt.removeClickListener(this);
			cancelBt.removeClickListener(this);
			okBt = null;
			cancelBt = null;
			descriptionPane = null;
		}
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
