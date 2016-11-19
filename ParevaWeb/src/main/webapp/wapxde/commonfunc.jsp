<%@include file="coreimport.jsp"%>
<%!
void doRedirect(HttpServletResponse response, String url) 
{
    try 
    {
        response.sendRedirect(url);
    }
    catch (Exception e) 
    {
        System.out.println("common function exception " + e);
        e.printStackTrace();
    }
}

public String getStackTraceAsString(Exception e) {
		 
		StringWriter stringWriter = new StringWriter();		 
		PrintWriter printWriter = new PrintWriter(stringWriter);		 
		e.printStackTrace(printWriter);		 
		StringBuffer error = stringWriter.getBuffer();		 
		String allError= error.toString();		
		char[] chars=allError.toCharArray();		
		StringBuffer sb=new StringBuffer();		
		sb.append("wapxza Exception:");		
		for(int i=0;i<chars.length;i++){			
			if(chars[i]=='\n'){				
				sb.append("\nwapxza: ");
			}else{
				sb.append(chars[i]);
			}
		}		
		return (sb.toString());		
	}


%>


<%
SdcRequest aReq = new SdcRequest(request);
UmeUser user = aReq.getUser();
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();
String domain = dmn.getUnique();
String ddir = dmn.getDefPublicDir();
String lang = aReq.getLanguage().getLanguageCode();

String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));

UmeLanguagePropertyDao langpropdao=null;
HandsetDao handsetdao=null;
MobileClubDao mobileclubdao=null;
UmeTempCache umesdc=null;
RedirectSettingDao redirectsettingdao=null;
MobileClubCampaignDao campaigndao=null;
InternetServiceProvider ipprovider=null;
CpaAdvertiserDao advertiserdao=null;
CpaVisitLogDao cpavisitlogdao=null;
UserClicksDao userclicksdao=null;
LandingPage landingpage=null;
CampaignHitCounterDao campaignhitcounterdao=null;
RevShareVisitorLogDao revvisitorlogdao=null;
RevSharePartersDao revsharepartnersdao=null;
UmeUserDao umeuserdao=null;
UmeMobileClubUserDao umemobileclubuserdao=null;
MobileNetworksDao mobilenetwork=null;
MobileBillingDao mobilebillingdao=null;
VideoClipDao videoclipdao=null;
try{
     ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
    langpropdao=(UmeLanguagePropertyDao) ac.getBean("umelanguagepropertydao");
    handsetdao=(HandsetDao) ac.getBean("handsetdao");
    mobileclubdao=(MobileClubDao) ac.getBean("mobileclubdao");
    umesdc=(UmeTempCache) ac.getBean("umesdc");
    redirectsettingdao=(RedirectSettingDao) ac.getBean("redirectsettingdao");
    campaigndao=(MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
    ipprovider=(InternetServiceProvider) ac.getBean("internetserviceprovider");
    advertiserdao=(CpaAdvertiserDao) ac.getBean("cpaadvertiserdao");
     cpavisitlogdao=(CpaVisitLogDao) ac.getBean("cpavisitlogdao");
     userclicksdao=(UserClicksDao) ac.getBean("userclicksdao");
     landingpage=(LandingPage) ac.getBean("landingpage");
     campaignhitcounterdao=(CampaignHitCounterDao) ac.getBean("campaignhitcounterdao");
     revvisitorlogdao=(RevShareVisitorLogDao) ac.getBean("revsharevisitorlogdao");
     revsharepartnersdao=(RevSharePartersDao) ac.getBean("revsharepartnerdao");
     umeuserdao=(UmeUserDao) ac.getBean("umeuserdao");
     umemobileclubuserdao=(UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
     mobilenetwork=(MobileNetworksDao) ac.getBean("mobilenetworkdao");
     mobilebillingdao=(MobileBillingDao) ac.getBean("mobilebillingdao");
     videoclipdao=(VideoClipDao) ac.getBean("videoclipdao");
     
}catch(Exception e){e.printStackTrace();}

String cloudfronturl=dmn.getContentUrl();
session.setAttribute("cloudfrontUrl",cloudfronturl);
application.setAttribute("cloudfrontUrl",cloudfronturl);

%>
