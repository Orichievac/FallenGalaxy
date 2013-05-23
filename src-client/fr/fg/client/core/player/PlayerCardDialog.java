/*
Copyright 2010 Ghost

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

/**
 * 
 */
package fr.fg.client.core.player;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.PlayerCardData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextPane;

/**
 * @author Ghost
 *
 */
public class PlayerCardDialog extends JSDialog implements ClickListener, ActionCallback {
	
	private JSTextPane txtDescription;
	
	private JSButton btOk, btCancel;
	
	private Action currentAction;
	

	public PlayerCardDialog() {
		super("Modifier votre carte", true, true, true);

		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		txtDescription = new JSTextPane();
		txtDescription.setPixelSize(350, 250);
		
		btOk = new JSButton(messages.ok());
		btOk.setPixelWidth(100);
		btOk.addClickListener(this);
		
		btCancel = new JSButton(messages.cancel());
		btCancel.setPixelWidth(100);
		btCancel.addClickListener(this);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("login", Settings.getPlayerLogin());
		
		//Création de la carte avec les infos en db
		currentAction = new Action("getPlayerCard", params, new ActionCallback() {
			public void onSuccess(AnswerData data) {
				String txt = "";
				if(data != null)
					txt = data.getPlayerCardData().getDescription();
				
				txtDescription.setHTML(txt);
				currentAction = null;
			}
			public void onFailure(String error) {
				txtDescription.setHTML("*Impossible de récupérer les informations*");
				currentAction = null;
			}
		});	
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(txtDescription);
		layout.addRow();
		layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		layout.addComponent(btOk);
		layout.addComponent(btCancel);
		
		setComponent(layout);
		setDefaultCloseOperation(DESTROY_ON_CLOSE);
		centerOnScreen();
		setVisible(true);
		
	}
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //



	public void onClick(Widget sender) {
		if (sender == btOk) {
			if (currentAction != null && currentAction.isPending())
				return;
							
			HashMap<String, String> params = new HashMap<String, String>();
			
			params.put("id", String.valueOf(Settings.getPlayerId()));
			params.put("description", txtDescription.getHTML());
			
			try{	
				currentAction = new Action("setPlayerCard", params, this);
			} catch (Exception e) {
				//LoggingSystem.getServerLogger().error("setPlayer fails:" + params.toString());
			}
		} else if (sender == btCancel) {
			setVisible(false);
		}
		
	}

	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		setVisible(false);
	}
}
