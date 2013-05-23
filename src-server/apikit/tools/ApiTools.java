/**
 * @file ApiTools.java
 * File providing convenient tool methods
 */

package apikit.tools;

import apikit.exception.ApiMissingHashFeatureException;
import apikit.exception.ApiMissingXMLFeatureException;

import java.io.IOException;
import java.io.StringReader;

import java.net.URLEncoder;

import java.security.MessageDigest;

import java.util.Iterator;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

import org.w3c.dom.Document;

/**
 * Class providing convenient tools
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class ApiTools {
    /**
     * Load an XML string into a mapping object
     *
     * @param xmlStr (String) The XML string to be parsed
     *
     * @return (Document) The mapping object
     */
    public static Document xmlParseString(String xmlStr) throws ApiMissingXMLFeatureException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db         = dbf.newDocumentBuilder();

            return db.parse(new InputSource(new StringReader(xmlStr)));
        }
        catch (Exception e) {
            throw new ApiMissingXMLFeatureException();
        }
    }
    
    /**
     * Hashs data based on an encryption cipher
     *
     * @param data (String) The data to be hashed
     * @param cipher (String) The encryption cipher to use
     *
     * @throws ApiMissingHashFeatureException If configured cipher API isn't loaded/supported
     *
     * @return (String) The hashed data
     */
    public static String toHash(String data, String cipher) throws ApiMissingHashFeatureException {
        cipher = cipher.toUpperCase();

        if (!cipher.equals("MD5") && !cipher.equals("SHA1")) {
            throw new ApiMissingHashFeatureException();
        }
        
        try {
            byte[] uniqueKey = data.getBytes("UTF-8");
            byte[] hash = null;

            hash = MessageDigest.getInstance(cipher).digest(uniqueKey);

            StringBuffer hashString = new StringBuffer();
            for ( int i = 0; i < hash.length; ++i ) {
               String hex = Integer.toHexString(hash[i]);
               if ( hex.length() == 1 ) {
                   hashString.append('0');
                   hashString.append(hex.charAt(hex.length()-1));
               } else {
                   hashString.append(hex.substring(hex.length()-2));
               }
            }
            return hashString.toString();
        }
        catch (Exception e) {}
        
        return null;
    }

    /**
     * Transform a TreeMap of parameters into a valid HTTP query string
     *
     * @param parameters (TreeMap) The array of parameters
     * @return (String) The query string
     * @throws IOException If UTF-8 encoding fails
     */
    public static String httpBuildQuery(TreeMap parameters) throws IOException {
        String str = "";

        if (!parameters.equals(null)) {
            for (Iterator i = parameters.keySet().iterator() ; i.hasNext() ; ){
                String key  = String.valueOf(i.next());

                if (parameters.get(key).getClass().getName().equals("java.util.TreeMap")) {
                    TreeMap al = (TreeMap) parameters.get(key);
                        
                    for (Iterator j = al.keySet().iterator() ; j.hasNext() ; ) {
                        String sKey = String.valueOf(j.next());

                        str += URLEncoder.encode(key, "UTF-8") 
                            + "[" + URLEncoder.encode(sKey, "UTF-8") + "]="
                            +  URLEncoder.encode(al.get(sKey).toString(), "UTF-8")
                            + (j.hasNext() ? "&" : "");
                    }
                }
                else {
                    String value = String.valueOf(parameters.get(key));
                    str += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");                    
                }
                str += i.hasNext() ? "&" : "";
            }
        }
        return str;
    }
}