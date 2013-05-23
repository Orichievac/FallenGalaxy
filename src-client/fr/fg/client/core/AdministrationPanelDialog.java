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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.PlayerInfosData;
import fr.fg.client.data.PlayersInfosData;
import fr.fg.client.data.MotdData;
import fr.fg.client.data.MotdsData;
import fr.fg.client.data.ScriptData;
import fr.fg.client.data.ScriptsData;
import fr.fg.client.openjwt.ui.DialogListener;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSList;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.JSTextPane;
import fr.fg.client.openjwt.ui.SelectionListener;

@SuppressWarnings("deprecation")
public class AdministrationPanelDialog extends JSDialog implements SelectionListener,
ClickListener, ActionCallback, DialogListener {
	// ------------------------------------------------------- CONSTANTES -- //
	public final int 	VIEW_GENERAL=0,
						VIEW_ARCHIVE=1,
						VIEW_MULTIACCOUNTS=2,
						VIEW_MANAGEMENT=3,
						VIEW_SCRIPTS = 4,
						VIEW_STATISTICS = 5,
						VIEW_BUGS = 6;
	// -------------------------------------------------------- ATTRIBUTS -- //
	private JSRowLayout mainLayout;
	private JSRowLayout layoutGeneral, layoutArchiveBan, layoutMultiAccounts, layoutManagement, layoutScripts,
						layoutStatistics, layoutBugsReports;
	private JSTabbedPane viewsPane;
	private JSLabel tabHeader, label3, label4, scriptSelectionLabel,loadingLabel, player1Label, player2Label,
					tabMultiAccountsHeader,spacingLabel;
	private JSTextField player1, player2;
	private JSButton changeMotdBt, showBannedBt, unbanBt, banBt,execScriptBt, checkMultiAccountsBt,
						genericMultiAccountsCheckBt;
	private JSList bannedList, multiAccountsList;
	private Widget currentWidget;
	private HTMLPanel motdModerators, scriptAnswer;
	private JSComboBox scriptSelection;
	
	List<MotdData> motd;
	ArrayList<String> scriptList;
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	/**
	 * @Lloyd: On te fera pas payé plus cher pour mettre des commentaires :p
	 */
	public AdministrationPanelDialog() {
		super("Panneau d'administration", false, true, true);
		mainLayout = new JSRowLayout(); //Le layout de la fenetre, on met tous les éléments affichés dedans
		layoutGeneral = new JSRowLayout();
		layoutArchiveBan = new JSRowLayout();
		layoutMultiAccounts = new JSRowLayout();
		layoutManagement = new JSRowLayout();
		layoutScripts = new JSRowLayout();
		layoutStatistics = new JSRowLayout();
		layoutBugsReports = new JSRowLayout();
		currentWidget = layoutGeneral;
		
		layoutArchiveBan.setVisible(false);
		layoutMultiAccounts.setVisible(false);
		layoutManagement.setVisible(false);
		layoutScripts.setVisible(false);
		layoutStatistics.setVisible(false);
		layoutBugsReports.setVisible(false);
		
		ArrayList<String> views = new ArrayList<String>(); //Les titres des onglets
		views.add("Général");
		views.add("Bans");
		if(Settings.isAdministrator()) {
			views.add("Doubles comptes");
			views.add("Gestion modérateurs");
			views.add("Scripts");
			views.add("Statistiques");
			views.add("Bugs reports");
		}
		
		label4 = new JSLabel("Onglet gestion modérateurs");
		label4.setWidth("240");
		
		/***************************** Partie Général *****************************/
		
		if(Settings.isAdministrator()) {
			changeMotdBt = new JSButton("Changer le message du jour");
			changeMotdBt.setPixelWidth(240);
			changeMotdBt.addClickListener(this);
		}
		
		motdModerators = new HTMLPanel("");
		motdModerators.setPixelSize(640, 360);
		
		motd = new ArrayList<MotdData>();
		
		if(Settings.isAdministrator()) {
			layoutGeneral.addComponent(changeMotdBt);
			layoutGeneral.addRow();
		}
		layoutGeneral.addComponent(motdModerators);
		
		/*********************** Partie Gestion des Bannis ***********************/
		
		showBannedBt = new JSButton("Afficher les bannis");
		showBannedBt.setPixelWidth(240);
		showBannedBt.addClickListener(this);
		
		unbanBt = new JSButton("Débannir");
		unbanBt.setPixelWidth(150);
		unbanBt.addClickListener(this);
		unbanBt.setVisible(false);
		
		banBt = new JSButton("Bannir un joueur");
		banBt.setPixelWidth(240);
		banBt.addClickListener(this);
		banBt.setVisible(true);
		
		bannedList = new JSList();
		bannedList.setPixelSize(640,320);
		bannedList.addSelectionListener(this);
		
		tabHeader = new JSLabel(new BannedPlayer().getTabHeader());
		tabHeader.setPixelSize(640, 30);
		
		layoutArchiveBan.addComponent(showBannedBt);
		layoutArchiveBan.addComponent(unbanBt);
		layoutArchiveBan.addComponent(banBt);
		layoutArchiveBan.addComponent(new JSLabel("&nbsp;"));
		layoutArchiveBan.addRowSeparator(3);
		layoutArchiveBan.addComponent(tabHeader);
		layoutArchiveBan.addRow();
		layoutArchiveBan.addComponent(bannedList);
		
		/*********************** Partie Multi-comptes ***********************/
		if(Settings.isAdministrator()) {
		
		player1Label = new JSLabel("&nbsp;Joueur 1: ");
		player1Label.setPixelWidth(65);
		
		player2Label = new JSLabel("Joueur 2: ");
		player2Label.setPixelWidth(65);
		
		spacingLabel = new JSLabel("&nbsp;");
		spacingLabel.setPixelWidth(10);
		
		player1 = new JSTextField();
		player1.setPixelWidth(120);
		
		player2 = new JSTextField();
		player2.setPixelWidth(120);
		
		tabMultiAccountsHeader = new JSLabel(new PlayerInfos().getTabHeader());
		tabMultiAccountsHeader.setPixelSize(640, 30);
		
		
		multiAccountsList = new JSList();
		multiAccountsList.setPixelSize(640,320);
		multiAccountsList.addSelectionListener(this);
		
		
		checkMultiAccountsBt = new JSButton("Vérifier");
		checkMultiAccountsBt.setPixelWidth(100);
		checkMultiAccountsBt.addClickListener(this);
		checkMultiAccountsBt.setToolTipText("Vérification entre les 2 joueurs");
		
		genericMultiAccountsCheckBt = new JSButton("Vérification générique");
		genericMultiAccountsCheckBt.setPixelWidth(160);
		genericMultiAccountsCheckBt.addClickListener(this);
		genericMultiAccountsCheckBt.setToolTipText("Vérification pour tous les joueurs (non implémenté)");
		
		layoutMultiAccounts.addComponent(player1Label);
		layoutMultiAccounts.addComponent(player1);
		layoutMultiAccounts.addComponent(spacingLabel);
		layoutMultiAccounts.addComponent(player2Label);
		layoutMultiAccounts.addComponent(player2);
		layoutMultiAccounts.addComponent(checkMultiAccountsBt);
		layoutMultiAccounts.addComponent(genericMultiAccountsCheckBt);
		layoutMultiAccounts.addRow();
		layoutMultiAccounts.addComponent(tabMultiAccountsHeader);
		layoutMultiAccounts.addRow();
		layoutMultiAccounts.addComponent(multiAccountsList);
		}
		
		/************************** Partie Scripts **************************/
		
		if(Settings.isAdministrator()) {
			scriptList = new ArrayList<String>();
			
			scriptSelectionLabel = new JSLabel("Script: ");
			scriptSelectionLabel.setPixelWidth(80);
			
			scriptSelection = new JSComboBox();
			scriptSelection.setPixelWidth(220);
			
			execScriptBt = new JSButton("Exécuter le script");
			execScriptBt.setPixelWidth(180);
			execScriptBt.addClickListener(this);
			
			loadingLabel = new JSLabel();
			loadingLabel.setStyleName("loading-script");
			loadingLabel.setVisible(false);
			
			scriptAnswer = new HTMLPanel("");
			scriptAnswer.setPixelSize(640, 360);
			
			layoutScripts.addComponent(scriptSelectionLabel);
			layoutScripts.addComponent(scriptSelection);
			layoutScripts.addComponent(execScriptBt);
			layoutScripts.addComponent(loadingLabel);
			layoutScripts.addRow();
			layoutScripts.addComponent(scriptAnswer);
			
		}
		
		/********************** Partie Statistiques **********************/
		
		
		/*********************** Partie Bug Report ***********************/
		if(Settings.isAdministrator()) {
			layoutBugsReports = new LayoutBugsReports();
		}
		
		
		/*********************** Layout principal ***********************/
		
		mainLayout.setPixelSize(640,360);
		layoutGeneral.setPixelSize(640,320);
		layoutArchiveBan.setPixelWidth(640);
		layoutMultiAccounts.setPixelSize(640,320);
		layoutManagement.setPixelSize(640,320);
		
		layoutManagement.addComponent(label4);
		layoutManagement.addRow();
		
		viewsPane = new JSTabbedPane(); //Les onglets
		viewsPane.setTabs(views);
		viewsPane.setPixelSize(720, 30);
		viewsPane.addSelectionListener(this);
		
		//On ajoute les éléments par défaut au layout
		mainLayout.addComponent(viewsPane);
		mainLayout.addRowSeparator(3);
		mainLayout.addComponent(layoutGeneral);
		mainLayout.addComponent(layoutArchiveBan);
		if(Settings.isAdministrator()) {
			mainLayout.addComponent(layoutMultiAccounts);
			mainLayout.addComponent(layoutManagement);
			mainLayout.addComponent(layoutScripts);
			mainLayout.addComponent(layoutStatistics);
		}
		mainLayout.addRowSeparator(30);
		mainLayout.addComponent(new JSLabel("&nbsp;"));
		setComponent(mainLayout);
		centerOnScreen();
		
		getMotd();
		if(Settings.isAdministrator()) {
			refreshBannedList();
			refreshScriptList();
		}
	}
	// --------------------------------------------------------- METHODES -- //
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if(sender==viewsPane) {
			currentWidget.setVisible(false);
			switch (viewsPane.getSelectedIndex()) {
				case VIEW_GENERAL:
					layoutGeneral.setVisible(true);
					currentWidget = layoutGeneral;
					layoutGeneral.update();
					break;
				case VIEW_ARCHIVE:
					layoutArchiveBan.setVisible(true);
					currentWidget = layoutArchiveBan;
					layoutArchiveBan.update();
					break;
				case VIEW_MULTIACCOUNTS:
					layoutMultiAccounts.setVisible(true);
					currentWidget = layoutMultiAccounts;
					layoutMultiAccounts.update();
					break;
				case VIEW_MANAGEMENT:
					layoutManagement.setVisible(true);
					currentWidget = layoutManagement;
					layoutManagement.update();
					break;
				case VIEW_SCRIPTS:
					layoutScripts.setVisible(true);
					currentWidget = layoutScripts;
					layoutScripts.update();
					break;
				case VIEW_STATISTICS:
					layoutStatistics.setVisible(true);
					currentWidget = layoutStatistics;
					layoutScripts.update();
					break;
				default:break;	
			}
			mainLayout.update();
		}
		else if(sender == bannedList) {
				if( bannedList.getSelectedIndex()!=-1) {
					if( ((BannedPlayer) bannedList.getSelectedItem()).isGameBanned() && !Settings.isAdministrator()) {
						unbanBt.setVisible(false);
						layoutArchiveBan.update();
					}
					else {
						unbanBt.setVisible(true);
						layoutArchiveBan.update();	
					}
			}
		}
	}

	public void onClick(Widget sender) {
		if(sender == showBannedBt) {
			getBanned();
		}
		else if(sender == unbanBt) {
			unbanPlayer();
		}
		else if(sender == banBt) {
			BanPlayerDialog banPlayerDialog = new BanPlayerDialog(this);
			banPlayerDialog.setVisible(true);
		}
		else if(sender == changeMotdBt) {
			MotdDialog motdDialog = new MotdDialog(this);
			motdDialog.setVisible(true);
		}
		else if(sender == execScriptBt) {
			executeScript();
		}
		else if(sender == checkMultiAccountsBt) {
			checkMultiAccounts();
		}
		
	}

	public void refreshBannedList() {
		getBanned();
	}
	
	public void refreshMotd() {
		getMotd();
	}
	
	public void refreshScriptList() {
		getScriptList();
	}
	
	public String getMotdAt(int index) {
		String message ="";
		if(index>=motd.size())
			return message;
		for(int i=0;i<motd.size();i++) {
			if(motd.get(i).getId()==index) {
				message = motd.get(i).getMessage();
				break;
			}
		}
		return message;
	}
	
	public void onFailure(String error) {
		//ignoré
		
	}

	public void onSuccess(AnswerData data) {
		
	}

	public void dialogClosed(Widget sender) {
	
	}
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void getBanned() {
		HashMap<String,String> params = new HashMap<String,String>();
		new Action("admin/getbanned", params, new ActionCallback() {
			public void onSuccess(AnswerData data) {
				unbanBt.setVisible(false);
				bannedList.setSelectedIndex(-1);
				onBannedPlayerLoaded(data);
			}

			public void onFailure(String error) {
				ActionCallbackAdapter.onFailureDefaultBehavior(error);
			}
		});
	}
	
	private void onBannedPlayerLoaded(AnswerData data) {
		ArrayList<BannedPlayer> bannedPlayers = new ArrayList<BannedPlayer>();
		
		PlayersInfosData playersInfosData = data.getPlayersInfosData();
		int length = playersInfosData.getPlayersInfosCount();
		for(int i=0;i<length;i++) {
			bannedPlayers.add(new BannedPlayer(playersInfosData.getPlayerInfosDataAt(i)));
		}
		bannedList.setItems(bannedPlayers);
	}
	
	private void unbanPlayer() {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("id", String.valueOf(((BannedPlayer) bannedList.getSelectedItem()).getId()));
		new Action("admin/unban", params, new ActionCallback() {
			public void onSuccess(AnswerData data) {
				getBanned();
			}

			public void onFailure(String error) {
				ActionCallbackAdapter.onFailureDefaultBehavior(error);
			}
		});
	}
	
	private void getMotd() {
		HashMap<String,String> params = new HashMap<String,String>();
		new Action("admin/getmotd", params, new ActionCallback() {
			public void onSuccess(AnswerData data) {
				motd.clear();
				MotdsData motds = data.getMotdsData();
				for(int i=0;i<motds.getMotdCount();i++) {
					motd.add(motds.getMotdDataAt(i));
				}
				motdModerators.getElement().setInnerHTML(Utilities.parseSmilies(getMotdAt(1)));
				layoutGeneral.update();
			}
	
			public void onFailure(String error) {
				ActionCallbackAdapter.onFailureDefaultBehavior(error);
				
			}
		});
	}
	
		private void getScriptList() {
			HashMap<String,String> params = new HashMap<String,String>();
			new Action("admin/getscripts", params, new ActionCallback() {
				public void onSuccess(AnswerData data) {
					scriptList.clear();
					ScriptsData scriptsData = data.getScriptsData();
					for(int i=0;i<scriptsData.getScriptCount();i++) {
						scriptList.add(scriptsData.getScriptDataAt(i).getName());
					}
					scriptSelection.setItems(scriptList);
					layoutScripts.update();
				}
		
				public void onFailure(String error) {
					ActionCallbackAdapter.onFailureDefaultBehavior(error);
					
				}
			});
		}
		
		private void executeScript() {
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("name", ((String) scriptSelection.getSelectedItem()));
			loadingLabel.setVisible(true);
			new Action("admin/usescript", params, new ActionCallback() {
				public void onSuccess(AnswerData data) {
					ScriptData scriptData = data.getScriptData();
					scriptAnswer.getElement().setInnerHTML(scriptData.getMessage());
					loadingLabel.setVisible(false);
				}
		
				public void onFailure(String error) {
					ActionCallbackAdapter.onFailureDefaultBehavior(error);
					
				}
			});
		}
		
		private void checkMultiAccounts() {
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("player1", ((String) player1.getText()));
			params.put("player2", ((String) player2.getText()));
			new Action("admin/checkmultiplesaccounts", params, new ActionCallback() {
				public void onSuccess(AnswerData data) {
					PlayerInfosData playerInfosData = data.getPlayerInfosData();
					ArrayList<PlayerInfos> multiAccountsPlayers = new ArrayList<PlayerInfos>();
					multiAccountsPlayers.add(new PlayerInfos(playerInfosData));
					multiAccountsList.setItems(multiAccountsPlayers);
				}
		
				public void onFailure(String error) {
					ActionCallbackAdapter.onFailureDefaultBehavior(error);
					
				}
			});
		}
	
	private static class BannedPlayer {
		
		int idPlayer;
		String login;
		boolean gameBanned;
		String reason;
		String date;
		
		public BannedPlayer() {}
		
		public BannedPlayer(PlayerInfosData data) {
			this.idPlayer = data.getId();
			this.login = data.getLogin();
			this.gameBanned = data.isBanned();
			this.reason = data.getReason();
			this.date = data.getDate();
		}
		
		public final String toString() {
			return "<table cellspacing=\"0\" style=\"width: 100%;\"><tr>" +
			"<td style=\"width: 25%;\">" + login + "</td>" +
			"<td class=\"center\" style=\"width: 25%;\">" + reason + "</td>" +
			"<td class=\"center\" style=\"width: 25%;\">" + (!gameBanned? "Chat":"Jeu") + "</td>" +
			"<td class=\"date right\" style=\"width: 25%;\">" +
			date + "&nbsp;</td>" +
			"</tr></table>";
		}
		
		public int getId() {
			return idPlayer;
		}
		
		public boolean isGameBanned() {
			return gameBanned;
		}
		
		public String getTabHeader() {
			return "<table cellspacing=\"0\" style=\"width: 100%;\"><tr>" +
			"<td style=\"width: 25%;\"> Pseudo </td>" +
			"<td class=\"center\" style=\"width: 25%;\"> Raison </td>" +
			"<td class=\"center\" style=\"width: 25%;\"> Type </td>" +
			"<td class=\"date right\" style=\"width: 25%;\">" +
			"Jusqu'au &nbsp;</td>" +
			"</tr></table>";
		}
	}
	
