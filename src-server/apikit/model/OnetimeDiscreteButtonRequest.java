/**
 * @file OnetimeDiscreteButtonRequest.java
 * File of the class OnetimeDiscreteButtonRequest
 */

package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;
import java.util.TreeMap;

/**
 * Class providing a onetime discrete button API request
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class OnetimeDiscreteButtonRequest extends ApiRequest {

     /**
     * Route path of the API
     */
    public final static String PATH = "onetime/discrete-button";

    /**
     * Constructor
     *
     * @param parameters (array) Query string parameters of the API call
     * @param mapping (boolean) Should the response be an object mapping or a plain response
     */
    public OnetimeDiscreteButtonRequest(TreeMap parameters, boolean mapping) {
        super(parameters, mapping);
    }
    /**
     * Provide a way to get the route of the request
     *
     * @return (string) The route of the request
     */
    @Override
    protected String getPath() {
        return OnetimeDiscreteButtonRequest.PATH;
    }

    /**
     * Overload of internal method which determinates that request has to be done using POST
     *
     * @return (boolean) The request has to be done using POST
     */
    @Override
    public boolean isHttpPost() {
        return true;
    }

    /**
     * Provide a way to get the wired response of the request
     *
     * @param signature (string) Expected response signature
     * @param headers (string) HTTP headers of the response
     * @param body (string) Raw data of the response
     *
     * @return (OnetimeButtonResponse) A new response
     */
    @Override
    protected ApiResponse newResponse(String signature, String headers, String body)
            throws ApiFalseResponseSignatureException,
                   ApiRemoteErrorException,
                   ApiMissingXMLFeatureException {
        return new OnetimeButtonResponse(signature, headers, body);
    }
}