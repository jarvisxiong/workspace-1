<%@include file="coreimport.jsp" %>
<%@include file="db.jsp" %>


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
   	String []itemDetails=new ItemTypeTicket().getDetails(item);
     
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
session.setAttribute("list",list);
boolean authnOk = false;
String menuItem="";
String link="";
String style1;
String style2;

java.util.List promo_hot_video_list=new ArrayList();
java.util.List promo_hot_video_category_list=new ArrayList();


for(int i=0;i<list.size();i++){
	
	String[] servicesList=(String[])list.get(i);
	for(int j=0;j<servicesList.length;j++)
	System.out.println("Services "+j +" = "+servicesList[j]);
	//String sqlstr = "";
	String srvc = servicesList[1];
	String fName=servicesList[3];
	if(fName.equals("promo_hot_video.jsp")){
		promo_hot_video_list.add(srvc);
		//hot_video=1;
	}	
	
	else if (fName.equals("promo_hot_video_category.jsp")){
		promo_hot_video_category_list.add(srvc);
		//hot_video_category=1;
	}
	
	
	}

session.setAttribute("promo_hot_video_list",promo_hot_video_list);
request.setAttribute("promo_hot_video_list",promo_hot_video_list);
application.setAttribute("promo_hot_video_list",promo_hot_video_list);
session.setAttribute("promo_hot_video_category_list",promo_hot_video_category_list);

//doMsisdnTrace( request, msisdn, "Rendering index_html with sub page " + subpage );

//System.out.println("PFI Tag Header/Transaction reference in index_main.jsp "+transaction_ref);

%>

<%
if(categoryX!=null &&categoryX.trim().length()>0){
    %>
       <jsp:include page="videos.jsp">
        <jsp:param name="cat" value="<%=categoryX%>" />
        <jsp:param name="transaction-ref" value="<%=transaction_ref%>" />
      </jsp:include> 
    
<%}else{
RequestDispatcher rd=getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_hot_video.jsp");
			    rd.forward(request,response);	

 
 } %>


