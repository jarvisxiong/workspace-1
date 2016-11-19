<%@include file="zaheader.jsp"%>
<%
String mobileno=(String) request.getParameter("msisdn");
String sentDoi=(String) request.getParameter("doirequest");
String status = "Experiencing technical difficulties , please try again later ";

 System.out.println("SMSFAILURE "+mobileno+" == "+sentDoi+" == "+status);

if(sentDoi!=null && !sentDoi.isEmpty() && sentDoi.trim().equalsIgnoreCase("alreadysent")) {
    status="You already made a subscription request for this Service in last 24 Hours! Please try again later ! ";
    
      System.out.println("SMSFAILURE za24hour inside failure message  for msisdn "+mobileno +" and status message is "+status);

      Integer waitingSubscriptionCounter=(Integer)session.getAttribute("waitingSubscriptionCounter");
    	if(waitingSubscriptionCounter==null)
    		waitingSubscriptionCounter=0;
    	
    		
    	
      MobileClubDao mobileclubdao=(MobileClubDao)request.getAttribute("mobileclubdao");
      UmeUser user=(UmeUser)request.getAttribute("user");
      MobileClub club=(MobileClub)request.getAttribute("club");
      UmeTempCache umesdc=(UmeTempCache)request.getAttribute("anyxsdc");
      MobileClubCampaignDao campaigndao=(MobileClubCampaignDao)request.getAttribute("campaigndao");	
      java.util.List notSubscribedClubDomains=null;
      
      try{
      notSubscribedClubDomains=getNotSubscribedClubDomains(club,mobileclubdao,user,umesdc);
      }catch(Exception e){}
      
    		if(waitingSubscriptionCounter>0 && waitingSubscriptionCounter<20){
                    try{
    			session.setAttribute("waitingSubscriptionCounter",waitingSubscriptionCounter+1);
    			UmeDomain sisterDomain=(UmeDomain)notSubscribedClubDomains.get((int) java.lang.Math.floor(java.lang.Math.random()* notSubscribedClubDomains.size()));
                         String enMsi=MiscCr.encrypt(mobileno);  
                        System.out.println(" za24hour redirection LINK "+"http://"+sisterDomain.getDefaultUrl()+"/?mid="+enMsi+"&cid="+campaigndao.getCampaignUnique(sisterDomain.getUnique(), "Auto Redirect"));	
                        
    			response.sendRedirect("http://"+sisterDomain.getDefaultUrl()+"/?mid="+enMsi+"&cid="+campaigndao.getCampaignUnique(sisterDomain.getUnique(), "Auto Redirect"));	
                        return;
                    }catch(Exception e){System.out.println("zasmsfailure for line no.40 Exception "+e);}
    		}
    			
    		  
}


 PrintWriter writer = response.getWriter();
 Map<String, Object> context = new HashMap();
 
context.put("smssuccess","false"); 
context.put("gotomain","http://"+ dmn.getDefaultUrl());
context.put("message",status);
context.put("contenturl","http://"+ dmn.getContentUrl());
engine.getTemplate("status").evaluate(writer, context);
String output = writer.toString();

%>



<%!
java.util.List getNotSubscribedClubDomains(MobileClub club,MobileClubDao mobileclubdao, UmeUser user, UmeTempCache umesdc){
	java.util.List<String> sisterClubList=new ArrayList<String>();
	   // String[] sisterClubArray=new String;
	    String sisterClubs=club.getSisterClubs();
	    //
	    if(sisterClubs!=null && !sisterClubs.trim().equals("") && sisterClubs.contains("?")){
                //System.out.println("SMSFAILURE Sister Clubs: "+sisterClubs);
	    	sisterClubList=new ArrayList<String>(Arrays.asList(sisterClubs.split("\\?")));
	    }
	    else{
	    	if(sisterClubs!=null && !sisterClubs.equals(""))
	    		sisterClubList.add(sisterClubs);
	    	
	    }
	    
	    	boolean activeInSisterClub=false;
	    java.util.List<UmeDomain> notSubscribedClubDomains=new ArrayList<UmeDomain>();
	    java.util.List<String> notSubscribedClubDomainsUrls=new ArrayList<String>();
	    
	    if(sisterClubList!=null && sisterClubList.size()>0){
	    for(String sisterClubUnique: sisterClubList){
	    	MobileClub sisterClub=UmeTempCmsCache.mobileClubMap.get(sisterClubUnique);
	    	if(user!=null){
	    	activeInSisterClub=mobileclubdao.isActive(user,sisterClub);
	    	if(!activeInSisterClub){
	    		String clubDomainUnique=sisterClub.getWapDomain();
	    		//UmeDomain sisterDomain=umesdc.getDomainMap().get(clubDomainUnique);
	    		notSubscribedClubDomains.add(umesdc.getDomainMap().get(clubDomainUnique));
	    		//notSubscribedClubDomainsUrls.add(umesdc.getDomainMap().get(clubDomainUnique).getDefaultUrl());
	    		/* doRedirect(response,"http://" + sisterDomain.getDefaultUrl() +"/?id=" + user.getWapId()+"&mid="+enMsi+"&logtype=redirect");
	    	    return; */
	    	}
	    	}else{
	    		String clubDomainUnique=sisterClub.getWapDomain();
	    		notSubscribedClubDomains.add(umesdc.getDomainMap().get(clubDomainUnique));
	    	}
	    	
	    }
}
	    return notSubscribedClubDomains;
}

%>