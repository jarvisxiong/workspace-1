/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.webservice;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author madan
 */
@WebService
public interface UserStopService {
    
    @WebMethod
    public boolean StopSingleSubscription(@WebParam(name = "login") String login,@WebParam(name = "pwd") String pwd,@WebParam(name = "msisdn") String msisdn,@WebParam(name = "clubunique") String clubUnique);
    
    @WebMethod
    public boolean StopAllSubscription(@WebParam(name = "login") String login,@WebParam(name = "pwd") String pwd,@WebParam(name = "msisdn") String msisdn);
    
    @WebMethod
    public boolean bulkStop(@WebParam(name = "login") String login,@WebParam(name = "pwd") String pwd, @WebParam(name = "msisdn") List<String> msisdn);
    
}
