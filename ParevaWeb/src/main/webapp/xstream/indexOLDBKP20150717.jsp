<%@include file="coreimport.jsp"%>
<%@include file="db.jsp"%>
<%@include file="ukheader.jsp"%>
<%@include file="cpavisit.jsp"%>

<%
String transaction_ref = "";
//Reading IMI Header HTTP_X_ESC_ALIAS from IMI
String pfitagHeader="";
String CPAcampainId = "";
String pageType="";
boolean visitinserted=false;
try{
CPAcampainId=request.getParameter("cid");
request.setAttribute("campaignid",CPAcampainId);
session.setAttribute("campaignid",CPAcampainId);
//System.out.println("CAMPAIGNID IN INDEX.jsp "+CPAcampainId+" in attribute "+request.getAttribute("campaignid"));
}catch(Exception e){CPAcampainId="";}

try{
pfitagHeader=request.getHeader("X-ESC-Alias");
if(pfitagHeader!=null && pfitagHeader.trim().length()>0)
{
    transaction_ref=pfitagHeader+"_"+CPAcampainId; 
    msisdn=pfitagHeader;
    session.setAttribute("ukidentified",msisdn);
    
}
} catch(Exception e){}
//System.out.println("PFI Tag Header index.jsp "+pfitagHeader);

if(transaction_ref==null || transaction_ref.trim().length()<=0) transaction_ref=misc.generateUniqueId()+"_"+CPAcampainId;

java.util.Enumeration names = request.getHeaderNames();
        while(names.hasMoreElements()){
            String name = (String) names.nextElement();
            //System.out.println("PFI Header:> "+name + ":" + request.getHeader(name) + "");
        }

//System.out.println("Index.jsp Transaction Reference "+transaction_ref);

String reqreferer="";
String queryStringSql = "",CPAhash="";
Date curDate = new Date(System.currentTimeMillis());
String cdate=MiscDate.toSqlDate(curDate);


String msisdnt=request.getHeader("x-up-calling-line-id");
String clubUnique = "";
if (club!=null) clubUnique = club.getUnique();
String useragent=misc.encodeForDb(request.getHeader("user-agent"));
String uksite=(String) request.getParameter("site");
if(uksite!=null) System.out.println("UK SITE Visited "+uksite);


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
        
        
        Transaction trans=dbsession.beginTransaction();
        String queryString = sb.toString();
          if(session.getAttribute("transaction-ref")==null && (useragent!=null &&
                (!useragent.trim().toLowerCase().contains("elb-healthchecker/1.0") && !useragent.trim().toLowerCase().contains("pingdom")) 
                )){
        if (queryString != null && queryString.trim().length() > 0) {
            //insertQueryString(dbsession,transaction_ref,queryString,cdate);
        }
        else{
            //insertQueryString(dbsession,transaction_ref,"No Query String found",cdate);
        }
        
        //======= CPA VISIT Log STarted============
      
        %>
        <%@include file="cpavisitlog.jsp"%>
        <%
        }
          
     
    //========CPA Visit Log End ==================    
          
   
          
          
   //CampaignHit Counter for LandingPage
    String landingPage="";
    
    if(CPAcampainId!=null && CPAcampainId.trim().length()>0)
    {
         try{
		landingPage=landingpage.initializeLandingPage(domain,CPAcampainId,"all");
		request.setAttribute("landingPage",landingPage);
            }
            catch(Exception e){landingPage="landing_default"; }
    }
       else {
              try{
				landingPage=landingpage.initializeLandingPage(domain);
				request.setAttribute("landingPage",landingPage);
                        }
                        catch(Exception e){landingPage="landing_default"; }
          }
	
          if(CPAcampainId!=null && CPAcampainId.trim().length()>0){
   if(session.getAttribute("transaction-ref")==null && (useragent!=null &&
                (!useragent.trim().toLowerCase().contains("elb-healthchecker/1.0") && !useragent.trim().toLowerCase().contains("pingdom")) 
                )){
       
       if(landingPage.equalsIgnoreCase("landing_default")) pageType=landingPage+" no-cid";
       else pageType=landingPage;
	
campaigndao.log("index", pageType, transaction_ref, msisdn, handset,domain, CPAcampainId, clubUnique, "INDEX", 0, request,response,"imi");
   if(msisdn!=null && pfitagHeader!=null && msisdn.trim().length()>0 && pfitagHeader.trim().length()>0 && !msisdn.trim().equalsIgnoreCase("xxxx"))
       campaigndao.log("index", landingPage, transaction_ref, msisdn, handset,domain, CPAcampainId, clubUnique, "IDENTIFIED", 0, request,response,"imi");
//System.out.println("CampaignDao Logging "+ "Transaction reference "+transaction_ref); 
          
          }
          }
       
          


session.setAttribute("transaction-ref", transaction_ref);
        
        
 //CampaignHit Counter for LandingPage
    java.sql.Date today=new java.sql.Date(System.currentTimeMillis());
    if(CPAcampainId==null) CPAcampainId="";
    CampaignHitCounter campaignHitCounter=campaignhitcounterdao.HitRecordExistsOrNot(today,domain,CPAcampainId,landingPage);
    if(campaignHitCounter==null){
	campaignHitCounter=new CampaignHitCounter();
	campaignHitCounter.setaUnique(Misc.generateUniqueId());
	campaignHitCounter.setaDomainUnique(domain);
	campaignHitCounter.setCampaignId(campaignId);
	campaignHitCounter.setLandingPage(landingPage);
	campaignHitCounter.setDate(today);
	campaignHitCounter.setHitCounter(1);
	campaignHitCounter.setSubscribeCounter(0);
	campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);
	
}else{
	//campaignHitCounter.setDate(today);
	campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
}


if(!visitinserted)
trans.commit();
dbsession.close();


 PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    Writer stringWriter=new StringWriter();
    

    context.put("transref",pfitagHeader);
    context.put("contenturl","http://"+dmn.getContentUrl());


String output = writer.toString();

if(engine!=null){
    engine.getTemplate(landingPage).evaluate(writer, context);
}
%>
