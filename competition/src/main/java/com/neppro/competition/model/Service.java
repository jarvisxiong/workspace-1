package com.neppro.competition.model;

import java.io.Serializable;
import java.sql.Timestamp;
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
@Table(name="service")
@NamedEntityGraphs({
	@NamedEntityGraph(name = "serviceMobileClubs", 
			attributeNodes = @NamedAttributeNode("mobileClubs"))
})
public class Service implements Serializable{

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
    
	@Column(name = "created")
	private Timestamp created;

	@OneToMany(mappedBy="service",cascade=CascadeType.ALL)
    private Set<MobileClub> mobileClubs;
	
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

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Set<MobileClub> getMobileClubs() {
		return mobileClubs;
	}

	public void setMobileClubs(Set<MobileClub> mobileClubs) {
		this.mobileClubs = mobileClubs;
	}   
}
