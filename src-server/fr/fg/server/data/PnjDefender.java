package fr.fg.server.data;

import fr.fg.server.data.base.PnjDefenderBase;



public class PnjDefender extends PnjDefenderBase{
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public PnjDefender() {
		// Nécessaire pour la construction par réflection
	}
	
	public PnjDefender(int idPlayer, int idPnj) {
		setIdPlayer(idPlayer);
		setIdPnj(idPnj);
	}


	
	// --------------------------------------------------------- METHODES -- //
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
