/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.eclipse.jetty.webapp.WebAppContext;

import fr.fg.server.util.Config;

public class Utilities {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static char[] digits = {
		'0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'a', 'b',
		'c', 'd', 'e', 'f', 'g', 'h',
		'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z',
		'A', 'B', 'C', 'D', 'E', 'F',
		'G', 'H', 'I', 'J', 'K', 'L',
		'M', 'N', 'O', 'P', 'Q', 'R',
		'S', 'T', 'U', 'V', 'W', 'X',
		'Y', 'Z', 'À', 'Á', 'Â', 'Ã',
		'Ä', 'Å', 'Æ', 'Ç', 'È', 'É',
		'Ê', 'Ë', 'Ì', 'Í', 'Î', 'Ï',
		'Ð', 'Ñ', 'Ò', 'Ó', 'Ô', 'Õ',
	};
	
	private static Pattern[] accents;
	
	private static String[] replacements;
	
    private static final String HEX_DIGITS = "0123456789abcdef";
    
    private static final Random random = new Random();
    
    public static final int SEC_IN_DAY = 86400;
    
	static {
		accents = new Pattern[]{
			Pattern.compile("[ÀÁÂÃÄÅàáâãäå]"),
			Pattern.compile("[Ææ]"),
			Pattern.compile("[Çç]"),
			Pattern.compile("[Ðđ]"),
			Pattern.compile("[ÈÉÊËèéêë]"),
			Pattern.compile("[ÌÍÎÏìíîï]"),
			Pattern.compile("[Ññ]"),
			Pattern.compile("[Œœ]"),
			Pattern.compile("[ÒÓÔÕÖØðñòóôõöø]"),
			Pattern.compile("[ÙÚÛÜùúûü]"),
			Pattern.compile("[ÝŸýÿ]"),
			Pattern.compile("[ß]"),
		};
		replacements = new String[]{
			"a",
			"ae",
			"c",
			"d",
			"e",
			"i",
			"n",
			"oe",
			"o",
			"u",
			"y",
			"ss",
		};
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
    public static String getCreditsResourceImg() {
    	return "<img class=\"resource credits\" src=\"" +
    		Config.getMediaURL() + "images/misc/blank.gif\"/>";
    }
    
    public static String getResourceImg(int resource) {
    	return "<img class=\"resource r" + resource + "\" src=\"" +
    		Config.getMediaURL() + "images/misc/blank.gif\"/>";
    }
    
    public static String getXpImg() {
    	return "<img class=\"resource xp\" src=\"" +
    		Config.getMediaURL() + "images/misc/blank.gif\"/>";
    }
    
	public static long now() {
		return System.currentTimeMillis() / 1000;
	}
	
	/**
	 * Come now mais calé sur minuit
	 * @return
	 */
	public static long today(){
		long now = now();
		return (now+(SEC_IN_DAY-(now%SEC_IN_DAY)))-1;
	}
	
	//Affiche une date selon le format donné
	public static String formatDate(long time,String format)
	{
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	public static String encryptPassword(String password) {
		return encryptPassword(password, Config.getPasswordEncryption());
	}
	
	public static String encryptPassword(String password, String algorithm) {
		MessageDigest hash = null;
		try {
			hash = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			LoggingSystem.getServerLogger().error("Unknown algorithm.", e);
		}
		
		return toHexString(hash.digest(password.getBytes()));
	}
	
	/**
	 * Retourne un nombre aléatoire avec une distribution de Gauss (loi
	 * normale).
	 *
	 * @param mean Moyenne de la loi normale.
	 * @param stdDev Ecart-type de la loi normale.
	 *
	 * @return Un nombre alétoire suivant la loi N(mean, stdDev).
	 */
	public static double randn(double mean, double stdDev) {
		return stdDev * random.nextGaussian() + mean;
	}
	
	/**
	 * Retourne un nombre pseudo-alétaoire compris entre deux bornes
	 * 
	 * @param lowerBound Borne inférieur
	 * @param upperBound Borne supérieur
	 * @return Un nombre pseudo-aléatoire
	 */
	public static int random(int lowerBound, int upperBound) {
		return (int) (Math.random() * (
				upperBound - lowerBound + 1)) + lowerBound;
	}
	
	/**
	 * Retourne un nombre pseudo-alétaoire compris entre deux bornes (long)
	 * 
	 * @param lowerBound Borne inférieur
	 * @param upperBound Borne supérieur
	 * @return Un nombre pseudo-aléatoire
	 */
	public static long random(long lowerBound, long upperBound) {
		return (long)(Math.random() * (
				upperBound - lowerBound + 1)) + lowerBound;
	}
	
	public static String getDate(long timestamp){
		Date date = new Date(timestamp * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		return sdf.format(date);
	}
	
	public static String getTime(long timestamp){
		Date date = new Date(timestamp * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(date);
	}
	
	public static String getDateTime(long timestamp){
		return Utilities.getDate(timestamp)+" "+Utilities.getTime(timestamp);
	}
	
	public static String escape(String string) {
		return string.replace("\"", "\\\"");
	}
	
	public static Class<?>[] getClasses(String packageName) {
		try {
			return getClassesByFileSystem(packageName);
		} catch (Exception e) {
			// Ignoré
		}
		
		try {
			return getClassesByWar(packageName);
		} catch (Exception e) {
			// Ignoré
		}
		
		return null;
	}
	
	public static String formatString(String string) {
		string = string.toLowerCase();
		for (int i = 0; i < accents.length; i++)
			string = accents[i].matcher(string).replaceAll(replacements[i]);
		return string;
	}
	
	 public static long parseBase(String orig, int intRadix)
		throws NumberFormatException {
	if (orig == null) {
		throw new NumberFormatException("null");
	}
	if (orig.length() == 0) {
		throw new NumberFormatException("For input string: \"" + orig + "\"");
	}

	if (intRadix < Character.MIN_RADIX || intRadix > 84) {
		throw new NumberFormatException("radix " + intRadix
				+ " out of range");
	}

	boolean neg = false;
	String s;
	if (orig.charAt(0) == '-') {
		neg = true;
		s = orig.substring(1);
	} else {
		s = orig;
	}

	long result = 0;

	// Cache a converted version for performance (pure long ops are
	// faster).
	long radix = intRadix;
	for (int i = 0, len = s.length(); i < len; ++i) {
		if (result < 0) {
			throw new NumberFormatException("For input string: \"" + s + "\"");
		}
		result *= radix;
		char c = s.charAt(i);
		int value = digit(c, intRadix);
		if (value < 0) {
			throw new NumberFormatException("For input string: \"" + s + "\"");
		}
		result += value;
	}

	if (result < 0 && result != Long.MIN_VALUE) {
		throw new NumberFormatException("For input string: \"" + s + "\"");
	}
	if (neg) {
		return -result;
	} else {
		return result;
	}
	}
	    
		public static int digit(char c, int radix) {
			if (radix < 0 || radix > digits.length) {
				return -1;
			}
		    
			for (int i = 0; i < digits.length; i++)
				if (c == digits[i])
					return i;
			
			return -1;
		}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static Class<?>[] getClassesByWar(String packageName) {
		String resourceBase = WebAppContext.getCurrentWebAppContext() != null ?
			WebAppContext.getCurrentWebAppContext().getResourceBase() : "";
		
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		packageName = packageName.replaceAll("\\." , "/");
		try {
			URL url = new URL(resourceBase);
			JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
			JarFile jarFile = jarConnection.getJarFile();
			
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			
			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				
				if ((jarEntry.getName().contains(packageName)) &&
						(jarEntry.getName().endsWith(".class"))) {
					classes.add(Class.forName(
						jarEntry.getName().replaceAll("/", "\\.").substring(
							jarEntry.getName().indexOf(packageName),
							jarEntry.getName().length() - 6)));
				}
			}
		} catch( Exception e){
			e.printStackTrace ();
		}
		
		Class<?>[] classesA = new Class<?>[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}
	
	// Liste les classes à l'intérieur d'un package
	private static Class<?>[] getClassesByFileSystem(String packageName)
			throws ClassNotFoundException {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		// Get a File object for the package
		File directory = null;
		try {
			ClassLoader classLoader =
				Thread.currentThread().getContextClassLoader();
			if (classLoader == null)
				throw new ClassNotFoundException("Can't get class loader.");
			
			String path = packageName.replace('.', '/');
			URL resource = classLoader.getResource(path);
			if (resource == null)
				throw new ClassNotFoundException("No resource for " + path);
			directory = new File(resource.getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(packageName + " (" + directory
					+ ") does not appear to be a valid package");
		}
		
		if (directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class") && !files[i].contains("$")) {
					// removes the .class extension
					classes.add(Class.forName(packageName + '.'
							+ files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(packageName
					+ " does not appear to be a valid package");
		}
		
		Class<?>[] classesA = new Class<?>[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}
	
    private static String toHexString(byte[] v) {
        StringBuffer sb = new StringBuffer(v.length * 2);
        for (int i = 0; i < v.length; i++) {
             int b = v[i] & 0xFF;
             sb.append(HEX_DIGITS.charAt(b >>> 4))
               .append(HEX_DIGITS.charAt(b & 0xF));
        }
        return sb.toString();
    }
    
   
}
