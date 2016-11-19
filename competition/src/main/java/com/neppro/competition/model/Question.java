package com.neppro.competition.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.Table;

@Entity
@Table(name="question")
@NamedEntityGraphs({
	@NamedEntityGraph(name = "questionsInPack", 
            attributeNodes = @NamedAttributeNode(value = "questionPacks", subgraph = "questionInCompetitions"), 
            subgraphs = @NamedSubgraph(name = "questionInCompetitions", attributeNodes = @NamedAttributeNode("competitions")))
})
public class Question implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "question")
	private String question;
	
	@Column(name = "option_a")
	private String optionA;
	
	@Column(name = "option_b")
	private String optionB;
	
	@Column(name = "correct_option")
	private String correctOption;
	
	@Column(name = "created")
	private Timestamp created;
	
	@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "question_in_question_pack", 
    			joinColumns = @JoinColumn(name = "question_id", referencedColumnName = "id"), 
    			inverseJoinColumns = @JoinColumn(name = "question_pack_id", referencedColumnName = "id"))
	private Set<QuestionPack> questionPacks;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getOptionA() {
		return optionA;
	}
	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}
	public String getOptionB() {
		return optionB;
	}
	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}
	public String getCorrectOption() {
		return correctOption;
	}
	public void setCorrectOption(String correctOption) {
		this.correctOption = correctOption;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public Set<QuestionPack> getQuestionPacks() {
		return questionPacks;
	}
	public void setQuestionPacks(Set<QuestionPack> questionPacks) {
		this.questionPacks = questionPacks;
	}
	
	

}
