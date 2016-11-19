package ume.pareva.uk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SendMessageResponse {
	
	@JsonProperty("Fault")
	private Fault fault;
	
	@JsonProperty("TransactionId")
	private int transactionId;
	
	@JsonProperty("Status")
	private boolean status;

	public Fault getFault() {
		return fault;
	}

	public void setFault(Fault fault) {
		this.fault = fault;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
