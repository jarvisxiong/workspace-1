<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="org.hibernate.Hibernate"%>
<%@include file="imiVariables.jsp"%>
<%@include file="coreimport.jsp"%>
<%@include file="db.jsp"%>
<%@include file="cpanotify.jsp"%>
<%@include file="ukheader.jsp"%>
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
          //String hashcode = "";
	//String CampaignId = "";
	//String cpacampaignid = "";
	//String clickid = "";
        //String campaignsource="";
        String type="";
        String sid="";
        String clubid ="";
        String aHash="";
        String landingPage="";
        String splitby="-";
        
          UKSuccess ukSuccess=null;
          String status_imi = request.getParameter("status");
          
          try{
              aHash=request.getParameter("hash");
          }catch(Exception e){aHash="";}
          
          try{          
          sid=request.getParameter("sid");
          }catch(Exception e){sid="196";} //Keeping old UK URL's sid for default reason
          
          if(sid!=null && sid.equalsIgnoreCase("196")){ clubid="7741785002141KDS";contentprice="9";}
          if(sid!=null && sid.equalsIgnoreCase("287")){ clubid="9062764922341KDS";contentprice="4.50";}
          
          
          
	
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
        
        String transactionRef_CampaignId="";//afterUnderScore(reference_id);
        
         if("196".equalsIgnoreCase(sid)){
             transactionRef_CampaignId=afterUnderScore(reference_id);
         }
        
        if("287".equalsIgnoreCase(sid)){
            //if(reference_id.contains("_")) splitby="_";
            //if(reference_id.contains("-")) splitby="-";
            System.out.println("287debug sid "+reference_id);
            String[] references=afterUnderScores(reference_id,"-");
            
            //System.out.println("287debug "+reference_id);
            transactionRef_CampaignId=references[1];
            try{
            landingPage=references[2];
            }
            
            catch(Exception e){landingPage="";}
            
        }
        
        
        
        if(transactionRef_CampaignId!=null && MSISDN!=null && !MSISDN.equalsIgnoreCase("Hae123Fake")) {
            campaignid=transactionRef_CampaignId;            
            cmpg = UmeTempCmsCache.campaignMap.get(transactionRef_CampaignId);            
        }
        
        try{
                      
        tid=request.getParameter("tid");
        type=request.getParameter("type");
        
        //if(clubid.equalsIgnoreCase("9062764922341KDS")){ //UK Subscription Flow club (for now hard-coded)
            
            if(type!=null && type.equalsIgnoreCase("stop")){
                System.out.println("iminotification_debug "+"type "+type);
                System.out.println("xstreamtesting STOP Notification from MSISDN "+MSISDN+" type "+type);
                Cookie[] cookies = null;
                cookies = request.getCookies();
                if( cookies != null ){
      		for (Cookie cookie:cookies){
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                }
            }
               if(cmpg!=null && cmpg.getSrc().endsWith("RS")){
                 
                String revloggingquery="insert into revShareLogging (aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values"
	            	+"('"+Misc.generateUniqueId()+"','0','"+cmpg.getPayoutCurrency()+"','"+MSISDN+"','"+MiscCr.encrypt(MSISDN)+"','"+cmpg.getUnique()+"','"+clubid+"','"+sdf2.format(new Date())+"','"+sdf2.format(new Date())+"','0','"+imiNetworkid+"','"+cmpg.getSrc() +"','2')";
                
                zacpa.executeUpdateCPA(revloggingquery);         
                
                                        }
                
                    }
        
        //This is to check for UKSuccess for Subscription Flow
        	ukSuccess=uksuccessdao.checkTid(tid);
                	
                if(ukSuccess!=null && ukSuccess.getStatus().equals("1") && !type.equalsIgnoreCase("stop") ){        		
        		Calendar c = Calendar.getInstance();
        		c.setTime(sdf2.parse(ukSuccess.getExpiry()));
        		c.add(Calendar.DATE, 7);  // number of days to add
        		ukSuccess.setExpiry(sdf2.format(c.getTime()));
        		uksuccessdao.updateExpiryDate(ukSuccess);
                 
                    }
                
                if(ukSuccess!=null && ukSuccess.getStatus().equals("1") && type.equalsIgnoreCase("stop") ){        		
        		ukSuccess.setType("stop");
                        ukSuccess.setExpiry(sdf2.format(new Date()));
                        System.out.println("xstreamtesting STOP Notification from MSISDN "+MSISDN+" type "+type+" stopping user");
        		uksuccessdao.saveSuccessfulUser(ukSuccess);
                        
                    SdcMobileClubUser clubUser = null;
         
             try{
             clubUser=umemobileclubuserdao.getClubUserByMsisdn(MSISDN,clubid);
              
                if (clubUser!=null) {
             clubUser.setParsedMobile(MSISDN);
             clubUser.setUnsubscribed(new Date());
             clubUser.setActive(0);
             umemobileclubuserdao.saveItem(clubUser); //a
              }
                 }
              catch(Exception e){}
                campaigndao.log("iminotification","", reference_id, MSISDN,null,domain, cmpg.getUnique(), clubid, "STOP", 0, request,response,imiNetworkid);        
                 
        	}
        //Ending checking UKSuccess for Subscription Flow
                
         if(status_imi.equals("1") && type.equalsIgnoreCase("sub") && clubid.equalsIgnoreCase("9062764922341KDS")) {
             Calendar currentTime=Calendar.getInstance();
             String creationdate=sdf2.format(currentTime.getTime());
             currentTime.add(Calendar.HOUR,168);
              String expiry=sdf2.format(currentTime.getTime());
              UKSuccess successfuluser=new UKSuccess();
        successfuluser.setTid(tid);
        successfuluser.setClubUnique(clubid);
        if(cmpg!=null) successfuluser.setCampaignId(cmpg.getUnique());
        else successfuluser.setCampaignId("");
            
        successfuluser.setStatus(status_imi);
        successfuluser.setType(type);
        successfuluser.setSid(sid);
        successfuluser.setaParsedMobile(MSISDN);
        successfuluser.setaNetworkId(imiNetworkid);
        successfuluser.setaHash(aHash);
        successfuluser.setCreated(sdf2.parse(creationdate));        
        successfuluser.setExpiry(expiry);
        
         SdcMobileClubUser clubUser = null;
         
         try{
             clubUser=umemobileclubuserdao.getClubUserByMsisdn(MSISDN,clubid);
         }
         catch(Exception e){}
         if (clubUser!=null) {
             clubUser.setParsedMobile(MSISDN);
             clubUser.setActive(1);
             umemobileclubuserdao.saveItem(clubUser);
         }
         else{
         clubUser=new SdcMobileClubUser();
        clubUser.setUnique(SdcMisc.generateUniqueId("BUS"));
	clubUser.setUserUnique(MSISDN);
	clubUser.setClubUnique(club.getUnique());
	clubUser.setParsedMobile(MSISDN);
	clubUser.setActive(1);
        clubUser.setCredits(100);
	clubUser.setAccountType(0);
		            //if (confMsg) clubUser.setAccountType(5);
	clubUser.setBillingStart(new Date());
	clubUser.setBillingEnd(new Date());
	clubUser.setBillingRenew(new Date());
	clubUser.setPushCount(0);
	clubUser.setCreated(new Date());
	clubUser.setCampaign(cmpg.getUnique());
	clubUser.setNetworkCode(imiNetworkid);
        clubUser.setSubscribed(new Date());
	clubUser.setUnsubscribed(new Date(0));
	clubUser.setNextPush(MiscDate.parseSqlDate("2011-01-01"));
        clubUser.setParam1("");
        clubUser.setParam2("");
        clubUser.setaExternalId("");
        umemobileclubuserdao.saveItem(clubUser);
         }
		            
        
        System.out.println("xstreamtesting:  expiry date is "+expiry +" "+ successfuluser.getExpiry());
        uksuccessdao.saveSuccessfulUser(successfuluser);
        //campaigndao.log("iminotification", landingPage,reference_id,MSISDN,null,domain,cmpg.getUnique(), clubid, "SUBSCRIBED", 0, request,response,imiNetworkid);
        
       
        if(cmpg!=null && cmpg.getSrc().toLowerCase().endsWith("cpa"))
        { 
            
             if(("billing").equalsIgnoreCase(cmpg.getCpaType())){
            logtoCpaLogging(zacpa,MSISDN,cmpg.getSrc(),cmpg.getUnique(),clubid, imiNetworkid);
             }
        }
        
        if(cmpg.getSrc().endsWith("RS")){
            String cpaloggingquery="insert into revShareLogging (aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values"
	          +"('"+Misc.generateUniqueId()+"','0','"+cmpg.getPayoutCurrency()+"','"+MSISDN+"','"+MiscCr.encrypt(msisdn)+"','"+cmpg.getUnique()+"','"+clubid+"','"+sdf2.format(new Date())+"','"+sdf2.format(new Date())+"','0','"+imiNetworkid+"','"+cmpg.getSrc() +"','0')";
	    
            int insertedRows=zacpa.executeUpdateCPA(cpaloggingquery);
            
            String rslogQuery="insert into revShareLogging (aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values"
	          +"('"+Misc.generateUniqueId()+"','0','"+cmpg.getPayoutCurrency()+"','"+MSISDN+"','"+MiscCr.encrypt(msisdn)+"','"+cmpg.getUnique()+"','"+clubid+"','"+sdf2.format(new Date())+"','"+sdf2.format(new Date())+"','0','"+imiNetworkid+"','"+cmpg.getSrc() +"','1')";
	    
            insertedRows=zacpa.executeUpdateCPA(cpaloggingquery);
            
        }
                  UKSuccess ukuser=uksuccessdao.getSubscriptionDetails(MSISDN);
                         if(ukuser!=null){
                          int userdifference=-1;
                          Date usercreateddate=ukuser.getCreated();
                          Calendar responseTime=Calendar.getInstance();
                          Date visitedTime=responseTime.getTime();
                          try{
                            userdifference=DateUtil.hoursDiff(usercreateddate,visitedTime);
                          }
                          catch(Exception e){userdifference=-1;}
                          
                          if(userdifference!=-1 && userdifference<=24){
                             String bicode="003";
                           logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "success",bicode, "successful","UK", imiNetworkid, MSISDN, contentprice, "club", clubid,
				cmpg.getUnique());
                          }
                         }
         }
                
         }
        catch(Exception e){System.out.println("xstreamtesting error "+e); e.printStackTrace();}
        
        checktidQuery="select * from uk_notification_info where paramName='tid' and paramValue='"+tid+"'";
        query=dbsession.createSQLQuery(checktidQuery).addScalar("paramValue", StandardBasicTypes.STRING);
        java.util.List tidresult=null;
       
        tidresult=query.list();
       //System.out.println("iminotification_debug  query list size for tid="+tid+" is "+tidresult.size()+" query: "+checktidQuery);
       if(tidresult!=null && tidresult.size()<=0)       
       {
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
	
        try{
            if(!(status_imi.equals("1") && "sub".equalsIgnoreCase(type))){
              Calendar todaycal = Calendar.getInstance();
	      Date currentdate = todaycal.getTime();
	      String entryTime = sdf2.format(currentdate.getTime());
              
             
            
           String sqlcpaqueuequery="INSERT INTO cpaqueue(tid,msisdn,reference,campaign,cpasource,entrytime)"
                   + " VALUES('"+tid+"','"+MSISDN+"','"+reference_id+"','"+cmpg.getUnique()+"','"+cmpg.getSrc()+"','"+entryTime+"')"; 
           
            //System.out.println("iminotification_debug "+" inserting into cpaqueue "+sqlcpaqueuequery);
                
            query=dbsession.createSQLQuery(sqlcpaqueuequery);
            query.executeUpdate();

	String campaignsrc =cmpg.getSrc();// findCampaignSrc(dbsession,CampaignId.trim());
	
        
	String responsecode = "", responsedesc = "", responseref = "";
        responsecode = "001";
         System.out.println("iminotification_debug "+" campaignsrc "+campaignsrc+" clubid "+clubid+" responsecode="+responsecode);
        
        if(clubid==null || clubid.trim().equalsIgnoreCase("")) clubid="7741785002141KDS"; //Default clubid if not received
        
	if ((status_imi.equalsIgnoreCase("1")) && (cmpg != null && cmpg.getSrc().trim().length() > 0)){
            
             if(reference_id!=null || !reference_id.equalsIgnoreCase("null")){                
                
                 if(cmpg!=null && cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("billing") && !clubid.equalsIgnoreCase("9062764922341KDS")){
                   if (cmpg.getSrc().equalsIgnoreCase("KimiaCPA")) {
			System.out.println("kimiacampaign "+MSISDN+" campaign"+cmpg.getUnique());
                    boolean notified=checkMsisdn(dbsession, MSISDN,cmpg.getUnique());
			System.out.println("kimiacampaign "+MSISDN+" campaign"+cmpg.getUnique()+ " notified value"+notified);

                    
			if(!notified){
                            
                         if(clubid.equalsIgnoreCase("9062764922341KDS")){
                         UKSuccess ukuser=uksuccessdao.getSubscriptionDetails(MSISDN);
                         if(ukuser!=null){
                          int userdifference=-1;
                          Date usercreateddate=ukuser.getCreated();
                          Calendar responseTime=Calendar.getInstance();
                          Date visitedTime=responseTime.getTime();
                          try{
                            userdifference=DateUtil.hoursDiff(usercreateddate,visitedTime);
                          }
                          catch(Exception e){userdifference=-1;}
                          
                          if(userdifference!=-1 && userdifference<=24){
                              responsecode="003";
                           logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "success", responsecode, "successful","UK", imiNetworkid, MSISDN, contentprice, "club", clubid,
				cmpg.getUnique());
                          }
                         }
                        
                         
                      }
                            System.out.println("kimiacampaign "+MSISDN+" campaign"+cmpg.getUnique()+ " notified value"+notified+" logging to cpalogging");
		logtoCpaLogging(zacpa,MSISDN, cmpg.getSrc(),cmpg.getUnique(), clubid, imiNetworkid);
                    }
                 else{
                    //System.out.println("kimiacampaign "+MSISDN+" campaign"+CampaignId+ " notified value"+"logging to BI Log only because notified is "+notified);
                      if(clubid.equalsIgnoreCase("9062764922341KDS")){
                         UKSuccess ukuser=uksuccessdao.getSubscriptionDetails(MSISDN);
                         if(ukuser!=null){
                          int userdifference=-1;
                          Date usercreateddate=ukuser.getCreated();
                          Calendar responseTime=Calendar.getInstance();
                          Date visitedTime=responseTime.getTime();
                          try{
                            userdifference=DateUtil.hoursDiff(usercreateddate,visitedTime);
                          }
                          catch(Exception e){userdifference=-1;}
                          
                          if(userdifference!=-1 && userdifference<=24) responsecode="003";
                         }
                         else{ responsecode="00";}
                         
                      }
                      else{
                     responsecode="00";
                      }
           logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "success", responsecode, "successful","UK", imiNetworkid, MSISDN, contentprice, "club", clubid,
				cmpg.getUnique());
                     }
                 }
                  else
                   {
                         if(clubid.equalsIgnoreCase("9062764922341KDS")){
                         UKSuccess ukuser=uksuccessdao.getSubscriptionDetails(MSISDN);
                         if(ukuser!=null){
                          int userdifference=-1;
                          Date usercreateddate=ukuser.getCreated();
                          Calendar responseTime=Calendar.getInstance();
                          Date visitedTime=responseTime.getTime();
                          try{
                            userdifference=DateUtil.hoursDiff(usercreateddate,visitedTime);
                          }
                          catch(Exception e){userdifference=-1;}
                          
                          if(userdifference!=-1 && userdifference<=24){
                              responsecode="003";
                           logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "success", responsecode, "successful","UK", imiNetworkid, MSISDN, contentprice, "club", clubid,
				cmpg.getUnique());
                          }
                         }
                        
                         
                      }
                       
                       
                       logtoCpaLogging(zacpa,MSISDN, cmpg.getSrc(),cmpg.getUnique(), clubid, imiNetworkid); 
                   }
                 
             
             }
             else if(cmpg!=null && cmpg.getSrc().endsWith("RS") && !type.equalsIgnoreCase("stop")){
                  if(clubid.equalsIgnoreCase("9062764922341KDS")){
                     
                     RevSharePayout revShare=revsharepayoutdao.getPayouts("UK",imiNetworkid);
                     double revSharePayout=revShare.getPayout();
                     double payoutpercentage = cmpg.getRevSharePayoutPercentage();
                    double notifyPayout = (payoutpercentage / 100) * revSharePayout;
                    double resultpayout = round(notifyPayout, 2);
                    
                    String revloggingquery="insert into revShareLogging (aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values"
	            			 			+"('"+Misc.generateUniqueId()+"','"+resultpayout+"','"+cmpg.getPayoutCurrency()+"','"+MSISDN+"','"+MiscCr.encrypt(MSISDN)+"','"+cmpg.getUnique()+"','"+clubid+"','"+sdf2.format(new Date())+"','"+sdf2.format(new Date())+"','0','"+imiNetworkid+"','"+cmpg.getSrc() +"','1')";
                     
                     zacpa.executeUpdateCPA(revloggingquery);
                     }
                 
             }
                 else{
                     
                    if(!type.equalsIgnoreCase("stop")){
                     responsecode="00";
                     logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "success", responsecode, "successful","UK", imiNetworkid, MSISDN, contentprice, "club", clubid,
				cmpg.getUnique());
                    }
                 }
                 
            }
        }
        //reference id not null 
        else if ((status_imi.equalsIgnoreCase("1")) && (cmpg == null || cmpg.getSrc().trim().length() <= 0) && !type.equalsIgnoreCase("stop"))  {
            
                         if(clubid.equalsIgnoreCase("9062764922341KDS")){
                         UKSuccess ukuser=uksuccessdao.getSubscriptionDetails(MSISDN);
                         if(ukuser!=null){
                          int userdifference=-1;
                          Date usercreateddate=ukuser.getCreated();
                          Calendar responseTime=Calendar.getInstance();
                          Date visitedTime=responseTime.getTime();
                          try{
                            userdifference=DateUtil.hoursDiff(usercreateddate,visitedTime);
                          }
                          catch(Exception e){userdifference=-1;}
                          
                          if(userdifference!=-1 && userdifference<=24) responsecode="003";
                         }
                         else{ responsecode="00";}
                         
                      }
                      else{
                     responsecode="00";
                      }

		logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "success",responsecode, "successful", "UK",imiNetworkid, MSISDN, contentprice, "club", clubid, "");
	}

	else if (status_imi.equalsIgnoreCase("3")) {
		responsecode = "51";
		responsedesc = "Insufficient Fund";
		responseref = "failed";
		logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "failed", "51", "Insufficient Fund","UK", imiNetworkid, "", contentprice, "club", clubid,
				cmpg.getUnique());
	} else if (status_imi != null && !(status_imi.equalsIgnoreCase("1") || status_imi.equalsIgnoreCase("3"))) {
		responsecode = "91";
		responsedesc = "Other Error";
		responseref = "failed";
		logtoBi(dbsession,SdcMisc.generateUniqueId(), "imi", status_imi,reference_id, "failed", "91", "Other Error", "UK",
				imiNetworkid, "", contentprice, "club", clubid, cmpg.getUnique());
	} else {
		System.out.println("imi bi log" + "campain id found null OR Status is not received");
	}
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

public String[] afterUnderScores(String longString, String separateby){
    System.out.println("287debug  inside underscore: "+longString);
    String[] array = longString.split(separateby);
    System.out.println("287debug 0: "+array[0]+" 1: "+array[1]+ " 2: "+array[2]);
    return array;
}

%>

IMI's Billing Notification