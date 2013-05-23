/**
 * @file ApiRemoteErrorException.java
 * File of the class ApiRemoteErrorException
 */

package apikit.exception;

/**
 * Class of an exception for a exception thrown by remote Allopass API
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class ApiRemoteErrorException extends Exception {

    public final static String MESSAGE = "The response indicates that an error occured when processing the request";

    public ApiRemoteErrorException() {
        super(ApiRemoteErrorException.MESSAGE);
    }

     public ApiRemoteErrorException(String message, String description, int code) {
        super(ApiRemoteErrorException.MESSAGE
                + "[Message: "  + message + " | "
                + "Code: " + code    + " | "
                + "Description: "    + description + "]");
    }
}
