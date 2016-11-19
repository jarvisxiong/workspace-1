<%@include file="commonfunc.jsp"%>
<%

System.out.println("melodybilling fail.jsp called upon ");
PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
	TemplateEngine templateengine=null;
	try{
     ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
     ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
     templateengine=(TemplateEngine) ac.getBean("templateengine");
       
     }
        catch(Exception e){}
        
 String enMsisdn = aReq.get("mid");
 String deMsisdn="",failpage="fail";
 
 Integer counter=0;
try{
counter=(Integer) session.getAttribute("counter");
System.out.println("melodybilling fail.jsp called upon counter value is "+counter);
      counter=counter+1;
      session.setAttribute("counter",counter);
      System.out.println("melodybilling  setting counter to "+counter);
}catch(Exception e){counter=0;}
 
  if(!enMsisdn.equals("")) deMsisdn = MiscCr.decrypt(enMsisdn);
  
  context.put("msisdn",deMsisdn);
  context.put("contenturl","http://"+dmn.getContentUrl());
  
  PebbleEngine engine=templateengine.getTemplateEngine(dmn.getUnique());
  engine.getTemplate(failpage).evaluate(writer, context); 

%>