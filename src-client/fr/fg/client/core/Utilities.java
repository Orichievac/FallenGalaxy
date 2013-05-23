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

package fr.fg.client.core;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;

import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.core.Config;

public final class Utilities {
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
	
	public final static String
		IMG_HULL = "struct",
		IMG_COOLDOWN = "cooldown";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static void log(Object message) {
		log(String.valueOf(message));
	}
	
	public static void log(boolean message) {
		log(String.valueOf(message));
	}

	public static void log(int message) {
		log(String.valueOf(message));
	}
	
	public static void log(long message) {
		log(String.valueOf(message));
	}

	public static void log(float message) {
		log(String.valueOf(message));
	}

	public static void log(double message) {
		log(String.valueOf(message));
	}
	
	public static void log(String message) {
		GWT.log(message, null);
		
		DOM.getElementById("console").setInnerHTML(
				DOM.getElementById("console").getInnerHTML() + "<br/>" + message);
	}
	
	public static void log(String message, Throwable e) {
		GWT.log(message, e);
		
		DOM.getElementById("console").setInnerHTML(
				DOM.getElementById("console").getInnerHTML() +
				"<br/>" + message);
		DOM.getElementById("console").setInnerHTML(
				DOM.getElementById("console").getInnerHTML() +
				"<br/>" + e.getMessage());
	}
	
	public static long getCurrentTime() {
		return new Date().getTime() / 1000;
	}
	
