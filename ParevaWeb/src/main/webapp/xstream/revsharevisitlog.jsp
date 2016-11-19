<%
    
SimpleDateFormat revsharesdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Calendar revnowTime = new GregorianCalendar();
String ipbitterAddress=getClientIpAddr(request);

String revparam1="",revparam2="",revcpaparam1="",revcpaparam2="",revcpaparam3="",revparam3="",revcpaquery="";
RevSharePartners revadvertiser=null;

if(campaignsrc.trim().endsWith("RS")) {
    revadvertiser=revsharepartnersdao.getAdvertiser(campaignsrc);
    
    System.out.println(advertiser);
    if(revadvertiser.getParameter1()!=null && revadvertiser.getParameter1().trim().length()>0)
    {        
        revparam1="tracker_id";
        revcpaparam1=request.getParameter(revadvertiser.getParameter1());
    }
    if(revadvertiser.getParameter2()!=null && revadvertiser.getParameter2().trim().length()>0)
    {
        
        revparam2="cpacampaignid";
        revcpaparam2=request.getParameter(revadvertiser.getParameter2());
    }
    if(revadvertiser.getParameter3()!=null && revadvertiser.getParameter3().trim().length()>0)
    {       
        revparam3="clickid";
        revcpaparam3=request.getParameter(revadvertiser.getParameter3());
    }
    
    System.out.println("REVSHARE LOG JSP "+revcpaparam1);
    if(revcpaparam1!=null && revcpaparam2!=null && revcpaparam3!=null) {
    RevShareVisitorLog revVisitLog=new RevShareVisitorLog();
    revVisitLog.setParameter1(revcpaparam1);
    revVisitLog.setParameter2(revcpaparam2);
    revVisitLog.setParameter3(revcpaparam3);
    revVisitLog.setaParsedMobile(msisdn);
    revVisitLog.setaCampaignId(CPAcampainId);
    revVisitLog.setaCreated(revsharesdf2.format(revnowTime.getTime()));
    revVisitLog.setaSubscribed(visitsubscribed);
    revVisitLog.setIsSubscribed(isSubscribed);
    revVisitLog.setaSrc(campaignsrc);
    revVisitLog.setIp(ipbitterAddress);
    revVisitLog.setTransaction_id("ukrevshare");
    revVisitLog.setPublisherid("");
    revvisitorlogdao.saveCpaVisitLog(revVisitLog);
    session.setAttribute("revparam1",revVisitLog.getParameter1());
    session.setAttribute("revparam2",revVisitLog.getParameter2());
    session.setAttribute("revparam3",revVisitLog.getParameter3());
    }
}
%>