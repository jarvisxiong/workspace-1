<%
  // CPA Visit Log Record
 
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Calendar nowtime = new GregorianCalendar();
	String visitsubscribed = "1970-01-01 00:00:00";
	String campaignsrc= "";
	String clientIp = getClientIpAddr(request); //"iptest";
	String isSubscribed = "1";
        if(msisdn==null || msisdn.trim().length()<=0) msisdn="xxxx";
        
	//System.out.println("imi debug " + " cpacampaign " + CPAcampainId);
    if(CPAcampainId==null || CPAcampainId.trim().length()<=0)
        {
            String referUrl=request.getHeader("Referer");
//            if(referUrl!=null && referUrl.trim().length()>0)
//            {
//               Map<String,String> parammap= getCampaignFromReferer(referUrl);
//               CPAcampainId=parammap.get("cid");
//               CPAhash=parammap.get("hash");
//               System.out.println("imi debugging " + "inside referurl cpacampaign " + CPAcampainId+" hash: "+CPAhash);
//            }
        }
    
    if (CPAcampainId != null && CPAcampainId.trim().length() > 0) {
           
            MobileClubCampaign campaign = null;
            campaign = UmeTempCmsCache.campaignMap.get(CPAcampainId);
            if(campaign!=null) campaignsrc = cmpg.getSrc();  
            
            }
    
    // BitterCPA
    if (campaignsrc.equalsIgnoreCase("BitterCPA")) {
           if(CPAhash==null || CPAhash.trim().length()<=0) CPAhash = request.getParameter("hash");
            if(CPAhash==null || CPAhash.trim().length()<=0){
                  
            String referurl=request.getHeader("Referer");
            if(referurl!=null && referurl.trim().length()>0)
            {
               Map<String,String> parammap= getCampaignFromReferer(referurl);
               CPAhash=parammap.get("hash");
               System.out.println("imidebugging " + "inside referurl cpacampaign " + CPAcampainId+" hash: "+CPAhash);
            }
            }
            if (CPAhash != null && CPAhash.trim().length() > 0) {
			session.setAttribute("CPAhash", CPAhash);

		} else {
			CPAhash = "cpa_not_recorded";
		}
            insertCpaVisitLog(dbsession,transaction_ref,CPAhash,msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","","",clientIp);
    }
    
    //BitterOldCPA
    else if (campaignsrc.equalsIgnoreCase("BitterOldCPA")) {
        	String CPAparam = request.getParameter("param");
		if (CPAparam != null && CPAparam.trim().length() > 0) {
			session.setAttribute("CPAparam", CPAparam);

		} else {
			CPAparam = "cpa_not_recorded";
		}
        
           insertCpaVisitLog(dbsession,transaction_ref,CPAparam,msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","","",clientIp);
                
    }
    
    //MundoCPA
    else if (campaignsrc.equalsIgnoreCase("MundoCPA")) {
        
        String CPAcampaignid = request.getParameter("CampaignID");
        String CPAclickid = request.getParameter("ClickID");
        String publisherId=request.getParameter("PublisherID");

		if ((CPAcampaignid != null && CPAcampaignid.trim().length() > 0)
				&& (CPAclickid != null && CPAclickid.trim().length() > 0)) {
			session.setAttribute("CPAcampaignid", CPAcampaignid);
			session.setAttribute("CPAclickid", CPAclickid);

		} else {
			CPAcampaignid = "cpa_not_recorded";
			CPAclickid = "cpa_not_recorded";
		}
             insertCpaVisitLog(dbsession,transaction_ref,"",msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,publisherId,CPAcampaignid,CPAclickid,clientIp);     
    }
    
    //UniteMobCPA
    else if (campaignsrc.equalsIgnoreCase("UniteMobCPA")) {
        String CPAsubid = request.getParameter("subid");
		if (CPAsubid != null && CPAsubid.trim().length() > 0) {
			session.setAttribute("CPAsubid", CPAsubid);

		} else {
			CPAsubid = "cpa_Not_recorded";
		}
                 insertCpaVisitLog(dbsession,transaction_ref,CPAsubid,msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","","",clientIp);  
    }
    
    //KimiaCPA
    else if (campaignsrc.equalsIgnoreCase("KimiaCPA")){
        
        String CPAclickid = request.getParameter("click_id"); 
        if((CPAclickid!=null && CPAclickid.trim().length()>0)){
            session.setAttribute("kimiaclickid", CPAclickid);              
		
	}else{
		CPAclickid="cpa_Not_recorded";
	}

boolean clickidcheck=clickidExist(dbsession,CPAclickid,campaignsrc);

 if(!clickidcheck){
     transaction_ref=transaction_ref+misc.generateUniqueId();
         insertCpaVisitLog(dbsession,transaction_ref,"",msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","kimiacampaign",CPAclickid,clientIp);  
}
        
    }
    
    //RethulaCPA
       else if (campaignsrc.equalsIgnoreCase("RethulaCPA")){
        
        String CPAclickid = request.getParameter("clickid"); 
        if((CPAclickid!=null && CPAclickid.trim().length()>0)){
            session.setAttribute("rethulaclickid", CPAclickid);              
		
	}else{
		CPAclickid="cpa_not_recorded";
	}
         insertCpaVisitLog(dbsession,transaction_ref,"",msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","rethulacampaign",CPAclickid,clientIp);  
        
    }

          //MountWilSonCPA
       else if (campaignsrc.equalsIgnoreCase("MountWilsonCPA")){
        
        String CPAclickid = request.getParameter("clickid"); 
        if((CPAclickid!=null && CPAclickid.trim().length()>0)){
            session.setAttribute("mountwilsonclickid", CPAclickid);              
		
	}else{
		CPAclickid="cpa_not_recorded";
	}
         insertCpaVisitLog(dbsession,transaction_ref,"",msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","mountwilsoncampaign",CPAclickid,clientIp);  
        
    }
    
    //GoMobiCPA
    else if (campaignsrc.equalsIgnoreCase("GoMobiCPA")){
        String CPAGoMobiTransactionId = request.getParameter("transaction_id");
        if(CPAGoMobiTransactionId!=null && CPAGoMobiTransactionId.trim().length()>0){
		session.setAttribute("hashcode", CPAGoMobiTransactionId);
               
		
	}else{
		CPAGoMobiTransactionId="cpa_not_recorded";
	}
         insertCpaVisitLog(dbsession,transaction_ref,CPAGoMobiTransactionId,msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","","",clientIp);
        
    }
    
      //GoMobiCPA
    else if (campaignsrc.equalsIgnoreCase("YaziCPA")){
        String CPAYaziTransactionId = request.getParameter("transaction_id");
        if(CPAYaziTransactionId!=null && CPAYaziTransactionId.trim().length()>0){
		session.setAttribute("hashcode", CPAYaziTransactionId);
               
		
	}else{
		CPAYaziTransactionId="cpa_not_recorded";
	}
         insertCpaVisitLog(dbsession,transaction_ref,CPAYaziTransactionId,msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","","",clientIp);
        
    }
    
    
    
    
    
    
        else if (campaignsrc.equalsIgnoreCase("MobideaCPA")){
	
	String CPAclickid = request.getParameter("clickID"); 
	
	if((CPAclickid!=null && CPAclickid.trim().length()>0)){
		session.setAttribute("mobideaclickid", CPAclickid);
                	
	}else{
		CPAclickid="cpa_not_recorded";
	}
         insertCpaVisitLog(dbsession,transaction_ref,"",msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","mobideacampaign",CPAclickid,clientIp);  
         //System.out.println("ZA cpa debug "+" MUNDOMEDIA cpaquery "+insertQuery);
        }
        
            else if (campaignsrc.equalsIgnoreCase("KissmyadsCPA")){
	//MyAdsCPA:
	String myadsCPAhash = request.getParameter("transaction_id");
        

	if(myadsCPAhash!=null && myadsCPAhash.trim().length()>0){
		session.setAttribute("hashcode", myadsCPAhash);
           		
	}else{
		myadsCPAhash="cpa_not_recorded";
	}
          insertCpaVisitLog(dbsession,transaction_ref,myadsCPAhash,msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","","",clientIp);
       }
       
               else if (campaignsrc.equalsIgnoreCase("BadhatCPA")){
	//BadhatCPA:
	String badhatCPAhash = request.getParameter("subid");
        

	if(badhatCPAhash!=null && badhatCPAhash.trim().length()>0){
		session.setAttribute("hashcode", badhatCPAhash);
                
		
	}else{
		badhatCPAhash="cpa_not_recorded";
	}
         insertCpaVisitLog(dbsession,transaction_ref,badhatCPAhash,msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","","",clientIp);  
}
               
           // MobSuiteCPA
               else if (campaignsrc.equalsIgnoreCase("MobSuiteCPA")) {
           if(CPAhash==null || CPAhash.trim().length()<=0) CPAhash = request.getParameter("visit_id");
            if(CPAhash==null || CPAhash.trim().length()<=0){
                  
            String referurl=request.getHeader("Referer");
            if(referurl!=null && referurl.trim().length()>0)
            {
               Map<String,String> parammap= getCampaignFromReferer(referurl);
               CPAhash=parammap.get("visit_id");
               System.out.println("imi debugging " + "inside referurl cpacampaign " + CPAcampainId+" hash: "+CPAhash);
            }
            }
            if (CPAhash != null && CPAhash.trim().length() > 0) {
			session.setAttribute("CPAhash", CPAhash);

		} else {
			CPAhash = "cpa_not_recorded";
		}
            insertCpaVisitLog(dbsession,transaction_ref,CPAhash,msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","","",clientIp);
    }
        
        
    else{
        insertCpaVisitLog(dbsession,transaction_ref,"",msisdn,CPAcampainId,sdf2.format(nowtime.getTime()),visitsubscribed,
                    isSubscribed,campaignsrc,"","","",clientIp);
    }
    

%>



<%!
public boolean clickidExist(Session dbsession,String clickid, String src)
{
    String sqlstr="select * from cpavisitlog where clickid='"+clickid+"' AND aSrc='"+src+"' LIMIT 1";
    boolean clickidexist=false;
       Query query=null;
    try{
        query=dbsession.createSQLQuery(sqlstr);
        java.util.List result=query.list();
        if(result.size()>0) clickidexist=true;
    }
    catch(Exception e){}
    
    return clickidexist;
    
}



%>