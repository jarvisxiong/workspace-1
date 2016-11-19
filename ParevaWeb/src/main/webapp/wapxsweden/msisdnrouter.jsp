<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.DateTime"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>
<%@include file="cpavisit.jsp"%>



<%
String step2request=httprequest.get("step2request");

System.out.println("irevodafone msisdnrouter called upon with step2request "+step2request);
PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();
String msisdn="";
if(step2request!=null && !step2request.isEmpty() && step2request.equalsIgnoreCase("true")) {
    
       System.out.println("irevodafone msidnrouter step2 request is initiated");
 String httpsrequest="https://msisdn.sla-alacrity.com/authenticate/msisdn";
 System.out.println("irevodafone step 2 calling up "+httpsrequest);
 
 String username= "txtnationl_731_live";
 String pass="ndondawutu";

 

 HttpURLConnectionWrapper urlwrapper=new HttpURLConnectionWrapper(httpsrequest);
     Map<String, String> ireMap=new HashMap<String,String>();
     
        
        ireMap.put("uri", "partner:91909c32-e422-42e3-845a-d3cbf15af4fa");
        ireMap.put("transaction_id",request.getParameter("transactionid"));
        
        System.out.println("irevodafone msisdnrouter transctionid "+request.getParameter("transactionid"));
        
        Map<String,String> headers = new HashMap<String,String>();
        String basic_auth=DatatypeConverter.printBase64Binary((username+":"+pass).getBytes("UTF-8"));
        headers.put("Authorization", "Basic " + basic_auth);
        headers.put("Accept","application/json");
        
        urlwrapper.wrapGet(ireMap,headers);
    
        String responsecode=urlwrapper.getResponseCode();
        System.out.println("irevodafone:  msidnrouter response from msisdn router  "+responsecode+" -- "+urlwrapper.getResponseMessage()+" -- "+urlwrapper.getResponseContent());
        
        
        
        
        if(urlwrapper.isSuccessful()){
            String responsedesc=urlwrapper.getResponseContent();
           msisdn= parseForMsisdn(responsedesc);  
           if(null!=msisdn && !"".equals(msisdn)) {
               context.put("msisdnexist","true");
                System.out.println("txtnation msisdn exist is true "+msisdn);
               
           }
           
        }

        context.put("msisdn",msisdn);
     engine.getTemplate("msisdnparser").evaluate(writer, context); 
    
}





%>
<%!

private static String parseForMsisdn(String data){

		Map<String, Map<String,String>> mapData = new HashMap<String, Map<String,String>>();
		String msisdn = null;
                System.out.println("txtnation data " +data);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			mapData = objectMapper.readValue(data, Map.class);
			
			Map<String,String> innerMap = (mapData.get("msisdn_protection"));
			if(innerMap!=null){
				String value = innerMap.get("msisdn");
                                System.out.println("txtnation value is "+value);
				if(value!=null){
					String[] tels = value.split(":");
					msisdn = tels[1];
                                        
                                        System.out.println("txtnation msisdn is "+msisdn);
                                        
				}
			}else{ //
				System.out.println("txtnation msisdn_protection is null!");	
			}
		} catch (Exception ex) {
			System.out.println("txtnation Couldn't parse!");
		} finally {
			// to avoid null pointer exceptions
			if(msisdn==null)msisdn="";
		}
                System.out.println("txtnation returned msisdn is "+msisdn);
		return msisdn;
	}

  String turnParametersMapIntoString(Map<String, String> parameters) {

        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (String key : parameters.keySet()) {
            sb.append(key);
            if (!parameters.get(key).isEmpty()) {//
                sb.append("=");
                sb.append(parameters.get(key));
            }
            if (count < parameters.size()) {
                count++;
                sb.append("&");
            }
        }
        System.out.println("txtnation parameters "+sb.toString());
        return sb.toString();
    }
	



%>