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

package fr.fg.client.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.core.ally.AllyDialog;
import fr.fg.client.core.LotteryDialog;
import fr.fg.client.core.login.FirstSystemDialog;
import fr.fg.client.core.login.LoginDialog;
import fr.fg.client.core.settings.OptionsDialog;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.core.tactics.PlayerTacticsDialog;
import fr.fg.client.core.tactics.TacticsDialog;
import fr.fg.client.data.AllyNewsData;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.ChangelogData;
import fr.fg.client.data.IndexedAreaData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.PlayerGeneratorData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.data.ServerData;
import fr.fg.client.data.Sounds;
import fr.fg.client.data.StateData;
import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.impl.MiniMap;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.BaseWidget;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Dimension;
import fr.fg.client.openjwt.core.EventManager;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.SoundManager;
import fr.fg.client.openjwt.ui.JSComponent;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSScrollPane;
import fr.fg.client.core.AwayManager;

public final class Client implements EntryPoint, WindowResizeListener,
		ActionCallback, WindowCloseListener, EventPreview {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static Client instance;
	
	private AreaContainer areaContainer;
	
	private ActionManager actionManager;
	
	private EmpireView empireView;
	
	private EmpireControlPanel empireControlPanel;
	
	private ProgressBar progressBar;
	
	private Chat chat;
	
	private MiniMap miniMap;
	
	private ToolBar toolBar;

	private NamePanel namePanel;
	
	private SelectionInfo selectionInfo;
	
	private Ladder ladder;
	
	private Messenger messenger;
	
	private AbsolutePanel fullScreenPanel;
	
	private BattleReport battleReport;
	
	private GalaxyMap galaxyMap;
	
	private SwapDialog swapDialog;
	
	private ResourcesManager resourcesManager;
	
	private ResearchManager researchManager;
	
	private ProductsManager productsManager;
	
	private DialogManager dialogManager;
	
	private LoginDialog loginDialog;
	
	private OptionsDialog optionsDialog;
	
	private MissionPanel missionPanel;
	
	private TacticsDialog tacticsDialog;
	
	private PlayerTacticsDialog playerTacticsDialog;

	private AllyDialog allyDialog;
	
	private EventDialog eventsDialog;
	
	private TradeDialog tradeDialog;
	
	private BankDialog bankDialog;
	
	private LotteryDialog lotteryDialog;
	
	private DiplomacyDialog diplomacyDialog;
	
	private PremiumDialog premiumDialog;
	
	//private ProductionDialog productionDialog;
	
	private ContactDialog contactDialog;
	
	private AchievementDialog achievementDialog;
	
	private AdvancementDialog advancementDialog;
	
	private ContractDialog contractDialog;
	
	private HelpDialog helpDialog;
	
	private BugReportDialog bugReportDialog;
	
	private CodexDialog codexDialog;
	
	private Lagometer lagometer;
	
	private Widget tipOfTheDay;

	private JSScrollPane changelogContainer;

	private Widget changelog;
	
	private Widget logo;
	
	private Widget serverShutdown;
	
	private Tutorial tutorial;
	
	private ServerShutdownUpdater serverShutdownUpdater;
	
	private FirstSystemDialog firstSystemDialog;
	
	private AdministrationPanelDialog administrationPanelDialog;

	private ChangelogDialog changelogDialog;
	
	private int currentClientWidth;
	
	private ArrayList<DialogState> dialogStates;
	
	private boolean systemInfoVisible;
	
	private boolean fullScreen;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void onModuleLoad() {
		instance = this;
		systemInfoVisible = false;
		
		setStatus("Chargement...");
		
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable e) {
				Utilities.log("Uncaught exception", e);
			}
		});
		
		// Charge le fichier XML de définition du thème
		if (Config.isDebug())
			OpenJWT.loadStyle(Config.getTheme() + "/style.xml");
		else
			OpenJWT.loadStyle(Config.getServerUrl() + "themeproxy?theme=" + URL.encodeComponent(Config.getTheme()));
		
		if (!OpenJWT.isStyleReady()) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					if (OpenJWT.isStyleReady()) {
						load();
					} else {
						schedule(100);
					}
				}
			};
			
			timer.schedule(100);
		} else {
			load();
		}
	}
	
	public void setStatus(String status) {
		DOM.getElementById("preloadingStatus").setInnerHTML(status);
	}
	
	public void hideLoading() {
		Element element = DOM.getElementById("preloading");
		element.getParentNode().removeChild(element);
	}
	
	public void load() {
		SoundManager.getInstance().configSound(Sounds.SMALL_SHOT1, 16, false);
		SoundManager.getInstance().configSound(Sounds.SMALL_SHOT2, 16, false);
		SoundManager.getInstance().configSound(Sounds.IMPACT, 70, false);
		SoundManager.getInstance().configSound(Sounds.EXPLOSION, 100, false);
		SoundManager.getInstance().configSound(Sounds.AVERAGE_SHOT1, 21, false);
		SoundManager.getInstance().configSound(Sounds.AVERAGE_SHOT2, 30, false);
		SoundManager.getInstance().configSound(Sounds.AVERAGE_SHOT2, 30, false);
		SoundManager.getInstance().configSound(Sounds.MISCLICK, 80, false);
		SoundManager.getInstance().configSound(Sounds.HYPERSPACE, 100, false);
		SoundManager.getInstance().configSound(Sounds.ENGINE, 100, false);
		SoundManager.getInstance().configMusic(Sounds.MUSIC1, 55);
		SoundManager.getInstance().configMusic(Sounds.MUSIC2, 55);
		SoundManager.getInstance().configMusic(Sounds.MUSIC3, 55);
		SoundManager.getInstance().configMusic(Sounds.MUSIC4, 55);
		
		dialogStates = new ArrayList<DialogState>();
		fullScreen = false;
		
		currentClientWidth = Window.getClientWidth();
		
		// Carte
		areaContainer = new AreaContainer();
		
		if (Window.getClientWidth() < 1024)
			RootPanel.get().addStyleName("lowres");
		
		// Panel pour les graphismes en plein écran
		fullScreenPanel = new AbsolutePanel();
		fullScreenPanel.getElement().setId("extra");
		fullScreenPanel.getElement().setAttribute("unselectable", "on");
		fullScreenPanel.setVisible(false);
		
		// Astuce du jour
		tipOfTheDay = new HTMLPanel("") {
			@Override
			public void onBrowserEvent(Event event) {
				switch (event.getTypeInt()) {
				case Event.ONCLICK:
					// Change le tooltip quand on clique dessus
					DynamicMessages messages =
						(DynamicMessages) GWT.create(DynamicMessages.class);
					
					tipOfTheDay.getElement().setInnerHTML(
						"<div class=\"icon\"></div>" + messages.getString(
							"tip" + (int) (15 * Math.random())));
					break;
				}
			}
		};
		tipOfTheDay.getElement().setId("tipOfTheDay");
		tipOfTheDay.getElement().setAttribute("unselectable", "on");
		tipOfTheDay.sinkEvents(Event.ONCLICK);

		// Changelog
		changelog = new HTMLPanel("");
		changelog.getElement().setAttribute("unselectable", "on");
		changelog.getElement().setId("changelog");
		
		changelogContainer = new JSScrollPane();
		changelogContainer.getElement().setId("changelog-container");
		changelogContainer.setView(changelog);
		changelogContainer.setPixelSize(270, OpenJWT.getClientHeight());
		
		// Logo du jeu
		logo = new BaseWidget();
		logo.getElement().setId("logo");
		logo.getElement().setAttribute("unselectable", "on");
		
		// Arrêt du serveur
		serverShutdown = new HTMLPanel("");
		serverShutdown.getElement().setId("serverShutdown");
		serverShutdown.getElement().setAttribute("unselectable", "on");
		
		// Fenêtre de connexion
		loginDialog = new LoginDialog(this);
		
		Window.addWindowResizeListener(this);
		Window.addWindowCloseListener(this);
		
		setStatus("Récupération des données...");
		
		if (Location.getHref().contains("battle")) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("hash", Location.getHref().substring(
					Location.getHref().lastIndexOf("/") + 1));
			
			new Action("getreportbyhash", params, new ActionCallbackAdapter() {
				public void onSuccess(AnswerData data) {
					hideLoading();
					
					RootPanel.get().add(areaContainer);
					RootPanel.get().add(fullScreenPanel);
					
					BattleReport battleReport = new BattleReport(true);
					battleReport.cacheReport(data.getReport());
					battleReport.showReport(data.getReport().getId());
				}
			});
		} else {
			new Action("initialization", Action.NO_PARAMETERS, this);
		}
	}

	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	public void onSuccess(AnswerData data) {
		StateData state = data.getState();
		
		switch (state.getState()) {
		case StateData.STATE_OFFLINE:
			// Joueur déconnecté
			LoginDialog dialog = getLoginDialog();
			
			// Liste des serveurs
			ArrayList<ServerData> servers = new ArrayList<ServerData>();
			for (int i = 0; i < state.getServersCount(); i++)
				servers.add(state.getServerAt(i));
			dialog.setServers(servers, state.getCurrentServerIndex());
			
			DynamicMessages messages =
				(DynamicMessages) GWT.create(DynamicMessages.class);

			StaticMessages staticMessages =
				(StaticMessages) GWT.create(StaticMessages.class);
			
			// Astuce du jour
			tipOfTheDay.getElement().setInnerHTML("<div class=\"icon\"></div>" +
					messages.getString("tip" + (int) (9 * Math.random())));

			String changelogHTML = "<div class=\"title\">Journal des modifications</div>";
			for (int i = 0; i < state.getChangelogsCount(); i++) {
				ChangelogData changelog = state.getChangelogAt(i);
				changelogHTML += "<div class=\"entry\">" +
					"<div class=\"date\">" + DateTimeFormat.getFormat(staticMessages.dateTimeFormat()
							).format(new Date(1000l * changelog.getDate())) + "</div>" +
					changelog.getText() + "</div>";
			}
			changelog.getElement().setInnerHTML(changelogHTML);
			changelogContainer.update();
			
			hideLoading();
			
			RootPanel.get().add(areaContainer);
			areaContainer.setNebula((int) (AreaContainer.NEBULAS_COUNT * Math.random()) + 1, true);
			dialog.setVisible(true);
			
			// Lien direct pour le parainage
			
			String currentUrl = Window.Location.getHref();
			int urlSearchIndex = currentUrl.indexOf("#par_:");
			
			if( urlSearchIndex != -1 ){
				String urlSponsorName = currentUrl.substring(urlSearchIndex + 6, currentUrl.indexOf(":par")).replace("/", "");
				String oldSponsorName = urlSponsorName;
				
				do{
					oldSponsorName = urlSponsorName;
					urlSponsorName = urlSponsorName.replace("%20", " ");
				}while( oldSponsorName != urlSponsorName );
				
				dialog.activateSponsor( urlSponsorName );
				
				Window.Location.replace(currentUrl.substring(0, urlSearchIndex+1));
			}
			
			RootPanel.get().add(tipOfTheDay);
			RootPanel.get().add(changelogContainer);
			RootPanel.get().add(logo);
			
			setServerShutdown(state.getServerShutdown());
			break;
		case StateData.STATE_ONLINE:
			// Sauvegarde le thème de l'utilisateur dans un cookie
			// (durée d'un an)
			Cookies.setCookie("theme",
					state.getOptions().getTheme(),
					new Date(1000l * Utilities.getCurrentTime() + 31536000000l));
			
			// Recharge la page si le thème de l'utilisateur ne correspond pas
			// au thème affiché
			if (!Config.isDebug() && !state.getOptions().getTheme().equals(Config.getTheme())) {
				Window.Location.reload();
				return;
			}
			
			// Chargement des options
			Settings.setGridVisible(state.getOptions().isGridVisible());
			Settings.setBrightness(state.getOptions().getBrightness());
			Settings.setFleetsSkin(state.getOptions().getFleetsSkin());
			Settings.setPlayerId(state.getPlayerId());
			Settings.setPlayerLogin(state.getPlayerLogin());
			Settings.setSecurityKey(state.getSecurityKey());
			Settings.setPremium(state.isPremium());
			Settings.setRights(state.getRights());
			Settings.setEkey(state.getEkey());
			Settings.setTimeUnit(state.getTimeUnit());
			Settings.setCensorshipActive(state.getOptions().isCensorshipActive());
			Settings.setConnectionOptimized(state.getOptions().isConnectionOptimized());
			Settings.setFleetPrefix(state.getOptions().getFleetPrefix());
			Settings.setFleetSuffix(state.getOptions().getFleetSuffix());
			SoundManager.getInstance().setGeneralVolume(state.getOptions().getGeneralVolume());
			SoundManager.getInstance().setSoundVolume(state.getOptions().getSoundVolume());
			SoundManager.getInstance().setMusicVolume(state.getOptions().getMusicVolume());
			Config.setGraphicsQuality(state.getOptions().getGraphicsQuality());
			
			Cookies.setCookie("login", state.getPlayerLogin(),
					new Date(1000l * Utilities.getCurrentTime() + 31536000000l));
			
			// Initialise les différents composants de l'interface
			int miniMapSize = Window.getClientWidth() < 1024 ? 120 : 160;
			miniMap = new MiniMap(new Dimension(
					miniMapSize, miniMapSize), areaContainer.getMap());
			areaContainer.addMiniMap(miniMap);
			actionManager = new ActionManager(areaContainer);
			resourcesManager = new ResourcesManager();
			researchManager = new ResearchManager();
			productsManager = new ProductsManager();
			progressBar = new ProgressBar();
			chat = new Chat();
			empireControlPanel = new EmpireControlPanel();
			empireView = new EmpireView();
			namePanel = new NamePanel();
			selectionInfo = new SelectionInfo();
			toolBar = new ToolBar();
			missionPanel = new MissionPanel();
			allyDialog = new AllyDialog();
			lagometer = new Lagometer();
			tutorial = new Tutorial();
			advancementDialog = new AdvancementDialog();
			playerTacticsDialog = new PlayerTacticsDialog(researchManager);
			contractDialog = new ContractDialog();
			bugReportDialog = new BugReportDialog();
			codexDialog = new CodexDialog();
			
			if(Settings.isModerator()) {
				administrationPanelDialog = new AdministrationPanelDialog();
			}
			
			// Zik-mu
			String[] playlist = new String[Sounds.MUSICS.length];
			for (int i = 0; i < playlist.length; i++)
				playlist[i] = Sounds.MUSICS[i];
			
			for (int i = playlist.length; i > 1; i--) {
                int a = i - 1;
                int b = (int) (Math.random() * i);
                
                String tmp = playlist[a];
                playlist[a] = playlist[b];
                playlist[b] = tmp;
			}
			
			SoundManager.getInstance().setPlaylist(playlist);
			SoundManager.getInstance().playMusic(0);
			
			if (loginDialog.isVisible()) {
				loginDialog.setVisible(false);
				RootPanel.get().remove(tipOfTheDay);
				RootPanel.get().remove(changelogContainer);
				RootPanel.get().remove(logo);
			} else if (firstSystemDialog != null && firstSystemDialog.isVisible()) {
				firstSystemDialog.setVisible(false);
				firstSystemDialog = null;
			} else {
				hideLoading();
				RootPanel.get().add(areaContainer);
			}
			
			// Ajoute les composants de l'interface à la page
			RootPanel.get().add(miniMap);
			RootPanel.get().add(progressBar);
			RootPanel.get().add(chat);
			RootPanel.get().add(empireControlPanel);
			RootPanel.get().add(empireView);
			RootPanel.get().add(namePanel);
			RootPanel.get().add(toolBar);
			RootPanel.get().add(fullScreenPanel);
			RootPanel.get().add(selectionInfo);
			RootPanel.get().add(missionPanel);
			RootPanel.get().add(lagometer);
			
			// Initialisation des composants
			productsManager.addProductsListener(allyDialog);
			productsManager.setProducts(state.getProducts());
			allyDialog.setAlly(state.getAlly());
			areaContainer.setArea(new IndexedAreaData(state.getArea()), new Point(
					state.getViewX(), state.getViewY()));
			areaContainer.getMap().setGridVisible(Settings.isGridVisible());
			researchManager.setResearches(state.getResearch());
			
			Client.getInstance().getProgressBar().setPlayerGeneratorsCount(state.getPlayerGenerators().getGeneratorsCount());
			
			ArrayList<PlayerStarSystemData> systems =
				new ArrayList<PlayerStarSystemData>();
			for (int i = 0; i < state.getPlayerSystems().getSystemsCount(); i++)
				systems.add(state.getPlayerSystems().getSystemAt(i));
			
			ArrayList<PlayerFleetData> fleets = new ArrayList<PlayerFleetData>();
			for (int i = 0; i < state.getPlayerFleets().getFleetsCount(); i++)
				fleets.add(state.getPlayerFleets().getFleetAt(i));
			
			ArrayList<PlayerGeneratorData> generators = new ArrayList<PlayerGeneratorData>();
			for (int i = 0; i < state.getPlayerGenerators().getGeneratorsCount(); i++)
				generators.add(state.getPlayerGenerators().getGeneratorAt(i));
			
			empireView.setData(systems, fleets, generators);
			resourcesManager.setSystems(systems);
			chat.addMessage("info", "", "", "Bienvenue sur Fallen Galaxy !", null, null,null, null,(Integer) null, (Integer) null);
			chat.addMessage("info", "", "", "Il y a actuellement " +
				state.getOnlinePlayers() + (state.getOnlinePlayers() > 1 ?
				" joueurs connectés." : " joueur connecté.") +
				(state.getLastConnection().length() > 0 ? " Dernière " +
				"connexion le " + state.getLastConnection() + "." : ""), null, null,null,null,(Integer) null, (Integer) null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("type", "chat");
			new Action("messages/getmotd", params, UpdateManager.UPDATE_CALLBACK);
			missionPanel.setMissionStates(state.getContractsStates());
			contractDialog.setContracts(state.getPlayerContracts());
			progressBar.setPlayerData(state.getXp(), state.getColonizationPoints());
			chat.setChannels(state.getChatChannels());
			tutorial.setLessonDone((long) state.getTutorial());
			advancementDialog.setAdvancements(state.getAdvancements());
			playerTacticsDialog.setPlayerTactics(state.getTactics());
			
			if (state.getUnreadMessages() > 0)
				getMessenger().setUnreadMessages(state.getUnreadMessages());
			if (state.hasNewEvents())
				getEventsDialog().setNewEvents(state.hasNewEvents());
			
			if (state.getAlly().getId() != 0) {
				int unreadNews = 0;
				for (int i = 0; i < state.getAlly().getNewsCount(); i++) {
					AllyNewsData allyNews = state.getAlly().getNewsAt(i);
					
					if (!allyNews.isRead())
						unreadNews++;
				}
				allyDialog.setUnreadNews(unreadNews);
			}
			
			EventManager.addEventHook(this);
			
			// Démarre la gestion des mises à jour en temps réel
			UpdateManager.start();
			
			AwayManager.start();
			
			setServerShutdown(state.getServerShutdown());
			break;
		case StateData.STATE_ALREADY_ONLINE:
			// Joueur déjà connecté
			Settings.setSecurityKey(state.getSecurityKey());
			setStatus("Vous êtes déjà connecté.");
			break;
		case StateData.STATE_NO_SYSTEM:
			Settings.setSecurityKey(state.getSecurityKey());
			
			if (loginDialog.isVisible()) {
				loginDialog.setVisible(false);
				RootPanel.get().remove(tipOfTheDay);
				RootPanel.get().remove(changelogContainer);
				RootPanel.get().remove(logo);
			} else {
				RootPanel.get().add(areaContainer);
				areaContainer.setNebula((int) (AreaContainer.NEBULAS_COUNT * Math.random() + 1), true);
			}
			
			firstSystemDialog = new FirstSystemDialog(this);
			firstSystemDialog.setVisible(true);
			
			setServerShutdown(state.getServerShutdown());
			break;
		}
	}
	
	public BattleReport getBattleReport() {
		if (battleReport == null)
			battleReport = new BattleReport(false);
		return battleReport;
	}
	
	public PremiumDialog getPremiumDialog() {
		if (premiumDialog == null)
			premiumDialog = new PremiumDialog();
		return premiumDialog;
	}
	
	/*public ProductionDialog getProductionDialog() {
		if (productionDialog == null)
			productionDialog = new ProductionDialog();
		return productionDialog;
	}*/
	
	public GalaxyMap getGalaxyMap() {
		if (galaxyMap == null)
			galaxyMap = new GalaxyMap();
		return galaxyMap;
	}
	
	
	
	public Tutorial getTutorial() {
		return tutorial;
	}

	public ResearchManager getResearchManager() {
		return researchManager;
	}
	
	public AreaContainer getAreaContainer() {
		return areaContainer;
	}

	public ActionManager getActionManager() {
		return actionManager;
	}
	
	public MiniMap getMiniMap() {
		return miniMap;
	}
	
	public ProgressBar getProgressBar() {
		return progressBar;
	}
	
	public OptionsDialog getOptionsDialog() {
		if (optionsDialog == null)
			optionsDialog = new OptionsDialog();
		return optionsDialog;
	}
	
	public Chat getChat() {
		return chat;
	}

	public EmpireControlPanel getEmpireControlPanel() {
		return empireControlPanel;
	}

	public EmpireView getEmpireView() {
		return empireView;
	}
	
	public ToolBar getToolBar() {
		return toolBar;
	}

	public NamePanel getNamePanel() {
		return namePanel;
	}
	
	public MissionPanel getMissionPanel() {
		return missionPanel;
	}
	
	public Lagometer getLagometer() {
		return lagometer;
	}
	
	public AdvancementDialog getAdvancementDialog() {
		return advancementDialog;
	}
	
	public SelectionInfo getSelectionInfo() {
		return selectionInfo;
	}
	
	public Ladder getLadder() {
		if (ladder == null)
			ladder = new Ladder();
		return ladder;
	}
	
	public Messenger getMessenger() {
		if (messenger == null)
			messenger = new Messenger();
		return messenger;
	}

	public AllyDialog getAllyDialog() {
		if (allyDialog == null)
			allyDialog = new AllyDialog();
		return allyDialog;
	}
	
	public DiplomacyDialog getDiplomacyDialog() {
		if (diplomacyDialog == null)
			diplomacyDialog = new DiplomacyDialog();
		return diplomacyDialog;
	}
	
	public AchievementDialog getAchievementDialog() {
		if (achievementDialog == null)
			achievementDialog = new AchievementDialog();
		return achievementDialog;
	}
	
	public ContactDialog getContactDialog() {
		if (contactDialog == null)
			contactDialog = new ContactDialog();
		return contactDialog;
	}
	
	public EventDialog getEventsDialog() {
		if (eventsDialog == null)
			eventsDialog = new EventDialog();
		return eventsDialog;
	}
	
	public TacticsDialog getTacticsDialog() {
		if (tacticsDialog == null)
			tacticsDialog = new TacticsDialog(getResearchManager(), getPlayerTacticsDialog());
		return tacticsDialog;
	}

	public PlayerTacticsDialog getPlayerTacticsDialog() {
		return playerTacticsDialog;
	}
	
	public SwapDialog getSwapDialog() {
		if (swapDialog == null)
			swapDialog = new SwapDialog();
		return swapDialog;
	}

	public TradeDialog getTradeDialog() {
		if (tradeDialog == null)
			tradeDialog = new TradeDialog();
		return tradeDialog;
	}

	public BankDialog getBankDialog() {
		if (bankDialog == null)
			bankDialog = new BankDialog(getResourcesManager());
		return bankDialog;
	}
	
	public DialogManager getDialogManager() {
		if (dialogManager == null)
			dialogManager = new DialogManager();
		return dialogManager;
	}
	
	public ResourcesManager getResourcesManager() {
		return resourcesManager;
	}
	
	public ProductsManager getProductsManager() {
		return productsManager;
	}
	
	public AbsolutePanel getFullScreenPanel() {
		return fullScreenPanel;
	}
	
	public ContractDialog getContractDialog() {
		if (contractDialog == null)
			contractDialog = new ContractDialog();
		return contractDialog;
	}
	
	public HelpDialog getHelpDialog() {
		if(helpDialog == null)
			helpDialog = new HelpDialog();
		return helpDialog;
	}
	
	public BugReportDialog getBugReportDialog() {
		if(bugReportDialog == null)
			bugReportDialog = new BugReportDialog();
		return bugReportDialog;
	}
	
	public AdministrationPanelDialog getAdministrationPanelDialog() {
		if(administrationPanelDialog == null)
			administrationPanelDialog = new AdministrationPanelDialog();
		return administrationPanelDialog;
	}

	public ChangelogDialog getChangelogDialog() {
		if (changelogDialog == null)
			changelogDialog = new ChangelogDialog();
		return changelogDialog;
	}
	
	public CodexDialog getCodexDialog(){
		return codexDialog;
	}
	
	public LoginDialog getLoginDialog() {
		return loginDialog;
	}
	
	public boolean isFullScreenMode() {
		return fullScreen;
	}
	
	public void setFullScreenMode(boolean fullScreen) {
		this.fullScreen = fullScreen;
		
		if (toolBar != null) {
			toolBar.setVisible(!fullScreen);
			miniMap.setVisible(!fullScreen);
			namePanel.setVisible(!fullScreen);
			empireControlPanel.setVisible(!fullScreen);
			empireView.setVisible(!fullScreen);
			progressBar.setVisible(!fullScreen);
			chat.setVisible(!fullScreen);
			chat.scrollDown();
			missionPanel.setVisible(!fullScreen);
			
			if (!fullScreen)
				chat.scrollDown();
		}
		
		areaContainer.setVisible(!fullScreen);
		fullScreenPanel.clear();
		fullScreenPanel.setVisible(fullScreen);
		
		if (fullScreen) {
			// Sauvegarde l'état et masque les dialogues
			Element element = RootPanel.getBodyElement().getFirstChildElement();
			
			while (element != null) {
				if (element.getClassName().contains(JSComponent.getUIProperty(
						JSDialog.UI_CLASS_ID, OpenJWT.CSS_CLASS))) {
					dialogStates.add(new DialogState(element, element.getStyle().getProperty("display")));
					element.getStyle().setProperty("display", "none");
				}
				
				element = element.getNextSiblingElement();
			}
			
			if (getSelectionInfo() != null) {
				systemInfoVisible = getSelectionInfo().isVisible();
				getSelectionInfo().setVisible(false);
			}
		} else {
			// Restaure l'état des dialogues
			for (DialogState dialogState : dialogStates)
				dialogState.getDialog().getStyle().setProperty("display", dialogState.getDisplay());
			dialogStates.clear();
			
			if (getSelectionInfo() != null)
				getSelectionInfo().setVisible(systemInfoVisible);
		}
	}
	
	public void setServerShutdown(int remainingTime) {
		if (serverShutdownUpdater != null) {
			TimerManager.unregister(serverShutdownUpdater);
			RootPanel.get().remove(serverShutdown);
			serverShutdownUpdater = null;
		}
		
		if (remainingTime != -1) {
			if (serverShutdownUpdater == null) {
				serverShutdownUpdater = new ServerShutdownUpdater(
						serverShutdown.getElement(), remainingTime);
				TimerManager.register(serverShutdownUpdater, TimerManager.SECOND_UNIT);
				RootPanel.get().add(serverShutdown);
			}
		}
	}
	
	public void onWindowResized(int width, int height) {
		if (chat != null)
			chat.updateSize();
		
		int clientWidth = Window.getClientWidth();
		int clientHeight = Window.getClientHeight();
		
		if (currentClientWidth > 1024 && clientWidth <= 1024) {
			RootPanel.get().addStyleName("lowres");
			if (miniMap != null)
				miniMap.setSize(new Dimension(120, 120));
		} else if (currentClientWidth <= 1024 && clientWidth > 1024) {
			RootPanel.get().removeStyleName("lowres");
			if (miniMap != null)
				miniMap.setSize(new Dimension(160, 160));
		}
		
		currentClientWidth = clientWidth;

		changelogContainer.setPixelHeight(clientHeight);
		changelogContainer.update();
		
		if (areaContainer != null)
			areaContainer.getMap().setBounds(new Dimension(clientWidth, clientHeight));
	}
	
	public void onWindowClosed() {
		// Ignoré
	}

	public String onWindowClosing() {
		UpdateManager.stop();
		areaContainer.getMap().clear();
		return null;
	}
	
	public boolean onEventPreview(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONKEYDOWN:
			if (event.getKeyCode() == 116) { // F5
				EventManager.removeEventHook(this);
				Window.removeWindowCloseListener(this);
				
				UpdateManager.stop();
				areaContainer.getMap().clear();
				new Action("killpolling", Action.NO_PARAMETERS, new ActionCallback() {
					public void onFailure(String error) {
						ActionCallbackAdapter.onFailureDefaultBehavior(error);
					}

					public void onSuccess(AnswerData data) {
						Window.Location.reload();
					}
				});
				
				event.preventDefault();
				event.cancelBubble(true);
			}
			break;
		case Event.ONMOUSEDOWN:
			// Bloque les clics de molette
			if (event.getButton() == Event.BUTTON_MIDDLE) {
				event.cancelBubble(true);
				event.preventDefault();
			}
			break;
		}
		return false;
	}
	
	public static Client getInstance() {
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private class DialogState {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private Element dialog;
		private String display;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public DialogState(Element dialog, String display) {
			this.dialog = dialog;
			this.display = display;
		}

		// ----------------------------------------------------- METHODES -- //

		public Element getDialog() {
			return dialog;
		}

		public String getDisplay() {
			return display;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class ServerShutdownUpdater implements TimerHandler {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private Element element;
		private int remainingTime;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public ServerShutdownUpdater(Element element, int remainingTime) {
			this.element = element;
			this.remainingTime = remainingTime * 1000;
			updateText();
		}

		// ----------------------------------------------------- METHODES -- //

		public void destroy() {
			// Sans effet
		}

		public boolean isFinished() {
			return remainingTime <= 0;
		}

		public void update(int interpolation) {
			remainingTime -= interpolation;
			remainingTime = Math.max(remainingTime, 0);
			updateText();
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
		
		private void updateText() {
			int seconds = (remainingTime / 1000);
			element.setInnerHTML("Mise à jour serveur dans " + seconds +
					" seconde" + (seconds > 1 ? "s" : "") + "...");
		}
	}
}
