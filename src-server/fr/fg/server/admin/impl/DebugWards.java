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

package fr.fg.server.admin.impl;

import java.util.ArrayList;
import java.util.List;

import fr.fg.client.data.ScriptData;
import fr.fg.server.admin.ScriptModel;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Ward;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.LoggingSystem;

public class DebugWards extends ScriptModel {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	@Override
	public JSONStringer execute() {
		
		LoggingSystem.getActionLogger().info(this.getClass().getName()+" script launched");
		String msg = new String();
		String logger = new String();
		List<Ward> wards = new ArrayList<Ward>(DataAccess.getAllWards());
		
		for(Ward ward : wards)
		{
			// Test si le proprio de la flotte existe
			if(DataAccess.getPlayerById(ward.getIdOwner())==null)
			{
				logger="Une balise ("+ward.getId()+") n'a pas de propriétaire existant.";
				LoggingSystem.getActionLogger().info(logger);
				msg+=logger+"<br/>";

				ward.delete();
			}
			// Test si la flotte est dans secteur inexistant
			else if(DataAccess.getAreaById(ward.getIdArea())==null)
			{
				logger=ward.getOwner().getLogin()+" ("+ward.getOwner().getId()+")"+
					" à une balise dans un secteur inexistant :"+ward.getIdArea();
				LoggingSystem.getActionLogger().info(logger);
				msg+=logger+".<br/>";

				ward.delete();
			}

		}
		
		msg+="Script terminé avec succès.";
		
		JSONStringer json = new JSONStringer();
		
		
		json.object().
		key(ScriptData.FIELD_MESSAGE)			.value(msg).
		key(ScriptData.FIELD_NAME)				.value(this.getClass().getName()).
		endObject();
		
		LoggingSystem.getActionLogger().info(this.getClass().getName()+" script finish");
		
		
		return json;
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
