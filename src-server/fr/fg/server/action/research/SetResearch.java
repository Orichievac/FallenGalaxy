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

package fr.fg.server.action.research;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import fr.fg.server.core.ResearchTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.data.Research;
import fr.fg.server.data.Technology;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;

public class SetResearch extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params, Session session)
			throws Exception {
		int[] pendingTechnologies = new int[3];
		
		player.updateResearch();
		List<Research> researches = new ArrayList<Research>(player.getResearches());
		
		for (int i = 0; i < pendingTechnologies.length; i++) {
			pendingTechnologies[i] = (Integer) params.get("queue" + i);
			
			if (pendingTechnologies[i] == 0)
				break;
			
			// Vérifie que le joueur a un compte premium pour pouvoir mettre
			// des technologies en attente
			if (i > 0 && !player.hasRight(Player.PREMIUM))
				throw new IllegalOperationException(
					"Compte premium uniquement.");
			
			// Vérifie que la technologie existe
			Technology technology = Technology.getTechnologyById(
				pendingTechnologies[i]);
			
			if (technology == null)
				throw new IllegalOperationException(
					"Cette technologie n'existe pas.");
			
			// Vérifie qu'une même technologie n'est pas présente deux fois
			// dans la liste d'attente
			for (int j = 0; j < i; j++)
				if (pendingTechnologies[i] == pendingTechnologies[j])
					throw new IllegalOperationException(
						"File d'attente invalide.");
			
			// Teste si la technologie a déjà été recherchée
			for (Research research : researches) {
				if (research.getIdTechnology() == technology.getId()) {
					if (research.getProgress() >= 1)
						throw new IllegalOperationException(
							"Vous avec déjà recherché cette technologie.");
				}
			}
			
			// Vérifie que les prérequis de la technologie demandée sont remplis
			loop:for (int requirement : technology.getRequirements()) {
				for (Research research : researches) {
					if (research.getIdTechnology() == requirement &&
							research.getProgress() >= 1)
						continue loop;
				}
				
				throw new IllegalOperationException(
						"Vous n'avez pas les technologies nécessaires.");
			}
		}
		
		// Modifie les recherches en cours
		for (Research research : researches) {
			boolean found = false;
			
			for (int i = 0; i < pendingTechnologies.length; i++) {
				if (pendingTechnologies[i] == research.getIdTechnology()) {
					research = DataAccess.getEditable(research);
					research.setQueuePosition(i);
					research.save();
					
					found = true;
					break;
				}
			}
			
			if (!found && research.getQueuePosition() != -1) {
				synchronized (research) {
					research = DataAccess.getEditable(research);
					research.setQueuePosition(-1);
					research.save();
				}
			}
		}
		
		// Lance les nouvelles recherches
		for (int i = 0; i < pendingTechnologies.length; i++) {
			if (pendingTechnologies[i] == 0)
				break;
			
			boolean found = false;
			
			for (Research research : researches) {
				if (pendingTechnologies[i] == research.getIdTechnology()) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				Research reseach = new Research(
					pendingTechnologies[i], 0, i, player.getId());
				reseach.save();
			}
		}
		
		return ResearchTools.getResearchData(null, player).toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
