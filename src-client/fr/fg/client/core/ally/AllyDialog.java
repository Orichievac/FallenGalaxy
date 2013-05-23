/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.Client;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.core.Utilities;
import fr.fg.client.core.WriteMessageDialog;
import fr.fg.client.core.ProductsManager.ProductsListener;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.core.tactics.TacticsTools;
import fr.fg.client.data.AllyData;
import fr.fg.client.data.AllyMemberData;
import fr.fg.client.data.AllyNewsData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.ProductData;
import fr.fg.client.data.ShipData;
import fr.fg.client.data.TechnologyData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.DialogListener;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSComponent;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSList;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSScrollPane;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;
import fr.fg.client.openjwt.ui.SelectionListener;
import fr.fg.client.core.ally.AllyDescriptionDialog;
import fr.fg.client.core.ally.ApplicantDialog;
import fr.fg.client.core.ally.NoAllyDialog;
//import fr.fg.client.core.ally.AllyDialog.MemberUI;
//import fr.fg.client.core.ally.AllyDialog.NewsUI;

public class AllyDialog extends JSDialog implements SelectionListener,
ClickListener, ActionCallback, DialogListener, WindowResizeListener,
ProductsListener {
// ------------------------------------------------------- CONSTANTES -- //

private final static int
ITEMS_PER_PAGE = 25;

private final static int
VIEW_NEWS = 0,
VIEW_PRODUCTS = 1,
VIEW_MEMBERS = 2,
VIEW_DESCRIPTION = 3,
VIEW_SETTINGS = 4;

private final static String[] TERRITORY_COLORS = {
"#1583db", // Bleu
"#5fd8f3", // Cyan
"#1de386", // Vert bleu
"#09fb23", // Vert
"#d7e840", // Jaune vert
"#e8b640", // Jaune Orange
"#fb7509", // Orange
"#f07468", // Rose
"#f40b00", // Rouge
"#f71780", // Mauve
"#b717f7", // Violet
"#7568f0", // Violet bleu
"#808080",
};

private final static String[] RIGHTS_SETTINGS = {
AllyData.RIGHT_MANAGE_CONTRACTS,
AllyData.RIGHT_MANAGE_DIPLOMACY,
AllyData.RIGHT_MANAGE_STATIONS,
AllyData.RIGHT_ACCEPT,
AllyData.RIGHT_MANAGE_NEWS,
AllyData.RIGHT_MANAGE_DESCRIPTION
};

@SuppressWarnings("unchecked")
private final static Comparator[] MEMBERS_COMPARATORS = {
// Tri par rang, puis par ordre alphabétique
new Comparator<MemberUI>() {
	public int compare(MemberUI p1, MemberUI p2) {
		if (p1.getData().getRank() > p2.getData().getRank())
			return -1;
		else if (p1.getData().getRank() < p2.getData().getRank())
			return 1;
		else
			return p1.getData().getLogin().compareToIgnoreCase(
					p2.getData().getLogin());
	}
},
// Tri par ordre alphabétique
new Comparator<MemberUI>() {
	public int compare(MemberUI p1, MemberUI p2) {
		return p1.getData().getLogin().compareToIgnoreCase(
				p2.getData().getLogin());
	}
},
// Tri par points
new Comparator<MemberUI>() {
	public int compare(MemberUI p1, MemberUI p2) {
		return p1.getData().getPoints() <
			p2.getData().getPoints() ? 1 : -1;
	}
},
// Tri par puissance des flottes
new Comparator<MemberUI>() {
	public int compare(MemberUI p1, MemberUI p2) {
		return p1.getData().getTotalFleetsPower() <
			p2.getData().getTotalFleetsPower() ? 1 : -1;
	}
},
// Tri par activé, puis par ordre alphabétique
new Comparator<MemberUI>() {
	public int compare(MemberUI p1, MemberUI p2) {
		if (p1.getData().getIdleTime() <
			p2.getData().getIdleTime())
			return -1;
		else if (p1.getData().getIdleTime() >
				p2.getData().getIdleTime())
			return 1;
		else
			return p1.getData().getLogin().compareToIgnoreCase(
					p2.getData().getLogin());
	}
}
};

private final static int
HIGH_RES_EXTRA_WIDTH = 180,
HIGH_RES_EXTRA_HEIGHT = 140;

// -------------------------------------------------------- ATTRIBUTS -- //

private int currentClientWidth;

private boolean highres;

private JSTabbedPane viewsPane;

private JSList messagesList;

private JSScrollPane messageScrollPane, descriptionScrollPane, productsScrollPane;

private HTMLPanel messagePanel, descriptionPanel, productsPanel;

private JSButton previousPageBt, nextPageBt, writeBt, allReadBt, deleteBt, answerBt,
stickBt, memberPromoteBt, memberDestituteBt, memberKickBt,
memberVoteKickBt, applicantAcceptBt, applicantDeclineBt,
applicantVoteBt, closeBt, exitBt, updateDescriptionBt, electBt, delegateBt;

private JSLabel pageLabel, blankLabel, membersComparatorLabel, separator,
separator2;

private long lastUpdate;

private int unreadNews;

private Action downloadAction, currentAction;

private ArrayList<NewsUI> newsList;

private ArrayList<MemberUI> membersList;

private NoAllyDialog noAllyDialog;

private ApplicantDialog applicantDialog;

private JSRowLayout layout;

private HTMLPanel horizontalSeparator;

private FlowPanel settingsLayout;

private JSComboBox membersComparatorComboBox, colorsComboBox;

private int currentPage;

private AllyData data;

private JSComboBox[] settingsRankComboBox;

// ---------------------------------------------------- CONSTRUCTEURS -- //

public AllyDialog() {
super("<img src=\"" + Config.getMediaUrl() +
		"images/misc/blank.gif\" class=\"iconAlly\"/> " +
		"Alliance", false, true, true);

StaticMessages messages =
	(StaticMessages) GWT.create(StaticMessages.class);

// Date du dernier téléchargement des messages
lastUpdate = 0;
currentPage = 0;
unreadNews = 0;
applicantDialog = new ApplicantDialog();
applicantDialog.addDialogListener(this);
noAllyDialog = new NoAllyDialog();
noAllyDialog.addDialogListener(this);
newsList = new ArrayList<NewsUI>();
membersList = new ArrayList<MemberUI>();
currentClientWidth = OpenJWT.getClientWidth();
highres = currentClientWidth > 1024;

// Choix de la vue
ArrayList<String> views = new ArrayList<String>();
views.add("<img src=\"" + Config.getMediaUrl() +
	"images/misc/blank.gif\" class=\"tabIcon iconAllyNews\"/> " + "News");
views.add("<img src=\"" + Config.getMediaUrl() +
	"images/misc/blank.gif\" class=\"tabIcon iconAllyProducts\"/> " + "Produits");
views.add("<img src=\"" + Config.getMediaUrl() +
	"images/misc/blank.gif\" class=\"tabIcon iconAllyMembers\"/> " + "Membres");
views.add("<img src=\"" + Config.getMediaUrl() +
	"images/misc/blank.gif\" class=\"tabIcon iconAllyDescription\"/> " + "Présentation");
views.add("<img src=\"" + Config.getMediaUrl() +
	"images/misc/blank.gif\" class=\"tabIcon iconAllySettings\"/> " + "Paramètres");

viewsPane = new JSTabbedPane();
viewsPane.setTabs(views);
viewsPane.setPixelWidth(550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0));
viewsPane.addSelectionListener(this);

// Boutons de la barre d'outils
writeBt = new JSButton("Poster une news");
writeBt.setPixelWidth(180);
writeBt.addClickListener(this);

allReadBt = new JSButton("Tout marquer comme lu");
allReadBt.setPixelWidth(180);
allReadBt.addClickListener(this);

deleteBt = new JSButton(messages.delete());
deleteBt.setPixelWidth(100);
deleteBt.addClickListener(this);
deleteBt.setVisible(false);

answerBt = new JSButton(messages.answer());
answerBt.setPixelWidth(100);
answerBt.addClickListener(this);
answerBt.setVisible(false);

stickBt = new JSButton("Épingler");
stickBt.setPixelWidth(100);
stickBt.addClickListener(this);
stickBt.setVisible(false);

closeBt = new JSButton("Fermer");
closeBt.setPixelWidth(100);
closeBt.addClickListener(this);
closeBt.setVisible(false);

memberPromoteBt = new JSButton("Promouvoir");
memberPromoteBt.setPixelWidth(100);
memberPromoteBt.addClickListener(this);
memberPromoteBt.setVisible(false);

memberDestituteBt = new JSButton("Destituer");
memberDestituteBt.setPixelWidth(100);
memberDestituteBt.addClickListener(this);
memberDestituteBt.setVisible(false);

memberKickBt = new JSButton("Éjecter");
memberKickBt.setPixelWidth(100);
memberKickBt.addClickListener(this);
memberKickBt.setVisible(false);

memberVoteKickBt = new JSButton("Voter éjection");
memberVoteKickBt.setPixelWidth(100);
memberVoteKickBt.addClickListener(this);
memberVoteKickBt.setVisible(false);

applicantAcceptBt = new JSButton("Accepter");
applicantAcceptBt.setPixelWidth(100);
applicantAcceptBt.addClickListener(this);
applicantAcceptBt.setVisible(false);

applicantDeclineBt = new JSButton("Refuser");
applicantDeclineBt.setPixelWidth(100);
applicantDeclineBt.addClickListener(this);
applicantDeclineBt.setVisible(false);

applicantVoteBt = new JSButton("Voter");
applicantVoteBt.setPixelWidth(100);
applicantVoteBt.addClickListener(this);
applicantVoteBt.setVisible(false);

electBt = new JSButton("Elire");
electBt.setPixelWidth(100);
electBt.addClickListener(this);
electBt.setVisible(false);

delegateBt = new JSButton("Déléguer");
delegateBt.setPixelWidth(100);
delegateBt.addClickListener(this);
delegateBt.setVisible(false);

// Pages suivante / précédente
previousPageBt = new JSButton();
previousPageBt.setPixelWidth(JSComponent.getUIPropertyInt(
	JSButton.UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT));
previousPageBt.addStyleName("iconLeft");
previousPageBt.addClickListener(this);
previousPageBt.setVisible(false);

nextPageBt = new JSButton();
nextPageBt.setPixelWidth(JSComponent.getUIPropertyInt(
	JSButton.UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT));
nextPageBt.addStyleName("iconRight");
nextPageBt.addClickListener(this);
nextPageBt.setVisible(false);

pageLabel = new JSLabel();
pageLabel.setPixelWidth(60);
pageLabel.setAlignment(JSLabel.ALIGN_CENTER);
pageLabel.setVisible(false);

separator = new JSLabel();
separator.setPixelWidth(310 + (highres ? HIGH_RES_EXTRA_WIDTH : 0) -
	writeBt.getPixelWidth() -
	allReadBt.getPixelWidth());

// Liste des messages
messagesList = new JSList();
messagesList.setPixelSize(
	550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0),
	280 + (highres ? HIGH_RES_EXTRA_HEIGHT : 0));
