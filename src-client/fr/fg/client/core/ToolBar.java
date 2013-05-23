/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc, Romain Prevot, Thierry Chevalier

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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.animation.ButtonIconBlinkUpdater;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.data.AnswerData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.EventManager;
import fr.fg.client.openjwt.ui.DialogListener;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.OptionPaneListener;
import fr.fg.client.core.Client;
import fr.fg.client.core.GalaxyMap;
import fr.fg.client.core.Utilities;

@SuppressWarnings("deprecation")
public class ToolBar extends AbsolutePanel implements ClickListener,
EventPreview, DialogListener {
// ------------------------------------------------------- CONSTANTES -- //

private final static int
MESSENGER = 0,
LADDER = 1,
EVENTS = 2,
ALLY = 3,
DIPLOMACY = 4,
CONTRACTS = 5,
TACTICS = 6,
HELP = 7;

// -------------------------------------------------------- ATTRIBUTS -- //

private boolean[] listeners;

private JSButton contractsBt, optionsBt, helpBt, allyBt, eventsBt,
researchBt, diplomacyBt, mapBt, messagesBt, ladderBt, menuBt,
disconnectBt, premiumBt, contactsBt, achievementsBt, advancementsBt,
tacticsBt, forumBt, bugReportBt;

private ButtonIconBlinkUpdater messagesBlink, eventsBlink, allyBlink,
researchBlink;

private JSDialog menuDialog;

private boolean disconnectDialogVisible;

private long lastEnterPressedDate;

// ---------------------------------------------------- CONSTRUCTEURS -- //

public ToolBar() {
this.disconnectDialogVisible = false;
this.listeners = new boolean[10];
this.lastEnterPressedDate = 0;

getElement().setId("toolbar");
getElement().setAttribute("unselectable", "on");

StaticMessages messages =
	(StaticMessages) GWT.create(StaticMessages.class);

// Premium
premiumBt = new JSButton("Premium");
premiumBt.setPixelWidth(140);
premiumBt.addClickListener(this);

// Contacts
contactsBt = new JSButton("Contacts");
contactsBt.setPixelWidth(140);
contactsBt.addClickListener(this);
contactsBt.setToolTipText(
	"<div class=\"title\">Contacts</div>" +
	"<div>" + messages.shortcut(
	"<span class=\"emphasize\">F</span>") + "</div>");

// Civilisation
advancementsBt = new JSButton("Civilisation");
advancementsBt.setPixelWidth(140);
advancementsBt.addClickListener(this);
advancementsBt.setToolTipText(
	"<div class=\"title\">Civilisation</div>" +
	"<div>" + messages.shortcut(
	"<span class=\"emphasize\">C</span>") + "</div>");

// Succès
achievementsBt = new JSButton("Trophées");
achievementsBt.setPixelWidth(140);
achievementsBt.addClickListener(this);
achievementsBt.setToolTipText(
	"<div class=\"title\">Trophées</div>" +
	"<div>" + messages.shortcut(
	"<span class=\"emphasize\">P</span>") + "</div>");

// Options
optionsBt = new JSButton(messages.options());
optionsBt.setPixelWidth(140);
optionsBt.addClickListener(this);
optionsBt.setToolTipText(
	"<div class=\"title\">" + messages.options() + "</div>" +
	"<div>" + messages.shortcut(
	"<span class=\"emphasize\">O</span>") + "</div>");

// Tactiques
if (Settings.isPremium()) {
	tacticsBt = new JSButton("Tactiques");
	tacticsBt.setPixelWidth(140);
	tacticsBt.addClickListener(this);
	tacticsBt.setToolTipText(
		"<div class=\"title\">Tactiques</div>" +
		"<div>" + messages.shortcut(
		"<span class=\"emphasize\">T</span>") + "</div>");
}

// Aide
helpBt = new JSButton("Aide");
helpBt.setPixelWidth(140);
helpBt.addClickListener(this);
helpBt.setToolTipText(
		"<div class=\"title\">Aide</div>" +
		"<div>" + messages.shortcut(
		"<span class=\"emphasize\">Ctrl+F1</span>") + "</div>");


// Déconnexion
disconnectBt = new JSButton(messages.exit());
disconnectBt.setPixelWidth(140);
disconnectBt.addClickListener(this);
disconnectBt.setToolTipText(
	"<div class=\"title\">" + messages.exit() + "</div>" +
	"<div>" + messages.shortcut(
	"<span class=\"emphasize\">Q</span>") + "</div>");

// Forum
forumBt = new JSButton("Forum");
forumBt.setPixelWidth(140);
forumBt.addClickListener(this);

//Signalement de bug
bugReportBt = new JSButton("Signaler un bug");
bugReportBt.setPixelWidth(140);
bugReportBt.addClickListener(this);

// Mise en forme du menu
JSRowLayout layout = new JSRowLayout();
layout.addRowSeparator(10);
layout.addComponent(JSRowLayout.createHorizontalSeparator(10));
layout.addComponent(premiumBt);
layout.addComponent(JSRowLayout.createHorizontalSeparator(10));
layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
layout.addRow();
if (Settings.isPremium()) {
	layout.addComponent(tacticsBt);
	layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
	layout.addRow();
}
layout.addComponent(advancementsBt);
layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
layout.addRow();
layout.addComponent(achievementsBt);
layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
layout.addRow();
layout.addComponent(contactsBt);
layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
layout.addRow();
layout.addComponent(optionsBt);
layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
layout.addRow();
layout.addComponent(helpBt);
layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
layout.addRow();
layout.addComponent(forumBt);
layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
layout.addRow();
layout.addComponent(bugReportBt);
layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
layout.addRow();
layout.addComponent(disconnectBt);
layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
layout.addRow();
layout.addRowSeparator(10);

// Menu du jeu
menuDialog = new JSDialog("<img src=\"" + Config.getMediaUrl() +
		"images/misc/blank.gif\" class=\"iconMenu\"/> " +
		"Menu", false, true, true);
menuDialog.setComponent(layout);
menuDialog.setLocation(35, 55, false);
menuDialog.addDialogListener(this);

// Contrats
contractsBt = new JSButton();
contractsBt.addStyleName("iconContracts");
contractsBt.setToolTipText("<div class=\"title\">" + "Missions" + "</div>" +
		"<div>" + messages.shortcut("<span class=\"emphasize\">I</span>") + "</div>");
contractsBt.setPixelSize(24, 23);
contractsBt.addClickListener(this);

// Alliance du joueur
allyBt = new JSButton();
allyBt.addStyleName("iconAlly");
allyBt.setToolTipText("<div class=\"title\">" + messages.ally() + "</div>" +
		"<div>" + messages.shortcut("<span class=\"emphasize\">A</span>") + "</div>");
allyBt.setPixelSize(24, 23);
allyBt.addClickListener(this);
allyBlink = new ButtonIconBlinkUpdater(allyBt, 500, 500);

// Recherche
researchBt = new JSButton();
researchBt.addStyleName("iconResearch");
researchBt.setPixelSize(24, 23);
researchBt.setToolTipText("<div class=\"title\">" + messages.research() + "</div>" +
		"<div>" + messages.shortcut("<span class=\"emphasize\">H</span>") + "</div>");
researchBt.addClickListener(this);
researchBlink = new ButtonIconBlinkUpdater(researchBt, 500, 500);

// Diplomatie
diplomacyBt = new JSButton();
diplomacyBt.addStyleName("iconDiplomacy");
diplomacyBt.setPixelSize(24, 23);
diplomacyBt.setToolTipText("<div class=\"title\">" + messages.diplomacy() + "</div>" +
		"<div>" + messages.shortcut("<span class=\"emphasize\">D</span>") + "</div>");
diplomacyBt.addClickListener(this);

// Carte de la galaxie
mapBt = new JSButton();
mapBt.addStyleName("iconMap");
mapBt.setPixelSize(24, 23);
mapBt.setToolTipText("<div class=\"title\">" + messages.galaxyMap() + "</div>" +
		"<div>" + messages.shortcut("<span class=\"emphasize\">G</span>") + "</div>");
mapBt.addClickListener(this);

// Evenements
eventsBt = new JSButton();
eventsBt.addStyleName("iconEvents");
eventsBt.setPixelSize(24, 23);
eventsBt.setToolTipText("<div class=\"title\">" + messages.events() + "</div>" +
		"<div>" + messages.shortcut("<span class=\"emphasize\">E</span>") + "</div>");
eventsBt.addClickListener(this);
eventsBlink = new ButtonIconBlinkUpdater(eventsBt, 500, 500);

// Messages reçus
messagesBt = new JSButton();
messagesBt.addStyleName("iconMessages");
messagesBt.setPixelSize(24, 23);
messagesBt.setToolTipText("<div class=\"title\">" + messages.messages() + "</div>" +
		"<div>" + messages.shortcut("<span class=\"emphasize\">M</span>") + "</div>");
messagesBt.addClickListener(this);
messagesBlink = new ButtonIconBlinkUpdater(messagesBt, 500, 500);

// Classement
ladderBt = new JSButton();
ladderBt.addStyleName("iconLadder");
ladderBt.setPixelSize(24, 23);
ladderBt.setToolTipText("<div class=\"title\">" + messages.ladder() + "</div>" +
		"<div>" + messages.shortcut("<span class=\"emphasize\">L</span>") + "</div>");
ladderBt.addClickListener(this);

// Menu
menuBt = new JSButton();
menuBt.addStyleName("iconMenu");
menuBt.setPixelSize(24, 23);
menuBt.setToolTipText("<div class=\"title\">" + messages.menu() + "</div>" +
		"<div>" + messages.shortcut("<span class=\"emphasize\">N</span>") + "</div>");
menuBt.addClickListener(this);

// Barre d'outils
add(contractsBt);
add(allyBt);
add(researchBt);
add(diplomacyBt);
add(mapBt);
add(eventsBt);
add(messagesBt);
add(ladderBt);
add(menuBt);

EventManager.addEventHook(this);
}

// --------------------------------------------------------- METHODES -- //

public void onClick(Widget sender) {
	if (sender == ladderBt) {
		onLadderBtClick();
	} else if (sender == messagesBt) {
		onMessageBtClick();
	} else if (sender == menuBt) {
		onMenuBtClick();
	} else if (sender == helpBt) {
		//Window.open("http://wiki.fallengalaxy.com/index.php/Accueil", "", "");
		onHelpBtClick();
	} else if (sender == mapBt) {
		onMapBtClick();
	} else if (sender == researchBt) {
		onResearchBtClick();
	} else if (sender == optionsBt) {
		onOptionsBtClick();
	} else if (sender == premiumBt) {
		onPremiumBtClick();
	} else if (sender == contactsBt) {
		onContactsBtClick();
	} else if (sender == achievementsBt) {
		onAchievementsBtClick();
	} else if (sender == advancementsBt) {
		onAdvancementsBtClick();
	} else if (sender == eventsBt) {
		onEventsBtClick();
	} else if (sender == disconnectBt) {
		onDisconnectBtClick();
	} else if (sender == allyBt) {
		onAllyBtClick();
	} else if (sender == diplomacyBt) {
		onDiplomacyClick();
	} else if (sender == contractsBt) {
		onContractsBtClick();
	} else if (sender == tacticsBt) {
		onTacticsBtClick();
	} else if (sender == forumBt) {
		Window.open("http://forum.fallengalaxy.com/", "", "");
	} else if( sender == bugReportBt) {
		onBugReportBtClick();
	}

}

public boolean onEventPreview(Event event) {
switch (event.getTypeInt()) {
case Event.ONKEYDOWN:
	Element target = DOM.eventGetTarget(event);
	
	if ((target != null && DOM.getElementProperty(
			target, "nodeName").toLowerCase().equals("input"))) //$NON-NLS-1$ //$NON-NLS-2$
		return true;
	
	if (Client.getInstance().isFullScreenMode())
		return true;
	
	if ( (event.getCtrlKey() || event.getAltKey() || event.getMetaKey()) && !(event.getKeyCode()==112 || event.getKeyCode()==119))
		return true;
	
	// BLoque les raccourcis pendant 3 secondes après que la touche
	// entrée ait été pressée
	int keyCode = event.getKeyCode();
	long now = Utilities.getCurrentTime();
	
	if (keyCode == 13 || keyCode == 3)
		lastEnterPressedDate = now;
	
	if (lastEnterPressedDate + 2 > now)
		return true;
	
	switch (keyCode) {
	case 65:
		onAllyBtClick();
		break;
	case 67:
		onAdvancementsBtClick();
		break;
	case 72:
		onResearchBtClick();
		break;
	case 68:
		onDiplomacyClick();
		break;
	case 70:
		onContactsBtClick();
		break;
	case 71:
		onMapBtClick();
		break;
	case 73:
		onContractsBtClick();
		break;
	case 69:
		onEventsBtClick();
		break;
	case 77:
		onMessageBtClick();
		break;
	case 76:
		onLadderBtClick();
		break;
	case 78:
		onMenuBtClick();
		break;
	case 79:
		onOptionsBtClick();
		break;
	case 80:
		onAchievementsBtClick();
		break;
	case 81:
		onDisconnectBtClick();
		break;
	case 84:
		onTacticsBtClick();
		break;
	case 112:
		if(event.getCtrlKey())
			onHelpBtClick();
		break;
	case 117:
		if (Settings.isAdministrator())
			onEditChangelogShortcutTriggered();
		break;
	case 119:
		if(event.getCtrlKey())
			onAdministrationPanelShortcutTriggered();
		break;
	}
	break;
}
return true;
}

public void dialogClosed(Widget sender) {
if (listeners[LADDER] && sender == Client.getInstance().getLadder()) {
	ladderBt.setSelected(false);
} else if (listeners[MESSENGER] && sender == Client.getInstance().getMessenger()) {
	messagesBt.setSelected(false);
} else if (listeners[EVENTS] && sender == Client.getInstance().getEventsDialog()) {
	eventsBt.setSelected(false);
} else if (listeners[ALLY] && sender == Client.getInstance().getAllyDialog()) {
	allyBt.setSelected(false);
} else if (sender == menuDialog) {
	menuBt.setSelected(false);
} else if (listeners[DIPLOMACY] && sender == Client.getInstance().getDiplomacyDialog()) {
	diplomacyBt.setSelected(false);
} else if (listeners[CONTRACTS] && sender == Client.getInstance().getContractDialog()) {
	contractsBt.setSelected(false);
} else if(listeners[HELP] && sender == Client.getInstance().getHelpDialog()) {
	helpBt.setSelected(false);
} else if(listeners[TACTICS] && sender == Client.getInstance().getTacticsDialog()) {
	tacticsBt.setSelected(false);
}
}

public void blinkMessages(boolean blink) {
if (blink) {
	TimerManager.register(messagesBlink);
} else {
	TimerManager.unregister(messagesBlink);
	messagesBt.removeStyleName("blinking");
}
}

public void blinkEvents(boolean blink) {
if (blink) {
	TimerManager.register(eventsBlink);
} else {
	TimerManager.unregister(eventsBlink);
	eventsBt.removeStyleName("blinking");
}
}

public void blinkAlly(boolean blink) {
if (blink) {
	TimerManager.register(allyBlink);
} else {
	TimerManager.unregister(allyBlink);
	allyBt.removeStyleName("blinking");
}
}

public void blinkResearch(boolean blink) {
if (blink) {
	TimerManager.register(researchBlink);
} else {
	TimerManager.unregister(researchBlink);
	researchBt.removeStyleName("blinking");
}
}

public void setAllyBtSelected(boolean selected) {
allyBt.setSelected(selected);
}

// ------------------------------------------------- METHODES PRIVEES -- //

private void onDiplomacyClick() {
if (!listeners[DIPLOMACY]) {
	Client.getInstance().getDiplomacyDialog().addDialogListener(this);
	listeners[DIPLOMACY] = true;
}

boolean show = !Client.getInstance().getDiplomacyDialog().isVisible();
diplomacyBt.setSelected(show);
Client.getInstance().getDiplomacyDialog().setVisible(show);
}

private void onAllyBtClick() {
if (!listeners[ALLY]) {
	Client.getInstance().getAllyDialog().addDialogListener(this);
	listeners[ALLY] = true;
}

boolean show = !Client.getInstance().getAllyDialog().isActive();
Client.getInstance().getAllyDialog().setActive(show);
allyBt.setSelected(Client.getInstance().getAllyDialog().isActive());
}

private void onMessageBtClick() {
if (!listeners[MESSENGER]) {
	Client.getInstance().getMessenger().addDialogListener(this);
	listeners[MESSENGER] = true;
}

boolean show = !Client.getInstance().getMessenger().isVisible();
messagesBt.setSelected(show);
Client.getInstance().getMessenger().setVisible(show);
}

private void onLadderBtClick() {
if (!listeners[LADDER]) {
	Client.getInstance().getLadder().addDialogListener(this);
	listeners[LADDER] = true;
}

boolean show = !Client.getInstance().getLadder().isVisible();
ladderBt.setSelected(show);
Client.getInstance().getLadder().setVisible(show);
}

private void onContractsBtClick() {
if (!listeners[CONTRACTS]) {
	Client.getInstance().getContractDialog().addDialogListener(this);
	listeners[CONTRACTS] = true;
}

boolean show = !Client.getInstance().getContractDialog().isVisible();
contractsBt.setSelected(show);
Client.getInstance().getContractDialog().setVisible(show);
}

private void onMapBtClick() {
Client.getInstance().getGalaxyMap().show(GalaxyMap.MODE_DEFAULT);
}

private void onMenuBtClick() {
boolean show = !menuDialog.isVisible();
menuBt.setSelected(show);
menuDialog.setVisible(show);
}

private void onPremiumBtClick() {
	Client.getInstance().getPremiumDialog().setVisible(
			!Client.getInstance().getPremiumDialog().isVisible());
}

private void onTacticsBtClick() {
	Client.getInstance().getPlayerTacticsDialog().setVisible(
			!Client.getInstance().getPlayerTacticsDialog().isVisible());
	}

private void onResearchBtClick() {
Client.getInstance().getResearchManager().show();
}

private void onOptionsBtClick() {
Client.getInstance().getOptionsDialog().setVisible(
	!Client.getInstance().getOptionsDialog().isVisible());
}

private void onAchievementsBtClick() {
Client.getInstance().getAchievementDialog().setVisible(
	!Client.getInstance().getAchievementDialog().isVisible());
}

private void onAdvancementsBtClick() {
Client.getInstance().getAdvancementDialog().setVisible(
	!Client.getInstance().getAdvancementDialog().isVisible());
}

private void onContactsBtClick() {
Client.getInstance().getContactDialog().setVisible(
		!Client.getInstance().getContactDialog().isVisible());
}

private void onHelpBtClick() {
	Client.getInstance().getHelpDialog().setVisible(
			!Client.getInstance().getHelpDialog().isVisible());
}

private void onBugReportBtClick() {
	Client.getInstance().getBugReportDialog().setVisible(
			!Client.getInstance().getBugReportDialog().isVisible());
}

private void onEditChangelogShortcutTriggered() {
	if (!Settings.isAdministrator())
		return;
	Client.getInstance().getChangelogDialog().setVisible(
			!Client.getInstance().getChangelogDialog().isVisible());
}

private void onAdministrationPanelShortcutTriggered() {
	if(!Settings.isModerator())
		return;
	Client.getInstance().getAdministrationPanelDialog().setVisible(
			!Client.getInstance().getAdministrationPanelDialog().isVisible());
}

private void onEventsBtClick() {
if (!listeners[EVENTS]) {
	Client.getInstance().getEventsDialog().addDialogListener(this);
	listeners[EVENTS] = true;
}

boolean show = !Client.getInstance().getEventsDialog().isVisible();
eventsBt.setSelected(show);
Client.getInstance().getEventsDialog().setVisible(show);
}

private void onDisconnectBtClick() {
if (!disconnectDialogVisible) {
	disconnectDialogVisible = true;
	JSOptionPane.showMessageDialog("Voulez-vous vous déconnecter ?",
		"Déconnexion",
		JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
		JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
			public void optionSelected(Object option) {
				disconnectDialogVisible = false;
				if ((Integer) option == JSOptionPane.OK_OPTION) {
					new Action("logout", Action.NO_PARAMETERS, new ActionCallbackAdapter() {
						public void onSuccess(AnswerData data) {
							Window.Location.reload();
						}
					});
				}
			}
	});
}
}
}
