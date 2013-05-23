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

package fr.fg.client.openjwt;

import java.util.HashMap;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class XmlStyle {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private HashMap<String, HashMap<String, String>> components;
	
	private boolean ready;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public XmlStyle() {
		ready = false;
		components = new HashMap<String, HashMap<String,String>>();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public boolean isReady() {
		return ready;
	}
	
	public String getProperty(String component, String key) {
		HashMap<String, String> properties = components.get(component);
		
		if (properties == null)
			return null;
		
		return properties.get(key);
	}
	
	public void setProperty(String component, String key, String value) {
		getProperties(component).put(key, value);
	}
	
	public static XmlStyle load(String url) {
		final XmlStyle style = new XmlStyle();
		loadXmlStyle(style, url);
		return style;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	private HashMap<String, String> getProperties(String component) {
		HashMap<String, String> properties = components.get(component);
		
		if (properties == null) {
			properties = new HashMap<String, String>();
			components.put(component, properties);
		}
		
		return properties;
	}
	
	private static native void loadXmlStyle(XmlStyle style, String url) /*-{
		var req = false;
		
		function parseStyle() {
    		try {
				xmlDoc = req.responseXML;
				var components = xmlDoc.getElementsByTagName("component");
				
				for (var i = 0; i < components.length; i++) {
					var type = components[i].getAttribute('type');
					for (var j = 0; j < components[i].childNodes.length; j++) {
						if (components[i].childNodes[j].nodeType != 1)
							continue;
						
						var keyNode = components[i].childNodes[j].getElementsByTagName("key");
						var valueNode = components[i].childNodes[j].getElementsByTagName("value");
						var key = keyNode[0].firstChild.nodeValue;
						var value = valueNode[0].firstChild.nodeValue;
						
						style.@fr.fg.client.openjwt.XmlStyle::setProperty(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(type, key, value);
					}
				}
        	} catch (e) {
        		// Ignoré
	        }
				
			style.@fr.fg.client.openjwt.XmlStyle::ready = true;
		}
		
    	function processReqChange() {
		    // only if req shows "loaded"
		    if (req.readyState == 4) {
		        // only if "OK"
		        if (req.status == 200) {
		            parseStyle();
		        } else {
		            alert("There was a problem retrieving the XML data:\n" +
		                req.statusText);
		        }
		    }
		}
		
		if (window.XMLHttpRequest && !(window.ActiveXObject)) {
			// branch for native XMLHttpRequest object
    		try {
				req = new XMLHttpRequest();
        	} catch (e) {
				req = false;
	        }
    	} else if(window.ActiveXObject) {
    		// branch for IE/Windows ActiveX version
       		try {
        		req = new ActiveXObject("Msxml2.XMLHTTP");
      		} catch (e) {
        		try {
          			req = new ActiveXObject("Microsoft.XMLHTTP");
        		} catch(e) {
          			req = false;
        		}
			}
    	}
    	
		if (req) {
			req.onreadystatechange = processReqChange;
			req.open("GET", url, true);
			req.send("");
		}
	}-*/;
}
