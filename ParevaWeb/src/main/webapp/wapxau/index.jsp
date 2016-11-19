<%@include file="auheader.jsp"%>

<%
String previewLandingPage=httprequest.get("previewLandingPage");
if(""!=previewLandingPage){
previewLandingPage=previewLandingPage.substring(0,previewLandingPage.indexOf("."));
System.out.println("previewLandingPage: "+previewLandingPage);
}

if(!previewLandingPage.equals("")){
    String msisdnexist=httprequest.get("msisdnexist");
    String previewNetwork=httprequest.get("previewNetworkInput");
    System.out.println("previewNetwork "+previewNetwork);
	PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
	TemplateEngine templateengine=null;
        
        if("".equals(msisdnexist)) msisdnexist="false";
        context.put("msisdnexist",msisdnexist);
        
        context.put("contenturl","http://"+dmn.getContentUrl());
        context.put("operator",previewNetwork);


	try{
     ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
     ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
     templateengine=(TemplateEngine) ac.getBean("templateengine");
       
     }
     catch(Exception e){
         e.printStackTrace();
     }
	PebbleEngine engine=templateengine.getTemplateEngine(dmn.getUnique());
	engine.getTemplate(previewLandingPage).evaluate(writer, context); 

}
else{
    String myip=request.getHeader("X-Forwarded-For");
    String  campaignsrc="";
    String visitsubscribed="1970-01-01 00:00:00";
    String isSubscribed="0";
String transaction_ref="aucpa";

if (myip != null) {           
           int idx = myip.indexOf(',');
           if (idx > -1) {
           myip = myip.substring(0, idx);
           }
    }
session.setAttribute("userip",myip);
request.setAttribute("userip",myip);

if(null!=cmpg && cmpg.getSrc().endsWith("CPA")) {
    campaignsrc = cmpg.getSrc();
    %>
    	<%@include file="cpavisitlog.jsp"%>
<% }

if(null!=cmpg && cmpg.getSrc().endsWith("RS")) {
    
campaignsrc = cmpg.getSrc();
%>   
	<%@include file="revsharevisitlog.jsp"%>
<% }

String myisp=ipprovider.findIsp(myip);
String landingPage="";


if(myisp!=null){ // && !myisp.equals("unknown")){	

if(!campaignId.equals("")){
		landingPage=landingpage.initializeLandingPage(domain,campaignId,"all");
                request.setAttribute("landingPage",landingPage);
                
	}else {
		landingPage=landingpage.initializeLandingPage(domain);
                request.setAttribute("landingPage",landingPage);
	}
 
}
else {
  landingPage=landingpage.initializeLandingPage(domain);
  	request.setAttribute("landingPage",landingPage);
}
java.sql.Date today=new java.sql.Date(System.currentTimeMillis());
CampaignHitCounter campaignHitCounter=campaignhitcounterdao.HitRecordExistsOrNot(today,domain,campaignId,landingPage);
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
try 
{ 
    
    if(!httprequest.get("cid").equalsIgnoreCase("")){
         
       /****************************************************************************************************************************************/
       
       //System.out.println("=========== MYISP IS ================ "+myisp);
     //campaigndao.log("index", "xhtml", "", "", handset,domain, campaignId, clubUnique, "INDEX", 0, request,response,myisp);
  System.out.println("***********************************au logging campaing id********************************************");
        campaigndao.log("index", landingPage, msisdn,msisdn, handset,domain, campaignId, clubUnique, "INDEX", 0, request,response,myisp);
        Cookie cookie = new Cookie("cid",campaignId);
        cookie.setMaxAge(168*60*60); 
        response.addCookie(cookie);
        
        Cookie cookie1=new Cookie("landingpage",landingPage);
        cookie1.setMaxAge(168*60*60);
        response.addCookie(cookie1);
    
    }
}
    catch(Exception e){System.out.println("ZA Index Error for campaignlog "+e); e.printStackTrace();}

PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();

 context.put("landingpage",landingPage);
 context.put("campaignid",campaignId);
 context.put("sendAction","processau.jsp");
 context.put("contenturl","http://"+dmn.getContentUrl());
 au_engine.getTemplate(landingPage).evaluate(writer, context);

    
}

%>
