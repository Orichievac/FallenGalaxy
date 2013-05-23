/**
 * @file AllopassRegion.java
 * File of the class AllopassRegion
 */

package apikit.model;

import java.util.ArrayList;
import org.w3c.dom.Node;

/**
 * Class providing object mapping of a region item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassRegion {
    /**
     * (XmlNode) XmlNode object representation of the item
     */
    private Node xml;

    /**
     * Constructor
     *
     * @param xml (XmlNode) The XmlNode object representation of the item
     */
    public AllopassRegion(Node xml) {
        this.xml = xml;
    }

    /**
     * Method retrieving the region name
     *
     * @return (string) The region name
     */
    public String getName() {
        return this.xml.getAttributes().getNamedItem("name").getTextContent();
    }

    /**
     * Method retrieving the region's countries
     *
     * @return (AllopassCountry[]) The region's countries
     */
    public ArrayList getCountries() {
        ArrayList countries = new ArrayList();

        for ( int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            if (this.xml.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                countries.add(new AllopassCountry(this.xml.getChildNodes().item(i)));
            }
        }

        return countries;
    }
}