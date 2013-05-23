/**
 * @file AllopassCode.java
 * File of the class AllopassCode
 */

package apikit.model;

import org.w3c.dom.Node;


/**
 * Class providing object mapping of a code item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassCode {

    /**
     * (Node) Node object representation of the item
     */
    private Node xml;

    /**
     * Constructor
     *
     * @param xml (XmlNode) The XmlNode object representation of the item
     */
    public AllopassCode(Node xml) {
        this.xml = xml;
    }

    /**
     * Method retrieving the code value (the code string)
     *
     * @return (String)
     */
    public String getValue() {
        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            Node node = this.xml.getChildNodes().item(i);

            if (node.getNodeName().equals("value")) {
                return node.getTextContent();
            }
        }
        return null;
    }

    /**
     * Method retrieving the code pricepoint
     *
     * @return (AllopassPricepoint) The code pricepoint
     */
    public AllopassPricepoint getPricepoint() {
        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            Node node = this.xml.getChildNodes().item(i);

            if (node.getNodeName().equals("pricepoint")) {
                return new AllopassPricepoint(node);
            }
        }
        return null;
    }

    /**
     * Method retrieving the code price
     *
     * @return (AllopassPrice) The code price
     */
    public AllopassPrice getPrice() {
        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            Node node = this.xml.getChildNodes().item(i);

            if (node.getNodeName().equals("price")) {
                return new AllopassPrice(node);
            }
        }
        return null;
    }

    /**
     * Method retrieving the code paid
     *
     * @return (AllopassPrice) The code paid
     */
    public AllopassPrice getPaid() {
        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            Node node = this.xml.getChildNodes().item(i);

            if (node.getNodeName().equals("paid")) {
                return new AllopassPrice(node);
            }
        }
        return null;
    }

    /**
     * Method retrieving the code payout
     *
     * @return (AllopassPrice) The code payout
     */
    public AllopassPrice getPayout() {
        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            Node node = this.xml.getChildNodes().item(i);

            if (node.getNodeName().equals("payout")) {
                return new AllopassPrice(node);
            }
        }
        return null;
    }
}
