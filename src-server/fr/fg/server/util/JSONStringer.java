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

package fr.fg.server.util;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

public class JSONStringer {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private StringWriter writer;
	
	private JsonGenerator json;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSONStringer() {
		this.writer = new StringWriter();
		
		try {
			this.json = new JsonFactory().createJsonGenerator(writer);
		} catch (IOException e) {
			LoggingSystem.getServerLogger().warn(
					"Could not initialize json generator.", e);
		}
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public JSONStringer object() {
		try {
			json.writeStartObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public JSONStringer array() {
		try {
			json.writeStartArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public JSONStringer endObject() {
		try {
			json.writeEndObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public JSONStringer endArray() {
		try {
			json.writeEndArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public JSONStringer key(String key) {
		try {
			json.writeFieldName(key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public JSONStringer value(String value) {
		try {
			json.writeString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public JSONStringer value(int value) {
		try {
			json.writeNumber(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public JSONStringer value(boolean value) {
		try {
			json.writeBoolean(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public JSONStringer value(double value) {
		try {
			json.writeNumber(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public String toString() {
		try {
			json.flush();
		} catch (IOException e) {
			// Ignoré
		}
		return writer.toString();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
