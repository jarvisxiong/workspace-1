package com.neppro.competition.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Fault {
	
	private int errorCode=0;
	private String errorDescription="";

	public Fault(){
		
	}
	
	public Fault(int errorCode, String errorDescription) {
		super();
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

}
