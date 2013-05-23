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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.fg.server.data.Player;

public class ThemeProxyServlet extends AjaxServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private static final long serialVersionUID = 4418641366546625639L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected void process(HttpServletRequest request,
			HttpServletResponse response, int method, Player player) {
		if (request.getParameter("theme") == null ||
				!request.getParameter("theme").startsWith("http://")) {
			write(request, response, "Invalid request.");
			return;
		}
		
		try {
			URL url = new URL(request.getParameter("theme") + "/style.xml");
			URLConnection connection = url.openConnection();
			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			
			response.setHeader("Content-Type", "application/xml");
			if (connection.getHeaderField("Last-Modified") != null)
				response.setHeader("Last-Modified", connection.getHeaderField("Last-Modified"));
			if (connection.getHeaderField("Content-Encoding") != null)
				response.setHeader("Content-Encoding", connection.getHeaderField("Content-Encoding"));
			if (connection.getHeaderField("Content-Length") != null)
				response.setHeader("Content-Length", connection.getHeaderField("Content-Length"));
			if (connection.getHeaderField("Etag") != null)
				response.setHeader("Etag", connection.getHeaderField("Etag"));
			
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			
			byte[] bytes = new byte[2048];
			int length;
			while ((length = in.read(bytes)) != -1)  {
				out.write(bytes, 0, length);
			}
			
			in.close();
			out.flush();
		} catch (Exception e) {
			write(request, response, "Invalid request.");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
