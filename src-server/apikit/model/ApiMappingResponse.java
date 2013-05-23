/**
 * @file ApiMappingResponse.java
 * File of the class AllopassApiMappingResponse
 */

package apikit.model;

import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;
import apikit.exception.ApiFalseResponseSignatureException;
import apikit.tools.ApiTools;
import org.w3c.dom.Document;

/**
 * Class defining an object mapped API response
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class ApiMappingResponse extends ApiResponse {

    protected Document xml;

    /**
     * Constructor
     */
    public ApiMappingResponse(String signature, String headers,  String body)
            throws ApiFalseResponseSignatureException,
                   ApiMissingXMLFeatureException,
                   ApiRemoteErrorException {
        super(signature, headers, body);

        this.xml = ApiTools.xmlParseString(body);

//        this.verify();
        
    }


    /**
     * Overload of parent internal method providing signature verification
     *
     * @throws ApiRemoteErrorException if response describe a remote API exception
     */
    @Override
    protected void verify() throws ApiFalseResponseSignatureException, ApiRemoteErrorException {
        super.verify();

        String value = this.xml.getChildNodes().item(0).getAttributes().getNamedItem("code").getTextContent();

        if (value == null || value.isEmpty()) {
            throw new ApiRemoteErrorException();
        }
    }
}
