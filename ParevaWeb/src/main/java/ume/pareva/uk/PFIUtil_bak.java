package ume.pareva.uk;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;

@Component("pfiUtil_bak")
public class PFIUtil_bak {
	
	private static final String sendPersonalLinkUrl="http://pfi.imimobile.net/api/PersonalLink.svc/rest/SendAjaxPersonalLink";
	private static final String hlrLookupUrl="http://pfi.imimobile.net/msisdnlookup/web/lookup";
	//private static final String hlrLookupCarrierUrl="http://pfi.imimobile.net/msisdnlookup/web/carrier";
	private static final String destinationUrl="http://uk.quiz2win.mobi/?cid=123456KDS";
	private static final String spoof="Voucher";
	private static final String merchantId="81";//81";79
	private static final String merchantToken="FBEEBAC4-9F42-4DC4-8E5D-B481A30986B0" ;//FBEEBAC4-9F42-4DC4-8E5D-B481A30986B0";// 7F34747B-EAD2-41E7-8BE0-1C2F04B878DF
	
	private RestTemplate getRestTemplate(){
		RestTemplate restTemplate=new RestTemplate();
		restTemplate.setRequestFactory(getClientRequestFactory());
		return restTemplate;
	}
	
	public SendPersonalLinkResponse sendPersonalLink(String msisdn){
		String message="Please click to confirm your request: {url}";
		String jsonInputData=prepareJsonInput(msisdn,destinationUrl,message,spoof);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + getAuthorizationData(merchantId,merchantToken));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(jsonInputData,headers);
		ResponseEntity<SendPersonalLinkResponse> sendPersonalLinkResponse = getRestTemplate().postForEntity(sendPersonalLinkUrl,entity, SendPersonalLinkResponse.class);
		return sendPersonalLinkResponse.getBody();
	}
        
        //-===============Used in the System =====================
        public SendPersonalLinkResponse sendPersonalLink(String msisdn, MobileClub partnerClub, String message, String destinationurl){
            UmeClubDetails userClubDetails=null; 
            if(partnerClub!=null){
                userClubDetails=UmeTempCmsCache.umeClubDetailsMap.get(partnerClub.getUnique());
            }
            if(partnerClub==null || userClubDetails==null) return null;
		//String message="personal link test {url}";
		String jsonInputData=prepareJsonInput(msisdn,destinationurl,message,userClubDetails.getClubSpoof());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + getAuthorizationData(merchantId,merchantToken));//partnerClub.getSmsExt()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(jsonInputData,headers);
		ResponseEntity<SendPersonalLinkResponse> sendPersonalLinkResponse = getRestTemplate().postForEntity(sendPersonalLinkUrl,entity, SendPersonalLinkResponse.class);
		return sendPersonalLinkResponse.getBody();
	}
	
	public HLRLookupResponse doHLRLookup(String msisdn){
		String sessionToken=UUID.randomUUID().toString();
		String url=hlrLookupUrl+"?sessionToken="+sessionToken+"&merchantToken="+merchantToken+"&msisdn="+msisdn;
	//	HttpHeaders headers = new HttpHeaders();
	//	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	//	HttpEntity<String> entity = new HttpEntity<String>(hlrLookupData,headers);
		ResponseEntity<String> hlrLookupResponse = getRestTemplate().getForEntity(url,String.class);
		return createHLRLookupResponse(hlrLookupResponse.getBody());
			
	}
        
        // ====================== Used in the System ============================
        
        public HLRLookupResponse doHLRLookup(String msisdn,String sessionToken,MobileClub partnerclub){
		//String sessionToken=UUID.randomUUID().toString();
		//String hlrLookupData="sessionToken="+sessionToken+"&merchantToken="+partnerclub.getSmsExt()+"&msisdn="+msisdn;
            String hlrLookupData="sessionToken="+sessionToken+"&merchantToken="+merchantToken+"&msisdn="+msisdn;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<String> entity = new HttpEntity<String>(hlrLookupData,headers);
		ResponseEntity<String> hlrLookupResponse = getRestTemplate().postForEntity(hlrLookupUrl,entity,String.class);
		return createHLRLookupResponse(hlrLookupResponse.getBody());
			
	}
	
	
	public String prepareJsonInput(String msisdn,String destinationUrl,String message,String spoof){
		String sessionToken=UUID.randomUUID().toString();
		return "{"
				+ "\"SessionToken\": \""+sessionToken+"\","
				+ "\"MSISDN\": "+msisdn+","
				+ "\"DestinationUrl\": \""+destinationUrl+"\","
				+ "\"Message\": \""+message+"\","
				+ "\"Spoof\": \""+spoof+"\""
				+ "}";
	}
	
	public String getAuthorizationData(String merchantId,String merchantToken){
		String plainCreds = merchantId+":"+merchantToken;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		return base64Creds;
	}
	
	private HttpComponentsClientHttpRequestFactory getClientRequestFactory(){
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory=new HttpComponentsClientHttpRequestFactory();
		HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		httpComponentsClientHttpRequestFactory.setConnectTimeout(5000);
		httpComponentsClientHttpRequestFactory.setReadTimeout(5000);
		httpComponentsClientHttpRequestFactory.setHttpClient(httpClient);
		return httpComponentsClientHttpRequestFactory;
		
	}
	
	public HLRLookupResponse createHLRLookupResponse(String response){
		HLRLookupResponse hlrLookupResponse=new HLRLookupResponse();
		if(response.contains("|")){
			String[] hlrLookupResponseArray=response.split("\\|");
			hlrLookupResponse.setHlrLookupType(hlrLookupResponseArray[0]);
			hlrLookupResponse.setNetwork(hlrLookupResponseArray[1]);
			hlrLookupResponse.setMsisdn(hlrLookupResponseArray[2]);
			hlrLookupResponse.setErrorText("");
		}else{
			hlrLookupResponse.setErrorText(response);
		}
		return hlrLookupResponse;
	}
	
	public static void main(String[] arg){
		PFIUtil_bak pfiUtil=new PFIUtil_bak();
		////////////////////////////////////////HLR LOOKUP/////////////////////////////////////////////////////////////
		//447778780454
		HLRLookupResponse hlrLookupResponse=pfiUtil.doHLRLookup("447427626522");
		System.out.println(hlrLookupResponse.getNetwork());
		System.out.println(hlrLookupResponse.getHlrLookupType());
		System.out.println(hlrLookupResponse.getMsisdn());
		System.out.println(hlrLookupResponse.getErrorText());
		
		////////////////////////////////////////SEND PERSONAL LINK/// Angel- 447778780454, dongol- 447496553139

///////////////////////////////////////////////////

		SendPersonalLinkResponse sendPersonalLinkResponse=pfiUtil.sendPersonalLink("447427626522");
		System.out.println(sendPersonalLinkResponse.getMessageSent());
		System.out.println(sendPersonalLinkResponse.getFault());

	}


}
