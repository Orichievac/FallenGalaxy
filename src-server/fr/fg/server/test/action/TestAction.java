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

package fr.fg.server.test.action;

import java.io.File;
import java.io.PrintStream;
import java.net.URLEncoder;

import junit.framework.TestCase;

import org.json.JSONObject;

import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.ActionServlet;
import fr.fg.server.servlet.AjaxServlet;

public class TestAction extends TestCase {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String SUCCESS = "success", ERROR = "error";
	
	public final static String DEFAULT_PLAYER = "JayJay";
	
	// -------------------------------------------------------- ATTRIBUTS -- //

	private static boolean outEnabled = true;
	
	protected String contextPath;
	
//	protected ServletTester tester;
//	
//	protected HttpTester request, response;
	
	protected String player;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	/**
	 * Crée les ressources nécessaires au test unitaire.
	 * 
	 * Si besoin, cette méthode peut être rédefinie dans les classes qui
	 * héritent de {@link TestAction}.
	 */
	public void init() {
		// Redéfini au besoin dans les classes filles
	}
	
	public void setPlayer(String player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return DataAccess.getPlayerByLogin(player);
	}
	
	public JSONObject doRequest(String url, String params) throws Exception {
//		request.setURI(contextPath + "/" + url + ".do");
//		request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//		request.setContent(AjaxServlet.LOGIN_PARAMETER + "=" +
//				URLEncoder.encode(player, "UTF-8") + "&" + params);
//		response.parse(tester.getResponses(request.generate()));
//		assertEquals(200, response.getStatus());
//		
//		return new JSONObject(response.getContent());
		return null;
	}

	/**
	 * Supprime les ressources crées dans {@link #init} pour les besoins du
	 * test unitaire.
	 * 
	 * Si besoin, cette méthode peut être rédefinie dans les classes qui
	 * héritent de {@link TestAction}.
	 */
	public void cleanUp() {
		// Redéfini au besoin dans les classes filles
	}
	
	/**
	 * Active ou désactive la sortie standard sur la console. Ceci permet de
	 * masquer la sortie console des appels à <code>System.out.println</code>.
	 * 
	 * @param outEnabled <code>false</code> si la sortie console doit être
	 * désactivée.
	 */
	public static void setOutEnabled(boolean outEnabled) {
		TestAction.outEnabled = outEnabled;
		
		if (!outEnabled) {
			try {
				System.setOut(new PrintStream(File.createTempFile("trash", null)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// REMIND jgottero réactiver la sortie console
		}
	}
	
	// Indique si la sortie console est active
	public static boolean isOutEnabled() {
		return outEnabled;
	}
	
	// Ne pas rédéfinir ; utiliser init
	public void setUp() throws Exception {
		// Démarre le serveur de test
//		tester = new ServletTester();
//		contextPath = "/FallenGalaxy";
//		tester.setContextPath(contextPath);
//		tester.addServlet(ActionServlet.class, "*.do");
//		tester.start();
//		
//		request = new HttpTester();
//		request.setVersion("HTTP/1.0");
//		request.setMethod("POST");
//		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
//		response = new HttpTester();
		
		player = DEFAULT_PLAYER;
		
		init();
	}
	
	// Ne pas rédéfinir ; utiliser cleanUp
	public void tearDown() throws Exception {
//		tester.stop();
		
		cleanUp();
		
		DataAccess.flush();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
