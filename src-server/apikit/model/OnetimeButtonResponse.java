/**
 * @file OnetimeButtonResponse.java
 * File of the class OnetimeButtonResponse
 */

package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;

/**
 * Class defining a onetime button request's response
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class OnetimeButtonResponse extends ApiMappingResponse {

    /**
     * Constructor
     *
     * @param signature (string) Expected response signature
     * @param headers (string) HTTP headers of the response
     * @param body (string) Raw data of the response
     */
    public OnetimeButtonResponse(String signature, String headers, String body)
            throws ApiFalseResponseSignatureException,
                   ApiRemoteErrorException,
                   ApiMissingXMLFeatureException {
        super(signature, headers, body);
    }

    /**
     * Method retrieving the button access-type
     *
     * @return (string) The button access-type
     */
    public String getAccessType() {
        return this.xml.getElementsByTagName("access_type").item(0).getTextContent();
    }

    /**
     * Method retrieving the button id
     *
     * @return (string) The button id
     */
    public String getButtonId() {
        return this.xml.getElementsByTagName("button_id").item(0).getTextContent();
    }

    /**
     * Method retrieving the button creation date
     *
     * @return (AllopassDate) The button creation date
     */
    public AllopassDate getCreationDate() {
        return new AllopassDate(this.xml.getElementsByTagName("creation_date").item(0));
    }

    /**
     * Method retrieving the button's website
     *
     * @return (AllopassWebsite) The button's website
     */
    public AllopassWebsite getWebsite() {
        return new AllopassWebsite(this.xml.getElementsByTagName("website").item(0));
    }

    /**
     * Method retrieving the button buy url
     *
     * @return (string) The button buy url
     */
    public String getBuyUrl() {
        return this.xml.getElementsByTagName("buy_url").item(0).getTextContent();
    }

    /**
     * Method retrieving the button checkout HTML string
     *
     * @return (string) The button checkout HTML string
     */
    public String getCheckoutButton() {
        return this.xml.getElementsByTagName("checkout_button").item(0).getTextContent();
    }

}
