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
		
		 if (ip != null) {           
             int idx = ip.indexOf(',');
             if (idx > -1) {
             ip = ip.substring(0, idx);
             }
      }
		 
		return ip;
	}
%>
