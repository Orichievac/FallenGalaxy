/*
Copyright 2012 jgottero

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

package fr.fg.server.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


public class ImageCache {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private BufferedImage rawImage;
	
	private int imageSize;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ImageCache(BufferedImage rawImage) {
		this.rawImage = rawImage;
	    ByteArrayOutputStream tmp = new ByteArrayOutputStream();
	    
	    try {
		    ImageIO.write(rawImage, "png", tmp);
		    tmp.close();
		    this.imageSize = tmp.size();
	    } catch (IOException e) {
	    	LoggingSystem.getServerLogger().warn("Cannot calculate image size.", e);
	    }
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public BufferedImage getRawImage() {
		return rawImage;
	}
	
	public int getImageSize() {
		return imageSize;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}