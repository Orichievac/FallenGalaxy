/**
 * @file TransactionPrepareResponse.java
 * File of the class TransactionPrepareResponse
 */

package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;

/**
 * Class defining a transaction prepare request's response
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class TransactionPrepareResponse extends ApiMappingResponse {

    /**
     * Constructor
     *
     * @param signature
     * @param headers (string)
     * @param body (string)
     */
    public TransactionPrepareResponse(String signature, String headers,  String body) 
            throws ApiFalseResponseSignatureException, 
                   ApiRemoteErrorException,
                   ApiMissingXMLFeatureException {
        super(signature, headers, body);
    }

    /**
     * Method retrieving the transaction access-type
     *
     * @return (string) The transaction access-type
     */
    public String getAccessType() {
        return this.xml.getElementsByTagName("access_type").item(0).getTextContent();
    }

    /**
     * Method retrieving the transaction id
     *
     * @return (string) The transaction id
     */
    public String getTransactionId() {
        return this.xml.getElementsByTagName("transaction_id").item(0).getTextContent();
    }


    /**
     * Method retrieving the transaction creation date
     *
     * @return (AllopassDate) The transaction creation date
     */
    public AllopassDate getCreationDate() {
        return new AllopassDate(this.xml.getElementsByTagName("creation_date").item(0));
    }

    /**
     * Method retrieving the transactions price
     *
     * @return (AllopassPrice) The transactions price
     */
    public AllopassPrice getPrice() {
        return new AllopassPrice(this.xml.getElementsByTagName("price").item(0));
    }

    /**
     * Method retrieving the transaction's pricepoint
     *
     * @return (AllopassPricepoint) The transaction's pricepoint
     */
    public AllopassPricepoint getPricepoint() {
        return new AllopassPricepoint(this.xml.getElementsByTagName("pricepoint").item(0));
    }

    /**
     * Method retrieving the transaction's website
     *
     * @return (AllopassWebsite) The transaction's website
     */
    public AllopassWebsite getWebsite() {
        return new AllopassWebsite(this.xml.getElementsByTagName("website").item(0));
    }

    /**
     * Method retrieving the transaction buy url
     *
     * @return (string) The transaction buy url
     */
    public String getBuyUrl() {
        return this.xml.getElementsByTagName("buy_url").item(0).getTextContent();
    }

    /**
     * Method retrieving the transaction checkout button HTML string
     *
     * @return (string) The transaction checkout button HTML string
     */
    public String getCheckoutButton() {
        return this.xml.getElementsByTagName("checkout_button").item(0).getTextContent();
    }
}