/**
 * @file OnetimeDiscretePricingRequest.java
 * File of the class OnetimeDiscretePricingRequest
 */
package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;
import java.util.TreeMap;

/**
 * Class providing a onetime discrete pricing API request
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class OnetimeDiscretePricingRequest extends ApiRequest {

    public final static String PATH = "onetime/discrete-pricing";

    /**
     * Constructor
     *
     * @param parameters (array) Query string parameters of the API call
     * @param mapping (boolean) Should the response be an object mapping or a plain response
     */
    public OnetimeDiscretePricingRequest(TreeMap parameters, boolean mapping) {
        super(parameters, true);
    }

    /**
     * Provide a way to get the route of the request
     *
     * @return (string) The route of the request
     */
    @Override
    protected String getPath() {
        return OnetimeDiscretePricingRequest.PATH;
    }

    /**
     * Provide a way to get the wired response of the request
     *
     * @param signature (string) Expected response signature
     * @param headers (string) HTTP headers of the response
     * @param body (string) Raw data of the response
     *
     * @return (OnetimePricingResponse) A new response
     */
    @Override
    protected ApiResponse newResponse(String signature, String headers, String body)
            throws ApiFalseResponseSignatureException, 
                   ApiRemoteErrorException,
                   ApiMissingXMLFeatureException {
        return new OnetimePricingResponse(signature, headers, body);
    }
}