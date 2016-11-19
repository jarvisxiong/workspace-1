<%@include file="coreimport.jsp" %>
<%@page import="com.zadoi.service.ZaDoi"%>
<%
ZaDoi zadoi = new ZaDoi();
long beforeAuth = System.currentTimeMillis();
String token = zadoi.authenticate();
SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String unSubscribed=sdf2.format(new Date());

UmeMobileClubUserDao clubuserdao=null;
MobileClubBillingPlanDao billingplandao=null;
try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      clubuserdao=(UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
      billingplandao=(MobileClubBillingPlanDao) ac.getBean("billingplandao");
      
    }
catch(Exception e){e.printStackTrace();}

//String serviceName="0feeeaa6-38c6-4187-87a5-ec0f95b3e839";
String msisdn=request.getParameter("msisdn");
if(msisdn==null)
msisdn="27723937357";//"27833260830";//"27723937357";//"27740821715";
//boolean responses=zadoi.delete_DoubleOptIn_Record(token, serviceName, msisdn);
MobileClub item=null;
String key="";
String serviceName="";
String message="";
 for (Iterator it = UmeTempCmsCache.mobileClubMap.keySet().iterator(); it.hasNext();) {
            key = (String) it.next();
            item = (MobileClub) UmeTempCmsCache.mobileClubMap.get(key);
            if(item.getRegion().equalsIgnoreCase("ZA"))
            {
                serviceName=item.getOtpServiceName();
                boolean responses=zadoi.delete_DoubleOptIn_Record(token, serviceName, msisdn);
                message+="Zadoi deletion done for msisdn "+msisdn+" with response "+responses +" for club "+item.getUnique()+":"+item.getName()+"\n";
System.out.println("Zadoi deletion done for msisdn "+msisdn+" with response "+responses +" for club "+item.getUnique()+":"+item.getName());

if(responses)
{
     SdcMobileClubUser clubUser=clubuserdao.getClubUserByMsisdn(msisdn,item.getUnique());
            if(clubUser!=null)
        {
             clubUser.setActive(0);
             clubUser.setUnsubscribed(SdcMiscDate.parseSqlDateString(unSubscribed));
             clubuserdao.saveItem(clubUser);
             
             int disabled=billingplandao.disableBillingPlan(msisdn, item.getUnique());
        }
}
                
            }
            
 }

%>

<%=msisdn%> <%=message%>