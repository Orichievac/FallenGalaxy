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

package fr.fg.client.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.data.AlertData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.EventData;
import fr.fg.client.data.EventsData;
import fr.fg.client.data.WardData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComponent;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSScrollPane;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.SelectionListener;
import fr.fg.client.core.Client;
import fr.fg.client.core.Utilities;

public class EventDialog extends JSDialog implements ActionCallback,
SelectionListener, WindowResizeListener, ClickListener {
// ------------------------------------------------------- CONSTANTES -- //

public final static int
VIEW_EVENTS = 0,
VIEW_ALERTS = 1,
VIEW_RSS = 2;

private final static int
HIGH_RES_EXTRA_WIDTH = 180,
HIGH_RES_EXTRA_HEIGHT = 140;

private final static String[] EVENT_FILTERS = {
"", "Battle", "Ally", "Diplomacy", "Structure", "Charge", "Emp",
"Swap", "Research", "Colonization","Blackhole", "Wards"
};

// -------------------------------------------------------- ATTRIBUTS -- //

private JSButton[] filtersBt;

private int currentClientWidth;

private boolean highres;

private JSTabbedPane viewPane;

private JSScrollPane scrollPane;

private HTMLPanel eventsPanel;

private Action downloadAction;

private long lastUpdate;

private boolean newEvents;

private ArrayList<EventData> events;

private ArrayList<AlertData> alerts;

private String eventFilter;

private JSRowLayout layout;

private JSLabel label;

// ---------------------------------------------------- CONSTRUCTEURS -- //

public EventDialog() {
super("<img src=\"" + Config.getMediaUrl() +
	"images/misc/blank.gif\" class=\"iconEvents\"/> " +
	((StaticMessages) GWT.create(StaticMessages.class)).eventsLog(),
	false, true, true);

this.lastUpdate = 0;
this.newEvents = false;
this.events = new ArrayList<EventData>();
this.alerts = new ArrayList<AlertData>();
this.currentClientWidth = OpenJWT.getClientWidth();
this.highres = currentClientWidth > 1024;
this.eventFilter = EVENT_FILTERS[0];

this.scrollPane = new JSScrollPane();
this.scrollPane.setPixelSize(
	550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0),
	345 + (highres ? HIGH_RES_EXTRA_HEIGHT : 0));

this.viewPane = new JSTabbedPane();
this.viewPane.addTab("Évènements");
this.viewPane.addTab("Alertes");
this.viewPane.addTab("Flux RSS");
this.viewPane.setPixelWidth(550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0));
this.viewPane.addSelectionListener(this);

this.eventsPanel = new HTMLPanel("");
this.eventsPanel.addStyleName("events");
this.scrollPane.setView(eventsPanel);

String[] toolTipTexts = {"Tous", "Combats", "Alliance", "Diplomatie",
	"Structures", "Artificier", "Impulsions électromagnétiques",
	"Distortions spatiales", "Recherche", "Colonies", "Trous noirs", "Balises"};
filtersBt = new JSButton[EVENT_FILTERS.length];

for (int i = 0; i < EVENT_FILTERS.length; i++) {
	filtersBt[i] = new JSButton(i == 0 ? "Tous" : "&nbsp;");
	filtersBt[i].setToolTipText("<div class=\"title\">" + toolTipTexts[i] + "</div>");
	filtersBt[i].addClickListener(this);
	
	if (i > 0) {
		filtersBt[i].setPixelWidth(JSComponent.getUIPropertyInt(
				JSButton.UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT));
		filtersBt[i].addStyleName("icon" + EVENT_FILTERS[i]);
	}
}

filtersBt[0].setSelected(true);
label = new JSLabel("&nbsp;Afficher&nbsp;");
layout = new JSRowLayout();
layout.addComponent(viewPane);
layout.addRowSeparator(3);
layout.addComponent(label);
for (int i = 0; i < EVENT_FILTERS.length; i++)
	layout.addComponent(filtersBt[i]);
layout.addRow();
layout.addComponent(scrollPane);

setComponent(layout);
centerOnScreen();

sinkEvents(Event.ONCLICK);

Window.addWindowResizeListener(this);
}

// --------------------------------------------------------- METHODES -- //

@Override
public void onBrowserEvent(Event event) {
super.onBrowserEvent(event);

switch (event.getTypeInt()) {
case Event.ONCLICK:
	String id = event.getTarget().getId();
	
	if (id == null || id.length() == 0)
		return;
	
	if (id.contains("showEventLocation")) {
		EventData eventData = events.get(Integer.parseInt(
			id.substring("showEventLocation".length())));
		
		if (eventData.getX() != -1 && eventData.getY() != -1)
			Client.getInstance().getAreaContainer().setIdArea(
				eventData.getIdArea(), new Point(eventData.getX(), eventData.getY()));
		else
			Client.getInstance().getAreaContainer().setIdArea(eventData.getIdArea());
	} else if (id.contains("showAlertLocation")) {
		AlertData alertData = alerts.get(Integer.parseInt(
			id.substring("showAlertLocation".length())));
		
		if (alertData.getX() != -1 && alertData.getY() != -1)
			Client.getInstance().getAreaContainer().setIdArea(
				alertData.getIdArea(), new Point(alertData.getX(), alertData.getY()));
		else
			Client.getInstance().getAreaContainer().setIdArea(alertData.getIdArea());
	} else if (id.contains("showBattleReport")) {
		Client.getInstance().getBattleReport().showReport(
			Integer.parseInt(id.substring("showBattleReport".length())));
	}
	break;
}
}

public void setVisible(boolean visible) {
	super.setVisible(visible);
	
	if (visible) {
		if (downloadAction != null && downloadAction.isPending())
			return;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("date", String.valueOf(lastUpdate));
		
		downloadAction = new Action("getevents", params, this);
		
		Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_EVENT);
	}
	
	layout.update();
}

public void onFailure(String error) {
ActionCallbackAdapter.onFailureDefaultBehavior(error);
}

