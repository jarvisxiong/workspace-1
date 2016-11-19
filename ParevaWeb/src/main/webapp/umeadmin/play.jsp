<%@ page import="ume.pareva.dao.*,ume.pareva.sdk.*, java.util.*, java.text.*, java.io.*" %><%

//***************************************************************************************************
SdcRequest aReq = new SdcRequest(request);
String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));
//LangProps lp = LangProps.getFromContext(service.getUnique(), fileName, langcode, domain, application, true);
//***************************************************************************************************

String path = Misc.hex8Decode(aReq.get("i"));
System.out.println(path);

File f1 = new File(path);
if (!f1.exists()) return;

System.out.println("f1: " + f1.getPath());
try {
response.setContentType("audio/mpeg");
response.setContentLength((int) f1.length());

BufferedInputStream fin = new BufferedInputStream(new FileInputStream(f1));
ServletOutputStream sout = response.getOutputStream();

byte buf[]=new byte[1024];
int len;
while((len=fin.read(buf))>0)
sout.write(buf,0,len);
sout.flush();

System.out.println("Done");
} catch (Exception e) { System.out.println(e); e.printStackTrace(); }
return;
%>