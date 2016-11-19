package ume.pareva.ire;

public class HLRLookupResponse {
	private String hlrLookupType;
	private String network;
	private String msisdn;
	private String errorText;
	public String getHlrLookupType() {
		return hlrLookupType;
	}
	public void setHlrLookupType(String hlrLookupType) {
		this.hlrLookupType = hlrLookupType;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getErrorText() {
		return errorText;
	}
	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

}
