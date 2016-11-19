<%
    
SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Calendar nowtime = new GregorianCalendar();
String ipbitterAddress=getClientIpAddr(request);

String param1="",param2="",cpaparam1="",cpaparam2="",cpaparam3="",param3="",cpaquery="";
CpaAdvertiser advertiser=null;

if(campaignsrc.toLowerCase().endsWith("cpa"))
{
    advertiser=advertiserdao.getAdvertiser(campaignsrc);
    if(advertiser.getParameter1()!=null && advertiser.getParameter1().trim().length()>0)
    {        
        param1="aHashcode";
        cpaparam1=request.getParameter(advertiser.getParameter1());
    }
    if(advertiser.getParameter2()!=null && advertiser.getParameter2().trim().length()>0)
    {
        
        param2="cpacampaignid";
        cpaparam2=request.getParameter(advertiser.getParameter2());
    }
    if(advertiser.getParameter3()!=null && advertiser.getParameter3().trim().length()>0)
    {       
        param3="clickid";
        cpaparam3=request.getParameter(advertiser.getParameter3());
    }
    
    
    
    if(cpaparam1!=null && cpaparam2!=null && cpaparam3!=null) {
    CpaVisitLog cpaVisitLog=new CpaVisitLog();
    cpaVisitLog.setaHashcode(cpaparam1);
    cpaVisitLog.setCpacampaignid(cpaparam2);
    cpaVisitLog.setClickid(cpaparam3);
    cpaVisitLog.setaParsedMobile(msisdn);
    cpaVisitLog.setaCampaignId(campaignId);
    cpaVisitLog.setaCreated(sdf2.format(nowtime.getTime()));
    cpaVisitLog.setaSubscribed(visitsubscribed);
    cpaVisitLog.setIsSubscribed(isSubscribed);
    cpaVisitLog.setaSrc(campaignsrc);
    cpaVisitLog.setIp(ipbitterAddress);
    cpaVisitLog.setTransaction_id(transaction_ref);
    
    if(request.getParameter("affiliate_id")!=null)
    cpaVisitLog.setPublisherid(request.getParameter("affiliate_id")+"");
    else
    cpaVisitLog.setPublisherid("");
    cpavisitlogdao.insert(cpaVisitLog);   
    
    session.setAttribute("cpaparam1",cpaVisitLog.getaHashcode());
    session.setAttribute("cpaparam2",cpaVisitLog.getCpacampaignid());
    session.setAttribute("cpaparam3",cpaVisitLog.getClickid());
    
    //System.out.println("SESSIONINFORMATION "+ session.getAttribute("cpaparam1")+" "+session.getAttribute("cpaparam2")+" "+session.getAttribute("cpaparam3"));
    }
}
%>