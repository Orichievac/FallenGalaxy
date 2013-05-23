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

package fr.fg.server.task.daily;

import java.util.ArrayList;
import java.util.List;

import fr.fg.server.data.AllyNews;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Message;
import fr.fg.server.data.Player;
import fr.fg.server.util.Utilities;

public class CleanUpMessages extends Thread {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void run() {
		this.setName("CleanUpMessages (daily)");
		// Efface les messages périmés
		List<Message> messages =
			new ArrayList<Message>(DataAccess.getAllMessages());
		
		long now = Utilities.now();
		
		for (Message message : messages) {
			long deadlineReceiver = GameConstants.MESSAGE_LIFESPAN;
			if(message.getReceiver()!=null){
			deadlineReceiver = now -
				(message.getReceiver().hasRight(Player.PREMIUM) ?
					GameConstants.MESSAGE_LIFESPAN_PREMIUM :
					GameConstants.MESSAGE_LIFESPAN);
			}
			long deadlineAuthor = now;
			if (message.getIdAuthor() != 0)
				deadlineAuthor = now -
					(message.getAuthor().hasRight(Player.PREMIUM) ?
						GameConstants.MESSAGE_LIFESPAN_PREMIUM :
						GameConstants.MESSAGE_LIFESPAN);
			
			if (message.getDate() < deadlineReceiver) {
				if (message.getDate() < deadlineAuthor) {
					if (message.isBookmarked()) {
						if (message.getIdAuthor() != 0) {
							// Message supprimé pour l'auteur
							synchronized (message.getLock()) {
								message = DataAccess.getEditable(message);
								message.setDeleted(message.getIdAuthor());
								message.save();
							}
						}
					} else {
						// Message supprimé pour l'auteur et le destinataire
						message.delete();
					}
				} else {
					if (message.isBookmarked()) {
						// Rien à faire
					} else {
						// Message supprimé pour le destinataire
						synchronized (message.getLock()) {
							message = DataAccess.getEditable(message);
							message.setDeleted(message.getIdPlayer());
							message.save();
						}
					}
				}
			} else {
				if (message.getDate() < deadlineAuthor) {
					if (message.getIdAuthor() != 0) {
						// Message supprimé pour l'auteur
						synchronized (message.getLock()) {
							message = DataAccess.getEditable(message);
							message.setDeleted(message.getIdAuthor());
							message.save();
						}
					}
				} else {
					// Rien à faire
				}
			}
		}
		
		// Efface les news périmées
		List<AllyNews> allyNews =
			new ArrayList<AllyNews>(DataAccess.getAllAllyNews());
		long deadline = now - GameConstants.NEWS_LIFESPAN;
		
		for (AllyNews news : allyNews) {
			if (news.getIdParent() != 0 || news.isSticky())
				continue;
			
			boolean delete = news.getDate() < deadline;
			
			if (delete) {
				List<AllyNews> answers = DataAccess.getAllyNewsByParent(news.getId());
				
				synchronized (answers) {
					for (AllyNews answer : answers)
						if (answer.getDate() >= deadline) {
							delete = false;
							break;
						}
					
				}
			}
			
			if (delete)
				news.delete();
		}
		
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
