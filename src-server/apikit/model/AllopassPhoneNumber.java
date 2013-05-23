/**
 * @file AllopassPhoneNumber.java
 * File of the class AllopassPhoneNumber
 */
package apikit.model;

import org.w3c.dom.Node;

/**
 * Class providing object mapping of a phone number item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassPhoneNumber {

    /**
     * (Node) Node object representation of the item
     */
    private Node xml;


    /**
     * Constructor
     *
     * @param xml (Node) The Node object representation of the item
     */
    public AllopassPhoneNumber(Node xml) {
        this.xml = xml;
    }



   /**   
    * Method retrieving the phone number number
    *
    * @return (string) The phone number number
    */
    public String getValue() {
        return this.xml.getAttributes().getNamedItem("value").getTextContent();
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
