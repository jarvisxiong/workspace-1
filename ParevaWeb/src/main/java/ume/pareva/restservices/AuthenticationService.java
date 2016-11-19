package ume.pareva.restservices;

import java.util.Set;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.snp.SnpUser;
import ume.pareva.snp.SnpUserDao;



@Component
public class AuthenticationService {
    
    @Autowired 
    SnpUserDao snpuserdao;
    
    
	public boolean authenticate(String authCredentials,final Set<String> rolesSet) {

		if (null == authCredentials)
			return false;
		// header value format will be "Basic encodedstring" for Basic
		// authentication. Example "Basic YWRtaW46YWRtaW4="
		final String encodedUserPassword = authCredentials.replaceFirst("Basic"	+ " ", "");
		String usernameAndPassword = null;
		try {
			byte[] decodedBytes = Base64.decodeBase64(encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
                        
              
                 
                  System.out.println("usernameAndPassword "+usernameAndPassword);
                  
		} catch (Exception e) {
			e.printStackTrace();
		}
//		final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
//		final String username = tokenizer.nextToken();
//		final String password = tokenizer.nextToken();
                
                final String username;
        final String password;
        {
            String[] rawTokens = usernameAndPassword.split(":");
            if (rawTokens.length != 2) {
        //invalid length!
                username = "";
                password = "";
            } else {
                username = (rawTokens[0] == null) ? "" : rawTokens[0];
                password = (rawTokens[1] == null) ? "" : rawTokens[1];
            }
        }

		// we have fixed the userid and password as admin
		// call some UserService/LDAP here
        
                SnpUser user= null;
                try{
                    System.out.println("restservice username and password "+username +" pwd "+password);
                    user=snpuserdao.authenticateUser(username, password);
                }catch(Exception e){System.out.println("restservice Exception "+e);e.printStackTrace();}
		boolean authenticationStatus =false;// "admin".equals(username)	&& "admin".equals(password);
                if(user!=null) {
                   authenticationStatus=isUserAllowed(user,rolesSet);
                }
		return authenticationStatus;
	}
        
        
        
        
    private boolean isUserAllowed(UmeUser user, final Set<String> rolesSet) {
        boolean isAllowed = false;
          
        //Step 1. Fetch password from database and match with password in argument
        //If both match then get the defined role for user from database and continue; else return isAllowed [false]
        //String userRole = userMgr.getUserRole(username);
         System.out.println("ISUSER ALLOWED METHOD CHECKS "+user.getFirstName()+" "+user.getEmail()+" "+user.getAdminGroup()+" roleset "+rolesSet);
        if(user.getAdminGroup()>=9) {
            String userRole = "ADMIN";
             
            //Step 2. Verify user role
            if(rolesSet.contains(userRole))
            {
                isAllowed = true;
            }
        }
        
        if(user.getAdminGroup()>=5 && user.getAdminGroup()<9) {
            String userRole = "MANAGER";
             
            //Step 2. Verify user role
            if(rolesSet.contains(userRole))
            {
                isAllowed = true;
            }
        }
        System.out.println("ISUSER ALLOWED METHOD CHECKS "+user.getFirstName()+" "+user.getEmail()+" "+user.getAdminGroup()+" isAloowed "+isAllowed);
        return isAllowed;
    }
    
    protected boolean ipwhitelist(String ip){
        
//        String defaultIP="127.0.0.1";
//        if(defaultIP.equals(ip)) return true;
//        else return false;
        
        return true;
        
    }
}