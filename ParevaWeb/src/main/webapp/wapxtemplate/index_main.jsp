
<%
String categoryX=request.getParameter("cat");
String domainName = System.getProperty(domain + "_url");
boolean subscribe = false;

Cookie ck = new Cookie("_MXMSUBPAGE", subpage);
ck.setPath("/");
ck.setMaxAge(-1);
response.addCookie(ck);

String adultcontent = dParamMap.get("adultcontent");
if (adultcontent==null) 
	adultcontent = "0";

if (adultcontent.equals("1")) {
    String referer = request.getHeader("referer");
    
	if (referer==null) 
		referer = "";
     
  }

String item = httprequest.get("i");
String unq = httprequest.get("uq");
String itype = httprequest.get("tp");

if (!item.equals("")) {

    String ticket = "";    
    
    if (itype.equals("")) {
       
        Transaction indextrans=dbsession.beginTransaction();
   	String []itemDetails=new ItemTypeTicket().getDetails(dbsession, item);
     
	item=String.valueOf(itemDetails[0]) ; 
	itype= String.valueOf(itemDetails[1]);
	ticket=String.valueOf(itemDetails[2]);
     
        if(item==null || item.trim().length()<=0){           
		item = "";
				
            }
        indextrans.commit();
        dbsession.close();
  
	
    }

   
}

java.util.List list = UmeTempCmsCache.clientServices.get(domain + subpage);
request.setAttribute("list",list);
boolean authnOk = false;
String menuItem="";
String link="";
String style1;
String style2;

//doMsisdnTrace( request, msisdn, "Rendering index_html with sub page " + subpage );

System.out.println("PFI Tag Header/Transaction reference in index_main.jsp "+transaction_ref);

%>

<%
if(categoryX!=null &&categoryX.trim().length()>0){
    %>
       <jsp:include page="videos.jsp">
        <jsp:param name="cat" value="<%=categoryX%>" />
        <jsp:param name="transaction-ref" value="<%=transaction_ref%>" />
      </jsp:include> 
    
<%}else{%>
<jsp:include page="promo_hot_video.jsp">
<jsp:param name="transaction-ref" value="<%=transaction_ref%>" />
</jsp:include> 
<% } %>


