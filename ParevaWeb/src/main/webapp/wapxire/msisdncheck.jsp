<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.net.URL"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.DateTime"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>



<%

String transactionid=request.getParameter("transactionid");
String uri="partner:91909c32-e422-42e3-845a-d3cbf15af4fa";
System.out.println("manualvodafone msisdnrouter called upon with step2request "+transactionid);
PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();
String httpsrequest="",message="";
int responseCode=-1;
StringBuilder sb = new StringBuilder();
StringBuilder jsonsb=new StringBuilder();
String msisdn="";

    
       System.out.println("manualvodafone msidnrouter step2 request is initiated");
       
 
 httpsrequest="https://msisdn.sla-alacrity.com/authenticate/msisdn?uri=partner:91909c32-e422-42e3-845a-d3cbf15af4fa&transaction_id="+request.getParameter("transactionid");
 System.out.println("manualvodafone msisdncheck step 2 calling up "+httpsrequest);
 
  String username="txtnationl_731_live";
    String pass="ndondawutu";

    
    String userpass=username+":"+pass;
    String encoding ="Basic "+ new sun.misc.BASE64Encoder().encode(userpass.getBytes());
            URL obj = new URL(httpsrequest);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("Authorization", encoding);
            con.setRequestProperty("Accept", "application/json");
            
            responseCode = con.getResponseCode();  
            message=con.getResponseMessage();
             //engine.getTemplate("msisdnparser").evaluate(writer, context); 
		System.out.println("manualvodafone HTTP URL Connection Sending 'GET' request to URL : " + httpsrequest);
		System.out.println("manualvodafone HTTPURLConnection  Response Code : " + responseCode+" message- "+message);
                
            sb.append("responsecode: "+responseCode+"\n"+ "message : "+message+"\n");
            try{    
           switch (responseCode) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                    jsonsb.append(line+"\n");
                }
                br.close();
                
        }
           String data=jsonsb.toString();
           msisdn=parseForMsisdn(data);

    } catch (Exception ex) {
       
            sb.append("error "+ex);
    }finally {
       if (con != null) {
          try {
              con.disconnect();
          } catch (Exception ex) {
             
            sb.append("error "+ex);
          }
       }
    }

                
                
                
                
System.out.println("manualvodafone HttpUrlConnection SB OUTPUT "+sb);

				
                %>

Message - <%=sb%> <br/>
JsonSb - <%=jsonsb%> <br/>
msisdn- <%=msisdn%> <br/>

<%-- Request sent to  : <%=httpsrequest%> <br/>
Responsecode : <%=responseCode%>
Message : <%=message%> --%>

   

<%!

private static String parseForMsisdn(String data){

		Map<String, Map<String,String>> mapData = new HashMap<String, Map<String,String>>();
		String msisdn = null;
                System.out.println("manualvodafone txtnation data " +data);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			mapData = objectMapper.readValue(data, Map.class);
			
			Map<String,String> innerMap = (mapData.get("msisdn_protection"));
			if(innerMap!=null){
				String value = innerMap.get("msisdn");
                                System.out.println("manualvodafone txtnation value is "+value);
				if(value!=null){
					String[] tels = value.split(":");
					msisdn = tels[1];
                                        
                                        System.out.println("manualvodafone txtnation msisdn is "+msisdn);
                                        
				}
			}else{
				System.out.println("manualvodafone txtnation msisdn_protection is null!");	
			}
		} catch (Exception ex) {
			System.out.println("manualvodafone txtnation Couldn't parse!");
		} finally {
			// to avoid null pointer exceptions
			if(msisdn==null)msisdn="";
		}
                System.out.println("manualvodafone txtnation returned msisdn is "+msisdn);
		return msisdn;
	}
	



%>