public void onSuccess(AnswerData data) {
newEvents = false;
Client.getInstance().getToolBar().blinkEvents(false);

EventsData newEvents = data.getEvents();

this.lastUpdate = (long) newEvents.getLastUpdate();

// Ne garde que les 100 derniers évènements
while (events.size() > 0 && events.size() + newEvents.getEventsCount() > 100)
	events.remove(events.size() - 1);

// Nouveaux évènements
for (int i = newEvents.getEventsCount() - 1; i >= 0; i--)
	this.events.add(0, newEvents.getEventAt(i));

// Nouvelles alertes
ArrayList<AlertData> alerts = new ArrayList<AlertData>();
for (int i = 0; i < newEvents.getAlertsCount(); i++)
	alerts.add(0, newEvents.getAlertAt(i));

Collections.sort(alerts, new Comparator<AlertData>() {
	public int compare(AlertData a1, AlertData a2) {
		return a1.getPriority() > a2.getPriority() ? -1 : 1;
	}
});

this.alerts = alerts;

updateUI();
}

public boolean hasNewEvents() {
return newEvents;
}

public void setNewEvents(boolean newEvents) {
this.newEvents = newEvents;

if (isVisible()) {
	if (downloadAction != null && downloadAction.isPending())
		return;
	
	HashMap<String, String> params = new HashMap<String, String>();
	params.put("date", String.valueOf(lastUpdate));
	
	downloadAction = new Action("getevents", params, this);
} else {
	Client.getInstance().getToolBar().blinkEvents(true);
}
}

public void selectionChanged(Widget sender, int newValue, int oldValue) {
if (sender == viewPane) {
	updateUI();
	scrollPane.scrollUp(99999);
}

String visibility = viewPane.getSelectedIndex() == VIEW_EVENTS ? "" : "hidden";
for (int i = 1; i < EVENT_FILTERS.length; i++) {
	filtersBt[i].getElement().getStyle().setProperty("visibility", visibility);
	
	if (viewPane.getSelectedIndex() == VIEW_EVENTS)
		filtersBt[i].setSelected(eventFilter.equals(EVENT_FILTERS[i]));
}

filtersBt[0].setSelected(viewPane.getSelectedIndex() == VIEW_ALERTS ||
		eventFilter.equals(EVENT_FILTERS[0]));

filtersBt[0].getElement().getStyle().setProperty("visibility", viewPane.getSelectedIndex() == VIEW_RSS ? "hidden" : "");
label.getElement().getStyle().setProperty("visibility", viewPane.getSelectedIndex() == VIEW_RSS ? "hidden" : "");
}

public void onWindowResized(int width, int height) {
int clientWidth = Window.getClientWidth();
highres = clientWidth > 1024;

if (currentClientWidth > 1024 && clientWidth <= 1024) {
	scrollPane.setPixelSize(550, 345);
	viewPane.setPixelWidth(550);
	
	updateUI();
	
	if (isVisible())
		centerOnScreen();
} else if (currentClientWidth <= 1024 && clientWidth > 1024) {
	scrollPane.setPixelSize(
		550 + HIGH_RES_EXTRA_WIDTH,
		345 + HIGH_RES_EXTRA_HEIGHT);
	viewPane.setPixelWidth(550 + HIGH_RES_EXTRA_WIDTH);
	
	updateUI();
	
	if (isVisible())
		centerOnScreen();
}

currentClientWidth = clientWidth;
}

public void onClick(Widget sender) {
if (viewPane.getSelectedIndex() == VIEW_EVENTS) {
	for (int i = 0; i < EVENT_FILTERS.length; i++) {
		if (sender == filtersBt[i]) {
			filtersBt[i].setSelected(true);
			eventFilter = EVENT_FILTERS[i];
		} else {
			filtersBt[i].setSelected(false);
		}
	}
	
	updateUI();
}
}

// ------------------------------------------------- METHODES PRIVEES -- //

private void updateUI() {
switch (viewPane.getSelectedIndex()) {
case VIEW_EVENTS:
	StringBuffer content = new StringBuffer("<table style=\"width: 100%;\" cellspacing=\"0\">");
	
	int count = 0;
	for (int i = 0; i < this.events.size(); i++) {
		String html = addEvent(i, events.get(i), count % 2 == 0);
		if (html != null) {
			content.append(html);
			count++;
		}
	}
	
	content.append("</table>");
	
	this.eventsPanel.getElement().setInnerHTML(content.toString());
	
	for (int i = 0; i < this.events.size(); i++) {
		EventData event = events.get(i);
		if (event.getIdArea() != 0) {
			Element element = OpenJWT.getElementById(
					"showEventLocation" + i, eventsPanel.getElement());
			
			if (element != null)
				ToolTipManager.getInstance().register(element, event.getAreaName());
		}
	}
	
	this.scrollPane.update();
	break;
case VIEW_ALERTS:
	content = new StringBuffer("<table style=\"width: 100%;\" cellspacing=\"0\">");
	
	count = 0;
	for (int i = 0; i < this.alerts.size(); i++) {
		String html = addAlert(i, alerts.get(i), count % 2 == 0);
		if (html != null) {
			content.append(html);
			count++;
		}
	}
	
	content.append("</table>");
	
	this.eventsPanel.getElement().setInnerHTML(content.toString());
	
	for (int i = 0; i < this.alerts.size(); i++) {
		AlertData alert = alerts.get(i);
		if (alert.getIdArea() != 0) {
			Element element = OpenJWT.getElementById(
				"showAlertLocation" + i, eventsPanel.getElement());
			
			if (element != null)
				ToolTipManager.getInstance().register(element, alert.getAreaName());
		}
	}
	
	this.scrollPane.update();
	break;
case VIEW_RSS:
	String html;
	Utilities.log(Settings.getEkey());
	String url = Config.getServerUrl() + "rss/events/" + Settings.getEkey() + ".xml";
	
	if (Settings.isPremium())
		html = "Copier / collez l'adresse suivante dans un agrégateur RSS pour visualiser vos évènements.<br/><br/><a href=\"" + url + "\" target=\"_blank\">" + url + "</a><br/><br/><br/><span style=\"color: red;\">Avertissement : ne communiquez <b>PAS</b> cette adresse à d'autres personnes, sans quoi elles auront accès à vos évènements !</span>";
	else
		html = "Vous devez disposer d'un compte premium pour accéder à l'export des évènements sous forme de flux RSS.";
	
	this.eventsPanel.getElement().setInnerHTML(
		"<div class=\"center\" style=\"padding: 100px 20px 0 20px;\">" + html + "</div>");
	
	this.scrollPane.update();
	break;
}
}

