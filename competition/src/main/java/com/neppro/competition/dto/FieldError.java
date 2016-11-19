package com.neppro.competition.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FieldError {
	private String field;
	private String message;
	
	public FieldError() {}
	
	public FieldError(String field, String message) {
		super();
		this.field = field;
		this.message = message;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
