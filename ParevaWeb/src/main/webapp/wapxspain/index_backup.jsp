<%@include file="commonfunc.jsp"%>
<%@include file="cpavisit.jsp"%>
<%    String uid = "";
    String wapid = "";
    String msisdn = aReq.getMsisdn();
    String mobilenumber = "", parsedmobile = "";

//====Required for CPA=====
    String insertQuery = "";
    String campaignsrc = "";
    String landingPage = "";
    String visitsubscribed = "1970-01-01 00:00:00";
    String myip = request.getHeader("X-Forwarded-For");
    MobileClubCampaign cmpg = null;

    if (myip != null) {
        int idx = myip.indexOf(',');
        if (idx > -1) {
            myip = myip.substring(0, idx);
        }
    }

    System.out.println("***** IPX dmn.getName(): " + dmn.getName());
    System.out.println("***** IPX dmn.getDefPublicPage(): " + dmn.getDefPrivateDir());
    System.out.println("***** IPX dmn.getDefPublicPage(): " + dmn.getDefPublicPage());

    if (dmn.getName().contains("videocasalinghi1")) {
        myip = request.getRemoteAddr();
        System.out.println("***** IPX myip: " + myip);
    }

    session.setAttribute("userip", myip);
    request.setAttribute("userip", myip);

    String isSubscribed = "0";
    String transaction_ref = "zacpa";
//====CPA Requirement end ===========

    if (campaignId != null && campaignId.trim().length() > 0) {

        if (!campaignId.equals("")) {
            cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
        }
        if (cmpg != null) {
            campaignsrc = cmpg.getSrc();
        }
        try {
            if (cmpg != null) {
                campaigndao.log("index", "xhtml", "", "", handset, domain, campaignId, club.getUnique(), "INDEX", 0, request, response, myip);
            }
        } catch (Exception e) {
            System.out.println("campaigndao Spain IPX Exception " + e);
        }
    }

    if (campaignsrc.trim().toLowerCase().contains("cpa")) {
%>
//CPA Tracking Code comes here. 
<%@include file="cpavisitlog.jsp"%>
<%    }
    if (campaignsrc.trim().endsWith("RS")) {
%>   
<%@include file="revsharevisitlog.jsp"%>
<%    }

//if(club!=null && club.getRegion().equals("IT") && !isDeskTopOrTablet && isIdentified && user==null)
/*
     if(club!=null && club.getRegion().equals("IT") && user==null)
     {	
     System.out.println("%%%% IPX goto identify %%%%%");
     String identifyUrl = "/identify.jsp" ;
     if(!campaignId.equals(""))
     identifyUrl += "?cid=" + campaignId;  
     doRedirect(response, identifyUrl);
     return;
     }
     */
    //isIdentified = ipxLookupIP(myip, clubIPXUserName, clubIPXPassword);
    isIdentified = true;
//NEW WAY
//if(club!=null && club.getRegion().equals("IT") && !isDeskTopOrTablet && isIdentified && user==null)
    if (club != null && club.getRegion().equals("ES") && !isDeskTopOrTablet && isIdentified && user == null) //if(club!=null && club.getRegion().equals("IT") && user==null)
    {
%>
<%@include file="identify_function.jsp"%>
<%    }
    try {
        application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
        //return;
        //application.getRequestDispatcher("/IndexMain").forward(request,response); 
    } catch (Exception e) {
        System.out.println("wapxspain index Exception " + e);
        String exception_ = getStackTraceAsString(e);
        System.out.println(exception_);
    }

%>


