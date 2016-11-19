package com.neppro.competition.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MobileClubSubscriberId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="id")
	private String userId;
	
	@Column(name="mobile_club_id")
	private String mobileClubId;

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getMobileClubId() {
		return mobileClubId;
	}


	public void setMobileClubId(String mobileClubId) {
		this.mobileClubId = mobileClubId;
	}

	
}
