<%!public static final SimpleDateFormat sdf2 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

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
		opcode.put("1", " Vodafone UK");
		opcode.put("2", "O2 Telefonica UK");
		opcode.put("3", "Orange UK");
		opcode.put("4", "T-Mobile\\EE UK");
		opcode.put("6", "Three UK ");

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

	public String callBackBitterStraw(Session dbsession,String amount, String currency,
			String payoutType, String MSISDN, String clubunique,
			String campainId, String hash) throws Exception {
		UUID transactionid = UUID.randomUUID();
		final String bittertoken = "768be44f2fc7977abea3792d45bc2af9";
		String callbackparam = "";
		String response = "";
		String bitterurl = "https://callbacks.bitterstrawberry.org/?token="
				+ bittertoken;

		URL bitterstrawcallbackurl = null;

		// String hashcode=getHash(MSISDN);

		String hashcode = hash;

		callbackparam = "&hash="+ java.net.URLEncoder.encode(hashcode, "UTF-8");
		callbackparam += "&transaction_id="+ java.net.URLEncoder.encode(transactionid.toString().trim(),"UTF-8");
		callbackparam += "&amount="+ java.net.URLEncoder.encode(amount, "UTF-8");
		callbackparam += "&payout_type="+ java.net.URLEncoder.encode(payoutType, "UTF-8");
		callbackparam += "&currency="+ java.net.URLEncoder.encode(currency, "UTF-8");

		bitterstrawcallbackurl = new URL(bitterurl + callbackparam);

		System.out.println("CPA debug " + " bitterURL " + bitterurl+ callbackparam);
		HttpURLConnection bitterpostback = (HttpURLConnection) bitterstrawcallbackurl.openConnection();
		bitterpostback.setDoOutput(true);
		bitterpostback.setRequestMethod("GET");

		String code = bitterpostback.getResponseCode() + "";
		String desc = bitterpostback.getResponseMessage();
		response = code + ":" + bitterpostback.getResponseCode();
		System.out.println(bitterurl + callbackparam);
		System.out.println(response);
		Calendar dcal = Calendar.getInstance();
		Date curdate = dcal.getTime();
		String currentTime = sdf2.format(curdate.getTime());

		String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
				+ " VALUES('"
				+ hashcode
				+ "','"
				+ amount
				+ "','"
				+ transactionid.toString().trim()
				+ "','"
				+ currency
				+ "','"
				+ payoutType
				+ "','"
				+ MSISDN
				+ "'"
				+ ",'"
				+ "BitterCPA"
				+ "','"
				+ campainId
				+ "','"
				+ clubunique
				+ "','"
				+ currentTime
				+ "','"
				+ code
				+ "','"
				+ desc
				+ "','"
				+ bitterurl
				+ callbackparam + "')";

		//System.out.println("imi debug cpaLogquery" + cpalogquery);

		Query query=dbsession.createSQLQuery(cpalogquery);
		query.executeUpdate();
		return response;
	}

	public String callBackUniteMob(Session dbsession,String amount, String currency,
			String payoutType, String MSISDN, String clubunique,
			String campaignId, String subid) throws Exception {
		String unitemoburl = "http://mongo1.bb800.com:8181/post.do?samdata="
				+ subid;
		URL unitemob = null;
		String response = "";

		System.out.println("UniteMob Query cpaLog " + " UniteMob URL : "
				+ unitemob);

		try {
			UUID transactionid = UUID.randomUUID();
			unitemob = new URL(unitemoburl);
			HttpURLConnection unitemobpostback = (HttpURLConnection) unitemob
					.openConnection();
			unitemobpostback.setDoOutput(true);
			unitemobpostback.setRequestMethod("POST");

			String code = unitemobpostback.getResponseCode() + "";
			String desc = unitemobpostback.getResponseMessage();
			response = code + ":" + unitemobpostback.getResponseCode();
			Calendar dcal = Calendar.getInstance();
			Date curdate = dcal.getTime();
			String currentTime = sdf2.format(curdate.getTime());

			String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
					+ " VALUES('"
					+ subid
					+ "','"
					+ amount
					+ "','"
					+ transactionid.toString().trim()
					+ "','"
					+ currency
					+ "','"
					+ payoutType
					+ "','"
					+ MSISDN
					+ "'"
					+ ",'"
					+ "unitemobcpa"
					+ "','"
					+ campaignId
					+ "','"
					+ clubunique
					+ "','"
					+ currentTime
					+ "','"
					+ code
					+ "','"
					+ desc
					+ "','"
					+ unitemoburl + "')";

			

			Query query=dbsession.createSQLQuery(cpalogquery);
                        query.executeUpdate();

		} catch (Exception e) {
			System.out.println("UniteMob Query cpaLog " + " Exception " + e);
		}

		return response;

	}

	public String callOldBitterStraw(Session dbsession,String amount, String currency,
			String payoutType, String MSISDN, String clubunique,
			String campaignId, String param) {
		String bitteroldurl = "https://api.bitterstrawberry.com/189/Moon/fcad7fcbeaaf6c014a2365065ab0e7b1/?param="
				+ param;
		URL bitterold = null;
		String response = "";

		System.out.println("BitterOld Query cpaLog " + " BitterOld URL : "
				+ bitteroldurl);

		try {
			UUID transactionid = UUID.randomUUID();
			bitterold = new URL(bitteroldurl);
			HttpURLConnection bitterpostback = (HttpURLConnection) bitterold
					.openConnection();
			bitterpostback.setDoOutput(true);
			bitterpostback.setRequestMethod("GET");

			String code = bitterpostback.getResponseCode() + "";
			String desc = bitterpostback.getResponseMessage();
			response = code + ":" + bitterpostback.getResponseCode();
			Calendar dcal = Calendar.getInstance();
			Date curdate = dcal.getTime();
			String currentTime = sdf2.format(curdate.getTime());

			String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
					+ " VALUES('"
					+ param
					+ "','"
					+ amount
					+ "','"
					+ transactionid.toString().trim()
					+ "','"
					+ currency
					+ "','"
					+ payoutType
					+ "','"
					+ MSISDN
					+ "'"
					+ ",'"
					+ "bitteroldcpa"
					+ "','"
					+ campaignId
					+ "','"
					+ clubunique
					+ "','"
					+ currentTime
					+ "','"
					+ code
					+ "','"
					+ desc
					+ "','"
					+ bitterold + "')";

			Query query=dbsession.createSQLQuery(cpalogquery);
                        query.executeUpdate();

		} catch (Exception e) {
			System.out.println("imi debug " + " Exception " + e);
		}

		return response;

	}

	public String callBackMundoMedia(Session dbsession,String amount, String currency,
			String clubid, String campainId, String MSISDN, String clickId,
			String cpaCampaign, String payoutType) {
		String postbackcampaignid = "";
		String response = "";
		java.net.URL mmtrackingurl = null;

		try {

			String clickid = clickId;
			String cpacampaign = cpaCampaign;
			postbackcampaignid = java.net.URLEncoder.encode(cpacampaign,
					"UTF-8");

			String postbackclickid = "&cookieid="
					+ java.net.URLEncoder.encode(clickid, "UTF-8");
			mmtrackingurl = new java.net.URL(
					"http://www.mlinktracker.com/lead/" + postbackcampaignid
							+ "/" + postbackclickid);

			java.net.HttpURLConnection mmpostback = (java.net.HttpURLConnection) mmtrackingurl
					.openConnection();
			mmpostback.setDoOutput(true);
			mmpostback.setRequestMethod("GET");

			String code = mmpostback.getResponseCode() + "";
			String desc = mmpostback.getResponseMessage();
			response = code + ":" + mmpostback.getResponseCode();
			System.out.println("MundoMedia tracking URL: " + mmtrackingurl);

			Calendar dcal = Calendar.getInstance();
			Date curdate = dcal.getTime();
			String currentTime = sdf2.format(curdate.getTime());
			String hashcode = "mundo";
			String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
					+ "VALUES('"
					+ hashcode
					+ "','"
					+ amount
					+ "','"
					+ clickid
					+ "','"
					+ currency
					+ "','"
					+ payoutType
					+ "','"
					+ MSISDN
					+ "'"
					+ ",'"
					+ "MundoCPA"
					+ "','"
					+ campainId
					+ "','"
					+ clubid
					+ "','"
					+ currentTime
					+ "','"
					+ code
					+ "','"
					+ desc + "','" + mmtrackingurl + "')";

			
                        Query query=dbsession.createSQLQuery(cpalogquery);
                        query.executeUpdate();
		} catch (Exception e) {
			System.out.println("imi debug " + e);
			e.printStackTrace();
		}

		return response;
	}
        
        public String callBackKimia(Session dbsession,String amount, String currency,
			String clubid, String campainId, String MSISDN, String clickId,
			String cpaCampaign, String payoutType) {
		String postbackcampaignid = "";
		String response = "";
		java.net.URL kimiatrackingurl = null;

		try {

			String clickid = clickId;
			String cpacampaign = cpaCampaign;
			String postbackclickid = "?kp="+ java.net.URLEncoder.encode(clickid, "UTF-8");
			kimiatrackingurl = new java.net.URL("http://adserver.kimia.es/conversion_get/pixel.jpg"+ postbackclickid);

			java.net.HttpURLConnection kimiapostback = (java.net.HttpURLConnection)kimiatrackingurl.openConnection();
			kimiapostback.setDoOutput(true);
			kimiapostback.setRequestMethod("GET");

			String code = kimiapostback.getResponseCode() + "";
			String desc = kimiapostback.getResponseMessage();
			response = code + ":" + kimiapostback.getResponseCode();
			System.out.println("Kimia tracking URL: " + kimiatrackingurl);

			Calendar dcal = Calendar.getInstance();
			Date curdate = dcal.getTime();
			String currentTime = sdf2.format(curdate.getTime());
			String hashcode = "kimia";
			String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
					+ "VALUES('"
					+ hashcode
					+ "','"
					+ amount
					+ "','"
					+ clickid
					+ "','"
					+ currency
					+ "','"
					+ payoutType
					+ "','"
					+ MSISDN
					+ "'"
					+ ",'"
					+ "KimiaCPA"
					+ "','"
					+ campainId
					+ "','"
					+ clubid
					+ "','"
					+ currentTime
					+ "','"
					+ code
					+ "','"
					+ desc + "','" + kimiatrackingurl + "')";

			
                        Query query=dbsession.createSQLQuery(cpalogquery);
                        query.executeUpdate();
		} catch (Exception e) {
			System.out.println("imi debug " + e);
			e.printStackTrace();
		}

		return response;
	}
        
         public String callBackMobIdea(Session dbsession,String amount, String currency,
			String clubid, String campainId, String MSISDN, String clickId,
			String cpaCampaign, String payoutType) {
		String postbackcampaignid = "";
		String response = "";
		java.net.URL mobideatrackingurl = null;

		try {

			String clickid = clickId;
			String cpacampaign = cpaCampaign;
			String postbackclickid ="?clickID="+ java.net.URLEncoder.encode(clickid, "UTF-8");
			mobideatrackingurl = new java.net.URL("http://www.securebill.mobi/bg.php"+ postbackclickid);

			java.net.HttpURLConnection mobideapostback = (java.net.HttpURLConnection)mobideatrackingurl.openConnection();
			mobideapostback.setDoOutput(true);
			mobideapostback.setRequestMethod("GET");

			String code = mobideapostback.getResponseCode() + "";
			String desc = mobideapostback.getResponseMessage();
			response = code + ":" + mobideapostback.getResponseCode();
			System.out.println("MobIdea tracking URL: " + mobideatrackingurl);

			Calendar dcal = Calendar.getInstance();
			Date curdate = dcal.getTime();
			String currentTime = sdf2.format(curdate.getTime());
			String hashcode = "mobidea";
			String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
					+ "VALUES('"
					+ hashcode
					+ "','"
					+ amount
					+ "','"
					+ clickid
					+ "','"
					+ currency
					+ "','"
					+ payoutType
					+ "','"
					+ MSISDN
					+ "'"
					+ ",'"
					+ "MobideaCPA"
					+ "','"
					+ campainId
					+ "','"
					+ clubid
					+ "','"
					+ currentTime
					+ "','"
					+ code
					+ "','"
					+ desc + "','" + mobideatrackingurl + "')";

			
                        Query query=dbsession.createSQLQuery(cpalogquery);
                        query.executeUpdate();
		} catch (Exception e) {
			System.out.println("imi debug " + e);
			e.printStackTrace();
		}

		return response;
	}
           public String callBackRethula(Session dbsession,String amount, String currency,
			String clubid, String campainId, String MSISDN, String clickId,
			String cpaCampaign, String payoutType) {
		String postbackcampaignid = "";
		String response = "";
		java.net.URL rehtulatrackingurl = null;

		try {

			String clickid = clickId;
			String cpacampaign = cpaCampaign;
			String postbackclickid = "?cid="+ java.net.URLEncoder.encode(clickid, "UTF-8");
                        postbackclickid += "&payout="+ java.net.URLEncoder.encode(amount, "UTF-8");
			rehtulatrackingurl = new java.net.URL("http://tyfbt.trackvoluum.com/postback"+ postbackclickid);
                        
			java.net.HttpURLConnection rehtulapostback = (java.net.HttpURLConnection)rehtulatrackingurl.openConnection();
			rehtulapostback.setDoOutput(true);
			rehtulapostback.setRequestMethod("GET");

			String code = rehtulapostback.getResponseCode() + "";
			String desc = rehtulapostback.getResponseMessage();
			response = code + ":" + rehtulapostback.getResponseCode();
			System.out.println("Rethula tracking URL: " + rehtulatrackingurl);

			Calendar dcal = Calendar.getInstance();
			Date curdate = dcal.getTime();
			String currentTime = sdf2.format(curdate.getTime());
			String hashcode = "rehtula";
			String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
					+ "VALUES('"
					+ hashcode
					+ "','"
					+ amount
					+ "','"
					+ clickid
					+ "','"
					+ currency
					+ "','"
					+ payoutType
					+ "','"
					+ MSISDN
					+ "'"
					+ ",'"
					+ "RethulaCPA"
					+ "','"
					+ campainId
					+ "','"
					+ clubid
					+ "','"
					+ currentTime
					+ "','"
					+ code
					+ "','"
					+ desc + "','" + rehtulatrackingurl + "')";

			
                        Query query=dbsession.createSQLQuery(cpalogquery);
                        query.executeUpdate();
		} catch (Exception e) {
			System.out.println("imi debug " + e);
			e.printStackTrace();
		}

		return response;
	}
           
           
           
           
           
           //=====MountWilson====
           public String callBackMountWilson(Session dbsession,String amount, String currency,
			String clubid, String campainId, String MSISDN, String clickId,
			String cpaCampaign, String payoutType) {
		String postbackcampaignid = "";
		String response = "";
		java.net.URL rehtulatrackingurl = null;
                UUID transactionid = UUID.randomUUID();

		try {

			String clickid = clickId;
			String cpacampaign = cpaCampaign;
			String postbackclickid = "?cid="+ java.net.URLEncoder.encode(clickid, "UTF-8");
                        postbackclickid += "&payout="+ java.net.URLEncoder.encode(amount, "UTF-8");
                        postbackclickid += "&txid="+ java.net.URLEncoder.encode(transactionid.toString().trim(), "UTF-8");
			rehtulatrackingurl = new java.net.URL("http://wzq0s.voluumtrk.com/postback"+ postbackclickid);
                        
			java.net.HttpURLConnection rehtulapostback = (java.net.HttpURLConnection)rehtulatrackingurl.openConnection();
			rehtulapostback.setDoOutput(true);
			rehtulapostback.setRequestMethod("GET");

			String code = rehtulapostback.getResponseCode() + "";
			String desc = rehtulapostback.getResponseMessage();
			response = code + ":" + rehtulapostback.getResponseCode();
			System.out.println("Rethula tracking URL: " + rehtulatrackingurl);

			Calendar dcal = Calendar.getInstance();
			Date curdate = dcal.getTime();
			String currentTime = sdf2.format(curdate.getTime());
			String hashcode = "rehtula";
			String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
					+ "VALUES('"
					+ hashcode
					+ "','"
					+ amount
					+ "','"
					+ clickid
					+ "','"
					+ currency
					+ "','"
					+ payoutType
					+ "','"
					+ MSISDN
					+ "'"
					+ ",'"
					+ "MountWilsonCPA"
					+ "','"
					+ campainId
					+ "','"
					+ clubid
					+ "','"
					+ currentTime
					+ "','"
					+ code
					+ "','"
					+ desc + "','" + rehtulatrackingurl + "')";

			
                        Query query=dbsession.createSQLQuery(cpalogquery);
                        query.executeUpdate();
		} catch (Exception e) {
			System.out.println("imi debug " + e);
			e.printStackTrace();
		}

		return response;
	}
           
           
           
           //=============================================================================================
        
        public String callBackGoMobi(Session dbsession,String amount, String currency,
			String payoutType, String MSISDN, String clubunique,
			String campainId, String hash) throws Exception {
		UUID transactionid = UUID.randomUUID();
		
		String callbackparam = "";
		String response = "";
		String gomobiurl = "http://gomobbi.go2cloud.org/aff_lsr";

		URL gomobicallbackurl = null;

		// String hashcode=getHash(MSISDN);

		String hashcode = hash;

		callbackparam = "?transaction_id="+ java.net.URLEncoder.encode(hashcode, "UTF-8");
		
		gomobicallbackurl = new URL(gomobiurl + callbackparam);

		System.out.println("CPA debug " + " gomobi " + gomobiurl+ callbackparam);
		HttpURLConnection gomobipostback = (HttpURLConnection) gomobicallbackurl.openConnection();
		gomobipostback.setDoOutput(true);
		gomobipostback.setRequestMethod("GET");

		String code = gomobipostback.getResponseCode() + "";
		String desc = gomobipostback.getResponseMessage();
		response = code + ":" + gomobipostback.getResponseCode();
		System.out.println(gomobiurl + callbackparam);
		System.out.println(response);
		Calendar dcal = Calendar.getInstance();
		Date curdate = dcal.getTime();
		String currentTime = sdf2.format(curdate.getTime());

		String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
				+ " VALUES('"
				+ hashcode
				+ "','"
				+ amount
				+ "','"
				+ transactionid.toString().trim()
				+ "','"
				+ currency
				+ "','"
				+ payoutType
				+ "','"
				+ MSISDN
				+ "'"
				+ ",'"
				+ "GoMobiCPA"
				+ "','"
				+ campainId
				+ "','"
				+ clubunique
				+ "','"
				+ currentTime
				+ "','"
				+ code
				+ "','"
				+ desc
				+ "','"
				+ gomobiurl
				+ callbackparam + "')";

		//System.out.println("imi debug cpaLogquery" + cpalogquery);

		Query query=dbsession.createSQLQuery(cpalogquery);
		query.executeUpdate();
		return response;
	}
        
        
        public String callBackYazi(Session dbsession,String amount, String currency,
			String payoutType, String MSISDN, String clubunique,
			String campainId, String hash) throws Exception {
		UUID transactionid = UUID.randomUUID();
		
		String callbackparam = "";
		String response = "";
		String yaziurl = "http://yazimedia.go2cloud.org/aff_lsr";

		URL yazicallbackurl = null;

		// String hashcode=getHash(MSISDN);

		String hashcode = hash;

		callbackparam = "?transaction_id="+ java.net.URLEncoder.encode(hashcode, "UTF-8");
		
		yazicallbackurl = new URL(yaziurl + callbackparam);

		System.out.println("CPA debug " + " yazi " + yaziurl+ callbackparam);
		HttpURLConnection yazipostback = (HttpURLConnection) yazicallbackurl.openConnection();
		yazipostback.setDoOutput(true);
		yazipostback.setRequestMethod("GET");

		String code = yazipostback.getResponseCode() + "";
		String desc = yazipostback.getResponseMessage();
		response = code + ":" + yazipostback.getResponseCode();
		System.out.println(yaziurl + callbackparam);
		System.out.println(response);
		Calendar dcal = Calendar.getInstance();
		Date curdate = dcal.getTime();
		String currentTime = sdf2.format(curdate.getTime());

		String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
				+ " VALUES('"
				+ hashcode
				+ "','"
				+ amount
				+ "','"
				+ transactionid.toString().trim()
				+ "','"
				+ currency
				+ "','"
				+ payoutType
				+ "','"
				+ MSISDN
				+ "'"
				+ ",'"
				+ "yazicpa"
				+ "','"
				+ campainId
				+ "','"
				+ clubunique
				+ "','"
				+ currentTime
				+ "','"
				+ code
				+ "','"
				+ desc
				+ "','"
				+ yaziurl
				+ callbackparam + "')";

		//System.out.println("imi debug cpaLogquery" + cpalogquery);

		Query query=dbsession.createSQLQuery(cpalogquery);
		query.executeUpdate();
		return response;
	}
        
        
        
        
        
        
        
        
        
        
        
        public String callBackKissMyAds(Session dbsession,String amount, String currency,
			String payoutType, String MSISDN, String clubunique,
			String campainId, String hash) throws Exception {
		UUID transactionid = UUID.randomUUID();
		
		String callbackparam = "";
		String response = "";
		String myadsurl = "http://tracking.kissmyads.com/aff_lm";

		URL myadscallbackurl = null;

		// String hashcode=getHash(MSISDN);

		String hashcode = hash;

		callbackparam = "?transaction_id="+ java.net.URLEncoder.encode(hashcode, "UTF-8");
		
		myadscallbackurl= new URL(myadsurl + callbackparam);

		System.out.println("CPA debug" + " Kissmyads " + myadsurl+ callbackparam);
		HttpURLConnection myadspostback = (HttpURLConnection) myadscallbackurl.openConnection();
		myadspostback.setDoOutput(true);
		myadspostback.setRequestMethod("GET");

		String code = myadspostback.getResponseCode() + "";
		String desc = myadspostback.getResponseMessage();
		response = code + ":" + myadspostback.getResponseCode();
		System.out.println(myadsurl + callbackparam);
		System.out.println(response);
		Calendar dcal = Calendar.getInstance();
		Date curdate = dcal.getTime();
		String currentTime = sdf2.format(curdate.getTime());

		String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
				+ " VALUES('"
				+ hashcode
				+ "','"
				+ amount
				+ "','"
				+ transactionid.toString().trim()
				+ "','"
				+ currency
				+ "','"
				+ payoutType
				+ "','"
				+ MSISDN
				+ "'"
				+ ",'"
				+ "kissmyads"
				+ "','"
				+ campainId
				+ "','"
				+ clubunique
				+ "','"
				+ currentTime
				+ "','"
				+ code
				+ "','"
				+ desc
				+ "','"
				+ myadsurl
				+ callbackparam + "')";

		//System.out.println("imi debug cpaLogquery" + cpalogquery);

		Query query=dbsession.createSQLQuery(cpalogquery);
		query.executeUpdate();
		return response;
	}
        
        public String callBackBadhatCPA(Session dbsession,String amount, String currency,
			String payoutType, String MSISDN, String clubunique,
			String campaignId, String subid) throws Exception {
		String badhaturl = "http://badhatmedia.com/tracker/track.php";
                String callbackparam = "";
                URL badhat = null;
		String response = "";

		try {
                    callbackparam += "?subid="+ java.net.URLEncoder.encode(subid, "UTF-8");
                    callbackparam += "&amt="+ java.net.URLEncoder.encode(amount, "UTF-8");
			UUID transactionid = UUID.randomUUID();
			badhat = new URL(badhaturl+callbackparam);
			HttpURLConnection badhatpostback = (HttpURLConnection) badhat.openConnection();
			badhatpostback.setDoOutput(true);
			badhatpostback.setRequestMethod("POST");

			String code = badhatpostback.getResponseCode() + "";
			String desc = badhatpostback.getResponseMessage();
			response = code + ":" + badhatpostback.getResponseCode();
			Calendar dcal = Calendar.getInstance();
			Date curdate = dcal.getTime();
			String currentTime = sdf2.format(curdate.getTime());

			String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
					+ " VALUES('"
					+ subid
					+ "','"
					+ amount
					+ "','"
					+ transactionid.toString().trim()
					+ "','"
					+ currency
					+ "','"
					+ payoutType
					+ "','"
					+ MSISDN
					+ "'"
					+ ",'"
					+ "badhatcpa"
					+ "','"
					+ campaignId
					+ "','"
					+ clubunique
					+ "','"
					+ currentTime
					+ "','"
					+ code
					+ "','"
					+ desc
					+ "','"
					+ badhaturl+callbackparam + "')";

			

			Query query=dbsession.createSQLQuery(cpalogquery);
                        query.executeUpdate();

		} catch (Exception e) {
			System.out.println("BadhatCPA Query cpaLog " + " Exception " + e);
		}

		return response;

	}
        
        public String callBackMobSuite(Session dbsession,String amount, String currency,
			String country, String MSISDN, String clubunique,
			String campainId, String hash) throws Exception {
		UUID transactionid = UUID.randomUUID();
		final String mobsuitetoken = "da73bf54dcd7806561cf4e0457fd0adf8920b7b8";
		String callbackparam = "";
		String response = "";
		String mobsuiteurl = "https://mobsuite.com/notify.php?auth_key="+ mobsuitetoken;

		URL mobsuitecallbackurl = null;

		// String hashcode=getHash(MSISDN);

		String hashcode = hash;

		callbackparam = "&visit_id="+ java.net.URLEncoder.encode(hashcode, "UTF-8");
		callbackparam += "&unique_id="+ java.net.URLEncoder.encode(transactionid.toString().trim(),"UTF-8");
		callbackparam += "&payout="+ java.net.URLEncoder.encode(amount, "UTF-8");
		callbackparam += "&country="+ java.net.URLEncoder.encode(country, "UTF-8");
		callbackparam += "&coin="+ java.net.URLEncoder.encode(currency, "UTF-8");

		mobsuitecallbackurl = new URL(mobsuiteurl + callbackparam);

		System.out.println("CPA debug " + " mobsuiteURL " + mobsuiteurl+ callbackparam);
		HttpURLConnection mobsuitepostback = (HttpURLConnection) mobsuitecallbackurl.openConnection();
		mobsuitepostback.setDoOutput(true);
		mobsuitepostback.setRequestMethod("GET");

		String code = mobsuitepostback.getResponseCode() + "";
		String desc = mobsuitepostback.getResponseMessage();
		response = code + ":" + mobsuitepostback.getResponseCode();
		System.out.println(mobsuiteurl + callbackparam);
		System.out.println(response);
		Calendar dcal = Calendar.getInstance();
		Date curdate = dcal.getTime();
		String currentTime = sdf2.format(curdate.getTime());

		String cpalogquery = "INSERT INTO cpaLog(aHashcode, aAmount,aTransactionid,aCurrency,aPayoutype,aParsedMobile, aSrc, aCampaign,aClubUnique,aCreated,aResponseCode,aResponseDesc,postbackurl)"
				+ " VALUES('"
				+ hashcode
				+ "','"
				+ amount
				+ "','"
				+ transactionid.toString().trim()
				+ "','"
				+ currency
				+ "','"
				+ "pps"
				+ "','"
				+ MSISDN
				+ "'"
				+ ",'"
				+ "MobSuiteCPA"
				+ "','"
				+ campainId
				+ "','"
				+ clubunique
				+ "','"
				+ currentTime
				+ "','"
				+ code
				+ "','"
				+ desc
				+ "','"
				+ mobsuiteurl
				+ callbackparam + "')";

		//System.out.println("imi debug cpaLogquery" + cpalogquery);

		Query query=dbsession.createSQLQuery(cpalogquery);
		query.executeUpdate();
		return response;
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