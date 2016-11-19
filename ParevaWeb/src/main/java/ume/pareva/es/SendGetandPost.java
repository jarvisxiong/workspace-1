/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.es;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author trung
 */
public class SendGetandPost {
 
	private final String USER_AGENT = "Mozilla/5.0";
 
	// HTTP GET request
	public String sendGet(String url) throws Exception {
 
		//String url = "http://ume/servlet/subtemp?transactionid=23907540697&op=wind&msisdn=393286782946";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("GET");
 
		con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		return(response.toString());
	} 
}
