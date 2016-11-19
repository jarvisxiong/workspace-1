package ume.pareva.uk;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Fault {
	
	@JsonProperty("Code")
	private String code;
	
	@JsonProperty("Message")
	private String message;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

    @Override
    public String toString() {
        return "Fault{" + "code=" + code + ", message=" + message + '}';
    }
	
        
}
