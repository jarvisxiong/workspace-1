/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.ae.util;
/**
 *
 * @author trung
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnectController {

    public static String getUrlPostResponse(String url) {
        URL urlObj;
        try {
            urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServerConnectController.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } catch (IOException ex) {
            Logger.getLogger(ServerConnectController.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static String getDescription(String returnCode){
        if(ae.sms.api.AppConstants.RETURN_CODE_MAP.containsKey(returnCode)){
            return ae.sms.api.AppConstants.RETURN_CODE_MAP.get(returnCode);
        } else {
            return "No such return code.";
        }
    }
}
