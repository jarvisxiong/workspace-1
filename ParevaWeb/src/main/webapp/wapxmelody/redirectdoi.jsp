<%@include file="commonfunc.jsp"%>
<%
String redirectionurl=request.getParameter("doiredirect");

   String myip=request.getHeader("X-Forwarded-For");
         
         
             if (myip != null) {           
                        int idx = myip.indexOf(',');
                        if (idx > -1) {
                        myip = myip.substring(0, idx);
                        }
                 }
             
        String myisp=ipprovider.findIsp(myip);
       if(myisp.toLowerCase().contains("mtn")) myisp="mtn";
       else if(myisp.toLowerCase().contains("vodacom") || myisp.toLowerCase().contains("research in motion uk limited")) myisp="vodacom";
       else if(myisp.toLowerCase().contains("cellc") || myisp.toLowerCase().contains("cell-c") || myisp.toLowerCase().contains("cell c")) 
           myisp="cellc";
       else if(myisp.toLowerCase().contains("heita") || myisp.toLowerCase().contains("8ta"))
           myisp="heita";
       else
           myisp="unknown";
       

    
             
if(redirectionurl!=null && redirectionurl.trim().length()>0)
{
    System.out.println("redirectzadoi "+redirectionurl);
     UserClicksPojo userclicks=new UserClicksPojo();
     
     String uniqueid="";
     synchronized(this){
     uniqueid=Misc.generateUniqueId();
     }
 userclicks.setUniqueid(uniqueid.trim());
 userclicks.setRedirecturl(redirectionurl);
 userclicks.setIpaddress(myip);
 userclicks.setMsisdn(aReq.get("msisdn"));
 userclicks.setCampaignUnique(aReq.get("cid"));
 userclicks.setaCreated(new Date());
 userclicks.setNetworkCode(myisp);
 
 try{
 userclicksdao.saveUserClicks(userclicks);
 }catch(Exception e){}
 
 
 if(!aReq.get("msisdn").equalsIgnoreCase("")) {
     String identifiedmsisdn=aReq.get("msisdn");
     MobileClub club=null;
     String clubUnique="";   
     
     club = UmeTempCmsCache.mobileClubMap.get(domain);
     SdcMobileClubUser clubuser = null;
     
     if (club != null) 
      clubUnique = club.getUnique();
     if (clubUnique != null){
         clubuser = umemobileclubuserdao.getClubUserByMsisdn(identifiedmsisdn,clubUnique);
         
         if (clubuser != null) user = umeuserdao.getUser(clubuser.getParsedMobile());
         
         if(user!=null){
             boolean isActive = false;
             String wapid = "";
             isActive = mobileclubdao.isActive(user, club);
             wapid = user.getWapId(); 
              if (isActive) {
                // System.out.println("user redirected to  redirectdoi"+"http://" + dmn.getDefaultUrl() + "/?id="	+ wapid+ " msisdn: "+clubuser.getParsedMobile());
		response.sendRedirect("http://" + dmn.getDefaultUrl() + "/?id="	+ wapid);
		return;		
                }
         }
          
     }
     
 }
 
 
 
 
 
    response.sendRedirect(redirectionurl);
    return;
}

%>