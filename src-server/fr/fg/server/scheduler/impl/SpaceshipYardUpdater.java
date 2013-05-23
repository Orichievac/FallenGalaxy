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

package fr.fg.server.scheduler.impl;

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureSpaceshipYard;
import fr.fg.server.events.GameEvent;
import fr.fg.server.events.GameEventListener;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.BuildSlotChangeEvent;
import fr.fg.server.scheduler.JobScheduler;
import fr.fg.server.util.Utilities;

public class SpaceshipYardUpdater extends JobScheduler<Long>
		implements GameEventListener {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public SpaceshipYardUpdater() {
		List<StructureSpaceshipYard> spaceshipYards =
			new ArrayList<StructureSpaceshipYard>(
				DataAccess.getAllSpaceshipYards());
		
		for (StructureSpaceshipYard spaceshipYard : spaceshipYards)
			updateAndQueueUpdate(spaceshipYard, false);
		
		GameEventsDispatcher.addGameEventListener(this,
			BuildSlotChangeEvent.class);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void onGameEvent(GameEvent event) throws Exception {
		if (event instanceof BuildSlotChangeEvent) {
			BuildSlotChangeEvent gameEvent = (BuildSlotChangeEvent) event;
			queueUpdate(gameEvent.getSource());
		}
	}
	
	@Override
	public void process(Long idStructure, long time) {
		StructureSpaceshipYard spaceshipYard =
			DataAccess.getSpaceshipYardByStructure(idStructure);
		
		updateAndQueueUpdate(spaceshipYard, true);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateAndQueueUpdate(StructureSpaceshipYard spaceshipYard,
			boolean update) {
		synchronized (spaceshipYard.getLock()) {
			spaceshipYard = DataAccess.getEditable(spaceshipYard);
			spaceshipYard.update();
			spaceshipYard.save();
		}
		
		if (update) {
			Structure structure = spaceshipYard.getStructure();
			Player owner = structure.getOwner();
			if (owner.getIdCurrentArea() == structure.getIdArea())
				UpdateTools.queueAreaUpdate(owner);
		}
		
		queueUpdate(spaceshipYard);
	}
	
	private void queueUpdate(StructureSpaceshipYard spaceshipYard) {
		if (spaceshipYard.isBuilding()) {
			int end = spaceshipYard.getBuildEnd();
			if (end != Integer.MAX_VALUE)
				addJob(spaceshipYard.getIdStructure(), Utilities.now() + end);
		}
	}
}
