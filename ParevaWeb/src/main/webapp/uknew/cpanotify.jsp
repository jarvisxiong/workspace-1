<%!

public static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String findCampaignSrc(Session dbsession,String campaignid) {
		String campaignsrc = "";
		String sqlstr = "Select aSrc from mobileClubCampaigns where aUnique='"+ campaignid + "'";

		Query query=null;

		try {
			query=dbsession.createSQLQuery(sqlstr).addScalar("aSrc", StandardBasicTypes.STRING);
                        java.util.List result=query.list();
                        query.setCacheable(true);
			if (result.size()>0) {
                            for(Object o:result)
				campaignsrc = o.toString();
			}
		} catch (Exception eE) {
			eE.printStackTrace();
		}

		return campaignsrc;
	}

public String getOperatorName(String code) {

		if (code == null || code.trim().length() <= 0) {
			return " ";
		}

		Hashtable<String, String> opcode = new Hashtable<String, String>();
		opcode.put("1", "vodafone");
		opcode.put("2", "o2");
		opcode.put("3", "orange");
		opcode.put("4", "ee");
		opcode.put("6", "three");

		try {
			return opcode.get(code.trim());
		} catch (Exception e) {
			return " ";
		}
	}

	String findClub(Session dbsession,String campaignid) {
		String clubid = "";
		
                Query query=null;
		String sqlstr = "SELECT mc.aUnique as aClubUnique FROM `mobileClubCampaigns` mcc JOIN `domains` d ON mcc.`aDomain` = d.`aDomainUnique` "
                        + " JOIN `mobileClubs` mc ON d.`aDomainUnique` = mc.`aWapDomain` WHERE mcc.`aUnique` ='"+ campaignid + "'";

		try {
			query=dbsession.createSQLQuery(sqlstr).addScalar("aClubUnique", StandardBasicTypes.STRING);
                        java.util.List result=query.list();
                        query.setCacheable(true);
			if (result.size()>0) {
                            for(Object o:result)
				clubid = o.toString();
			}
		} catch (Exception eE) {
			eE.printStackTrace();
		}

		return clubid;
	}
        
     public boolean logtoCpaLogging(Session dbsession,String msisdn, String src,
			String campaignid, String clubunique, String networkcode) {

		boolean logged = false;

		Calendar dcal = Calendar.getInstance();
		Date curdate = dcal.getTime();
		String currentTime = sdf2.format(curdate.getTime());
                int ukstatus=0;
                //if(src.equalsIgnoreCase("BitterCPA")) ukstatus=-1;

		String sqlstr = "INSERT INTO cpaLogging(aParsedMobile,aSrc,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode)"
				+ " VALUES('"
				+ msisdn
				+ "','"
				+ src
				+ "','"
				+ campaignid
				+ "','"
				+ clubunique
				+ "','"
				+ currentTime
				+ "','"
				+ currentTime
				+ "','"
				+ ukstatus
				+ "','"
				+ networkcode+"')";

		System.out.println("imi debug cpalogging query " + " sqlquery " + sqlstr);
		try {
			Query query=dbsession.createSQLQuery(sqlstr);
                        query.executeUpdate();
			logged = true;
			
		} catch (Exception eE) {
			System.out.println("imi debug Exception IMINotification " + eE);
			eE.printStackTrace();
		}

		return logged;

	}
     
     public boolean logtoBi(Session dbsession,String logunique, String aggregator, String status,
			String transid, String responseRef, String responseCode,
			String responseDesc, String region, String networkcode,
			String msisdn, String tariff, String billingtype,
			String clubunique, String campaign) {

		boolean logged = false;

		Calendar dcal = Calendar.getInstance();
		Date curdate = dcal.getTime();
		String currentTime = sdf2.format(curdate.getTime());

		String sqlstr = "INSERT INTO mobileclubbillingtries (aUnique, aLogUnique, aAggregator, aStatus, aTransactionId, aResponseRef, aResponseCode, "
				+ "aResponseDesc, aCreated, aRegionCode, aNetworkCode, aParsedMsisdn,aTariffClass, aBillingType, aClubUnique, aCampaign, aTicketCreated)"
				+ " VALUES('"
				+ SdcMisc.generateUniqueId()
				+ "','"
				+ logunique
				+ "','"
				+ aggregator
				+ "','"
				+ status
				+ "','"
				+ transid
				+ "','"
				+ responseRef
				+ "','"
				+ responseCode
				+ "','"
				+ responseDesc
				+ "','"
				+ currentTime
				+ "','"
				+ region
				+ "','"
				+ networkcode
				+ "','"
				+ msisdn
				+ "','"
				+ tariff
				+ "','"
				+ billingtype
				+ "','"
				+ clubunique + "','" + campaign + "','" + currentTime + "')";

		System.out.println("imi debug BILog query " + " sqlquery " + sqlstr);
		try {
			Query query=dbsession.createSQLQuery(sqlstr);
                        query.executeUpdate();
			logged = true;
			
		} catch (Exception eE) {
			System.out.println("imi debug Exception IMINotification " + eE);
			eE.printStackTrace();
		}

		return logged;

	}      
%>