messagesList.addStyleName("messages");
messagesList.addSelectionListener(this);

// Contenu d'un message
messagePanel = new HTMLPanel("");
OpenJWT.setElementFloat(messagePanel.getElement(), "left");

messageScrollPane = new JSScrollPane();
messageScrollPane.setView(messagePanel);
messageScrollPane.setPixelSize(
	550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0),
	182 + (highres ? HIGH_RES_EXTRA_HEIGHT / 2 : 0));
messageScrollPane.setVisible(false);

horizontalSeparator = new HTMLPanel("");
horizontalSeparator.addStyleName("horizontalSeparator");
horizontalSeparator.setWidth(
	550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0) + "px");
horizontalSeparator.setVisible(false);
OpenJWT.setElementFloat(horizontalSeparator.getElement(), "left");

// Tri des membres
ArrayList<String> membersComparators = new ArrayList<String>();
membersComparators.add("Rang");
membersComparators.add("Alphabétique");
membersComparators.add("Points");
membersComparators.add("Puissance");
membersComparators.add("Activité");

membersComparatorLabel = new JSLabel("&nbsp;Trier");
membersComparatorLabel.setPixelWidth(60);
membersComparatorLabel.setVisible(false);

membersComparatorComboBox = new JSComboBox();
membersComparatorComboBox.setPixelWidth(120);
membersComparatorComboBox.setItems(membersComparators);
membersComparatorComboBox.setVisible(false);
membersComparatorComboBox.addSelectionListener(this);

// Produits
productsPanel = new HTMLPanel("");
OpenJWT.setElementFloat(productsPanel.getElement(), "left");

productsScrollPane = new JSScrollPane();
productsScrollPane.setView(productsPanel);
productsScrollPane.setPixelSize(
	550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0),
	280 + (highres ? HIGH_RES_EXTRA_HEIGHT : 0));
productsScrollPane.setVisible(false);

// Description
updateDescriptionBt = new JSButton("Mettre à jour");
updateDescriptionBt.setPixelWidth(180);
updateDescriptionBt.setVisible(false);
updateDescriptionBt.addClickListener(this);

descriptionPanel = new HTMLPanel("");
OpenJWT.setElementFloat(descriptionPanel.getElement(), "left");

descriptionScrollPane = new JSScrollPane();
descriptionScrollPane.setView(descriptionPanel);
descriptionScrollPane.setPixelSize(
	550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0),
	280 + (highres ? HIGH_RES_EXTRA_HEIGHT : 0));
descriptionScrollPane.setVisible(false);

// Paramètres
String[] labels = {
	"Gérer les missions d'alliance",
	"Gérer la diplomatie",
	"Gérer les stations spatiales",
	"Accepter les candidatures",
	"Gérer les news",
	"Modifier la description",
};

JSRowLayout innerSettingsLayout = new JSRowLayout();
settingsRankComboBox = new JSComboBox[RIGHTS_SETTINGS.length];

for (int i = 0; i < RIGHTS_SETTINGS.length; i++) {
	JSLabel settingsLabel = new JSLabel(labels[i]);
	settingsLabel.setPixelWidth(330);
	
	settingsRankComboBox[i] = new JSComboBox();
	settingsRankComboBox[i].setPixelWidth(180);
	settingsRankComboBox[i].addSelectionListener(this);
	
	if (i > 0)
		innerSettingsLayout.addRow();
	innerSettingsLayout.addComponent(JSRowLayout.createHorizontalSeparator(20));
	innerSettingsLayout.addComponent(settingsLabel);
	innerSettingsLayout.addComponent(settingsRankComboBox[i]);
}

JSLabel colorsLabel = new JSLabel("Couleur du territoire");
colorsLabel.setPixelWidth(330);

colorsComboBox = new JSComboBox();
colorsComboBox.setPixelWidth(180);

innerSettingsLayout.addRow();
innerSettingsLayout.addComponent(JSRowLayout.createHorizontalSeparator(20));
innerSettingsLayout.addComponent(colorsLabel);
innerSettingsLayout.addComponent(colorsComboBox);

// Bouton pour quitter l'alliance
JSLabel exitLabel = new JSLabel("Quitter l'alliance");
exitLabel.setPixelWidth(330);

exitBt = new JSButton("Quitter l'alliance");
exitBt.setPixelWidth(180);
exitBt.addClickListener(this);

innerSettingsLayout.addRowSeparator(20);
innerSettingsLayout.addComponent(JSRowLayout.createHorizontalSeparator(20));
innerSettingsLayout.addComponent(exitLabel);
innerSettingsLayout.addComponent(exitBt);

settingsLayout = new FlowPanel();
settingsLayout.add(innerSettingsLayout);
settingsLayout.setPixelSize(
	550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0),
	280 + (highres ? HIGH_RES_EXTRA_HEIGHT : 0));
settingsLayout.setVisible(false);
OpenJWT.setElementFloat(settingsLayout.getElement(), "left");

blankLabel = new JSLabel("&nbsp;");
blankLabel.setVisible(false);

