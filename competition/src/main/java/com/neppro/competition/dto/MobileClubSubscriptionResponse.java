package com.neppro.competition.dto;

public class MobileClubSubscriptionResponse {
	private String subscriptionId;
	private String msisdn;
	private String mobileClub;
	private boolean active;
	private String createdDate;
		
	public String getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getMobileClub() {
		return mobileClub;
	}
	public void setMobileClub(String mobileClub) {
		this.mobileClub = mobileClub;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	

}
