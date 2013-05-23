/*
Copyright 2010 Jeremie Gottero, Nicolas

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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.ContractAttendeeData;
import fr.fg.client.data.ContractData;
import fr.fg.client.data.ContractRelationshipData;
import fr.fg.client.data.ContractRewardData;
import fr.fg.client.data.ContractsData;
import fr.fg.client.data.RelationshipData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.ToolTipManager;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSScrollPane;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;
import fr.fg.client.openjwt.ui.SelectionListener;

public class ContractDialog extends JSDialog implements ActionCallback,
		SelectionListener, ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int
		VIEW_CONTRACT_OFFERS = 0,
		VIEW_ACTIVE_CONTRACTS = 1,
		VIEW_RELATIONSHIPS = 2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSTabbedPane tabbedPane;
	
	private JSScrollPane missionsPane;
	
	private Action currentAction;
	
	private ContractsData contracts;
	
	private JSButton[] acceptBt, declineBt, giveUpBt;
	
	private int gotoTab;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ContractDialog() {
		super("Missions", false, true, true);
		
		acceptBt = new JSButton[0];
		declineBt = new JSButton[0];
		giveUpBt = new JSButton[0];
		gotoTab = -1;
		
		ArrayList<String> tabs = new ArrayList<String>();
		tabs.add("Propositions");
		tabs.add("En cours");
		tabs.add("Relations");
		
		tabbedPane = new JSTabbedPane();
		tabbedPane.setPixelWidth(500);
		tabbedPane.setTabs(tabs);
		tabbedPane.addSelectionListener(this);
		
		missionsPane = new JSScrollPane();
		missionsPane.setPixelSize(500, 340);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(tabbedPane);
		layout.addRowSeparator(5);
		layout.addComponent(missionsPane);
		
		setComponent(layout);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setContracts(ContractsData contracts) {
		this.contracts = contracts;
		
		if (gotoTab != -1) {
			tabbedPane.setSelectedIndex(gotoTab);
			gotoTab = -1;
		}
		
		updateUI();
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}

	public void onSuccess(AnswerData data) {
		setContracts(data.getContracts());
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (sender == tabbedPane) {
			updateUI();
		}
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		updateUI();
		Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_CONTRACT);
	}
	
	public void onClick(Widget sender) {
		if (currentAction != null && currentAction.isPending())
			return;
		
		switch (tabbedPane.getSelectedIndex()) {
		case VIEW_CONTRACT_OFFERS:
			for (int i = 0; i < contracts.getContractOffersCount(); i++) {
				if (sender == acceptBt[i]) {
					gotoTab = VIEW_ACTIVE_CONTRACTS;
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("id", String.valueOf((long) contracts.getContractOfferAt(i).getId()));
					
					currentAction = new Action("contracts/apply", params, UpdateManager.UPDATE_CALLBACK);
					break;
				} else if (sender == declineBt[i]) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("id", String.valueOf((long) contracts.getContractOfferAt(i).getId()));
					
					currentAction = new Action("contracts/decline", params, UpdateManager.UPDATE_CALLBACK);
					break;
				}
			}
		case VIEW_ACTIVE_CONTRACTS:
			for (int i = 0; i < contracts.getActiveContractsCount(); i++) {
				if (sender == giveUpBt[i]) {
					final long id = (long) contracts.getActiveContractAt(i).getId();
					JSOptionPane.showMessageDialog("Attention, abandonner " +
						"une mission nuira à vos relations avec la faction " +
						"qui vous l’a proposé !", "Avertissement",
						JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
						JSOptionPane.WARNING_MESSAGE, new OptionPaneListener() {
							public void optionSelected(Object option) {
								if ((Integer) option == JSOptionPane.OK_OPTION) {
									giveUp(id);
								}
							}
					});
					break;
				}
			}
			break;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void giveUp(long idContract) {
		if (currentAction != null && currentAction.isPending())
			return;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(idContract));
		
		currentAction = new Action("contracts/giveup", params, UpdateManager.UPDATE_CALLBACK);
	}
	
	private void updateUI() {
		double scroll = missionsPane.getScrollOffset();
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		JSRowLayout layout = new JSRowLayout();
		
		switch (tabbedPane.getSelectedIndex()) {
		case VIEW_CONTRACT_OFFERS:
			acceptBt = new JSButton[contracts.getContractOffersCount()];
			declineBt = new JSButton[contracts.getContractOffersCount()];
			
			for (int i = 0; i < contracts.getContractOffersCount(); i++) {
				ContractData contract = contracts.getContractOfferAt(i);
				
				if (i > 0) {
					JSLabel separator = new JSLabel();
					separator.setPixelWidth(450);
					layout.addRow();
					layout.addComponent(separator);
					layout.addRow();
				}
				
				StringBuffer missionDescription = new StringBuffer();
				
				missionDescription.append("<div style=\"padding: 5px; line-height: 20px;\">");
				missionDescription.append("<div class=\"title\">" + contract.getTitle() + "</div>");
				missionDescription.append("<div class=\"emphasize\">"+contract.getMissionType()+"</div>");
				missionDescription.append("<div class=\"small\" style=\"font-style: italic;\">" + contract.getDescription() + "</div>");
				missionDescription.append("<div><div style=\"float: left; width: 120px;\">Objectif</div><div style=\"padding-left: 120px;\">" + contract.getGoal() + "</div></div>");
				
				// Quadrants dans lesquels la mission a lieu
				missionDescription.append("<div><div style=\"float: left; width: 120px;\">Quadrants</div><div style=\"padding-left: 120px;\">");
				for (int j = 0; j < contract.getSectorsCount(); j++) {
					if (j > 0)
						missionDescription.append(", ");
					missionDescription.append(contract.getSectorAt(j));
				}
				missionDescription.append("</div></div>");
				
				missionDescription.append("<div><div style=\"float: left; width: 120px;\">Début</div>Immédiat</div>");
				
				// Récompense de la mission
				missionDescription.append("<div><div style=\"float: left; width: 120px;\">Récompense</div>" +
					"<div style=\"padding-left: 120px;\">");
				formatRewards(contract, missionDescription);
				missionDescription.append("</div></div>");

				// Relations
				missionDescription.append("<div><div style=\"float: left; width: 120px;\">Relations</div>" +
					"<div style=\"padding-left: 120px;\">");
				formatRelationships(contract, missionDescription);
				missionDescription.append("</div></div>");
				
				missionDescription.append("</div>");
				
				HTMLPanel missionDescriptionPanel = new HTMLPanel(missionDescription.toString());
				OpenJWT.setElementFloat(missionDescriptionPanel.getElement(), "left");
				missionDescriptionPanel.getElement().getStyle().setProperty("width", "100%");
				
				NodeList<Element> elements =
					missionDescriptionPanel.getElement().getElementsByTagName("span");
				
				for (int j = 0; j < elements.getLength(); j++) {
					Element element = elements.getItem(j);
					
					if (element.getClassName().toLowerCase().contains("allytag")) {
						String rel = element.getAttribute("rel");
						ToolTipManager.getInstance().register(element, rel);
					}
				}
				
				acceptBt[i] = new JSButton("Accepter");
				acceptBt[i].setPixelWidth(100);
				acceptBt[i].addClickListener(this);
				
				declineBt[i] = new JSButton("Refuser");
				declineBt[i].setPixelWidth(100);
				declineBt[i].addClickListener(this);
				
				layout.addComponent(missionDescriptionPanel);
				layout.addRow();
				layout.addComponent(acceptBt[i]);
				layout.addComponent(declineBt[i]);
			}
			break;
		case VIEW_ACTIVE_CONTRACTS:
			giveUpBt = new JSButton[contracts.getActiveContractsCount()];
			 
			for (int i = 0; i < contracts.getActiveContractsCount(); i++) {
				ContractData contract = contracts.getActiveContractAt(i);
				
				if (i > 0) {
					JSLabel separator = new JSLabel();
					separator.setPixelWidth(450);
					layout.addRow();
					layout.addComponent(separator);
					layout.addRow();
				}
				
				StringBuffer missionDescription = new StringBuffer();
				
				missionDescription.append("<div style=\"padding: 5px; line-height: 20px;\">");
				missionDescription.append("<div class=\"title\">" + contract.getTitle() + "</div>");
				missionDescription.append("<div class=\"emphasize\">"+contract.getMissionType()+"</div>");
				missionDescription.append("<div class=\"small\" style=\"font-style: italic;\">" + contract.getDescription() + "</div>");
				missionDescription.append("<div><div style=\"float: left; width: 120px;\">Objectif</div><div style=\"padding-left: 120px;\">" + contract.getGoal() + "</div></div>");
				
				// Participants à la mission
				missionDescription.append("<div><div style=\"float: left; width: 120px;\">Participants</div><div style=\"padding-left: 120px;\">");
				for (int j = 0; j < contract.getAttendeesCount(); j++) {
					if (j > 0)
						missionDescription.append(", ");
					
					ContractAttendeeData attendee = contract.getAttendeeAt(j);
					
					if (attendee.getType().equals(ContractAttendeeData.TYPE_PLAYER)) {
						if (attendee.getAllyTag().length() > 0)
							missionDescription.append("<span class=\"inlineAllyTag\" " +
								"rel=\"" + attendee.getAllyName() + "\">[" +
								attendee.getAllyTag() + "]</span>&nbsp;");
						missionDescription.append("<span class=\"owner-" +
							attendee.getTreaty() + "\">" +
							attendee.getLogin() + "</span>");
					} else {
						missionDescription.append("<span class=\"owner-" +
							attendee.getTreaty() + "\">" +
							attendee.getAllyName() + "</span>");
					}
				}
				missionDescription.append("</div></div>");
				
				// Récompense de la mission
				missionDescription.append("<div><div style=\"float: left; width: 120px;\">Récompense</div>" +
					"<div style=\"padding-left: 120px;\">");
				formatRewards(contract, missionDescription);
				missionDescription.append("</div></div>");

				// Relations
				missionDescription.append("<div><div style=\"float: left; width: 120px;\">Relations</div>" +
					"<div style=\"padding-left: 120px;\">");
				formatRelationships(contract, missionDescription);
				missionDescription.append("</div></div>");
				
				missionDescription.append("</div>");
				
				HTMLPanel missionDescriptionPanel = new HTMLPanel(missionDescription.toString());
				OpenJWT.setElementFloat(missionDescriptionPanel.getElement(), "left");
				missionDescriptionPanel.getElement().getStyle().setProperty("width", "100%");
				
				NodeList<Element> elements =
					missionDescriptionPanel.getElement().getElementsByTagName("span");
				
				for (int j = 0; j < elements.getLength(); j++) {
					Element element = elements.getItem(j);
					
					if (element.getClassName().toLowerCase().contains("allytag")) {
						String rel = element.getAttribute("rel");
						ToolTipManager.getInstance().register(element, rel);
					}
				}
				
				layout.addComponent(missionDescriptionPanel);
				
				if (!contract.isFinished() && 
						(contract.getMissionType().equals("Mission solo") || 
						contract.getMissionType().equals("Mission d'alliance"))) {
					giveUpBt[i] = new JSButton("Abandonner");
					giveUpBt[i].setPixelWidth(100);
					giveUpBt[i].addClickListener(this);
					
					layout.addRow();
					layout.addComponent(giveUpBt[i]);
				}
			}
			break;
		case VIEW_RELATIONSHIPS:
			String[] colors = {"#ff0000", "#ff3600", "#ff6c00", "#ffa201",
				"#ffd800", "#d8e201", "#b0ec01", "#89f501", "#62ff01"};
			
			layout.addRowSeparator(20);
			
			JSLabel relationTitle = new JSLabel("<div class=\"title\">Joueur</div>");
			relationTitle.setAlignment(JSLabel.ALIGN_CENTER);
			relationTitle.setPixelWidth(500);
			layout.addComponent(relationTitle);
			layout.addRow();
			for (int i = 0; i < contracts.getRelationshipsCount(); i++) {
				RelationshipData relationship = contracts.getRelationshipAt(i);
				if(relationship.getType()==0){
					if (i > 0)
						layout.addRow();
					
					JSLabel factionLabel = new JSLabel(
						"<img src=\"" + Config.getMediaUrl() +
						"images/misc/blank.gif\" class=\"faction faction-" +
						relationship.getFactionId() + "\"/>&nbsp;&nbsp;" +
						relationship.getFactionName());
					factionLabel.setPixelWidth(270);
					
					JSLabel relationLabel = new JSLabel(dynamicMessages.getString(
						"relationshipLevel" +
						(relationship.getLevel() < 0 ? "M" : "") +
						Math.abs(relationship.getLevel())) + " (" + relationship.getValue() + ")");
					relationLabel.setAlignment(JSLabel.ALIGN_CENTER);
					relationLabel.setPixelWidth(150);
					relationLabel.getElement().getStyle().setProperty("color", colors[relationship.getLevel() + 4]);
					
					layout.addComponent(JSRowLayout.createHorizontalSeparator(40));
					layout.addComponent(factionLabel);
					layout.addComponent(relationLabel);
				}
			}
			
			layout.addRowSeparator(10);
			
			JSLabel relationAllyTitle = new JSLabel("<div class=\"title\">Alliance</div>");
			relationAllyTitle.setAlignment(JSLabel.ALIGN_CENTER);
			relationAllyTitle.setPixelWidth(500);
			layout.addComponent(relationAllyTitle);
			layout.addRow();
			
			for (int i = 0; i < contracts.getAllyRelationshipsCount(); i++) {
				RelationshipData relationship = contracts.getAllyRelationshipAt(i);
				if(relationship.getType()==1){
						layout.addRow();
					
					JSLabel factionLabel = new JSLabel(
						"<img src=\"" + Config.getMediaUrl() +
						"images/misc/blank.gif\" class=\"faction faction-" +
						relationship.getFactionId() + "\"/>&nbsp;&nbsp;" +
						relationship.getFactionName());
					factionLabel.setPixelWidth(270);
					
					JSLabel relationLabel = new JSLabel(dynamicMessages.getString(
						"relationshipLevel" +
						(relationship.getLevel() < 0 ? "M" : "") +
						Math.abs(relationship.getLevel())) + " (" + relationship.getValue() + ")");
					relationLabel.setAlignment(JSLabel.ALIGN_CENTER);
					relationLabel.setPixelWidth(150);
					relationLabel.getElement().getStyle().setProperty("color", colors[relationship.getLevel() + 4]);
					
					layout.addComponent(JSRowLayout.createHorizontalSeparator(40));
					layout.addComponent(factionLabel);
					layout.addComponent(relationLabel);
				}
			}
			break;
		}
		
		missionsPane.setView(layout);
		missionsPane.setScrollOffset(scroll);
		layout.update();
	}
	
	private void formatRewards(ContractData contract, StringBuffer buffer) {
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		for (int j = 0; j < contract.getRewardsCount(); j++) {
			ContractRewardData reward = contract.getRewardAt(j);
			String label = "";
			
			if (reward.getType().equals(ContractRewardData.TYPE_XP)){
				if(contract.getMissionType().equals(ContractData.TYPE_SOLO)||
						contract.getMissionType().equals(ContractData.TYPE_PVP)||
						contract.getMissionType().equals(ContractData.TYPE_MULTIPLAYER))
					label = "<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"resource xp\"/>";
				else
					label = "<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"resource xp\"/> à partager.";
			}
			else if (reward.getType().equals(ContractRewardData.TYPE_FLEET_XP)){
				label = "<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"resource xp\"/> pour " + reward.getKey() + " flottes";
			}
			else if (reward.getType().equals(ContractRewardData.TYPE_RESOURCE)){
				if(contract.getMissionType().equals(ContractData.TYPE_SOLO)||
						contract.getMissionType().equals(ContractData.TYPE_PVP)||
						contract.getMissionType().equals(ContractData.TYPE_MULTIPLAYER))
					label = "<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"resource " + (reward.getKey() == 4 ? "credits" : reward.getKey()) + "\"/>";
				else
					label = "<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"resource " + (reward.getKey() == 4 ? "credits" : reward.getKey()) + "\"/> à partager.";	
			}
			else if (reward.getType().equals(ContractRewardData.TYPE_SHIP)){
				label = dynamicMessages.getString("ships" + reward.getType());
			}
			else if (reward.getType().equals(ContractRewardData.TYPE_CREDITS))
			{
				if(contract.getMissionType().equals(ContractData.TYPE_SOLO)||
						contract.getMissionType().equals(ContractData.TYPE_PVP)||
						contract.getMissionType().equals(ContractData.TYPE_MULTIPLAYER))
					label = "<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"resource credits" + "\"/>";
				else
					label = "<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"resource credits\"/> à partager.";
			}
				
			buffer.append(Formatter.formatNumber(reward.getValue(), true) + "&nbsp;" + label);
		}
	}
	
	private void formatRelationships(ContractData contract, StringBuffer buffer) {
		for (int j = 0; j < contract.getRelationshipsCount(); j++) {
			ContractRelationshipData relationship = contract.getRelationshipAt(j);
			
			if (j > 0)
				buffer.append("<br/>");
			buffer.append("<div style=\"float: left; width: 180px;" +
				(j > 0 ? " clear: both;" : "") + "\">" +
				"<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"faction faction-" +
				relationship.getFactionId() + "\"/>&nbsp;" +
				relationship.getFactionName() + "</div>" +
				"<span style=\"font-weight: bold; color: " +
				(relationship.getModifier() >= 0 ? "#00c000" : "red") + ";\">" +
				Utilities.parseSmilies(relationship.getModifier() > 0 ? ":)" : ":(") +
				"&nbsp;" + (relationship.getModifier() > 0 ? "+" : "" ) +
				relationship.getModifier() + "</span>");
		}
	}
}
