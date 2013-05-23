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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;

import fr.fg.client.core.settings.Settings;
import fr.fg.client.data.AnswerData;
import fr.fg.client.openjwt.core.Config;

public class Action implements RequestCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static HashMap<String, String> NO_PARAMETERS =
		new HashMap<String, String>();
	
	private final static ActionCallbackAdapter DEFAULT_CALLBACK =
		new ActionCallbackAdapter();
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// Nombre total de requêtes exécutées
	private static int requestsCount = 0;
	
	// Temps total passé a éxécuter des requetes
	private static long totalLag = 0;
	
	// Lag de la dernière requete
	private static int lastLag = 0;
	
	// Nombre de requetes qui ont échoué
	private static int errorsCount = 0;
	
	// Indique si la connexion a été perdue
	private static boolean disconnected = false;
	
	private String uri;
	
	private Map<String, String> params;
	
	private ActionCallback callback;
	
	private Request request;
	
	private int attempts;
	
	private long startTime;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Action(String uri, Map<String, String> params) {
		this(uri, params, DEFAULT_CALLBACK);
	}
	
	public Action(String uri, Map<String, String> params, ActionCallback callback) {
		this.uri = uri;
		this.params = params;
		this.callback = callback;
		this.attempts = 0;
		
		if (Settings.getSecurityKey() != null)
			params.put("seckey", Settings.getSecurityKey());
		
		doRequest();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isPending() {
		return request != null && request.isPending();
	}
	
	public void onError(Request request, Throwable exception) {
		retry(String.valueOf(exception.getMessage()));
	}
	
	public void onResponseReceived(Request request, Response response) {
		if (callback != null) {
			if (response.getStatusCode() == 200) {
				if (!uri.toLowerCase().contains("polling")) {
					int lag = (int) (System.currentTimeMillis() - startTime);
					totalLag += lag;
					lastLag = lag;
					requestsCount++;
				}
				
				AnswerData answerData = AnswerData.wrap(response.getText());
				
				String type = answerData.getType();
				
				if (type.equals(AnswerData.TYPE_SUCCESS)) {
					callback.onSuccess(answerData);
				} else if (type.equals(AnswerData.TYPE_ERROR)) {
					callback.onFailure(answerData.getStringData());
				} else if (type.equals(AnswerData.TYPE_CONFIRM_PASSWORD) ||
						type.equals(AnswerData.TYPE_WRONG_PASSWORD)) {
					FaultManager.showConfirmPasswordDialog(uri, params, callback,
							type.equals(AnswerData.TYPE_WRONG_PASSWORD));
				} else if (type.equals(AnswerData.TYPE_DISCONNECTED)) {
					disconnected = true;
					FaultManager.showDisconnectedDialog();
				}
				cleanUp();
			} else {
				retry(String.valueOf(response.getStatusCode()));
			}
		} else {
			if (response.getStatusCode() != 200) {
				retry(String.valueOf(response.getStatusCode()));
			}
			cleanUp();
		}
	}
	
	public final static int getAverageLag() {
		return requestsCount == 0 ? 0 : (int) (totalLag / requestsCount);
	}
	
	public final static int getLastLag() {
		return lastLag;
	}
	
	public final static boolean isDisconnected() {
		return disconnected;
	}

	public final static int getErrorsCount() {
		return errorsCount;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void doRequest() {
		// Encode les paramètres de la requête
		StringBuffer requestData = new StringBuffer();
		boolean first = true;
		for (String key : params.keySet()) {
			if (first)
				first = false;
			else
				requestData.append("&");
			
			requestData.append(key);
			requestData.append("=");
			requestData.append(URL.encodeComponent(params.get(key)));
		}
		
		// Exécute la requête
		RequestBuilder requestBuilder = new RequestBuilder(
				RequestBuilder.POST, Config.getServerUrl() + uri + ".do");
		requestBuilder.setHeader(
				"Content-type", "application/x-www-form-urlencoded");
		
		try {
			this.startTime = System.currentTimeMillis();
			request = requestBuilder.sendRequest(requestData.toString(), this);
		} catch (RequestException e) {
			retry(String.valueOf(e.getMessage()));
		}
	}
	
	private void retry(String error) {
		attempts++;
		errorsCount++;
		
		if (attempts < 3) {
			Timer t = new Timer() {
				public void run() {
					doRequest();
				}
			};
			t.schedule(200);
		} else {
			if (callback != null) {
				if (error.equals("404"))
					callback.onFailure("Serveur en cours de maintenance.");
				else
					callback.onFailure("Erreur " + error);
			}
			cleanUp();
		}
	}
	
	private void cleanUp() {
		uri = null;
		params = null;
		callback = null;
		request = null;
	}
}
