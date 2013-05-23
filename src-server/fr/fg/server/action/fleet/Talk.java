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


import fr.fg.client.data.DialogData;
import fr.fg.server.contract.ContractManager;
import fr.fg.server.contract.dialog.DialogDetails;
import fr.fg.server.data.Contract;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.JSONStringer;

public class Talk extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		Fleet source = DataAccess.getFleetById((Integer) params.get("source"));
		Fleet target = DataAccess.getFleetById((Integer) params.get("target"));
		int option = (Integer) params.get("option");
		
		if (source == null)
			throw new IllegalOperationException("Flotte inexistante");
		
		if (target == null)
			return getEndOfDialog();
		
		if (target.isStealth() && !player.isLocationRevealed(
				target.getCurrentX(), target.getCurrentY(), target.getArea()))
			throw new IllegalOperationException("Flotte inexistante.");
		
		if (source.getIdOwner() != player.getId())
			throw new IllegalOperationException("Cette flotte ne vous appartient pas.");
		
		if (source.getIdCurrentArea() != target.getIdCurrentArea())
			return getEndOfDialog();
		
		if (source.isDelude())
			throw new IllegalOperationException("Vous ne pouvez pas interagir avec un leurre.");
		
		if (Math.abs(source.getCurrentX() - target.getCurrentX()) > 1 ||
			Math.abs(source.getCurrentY() - target.getCurrentY()) > 1)
			return getEndOfDialog();
		
		if (source.getMovement() == 0)
			throw new IllegalOperationException("Vous n'avez pas assez de mouvement interagir avec la flotte.");
		
		if (source.isInHyperspace())
			throw new IllegalOperationException("Votre flotte ne peut interagir en hyperespace.");
		
		if (target.isEndingJump())
			throw new IllegalOperationException("Votre flotte ne peut interagir avec une flotte en hyperespace.");
		
		if (ContractManager.getNpcAction(player, target).equals(Fleet.NPC_ACTION_NONE))
			throw new IllegalOperationException("Aucune interaction possible avec cette flotte.");
		
		// Déclenche l'évènement de dialogue
		Contract contract = target.getContract();
		DialogDetails dialog = ContractManager.talk(contract, source, target, option);
		
		if (dialog == null)
			return getEndOfDialog();
		
		JSONStringer json = new JSONStringer();
		
		json.object().
			key(DialogData.FIELD_AVATAR).	value(target.getOwner().getAvatar()).
			key(DialogData.FIELD_TALKER).	value(target.getOwner().getLogin()).
			key(DialogData.FIELD_CONTENT).	value(dialog.getContent()).
			key(DialogData.FIELD_OPTIONS).	array();
		
		for (int i = 0; i < dialog.getOptions().length; i++)
			json.value(dialog.getOptions()[i]);
		
		json.endArray().
			key(DialogData.FIELD_VALID_OPTIONS).	array();
		
		for (int i = 0; i < dialog.getValidOptions().length; i++)
			json.value(dialog.getValidOptions()[i]);
		
		json.endArray().
			endObject();
		
		return json.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private String getEndOfDialog() {
		return new JSONStringer().
			object().
			key(DialogData.FIELD_TALKER).	value("").
			endObject().toString();
	}
}
