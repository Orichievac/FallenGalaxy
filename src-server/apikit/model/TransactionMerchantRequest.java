/**
 * @file TransactionMerchantRequest.java
 * File of the class TransactionMerchantRequest
 */

package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;
import java.util.TreeMap;

/**
 * Class providing a transaction detail from merchant transaction id API request
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class TransactionMerchantRequest extends ApiRequest {
    public final static String PATH = "transaction/merchant";

    /**
     * Constructor
     *
     * @param parameters (array) Query string parameters of the API call
     * @param mapping (boolean) Should the response be an object mapping or a plain response
     */
    public TransactionMerchantRequest(TreeMap parameters, boolean mapping) {
        super(parameters, mapping);
    }

    /**
     * Provide a way to get the route of the request
     *
     * @return (string) The route of the request
     */
    @Override
    protected String getPath() {
        return TransactionMerchantRequest.PATH;
    }

    /**
     * Provide a way to get the wired response of the request
     *
     * @param signature (string) Expected response signature
     * @param headers (string) HTTP headers of the response
     * @param body (string) Raw data of the response
     *
     * @return (TransactionDetailResponse) A new response
     */
    @Override
    protected ApiResponse newResponse(String signature, String headers, String body)
            throws ApiFalseResponseSignatureException, ApiRemoteErrorException, ApiMissingXMLFeatureException {
        return new TransactionDetailResponse(signature, headers, body);
    }

}
