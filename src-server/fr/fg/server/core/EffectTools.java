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

package fr.fg.server.core;

import fr.fg.client.data.EffectData;
import fr.fg.server.data.Effect;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.Utilities;

public class EffectTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getEffect(JSONStringer json, Effect effect) {
		if (json == null)
			json = new JSONStringer();
		
		json.object().
			key(EffectData.FIELD_ID).		value((effect.getX() * 289 + effect.getY() * 17 + effect.getIdArea()) + Utilities.now() * 100000).
			key(EffectData.FIELD_X).		value(effect.getX()).
			key(EffectData.FIELD_Y).		value(effect.getY()).
			key(EffectData.FIELD_TYPE).		value(effect.getType()).
			key(EffectData.FIELD_ID_AREA).	value(effect.getIdArea()).
			endObject();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
