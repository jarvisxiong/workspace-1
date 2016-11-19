package com.neppro.localbus.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class APIResponse<T> {
	private int errorCode=0;
	private String errorDescription="";
	List<T> T;
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
	public List<T> getT() {
		return T;
	}
	public void setT(List<T> t) {
		T = t;
	}	
}
