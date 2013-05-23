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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.fg.server.util.Config;
import fr.fg.server.util.ImageCache;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class LineOfSightServlet extends HttpServlet {
	// ------------------------------------------------------- CONSTANTES -- //

	private static final long serialVersionUID = 3655214806306489840L;
	
	private static final Map<String, Color> COLORS;
	
	static {
		COLORS = new HashMap<String, Color>();
		COLORS.put("ally", new Color(0x2c2125));
		COLORS.put("player", new Color(0x453a3e));
		COLORS.put("over", new Color(0x6a5f63));
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private Map<Integer, ImageCache> allyImagesCache,
		playerImagesCache, overImagesCache;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public LineOfSightServlet() {
		allyImagesCache = Collections.synchronizedMap(
				new HashMap<Integer, ImageCache>());
		playerImagesCache = Collections.synchronizedMap(
				new HashMap<Integer, ImageCache>());
		overImagesCache = Collections.synchronizedMap(
				new HashMap<Integer, ImageCache>());
	}
	
	// --------------------------------------------------------- METHODES -- //

	public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
    	doGet(request, response);
    }
    
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String uri = request.getRequestURI();
		
		if (uri.endsWith(".png"))
			uri = uri.substring(0, uri.length() - 4);
		
		try {
			// Décode les entités (ex : %20)
			String encoding = request.getCharacterEncoding();
			uri = URLDecoder.decode(
				uri.substring(uri.lastIndexOf("/") + 1),
				encoding == null ? "ISO-8859-1" : encoding);
		} catch (UnsupportedEncodingException e) {
			LoggingSystem.getServerLogger().warn("Unsupported encoding.", e);
		}
		
		if (uri.length() == 0 || !uri.contains("-")) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		// Parse les dimensions demandées
		String[] data = uri.split("-");
		
		if (data.length != 3) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		int width, height;
		String type;
		
		try {
			width = Integer.parseInt(data[0]);
		} catch (Exception e) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		try {
			height = Integer.parseInt(data[1]);
		} catch (Exception e) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		type = data[2];
		if (!type.equals("ally") && !type.equals("player") && !type.equals("over")) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		if (width < 1 || height < 1 || width > 2500 || height > 2500) {
			writeInvalidSyntax(request, response);
			return;
		}
		
		// Cache de 1 an
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Utilities.now() * 1000);
		calendar.set(
				calendar.get(Calendar.YEAR) + 1,
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		
		response.setHeader("Cache-Control", "public, max-age=" + 31536000);
		response.setHeader("Expires", calendar.getTime().toString());
		
		try {
			ImageCache imageCache = getLineOfSightImage(width, height, type);
			
			response.setContentType("image/png");
			response.setContentLength(imageCache.getImageSize());
			
			OutputStream os = response.getOutputStream();
			ImageIO.write(imageCache.getRawImage(), "png", os);
			os.close();
		} catch (Exception e) {
			// Ignoré (du en général à une coupure de la connexion du client)
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void writeInvalidSyntax(HttpServletRequest request,
			HttpServletResponse response) {
		write(request, response, "Syntaxe invalide. Utiliser l'URL : " +
			Config.getServerURL() + "los/largeur-hauteur-type.png");
	}
	
	public void write(HttpServletRequest request,
			HttpServletResponse response, String text) {
		try {
			response.setContentLength(text.getBytes("UTF-8").length);
			
			PrintWriter out = response.getWriter();
			out.print(text);
			out.flush();
		} catch (Exception e) {
			LoggingSystem.getServerLogger().warn(
					"An exception occured while handling URI '" +
					request.getServletPath() + "'.", e);
		}
	}
	
	private ImageCache getLineOfSightImage(int width, int height, String type) {
		Map<Integer, ImageCache> cache;
		
		if (type.equals("ally"))
			cache = allyImagesCache;
		else if (type.equals("player"))
			cache = playerImagesCache;
		else if (type.equals("over"))
			cache = overImagesCache;
		else
			throw new IllegalArgumentException("Invalid type: " + type);
		
		ImageCache image;
		
		synchronized (cache) {
			int key = width * 1000 + height;
			image = cache.get(key);
			
			if (image == null) {
				image = new ImageCache(createLineOfSightImage(width, height, type));
				cache.put(key, image);
			}
		}
		
		return image;
	}
	
	private BufferedImage createLineOfSightImage(int width, int height, String type) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics.setColor(COLORS.get(type));
		graphics.fillOval(0, 0, width, height);
		
		return image;
	}
}
