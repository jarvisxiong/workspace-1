<%@ include file="coreimport.jsp" %>

<%
    String activationCode = "409d-2c20-ded1-856e"; //Provided by Reporo
    String topBannerId = "22594"; //Provided by Reporo
  
    String bottomBannerId = "22598"; //Provided by Reporo
    String middleTextId = "22596"; //Provided by Reporo
    //String middleTextId2="9188"; // Provided by Reporo
    //String middleTextId3="22596"; // Provided by Reporo
    String adhesion="22590"; //Provided by Reporo
     //Provided by Reporo
    //String popunder="8132"; //Provided by Reporo*/
    HashMap<String,String> zones = new HashMap<String,String>();
//    Connection reporocon = DBHStatic.getConnection();
//    ResultSet reporors = null;
//    String sqlstr = "SELECT distinct aBody FROM clientFreeTexts WHERE aDomain='3976034988531CDS' AND aBody<>''";
//    System.out.println("babaplayer umereporo query"+sqlstr);
//    reporors = DBHStatic.getRs(reporocon, sqlstr);
//    while (reporors.next()) {
   //String zonebody=reporors.getString("aBody");
  // System.out.println("babaplayer umereporo zonebody"+zonebody);
//    if(zonebody.toLowerCase().contains("header"))
         zones.put("img_top", topBannerId);
//    if(zonebody.toLowerCase().contains("footer"))
        zones.put("img_bottom",bottomBannerId);
    
//    if(zonebody.toLowerCase().contains("middle"))
//        zones.put("txt_middle", middleTextId);

     
    
//     if(zonebody.toLowerCase().contains("adhesion"))
       // zones.put("ad_adhesion", adhesion);
//        }
////System.out.println("ADS BODY :> "+adsbody);
//reporors.close();
//DBHStatic.closeConnection(reporocon);
    
    //zones.put("txt_middle2", middleTextId2);
    //zones.put("txt_middle3", middleTextId3);

     //zones.put("ad_interstitial", interstitial);
    //zones.put("ad_popunder", popunder);
    

    //Instance service
    
    ReporoAds reporoService = ReporoAds.getNewInstance();
    reporoService.setActivationCode(activationCode); //Activation Code. Mandatory
    reporoService.setBlockBanner(true); //Avoid duplicated banners. Optional, default true
    reporoService.setBlockCampaign(false); //Avoid duplicated campaigns. Optional, default false
    //reporoService.callReporoLib();
    System.out.println("Reporo Example "+ ReporoAds.class.getCanonicalName());
    //Single Call per page.
    HashMap<String,ReporoAdBean> ads = reporoService.getAds(zones, request.getHeader("X-Forwarded-For"), request.getHeader("User-Agent"));
   //HashMap<String,ReporoAdBean> ads = reporoService.getAds(zones, request);    
    session.setAttribute("reporo", reporoService);
    //TODO Store ads inside session   
    session.setAttribute("ads", ads);
 System.out.println("Hello babe: " + ((HashMap<String,ReporoAdBean>)session.getAttribute("ads")).get("img_top").getMarkup());//.get("txt_middle2").getMarkup());
  String as=((HashMap<String,ReporoAdBean>)session.getAttribute("ads")).get("img_top").getMarkup();
    %>
 
