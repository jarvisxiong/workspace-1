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
int restarted=-1;

if(username!=null && username.trim().length()>0 && password!=null && password.trim().length()>0)
restarted=reload.reloadServers();//reInitialise(username,password);



String status="";
if(restarted>3) status="200:OK";
else status="1001:ERROR";

response.setStatus(200);


System.out.println("RESTARTED THE HASHMAPS "+restarted);



%>
<%=status%>