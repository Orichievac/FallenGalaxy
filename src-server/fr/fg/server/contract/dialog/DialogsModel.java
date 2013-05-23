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

package fr.fg.server.contract.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.fg.server.data.Contract;
import fr.fg.server.data.ContractDialog;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.events.GameEventsDispatcher;
import fr.fg.server.events.impl.DialogUpdateEvent;
import fr.fg.server.i18n.Messages;

public class DialogsModel {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private List<DialogKey> keys;
	
	private Map<String, Dialog> dialogs;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public DialogsModel() {
		this.dialogs = Collections.synchronizedMap(
			new HashMap<String, Dialog>());
		this.keys = Collections.synchronizedList(new ArrayList<DialogKey>());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addDialog(Dialog dialog) {
		dialogs.put(dialog.getNpcType(), dialog);
	}
	
	public void addKey(DialogKey key) {
		keys.add(key);
	}
	
	public String getCurrentEntry(int idPlayer, int idFleet) {
		List<ContractDialog> dialogs = DataAccess.getDialogsByFleet(idFleet);
		
		synchronized (dialogs) {
			for (ContractDialog dialog : dialogs) {
				if (dialog.getIdPlayer() == idPlayer)
					return dialog.getCurrentEntry();
			}
		}
		
		return null;
	}
	
	public void setCurrentEntry(int idPlayer, int idFleet, String currentEntry) {
		List<ContractDialog> dialogs = DataAccess.getDialogsByFleet(idFleet);
		
		synchronized (dialogs) {
			for (ContractDialog dialog : dialogs) {
				if (dialog.getIdPlayer() == idPlayer) {
					if (currentEntry == null) {
						dialog.delete();
					} else {
						synchronized (dialog.getLock()) {
							dialog = DataAccess.getEditable(dialog);
							dialog.setCurrentEntry(currentEntry);
							dialog.save();
						}
					}
					return;
				}
			}
		}
		
		ContractDialog dialog = new ContractDialog(idFleet, idPlayer, currentEntry);
		dialog.save();
	}
	
	public DialogDetails talk(Contract contract, Fleet source, Fleet target, int option)
			throws IllegalOperationException {
		// Récupère les dialogues du PNJ
		Dialog currentDialog = dialogs.get(target.getNpcType());
		
		if (currentDialog == null)
			throw new IllegalOperationException("Dialogue invalide.");
		
		// Récupère la dernière discussion entre le PNJ et le joueur
		String lastEntryName = getCurrentEntry(source.getIdOwner(), target.getId());
		if (lastEntryName == null)
			lastEntryName = DialogEntry.FIRST_ENTRY;
		
		// Vérifie que l'entrée existe
		DialogEntry lastEntry = currentDialog.getEntry(lastEntryName);
		if (lastEntry == null) {
			// Si l'entrée n'existe pas, on réinitialise la conversation (le
			// PNJ a changé d'état)
			lastEntryName = DialogEntry.FIRST_ENTRY;
			lastEntry = currentDialog.getEntry(lastEntryName);
			option = -1;
			
			if (lastEntry == null)
				throw new IllegalOperationException("Entrée de dialogue invalide.");
		}
		
		// Vérifie que le joueur peut sélectionner cette option
		String targetEntryName;
		if (option == -1) {
			targetEntryName = lastEntryName;
		} else {
			if (!lastEntry.isValidOption(option, contract, source, target))
				throw new IllegalOperationException("Option invalide.");
			targetEntryName = lastEntry.getOption(option).getTargetEntry();
		}
		
		setCurrentEntry(source.getIdOwner(), target.getId(), targetEntryName);
		
		if (option != -1)
			GameEventsDispatcher.fireGameNotification(new DialogUpdateEvent(
				contract.getId(), source.getIdOwner(), source.getId(),
				target.getId(), lastEntryName, targetEntryName));
		
		if (targetEntryName.equals(DialogEntry.END_OF_DIALOG)) {
			return null;
		} else {
			DialogEntry targetEntry = currentDialog.getEntry(targetEntryName);
			
			String base = "contract." + contract.getType() + "." +
				contract.getVariant() + "." + target.getNpcType() + "." +
				targetEntryName;
			String content = parseKeys(Messages.getString(base + ".dialog"),
				contract, source, target);
			
			String[] options = new String[targetEntry.getOptions().length];
			boolean[] validOptions = new boolean[options.length];
			
			for (int i = 0; i < options.length; i++) {
				options[i] = parseKeys(Messages.getString(base + ".option" + i),
					contract, source, target);
				validOptions[i] = targetEntry.isValidOption(i, contract, source, target);
			}
			
			return new DialogDetails(content, options, validOptions);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private String parseKeys(String text, Contract contract, Fleet source, Fleet target) {
		for (DialogKey key : keys) {
			String code = "%" + key.getName() + "%";
			if (text.contains(code))
				text = text.replace(code, key.getValue(contract, source, target));
		}
		
		if (text.contains("%player%"))
			text = text.replace("%player%", source.getOwner().getLogin());
		if (text.contains("%npc%"))
			text = text.replace("%npc%", target.getOwner().getLogin());
		
		return text;
	}
}
