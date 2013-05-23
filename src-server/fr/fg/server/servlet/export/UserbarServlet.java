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

package fr.fg.server.servlet.export;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.fg.server.core.Ladder;
import fr.fg.server.data.Ally;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Player;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.BaseServlet;
import fr.fg.server.util.Config;
import fr.fg.server.util.ImageCache;
import fr.fg.server.util.LoggingSystem;

@SuppressWarnings("serial")
public class UserbarServlet extends BaseServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// Images d'arrière-plan des userbars et de médailles pour les joueurs
	// et les alliances dans le top 25
	private static BufferedImage background, medals;
	
	// Police pour le texte et les points des userbars
	private static Font textFont, pointsFont;
	
	// Cache pour les userbars
	private static Map<String, ImageCache> playerCache, allyCache;
	
	static {
		playerCache = Collections.synchronizedMap(
				new HashMap<String, ImageCache>());
		allyCache = Collections.synchronizedMap(
				new HashMap<String, ImageCache>());
		
		// Charge l'arrière-plan des userbars
		try {
			background = ImageIO.read(Action.class.getClassLoader(
					).getResource("fr/fg/server/resources/background_" +
							Config.getServerName().toLowerCase() + ".png"));
		} catch (Exception e) {
			LoggingSystem.getServerLogger().warn(
					"Could not load userbars background.", e);
		}
		
		// Charge les médailles pour le top 25
		try {
			medals = ImageIO.read(Action.class.getClassLoader(
					).getResource("fr/fg/server/resources/medals.gif"));
		} catch (Exception e) {
			LoggingSystem.getServerLogger().warn(
					"Could not load userbars medals.", e);
		}
		
		// Charge la police pour le texte des userbars
		try {
			textFont = Font.createFont(Font.TRUETYPE_FONT,
					Action.class.getClassLoader().getResourceAsStream(
							"fr/fg/server/resources/prototype.ttf"));
			textFont = textFont.deriveFont(12f).deriveFont(Font.PLAIN);
			pointsFont = textFont.deriveFont(10f);
		} catch (Exception e) {
			LoggingSystem.getServerLogger().warn(
					"Could not load userbars font.", e);
		}
	}
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
    
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String name = request.getRequestURI();
		
		if (name.endsWith(".png"))
			name = name.substring(0, name.length() - 4);
		
		try {
			// Décode les entités (ex : %20)
			String encoding = request.getCharacterEncoding();
			name = URLDecoder.decode(
					name.substring(name.lastIndexOf("/") + 1),
					encoding == null ? "ISO-8859-1" : encoding);
		} catch (UnsupportedEncodingException e) {
			LoggingSystem.getServerLogger().warn("Unsupported encoding.", e);
		}
		
		if (name.length() == 0) {
			write(request, response, "Syntaxe invalide. Utiliser l'URL : " +
					Config.getServerURL() +
					(request.getServletPath().contains("player") ?
							"player/nom joueur" :
							"ally/nom alliance"));
		} else {
			ImageCache userbar;
			
			if (request.getServletPath().contains("player")) {
				userbar = getPlayerUserbar(name);
				
				if (userbar == null)
					write(request, response,
							"Nom de joueur invalide ou compte non validé.");
			} else {
				userbar = getAllyUserbar(name);
				
				if (userbar == null)
					write(request, response, "Nom d'alliance invalide.");
			}
			
			if (userbar != null) {
				try {
					response.setContentType("image/png");
					response.setContentLength(userbar.getImageSize());
					
					OutputStream os = response.getOutputStream();
					ImageIO.write(userbar.getRawImage(), "png", os);
					os.close();
				} catch (Exception e) {
					// Ignoré (du en général à une coupure de la connexion du
					// client)
				}
			}
		}
	}
	
	public static void clearCache() {
		playerCache.clear();
		allyCache.clear();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private ImageCache getPlayerUserbar(String name) {
		synchronized (playerCache) {
			ImageCache userbar = playerCache.get(name);
			
			if (userbar == null) {
				Player player = DataAccess.getPlayerByLogin(name);
				
				if (player != null && player.isRegistered()) {
					userbar = new ImageCache(createPlayerUserbar(player));
					playerCache.put(name, userbar);
				}
			}
			
			return userbar;
		}
	}

	private ImageCache getAllyUserbar(String name) {
		synchronized (allyCache) {
			ImageCache userbar = allyCache.get(name);
			
			if (userbar == null) {
				Ally ally = DataAccess.getAllyByName(name);
				
				if (ally != null) {
					userbar = new ImageCache(createAllyUserbar(ally));
					allyCache.put(name, userbar);
				}
			}
			
			return userbar;
		}
	}
	
	private BufferedImage createPlayerUserbar(Player player) {
		int rank = Ladder.getInstance().getPlayerRank(player.getId());
		String data = rank + ". " + player.getLogin();
		if (player.getIdAlly() != 0)
			data += " - " + player.getAllyName() + " ";
		
		return createUserbar(data, rank, player.getPoints());
	}
	
	private BufferedImage createAllyUserbar(Ally ally) {
		int rank = Ladder.getInstance().getAllyRank(ally.getId());
		
		return createUserbar(
				rank + ". " + ally.getName(), rank, ally.getPoints());
	}
	
	private BufferedImage createUserbar(String text, int rank, long points) {
		// Crée l'image
		BufferedImage image =
			new BufferedImage(350, 20, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.drawImage(background, 0, 0, null);
		
		// Classement, nom du joueur et de son alliance
		
		FontMetrics metrics = graphics.getFontMetrics();
		int width = metrics.stringWidth(text);
		
		// Dégradé gris -> blanc -> gris
		graphics.setPaint(new GradientPaint(12, 0, Color.LIGHT_GRAY,
				width / 2 + 22, 0, Color.WHITE, true));
		graphics.setFont(textFont);
		graphics.drawString(text, 22, 14);
		
		// Médailles pour le top 25
		if (rank <= 3)
			graphics.drawImage(medals, 3, 2, 18, 17, 0, 0, 15, 15, null);
		else if (rank <= 10)
			graphics.drawImage(medals, 3, 2, 18, 17, 16, 0, 31, 15, null);
		else if (rank <= 25)
			graphics.drawImage(medals, 3, 2, 18, 17, 32, 0, 47, 15, null);
		
		// Points
		String pointsString = String.format("%,d pts", points);
		
		metrics = graphics.getFontMetrics();
		graphics.setFont(pointsFont);
		graphics.setColor(Color.WHITE);
		graphics.drawString(pointsString,
				300 - metrics.stringWidth(pointsString), 14);
		
		return image;
	}
}
