<%@page import="com.zadoi.service.ZaDoi"%>
<%@include file="coreimport.jsp"%>
<%
            String mobileno="";
            String campaignid="";
            String clubUnique = "";
            UmeDomain dmn=null;
            String phase ="";
            String domain="";
            MobileClub club=null;
            UmeClubDetails clubdetails=null;
            UmeSessionParameters aReq = new UmeSessionParameters(request);
            MobileClubCampaign cmpgn = null;
            
            String landingPage=aReq.get("landingpage");
            
            String myip="";
            try{
            myip=(String) request.getAttribute("userip");
            }catch(Exception e){myip="";}
            
            try{
            if(myip==null || myip.trim().length()<=0 || myip.trim().equalsIgnoreCase("")) myip=request.getHeader("X-Forwarded-For");
            
            }catch(Exception e){myip="";}
            
                
         
         
             if (myip != null) {           
                        int idx = myip.indexOf(',');
                        if (idx > -1) {
                        myip = myip.substring(0, idx);
                        }
                 }
            
            try{
              
           dmn = (UmeDomain) request.getAttribute("umedomain");
           domain=dmn.getUnique();
            }
            catch(Exception e){System.out.println("Exception smsoptin error while reading umedomain parameter "+mobileno+" "+e);e.printStackTrace();}
           try
           {
           mobileno=(String) request.getParameter("submsisdn");
           //System.out.println("Msisdn: READ WAS  "+mobileno.toString());
           
           }catch(Exception e){mobileno="";}
           
                    if(mobileno!=null && mobileno.equals("")){
        	   response.sendRedirect("/");
        	   
           }                    
           if(mobileno==null || mobileno.trim().length()<=0){
               try{
               
               mobileno=(String) request.getParameter("msisdn");
               }
               catch(Exception e){mobileno="";}
               
           }
           
          club = UmeTempCmsCache.mobileClubMap.get(domain);
           
           //System.out.println("smsoptin debug "+dmn.getDefaultUrl()+"domain unique: "+domain+" mobileno: "+mobileno+" club "+club.getUnique());
           if(mobileno!=null && mobileno.trim().length()>0){
                if(mobileno.contains("+")) mobileno=mobileno.replace("+","").trim();
           }
            if (mobileno!=null && mobileno.startsWith("0")) mobileno = "27" + mobileno.substring(1);  
            //System.out.println("=====MOBILE NO IN SMSOPTIN IS ===== "+mobileno);
            try{
           campaignid=request.getParameter("cid");
            }catch(Exception e){campaignid="";}
            
            if(campaignid!=null && campaignid.trim().length()>0) {
                cmpgn = UmeTempCmsCache.campaignMap.get(campaignid);
            }
            
           boolean NotvalidMobileNo=checkMsisdn(mobileno);
           
           try{
           phase=(String) request.getParameter("phase");
           } catch(Exception e){phase=null;}
           
        
       if(!NotvalidMobileNo && (phase==null || phase.trim().length()<=0))
           {
             if (mobileno!=null && mobileno.startsWith("0")) mobileno = "27" + mobileno.substring(1);  
            
             //System.out.println("smsoptin testing domain "+domain+" defaulturl "+dmn.getDefaultUrl()+" MOBILE NO "+mobileno);
             UmeUser user = null;
             UmeUserDao umeuserdao=null;
             SdcMobileClubUser clubuser = null;
             UmeMobileClubUserDao umemobileclubuserdao=null;
             MobileClubDao mobileclubdao=null;
             SmsDoiLogDao smsdoilogdao=null;
             PassiveVisitorDao passivevisitordao=null;
             ZACPA zacpa=null;
             
             try{
                 ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
                 ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
                 umemobileclubuserdao=(UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
                 umeuserdao=(UmeUserDao) ac.getBean("umeuserdao");
                 mobileclubdao=(MobileClubDao) ac.getBean("mobileclubdao");
                 smsdoilogdao=(SmsDoiLogDao) ac.getBean("smsdoilogdao");
                 passivevisitordao=(PassiveVisitorDao) ac.getBean("passivevisitor");
                 zacpa=(ZACPA) ac.getBean("zacpa");
             }
             catch(Exception e){}
             
             if (club != null) 
                 clubUnique = club.getUnique();
             
             //System.out.println("MOBILE CLUB is "+mobileno+" clubunique is "+clubUnique);
             if (clubUnique != null){
                 if(mobileno!=null){
                     try{
                if(mobileno.trim().length()>11)
                mobileno=mobileno.substring(0,11);
                     }catch(Exception e){}
                
                 }
		clubuser = umemobileclubuserdao.getClubUserByMsisdn(mobileno,clubUnique);
                clubdetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
                if(clubdetails==null){
                	System.out.println("CLUB DETAILS IS NULL");
                }
                }
             if (clubuser != null) user = umeuserdao.getUser(clubuser.getParsedMobile());
             
             boolean isActive = false;
             String wapid = "";
             String requestuid=Misc.generateUniqueId();
             
              String initial=request.getHeader("User-Agent");
                    int end = initial.indexOf(")");
                    int start = initial.indexOf("(") + 1;
                       String[] aux =null; 
                       String uagent="";
                       
                       try{
                       aux =initial.substring(start, end).split(";");
                       aux[1] =java.net.URLEncoder.encode(aux[1].trim(), "UTF-8");
                       uagent=aux[1];
                       }catch(Exception e){uagent="";}
                       
	     String notificationurl = "http://" + dmn.getDefaultUrl()+ "/smsresponse.jsp?phase=1&optin=1&cid=" +campaignid+"&myuid="+requestuid+"&ip="+myip+"&l="+landingPage+"&uagent="+uagent;
             
             if (user != null) {
		isActive = mobileclubdao.isActive(user, club);
		wapid = user.getWapId();               
             
                System.out.println("mtncellcoptin is user active? "+mobileno+" isActive "+isActive+" club "+club.getUnique());
             if (isActive) {
		response.sendRedirect("http://" + dmn.getDefaultUrl() + "/?id="	+ wapid);
		return;		
                }
             }
             
             if(!isActive)
             {
                 //System.out.println("Trying to send smsoptin now to mobile "+mobileno);
                 String teaser=clubdetails.getTeaser();
                int freeDay=1;
                try{
                freeDay=Integer.parseInt(String.valueOf(clubdetails.getFreeDay()));
                }catch(Exception e){freeDay=1;}
                 if(teaser==null || teaser.length()<=0) teaser=" X-Rated Vids - 18+ ONLY";
                 if(freeDay<0) freeDay=1;
                 
                 
                
                 ZaDoi zadoi = new ZaDoi();
                 long beforeAuth = System.currentTimeMillis();
	         String token = zadoi.authenticate();
                 
                 String clubname = club.getClubName();
                 String serviceid = club.getOtpServiceName();
                 String networkCode="";
                 
                 try{
                     if(mobileno!=null && !mobileno.equalsIgnoreCase("")){
                         if(mobileno.trim().length()>11)
                         mobileno=mobileno.substring(0,11);
                         
                 networkCode=zadoi.request_MsisdnNetwork(token, mobileno);
                     }
                 }
                 catch(Exception e)
                            {
                              System.out.println("Exception SMSOPTIN for msisdn "+mobileno+" club"+clubname+" token:"+token+" "+e);
                              e.printStackTrace();
                             }
                 //System.out.println("NetworkCode name "+networkCode);
                 
                 if(networkCode.trim().equalsIgnoreCase("unknown")){
//                  response.sendRedirect("http://" + dmn.getDefaultUrl()+"/zaconfirm.jsp?msisdn=" + mobileno.trim()+ "&cid=" + campaignid+"&submsisdn="+mobileno.trim());
//                  return;
                     response.sendRedirect("http://"+dmn.getDefaultUrl()+"/smsfailure.jsp?invalidno="+mobileno);
                     return;
                  }
                 
                 else{
                     if(mobileno!=null && !mobileno.equalsIgnoreCase("")){
                         
                         
                      int frequency=3;
                      try{
                      frequency=clubdetails.getFrequency();
                      }catch(Exception e){frequency=3;}
                      
                      String requestUid=Misc.generateUniqueId();
                       Map<String, Object> xhtmlImagesMap = UmeTempCmsCache.xhtmlImages.get("xhtml_"+domain);
                       String headerImage="",logos="";
                       if(xhtmlImagesMap.get("img_header1_4")!=null)
                       headerImage=(String)xhtmlImagesMap.get("img_header1_4");
                       
                       if(headerImage!=null && headerImage.trim().length()>0) logos="http://"+dmn.getDefaultUrl()+"/images/wap/"+headerImage;
                        else
		       logos="http://"+dmn.getDefaultUrl()+"/static/ZA/x-rated/img/xratedsmall.png";
		       
                       
                    String returnURL = "http://" + dmn.getDefaultUrl() + "/zaconfirm.jsp?optin=1&cid=" +campaignid+"&myuid="+requestUid+"&ip="+myip+"&l="+landingPage+"&uagent="+uagent;
                      
                   String wappage =zadoi.wapOptIn(token, serviceid,clubname,frequency,mobileno,returnURL, logos,"", "", teaser, freeDay);
                                  
                        // System.out.println("Else Portion of Unknown Network Check");
                        
                        if(wappage!=null){
                           System.out.println("mtncellcoptin wappage URL "+wappage);
                            if(cmpgn!=null && cmpgn.getSrc().trim().endsWith("RS")){
                          String parameter1=(String) session.getAttribute("revparam1");
                          String parameter2=(String) session.getAttribute("revparam2");
                          String parameter3=(String) session.getAttribute("revparam3");  
                          
                          String cpaloggingquery="UPDATE revShareVisitLog SET aParsedMobile='"+mobileno+"' WHERE aCampaignId='"+campaignid+"' AND  (parameter1='"+parameter1+"' AND parameter2='"+parameter2+"'"
                              + " AND parameter3='"+parameter3+"')";
                      
                      int updateRow=zacpa.executeUpdateCPA(cpaloggingquery);
                            
                            }
                            
                                    if(cmpgn!=null && cmpgn.getSrc().trim().endsWith("CPA")){
                          String cpaparameter1=(String) session.getAttribute("cpaparam1");
                          String cpaparameter2=(String) session.getAttribute("cpaparam2");
                          String cpaparameter3=(String) session.getAttribute("cpaparam3");  
                          
                          String cpavisitUpdateQuery="UPDATE cpavisitlog SET aParsedMobile='"+mobileno+"' WHERE aCampaignId='"+campaignid+"' AND  (aHashcode='"+cpaparameter1+"' AND cpacampaignid='"+cpaparameter2+"'"
                              + " AND clickid='"+cpaparameter3+"')";
                      
                      int updateRow=zacpa.executeUpdateCPA(cpavisitUpdateQuery);
                            
                            }
                            
                            
                            
                            
                            boolean exist=passivevisitordao.exists(mobileno, club.getUnique());
                            if(!exist){
                            PassiveVisitor visitor=new PassiveVisitor();
                                 visitor.setUnique(SdcMisc.generateUniqueId());
                                 visitor.setClubUnique(club.getUnique());
                                 visitor.setFollowUpFlag(0);
                                 visitor.setParsedMobile(mobileno);
                                 visitor.setStatus(0);
                                 visitor.setCreated(new Date());
                                 passivevisitordao.insertPassiveVisitor(visitor);
                            }
                                
                            //System.out.println("Sent msisdn to "+mobileno+" sms response is "+confirmed);
                           response.sendRedirect(wappage);
                           return;
                        }
                         else {
//                        	 SmsDoiResponse smsDoiResponse=new SmsDoiResponse();
//                             smsDoiResponse.setRequestUid(requestUid);
//                             smsDoiResponse.setClubName(club.getName());
//                             smsDoiResponse.setConfirmed(confirmed);
//                             smsDoiResponse.setMsisdn(mobileno);
//                             smsDoiResponse.setToken(token);
//                             smsDoiResponse.setRespondedTime(new java.sql.Timestamp(System.currentTimeMillis()));
//                             smsdoilogdao.saveSmsDoiResponse(smsDoiResponse);  
//                             System.out.println("Couldn't send msisdn to "+mobileno+" sms response is "+confirmed);
//                            response.sendRedirect("http://"+dmn.getDefaultUrl()+"/smsfailure.jsp?invalidno="+mobileno);
//                            return;
                                
			}
                        
                 }
                        
                 }
                 
                 
             }
             
             
             
           }
%>



<%!
    boolean checkMsisdn(String mobileno){
       
        boolean validmsisdn=(mobileno != null && !mobileno.trim().equals(""))
			&& (mobileno.length() < 8 || (!mobileno.trim().startsWith("07")
					&& !mobileno.trim().startsWith("08")
                                        && !mobileno.trim().startsWith("06")
					&& !mobileno.trim().startsWith("0027") && !mobileno.trim().startsWith("27")));
        
        
        return validmsisdn;
        
    }



%>