/**
 * @file ApiConf.java
 * File of the class ApiConf
 */

package apikit.model;

import apikit.conf.Conf;

/**
 * Class providing convenient tools to access configuration data
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class ApiConf {    

    /**
     * (ApiConf) Static instance of the class
     */
    private static ApiConf instance = null;

    /*
     * Constructor
     */
    public ApiConf() {}
    /**
     * Method retrieving the API key
     *
     * @return (string) The public API key
     **/
    public String getApiKey() {
        return Conf.ACCOUNT_KEY_API_KEY;
    }

    /**
     * Method retrieving the private key
     *
     * @return (string) The private API key
     */
    public String getPrivateKey() {
        return Conf.ACCOUNT_KEY_PRIVATE_KEY;
    }

    /**
     * Method retrieving the default response format
     *
     * @return (string) The response format
     */
    public String getDefaultFormat() {
        return Conf.DEFAULT_FROMAT;
    }

    /**
     * Method retrieving the API hostname
     *
     * @return (string) The API hostname
     */
    public String getHost() {
        return Conf.HOST;
    }

    /**
     * Method retrieving the default hash function name
     *
     * @return (string) The default hash function name
     */
     public String getDefaultHash() {
        return Conf.DEFAULT_HASH;
     }

     /**
     * Method retrieving the network timeout delay
     *
     * @return (integer) The network timeout delay
     */
     public int getNetworkTimeout() {
         return Conf.NETWORK_TIMEOUT;
     }

     /**
      * Method retrieving the network protocol
      *
      * @return (string) The network protocol
      */
     public String getNetworkProtocol() {
         return Conf.NETWORK_PROTOCOL;
     }

     /**
      * Method retrieving the network port
      *
      * @return (integer) The network port
      */
     public int getNetworkPort() {
         return Conf.NETWORK_PORT;
     }

    /**
     * Static method providing a single access-point to the class
     *
     * @return (ApiConf) The class instance
     */
    public static ApiConf getInstance() {
        
        if (ApiConf.instance == null) {
            ApiConf.instance = new ApiConf();
	}

        return ApiConf.instance;
    }
}