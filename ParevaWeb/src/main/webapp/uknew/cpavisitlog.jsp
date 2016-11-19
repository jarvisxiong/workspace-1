<%
    //a
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Calendar nowtime = new GregorianCalendar();
	String visitsubscribed = "1970-01-01 00:00:00";
	String campaignsrc= "";
	String clientIp = getClientIpAddr(request); //"iptest";
	String isSubscribed = "1";
        if(msisdn==null || msisdn.trim().length()<=0) msisdn="xxxx";
        
        String param1="",param2="",cpaparam1="",cpaparam2="",cpaparam3="",param3="",cpaquery="",publisherid="";
CpaAdvertiser advertiser=null;


    
    if (CPAcampainId != null && CPAcampainId.trim().length() > 0) {
           
            MobileClubCampaign campaign = null;
            campaign = UmeTempCmsCache.campaignMap.get(CPAcampainId);
            if(campaign!=null) campaignsrc = cmpg.getSrc();  
//System.out.println("CAMPAIGN DETAILS ABOVE " +campaign.getSrc()+" ==="+ cmpg.getSrc()+"===== "+campaignsrc);
            
            }

//System.out.println("ONLY CAMPAN SRC here========== "+campaignsrc);

if(campaignsrc!=null && cmpg!=null && campaignsrc.equalsIgnoreCase("")) campaignsrc=cmpg.getSrc();


//System.out.println("ONLY CAMPAN SRC here==========AFTER CONDITION "+campaignsrc);

  //System.out.println("CAMPAIGN SOURCE OF CAMPAIGN ID "+CPAcampainId+" is "+campaignsrc+" -->"+ cmpg.getSrc());
    
    if(campaignsrc.toLowerCase().endsWith("cpa"))
{
    advertiser=getAdvertiser(dbsession,campaignsrc);
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


    if(request.getParameter("affiliate_id")!=null)
    publisherid=request.getParameter("affiliate_id")+"";
    else
    publisherid="";
    
  cpaquery="INSERT into cpavisitlog(aHashcode,cpacampaignid,clickid,aParsedMobile,aCampaignId,aCreated,aSubscribed,"
		    + "isSubscribed,aSrc,ip,transaction_id,publisherid) "
		    + "VALUES ('"+cpaparam1+"','"+cpaparam2+"','"+cpaparam3+"','"+msisdn+"','"+CPAcampainId+"','"+sdf2.format(nowtime.getTime())+"','"+visitsubscribed+"','"
					            + isSubscribed+"','"+campaignsrc+"','"+clientIp+"','"+transaction_ref+"','"+publisherid+"')";



    //System.out.println(cpaquery);
    Query query=null;
    try{
        query=dbsession.createSQLQuery(cpaquery);
        query.executeUpdate();
        trans.commit();
        visitinserted=true;
    }
    
    catch(Exception eE)
    {
        System.out.println("Exception UK cpa debug "+"index query: Exception "+ eE);
	eE.printStackTrace();
        
    }
}

%>

<%!
public CpaAdvertiser getAdvertiser(Session session,String src)
   {
      Query query=null;
      CpaAdvertiser advertiser=null;
       String sqlquery="SELECT * FROM cpaadvertisers where aSrc='"+src+"'";
       try{
         
          query=session.createSQLQuery(sqlquery).addScalar("parameter1").addScalar("parameter2").addScalar("parameter3");
          java.util.List result = query.list();
			//System.out.println("GeneralSettings query: " + sqlquery	+ " result list size" + result.size());
          if(result.size()>0){
              
                for (Object o : result) {
                    advertiser=new CpaAdvertiser();
                    Object[] row=(Object[])o;
                    getAdvertiser(advertiser,row);
                            
                        }
                        
          }
     
       }
       catch(Exception e){e.printStackTrace();}
       
//      System.out.println("CPA Advertiser Details");
//      System.out.println(advertiser.getParameter1());
//      System.out.println(advertiser.getParameter2());
//      System.out.println(advertiser.getParameter3());
       return advertiser;
   }
    
    public void getAdvertiser(CpaAdvertiser advertiser, Object[] row) throws Exception {
        
        advertiser.setParameter1((String) row[0]);
        advertiser.setParameter2((String) row[1]);
        advertiser.setParameter3((String) row[2]);
    }




%>