exitLabel = new JSLabel(
		"Vous deviendrez neutre avec les membres de l'alliance.");
exitLabel.setPixelWidth(550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0));
exitLabel.getElement().getStyle().setProperty("padding", "80px 0 20px 0");
exitLabel.setAlignment(JSLabel.ALIGN_CENTER);

separator2 = new JSLabel();
separator2.setPixelWidth(185 + (highres ? HIGH_RES_EXTRA_WIDTH / 2 : 0));

// Mise en forme des composants
layout = new JSRowLayout();
layout.addComponent(viewsPane);
layout.addRowSeparator(3);
layout.addComponent(writeBt);
layout.addComponent(allReadBt);
layout.addComponent(membersComparatorLabel);
layout.addComponent(membersComparatorComboBox);
layout.addComponent(updateDescriptionBt);
layout.addComponent(separator);
layout.addComponent(previousPageBt);
layout.addComponent(pageLabel);
layout.addComponent(nextPageBt);
layout.addRowSeparator(1);
layout.addComponent(messagesList);
layout.addComponent(productsScrollPane);
layout.addComponent(descriptionScrollPane);
layout.addComponent(settingsLayout);
layout.addRowSeparator(1);
layout.addComponent(horizontalSeparator);
layout.addRowSeparator(1);
layout.addComponent(messageScrollPane);
layout.addRow();
layout.addComponent(answerBt);
layout.addComponent(deleteBt);
layout.addComponent(stickBt);
layout.addComponent(memberPromoteBt);
layout.addComponent(memberDestituteBt);
layout.addComponent(memberKickBt);
layout.addComponent(memberVoteKickBt);
layout.addComponent(applicantAcceptBt);
layout.addComponent(applicantDeclineBt);
layout.addComponent(applicantVoteBt);
layout.addComponent(electBt);
layout.addComponent(delegateBt);
layout.addComponent(closeBt);
layout.addComponent(blankLabel);

setComponent(layout);
centerOnScreen();

Window.addWindowResizeListener(this);
}

// --------------------------------------------------------- METHODES -- //

