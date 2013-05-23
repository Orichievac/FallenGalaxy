/**
 * @file ApiFalseResponseSignatureException.java
 * File of the class ApiFalseResponseSignatureException
 */
package apikit.exception;

/**
 * Class of an exception if a section is not found into configuration file
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class ApiFalseResponseSignatureException extends Exception {
    /**
     * Exception definition
     */
    public final static String MESSAGE = "The signature of the response is false, possible hack attempt";

    /**
     * Constructor
     */
    public ApiFalseResponseSignatureException() {
        super(ApiFalseResponseSignatureException.MESSAGE);
    }
}
