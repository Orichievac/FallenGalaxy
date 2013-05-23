/**
 * @file ApiMissingHashFeatureException.java
 * File of the class ApiMissingHashFeatureException
 */
package apikit.exception;

/**
 * Class of an exception if local system doesn't support configured hash cipher
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class ApiMissingHashFeatureException extends Exception {

    public final static String MESSAGE = "Your local JAVA system doesn't support the configurated hash cipher";

    public ApiMissingHashFeatureException() {
           super(ApiMissingHashFeatureException.MESSAGE);
    }

}
