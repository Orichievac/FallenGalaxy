/**
 * @file AllopassAPI.java
 * File of the class AllopassAPI
 */

package apikit.api;

import apikit.exception.*;


import apikit.model.ApiResponse;
import apikit.model.OnetimeButtonRequest;
import apikit.model.OnetimeDiscreteButtonRequest;
import apikit.model.OnetimeDiscretePricingRequest;
import apikit.model.OnetimePricingRequest;
import apikit.model.OnetimeValidateCodesRequest;
import apikit.model.ProductDetailRequest;
import apikit.model.TransactionDetailRequest;
import apikit.model.TransactionMerchantRequest;
import apikit.model.TransactionPrepareRequest;

import java.util.TreeMap;

/**
 * Class providing a convenient way to make Allopass API requests
 *
 * @author Jérémie Havret <jhavret@hi-media.com>
 *
 * @date 2010 (c) Hi-media
 */
public class AllopassAPI {
    
    /**
     * Constructor
     */
    public AllopassAPI() {
    }

    /**
     * Method performing a onetime pricing request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     *
     * @return (ApiResponse) The API call response
     * Will be a OnetimePricingResponse instance
     *
     * @code
     *  TreeMap parameters = new TreeMap();
     *	parameters.put("site_id", "127042");
     *	parameters.put("country", "FR");
     *
     *	AllopassAPI api                 = new AllopassAPI();
     *	OnetimePricingResponse response = (OnetimePricingResponse)api.getOnetimePricing(parameters);
     *
     *	System.out.println(response.getWebsite().getName());
     *
     *	for (int i = 0 ; i < response.getCountries().size() ; i++) {
     *      AllopassCountry country = (AllopassCountry) response.getCountries().get(i);
     *      System.out.println(country.getCode() + " - " + country.getName());
     *	}
     * @endcode
     */
     public ApiResponse getOnetimePricing(TreeMap parameters)
             throws ApiUnavailableResourceException,
                    ApiMissingHashFeatureException,
                    ApiFalseResponseSignatureException,
                    ApiRemoteErrorException,
                    ApiUnavailableResourceException,
                    Exception {
        return this.getOnetimePricing(parameters, true);
     }

    /**
     * Method performing a onetime pricing request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     * @param mapping (bool) Should the response be an object mapping or a plain response
     *
     * @return (ApiResponse) The API call response
     * Will be a OnetimePricingResponse instance if mapping is true, an ApiPlainResponse if not
     */
    public ApiResponse getOnetimePricing(TreeMap parameters, boolean mapping)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   ApiRemoteErrorException,
                   ApiUnavailableResourceException,
                   Exception {
        OnetimePricingRequest request = new OnetimePricingRequest(parameters, mapping);
        return request.call();
    }

    /**
     * Method performing a onetime discrete pricing request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     *
     * @return (ApiResponse) The API call response
     * Will be a OnetimePricingResponse instance
     *
     * @code
     * TreeMap parameters = new TreeMap();
     * parameters.put("site_id", "127042");
     * parameters.put("country", "FR");
     * parameters.put("amount", "12");
     *
     * AllopassAPI api                 = new AllopassAPI();
     * OnetimePricingResponse response = (OnetimePricingResponse)api.getOnetimeDiscretePricing(parameters);
     *
     * System.out.println(response.getWebsite().getName());
     *
     *	for (int i = 0 ; i < response.getCountries().size() ; i++) {
     *      AllopassCountry country = (AllopassCountry) response.getCountries().get(i);
     *      System.out.println(country.getCode() + " - " + country.getName());
     *	}
     * @endcode
     */
    public ApiResponse getOnetimeDiscretePricing(TreeMap parameters)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   ApiRemoteErrorException,
                   ApiUnavailableResourceException,
                   Exception {
        return this.getOnetimeDiscretePricing(parameters, true);
    }

