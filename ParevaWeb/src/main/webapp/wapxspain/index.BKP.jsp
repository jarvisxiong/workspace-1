l<%@include file="db.jsp"%>
<%@include file="commonfunc.jsp"%>
<%
String uid = "";
String wapid = "";
String msisdn = aReq.getMsisdn();
String mobilenumber="", parsedmobile="";


//====Required for CPA=====
String insertQuery="";
String campaignsrc="";
String visitsubscribed="1970-01-01 00:00:00";
String ipaddress=request.getRemoteAddr();
String isSubscribed="0";
String transaction_ref="zacpa";

//====CPA Requirement end ===========


Handset handset = handsetdao.getHandset(request);
if(handset!=null) session.setAttribute("handset",handset);

String campaignId = aReq.get("cid");
MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
String clubUnique = "";
if (club!=null) clubUnique = club.getUnique();

if(campaignId!=null && campaignId.trim().length()>0) {
    MobileClubCampaign cmpg = null;
   if (!campaignId.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
   if(cmpg!=null) campaignsrc = cmpg.getSrc();
    
    }

if(campaignsrc.trim().toLowerCase().contains("cpa"))
{
    //CPA Tracking Code comes here. 
}
if( user != null && club != null && mobileclubdao.isActive(user, club) && null == request.getParameter("id") && null != request.getParameter("incoming"))
{
    String userMobile=user.getMobile().toString().trim();
    String enMsi=MiscCr.encrypt(userMobile);
    boolean activeuser=mobileclubdao.isActive(user, club);
    String orgigettingUrl="";
    int counter=0;
    while(activeuser && counter<20)
    {        
            RedirectSetting setting=umesdc.getRedirectByUnique().get(domain);
            orgigettingUrl=setting.getRedirectUrl();
            String gettingUrl=orgigettingUrl;
            
         if(gettingUrl.contains(".mobi"))
        {
         gettingUrl=gettingUrl.substring(0,gettingUrl.indexOf(".mobi"));
         gettingUrl=gettingUrl+".mobi";
        }
         
        if(gettingUrl.contains(".co.uk"))
        {
             gettingUrl=gettingUrl.substring(0,gettingUrl.indexOf(".co.uk"));
         gettingUrl=gettingUrl+".co.uk";
        }
           if(gettingUrl.contains(".com"))
        {
         gettingUrl=gettingUrl.substring(0,gettingUrl.indexOf(".com"));
         gettingUrl=gettingUrl+".com";
        }
           if(gettingUrl.contains(".tv"))
        {
             gettingUrl=gettingUrl.substring(0,gettingUrl.indexOf("tv"));
         gettingUrl=gettingUrl+".tv";
        }
           
        RedirectSetting settings=redirectsettingdao.getDomainDetails(gettingUrl);
        domain=settings.getDomainUnique();
        club = UmeTempCmsCache.mobileClubMap.get(domain);
        activeuser=mobileclubdao.isActive(user,club);
        counter++;
        
    }
    doRedirect(response,"http://" + orgigettingUrl +"&id=" + user.getWapId()+"&mid="+enMsi+"&logtype=redirect");
    return;
}
try 
{ 
  
     //System.out.println("wapzatest test: "++"New Global wap header: "+" WAPXMADAN"+" /" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp");
	application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request,response); 
}
catch (Exception e) 
{
    String exception_=getStackTraceAsString(e);             
   System.out.println("wapxza "+" exception "+e); System.out.println(exception_);
e.printStackTrace();
}


%>


