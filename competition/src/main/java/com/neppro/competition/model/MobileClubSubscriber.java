package com.neppro.competition.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="mobile_club_subscriber")
@NamedEntityGraphs({
	@NamedEntityGraph(name = "mobileClubSubscriberDetails", 
			attributeNodes = @NamedAttributeNode("user")),
	@NamedEntityGraph(name = "mobileClubDetails", 
            attributeNodes = @NamedAttributeNode(value = "mobileClub", subgraph = "subscribedCompetition"), 
            subgraphs = @NamedSubgraph(name = "subscribedCompetition", attributeNodes = @NamedAttributeNode("competition")))
})
public class MobileClubSubscriber implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*@EmbeddedId
	private MobileClubSubscriberId mobileClubSubscriberId;*/
	
	@Id
	@Column(name = "id")
	private String id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id", updatable = false)
	private User user;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="mobile_club_id", updatable = false)
	private MobileClub mobileClub;
	
	@Type(type="yes_no")
	@Column(name="active")
	private Boolean active;
	
	@Column(name="created")
	private Timestamp createdDate;

	/*public MobileClubSubscriberId getMobileClubSubscriberId() {
		return mobileClubSubscriberId;
	}

	public void setMobileClubSubscriberId(
			MobileClubSubscriberId mobileClubSubscriberId) {
		this.mobileClubSubscriberId = mobileClubSubscriberId;
	}*/

	public Boolean getActive() {
		return active;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}


	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public MobileClub getMobileClub() {
		return mobileClub;
	}

	public void setMobileClub(MobileClub mobileClub) {
		this.mobileClub = mobileClub;
	}
	
	
}