@SuppressWarnings("unchecked")
public void selectionChanged(Widget sender, int newValue, int oldValue) {
StaticMessages messages =
	(StaticMessages) GWT.create(StaticMessages.class);
DynamicMessages dynamicMessages =
	(DynamicMessages) GWT.create(DynamicMessages.class);
int rank = this.getPlayerRank();
int rightDescriptionRank = data.getRequiredRank(AllyData.RIGHT_MANAGE_DESCRIPTION);

if (sender == viewsPane) {
	messagesList.setSelectedIndex(-1);
	messagesList.setVisible(newValue == VIEW_NEWS || newValue == VIEW_MEMBERS);
	productsScrollPane.setVisible(newValue == VIEW_PRODUCTS);
	descriptionScrollPane.setVisible(newValue == VIEW_DESCRIPTION);
	updateDescriptionBt.setVisible(newValue == VIEW_DESCRIPTION && rank >= rightDescriptionRank);
	settingsLayout.setVisible(newValue == VIEW_SETTINGS);
	writeBt.setVisible(newValue == VIEW_NEWS);
	allReadBt.setVisible(newValue == VIEW_NEWS);
	membersComparatorLabel.setVisible(newValue == VIEW_MEMBERS);
	membersComparatorComboBox.setVisible(newValue == VIEW_MEMBERS);
	previousPageBt.setVisible(false);
	pageLabel.setVisible(false);
	nextPageBt.setVisible(false);
	layout.update();
	
	switch (newValue) {
	case VIEW_NEWS:
	case VIEW_MEMBERS:
		currentPage = 0;
		updateUI();
		break;
	case VIEW_PRODUCTS:
		productsScrollPane.update();
		break;
	case VIEW_DESCRIPTION:
		descriptionScrollPane.update();
		break;
	}
} else if (sender == messagesList) {
	deleteBt.setVisible(false);
	answerBt.setVisible(false);
	stickBt.setVisible(false);
	memberPromoteBt.setVisible(false);
	memberDestituteBt.setVisible(false);
	memberKickBt.setVisible(false);
	memberVoteKickBt.setVisible(false);
	electBt.setVisible(false);
	delegateBt.setVisible(false);
	applicantAcceptBt.setVisible(false);
	applicantDeclineBt.setVisible(false);
	applicantVoteBt.setVisible(false);
	messagesList.setPixelSize(
		550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0),
		70 + (highres ? HIGH_RES_EXTRA_HEIGHT / 2 : 0));
	messageScrollPane.setVisible(true);
	blankLabel.setVisible(true);
	horizontalSeparator.setVisible(true);
	closeBt.setVisible(true);
	
	int scroll = 0;
	
	if (newValue != -1) {
		// Recherche le rang du joueur
		int playerRank = 0;
		for (int i = 0; i < data.getMembersCount(); i++)
			if (data.getMemberAt(i).getId() == Settings.getPlayerId()) {
				playerRank = data.getMemberAt(i).getRank();
				break;
			}
		
		// Affiche le contenu du message
		switch (viewsPane.getSelectedIndex()) {
		case VIEW_NEWS:
			NewsUI newsUI = (NewsUI) messagesList.getSelectedItem();
			
			StringBuffer content = new StringBuffer();
			
			for (int i = 0; i < newsUI.getData().length; i++) {
				AllyNewsData data = newsUI.getData()[i];
				
				String date = DateTimeFormat.getFormat(messages.dateTimeFormat()).format(
						new Date((long) (1000 * data.getDate())));
				
				content.append("<div style=\"padding: 3px;\">" +
					"<div class=\"messageHeader\">" + (data.hasAuthor() ?
					"<span class=\"owner-" + data.getTreaty() +
					"\" unselectable=\"on\">" + (data.hasAllyTag() ?
					"<span unselectable=\"on\" class=\"allyTag\">[" +
					data.getAllyTag() + "]</span> " : "") + data.getAuthor() +
					"</span> - " : "") + Utilities.parseSmilies(data.getTitle()) +
					"</div><div style=\"padding: 5px 0;\">" +
					TacticsTools.parseTacticsLinks(Utilities.parseUrlAndSmilies(
						data.getContent())) + "</div>" +
					"<div class=\"small messageDate\">Envoyé le " +
					date + "</div></div>");
			}
			
			// Marque le message comme lu si ce n'est pas le cas
			AllyNewsData currentNews = newsUI.getData()[newsUI.getData().length - 1];
			
		//	if (!currentNews.isRead()) {
				for (int i = 0; i < newsUI.getData().length; i++)
					if (!newsUI.getData()[i].isRead()) {
						newsUI.getData()[i].setRead(true);
						unreadNews--;
			//		}
				
				if (unreadNews == 0)
					Client.getInstance().getToolBar().blinkAlly(false);
				
				messagesList.setItemAt(new NewsUI(newsUI.getData()), newValue);
				
				HashMap<String, String> params =
					new HashMap<String, String>();
				params.put("id", String.valueOf(currentNews.getId()));
				
				new Action("allies/setnewsread", params);
			}
			
			int idApplicant = newsUI.getData()[newsUI.getData().length - 1].getIdApplicant();
			
			deleteBt.setVisible(idApplicant == 0 &&
					playerRank >= data.getRequiredRank(AllyData.RIGHT_MANAGE_NEWS));
			answerBt.setVisible(true);
			stickBt.setVisible(playerRank >= data.getRequiredRank(AllyData.RIGHT_MANAGE_NEWS) &&
					!newsUI.getData()[0].isSticky());
			
			if (idApplicant != 0) {
				// Recherche si un vote concerne le joueur
				boolean voteAccept = false;
				for (int i = 0; i < data.getVotesCount(); i++)
					if (data.getVoteAt(i).getType().equals("accept") &&
							data.getVoteAt(i).getIdPlayer() == idApplicant) {
						voteAccept = true;
						break;
						
					}
				
				if (playerRank >= data.getRequiredRank(AllyData.RIGHT_ACCEPT))
					applicantAcceptBt.setVisible(true);
				if (playerRank >= data.getRequiredRank(AllyData.RIGHT_ACCEPT))
					applicantDeclineBt.setVisible(true);
				if (/*!voteAccept && */playerRank >= data.getRequiredRank(AllyData.RIGHT_VOTE_ACCEPT))
					applicantVoteBt.setVisible(true);
			}
			
			messagePanel.getElement().setInnerHTML(content.toString());
			scroll = 99999;
			break;
		case VIEW_MEMBERS:
			MemberUI memberUI = (MemberUI) messagesList.getSelectedItem();
			AllyMemberData member = memberUI.getData();
			
			// Recherche si un vote concerne le joueur
			boolean voteKick = false;
			for (int i = 0; i < data.getVotesCount(); i++)
				if (data.getVoteAt(i).getType().equals("kick") &&
						data.getVoteAt(i).getIdPlayer() == member.getId()) {
					voteKick = true;
					break;
				}
			
			boolean isPlayer = member.getLogin(
					).equals(Settings.getPlayerLogin());
			
			int maxPower = -1;
			for (int i = 0; i < member.getFleetsCount(); i++)
				if (maxPower < member.getFleetAt(i).getPower())
					maxPower = member.getFleetAt(i).getPower();
			
			StringBuffer fleets = new StringBuffer(
				"<div class=\"title\" style=\"float: left; " +
				"width: 500px; clear: both;\">Flottes</div>");
			
			if (maxPower != -1) {
				// Compte les flottes en fonction de leur puissance
				// par secteur
				ArrayList<String> areas = new ArrayList<String>();
				for (int i = 0; i < member.getFleetsCount(); i++)
					if (!areas.contains(member.getFleetAt(i).getArea()))
						areas.add(member.getFleetAt(i).getArea());
				
				Collections.sort(areas, String.CASE_INSENSITIVE_ORDER);
				
				for (String area : areas) {
					fleets.append("<div style=\"float: left; " +
						"width: 120px; clear: both;\">" + area + "</div>" +
						"<div style=\"float: left; width: 400px;\">");
					
					int[] fleetPower = new int[maxPower + 1];
					
					for (int i = 0; i < member.getFleetsCount(); i++)
						if (area.equals(member.getFleetAt(i).getArea()))
							fleetPower[member.getFleetAt(i).getPower()]++;
					
					boolean first = true;
					for (int i = fleetPower.length - 1; i >= 0; i--)
						if (fleetPower[i] > 0) {
							fleets.append((first ? "" : " &nbsp; ") + "<b>" +
								fleetPower[i] + "x</b> <span class=\"emphasize\">" +
								i + "</span><img src=\"" + Config.getMediaUrl() +
								"images/misc/blank.gif\" class=\"stat s-power\" " +
								"unselectable=\"on\"/>");
							first = false;
						}
					
					fleets.append("</div>");
				}
				
				// Compte le total des flottes en fonction de leur
				// puissance
				fleets.append("<div style=\"float: left; " +
					"width: 120px; clear: both;\">Total</div>" +
					"<div style=\"float: left; width: 400px;\">");
					
				int[] fleetPower = new int[maxPower + 1];
				
				for (int i = 0; i < member.getFleetsCount(); i++)
					fleetPower[member.getFleetAt(i).getPower()]++;
				
				boolean first = true;
				for (int i = fleetPower.length - 1; i >= 0; i--)
					if (fleetPower[i] > 0) {
						fleets.append((first ? "" : " &nbsp; ") + "<b>" +
							fleetPower[i] + "x</b></b> <span class=\"emphasize\">" +
							i + "</span><img src=\"" + Config.getMediaUrl() +
							"images/misc/blank.gif\" class=\"stat s-power\" " +
							"unselectable=\"on\"/>");
						first = false;
					}
				
				fleets.append("</div>");
			}
			
			// Affiche les systèmes du joueur, par secteur et au total
			StringBuffer systems = new StringBuffer(
				"<div class=\"title\" style=\"float: left; " +
				"padding-top: 5px; width: 500px; clear: both;\">" +
				"Systèmes</div>");
			
			ArrayList<String> areas = new ArrayList<String>();
			for (int i = 0; i < member.getSystemsCount(); i++)
				if (!areas.contains(member.getSystemAt(i)))
					areas.add(member.getSystemAt(i));
			
			Collections.sort(areas, String.CASE_INSENSITIVE_ORDER);
			
			for (String area : areas) {
				int count = 0;
				for (int i = 0; i < member.getSystemsCount(); i++)
					if (member.getSystemAt(i).equals(area))
						count++;
				
				systems.append("<div style=\"float: left; " +
					"width: 120px; clear: both;\">" + area + "</div>" +
					"<div style=\"float: left; width: 400px;\">" +
					"<b>" + count + "</b></div>");
			}
			systems.append("<div style=\"float: left; " +
				"width: 120px; clear: both;\">Total</div>" +
				"<div style=\"float: left; width: 400px;\">" +
				"<b>" + member.getSystemsCount() + "</b></div>");
			
			messagePanel.getElement().setInnerHTML(
				"<div style=\"padding: 3px;\">" +
				"<div class=\"messageHeader\">" +
				member.getLogin() + "<span class=\"deemphasize\"> - " +
				dynamicMessages.getString(data.getOrganization() +
				"Rank" + member.getRank()) + "</span></div>" +
				"<div style=\"padding: 5px 0;\">" +
				fleets.toString() + systems.toString() +
				"<div class=\"title\" style=\"padding-top: 5px; " +
				"float: left; width: 500px; clear: both;\">" +
				"Points</div><div style=\"float: left; " +
				"width: 120px; clear: both;\">Total</div>" +
				"<div style=\"float: left; width: 400px;\"><b>" +
				Formatter.formatNumber(member.getPoints()) +
				"</b></div></div>");
			
			// Affiche les boutons pour promouvoir / destituer / éjecter le
			// membre si le joueur a les droits requis
			if (!isPlayer && member.getRank() < 3 && playerRank >= data.getRequiredRank(AllyData.RIGHT_PROMOTE, member.getRank() + 1))
				memberPromoteBt.setVisible(true);
			if (!isPlayer && member.getRank()>1 && playerRank >= data.getRequiredRank(AllyData.RIGHT_PROMOTE, member.getRank()))
				memberDestituteBt.setVisible(true);
			if (!isPlayer && playerRank >= data.getRequiredRank(AllyData.RIGHT_KICK, member.getRank()))
				memberKickBt.setVisible(true);
			if (/*!voteKick && */!isPlayer && playerRank >= data.getRequiredRank(AllyData.RIGHT_VOTE_KICK, member.getRank()))
				memberVoteKickBt.setVisible(true);
			if (!isPlayer && data.getOrganization().equals("democracy"))
				electBt.setVisible(true);
			if (!isPlayer && isPlayerLeader() && playerRank > member.getRank())
				delegateBt.setVisible(true);
				
			scroll = -99999;
			break;
		}
	} else {
		closeBt.setVisible(false);
		horizontalSeparator.setVisible(false);
		blankLabel.setVisible(false);
		messagesList.setPixelSize(
			550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0),
			280 + (highres ? HIGH_RES_EXTRA_HEIGHT : 0));
		messageScrollPane.setVisible(false);
		messagePanel.getElement().setInnerHTML("");
	}
	messageScrollPane.update();
	if (scroll > 0)
		messageScrollPane.scrollDown(scroll);
	else if (scroll < 0)
		messageScrollPane.scrollUp(-scroll);
	
	layout.update();
	
	// S'il y a des images en cours de chargement, les barres de
	// défilement ne descendent pas toujours jusqu'en bas
	if (scroll > 0) {
		messageScrollPane.update();
		messageScrollPane.scrollDown(scroll);
	}
} else if (sender == membersComparatorComboBox) {
	Collections.sort(membersList,
		MEMBERS_COMPARATORS[membersComparatorComboBox.getSelectedIndex()]);
	updateUI();
} else if (sender == colorsComboBox) {
	if (currentAction != null && currentAction.isPending())
		return;
	
	HashMap<String, String> params = new HashMap<String, String>();
	params.put("color", String.valueOf(newValue));
	params.put("update", String.valueOf(lastUpdate));
	
	currentAction = new Action("allies/setterritorycolor", params, UpdateManager.UPDATE_CALLBACK);
} else {
	for (int i = 0; i < settingsRankComboBox.length; i++) {
		if (sender == settingsRankComboBox[i]) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("right", RIGHTS_SETTINGS[i]);
			params.put("rank", String.valueOf(settingsRankComboBox[i].getSelectedIndex() + 1));
			params.put("update", String.valueOf(lastUpdate));
			
			currentAction = new Action("allies/setright", params, UpdateManager.UPDATE_CALLBACK);
		}
	}
}
}

