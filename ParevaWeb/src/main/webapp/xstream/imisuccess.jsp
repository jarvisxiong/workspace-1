<%@include file="ukheader.jsp"%>
<%@include file="cpanotify.jsp"%>
<%!
Calendar currentTime=Calendar.getInstance();
//SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>

<%

System.out.println("xstreamtesting: "+"Calling imisuccess page ");
String transaction_ref = "",cpaparam1="",cpaparam2="",cpaparam3="",revparam1="",revparam2="",revparam3="";

int hourDifference=-1;
String expired="";
Date expiryDate=null;
boolean isLoadContent=false;
boolean msisdnPresent=false;
boolean servicePresent=false;        

String creationdate=sdf2.format(currentTime.getTime());

currentTime.add(Calendar.HOUR,168);
String expiry=sdf2.format(currentTime.getTime());

String tid="",aClubUnique="",aCampaignId="",aStatus="",aType="",aSid="",aParsedMobile="",aNetworkId="",aHash="",aCreated="",landingPage="",imireference="";

 try{
   
    msisdn=httprequest.get("msisdn");
    tid=httprequest.get("tid");
    aStatus=httprequest.get("status");
    aType=httprequest.get("type");
    aSid=httprequest.get("sid");
    aNetworkId=httprequest.get("networkid");
    aHash=httprequest.get("hash");
    imireference=httprequest.get("ref");
    //a
    String imiNetworkid = getOperatorName(aNetworkId);
    
    if(imireference!=null && !imireference.trim().equals("")) transaction_ref=imireference;
    
    //System.out.println("xstreamtesting imisuccess: msisdn received is "+msisdn+" aStatus: "+aStatus+" imireference "+imireference);
    
    if(msisdn!=null && msisdn.trim().length()>0)
        {
        session.setAttribute("msisdn",msisdn);    
        }
    
 /*
        UKSuccess tidRecord=uksuccessdao.checkTid(tid);
       if(tidRecord!=null && tidRecord.getaParsedMobile()!=null && tidRecord.getType().equalsIgnoreCase("sub") && aType.equalsIgnoreCase("sub"))
        {
          Date usercreateddate=tidRecord.getCreated();
          Calendar nowtime=Calendar.getInstance();
          Date visitedTime=nowtime.getTime();
          int userdifference=-1;
                try{
                userdifference=DateUtil.hoursDiff(usercreateddate,visitedTime);
                 }catch(Exception e){userdifference=-1;}
          
          if(userdifference<24) isLoadContent=true;
          
        }
	*/
	if(aStatus.equals("7")){
		isLoadContent=true;
         UKSuccess successfuluser=new UKSuccess();
        successfuluser.setTid(tid);
        successfuluser.setClubUnique(club.getUnique());
        successfuluser.setCampaignId(campaignId);
        successfuluser.setStatus(aStatus);
        successfuluser.setType(aType);
        successfuluser.setSid(aSid);
        successfuluser.setaParsedMobile(msisdn);
        successfuluser.setaNetworkId(imiNetworkid);
        successfuluser.setaHash(aHash);
        successfuluser.setCreated(sdf2.parse(creationdate));        
        successfuluser.setExpiry(expiry);
        uksuccessdao.saveSuccessfulUser(successfuluser);
        session.setMaxInactiveInterval(168*60*60);
	}
    
    else {
          if(aStatus.equals("1") && aType.equalsIgnoreCase("sub")){
               
        //Reading Cookies for New User set from LandingPage
        String campaignsrc="";
    String clubUnique="";
        Cookie[] cookies = null;
       cookies = request.getCookies();
        if( cookies != null ){
      		for (Cookie cookie:cookies){
      			if(cookie.getName().equals("cid") && (cookie.getValue()!=null && !cookie.getValue().isEmpty()))
      				campaignId=cookie.getValue();
      			if(cookie.getName().equals("clubUnique") && (cookie.getValue()!=null && !cookie.getValue().isEmpty()))
      				clubUnique=cookie.getValue();
                        
                        if(cookie.getName().equals("landingpage") && (cookie.getValue()!=null && !cookie.getValue().isEmpty()))
      				landingPage=cookie.getValue();
                        
                        if(cookie.getName().equals("cpaparam1") && (cookie.getValue()!=null && !cookie.getValue().isEmpty()))
                            cpaparam1=cookie.getValue();
                        
                        if(cookie.getName().equals("cpaparam2") && (cookie.getValue()!=null && !cookie.getValue().isEmpty()))
                            cpaparam2=cookie.getValue();
                         
                        if(cookie.getName().equals("cpaparam3") && (cookie.getValue()!=null && !cookie.getValue().isEmpty()))
                            cpaparam3=cookie.getValue();
                        
                          if(cookie.getName().equals("revparam1") && (cookie.getValue()!=null && !cookie.getValue().isEmpty()))
                            revparam1=cookie.getValue();
                        
                        if(cookie.getName().equals("revparam2") && (cookie.getValue()!=null && !cookie.getValue().isEmpty()))
                            revparam2=cookie.getValue();
                         
                        if(cookie.getName().equals("revparam3") && (cookie.getValue()!=null && !cookie.getValue().isEmpty()))
                            revparam3=cookie.getValue();
      		}
        }
        
        
         if(campaignId.trim().length()<=0 && campaignId.trim().equalsIgnoreCase(""))
        {
            campaignId=afterUnderScore(imireference);
        }
         
         //This is to avoid duplicate subscription record to campaignLog. If that tid exist already then don't record. 
      UKSuccess ukSuccess=uksuccessdao.checkTid(tid);
      //if(ukSuccess==null){         
    //campaigndao.log("imisuccess", landingPage, transaction_ref, msisdn, handset,domain, campaignId, clubUnique, "SUBSCRIBED", 0, request,response,imiNetworkid);
      //}
        if(campaignId!=null && campaignId.trim().length()>0) {
        	if (!campaignId.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
			if(cmpg!=null) campaignsrc = cmpg.getSrc();
                        
        //campaigndao.log("imisuccess", landingPage, transaction_ref, msisdn, handset,domain, campaignId, clubUnique, "SUBSCRIBED", 0, request,response,imiNetworkid);
                        
        }
        
        if  (campaignsrc!=null && campaignsrc.endsWith("CPA")){
        	
        	//logtoCpaLogging(zacpa,msisdn, campaignsrc,campaignId, clubUnique, imiNetworkid);
                String cpalogQuery="UPDATE cpavisitlog set aParsedMobile='"+msisdn+"',isSubscribed='1',aSubscribed='"+creationdate+"' WHERE aCampaignId='"+cmpg.getUnique()+"' AND (aHashcode='"+cpaparam1+"' AND cpacampaignid='"+cpaparam2+"'"
                              + " AND clickid='"+cpaparam3+"') ";
                int updatecpavisit=zacpa.executeUpdateCPA(cpalogQuery);
        }
        		
        if (campaignsrc!=null && campaignsrc.endsWith("RS")){
//             String cpaloggingquery="insert into revShareLogging (aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values"
//	     +"('"+Misc.generateUniqueId()+"','0','"+cmpg.getPayoutCurrency()+"','"+msisdn+"','"+MiscCr.encrypt(msisdn)+"','"+cmpg.getUnique()+"','"+club.getUnique()+"','"+sdf2.format(new Date())+"','"+sdf2.format(new Date())+"','0','"+imiNetworkid+"','"+cmpg.getSrc() +"','0')";
//	            	 int insertedRows=zacpa.executeUpdateCPA(cpaloggingquery);
                         
                      String revsharequery="UPDATE revShareVisitLog set aParsedMobile='"+msisdn+"',isSubscribed='1',aSubscribed='"+creationdate+"' WHERE aCampaignId='"+cmpg.getUnique()+"' AND (  parameter1='"+revparam1+"' AND parameter2='"+revparam2+"'"
                              + " AND parameter3='"+revparam3+"') ";
//                      if(msisdn!=null && msisdn.trim().length()>0)
//                          cpaloggingquery+=" AND aParsedMobile='"+msisdn+"'"; 
                      
                      //System.out.println("ZA CONFIRM ----- "+revsharequery);
                      int updateRow=zacpa.executeUpdateCPA(revsharequery);
        	
        }
        
        
        
        //End reading cookies for new user set from landingPage
        
        
        
                Cookie cookie1 = new Cookie("umeservicename",club.getUnique());
                //cookie1.setDomain(domain);
                cookie1.setMaxAge(168*60*60); //24 hours
                
                //System.out.println("xstreamtesting: Imisuccess cookie1 is added");
                
                Cookie cookie2=new Cookie("msisdn",msisdn);
                //cookie2.setDomain(domain);
                cookie2.setMaxAge(168*60*60); //7 days
                //System.out.println("xstreamtesting: Imisuccess cookie2 is added");
                
                Cookie cookie3=new Cookie("activationdate",creationdate);
                //cookie3.setDomain(domain);
                cookie3.setMaxAge(168*60*60);
                
                Cookie cookie4=new Cookie("expiryDate",expiry);
                //cookie4.setDomain(domain);
                cookie3.setMaxAge(168*60*60);
                
                
                response.addCookie(cookie1);
                response.addCookie(cookie2);
                response.addCookie(cookie3);
                response.addCookie(cookie4);
                isLoadContent=true;
                
              
                
        }           
    }
        
    
        
    if(isLoadContent) {
        session.setMaxInactiveInterval(168*60*60);
         response.sendRedirect("/content.jsp?msisdn="+msisdn+"&transref="+transaction_ref);
         //System.out.println("load content true");
                return;
    }
    
    else{
        response.sendRedirect("/landingpage.jsp?msisdn="+msisdn+"&transref="+transaction_ref);
        return;
    }
    }
    catch(Exception e){}
 
               
    
               










%>

<%!
public String afterUnderScore(String longString){
        if(longString==null)return "";
        
        int index = longString.indexOf(",");
        if(index==-1)return longString;
        
        String aux = longString.substring(index+1);
        if(aux.equalsIgnoreCase("null"))return "";
                
        return aux;
    }



%>