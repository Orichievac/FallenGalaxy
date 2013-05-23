/**
 * @file AllopassDate.java
 * File of the class AllopassDate
 */

package apikit.model;

import org.w3c.dom.Node;

/**
 * Class providing object mapping of a date item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassDate {

   /**
    * (XmlNode) XmlNode object representation of the item
    */
    private Node xml;

    /**
     * Constructor
     *
     * @param xml (XmlNode) The XmlNode object representation of the item
     */
    public AllopassDate(Node xml) {
        this.xml = xml;
    }

    /**
     * Method retrieving the date timestamp
     *
     * @return (integer) The partner timestamp
     */
    public int getTimestamp() {
        String value = this.xml.getAttributes().getNamedItem("timestamp").getTextContent();

        if (!value.isEmpty()) {
            return Integer.parseInt(value);
        }

        return 0;
    }

    /**
     * Method retrieving the date ISO-8601 representation
     *
     * @return (string) The date ISO-8601 representation
     */
    public String getDate() {
        return this.xml.getAttributes().getNamedItem("date").getTextContent();
    }
}
