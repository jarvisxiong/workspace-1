<%@page import="ume.pareva.revshare.RevSharePartners"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="ume.pareva.revshare.RevShareVisitorLog"%>
<%
    
SimpleDateFormat revsharesdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Calendar nowtime = new GregorianCalendar();
String ipbitterAddress=getClientIpAddr(request);

String param1="",param2="",revparam1="",revparam2="",revparam3="",param3="",cpaquery="";
RevSharePartners advertiser=null;

if(campaignsrc.trim().endsWith("RS")) {
    advertiser=revsharepartnersdao.getAdvertiser(campaignsrc);
    
    System.out.println(advertiser);
    if(advertiser.getParameter1()!=null && advertiser.getParameter1().trim().length()>0)
    {        
        param1="tracker_id";
        revparam1=request.getParameter(advertiser.getParameter1());
    }
    if(advertiser.getParameter2()!=null && advertiser.getParameter2().trim().length()>0)
    {
        
        param2="cpacampaignid";
        revparam2=request.getParameter(advertiser.getParameter2());
    }
    if(advertiser.getParameter3()!=null && advertiser.getParameter3().trim().length()>0)
    {       
        param3="clickid";
        revparam3=request.getParameter(advertiser.getParameter3());
    }
    
    System.out.println("REVSHARE LOG JSP "+revparam1);
    if(revparam1!=null && revparam2!=null && revparam3!=null) {
    RevShareVisitorLog revVisitLog=new RevShareVisitorLog();
    revVisitLog.setParameter1(revparam1);
    revVisitLog.setParameter2(revparam2);
    revVisitLog.setParameter3(revparam3);
    revVisitLog.setaParsedMobile(msisdn);
    revVisitLog.setaCampaignId(campaignId);
    revVisitLog.setaCreated(revsharesdf2.format(nowtime.getTime()));
    revVisitLog.setaSubscribed(visitsubscribed);
    revVisitLog.setIsSubscribed(isSubscribed);
    revVisitLog.setaSrc(campaignsrc);
    revVisitLog.setIp(ipbitterAddress);
    revVisitLog.setTransaction_id("zarevshare");
    revVisitLog.setPublisherid("");
    revvisitorlogdao.insert(revVisitLog);
    session.setAttribute("revparam1",revVisitLog.getParameter1());
    session.setAttribute("revparam2",revVisitLog.getParameter2());
    session.setAttribute("revparam3",revVisitLog.getParameter3());
    
    
    Cookie cookie1 = new Cookie("revparam1",revVisitLog.getParameter1());
    cookie1.setMaxAge(24*60*60); //24 hours
    response.addCookie(cookie1); 
                     
    Cookie cookie2 = new Cookie("revparam2",revVisitLog.getParameter2());
    cookie2.setMaxAge(24*60*60); //24 hours
    response.addCookie(cookie2);
                     
    Cookie cookie3 = new Cookie("revparam3",revVisitLog.getParameter3());
    cookie3.setMaxAge(24*60*60); //24 hours
    response.addCookie(cookie3);
    
    
    
    
    }
}
%>