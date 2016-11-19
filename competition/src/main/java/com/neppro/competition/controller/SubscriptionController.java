package com.neppro.competition.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.neppro.competition.dto.MobileClubSubscriptionResponse;
import com.neppro.competition.dto.MobileClubSubscriptionRequest;
import com.neppro.competition.mapper.MobileClubSubscriberMapper;
import com.neppro.competition.service.MobileClubSubscriberService;
import com.neppro.competition.service.UserService;

@RestController
public class SubscriptionController {
	
	private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);
	
	private MobileClubSubscriberService mobileClubSubscriberService;
	private MobileClubSubscriberMapper mobileClubSubscriberMapper;
	private UserService userService;
	
	public MobileClubSubscriberService getMobileClubSubscriberService() {
		return mobileClubSubscriberService;
	}

	@Autowired
	public void setMobileClubSubscriberService(
			MobileClubSubscriberService mobileClubSubscriberService) {
		this.mobileClubSubscriberService = mobileClubSubscriberService;
	}
	
	public MobileClubSubscriberMapper getMobileClubSubscriberMapper() {
		return mobileClubSubscriberMapper;
	}

	@Autowired
	public void setMobileClubSubscriberMapper(
			MobileClubSubscriberMapper mobileClubSubscriberMapper) {
		this.mobileClubSubscriberMapper = mobileClubSubscriberMapper;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(value = "/subscriptions", method = RequestMethod.POST)
	public ResponseEntity<?> subscriptions(@Valid @RequestBody MobileClubSubscriptionRequest mobileClubSubscriptionRequest){
		logger.error("subscription called");
		MobileClubSubscriptionResponse mobileClubSubscriptionResponse=null;
		if(userService.getUserByMsisdn(mobileClubSubscriptionRequest.getMsisdn())==null){
			userService.saveUser(mobileClubSubscriptionRequest.getMsisdn());
		}
		mobileClubSubscriptionResponse=mobileClubSubscriberService.createSubscription(mobileClubSubscriptionRequest);
		if(mobileClubSubscriptionResponse!=null)
			return new ResponseEntity<MobileClubSubscriptionResponse>(mobileClubSubscriptionResponse,HttpStatus.CREATED);
		else
			return new ResponseEntity<MobileClubSubscriptionResponse>(mobileClubSubscriptionResponse,HttpStatus.CREATED);
		
	}
}
