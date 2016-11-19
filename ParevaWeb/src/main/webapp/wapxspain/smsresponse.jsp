<%@include file="coreimport.jsp"%>

<%
	
	UmeSessionParameters aReq = new UmeSessionParameters(request);
	UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");	
	String mobileno="";
	String campaignid=aReq.get("cid");
    String phase=request.getParameter("phase");
    System.out.println("PASE RECEIVED IS "+phase);
   
    
    if (phase != null && phase.trim().length() > 0 && phase.trim().equals("1")) {
        
        String optin = aReq.get("optin");
    String requestreference = aReq.get("requestreference");
        String serviceidentifier = aReq.get("serviceidentifier");
        String result = aReq.get("result");//if result is confirm then go to confirm page same as wap
        mobileno = aReq.get("msisdn").trim();
        String errordescription = aReq.get("errordescription");
        String operatorid = aReq.get("operatorid");
        String timestamp = aReq.get("timestamp");
        String stsClubName=aReq.get("clubname");
        boolean validmsisdn=checkMsisdn(mobileno);
        
         if(mobileno!=null && mobileno.trim().length()>0){
         if(mobileno.contains("+")) mobileno=mobileno.replace("+","").trim();
         }
         
         DoiResult doiresult=null;
         try{
              ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
              ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
              doiresult=(DoiResult) ac.getBean("doiresult");
              //doiresult.saveDOIResult(mobileno,"smsoptin",campaignid,stsClubName,timestamp,result,operatorid);
              }
              catch(Exception e){
                  e.printStackTrace();
              }
         
         
       
      if(!validmsisdn){
          if (result != null
			&& (result.trim().toUpperCase().equals("CONFIRM")                                
                         || result.trim().toUpperCase().equals("ERROR")
                         || result.trim().toUpperCase().equals("TERMINATE")
                         || result.trim().toUpperCase().equals("DECLINE")
                         || result.trim().toUpperCase().equals("DECLINED"))) {
             
	String redirecturl = "http://" + dmn.getDefaultUrl()
				+ "/zaconfirm.jsp?optin=1&cid=" + campaignid
				+ "&requestreference=" + requestreference
				+ "&serviceidentifier=" + serviceidentifier
                                 + "&result="+result
				+ "&submsisdn=" + mobileno.trim() + "&errordescription="
				+ errordescription + "&operatorid=" + operatorid
				+ "&timestamp=" + timestamp+"&optintype=smsoptin";
         response.sendRedirect(redirecturl);
         return;
      }// end result 
          else{
              response.sendRedirect(dmn.getDefaultUrl()); return;
          }
        
    }// end valid msisdn
            
 } //End phase param

 boolean NotvalidMobileNo=checkMsisdn(mobileno);
if(NotvalidMobileNo)
    {
       response.sendRedirect("http://" + dmn.getDefaultUrl()+ "/index.jsp?cid=" + campaignid);
       return;
    }


%>

<%!
    boolean checkMsisdn(String mobileno){
       
        boolean validmsisdn=(mobileno != null && !mobileno.trim().equals(""))
			&& (mobileno.length() < 8 || (!mobileno.trim().startsWith("07")
					&& !mobileno.trim().startsWith("08")
                                        && !mobileno.trim().startsWith("06")
					&& !mobileno.trim().startsWith("0027") && !mobileno.trim().startsWith("27")));
        
        
        return validmsisdn;
        
    }



%>