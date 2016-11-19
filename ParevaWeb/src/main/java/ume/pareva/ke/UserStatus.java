package ume.pareva.ke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.pojo.UmeUser;

@Component("userStatus")
public class UserStatus { //
	
	@Autowired
	MobileClubDao mobileclubdao;
	
	public boolean checkIfUserActiveAndNotSuspended(UmeUser user,MobileClub club){
		boolean userStatus=false;
		if(user!=null){
			userStatus=club!=null
					&& mobileclubdao.isActive(user, club)
					&& club.getOptIn() > 0 ;	

					if (user != null && user.getAccountType() == 99) { // User account is blocked/barred so don't DOI him
						userStatus = false;
					}
		}

		return userStatus;
	}	

}
