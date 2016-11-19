/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.userservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Map;
import java.util.HashMap;
import org.springframework.stereotype.Component;

/**
 *
 * @author madan
 */

@Component("regionaldate")
public class RegionalDate {
    
    private static final String DEFAULT_REGION = "DEFAULT_REGION_13124EUIDCHJ2EIYSDUHCHXZKJCDKSJ CNNZJHGERJHASFIDUHIU";
    private Map<String, SimpleDateFormat> timezoneMap=null;

    private void initialise(){
        if(timezoneMap==null){
            
        Map<String, String> timezoneRegions = new HashMap<>();

        timezoneRegions.put("ZA", "Africa/Johannesburg");
        timezoneRegions.put("AU", "Australia/Sydney");
        timezoneRegions.put("UK", "Europe/London");
        timezoneRegions.put("ES", "Europe/Madrid");
        timezoneRegions.put("IT", "Europe/Rome");
        timezoneRegions.put("FR", "Europe/Paris");
        timezoneRegions.put("IE", "Europe/Dublin");
        timezoneRegions.put("PT", "Europe/Lisbon");
        timezoneRegions.put("NL", "Europe/Copenhagen");
        timezoneRegions.put("PL", "Europe/Warsaw");

        timezoneMap = new HashMap<>();
        for (String region : timezoneRegions.keySet()) {
            TimeZone tz = TimeZone.getTimeZone(timezoneRegions.get(region));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(tz);
            timezoneMap.put(region, sdf);
        }

        // Adds a default for unknown regions;
        {
            TimeZone tz = TimeZone.getDefault();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(tz);
            timezoneMap.put(DEFAULT_REGION, sdf);
        }
    }
    }
    public Date getRegionalDate(String region, Date dateToFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(getRegionalStringDate(region, dateToFormat));
        } catch (Exception ex) {
            return new Date();
        }
    }

    public String getRegionalStringDate(String region, Date dateToFormat) {
        initialise();
        if (!timezoneMap.keySet().contains(region)) {
            region = DEFAULT_REGION;
        }
        return timezoneMap.get(region).format(dateToFormat);
    }
    /* public String OLD-getRegionalStringDate(String region, Date dateToFormat) {
     String timezone;
     String upcaseRegion = region.trim().toUpperCase();
     switch (upcaseRegion) {
     case "ZA":
     timezone = "Africa/Johannesburg";
     break; //South Africa
     case "AU":
     timezone = "Australia/Sydney";
     break; //Australia
     case "UK":
     timezone = "Europe/London";
     break; //UK
     case "ES":
     timezone = "Europe/Madrid";
     break; //Spain                
     case "IT":
     timezone = "Europe/Rome";
     break; //Italy
     case "FR":
     timezone = "Europe/Paris";
     break; //France
     case "IE":
     timezone = "Europe/Dublin";
     break; //Ireland
     case "PT":
     timezone = "Europe/Lisbon";
     break; //Portugal            
     case "NL":
     timezone = "Europe/Copenhagen";
     break;  //Netherland           
     case "PL":
     timezone = "Europe/Warsaw";
     break; //Poland
     default:
     timezone = "";
     }

     TimeZone tz;
     if (timezone.isEmpty()) {
     tz = TimeZone.getDefault();
     } else {
     tz = TimeZone.getTimeZone(timezone);
     }

     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     sdf.setTimeZone(tz);

     return sdf.format(dateToFormat);}*/

    
}
    

