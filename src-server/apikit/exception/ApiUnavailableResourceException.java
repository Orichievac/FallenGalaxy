/**
 * @file ApiUnavailableResourceException.java
 * File of the class ApiUnavailableResourceException
 */

package apikit.exception;

/**
 * Class of an exception for an unreachable Allopass API
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class ApiUnavailableResourceException extends Exception {

    /**
     * Exception definition
     */
    public final static String MESSAGE = "The API you are requesting is currently unavailable";

    public ApiUnavailableResourceException() {
        super(ApiUnavailableResourceException.MESSAGE);
    }

    public ApiUnavailableResourceException(String message, String description, int code) {
        super("Message: "  + message + " | "
                + "Code: " + code    + " | "
                + "Description: "    + description);
    }
}
