package ume.pareva.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * Servlet implementation class Test
 */
//@WebServlet("/Test")
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String USER_AGENT = "Mozilla/5.0";
		String url = "http://httpgw.winengage.com:10030/win-smsgwweb/winmt"; 

	    HttpClient client = new DefaultHttpClient();
	    HttpPost post = null;
            try{
            post=new HttpPost(url);
            }catch(Exception e){}

	    // add header
	    post.setHeader("User-Agent", USER_AGENT);
	    String xmlString="<?xml version=\"1.0\" standalone=\"no\"?>"
	    				+"<!DOCTYPE WIN_DELIVERY_2_SMS SYSTEM \"winbound_messages_v1.dtd\">"
	    				+"	<WIN_DELIVERY_2_SMS>"
	    				+"		<SMSMESSAGE>"
	    				+"			<DESTINATION_ADDR>+447765402393</DESTINATION_ADDR>"
	    				+"			<TEXT>Good News!! UME giving holiday to all staff...  </TEXT>"
	    				+"			<TRANSACTIONID>1</TRANSACTIONID>"
	    				+"			<TYPEID>2</TYPEID>"
	    				+"			<SERVICEID>1</SERVICEID>"
	    				+"			<COSTID>1</COSTID>"
	    				+"			<SOURCE_ADDR>WINTEST</SOURCE_ADDR>"
	    				+"		</SMSMESSAGE>"
    					+"	</WIN_DELIVERY_2_SMS>";
	    								
	    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(new BasicNameValuePair("User", "Moonlightmobile"));
	    urlParameters.add(new BasicNameValuePair("Password", "M00ngtw1!"));
	    urlParameters.add(new BasicNameValuePair("RequestID", "123"));
	    urlParameters.add(new BasicNameValuePair("WIN_XML", xmlString));
	    HttpResponse smsResponse=null;
	   try{
		   post.setEntity(new UrlEncodedFormEntity(urlParameters));
		   smsResponse = client.execute(post);
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	    System.out.println("\nSending 'POST' request to URL : " + url);
	    System.out.println("Post parameters : " + post.getEntity());
	    System.out.println("Response Code : " + 
	    		smsResponse.getStatusLine().getStatusCode());
	    try{
	    	BufferedReader rd = new BufferedReader(
	                    new InputStreamReader(smsResponse.getEntity().getContent()));

	    	StringBuffer result = new StringBuffer();
	    	String line = "";
	    	while ((line = rd.readLine()) != null) {
	    		result.append(line);
	    	}
	    

	    System.out.println(result.toString());
	    }catch(Exception e){
	    	
	    }
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
