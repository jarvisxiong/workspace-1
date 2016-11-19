package com.neppro.competition.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.hash.SimpleHashRequest;
import org.apache.shiro.util.ByteSource;


public class SaltedCredentialsMatcher implements CredentialsMatcher {

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token,
			AuthenticationInfo info) {
		// TODO Auto-generated method stub
		
		System.out.println("Inside SaltedCredentialsMatcher");
		UsernamePasswordToken usernamePasswordToken=(UsernamePasswordToken)token;
		
		ByteSource suppliedPassword = ByteSource.Util.bytes(usernamePasswordToken.getPassword());
		System.out.println("Supplied Password: "+suppliedPassword); 
		 
		ByteSource baseSalt = ByteSource.Util.bytes("basesalt");
		  int iterations = 10;
		  DefaultHashService hasher = new DefaultHashService();
		  hasher.setPrivateSalt(baseSalt);
		 // hasher.setHashIterations(iterations);
		//  hasher.setHashAlgorithmName(Sha256Hash.ALGORITHM_NAME);
		   
		  //custom public salt
		 // byte[] publicSalt = {1, 3, 5, 7, 9};
		  ByteSource salt = ((SaltedAuthenticationInfo) info).getCredentialsSalt();
			System.out.println("Salt: "+salt);	  
				 
		   
		  //use hasher to compute password hash
		  HashRequest request = new SimpleHashRequest(Sha256Hash.ALGORITHM_NAME,suppliedPassword, salt,iterations);
		  Hash response = hasher.computeHash(request);
		  if(response.toHex().equals(info.getCredentials()))
			  return true;
		  else{
			  return false; 
		  }
		
/*		  System.out.println(response.getSalt());
		  System.out.println(response.toHex());
		
		
		return false;
*/	}

}
