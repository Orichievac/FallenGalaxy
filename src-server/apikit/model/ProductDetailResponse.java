/**
 * @file ProductDetailResponse.java
 * File of the class ProductDetailResponse
 */

package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;

/**
 * Class defining a product detail request's response
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class ProductDetailResponse extends ApiMappingResponse {
    /**
     * Constructor
     *
     * @param signature
     * @param headers (string)
     * @param body (string)
     */
    public ProductDetailResponse(String signature, String headers, String body) 
            throws ApiFalseResponseSignatureException,                   
                   ApiRemoteErrorException,
                   ApiMissingXMLFeatureException {
        super(signature, headers, body);
    }

    /**
     * Method retrieving the product id
     *
     * @return (integer) The product id
     */
    public int getId() {
        return Integer.parseInt(this.xml.getElementsByTagName("id").item(0).getTextContent());
    }

    /**
     * Method retrieving the product key
     *
     * @return (string) The product key
     */
    public String getKey() {
        return this.xml.getElementsByTagName("key").item(0).getTextContent();
    }

    /**
     * Method retrieving the product access-type
     *
     * @return (string) The product access-type
     */
    public String getAccessType() {
        return this.xml.getElementsByTagName("access_type").item(0).getTextContent();
    }

    /**
     * Method retrieving the product creation date
     *
     * @return (AllopassDate) The product creation date
     */
    public AllopassDate getCreationDate() {
        return new AllopassDate(this.xml.getElementsByTagName("creation_date").item(0));
    }

    /**
     * Method retrieving the product name
     *
     * @return (string) The product name
     */
    public String getName() {
        return this.xml.getElementsByTagName("name").item(0).getTextContent();
    }

    /**
     * Method retrieving the product's website
     *
     * @return (AllopassWebsite) The product's website
     */
    public AllopassWebsite getWebsite() {
       return new AllopassWebsite(this.xml.getElementsByTagName("website").item(0));
    }

   /**
    * Method retrieving the product expected number of codes
    *
    * @return (integer) The product expected number of codes
    */
    public int getExpectedNumberOfCodes() {
       return Integer.parseInt(this.xml.getElementsByTagName("expected_number_of_codes").item(0).getTextContent());
    }
    
    /**
     * Method retrieving if the transaction is in direct access
     *
     * @return (boolean) If the transaction is in direct access
     */
    public boolean isDirectAccess() {
        return this.xml.getElementsByTagName("direct_access").item(0).getTextContent().equals("true");
    }

    /**
     * Method retrieving the product purchase url
     *
     * @return (string) The product purchase url
     */
    public String getPurchaseUrl() {
        return this.xml.getElementsByTagName("purchase_url").item(0).getTextContent();
    }

    /**
     * Method retrieving the product forward url
     *
     * @return (string) The product forward url
     */
    public String getForwardUrl() {
        return this.xml.getElementsByTagName("forward_url").item(0).getTextContent();
    }

    /**
     * Method retrieving the product error url
     *
     * @return (string) The product error url
     */
    public String getErrorUrl() {
        return this.xml.getElementsByTagName("error_url").item(0).getTextContent();
    }

    /**
     * Method retrieving the product notification url
     *
     * @return (string) The product notification url
     */
    public String getNotificationUrl() {
          return this.xml.getElementsByTagName("notification_url").item(0).getTextContent();
    }
}