public void onClick(Widget sender) {
if (sender == memberPromoteBt) {
	// Promotion
	if (currentAction != null && currentAction.isPending())
		return;
	
	MemberUI memberUI = (MemberUI) messagesList.getSelectedItem();
	
	if (memberUI != null) {
		AllyMemberData member = memberUI.getData();
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(member.getId()));
		params.put("rank", String.valueOf(member.getRank() + 1));
		params.put("update", String.valueOf(lastUpdate));
		
		currentAction = new Action("allies/setrank", params,
				UpdateManager.UPDATE_CALLBACK);
	}
}
else if (sender==allReadBt){
	//Marquage de tous les messages de news comme étant lus
	JSOptionPane.showMessageDialog(
			"Marquer tous les messages comme lu?", "Confirmation",
			JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
			JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
				public void optionSelected(Object option) {
					currentAction = new Action("allies/setallnewsread", Action.NO_PARAMETERS,
							UpdateManager.UPDATE_CALLBACK);
					setVisible(true);
					unreadNews=0;
					Client.getInstance().getToolBar().blinkAlly(false);
				}
				});
}
else if (sender==deleteBt){
	// Suppression d'une news
	if (currentAction != null && currentAction.isPending())
		return;
	
	if (messagesList.getSelectedIndex() != -1) {
		JSOptionPane.showMessageDialog(
				"Voulez vous vraiment supprimer le sujet?", "Confirmation",
				JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.OK_OPTION) {
							
							AllyNewsData[] news = ((NewsUI) messagesList.getSelectedItem()).getData();
						
							HashMap<String, String> params =
								new HashMap<String, String>();
							params.put("news", String.valueOf(news[0].getId()));
							
							currentAction = new Action("allies/deletenews", params,
									UpdateManager.UPDATE_CALLBACK);
							
		//					messagesList.remove(
			//						messagesList.getSelectedIndex());
							messagesList.setSelectedIndex(-1);
							setVisible(true); //Force la mise à jour des messages
						}
					}
				}
				);
	}
	
	
}

 else if (sender==stickBt){
		// Epingle une news
		if (currentAction != null && currentAction.isPending())
			return;
		
		if (messagesList.getSelectedIndex() != -1) {
			JSOptionPane.showMessageDialog(
					"Voulez vous vraiment epingler le sujet?", "Confirmation",
					JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.OK_OPTION) {
								
								AllyNewsData[] news =
									((NewsUI) messagesList.getSelectedItem()).getData();
								
								HashMap<String, String> params =
									new HashMap<String, String>();
								params.put("news", String.valueOf(news[0].getId()));
								
								currentAction = new Action("allies/sticknews", params,
										UpdateManager.UPDATE_CALLBACK);
								
							}
						}
					}
					);
		}
		
 } else if (sender == memberDestituteBt) {
	// Destitution
	if (currentAction != null && currentAction.isPending())
		return;
	
	MemberUI memberUI = (MemberUI) messagesList.getSelectedItem();
	
	if (memberUI != null) {
		AllyMemberData member = memberUI.getData();
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(member.getId()));
		params.put("rank", String.valueOf(member.getRank() - 1));
		params.put("update", String.valueOf(lastUpdate));
		
		currentAction = new Action("allies/setrank", params,
				UpdateManager.UPDATE_CALLBACK);
	}
} else if (sender == memberKickBt) {
	// Ejection
	if (currentAction != null && currentAction.isPending())
		return;
	
	JSOptionPane.showMessageDialog(
		"Voulez-vous vraiment éjecter ce joueur ?",
		"Confirmation",
		JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
		JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
			public void optionSelected(Object option) {
				if ((Integer) option == JSOptionPane.YES_OPTION) {
					MemberUI memberUI = (MemberUI) messagesList.getSelectedItem();
					
					if (memberUI != null) {
						AllyMemberData member = memberUI.getData();
						
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("id", String.valueOf(member.getId()));
						params.put("update", String.valueOf(lastUpdate));
						
						currentAction = new Action("allies/kick", params,
							UpdateManager.UPDATE_CALLBACK);
					}
				}
			}
	});
} else if (sender == memberVoteKickBt) {
	// Vote pour l'éjection
	if (currentAction != null && currentAction.isPending())
		return;
	
	JSOptionPane.showMessageDialog(
			"Votez oui ou non pour l'éjection de ce joueur ?",
			"Vote",
			JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
			JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
				public void optionSelected(Object option) {
					if ((Integer) option == JSOptionPane.YES_OPTION) {
						MemberUI memberUI = (MemberUI) messagesList.getSelectedItem();
						
						if (memberUI != null) {
							AllyMemberData member = memberUI.getData();
							
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("id", String.valueOf(member.getId()));
							params.put("vote", "yes");
							
							currentAction = new Action("allies/votekick", params, UpdateManager.UPDATE_CALLBACK);
						}
					}
					
					if ((Integer) option == JSOptionPane.NO_OPTION) {
						MemberUI memberUI = (MemberUI) messagesList.getSelectedItem();
						
						if (memberUI != null) {
							AllyMemberData member = memberUI.getData();
							
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("id", String.valueOf(member.getId()));
							params.put("vote", "no");
							
							currentAction = new Action("allies/votekick", params, UpdateManager.UPDATE_CALLBACK);
						}
					}
				}
		});
	
	
} else if (sender == electBt) {
	// Vote pour l'éjection
	if (currentAction != null && currentAction.isPending())
		return;
	
	JSOptionPane.showMessageDialog(
			"Voulez-vous vraiment voter pour ce candidat?",
			"Elections",
			JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
			JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
				public void optionSelected(Object option) {
					if ((Integer) option == JSOptionPane.YES_OPTION) {
						MemberUI memberUI = (MemberUI) messagesList.getSelectedItem();
						
						if (memberUI != null) {
							AllyMemberData member = memberUI.getData();
							
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("id", String.valueOf(member.getId()));
							
							currentAction = new Action("allies/elect", params, UpdateManager.UPDATE_CALLBACK);
						}
					}
				}
		});

}

