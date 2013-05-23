/**
 * @file TransactionDetailResponse.java
 * File of the class TransactionDetailResponse
 */

package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class defining a product detail request's response
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class TransactionDetailResponse extends ApiMappingResponse {
    /**
     * The transaction is at first step : initialization
     */
    public final static int TRANSACTION_INIT               = -1;

    /**
     * The transaction is successful
     */
    public final static int TRANSACTION_SUCCESS            = 0;

    /**
     * The transaction failed due to insufficient funds
     */
    public final static int TRANSACTION_INSUFFICIENT_FUNDS = 1;

    /**
     * The transaction timeouted
     */
    public final static int TRANSACTION_TIMEOUT            = 2;

    /**
     * The transaction has been cancelled by user
     */
    public final static int TRANSACTION_CANCELLED          = 3;

    /**
     * The transaction has been blocked due to fraud suspicions
     */
    public final static int TRANSACTION_ANTI_FRAUD         = 4;

    /**
     * Constructor
     *
     * @param signature
     * @param headers (string)
     * @param body (string)
     */
    public TransactionDetailResponse(String signature, String headers, String body)
            throws ApiFalseResponseSignatureException, 
                   ApiRemoteErrorException,
                   ApiMissingXMLFeatureException {
        super(signature, headers, body);
    }

    /**
     * Method retrieving the transaction status
     *
     * @return (integer) The transaction status
     */
    public int getStatus() {
        return Integer.parseInt(this.xml.getElementsByTagName("status").item(0).getTextContent());
    }

    /**
     * Method retrieving the transaction status description
     *
     * @return (string) The transaction status description
     */
    public String getStatusDescription() {
        return this.xml.getElementsByTagName("status_description").item(0).getTextContent();
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
     * Method retrieving the transaction's price
     *
     * @return (AllopassPrice) The transaction's price
     */
    public AllopassPrice getPrice() {
        return new AllopassPrice(this.xml.getElementsByTagName("price").item(0));
    }

    /**
     * Method retrieving the transaction's paid price
     *
     * @return (AllopassPrice) The transaction's paid price
     */
    public AllopassPrice getPaid() {
        return new AllopassPrice(this.xml.getElementsByTagName("paid").item(0));
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
     * Method retrieving the transaction end date
     *
     * @return (AllopassDate) The transaction end date
     */
    public AllopassDate getEndDate() {
        return new AllopassDate(this.xml.getElementsByTagName("end_date").item(0));
    }

    /**
     * Method retrieving the transaction product name
     *
     * @return (string) The transaction product name
     */
    public String getProductName() {
        return this.xml.getElementsByTagName("product_name").item(0).getTextContent();
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
     * Method retrieving the transaction customer ip
     *
     * @return (string) The transaction customer ip
     */
    public String getCustomerIp() {
        return this.xml.getElementsByTagName("customer_ip").item(0).getTextContent();
    }
    
    /**
     * Method retrieving the transaction customer country
     *
     * @return (string) The transaction customer country
     */
    public String getCustomerCountry() {
        return this.xml.getElementsByTagName("customer_country").item(0).getTextContent();
    }

    /**
     * Method retrieving the transaction expected number of codes
     *
     * @return (integer) The transaction expected number of codes
     */
    public int getExpectedNumberOfCodes() {
        return Integer.parseInt(this.xml.getElementsByTagName("expected_number_of_codes").item(0).getTextContent());
    }

    /**
     * Method retrieving the transaction codes
     *
     * @return (string[]) The transaction codes
     */
    public ArrayList getCodes() {
        ArrayList codes = new ArrayList();
        NodeList  nodes = this.xml.getElementsByTagName("codes").item(0).getChildNodes();

        for (int i = 0 ; i < nodes.getLength() ; i++) {     
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                codes.add(nodes.item(i).getTextContent());
            }
        }

        return codes;
    }

    /**
     * Method retrieving the transaction merchant transaction id
     *
     * @return (string) The transaction merchant transaction id
     */
    public String getMerchantTransactionId() {
        return this.xml.getElementsByTagName("merchant_transaction_id").item(0).getTextContent();
    }

    /**
     * Method retrieving the transaction client data
     *
     * @return (string) The transaction client data
     */
    public String getData() {
        return this.xml.getElementsByTagName("data").item(0).getTextContent();
    }

    /**
     * Method retrieving the affiliate code
     *
     * @return (string) The affiliate code
     */
    public String getAffiliate() {
        return this.xml.getElementsByTagName("affiliate").item(0).getTextContent();
    }

    /**
     * Method retrieving the partners
     *
     * @return (AllopassPartner[]) The partners
     */
    public ArrayList getPartners() {
        ArrayList partners = new ArrayList();
        NodeList  nodes    = this.xml.getElementsByTagName("partners").item(0).getChildNodes();

        for (int i = 0 ; i < nodes.getLength() ; i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {                
                partners.add(new AllopassPartner(nodes.item(i)));
            }
        }

        return partners;
    }
}