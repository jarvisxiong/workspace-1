package com.neppro.competition.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="competition")
@NamedEntityGraphs({
	@NamedEntityGraph(name = "competitionMarket", 
			attributeNodes = @NamedAttributeNode("market")),
	@NamedEntityGraph(name = "competitionMobileClub", 
			attributeNodes = @NamedAttributeNode("mobileClub")),
	@NamedEntityGraph(name = "competitionQuestionPacks", 
            attributeNodes = @NamedAttributeNode(value = "questionPacks", subgraph = "competitionQuestions"), 
            subgraphs = @NamedSubgraph(name = "competitionQuestions", attributeNodes = @NamedAttributeNode("questions")))
})
public class Competition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="period")
	private String period;
	
	@Type(type="yes_no")
	@Column(name="active")
	private boolean active;
	
	@Column(name="start_date")
	private Timestamp startDate;
	
	@Column(name="question_frequency")
	private int questionFrequency;
	
	@Column(name="prize")
	private String prize;
	
	@Column(name="created")
	private Timestamp created;
	
	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="market_id")
    private Market market;
	
	@ManyToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinTable(name = "question_pack_in_competition", 
    			joinColumns = @JoinColumn(name = "competition_id", referencedColumnName = "id"), 
    			inverseJoinColumns = @JoinColumn(name = "question_pack_id", referencedColumnName = "id"))
	private Set<QuestionPack> questionPacks;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="mobile_club_id")
	private MobileClub mobileClub;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public int getQuestionFrequency() {
		return questionFrequency;
	}

	public void setQuestionFrequency(int questionFrequency) {
		this.questionFrequency = questionFrequency;
	}

	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}

	public Set<QuestionPack> getQuestionPacks() {
		return questionPacks;
	}

	public void setQuestionPacks(Set<QuestionPack> questionPacks) {
		this.questionPacks = questionPacks;
	}

	public MobileClub getMobileClub() {
		return mobileClub;
	}

	public void setMobileClub(MobileClub mobileClub) {
		this.mobileClub = mobileClub;
	}
	
	
	

}
