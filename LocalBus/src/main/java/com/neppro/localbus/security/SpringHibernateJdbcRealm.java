package com.neppro.localbus.security;

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

import com.neppro.localbus.model.Role;
import com.neppro.localbus.model.User;
import com.neppro.localbus.service.RoleService;
import com.neppro.localbus.service.UserService;


public class SpringHibernateJdbcRealm extends AuthorizingRealm {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token){
		SimpleAuthenticationInfo info=null;
		System.out.println("Inside Authentication");
		UsernamePasswordToken usernamePasswordToken=(UsernamePasswordToken) token;
		System.out.println(usernamePasswordToken.getUsername());
		System.out.println(usernamePasswordToken.getPassword());
		User user=userService.getUser(usernamePasswordToken.getUsername());
		if(user!=null){
			info=new SimpleAuthenticationInfo(user.getUsername(),user.getPassword(),"HibernateJdbcRealm");
			info.setCredentialsSalt(ByteSource.Util.bytes(user.getPasswordSalt()));
		}
		return info;
	}
	
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection ){
		System.out.println("Inside Authorization"); 
        String currentUser = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Role userRole=roleService.getUserRole(currentUser);
        if(userRole!=null){
        	System.out.println(userRole.getRoleName());
        	System.out.println(userRole.getPermission());
        	info.addRole(userRole.getRoleName());
        	info.addStringPermission(userRole.getPermission());
        }
        return info;
	}
	
	

}
