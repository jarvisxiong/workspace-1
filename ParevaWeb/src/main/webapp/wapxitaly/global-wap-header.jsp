<%@page import="ume.pareva.smsapi.*"%>
<%@include file="coreimport.jsp"%>
<%@include file="db.jsp"%>
<%--<%@include file="globalfunction.jsp"%>--%>

<%!
void doRedirect(HttpServletResponse response, String url) {
        String referer = "headers";
        System.out.println("doRedirect: " + url);
        try {
              response.sendRedirect(url); return;
        }
        catch (Exception e) {
                System.out.println("doRedirect EXCEPTION: " + e);
        }
}
String findIsp(Session dbsession, String ipaddress)
{
    String ispnetwork=null;
    try{
        
        String findipquery="SELECT isp FROM ispLookup WHERE INET_ATON('"+ipaddress+"') BETWEEN ip_start AND ip_end LIMIT 1";
        
       Query query=dbsession.createSQLQuery(findipquery).addScalar("isp");
       java.util.List isplist=query.list();
       query.setCacheable(true);
        if(isplist.size()>0)
        {
            for(Object o:isplist){
                String row=o.toString();
                ispnetwork=row;
            }
           
        }
    }
    catch(Exception e){e.printStackTrace();}

System.out.println("wapzatest test: "+" globalwapfunctions.jsp :> isp : "+ispnetwork);
    
    return ispnetwork;
}

%>

<%
//*************************************************************************************
UmeSessionParameters aReq = new UmeSessionParameters(request);
UmeUser user = aReq.getUser();
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();
Transaction dbtransaction=dbsession.beginTransaction();




String domain = dmn.getUnique();
String ddir = dmn.getDefPublicDir();
String lang = aReq.getLanguage().getLanguageCode();

String durl=dmn.getDefaultUrl().toString().trim();
String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String stsReturnId = aReq.get("ID"); //This ID is received only after msisdn lookup process which must be equivalent to sts_id declared below. 

String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));
request.setAttribute("fileName",fileName);

HandsetDao handsetdao=null;
UmeTempCache anyxsdc=null;
UmeLanguagePropertyDao langpropdao=null;
Handset handset = (Handset)session.getAttribute("handset");
MobileClubDao mobileclubdao=null;
VideoClipDao videoclipdao=null;
PebbleEngine za_engine=null;
Misc misc=null;
UmeClubDetails clubdetails=null;
MobileClubCampaignDao campaigndao=null;
UmeUserDao umeuserdao=null;
UmeMobileClubUserDao umemobileclubuserdao=null;
UmeSmsDao umesmsdao=null;
 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      handsetdao=(HandsetDao) ac.getBean("handsetdao");
      anyxsdc=(UmeTempCache) ac.getBean("umesdc");
      langpropdao=(UmeLanguagePropertyDao) ac.getBean("umelanguagepropertydao");
      mobileclubdao=(MobileClubDao) ac.getBean("mobileclubdao");
      videoclipdao=(VideoClipDao) ac.getBean("videoclipdao");
      misc=(Misc) ac.getBean("misc");
      umeuserdao=(UmeUserDao) ac.getBean("umeuserdao");
      umemobileclubuserdao=(UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
      campaigndao=(MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
      umesmsdao=(UmeSmsDao) ac.getBean("umesmsdao");
      //za_engine=(PebbleEngine) ac.getBean("pebbleEngine");
      }
      catch(Exception e){
          e.printStackTrace();
      }
 
SdcLanguageProperty lp = langpropdao.get("general", service, aReq.getLanguage(), dmn);
//***************************************************************************************************
boolean onMainIndexPage = fileName.indexOf("index")>-1; 
        
String crlf = "\r\n";
StringBuilder sbf = new StringBuilder();
sbf.append(fileName.toUpperCase() + ".JSP: " + new Date() + crlf + crlf);
Misc.addHttpHeaders(sbf, request);
Misc.addHttpParameters(sbf, request);
Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
if (dParamMap==null) dParamMap = new HashMap<String,String>();

MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
if(club!=null)
clubdetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

//Map dImages = (Map) ume.pareva.cms.UmeTempCmsCache.xhtmlImages.get("xhtml_" + domain);
//Getting Campaign of visited domain
String campaignId = aReq.get("cid");
MobileClubCampaign cmpg = null;
if (!campaignId.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(campaignId);

String uid = "";
String wapid = "";

String ip = request.getRemoteAddr();
String xprof = aReq.get("xprof");
String subpage = aReq.get("pg");
String stsMsisdn = aReq.get("msisdn");
HttpSession ses = request.getSession();
String msisdn = "";
if(!stsMsisdn.equals("")) msisdn=stsMsisdn;
String enMsisdn = aReq.get("mid");
String fntString = aReq.get("fnt");
String isp=findIsp(dbsession,ip);

if(handset==null) handset=handsetdao.getHandset(request);

//response.setContentType(handset.getContentType(pageEnc));
String backlink = aReq.get("back");
if (backlink.startsWith("-hex-")) backlink = Misc.hex8Decode(backlink.substring(5));

dbtransaction.commit();

Map<String,TemplatePojo> templates=new HashMap<String,TemplatePojo>();
if(getServletContext().getAttribute("za_engine")==null){
    
    templates=anyxsdc.getTemplateMap();
 
TemplatePojo template=templates.get(dmn.getUnique());
String templatefolder=template.getTemplateFolder();
FileLoader za_template_loader = new FileLoader();
//za_template_loader.setPrefix("/opt/pareva/templates/parsed/ZA/x-rated");
za_template_loader.setPrefix(templatefolder.trim());
za_template_loader.setSuffix(template.getSuffix().trim());
za_engine = new PebbleEngine(za_template_loader);
EscaperExtension za_escaper = za_engine.getExtension(EscaperExtension.class);
za_escaper.setAutoEscaping(false);
this.getServletContext().setAttribute("za_engine",za_engine);
}else{
	za_engine=(PebbleEngine)getServletContext().getAttribute("za_engine");
}

%>






