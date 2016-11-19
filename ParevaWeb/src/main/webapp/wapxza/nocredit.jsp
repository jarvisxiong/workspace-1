<%@include file="commonfunc.jsp"%>
<%
String enMsisdn = aReq.get("mid");
String deMsisdn="",billingpage="";

Integer counter=0;
try{
counter=(Integer) session.getAttribute("counter");
}catch(Exception e){counter=0;}

System.out.println("melodybilling counter "+counter);

Map<String, Object> xhtmlImagesMap = UmeTempCmsCache.xhtmlImages.get("xhtml_"+domain);
String headerImage="";

//              String footerImage="";
                  if(xhtmlImagesMap.get("img_header1_4")!=null)
    		headerImage=(String)xhtmlImagesMap.get("img_header1_4");

     String logos="http://"+dmn.getContentUrl()+"/images/wap/"+headerImage;


    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
	TemplateEngine templateengine=null;
	try{
     ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
     ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
     templateengine=(TemplateEngine) ac.getBean("templateengine");
       
     }
        catch(Exception e){}
        
  if(!enMsisdn.equals("")) deMsisdn = MiscCr.decrypt(enMsisdn);
  

  if(counter==null) counter=0;
    System.out.println("melodybilling counter "+counter+" for msisdn "+deMsisdn);  
    
    if(counter!=null && counter>=3) billingpage="fail";
    else billingpage="nocredit";
    
  context.put("msisdn",deMsisdn);
  context.put("landingpage",billingpage+".html");
  context.put("headerimage",logos);
  context.put("contenturl","http://"+dmn.getContentUrl());
  
  String failresponsecode=request.getParameter("failcode");
  String failresponsedesc=request.getParameter("faildesc");
  String statusMsg="";
  
  if(failresponsecode!=null && failresponsedesc!=null){
      if(failresponsecode.equals("51")) statusMsg="Insufficient Funds! Please topup your Credit";
      else statusMsg=" Billing Failed! ResponseCode: "+failresponsecode+"  Billing failed description: "+failresponsedesc;
      counter=counter+1;      
      System.out.println("melodybilling  setting counter to "+counter);
  }
  if(null!=statusMsg && !"".equalsIgnoreCase(statusMsg))
  {
      context.put("showMsg","true");
      context.put("statusmsg",statusMsg);
  }
      
  session.setAttribute("counter",counter);
    PebbleEngine engine=templateengine.getTemplateEngine(dmn.getUnique());
    engine.getTemplate(billingpage).evaluate(writer, context); 



%>
