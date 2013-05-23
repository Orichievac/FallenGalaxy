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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AchievementData;
import fr.fg.client.data.AchievementsData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSScrollPane;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.SelectionListener;

public class AchievementDialog extends JSDialog implements ActionCallback,
		SelectionListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSTabbedPane tabs;
	
	private HTMLPanel container;
	
	private JSScrollPane scrollPane;
	
	private Action downloadAction;
	
	private AchievementsData achievements;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AchievementDialog() {
		super("Trophées", false, true, true);
		
		tabs = new JSTabbedPane();
		tabs.addTab("En cours");
		tabs.addTab("Réussi");
		tabs.setPixelWidth(420);
		tabs.addSelectionListener(this);
		
		container = new HTMLPanel("");
		OpenJWT.setElementFloat(container.getElement(), "left");
		
		scrollPane = new JSScrollPane();
		scrollPane.setPixelSize(420, 300);
		scrollPane.setView(container);
		
		updateUI();
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(tabs);
		layout.addRowSeparator(5);
		layout.addComponent(scrollPane);
		
		setComponent(layout);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible) {
			Client.getInstance().getTutorial().setLesson(Tutorial.LESSON_ACHIEVEMENTS);
			
			if (downloadAction != null && downloadAction.isPending())
				return;
			
			downloadAction = new Action("getachievements", Action.NO_PARAMETERS, this);
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		this.achievements = data.getAchievements();
		
		updateUI();
	}

	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (sender == tabs) {
			updateUI();
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private long getScore(int type) {
		if (achievements == null)
			return 0;
		
		for (int i = 0; i < achievements.getAchievementsCount(); i++)
			if (achievements.getAchievementAt(i).getType() == type)
				return (long) achievements.getAchievementAt(i).getScore();
		
		return 0;
	}
	
	private int getLevel(int type) {
		long score = getScore(type);
		
		for (int i = 0; i < 5; i++)
			if (score < AchievementData.REQUIRED_SCORE[type][i])
				return i;
		
		return 5;
	}
	
	private void updateUI() {
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		StringBuffer html = new StringBuffer();
		html.append("<div style=\"padding: 5px;\">");
		
		String[] medals = {"bronze", "silver", "gold", "silverCup", "goldCup"};
		
		switch (tabs.getSelectedIndex()) {
		case 0:
			// Trophées en cours
			for (int i = 0; i < 20; i++) {
				int level = getLevel(i);
				
				if (level != 5) {
					html.append(
						"<div class=\"achievement\">" +
						"<div class=\"title\"><div class=\"medal " + medals[level] + "\"></div>" +
						dynamicMessages.getString("achievementLevel" + (level + 1)) + " " +
						dynamicMessages.getString("achievement" + i) + "</div>" +
						"<div class=\"details\">" +
						"<div class=\"justify\">" +
						dynamicMessages.getString("achievement" + i + "Desc") + "</div>" +
						"<div class=\"justify emphasize\">" +
						Formatter.formatNumber(getScore(i), true) + " / " +
						Formatter.formatNumber(AchievementData.REQUIRED_SCORE[i][level], true) + " " +
						dynamicMessages.getString("achievement" + i + "Goal") + "</div>" +
						"</div>" +
						"</div>"
					);
				}
			}
			break;
		case 1:
			// Trophées réussis
			for (int i = 0; i < 20; i++) {
				int level = getLevel(i);
				
				if (level > 0) {
					html.append(
						"<div class=\"achievement\">" +
						"<div class=\"title\"><div class=\"medal " + medals[level - 1] + "\"></div>" +
						dynamicMessages.getString("achievementLevel" + level) + " " +
						dynamicMessages.getString("achievement" + i) + "</div>" +
						"<div class=\"details\">" +
						"<div class=\"justify\">" +
						dynamicMessages.getString("achievement" + i + "Desc") + "</div>" +
						"<div class=\"justify emphasize\">" +
						Formatter.formatNumber(AchievementData.REQUIRED_SCORE[i][level - 1], true) + " / " +
						Formatter.formatNumber(AchievementData.REQUIRED_SCORE[i][level - 1], true) + " " +
						dynamicMessages.getString("achievement" + i + "Goal") + "</div>" +
						"</div>" +
						"</div>"
					);
				}
			}
			break;
		}
		
		html.append("</div>");
		
		container.getElement().setInnerHTML(html.toString());
		scrollPane.update();
	}
}
