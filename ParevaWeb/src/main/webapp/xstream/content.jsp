<%@include file="coreimport.jsp"%>
<%@include file="ukheader.jsp"%>

<%
//a

System.out.println("xstreamtesting: On Content.jsp ");

//Testing Cookies 
Cookie[] cookies = null;
        //System.out.println("xstreamtesting: Content.jsp before Cookie loop "+session.getId());
        cookies = request.getCookies();
         if( cookies != null ){
      
         for (Cookie cookie:cookies){
            //cookie = cookies[];
             System.out.println("xstreamtesting: UKService: "+cookie.getName()+": "+cookie.getValue());
         }
         }





//Testing Cookies









String transaction_ref = "";
//Reading IMI Header HTTP_X_ESC_ALIAS from IMI
String pfitagHeader="";
String CPAcampainId = "";
boolean visitinserted=false;
try{
CPAcampainId=request.getParameter("cid");
request.setAttribute("campaignid",CPAcampainId);
session.setAttribute("campaignid",CPAcampainId);
//System.out.println("CAMPAIGNID IN INDEX.jsp "+CPAcampainId+" in attribute "+request.getAttribute("campaignid"));
}catch(Exception e){CPAcampainId="";}
try{
pfitagHeader=httprequest.get("msisdn");

if(pfitagHeader!=null && pfitagHeader.trim().length()>0)
{
    transaction_ref=httprequest.get("transref"); 
    msisdn=pfitagHeader;
    session.setAttribute("ukidentified",msisdn);
    System.out.println("xstreamtesting: msisdn received is "+pfitagHeader);
}
} catch(Exception e){}
System.out.println("PFI Tag Header index.jsp "+pfitagHeader);

if(transaction_ref==null || transaction_ref.trim().length()<=0) transaction_ref="x";//misc.generateUniqueId();

java.util.Enumeration names = request.getHeaderNames();
        while(names.hasMoreElements()){
            String name = (String) names.nextElement();
            System.out.println("PFI Header:> "+name + ":" + request.getHeader(name) + "");
        }

System.out.println("content Transaction Reference "+transaction_ref);

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
        String queryString = sb.toString();

    //========CPA Visit Log End ==================    
    
          if(CPAcampainId!=null && CPAcampainId.trim().length()>0){
   if(session.getAttribute("transaction-ref")==null && (useragent!=null &&
                (!useragent.trim().toLowerCase().contains("elb-healthchecker/1.0") && !useragent.trim().toLowerCase().contains("pingdom")) 
                )){
      
campaigndao.log("ukcontent", "xhtml", transaction_ref, msisdn, handset,domain, CPAcampainId, clubUnique, "INDEX", 0, request,response,"imi");
   if(msisdn.trim().length()>0 && pfitagHeader.trim().length()>0 && !msisdn.trim().equalsIgnoreCase("xxxx"))
       campaigndao.log("ukcontent", "xhtml", transaction_ref, msisdn, handset,domain, CPAcampainId, clubUnique, "IDENTIFIED", 0, request,response,"imi");
System.out.println("CampaignDao Logging "+ "Transaction reference "+transaction_ref); 
          
          }
          }

session.setAttribute("transaction-ref", transaction_ref);

%>
<%@include file="index_main.jsp"%>