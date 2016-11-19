package com.neppro.competition.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;

@Entity
@Table(name="question_pack")
@NamedEntityGraphs({
	@NamedEntityGraph(name = "questionPackQuestions", 
			attributeNodes = @NamedAttributeNode("questions")),
	@NamedEntityGraph(name = "questionPackCompetitions", 
			attributeNodes = @NamedAttributeNode("competitions"))
})
public class QuestionPack implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="language")
	private String language;
	
	@Column(name="created")
	private Timestamp created;
	
	@ManyToMany(mappedBy = "questionPacks")
	private Set<Question> questions;
	
	@ManyToMany(mappedBy = "questionPacks")
	private Set<Competition> competitions;


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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Set<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(Set<Question> questions) {
		this.questions = questions;
	}

	public Set<Competition> getCompetitions() {
		return competitions;
	}

	public void setCompetitions(Set<Competition> competitions) {
		this.competitions = competitions;
	}	
	
}