else if (sender == delegateBt) {
	// Déléguer son rang de leader
	if (currentAction != null && currentAction.isPending())
		return;
	
	JSOptionPane.showMessageDialog(
			"Voulez-vous vraiment donner votre place de leader?",
			"Délégation",
			JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
			JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
				public void optionSelected(Object option) {
					if ((Integer) option == JSOptionPane.YES_OPTION) {
						MemberUI memberUI = (MemberUI) messagesList.getSelectedItem();
						
						if (memberUI != null) {
							AllyMemberData member = memberUI.getData();
							
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("target", String.valueOf(member.getId()));
							
							currentAction = new Action("allies/delegate", params, UpdateManager.UPDATE_CALLBACK);
						}
					}
				}
		});

}
 	else if (sender == writeBt) {
	// Poster une news
	WriteMessageDialog dialog = new WriteMessageDialog(
			WriteMessageDialog.TYPE_NEWS,
			lastUpdate,
			WriteMessageDialog.OPTION_TITLE, "", 0);
	dialog.setVisible(true);
	this.setVisible(true);
} else if (sender == answerBt) {
	// Répondre à une news
	AllyNewsData[] news =
		((NewsUI) messagesList.getSelectedItem()).getData();
	
	WriteMessageDialog dialog = new WriteMessageDialog(
			WriteMessageDialog.TYPE_NEWS,
			lastUpdate,
			WriteMessageDialog.OPTION_TITLE,
			news[news.length - 1].getTitle(),
			news[0].getId());
	dialog.setVisible(true);
	this.setVisible(true);
} else if (sender == closeBt) {
	// Fermer l'item affiché
	messagesList.setSelectedIndex(-1);
} else if (sender == exitBt) {
	// Quitter l'alliance
	if (currentAction != null && currentAction.isPending())
		return;
	
	JSOptionPane.showMessageDialog(
			"Voulez-vous vraiment quitter votre alliance ? Vous ne pourrez pas en rejoindre d'autres avant 1 semaine.",
			"Confirmation",
			JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
			JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
				public void optionSelected(Object option) {
					if ((Integer) option == JSOptionPane.YES_OPTION) {
						currentAction = new Action("allies/leave", Action.NO_PARAMETERS,
								UpdateManager.UPDATE_CALLBACK);
					}
				}
		});
	
	
} else if (sender == applicantAcceptBt) {
	// Accepter un postulant
	if (currentAction != null && currentAction.isPending())
		return;
	
	HashMap<String, String> params = new HashMap<String, String>();
	params.put("id", String.valueOf(((NewsUI)
		messagesList.getSelectedItem()).getData()[0].getIdApplicant()));
	params.put("update", String.valueOf(lastUpdate));
	
	currentAction = new Action("allies/accept", params,
			UpdateManager.UPDATE_CALLBACK);
} else if (sender == applicantDeclineBt) {
	// Refuser un postulant
	if (currentAction != null && currentAction.isPending())
		return;
	
	HashMap<String, String> params = new HashMap<String, String>();
	params.put("id", String.valueOf(((NewsUI)
		messagesList.getSelectedItem()).getData()[0].getIdApplicant()));
	params.put("update", String.valueOf(lastUpdate));
	
	currentAction = new Action("allies/decline", params,
			UpdateManager.UPDATE_CALLBACK);
	
} else if(sender == applicantVoteBt) {
	
	if (currentAction != null && currentAction.isPending())
		return;
	
	JSOptionPane.showMessageDialog(
			"Votez oui ou non pour la candidature de ce joueur ?",
			"Vote",
			JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
			JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
				public void optionSelected(Object option) {
					if ((Integer) option == JSOptionPane.YES_OPTION) {
							
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("id", String.valueOf(((NewsUI)
									messagesList.getSelectedItem()).getData()[0].getIdApplicant()));
								//params.put("update", String.valueOf(lastUpdate));
							params.put("vote", "yes");
							
							currentAction = new Action("allies/voteaccept", params, UpdateManager.UPDATE_CALLBACK);
						
					}
					
					if ((Integer) option == JSOptionPane.NO_OPTION) {

							HashMap<String, String> params = new HashMap<String, String>();
							params.put("id", String.valueOf(((NewsUI)
									messagesList.getSelectedItem()).getData()[0].getIdApplicant()));
							params.put("vote", "no");
							
							currentAction = new Action("allies/voteaccept", params, UpdateManager.UPDATE_CALLBACK);		
					}
				}
		});
	
} else if (sender == nextPageBt) {
	currentPage++;
	updateUI();
} else if (sender == previousPageBt) {
	currentPage--;
	updateUI();
} else if (sender == updateDescriptionBt) {
	AllyDescriptionDialog dialog = new AllyDescriptionDialog(
			data.getDescription(), lastUpdate);
	dialog.setVisible(true);
}
}

public void onFailure(String error) {
ActionCallbackAdapter.onFailureDefaultBehavior(error);
}

public void setVisible(boolean visible) {
if (visible) {
	// Télécharge les dernières données sur l'alliance
	if (downloadAction != null && downloadAction.isPending())
		return;
	
	super.setVisible(visible);
	
	HashMap<String, String> params = new HashMap<String, String>();
	//params.put("update", String.valueOf(lastUpdate));
	params.put("update", String.valueOf(0)); //Permet de recevoir tous les messages de l'alliance
	
	downloadAction = new Action("allies/getplayerally", params, this);
} else {
	super.setVisible(visible);
}
}

public void onSuccess(AnswerData data) {
setAlly(data.getAlly());
}

public int getPlayerRank() {
if (data.getId() == 0)
	return -1;

// Recherche le rang du joueur
for (int i = 0; i < data.getMembersCount(); i++)
	if (data.getMemberAt(i).getId() == Settings.getPlayerId()) {
		return data.getMemberAt(i).getRank();
	}
return -1;
}

public boolean isPlayerLeader() {
return data.getId() != 0 &&
	getPlayerRank() == getLeaderRank(data.getOrganization());
}

public AllyData getAlly() {
return data;
}

@SuppressWarnings("unchecked")
public void setAlly(AllyData ally) {
this.data = ally;
this.lastUpdate = (long) ally.getLastUpdate();

DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);

if (ally.getId() != 0) {
	// Le joueur est membre d'une alliance
	ArrayList<MemberUI> membersList = new ArrayList<MemberUI>();
	for (int i = 0; i < ally.getMembersCount(); i++)
		membersList.add(new MemberUI(ally.getMemberAt(i)));
	
	Collections.sort(membersList,
		MEMBERS_COMPARATORS[membersComparatorComboBox.getSelectedIndex()]);
	
	this.membersList = membersList;
	
	// Nouvelles news
	ArrayList<AllyNewsData> tmpNewsList = new ArrayList<AllyNewsData>();
	for (int i = 0; i < ally.getNewsCount(); i++)
		tmpNewsList.add(ally.getNewsAt(i));
/*	
	// Ajoute les news existantes
	for (NewsUI newsUI : this.newsList)
		for (AllyNewsData newsData : newsUI.getData())
			tmpNewsList.add(newsData);
	*/
	// Tri les news par date (du plus vieux au plus récent)
	Collections.sort(tmpNewsList, new Comparator<AllyNewsData>() {
		public int compare(AllyNewsData n1, AllyNewsData n2) {
			if (n1.getDate() < n2.getDate())
				return -1;
			if (n1.getDate() == n2.getDate())
				return 0;
			return 1;
		}
	});
	
	// Regroupe les news en topics
	ArrayList<NewsUI> newsList = new ArrayList<NewsUI>();
	for (AllyNewsData news : tmpNewsList) {
		if (news.getParent() == 0) {
			newsList.add(new NewsUI(news));
		} else {
			for (NewsUI newsUI : newsList) {
				if (newsUI.getData()[0].getId() == news.getParent()) {
					newsUI.addNews(news);
					
					break;
				}
			}
		}
	}
	
	// Tri les news par date (du plus récent au plus vieux)
	Collections.sort(newsList, new Comparator<NewsUI>() {
		public int compare(NewsUI n1, NewsUI n2) {
			if (n1.getData()[n1.getData().length - 1].getDate() >
				n2.getData()[n2.getData().length - 1].getDate())
				return -1;
			if (n1.getData()[n1.getData().length - 1].getDate() ==
				n2.getData()[n2.getData().length - 1].getDate())
				return 0;
			return 1;
		}
	});
	
	this.newsList = newsList;
	
	descriptionPanel.getElement().setInnerHTML(ally.getDescription());
	
	for (int i = 0; i < settingsRankComboBox.length; i++) {
		ArrayList<String> ranks = new ArrayList<String>();
		int leaderRank = getLeaderRank(ally.getOrganization());
		if (getPlayerRank() == leaderRank) {
			for (int j = 1; j <= leaderRank; j++)
				ranks.add(dynamicMessages.getString(
					data.getOrganization() + "Rank" + j));
		} else {
			ranks.add(dynamicMessages.getString(
				data.getOrganization() + "Rank" +
				ally.getRequiredRank(RIGHTS_SETTINGS[i])));
		}
		
		settingsRankComboBox[i].removeSelectionListener(this);
		settingsRankComboBox[i].setItems(ranks);
		settingsRankComboBox[i].setSelectedIndex(ally.getRequiredRank(RIGHTS_SETTINGS[i]) - 1);
		settingsRankComboBox[i].addSelectionListener(this);
	}
	
	ArrayList<String> colors = new ArrayList<String>();
	int leaderRank = getLeaderRank(ally.getOrganization());
	if (getPlayerRank() == leaderRank) {
		for (int i = 0; i < TERRITORY_COLORS.length; i++)
			colors.add("<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
				"style=\"height: 12px; width: 80px; vertical-align: middle; background-color: " +
				TERRITORY_COLORS[i] + ";\"/>&nbsp;");
	} else {
		colors.add("<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" " +
			"style=\"height: 12px; width: 80px; vertical-align: middle; background-color: " +
			TERRITORY_COLORS[ally.getTerritoryColor()] + ";\"/>&nbsp;");
	}
	
	colorsComboBox.removeSelectionListener(this);
	colorsComboBox.setItems(colors);
	colorsComboBox.setSelectedIndex(ally.getTerritoryColor());
	colorsComboBox.addSelectionListener(this);
}

