/**
 * @file ApiResponse.java
 * File of the abstract class ApiResponse
 */

package apikit.model;

import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiRemoteErrorException;

/**
 * Class defining the basis of an API response and providing convenient tools
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public abstract class ApiResponse {

    /**
     * (string) Expected response signature
     */
    protected String signature;

    /**
     * (string) HTTP headers of the response
     */
    protected String headers;

    /**
     * (string) Raw data of the response
     */
    protected String body;

    /**
     * Constructor
     *
     * @param signature (string) Expected response signature
     * @param headers (string) HTTP headers of the response
     * @param body (string) Raw data of the response
     */
    public ApiResponse(String signature, String headers, String body) {
        this.signature = signature;
        this.headers   = headers;
        this.body      = body;
    }

    /**
     * Constructor
     *
     * @throws ApiFalseResponseSignatureException If the expected signature is not found among response headers
     */
    protected void verify() throws ApiFalseResponseSignatureException, ApiRemoteErrorException {
        if (!this.headers.contains("X-Allopass-Response-Signature=[" + this.signature + "]")) {
            throw new ApiFalseResponseSignatureException();
        }
    }

    /**
     * Overload of parent toString magic method
     *
     * @return (string) String representation of an API response (its body)
     */
    @Override
    public String toString() {
        return this.body;
    }
}