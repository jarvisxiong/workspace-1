<%@include file="coreimport.jsp"%>
<%@include file="ukheader.jsp"%>
<%@include file="cpavisit.jsp"%>

<%
String transaction_ref = "",campaignsrc="";
//Reading IMI Header HTTP_X_ESC_ALIAS from IMI
String CPAcampainId = "";
boolean visitinserted=false;
try{
CPAcampainId=request.getParameter("cid");
request.setAttribute("campaignid",CPAcampainId);
session.setAttribute("campaignid",CPAcampainId);
//System.out.println("xstreamtesting: CAMPAIGNID IN INDEX.jsp "+CPAcampainId);
   
    if (CPAcampainId != null && CPAcampainId.trim().length() > 0) {           
            MobileClubCampaign campaign = null;
            campaign = UmeTempCmsCache.campaignMap.get(CPAcampainId);
            if(campaign!=null) campaignsrc = cmpg.getSrc();
//System.out.println("CAMPAIGN DETAILS ABOVE " +campaign.getSrc()+" ==="+ cmpg.getSrc()+"===== "+campaignsrc);
            
            }


}catch(Exception e){CPAcampainId="";}
try{
transaction_ref =(String) request.getParameter("transref");
if(transaction_ref==null || transaction_ref.trim().length()<=0) transaction_ref=(String) request.getAttribute("transref");
if(transaction_ref==null || transaction_ref.trim().length()<=0) transaction_ref="x";//misc.generateUniqueId();
} catch(Exception e){}

java.util.Enumeration names = request.getHeaderNames();
        while(names.hasMoreElements()){
            String name = (String) names.nextElement();
            //System.out.println("xstreamtesting: PFI Header:> "+name + ":" + request.getHeader(name) + "");
        }

//System.out.println("xstreamtesting: landing.jsp Transaction Reference "+transaction_ref);

String reqreferer="";
String queryStringSql = "",CPAhash="";
Date curDate = new Date(System.currentTimeMillis());
String cdate=MiscDate.toSqlDate(curDate);

String clubUnique = "";
if (club!=null) clubUnique = club.getUnique();
String useragent=Misc.encodeForDb(request.getHeader("user-agent"));
String uksite=(String) request.getParameter("site");

//if(uksite!=null) System.out.println("xstreamtesting: UK SITE Visited "+uksite);


StringBuilder sb=new StringBuilder();
	
	try{
	
	Enumeration parameterList = request.getParameterNames();
	while (parameterList.hasMoreElements()) {
		String sName = parameterList.nextElement().toString();
		String sValue = request.getParameter(sName).toString();
		
		if(sb.toString().length()==0){
			sb.append(sName+"="+sValue);
		}else{
			sb.append("&"+sName+"="+sValue);
		}
		 
	}
	}catch(Exception m){
		sb.append("  [Error Occur]");
	}
        
        
        
        String queryString = sb.toString();
     //========CPA Visit Log End ==================    
    
      String landingPage="";
                    try{
                        
                        //landingPage=landingpage.initializeLandingPage(domain,"","all");
                           if(CPAcampainId!=null && CPAcampainId.trim().length()>0)
                            {
                                try{
                                    landingPage=landingpage.initializeLandingPage(domain,CPAcampainId,"all");
                                    request.setAttribute("landingPage",landingPage);
                                    }
                                catch(Exception e){landingPage="landing_default"; }
                            }
                           else{
                               try{
				landingPage=landingpage.initializeLandingPage(domain);
				request.setAttribute("landingPage",landingPage);
                        }
                        catch(Exception e){landingPage="landing_default"; }
                           }
                        
                        java.sql.Date today=new java.sql.Date(System.currentTimeMillis());
                        if(CPAcampainId==null) CPAcampainId="";
                        //System.out.println("xstreamtesting: CPACampaing ID: "+CPAcampainId);
                        CampaignHitCounter campaignHitCounter=campaignhitcounterdao.HitRecordExistsOrNot(today,domain,CPAcampainId,landingPage);
                        if(campaignHitCounter==null){
                    		campaignHitCounter=new CampaignHitCounter();
                    		campaignHitCounter.setaUnique(Misc.generateUniqueId());
                    		campaignHitCounter.setaDomainUnique(domain);
                    		campaignHitCounter.setCampaignId(CPAcampainId);
                    		campaignHitCounter.setLandingPage(landingPage);
                    		campaignHitCounter.setDate(today);
                    		campaignHitCounter.setHitCounter(1);
                    		campaignHitCounter.setSubscribeCounter(0);
                    		campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);
                    	
                    	}else{
                    		//campaignHitCounter.setDate(today);
                    		campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
                    	}
                    
                    }
                    catch(Exception e){landingPage="landing-page";} 		
    		
    
         //Adding landingPage to transaction_ref so as to read it in IMINotification 
         //when we receive notification. This is due to sometime IMI are not redirecting users to our success URL (imisuccess.jsp)
         //@Date 2015-08-17
         
         transaction_ref=transaction_ref+"-"+landingPage;
                    
                    
          if(CPAcampainId!=null && CPAcampainId.trim().length()>0){
   if((useragent!=null && (!useragent.trim().toLowerCase().contains("elb-healthchecker/1.0") && !useragent.trim().toLowerCase().contains("pingdom")) 
                )){
      
campaigndao.log("index", landingPage, transaction_ref, msisdn, handset,domain, CPAcampainId, clubUnique, "INDEX", 0, request,response,"imi");
         
          }
          }

session.setAttribute("transaction-ref", transaction_ref);
request.setAttribute("transaction-ref",transaction_ref);
boolean cidPresent=false;
boolean clubUniquePresent=false;
Cookie[] cookies = null;

cookies = request.getCookies();
 if( cookies != null ){

 for (Cookie cookie:cookies){
    cidPresent=cookie.getName().equals("cid") && (cookie.getValue()!=null && !cookie.getValue().isEmpty());
    clubUniquePresent=cookie.getName().equals("clubUnique") && (cookie.getValue()!=null && !cookie.getValue().isEmpty());     
 }
 }
 
 if(!cidPresent){
	Cookie cookie1 = new Cookie("cid",CPAcampainId);
   	cookie1.setMaxAge(168*60*60); //24 hours
        response.addCookie(cookie1); 
    
 }
 
 if(!clubUniquePresent){
	 Cookie cookie1 = new Cookie("clubUnique",clubUnique);
	 cookie1.setMaxAge(168*60*60); //24 hours
	    response.addCookie(cookie1); 
	    
 }
 
 Cookie landingcookie=new Cookie("landingpage",landingPage);
 landingcookie.setMaxAge(168*60*60);
 response.addCookie(landingcookie);


 PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    Writer stringWriter=new StringWriter();
    

    context.put("transref",transaction_ref);
    context.put("contenturl","http://"+dmn.getContentUrl());
    context.put("defaulturl","http://"+dmn.getDefaultUrl());

String output = writer.toString();              
engine.getTemplate(landingPage).evaluate(writer, context);

%>