if (isVisible() || applicantDialog.isVisible() ||
		noAllyDialog.isVisible()) {
	if (ally.getId() == 0) {
		if (ally.isApplicant()) {
			// Le joueur est en train de postuler pour une alliance
			applicantDialog.setParameters(ally.getName(),
					ally.isApplicationVoted());
			
			super.setVisible(false);
			noAllyDialog.setVisible(false);
			applicantDialog.setVisible(true);
		} else {
			// Le joueur n'a pas d'alliance
			super.setVisible(false);
			applicantDialog.setVisible(false);
			noAllyDialog.setVisible(true);
		}
	} else {
		// Le joueur est membre d'une alliance
		updateUI();
		noAllyDialog.setVisible(false);
		applicantDialog.setVisible(false);
		super.setVisible(true);
	}
	Client.getInstance().getToolBar().setAllyBtSelected(true);
}
}

public void dialogClosed(Widget sender) {
Client.getInstance().getToolBar().setAllyBtSelected(false);
}

public boolean isActive() {
return isVisible() || applicantDialog.isVisible() ||
	noAllyDialog.isVisible();
}

public void setActive(boolean active) {
if (active) {
	if (data.getId() == 0) {
		if (data.isApplicant()) {
			// Le joueur est en train de postuler pour une alliance
			applicantDialog.setParameters(data.getName(),
					data.isApplicationVoted());
			
			setVisible(false);
			noAllyDialog.setVisible(false);
			applicantDialog.setVisible(true);
		} else {
			// Le joueur n'a pas d'alliance
			setVisible(false);
			applicantDialog.setVisible(false);
			noAllyDialog.setVisible(true);
		}
	} else {
		// Le joueur est membre d'une alliance
		noAllyDialog.setVisible(false);
		applicantDialog.setVisible(false);
		setVisible(true);
	}
} else {
	if (isVisible())
		setVisible(false);
	if (applicantDialog.isVisible())
		applicantDialog.setVisible(false);
	if (noAllyDialog.isVisible())
		noAllyDialog.setVisible(false);
}
}

public static int getLeaderRank(String organization) {
if (organization.equals("tyranny"))
	return 3;
if (organization.equals("warmonger"))
	return 4;
if (organization.equals("democracy"))
	return 3;
if (organization.equals("oligarchy"))
	return 3;
if (organization.equals("anarchy"))
	return 1;
else
	return 5;
}

public int getUnreadNews() {
return unreadNews;
}

public void setUnreadNews(int unreadNews) {
this.unreadNews = unreadNews;

if (unreadNews > 0)
	Client.getInstance().getToolBar().blinkAlly(true);

if (isVisible()) {
	setVisible(true);
	if (downloadAction != null && downloadAction.isPending())
		return;
	
	HashMap<String, String> params = new HashMap<String, String>();
	params.put("update", String.valueOf(lastUpdate));
	
	downloadAction = new Action("allies/getplayerally", params, this);
}
}

public void onWindowResized(int width, int height) {
int clientWidth = Window.getClientWidth();
highres = clientWidth > 1024;

if (currentClientWidth > 1024 && clientWidth <= 1024) {
	viewsPane.setPixelWidth(550);
	messagesList.setPixelSize(550,
		(messagesList.getSelectedIndex() != -1 ? 70 : 280));
	messageScrollPane.setPixelSize(550, 182);
	horizontalSeparator.setWidth("550px");
	separator.setPixelWidth(310 -
			writeBt.getPixelWidth() -
			allReadBt.getPixelWidth());
	descriptionScrollPane.setPixelSize(550, 280);
	productsScrollPane.setPixelSize(550, 280);
	settingsLayout.setPixelSize(550, 280);
	separator2.setPixelWidth(185);
	
	layout.update();
	
	updateUI();
	
	if (isVisible())
		centerOnScreen();
} else if (currentClientWidth <= 1024 && clientWidth > 1024) {
	viewsPane.setPixelWidth(550 + HIGH_RES_EXTRA_WIDTH);
	messagesList.setPixelSize(
		550 + HIGH_RES_EXTRA_WIDTH,
		(messagesList.getSelectedIndex() != -1 ?
		70 + (highres ? HIGH_RES_EXTRA_HEIGHT / 2 : 0) :
		280 + (highres ? HIGH_RES_EXTRA_HEIGHT : 0)));
	messageScrollPane.setPixelSize(
		550 + HIGH_RES_EXTRA_WIDTH,
		182 + (highres ? HIGH_RES_EXTRA_HEIGHT / 2 : 0));
	horizontalSeparator.setWidth((550 + HIGH_RES_EXTRA_WIDTH) + "px");
	separator.setPixelWidth(310 + HIGH_RES_EXTRA_WIDTH -
			writeBt.getPixelWidth() -
			allReadBt.getPixelWidth());
	descriptionScrollPane.setPixelSize(
		550 + HIGH_RES_EXTRA_WIDTH,
		280 + HIGH_RES_EXTRA_HEIGHT);
	productsScrollPane.setPixelSize(
		550 + HIGH_RES_EXTRA_WIDTH,
		280 + HIGH_RES_EXTRA_HEIGHT);
	settingsLayout.setPixelSize(
		550 + HIGH_RES_EXTRA_WIDTH,
		280 + HIGH_RES_EXTRA_HEIGHT);
	separator2.setPixelWidth(185 + HIGH_RES_EXTRA_WIDTH / 2);
	
	layout.update();
	
	updateUI();
	
	if (isVisible())
		centerOnScreen();
}

currentClientWidth = clientWidth;
}

public void onProductsChanged(HashMap<Integer, Integer> newProducts) {
DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);

String content = "<div style=\"padding: 0 20px;\">";

// Produits
for (int product : newProducts.keySet()) {
	int count = newProducts.get(product);
	content += "<div class=\"title\">" + count + "x <img src=\"" +
		Config.getMediaUrl() + "images/misc/blank.gif\" " +
		"class=\"product\" style=\"background-position: -" +
		(16 * product) + "px -2718px;\"/> " +
		dynamicMessages.getString("product" + product) + "</div>" +
		"<div class=\"justify small\">" +
		ProductData.getDesc(product, count) + "</div>" +
		"<div>&nbsp;</div>";
}

if (newProducts.size() == 0)
	content += "Votre alliance ne dispose d'aucun produit.";

// Combinaisons de produits
for (int i = 0; i < ShipData.SHIPS.length; i++) {
	if (ShipData.SHIPS[i] != null) {
		ProductData[] requiredProducts = ShipData.SHIPS[i].getRequiredProducts();
		
		if (requiredProducts.length > 0) {
			boolean available = true;
			
			for (ProductData requiredProduct : requiredProducts) {
				Integer productCount = newProducts.get(requiredProduct.getType());
				
				if (productCount == null || productCount < requiredProduct.getCount()) {
					available = false;
					break;
				}
			}
			
			if (available) {
				content += "<div class=\"title\">";
				boolean first = true;
				for (ProductData requiredProduct : requiredProducts) {
					if (first)
						first = false;
					else
						content += " + ";
					content += requiredProduct.getCount() + "x <img src=\"" +
						Config.getMediaUrl() + "images/misc/blank.gif\" " +
						"class=\"product\" style=\"background-position: -" +
						(16 * requiredProduct.getType()) + "px -2718px;\"/> " +
						dynamicMessages.getString("product" +
						requiredProduct.getType());
				}
				content += "</div>";
				content += "<div class=\"justify small\">" +
					"Nouveau vaisseau : <span class=\"emphasize\">" +
					dynamicMessages.getString("ship" + i) + "</span>";
				
				int[] requiredTechnologies = ShipData.SHIPS[i].getTechnologies();
				
				if (requiredTechnologies.length > 0) {
					content += " (Technologies requises : ";
					
					first = true;
					for (int requiredTechnology : requiredTechnologies) {
						if (first)
							first = false;
						else
							content += ",";
						content += TechnologyData.getTechnologyById(
							requiredTechnology).getName();
					}
					
					content += ")";
				}
				
				content += "</div><div>&nbsp;</div>";
			}
		}
	}
}

