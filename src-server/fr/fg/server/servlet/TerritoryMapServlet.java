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

package fr.fg.server.servlet;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.fg.server.core.TerritoryManager;
import fr.fg.server.data.DataAccess;
import fr.fg.server.util.ImageCache;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class TerritoryMapServlet extends BaseServlet {
	// ------------------------------------------------------- CONSTANTES -- //

	private static final long serialVersionUID = 3655214806306489840L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
    	doGet(request, response);
    }
    
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String sectorData = request.getRequestURI();
		
		if (sectorData.endsWith(".xml"))
			sectorData = sectorData.substring(0, sectorData.length() - 4);
		
		try {
			// Décode les entités (ex : %20)
			String encoding = request.getCharacterEncoding();
			sectorData = URLDecoder.decode(
				sectorData.substring(sectorData.lastIndexOf("/") + 1),
				encoding == null ? "ISO-8859-1" : encoding);
		} catch (UnsupportedEncodingException e) {
			LoggingSystem.getServerLogger().warn("Unsupported encoding.", e);
		}
		
		if (sectorData.length() == 0 || !sectorData.contains("-")) {
			write(request, response, "Syntaxe invalide.");
			return;
		}
		
		// Parse l'intervalle du classement demandé
		String[] splittedData = sectorData.split("-");
		
		if (splittedData.length != 2) {
			write(request, response, "Syntaxe invalide.");
			return;
		}
		
		int idSector;
		
		try {
			idSector = Integer.parseInt(splittedData[0]);
		} catch (Exception e) {
			write(request, response, "Syntaxe invalide.");
			return;
		}
		
		if (DataAccess.getSectorById(idSector) == null) {
			write(request, response, "Secteur invalide.");
			return;
		}
		
		// Cache de 1 jour
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Utilities.now() * 1000);
		calendar.set(
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
		
		response.setHeader("Cache-Control", "public, max-age=" + (24 * 3600));
		response.setHeader("Expires", calendar.getTime().toString());
		
		try {
			ImageCache imageCache = TerritoryManager.getInstance().getTerritoryMap(idSector);
			
			response.setContentType("image/png");
			response.setContentLength(imageCache.getImageSize());
			
			OutputStream os = response.getOutputStream();
			ImageIO.write(imageCache.getRawImage(), "png", os);
			os.close();
		} catch (Exception e) {
			// Ignoré (du en général à une coupure de la connexion du client)
			LoggingSystem.getServerLogger().warn("e", e);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
