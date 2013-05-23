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

package fr.fg.server.contract;

import fr.fg.server.contract.dialog.DialogDetails;
import fr.fg.server.contract.dialog.DialogsModel;
import fr.fg.server.contract.dialog.XmlDialogParser;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Contract;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Messages;
import fr.fg.server.util.Utilities;

public abstract class ContractModel {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private String type;
	
	private int maxAttendees;
	
	private DialogsModel dialogsModel;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	protected ContractModel(int maxAttendees) throws Exception {
		this.maxAttendees = maxAttendees;
		this.type = getClass().getSimpleName();
		this.dialogsModel = XmlDialogParser.parse(
			ContractModel.class.getResource("impl/" + type + ".xml"), this);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isAdmissible(Player player) {
		return false;
	}
	
	public boolean isAdmissible(Ally ally) {
		return false;
	}
	
	public String getType() {
		return type;
	}
	
	public int getMaxAttendees() {
		return maxAttendees;
	}
	
	public String getDescription() {
		return Messages.getString("contract." + type + ".description");
	}
	
	public String getTitle(Contract contract) {
		return Messages.getString("contract." + getType() + "." + contract.getVariant() + ".title");
	}
	
	public String getDescription(Contract contract) {
		return Messages.getString("contract." + getType() + "." + contract.getVariant() + ".description");
	}
	
	public String getGoal(Contract contract) {
		return Messages.getString("contract." + getType() + "." + contract.getVariant() + ".goal");
	}
	
	public DialogDetails talk(Contract contract, Fleet source, Fleet target,
			int option) throws IllegalOperationException {
		return dialogsModel.talk(contract, source, target, option);
	}
	
	public String getXpImg() {
		return Utilities.getXpImg();
	}
	
	public abstract void createContract(Player player, int maxAttendees);
	
	public abstract Contract createContractPvP(Player player, int maxAttendees);
	
	public abstract void createContract(Ally ally, int maxAttendees);
	
	public abstract Contract createContractAvA(Ally ally, int maxAttendees);
	
	public abstract String getNpcAction(Contract contract, Player player, Fleet target);
	
	public abstract void launch(Contract contract);
	
	public abstract String getDetailedGoal(Contract contract, Player player);
	
	public abstract String getDetailedGoal(Contract contract, Ally ally);
	
	public abstract ContractState getState(Contract contract, Player player);
	
	public abstract String getContractType();

	public abstract long getDifficulty(Player player);
	
	public abstract long getDifficulty(Ally ally);
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