private String addAlert(int index, AlertData alert, boolean odd) {
String description = "";

switch (alert.getType()) {
case AlertData.ALERT_AVAILABLE_SKILL_POINT:
	// Point de compétence non attribué
	description = "Notre flotte <b>" + alert.getArg1() +
		"</b> a des points de compétences non attribués.";
	break;
case AlertData.ALERT_NO_RESEARCH:
	// Pas de recherche en cours
	description = "Aucune recherche n'a été définie pour nos scientifiques !";
	break;
case AlertData.ALERT_SYSTEM_STOCK:
	// Stocks élevés
	description = "Les stocks du système <b>" + alert.getArg1() + "</b> sont remplis à <b>" +
		(int) Math.floor(Double.parseDouble(alert.getArg2()) * 100) + "%</b>.";
	break;
case AlertData.ALERT_PENDING_TREATY:
	// Traité en attente
	description = "Une proposition de traité avec <b>" +
		alert.getArg1() + "</b> est en attente.";
	break;
case AlertData.ALERT_NO_TACTICS:
	// Flotte sans tactique
	description = "Aucune tactique n'a été définie pour notre flotte <b>" +
		alert.getArg1() + "</b>.";
	break;
case AlertData.ALERT_DESACTIVATE_STRUCTURE:
	// Structure non activée
	description = "Notre structure <b>" +
		alert.getArg1() + "</b> n'est pas activée.";
	break;
case AlertData.ALERT_AVAILABLE_CIVIL_POINT:
	// Point(s) de civilisation non utilisé(s)
	description = "<b>"+ alert.getArg1() +"<b> point(s) de civilisation "+
	"non utilisé(s).";
	break;
default:
	description = "!Unknown alert: " + alert.getType() + "!";
	break;
}

if (alert.getIdArea() != 0)
	description = "<img id=\"showAlertLocation" + index +
		"\" class=\"goToLocation\" src=\"" + Config.getMediaUrl() +
		"images/misc/blank.gif\"/ style=\"float: right; margin: 0 5px;\">" +
		description;

return "<tr class=\"" + (odd ? "odd" : "even") + "\" style=\"padding: 0;\">" +
		"<td class=\"alert priority" + alert.getPriority() + " small\" style=\"padding: 4px;\">" +
		description + "</td></tr>";
}

private String addEvent(int index, EventData event, boolean odd) {
String type = "", description = "", icon = "";

StaticMessages messages = GWT.create(StaticMessages.class);
DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);

