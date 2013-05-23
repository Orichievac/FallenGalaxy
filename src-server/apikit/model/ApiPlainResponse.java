/**
 * @file ApiPlainResponse.java
 * File of the abstract class ApiPlainResponse
 */

package apikit.model;

/**
 * Class defining a plain API response
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class ApiPlainResponse extends ApiResponse {
    /**
     * Constructor
     *
     * @param signature (string) Expected response signature
     * @param headers (string) HTTP headers of the response
     * @param body (string) Raw data of the response
     */
    public ApiPlainResponse(String signature, String headers, String body) {
        super(signature, headers, body);
    }
}
