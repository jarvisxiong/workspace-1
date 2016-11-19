<%!
Map<String,String> getCampaignFromReferer(String refererUrl){
    String delimiter="?";
        String delimeter2="&";
        refererUrl=refererUrl.replace(delimiter,delimeter2);
        
        String[] params = refererUrl.split("&"); 
        
    Map<String, String> map = new HashMap<String, String>();  
    String value="";
    for (String param : params)  
    {  
        if(param.contains("=")){
        String name = param.split("=")[0];  
        if(param.length()>1)
        value = param.split("=")[1];  
        
        map.put(name, value);  
        }
    }
    return map;
    }

String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

boolean insertQueryString(Session dbsession,String transaction_ref,String queryString,String nowdate){
    boolean inserted=false;
    String queryStringSql="";
    Query query=null;
    try{
        
		queryStringSql = "INSERT into ukQueryStringlog(transactionRef,queryString,aCreated) "
				+ "VALUES ('"
				+ transaction_ref
				+ "','"
				+ queryString 
				+ "','"
				+nowdate
				+"')";
     query=dbsession.createSQLQuery(queryStringSql);
     query.executeUpdate();
     inserted=true;
    }
    catch(Exception e){System.out.println("ukQueryStringlog Exception : "+e); e.printStackTrace();inserted=false;}
    return inserted;
}

boolean insertCpaVisitLog(Session dbsession,String transaction_id, String aHashcode,String aParsedMobile,String aCampaignId,String aCreated,String aSubscribed,String isSubscribed,String aSrc,String publisherid,String cpacampaignid,String clickid,String ip)
{
    boolean inserted=false;
    String insertQuery="";
    Query query=null;
    try{
       insertQuery="INSERT INTO cpavisitlog(transaction_id, aHashcode,aParsedMobile,aCampaignId,aCreated,aSubscribed,isSubscribed,aSrc,publisherid,cpacampaignid,clickid,ip) "
               + "VALUES ('"+transaction_id+"','"+aHashcode+"','"+aParsedMobile+"','"+aCampaignId+"','"+aCreated+"','"+aSubscribed+"','"+isSubscribed+"','"+aSrc+"','"+publisherid+"','"+cpacampaignid+"','"
               + clickid+"','"+ip+"')";
     System.out.println("CPAVISIT Query : "+insertQuery);
       query=dbsession.createSQLQuery(insertQuery);
     query.executeUpdate();
     inserted=true;
     
    }
    catch(Exception e){System.out.println("cpavisitlog Entry error : "+e);e.printStackTrace(); inserted=false;}
    
    return inserted;
}
%>
