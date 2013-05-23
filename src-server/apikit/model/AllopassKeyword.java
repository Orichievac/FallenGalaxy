/**
 * @file AllopassKeyword.java
 * File of the class AllopassKeyword
 */

package apikit.model;

import org.w3c.dom.Node;

/**
 * Class providing object mapping of a date item
 *
 * @author Jérémie <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassKeyword {

    /**
     * (XmlNode) XmlNode object representation of the item
     */
    private Node xml;

    /**
     * Constructor
     *
     * @param xml (XMLDocument) The XmlNode object representation of the item
     */
    public AllopassKeyword(Node xml) {
        this.xml = xml;
    }

    /**
     * Method retrieving the keyword name
     *
     * @return (string) The keyword name
     */
    public String getName() {
        return this.xml.getAttributes().getNamedItem("name").getTextContent();
    }


    /**
     * Method retrieving the keyword shortcode
     *
     * @return (integer) The keyword shortcode
     */
    public int getShortcode() {
        return Integer.parseInt(this.xml.getAttributes().getNamedItem("shortcode")
                .getTextContent());
    }

    /**
     * Method retrieving the keyword operators
     *
     * @return (string) The keyword operators
     */
    public String getOperators() {
        return this.xml.getAttributes().getNamedItem("operators").getTextContent();
    }

    /**
     * Method retrieving the keyword number billed messages
     *
     * @return (integer) The keyword number billed messages
     */
    public int getNumberBilledMessages() {
        return Integer.parseInt(this.xml.getAttributes().getNamedItem("number_billed_messages")
                .getTextContent());
    }

    /**
     * Method retrieving the phone number description
     *
     * @return (string) The phone number description
     */
    public String getDescription() {
        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            Node node = this.xml.getChildNodes().item(i);

            if (node.getNodeName().equals("description")) {
                return node.getTextContent();
            }
        }

        return null;
    }
}