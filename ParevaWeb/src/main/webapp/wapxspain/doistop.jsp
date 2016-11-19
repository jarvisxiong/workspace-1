
<%@page import="com.zadoi.service.ZaDoi"%>
<%
ZaDoi zadoi = new ZaDoi();
long beforeAuth = System.currentTimeMillis();
String token = zadoi.authenticate();

String serviceName="0feeeaa6-38c6-4187-87a5-ec0f95b3e839";
String msisdn="27833260830";
boolean responses=zadoi.delete_DoubleOptIn_Record(token, serviceName, msisdn);
System.out.println("Zadoi deletion done for msisdn "+msisdn+" with response "+responses);

%>

<%=msisdn%> <%=responses%>