    /**
     * Method performing a onetime discrete pricing request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     * @param mapping (bool) Should the response be an object mapping or a plain response
     *
     * @return (ApiResponse) The API call response
     * Will be a OnetimePricingResponse instance if mapping is true, an ApiPlainResponse if not
     */
    public ApiResponse getOnetimeDiscretePricing(TreeMap parameters, boolean mapping)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   ApiRemoteErrorException,
                   ApiUnavailableResourceException,
                   Exception {
        OnetimeDiscretePricingRequest request = new OnetimeDiscretePricingRequest(parameters, mapping);
        return request.call();
    }

    /**
     * Method performing a onetime validate codes request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     *
     * @return (ApiResponse) The API call response
     * Will be a OnetimeValidateCodesResponse instance
     *
     * @code
     *  TreeMap parameters = new TreeMap();
     *	parameters.put("site_id", "127042");
     *	parameters.put("product_id", "354926");
     *
     *	TreeMap mapCode = new TreeMap();
     *	mapCode.put("0", "49R7U894");
     *	parameters.put("code", mapCode);
     *
     *	AllopassAPI api                       = new AllopassAPI();
     *	OnetimeValidateCodesResponse response = (OnetimeValidateCodesResponse)api.validateCodes(parameters);
     *
     *	System.out.println(response.getStatus());
     *	System.out.println(response.getStatusDescription());
     * @endcode
     */
    public ApiResponse validateCodes(TreeMap parameters)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        return this.validateCodes(parameters, true);
    }

    /**
     * Method performing a onetime validate codes request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     * @param mapping (bool) Should the response be an object mapping or a plain response
     *
     * @return (ApiResponse) The API call response
     * Will be a OnetimeValidateCodesResponse instance if mapping is true, an ApiPlainResponse if not
     */
    public ApiResponse validateCodes(TreeMap parameters, boolean mapping)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        OnetimeValidateCodesRequest request = new OnetimeValidateCodesRequest(parameters, mapping);
        return request.call();
    }

    /**
     * Method performing a product detail request
     *
     * @param id (integer) The product id
     *
     * @return (ApiResponse) The API call response
     * Will be a ProductDetailResponse instance
     *
     * @code
     * AllopassAPI api = new AllopassAPI();
     * ProductDetailResponse response = (ProductDetailResponse)api.getProduct(354926);
     * System.out.println(response.getName());
     * @endcode
     */
    public ApiResponse getProduct(int id)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        return this.getProduct(id, new TreeMap(), true);
    }

    /**
     * Method performing a product detail request
     *
     * @param id (integer) The product id
     * @param parameters (TreeMap) Query string parameters of the API call
     *
     * @return (ApiResponse) The API call response
     * Will be a ProductDetailResponse instance
     */
    public ApiResponse getProduct(int id, TreeMap parameters)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        return this.getProduct(id, parameters, true);
    }

    /**
     * Method performing a product detail request
     *
     * @param id (integer) The product id
     * @param parameters (TreeMap) Query string parameters of the API call
     * @param mapping (bool) Should the response be an object mapping or a plain response
     *
     * @return (ApiResponse) The API call response
     * Will be a ProductDetailResponse instance if mapping is true, an ApiPlainResponse if not
     */
    public ApiResponse getProduct(int id, TreeMap parameters, boolean mapping)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        parameters.put("id", id);
        ProductDetailRequest request = new ProductDetailRequest(parameters, mapping);
        return request.call();
    }

    /**
     * Method performing a transaction prepare request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     *
     * @return (ApiResponse) The API call response
     * Will be a TransactionPrepareResponse instance
     *
     * @code
     * 	TreeMap parameters = new TreeMap();
     * 	parameters.put("site_id", "127042");
     * 	parameters.put("pricepoint_id", "2");;
     *	parameters.put("product_name", "premium calling product");
     *	parameters.put("forward_url", "http://product-page.com");
     *
     *	AllopassAPI api = new AllopassAPI();
     *	TransactionPrepareResponse response = (TransactionPrepareResponse)api.prepareTransaction(parameters);
     *  System.out.println(response.getBuyUrl());
     *	System.out.println(response.getCheckoutButton());
     * @endcode
     */
    public ApiResponse prepareTransaction(TreeMap parameters)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        return this.prepareTransaction(parameters, true);
    }
    /**
     * Method performing a transaction prepare request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     * @param mapping (bool) Should the response be an object mapping or a plain response
     *
     * @return (ApiResponse) The API call response
     * Will be a TransactionPrepareResponse instance if mapping is true, an ApiPlainResponse if not
     */
    public ApiResponse prepareTransaction(TreeMap parameters, boolean mapping)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        TransactionPrepareRequest request = new TransactionPrepareRequest(parameters, mapping);
        return request.call();
    }

   /**
     * Method performing a transaction detail request based on the transaction id
     *
     * @param id (string) The transaction id
     *
     * @return (TransactionDetailResponse) The API call response
     * Will be a TransactionDetailResponse instance if mapping is true, an ApiPlainResponse if not
     *
     * @code
     *	AllopassAPI api = new AllopassAPI();
     *	TransactionDetailResponse myResponse = (TransactionDetailResponse)api.getTransaction("c2d0f0db-2374-48a2-96b1-2064ed27b689");
     *	System.out.println(myResponse.getPaid().getCurrency());
     *	System.out.println(myResponse.getPaid().getAmount());
     * @endcode
     */
    public ApiResponse getTransaction(String id)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        return this.getTransaction(id, new TreeMap(), true);
    }

    /**
     * Method performing a transaction detail request based on the transaction id
     *
     * @param id (string) The transaction id
     * @param parameters (TreeMap) Query string parameters of the API call
     *
     * @return (TransactionDetailResponse) The API call response
     * Will be a TransactionDetailResponse instance
     */
    public ApiResponse getTransaction(String id, TreeMap parameters)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        return this.getTransaction(id, parameters, true);
    }

    /**
     * Method performing a transaction detail request based on the transaction id
     *
     * @param id (string) The transaction id
     * @param parameters (TreeMap) Query string parameters of the API call
     * @param mapping (bool) Should the response be an object mapping or a plain response
     *
     * @return (TransactionDetailResponse) The API call response
     * Will be a TransactionDetailResponse instance if mapping is true, an ApiPlainResponse if not
     */
    public ApiResponse getTransaction(String id, TreeMap parameters, boolean mapping)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        parameters.put("id", id);
        TransactionDetailRequest request = new TransactionDetailRequest(parameters, mapping);
        return request.call();
    }

    /**
     * Method performing a transaction detail request based on the merchant transaction id
     *
     * @param id (string) The merchant transaction id
     *
     * @return (AllopassApiResponse) The API call response
     * Will be a TransactionDetailResponse instance
     *
     * @code
     *  AllopassAPI api = new AllopassAPI();
     *  TransactionDetailResponse myResponse = (TransactionDetailResponse)api.getTransactionMerchant("TRXPT100098");
     *	System.out.println(myResponse.getPaid().getCurrency());
     *	System.out.println(myResponse.getPaid().getAmount());
     * @endcode
     */
    public ApiResponse getTransactionMerchant(String id)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        return this.getTransactionMerchant(id, new TreeMap(), true);
    }

    /**
     * Method performing a transaction detail request based on the merchant transaction id
     *
     * @param id (string) The merchant transaction id
     * @param parameters (TreeMap) Query string parameters of the API call
     *
     * @return (AllopassApiResponse) The API call response
     * Will be a TransactionDetailResponse instance
     */
    public ApiResponse getTransactionMerchant(String id, TreeMap parameters)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        return this.getTransaction(id, parameters, true);
    }

    /**
     * Method performing a transaction detail request based on the merchant transaction id
     *
     * @param id (string) The merchant transaction id
     * @param parameters (TreeMap) Query string parameters of the API call
     * @param mapping (bool) Should the response be an object mapping or a plain response
     *
     * @return (AllopassApiResponse) The API call response
     * Will be a TransactionDetailResponse instance if mapping is true, an ApiPlainResponse if not
     */
    public ApiResponse getTransactionMerchant(String id, TreeMap parameters, boolean mapping)
            throws ApiUnavailableResourceException,
                   ApiMissingHashFeatureException,
                   ApiFalseResponseSignatureException,
                   Exception {
        parameters.put("id", id);
        TransactionMerchantRequest request = new TransactionMerchantRequest(parameters, mapping);
        return request.call();
    }

    /**
     * Method performing a onetime button request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     * @param mapping (boolean) Should the response be an object mapping or a plain response
     *
     * @return (AllopassApiResponse) The API call response
     * Will be a AllopassOnetimeButtonResponse instance if mapping is true, an AllopassApiPlainResponse if not
     *
     * @code
     * AllopassAPI api = new AllopassApi();
     * parameters.put("site_id", 127042);
     * parameters.put("product_name", "TEST ACTE");
     * parameters.put("forward_url",  "http://google.com");
     * parameters.put("reference_currency", "EUR");
     * parameters.put("price_mode", "price");
     * parameters.put("price_policy", "nearest");
     * parameters.put("amount", "3");
     * OnetimeButtonResponse response = (OnetimeButtonResponse)api->createButton(parameters);
     * System.out.println(response->getButtonId());
     * System.out.println(response->getBuyUrl());
     * @endcode
     */
    public ApiResponse createButton(TreeMap parameters, boolean mapping)
            throws ApiMissingHashFeatureException,
                   ApiUnavailableResourceException,
                   Exception {
        OnetimeButtonRequest request = new OnetimeButtonRequest(parameters, mapping);
        return request.call();
    }

    /**
     * Method performing a onetime discrete button request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     * @param mapping (boolean) Should the response be an object mapping or a plain response
     *
     * @return (AllopassApiResponse) The API call response
     * Will be a AllopassOnetimeButtonResponse instance if mapping is true, an AllopassApiPlainResponse if not
     *
     * @code
     * AllopassAPI api = new AllopassAPI();
     *
     * parameters.put("site_id", 127042);
     * parameters.put("product_name", "TEST ACTE");
     * parameters.put("forward_url",  "http://google.com");
     * parameters.put("reference_currency", "EUR");
     * parameters.put("price_mode", "price");
     * parameters.put("price_policy", "nearest");
     * parameters.put("amount", "3");
     * OnetimeButtonResponse response = (OnetimeButtonResponse)api->createDiscreteButton(parameters, true);
     * System.out.println(response->getButtonId());
     * System.out.println(response->getBuyUrl());
     * @endcode
     */
    public ApiResponse createDiscreteButton(TreeMap parameters, boolean mapping)
            throws ApiMissingHashFeatureException,
                   ApiUnavailableResourceException,
                   Exception {
        OnetimeDiscreteButtonRequest request = new OnetimeDiscreteButtonRequest(parameters, mapping);
        return request.call();
    }
     /**
     * Method performing a onetime button request
     *
     * @param parameters (TreeMap) Query string parameters of the API call
     *
     * @return (AllopassApiResponse) The API call response
     * Will be a AllopassOnetimeButtonResponse instance if mapping is true, an AllopassApiPlainResponse if not
     *
     */
    public ApiResponse createButton(TreeMap parameters)
            throws ApiMissingHashFeatureException,
                   ApiUnavailableResourceException,
                   Exception {
        OnetimeButtonRequest request = new OnetimeButtonRequest(parameters, true);
        return request.call();
    }

    /**
     * Method performing a onetime discrete button request
     *
     * @param parameters (array) Query string parameters of the API call
     *
     * @return (AllopassApiResponse) The API call response
     * Will be a AllopassOnetimeButtonResponse instance if mapping is true, an AllopassApiPlainResponse if not
     */
    public ApiResponse createDiscreteButton(TreeMap parameters)
            throws ApiMissingHashFeatureException,
                   ApiUnavailableResourceException,
                   Exception {
        OnetimeDiscreteButtonRequest request = new OnetimeDiscreteButtonRequest(parameters, true);
        return request.call();
    }
}