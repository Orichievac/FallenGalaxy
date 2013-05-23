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

package fr.fg.client.ajax;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.OptionPaneListener;

public class FaultManager {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static FaultManager instance = new FaultManager();
	
	private boolean confirmPasswordDialogVisible;
	
	private boolean disconnectedDialogVisible;
	
	private String cachedPassword;
	
	private List<ActionParameters> pendingActions;
	
	private Timer pendingActionsTimer;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private FaultManager() {
		confirmPasswordDialogVisible = false;
		disconnectedDialogVisible = false;
		cachedPassword = "";
		pendingActions = new ArrayList<ActionParameters>();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public static void showDisconnectedDialog() {
		if (instance.disconnectedDialogVisible)
			return;
		
		instance.disconnectedDialogVisible = true;
		
		JSOptionPane.showMessageDialog("Vous avez été déconnecté. " +
			"Cochez l'option <b>Proxy</b> dans les options si vous avez " +
			"souvent des problèmes de connexion.<br/>" +
			"Voulez-vous recharger le jeu ?", "Déconnexion",
			JSOptionPane.YES_OPTION | JSOptionPane.NO_OPTION,
			JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
				public void optionSelected(Object option) {
					if ((Integer) option == JSOptionPane.YES_OPTION) {
						Window.Location.reload();
					}
					
					instance.disconnectedDialogVisible = false;
				}
		});
	}
	
	public static void showConfirmPasswordDialog(String uri,
			Map<String, String> params, ActionCallback callback,
			boolean resetPassword) {
		if (resetPassword) {
			// Le mot de passe en cache est invalide
			instance.cachedPassword = "";
			
			if (instance.pendingActions != null) {
				instance.pendingActionsTimer.cancel();
				instance.pendingActionsTimer = null;
			}
		}
		
		if (instance.cachedPassword.length() > 0 &&
				instance.pendingActionsTimer == null) {
			// Mot de passe en cache valide
			params.put("_password", instance.cachedPassword);
			new Action(uri, params, callback);
		} else {
			// Mot de passe pas en cache ou pas encore vérifié sur une requete
			instance.pendingActions.add(new ActionParameters(
					uri, params, callback));
			
			if (instance.confirmPasswordDialogVisible)
				return;
			
			instance.confirmPasswordDialogVisible = true;
			
			// Demande une confirmation du mot de passe si
			// l'adresse IP du joueur a changée
			JSOptionPane.showInputDialog("Votre adresse IP a changé. " +
				"Confirmez votre mot de passe pour pouvoir continuer :",
				"Confirmation", JSOptionPane.OK_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if (option != null)
							instance.cachedPassword = (String) option;
						
						// Lance une requete avec la 1e action en attente pour
						// vérifier que le mot de passe est valide
						instance.pendingActions.remove(0).doRequest(instance.cachedPassword);
						
						// Lance toutes les requetes suivantes 3 secondes plus
						// tard
						instance.pendingActionsTimer = new Timer() {
							@Override
							public void run() {
								instance.pendingActionsTimer = null;
								
								while (instance.pendingActions.size() > 0) {
									instance.pendingActions.remove(0).doRequest(instance.cachedPassword);
								}
							}
						};
						instance.pendingActionsTimer.schedule(3000);
						
						instance.confirmPasswordDialogVisible = false;
					}
			}, "", true);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static class ActionParameters {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private String uri;
		
		private Map<String, String> params;
		
		private ActionCallback callback;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public ActionParameters(String uri, Map<String, String> params,
				ActionCallback callback) {
			this.uri = uri;
			this.params = params;
			this.callback = callback;
		}

		// ----------------------------------------------------- METHODES -- //
		
		public void doRequest(String password) {
			params.put("_password", password);
			new Action(uri, params, callback);
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
