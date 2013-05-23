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

package fr.fg.server.action.structure;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.contract.ContractManager;
import fr.fg.server.core.StructureTools;
import fr.fg.server.core.Update;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Effect;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Skill;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureSkill;
import fr.fg.server.data.Treaty;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Utilities;

public class CastStasis extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		long idStructure = (Long) params.get("structure");
		int idTarget = (Integer) params.get("target");
		
		Structure structure = StructureTools.getStructureByIdWithChecks(
				idStructure, player.getId());
		
		if (!structure.isActivated())
			throw new IllegalOperationException("La structure est désactivée.");
		
		// Recherche la compétence
		List<StructureSkill> skills = DataAccess.getSkillsByStructure(idStructure);
		StructureSkill skill = null;
		
		synchronized (skills) {
			for (StructureSkill structureSkill : skills) {
				if (structureSkill.getType() == StructureSkill.TYPE_STASIS) {
					skill = structureSkill;
					break;
				}
			}
		}
		
		if (skill == null)
			throw new IllegalOperationException("La structure ne dipose pas de la compétence.");
		
		// Vérifie que la compétence est rechargée
		if (skill.getReload() > Utilities.now())
			throw new IllegalOperationException("La compétence n'est pas encore rechargée.");
		
		// Vérifie que la cible de la stase existe
		Fleet target = DataAccess.getFleetById(idTarget);
		
		if (target == null)
			throw new IllegalOperationException("La cible n'existe pas.");
		
		// Vérifie que la cible est valide
		String treaty = target.getOwner().getTreatyWithPlayer(player);
		if ((treaty.equals(Treaty.NEUTRAL) && target.getSkillLevel(Skill.SKILL_PIRATE) == -1) ||
				!ContractManager.isNpcAvailable(player, target))
			throw new IllegalOperationException("Cible invalide.");
		
		boolean allied = treaty.equals(Treaty.PLAYER) ||
			treaty.equals(Treaty.ALLY) || Treaty.isPact(treaty);
		
		if (target.isInHyperspace())
			throw new IllegalOperationException("Cible invalide.");
		
		// Vérifie que la cible n'exécute pas une action non interruptible
		if (allied && (target.getCurrentAction().equals(Fleet.CURRENT_ACTION_COLONIZE) ||
				target.getCurrentAction().equals(Fleet.CURRENT_ACTION_ATTACK_STRUCTURE) ||
				target.getCurrentAction().equals(Fleet.CURRENT_ACTION_JUMP)) ||
				target.getCurrentAction().equals(Fleet.CURRENT_ACTION_DISMOUNT_STRUCTURE) ||
				target.getCurrentAction().equals(Fleet.CURRENT_ACTION_MOUNT_STRUCTURE) ||
				target.getCurrentAction().equals(Fleet.CURRENT_ACTION_REPAIR_STRUCTURE) ||
				target.getCurrentAction().equals(Fleet.CURRENT_ACTION_MIGRATE))
			throw new IllegalOperationException("La flotte exécute une action qui ne peut être écourtée.");
		
		// Vérifie que la cible est à portée
		int dx = target.getCurrentX() - structure.getX();
		int dy = target.getCurrentY() - structure.getY();
		
		if (dx * dx + dy * dy > StructureSkill.STASIS_RANGE * StructureSkill.STASIS_RANGE)
			throw new IllegalOperationException("La cible n'est pas à portée.");
		
		long now = Utilities.now();
		
		synchronized (target.getLock()) {
			target = DataAccess.getEditable(target);
			if (allied) {
				target.setCurrentAction(Fleet.CURRENT_ACTION_NONE);
				target.setMovementReload(0);
			} else {
				if (target.getMovementReload() != 0) {
					target.setMovementReload(Math.max(now +
						StructureSkill.STASIS_LENGTH,
						target.getMovementReload()));
				} else {
					target.doAction(Fleet.CURRENT_ACTION_NONE, now +
						StructureSkill.STASIS_LENGTH);
				}
			}
			target.save();
		}
		
		synchronized (skill.getLock()) {
			skill = DataAccess.getEditable(skill);
			skill.setLastUse(now);
			skill.setReload(now + skill.getReloadLength());
			skill.save();
		}
		
		// Mise à jour du secteur et de la flotte
		int targetX = target.getCurrentX();
		int targetY = target.getCurrentY();
		
		Point structureLocation = new Point(structure.getX(), structure.getY());
		Point targetLocation = new Point(targetX, targetY);
		
		Effect effect = new Effect(Effect.TYPE_EMP,
			targetX, targetY, structure.getIdArea());
		
		List<Update> updates = new ArrayList<Update>();
		updates.add(Update.getAreaUpdate());
		updates.add(Update.getEffectUpdate(effect));
		
		UpdateTools.queueEffectUpdate(effect, player.getId(), false);
		UpdateTools.queueAreaUpdate(structure.getIdArea(), player.getId(), structureLocation, targetLocation);
		
		if (target.getIdOwner() != player.getId())
			UpdateTools.queuePlayerFleetUpdate(target.getIdOwner(), target.getId());
		else
			updates.add(Update.getPlayerFleetUpdate(target.getId()));
		
		return UpdateTools.formatUpdates(player, updates);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
