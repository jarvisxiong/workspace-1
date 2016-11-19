/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.au;

/**
 *
 * @author Alex
 */
public class AustraliaConnConstants {

    private static final String username = "umeremote";
    private static final String password = "4MVjq9MexG9nMkMz";

    private static final String DOMAIN_HTTPS = "https://http.au.oxygen8.com/dob/"; 
    private static final String DOMAIN_HTTP  = "http://http.au.oxygen8.com/dob/";

    protected static String getUserName() {
        return username;
    }

    protected static String getPassword() {
        return password;
    }

    protected static String getDomainHttps() {
        return DOMAIN_HTTPS;
    }

    protected static String getDomainHttp() {
        return DOMAIN_HTTP;
    }

}
