/**
 * @file ApiMissingXMLFeatureException.java
 * File of the class ApiMissingXMLFeatureException
 */

package apikit.exception;


public class ApiMissingXMLFeatureException extends Exception{
    public final static String MESSAGE = "Your local JAVA system had a problem parsing a XML string";
    public ApiMissingXMLFeatureException() {
        super(ApiMissingXMLFeatureException.MESSAGE);
    }

}
