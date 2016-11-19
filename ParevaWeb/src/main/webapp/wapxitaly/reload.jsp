
<%@include file="coreimport.jsp" %>
<%@page import="ume.pareva.reload.ReloadEverything"%>
<%
ReloadEverything reload=null;

String username="";
String password="";
try{
username=request.getParameter("user");
}
catch(Exception e){username="";}

try{
password=request.getParameter("pass");
}
catch(Exception e){password="";}

try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      reload=(ReloadEverything)ac.getBean("reloadeverything");
      }
      catch(Exception e){
          e.printStackTrace();
      }
boolean restarted=false;

if(username!=null && username.trim().length()>0 && password!=null && password.trim().length()>0)
restarted=reload.reInitialise(username,password);



String status="";
if(restarted) status=username+": Successfully reloaded Everything ";
else status=username+": Something went wrong. Please check with UME Technical.  James Valentine <email>james@umelimited.com</email> ";

System.out.println("RESTARTED DAEMON "+restarted);



%>
<%=status%>