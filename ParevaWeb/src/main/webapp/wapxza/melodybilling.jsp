<%@page import="java.math.BigDecimal"%>
<%@include file="coreimport.jsp" %>
<%@include file="commonfunc.jsp"%>
<%!
private String billHttp(SdcMobileClubUser clubUser, Date serviceDate, MobileClub club, int counter) {

        StringBuilder sb = new StringBuilder("http://obs.stsp.co.za/http/?username=UME2&password=UM32P@ssword&tickettype=S&");
        try {
            
            sb.append("contentdescription=");
                    sb.append(club.getUnique());
            sb.append("&forservicedate=");
            sb.append(java.net.URLEncoder.encode(longDate(serviceDate),"utf-8"));
            sb.append("&transactionid=");
            sb.append(counter + 1);
            sb.append("&msisdn=");       
            sb.append(clubUser.getParsedMobile());           
            sb.append("&amount=");
            sb.append(java.net.URLEncoder.encode(String.valueOf(club.getPrice()),"UTF-8"));
            sb.append("&serviceguid=");
            sb.append(club.getOtpServiceName());
            sb.append("&subscriptiondate=");
            sb.append(java.net.URLEncoder.encode(longDate(clubUser.getSubscribed()), "UTF-8"));
        } catch (Exception uee) {
           //logger.error("ENCODING ERROR FOR " + item.getParsedMobile() + ": " + sb.toString());
        }

        String code, desc, content;
        long startTime = 0;

        StringBuilder sbResponse = new StringBuilder("");
        try {
            java.net.URL url = new java.net.URL(sb.toString());
            startTime = System.currentTimeMillis();

            java.net.HttpURLConnection httpConnection = (java.net.HttpURLConnection) url.openConnection();

            httpConnection.setConnectTimeout(35000);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    httpConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sbResponse.append(inputLine);
            }
            in.close();
            code = httpConnection.getResponseCode() + "";
            desc = httpConnection.getResponseMessage();
            content = sbResponse.toString();

        }catch (Exception e) {
           
            code = "ERROR";
            desc = "ERROR,ERROR_GETTING_HTTP_RESPONSE";
            content = desc;
        }

     
        return content;
    }


    private String longDate(Date date) {
        SimpleDateFormat longDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        return longDateFormatter.format(date);
    }
    
     private MobileClubBillingTry parseHttpResponse(String resp, SdcMobileClubUser item, MobileClub mobileclub, Date serviceDate, String itemCampaign) {

        /*
         token + "," + msisdn + "," + result + ","
         + response_Ref + "," + response_Errorcode + "," + response_Desc + ","
         + response_Network + "," + response_ContractType + "," + sw.ElapsedMilliseconds;
         */
        MobileClubBillingTry btry = new MobileClubBillingTry();
        
        btry.setUnique(SdcMisc.generateUniqueId());
        btry.setCreated(new Date());
        btry.setAggregator("sts");
        btry.setRegionCode(mobileclub.getRegion());
        btry.setLogUnique(item.getUnique());
        btry.setTariffClass(mobileclub.getPrice());
        btry.setClubUnique(mobileclub.getUnique());
        btry.setCampaign(itemCampaign);
        btry.setTicketCreated(serviceDate);
        String networkname=item.getNetworkCode();
        
        String[] responseFields = resp.split(",");
        
        if (responseFields.length >= 9) { // ORIGINAL SIZE: 9 FIELDS

            btry.setTransactionId(responseFields[0]);
            btry.setParsedMsisdn(responseFields[1]);//item.getParsedMobile());
            btry.setStatus(responseFields[2]);
            
            btry.setResponseRef(responseFields[3]);
            
            btry.setResponseCode(responseFields[4]);
            btry.setResponseDesc(responseFields[5]);
            if (item.getNetworkCode().equalsIgnoreCase("heita") && btry.getResponseDesc().contains(".")) {
                btry.setResponseDesc(btry.getResponseDesc().substring(0, btry.getResponseDesc().indexOf('.') + 1).trim());
            }
            
            if (responseFields[6].toLowerCase().equalsIgnoreCase(networkname)) {
                btry.setNetworkCode(responseFields[6].toLowerCase()); //item.getNetworkCode());
            } else {
                btry.setNetworkCode(networkname);
                
            }
            
            btry.setBillingType("Adhoc");
         
        } else {
            btry.setParsedMsisdn(item.getParsedMobile());
            btry.setNetworkCode(item.getNetworkCode());
            
            btry.setStatus("");
            btry.setTransactionId(SdcMisc.generateUniqueId());
            btry.setResponseRef("");
            btry.setResponseCode("");
            btry.setBillingType("");
            
            if (responseFields.length <= 2) {
                btry.setResponseDesc(resp);
            }
        }
        //logger.info(item.getParsedMobile() + "-" + btry.getTariffClass() + " RESPONSE CODE = " + btry.getResponseCode() + " : " + btry.getResponseDesc());
        
        return btry;
    }
    






%>

<%
String msisdntobill=request.getParameter("msisdn");
String region=request.getParameter("region");

Integer counter=0;
try{
counter=(Integer) session.getAttribute("counter");
System.out.println("melodybilling counter in melodybilling.jsp "+counter);
}catch(Exception e){counter=0;}

System.out.println("melodybilling  in melodybilling.jsp "+msisdntobill+"  "+counter);
if(msisdntobill!=null && region!=null){
    
 if(user==null && (msisdntobill!=null || !msisdntobill.equalsIgnoreCase(""))){                    
                    user=umeuserdao.getUser(msisdntobill);        
                }
 
 if(user!=null){
     MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
     SdcMobileClubUser clubUser =umemobileclubuserdao.getClubUserByMsisdn(msisdntobill,club.getUnique());
     if(clubUser!=null && clubUser.getActive()==1){
         String billResponse=billHttp(clubUser, new Date(),club,1);
         MobileClubBillingTry billingtry=parseHttpResponse(billResponse,clubUser,club,new Date(),"");
         
         if(billingtry!=null){
             System.out.println("melodybillingtest response "+billingtry.getParsedMsisdn()+" "+billingtry.getResponseCode()+" "+billingtry.getResponseDesc());
             if(billingtry.getResponseCode().equals("00") || billingtry.getResponseCode().equals("0")) billingtry.setResponseCode("003");
             mobilebillingdao.insertBillingTry(billingtry);
             if(billingtry.getResponseCode().equals("00") || billingtry.getResponseCode().equals("0") || billingtry.getResponseCode().equals("003")){
                 clubUser.setCredits(1);
                 umemobileclubuserdao.saveItem(clubUser);
                 String redirecturl="http://" + dmn.getRedirectUrl() + "/?m="+Misc.encrypt(clubUser.getParsedMobile())+"&sub=success&credit="+clubUser.getCredits();
                 response.sendRedirect(redirecturl);
                 return;
             }
              else {
             String redirectUrl="http://"+dmn.getDefaultUrl()+"/fail.jsp?mid="+Misc.encrypt(clubUser.getParsedMobile())+"&failcode="+billingtry.getResponseCode()+"&faildesc="+billingtry.getResponseDesc();
             response.sendRedirect(redirectUrl);
             return;
         }
         }
         else {
             String redirectUrl="http://"+dmn.getDefaultUrl()+"/fail.jsp?mid="+Misc.encrypt(clubUser.getParsedMobile())+"failcode=12&faildesc=couldn't attempt";
             response.sendRedirect(redirectUrl);
             return;
         }
             
         
     }
     
     
 }
    
}







%>