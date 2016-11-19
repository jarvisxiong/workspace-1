<%@include file="coreimport.jsp" %>
<%
    String categoryX=request.getParameter("cat");
String landingloaded=(String) request.getAttribute("landingPage");
             //System.out.println("LANDING PAGE IN INDEX_MAIN "+landingloaded);
             
               if(landingloaded==null || landingloaded.equalsIgnoreCase("")) {
                 landingloaded=(String) session.getAttribute("landingPage");
             }            
        
             
             request.setAttribute("landingPage",landingloaded);
 
/**
 *   request.setAttribute("merchanttoken",merchanttoken);
            session.setAttribute("merchanttoken",merchanttoken);
    session.setAttribute("sessiontoken",transaction_ref);
                request.setAttribute("sessiontoken",transaction_ref);
 */

 String merchanttoken=(String) request.getAttribute("merchanttoken");
 if(merchanttoken==null) merchanttoken=(String) session.getAttribute("merchanttoken");
 
 String sessiontoken=(String) request.getAttribute("sessiontoken");
 if(sessiontoken==null) sessiontoken=(String) session.getAttribute("sessiontoken");
 
    response.addHeader("X-PFI-MerchantToken",merchanttoken);
    response.addHeader("X-PFI-SessionToken", sessiontoken);

%>
<jsp:include page="/MainIndex"/>
<%
  String redirecturl=(String) request.getParameter("redirectto");
        if(redirecturl==null) redirecturl=(String) request.getAttribute("redirectto");
        if(redirecturl==null) redirecturl=(String) session.getAttribute("redirectto");
        if(redirecturl!=null)
        {
            response.sendRedirect(redirecturl);
            return;
        }



%>
<%
if(categoryX!=null &&categoryX.trim().length()>0){
    %>
       <jsp:include page="videos.jsp">
        <jsp:param name="cat" value="<%=categoryX%>" />
        <jsp:param name="transaction-ref" value="<%=sessiontoken%>" />
      </jsp:include> 
    
<%}%>