package ume.pareva.uk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
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

@Component("pfiUtil")
public class PFIUtil {
	
	private static final String sendPersonalLinkUrl="http://pfi.imimobile.net/api/PersonalLink.svc/rest/SendAjaxPersonalLink";
	private static final String hlrLookupUrl="http://pfi.imimobile.net/msisdnlookup/web/lookup";
	private static final String sendMessageUrl="http://pfi.imimobile.net/api/messaging.svc/rest/SendMessage";
	private static final String destinationUrl="http://uk.clubvoucher.co.uk/?cid=123456KDS";
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
		String jsonInputData=prepareJsonInput(msisdn,destinationUrl+"&pmob="+msisdn,message,spoof);
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
                System.out.println("uknewlogic inside PFI UTIL "+destinationurl+" msisdn "+msisdn);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + getAuthorizationData(merchantId,merchantToken));//partnerClub.getSmsExt()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(jsonInputData,headers);
		ResponseEntity<SendPersonalLinkResponse> sendPersonalLinkResponse = getRestTemplate().postForEntity(sendPersonalLinkUrl,entity, SendPersonalLinkResponse.class);
		return sendPersonalLinkResponse.getBody();
	}
	
	public HLRLookupResponse doHLRLookup(String msisdn){
		String sessionToken=UUID.randomUUID().toString();
		String hlrLookupData="sessionToken="+sessionToken+"&merchantToken="+merchantToken+"&msisdn="+msisdn;
                System.out.println("MANUAL HLRLookUP values "+hlrLookupUrl+" "+ hlrLookupData);
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//		HttpEntity<String> entity = new HttpEntity<String>(hlrLookupData,headers);
//		ResponseEntity<String> hlrLookupResponse = getRestTemplate().postForEntity(hlrLookupUrl,entity,String.class);
//		return createHLRLookupResponse(hlrLookupResponse.getBody());
                
                   String response="";
                try{
                response=sendGet(hlrLookupData);
                }catch(Exception e){}
		//return createHLRLookupResponse(hlrLookupResponse.getBody());
                return createHLRLookupResponse(response); //hlrLookupData);
			
	}
        
        // ====================== Used in the System ============================
        
        public HLRLookupResponse doHLRLookup(String msisdn,String sessionToken,MobileClub partnerclub){
		//String sessionToken=UUID.randomUUID().toString();
		//String hlrLookupData="sessionToken="+sessionToken+"&merchantToken="+partnerclub.getSmsExt()+"&msisdn="+msisdn;
            String hlrLookupData="sessionToken="+sessionToken+"&merchantToken="+merchantToken+"&msisdn="+msisdn;
            System.out.println("uknewlogic HLR LookUP parameters: "+hlrLookupData);
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//		HttpEntity<String> entity = new HttpEntity<String>(hlrLookupData,headers);
//                
//		ResponseEntity<String> hlrLookupResponse = getRestTemplate().postForEntity(hlrLookupUrl,entity,String.class);
//             
//		return createHLRLookupResponse(hlrLookupResponse.getBody());
//                //return createHLRLookupResponse(hlrLookupData);
            
                String response="";
                try{
                response=sendGet(hlrLookupData);
                }catch(Exception e){}
		//return createHLRLookupResponse(hlrLookupResponse.getBody());
                return createHLRLookupResponse(response);
			
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
		httpComponentsClientHttpRequestFactory.setConnectTimeout(60000);
		httpComponentsClientHttpRequestFactory.setReadTimeout(60000);
		return httpComponentsClientHttpRequestFactory;
	}
	
	public HLRLookupResponse createHLRLookupResponse(String response){
		HLRLookupResponse hlrLookupResponse=new HLRLookupResponse();
                System.out.println("HLRLookup REspnse value received is "+response);
		if(response.contains("|")){
			String[] hlrLookupResponseArray=response.split("\\|");
                        System.out.println("HLRLookup Response :>"+hlrLookupResponseArray[0]+" "+hlrLookupResponseArray[1]+" "+hlrLookupResponseArray[2]);
			hlrLookupResponse.setHlrLookupType(hlrLookupResponseArray[0]);
			hlrLookupResponse.setNetwork(hlrLookupResponseArray[1]);
			hlrLookupResponse.setMsisdn(hlrLookupResponseArray[2]);
			hlrLookupResponse.setErrorText("");
		}else{
			hlrLookupResponse.setErrorText(response);
		}
		return hlrLookupResponse;
	}
	
	public SendMessageResponse sendMessage(String msisdnAlias,String message){
		String sendMessageInput="{"
				+ "\"MSISDN\": \""+msisdnAlias+"\","
				+ "\"NetworkId\": \"1\","
				+ "\"Message\": \""+message+"\","
				+ "\"Spoof\": \"MoonLight\""
				+"}";
		System.out.println("Send Message Input: "+sendMessageInput);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + getAuthorizationData(merchantId,merchantToken));//partnerClub.getSmsExt()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(sendMessageInput,headers);
		ResponseEntity<SendMessageResponse> sendMessageResponse = getRestTemplate().postForEntity(sendMessageUrl,entity, SendMessageResponse.class);
		return sendMessageResponse.getBody();
		
		
	}
	
	public static void main(String[] arg){
		PFIUtil pfiUtil=new PFIUtil();
		////////////////////////////////////////HLR LOOKUP/////////////////////////////////////////////////////////////
		//447778780454
		/*HLRLookupResponse hlrLookupResponse=pfiUtil.doHLRLookup("447427626522");
		System.out.println(hlrLookupResponse.getNetwork());
		System.out.println(hlrLookupResponse.getHlrLookupType());
		System.out.println(hlrLookupResponse.getMsisdn());
		System.out.println(hlrLookupResponse.getErrorText());*/
		
		////////////////////////////////////////SEND PERSONAL LINK/// Angel- 447778780454, dongol- 447496553139

///////////////////////////////////////////////////

		SendPersonalLinkResponse sendPersonalLinkResponse=pfiUtil.sendPersonalLink("447427626522");
		System.out.println(sendPersonalLinkResponse.getMessageSent());
		System.out.println(sendPersonalLinkResponse.getFault());
		
		
	/*	SendMessageResponse sendMessageResponse=pfiUtil.sendMessage("bV0KUEJLMNYFIVLVGuw0SQ==", "Did you receive Message, Richard? Regards, Raj");
		boolean status=sendMessageResponse.getStatus();
		if(status){
			System.out.println("Transaction ID: "+sendMessageResponse.getTransactionId());
		}else
			System.out.println("Error Code: "+sendMessageResponse.getFault().getCode());*/

	}

        
        
        
            private String sendGet(String parameters) throws Exception {

                String lookupresponse="";
		String requesturl = hlrLookupUrl+"?"+parameters;
		
                System.out.println("SENDGET URL is "+requesturl);
		URL obj = new URL(requesturl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");
                //con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

		//add request header
		//con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + requesturl);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
                lookupresponse=response.toString();
		System.out.println("RESPONSE OF GET IS "+response.toString());
                
                return lookupresponse;

	}

}
