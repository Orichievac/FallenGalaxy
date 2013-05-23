/**
 * @file AllopassPartner.java
 * File of the class AllopassPartner
 */
package apikit.model;

import org.w3c.dom.Node;

/**
 * Class providing object mapping of a partner item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassPartner {
    /**
     * (XmlNode) XmlNode object representation of the item
     */
    private Node xml;

    /**
     * Constructor
     *
     * @param xml (XmlNode) The XmlNode object representation of the item
     */
    public AllopassPartner(Node xml) {
        this.xml = xml;
    }

    /**
     * Method retrieving the partner id
     *
     * @return (integer) The partner id
     */
    public int getId() {
        return Integer.parseInt(this.xml.getAttributes().getNamedItem("id")
                .getTextContent());
    }

    /**
     * Method retrieving the partner share amount
     *
     * @return (float) The partner share amount
     */
    public float getShare() {
        return Float.parseFloat(this.xml.getAttributes().getNamedItem("share")
                .getTextContent());
    }

    /**
     * Method retrieving the partner map id
     *
     * @return (integer) The partner map id
     */
    public int getMap() {
        String value = this.xml.getAttributes().getNamedItem("map").getTextContent();
        
        if (!value.isEmpty()) {
            return Integer.parseInt(value);
        }

        return 0;
    }
}