<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="org.hibernate.Hibernate"%>
<%@include file="imiVariables.jsp"%>
<%@include file="coreimport.jsp"%>
<%@include file="db.jsp"%>
<%@include file="cpanotify.jsp"%>
<%
Transaction dbtransaction=dbsession.beginTransaction();
Query query=null;
String clickid="";
String responsecode="001";

String checktidQuery="select aParsedMsisdn from mobileclubbillingtries where aCampaign='6147755241KDS' and aResponseCode='00'";
String campaignsrc="KimiaCPA";
try{
    query=dbsession.createSQLQuery(checktidQuery).addScalar("aParsedMsisdn", StandardBasicTypes.STRING);
    java.util.List tidresult=query.list();
    System.out.println("SIZE OF TIDRESULT : "+tidresult.size());
    
    if(tidresult.size()>0)
    {
        for(Object o:tidresult)
        {
            String msisdn=o.toString();
            String selectQuery = "select clickid from cpavisitlog where aCampaignId='6147755241KDS'"
		+" AND aParsedMobile='xxxx' AND clickid<>'' AND clickid<>'null' AND clickid<>'cpa_Not_recorded' "
                    + "AND clickid NOT IN (select aHashcode from cpaLog where aCampaign='6147755241KDS') group by clickid ORDER by aCreated"
                    + " LIMIT 1 ";
            
            query=dbsession.createSQLQuery(selectQuery).addScalar("clickid", StandardBasicTypes.STRING);
            java.util.List result1=query.list();
            
            for(Object o1:result1){
                       clickid=o1.toString();
            }
            
               if (campaignsrc.equalsIgnoreCase("KimiaCPA")) {

			callBackKimia(dbsession,"3.25", "EUR","7741785002141KDS","6147755241KDS", msisdn,clickid, "", "kimiapps");
                         responsecode = "001";
             logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", "1",msisdn, "success", responsecode, "successful", "UK", " Vodafone UK", msisdn, "9", "club", "7741785002141KDS", "6147755241KDS");
		}
        }
    }
    dbtransaction.commit();
    dbsession.close();
}
catch(Exception e){e.printStackTrace();}
        



%>