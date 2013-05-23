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

package fr.fg.server.action.allies;

import java.util.List;
import java.util.Map;

import fr.fg.server.core.TreatyTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class DeclareWar extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		String allyName = (String) params.get("ally");

		Ally ally = player.getAlly();

		if (ally == null)
			throw new IllegalOperationException(
					"Vous n'appartenez à aucune alliance.");

		if (ally.getMembers().size() < 4)
			throw new IllegalOperationException(
					"Une alliance doit compter au moins 4 "
							+ "membres avant de pouvoir déclarer des guerres.");

		Ally enemy = DataAccess.getAllyByName(allyName);
		if (enemy == null)
			enemy = DataAccess.getAllyByTag(allyName);

		if (enemy == null)
			throw new IllegalOperationException("L'alliance "
					+ (String) params.get("ally") + " n'existe pas.");

		if (enemy.getName().equals(ally.getName()))
			throw new IllegalOperationException(
					"Vous ne pouvez pas déclarer la guerre à votre propre alliance.");
		
		if(enemy.getTreatyWithAlly(ally).equals(AllyTreaty.ENEMY))
			throw new IllegalOperationException(
					"Vous êtes déjà en guerre contre " + enemy.getName() + "!");

		if (player.getAllyRank() < ally
				.getRequiredRank(Ally.RIGHT_MANAGE_DIPLOMACY))
			throw new IllegalOperationException("Vous n'avez pas les "
					+ "droits nécessaires pour effectuer cette action.");

		if (enemy.isAi())
			throw new IllegalOperationException(
					"Vous ne pouvez pas déclarer la guerre à une alliance PNJ.");

		List<AllyTreaty> allyTreaties = DataAccess
				.getAllyTreaties(ally.getId());

		synchronized (allyTreaties) {
			for (AllyTreaty treaty : allyTreaties) {
				int otherAlly = (treaty.getIdAlly1() == ally.getId()) ? treaty
						.getIdAlly2() : treaty.getIdAlly1();
				if (enemy.getId() == otherAlly)
					throw new IllegalOperationException(
							"Il existe déjà un traité entre vos deux alliances.");
			}
		}

		AllyTreaty allyTreaty = new AllyTreaty(ally.getId(), enemy.getId(),
				AllyTreaty.TYPE_WAR, 0);
		allyTreaty.save();

		List<Treaty> treaties = DataAccess.getPlayerTreaties(player.getId());

		for (Treaty treaty : treaties) {
			Player otherPlayer = DataAccess.getPlayerById((treaty
					.getIdPlayer1() == player.getId()) ? treaty.getIdPlayer2()
					: treaty.getIdPlayer1());
			if (otherPlayer.getIdAlly() == enemy.getId()
					&& treaty.getType().equals(Treaty.TYPE_ALLY)) {
				treaty.delete();
			}
		}

		Event event = new Event(Event.EVENT_ALLY_WAR_DECLARED,
				Event.TARGET_ALLY, enemy.getId(), 0, -1, -1, ally.getName());
		event.save();

		event = new Event(Event.EVENT_ALLY_DECLARE_WAR, Event.TARGET_ALLY, ally
				.getId(), 0, -1, -1, enemy.getName());
		event.save();

		/*
		 * Si un pacte total a été conclu entre les 2 alliances, alors on
		 * regarde si elles sont en guerres contre d'autres alliances et on
		 * change les relations entre elles si nécessaires Si une l'alliance A,
		 * qui a conclu un pacte total avec B est en guerre contre C, B ne lui
		 * déclare la guerre que si elle n'a pas de pacte avec cette dernière.
		 * Dans le cas où A et B ont un pacte défensif et C attaque A, B déclare
		 * la guerre à C si ces 2 alliances n'ont pas de pacte . Si A attaque C,
		 * rien ne se passe.
		 */
		ally.checkNewRelationships(allyTreaty, true);
		enemy.checkNewRelationships(allyTreaty, false);
		ally.cancelAllMembersTreatiesWith(enemy);

		// Signale aux membres des deux alliances qu'ils sont en guerre
		UpdateTools.queueNewEventUpdate(ally.getMembers(), false);
		UpdateTools.queueNewEventUpdate(enemy.getMembers(), false);

		UpdateTools.queueAreaUpdate(ally.getMembers());
		UpdateTools.queueAreaUpdate(enemy.getMembers());

		return TreatyTools.getPlayerTreaties(null, player).toString();
	}

	// ------------------------------------------------- METHODES PRIVEES -- //
}