private static class PlayerInfos {
		
		String login;
		String otherLogin;
		String reason;
		int probability;
		int lastIp;
		String color;
		
		public PlayerInfos() {}
		
		public PlayerInfos(PlayerInfosData data) {
			this.login = data.getLogin();
			this.otherLogin = data.getOtherLogin();
			this.lastIp = data.getRedundantIp();
			this.probability = data.getProbability();
			this.reason = data.getReason();
			
			if(probability<20) {
				color = "#347C2C";
			}
			else if(probability>=20 && probability<40) {
				color = "#667C26";
			}
			else if(probability>=40 && probability<70) {
				color = "#F88017";
			}
			else {
				color = "#FF0000";
			}
			
		}
		
		public final String toString() {
			Utilities.log("Avant toString");
			return "<table cellspacing=\"0\" style=\"width: 100%;\"><tr>" +
			"<td class=\"center\" style=\"width: 15%;\">" + login + "</td>" +
			"<td class=\"center\" style=\"width: 15%;\">" + otherLogin + "</td>" +
			"<td class=\"center\" style=\"width: 15%; color:"+color+";\">" + probability+"% </td>" +
			"<td class=\"center\" style=\"width: 20%;\">" +(lastIp!=0? Utilities.long2ip(lastIp):"")+ "</td>" +
			"<td class=\"center\" style=\"width: 35%;\">" +reason+ "</td>" +
			"</tr></table>";
		}	
		
