package ume.pareva.ire;

import java.util.UUID; //

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component("IEutil")
public class IEutil {
	
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
	
	public MsisdnPassingResponse sendPersonalLink(String transactionid){
		String httpsrequest="https://msisdn.sla-alacrity.com/authenticate/msisdn?uri=partner:91909c32-e422-42e3-845a-d3cbf15af4fa&transaction_id="+transactionid;
                String username="txtnationl_731_live"; //txtnationl_743_live"; 
                String pass="ndondawutu"; //"2y5okuty";//"ndondawutu";
		//String message="Please click to confirm your request: {url}";
		//String jsonInputData=prepareJsonInput(transactionid);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + getAuthorizationData(username,pass));
		headers.add("Accept", "application/json");
		System.out.println("irevodfone Basic Authorization " + getAuthorizationData(username,pass));
	//	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                System.out.println("irevodafone STEP2 http request sent is "+httpsrequest);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<MsisdnPassingResponse> sendPersonalLinkResponse = getRestTemplate().exchange(httpsrequest,HttpMethod.GET, entity, MsisdnPassingResponse.class);
		return sendPersonalLinkResponse.getBody();
	}
        
 
	
	public String prepareJsonInput(String transactionid){
		String sessionToken=UUID.randomUUID().toString();
                String uridata="partner:91909c32-e422-42e3-845a-d3cbf15af4fa";
		return "{"
				+ "\"uri\": \""+uridata+"\","
				+ "\"transaction_id\": "+transactionid+","
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
	
	
	public static void main(String[] arg){
		IEutil pfiUtil=new IEutil();
                String transactionid="5980a665eae3518dc2562f796807ce0d";
		////////////////////////////////////////HLR LOOKUP/////////////////////////////////////////////////////////////
		//447778780454
//		HLRLookupResponse hlrLookupResponse=pfiUtil.doHLRLookup("447427626522");
//		System.out.println(hlrLookupResponse.getNetwork());
//		System.out.println(hlrLookupResponse.getHlrLookupType());
//		System.out.println(hlrLookupResponse.getMsisdn());
//		System.out.println(hlrLookupResponse.getErrorText());
		
		////////////////////////////////////////SEND PERSONAL LINK/// Angel- 447778780454, dongol- 447496553139

///////////////////////////////////////////////////

                MsisdnPassingResponse sendPersonalLinkResponse=pfiUtil.sendPersonalLink(transactionid);
		System.out.println(sendPersonalLinkResponse);
		/*System.out.println(sendPersonalLinkResponse.getMessage());
		System.out.println(sendPersonalLinkResponse.getError());*/
	}


}
