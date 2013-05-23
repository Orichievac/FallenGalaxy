/*
Copyright 2010 Romain Prevot

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
 * TODO ALTER TABLE `fg_player` CHANGE `avatar` `avatar` VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL
 */
package fr.fg.client.core;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.data.AnswerData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSScrollPane;

/**
 * @author Ghost
 *
 */
public class CodexDialog extends JSDialog {
	// ------------------------------------------------------- CONSTANTES -- //
	public static final String 
		INFO_DOODADS 	= "infos.doodads",
		INFO_PLAYER 	= "infos.player",
		
		TITLE_ERROR 	= "Erreur!";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	private JSRowLayout content;
	
	private DockPanel mainPan;
	
	private JSLabel title;
	private FlowPanel description;
	private Image avatar;
	
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	public CodexDialog() {
		super("Codex", false, true, true);
		
		setStylePrimaryName("codex");
		getElement().setId("codexDialog");
	
		
		//initialisations
		content = new JSRowLayout();
		mainPan = new DockPanel();
		title = new JSLabel();
		title.setStyleName("codex-title");
		description = new FlowPanel();
		description.setStyleName("codex-description");
		avatar = new Image();
		avatar.setStyleName("codex-avatar");
		
		//constructions
		JSScrollPane scroller = new JSScrollPane();
		scroller.setView(description);
	    scroller.setSize("500px", "300px");
	    
		mainPan.add(title, DockPanel.NORTH);
		mainPan.add(scroller, DockPanel.CENTER);
		mainPan.add(avatar, DockPanel.WEST);

		setComponent(mainPan);
		centerOnScreen();
		
		setVisible(false);
		
	}
	// --------------------------------------------------------- METHODES -- //
	
	public void show(String type){
		show(type, null);
	}
	
	public void show(String type, String login){
		clear();
		StaticMessages messages = 
			(StaticMessages) GWT.create(StaticMessages.class);
		setVisible(true);
		
		//on test en fonction du type de l'objet
		if(type.equals(INFO_DOODADS)){
			//et on récupère le titre et la description qui vont bien
			//title.setText(messages.doodadsInfoTitle());
			//description.setText(messages.doodadsInfoDescription());
		} else if(type.equals(INFO_PLAYER)) {
			buildPlayerCard(login);
		}
	}
	
	/**
	 * Efface le contenu du codex
	 */
	public void clear(){
		setInfoTitle("");
		setInfoDescription("");
		setTitle("Codex");
	}
	
	/**
	 * Définit la description
	 * @param desc
	 */
	public void setInfoDescription(String desc){
		int iMax = description.getWidgetCount();
		for(int i = 0; i < iMax ; i++)
			description.remove(i);
		
		description.add(new HTML(desc));
	}
	
	/**
	 * Définit le titre
	 * @param title
	 */
	public void setInfoTitle(String title){
		this.title.setText(title);
	}
	
	public void setInfoImage(String url){
		if(url == null)
			return;
		this.avatar.setUrl(url);
	}
		
	// ------------------------------------------------- METHODES PRIVEES -- //
	/**
	 * Remplis les champs pour afficher la carte du joueur dont le login est passé en paramètre
	 * @param login
	 */
	private void buildPlayerCard(final String login){
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("login", login);
		
		//Création de la carte avec les infos en db
		new Action("getPlayerCard", params, new ActionCallback() {
			public void onSuccess(AnswerData data) {
				if(data != null){
					//TODO à optimiser
					String txt = data.getPlayerCardData().getDescription();
					String avatarUrl = data.getPlayerCardData().getAvatarUrl();
					
					setInfoTitle(login);
					setInfoDescription(txt);
					setInfoImage(avatarUrl);
				} else {
					onFailure("Infos non trouvées");
				}
			}
			public void onFailure(String error) {
				setInfoDescription(error);
				setInfoTitle(TITLE_ERROR);
			}
		});	
	}
}