	public static long parseNumber(String number) {
		if (number.length() == 0)
			return -1;
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		number = number.replace(messages.unitK().toLowerCase(), "000"); //$NON-NLS-1$
		number = number.replace(messages.unitK().toUpperCase(), "000"); //$NON-NLS-1$
		number = number.replace(messages.unitM().toLowerCase(), "000000"); //$NON-NLS-1$
		number = number.replace(messages.unitM().toUpperCase(), "000000"); //$NON-NLS-1$
		number = number.replace(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
		
		long result = -1;
		try {
			result = Long.parseLong(number);
		} catch (Exception e) {
			return -1;
		}
		
		if (result < 0)
			return -1;
		return result;
	}
	
	public static String parseUrlAndSmilies(String html) {
		html = parseUrl(html);
		html = parseSmilies(html);
		
		return html;
	}
	
	public static String parseUrl(String html) {
		html = html.replaceAll("^([fF][tT][pP]|[hH][tT][tT][pP]|[hH][tT][tT][pP][sS])(:\\/\\/[^<\\s\\(\\)\\[\\]]+)",
			"<a href=\"$1$2\" target=\"_blank\">$1$2</a>");
		html = html.replaceAll("(<a[^>]*>|[^\"\\w])([fF][tT][pP]|[hH][tT][tT][pP]|[hH][tT][tT][pP][sS])(:\\/\\/[^<\\s\\(\\)\\[\\]]+)",
			"$1<a href=\"$2$3\" target=\"_blank\">$2$3</a>");
		html = html.replaceAll("^(www\\.[^<\\s\\(\\)\\[\\]]+)",
			"<a href=\"http://$1\" target=\"_blank\">$1</a>");
		html = html.replaceAll("(<a[^>]*>|[^\"\\w/])(www\\.[^<\\s\\(\\)\\[\\]]+)",
			"$1<a href=\"http://$2\" target=\"_blank\">$2</a>");
		html = html.replaceAll("^([-\\w]+\\.)(net|de|fr|co\\.uk|ca|com|org|net|gov|mil|biz|info)(/[^<\\s\\(\\)\\[\\]]*)",
			"<a href=\"http://$1$2$3\" target=\"_blank\">$1$2$3</a>");
		html = html.replaceAll("(<a[^>]*>|[^\\S]|\\()([-\\w]+\\.)(net|de|fr|co\\.uk|ca|com|org|net|gov|mil|biz|info)(/[^<\\s\\(\\)\\[\\]]*)",
			"$1<a href=\"http://$2$3$4\" target=\"_blank\">$2$3$4</a>");
		html = html.replaceAll("^([-\\w]+\\.)(net|de|fr|co\\.uk|ca|com|org|net|gov|mil|biz|info)",
			"<a href=\"http://$1$2\" target=\"_blank\">$1$2</a>");
		html = html.replaceAll("(<a[^>]*>|[^\\S]|\\()([-\\w]+\\.)(net|de|fr|co\\.uk|ca|com|org|net|gov|mil|biz|info)",
			"$1<a href=\"http://$2$3\" target=\"_blank\">$2$3</a>");
		
	    return html;
	}
	
	public static String parseSmilies(String html) {
		ArrayList<String> strings = new ArrayList<String>();
		
		while (html.contains("<")) {
			int index = html.indexOf("<");
			
			if (index > 0) {
				strings.add(html.substring(0, index));
				html = html.substring(index);
			} else {
				if (html.indexOf("<a") == 0) {
					int end = html.indexOf("</a>") + 4;
					strings.add(html.substring(0, end));
					html = html.substring(end);
				} else {
					// FIXME jgottero code mal formaté : balise < mais pas de >
					int end = html.indexOf(">") + 1;
					strings.add(html.substring(0, end));
					html = html.substring(end);
				}
			}
		}
		
		if (html.length() > 0)
			strings.add(html);
		
		ArrayList<String> parsedStrings = new ArrayList<String>();
		
		for (String string : strings) {
			if (!string.startsWith("<")) {
				string = string.replaceAll(":hp:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s30\"/>");
				string = string.replaceAll(":pv:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s30\"/>");
				string = string.replaceAll(":hull:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s30\"/>");
				string = string.replaceAll(":coque:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s30\"/>");
				string = string.replaceAll(":shield:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s29\"/>");
				string = string.replaceAll(":bouclier:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s29\"/>");
				string = string.replaceAll(":protection:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s29\"/>");
				string = string.replaceAll(":s:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s29\"/>");
				string = string.replaceAll(":attack:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s31\"/>");
				string = string.replaceAll(":attaque:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s31\"/>");
				string = string.replaceAll(":damage:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s31\"/>");
				string = string.replaceAll(":d[eé]g[aâ]ts?:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s31\"/>");
				string = string.replaceAll(":cd:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s32\"/>");
				string = string.replaceAll(":cooldown:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s32\"/>");
				string = string.replaceAll(":rechargement:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s32\"/>");
				string = string.replaceAll(":sick:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s20\"/>");
				string = string.replaceAll(":ordinateur:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s14\"/>");
				string = string.replaceAll(":beer:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s11\"/>");
				string = string.replaceAll(":bi[eè]re:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s11\"/>");
				string = string.replaceAll(":-?@",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s12\"/>");
				string = string.replaceAll(":caf[eé]:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s13\"/>");
				string = string.replaceAll(":coffee:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s13\"/>");
				string = string.replaceAll(":ia:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s14\"/>");
				string = string.replaceAll(":ai:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s14\"/>");
				string = string.replaceAll(":computer:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s14\"/>");
				string = string.replaceAll(":t:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s15\"/>");
				string = string.replaceAll(":ti:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s15\"/>");
				string = string.replaceAll(":r1:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s15\"/>");
				string = string.replaceAll(":titane:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s15\"/>");
				string = string.replaceAll(":titanium:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s15\"/>");
				string = string.replaceAll(":r2:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s16\"/>");
				string = string.replaceAll(":cristal:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s16\"/>");
				string = string.replaceAll(":crystal:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s16\"/>");
				string = string.replaceAll(":r3:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s17\"/>");
				string = string.replaceAll(":andium:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s17\"/>");
				string = string.replaceAll(":r4:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s18\"/>");
				string = string.replaceAll(":antimati[eè]re:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s18\"/>");
				string = string.replaceAll(":antimatter:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s18\"/>");
				string = string.replaceAll(":am:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s18\"/>");
				string = string.replaceAll(":research:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s19\"/>");
				string = string.replaceAll(":recherche:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s19\"/>");
				string = string.replaceAll(":ill:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s20\"/>");
				string = string.replaceAll(":malade:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s20\"/>");
				string = string.replaceAll(":cr:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s21\"/>");
				string = string.replaceAll(":credit:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s21\"/>");
				string = string.replaceAll(":credits:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s21\"/>");
				string = string.replaceAll(":p:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s28\"/>");
				string = string.replaceAll(":pow:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s28\"/>");
				string = string.replaceAll(":power:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s28\"/>");
				string = string.replaceAll(":puissance:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s28\"/>");
				string = string.replaceAll(":e:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s33\"/>");
				string = string.replaceAll(":energy:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s33\"/>");
				string = string.replaceAll(":[eé]nergie:",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s33\"/>");
				string = string.replaceAll("[:=]-?\\)",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s0\"/>");
				string = string.replaceAll("X\\)",
						"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s34\"/>");
				string = string.replaceAll(":bave:",
						"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s35\"/>");
				string = string.replaceAll(":fear:",
						"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s36\"/>");
				string = string.replaceAll(":cry:",
						"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s37\"/>");
				string = string.replaceAll(":pleure:",
						"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s37\"/>");
				string = string.replaceAll(":boulet:",
						"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s38\"/>");
				string = string.replaceAll(":sheep:",
						"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s39\"/>");
				string = string.replaceAll("\\+_?\\+",
						"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s40\"/>");
				string = string.replaceAll(":burger:",
						"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s41\"/>");
			
				
				string = string.replaceAll("[:=]-?\\(",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s1\"/>");
				string = string.replaceAll("^8-?\\)",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s2\"/>");
				string = string.replaceAll("([^\\(])8-?\\)", // pour prend en compte Re (8):
					"$1<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s2\"/>");
				string = string.replaceAll("[:=]-?[pP]",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s3\"/>");
				string = string.replaceAll("[:=]-?[oO]",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s4\"/>");
				string = string.replaceAll("^[:=]-?/",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s5\"/>");
				string = string.replaceAll("([^ps])[:=]-?/", // pour prendre en compte les http:// et https://
					"$1<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s5\"/>");
				string = string.replaceAll("[:=]-?[dD]",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s6\"/>");
				string = string.replaceAll("^;-?\\)",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s7\"/>");
				string = string.replaceAll("([^t]);-?\\)",
					"$1<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s7\"/>");
				string = string.replaceAll("[:=]-?[sS]",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s8\"/>");
				string = string.replaceAll("&gt;_?&lt;",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s25\"/>");
				string = string.replaceAll("^[:=]3",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s26\"/>");
				string = string.replaceAll("([^a-zA-Z0-9])[:=]3",
					"$1<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s26\"/>");
				string = string.replaceAll("^[xX][dD]$",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s27\"/>");
				string = string.replaceAll("([^a-zA-Z])[xX][dD]$",
					"$1<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s27\"/>");
				string = string.replaceAll("^[xX][dD]([^a-zA-Z])",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s27\"/>$1");
				string = string.replaceAll("([^a-zA-Z])[xX][dD]([^a-zA-Z])",
					"$1<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s27\"/>$2");
				string = string.replaceAll("[:=]-?\\|",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s9\"/>");
				string = string.replaceAll("&lt;3",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s10\"/>");
				string = string.replaceAll("o_?O",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s22\"/>");
				string = string.replaceAll("O_?o$",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s23\"/>");
				string = string.replaceAll("O_?o([^a-zA-Z])",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s23\"/>$1");
				string = string.replaceAll("\\^_?\\^",
					"<img src=\"" + Config.getMediaUrl() + "images/misc/blank.gif\" class=\"smiley s24\"/>");
				
			}
			
			parsedStrings.add(string);
		}
		
		String result = "";
		
		for (String parsedString : parsedStrings)
			result += parsedString;
		
		return result;
	}
	
	public static String toBaseString(long i, int radix) {
        if (radix < Character.MIN_RADIX || radix > 84)
        	radix = 10;
        if (radix == 10)
        	return Long.toString(i);
        
        char[] buf = new char[65];
        int charPos = 64;
        boolean negative = (i < 0);
        
        if (!negative)
            i = -i;
        
        while (i <= -radix) {
            buf[charPos--] = digits[(int) (-(i % radix))];
            i = i / radix;
        }
        buf[charPos] = digits[(int) (-i)];
        
        if (negative)
            buf[--charPos] = '-';
        
        return new String(buf, charPos, (65 - charPos));
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
	
	public static String getStatImage(String stat) {
		return "<img class=\"stat s-" + stat + "\" src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\"/>";
	}
	
	public static String getResourceImage(int index) {
		return "<img class=\"resource r" + index + "\" src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\"/>";
	}
	
	public static String getCreditsImage() {
		return "<img class=\"resource credits\" src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\"/>";
	}
	
	public static String getClockImage() {
		return "<img class=\"clock\" src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\"/>";
	}
	
	public static String getEnergyImage() {
		return "<img class=\"resource energy\" src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\"/>";
	}
	
	public static String getXpImage() {
		return "<img class=\"resource xp\" src=\"" +
			Config.getMediaUrl() + "images/misc/blank.gif\"/>";
	}
	
	public static String getIdeaImage(){
		return "<img class=\"iconResearch\" src=\"" +
		Config.getMediaUrl() + "/images/misc/blank.gif\"/>";
	}
	
	public static String getOnlineImage() {
        return "<img src=\"" + Config.getMediaUrl() +
                "images/misc/blank.gif\" class=\"online\"/>";
}

public static String getAwayImage() {
        return "<img src=\"" + Config.getMediaUrl() +
                "images/misc/blank.gif\" class=\"away\"/>";
}

public static String getOfflineImage() {
        return "<img src=\"" + Config.getMediaUrl() +
                "images/misc/blank.gif\" class=\"offline\"/>";
}
	
	public static String long2ip(long ip)
	   {
	      byte[] bytes=new byte[4];
	      
	      bytes[0]=(byte) ((ip & 0xff000000)>>24);
	      bytes[1]=(byte) ((ip & 0x00ff0000)>>16);
	      bytes[2]=(byte) ((ip & 0x0000ff00)>>8);
	      bytes[3]=(byte) (ip & 0x000000ff);
	      String result = new String(String.valueOf((int)bytes[0])+"."+String.valueOf((int)bytes[1])+"."+
	    		  String.valueOf((int)bytes[2])+"."+String.valueOf((int)bytes[3]));
	      return result;
		
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	public static int digit(char c, int radix) {
		if (radix < 0 || radix > digits.length) {
			return -1;
		}
	    
		for (int i = 0; i < digits.length; i++)
			if (c == digits[i])
				return i;
		
		return -1;
	}
}