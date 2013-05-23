/*
Copyright 2010 Thierry Chevalier

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

import fr.fg.server.data.base.MessageOfTheDayBase;
import fr.fg.server.util.Utilities;

public class MessageOfTheDay extends MessageOfTheDayBase {
	// ------------------------------------------------------- CONSTANTES -- //
	public static int	CHAT = 0,
						INGAME = 1;
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	public MessageOfTheDay()
	{
		
	}
	
	public MessageOfTheDay(int motdType, String message)
	{
		int type = motdType;
		if(motdType!=CHAT && motdType!=INGAME)
		{
			type = INGAME;
			//Si le type de message du jour donné est invalide, on le met en INGAME par defaut
		}
		setId(type);
		setMessage(message);
		setDate((int)Utilities.now());
	}
	// --------------------------------------------------------- METHODES -- //
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
