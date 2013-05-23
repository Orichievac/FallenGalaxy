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

package fr.fg.server.servlet.export;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.CDATA;
import org.jdom.Element;

import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.Player;
import fr.fg.server.data.Ward;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.BaseServlet;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;


public class RssEventsServlet extends BaseServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private static final long serialVersionUID = -8548289485249629930L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String ekey = request.getRequestURI();

		if (ekey.endsWith(".xml"))
			ekey = ekey.substring(0, ekey.length() - 4);
		
		try {
			// Décode les entités (ex : %20)
			String encoding = request.getCharacterEncoding();
			ekey = URLDecoder.decode(
				ekey.substring(ekey.lastIndexOf("/") + 1),
				encoding == null ? "ISO-8859-1" : encoding);
		} catch (UnsupportedEncodingException e) {
			LoggingSystem.getServerLogger().warn("Unsupported encoding.", e);
		}
		
		if (ekey.length() != 32) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		// Vérifie qu'un joueur correspond à l'ekey
		Player player = DataAccess.getPlayerByEkey(ekey);
		
		if (player == null) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		// Vérifie que le joueur est premium
		if (!player.hasRight(Player.PREMIUM)) {
			writeNotPremium(request, response);
			return;
		}
		
		// Construit le flux RSS
		Element root = new Element("rss");
		root.setAttribute("version", "2.0");
		
		Element channel = new Element("channel");
		root.addContent(channel);
		
		// Description du flux
		Element channelTitle = new Element("title");
		channelTitle.addContent("Fallen Galaxy :: " + Config.getServerName());
		channel.addContent(channelTitle);
		
		Element channelDescription = new Element("description");
		channelDescription.addContent("Flux RSS des évènements sur Fallen Galaxy");
		channel.addContent(channelDescription);
		
		Element lang = new Element("lang");
		lang.addContent(Config.getServerLang());
		channel.addContent(lang);
		
		Element copyright = new Element("copyright");
		copyright.addContent("Copyright 2010 Jérémie Gottero, Nicolas Bosc, Thierry Chevalier");
		channel.addContent(copyright);
		
		Element webmaster = new Element("webmaster");
		webmaster.addContent("contact@fallengalaxy.com");
		channel.addContent(webmaster);
		
		// Tri les évènements par date
		List<Event> events = player.getEvents();
		Collections.sort(events, new Comparator<Event>() {
			public int compare(Event p1, Event p2) {
				if (p1.getDate() > p2.getDate())
					return -1;
				if (p1.getDate() == p2.getDate())
					return p1.getId() < p2.getId() ? -1 : 1;
				return 1;
			}
		});
		
		// Liste des 15 derniers évènements
		SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
		
		for (int i = 0; i < Math.min(15, events.size()); i++) {
			Event event = events.get(i);
			
			String title, description;
			
			switch (event.getType()) {
			case Event.EVENT_FLEET_ATTACK:
				// Attaque de flotte
				title = "Attaque flotte de " + event.getArg3();
				description = "Notre flotte <b>" + event.getArg1() + "</b> " +
					"a engagé le combat avec la flotte <b>" + event.getArg2() + "</b> " +
					"appartenant à <b>" + event.getArg3() + "</b>.";
				break;
			case Event.EVENT_FLEET_UNDER_ATTACK:
				// Flotte attaquée
				title = "Flotte attaquée par " + event.getArg3();
				description = "Notre flotte <b>" + event.getArg1() + "</b> " +
					"a été attaquée par la flotte <b>" + event.getArg2() + "</b> " +
					"appartenant à <b>" + event.getArg3() + "</b>.";
				break;
			case Event.EVENT_FLEET_DESTROYED:
				// Flotte détruite
				title = "Flotte " + event.getArg1() + " détruite";
				description = "Nos forces ont détruit la flotte <b>" + event.getArg1() + "</b> !";
				break;
			case Event.EVENT_FLEET_LOST:
				// Flotte perdue
				title = "Flotte " + event.getArg1() + " perdue";
				description = "Notre flotte <b>" + event.getArg1() +
					"</b> a été détruite par les forces ennemies !";
				break;
			case Event.EVENT_BATTLE_REPORT:
				// Rapport de combat
				title = "Rapport de combat";
				description = "Lien pour visionner le combat : <a href=\"" + event.getArg2() +
					"\" target=\"_blank\">" + event.getArg2() + "</a>";
				break;
			case Event.EVENT_SWAP:
				// Distorsion spatiale
				title = "Distortion spatiale de " + event.getArg3();
				description = "Notre flotte <b>" + event.getArg1() + "</b> a " +
					"été déplacée suite à une distorsion spatiale générée " +
					"par la flotte <b>" + event.getArg2() + "</b> appartenant " +
					"à <b>" + event.getArg3() + "</b>.";
				break;
			case Event.EVENT_EMP:
				// IEM
				title = "Impulsion électromagnétique de " + event.getArg3();
				description = "Notre flotte <b>" + event.getArg1() + "</b> a " +
					"subi une impulsion électromagnétique générée " +
					"par la flotte <b>" + event.getArg2() + "</b> appartenant " +
					"à <b>" + event.getArg3() + "</b>.";
				break;
			case Event.EVENT_DELUDE_LOST:
				// Leurre détruit
				title = "Leurre détruit par " + event.getArg3();
				description = "Notre leurre <b>" + event.getArg1() + "</b> " +
					"a été détruit par la flotte <b>" + event.getArg2() + "</b> " +
					"appartenant à <b>" + event.getArg3() + "</b>.";
				break;
			case Event.EVENT_COLONIZATION:
				// Colonisation
				title = "Colonisation " + event.getArg2();
				description = "Notre flotte <b>" + event.getArg1() +
					"</b> a colonisé le système <b>" + event.getArg2() + "</b>.";
				break;
			case Event.EVENT_SYSTEM_CAPTURED:
				// Système capturé
				title = "Système " + event.getArg1() + " capturé";
				description = "Notre flotte <b>" + event.getArg1() +
					"</b> a pris le contrôle du système <b>" + event.getArg3() +
					"</b> appartenant à <b>" + event.getArg2() + "</b> !";
				break;
			case Event.EVENT_SYSTEM_LOST:
				// Système perdu
				title = "Système " + event.getArg3() + " perdu";
				description = "Notre système <b>" + event.getArg3() +
					"</b> a été capturé par la flotte <b>" + event.getArg1() +
					"</b> appartenant à <b>" + event.getArg2() + "</b> !";
				break;
			case Event.EVENT_START_CAPTURE:
				// Début de capture d"un système
				title = "Système " + event.getArg3() + " en cours de capture";
				description = "Notre système <b>" + event.getArg3() +
					"</b> est en train d'être capturé par la flotte <b>" +
					event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b> !";
				break;
			case Event.EVENT_DEVASTATE_SYSTEM:
				// Devastation de système
				title = "Devastation système " + event.getArg3();
				description = "Notre flotte <b>" + event.getArg1() +
					"</b> a dévasté le système <b>" + event.getArg3() +
					"</b> appartenant à <b>" + event.getArg2() + "</b> !";
				break;
			case Event.EVENT_SYSTEM_DEVASTATED:
				// Système devasté
				title = "Système " + event.getArg3() + " dévasté";
				description = "Notre système <b>" + event.getArg3() +
					"</b> a été dévasté par la flotte <b>" + event.getArg1() +
					"</b> appartenant à <b>" + event.getArg2() + "</b> !";
				break;
			case Event.EVENT_STATION_UNDER_ATTACK:
				// Station endommagée
				title = "Station " + event.getArg1() + " endommagée";
				description = "Notre station spatiale <b>" + event.getArg1() +
					"</b> est endommagée à <b>" + (int) Math.round((1 -
					Double.parseDouble(event.getArg2())) * 100) + "%</b>.";
				break;
			case Event.EVENT_STATION_LOST:
				// Station perdue
				title = "Station " + event.getArg1() + " perdue";
				description = "Notre station spatiale <b>" + event.getArg1() +
					"</b> a été détruite !";
				break;
			case Event.EVENT_STATION_SELF_DESTRUCT:
				// Station auto détruite
				title = "Auto-destruction station " + event.getArg1();
				description = "<b>" + event.getArg2() + "</b> a enclenché la procédure " +
					"d'auto-destruction de notre station spatiale <b>" + event.getArg1() + "</b>.";
				break;
			case Event.EVENT_NEW_TECHNOLOGY:
				// Nouvelle technologie recherchée
				String research = Messages.getString("research" + event.getArg1());
				title = "Technologie " + research + " découverte";
				description = "Nos chercheurs ont découvert la technologie <b>" +
					research + "</b>.";
				break;
			case Event.EVENT_ALLY_CREATED:
				// Création d"une alliance
				title = "Alliance " + event.getArg2() + " fondée";
				description = "L'alliance <b>" + event.getArg2() +
					"</b> a été fondée par <b>" + event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_DESTROYED:
				// Dissolution d"une alliance
				title = "Alliance " + event.getArg2() + " dissoute";
				description = "L'alliance <b>" + event.getArg1() + "</b> a été dissoute.";
				break;
			case Event.EVENT_ALLY_MEMBER_JOINED:
				// Nouveau membre
				title = "Intégration " + event.getArg1();
				description = "<b>" + event.getArg1() + "</b> a intégré notre alliance.";
				break;
			case Event.EVENT_ALLY_MEMBER_LEFT:
				// Membre qui quitte l"alliance
				title = "Départ " + event.getArg1();
				description = "<b>" + event.getArg1() + "</b> a quitté notre alliance.";
				break;
			case Event.EVENT_ALLY_NEW_RANK:
				// Changement de rang du joueur
				String rank = Ally.getRankName(event.getArg1(), Integer.parseInt(event.getArg2()));
				title = "Nouveau rang : " + rank;
				description = "Vous avez désormais le rang de <b>" + rank + "</b>.";
				break;
			case Event.EVENT_ALLY_APPLICANT:
				// Nouveau postulant
				title = "Candidature " + event.getArg1();
				description = "<b>" + event.getArg1() +
					"</b> a postulé à notre alliance.";
				break;
			case Event.EVENT_ALLY_CANCEL_APPLY:
				// Postulant qui retire sa candidature
				title = "Annulation candidature " + event.getArg1();
				description = "<b>" + event.getArg1() + "</b> a retiré sa " +
						"candidature dans notre alliance.";
				break;
			case Event.EVENT_ALLY_NEW_VOTEKICK:
				// Joueur qui lance un vote de kick
				title = "Vote éjection " + event.getArg2();
				description = "<b>" + event.getArg1() +
					"</b> a lancé un vote pour éjecter <b>" +
					event.getArg2() + "</b> de notre alliance.";
				break;
			case Event.EVENT_ALLY_NEW_VOTEACCEPT:
				// Joueur qui lance un vote d"accept
				title = "Vote intégration " + event.getArg2();
				description = "<b>" + event.getArg1() +
					"</b> a lancé un vote pour accepter <b>" +
					event.getArg2() + "</b> dans notre alliance.";
				break;
			case Event.EVENT_ALLY_BREAK_ALLY:
				// Alliance qui rompt un traité d"alliance
				title = "Pacte de non agression avec " + event.getArg1() + " rompu";
				description = "Nous avons rompu le pacte de non agression " +
					"passé avec l'alliance <b>" + event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_BROKEN:
				// Traité d"alliance rompu
				title = "Pacte de non agression avec " + event.getArg1() + " rompu";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a rompu le pacte de non agression passé avec notre alliance.";
				break;
			case Event.EVENT_ALLY_DECLARE_WAR:
				// Déclaration de guerre
				title = "Guerre contre " + event.getArg1() + " déclarée";
				description = "Nous avons déclaré la guerre à l'alliance <b>" +
					event.getArg1() + "</b> !";
				break;
			case Event.EVENT_ALLY_WAR_DECLARED:
				// Guerre déclarée
				title = "Guerre contre " + event.getArg1() + " déclarée";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> nous a déclaré la guerre !";
				break;
			case Event.EVENT_ALLY_OFFER_ALLY:
				// Proposition d"alliance
				title = "Pacte de non agression proposé à " + event.getArg1();
				description = "Nous avons proposé un pacte de non agression à l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_OFFERED:
				// Alliance proposée
				title = "Pacte de non agression proposé par " + event.getArg1();
				description = "L'alliance <b>" + event.getArg1() +
					"</b> souhaite signer un pacte de non agression avec notre alliance.";
				break;
			case Event.EVENT_ALLY_DECLINE_ALLY:
				// Refus d"alliance
				title = "Pacte de non agression avec " + event.getArg1() + " refusé";
				description = "Nous avons refusé le pacte de non agression avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_DECLINED:
				// Alliance refusée
				title = "Pacte de non agression avec " + event.getArg1() + " refusé";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a refusé de signer un pacte de non agression avec notre alliance.";
				break;
			case Event.EVENT_ALLY_CANCEL_ALLY:
				// Proposition d'alliance retirée
				title = "Proposition pacte de non agression avec " + event.getArg1() + " annulée";
				description = "Nous avons retiré notre proposition de pacte de non agression avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_CANCELED:
				// Proposition d'alliance retirée
				title = "Proposition de pacte de non agression avec " + event.getArg1() + " retirée";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a retiré sa proposition de pacte de non agression.";
				break;
			case Event.EVENT_ALLY_NEW_ALLY:
				// Alliance acceptée
				title = "Pacte de non agression avec " + event.getArg1() + " signé";
				description = "Nous avons désormais un pacte de non agression avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_OFFER_PEACE:
				// Proposition de paix
				title = "Trêve proposée à " + event.getArg1();
				description = "Nous avons proposé de signer la paix à l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_PEACE_OFFERED:
				// Paix proposée
				title = "Trêve proposée par " + event.getArg1();
				description = "L'alliance <b>" + event.getArg1() +
					"</b> souhaite signer la paix avec notre alliance.";
				break;
			case Event.EVENT_ALLY_DECLINE_PEACE:
				// Refus proposition de paix
				title = "Trêve avec " + event.getArg1() + " refusée";
				description = "Nous avons refusé de signer la paix avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_PEACE_DECLINED:
				// Proposition de paix refusée
				title = "Trêve avec " + event.getArg1() + " refusée";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a refusé de signer la paix avec notre alliance.";
				break;
			case Event.EVENT_ALLY_CANCEL_PEACE:
				// Retirer proposition de paix
				title = "Proposition trêve avec " + event.getArg1() + " annulée";
				description = "Nous avons retiré notre proposition de paix avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_PEACE_CANCELED:
				// Proposition de paix retirée
				title = "Proposition trêve avec " + event.getArg1() + " retirée";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a retiré sa proposition de paix avec notre alliance.";
				break;
			case Event.EVENT_ALLY_NEW_PEACE:
				// Paix signée
				title = "Paix signée avec " + event.getArg1();
				description = "Nous avons signé la paix avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_BREAK_ALLY:
				title = "Pacte de non agression avec " + event.getArg1() + " rompu";
				description = "Nous avons rompu le pacte de non agression " +
					"passé avec <b>" + event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_BROKEN:
				title = "Pacte de non agression avec " + event.getArg1() + " rompue";
				description = "<b>" + event.getArg1() +
					"</b> a rompu le pacte de non agression passé avec notre gouvernement.";
				break;
			case Event.EVENT_PLAYER_DECLARE_WAR:
				// Déclaration de guerre
				title = "Guerre contre " + event.getArg1() + " déclarée";
				description = "Nous avons déclaré la guerre à <b>" +
					event.getArg1() + "</b> !";
				break;
			case Event.EVENT_PLAYER_WAR_DECLARED:
				// Guerre déclarée
				title = "Guerre contre " + event.getArg1() + " déclarée";
				description = "<b>" + event.getArg1() +
					"</b> nous a déclaré la guerre !";
				break;
			case Event.EVENT_PLAYER_OFFER_ALLY:
				// Proposition d"alliance
				title = "Pacte de non agression proposé à " + event.getArg1();
				description = "Nous avons proposé un pacte de non agression à <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_OFFERED:
				// Alliance proposée
				title = "Pacte de non agression proposé par " + event.getArg1();
				description = "<b>" + event.getArg1() +
					"</b> souhaite signer un pacte de non agression avec notre gouvernement.";
				break;
			case Event.EVENT_PLAYER_DECLINE_ALLY:
				// Refus d'alliance
				title = "Pacte de non agression avec " + event.getArg1() + " refusé";
				description = "Nous avons refusé le pacte de non agression avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_DECLINED:
				// Alliance refusée
				title = "Pacte de non agression avec " + event.getArg1() + " refusé";
				description = "<b>" + event.getArg1() +
					"</b> a refusé de signer un pacte de non agression avec notre gouvernement.";
				break;
			case Event.EVENT_PLAYER_CANCEL_ALLY:
				// Proposition d'alliance retirée
				title = "Proposition pacte de non agression avec " + event.getArg1() + " annulée";
				description = "Nous avons retiré notre proposition de pacte de non agression avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_CANCELED:
				// Proposition d'alliance retirée
				title = "Proposition pacte de non agression avec " + event.getArg1() + " retirée";
				description = "<b>" + event.getArg1() +
					"</b> a retiré sa proposition de pacte de non agression.";
				break;
			case Event.EVENT_PLAYER_NEW_ALLY:
				// Alliance acceptée
				title = "Pacte de non agression avec " + event.getArg1() + " signée";
				description = "Nous avons désormais un pacte de non agression avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_OFFER_PEACE:
				// Proposition de paix
				title = "Trêve proposée à " + event.getArg1();
				description = "Nous avons proposé de signer la paix à <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_PEACE_OFFERED:
				// Paix proposée
				title = "Trêve proposée par " + event.getArg1();
				description = "<b>" + event.getArg1() +
					"</b> souhaite signer la paix avec nous.";
				break;
			case Event.EVENT_PLAYER_DECLINE_PEACE:
				// Refus proposition de paix
				title = "Trêve avec " + event.getArg1() + " refusée";
				description = "Nous avons refusé de signer la paix avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_PEACE_DECLINED:
				// Proposition de paix refusée
				title = "Trêve avec " + event.getArg1() + " refusée";
				description = "<b>" + event.getArg1() +
					"</b> a refusé de signer la paix avec notre gouvernement.";
				break;
			case Event.EVENT_PLAYER_CANCEL_PEACE:
				// Retirer proposition de paix
				title = "Proposition trêve avec " + event.getArg1() + " annulée";
				description = "Nous avons retiré notre proposition de signer la paix avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_PEACE_CANCELED:
				// Proposition de paix retirée
				title = "Proposition trêve avec " + event.getArg1() + " retirée";
				description = "<b>" + event.getArg1() +
					"</b> a retiré sa proposition de signer la paix.";
				break;
			case Event.EVENT_PLAYER_NEW_PEACE:
				// Paix signée
				title = "Paix signée avec " + event.getArg1();
				description = "Nous avons signé la paix avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_END_OF_WAR:
				// Paix signée par guerre inactive
				title = "Fin des hostilités avec " + event.getArg1();
				description = "Les affrontements avec <b>" + event.getArg1() + "</b> ayant cessé, la paix a été signée.";
				break;
			case Event.EVENT_NEW_STATION:
				// Nouvelle station
				title = "Nouvelle station";
				description = "Nous disposons d'une nouvelle station spatiale dans le secteur <b>" + event.getArg1() + "</b> !";
				break;
			case Event.EVENT_STATION_UPGRADED:
				// Station améliorée
				title = "Station " + event.getArg1() + " améliorée";
				description = "Notre station spatiale <b>" + event.getArg1() + "</b> a été améliorée au niveau <b>" + event.getArg2() + "</b> !";
				break;
			case Event.EVENT_CHARGE_DEFUSED:
				// Charge désamorcée
				title = "Charge désamorcée par " + event.getArg2();
				description = "La flotte <b>" + event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b> a désamorcé une de nos " + Ward.getWardName(event.getArg3(), 2) + ".";
				break;
			case Event.EVENT_CHARGE_TRIGGERED:
				// Charge déclenchée
				title = "Charge déclenchée par " + event.getArg2();
				if (event.getArg3().startsWith(Ward.TYPE_MINE))
					description = "La flotte <b>" + event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b> a été endommagée par " + event.getArg4() + " de nos " + Ward.getWardName(event.getArg3(), 2) + ".";
				else
					description = "La flotte <b>" + event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b> a été immobilisée par " + event.getArg4() + " de nos " + Ward.getWardName(event.getArg3(), 2) + ".";
				break;
			case Event.EVENT_CHARGE_BLOWED:
				// Charge subie
				if (event.getArg2().startsWith(Ward.TYPE_MINE)) {
					title = "Flotte " + event.getArg1() + " endommagée par une charge";
					description = "Notre flotte <b>" + event.getArg1() + "</b> a été endommagée par " + event.getArg3() + " " + Ward.getWardName(event.getArg2(), Integer.parseInt(event.getArg3())) + ".";
				} else {
					title = "Flotte " + event.getArg1() + " immobilisée par une charge";
					description = "Notre flotte <b>" + event.getArg1() + "</b> a été immobilisée par " + event.getArg3() + " " + Ward.getWardName(event.getArg2(), Integer.parseInt(event.getArg3())) + ".";
				}
				break;
			case Event.EVENT_CHARGE_FLEET_DESTROYED:
				// Flotte détruite par une charge
				title = "Flotte de " + event.getArg2() + " détruite";
				description = "La flotte <b>" + event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b> a été détruite par " + event.getArg4() + " de nos " + Ward.getWardName(event.getArg3(), 2) + ".";
				break;
			case Event.EVENT_CHARGE_FLEET_LOST:
				// Flotte perdue à cause d'une charge
				title = "Flotte " + event.getArg2() + " perdue";
				description = "Notre flotte <b>" + event.getArg1() + "</b> a été détruite par " + event.getArg3() + " " + Ward.getWardName(event.getArg2(), Integer.parseInt(event.getArg3())) + ".";
				break;
			case Event.EVENT_STRUCTURE_ATTACKED:
				// Structure bombardée
				title = "Structure " + event.getArg1() + " bombardée";
				description = "Notre structure <b>" + event.getArg1() + "</b> subit un bombardement de la flotte <b>" + event.getArg2() + "</b> appartenant à <b>" + event.getArg3() + "</b>.";
				break;
			case Event.EVENT_STRUCTURE_LOST:
				// Structure perdue
				title = "Structure " + event.getArg1() + " perdue";
				description = "Notre structure <b>" + event.getArg1() + "</b> a été détruite par la flotte <b>" + event.getArg2() + "</b> appartenant à <b>" + event.getArg3() + "</b>.";
				break;
			case Event.EVENT_STRUCTURE_DESTROYED:
				// Structure détruite
				title = "Structure de " + event.getArg2() + " détruite";
				description = "Notre flotte <b>" + event.getArg3() + "</b> a détruit la structure <b>" + event.getArg1() + "</b> appartenant à <b>" + event.getArg2() + "</b>.";
				break;
			case Event.EVENT_STRUCTURE_DISMOUNTED:
				// Structure détruite
				title = "Structure " + event.getArg1() + " démontée";
				description = "Notre flotte <b>" + event.getArg2() + "</b> a achevé le démontage de la structure <b>" + event.getArg1() + "</b>.";
				break;
			case Event.EVENT_STRUCTURE_MOUNTED:
				// Structure détruite
				title = "Structure " + event.getArg1() + " assemblée";
				description = "Notre flotte <b>" + event.getArg2() + "</b> a achevé l'assemblage de la structure <b>" + event.getArg1() + "</b>.";
				break;
			case Event.EVENT_STRUCTURE_DAMAGED:
				// Structure endommagée
				title = "Structure " + event.getArg1() + " endommagée";
				description = "Notre structure <b>" + event.getArg1() +
					"</b> est endommagée à <b>" + (int) Math.round((1 -
					Double.parseDouble(event.getArg2())) * 100) + "</b>.";
				break;
			case Event.EVENT_FLEET_CAPTURED_BLACKHOLE:
				// Flotte capturée par un trou noir
				title = "Flotte " + event.getArg1() + " dans un trou noir !";
				description = "Notre flotte <b>" + event.getArg1() +
					"</b> vient d'être affaiblie par un trou noir !";
				break;
			case Event.EVENT_WARD_OBSERVER_LOST:
				// Observer ward détruite
				title = "Balise d'observation détruite";
				description = event.getArg1()+" à détruit une de nos balises d'observation.";
				break;
			case Event.EVENT_WARD_SENTRY_LOST:
				// Observer ward détruite
				title = "Balise de détection détruite";
				description = event.getArg1()+" à détruit une de nos balises de détection.";
				break;
			case Event.EVENT_PLAYER_ADD_FRIEND:
				//Ajout d'un ami
				title = "Ajout d'ami";
				description = "<b>" + event.getArg1() + "</b> a été ajouté à votre liste d'amis.";
				break;
			case Event.EVENT_PLAYER_REMOVE_FRIEND:
				//Suppression d'un ami
				title = "Suppression d'un ami";
				description = "<b>" + event.getArg1() + "</b> a été retiré de votre liste d'amis.";
				break;
			case Event.EVENT_PLAYER_ADDED_FRIEND:
				//Ajout d'un ami
				title = "Un joueur vous a ajouté à sa liste d'amis";
				description = "<b>" + event.getArg1() + "</b> vous a ajouté à sa liste d'amis.";
				break;
			case Event.EVENT_PLAYER_REMOVED_FRIEND:
				//Suppression d'un ami
				title = "Un joueur vous a retiré de sa liste d'amis";
				description = "<b>" + event.getArg1() + "</b> vous a retiré de sa liste d'amis.";
				break;
			case Event.EVENT_ALLY_ADDED_NEWS:
				//Ajout d'une news
				title = "Nouvelle news de votre alliance";
				description = "<b>" + event.getArg1() + "</b> a ajouté une news";
				break;
			case Event.EVENT_ALLY_REMOVED_NEWS:
				//Suppression d'une news
				title = "Suppression d'une news de votre alliance";
				description = "<b>" + event.getArg1() + "</b> a supprimé une news";
				break;
			case Event.EVENT_MIGRATION_START:
				//Debut de migration
				title = "Début de migration";
				description = "Notre flotte <b>" + event.getArg1() +
					"</b> a commencé à migrer le système <b>" + event.getArg2() +
					"</b> vers le système <b>" + event.getArg3()+"</b>.";
				break;
			case Event.EVENT_MIGRATION_END:
				//Fin de migration
				title = "Migration achevée";
				description = "Notre flotte <b>" + event.getArg1() +
					"</b> vient d'achever la migration du système <b>" + event.getArg2() +
					"</b> vers le système <b>" + event.getArg3()+"</b>.";
				break;
			case Event.EVENT_ALLY_BREAK_ALLY_DEFENSIVE:
				title = "Pacte rompu";
				description = "Nous avons rompu le pacte défensif " +
					"passé avec l'alliance <b>" + event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_DEFENSIVE_BROKEN:
				title = "Pacte rompu";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a rompu le pacte défensif passé avec notre alliance.";
				break;
			case Event.EVENT_ALLY_OFFER_ALLY_DEFENSIVE:
				title = "Proposition de pacte";
				description = "Nous avons proposé un pacte défensif à l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_DEFENSIVE_OFFERED:
				title = "Proposition de pacte";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> souhaite signer un pacte défensif avec notre alliance.";
				break;
			case Event.EVENT_ALLY_DECLINE_ALLY_DEFENSIVE:
				title = "Pacte refusé";
				description = "Nous avons refusé le pacte défensif avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_DEFENSIVE_DECLINED:
				title = "Pacte refusé";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a refusé de signer un pacte défensif avec notre alliance.";
				break;
			case Event.EVENT_ALLY_CANCEL_ALLY_DEFENSIVE:
				title = "Retrait de proposition de pacte";
				description = "Nous avons retiré notre proposition de pacte défensif avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_DEFENSIVE_CANCELED:
				title = "Retrait de proposition de pacte";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a retiré sa proposition de pacte défensif.";
				break;
			case Event.EVENT_ALLY_NEW_ALLY_DEFENSIVE:
				title = "Pacte défensif signé";
				description = "Nous avons désormais un pacte défensif avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_BREAK_ALLY_TOTAL:
				title = "Pacte rompu";
				description = "Nous avons rompu le pacte total " +
					"passé avec l'alliance <b>" + event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_TOTAL_BROKEN:
				title = "Pacte rompu";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a rompu le pacte total passé avec notre alliance.";
				break;
			case Event.EVENT_ALLY_OFFER_ALLY_TOTAL:
				title = "Proposition de pacte";
				description = "Nous avons proposé un pacte total à l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_TOTAL_OFFERED:
				title = "Proposition de pacte";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> souhaite signer un pacte total avec notre alliance.";
				break;
			case Event.EVENT_ALLY_DECLINE_ALLY_TOTAL:
				title = "Pacte refusé";
				description = "Nous avons refusé le pacte total avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_TOTAL_DECLINED:
				title = "Pacte refusé";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a refusé de signer un pacte total avec notre alliance.";
				break;
			case Event.EVENT_ALLY_CANCEL_ALLY_TOTAL:
				title = "Retrait de proposition de pacte";
				description = "Nous avons retiré notre proposition de pacte total avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_ALLY_TOTAL_CANCELED:
				title = "Retrait de proposition de pacte";
				description = "L'alliance <b>" + event.getArg1() +
					"</b> a retiré sa proposition de pacte total.";
				break;
			case Event.EVENT_ALLY_NEW_ALLY_TOTAL:
				title = "Pacte total signé";
				description = "Nous avons désormais un pacte total avec l'alliance <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_BREAK_ALLY_DEFENSIVE:
				title = "Pacte défensif avec " + event.getArg1() + " rompu";
				description = "Nous avons rompu le pacte défensif " +
					"passé avec <b>" + event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_DEFENSIVE_BROKEN:
				title = "Pacte défensif avec " + event.getArg1() + " rompu";
				description = "<b>" + event.getArg1() +
					"</b> a rompu le pacte défensif passé avec notre gouvernement.";
				break;
			case Event.EVENT_PLAYER_OFFER_ALLY_DEFENSIVE:
				// Proposition d"alliance
				title = "Pacte défensif proposé à " + event.getArg1();
				description = "Nous avons proposé un pacte défensif à <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_DEFENSIVE_OFFERED:
				// Alliance proposée
				title = "Pacte défensif proposé par " + event.getArg1();
				description = "<b>" + event.getArg1() +
					"</b> souhaite signer un pacte défensif avec notre gouvernement.";
				break;
			case Event.EVENT_PLAYER_DECLINE_ALLY_DEFENSIVE:
				// Refus d'alliance
				title = "Pacte défensif avec " + event.getArg1() + " refusé";
				description = "Nous avons refusé le pacte défensif avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_DEFENSIVE_DECLINED:
				// Alliance refusée
				title = "Pacte défensif avec " + event.getArg1() + " refusé";
				description = "<b>" + event.getArg1() +
					"</b> a refusé de signer un pacte défensif avec notre gouvernement.";
				break;
			case Event.EVENT_PLAYER_CANCEL_ALLY_DEFENSIVE:
				// Proposition d'alliance retirée
				title = "Proposition pacte défensif avec " + event.getArg1() + " annulée";
				description = "Nous avons retiré notre proposition de pacte défensif avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_DEFENSIVE_CANCELED:
				// Proposition d'alliance retirée
				title = "Proposition pacte défensif avec " + event.getArg1() + " retirée";
				description = "<b>" + event.getArg1() +
					"</b> a retiré sa proposition de pacte défensif.";
				break;
			case Event.EVENT_PLAYER_NEW_ALLY_DEFENSIVE:
				// Alliance acceptée
				title = "Pacte défensif avec " + event.getArg1() + " signé";
				description = "Nous avons désormais un pacte défensif avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_BREAK_ALLY_TOTAL:
				title = "Pacte total avec " + event.getArg1() + " rompu";
				description = "Nous avons rompu le pacte total " +
					"passé avec <b>" + event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_TOTAL_BROKEN:
				title = "Pacte total avec " + event.getArg1() + " rompu";
				description = "<b>" + event.getArg1() +
					"</b> a rompu le pacte total passé avec notre gouvernement.";
				break;
			case Event.EVENT_PLAYER_OFFER_ALLY_TOTAL:
				// Proposition d"alliance
				title = "Pacte total proposé à " + event.getArg1();
				description = "Nous avons proposé un pacte total à <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_TOTAL_OFFERED:
				// Alliance proposée
				title = "Pacte total proposé par " + event.getArg1();
				description = "<b>" + event.getArg1() +
					"</b> souhaite signer un pacte total avec notre gouvernement.";
				break;
			case Event.EVENT_PLAYER_DECLINE_ALLY_TOTAL:
				// Refus d'alliance
				title = "Pacte total avec " + event.getArg1() + " refusé";
				description = "Nous avons refusé le pacte total avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_TOTAL_DECLINED:
				// Alliance refusée
				title = "Pacte total avec " + event.getArg1() + " refusé";
				description = "<b>" + event.getArg1() +
					"</b> a refusé de signer un pacte total avec notre gouvernement.";
				break;
			case Event.EVENT_PLAYER_CANCEL_ALLY_TOTAL:
				// Proposition d'alliance retirée
				title = "Proposition pacte total avec " + event.getArg1() + " annulée";
				description = "Nous avons retiré notre proposition de pacte total avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_PLAYER_ALLY_TOTAL_CANCELED:
				// Proposition d'alliance retirée
				title = "Proposition pacte total avec " + event.getArg1() + " retirée";
				description = "<b>" + event.getArg1() +
					"</b> a retiré sa proposition de pacte total.";
				break;
			case Event.EVENT_PLAYER_NEW_ALLY_TOTAL:
				// Alliance acceptée
				title = "Pacte total avec " + event.getArg1() + " signé";
				description = "Nous avons désormais un pacte total avec <b>" +
					event.getArg1() + "</b>.";
				break;
			case Event.EVENT_ALLY_DECLARE_WAR_TOTAL:
				title = "Déclaration de guerre forcée";
				description = "Notre pacte Total passé avec <b>"+event.getArg1()+"</b> nous oblige à déclarer "+
				"la guerre à l'alliance <b>" +event.getArg2() + "</b> !";
				break;
			case Event.EVENT_ALLY_WAR_DECLARED_TOTAL:
				title = "Déclaration de guerre";
				description = "A cause du pacte Total conclu avec <b>"+event.getArg1()+"</b>, <b>"+event.getArg2()+
				"</b> nous a déclaré la guerre!";
				break;
			case Event.EVENT_ALLY_DECLARE_WAR_DEFENSIVE:
				title = "Déclaration de guerre forcée";
				description = "Notre pacte Défensif passé avec <b>"+event.getArg1()+"</b> nous oblige à déclarer "+
				"la guerre à l'alliance <b>" +event.getArg2() + "</b> !";
				break;
			case Event.EVENT_ALLY_WAR_DECLARED_DEFENSIVE:
				title = "Déclaration de guerre";
				description = "A cause du pacte Défensif conclu avec <b>"+event.getArg1()+"</b>, <b>"+event.getArg2()+
				"</b> nous a déclaré la guerre!";
				break;
			case Event.EVENT_PLAYER_DECLARE_WAR_TOTAL:
				title = "Déclaration de guerre forcée";
				description = "Notre pacte Total passé avec <b>"+event.getArg1()+"</b> nous oblige à déclarer "+
				"la guerre à <b>" +event.getArg2() + "</b> !";
				break;
			case Event.EVENT_PLAYER_WAR_DECLARED_TOTAL:
				title = "Déclaration de guerre";
				description = "A cause du pacte Total conclu avec <b>"+event.getArg1()+"</b>, <b>"+event.getArg2()+
				"</b> nous a déclaré la guerre!";
				break;
			case Event.EVENT_PLAYER_DECLARE_WAR_DEFENSIVE:
				title = "Déclaration de guerre forcée";
				description = "Notre pacte Défensif passé avec <b>"+event.getArg1()+"</b> nous oblige à déclarer "+
				"la guerre à <b>" +event.getArg2() + "</b> !";
				break;
			case Event.EVENT_PLAYER_WAR_DECLARED_DEFENSIVE:
				title = "Déclaration de guerre";
				description = "A cause du pacte Défensif conclu avec <b>"+event.getArg1()+"</b>, <b>"+event.getArg2()+
				"</b> nous a déclaré la guerre!";
				break;
			case Event.EVENT_REWARD_PERSO:
				// Obtention du %reward% à la fin d'un contrat d'alliance
				title = "Récompense de mission d'alliance";
				description = "Votre alliance vient de terminer un contrat d'alliance ! " +
						"Vous obtenez les bonus suivants : <b>" + event.getArg1() +
				"</b>.";
				break;
			case Event.EVENT_ELECTION_START:
				// Les elections commencent
				title = "Début des éléctions";
				description = "Les elections pour la gestion de l'alliance viennent de commencer.";
				break;		
				
			case Event.EVENT_LEADER_DELEGATE:
				// Délégation des droits de leader à un joueur
				title = "Récompense de mission d'alliance";
				description = "<b>"+event.getArg1()+"</b> a donné son rang de leader de l'alliance à <b>"
				+ event.getArg2()+"</b> !";
				break;

			default:
				title = "!Unknown event: " + event.getType() + "!";
				description = title;
			}
			
			if (event.getIdArea() != 0)
				description += "<br/>Secteur <b>" + DataAccess.getAreaById(
					event.getIdArea()).getName() + "</b>";
			
			Element item = new Element("item");
			channel.addContent(item);
			
			Element titleElement = new Element("title");
			titleElement.addContent(title);
			item.addContent(titleElement);
			
			CDATA cdata = new CDATA(description);
			
			Element descriptionElement = new Element("description");
			descriptionElement.addContent(cdata);
			item.addContent(descriptionElement);
			
			Element pubDate = new Element("pubDate");
			pubDate.addContent(format.format(new Date(event.getDate() * 1000)));
			item.addContent(pubDate);
		}
		
		// Sortie en XML
		writeXML(response, root);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void writeInvalidSyntax(HttpServletRequest request,
			HttpServletResponse response) {
		write(request, response, "Syntaxe ou clé invalide. Utiliser l'URL : " +
			Config.getServerURL() + "rss/events/ekey.xml");
	}
	
	private void writeNotPremium(HttpServletRequest request,
			HttpServletResponse response) {
		write(request, response, "Fonctionnalité réservée aux comptes premiums.");
	}
}

