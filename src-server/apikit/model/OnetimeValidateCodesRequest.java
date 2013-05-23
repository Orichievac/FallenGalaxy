/**
 * @file OnetimeValidateCodesRequest.java
 * File of the class OnetimeValidateCodesRequest
 */

package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;
import java.util.TreeMap;

/**
 * Class providing a onetime validate codes API request
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class OnetimeValidateCodesRequest extends ApiRequest {
    public final static String PATH = "onetime/validate-codes";

    /**
     * Constructor
     *
     * @param parameters (array) Query string parameters of the API call
     * @param mapping (boolean) Should the response be an object mapping or a plain response
     */
    public OnetimeValidateCodesRequest(TreeMap parameters, boolean mapping) {
        super(parameters, mapping);
    }

    /**
     * Overload of internal method which determinates that request has to be done using POST
     *
     * @return (boolean) The request has to be done using POST
     */
    @Override
    protected String getPath() {
        return OnetimeValidateCodesRequest.PATH;
    }

    /**
     * Overload of internal method which determinates that request has to be done using POST
     *
     * @return (boolean) The request has to be done using POST
     */
    @Override
    protected boolean isHttpPost() {
        return true;
    }

    /**
     * Provide a way to get the wired response of the request
     *
     * @param signature (string) Expected response signature
     * @param headers (string) HTTP headers of the response
     * @param body (string) Raw data of the response
     *
     * @return (OnetimeValidateCodesResponse) A new response
     */
    @Override
    protected ApiResponse newResponse(String signature, String headers, String body)
            throws ApiFalseResponseSignatureException, ApiRemoteErrorException, ApiMissingXMLFeatureException {
        return new OnetimeValidateCodesResponse(signature, headers, body);
    }

}
