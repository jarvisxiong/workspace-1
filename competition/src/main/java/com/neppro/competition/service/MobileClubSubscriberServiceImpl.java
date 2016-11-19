package com.neppro.competition.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neppro.competition.dao.MobileClubSubscriberDao;
import com.neppro.competition.dto.MobileClubSubscriptionResponse;
import com.neppro.competition.dto.MobileClubSubscriptionRequest;
import com.neppro.competition.exception.NotFoundException;
import com.neppro.competition.mapper.MobileClubSubscriberMapper;
import com.neppro.competition.model.MobileClubSubscriber;

@Service
public class MobileClubSubscriberServiceImpl implements MobileClubSubscriberService {
	private MobileClubSubscriberDao mobileClubSubscriberDao;
	private MobileClubSubscriberMapper mobileClubSubscriberMapper;
	
	public MobileClubSubscriberDao getMobileClubSubscriberDao() {
		return mobileClubSubscriberDao;
	}

	@Autowired
	public void setMobileClubSubscriberDao(
			MobileClubSubscriberDao mobileClubSubscriberDao) {
		this.mobileClubSubscriberDao = mobileClubSubscriberDao;
	}
	
	public MobileClubSubscriberMapper getMobileClubSubscriberMapper() {
		return mobileClubSubscriberMapper;
	}

	@Autowired
	public void setMobileClubSubscriberMapper(
			MobileClubSubscriberMapper mobileClubSubscriberMapper) {
		this.mobileClubSubscriberMapper = mobileClubSubscriberMapper;
	}

	@Transactional
	public MobileClubSubscriptionResponse createSubscription(
			MobileClubSubscriptionRequest mobileClubSubscriptionRequest) {
		MobileClubSubscriber mobileClubSubscriber=null;
		mobileClubSubscriber=mobileClubSubscriberMapper.mobileClubSubscriptionRequestToMobileClubSubscriber(mobileClubSubscriptionRequest);
		System.out.println(mobileClubSubscriber.getMobileClub().getName());
		if(mobileClubSubscriber.getMobileClub()==null){
			throw new NotFoundException("Mobile Club with ID: "+mobileClubSubscriptionRequest.getMobileClub()+" Not Found");
		}
		if(mobileClubSubscriber.getUser()==null){
			throw new NotFoundException("User with MSISDN: "+mobileClubSubscriptionRequest.getMsisdn()+" Not Found");
		}
		mobileClubSubscriber=mobileClubSubscriberDao.save(mobileClubSubscriber);
		return mobileClubSubscriberMapper.mobileClubSubscriberToMobileClubSubscriptionResponse(mobileClubSubscriber);
	}

	

}
