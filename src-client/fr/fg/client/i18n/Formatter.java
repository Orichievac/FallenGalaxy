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

package fr.fg.client.i18n;

import com.google.gwt.core.client.GWT;

public class Formatter {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static String formatDate(double timestamp) {
		return formatDate(timestamp, true);
	}
	
	public static String formatDate(double timestamp, boolean showTimeIfDate) {
		String value = "";
		int days  = (int) Math.floor(timestamp / 86400);
		int hour = (int) Math.floor((timestamp - days * 86400) / 3600);
		int min  = (int) Math.floor((timestamp - days * 86400 - hour * 3600) / 60);
		int sec  = (int) Math.ceil(timestamp - days * 86400 - hour * 3600 - min * 60);
		
		if (days > 0) {
			StaticMessages messages = (StaticMessages) GWT.create(StaticMessages.class);
			if (days > 1)
				value = messages.days(days) + "&nbsp;";
			else
				value = messages.day(days) + "&nbsp;";
			
			if (!showTimeIfDate)
				return value;
		}
		
		value +=
			(hour < 10 ? "0" : "") + hour + ':' +
			(min  < 10 ? "0" : "") + min  + ':' +
			(sec  < 10 ? "0" : "") + sec;
		
		return value;
	}
	
	public static String formatNumber(double number) {
		return formatNumber(number, false);
	}
	
	public static String formatNumber(double number, boolean useSIPrefix) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		if (useSIPrefix) {
			String value;
			if (number > 9999999)
				value = splitNumber((int) Math.floor(number / 1000000)) + "&nbsp;" + messages.unitM();
			else if (number > 9999)
				value = splitNumber((int) Math.floor(number / 1000)) + "&nbsp;" + messages.unitK();
			else
				value = splitNumber(number);
			return value;
		} else {
			return splitNumber(number);
		}
	}
	
	public static String formatTime(int timestamp) {
        return formatTime(timestamp, true);
}

public static String formatTime(int timestamp, boolean showTimeIfDate) {
        String value = "";
        int days  = (int) Math.floor(timestamp / 86400);
        int hours = (int) Math.floor((timestamp - days * 86400) / 3600);
        int mins  = (int) Math.floor((timestamp - days * 86400 - hours * 3600) / 60);
       
        if (days > 0) {
                StaticMessages messages = (StaticMessages) GWT.create(StaticMessages.class);
                if (days > 1)
                        value = messages.days(days);
                else
                        value = messages.day(days);
               
                if (!showTimeIfDate)
                        return value;
               
                if (hours > 0 || mins > 0)
                        value += "&nbsp;";
        }
       
        if (hours == 0) {
                if (mins != 0)
                        value += mins + " min";
        } else {
                value += hours + "h" + (mins > 0 ? (mins > 9 ? mins : "0" + mins) : "");
        }
       
        return value;
}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	public static String splitNumber(double number) {
		String value = String.valueOf((long) Math.floor(number));
		int ws = (int) Math.floor((value.length() - 1) / 3);
		int offset = value.length() - ws * 3;
		for (int i = ws; i > 0; i--)
			value = value.substring(0, (i - 1) * 3 + offset) + "&nbsp;" +
				value.substring((i - 1) * 3 + offset);
		return value;
	}
}
