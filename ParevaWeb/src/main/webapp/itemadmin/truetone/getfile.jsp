<%@include file="/WEB-INF/jspf/coreimport.jspf"%>

<%

//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";

String fileName = "getfile";
//***************************************************************************************************

String dir = System.getProperty("installDir");
dir += Misc.hex8Decode(aReq.get("dir"));
String file = Misc.hex8Decode(aReq.get("file"));

System.out.println("dir:"  + dir);
System.out.println("file: " + file);
int k;
char[] chars = new char[128];
File f1 = new File(dir, file);

System.out.println(f1 + ": exists: " + f1.exists());

response.setContentType("audio/mpeg");
BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(f1), "iso-8859-1"));
while (true) {
	k = fin.read(chars, 0, chars.length);
	if (k == -1) break;
	out.write(chars, 0, k);
}
fin.close();
%>
