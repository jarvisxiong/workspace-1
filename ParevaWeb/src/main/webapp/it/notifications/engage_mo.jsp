<%@page import="ume.pareva.pojo.EngageDRParameters,ume.pareva.uk.EngageMO"%>
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>


<%
System.out.println("ENGAGEMO User: "+request.getParameter("USER"));
System.out.println("ENGAGEMO Password: "+request.getParameter("Password"));
System.out.println("ENGAGEMO RequestID: "+request.getParameter("RequestID"));
System.out.println("ENGAGEMO XML: "+request.getParameter("TP_XML"));
//String tpXML="<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><!DOCTYPE WIN_TPBOUND_MESSAGES SYSTEM \"tpbound_messages_v1.dtd\"><WIN_TPBOUND_MESSAGES><SMSTOTP><SOURCE_ADDR>+447333111111</SOURCE_ADDR><TEXT>MoonB</TEXT><WINTRANSACTIONID>984317322</WINTRANSACTIONID><DESTINATION_ADDR>81222</DESTINATION_ADDR><SERVICEID>2</SERVICEID><NETWORKID>6</NETWORKID><ARRIVALDATETIME><DD>18</DD><MMM>AUG</MMM><YYYY>2015</YYYY><HH>12</HH><MM>32</MM></ARRIVALDATETIME></SMSTOTP></WIN_TPBOUND_MESSAGES>";

EngageDRParameters engageDRParameters=new EngageDRParameters();
engageDRParameters.setUser(request.getParameter("USER"));
engageDRParameters.setPassword(request.getParameter("Password"));
engageDRParameters.setRequestId(request.getParameter("RequestID"));
engageDRParameters.setTpXML(request.getParameter("TP_XML")); //



EngageMO engageMO=null;

try{
     ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
     ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
     engageMO=(EngageMO) ac.getBean("engagemo");
     engageMO.processRequest(engageDRParameters);
     }
     catch(Exception e){
         e.printStackTrace();
     }


%>
