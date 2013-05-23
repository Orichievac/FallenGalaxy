/**
 * @file AllopassPrice.java
 * File of the class AllopassPrice
 */

package apikit.model;

import org.w3c.dom.Node;

/**
 * Class providing object mapping of a price item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassPrice {
    /**
     * (Node) Node object representation of the item
     */
    private Node xml;

    /**
     * Constructor
     *
     * @param xml (Node) The Node object representation of the item
     */
    public AllopassPrice(Node xml) {
        this.xml = xml;
    }
    /**
     * Method retrieving the price currency
     *
     * @return (String) The price currency
     */
    public String getCurrency() {
        return this.xml.getAttributes().getNamedItem("currency").getTextContent();
    }

    /**
     * Method retrieving the price amount
     *
     * @return (float) The price amount
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
     * Method retrieving the price exchange rate
     *
     * @return (double) The price exchange rate
     */
    public String getReferenceCurrency() {
        return this.xml.getAttributes().getNamedItem("reference_currency")
                .getTextContent();
    }

    /**
     * Method retrieving the price reference currency
     *
     *
     * @return (string) The price reference currency
     */
    public float getReferenceAmount() {
        return Float.parseFloat(this.xml.getAttributes().getNamedItem("reference_amount")
                .getTextContent());
    }
}