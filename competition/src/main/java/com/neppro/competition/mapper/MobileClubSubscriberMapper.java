package com.neppro.competition.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.neppro.competition.dto.MobileClubSubscriptionRequest;
import com.neppro.competition.dto.MobileClubSubscriptionResponse;
import com.neppro.competition.model.MobileClubSubscriber;

@Mapper(uses={MsisdnToUserMapper.class,MobileClubIdToMobileClubMapper.class},componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MobileClubSubscriberMapper {
	private MsisdnToUserMapper msisdnToUserMapper;
	private MobileClubIdToMobileClubMapper mobileClubIdToMobileClubMapper;
	
	public MsisdnToUserMapper getMsisdnToUserMapper() {
		return msisdnToUserMapper;
	}

	@Autowired
	public void setMsisdnToUserMapper(MsisdnToUserMapper msisdnToUserMapper) {
		this.msisdnToUserMapper = msisdnToUserMapper;
	}

	public MobileClubIdToMobileClubMapper getMobileClubIdToMobileClubMapper() {
		return mobileClubIdToMobileClubMapper;
	}

	@Autowired
	public void setMobileClubIdToMobileClubMapper(
			MobileClubIdToMobileClubMapper mobileClubIdToMobileClubMapper) {
		this.mobileClubIdToMobileClubMapper = mobileClubIdToMobileClubMapper;
	}

	@Mappings({
		@Mapping(source = "id", target= "subscriptionId"),
		@Mapping(source = "user.msisdn", target = "msisdn"),
		@Mapping(source = "mobileClub.id", target = "mobileClub"),
		@Mapping(source = "active", target = "active"),
		@Mapping(source="createdDate", dateFormat = "yyyy-MM-dd hh:mm:ss", target="createdDate")
	})
	public abstract MobileClubSubscriptionResponse mobileClubSubscriberToMobileClubSubscriptionResponse(MobileClubSubscriber mobileClubSubscriber);

	@Mappings({
		@Mapping(target = "id", expression = "java(String.valueOf(new java.util.Date().getTime()))"),
		@Mapping(source = "msisdn", target = "user"),
		@Mapping(source = "mobileClub", target = "mobileClub"),
		@Mapping(target = "active", expression = "java(new java.lang.Boolean(false))"),
		@Mapping(target = "createdDate", expression = "java(new java.sql.Timestamp(new java.util.Date().getTime()))")
	})
	public abstract MobileClubSubscriber mobileClubSubscriptionRequestToMobileClubSubscriber(MobileClubSubscriptionRequest mobileClubSubscriptionRequest);
	/*public MobileClubSubscriber mobileClubSubscriberDTOToMobileClubSubscriber(MobileClubSubscriberDTO mobileClubSubscriberDTO){
		MobileClubSubscriber mobileClubSubscriber=new MobileClubSubscriber();
		MobileClubSubscriberId mobileClubSubscriberId=new MobileClubSubscriberId();
		MobileClub mobileClub=mobileClubIdToMobileClubMapper.MobileClubIdToMobileClub(mobileClubSubscriberDTO.getMobileClub());
		if(mobileClub==null){
			throw new NotFoundException("Mobile Club with ID: "+mobileClubSubscriberDTO.getMobileClub()+" Not Found");
		}
		User user=msisdnToUserMapper.msisdnToUser(mobileClubSubscriberDTO.getMsisdn());
		if(user==null){
			throw new NotFoundException("User with MSISDN: "+mobileClubSubscriberDTO.getMsisdn()+" Not Found");
		}
		mobileClubSubscriber.setMobileClub(mobileClub);
		mobileClubSubscriber.setUser(user);
		mobileClubSubscriber.setActive(false);
		mobileClubSubscriber.setCreated(new Timestamp(new Date().getTime()));
		mobileClubSubscriberId.setMobileClubId(mobileClubSubscriberDTO.getMobileClub());
		mobileClubSubscriberId.setUserId(user.getId());
		mobileClubSubscriber.setMobileClubSubscriberId(mobileClubSubscriberId);
		return mobileClubSubscriber;
		
	}*/
}
