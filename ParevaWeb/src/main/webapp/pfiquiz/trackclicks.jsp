<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>
<%
Enumeration parameterlist=request.getParameterNames();
while(parameterlist.hasMoreElements()){
    
    String param=(String) parameterlist.nextElement();
    System.out.println("trackclist - "+param+":"+request.getParameter(param));
    
}

String aCampaign=httprequest.get("cid");
String referenceid=httprequest.get("referenceid");
String serviceid=httprequest.get("serviceid");
String aParsedMobile=httprequest.get("msisdn");
String tracklandingpage=httprequest.get("l");
String sessiontoken=httprequest.get("sessiontoken");
String action=httprequest.get("action");
String region="UK";
String sqlstr="INSERT INTO trackClick(aUrl,aCampaign,referenceid,serviceid,aParsedMobile,aLandingPage,aSessionToken,aCreated,aAction,aRegion) "+
        " VALUES ('"+domain+"','"+aCampaign+"','"+referenceid+"','"+serviceid+"','"+Misc.encodeForDb(aParsedMobile)+"','"+tracklandingpage+"','"+sessiontoken+"','"+
        SdcMiscDate.toSqlDate(new Date())+"','"+action+"','"+region+"')";


int updateclicks=zacpalog.executeUpdateCPA(sqlstr);


%>