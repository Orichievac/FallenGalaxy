/*
Copyright 2010 Nicolas Bosc

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

package fr.fg.server.contract.ally;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fr.fg.server.contract.ContractHelper;
import fr.fg.server.contract.ContractModel;
import fr.fg.server.core.UpdateTools;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Area;
import fr.fg.server.data.Contract;
import fr.fg.server.data.ContractArea;
import fr.fg.server.data.ContractAttendee;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.data.Sector;
import fr.fg.server.data.StarSystem;

public abstract class AllyContractModel extends ContractModel {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private AllyRequirements requirements;
	
	private AllyLocationConstraints locationConstraints;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public AllyContractModel(int maxAttendees, AllyRequirements requirements,
			AllyLocationConstraints locationConstraints) throws Exception {
		super(maxAttendees);
		
		this.requirements = requirements;
		this.locationConstraints = locationConstraints;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public final boolean isAdmissible(Ally ally) {
		return requirements.isAdmissible(ally);
	}
	
	public final void createContractLocations(Ally ally, Contract contract) {
		if (contract.getAreas().size() > 0)
			throw new IllegalArgumentException("Areas already created.");
		
		int areasCount = locationConstraints.getAreasCount(ally);
		boolean multipleSelectionAllowed =
			locationConstraints.allowMultipleAreaSelection();
		
		if (ally.getMediumLevel() == 1 && areasCount > 0) {
			int idArea = ally.getMembers().get(0).getFirstSystem().getIdArea();
			
			ContractArea contractArea = new ContractArea(contract.getId(), idArea);
			contractArea.save();
		} else {
			List<StarSystem> systems = ally.getMembersSystems();
			StarSystem system = systems.get((int) (Math.random() * systems.size()));
			Sector sector = system.getArea().getSector();
			List<Area> areas = new ArrayList<Area>(sector.getAreas());
			
			Iterator<Area> j = areas.iterator();
			while (j.hasNext()) {
				Area area = j.next();
				
				if (area.getGeneralType()== Area.AREA_GENERAL_MINING_X5)
					j.remove();
			}
			
			
			if (!multipleSelectionAllowed && areas.size() < areasCount)
				throw new IllegalArgumentException("Invalid area count.");
			
			int[] idAreas = new int[areasCount];
			
			if (multipleSelectionAllowed) {
				for (int i = 0; i < areasCount; i++)
					idAreas[i] = areas.get((int) (Math.random() * areas.size())).getId();
			} else {
				Collections.shuffle(areas);
				
				for (int i = 0; i < areasCount; i++)
					idAreas[i] = areas.get(i).getId();
			}
			
			for (int i = 0; i < areasCount; i++) {
				ContractArea contractArea = new ContractArea(
					contract.getId(), idAreas[i]);
				contractArea.save();
			}
		}
	}
	
	@Override
	public final void createContract(Ally ally, int maxAttendees) {
		Contract contract = new Contract(getType(), getVariant(ally),
			Contract.TARGET_ALLY, maxAttendees, getDifficulty(ally));
		contract.save();
		
		ContractAttendee attendee = new ContractAttendee(
			contract.getId(), 0, ally.getId());
		attendee.save();
		
		// Génère les secteurs dans lesquels le contrat se déroulera
		createContractLocations(ally, contract);
		
		// Initialise le contrat
		initialize(contract);
		
		// Génère la récompense du contrat
		createReward(contract);
		
		// Génère les modificateurs de relations du contrat
		createRelationships(contract);
	}
	
	@Override
	public final Contract createContractAvA(Ally ally, int maxAttendees) {
		Contract contract = new Contract(getType(), getVariant(ally),
			Contract.TARGET_ALLY, maxAttendees, getDifficulty(ally));
		contract.save();
		
		ContractAttendee attendee = new ContractAttendee(
			contract.getId(), 0, ally.getId());
		attendee.save();
		
		// Génère les secteurs dans lesquels le contrat se déroulera
		createContractLocations(ally, contract);
		
		// Initialise le contrat
		initialize(contract);
		
		// Génère la récompense du contrat
		createReward(contract);
		
		// Génère les modificateurs de relations du contrat
		createRelationships(contract);
		return contract;
	}
	
	@Override
	public void createContract(Player player, int maxAttendees) {
		throw new IllegalStateException("Ally only contract.");
	}
	
	@Override
	public Contract createContractPvP(Player player, int maxAttendees) {
		throw new IllegalStateException("Ally only contract.");
	}
	
	@Override
	public String getDetailedGoal(Contract contract, Player player) {
		throw new IllegalStateException("Ally only contract.");
	}
	
	public abstract void createReward(Contract contract);
	
	public abstract void createRelationships(Contract contract);
	
	public abstract long getDifficulty(Ally ally);
	
	public long getDifficulty(Player player){
		throw new IllegalStateException("Ally only contract.");
	}
	
	public abstract String getVariant(Ally ally);
	
	public void initialize(Contract contract) {
		// A redéfinir au besoin
	}
	
	public void finalize(Contract contract) {
		// A redéfinir au besoin
	}
	
	public void setSuccess(Contract contract, Ally ally) {
		List<ContractAttendee> attendees = new ArrayList<ContractAttendee>(
				contract.getAttendees());
		
		for (ContractAttendee attendee : attendees) {
			if (attendee.getIdAlly() != ally.getId()) {
				attendee.delete();
				
				Ally attendeeAlly = attendee.getAlly();
				
				UpdateTools.queueContractsUpdate(attendeeAlly.getId(), false);
				
				List<Player> members = attendeeAlly.getMembers();
				
				synchronized (members) {
					for (Player member : members)
						UpdateTools.queueContractsUpdate(member.getId());
				}
				//UpdateTools.queueAreaUpdate(attendeeAlly);
			}
		}
		
		synchronized (contract.getLock()) {
			contract = DataAccess.getEditable(contract);
			contract.setFinalizingState();
			contract.save();
		}
		
		ContractHelper.applyRelationships(contract, ally, true);
		
		finalize(contract);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
