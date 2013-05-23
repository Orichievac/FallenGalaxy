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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.data.AllyData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.DiplomacyData;
import fr.fg.client.data.DiplomacyStateData;
import fr.fg.client.data.TreatyData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSList;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSScrollPane;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;
import fr.fg.client.openjwt.ui.SelectionListener;

public class DiplomacyDialog extends JSDialog implements ActionCallback,
		SelectionListener, ClickListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int
		VIEW_PLAYERS = 0,
		VIEW_ALLIES = 1,
		VIEW_OPTIONS = 2;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private DiplomacyData diplomacyData;
	
	private Action currentAction;
	
	private int view;
	
	private JSTabbedPane tabs;
	
	private JSList treatiesList;
	
	private JSScrollPane treatiesScrollPane;
	
	private JSButton newTreatyBt, action1Bt, action2Bt, diplomacyBt;
	
	private JSRowLayout mainLayout, layout, optionsLayout;
	
	private JSLabel switchDiplomacyLabel;
	
	private DiplomacyStateData diplomacyState;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DiplomacyDialog() {
		super("<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"iconDiplomacy\"/> " +
				"Diplomatie", false, true, true);
		
		view = VIEW_PLAYERS;
		
		tabs = new JSTabbedPane();
		tabs.addTab("Joueurs");
		tabs.addTab("Alliances");
		tabs.addTab("Options");
		tabs.setPixelWidth(450);
		tabs.addSelectionListener(this);
		
		newTreatyBt = new JSButton("Proposer pacte / Déclarer Guerre");
		newTreatyBt.setPixelWidth(240);
		newTreatyBt.addClickListener(this);
		
		action1Bt = new JSButton();
		action1Bt.setPixelWidth(180);
		action1Bt.addClickListener(this);
		action1Bt.setVisible(false);
		
		action2Bt = new JSButton("Refuser");
		action2Bt.setPixelWidth(100);
		action2Bt.addClickListener(this);
		action2Bt.setVisible(false);
		
		treatiesList = new JSList();
		treatiesList.setPixelSize(448, 255);
		treatiesList.addSelectionListener(this);
		
		treatiesScrollPane = new JSScrollPane();
		treatiesScrollPane.setView(treatiesList);
		treatiesScrollPane.setPixelSize(450, 280);
		
		JSLabel diplomacyLabel = new JSLabel("Diplomatie&nbsp;&nbsp;");
		
		diplomacyBt = new JSButton("Activée");
		diplomacyBt.setPixelWidth(100);
		diplomacyBt.addClickListener(this);
		
		HTMLPanel diplomacyHelp = new HTMLPanel(
			"<div class=\"justify\" style=\"padding: 0 20px;\">Quand la " +
			"diplomatie est <b>activée</b>, vous pouvez déclarer des guerres, " +
			"être attaqué par d'autres joueurs et signer des coalitions. " +
			"Lorsque la diplomatie est <b>désactivée</b>, vous ne pouvez plus " +
			"attaquer ou être attaqué, ni signer de coalitions, ni posséder " +
			"de flottes pirates.<br/><br/>" +
			"Attention, si vous avez une alliance, vous pouvez toujours " +
			"déclarer la guerre et être attaqué par d'autres alliances.</div>"
		);
		diplomacyHelp.setPixelSize(450, 152);
		OpenJWT.setElementFloat(diplomacyHelp.getElement(), "left");
		
		switchDiplomacyLabel = new JSLabel();
		switchDiplomacyLabel.setAlignment(JSLabel.ALIGN_CENTER);
		switchDiplomacyLabel.setPixelWidth(450);
		
		optionsLayout = new JSRowLayout();
		optionsLayout.addRowSeparator(30);
		optionsLayout.addComponent(diplomacyLabel);
		optionsLayout.addComponent(diplomacyBt);
		optionsLayout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
		optionsLayout.addRowSeparator(10);
		optionsLayout.addComponent(switchDiplomacyLabel);
		optionsLayout.addRowSeparator(10);
		optionsLayout.addComponent(diplomacyHelp);
		
		layout = new JSRowLayout();
		layout.addComponent(newTreatyBt);
		layout.addComponent(action1Bt);
		layout.addComponent(action2Bt);
		layout.addComponent(new JSLabel("&nbsp;"));
		layout.addRowSeparator(3);
		layout.addComponent(treatiesScrollPane);
		
		mainLayout = new JSRowLayout();
		mainLayout.add(tabs);
		mainLayout.addRowSeparator(3);
		mainLayout.addComponent(layout);
		
		setComponent(mainLayout);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			currentAction = new Action("gettreaties", Action.NO_PARAMETERS, this);
			
			Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_DIPLOMACY);
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		this.diplomacyData = data.getDiplomacy();
		setDiplomacyState(diplomacyData.getDiplomacyState());
		
		updateUI();
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (sender == tabs) {
			view = newValue;
			
			newTreatyBt.setVisible(
				view == VIEW_PLAYERS || (view == VIEW_ALLIES &&
				Client.getInstance().getAllyDialog().getPlayerRank() >=
					Client.getInstance().getAllyDialog().getAlly(
						).getRequiredRank(AllyData.RIGHT_MANAGE_DIPLOMACY)));
			
			updateUI();
		} else if (sender == treatiesList) {
			if (view == VIEW_ALLIES &&
					Client.getInstance().getAllyDialog().getPlayerRank() <
						Client.getInstance().getAllyDialog().getAlly(
						).getRequiredRank(AllyData.RIGHT_MANAGE_DIPLOMACY))
				return;
			
			if (newValue == -1) {
				action1Bt.setVisible(false);
				action2Bt.setVisible(false);
			} else {
				TreatyData treatyData = (
					(TreatyUI) treatiesList.getSelectedItem()).treatyData;
				
				action1Bt.setPixelWidth(180);
				action2Bt.setVisible(false);
				
				int currentId = view == VIEW_PLAYERS ?
					Settings.getPlayerId() :
					Client.getInstance().getAllyDialog().getAlly().getId();
				
				if (treatyData.getType().equals("war")) {
					if (treatyData.getSource() == 0) {
						action1Bt.setLabel("Proposer une trêve");
					} else if (treatyData.getSource() == currentId) {
						action1Bt.setLabel("Annuler proposition");
					} else {
						action1Bt.setLabel("Accepter");
						action1Bt.setPixelWidth(100);
						action2Bt.setVisible(true);
					}
				} else {
					if (treatyData.getSource() == 0) {
						action1Bt.setLabel("Rompre le pacte");
					} else if (treatyData.getSource() == currentId) {
						action1Bt.setLabel("Annuler proposition");
					} else {
						action1Bt.setLabel("Accepter");
						action1Bt.setPixelWidth(100);
						action2Bt.setVisible(true);
					}
				}
				
				action1Bt.setVisible(true);
			}
			
			layout.update();
		}
	}
	
	public void onClick(Widget sender) {
		if (sender == newTreatyBt) {
			NewTreatyDialog dialog = new NewTreatyDialog(view, this);
			dialog.setVisible(true);
		} else if (sender == action1Bt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			TreatyData treatyData = (
				(TreatyUI) treatiesList.getSelectedItem()).treatyData;
			
			HashMap<String, String> params = new HashMap<String, String>();
			String uri = view == VIEW_PLAYERS ? "" : "allies/";
			
			int currentId = view == VIEW_PLAYERS ?
				Settings.getPlayerId() :
				Client.getInstance().getAllyDialog().getAlly().getId();
			
			if (treatyData.getType().equals("war")) {
				uri += "offerpeace";
				
				if (treatyData.getSource() == 0)
					params.put("accept", "true");
				else if (treatyData.getSource() == currentId)
					params.put("accept", "false");
				else
					params.put("accept", "true");
			} else {
				if (treatyData.getSource() == 0) {
					uri += "breakally";
				} else if (treatyData.getSource() == currentId) {
					params.put("accept", "false");
					uri += "offerally";
				} else {
					params.put("accept", "true");
					uri += "offerally";
				}
			}
			
			params.put(view == VIEW_PLAYERS ? "player" : "ally", treatyData.getTarget());
			
			currentAction = new Action(uri, params, this);
		} else if (sender == action2Bt) {
			if (currentAction != null && currentAction.isPending())
				return;
			
			TreatyData treatyData = (
					(TreatyUI) treatiesList.getSelectedItem()).treatyData;

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("accept", "false");
			
			String uri = view == VIEW_PLAYERS ? "" : "allies/";
			
			if (treatyData.getType().equals("war")) {
				uri += "offerpeace";
			} else {
				uri += "offerally";
			}
			
			params.put(view == VIEW_PLAYERS ? "player" : "ally", treatyData.getTarget());
			
			currentAction = new Action(uri, params, this);
		} else if (sender == diplomacyBt) {
			// (Dés)activation de la diplomatie
			if (diplomacyState.getEffectiveRemainingTime() != 0) {
				JSOptionPane.showMessageDialog(
					"Voulez-vous annuler le changement de mode ?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.YES_OPTION) {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("active", String.valueOf(diplomacyState.isActivated()));
								
								new Action("setdiplomacystate", params, new ActionCallbackAdapter() {
									@Override
									public void onSuccess(AnswerData data) {
										setDiplomacyState(data.getDiplomacyState());
									}
								});
							}
						}
				});
			} else {
				JSOptionPane.showMessageDialog(
					"Voulez-vous " + (diplomacyState.isActivated() ?
					"désactiver" : "activer") + " la diplomatie (le changement " +
					"sera effectif dans 4 jours) ?", "Confirmation",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.YES_OPTION) {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("active", String.valueOf(!diplomacyState.isActivated()));
								
								new Action("setdiplomacystate", params, new ActionCallbackAdapter() {
									@Override
									public void onSuccess(AnswerData data) {
										setDiplomacyState(data.getDiplomacyState());
									}
								});
							}
						}
				});
			}
		}
	}
	
	public void setDiplomacyState(DiplomacyStateData diplomacyState) {
		this.diplomacyState = diplomacyState;
		
		int days = diplomacyState.getEffectiveRemainingTime() == 0 ? 0 :
			1 + diplomacyState.getEffectiveRemainingTime() / (3600 * 24);
		
		diplomacyBt.setLabel(diplomacyState.isActivated() ? "Activée" : "Désactivée");
		switchDiplomacyLabel.setText("<span style=\"color: red;\">" + (days == 0 ? "" :
			"Changement dans " + days + " jour" + (days > 1 ? "s" : "")) + "</span>");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateUI() {
		ArrayList<TreatyUI> treatiesUI = new ArrayList<TreatyUI>();
		
		switch (view) {
		case VIEW_PLAYERS:
			for (int i = 0; i < diplomacyData.getPlayerTreatiesCount(); i++)
				treatiesUI.add(new TreatyUI(diplomacyData.getPlayerTreatyAt(i), view));
			
			Collections.sort(treatiesUI, new Comparator<TreatyUI>() {
				public int compare(TreatyUI o1, TreatyUI o2) {
					return o1.treatyData.getDate() > o2.treatyData.getDate() ? -1 : 1;
				}
			});
			
			layout.clear();
			layout.addComponent(newTreatyBt);
			layout.addComponent(action1Bt);
			layout.addComponent(action2Bt);
			layout.addComponent(new JSLabel("&nbsp;"));
			layout.addRowSeparator(3);
			layout.addComponent(treatiesScrollPane);
			
			treatiesList.setItems(treatiesUI);
			break;
		case VIEW_ALLIES:
			for (int i = 0; i < diplomacyData.getAllyTreatiesCount(); i++)
				treatiesUI.add(new TreatyUI(diplomacyData.getAllyTreatyAt(i), view));

			Collections.sort(treatiesUI, new Comparator<TreatyUI>() {
				public int compare(TreatyUI o1, TreatyUI o2) {
					return o1.treatyData.getDate() > o2.treatyData.getDate() ? -1 : 1;
				}
			});
			
			layout.clear();
			layout.addComponent(newTreatyBt);
			layout.addComponent(action1Bt);
			layout.addComponent(action2Bt);
			layout.addComponent(new JSLabel("&nbsp;"));
			layout.addRowSeparator(3);
			layout.addComponent(treatiesScrollPane);
			
			treatiesList.setItems(treatiesUI);
			break;
		case VIEW_OPTIONS:
			layout.clear();
			layout.addComponent(optionsLayout);
			break;
		}
		
		mainLayout.update();
	}

	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class TreatyUI {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private TreatyData treatyData;
		
		private int view;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public TreatyUI(TreatyData treatyData, int view) {
			this.treatyData = treatyData;
			this.view = view;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		@Override
		public String toString() {
			String style = "", label = "";
			
			int currentId = view == VIEW_PLAYERS ?
				Settings.getPlayerId() :
				Client.getInstance().getAllyDialog().getAlly().getId();
			
			if (treatyData.getType().equals("war")) {
				style = "war";
				
				if (treatyData.getSource() == 0)
					label = "En guerre";
				else if (treatyData.getSource() == currentId)
					label = "Proposition de paix envoyée";
				else
					label = "Proposition de paix reçue";
			} else {
				style = "allied";
				int type = treatyData.getType().equals("ally")? 0 : (treatyData.getType().equals("defensive")? 1 : 2);
				if (treatyData.getSource() == 0){
					switch(type) {
					case 0:
						label = "Pacte de Non Agression";
						break;
					case 1:
						label = "Pacte Défensif";
						break;
					case 2:
						label = "Pacte Total";
						break;
					}
				}
				else if (treatyData.getSource() == currentId) {
					switch(type) {
					case 0:
						label = "Proposition pacte de Non Agression envoyée";
						break;
					case 1:
						label = "Proposition Pacte Défensif envoyée";
						break;
					case 2:
						label = "Proposition Pacte Total envoyée";
						break;
					}
				}
				else {
					switch(type) {
					case 0:
						label = "Proposition pacte de Non Agression reçue";
						break;
					case 1:
						label = "Proposition Pacte Défensif reçue";
						break;
					case 2:
						label = "Proposition Pacte Total reçue";
						break;
					}
				}
			}
			
			StaticMessages messages = GWT.create(StaticMessages.class);
			
			return "<div class=\"treaty\">" +
				"<table cellspacing=\"0\" style=\"width: 100%;\"><tr>" +
				"<td style=\"width: 35%;\">" + treatyData.getTarget() + "</td>" +
				"<td class=\"center " + style + "\" style=\"width: 30%;\">" + label + "</td>" +
				"<td class=\"date right\" style=\"width: 35%;\">Depuis le " +
				DateTimeFormat.getFormat(messages.dateFormat()).format(
				new Date((long) (1000 * treatyData.getDate()))) + "&nbsp;</td>" +
				"</tr></table></div>";
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
