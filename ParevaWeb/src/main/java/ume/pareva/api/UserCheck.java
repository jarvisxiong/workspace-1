/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.api;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam; //

/**
 *
 * @author madan
 */
@WebService(serviceName = "UserCheck")
public class UserCheck {

    /**
     * This is a sample web service operation
     */
    
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
}
