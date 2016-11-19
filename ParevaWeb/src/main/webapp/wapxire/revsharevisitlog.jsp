<%@page import="ume.pareva.revshare.RevSharePartners"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="ume.pareva.revshare.RevShareVisitorLog"%>
<%
    
SimpleDateFormat revsharesdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
nowtime = new GregorianCalendar();
ipbitterAddress=getClientIpAddr(request);

//String param1="",param2="",cpaparam1="",cpaparam2="",cpaparam3="",param3="",cpaquery="";
RevSharePartners revadvertiser=null;

if(campaignsrc.trim().endsWith("RS")) {
    revadvertiser=revsharepartnersdao.getAdvertiser(campaignsrc);
    
    System.out.println(revadvertiser);
    if(revadvertiser.getParameter1()!=null && revadvertiser.getParameter1().trim().length()>0)
    {        
        param1="tracker_id";
        cpaparam1=request.getParameter(revadvertiser.getParameter1());
    }
    if(revadvertiser.getParameter2()!=null && revadvertiser.getParameter2().trim().length()>0)
    {
        
        param2="cpacampaignid";
        cpaparam2=request.getParameter(revadvertiser.getParameter2());
    }
    if(revadvertiser.getParameter3()!=null && revadvertiser.getParameter3().trim().length()>0)
    {       
        param3="clickid";
        cpaparam3=request.getParameter(revadvertiser.getParameter3());
    }
    
    System.out.println("REVSHARE LOG JSP "+cpaparam1);
    if(cpaparam1!=null && cpaparam2!=null && cpaparam3!=null) {
    RevShareVisitorLog cpaVisitLog=new RevShareVisitorLog();
    cpaVisitLog.setParameter1(cpaparam1);
    cpaVisitLog.setParameter2(cpaparam2);
    cpaVisitLog.setParameter3(cpaparam3);
    cpaVisitLog.setaParsedMobile(msisdn);
    cpaVisitLog.setaCampaignId(campaignId);
    cpaVisitLog.setaCreated(revsharesdf2.format(nowtime.getTime()));
    cpaVisitLog.setaSubscribed(visitsubscribed);
    cpaVisitLog.setIsSubscribed(isSubscribed);
    cpaVisitLog.setaSrc(campaignsrc);
    cpaVisitLog.setIp(ipbitterAddress);
    cpaVisitLog.setTransaction_id("zarevshare");
    cpaVisitLog.setPublisherid("");
    revvisitorlogdao.insert(cpaVisitLog);
    session.setAttribute("revparam1",cpaVisitLog.getParameter1());
    session.setAttribute("revparam2",cpaVisitLog.getParameter2());
    session.setAttribute("revparam3",cpaVisitLog.getParameter3());
    }
}
%>