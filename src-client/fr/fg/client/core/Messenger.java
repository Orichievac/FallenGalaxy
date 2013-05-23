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
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.core.tactics.TacticsTools;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.MessageBoxData;
import fr.fg.client.data.MessageData;
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

public class Messenger extends JSDialog implements SelectionListener,
		ClickListener, ActionCallback, WindowResizeListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int
		MESSAGES_PER_PAGE = 25;
	
	private final static int
		FOLDER_INBOX = 0,
		FOLDER_SENT = 1,
		FOLDER_ARCHIVES = 2;
	
	private final static int
		HIGH_RES_EXTRA_WIDTH = 180,
		HIGH_RES_EXTRA_HEIGHT = 140;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int currentClientWidth;
	
	private boolean highres;
	
	private JSTabbedPane foldersPane;
	
	private JSButton writeBt, answerBt, deleteBt, bookmarkBt, previousPageBt,
		nextPageBt, closeBt, forwardBt;
	
	private JSList messagesList;
	
	private JSScrollPane messageScrollPane;
	
	private HTMLPanel messagePanel;
	
	private JSLabel pageLabel, separator;
	
	private JSRowLayout layout;
	
	private int currentFolder, currentPage;
	
	private long lastUpdate;
	
	private int unreadMessages;
	
	private HTMLPanel horizontalSeparator;
	
	private Action downloadAction;
	
	private ArrayList<MessageData> inboxMessages, sentboxMessages,
		bookmarkedMessages;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Messenger() {
		super("<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"iconMessages\"/> " +
				"Messages", false, true, true);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		// Date du dernier téléchargement des messages
		lastUpdate = 0;
		unreadMessages = 0;
		currentClientWidth = OpenJWT.getClientWidth();
		highres = currentClientWidth > 1024;
		
		currentFolder = FOLDER_INBOX;
		currentPage = 0;
		
		inboxMessages = new ArrayList<MessageData>();
		sentboxMessages = new ArrayList<MessageData>();
		bookmarkedMessages = new ArrayList<MessageData>();
		
		// Choix de la boite
		ArrayList<String> folders = new ArrayList<String>();
		folders.add("<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"tabIcon iconMsgInbox\"/> " +
				messages.inbox());
		folders.add("<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"tabIcon iconMsgSent\"/> " +
				messages.sentbox());
		folders.add("<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"tabIcon iconMsgArchives\"/> " +
				messages.archives());
		
		foldersPane = new JSTabbedPane();
		foldersPane.setPixelWidth(550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0));
		foldersPane.addTabs(folders);
		foldersPane.addSelectionListener(this);
		
		// Boutons de la barre d'outils
		writeBt = new JSButton(messages.write());
		writeBt.setPixelWidth(180);
		writeBt.addClickListener(this);
		
		deleteBt = new JSButton(messages.delete());
		deleteBt.setPixelWidth(100);
		deleteBt.addClickListener(this);
		deleteBt.setVisible(false);
		
		answerBt = new JSButton(messages.answer());
		answerBt.setPixelWidth(100);
		answerBt.addClickListener(this);
		answerBt.setVisible(false);
		
		bookmarkBt = new JSButton(messages.bookmark());
		bookmarkBt.setPixelWidth(100);
		bookmarkBt.addClickListener(this);
		bookmarkBt.setVisible(false);
		
		closeBt = new JSButton(messages.close());
		closeBt.setPixelWidth(100);
		closeBt.addClickListener(this);
		closeBt.setVisible(false);
		
		forwardBt = new JSButton("Transférer");
		forwardBt.setPixelWidth(100);
		forwardBt.addClickListener(this);
		forwardBt.setVisible(false);
		
		// Pages suivante / précédente
		previousPageBt = new JSButton();
		previousPageBt.setPixelWidth(31);
		previousPageBt.addStyleName("iconLeft");
		previousPageBt.addClickListener(this);
		previousPageBt.setVisible(false);
		
		nextPageBt = new JSButton();
		nextPageBt.setPixelWidth(31);
		nextPageBt.addStyleName("iconRight");
		nextPageBt.addClickListener(this);
		nextPageBt.setVisible(false);
		
		pageLabel = new JSLabel();
		pageLabel.setPixelWidth(60);
		pageLabel.setAlignment(JSLabel.ALIGN_CENTER);
		
		separator = new JSLabel();
		separator.setPixelWidth(248 + (highres ? HIGH_RES_EXTRA_WIDTH : 0));
		
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
		horizontalSeparator.setWidth(550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0) + "px");
		horizontalSeparator.setVisible(false);
		OpenJWT.setElementFloat(horizontalSeparator.getElement(), "left");
		
		// Mise en forme des composants
		layout = new JSRowLayout();
		layout.addComponent(foldersPane);
		layout.addRowSeparator(3);
		layout.addComponent(writeBt);
		layout.addComponent(separator);
		layout.addComponent(previousPageBt);
		layout.addComponent(pageLabel);
		layout.addComponent(nextPageBt);
		layout.addRowSeparator(1);
		layout.addComponent(messagesList);
		layout.addRowSeparator(1);
		layout.addComponent(horizontalSeparator);
		layout.addRowSeparator(1);
		layout.addComponent(messageScrollPane);
		layout.addRow();
		layout.addComponent(answerBt);
		layout.addComponent(forwardBt);
		layout.addComponent(deleteBt);
		layout.addComponent(bookmarkBt);
		layout.addComponent(closeBt);
		
		setComponent(layout);
		centerOnScreen();
		
		Window.addWindowResizeListener(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible) {
			if (downloadAction != null && downloadAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("update", String.valueOf(lastUpdate));
			
			downloadAction = new Action("messages/get", params, this);
		}
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		if (sender == foldersPane) {
			messagesList.setSelectedIndex(-1);
			currentFolder = newValue;
			currentPage = 0;
			updateUI();
		} else if (sender == messagesList) {
			if (newValue != -1) {
				// Affiche le contenu du message
				MessageData currentMessage = getSelectedMessage();
				
				String date = DateTimeFormat.getFormat(messages.dateTimeFormat()).format(
						new Date((long) (1000 * currentMessage.getDate())));
				
				messagePanel.getElement().setInnerHTML(
					"<div style=\"padding: 3px;\">" +
					"<div class=\"messageHeader\">" + 
					(currentMessage.hasTargetPlayer() ?
					"<span class=\"owner-" + TreatyData.getPact(currentMessage.getTreaty()) + "\">" +
					(currentMessage.hasAllyTag() ?
					"<span unselectable=\"on\" class=\"allyTag\">[" +
					currentMessage.getAllyTag() + "]</span> " : "") +
					currentMessage.getTargetPlayer() + "</span> - " : "") +
					currentMessage.getTitle() + "</div>" +
					"<div style=\"padding: 5px 0;\">" +
					TacticsTools.parseTacticsLinks(Utilities.parseUrlAndSmilies(
						currentMessage.getContent())) + "</div>" +
					"<div class=\"small messageDate\">Envoyé le " +
					date + "</div></div>");
				
				horizontalSeparator.setVisible(true);
				messagesList.setPixelSize(
					550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0),
					70 + (highres ? HIGH_RES_EXTRA_HEIGHT / 2 : 0));
				messageScrollPane.setVisible(true);
				deleteBt.setVisible(true);
				answerBt.setVisible(currentMessage.hasTargetPlayer());
				bookmarkBt.setVisible(Settings.isPremium() &&
						currentFolder == FOLDER_INBOX &&
						!currentMessage.isBookmark());
				forwardBt.setVisible(true);
				closeBt.setVisible(true);
				
				// Marque le message comme lu si ce n'est pas le cas
				if (!currentMessage.isRead()) {
					currentMessage.setRead(true);
					messagesList.setItemAt(new MessageUI(currentMessage), newValue);
					
					unreadMessages--;
					
					if (unreadMessages == 0)
						Client.getInstance().getToolBar().blinkMessages(false);
					
					HashMap<String, String> params =
						new HashMap<String, String>();
					params.put("id", String.valueOf(currentMessage.getId()));
					
					new Action("messages/setread", params);
				}
			} else {
				horizontalSeparator.setVisible(false);
				messagesList.setPixelSize(
					550 + (highres ? HIGH_RES_EXTRA_WIDTH : 0),
					280 + (highres ? HIGH_RES_EXTRA_HEIGHT : 0));
				messageScrollPane.setVisible(false);
				messagePanel.getElement().setInnerHTML("");
				
				deleteBt.setVisible(false);
				answerBt.setVisible(false);
				bookmarkBt.setVisible(false);
				forwardBt.setVisible(false);
				closeBt.setVisible(false);
			}
			messageScrollPane.update();
			messageScrollPane.scrollUp(99999);
			layout.update();
		}
	}

	public void onClick(Widget sender) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		if (sender == writeBt) {
			WriteMessageDialog dialog = new WriteMessageDialog(
					WriteMessageDialog.TYPE_MESSAGE,
					lastUpdate,
					WriteMessageDialog.OPTION_RECEIVER |
					WriteMessageDialog.OPTION_TITLE);
			dialog.setVisible(true);
		} else if (sender == deleteBt) {
			// Supprime le message sélectionné
			if (messagesList.getSelectedIndex() != -1) {
				JSOptionPane.showMessageDialog(
					messages.confirmDel(), "Confirmation",
					JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.OK_OPTION) {
								MessageData message = getSelectedMessage();
								
								HashMap<String, String> params =
									new HashMap<String, String>();
								params.put("id", String.valueOf(message.getId()));
								
								new Action("messages/delete", params);
								
								// Supprime le message de la liste des messages
								getCurrentMessages().remove(
										messagesList.getSelectedIndex());
								
								if (currentFolder == FOLDER_ARCHIVES) {
									for (int i = 0; i < inboxMessages.size(); i++)
										if (inboxMessages.get(i).getId() == message.getId()) {
											inboxMessages.remove(i);
											break;
										}
								}
								
								messagesList.removeItemAt(
										messagesList.getSelectedIndex());
							}
						}
					});
			}
		} else if (sender == forwardBt) {
			// Transférer un message
			MessageData message = getSelectedMessage();
			String title = message.getTitle();
			
			if (!title.startsWith("Tr :"))
				title = "Tr : " + title;
			
			if (title.length() > 30)
				title = title.substring(0, 30);
			
			WriteMessageDialog dialog = new WriteMessageDialog(
				WriteMessageDialog.TYPE_MESSAGE,
				lastUpdate,
				WriteMessageDialog.OPTION_RECEIVER |
				WriteMessageDialog.OPTION_TITLE,
				"", title, message.getContent());
			dialog.setVisible(true);
		} else if (sender == answerBt) {
			// Réponse à un message
			MessageData message = getSelectedMessage();
			
			// Titre du message, en ajoutant Re : ou Re(xx) : au titre
			String title;
			String messageTitle = message.getTitle();
			if (messageTitle.indexOf("Re(") == 0 ||
					messageTitle.indexOf("Re :") == 0) {
				if (messageTitle.indexOf("Re(") == 0 &&
						messageTitle.indexOf(") :") != -1) {
					String number = messageTitle.substring(
						3, messageTitle.indexOf(")"));
					
					try {
						int index = Integer.parseInt(number);
						
						if (index > 0)
							title = "Re(" + (index + 1) + ") :" +
								messageTitle.substring(6 + number.length());
						else
							title = "Re : " + messageTitle;
					} catch (Exception e) {
						title = "Re : " + messageTitle;
					}
				} else {
					title = "Re(2) : " + messageTitle.substring(4);
				}
			} else {
				title = "Re : " + messageTitle;
			}
			
			if (title.length() > 30)
				title = title.substring(0, 30);
			
			WriteMessageDialog dialog = new WriteMessageDialog(
					WriteMessageDialog.TYPE_MESSAGE,
					lastUpdate,
					WriteMessageDialog.OPTION_RECEIVER |
					WriteMessageDialog.OPTION_TITLE,
					message.getTargetPlayer(), title);
			dialog.setVisible(true);
		} else if (sender == bookmarkBt) {
			// Archivage du message
			final MessageData message = getSelectedMessage();
			
			if (!message.isBookmark()) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("id", String.valueOf(message.getId()));
				
				new Action("messages/bookmark", params, new ActionCallbackAdapter() {
					public void onSuccess(AnswerData data) {
						// Marque le message comme archivé
						message.setBookmark(true);
						for (MessageData msg : inboxMessages)
							if (msg.getId() == message.getId()) {
								msg.setBookmark(true);
								break;
							}
						
						messagesList.setItemAt(new MessageUI(message),
								messagesList.getSelectedIndex());
						
						bookmarkBt.setVisible(false);
						layout.update();
						
						// Ajoute le message aux messages archivés
						for (int i = 0; i < bookmarkedMessages.size(); i++) {
							if (bookmarkedMessages.get(i).getDate() < message.getDate()) {
								bookmarkedMessages.add(i, message);
								return;
							}
						}
						bookmarkedMessages.add(message);
					}
				});
			}
		} else if (sender == nextPageBt) {
			currentPage++;
			updateUI();
		} else if (sender == previousPageBt) {
			currentPage--;
			updateUI();
		} else if (sender == closeBt) {
			messagesList.setSelectedIndex(-1);
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		MessageBoxData box = data.getMessageBox();
		
		this.lastUpdate = (long) box.getLastUpdate();
		
		boolean updateUI = false;
		
		// Messages reçus
		for (int i = 0; i < box.getReceivedMessageCount(); i++) {
			inboxMessages.add(0, box.getReceivedMessageAt(i));
			
			if (box.getReceivedMessageAt(i).isBookmark()) {
				bookmarkedMessages.add(0, box.getReceivedMessageAt(i));
				
				if (currentFolder == FOLDER_ARCHIVES)
					updateUI = true;
			}
			
			if (currentFolder == FOLDER_INBOX)
				updateUI = true;
		}
		
		// Messages envoyés
		for (int i = 0; i < box.getSentMessageCount(); i++) {
			sentboxMessages.add(0, box.getSentMessageAt(i));
			
			if (currentFolder == FOLDER_SENT)
				updateUI = true;
		}
		
		if (updateUI)
			updateUI();
	}
	
	public int getUnreadMessages() {
		return unreadMessages;
	}
	
	public void setUnreadMessages(int unreadMessages) {
		this.unreadMessages = unreadMessages;
		
		if (unreadMessages > 0)
			Client.getInstance().getToolBar().blinkMessages(true);
		
		if (isVisible()) {
			if (downloadAction != null && downloadAction.isPending())
				return;
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("update", String.valueOf(lastUpdate));
			
			downloadAction = new Action("messages/get", params, this);
		}
	}
	
	public void onWindowResized(int width, int height) {
		int clientWidth = Window.getClientWidth();
		highres = clientWidth > 1024;
		
		if (currentClientWidth > 1024 && clientWidth <= 1024) {
			foldersPane.setPixelWidth(550);
			messagesList.setPixelSize(550,
				(messagesList.getSelectedIndex() != -1 ? 70 : 280));
			messageScrollPane.setPixelSize(550, 182);
			horizontalSeparator.setWidth("550px");
			separator.setPixelWidth(248);
			
			layout.update();
			
			updateUI();
			
			if (isVisible())
				centerOnScreen();
		} else if (currentClientWidth <= 1024 && clientWidth > 1024) {
			foldersPane.setPixelWidth(550 + HIGH_RES_EXTRA_WIDTH);
			messagesList.setPixelSize(
				550 + HIGH_RES_EXTRA_WIDTH,
				messagesList.getSelectedIndex() != -1 ?
				70 + HIGH_RES_EXTRA_HEIGHT / 2 :
				280 + HIGH_RES_EXTRA_HEIGHT);
			messageScrollPane.setPixelSize(
				550 + HIGH_RES_EXTRA_WIDTH,
				182 + HIGH_RES_EXTRA_HEIGHT / 2);
			horizontalSeparator.setWidth((550 + HIGH_RES_EXTRA_WIDTH) + "px");
			separator.setPixelWidth(248 + HIGH_RES_EXTRA_WIDTH);
			
			layout.update();
			
			updateUI();
			
			if (isVisible())
				centerOnScreen();
		}
		
		currentClientWidth = clientWidth;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private ArrayList<MessageData> getCurrentMessages() {
		switch (currentFolder) {
		case FOLDER_INBOX:
			return inboxMessages;
		case FOLDER_SENT:
			return sentboxMessages;
		case FOLDER_ARCHIVES:
			return bookmarkedMessages;
		}
		return null;
	}
	
	public MessageData getSelectedMessage() {
		return messagesList.getSelectedIndex() != -1 ?
				((MessageUI) messagesList.getSelectedItem()).getData() : null;
	}
	
	private void updateUI() {
		MessageData selectedMessage = getSelectedMessage();
		
		ArrayList<MessageData> messages = getCurrentMessages();
		
		if (messages.size() > MESSAGES_PER_PAGE) {
			int maxPage = (int) Math.ceil(
					messages.size() / (double) MESSAGES_PER_PAGE);
			pageLabel.setText("<b>" + (currentPage + 1) + "</b> / " + maxPage);
			pageLabel.setVisible(true);
			previousPageBt.setVisible(true);
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
		
		ArrayList<MessageUI> items = new ArrayList<MessageUI>();
		int limit = Math.min(messages.size(), (currentPage + 1) * MESSAGES_PER_PAGE);
		for (int i = currentPage * MESSAGES_PER_PAGE; i < limit; i++) {
			items.add(new MessageUI(messages.get(i)));
			
			if (selectedMessage != null && selectedMessage.getId() == messages.get(i).getId())
				selectedIndex = i;
		}
		
		messagesList.setItems(items);
		messagesList.setSelectedIndex(selectedIndex);
		messagesList.getScrollPane().scrollUp(9999);
	}
	
	private class MessageUI {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private MessageData data;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public MessageUI(MessageData data) {
			this.data = data;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public MessageData getData() {
			return data;
		}
		
		public String toString() {
			StaticMessages messages =
				(StaticMessages) GWT.create(StaticMessages.class);
			
			long now = Utilities.getCurrentTime();
			
			// Affiche l'heure quand le message a été envoyé dans la journée
			String date;
			if ((int) Math.floor(now / (3600 * 24)) ==
					(int) Math.floor(data.getDate() / (3600 * 24)))
				date = DateTimeFormat.getFormat(messages.timeFormat()
					).format(new Date((long) (1000 * data.getDate())));
			else
				date = DateTimeFormat.getFormat(messages.dateFormat()
					).format(new Date((long) (1000 * data.getDate())));
			
			return "<table " + (!data.isRead() ? "class=\"unread\" " : "") +
				"unselectable=\"on\" cellspacing=\"0\"><tr unselectable=\"on\">" +
				"<td unselectable=\"on\" style=\"width:  20px;\">" +
				(data.isBookmark() ? "<div class=\"bookmark\"></div>" : "") + "</td>" +
				"<td " + (data.hasTargetPlayer() ? "class=\"owner-" +
				TreatyData.getPact(data.getTreaty()) + "\" " : "") + "unselectable=\"on\" " +
				"style=\"width: " + (170 + (highres ? 40 : 0)) + "px;\">" + (data.hasTargetPlayer() ?
				(data.hasAllyTag() ? "<span unselectable=\"on\" class=\"allyTag\">[" +
				data.getAllyTag() + "]</span> " : "") + data.getTargetPlayer() : "-") + "</td>" +
				"<td unselectable=\"on\" style=\"width: " +
				(270 + (highres ? HIGH_RES_EXTRA_WIDTH - 60 : 0)) + "px;\">" +
				Utilities.parseSmilies(data.getTitle()) + "</td>" +
				"<td class=\"center\" unselectable=\"on\" style=\"width: " +
				(80 + (highres ? 20 : 0)) + "px;\">" + date + "</td></tr></table>";
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
