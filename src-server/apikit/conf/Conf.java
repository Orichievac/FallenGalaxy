/**
 * @file Conf.java
 * File of the class Conf
 */

package apikit.conf;

import fr.fg.server.util.Config;

/**
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public abstract class Conf {
    /**
     * Account api key
     * (String)
     */
    public final static String ACCOUNT_KEY_API_KEY     = Config.getAllopassPublicApiKey();

    /**
     * Account private key
     * (String)
     */
    public final static String ACCOUNT_KEY_PRIVATE_KEY = Config.getAllopassPrivateApiKey();

    /**
     * Default hash
     * (String)
     */
    public final static String DEFAULT_HASH            = "sha1";

    /**
     * Default format
     * (String)
     */
    public final static String DEFAULT_FROMAT          = "xml";

    /**
     * Network timeout in seconds
     * (integer)
     */
    public final static int    NETWORK_TIMEOUT         = 30;

    /**
     * Network protocol
     * (String)
     */
    public final static String NETWORK_PROTOCOL        = "http";

    /**
     * Network port
     * (String)
     */
    public final static int    NETWORK_PORT            = 80;

    /**
     * Host
     * (String)
     */
    public final static String HOST                    = "api.allopass.com";
}
