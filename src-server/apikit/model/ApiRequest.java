/**
 * @file ApiRequest.java
 * File of the abstract class ApiRequest
 */

package apikit.model;

import apikit.exception.ApiMissingHashFeatureException;
import apikit.exception.ApiFalseResponseSignatureException;
import apikit.exception.ApiMissingXMLFeatureException;
import apikit.exception.ApiRemoteErrorException;
import apikit.exception.ApiUnavailableResourceException;
import apikit.tools.ApiTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;
import org.w3c.dom.Document;


/**
 * Class defining the basis of an API request and providing convenient tools
 *
* @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public abstract class ApiRequest {

    /**
     * The response format needed to provide response object mapping
     */
    public final static String MAPPING_FORMAT          = "xml";

    /**
     * The API remote path
     */
    public final static String API_PATH                = "/rest/";

    /**
     * The HTTP connector
     */
    public final static String HTTP_CONNECTOR          = "://";

    /**
     * The HTTP query string connector
     */
    public final static String HTTP_QUERY_CONNECTOR    = "?";

    /**
     * The HTTP carriage return line feed
     */
    public final static String HTTP_CRLF               = "\r\n";

    /**
     * The HTTP version
     */
    public final static String HTTP_VERSION            = "1.1";

    /**
     * The HTTP chunk size
     */
    public final static int HTTP_CHUNK_SIZE            = 4096;

    /**
     * The HTTP size of separator between headers and data
     */
    public final static int HTTP_HEADER_SEPARATOR_SIZE = 4;

    /**
     * The HTTP specific user agent header
     */
    public final static String HTTP_USER_AGENT         = "Allopass-ApiKit-JAVA";


    /**
     * (TreeMap) Query string parameters of the API call
     */
    protected TreeMap parameters;

    /**
     * (boolean) If the request call has to return an object mapped response
     */
    protected boolean mapping;

    /**
     * Constructor
     *
     * @param parameters (array) Query string parameters of the API call
     * @param mapping (boolean) Should the response be an object mapping or a plain response
     */
    public ApiRequest (TreeMap parameters, boolean mapping) {
        this.parameters   = parameters;
        this.mapping      = mapping;
    }

    /**
     * Provide a way to get the route of each child request
     *
     * @return (string) The route of the request
     */
    abstract protected String getPath();

    /**
     * Provide a way to get the wired response of each child request
     *
     * @param signature (string) Expected response signature
     * @param headers (string) HTTP headers of the response
     * @param body (string) Raw data of the response
     *
     * @return (ApiResponse) A new response regarding the type of the request
     */
    abstract protected ApiResponse newResponse(String signature, String headers, String body)
            throws ApiFalseResponseSignatureException,
                   ApiMissingXMLFeatureException,
                   ApiRemoteErrorException;

    /**
     * Provide a way to get the wired response of each child request
     *
     * @return (ApiResponse) A new response regarding the type of the request
     */
    public ApiResponse call() throws ApiMissingHashFeatureException,
                                     ApiUnavailableResourceException, Exception  {

        String[] response = this.buildParameters().sign().xmlCall();
        String headers    = response[1];
        String body       = new String(response[0].getBytes(), "UTF-8") + "\n";
        String signature  = this.hash(body + ApiConf.getInstance().getPrivateKey());

        return this.mapping ?
            this.newResponse(signature, headers, body) :
            new ApiPlainResponse(signature, headers, body);
    }

    /**
     * Internal method to hash data with the defined cipher
     *
     * @param data (string) Data to be hashed
     *
     * @return (string) The hashed data
     *
     * @throws ApiMissingHashFeatureException If configured cipher (SHA1) API isn't loaded/supported
     */
    protected String hash(String data) throws ApiMissingHashFeatureException,
                                              Exception {
        String cipher  = ApiConf.getInstance().getDefaultHash();
        String encoded = ApiTools.toHash(data, cipher);

        if (encoded.isEmpty() || encoded.equals(null)) {
            throw new ApiMissingHashFeatureException();
        }

        return encoded;
    }

    /**
     * Internal method which determinates if the request has to be done using POST
     *
     * @return (boolean) If the request has to be done using POST
     */
    protected boolean isHttpPost() {
        return false;
    }

    /**
     * Internal method which tries to call remote API
     *
     * @return (string[]) A 0-indexed array which contains response headers and body
     *
     * @throws ApiMissingNetworkFeatureException If there is neither cURL nor fsockopen enabled/available
     */
    protected String[] xmlCall() throws ApiUnavailableResourceException,
                                        Exception, ApiRemoteErrorException {
        try {
            String host = ApiConf.getInstance().getNetworkProtocol()
                                      + ApiRequest.HTTP_CONNECTOR
                                      + ApiConf.getInstance().getHost()
                                      + ":" + ApiConf.getInstance().getNetworkPort()
                                      + ApiRequest.API_PATH
                                      + this.getPath();
            String requestParameters  = ApiTools.httpBuildQuery(this.parameters);
            host                      = this.isHttpPost() ? host : host
                                      + ApiRequest.HTTP_QUERY_CONNECTOR + requestParameters;
            URL url                   = new URL(host);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            request.setRequestMethod(this.isHttpPost() ? "POST" : "GET");
            request.setConnectTimeout(ApiConf.getInstance().getNetworkTimeout() * 1000);
            request.addRequestProperty("User-Agent", ApiRequest.HTTP_USER_AGENT);
            request.addRequestProperty("Connection", "keep-alive");
            request.setDoOutput(true);

            if (this.isHttpPost()) {
                request.setDoInput(true);
                request.addRequestProperty("Content-Lenght", String.valueOf(requestParameters.getBytes().length));
                request.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStreamWriter writer = new OutputStreamWriter(request.getOutputStream());
                writer.write(requestParameters);
                writer.flush();
                writer.close();
            }
            
            InputStream response = request.getResponseCode() < 400 ?
                request.getInputStream() : request.getErrorStream();

            Scanner scanner = new Scanner(new InputStreamReader(response)).useDelimiter("\\Z");
            String str      = scanner.next();
            scanner.close();

            request.disconnect();

            if (request.getResponseCode() >= 400) {        
                Document xmlDocument = ApiTools.xmlParseString(str);
                Integer code   = Integer.parseInt(xmlDocument.getDocumentElement().getAttributes().getNamedItem("code").getTextContent());
                String message = xmlDocument.getDocumentElement().getAttributes().getNamedItem("message").getTextContent();
                throw new ApiRemoteErrorException(message, "", code);
            }

            return new String[] {str, request.getHeaderFields().toString()};
        }
        catch (IOException ioe) {
            throw new Exception(ioe);
        }
    }

    /**
     * Internal method to build special required API parameters
     *
     * @return (AllopassApiRequest) The class instance
     */
     protected ApiRequest buildParameters() throws Exception {
         ArrayList formats   = new ArrayList(2);

         formats.add("json");
         formats.add("xml");

         this.parameters.put("api_ts",   String.valueOf(Calendar.getInstance().getTime().getTime() / 1000));
         this.parameters.put("api_key",  ApiConf.getInstance().getApiKey());
         this.parameters.put("api_hash", ApiConf.getInstance().getDefaultHash());

         if (this.parameters.containsKey("format")) {
             if (this.mapping) {
                 this.parameters.put("format", ApiRequest.MAPPING_FORMAT);
             }
             else if (!formats.contains(this.parameters.get("format"))) {
                 this.parameters.put("format", ApiConf.getInstance().getDefaultFormat());
             }
         }
         else {
             this.parameters.put("format", ApiConf.getInstance().getDefaultFormat());
         }

         return this;
     }


    /**
     * Internal method to sign the request call
     *
     * @return (AllopassApiRequest) The class instance
     */
     protected ApiRequest sign() throws ApiMissingHashFeatureException,
                                        Exception {
         String sign = "";

         for (Iterator i = this.parameters.keySet().iterator() ; i.hasNext() ; ) {
             String key   = String.valueOf(i.next());
             sign        += key;
             Object value  = this.parameters.get(key);
             
             if (value.getClass().getName().equals("java.util.TreeMap")) {
                 TreeMap al = (TreeMap) this.parameters.get(key);

                 for (Iterator j = al.keySet().iterator() ; j.hasNext() ; ) {
                    String sKey = String.valueOf(j.next());
                    sign += al.get(sKey).toString();
                 }
             }
             else {
                sign += String.valueOf(value);
             }
         }

         sign += ApiConf.getInstance().getPrivateKey();

         this.parameters.put("api_sig", this.hash(sign));
        
         return this;
     }
}