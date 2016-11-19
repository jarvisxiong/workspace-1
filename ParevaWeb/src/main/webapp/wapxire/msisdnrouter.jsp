<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="ume.pareva.ire.MsisdnPassingResponse"%>
<%@page import="ume.pareva.ire.IEutil"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.DateTime"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>




<%


PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();
String step2request=httprequest.get("step2request");
System.out.println("irevodafone msisdnrouter called upon with step2request "+step2request);
String msisdn="";
String pubId=(String) session.getAttribute("cpapubid");
String landingPage=(String) session.getAttribute("landingPage");
MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);

String campaignid=request.getParameter("cid");
if(campaignid==null) campaignid=(String) session.getAttribute("cid");
if(campaignid==null) campaignid="";

if(pubId==null) pubId="";
if(landingPage==null) landingPage="";

String httpsrequest="",message="";
int responseCode=-1;
StringBuilder sb = new StringBuilder();
StringBuilder jsonsb=new StringBuilder();



if(step2request!=null && !step2request.isEmpty() && step2request.equalsIgnoreCase("true")) {
//IEutil pfiUtil=new IEutil();
String transactionid=request.getParameter("transactionid");

System.out.println("irevodafone  step2 transactionid: "+transactionid);
httpsrequest="https://msisdn.sla-alacrity.com/authenticate/msisdn?uri=partner:91909c32-e422-42e3-845a-d3cbf15af4fa&transaction_id="+request.getParameter("transactionid");
 System.out.println("irevodafone msisdncheck step 2 calling up "+httpsrequest);
 
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
		System.out.println("irevodafone HTTP URL Connection Sending 'GET' request to URL : " + httpsrequest);
		System.out.println("irevodafone HTTPURLConnection  Response Code : " + responseCode+" message- "+message);
                
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

                
}

System.out.println("irevodafone sb value is "+sb);
System.out.println("irevodafone step2 msisdn value is "+msisdn);

if(msisdn!=null && !msisdn.trim().equalsIgnoreCase("")) {
    context.put("msisdnexist","true");
    if(campaignid!=null && !"".equalsIgnoreCase(campaignid)){
System.out.println(request.getParameter("cid")+" msisdnrouter "+context.get("msisdnexist")+ " "+msisdn);
campaigndao.log("iemsisdnrouter", landingPage, msisdn,msisdn, handset,domain, campaignid, club.getUnique(), "IDENTIFIED", 0, request,response,"vodafone","","","",pubId);   
session.setAttribute("vodafoneidentified",true);

}
}


context.put("msisdn",msisdn);
engine.getTemplate("msisdnparser").evaluate(writer, context); 

%>

<%!

private static String parseForMsisdn(String data){

		Map<String, Map<String,String>> mapData = new HashMap<String, Map<String,String>>();
		String msisdn = null;
                System.out.println("irevodafone parser txtnation data " +data);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			mapData = objectMapper.readValue(data, Map.class);
			
			Map<String,String> innerMap = (mapData.get("msisdn_protection"));
			if(innerMap!=null){
				String value = innerMap.get("msisdn");
                                System.out.println("irevodafone parser txtnation value is "+value);
				if(value!=null){
					String[] tels = value.split(":");
					msisdn = tels[1];
                                        
                                        System.out.println("irevodafone parser txtnation msisdn is "+msisdn);
                                        
				}
			}else{
				System.out.println("irevodafone parser txtnation msisdn_protection is null!");	
			}
		} catch (Exception ex) {
			System.out.println("irevodafone parser txtnation Couldn't parse!");
		} finally {
			// to avoid null pointer exceptions
			if(msisdn==null)msisdn="";
		}
                System.out.println("irevodafone parser txtnation returned msisdn is "+msisdn);
		return msisdn;
	}
	



%>