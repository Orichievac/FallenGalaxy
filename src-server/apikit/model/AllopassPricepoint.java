/**
 * @file AllopassPricepoint.java
 * File of the class AllopassPricepoint
 */

package apikit.model;

import java.util.ArrayList;
import org.w3c.dom.Node;

/**
 * Class providing object mapping of a pricepoint item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassPricepoint {
    /**
     * (Node) Node object representation of the item
     */
    private Node xml;

    public AllopassPricepoint(Node xml) {
        this.xml = xml;
    }

    /**
     * Method retrieving the pricepoint id
     *
     * @return (integer) The pricepoint id
     */
    public int getId() {
        return Integer.parseInt(this.xml.getAttributes().getNamedItem("id").getTextContent());
    }

    /**
     * Method retrieving the pricepoint type
     *
     * @return (string) The pricepoint type
     */
    public String getType() {
        return this.xml.getAttributes().getNamedItem("type").getTextContent();
    }
    
    
    /**
     * Method retrieving the pricepoint category
     *
     * @return (string) The pricepoint category
     */
    public String getCategory() {
        return this.xml.getAttributes().getNamedItem("category").getTextContent();
    }

    /**
     * Method retrieving the pricepoint country code
     *
     * @return (string) The pricepoint country code
     */
    public String getCountryCode() {
        return this.xml.getAttributes().getNamedItem("country_code").getTextContent();
    }

    /**
     * Method retrieving the pricepoint's price
     *
     * @return (AllopassPrice) The pricepoint's price
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
     * Method retrieving the pricepoint's payout
     *
     * @return (AllopassPayout) The pricepoint's payout
     */
    public AllopassPayout getPayout() {
        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            Node node = this.xml.getChildNodes().item(i);

            if (node.getNodeName().equals("payout")) {
                return new AllopassPayout(node);
            }
        }

        return null;
    }

    /**
     * Method retrieving the pricepoint's phone numbers
     *
     * @return (ArrayList) The pricepoint's phone numbers
     */
    public String getBuyUrl() {
        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            Node node = this.xml.getChildNodes().item(i);

            if (node.getNodeName().equals("buy_url")) {
                return node.getTextContent();
            }
        }

        return null;
    }

    /**
     * 
     * Method retrieving the pricepoint's phone numbers
     *
     * @return (ArrayList) The pricepoint's phone numbers
     */	
    public ArrayList getPhoneNumbers() {
        ArrayList phoneNumbers = new ArrayList();
        
        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            Node node = this.xml.getChildNodes().item(i);
            
            if (node.getNodeType() == Node.ELEMENT_NODE && 
                    node.getNodeName().equals("phone_numbers")) {
                for (int j = 0 ; j < node.getChildNodes().getLength() ; j++) {
                    if (node.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
                        phoneNumbers.add(new AllopassPhoneNumber(node.getChildNodes().item(j)));
                    }
                }
            }
        }
        
        return phoneNumbers;
    }

    /**
     * Method retrieving the pricepoint's keywords
     *
     * @return (ArrayList) The pricepoint's keywords
     */
    public ArrayList getKeywords() {
        ArrayList keyWords = new ArrayList();

        for (int i = 0 ; i < this.xml.getChildNodes().getLength() ; i++) {
            Node node = this.xml.getChildNodes().item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE &&
                    node.getNodeName().equals("keywords")) {
                for (int j = 0 ; j < node.getChildNodes().getLength() ; j++) {
                    if (node.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
                        keyWords.add(new AllopassKeyword(node.getChildNodes().item(j)));
                    }
                }
            }
        }

        return keyWords;
    }


     /**
      *
      * Method retrieving the pricepoint description
      *
      * @return (string) The pricepoint description
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