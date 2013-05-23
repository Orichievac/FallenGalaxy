/**
 * @file OnetimePricingResponse.java
 * File of the class OnetimePricingResponse
 */

package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class defining a onetime pricing/discrete-pricing request's response
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class OnetimePricingResponse extends ApiMappingResponse {

    /**
     * Constructor
     *
     * @param signature
     * @param headers (string)
     * @param body (string)
     */
    public OnetimePricingResponse(String signature, String headers, String body) 
           throws ApiFalseResponseSignatureException, 
                  ApiRemoteErrorException,
                  ApiMissingXMLFeatureException {
        super(signature, headers, body);
    }

    /**
     * Method retrieving the creation date
     *
     * @return (AllopassDate) The creation date
     */
    public AllopassDate getCreationDate() {
        return new AllopassDate(this.xml.getElementsByTagName("creation_date").item(0));
    }

    /**
     * Method retrieving the customer ip
     *
     * @return (string) The customer ip
     */
    public String getCustomerIp() {
        return this.xml.getElementsByTagName("customer_ip").item(0).getTextContent();
    }

    /**
     * Method retrieving the customer country
     *
     * @return (string) The customer country
     */
    public String getCustomerCountry() {
        return this.xml.getElementsByTagName("customer_country").item(0).getTextContent();
    }

    /**
     * Method retrieving the customer country
     *
     * @return (string) The customer country
     */
    public AllopassWebsite getWebsite() {
        return new AllopassWebsite(this.xml.getElementsByTagName("website").item(0));
    }

    /**
     * Method retrieving the countries
     *
     * @return (AllopassCountry[]) The countries
     */
    public ArrayList getCountries() {
        ArrayList countries = new ArrayList();
        NodeList  nodes     = this.xml.getElementsByTagName("countries").item(0).getChildNodes();

        for (int i = 0 ; i < nodes.getLength() ; i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                for (int j = 0 ; j < nodes.item(i).getChildNodes().getLength() ; j++) {
                    if (nodes.item(i).getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
                        countries.add(new AllopassCountry(nodes.item(i).getChildNodes().item(j)));
                    }
                }
            }
        }

        return countries;
    }

    /**
     * Method retrieving the regions
     *
     * @return (AllopassRegion[]) The regions
     */
    public ArrayList getRegions() {
        ArrayList regions = new ArrayList();
        NodeList  nodes   = this.xml.getElementsByTagName("countries").item(0).getChildNodes();

        for (int i = 0 ; i < nodes.getLength() ; i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                regions.add(new AllopassRegion(nodes.item(i)));
            }
        }
        
        return regions;
    }

    /**
     * Method retrieving the markets
     *
     * @return (AllopassMarket[]) The markets
     */
    public ArrayList getMarkets() {
        ArrayList markets = new ArrayList();
        NodeList  nodes   = this.xml.getElementsByTagName("markets").item(0).getChildNodes();

        for (int i = 0 ; i < nodes.getLength() ; i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                markets.add(new AllopassMarket(nodes.item(i)));
            }
        }

        return markets;
    }    
}