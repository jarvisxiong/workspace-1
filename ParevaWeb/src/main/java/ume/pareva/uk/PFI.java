package ume.pareva.uk;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import ume.pareva.dao.SdcRequest;

public class PFI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String url="http://pfi.imimobile.net/api/PersonalLink.svc/rest/SendAjaxPersonalLink";
	private static final String destinationUrl="http://uk.quiz2win.mobi/index.jsp";
	private static final String spoof="UME";
	private static final String merchantId="81";
	private static final String merchantToken="FBEEBAC4-9F42-4DC4-8E5D-B481A30986B0";
       
    public PFI() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SdcRequest sdcRequest=new SdcRequest(request);
		String msisdn=sdcRequest.get("msisdn");
		String message="personal link test {url}";
		RestTemplate restTemplate=new RestTemplate();
		restTemplate.setRequestFactory(getClientRequestFactory());
		String jsonInputData=prepareJsonInput(msisdn,destinationUrl,message,spoof);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + getAuthorizationData(merchantId,merchantToken));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(jsonInputData,headers);
		String answer = restTemplate.postForObject(url, entity, String.class);
		System.out.println(answer);
	}
	
	public void sendPersonalLink(String msisdn){
		String message="personal link test {url}";
		RestTemplate restTemplate=new RestTemplate();
		restTemplate.setRequestFactory(getClientRequestFactory());
		String jsonInputData=prepareJsonInput(msisdn,destinationUrl,message,spoof);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + getAuthorizationData(merchantId,merchantToken));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(jsonInputData,headers);
		String answer = restTemplate.postForObject(url, entity, String.class);
		System.out.println(answer);
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
		httpComponentsClientHttpRequestFactory.setConnectTimeout(2000);
		httpComponentsClientHttpRequestFactory.setReadTimeout(2000);
		return httpComponentsClientHttpRequestFactory;
	}

}
