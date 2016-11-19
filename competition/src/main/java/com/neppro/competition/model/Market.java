package com.neppro.competition.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="market")
@NamedEntityGraphs({
	@NamedEntityGraph(name = "marketMobileClubs", 
			attributeNodes = @NamedAttributeNode("mobileClubs")),
	@NamedEntityGraph(name = "marketCompetitions", 
			attributeNodes = @NamedAttributeNode("competitions"))
})
public class Market implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@OneToMany(mappedBy="market",cascade=CascadeType.ALL)
    private Set<MobileClub> mobileClubs;
	
	@OneToMany(mappedBy="market",cascade=CascadeType.ALL)
    private Set<Competition> competitions;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<MobileClub> getMobileClubs() {
		return mobileClubs;
	}

	public void setMobileClubs(Set<MobileClub> mobileClubs) {
		this.mobileClubs = mobileClubs;
	}

	public Set<Competition> getCompetitions() {
		return competitions;
	}

	public void setCompetitions(Set<Competition> competitions) {
		this.competitions = competitions;
	}	
		
}