switch (event.getType()) {
case EventData.EVENT_PREMIUM_START:
	// Nouveau compte premium
	type = "e-info";
	icon = "Premium";
	description = "Vous disposez désormais d'un compte Premium pendant " + event.getArg1() + " jours.";
	break;
case EventData.EVENT_PREMIUM_EXTENDED:
	// Compte premium prolongé
	type = "e-info";
	icon = "Premium";
	description = "Vous avez prolongé votre compte Premium de " + event.getArg1() + " jours ; il expirera dans " + event.getArg2() + " jours.";
	break;
case EventData.EVENT_PREMIUM_NEAR_END:
	// Compte premium proche de l'expiration
	type = "e-info";
	icon = "Premium";
	description = "Votre compte Premium expirera dans " + event.getArg1() + " jours.";
	break;
case EventData.EVENT_PREMIUM_END:
	// Compte premium expiré
	type = "e-info";
	icon = "Premium";
	description = "Votre compte Premium a expiré ; vous ne bénéficiez plus des avantages liés aux comptes Premium.";
	break;
case EventData.EVENT_FLEET_ATTACK:
	// Attaque de flotte
	type = "e-war";
	icon = "Battle";
	description = "Notre flotte <b>" + event.getArg1() + "</b> " +
		"a engagé le combat avec la flotte <b>" + event.getArg2() + "</b> " +
		"appartenant à <b>" + event.getArg3() + "</b>.";
	break;
case EventData.EVENT_FLEET_UNDER_ATTACK:
	// Flotte attaquée
	type = "e-war";
	icon = "Battle";
	description = "Notre flotte <b>" + event.getArg1() + "</b> " +
		"a été attaquée par la flotte <b>" + event.getArg2() + "</b> " +
		"appartenant à <b>" + event.getArg3() + "</b>.";
	break;
case EventData.EVENT_FLEET_DESTROYED:
	// Flotte détruite
	type = "e-war";
	icon = "Battle";
	description = "Nos forces ont détruit la flotte <b>" + event.getArg1() + "</b> !";
	break;
case EventData.EVENT_FLEET_LOST:
	// Flotte perdue
	type = "e-war";
	icon = "Battle";
	description = "Notre flotte <b>" + event.getArg1() +
		"</b> a été détruite par les forces ennemies !";
	break;
case EventData.EVENT_BATTLE_REPORT:
	// Rapport de combat
	type = "e-war";
	icon = "Battle";
	description = "<b><a id=\"showBattleReport" + event.getArg1() +
		"\" class=\"link\">Voir le combat</a></b> - " +
		"Lien pour partager le combat : <a href=\"" + event.getArg2() +
		"\" target=\"_blank\">" + event.getArg2() + "</a>";
	break;
case EventData.EVENT_SWAP:
	// Distorsion spatiale
	type = "e-war";
	icon = "Swap";
	description = "Notre flotte <b>" + event.getArg1() + "</b> a " +
		"été déplacée suite à une distorsion spatiale générée " +
		"par la flotte <b>" + event.getArg2() + "</b> appartenant " +
		"à <b>" + event.getArg3() + "</b>.";
	break;
case EventData.EVENT_EMP:
	// IEM
	type = "e-war";
	icon = "Emp";
	description = "Notre flotte <b>" + event.getArg1() + "</b> a " +
		"subi une impulsion électromagnétique générée " +
		"par la flotte <b>" + event.getArg2() + "</b> appartenant " +
		"à <b>" + event.getArg3() + "</b>.";
	break;
case EventData.EVENT_DELUDE_LOST:
	// Leurre détruit
	type = "e-war";
	icon = "Battle";
	description = "Notre leurre <b>" + event.getArg1() + "</b> " +
		"a été détruit par la flotte <b>" + event.getArg2() + "</b> " +
		"appartenant à <b>" + event.getArg3() + "</b>.";
	break;
case EventData.EVENT_COLONIZATION:
	// Colonisation
	type = "e-info";
	icon = "Colonization";
	description = "Notre flotte <b>" + event.getArg1() +
		"</b> a colonisé le système <b>" + event.getArg2() + "</b>.";
	break;
case EventData.EVENT_SYSTEM_CAPTURED:
	// Système capturé
	type = "e-war";
	icon = "Colonization";
	description = "Notre flotte <b>" + event.getArg1() +
		"</b> a pris le contrôle du système <b>" + event.getArg3() +
		"</b> appartenant à <b>" + event.getArg2() + "</b> !";
	break;
case EventData.EVENT_SYSTEM_LOST:
	// Système perdu
	type = "e-war";
	icon = "Colonization";
	description = "Notre système <b>" + event.getArg3() +
		"</b> a été capturé par la flotte <b>" + event.getArg1() +
		"</b> appartenant à <b>" + event.getArg2() + "</b> !";
	break;
case EventData.EVENT_START_CAPTURE:
	// Début de capture d"un système
	type = "e-war";
	icon = "Colonization";
	description = "Notre système <b>" + event.getArg3() +
		"</b> est en train d'être capturé par la flotte <b>" +
		event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b> !";
	break;
case EventData.EVENT_DEVASTATE_SYSTEM:
	// Devastation de système
	type = "e-war";
	icon = "Colonization";
	description = "Notre flotte <b>" + event.getArg1() +
		"</b> a dévasté le système <b>" + event.getArg3() +
		"</b> appartenant à <b>" + event.getArg2() + "</b> !";
	break;
case EventData.EVENT_SYSTEM_DEVASTATED:
	// Système devasté
	type = "e-war";
	icon = "Colonization";
	description = "Notre système <b>" + event.getArg3() +
		"</b> a été dévasté par la flotte <b>" + event.getArg1() +
		"</b> appartenant à <b>" + event.getArg2() + "</b> !";
	break;
case EventData.EVENT_STATION_UNDER_ATTACK:
	// Station endommagée
	type = "e-war";
	icon = "Battle";
	description = "Notre station spatiale <b>" + event.getArg1() +
		"</b> est endommagée à <b>" + (int) Math.round((1 -
		Double.parseDouble(event.getArg2())) * 100) + "%</b>.";
	break;
case EventData.EVENT_STATION_LOST:
	// Station perdue
	type = "e-war";
	icon = "Battle";
	description = "Notre station spatiale <b>" + event.getArg1() +
		"</b> a été détruite !";
	break;
case EventData.EVENT_STATION_SELF_DESTRUCT:
	// Station auto détruite
	type = "e-war";
	icon = "Battle";
	description = "<b>" + event.getArg2() + "</b> a enclenché la procédure " +
		"d'auto-destruction de notre station spatiale <b>" + event.getArg1() + "</b>.";
	break;
case EventData.EVENT_NEW_TECHNOLOGY:
	// Nouvelle technologie recherchée
	type = "e-info";
	icon = "Research";
	description = "Nos chercheurs ont découvert la technologie <b>" +
	dynamicMessages.getString("research" + Integer.parseInt(event.getArg1())) + "</b>.";
	break;
case EventData.EVENT_ALLY_CREATED:
	// Création d"une alliance
	type = "e-ally";
	icon = "Ally";
	description = "L'alliance <b>" + event.getArg2() +
		"</b> a été fondée par <b>" + event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_DESTROYED:
	// Dissolution d"une alliance
	type = "e-ally";
	icon = "Ally";
	description = "L'alliance <b>" + event.getArg1() + "</b> a été dissoute.";
	break;
case EventData.EVENT_ALLY_MEMBER_JOINED:
	// Nouveau membre
	type = "e-ally";
	icon = "Ally";
	description = "<b>" + event.getArg1() + "</b> a intégré notre alliance.";
	break;
case EventData.EVENT_ALLY_MEMBER_LEFT:
	// Membre qui quitte l"alliance
	type = "e-ally";
	icon = "Ally";
	description = "<b>" + event.getArg1() + "</b> a quitté notre alliance.";
	break;
case EventData.EVENT_ALLY_NEW_RANK:
	// Changement de rang du joueur
	type = "e-ally";
	icon = "Ally";
	description = "Vous avez désormais le rang de <b>" +
		dynamicMessages.getString(event.getArg1() + "Rank" + event.getArg2()) + "</b>.";
	break;
case EventData.EVENT_ALLY_APPLICANT:
	// Nouveau postulant
	type = "e-ally";
	icon = "Ally";
	description = "<b>" + event.getArg1() +
		"</b> a postulé à notre alliance.";
	break;
case EventData.EVENT_ALLY_CANCEL_APPLY:
	// Postulant qui retire sa candidatuer
	type = "e-ally";
	icon = "Ally";
	description = "<b>" + event.getArg1() + "</b> a retiré sa " +
			"candidature dans notre alliance.";
	break;
case EventData.EVENT_ALLY_NEW_VOTEKICK:
	// Joueur qui lance un vote de kick
	type = "e-ally";
	icon = "Ally";
	description = "<b>" + event.getArg1() +
		"</b> a lancé un vote pour éjecter <b>" +
		event.getArg2() + "</b> de notre alliance.";
	break;
case EventData.EVENT_ALLY_NEW_VOTEACCEPT:
	// Joueur qui lance un vote d"accept
	type = "e-ally";
	icon = "Ally";
	description = "<b>" + event.getArg1() +
		"</b> a lancé un vote pour accepter <b>" +
		event.getArg2() + "</b> dans notre alliance.";
	break;
case EventData.EVENT_ALLY_BREAK_ALLY:
	// Alliance qui rompt un traité d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons rompu le pacte de non agression " +
		"passé avec l'alliance <b>" + event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_BROKEN:
	// Traité d"alliance rompu
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a rompu le pacte de non agression passé avec notre alliance.";
	break;
case EventData.EVENT_ALLY_DECLARE_WAR:
	// Déclaration de guerre
	type = "e-war";
	icon = "Diplomacy";
	description = "Nous avons déclaré la guerre à l'alliance <b>" +
		event.getArg1() + "</b> !";
	break;
case EventData.EVENT_ALLY_WAR_DECLARED:
	// Guerre déclarée
	type = "e-war";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> nous a déclaré la guerre !";
	break;
case EventData.EVENT_ALLY_OFFER_ALLY:
	// Proposition d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons proposé un pacte de non agression à l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_OFFERED:
	// Alliance proposée
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> souhaite signer un pacte de non agression avec notre alliance.";
	break;
case EventData.EVENT_ALLY_DECLINE_ALLY:
	// Refus d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons refusé le pacte de non agression avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_DECLINED:
	// Alliance refusée
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a refusé de signer un pacte de non agression avec notre alliance.";
	break;
case EventData.EVENT_ALLY_CANCEL_ALLY:
	// Proposition d'alliance retirée
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons retiré notre proposition de pacte de non agression avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_CANCELED:
	// Proposition d'alliance retirée
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a retiré sa proposition de pacte de non agression.";
	break;
case EventData.EVENT_ALLY_NEW_ALLY:
	// Alliance acceptée
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons désormais un pacte de non agression avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_OFFER_PEACE:
	// Proposition de paix
	type = "e-war";
	icon = "Diplomacy";
	description = "Nous avons proposé de signer la paix à l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_PEACE_OFFERED:
	// Paix proposée
	type = "e-war";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> souhaite signer la paix avec notre alliance.";
	break;
case EventData.EVENT_ALLY_DECLINE_PEACE:
	// Refus proposition de paix
	type = "e-war";
	icon = "Diplomacy";
	description = "Nous avons refusé de signer la paix avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_PEACE_DECLINED:
	// Proposition de paix refusée
	type = "e-war";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a refusé de signer la paix avec notre alliance.";
	break;
case EventData.EVENT_ALLY_CANCEL_PEACE:
	// Retirer proposition de paix
	type = "e-war";
	icon = "Diplomacy";
	description = "Nous avons retiré notre proposition de paix avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_PEACE_CANCELED:
	// Proposition de paix retirée
	type = "e-war";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a retiré sa proposition de paix avec notre alliance.";
	break;
case EventData.EVENT_ALLY_NEW_PEACE:
	// Paix signée
	type = "e-war";
	icon = "Diplomacy";
	description = "Nous avons signé la paix avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_BREAK_ALLY:
	// Alliance qui rompt un traité d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons rompu le pacte de non agression " +
		"passé avec <b>" + event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_BROKEN:
	// Traité d"alliance rompu
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a rompu le pacte de non agression passé avec notre gouvernement.";
	break;
case EventData.EVENT_PLAYER_DECLARE_WAR:
	// Déclaration de guerre
	type = "e-war";
	icon = "Diplomacy";
	description = "Nous avons déclaré la guerre à <b>" +
		event.getArg1() + "</b> !";
	break;
case EventData.EVENT_PLAYER_WAR_DECLARED:
	// Guerre déclarée
	type = "e-war";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> nous a déclaré la guerre !";
	break;
case EventData.EVENT_PLAYER_OFFER_ALLY:
	// Proposition d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons proposé pacte de non agression à <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_OFFERED:
	// Alliance proposée
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> souhaite signer un pacte de non agression avec notre gouvernement.";
	break;
case EventData.EVENT_PLAYER_DECLINE_ALLY:
	// Refus d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons refusé le pacte de non agression avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_DECLINED:
	// Alliance refusée
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a refusé de signer un pacte de non agression avec notre gouvernement.";
	break;
case EventData.EVENT_PLAYER_CANCEL_ALLY:
	// Proposition d'alliance retirée
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons retiré notre proposition de pacte de non agression avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_CANCELED:
	// Proposition d'alliance retirée
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a retiré sa proposition de coalition.";
	break;
case EventData.EVENT_PLAYER_NEW_ALLY:
	// Alliance acceptée
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons désormais un pacte de non agression avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_OFFER_PEACE:
	// Proposition de paix
	type = "e-war";
	icon = "Diplomacy";
	description = "Nous avons proposé de signer la paix à <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_PEACE_OFFERED:
	// Paix proposée
	type = "e-war";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> souhaite signer la paix avec nous.";
	break;
case EventData.EVENT_PLAYER_DECLINE_PEACE:
	// Refus proposition de paix
	type = "e-war";
	icon = "Diplomacy";
	description = "Nous avons refusé de signer la paix avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_PEACE_DECLINED:
	// Proposition de paix refusée
	type = "e-war";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a refusé de signer la paix avec notre gouvernement.";
	break;
case EventData.EVENT_PLAYER_CANCEL_PEACE:
	// Retirer proposition de paix
	type = "e-war";
	icon = "Diplomacy";
	description = "Nous avons retiré notre proposition de signer la paix avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_PEACE_CANCELED:
	// Proposition de paix retirée
	type = "e-war";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a retiré sa proposition de signer la paix.";
	break;
case EventData.EVENT_PLAYER_NEW_PEACE:
	// Paix signée
	type = "e-war";
	icon = "Diplomacy";
	description = "Nous avons signé la paix avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_END_OF_WAR:
	// Paix signée par guerre inactive
	type = "e-war";
	icon = "Diplomacy";
	description = "Les affrontements avec <b>" + event.getArg1() + "</b> ayant cessé, la paix a été signée.";
	break;
case EventData.EVENT_NEW_STATION:
	// Nouvelle station
	type = "e-ally";
	icon = "Ally";
	description = "Nous disposons d'une nouvelle station spatiale dans le secteur <b>" + event.getArg1() + "</b> !";
	break;
case EventData.EVENT_STATION_UPGRADED:
	// Station améliorée
	type = "e-ally";
	icon = "Ally";
	description = "Notre station spatiale <b>" + event.getArg1() + "</b> a été améliorée au niveau <b>" + event.getArg2() + "</b> !";
	break;
case EventData.EVENT_CHARGE_DEFUSED:
	// Charge désamorcée
	type = "e-war";
	icon = "Charge";
	description = "La flotte <b>" + event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b> a désamorcé une de nos " + getChargeName(event.getArg3(), 2) + ".";
	break;
case EventData.EVENT_CHARGE_TRIGGERED:
	// Charge déclenchée
	type = "e-war";
	icon = "Charge";
	if (event.getArg3().startsWith(WardData.TYPE_MINE))
		description = "La flotte <b>" + event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b> a été endommagée par " + event.getArg4() + " de nos " + getChargeName(event.getArg3(), 2) + ".";
	else
		description = "La flotte <b>" + event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b> a été immobilisée par " + event.getArg4() + " de nos " + getChargeName(event.getArg3(), 2) + ".";
	break;
case EventData.EVENT_CHARGE_BLOWED:
	// Charge subie
	type = "e-war";
	icon = "Charge";
	if (event.getArg2().startsWith(WardData.TYPE_MINE))
		description = "Notre flotte <b>" + event.getArg1() + "</b> a été endommagée par " + event.getArg3() + " " + getChargeName(event.getArg2(), Integer.parseInt(event.getArg3())) + ".";
	else
		description = "Notre flotte <b>" + event.getArg1() + "</b> a été immobilisée par " + event.getArg3() + " " + getChargeName(event.getArg2(), Integer.parseInt(event.getArg3())) + ".";
	break;
case EventData.EVENT_CHARGE_FLEET_DESTROYED:
	// Flotte détruite par une charge
	type = "e-war";
	icon = "Charge";
	description = "La flotte <b>" + event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b> a été détruite par " + event.getArg4() + " de nos " + getChargeName(event.getArg3(), 2) + ".";
	break;
case EventData.EVENT_CHARGE_FLEET_LOST:
	// Flotte perdue à cause d'une charge
	type = "e-war";
	icon = "Charge";
	description = "Notre flotte <b>" + event.getArg1() + "</b> a été détruite par " + event.getArg3() + " " + getChargeName(event.getArg2(), Integer.parseInt(event.getArg3())) + ".";
	break;
case EventData.EVENT_STRUCTURE_ATTACKED:
	// Structure bombardée
	type = "e-war";
	icon = "Structure";
	description = "Notre structure <b>" + event.getArg1() + "</b> subit un bombardement de la flotte <b>" + event.getArg2() + "</b> appartenant à <b>" + event.getArg3() + "</b>.";
	break;
case EventData.EVENT_STRUCTURE_LOST:
	// Structure perdue
	type = "e-war";
	icon = "Structure";
	description = "Notre structure <b>" + event.getArg1() + "</b> a été détruite par la flotte <b>" + event.getArg2() + "</b> appartenant à <b>" + event.getArg3() + "</b>.";
	break;
case EventData.EVENT_STRUCTURE_DESTROYED:
	// Structure détruite
	type = "e-war";
	icon = "Structure";
	description = "Notre flotte <b>" + event.getArg3() + "</b> a détruit la structure <b>" + event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b>.";
	break;
case EventData.EVENT_STRUCTURE_DISMOUNTED:
	// Structure détruite
	type = "e-info";
	icon = "Structure";
	description = "Notre flotte <b>" + event.getArg2() + "</b> a achevé le démontage de la structure <b>" + event.getArg1() + "</b>.";
	break;
case EventData.EVENT_STRUCTURE_MOUNTED:
	// Structure détruite
	type = "e-info";
	icon = "Structure";
	description = "Notre flotte <b>" + event.getArg2() + "</b> a achevé l'assemblage de la structure <b>" + event.getArg1() + "</b>.";
	break;
case EventData.EVENT_STRUCTURE_DAMAGED:
	// Structure endommagée
	type = "e-war";
	icon = "Structure";
	description = "Notre structure <b>" + event.getArg1() +
		"</b> est endommagée à <b>" + (int) Math.round((1 -
		Double.parseDouble(event.getArg2())) * 100) + "</b>.";
	break;
case EventData.EVENT_FLEET_CAPTURED_BLACKHOLE:
	// Flotte capturée par un trou noir
	type = "e-war";
	icon = "Blackhole";
	description = "Notre flotte <b>" + event.getArg1() +
		"</b> vient d'être affaiblie par un trou noir !";
	break;
case EventData.EVENT_WARD_OBSERVER_LOST:
	// Observer ward détruite
	type = "e-war";
	icon = "Wards";
	description = "<b>" + event.getArg1() + "</b> à détruit une de nos balises d'observation.";
	break;
case EventData.EVENT_WARD_SENTRY_LOST:
	// Sentry ward détruite
	type = "e-war";
	icon = "Wards";
	description = "<b>" + event.getArg1() + "</b> à détruit une de nos balises de détection.";
	break;
case EventData.EVENT_PLAYER_ADD_FRIEND:
	//Ajout d'un ami
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() + "</b> a été ajouté à votre liste d'amis.";
	break;
case EventData.EVENT_PLAYER_REMOVE_FRIEND:
	//Suppression d'un ami
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() + "</b> a été retiré de votre liste d'amis.";
	break;
case EventData.EVENT_PLAYER_ADDED_FRIEND:
	//Ajout d'un ami
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() + "</b> vous a ajouté à sa liste d'amis.";
	break;
case EventData.EVENT_PLAYER_REMOVED_FRIEND:
	//Suppression d'un ami
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() + "</b> vous a retiré de sa liste d'amis.";
	break;
case EventData.EVENT_ALLY_ADDED_NEWS:
	//Ajout d'une news
	type = "e-ally";
	icon = "Ally";
	description = "<b>" + event.getArg1() + "</b> a ajouté une news.";
	break;
case EventData.EVENT_ALLY_REMOVED_NEWS:
	//Suppression d'une news
	type = "e-ally";
	icon = "Ally";
	description = "<b>" + event.getArg1() + "</b> a supprimé une news.";
	break;
case EventData.EVENT_MIGRATION_START:
	//Debut de migration
	type = "e-info";
	icon = "Colonization";
	description = "Notre flotte <b>" + event.getArg1() +
		"</b> a commencé à migrer le système <b>" + event.getArg2() +
		"</b> vers le système <b>" + event.getArg3()+"</b>.";
	break;
case EventData.EVENT_MIGRATION_END:
	//Fin de migration
	type = "e-info";
	icon = "Colonization";
	description = "Notre flotte <b>" + event.getArg1() +
		"</b> vient d'achever la migration du système <b>" + event.getArg2() +
		"</b> vers le système <b>" + event.getArg3()+"</b>.";
	break;
case EventData.EVENT_ALLY_BREAK_ALLY_DEFENSIVE:
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons rompu le pacte Défensif " +
		"passé avec l'alliance <b>" + event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_DEFENSIVE_BROKEN:
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a rompu le pacte Défensif passé avec notre alliance.";
	break;
case EventData.EVENT_ALLY_OFFER_ALLY_DEFENSIVE:
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons proposé un pacte Défensif à l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_DEFENSIVE_OFFERED:
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> souhaite signer un pacte Défensif avec notre alliance.";
	break;
case EventData.EVENT_ALLY_DECLINE_ALLY_DEFENSIVE:
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons refusé le pacte Défensif avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_DEFENSIVE_DECLINED:
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a refusé de signer un pacte Défensif avec notre alliance.";
	break;
case EventData.EVENT_ALLY_CANCEL_ALLY_DEFENSIVE:
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons retiré notre proposition de pacte Défensif avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_DEFENSIVE_CANCELED:
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a retiré sa proposition de pacte Défensif.";
	break;
case EventData.EVENT_ALLY_NEW_ALLY_DEFENSIVE:
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons désormais un pacte Défensif avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_BREAK_ALLY_TOTAL:
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons rompu le pacte Total " +
		"passé avec l'alliance <b>" + event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_TOTAL_BROKEN:
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a rompu le pacte Total passé avec notre alliance.";
	break;
case EventData.EVENT_ALLY_OFFER_ALLY_TOTAL:
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons proposé un pacte Total à l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_TOTAL_OFFERED:
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> souhaite signer un pacte Total avec notre alliance.";
	break;
case EventData.EVENT_ALLY_DECLINE_ALLY_TOTAL:
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons refusé le pacte Total avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_TOTAL_DECLINED:
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a refusé de signer un pacte Total avec notre alliance.";
	break;
case EventData.EVENT_ALLY_CANCEL_ALLY_TOTAL:
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons retiré notre proposition de pacte Total avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_ALLY_TOTAL_CANCELED:
	type = "e-ally";
	icon = "Diplomacy";
	description = "L'alliance <b>" + event.getArg1() +
		"</b> a retiré sa proposition de pacte Total.";
	break;
case EventData.EVENT_ALLY_NEW_ALLY_TOTAL:
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons désormais un pacte Total avec l'alliance <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_BREAK_ALLY_DEFENSIVE:
	// Alliance qui rompt un traité d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons rompu le pacte Défensif " +
		"passé avec <b>" + event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_DEFENSIVE_BROKEN:
	// Traité d"alliance rompu
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a rompu le pacte Défensif passé avec notre gouvernement.";
	break;
case EventData.EVENT_PLAYER_OFFER_ALLY_DEFENSIVE:
	// Proposition d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons proposé un pacte Défensif à <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_DEFENSIVE_OFFERED:
	// Alliance proposée
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> souhaite signer un pacte Défensif avec notre gouvernement.";
	break;
case EventData.EVENT_PLAYER_DECLINE_ALLY_DEFENSIVE:
	// Refus d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons refusé le pacte Défensif avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_DEFENSIVE_DECLINED:
	// Alliance refusée
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a refusé de signer un pacte Défensif avec notre gouvernement.";
	break;
case EventData.EVENT_PLAYER_CANCEL_ALLY_DEFENSIVE:
	// Proposition d'alliance retirée
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons retiré notre proposition de pacte Défensif avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_DEFENSIVE_CANCELED:
	// Proposition d'alliance retirée
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a retiré sa proposition de pacte Défensif.";
	break;
case EventData.EVENT_PLAYER_NEW_ALLY_DEFENSIVE:
	// Alliance acceptée
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons désormais un pacte Défensif avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_BREAK_ALLY_TOTAL:
	// Alliance qui rompt un traité d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons rompu le pacte Total " +
		"passé avec <b>" + event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_TOTAL_BROKEN:
	// Traité d"alliance rompu
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a rompu le pacte Total passé avec notre gouvernement.";
	break;
case EventData.EVENT_PLAYER_OFFER_ALLY_TOTAL:
	// Proposition d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons proposé un pacte Total à <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_TOTAL_OFFERED:
	// Alliance proposée
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> souhaite signer un pacte Total avec notre gouvernement.";
	break;
case EventData.EVENT_PLAYER_DECLINE_ALLY_TOTAL:
	// Refus d"alliance
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons refusé le pacte Total avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_TOTAL_DECLINED:
	// Alliance refusée
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a refusé de signer un pacte Total avec notre gouvernement.";
	break;
case EventData.EVENT_PLAYER_CANCEL_ALLY_TOTAL:
	// Proposition d'alliance retirée
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons retiré notre proposition de pacte Total avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_PLAYER_ALLY_TOTAL_CANCELED:
	// Proposition d'alliance retirée
	type = "e-ally";
	icon = "Diplomacy";
	description = "<b>" + event.getArg1() +
		"</b> a retiré sa proposition de pacte Total.";
	break;
case EventData.EVENT_PLAYER_NEW_ALLY_TOTAL:
	// Alliance acceptée
	type = "e-ally";
	icon = "Diplomacy";
	description = "Nous avons désormais un pacte Total avec <b>" +
		event.getArg1() + "</b>.";
	break;
case EventData.EVENT_ALLY_DECLARE_WAR_TOTAL:
	type = "e-war";
	icon = "Diplomacy";
	description = "Notre pacte Total passé avec <b>"+event.getArg1()+"</b> nous oblige à déclarer "+
	"la guerre à l'alliance <b>" +event.getArg2() + "</b> !";
	break;
case EventData.EVENT_ALLY_WAR_DECLARED_TOTAL:
	type = "e-war";
	icon = "Diplomacy";
	description = "A cause du pacte Total conclu avec <b>"+event.getArg1()+"</b>, <b>"+event.getArg2()+
	"</b> nous a déclaré la guerre!";
	break;
case EventData.EVENT_ALLY_DECLARE_WAR_DEFENSIVE:
	type = "e-war";
	icon = "Diplomacy";
	description = "Notre pacte Défensif passé avec <b>"+event.getArg1()+"</b> nous oblige à déclarer "+
	"la guerre à l'alliance <b>" +event.getArg2() + "</b> !";
	break;

case EventData.EVENT_ALLY_WAR_DECLARED_DEFENSIVE:
	type = "e-war";
	icon = "Diplomacy";
	description = "A cause du pacte Défensif conclu avec <b>"+event.getArg1()+"</b>, <b>"+event.getArg2()+
	"</b> nous a déclaré la guerre!";
	break;
case EventData.EVENT_PLAYER_DECLARE_WAR_TOTAL:
	type = "e-war";
	icon = "Diplomacy";
	description = "Notre pacte Total passé avec <b>"+event.getArg1()+"</b> nous oblige à déclarer "+
	"la guerre à <b>" +event.getArg2() + "</b> !";
	break;
case EventData.EVENT_PLAYER_WAR_DECLARED_TOTAL:
	type = "e-war";
	icon = "Diplomacy";
	description = "A cause du pacte Total conclu avec <b>"+event.getArg1()+"</b>, <b>"+event.getArg2()+
	"</b> nous a déclaré la guerre!";
	break;
case EventData.EVENT_PLAYER_DECLARE_WAR_DEFENSIVE:
	type = "e-war";
	icon = "Diplomacy";
	description = "Notre pacte Défensif passé avec <b>"+event.getArg1()+"</b> nous oblige à déclarer "+
	"la guerre à <b>" +event.getArg2() + "</b> !";
	break;
case EventData.EVENT_PLAYER_WAR_DECLARED_DEFENSIVE:
	type = "e-war";
	icon = "Diplomacy";
	description = "A cause du pacte Défensif conclu avec <b>"+event.getArg1()+"</b>, <b>"+event.getArg2()+
	"</b> nous a déclaré la guerre!";
	break;
case EventData.EVENT_REWARD_PERSO:
	// Obtention du %reward% à la fin d'un contrat d'alliance
	type = "e-info";
	icon = "Ally";
	description = "Votre alliance vient de terminer un contrat d'alliance ! " +
			"Vous obtenez les bonus suivants : <b>" + event.getArg1() +
	"</b>.";
	break;
case EventData.EVENT_ELECTION_START:
	// Les elections commencent
	type = "e-ally";
	icon = "Ally";
	description = "Les elections pour la gestion de l'alliance viennent de commencer.";
	break;		
case EventData.EVENT_LEADER_DELEGATE:
	// Délégation des droits de leader à un joueur
	type = "e-ally";
	icon = "Ally";
	description = "<b>"+event.getArg1()+"</b> a donné son rang de leader de l'alliance à <b>"
	+ event.getArg2()+"</b> !";
	break;
case EventData.EVENT_PREMIUM:
	//Le joueur devient premium
	type = "e-info";
	icon = "Diplomacy";
	description = "Vous possédez maintenant un compte premium, il expirera dans 30 jours.";
	break;
case EventData.EVENT_PREMIUM_EXPIRED:
	//Le joueur n'est plus premium
	type = "e-info";
	icon = "Diplomacy";
	description = "Vous n'êtes plus premium!";
	break;
case EventData.EVENT_PREMIUM_ADDED:
	type = "e-info";
	icon = "Diplomacy";
	description = "Votre compte premium expirera le "+event.getArg1()+".";
	break;
case EventData.EVENT_LOTTERY_WON:
    // Tirage gagnant
    type = "e-info";
    icon = "Lottery";
    description = "Le symbole gagnant pour la loterie Eden est le " +
            LotteryDialog.SIGNS.charAt(Integer.parseInt(event.getArg1())) +
            ", nous avons gagné " + event.getArg2() + " !";
    break;
case EventData.EVENT_LOTTERY_LOST:
    // Tirage perdant
    type = "e-info";
    icon = "Lottery";
    description = "Le symbole gagnant pour la loterie Eden est le " +
            LotteryDialog.SIGNS.charAt(Integer.parseInt(event.getArg1())) +
            ", nous avons perdu. La prochaine fois sera la bonne !";
    break;
default:
	type = "";
	icon = "Error";
	description = "!Unknown event: " + event.getType() + "!";
	break;
}

