package ume.pareva.ire;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MsisdnPassingResponse {
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	@JsonProperty("error")
	private String error;
	
	@JsonProperty("message")
	private String message;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    @Override
    public String toString() {
        return "IE Msisdn Parsing{" + "msisdn=" + msisdn + ", error=" + error + ", message=" + message + '}';
    }
	
	
		

}
