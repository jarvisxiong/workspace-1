<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="org.hibernate.Hibernate"%>
<%@include file="imiVariables.jsp"%>
<%@include file="coreimport.jsp"%>
<%@include file="db.jsp"%>
<%@include file="cpanotify.jsp"%>
<%
	/**

	create table uk_notification_info (aUnique varchar(30),paramName varchar(50),paramValue varchar(50),entrytime datetime);
	 **/
   Transaction dbtransaction=dbsession.beginTransaction();
   Query query=null;

	Map<String, String[]> parameters = request.getParameterMap();

	java.util.List<String[]> valuesList = new ArrayList<String[]>();

	for (String parameter : parameters.keySet()) {

		String values[] = new String[2];

		values[0] = parameter;

		if (parameter.trim().equalsIgnoreCase("anyx_srvc")
				|| parameter.trim().equalsIgnoreCase("_sdcpath")) {
			continue;
		}

		String[] value = parameters.get(parameter);
		if (value != null && value.length > 0) {

			values[1] = value[0];
		} else {

			values[1] = "";
		}

		valuesList.add(values);

	}
          String tid="";
          String checktidQuery="";
        try{
                      
        tid=request.getParameter("tid");
        checktidQuery="select * from uk_notification_info where paramName='tid' and paramValue='"+tid+"'";
        query=dbsession.createSQLQuery(checktidQuery).addScalar("paramValue", StandardBasicTypes.STRING);
        }
        catch(Exception e){System.out.println("IMI duplicate tid error "+e); e.printStackTrace();}
        java.util.List tidresult=query.list();
        
           
        System.out.println("iminotification_debug  query list size for tid="+tid+" is "+tidresult.size()+" query: "+checktidQuery);
       if(tidresult!=null && tidresult.size()<=0) {

	String unique = Misc.generateUniqueId();

	String addMobileTriesQuery = "";

	Date curDate = new Date(System.currentTimeMillis());

	for (String[] value : valuesList) {

		addMobileTriesQuery = " INSERT INTO uk_notification_info"
			+ " (aUnique, paramName, paramValue, entrytime)"
			+ " VALUES('"+unique+"','"+value[0]+"','"+value[1]+"','"+MiscDate.toSqlDate(curDate)+"')";

		try {
		
                    System.out.println("iminotification_debug uknotificationQuery "+addMobileTriesQuery);
		query=dbsession.createSQLQuery(addMobileTriesQuery);
                String mobileno = request.getParameter("msisdn");
                if(mobileno!=null && mobileno.trim().length()>0 && !mobileno.trim().toLowerCase().contains("fake"))
                query.executeUpdate();
                
                } catch (Exception e) {
                    System.out.println("iminotification_debug "+" Exception "+e);
			e.printStackTrace();
		}
	}
         }
      

	String status_imi = request.getParameter("status");
	if (status_imi != null && status_imi.trim().length() > 0) {
		status_imi = status_imi.trim();
	} else {
		status_imi = "-1";
	}

	String reference_id = request.getParameter("ref");
	String MSISDN ="";
        try{
        MSISDN=request.getParameter("msisdn");
        }catch(Exception e){MSISDN="";}
        String campaignid="";//request.getParameter("cid");
        
        if(MSISDN!=null && MSISDN.equalsIgnoreCase("Hae123Fake")){
            campaignid=request.getParameter("cid");
            
        }

	String imiNetworkid = getOperatorName(request.getParameter("networkid"));

	System.out.println("iminotification_debug  referenceid " + reference_id+" status "+status_imi);

	//System.out.println("imi debug : referenceid" + reference_id);
    
	String hashcode = "";
	String CampaignId = "";
	String cpacampaignid = "";
	String clickid = "";
        String campaignsource="";
        String transactionRef_CampaignId=afterUnderScore(reference_id);
        
        if(transactionRef_CampaignId!=null && MSISDN!=null && !MSISDN.equalsIgnoreCase("Hae123Fake")) {
            campaignid=transactionRef_CampaignId;
            CampaignId=campaignid;
            System.out.println("iminotification_debug "+"campaignid "+campaignid+" CampaignId= "+CampaignId);
        }

