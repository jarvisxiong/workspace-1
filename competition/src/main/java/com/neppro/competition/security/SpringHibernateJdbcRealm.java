package com.neppro.competition.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.neppro.competition.model.Privilege;
import com.neppro.competition.model.User;
import com.neppro.competition.service.UserService;


public class SpringHibernateJdbcRealm extends AuthorizingRealm {
	
	private UserService userService;
	
	@Autowired
	public SpringHibernateJdbcRealm(UserService userService){
		this.userService=userService;
	}
	
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token){
		SimpleAuthenticationInfo info=null;
		System.out.println("Inside Authentication");
		UsernamePasswordToken usernamePasswordToken=(UsernamePasswordToken) token;
		System.out.println(usernamePasswordToken.getUsername());
		System.out.println(usernamePasswordToken.getPassword());
		/*System.out.println(vehicleCategoryService.getAllVehicleCategories());
		System.out.println(roleService.getAllRoles());
		
		*/
		System.out.println("Username: "+usernamePasswordToken.getUsername());
		System.out.println(userService.getUser(usernamePasswordToken.getUsername()));
		User user=userService.getUser(usernamePasswordToken.getUsername());
		if(user!=null){
			info=new SimpleAuthenticationInfo(user.getUsername(),user.getPassword(),"HibernateJdbcRealm");
			info.setCredentialsSalt(ByteSource.Util.bytes(user.getPasswordSalt()));
		}
		System.out.println("Returning Authentication Info");
		return info;
	}
	
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection ){
		System.out.println("Inside Authorization"); 
        List<String> permissions=new ArrayList<String>();
		String currentUser = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        User user=userService.getUser(currentUser);
        Set<Privilege> userPrivileges=user.getPrivileges();
        if(userPrivileges!=null){
        	for(Privilege userPrivilege:userPrivileges){
        		permissions.add(userPrivilege.getDescription());
        	}
        	info.addStringPermissions(permissions);
        }
        return info;
	}
	
	

}
