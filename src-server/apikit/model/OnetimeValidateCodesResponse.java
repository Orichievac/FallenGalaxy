/**
 * @file OnetimeValidateCodesResponse.java
 * File of the class OnetimeValidateCodesResponse
 */

package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;
import java.util.ArrayList;
import javax.xml.soap.Node;
import org.w3c.dom.NodeList;

/**
 * Class defining a onetime validate-codes request's response
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 * @date 2010 (c) Hi-media
 */
public class OnetimeValidateCodesResponse extends ApiMappingResponse {
    /**
     * The validation is successful
     */
    public final static int VALIDATECODES_SUCCESS = 0;

    /**
     * The validation failed
     */
    public final static int VALIDATECODES_FAILED  = 1;

    /**
     * Constructor
     *
     * @param signature
     * @param headers (string)
     * @param body (string)
     */
    public OnetimeValidateCodesResponse(String signature, String headers, String body)
            throws ApiFalseResponseSignatureException,                 
                   ApiRemoteErrorException,
                   ApiMissingXMLFeatureException {
        super(signature, headers, body);
    }

    /**
     * Method retrieving the validation status
     *
     * @return (integer) The validation status
     */
    public int getStatus() {
        return Integer.parseInt(this.xml.getElementsByTagName("status").item(0).getTextContent());

    }

    /**
     * Method retrieving the validation status
     *
     * @return (integer) The validation status
     */
    public String getStatusDescription() {
        return this.xml.getElementsByTagName("status_description").item(0).getTextContent();

    }

    /**
     * Method retrieving the access-type
     *
     * @return (string) The access-type
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
     * Method retrieving the validation's price
     *
     * @return (AllopassPrice) The validation's price
     */
    public AllopassPrice getPrice() {
        return new AllopassPrice(this.xml.getElementsByTagName("price").item(0));
    }

    /**
     * Method retrieving the validation's paid price
     *
     * @return (AllopassPrice) The validation's paid price
     */
    public AllopassPrice getPaid() {
        return new AllopassPrice(this.xml.getElementsByTagName("paid").item(0));
    }

    /**
     * Method retrieving the validation date
     *
     * @return (AllopassDate) The validation date
     */
    public AllopassDate getValidationDate() {
        return new AllopassDate(this.xml.getElementsByTagName("validation_date").item(0));
    }

    /**
     * Method retrieving the product name
     *
     * @return (string) The product name
     */
    public String getProductName() {
        return this.xml.getElementsByTagName("product_name").item(0).getTextContent();
    }

    /**
     * Method retrieving the website
     *
     * @return (AllopassWebsite) The website
     */
    public AllopassWebsite getWebsite() {
        return new AllopassWebsite(this.xml.getElementsByTagName("website").item(0));
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
     * Method retrieving the expected number of codes
     *
     * @return (integer) The expected number of codes
     */
    public int getExpectedNumberOfCodes() {
        return Integer.parseInt(this.xml.getElementsByTagName("expected_number_of_codes").item(0).getTextContent());
    }

    /**
     * Method retrieving the validation codes
     *
     * @return (ArrayList) The validation codes
     */
    public ArrayList getCodes() {
        ArrayList codes = new ArrayList();
        NodeList  nodes = this.xml.getElementsByTagName("codes").item(0).getChildNodes();

        for (int i = 0 ; i < nodes.getLength() ; i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                codes.add(new AllopassCode(nodes.item(i)));
            }
        }

        return codes;
    }

    /**
     * Method retrieving the merchant transaction id
     *
     * @return (string) The merchant transaction id
     */
    public String getMerchantTransactionId() {
        return this.xml.getElementsByTagName("merchant_transaction_id").item(0).getTextContent();
    }

    /**
     * Method retrieving the client data
     *
     * @return (string) The client data
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
            partners.add(new AllopassPartner(nodes.item(i)));
        }

        return partners;
    }
}