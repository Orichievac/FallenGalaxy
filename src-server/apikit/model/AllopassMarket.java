/**
 * @file AllopassMarket.java
 * File of the class AllopassMarket
 */

package apikit.model;

import java.util.ArrayList;
import org.w3c.dom.Node;

/**
 * Class providing object mapping of a market item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassMarket {
    /**  
     * (Node) Node object representation of the item
     */
    private Node xml;

    /**
     * Constructor
     *
     * @param xml (XmlNode) The XmlNode object representation of the item
     */
    public AllopassMarket(Node xml) {
        this.xml = xml;
    }

    /**
     * Method retrieving the market country code
     *
     * @return (string) The market country code
     */
    public String getCountryCode() {
        return this.xml.getAttributes().getNamedItem("country_code").getTextContent();
    }

    /**
     * Method retrieving the  market country
     *
     * @return (string) The market country
     */
    public String getCountry() {
        return this.xml.getAttributes().getNamedItem("country").getTextContent();
    }

    /**
     * Method retrieving the market pricepoints
     *
     * @return (ArrayList) The market pricepoints
     */
    public ArrayList getPricepoints() {
        ArrayList pricepoints = new ArrayList();

        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i ++) {
            if (this.xml.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                pricepoints.add(new AllopassPricepoint(this.xml.getChildNodes().item(i)));
            }
        }

        return pricepoints;
    }
}