content += "</div>";

productsPanel.getElement().setInnerHTML(content);
productsScrollPane.update();
}

// ------------------------------------------------- METHODES PRIVEES -- //

private ArrayList<?> getCurrentItems() {
switch (viewsPane.getSelectedIndex()) {
case VIEW_NEWS:
	return newsList;
case VIEW_MEMBERS:
	return membersList;
default:
	return null;
}
}

private void updateUI() {
int view = viewsPane.getSelectedIndex();

switch (view) {
case VIEW_NEWS:
case VIEW_MEMBERS:
	Object selectedItem = messagesList.getSelectedItem();
	
	ArrayList<?> items = getCurrentItems();
	
	if (items.size() > ITEMS_PER_PAGE) {
		int maxPage = (int) Math.ceil(
				items.size() / (double) ITEMS_PER_PAGE);
		pageLabel.setText("<b>" + (currentPage + 1) + "</b> / " + maxPage);
		previousPageBt.setVisible(true);
		pageLabel.setVisible(true);
		nextPageBt.setVisible(true);
		
		previousPageBt.getElement().getStyle().setProperty(
				"visibility", currentPage == 0 ? "hidden" : "");
		nextPageBt.getElement().getStyle().setProperty(
				"visibility", currentPage == maxPage - 1 ? "hidden" : "");
	} else {
		previousPageBt.setVisible(false);
		pageLabel.setVisible(false);
		nextPageBt.setVisible(false);
	}
	
	layout.update();
	
	int selectedIndex = -1;
	ArrayList<Object> itemsUI = new ArrayList<Object>();
	int limit = Math.min(items.size(), (currentPage + 1) * ITEMS_PER_PAGE);
	for (int i = currentPage * ITEMS_PER_PAGE; i < limit; i++) {
		switch (view) {
		case VIEW_NEWS:
			itemsUI.add(items.get(i));
			
			if (selectedItem != null &&
					((NewsUI) selectedItem).getData()[0].getId() ==
						((NewsUI) items.get(i)).getData()[0].getId())
				selectedIndex = i;
			break;
		case VIEW_MEMBERS:
			itemsUI.add(items.get(i));

			if (selectedItem != null &&
					((MemberUI) selectedItem).getData().getId() ==
						((MemberUI) items.get(i)).getData().getId())
			break;
		}
	}
	
	messagesList.setItems(itemsUI);
	messagesList.setSelectedIndex(selectedIndex);
	messagesList.getScrollPane().scrollUp(9999);
	break;
}
}

private class NewsUI {
// --------------------------------------------------- CONSTANTES -- //
// ---------------------------------------------------- ATTRIBUTS -- //

private AllyNewsData[] data;

// ------------------------------------------------ CONSTRUCTEURS -- //

public NewsUI(AllyNewsData data) {
	this.data = new AllyNewsData[]{data};
}

public NewsUI(AllyNewsData[] data) {
	this.data = data;
}

// ----------------------------------------------------- METHODES -- //

public void addNews(AllyNewsData data) {
	AllyNewsData[] newData = new AllyNewsData[this.data.length + 1];
	System.arraycopy(this.data, 0, newData, 0, this.data.length);
	newData[this.data.length] = data;
	this.data = newData;
}

public AllyNewsData[] getData() {
	return data;
}

public String toString() {
	StaticMessages messages =
		(StaticMessages) GWT.create(StaticMessages.class);
	
	long now = Utilities.getCurrentTime();
	
	// Affiche l'heure quand le message a été envoyé dans la journée
	String date;
	if ((int) Math.floor(now / (3600 * 24)) ==
			(int) Math.floor(data[data.length - 1].getDate() / (3600 * 24)))
		date = DateTimeFormat.getFormat(messages.timeFormat()
			).format(new Date((long) (1000 * data[data.length - 1].getDate())));
	else
		date = DateTimeFormat.getFormat(messages.dateFormat()
			).format(new Date((long) (1000 * data[data.length - 1].getDate())));
	
	return "<table " + (!data[data.length - 1].isRead() ? "class=\"unread\" " : "") +
		"unselectable=\"on\" cellspacing=\"0\">" +
		"<tr unselectable=\"on\">" +
		"<td unselectable=\"on\" style=\"width:  20px;\">" +
		(data[0].isSticky() ? "<div class=\"bookmark\"></div>" : "") +
		"</td><td " + (data[0].hasAuthor() ? "class=\"owner-" +
		data[0].getTreaty() + "\" " : "") + "unselectable=\"on\" " +
		"style=\"width: " + (140 + (highres ? 60 : 0)) +
		"px;\">" + (data[0].hasAuthor() ?
		(data[0].hasAllyTag() ? "<span unselectable=\"on\" class=\"allyTag\">[" +
		data[0].getAllyTag() + "]</span> " : "") + data[0].getAuthor() : "-") +
		"</td><td unselectable=\"on\" style=\"width: " +
		(300 + (highres ? HIGH_RES_EXTRA_WIDTH - 80 : 0)) + "px;\">" +
		Utilities.parseSmilies(data[0].getTitle()) + (data.length > 1 ?
		"<span class=\"deemphasize\"> - " + (data.length - 1) +
		" réponse" + (data.length > 2 ? "s" : "") + "</span>" : "") +
		"</td><td class=\"center\" unselectable=\"on\" " +
		"style=\"width: " + (130 + (highres ? 20 : 0)) +
		"px;\">" + date + "</td></tr></table>";
}

// --------------------------------------------- METHODES PRIVEES -- //
}

private class MemberUI {
// --------------------------------------------------- CONSTANTES -- //
// ---------------------------------------------------- ATTRIBUTS -- //

private AllyMemberData data;

// ------------------------------------------------ CONSTRUCTEURS -- //

public MemberUI(AllyMemberData data) {
	this.data = data;
}

// ----------------------------------------------------- METHODES -- //

public AllyMemberData getData() {
	return data;
}

@Override
public String toString() {
	String status;
	if (data.getIdleTime() == 0)
		status = "<span style=\"color: #00c000;\">Actif</span>";
	else if (data.getIdleTime() < 14)
		status = "<span style=\"color: #ffd200;\">Inactif depuis " +
				data.getIdleTime() + " jours</span>";
	else
		status = "<span style=\"color: #ff0000;\">Inactif depuis " +
				(data.getIdleTime() / 7) + " semaines</span>";
	
	String rank = "";
	for (int i = 0; i < data.getRank(); i++)
		rank += "<img src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" class=\"allyRank\"/>";
	
	return "<table unselectable=\"on\" cellspacing=\"0\">" +
		"<tr unselectable=\"on\">" +
		"<td unselectable=\"on\" style=\"width:  20px;\"></td>" +
		"<td unselectable=\"on\" style=\"width: " +
		(260 + (highres ? 80 : 0)) + "px;\">" +
		data.getLogin() + "</td>" +
		"<td unselectable=\"on\" style=\"width: " +
		(200 + (highres ? HIGH_RES_EXTRA_WIDTH - 80 : 0)) + "px;\">" +
		status + "</td><td class=\"right\" " +
		"unselectable=\"on\" style=\"width: 70px;\">" +
		rank + "&nbsp;</td></tr></table>";
}

// --------------------------------------------- METHODES PRIVEES -- //
}
}
