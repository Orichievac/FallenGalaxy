/*
Copyright 2012 jgottero

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.fg.client.data.ChangelogData;
import fr.fg.server.data.Changelog;
import fr.fg.server.data.DataAccess;
import fr.fg.server.util.JSONStringer;

public class ChangelogTools {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int DEFAULT_LIMIT = 10;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getLastChangelogs(JSONStringer json) {
		return getLastChangelogs(json, DEFAULT_LIMIT);
	}
	
	public static JSONStringer getLastChangelogs(JSONStringer json, int limit) {
		if (json == null)
			json = new JSONStringer();
		
		List<Changelog> changelogs = new ArrayList<Changelog>(DataAccess.getAllChangelogs());
		
		Collections.sort(changelogs, new Comparator<Changelog>() {
			public int compare(Changelog c1, Changelog c2) {
				return c1.getDate() < c2.getDate() ? 1 : -1;
			}
		});
		
		json.array();
		
		for (int i = 0; i < Math.min(changelogs.size(), limit); i++) {
			Changelog changelog = changelogs.get(i);
			
			json.object().
				key(ChangelogData.FIELD_ID).	value(changelog.getId()).
				key(ChangelogData.FIELD_TEXT).	value(changelog.getText()).
				key(ChangelogData.FIELD_DATE).	value(changelog.getDate()).
				endObject();
		}
		
		json.endArray();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
