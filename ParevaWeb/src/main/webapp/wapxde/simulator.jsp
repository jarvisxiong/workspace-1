<%@include file="/WEB-INF/jspf/coreimport.jspf"%><%

//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
int sLevel = 0;
try { sLevel = Integer.parseInt(aReq.getSecurityLevel()); } catch (NumberFormatException e) {}
String ugid = aReq.getUserGroup();
String[] packages = aReq.getPackages();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";
String p=aReq.get("p");
String fileName = "simulator";
//LangProps lp = LangProps.getFromContext(anyxSrvc, "general", lang, domain, Misc.cxt, true);
//***************************************************************************************************

String pg = aReq.get("pg");

System.out.println("PGPGP_ " + pg);


/* TemplateEngine templateengine=null;
PebbleEngine za_engine=null;
try{
ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
templateengine=(TemplateEngine) ac.getBean("templateengine");
}
catch(Exception e){
    e.printStackTrace();
}
PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();
context.put("simulate","true");
za_engine=templateengine.getTemplateEngine(domain);
za_engine.getTemplate("test").evaluate(writer, context); */
String simulate=aReq.get("p");
String pageName=aReq.get("n");
//simulate="video";

%>

<html>
 <frameset rows="30,*" border="0" frameborder="0" framespacing="0">
<frame src="topsimu.jsp?pg=<%=pg%>" marginheight="0" marginwidth="0" scrolling="no">
<%if (simulate.equals("video")){ %>
<frame src="index.jsp?xprof=3&pg=<%=pg%>&simulate=<%=simulate%>&pageName=<%=pageName%>&routerresponse=1" marginheight="0" marginwidth="0" scrolling="yes" name="content">
<%}else{ %>
<frame src="/?xprof=3&pg=<%=pg%>&simulate=<%=simulate%>&pageName=<%=pageName%>" marginheight="0" marginwidth="0" scrolling="yes" name="content">
<%} %>
</frameset> 

</html>