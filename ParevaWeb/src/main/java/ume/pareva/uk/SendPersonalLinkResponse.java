package ume.pareva.uk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SendPersonalLinkResponse {
	
	@JsonProperty("Fault")
	private Fault fault;
	
	@JsonProperty("MessageSent")
	private String messageSent;
	
	public Fault getFault() {
		return fault;
	}
	public void setFault(Fault fault) {
		this.fault = fault;
	}
	public String getMessageSent() {
		return messageSent;
	}
	public void setMessageSent(String messageSent) {
		this.messageSent = messageSent;
	}
		

}
