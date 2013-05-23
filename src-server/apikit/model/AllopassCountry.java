/**
 * @file AllopassCountry.java
 * File of the class AllopassCountry
 */

package apikit.model;

import org.w3c.dom.Node;

/**
 * Class providing object mapping of a country item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassCountry {
    /**
     * (XmlNode) XmlNode object representation of the item
     */
    private Node xml;

    /**
     * Constructor
     *
     * @param xml (XmlNode) The XmlNode object representation of the item
     */
    public AllopassCountry(Node xml) {
        this.xml = xml;
    }

    /**
     * Method retrieving the country code
     *
     * @return (string) The country code
     */
    public String getCode() {
        return this.xml.getAttributes().getNamedItem("code").getTextContent();
    }

    /**
     * Method retrieving the country name
     *
     * @return (string) The country name
     */
    public String getName() {
        return this.xml.getAttributes().getNamedItem("name").getTextContent();
    }
}