if (!eventFilter.equals("") && !icon.equals(eventFilter))
	return null;

if (event.getIdArea() != 0)
	description = "<img id=\"showEventLocation" + index +
		"\" class=\"goToLocation\" src=\"" + Config.getMediaUrl() +
		"images/misc/blank.gif\"/ style=\"float: right; margin: 0 5px;\">" +
		description;

String date;
if ((int) Math.floor(Utilities.getCurrentTime() / (3600 * 24)) ==
		(int) Math.floor(event.getDate() / (3600 * 24)))
	date = DateTimeFormat.getFormat(messages.timeFormat()
		).format(new Date((long) (1000 * event.getDate())));
else
	date = DateTimeFormat.getFormat(messages.dateFormat()
		).format(new Date((long) (1000 * event.getDate())));

return "<tr class=\"" + (odd ? "odd" : "even") + "\" style=\"padding: 0;\">" +
	"<td class=\"date\" style=\"width: 60px;\" style=\"padding: 4px;\">" +
	date + "</td><td style=\"width: 16px;\"><img src=\"" + Config.getMediaUrl() +
	"images/misc/blank.gif\" class=\"icon icon" + icon + "\"/></td>" +
	"<td class=\"" + type + " small\" style=\"padding: 4px;\">" +
	description + "</td></tr>";
}

private String getChargeName(String type, int count) {
DynamicMessages messages = GWT.create(DynamicMessages.class);

String key = "ward";
if (type.equals(WardData.TYPE_MINE))
	key += "Mine";
else if (type.equals(WardData.TYPE_MINE_INVISIBLE))
	key += "MineInvisible";
else if (type.equals(WardData.TYPE_STUN))
	key += "Stun";
else if (type.equals(WardData.TYPE_STUN_INVISIBLE))
	key += "StunInvisible";

return messages.getString(key + (count > 1 ? "s" : "")).toLowerCase();
}
}
