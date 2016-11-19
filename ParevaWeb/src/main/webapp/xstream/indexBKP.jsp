<%@include file="coreimport.jsp"%>
<%@include file="db.jsp"%>
<%@include file="ukheader.jsp"%>
<%@include file="cpavisit.jsp"%>

<%
//a
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
pfitagHeader=request.getHeader("X-ESC-Alias");

if(pfitagHeader!=null && pfitagHeader.trim().length()>0)
{
    transaction_ref=pfitagHeader; 
    msisdn=pfitagHeader;
    session.setAttribute("ukidentified",msisdn);
    
}
} catch(Exception e){}
System.out.println("PFI Tag Header index.jsp "+pfitagHeader);

if(transaction_ref==null || transaction_ref.trim().length()<=0) transaction_ref=misc.generateUniqueId();

java.util.Enumeration names = request.getHeaderNames();
        while(names.hasMoreElements()){
            String name = (String) names.nextElement();
            System.out.println("PFI Header:> "+name + ":" + request.getHeader(name) + "");
        }

System.out.println("Index.jsp Transaction Reference "+transaction_ref);

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
    
          if(CPAcampainId!=null && CPAcampainId.trim().length()>0){
   if(session.getAttribute("transaction-ref")==null && (useragent!=null &&
                (!useragent.trim().toLowerCase().contains("elb-healthchecker/1.0") && !useragent.trim().toLowerCase().contains("pingdom")) 
                )){
      
campaigndao.log("index", "xhtml", transaction_ref, msisdn, handset,domain, CPAcampainId, clubUnique, "INDEX", 0, request,response,"imi");
   if(msisdn.trim().length()>0 && pfitagHeader.trim().length()>0 && !msisdn.trim().equalsIgnoreCase("xxxx"))
       campaigndao.log("index", "xhtml", transaction_ref, msisdn, handset,domain, CPAcampainId, clubUnique, "IDENTIFIED", 0, request,response,"imi");
System.out.println("CampaignDao Logging "+ "Transaction reference "+transaction_ref); 
          
          }
          }

session.setAttribute("transaction-ref", transaction_ref);
if(!visitinserted)
trans.commit();
dbsession.close();

 PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    Writer stringWriter=new StringWriter();
    

    context.put("transref",pfitagHeader);
    context.put("contenturl","http://"+dmn.getContentUrl());

//engine.getTemplate("landingpage").evaluate(writer, context);
String output = writer.toString();

           	
            	if(engine!=null){
                    
                    String landingPage="";
                    try{
                        
                        landingPage=landingpage.initializeLandingPage(domain);
                    
                    }
                    catch(Exception e){landingPage="landing-page";}
            			engine.getTemplate(landingPage).evaluate(writer, context);
            
            	}else
            	{
                engine.getTemplate("landing-page").evaluate(writer, context);
            	}
%>
<%--<%@include file="index_main.jsp"%>--%>