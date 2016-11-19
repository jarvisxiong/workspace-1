package com.neppro.competition.service;

import com.neppro.competition.dto.MobileClubSubscriptionResponse;
import com.neppro.competition.dto.MobileClubSubscriptionRequest;

public interface MobileClubSubscriberService {
	public MobileClubSubscriptionResponse createSubscription(MobileClubSubscriptionRequest mobileClubSubscriptionRequest);

}
