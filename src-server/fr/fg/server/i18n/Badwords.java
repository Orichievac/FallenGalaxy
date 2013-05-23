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

package fr.fg.server.i18n;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;

public class Badwords {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static List<Pattern> patterns;
	private static List<String> replacements;
	private static List<String> goodwords;
	
	static {
		patterns = Collections.synchronizedList(new ArrayList<Pattern>());
		replacements = Collections.synchronizedList(new ArrayList<String>());
		goodwords = Collections.synchronizedList(new ArrayList<String>());
		
		String alpha = "-\\p{L}";
		String alphas = "[" + alpha + "]*",
			notAlpha  = "([^" + alpha + "])";
		
		String lang = Config.getServerLang();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					Badwords.class.getResource(
							"badwords_" + lang + ".txt").getFile())));
			
			String line;
			int lineNumber = 1;
			int flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
			
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, "\t");
				if (tokenizer.countTokens() != 2) {
					LoggingSystem.getServerLogger().warn("Invalid badword on " +
							"line " + lineNumber + ": '" + line + "'.");
				} else {
					try {
						String badword = tokenizer.nextToken();
						String type = tokenizer.nextToken();
						
						StringBuffer buffer = new StringBuffer();
						char[] chars = new char[badword.length()];
						badword.getChars(0, badword.length(), chars, 0);
						for (int i = 0; i < chars.length; i++) {
							String c = String.valueOf(chars[i]);
							
							// Caractères pour forcer deux caractères à se
							// suivre et pour ignorer les variantes d'une
							// lettre
							if (c.equals("=") || c.equals("\""))
								continue;
							
							boolean variation = true;
							if (i < chars.length - 1 && chars[i + 1] == '"')
								variation = false;
							
							if (variation) {
								if (c.equalsIgnoreCase("e"))
									c = "eéèêë3€";
								else if (c.equalsIgnoreCase("a"))
									c = "aàáâä4@";
								else if (c.equalsIgnoreCase("c"))
									c = "cç©k";
								else if (c.equalsIgnoreCase("k"))
									c = "kcç©";
								else if (c.equalsIgnoreCase("i"))
									c = "iîï1yÿ";
								else if (c.equalsIgnoreCase("y"))
									c = "yÿiîï1";
								else if (c.equalsIgnoreCase("o"))
									c = "oô0òöø©☼";
								else if (c.equalsIgnoreCase("u"))
									c = "uûüùv";
								else if (c.equalsIgnoreCase("s"))
									c = "s5";
								else if (c.equalsIgnoreCase("f"))
									c = "f7";
								else if (c.equalsIgnoreCase("t"))
									c = "t7";
							} else {
								c = c.toLowerCase();
							}
							
							boolean linked = false;
							if (i < chars.length - 1 && chars[i + 1] == '=')
								linked = true;
							
							if (i != 0 && chars[i - 1] != '=')
								buffer.append("[\\p{Punct} ]*");
							
							buffer.append("[" + c + "]");
							
							if (variation) {
								if (i == chars.length - 1 || linked)
									buffer.append("[" + c + "]*");
								else
									buffer.append("[\\p{Punct} " + c + "]*");
							}
						}
						badword = buffer.toString();
						
						if (type.equals("START")) {
							// Mot en milieu de phrase
							patterns.add(Pattern.compile(notAlpha +
									"(" + badword + alphas + ")", flags));
							replacements.add("$1***");
							
							// Mot en début de phrase
							patterns.add(Pattern.compile("^" +
									"(" + badword + alphas + ")", flags));
							replacements.add("***");
						} else if (type.equals("END")) {
							// Mot en milieu de phrase
							patterns.add(Pattern.compile("(" + alphas +
									badword + ")" + notAlpha, flags));
							replacements.add("***$2");
							
							// Mot en début de fin phrase
							patterns.add(Pattern.compile(
									"(" + alphas + badword + ")$", flags));
							replacements.add("***");
						} else if (type.equals("SINGLE")) {
							// Mot en milieu de phrase
							patterns.add(Pattern.compile(notAlpha +
									"(" + badword + ")" + notAlpha, flags));
							replacements.add("$1***$3");
							
							// Mot en début de fin phrase
							patterns.add(Pattern.compile(notAlpha +
									"(" + badword + ")$", flags));
							replacements.add("$1***");
							
							// Mot en début de phrase
							patterns.add(Pattern.compile("^" +
									"(" + badword + ")" + notAlpha, flags));
							replacements.add("***$2");
							
							// Mot unique
							patterns.add(Pattern.compile("^" +
									"(" + badword + ")$", flags));
							replacements.add("***");
						} else {
							LoggingSystem.getServerLogger().warn("Invalid badword " +
								"type on line " + lineNumber + ": '" + line +
								"'. Must be START, END or SINGLE.");
						}
					} catch (Exception e) {
						LoggingSystem.getServerLogger().warn("Could not load " +
								"badword on line " + lineNumber + ": '" +
								line + "'.");
					}
				}
				
				lineNumber++;
			}
			
			reader.close();
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error("Could not load badwords.", e);
		}
		
		goodwords.add("@¤%#");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					Badwords.class.getResource(
							"goodwords_" + lang + ".txt").getFile())));
			
			String line;
			while ((line = reader.readLine()) != null)
				goodwords.add(line);
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error("Could not load goodwords.", e);
		}
	}
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static boolean containsBadwords(String line) {
		for (int i = 0; i < patterns.size(); i++) {
			Matcher m = patterns.get(i).matcher(line);
			
			if (m.find())
				return true;
		}
		return false;
	}
	
	public static String parse(String line) {
		for (int i = 0; i < patterns.size(); i++) {
			Matcher m = patterns.get(i).matcher(line);
			
			line = m.replaceAll(replacements.get(i).replace("***",
					goodwords.get((int) (Math.random() * goodwords.size()))));
		}
		
		// REMIND jgottero faire le parsing en une passe
		for (int i = 0; i < patterns.size(); i++) {
			Matcher m = patterns.get(i).matcher(line);
			line = m.replaceAll(replacements.get(i).replace("***",
					goodwords.get((int) (Math.random() * goodwords.size()))));
		}
		
		return line;
	}
	
	public static void main(String[] args) {
//		System.out.println(Badwords.containsBadwords("con"));
//		System.out.println(Badwords.containsBadwords("con con con con con con"));
//		System.out.println(Badwords.containsBadwords("m.e.er.d.e"));
//		System.out.println(Badwords.containsBadwords("bité bites conardos conards conardos conard conard conard"));
//		
//		System.out.println(Badwords.containsBadwords("petit connard des mes deux"));
//		System.out.println(Badwords.containsBadwords("oh le con"));
//		System.out.println(Badwords.containsBadwords("oh la connaissance"));
//		System.out.println(Badwords.containsBadwords("con de joueur"));
//		
//		System.out.println(Badwords.containsBadwords("couille sd "));
//		System.out.println(Badwords.containsBadwords("mes COUILLES"));
//		System.out.println(Badwords.containsBadwords("mes couilles dksl"));
//		System.out.println(Badwords.containsBadwords("mes ssemmerde dksl"));
//
//		System.out.println(Badwords.containsBadwords("tin"));
//		System.out.println(Badwords.containsBadwords("t'intéresse"));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
