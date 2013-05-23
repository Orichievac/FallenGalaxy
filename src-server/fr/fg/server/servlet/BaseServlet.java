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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import fr.fg.server.util.LoggingSystem;

public class BaseServlet extends HttpServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private static final long serialVersionUID = 9137318331189419389L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }
    
	public void write(HttpServletRequest request,
			HttpServletResponse response, String text) {
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/xml");
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
	
	public void writeXML(HttpServletResponse response, Element root) {
		// Sortie en XML
		StringWriter writer = new StringWriter();
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		Document document = new Document(root);
		
		try {
			outputter.output(document, writer);
			
			String result = writer.toString();
			
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/xml");
			response.setContentLength(result.getBytes("UTF-8").length);
			
			PrintWriter out = response.getWriter();
			out.write(result);
			out.flush();
		} catch (IOException e) {
			LoggingSystem.getServerLogger().warn("Could not serialise xml.", e);
			return;
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
