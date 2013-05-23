/*
Copyright 2010 Jeremie Gottero, Thierry Chevalier

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

package fr.fg.server.data;

import java.util.List;

import fr.fg.server.data.base.ConnectionBase;
import fr.fg.server.util.Utilities;

public class Connection extends ConnectionBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Connection() {
		// Nécessaire pour la construction par réflection
	}
	
	public Connection(int ip, int idPlayer) {
		setIp(ip);
		setStart(Utilities.now());
		setEnd(0);
		setIdPlayer(idPlayer);
	}
	
	// --------------------------------------------------------- METHODES -- //
	public static boolean compare(int ip, int id, long time)
	{
		boolean ok = false;
		List<Connection> connections  = DataAccess.getConnectionsByPlayer(id);
		for(Connection connection : connections)
		{
			if(connection.getEnd()<time)
			{
				if(connection.getId()==ip)
				{
					ok = true;
					break;
				}
			}
		}
		return ok;
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