		public String getTabHeader() {
			return "<table cellspacing=\"0\" style=\"width: 100%;\"><tr>" +
			"<td class=\"center\" style=\"width: 15%;\"> Joueur 1 </td>" +
			"<td class=\"center\" style=\"width: 15%;\">Joueur 2</td>" +
			"<td class=\"center\" style=\"width: 15%;\">Probabilité</td>" +
			"<td class=\"center\" style=\"width: 20%;\">ip commune</td>" +
			"<td class=\"center\" style=\"width: 35%;\">Description détaillée</td>" +
			"</tr></table>";
		}
	}
	
	private class BanPlayerDialog extends JSDialog implements ClickListener, ActionCallback {
		
		private JSRowLayout mainLayout;
		private JSTextField playerLogin, banTimeDay, banTimeHour, banTimeMinute;
		private JSLabel playerLoginLabel, banReasonLabel,banTypeLabel, banTimeLabel, banTimeDayLabel,
		banTimeHourLabel, banTimeMinuteLabel;
		private JSComboBox banType;
		private JSTextPane banReason;
		private JSButton confirmBt, cancelBt;
		
		AdministrationPanelDialog administrationPanelDialog;
		
		
		public BanPlayerDialog(AdministrationPanelDialog administrationPanelDialog) {
			super("Bannir un joueur", true, true, true);
			
			this.administrationPanelDialog = administrationPanelDialog;
			
			playerLoginLabel = new JSLabel("Pseudo: ");
			playerLoginLabel.setPixelWidth(120);
			
			playerLogin = new JSTextField();
			playerLogin.setPixelWidth(200);
			
			banTimeLabel = new JSLabel("Temps: ");
			banTimeLabel.setPixelWidth(120);
			
			banTimeDayLabel = new JSLabel("&nbsp;j");
			banTimeDayLabel.setPixelWidth(20);
			
			banTimeHourLabel = new JSLabel("&nbsp;h");
			banTimeHourLabel.setPixelWidth(20);
			
			banTimeMinuteLabel = new JSLabel("&nbsp;m");
			banTimeMinuteLabel.setPixelWidth(20);
			
			banTimeDay = new JSTextField();
			banTimeDay.setPixelWidth(30);
			
			banTimeHour = new JSTextField();
			banTimeHour.setPixelWidth(30);
			
			banTimeMinute = new JSTextField();
			banTimeMinute.setPixelWidth(30);
			
			banTypeLabel = new JSLabel("Type de ban: ");
			banTypeLabel.setPixelWidth(120);
			
			banType = new JSComboBox();
			banType.setPixelWidth(120);
			banType.addItem("Chat");
			if(Settings.isAdministrator()) {
				banType.addItem("Jeu");
			}
			banReasonLabel = new JSLabel("Raison: ");
			banReasonLabel.setPixelWidth(160);
			
			banReason = new JSTextPane();
			banReason.setPixelSize(320, 120);
			
			confirmBt = new JSButton("Bannir");
			confirmBt.setPixelWidth(160);
			confirmBt.addClickListener(this);
			
			cancelBt = new JSButton("Annuler");
			cancelBt.setPixelWidth(160);
			cancelBt.addClickListener(this);
			
			mainLayout = new JSRowLayout();
			mainLayout.addComponent(playerLoginLabel);
			mainLayout.addComponent(playerLogin);
			mainLayout.addRow();
			mainLayout.addComponent(banTimeLabel);
			mainLayout.addComponent(banTimeDay);
			mainLayout.addComponent(banTimeDayLabel);
			mainLayout.addComponent(banTimeHour);
			mainLayout.addComponent(banTimeHourLabel);
			mainLayout.addComponent(banTimeMinute);
			mainLayout.addComponent(banTimeMinuteLabel);
			mainLayout.addRow();
			mainLayout.addComponent(banTypeLabel);
			mainLayout.addComponent(banType);
			mainLayout.addRow();
			mainLayout.addComponent(banReasonLabel);
			mainLayout.addRow();
			mainLayout.addComponent(banReason);
			mainLayout.addRow();
			mainLayout.addComponent(confirmBt);
			mainLayout.addComponent(cancelBt);
			
			setComponent(mainLayout);
			centerOnScreen();
		}


		public void onClick(Widget sender) {
			if(sender == confirmBt) {
				if(playerLogin.getText().length()>0 &&
						(banTimeDay.getText().length()>0 || banTimeHour.getText().length()>0 || banTimeMinute.getText().length()>0)
						&& banType.getSelectedIndex()>=0) {
					banPlayer();
				}
			}
			else if(sender == cancelBt) {
				setVisible(false);
			}
			
		}
		
		private void banPlayer() {
			HashMap<String,String> params = new HashMap<String,String>();
			int days = banTimeDay.getText().length()>0?  Integer.parseInt(banTimeDay.getText()) : 0;
			int hours = banTimeHour.getText().length()>0?  Integer.parseInt(banTimeHour.getText()) : 0;
			int minutes = banTimeMinute.getText().length()>0?  Integer.parseInt(banTimeMinute.getText()) : 0;
			long time = (days*24*3600) + (hours*3600) + (minutes*60);
			
			params.put("login", playerLogin.getText());
			params.put("time", String.valueOf(time));
			params.put("reason", banReason.getHTML());
			params.put("bangame", String.valueOf(banType.getSelectedIndex()==0? false : true));
			
			new Action("admin/ban", params, this);
		}

		public void onSuccess(AnswerData data) {
			administrationPanelDialog.refreshBannedList();
			setVisible(false);
			
		}
		
		public void onFailure(String error) {
			ActionCallbackAdapter.onFailureDefaultBehavior(error);
			
		}
	} //End BanPlayerDialog
	
	private class MotdDialog extends JSDialog implements SelectionListener, ClickListener {
		
		private JSRowLayout mainLayout;
		private JSLabel typeLabel;
		private JSComboBox type;
		private JSTextPane content;
		private JSButton confirmBt, cancelBt;
		private AdministrationPanelDialog administrationPanelDialog;
		
		public MotdDialog(AdministrationPanelDialog administrationPanelDialog) {
			super("Message du jour",true,true,true);
			this.administrationPanelDialog = administrationPanelDialog;
			if(!Settings.isAdministrator()) {
				setVisible(false);
				return;
			}
			typeLabel = new JSLabel("Type: ");
			typeLabel.setPixelWidth(120);
			
			type = new JSComboBox();
			type.setPixelWidth(200);
			type.addItem("Chat");
			type.addItem("Moderateur");
			type.addSelectionListener(this);
			
			content = new JSTextPane();
			content.setPixelSize(320, 120);
			content.setHTML(administrationPanelDialog.getMotdAt(0));
			
			confirmBt = new JSButton("Confirmer");
			confirmBt.setPixelWidth(160);
			confirmBt.addClickListener(this);
			
			cancelBt = new JSButton("Annuler");
			cancelBt.setPixelWidth(160);
			cancelBt.addClickListener(this);
			
			mainLayout = new JSRowLayout();
			mainLayout.addComponent(typeLabel);
			mainLayout.addComponent(type);
			mainLayout.addRow();
			mainLayout.addComponent(content);
			mainLayout.addRow();
			mainLayout.addComponent(confirmBt);
			mainLayout.addComponent(cancelBt);
			
			setComponent(mainLayout);
			centerOnScreen();
		}

		public void onClick(Widget sender) {
			if(sender == confirmBt) {
				changeMotd();
			}
			else if(sender == cancelBt) {
				setVisible(false);
			}
		}
		
		private void changeMotd() {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("type", (String.valueOf(type.getSelectedIndex())));
			params.put("content", content.getHTML());
			new Action("admin/changemotd", params, new ActionCallback() {

				public void onSuccess(AnswerData data) {
					administrationPanelDialog.refreshMotd();
					setVisible(false);
					
				}
				public void onFailure(String error) {
					ActionCallbackAdapter.onFailureDefaultBehavior(error);
					
				}
				
			});
		}

		public void selectionChanged(Widget sender, int newValue, int oldValue) {
			if(sender == type) {
				if(content.getHTML().equals(administrationPanelDialog.getMotdAt(oldValue))) {
					content.setHTML(administrationPanelDialog.getMotdAt(newValue));
					mainLayout.update();
				}
			}
			
		}
	}
	
	/**
	 * layout pour les bugs reports
	 * @author Ghost
	 * @todo Ghost commencer!
	 */
	private class LayoutBugsReports extends JSRowLayout {
		
		public LayoutBugsReports(){
			
		}
	}
	
	
	
} //End AdministrationPanelDialog
