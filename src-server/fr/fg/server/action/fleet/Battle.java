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

package fr.fg.server.action.fleet;

import java.util.Map;

import fr.fg.client.data.ReportData;
import fr.fg.server.contract.ContractManager;
import fr.fg.server.core.BattleTools;
import fr.fg.server.core.ReportTools;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Effect;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Item;
import fr.fg.server.data.ItemContainer;
import fr.fg.server.data.Player;
import fr.fg.server.data.Report;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;

public class Battle extends Action {

	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		Fleet attacking = DataAccess.getFleetById((Integer)params.get("fleet"));
		Fleet defending = DataAccess.getFleetById((Integer)params.get("enemy"));
		int mode = (Integer) params.get("mode");
		boolean bombing = (Boolean) params.get("bombing");
		
		if (attacking == null || defending == null)
			throw new IllegalOperationException("Flotte inexistante.");
		
		Player enemy = defending.getOwner(); 
		
		if (attacking.getIdOwner() != player.getId())
			throw new IllegalOperationException("Cette flotte ne vous appartient pas.");
		
//		if(attacking.getPowerLevel()>attacking.getArea().getSector().getLvlMax())
//		{
//			throw new IllegalOperationException("Votre flotte doit avoir une puissance " +
//					"inférieure à " + attacking.getArea().getSector().getLvlMax()+
//					" pour combattre dans ce quadrant.");
//		}
//		
//		if(defending.getPowerLevel()>defending.getArea().getSector().getLvlMax())
//		{
//			throw new IllegalOperationException("La flotte adverse est trop puissante " +
//					"pour combattre dans ce quadrant");
//		}
		
		if (defending.isStealth() && !player.isLocationRevealed(
				defending.getCurrentX(), defending.getCurrentY(), defending.getArea()))
			throw new IllegalOperationException("Flotte inexistante.");
		
		if ((attacking.getSkillLevel(Skill.SKILL_PIRATE) == -1 &&
				defending.getSkillLevel(Skill.SKILL_PIRATE) == -1 &&
				player.getTreatyWithPlayer(enemy) != Treaty.ENEMY) ||
				player.getTreatyWithPlayer(enemy) == Treaty.ALLY ||
				player.getTreatyWithPlayer(enemy) == Treaty.ALLIED ||
				player.getTreatyWithPlayer(enemy) == Treaty.PLAYER)
			throw new IllegalOperationException("Vous n'êtes pas en guerre contre ce joueur.");
		
		if (attacking.getIdCurrentArea() != defending.getIdCurrentArea())
			throw new IllegalOperationException("Les deux flottes ne se trouvent pas dans le même secteur.");
		
		if (attacking.isDelude())
			throw new IllegalOperationException("Vous ne pouvez pas attaquer avec un leurre.");
		
		if (bombing) {
			if (attacking.getSkillLevel(Skill.SKILL_BOMBING) == -1)
				throw new IllegalOperationException("La flotte ne peut pas bombarder.");
			
			int dx = attacking.getCurrentX() - defending.getCurrentX();
			int dy = attacking.getCurrentY() - defending.getCurrentY();
			int radius = Skill.SKILL_BOMBING_RANGE[attacking.getSkillLevel(Skill.SKILL_BOMBING)];
			
			if (dx * dx + dy * dy > radius * radius)
				throw new IllegalOperationException("La flotte est hors de portée.");
		} else {
			if (Math.abs(attacking.getCurrentX() - defending.getCurrentX()) > 1 ||
					Math.abs(attacking.getCurrentY() - defending.getCurrentY()) > 1)
				throw new IllegalOperationException("Les deux flottes ne sont pas adjacentes.");
		}
		
		if (attacking.getMovement() == 0)
			throw new IllegalOperationException("Vous n'avez pas assez de mouvement pour attaquer la flotte ennemie.");
		
		if (attacking.isInHyperspace())
			throw new IllegalOperationException("Votre flotte ne peut attaquer en hyperespace.");
		
		if (defending.isEndingJump())
			throw new IllegalOperationException("Votre flotte ne peut attaquer une flotte en hyperespace.");
		
		if (!ContractManager.isNpcAvailable(player, defending))
			throw new IllegalOperationException("Cette flotte ne peut être " +
				"attaquée que par un autre joueur dans le cadre d'une mission.");
		
		if (enemy.isAi() && defending.getSkillLevel(Skill.SKILL_PIRATE) == -1 &&
				player.getTreatyWithPlayer(defending.getIdOwner()) != Treaty.ENEMY)
			throw new IllegalOperationException("Vous ne pouvez pas attaquer de flotte PNJ.");
		
//		double defendingPayload= defending.getPayload();
//		double attackingPayload= attacking.getPayload();
//		ItemContainer defendingContainer =
//			DataAccess.getItemContainerByFleet(defending.getId());
//		ItemContainer attackingContainer =
//			DataAccess.getItemContainerByFleet(attacking.getId());
		
		Report report = BattleTools.battle(mode, attacking, defending, bombing);
		
		Fleet newAttackingFleet = DataAccess.getFleetById(attacking.getId());
		if (newAttackingFleet == null)
			UpdateTools.queueEffectUpdate(new Effect(Effect.TYPE_FLEET_DESTRUCTION,
				attacking.getCurrentX(), attacking.getCurrentY(),
				attacking.getIdCurrentArea()), 0, false);
		
		Fleet newDefendingFleet = DataAccess.getFleetById(defending.getId());
		if (newDefendingFleet == null)
			UpdateTools.queueEffectUpdate(new Effect(Effect.TYPE_FLEET_DESTRUCTION,
				defending.getCurrentX(), defending.getCurrentY(),
				defending.getIdCurrentArea()), 0, false);
		
		// Vérifie si les flottes n'ont pas plus d'item que possible
		//defendingContainer=newDefendingFleet.isOverLoaded(defendingContainer, defendingPayload);
		//attackingContainer=newAttackingFleet.isOverLoaded(attackingContainer, attackingPayload);
		
		UpdateTools.queueNewEventUpdate(enemy.getId(), false);
		
		// Met à jour la liste des flottes des joueurs
		UpdateTools.queuePlayerFleetsUpdate(player.getId(), false);
		UpdateTools.queuePlayerFleetsUpdate(enemy.getId(), false);
		
		// Met à jour l'XP des joueurs
		UpdateTools.queueXpUpdate(player.getId(), false);
		UpdateTools.queueXpUpdate(enemy.getId(), false);
		
		// Met à jour le secteur
		UpdateTools.queueAreaUpdate(attacking.getIdCurrentArea());
		
		if (report == null) {
			return new JSONStringer().object().
				key(ReportData.FIELD_DELUDE).	value(true).
				endObject().
				toString();
		} else {
			return ReportTools.getReport(null, player, DataAccess.getReportById(report.getId())).toString();
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
}