String selectQuery = "select aHashcode,aCampaignId,cpacampaignid,clickid,aSrc from cpavisitlog where transaction_id='"
			+ reference_id + "' AND aCampaignId<>'' AND aCampaignId<>'null' AND aHashcode<>'cpa_not_recorded' "
        + "";
        
            if(campaignid!=null && campaignid.trim().length()>0)
                selectQuery+=" AND aCampaignId='"+campaignid+"'";
        
                   selectQuery += " ORDER by aCreated desc LIMIT 1";
               // + " AND aParsedMobile='"+MSISDN+"'";
	
	try {
            System.out.println("iminotification_debug  cpavisitlog Query "+selectQuery);
		query=dbsession.createSQLQuery(selectQuery)
                        .addScalar("aHashcode", StandardBasicTypes.STRING).addScalar("aCampaignId", StandardBasicTypes.STRING)
                        .addScalar("cpacampaignid", StandardBasicTypes.STRING).addScalar("clickid", StandardBasicTypes.STRING)
                        .addScalar("aSrc", StandardBasicTypes.STRING);
                
                java.util.List result=query.list();
		if (result.size()>0) {
                    System.out.println("iminotification_debug "+"cpavisitQuery result size "+result.size());
                    for(Object o:result){
                        Object[] row=(Object[]) o;
			try {
				hashcode = String.valueOf(row[0]);
			} catch (Exception m) {
				hashcode = "";
			}
			try {
                            	CampaignId = String.valueOf(row[1]);
			} catch (Exception m) {
				CampaignId = "";
			}

			try {
				cpacampaignid =String.valueOf(row[2]);
			} catch (Exception m) {
				cpacampaignid = "";
			}
			try {
				clickid = String.valueOf(row[3]);
			} catch (Exception m) {
				clickid = "";
			}
                        try {
				campaignsource = String.valueOf(row[4]);
			} catch (Exception m) {
				campaignsource = "";
			}
		}
        }
       
       

		
	} catch (Exception e) {
		System.out.println("iminotification_debug " + "cpavisitlog  Exception " + e);
		e.printStackTrace();
	}
        
        try{
              Calendar todaycal = Calendar.getInstance();
	      Date currentdate = todaycal.getTime();
	      String entryTime = sdf2.format(currentdate.getTime());
              
             
            
           String sqlcpaqueuequery="INSERT INTO cpaqueue(tid,msisdn,reference,campaign,cpasource,entrytime)"
                   + " VALUES('"+tid+"','"+MSISDN+"','"+reference_id+"','"+CampaignId+"','"+campaignsource+"','"+entryTime+"')"; 
           
            System.out.println("iminotification_debug "+" inserting into cpaqueue "+sqlcpaqueuequery);
                
            query=dbsession.createSQLQuery(sqlcpaqueuequery);
            query.executeUpdate();

	String campaignsrc =campaignsource;// findCampaignSrc(dbsession,CampaignId.trim());
	String clubid = findClub(dbsession,CampaignId.trim());
	String responsecode = "", responsedesc = "", responseref = "";
        responsecode = "001";
         System.out.println("iminotification_debug "+" campaignsrc "+campaignsrc+" clubid "+clubid+" responsecode="+responsecode);
        
        if(clubid==null || clubid.trim().equalsIgnoreCase("")) clubid="7741785002141KDS"; //Default clubid if not received
	

	if ((status_imi.equalsIgnoreCase("1")) && (CampaignId != null && CampaignId.trim().length() > 0)) {
            
             if(reference_id!=null || !reference_id.equalsIgnoreCase("null")){
                 
                 if(campaignsrc.toLowerCase().endsWith("cpa")){
                 if (campaignsrc.equalsIgnoreCase("KimiaCPA")) {

			System.out.println("kimiacampaign "+MSISDN+" campaign"+CampaignId);
                    boolean notified=checkMsisdn(dbsession, MSISDN,CampaignId);
			System.out.println("kimiacampaign "+MSISDN+" campaign"+CampaignId+ " notified value"+notified);

                    
			if(!notified){
                            System.out.println("kimiacampaign "+MSISDN+" campaign"+CampaignId+ " notified value"+notified+" logging to cpalogging");
		logtoCpaLogging(dbsession,MSISDN, campaignsrc,CampaignId, clubid, imiNetworkid);
                    }
                 else{
System.out.println("kimiacampaign "+MSISDN+" campaign"+CampaignId+ " notified value"+"logging to BI Log only because notified is "+notified);
                     responsecode="00";
           logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "success", responsecode, "successful","UK", imiNetworkid, MSISDN, contentprice, "club", clubid,
				CampaignId);
                     }
                 }
                  else
                       logtoCpaLogging(dbsession,MSISDN, campaignsrc,CampaignId, clubid, imiNetworkid);  
                 
             }
                 else{                  
                     responsecode="00";
                     logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "success", responsecode, "successful","UK", imiNetworkid, MSISDN, contentprice, "club", clubid,
				CampaignId);
                 }
                 
            }
        }
        //reference id not null 
        else if ((status_imi.equalsIgnoreCase("1")) && (CampaignId == null || CampaignId.trim().length() <= 0))  {
            
               

		logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,
				reference_id, "success", "00", "successful", "UK",imiNetworkid, MSISDN, contentprice, "club", clubid, "");
	}

	else if (status_imi.equalsIgnoreCase("3")) {
		responsecode = "51";
		responsedesc = "Insufficient Fund";
		responseref = "failed";
		logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "failed", "51", "Insufficient Fund","UK", imiNetworkid, "", contentprice, "club", clubid,
				CampaignId);
	} else if (status_imi != null && !(status_imi.equalsIgnoreCase("1") || status_imi.equalsIgnoreCase("3"))) {
		responsecode = "91";
		responsedesc = "Other Error";
		responseref = "failed";
		logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "failed", "91", "Other Error", "UK",
				imiNetworkid, "", contentprice, "club", clubid, CampaignId);
	} else {
		System.out.println("imi bi log" + "campain id found null OR Status is not received");
	}
        }
        
        catch(Exception ee){System.out.println("PRIMARY KEY FAILURE ERROR "+ee);ee.printStackTrace();}
         
//        }
//        catch(Exception e){System.out.println("IMI duplicate tid error "+e); e.printStackTrace();}
        
        dbtransaction.commit();
        dbsession.close();
%>

<%!
public boolean checkMsisdn(Session dbsession, String msisdn, String campaignid)
{
    boolean notified=false;
    String sqlstr = "Select * from cpaLog where aParsedMobile='"+ msisdn+"' AND aCampaign='"+campaignid+"' LIMIT 1";
    Query query=null;
    try{
        query=dbsession.createSQLQuery(sqlstr);
        java.util.List result=query.list();
        if(result.size()>0) notified=true;
    }
    catch(Exception e){}
    return notified;
}


public String afterUnderScore(String longString){
        if(longString==null)return "";
        
        int index = longString.indexOf("_");
        if(index==-1)return longString;
        
        String aux = longString.substring(index+1);
        if(aux.equalsIgnoreCase("null"))return "";
                
        return aux;
    }

%>
