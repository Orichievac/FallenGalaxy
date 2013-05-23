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
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AllyDescriptionData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.LadderAllyData;
import fr.fg.client.data.LadderData;
import fr.fg.client.data.LadderPlayerData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSList;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSScrollPane;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.SelectionListener;

public class Ladder extends JSDialog implements ActionCallback,
		SelectionListener, KeyboardListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int 
		LADDER_PLAYERS = 0,
		LADDER_ALLIES 	= 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int currentLadder;
	
	private int currentRange;
	
	private boolean hasCurrentPlayerData, hasCurrentAllyData;
	
	private LadderPlayerData currentPlayerData;
	
	private LadderAllyData currentAllyData;
	
	private LadderData[] playersLadderCache, alliesLadderCache;
	
	private long[] playersCacheDownloadTimes, alliesCacheDownloadTimes;
	
	private JSTabbedPane ladderPane;
	
	private JSTextField searchField;
	
	private JSList ladder;
	
	private JSComboBox ranksComboBox;
	
	private JSLabel header;
	
	private JSLabel selfPanel;
	
	private boolean updatingLadder;
	
	private Action downloadAction;
	
	private DescriptionDialog descriptionDialog;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Ladder() {
		super("<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"iconLadder\"/> " +
				((StaticMessages) GWT.create(StaticMessages.class)).ladder(),
				false, true, true);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		// Classement et échelle visualisé
		currentLadder = LADDER_PLAYERS;
		currentRange = 0;
		descriptionDialog = new DescriptionDialog();
		
		// Classement des joueurs et des alliances
		// Aucune échelle de classement n'est chargée par défaut
		playersLadderCache = new LadderData[10];
		alliesLadderCache = new LadderData[2];
		playersCacheDownloadTimes = new long[10];
		alliesCacheDownloadTimes = new long[2];
		
		// Classement du joueur courant
		hasCurrentPlayerData = false;
		hasCurrentAllyData = false;
		currentPlayerData = null;
		currentAllyData = null;
		updatingLadder = false;
		
		// Onglets pour les classements
		ladderPane = new JSTabbedPane();
		ladderPane.addTab(messages.ladderPlayers());
		ladderPane.addTab(messages.ladderAllies());
		ladderPane.setPixelWidth(520);
		ladderPane.addSelectionListener(this);

		// Icone recherche
		JSLabel searchLabel = new JSLabel();
		searchLabel.addStyleName("iconSearch");
		searchLabel.setPixelWidth(31);
		
		// Champ pour rechercher un joueur / une alliance
		searchField = new JSTextField();
		searchField.setPixelWidth(169);
		searchField.addKeyboardListener(this);
		searchField.setToolTipText("Utilisez * pour remplacer n'importe quels caractères.", 200);
		
		// Choix des rangs
		JSLabel ranksLabel = new JSLabel("&nbsp;Échelle");
		ranksLabel.setPixelWidth(80);
		
		ArrayList<String> ranges = new ArrayList<String>();
		ranges.add("1 - 50"); //$NON-NLS-1$
		ranges.add("51 - 100"); //$NON-NLS-1$
		ranges.add("101 - 150"); //$NON-NLS-1$
		ranges.add("151 - 200"); //$NON-NLS-1$
		ranges.add("201 - 250"); //$NON-NLS-1$
		ranges.add("251 - 300"); //$NON-NLS-1$
		ranges.add("301 - 350"); //$NON-NLS-1$
		ranges.add("351 - 400"); //$NON-NLS-1$
		ranges.add("401 - 450"); //$NON-NLS-1$
		ranges.add("451 - 500"); //$NON-NLS-1$
		
		ranksComboBox = new JSComboBox();
		ranksComboBox.setPixelWidth(100);
		ranksComboBox.setItems(ranges);
		ranksComboBox.addSelectionListener(this);
		
		header = new JSLabel();
		header.setPixelSize(520, 22);
		
		// Classement du joueur et de son alliance
		selfPanel = new JSLabel();
		selfPanel.addStyleName("playerLadder"); //$NON-NLS-1$
		selfPanel.setPixelWidth(520);
		selfPanel.setPixelHeight(24);
		
		ladder = new JSList();
		ladder.setPixelSize(520, 300);
		ladder.addStyleName("ladder"); //$NON-NLS-1$
		ladder.addSelectionListener(this);
		
		// Description des tables de classement
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(ladderPane);
		layout.addRowSeparator(3);
		layout.addComponent(ranksLabel);
		layout.addComponent(ranksComboBox);
		layout.addComponent(JSRowLayout.createHorizontalSeparator(120));
		layout.addComponent(searchLabel);
		layout.addComponent(searchField);
		layout.addRowSeparator(6);
		layout.addComponent(header);
		layout.addRow();
		layout.addComponent(selfPanel);
		layout.addRow();
		layout.addComponent(ladder);
		
		setComponent(layout);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setVisible(boolean visible) {
		if (visible) {
			super.setVisible(true);
			
			// Lance en asynchrone le téléchargement du classement
			showLadder(currentLadder, currentRange);
		} else {
			super.setVisible(false);
			
			searchField.setFocus(false);
		}
	}
	
	public void showLadder(int ladder, int range) {
		// Vérifie que le cache n'a pas expiré
		if (playersLadderCache[0] != null && Utilities.getCurrentTime() -
				playersCacheDownloadTimes[0] > playersLadderCache[0].getLifespan()) {
			// Vide le cache
			playersLadderCache = new LadderData[10];
			alliesLadderCache = new LadderData[2];
			playersCacheDownloadTimes = new long[10];
			alliesCacheDownloadTimes = new long[2];
			
			hasCurrentPlayerData = false;
			hasCurrentAllyData = false;
			currentPlayerData = null;
			currentAllyData = null;
		}
		
		LadderData ladderCache;
		
		// Indique si l'on doit télécharger des données sur le joueur ou son
		// alliance en meme temps que le classsement
		boolean self;
		
		// Type de classement (players ou allies)
		switch (ladder) {
		case Ladder.LADDER_PLAYERS:
			ladderCache = playersLadderCache[range];
			self = !hasCurrentPlayerData;
			break;
		case Ladder.LADDER_ALLIES:
			ladderCache = alliesLadderCache[range];
			self = !hasCurrentAllyData;
			break;
		default:
			throw new IllegalArgumentException();
		}
		
		// Teste si le classement demandé a déjà été téléchargé
		if (ladderCache != null && !self) {
			displayLadder(ladderCache, true);
		} else {
			// Télécharge le classement s'il n'y pas de requête en cours
			if (downloadAction != null && downloadAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("ladder", ladder == Ladder.LADDER_PLAYERS ? "players" : "allies"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			params.put("range", String.valueOf(range)); //$NON-NLS-1$
			params.put("self", String.valueOf(self)); //$NON-NLS-1$
			
			downloadAction = new Action("ladder/get", params, this); //$NON-NLS-1$
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		displayLadder(data.getLadder(), false);
	}
	
	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		// Non utilisé
	}
	
	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		// Non utilisé
	}
	
	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		switch (keyCode) {
		case 3:
		case 13:
			if (searchField.getText().length() == 0) {
				showLadder(currentLadder, currentRange);
			} else {
				if (downloadAction != null && downloadAction.isPending())
					return;
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("ladder", currentLadder == Ladder.LADDER_PLAYERS ? "players" : "allies");
				params.put("search", searchField.getText());
				
				downloadAction = new Action("ladder/search", params, this);
			}
			break;
		}
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (sender == ranksComboBox) {
			searchField.setText("");
			if (!updatingLadder)
				showLadder(currentLadder, newValue);
		} else if (sender == ladder) {
			switch (currentLadder) {
			case LADDER_ALLIES:
				if (newValue != -1) {
					if (downloadAction != null && downloadAction.isPending())
						return;
					
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("name", ((LadderAllyUI) ladder.getSelectedItem()).data.getName());
					
					downloadAction = new Action("allies/getdescription", params,
							new ActionCallbackAdapter() {
						public void onSuccess(AnswerData data) {
							descriptionDialog.setVisible(true);
							descriptionDialog.setAllyDescription(data.getAllyDescription());
						}
					});
					break;
				}
			}
		} else if (sender == ladderPane) {
			searchField.setText("");
			switch (ladderPane.getSelectedIndex()) {
			case LADDER_PLAYERS:
				showLadder(LADDER_PLAYERS, 0);
				break;
			case LADDER_ALLIES:
				showLadder(LADDER_ALLIES, 0);
				break;
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void displayLadder(LadderData ladderData, boolean cached) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		updatingLadder = true;
		
		if (ladderData.isPlayerLadder()) {
			if (currentLadder != LADDER_PLAYERS) {
				ArrayList<String> ranges = new ArrayList<String>();
				ranges.add("1 - 50"); //$NON-NLS-1$
				ranges.add("51 - 100"); //$NON-NLS-1$
				ranges.add("101 - 150"); //$NON-NLS-1$
				ranges.add("151 - 200"); //$NON-NLS-1$
				ranges.add("201 - 250"); //$NON-NLS-1$
				ranges.add("251 - 300"); //$NON-NLS-1$
				ranges.add("301 - 350"); //$NON-NLS-1$
				ranges.add("351 - 400"); //$NON-NLS-1$
				ranges.add("401 - 450"); //$NON-NLS-1$
				ranges.add("451 - 500"); //$NON-NLS-1$
				
				ranksComboBox.setItems(ranges);
			}
			
			if (ladderData.getRange() != -1 &&
					ranksComboBox.getSelectedIndex() != ladderData.getRange())
				ranksComboBox.setSelectedIndex(ladderData.getRange());
			
			currentLadder = LADDER_PLAYERS;
			if (ladderData.getRange() != -1)
				currentRange = ladderData.getRange();
			
			ladderPane.setSelectedIndex(currentLadder);
			
			header.getElement().setInnerHTML(
				"<table unselectable=\"on\" cellspacing=\"0\"><tr unselectable=\"on\">" + //$NON-NLS-1$
				"<td unselectable=\"on\" style=\"width: 48px;\"></td>" + //$NON-NLS-1$
				"<td unselectable=\"on\" style=\"width: 150px;\"><b unselectable=\"on\">" + messages.ladderPlayer() + "</b></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<td unselectable=\"on\" style=\"width: 140px;\"><b unselectable=\"on\">" + messages.ladderAlly() + "</b></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<td unselectable=\"on\" class=\"center\" style=\"width: 30px;\"><b unselectable=\"on\">" + messages.ladderLevel() + "</b></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<td unselectable=\"on\" class=\"center\" style=\"width: 30px;\"><div class=\"medal goldCup\" style=\"margin-left: 7px;\"></div></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<td unselectable=\"on\" class=\"center\" style=\"width: 70px;\"><b unselectable=\"on\">" + messages.ladderPoints() + "</b></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"</tr></table>"); //$NON-NLS-1$
			
			// Enregistre les données dans le cache si ce n'est pas déjà fait
			if (ladderData.getRange() != -1 && !cached) {
				playersLadderCache[currentRange] = ladderData;
				playersCacheDownloadTimes[currentRange] = Utilities.getCurrentTime();
			}
			
			// Construit la liste des joueurs
			ArrayList<LadderPlayerUI> players = new ArrayList<LadderPlayerUI>();
			for (int i = 0; i < ladderData.getPlayersCount(); i++)
				players.add(new LadderPlayerUI(ladderData.getPlayerAt(i)));
			
			ladder.setItems(players);
			
			// Données sur le joueur courant
			if (ladderData.hasSelfData()) {
				hasCurrentPlayerData = true;
				currentPlayerData = ladderData.getSelfPlayerData();
			}
			
			if (hasCurrentPlayerData)
				selfPanel.getElement().setInnerHTML(
					new LadderPlayerUI(currentPlayerData).toString());
		} else {
			if (currentLadder != LADDER_ALLIES) {
				ArrayList<String> ranges = new ArrayList<String>();
				ranges.add("1 - 50"); //$NON-NLS-1$
				
				ranksComboBox.setItems(ranges);
			}
			
			if (ladderData.getRange() != -1 &&
					ranksComboBox.getSelectedIndex() != ladderData.getRange())
				ranksComboBox.setSelectedIndex(ladderData.getRange());
			
			currentLadder = LADDER_ALLIES;
			if (ladderData.getRange() != -1)
				currentRange = ladderData.getRange();
			
			ladderPane.setSelectedIndex(currentLadder);
			
			header.getElement().setInnerHTML(
				"<table unselectable=\"on\" cellspacing=\"0\"><tr>" + //$NON-NLS-1$
				"<td unselectable=\"on\" style=\"width: 48px;\"></td>" + //$NON-NLS-1$
				"<td unselectable=\"on\" style=\"width: 150px;\"><b unselectable=\"on\">" + messages.ladderAlly() + "</b></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<td unselectable=\"on\" style=\"width: 120px;\"><b unselectable=\"on\">" + messages.ladderAllyOrganization() + "</b></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<td unselectable=\"on\" style=\"width: 40px;\"><b unselectable=\"on\">" + messages.ladderAllyMembers() + "</b></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<td unselectable=\"on\" class=\"center\" style=\"width: 30px;\"><div class=\"medal goldCup\" style=\"margin-left: 7px;\"></div></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<td unselectable=\"on\" class=\"right\" style=\"width: 80px;\"><b unselectable=\"on\">" + messages.ladderPoints() + "</b></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<td unselectable=\"on\" style=\"width: 42px;\"></td>" +
				"</tr></table>"); //$NON-NLS-1$
			
			// Enregistre les données dans le cache si ce n'est pas déjà fait
			if (ladderData.getRange() != -1 && !cached) {
				alliesLadderCache[currentRange] = ladderData;
				alliesCacheDownloadTimes[currentRange] = Utilities.getCurrentTime();
			}
			
			// Construit la liste des alliances
			ArrayList<LadderAllyUI> allies = new ArrayList<LadderAllyUI>();
			for (int i = 0; i < ladderData.getAlliesCount(); i++)
				allies.add(new LadderAllyUI(ladderData.getAllyAt(i)));
			
			ladder.setItems(allies);
			
			// Données sur l'alliance du joueur courant
			if (ladderData.hasSelfData()) {
				hasCurrentAllyData = true;
				if (ladderData.hasSelfAlly())
					currentAllyData = ladderData.getSelfAllyData();
			}
			
			if (hasCurrentAllyData)
				if (currentAllyData != null)
					selfPanel.getElement().setInnerHTML(
						new LadderAllyUI(currentAllyData).toString());
				else
					selfPanel.getElement().setInnerHTML(
							"<span style=\"padding-left: 10px;\">" + //$NON-NLS-1$
							messages.ladderNoAlly() + "</span>"); //$NON-NLS-1$
		}
		
		updatingLadder = false;
	}
	
	private static class LadderPlayerUI {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private LadderPlayerData data;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public LadderPlayerUI(LadderPlayerData data) {
			this.data = data;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public final String toString() {
			String line = "<table unselectable=\"on\" cellspacing=\"0\"><tr unselectable=\"on\">"; //$NON-NLS-1$
			
			if (data.getMedal() == "none") //$NON-NLS-1$
				line += "<td unselectable=\"on\" class=\"rank right t-" + data.getPact() + "\" style=\"width: 50px;\">" + data.getRank() + ".&nbsp;</td>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			else
				line += "<td unselectable=\"on\" style=\"width: 20px;\"><div unselectable=\"on\" class=\"medal " + data.getMedal() + "\"></div></td>" + //$NON-NLS-1$ //$NON-NLS-2$
					"<td unselectable=\"on\" class=\"rank right t-" +data.getPact() + "\" style=\"width: 28px;\">" + data.getRank() + ".&nbsp;</td>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			String className = "player t-" + data.getPact();
			if(data.isPremium())
				className += " ispremium";
			
			
			line += "<td unselectable=\"on\" class=\"" + className + "\" style=\"width: 150px;\">" + data.getLogin() + "</td>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"<td unselectable=\"on\" class=\"ally t-" + data.getPact() + "\" style=\"width: 140px;\">" + data.getAlly() + "</td>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"<td unselectable=\"on\" class=\"level center t-" + data.getPact() + "\" style=\"width: 30px;\">" + data.getLevel() + "</td>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"<td unselectable=\"on\" class=\"level center t-" + data.getPact() + "\" style=\"width: 30px;\">" + data.getAchievements() + "</td>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"<td unselectable=\"on\" class=\"points center t-" + data.getPact() + "\" style=\"width: 70px;\">" + Formatter.formatNumber(data.getPoints()) + "</td>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			line += "</tr></table>"; //$NON-NLS-1$
			
			return line;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private static class LadderAllyUI {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private LadderAllyData data;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public LadderAllyUI(LadderAllyData data) {
			this.data = data;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public final String toString() {
			DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
			
			return "<table unselectable=\"on\" cellspacing=\"0\"><tr unselectable=\"on\">" + //$NON-NLS-1$
				"<td unselectable=\"on\" style=\"width: 20px;\"><div unselectable=\"on\" class=\"medal " + data.getMedal() + "\"></div></td>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<td unselectable=\"on\" class=\"rank right t-" + data.getPact() + "\" style=\"width: 28px;\">" + data.getRank() + ".&nbsp;</td>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"<td unselectable=\"on\" class=\"ally t-" + data.getPact() + "\" style=\"width: 150px;\">" + data.getName() + "</td>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"<td unselectable=\"on\" class=\"organization t-" + data.getPact() + "\" style=\"width: 120px;\">" + dynamicMessages.getString(data.getOrganization()) + "</td>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"<td unselectable=\"on\" class=\"members center t-" + data.getPact() + "\" style=\"width: 40px;\">" + data.getMembers() + "</td>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"<td unselectable=\"on\" class=\"level center t-" + data.getPact() + "\" style=\"width: 30px;\">" + data.getAchievements() + "</td>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"<td unselectable=\"on\" class=\"points right t-" + data.getPact() + "\" style=\"width: 80px;\">" + Formatter.formatNumber(data.getPoints()) + "</td>" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"</tr></table>"; //$NON-NLS-1$
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class DescriptionDialog extends JSDialog implements SelectionListener {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private AllyDescriptionData data;
		
		private JSScrollPane scrollPane;
		
		private JSTabbedPane viewsPane;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public DescriptionDialog() {
			super("", true, true, true);
			
			viewsPane = new JSTabbedPane();
			viewsPane.addTab("Présentation");
			viewsPane.addTab("Informations");
			viewsPane.setPixelWidth(400);
			viewsPane.addSelectionListener(this);
			
			scrollPane = new JSScrollPane();
			scrollPane.setPixelSize(400, 300);
			
			JSRowLayout layout = new JSRowLayout();
			layout.addComponent(viewsPane);
			layout.addRowSeparator(3);
			layout.addComponent(scrollPane);
			
			setComponent(layout);
			centerOnScreen();
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void setAllyDescription(AllyDescriptionData data) {
			this.data = data;
			
			setTitle(data.getName());
			viewsPane.setSelectedIndex(0);
			updateUI();
		}
		
		public void selectionChanged(Widget sender, int newValue, int oldValue) {
			if (sender == viewsPane) {
				updateUI();
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
		
		private void updateUI() {
			scrollPane.scrollUp(99999);
			
			switch (viewsPane.getSelectedIndex()) {
			case 0:
				HTMLPanel descriptionPanel = new HTMLPanel(data.getDescription());
				OpenJWT.setElementFloat(descriptionPanel.getElement(), "left");
				
				scrollPane.setView(descriptionPanel);
				break;
			case 1:
				StringBuffer infos = new StringBuffer();
				infos.append("<div style=\"line-height: 24px;\"><div style=\"float: left; width: 100px;\">&nbsp;Tag</div>");
				infos.append(data.getTag());
				infos.append("</div><div style=\"line-height: 24px; clear: both;\">" +
						"<div style=\"float: left; width: 100px;\">&nbsp;Fondateur</div>");
				infos.append(data.getFounder());
				infos.append("</div><div style=\"line-height: 24px; clear: both;\">" +
						"<div style=\"clear: both; float: left; width: 100px;\">&nbsp;Dirigeant" +
						(data.getLeadersCount() > 1 ? "s" : "") + "</div>");
				infos.append("<div style=\"padding-left: 100px;\">");
				
				for (int i = 0; i < data.getLeadersCount(); i++) {
					if (i > 0)
						infos.append(", ");
					infos.append(data.getLeaderAt(i));
				}
				
				infos.append("</div></div>");
				
				HTMLPanel infoPanel = new HTMLPanel(infos.toString());
				
				scrollPane.setView(infoPanel);
				break;
			}
		}
	}
}
