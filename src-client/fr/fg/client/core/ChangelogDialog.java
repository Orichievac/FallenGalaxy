/*
Copyright 2012 jgottero

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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.ChangelogData;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSList;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTextPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;

public class ChangelogDialog extends JSDialog implements ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSButton addBt, editBt, deleteBt;
	
	private JSList changelogsList;
	
	private Action action;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ChangelogDialog() {
		super("Changelogs", false, true, true);
		
		addBt = new JSButton("Ajouter");
		addBt.setPixelWidth(100);
		addBt.addClickListener(this);

		editBt = new JSButton("Editer");
		editBt.setPixelWidth(100);
		editBt.addClickListener(this);
		
		deleteBt = new JSButton();
		deleteBt.setPixelWidth(34);
		deleteBt.addStyleName("iconDelete");
		deleteBt.addClickListener(this);
		
		changelogsList = new JSList();
		changelogsList.setPixelSize(300, 300);
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(addBt);
		layout.addComponent(editBt);
		layout.addComponent(deleteBt);
		layout.addRow();
		layout.addComponent(changelogsList);
		
		setComponent(layout);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void onSuccess(AnswerData data) {
		while (changelogsList.getItemsCount() > 0)
			changelogsList.removeItemAt(0);
		for (int i = 0; i < data.getChangelogsData().getChangelogsCount(); i++) {
			changelogsList.addItem(new ChangelogUI(data.getChangelogsData().getChangelogAt(i)));
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible) {
			new Action("changelog/getlast", Action.NO_PARAMETERS, this);
		}
	}

	public void onClick(Widget sender) {
		if (sender == addBt) {
			EditChangelogDialog dialog = new EditChangelogDialog(-1, "");
			dialog.setVisible(true);
		} else if (sender == editBt) {
			if (changelogsList.getSelectedIndex() != -1) {
				ChangelogUI changelog = (ChangelogUI) changelogsList.getSelectedItem();
				
				EditChangelogDialog dialog = new EditChangelogDialog(
					changelog.getData().getId(), changelog.getData().getText());
				dialog.setVisible(true);
			}
		} else if (sender == deleteBt) {
			if (changelogsList.getSelectedIndex() != -1) {
				JSOptionPane.showMessageDialog("Voulez-vous supprimer ce message ?",
						"Confirmation", JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
						JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.YES_OPTION) {
							if (action != null && action.isPending())
								return;
							
							ChangelogUI changelog = (ChangelogUI) changelogsList.getSelectedItem();
							
							Map<String, String> params = new HashMap<String, String>();
							params.put("id", String.valueOf(changelog.getData().getId()));
							
							action = new Action("changelog/delete", params, ChangelogDialog.this);
						}
					}
				});
			}
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private class EditChangelogDialog extends JSDialog implements ClickListener {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int idChangelog;
		
		private JSButton saveBt, cancelBt;
		
		private JSTextPane editorPane;

		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public EditChangelogDialog(int idChangelog, String text) {
			super("Editer changelog", false, true, true);
			
			this.idChangelog = idChangelog;
			
			saveBt = new JSButton("Sauvegarder");
			saveBt.setPixelWidth(100);
			saveBt.addClickListener(this);
			
			cancelBt = new JSButton("Annuler");
			cancelBt.setPixelWidth(100);
			cancelBt.addClickListener(this);
			
			editorPane = new JSTextPane();
			editorPane.setPixelSize(350, 250);
			editorPane.setHTML(text);
			
			JSRowLayout layout = new JSRowLayout();
			layout.addComponent(editorPane);
			layout.addRowSeparator(5);
			layout.addComponent(saveBt);
			layout.addComponent(cancelBt);
			layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
			
			setComponent(layout);
			setDefaultCloseOperation(DESTROY_ON_CLOSE);
			centerOnScreen();
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void onClick(Widget sender) {
			if (sender == saveBt) {
				if (action != null && action.isPending())
					return;
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("id", String.valueOf(idChangelog));
				params.put("text", editorPane.getHTML());
				
				action = new Action("changelog/" +
						(idChangelog != -1 ? "edit" : "add"),
						params, new ActionCallbackAdapter() {
					@Override
					public void onSuccess(AnswerData data) {
						setVisible(false);
						ChangelogDialog.this.onSuccess(data);
					}
				});
			} else if (sender == cancelBt) {
				setVisible(false);
			}
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private static class ChangelogUI {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private ChangelogData data;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public ChangelogUI(ChangelogData data) {
			this.data = data;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public ChangelogData getData() {
			return data;
		}
		
		@Override
		public String toString() {
			return data.getText().length() > 200 ? data.getText().substring(0, 200) + "..." : data.getText();
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
