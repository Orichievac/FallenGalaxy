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

package fr.fg.server.test.codegen;

import java.util.Scanner;

import fr.fg.server.dao.codegen.JavaCodeGen;
import fr.fg.server.util.Config;

public class DataGenerator {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starting code generation");
		
		Scanner scanner = new Scanner(DataGenerator.class.getResourceAsStream(
			"header.txt")).useDelimiter("\\Z");
		String header = scanner.next();
		scanner.close();
		
		JavaCodeGen.getInstance().generate(Config.getDbMapping(),
			"src-server", "base", "Base", header);
		
		System.out.println("Code generation finished");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
