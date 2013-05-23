/**
 * @file AllopassWebsite.java
 * File of the class AllopassWebsite
 */

package apikit.model;

import org.w3c.dom.Node;


/**
 * Class providing object mapping of a website item
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassWebsite {

    /**
     * (XmlNode) XmlNode object representation of the item
     */
    private Node xml;

    /**
     * Constructor
     *
     * @param xml (XmlNode) The XmlNode object representation of the item
     */
    public AllopassWebsite(Node xml) {
        this.xml = xml;
    }

    /**
     * Method retrieving the website id
     *
     * @return (integer) The website id
     */
    public int getId() {
        return Integer.parseInt(this.xml.getAttributes().getNamedItem("id")
                .getTextContent());
    }

    /**
     * Method retrieving the website url
     *
     * @return (string) The website url
     */
    public String getUrl() {
        return this.xml.getAttributes().getNamedItem("url").getTextContent();
    }

    /**
     * Method retrieving if the website's audience is restricted
     *
     * @return (boolean) If the website's audience is restricted
     */
    public boolean isAudienceRestricted() {
        return this.xml.getAttributes().getNamedItem("audience_restricted")
                .getTextContent().equals("true");
    }

    /**
     * Method retrieving the website category
     *
     * @return (string) The website category
     */
    public String getCategory() {
        return this.xml.getAttributes().getNamedItem("category").getTextContent();
    }

    public String getName() {
        return this.xml.getAttributes().getNamedItem("name").getTextContent();
    }
}