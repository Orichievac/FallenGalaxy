/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier

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

package fr.fg.client.core.settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.AreaContainer;
import fr.fg.client.core.Client;
import fr.fg.client.core.Tutorial;
import fr.fg.client.core.UpdateManager;
import fr.fg.client.core.Utilities;
import fr.fg.client.core.player.PlayerCardDialog;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.OptionsData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.SoundManager;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSComponent;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;
import fr.fg.client.openjwt.ui.SelectionListener;

public class OptionsDialog extends JSDialog implements ClickListener,
		SelectionListener, ActionCallback, OptionPaneListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// Ajouter un theme
	// 1. ajouter l'indice dans le paramètre theme de setsettings dans action-mapping
	// 2. ajouter le nom du thème dans OptionsData.THEMES
	// 3. ajouter le nom du thème dans GameConstants.THEMES
	// 4. ajouter la méthode themeX dans DynamicMessages
	
	public final static int
		CATEGORY_GENERAL = 0,
		CATEGORY_VIDEO = 1,
		CATEGORY_AUDIO = 2;
	
	public final static String
		SETTING_GRID = "grid",
		SETTING_BRIGHTNESS = "brightness",
		SETTING_FLEETS_SKIN = "skin",
		SETTING_GENERAL_VOLUME = "generalvol",
		SETTING_SOUND_VOLUME = "soundvol",
		SETTING_MUSIC_VOLUME = "musicvol",
		SETTING_CENSORSHIP = "censorship";
	
	public final static int
		VALUE_BRIGHTNESS_DARK = 0,
		VALUE_BRIGHTNESS_BRIGHT = 1;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSTabbedPane categoriesPane;
	
	private JSLabel graphicsLabel, gridLabel, brightnessLabel,
		censorshipLabel, fleetsSkinLabel, passwordLabel, themeLabel,
		soundVolumeLabel, musicVolumeLabel, generalVolumeLabel,
		optimizeConnectionLabel, fleetsNameLabel, resetTutoLabel, cardLabel, closeAccountLabel;
	
	private JSButton gridBt, brightnessBt, passwordBt, censorshipBt,
		optimizeConnectionBt, fleetsNameBt, customThemeBt, resetTutoBt, cardBt, closeAccountBt;
	
	private JSComboBox fleetsSkinComboBox, themeComboBox, graphicsComboBox,
		generalVolumeComboBox, soundVolumeComboBox, musicVolumeComboBox;
	
	private JSRowLayout layout, categoryLayout;
	
	private String customTheme;
	
	private Action currentAction;
	
	private String closeReason;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public OptionsDialog() {
		super(((StaticMessages) GWT.create(StaticMessages.class)).options(),
				false, true, true);
		
		StaticMessages messages = GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages = GWT.create(DynamicMessages.class);
		
		categoriesPane = new JSTabbedPane();
		categoriesPane.setPixelWidth(300);
		categoriesPane.addTab("Général");
		categoriesPane.addTab("Graphismes");
		categoriesPane.addTab("Audio");
		categoriesPane.addSelectionListener(this);
		
		// Préfixe nom des flottes
		String toolTipText =
			"<div class=\"title\">" + messages.optionFleetsName() + "</div>" +
			"<div class=\"justify\">" + messages.optionFleetsNameHelp() + "</div>";
		
		fleetsNameLabel = new JSLabel(
				"&nbsp;" + messages.optionFleetsName());
		fleetsNameLabel.setPixelWidth(200);
		fleetsNameLabel.setToolTipText(toolTipText, 200);
		
		fleetsNameBt = new JSButton(messages.optionChange());
		fleetsNameBt.setPixelWidth(100);
		fleetsNameBt.addClickListener(this);
		
		// Optimisation connexion
		toolTipText =
			"<div class=\"title\">" + messages.optionProxy() + "</div>" +
			"<div class=\"justify\">" + messages.optionProxyHelp() + "</div>";
		
		optimizeConnectionBt = new JSButton();
		optimizeConnectionBt.setPixelWidth(JSComponent.getUIPropertyInt(
			JSButton.UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT));
		optimizeConnectionBt.addClickListener(this);
		optimizeConnectionBt.setToolTipText(toolTipText, 200);
		optimizeConnectionBt.addStyleName("checkBox");
		
		if (Settings.isConnectionOptimized())
			optimizeConnectionBt.addStyleName("checked");
		
		optimizeConnectionLabel = new JSLabel(
				"&nbsp;" + messages.optionProxy());
		optimizeConnectionLabel.setPixelWidth(300 - optimizeConnectionBt.getPixelWidth());
		optimizeConnectionLabel.setToolTipText(toolTipText, 200);
		
		// Qualité graphique
		toolTipText =
			"<div class=\"title\">" + messages.optionGraphicsQuality() + "</div>" +
			"<div class=\"justify\">" + messages.optionGraphicsQualityHelp() + "</div>" +
			"<div class=\"emphasize\">" + messages.premium() + "</div>";
		
		graphicsLabel = new JSLabel(
				"&nbsp;" + messages.optionGraphicsQuality());
		graphicsLabel.setPixelWidth(200);
		graphicsLabel.setToolTipText(toolTipText, 200);
		
		ArrayList<String> graphicsQuality = new ArrayList<String>();
		graphicsQuality.add(messages.optionGraphicsQualityLow());
		if (Settings.isPremium()) {
			graphicsQuality.add(messages.optionGraphicsQualityAverage());
			graphicsQuality.add(messages.optionGraphicsQualityHigh());
			graphicsQuality.add(messages.optionGraphicsQualityMax());
		}
		
		graphicsComboBox = new JSComboBox();
		graphicsComboBox.setPixelWidth(100);
		graphicsComboBox.setItems(graphicsQuality);
		graphicsComboBox.setSelectedIndex(Config.getGraphicsQuality());
		graphicsComboBox.addSelectionListener(this);
		
		// Afficher / masquer la grille
		gridBt = new JSButton();
		gridBt.setPixelWidth(JSComponent.getUIPropertyInt(
			JSButton.UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT));
		gridBt.addClickListener(this);
		gridBt.addStyleName("checkBox");
		
		if (Settings.isGridVisible())
			gridBt.addStyleName("checked");
		
		gridLabel = new JSLabel(
				"&nbsp;" + messages.optionGrid());
		gridLabel.setPixelWidth(300 - gridBt.getPixelWidth());
		
		// Luminosité
		brightnessBt = new JSButton();
		brightnessBt.setPixelWidth(JSComponent.getUIPropertyInt(
			JSButton.UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT));
		brightnessBt.addClickListener(this);
		brightnessBt.addStyleName("checkBox");
		
		if (Settings.getBrightness() == VALUE_BRIGHTNESS_BRIGHT)
			brightnessBt.addStyleName("checked");
		
		brightnessLabel = new JSLabel("&nbsp;" + messages.optionBrightness());
		brightnessLabel.setPixelWidth(300 - brightnessBt.getPixelWidth());
		
		// Filtre language
		toolTipText =
			"<div class=\"title\">" + messages.optionCensorship() + "</div>" +
			"<div class=\"justify\">" + messages.optionCensorshipHelp() + "</div>";
		
		censorshipBt = new JSButton();
		censorshipBt.setPixelWidth(JSComponent.getUIPropertyInt(
			JSButton.UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT));
		censorshipBt.addClickListener(this);
		censorshipBt.setToolTipText(toolTipText, 200);
		censorshipBt.addStyleName("checkBox");
		
		if (Settings.isCensorshipActive())
			censorshipBt.addStyleName("checked");
		
		censorshipLabel = new JSLabel(
				"&nbsp;" + messages.optionCensorship());
		censorshipLabel.setPixelWidth(300 - censorshipBt.getPixelWidth());
		censorshipLabel.setToolTipText(toolTipText, 200);
		
		// Choix de la skin des flottes
		toolTipText =
			"<div class=\"title\">" + messages.optionFleetsSkin() + "</div>" +
			"<div class=\"emphasize\">" + messages.premium() + "</div>";
		
		fleetsSkinLabel = new JSLabel(
				"&nbsp;" + messages.optionFleetsSkin());
		fleetsSkinLabel.setPixelWidth(200);
		fleetsSkinLabel.setToolTipText(toolTipText, 200);
		
		ArrayList<String> fleetsSkin = new ArrayList<String>();
		int maxSkins = Settings.isPremium() ? 10 : 1;
		for (int i = 1; i <= maxSkins; i++)
			fleetsSkin.add("#" + i+"<div style=\"background-position: -"+(40*i)+"px -791px;\" unselectable=\"on\" class=\"fleetskinPreview\"></div>");
		
		fleetsSkinComboBox = new JSComboBox();
		fleetsSkinComboBox.setItems(fleetsSkin);
		fleetsSkinComboBox.setPixelWidth(100);
		fleetsSkinComboBox.setSelectedIndex(Settings.getFleetsSkin() - 1);
		
		// Mot de passe
		passwordLabel = new JSLabel(
				"&nbsp;" + messages.optionPassword());
		passwordLabel.setPixelWidth(200);
		
		passwordBt = new JSButton(messages.optionChange());
		passwordBt.setPixelWidth(100);
		passwordBt.addClickListener(this);
		
		categoryLayout = new JSRowLayout();
		
		//Remise à Zero des tutos
		resetTutoLabel = new JSLabel("&nbsp;Reset des Tutos");
		resetTutoLabel.setPixelWidth(200);
		
		resetTutoBt = new JSButton("Reset");
		resetTutoBt.setPixelWidth(100);
		resetTutoBt.addClickListener(this);
		
		//Carte du joueur
		cardLabel = new JSLabel("&nbsp;Présentation");
		cardLabel.setPixelWidth(200);
		
		cardBt = new JSButton("Modifier");
		cardBt.setPixelWidth(100);
		cardBt.addClickListener(this);
		
		// Fermer compte
		closeAccountLabel = new JSLabel("&nbsp;Fermer compte");
		closeAccountLabel.setPixelWidth(200);
		
		closeAccountBt = new JSButton("Sabordage !");
		closeAccountBt.setPixelWidth(100);
		closeAccountBt.addClickListener(this);
		
		// Volume général
		toolTipText =
			"<div class=\"title\">" + messages.optionGeneralVolume() + "</div>" +
			"<div class=\"emphasize\">" + messages.premium() + "</div>";
		
		generalVolumeLabel = new JSLabel(
			"&nbsp;" + messages.optionGeneralVolume());
		generalVolumeLabel.setPixelWidth(220);
		generalVolumeLabel.setToolTipText(toolTipText, 200);
		
		ArrayList<String> volumes = new ArrayList<String>();
		for (int i = 0; i <= 100; i += 10)
			volumes.add(i + "%");
		
		generalVolumeComboBox = new JSComboBox();
		generalVolumeComboBox.setPixelWidth(80);
		if (Settings.isPremium())
			generalVolumeComboBox.setItems(volumes);
		else
			generalVolumeComboBox.addItem(volumes.get(0));
		generalVolumeComboBox.setSelectedIndex(
				SoundManager.getInstance().getGeneralVolume() / 10);
		generalVolumeComboBox.addSelectionListener(this);
		
		// Volume de la musique
		toolTipText =
			"<div class=\"title\">" + messages.optionMusicVolume() + "</div>" +
			"<div class=\"emphasize\">" + messages.premium() + "</div>";
		
		musicVolumeLabel = new JSLabel(
				"&nbsp;" + messages.optionMusicVolume());
		musicVolumeLabel.setPixelWidth(220);
		musicVolumeLabel.setToolTipText(toolTipText, 200);
		
		musicVolumeComboBox = new JSComboBox();
		if (Settings.isPremium())
			musicVolumeComboBox.setItems(volumes);
		else
			musicVolumeComboBox.addItem(volumes.get(0));
		musicVolumeComboBox.setPixelWidth(80);
		musicVolumeComboBox.setSelectedIndex(
				SoundManager.getInstance().getMusicVolume() / 10);
		musicVolumeComboBox.addSelectionListener(this);
		
		// Volume des sons
		toolTipText =
			"<div class=\"title\">" + messages.optionSoundVolume() + "</div>" +
			"<div class=\"emphasize\">" + messages.premium() + "</div>";
		
		soundVolumeLabel = new JSLabel(
				"&nbsp;" + messages.optionSoundVolume());
		soundVolumeLabel.setPixelWidth(220);
		soundVolumeLabel.setToolTipText(toolTipText, 200);
		
		soundVolumeComboBox = new JSComboBox();
		if (Settings.isPremium())
			soundVolumeComboBox.setItems(volumes);
		else
			soundVolumeComboBox.addItem(volumes.get(0));
		soundVolumeComboBox.setPixelWidth(80);
		soundVolumeComboBox.setSelectedIndex(
				SoundManager.getInstance().getSoundVolume() / 10);
		soundVolumeComboBox.addSelectionListener(this);
		
		// Thème
		toolTipText =
			"<div class=\"title\">" + messages.optionTheme() + "</div>" +
			"<div class=\"justify\">" + messages.optionThemeHelp() + "</div>" +
			"<div class=\"emphasize\">" + messages.premium() + "</div>";
		
		themeLabel = new JSLabel(
				"&nbsp;" + messages.optionTheme());
		themeLabel.setPixelWidth(120);
		themeLabel.setToolTipText(toolTipText, 200);
		
		int index = -1;
		ArrayList<String> themes = new ArrayList<String>();
		for (int i = 0; i < OptionsData.THEMES.length; i++) {
			themes.add(dynamicMessages.getString("theme" + i));
			
			if (OptionsData.THEMES[i].equals(Config.getTheme()))
				index = i;
		}
		if (index == -1)
			index = OptionsData.THEMES.length;
		if (Settings.isPremium())
			themes.add("Personnalisé...");
		
		themeComboBox = new JSComboBox();
		if (Settings.isPremium())
			themeComboBox.setItems(themes);
		else
			themeComboBox.addItem(dynamicMessages.getString("theme" + index));
		themeComboBox.setPixelWidth(Settings.isPremium() &&
				index == OptionsData.THEMES.length ? 140 : 180);
		themeComboBox.setSelectedIndex(index);
		
		customThemeBt = new JSButton("...");
		customThemeBt.setPixelWidth(40);
		customThemeBt.addClickListener(this);
		customThemeBt.setVisible(index == OptionsData.THEMES.length);
		
		// Mise en forme des composants
		layout = new JSRowLayout();
		layout.addComponent(categoriesPane);
		layout.addRowSeparator(3);
		layout.addComponent(categoryLayout);
		updateCategoryLayout();
		
		setComponent(layout);
		setLocation(235, 55, false);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible)
			fleetsSkinComboBox.addSelectionListener(this);
		else
			fleetsSkinComboBox.removeSelectionListener(this);
		
		if (Settings.isPremium()) {
			if (visible)
				themeComboBox.addSelectionListener(this);
			else
				themeComboBox.removeSelectionListener(this);
		}
	}
	
	public void onClick(Widget sender) {
		if (sender == cardBt) {
			//Création et ouverture du dialog de modification de la carte joueur
			new PlayerCardDialog();
		} else if (sender == gridBt) {
			// Grille
			Settings.setGridVisible(!Settings.isGridVisible());
			
			if (Settings.isGridVisible())
				this.gridBt.addStyleName("checked");
			else
				this.gridBt.removeStyleName("checked");
			
			AreaContainer areaContainer = Client.getInstance().getAreaContainer();
			
			if (areaContainer.getArea() != null)
				areaContainer.getMap().setGridVisible(Settings.isGridVisible());
			
			saveSettings();
		} else if (sender == brightnessBt) {
			// Luminosité
			Settings.setBrightness(Settings.getBrightness() == VALUE_BRIGHTNESS_BRIGHT ?
					VALUE_BRIGHTNESS_DARK : VALUE_BRIGHTNESS_BRIGHT);
			
			if (Settings.getBrightness() == VALUE_BRIGHTNESS_BRIGHT)
				this.brightnessBt.addStyleName("checked");
			else
				this.brightnessBt.removeStyleName("checked");
			
			AreaContainer areaContainer = Client.getInstance().getAreaContainer();
			
			if (areaContainer.getArea() != null)
				areaContainer.setNebula(areaContainer.getArea().getNebula(), false);
			
			saveSettings();
		} else if (sender == censorshipBt) {
			// Filtre de langage
			Settings.setCensorshipActive(!Settings.isCensorshipActive());
			
			if (Settings.isCensorshipActive())
				this.censorshipBt.addStyleName("checked");
			else
				this.censorshipBt.removeStyleName("checked");
			
			saveSettings();
		} else if (sender == passwordBt) {
			// Changement de mot de passe
			PasswordDialog passwordDialog = new PasswordDialog();
			passwordDialog.setVisible(true);
		} else if (sender == optimizeConnectionBt) {
			// Optimisation connexion
			Settings.setConnectionOptimized(!Settings.isConnectionOptimized());
			
			if (Settings.isConnectionOptimized())
				this.optimizeConnectionBt.addStyleName("checked");
			else
				this.optimizeConnectionBt.removeStyleName("checked");
			
			saveSettings();
		} else if (sender == fleetsNameBt) {
			// Modification du nom des flottes achetées
			FleetNameDialog dialog = new FleetNameDialog();
			dialog.setVisible(true);
		} else if (sender == customThemeBt) {
			// Thème personnalisé
			showCustomThemeSelection();
		} else if (sender == resetTutoBt) {
			JSOptionPane.showMessageDialog("Si ils sont remis à zéro, les tutoriaux réapparaîtront "+
					"à tous les endroit où il y en a. "+
					"Êtes-vous sur de vouloir remettre à zéro "+
					"les tutoriaux?", "Remise à zéro des tutoriaux",
					JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
					JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
						public void optionSelected(Object option) {
							if ((Integer) option == JSOptionPane.YES_OPTION) {
								HashMap<String, String> params = new HashMap<String, String>();
								
								currentAction = new Action("resettuto", params, new ActionCallbackAdapter() {
									@Override
									public void onSuccess(AnswerData data) {
										Client.getInstance().getTutorial().resetTutorial();
										JSOptionPane.showMessageDialog("La remise à zéro des " +
											"tutoriaux a été effectuée avec succès!", "Remise à zéro des tutoriaux",
											JSOptionPane.OK_OPTION,
											JSOptionPane.INFORMATION_MESSAGE, null);
									}
								});
							}
							else return;
						}
					});
		} else if (sender == closeAccountBt) {
			// Fermeture du compte
			JSOptionPane.showMessageDialog("Attention : la fermeture du " +
				"compte est définitive et irréversible. Êtes-vous sur de " +
				"vouloir fermer votre compte ?", "Fermeture compte",
				JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if ((Integer) option == JSOptionPane.YES_OPTION) {
							JSOptionPane.showOptionDialog("Afin d'améliorer " +
								"la qualité du jeu, merci de nous indiquer " +
								"la raison qui motive la fermeture du compte :",
								"Fermeture compte", JSOptionPane.OK_OPTION |
								JSOptionPane.CANCEL_OPTION,
								JSOptionPane.QUESTION_MESSAGE,
								new OptionPaneListener() {
									public void optionSelected(Object option) {
										if (option != null) {
											closeReason = (String) option;
											
											if (option.equals("Autre")) {
												JSOptionPane.showInputDialog(
													"Merci de préciser la " +
													"raison de la fermeture " +
													"du compte :",
													"Fermeture compte",
													JSOptionPane.OK_OPTION |
													JSOptionPane.CANCEL_OPTION,
													JSOptionPane.QUESTION_MESSAGE,
													new OptionPaneListener() {
														public void optionSelected(
																Object option) {
															if (option != null) {
																closeReason = (String) option;
																showCloseAccountPasswordConfirmation();
															}
														}
													}, "");
											} else {
												showCloseAccountPasswordConfirmation();
											}
										}
									}
								}, new String[]{
									"Trop d'investissement",
									"Ne m'intéresse pas",
									"Harcèlement de joueurs",
									"Autre"}, "Trop d'investissement");
						}
					}
			});
		}
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (sender == fleetsSkinComboBox) {
			// Modèle de flottes
			Settings.setFleetsSkin(newValue + 1);
			saveSettings();
		} else if (sender == themeComboBox) {
			// Thèmes
			boolean custom = Settings.isPremium() &&
				newValue == OptionsData.THEMES.length;
			customThemeBt.setVisible(custom);
			themeComboBox.setPixelWidth(custom ? 140 : 180);
			if (newValue != OptionsData.THEMES.length)
				saveSettings();
			else
				showCustomThemeSelection();
		} else if (sender == generalVolumeComboBox) {
			// Volume général
			SoundManager.getInstance().setGeneralVolume(newValue * 10);
			saveSettings();
		} else if (sender == musicVolumeComboBox) {
			// Volume de la musique
			SoundManager.getInstance().setMusicVolume(newValue * 10);
			saveSettings();
		} else if (sender == soundVolumeComboBox) {
			// Volume des sons
			SoundManager.getInstance().setSoundVolume(newValue * 10);
			saveSettings();
		} else if (sender == graphicsComboBox) {
			// Options graphiques
			saveSettings();
		} else if (sender == categoriesPane) {
			// Met à jour la catégorie sélectionnée
			updateCategoryLayout();
		}
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		UpdateManager.UPDATE_CALLBACK.onSuccess(data);
		
		if (graphicsComboBox.getSelectedIndex() != Config.getGraphicsQuality() ||
				(Settings.isPremium() &&
				!Config.getTheme().equals(themeComboBox.getSelectedIndex() ==
					OptionsData.THEMES.length ? customTheme :
					OptionsData.THEMES[themeComboBox.getSelectedIndex()]))) {
			if (Settings.isPremium())
				// Sauvegarde le thème de l'utilisateur dans un cookie
				// (durée d'un an)
				Cookies.setCookie("theme",
					themeComboBox.getSelectedIndex() ==
						OptionsData.THEMES.length ? customTheme :
						OptionsData.THEMES[themeComboBox.getSelectedIndex()],
					new Date(1000l * Utilities.getCurrentTime() + 31536000000l));
			
			JSOptionPane.showMessageDialog("Fallen Galaxy doit être rechargé " +
				"pour pouvoir modifier les options graphiques. Voulez-vous " +
				"rechargez le jeu immédiatement ?", "Avertissement",
				JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
				JSOptionPane.QUESTION_MESSAGE, this);
		}
	}
	
	public void optionSelected(Object option) {
		if ((Integer) option == JSOptionPane.OK_OPTION) {
			Window.Location.reload();
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void showCustomThemeSelection() {
		JSOptionPane.showInputDialog(
			"Indiquez l'URL du thème que vous souhaitez charger :<br/><br/>" +
			"<a href=\"http://wiki.fallengalaxy.com/index.php?title=Liste_des_th%C3%A8mes\" " +
			"target=\"_blank\">Thèmes additionnels...</a>",
			"Thème personnalisé",
			JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
			JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
				public void optionSelected(Object option) {
					if (option != null) {
						String value = (String) option;
						value = value.trim();
						if (value.endsWith("/"))
							value = value.substring(0, value.length() - 1);
						
						if (!value.startsWith("http://") ||
								value.length() < 12) {
							JSOptionPane.showMessageDialog("URL invalide.",
								"Erreur", JSOptionPane.OK_OPTION,
								JSOptionPane.ERROR_MESSAGE, null);
						} else {
							customTheme = value;
							saveSettings();
						}
					}
				}
			}, customTheme != null ? customTheme : "http://");
	}
	
	private void updateCategoryLayout() {
		categoryLayout.clear();
		
		switch (categoriesPane.getSelectedIndex()) {
		case CATEGORY_GENERAL:

			categoryLayout.addComponent(cardLabel);
			categoryLayout.addComponent(cardBt);
			categoryLayout.addRow();
			categoryLayout.addComponent(censorshipLabel);
			categoryLayout.addComponent(censorshipBt);
			categoryLayout.addRow();
			categoryLayout.addComponent(optimizeConnectionLabel);
			categoryLayout.addComponent(optimizeConnectionBt);
			categoryLayout.addRow();
			categoryLayout.addComponent(passwordLabel);
			categoryLayout.addComponent(passwordBt);
			categoryLayout.addRow();
			categoryLayout.addComponent(fleetsNameLabel);
			categoryLayout.addComponent(fleetsNameBt);
			categoryLayout.addRow();
			categoryLayout.addComponent(resetTutoLabel);
			categoryLayout.addComponent(resetTutoBt);
			categoryLayout.addRow();
			categoryLayout.addComponent(closeAccountLabel);
			categoryLayout.addComponent(closeAccountBt);
			break;
		case CATEGORY_VIDEO:
			boolean custom = Settings.isPremium() &&
				themeComboBox.getSelectedIndex() == OptionsData.THEMES.length;
			customThemeBt.setVisible(custom);
			themeComboBox.setPixelWidth(custom ? 140 : 180);
			categoryLayout.addComponent(themeLabel);
			categoryLayout.addComponent(themeComboBox);
			categoryLayout.addComponent(customThemeBt);
			categoryLayout.addRow();
			categoryLayout.addComponent(graphicsLabel);
			categoryLayout.addComponent(graphicsComboBox);
			categoryLayout.addRow();
			categoryLayout.addComponent(fleetsSkinLabel);
			categoryLayout.addComponent(fleetsSkinComboBox);
			categoryLayout.addRow();
			categoryLayout.addComponent(gridLabel);
			categoryLayout.addComponent(gridBt);
			categoryLayout.addRow();
			categoryLayout.addComponent(brightnessLabel);
			categoryLayout.addComponent(brightnessBt);
			break;
		case CATEGORY_AUDIO:
			// Volume général
			categoryLayout.addComponent(generalVolumeLabel);
			categoryLayout.addComponent(generalVolumeComboBox);
			categoryLayout.addRow();
			categoryLayout.addComponent(musicVolumeLabel);
			categoryLayout.addComponent(musicVolumeComboBox);
			categoryLayout.addRow();
			categoryLayout.addComponent(soundVolumeLabel);
			categoryLayout.addComponent(soundVolumeComboBox);
			categoryLayout.addRow();
			categoryLayout.addComponent(JSRowLayout.createHorizontalSeparator(300));
			categoryLayout.addRow();
			categoryLayout.addComponent(JSRowLayout.createHorizontalSeparator(300));
			break;
		}
		
		layout.update();
	}
	
	private void saveSettings() {
		if (currentAction != null && currentAction.isPending())
			return;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("grid", String.valueOf(Settings.isGridVisible()));
		params.put("brightness", String.valueOf(Settings.getBrightness()));
		params.put("fleetsSkin", String.valueOf(Settings.getFleetsSkin()));
		params.put("theme", themeComboBox.getSelectedIndex() == OptionsData.THEMES.length ?
			(customTheme != null ? customTheme : OptionsData.THEMES[0]) :
			OptionsData.THEMES[themeComboBox.getSelectedIndex()]);
		params.put("generalvol", String.valueOf(SoundManager.getInstance().getGeneralVolume()));
		params.put("musicvol", String.valueOf(SoundManager.getInstance().getMusicVolume()));
		params.put("soundvol", String.valueOf(SoundManager.getInstance().getSoundVolume()));
		params.put("graphics", String.valueOf(graphicsComboBox.getSelectedIndex()));
		params.put("censorship", String.valueOf(Settings.isCensorshipActive()));
		params.put("optimConnect", String.valueOf(Settings.isConnectionOptimized()));
		
		currentAction = new Action("setsettings", params, this);
	}
	
	private void showCloseAccountPasswordConfirmation() {
		JSOptionPane.showInputDialog(
			"Saississez votre mot de passe pour fermer le compte :",
			"Fermeture compte",
			JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
			JSOptionPane.QUESTION_MESSAGE,
			new OptionPaneListener() {
				public void optionSelected(Object option) {
					if (option != null) {
						if (currentAction != null && currentAction.isPending())
							return;
						
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("password", (String) option);
						params.put("reason", closeReason);
						
						currentAction = new Action("closeaccount", params, new ActionCallbackAdapter() {
							@Override
							public void onSuccess(AnswerData data) {
								JSOptionPane.showMessageDialog("Un email vous a " +
									"été envoyé avec un lien pour valider la " +
									"fermeture du compte.", "Fermeture compte",
									JSOptionPane.OK_OPTION,
									JSOptionPane.INFORMATION_MESSAGE, null);
							}
						});
					}
				}
			}, "", true);
	}
}
