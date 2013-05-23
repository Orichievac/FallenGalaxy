/**
 * @file AllopassPayout.java
 * File of the class AllopassPayout
 */

package apikit.model;

import org.w3c.dom.Node;

/**
 * Class providing object mapping of a payout item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassPayout {
    /**
     * (XmlNode) XmlNode object representation of the item
     */
    private Node xml;


    /**
     * Constructor
     *
     * @param xml (XmlNode) The XmlNode object representation of the item
     */
    public AllopassPayout(Node xml) {
        this.xml = xml;
    }

    public String getCurrency() {
        return this.xml.getAttributes().getNamedItem("currency").getTextContent();
    }

    /**
     * Method retrieving the price exchange rate
     *
     * @return (double) The price exchange rate
     */
    public float getAmount() {
        return Float.parseFloat(this.xml.getAttributes().getNamedItem("amount")
                .getTextContent());
    }

    /**
     * Method retrieving the price exchange rate
     *
     * @return (double) The price exchange rate
     */
    public double getExchange() {
        return Double.parseDouble(this.xml.getAttributes().getNamedItem("exchange")
                .getTextContent());
    }

    /**
     * Method retrieving the price reference currency
     *
     * @return (string) The price reference currency
     */
    public String getReferenceCurrency() {
        return this.xml.getAttributes().getNamedItem("reference_currency").getTextContent();
    }

    /**
     * Method retrieving the price reference amount
     *
     * @return (float) The price reference amount
     */
    public float getReferenceAmount() {
        return Float.parseFloat(this.xml.getAttributes().getNamedItem("reference_amount")
                .getTextContent());
    }
}