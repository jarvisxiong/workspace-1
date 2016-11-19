/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.ire;

/**
 *
 * @author madan
 */
public class IREConnConstants {
  
    private static final String EKEY= "a6815e707c675f7a3f307656d462bca6";

   // private static final String DOMAIN_HTTPS = "https://http.au.oxygen8.com/dob/";
    private static final String DOMAIN_HTTP  = "http://client.txtnation.com/gateway.php";


//    protected static String getDomainHttps() {
//        return DOMAIN_HTTPS;
//    }

    public static String getDomainHttp() {
        return DOMAIN_HTTP;
    }
    
     public static String getEKey() {
        return EKEY;
    }
    
}
