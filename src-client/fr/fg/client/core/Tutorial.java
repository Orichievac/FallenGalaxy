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

import java.util.HashMap;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;

import fr.fg.client.ajax.Action;
import fr.fg.client.core.DialogManager.ChoiceListener;
import fr.fg.client.core.settings.Settings;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.EventManager;

public class Tutorial implements EventPreview, ChoiceListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		LESSON_NONE 			= 0,
		LESSON_BEGIN 			= 1 << 0,
		LESSON_SECTOR_MAP 		= 1 << 1,
		LESSON_GALAXY_MAP 		= 1 << 2,
		LESSON_DIPLOMACY 		= 1 << 3,
		LESSON_RESEARCH 		= 1 << 4,
		LESSON_ACHIEVEMENTS		= 1 << 5,
		LESSON_BANK 			= 1 << 6,
		LESSON_CIVILIZATION 	= 1 << 7,

		LESSON_FLEET 			= 1 << 8,
		LESSON_TRADECENTER 		= 1 << 9, 
		LESSON_MAP 				= 1 << 11, 
		LESSON_SYSTEM 			= 1 << 12, 
		LESSON_EVENT 			= 1 << 13, 
		LESSON_CONTRACT 		= 1 << 14, 
		LESSON_CONTACT 			= 1 << 15, 
		LESSON_TACTIC 			= 1 << 10, 
		LESSON_SYSTEM_BUILD 	= 1 << 16, 
		LESSON_STRUCTURE 		= 1 << 17; 
	
	
	private final static int
		SUB_LESSON_NONE						=    0,
		SUB_LESSON_BEGIN_START				=    1,
		SUB_LESSON_BEGIN_MOVE_MAP			=    2,
		SUB_LESSON_SECTOR_MAP_START			=  100,
		SUB_LESSON_SECTOR_MAP_EXPLORATION	=  101,
		SUB_LESSON_GALAXY_MAP_START			=  200,
		SUB_LESSON_DIPLOMACY_START			=  400,
		SUB_LESSON_DIPLOMACY_TREATIES		=  401,
		SUB_LESSON_DIPLOMACY_DISABLE		=  402,
		SUB_LESSON_RESEARCH_START			=  600,
		SUB_LESSON_RESEARCH_LABORATORIES	=  601,
		SUB_LESSON_ACHIEVEMENTS_START		=  800,
		SUB_LESSON_BANK_START				= 1000,
		SUB_LESSON_BANK_ADVICE				= 1001,
		SUB_LESSON_CIVILIZATION_START		= 1200,
		SUB_LESSON_CIVILIZATION_TABS		= 1201,
		SUB_LESSON_FLEET_START 				= 1400,
		SUB_LESSON_FLEET_TIPS				= 1401,
		SUB_LESSON_TRADECENTER_START		= 1600,
		SUB_LESSON_EVENT_START				= 1800,
		SUB_LESSON_CONTRACT_START			= 2000,
		SUB_LESSON_CONTRACT_FACTION			= 2001,
		SUB_LESSON_CONTRACT_TIPS			= 2002,
		SUB_LESSON_CONTACT_START			= 2200,
		SUB_LESSON_TACTIC_START				= 2400,
		SUB_LESSON_TACTIC_TIPS				= 2401,
		SUB_LESSON_SYSTEM_START				= 2600,
		SUB_LESSON_SYSTEM_BUILD_START		= 2800,
		SUB_LESSON_MAP_START				= 3000,
		SUB_LESSON_MAP_TYPES				= 3001,
		SUB_LESSON_STRUCTURE_START			= 3200,
		SUB_LESSON_STRUCTURE_NEXT			= 3201;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int lesson, subLesson;
	
	private long lessonsDone;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Tutorial() {
		this.lesson = LESSON_NONE;
		
		Client.getInstance().getDialogManager().addChoiceListener(this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addLesson(int lesson) {
		this.lessonsDone = lessonsDone | lesson;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("section", String.valueOf(lesson));
		
		new Action("hidetutorial", params);
	}
	
	public void setLessonDone(long lessonsDone) {
		this.lessonsDone = lessonsDone;
	}
	
	public void setLesson(int lesson) {
		if ((lessonsDone & lesson) != 0)
			return;
		
		this.lesson = lesson;
		
		switch (this.lesson) {
		case LESSON_BEGIN:
			setSubLesson(SUB_LESSON_BEGIN_START);
			break;
		case LESSON_SECTOR_MAP:
			setSubLesson(SUB_LESSON_SECTOR_MAP_START);
			break;
		case LESSON_GALAXY_MAP:
			setSubLesson(SUB_LESSON_GALAXY_MAP_START);
			break;
		case LESSON_DIPLOMACY:
			setSubLesson(SUB_LESSON_DIPLOMACY_START);
			break;
		case LESSON_RESEARCH:
			setSubLesson(SUB_LESSON_RESEARCH_START);
			break;
		case LESSON_ACHIEVEMENTS:
			setSubLesson(SUB_LESSON_ACHIEVEMENTS_START);
			break;
		case LESSON_BANK:
			setSubLesson(SUB_LESSON_BANK_START);
			break;
		case LESSON_CIVILIZATION:
			setSubLesson(SUB_LESSON_CIVILIZATION_START);
			break;
		case LESSON_FLEET:
			setSubLesson(SUB_LESSON_FLEET_START);
			break;
		case LESSON_TRADECENTER:
			setSubLesson(SUB_LESSON_TRADECENTER_START);
			break;
		case LESSON_EVENT:
			setSubLesson(SUB_LESSON_EVENT_START);
			break;
		case LESSON_CONTRACT:
			setSubLesson(SUB_LESSON_CONTRACT_START);
			break;
		case LESSON_CONTACT:
			setSubLesson(SUB_LESSON_CONTACT_START);
			break;
		case LESSON_TACTIC:
			setSubLesson(SUB_LESSON_TACTIC_START);
			break;
		case LESSON_SYSTEM:
			setSubLesson(SUB_LESSON_SYSTEM_START);
			break;
		case LESSON_SYSTEM_BUILD:
			setSubLesson(SUB_LESSON_SYSTEM_BUILD_START);
			break;
		case LESSON_MAP:
			setSubLesson(SUB_LESSON_MAP_START);
			break;
		case LESSON_STRUCTURE:
			setSubLesson(SUB_LESSON_STRUCTURE_START);
			break;
		default:
			setSubLesson(SUB_LESSON_NONE);
			break;
		}
	}
	
	public boolean onEventPreview(Event event) {
		boolean validEvent = false;
		
		// Autorise les évènements sur le dialogue, et un certain
		// nombre de types d'évènements
		if (Client.getInstance().getDialogManager().getElement(
				).isOrHasChild(event.getTarget()))
			validEvent = true;
		
		if (event.getTypeInt() == Event.ONMOUSEOVER ||
				event.getTypeInt() == Event.ONMOUSEOUT ||
				event.getTypeInt() == Event.ONMOUSEMOVE ||
				event.getTypeInt() == Event.ONMOUSEUP)
			validEvent = true;
		
		if (!validEvent) {
			switch (subLesson) {
			case SUB_LESSON_BEGIN_MOVE_MAP:
				// Bloque tous les événements sauf ceux pour déplacer la vue
				switch (event.getTypeInt()) {
				case Event.ONMOUSEDOWN:
					if (DOM.isOrHasChild(Client.getInstance().getAreaContainer(
							).getElement(), DOM.eventGetTarget(event)) ||
						DOM.isOrHasChild(Client.getInstance().getAreaContainer(
							).getMap().getMiniMaps().get(0).getElement(),
							DOM.eventGetTarget(event))) {
						validEvent = true;
					}
					break;
				case Event.ONCLICK:
					if (DOM.isOrHasChild(Client.getInstance().getAreaContainer(
							).getMap().getMiniMaps().get(0).getElement(),
							DOM.eventGetTarget(event))) {
						validEvent = true;
					}
					break;
				case Event.ONKEYUP:
				case Event.ONKEYDOWN:
					int keyCode = DOM.eventGetKeyCode(event);
					
					if (keyCode >= 32 && keyCode <= 40)
						validEvent = true;
					break;
				}
				break;
			}
		}
		
		if (validEvent) {
			for (EventPreview hook : EventManager.getEventHooks())
				hook.onEventPreview(event);
		} else {
			event.cancelBubble(true);
			event.preventDefault();
		}
		
		return validEvent;
	}

	public void onChoice(DialogManager source, int choice) {
		switch (subLesson) {
		case SUB_LESSON_BEGIN_START:
			setSubLesson(SUB_LESSON_BEGIN_MOVE_MAP);
			break;
		case SUB_LESSON_SECTOR_MAP_START:
			setSubLesson(SUB_LESSON_SECTOR_MAP_EXPLORATION);
			break;
		case SUB_LESSON_DIPLOMACY_START:
			setSubLesson(SUB_LESSON_DIPLOMACY_TREATIES);
			break;
		case SUB_LESSON_DIPLOMACY_TREATIES:
			setSubLesson(SUB_LESSON_DIPLOMACY_DISABLE);
			break;
		case SUB_LESSON_RESEARCH_START:
			setSubLesson(SUB_LESSON_RESEARCH_LABORATORIES);
			break;
		case SUB_LESSON_BANK_START:
			setSubLesson(SUB_LESSON_BANK_ADVICE);
			break;
		case SUB_LESSON_CIVILIZATION_START:
			setSubLesson(SUB_LESSON_CIVILIZATION_TABS);
			break;
		case SUB_LESSON_CONTRACT_START:
			setSubLesson(SUB_LESSON_CONTRACT_FACTION);
			break;
		case SUB_LESSON_CONTRACT_FACTION:
			setSubLesson(SUB_LESSON_CONTRACT_TIPS);
			break;
		case SUB_LESSON_TACTIC_START:
			setSubLesson(SUB_LESSON_TACTIC_TIPS);
			break;
		case SUB_LESSON_FLEET_START:
			setSubLesson(SUB_LESSON_FLEET_TIPS);
			break;
		case SUB_LESSON_MAP_START:
			setSubLesson(SUB_LESSON_MAP_TYPES);
			break;
		case SUB_LESSON_STRUCTURE_START:
			setSubLesson(SUB_LESSON_STRUCTURE_NEXT);
			break;
		default:
			Client.getInstance().getDialogManager().hide();
			break;
		}
	}
	
	public void setSubLesson(int subLesson) {
		// Efface les paramètres de la leçon précédente
		switch (this.subLesson) {
		case SUB_LESSON_BEGIN_MOVE_MAP:
			DOM.removeEventPreview(this);
			break;
		}
		
		this.subLesson = subLesson;
		
		// Paramètres de la nouvelle leçon
		switch (this.subLesson) {
		case SUB_LESSON_BEGIN_START:
			DOM.addEventPreview(this);
			Client.getInstance().getDialogManager().show("Jade",
				"Bienvenue " + Settings.getPlayerLogin() + " ! Je suis une " +
				"Intelligence Virtuelle, que les gens de votre espèce " +
				"biologique appellent Jade, bien que mon vrai nom soit " +
				"XV-157RT-8853W40T. J'ai été programmée dans le but de vous " +
				"assister dans vos fonctions.",
				new String[]{"Je t'écoute... Jade.", "Je ne veux pas de tes conseils."},
				new boolean[]{true, true}, "jade");
			break;
		case SUB_LESSON_BEGIN_MOVE_MAP:
			Client.getInstance().getDialogManager().show("Jade",
				"Gros con.",
				new String[]{"Ok.", "Pas cool."}, new boolean[]{true, false}, "jade");
			break;
		case SUB_LESSON_SECTOR_MAP_START:
			Client.getInstance().getDialogManager().show("Jade",
				"Voici la carte de votre <b>quadrant</b>.<br/>" +
				"Un quadrant une région de la galaxie composée de nombreux " +
				"<b>secteurs</b>. Le secteur clignotant est le secteur " +
				"dans lequel vous vous trouvez actuellement.<br/>" +
				"Vous pouvez déplacer la vue en cliquant sur la carte sans " +
				"relâcher le bouton puis en déplaçant la souris.",
				new String[]{"Suite"},
				new boolean[]{true},
				"jade"
			);
			break;
		case SUB_LESSON_SECTOR_MAP_EXPLORATION:
			// Carte de la galaxie
			Client.getInstance().getDialogManager().show("Jade",
				"Pour afficher le contenu d'un secteur exploré, il suffit " +
				"de cliquer dessus.<br/>" +
				"Comme vous pouvez l'apercevoir, il y a des secteurs inconnus " +
				"proches de votre secteur et qu'il vous faudra explorer. Pour " +
				"explorer un secteur, déplacez une flotte près d'un " +
				"<b>relai hyperspatial</b>, et cliquez sur le bouton " +
				"<b>saut hyperspatial</b>.<br/><br/>" +
				"<span class=\"emphasize\">Faites maintenant un clic droit " +
				"sur la carte pour afficher la galaxie (ou molette arrière).</span>",
				new String[]{},
				new boolean[]{},
				"jade"
			);
			addLesson(lesson);
			break;
		case SUB_LESSON_GALAXY_MAP_START:
			Client.getInstance().getDialogManager().show("Jade",
				"Et voici la carte de la galaxie.<br/>La galaxie est composée " +
				"de quadrants, eux-même composés de secteurs. Le quadrant " +
				"clignotant est le quadrant qui contient le secteur dans " +
				"lequel vous vous trouvez actuellement.<br/>Pour afficher le " +
				"contenu d'un quadrant exploré, cliquez simplement dessus.",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(lesson);
			break;
		case SUB_LESSON_DIPLOMACY_START:
			// Diplomatie
			Client.getInstance().getDialogManager().show("Jade",
				"Cette fenêtre vous permet de gérer vos relations diplomatiques " +
				"avec les autres joueurs ; les traités en cours y sont listés. " +
				"Par défaut, vous avez un statut <b>neutre</b> avec tous les joueurs.<br/>" +
				"Vous pouvez signer des <b>coalitions</b> avec d'autres joueurs, " +
				"afin de partager certaines informations comme le contenu de " +
				"vos flottes ; ou <b>déclarer la guerre</b>, afin d'attaquer un joueur.",
				new String[]{"Suite"},
				new boolean[]{true},
				"jade"
			);
			break;
		case SUB_LESSON_DIPLOMACY_TREATIES:
			Client.getInstance().getDialogManager().show("Jade",
				"Si vous faites partie d'une alliance, votre alliance peut également " +
				"déclarer la guerre ou signer des coalitions avec d'autres " +
				"alliances. Seuls les dirigeants peuvent gérer les relations " +
				"diplomatiques de l'alliance.<br/>" +
				"Notez que la diplomatie de l'alliance est prioritaire sur " +
				"vos relations diplomatiques.",
				new String[]{"Suite"},
				new boolean[]{true},
				"jade"
			);
			break;
		case SUB_LESSON_DIPLOMACY_DISABLE:
			Client.getInstance().getDialogManager().show("Jade",
				"Enfin, si vous ne souhaitez pas être attaqué par d'autres joueurs, " +
				"la diplomatie peut être désactivée dans l'onglet options.<br/>" +
				"Attention, vous ne pourrez plus déclarer de guerre ni avoir " +
				"de coalitions avec d'autres joueurs tant que la diplomatie sera " +
				"désactivée. Si vous faites partie une alliance, vous restez en guerre " +
				"contre les alliances ennemies, même si vous avez désactivé la " +
				"diplomatie.",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_DIPLOMACY);
			break;
		case SUB_LESSON_RESEARCH_START:
			// Recherche
			Client.getInstance().getDialogManager().show("Jade",
				"Cet écran affiche votre avancement technologique. Il existe " +
				"de nombreuses technologies, qui vous permettront d'accéder " +
				"à de nouveaux bâtiments, de nouveaux vaisseaux ou des capacités " +
				"pour vos vaisseaux.<br/>Pour commencer à rechercher une " +
				"technologie, cliquez dessus. Passez la souris sur une " +
				"technologie pour voir ce à quoi elle donne accès. Vous " +
				"pouvez vous déplacer dans l'arbre des technologies en " +
				"cliquant sans relâcher le bouton puis en déplaçant la souris.",
				new String[]{"Suite"},
				new boolean[]{true},
				"jade"
			);
			break;
		case SUB_LESSON_RESEARCH_LABORATORIES:
			Client.getInstance().getDialogManager().show("Jade",
				"Afin de diminuer le temps pour développer une technologie, " +
				"il faut construire des <b>laboratoires</b> et des <b>centres " +
				"de recherche</b> sur votre système.<br/>Les laboratoires augmentent " +
				"la génération de <img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" class=\"resource research\"/>, et " +
				"les centres de recherches augmentent l'efficacité de vos " +
				"laboratoires.",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_RESEARCH);
			break;
		case SUB_LESSON_ACHIEVEMENTS_START:
			Client.getInstance().getDialogManager().show("Jade",
				"Cette fenêtre liste les trophés que vous avez acquis au cours du jeu. " +
				"Les trophés n'apportent aucun avantage, ils témoignent des « prouesses » que vous avez accomplies. " +
				"Pour chaque trophée, il y a 5 niveaux possibles : Apprenti, Initié, Expert, Maître et Grand maître. " +
				"Pour voir les trophés acquis par un autre joueur, utilisez la commande /trophées joueur.",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_ACHIEVEMENTS);
			break;
		case SUB_LESSON_BANK_START:
			Client.getInstance().getDialogManager().show("Jade",
				"Vous voici dans une banque ; vous pouvez y déposer des " +
				"ressources afin de gagner des intérêts. Plus une ressource " +
				"est présente dans une banque, plus son taux d'intérêt sera faible. " +
				"Les taux d'intérêt sont donnés pour une semaine.",
				new String[]{"Suite"},
				new boolean[]{true},
				"jade"
			);
			break;
		case SUB_LESSON_BANK_ADVICE:
			Client.getInstance().getDialogManager().show("Jade",
				"Attention, à chaque fois que vous transférez des ressources " +
				"vers la banque ou vers une flotte, vous payez des frais. " +
				"Les frais sont indépendants de la quantité échangée, aussi " +
				"essayez toujours de transférer un maximum de ressources en " +
				"une fois.<br/>Pour transférer des ressources, cliquez " +
				"sur les ressources que vous souhaitez transférer, pour " +
				"cliquez sur le bouton Transférer pour valider le transfert.",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_BANK);
			break;
		case SUB_LESSON_CIVILIZATION_START:
			Client.getInstance().getDialogManager().show("Jade",
				"A chaque fois que vous gagnez un niveau, vous obtenez <b>5 " +
				"points de civilisation</b>, qui vous permettent d'acquérir des " +
				"avancées. Une <b>avancée</b> est un bonus dans un domaine " +
				"(économie, militaire...) qui profite à l'ensemble de votre " +
				"empire. Chaque avancée coûte un nombre variable de points de " +
				"civilisation. Notez que certaines avancées nécessitent " +
				"d'économiser des points de colonisation pendant deux " +
				"niveaux.",
				new String[]{"Suite"},
				new boolean[]{true},
				"jade"
			);
			break;
		case SUB_LESSON_CIVILIZATION_TABS:
			Client.getInstance().getDialogManager().show("Jade",
				"L'onglet <b>Avancées acquises</b> liste les avancées dont " +
				"vous disposez.<br/>L'onglet <b>Amélioration</b> permet " +
				"d'acquérir de nouvelles avancées ou améliorer celles dont " +
				"vous disposez déjà.",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_CIVILIZATION);
			break;
		case SUB_LESSON_FLEET_START:
			Client.getInstance().getDialogManager().show("Jade",
					"Vous venez de selectionner une flotte !<br/>" +
					"Les flottes peuvent se déplacer, combattre, sauter" +
					" dans d'autres zones etc.<br/>" +
					"Lorsque vous selectionnez une flotte, vous pouvez modifier" +
					" sa tactique de combat. Vous aurez des tactiques plus élaborées" +
					" quand vous aurez fait les recherches pour de nouveaux vaisseaux" +
					" et de nouvelles capacités !",
					new String[]{"Suite"},
					new boolean[]{true},
					"jade"
				);
			break;
		case SUB_LESSON_FLEET_TIPS:
			Client.getInstance().getDialogManager().show("Jade",
				"Vos flottes peuvent être constituées de cargos, permettant " +
				"de transporter des ressources et de commercer.<br/>" +
				"Attention ! Vous ne pouvez mettre qu'un certain nombre de vaisseaux dans une" +
				" flotte, selon votre niveau.<br/>" +
				"Le niveau d'une flotte lui permet d'avoir des compétences " +
				"qui permettent de poser des balises, des mines, de miner des " +
				"astéroides ... ainsi que des compétences ultimes !<br/>" +
				"La compétence de flotte \"Ingénieur\" permet de créer des " +
				"structures, très utiles dans une alliance !",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_FLEET);
			break;
			
		case SUB_LESSON_TRADECENTER_START:
			Client.getInstance().getDialogManager().show("Jade",
				"Vous voici dans un centre de commerce ; vous pouvez y échanger " +
				"des ressources contre des crédits, ou l'inverse. <br/>" +
				"Attention, chaque transaction est taxée, et plus elle est " +
				"importante, plus la taxe l'est aussi !",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_TRADECENTER);
			break;
		case SUB_LESSON_EVENT_START:
			Client.getInstance().getDialogManager().show("Jade",
				"Voici la fenêtre d'évenements et d'alertes.<br/>" +
				"Celle ci vous permettra de savoir ce qui c'est passé durant " +
				"votre absence, et vous donnera des indications sur vos flottes, " +
				"systèmes etc.<br/>" +
				"Les évenements vous indiqueront les derniers combats, les " +
				"changements de diplomatie. Les alertes vous indiqueront si " +
				"vos flottes n'ont pas de tactiques de combats ou si vos dépôts " +
				"sont pleins !",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_EVENT);
			break;
		case SUB_LESSON_CONTRACT_START:
			Client.getInstance().getDialogManager().show("Jade",
				"Ceci est la fenêtre permettant de gérer vos missions !<br/>" +
				"Chaque heure, vous recevrez 1 proposition de mission, dans la " +
				"limite de 2 missions maximum.<br/>" +
				"Les missions permettent de gagner des récompenses" +
				" comme de l'expérience pour vous ou pour vos flottes.<br/>",
				new String[]{"Suite"},
				new boolean[]{true},
				"jade"
			);
			break;
		case SUB_LESSON_CONTRACT_FACTION:
			Client.getInstance().getDialogManager().show("Jade",
				"Les missions peuvent être proposées par différentes " +
				"factions.<br/>" +
				" Chacune de vos missions accomplies améliore vos relation " +
				"avec la faction qui vous a proposé la mission, mais peut " +
				"faire diminuer vos relations avec d'autres factions.<br/>" +
				"Vous gagnerez des bonus suivant le niveau de relation que vous " +
				"avez avec une faction !",
				new String[]{"Suite"},
				new boolean[]{true},
				"jade"
			);
			break;
		case SUB_LESSON_CONTRACT_TIPS:
			Client.getInstance().getDialogManager().show("Jade",
				"Il y a différent type de missions : celles où vous devrez " +
				"combattre l'intelligence artificielle, d'autres où vous devrez " +
				"faire des actions sans combats, ou encore d'autres contre " +
				"des joueurs !<br/>" +
				"Vos relations tendent à devenir neutre chaque semaine.<br/> " +
				"Pensez donc à faire régulièrement des missions pour votre faction préférée !",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_CONTRACT);
			break;
		case SUB_LESSON_CONTACT_START:
			Client.getInstance().getDialogManager().show("Jade",
				"Vous pouvez ici ajouter vos amis si vous avez un compte premium " +
				"ou alors les accepter uniquement. Cela permet de savoir quand " +
				"ceux-ci se connectent.",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_CONTACT);
			break;
		case SUB_LESSON_TACTIC_START:
			Client.getInstance().getDialogManager().show("Jade",
				"Vous voici dans la fenêtre permettant de gérer la tactique " +
				"d'un flotte !<br/>" +
				"La tactique d'une flotte permet de choisir " +
				"quels sont les vaisseaux que vous voulez placer en " +
				"première/arrière ligne, quels sont les vaisseaux qui doivent tirer " +
				"durant l'action, ou encore lequels doivent utiliser leurs capacités !<br/>" +
				"Vous débloquerez de nouveaux vaisseaux et de nouvelles capacités " +
				"en faisant des recherches !",
				new String[]{"Suite"},
				new boolean[]{true},
				"jade"
			);
			break;
		case SUB_LESSON_TACTIC_TIPS:
			Client.getInstance().getDialogManager().show("Jade",
				"Voici quelques indications supplémentaires :<br/>" +
				"-Les capacités des vaisseaux ont un temps de rechargement de plusieurs " +
				"tours. Il faut donc bien préparer ses tactiques avant un combat.<br/>" +
				"-Les cargos ne peuvent pas être placé en première ligne !<br/>" +
				"-Vous pouvez modifier vos tactiques à n'importe quel moment, même" +
				" si vos flottes sont immobilisées.<br/>",
				new String[]{"Terminé"},
				new boolean[]{true},
				"jade"
			);
			addLesson(LESSON_TACTIC);
			break;
			
		case SUB_LESSON_SYSTEM_START:
			Client.getInstance().getDialogManager().show("Jade",
					"Vous venez de selectionner un système !<br/>" +
					"Vous pouvez construire différents types de bâtiments " +
					"sur celui ci, comme les exploitations qui produisent des " +
					"ressources.<br/>" +
					"Les systèmes permettent aussi de construire des vaisseaux.<br/>" +
					"Essayons de construire un bâtiment. Pour cela, veuillez cliquer sur " +
					"le deuxième bouton rouge, encadré par des rayures jaune/noir" +
					" en bas de votre écran.",
					new String[]{},
					new boolean[]{true},
					"jade"
				);
			addLesson(LESSON_SYSTEM);
				break;
		case SUB_LESSON_SYSTEM_BUILD_START:
			Client.getInstance().getDialogManager().show("Jade",
					"Voici la fenêtre de création de bâtiment. Vous " +
					"pouvez créer de nouveaux batiments et en améliorer " +
					"d'autres si vous avez fait les recherches nécessaires.<br/>" +
					"Selectionnez le bâtiment que vous souhaitez construire, " +
					"puis cliquez sur le bouton \"OK\". ",
					new String[]{"Terminé"},
					new boolean[]{true},
					"jade"
				);
				addLesson(LESSON_SYSTEM_BUILD);
				break;
		case SUB_LESSON_MAP_START:
			Client.getInstance().getDialogManager().show("Jade",
					"Vous venez de déplacer une flotte dans un secteur. " +
					"Les secteurs contiennent des systèmes d'autres joueurs, parfois un " +
					"centre de commerce ou une banque, des astéroïdes etc.<br/>" +
					"Mais ils sont aussi peuplé de pirates qui n'hésites pas à attaquer " +
					"les flottes à leur portée !<br/>" +
					"Il y a aussi au moins une porte hyperspatiale dans chaque secteur, " +
					"qui permet de se déplacer dans d'autres secteurs ou quadrants " +
					"de la galaxie.<br/>" +
					"Les éléments intéressants du secteur sont représentés sur la " +
					"minimap en bas à gauche de votre écran.",
					new String[]{"Suite"},
					new boolean[]{true},
					"jade"
				);
				break;
		case SUB_LESSON_MAP_TYPES:
			Client.getInstance().getDialogManager().show("Jade",
					"Vous explorerez différents types de secteurs qui peuvent " +
					"être :<br/>" +
					"-Des secteurs de départ, comme celui dans lequel vous êtes. " +
					"Ces secteurs ne sont peuplés que de peu de pirates, et sont " +
					"donc peu dangereux, et moins intéressants que d'autres " +
					"types de secteurs.<br/>" +
					"-Des secteurs banquaires, sans pirate, vous permettant " +
					"d'entreposer vos ressources pour y gagner des interêts.<br/>" +
					"-Des secteurs pirates, dangereux car peuplés de pirates plus " +
					"agressifs, plus forts et plus nombreux. Ces secteurs permettent " +
					"donc de monter de niveaux, et des astéroïdes interessants y " +
					"sont présents !",
					new String[]{"Terminé"},
					new boolean[]{true},
					"jade"
				);
				addLesson(LESSON_MAP);
				break;
		case SUB_LESSON_STRUCTURE_START:
			Client.getInstance().getDialogManager().show("Jade",
					"Les flottes disposant de la compétence ingénieur peuvent " +
					"construire des structures.<br/>" +
					"Tout d'abord, pour construire une structure, vous " +
					"(ou votre alliance) doit disposer d'une source d'énergie " +
					"pour alimenter vos structures.<br/>" +
					"Les générateurs seront vos " +
					"premières sources d'énergie, et c'est une structure, " +
					"constructible uniquement sur les puits gravitationnels " +
					"présents dans chaque secteur.<br/>" +
					"Le silo est la structure permettant de stocker vos " +
					"ressources pour vos structures.",
					new String[]{"Suite"},
					new boolean[]{true},
					"jade"
				); 
			break;
		case SUB_LESSON_STRUCTURE_NEXT:
			Client.getInstance().getDialogManager().show("Jade",
					"Vous pouvez construire des structures de production ou " +
					"de protection, des structures permettant d'accélérer les " +
					"sauts hyperspatiaux etc.<br/>" +
					"Vos structures sont bien sûr améliorables, mais peuvent " +
					"être attaquées par d'autres flottes! Vous devrez souvent " +
					"les défendre.",
					new String[]{"Terminé"},
					new boolean[]{true},
					"jade"
				); 
				addLesson(LESSON_STRUCTURE);
				break;
				
		}
	}
	
	public void resetTutorial()
	{
		this.lessonsDone = LESSON_NONE;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
