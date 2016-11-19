package com.neppro.competition.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.neppro.competition.dao.MobileClubDao;
import com.neppro.competition.model.MobileClub;

@Component("mobileClubIdToMobileClubMapper")
public class MobileClubIdToMobileClubMapper {

	private MobileClubDao mobileClubDao;
	
	public MobileClubDao getMobileClubDao() {
		return mobileClubDao;
	}
	
	@Autowired
	public void setMobileClubDao(MobileClubDao mobileClubDao) {
		this.mobileClubDao = mobileClubDao;
	}
	
	public MobileClub MobileClubIdToMobileClub(String mobileClubId){
		MobileClub mobileClub=null;
		mobileClub=mobileClubDao.findOne(mobileClubId);
		return mobileClub;
	}
}
