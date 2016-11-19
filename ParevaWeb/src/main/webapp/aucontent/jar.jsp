<%@ page import="com.mixmobile.anyx.sdk.*, com.mixmobile.anyx.cms.*, java.util.*, java.sql.*, java.io.*" %><%
//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String msisdn = aReq.getMobile();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";

String fileName = "jar";
//***************************************************************************************************
String singleServer = System.getProperty("CMS_singleServer");
String dir = System.getProperty("installDir") + "/data/javagames";

Handset handset = HandsetDao.getHandset(request, true);

Connection con = DBHStatic.getConnection();
String sqlstr = "";
String status = "";
String statusDesc = "";

String id = aReq.get("i");
String javaUnique = "";
String jarFile = "";
String fileType = "";
String owner = "";
String clientRef = "";

sqlstr = "SELECT * FROM itemOrderLog WHERE aUnique='" + id + "'";
System.out.println(sqlstr);
ResultSet rs = DBHStatic.getRs(con, sqlstr);
if (rs.next()) {
    status = "ok";
    javaUnique = rs.getString("aItemUnique");
    jarFile = rs.getString("aDataFile");
    fileType = rs.getString("aItemParam");    
    owner = rs.getString("aOwner"); 
    clientRef = rs.getString("aClientRef");
}
else { status = "error"; statusDesc = "item not found in itemOrderLog"; }
rs.close();

System.out.println("jar status:" + status);

if (jarFile.toLowerCase().endsWith(".jad")) jarFile = jarFile.substring(0, (jarFile.length()-4));
System.out.println("Status: " + status);

if (status.equals("ok")) {
    
    System.out.println("JarFile: " + dir + "/" + javaUnique + "_" + fileType + "/" + jarFile + ".jar");
    
    int k;
    int totalBytes = 0;
    char[] chars = new char[1024];
    File f1 = new File(dir + "/" + javaUnique + "_" + fileType + "/" + jarFile + ".jar");
    
    if (f1.exists()) {      
        response.setContentType("application/java-archive");
        
        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(f1), "iso-8859-1"));
        while (true) {
            k = fin.read(chars, 0, chars.length);
            if (k == -1) break;
            out.write(chars, 0, k);
            totalBytes += k;
        }
        fin.close();
        out.flush();
        System.out.println("File loaded ok. Total bytes written: " + totalBytes);
        statusDesc = totalBytes + " bytes written";
       
    } else {
        status = "error";
        statusDesc = "Jar file not found";
    }
}
else {
    response.setContentType("text/vnd.wap.wml");
    %><?xml version="1.0"?>
    <!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

    <wml>
    <card id="c1" title="Download">
    <p>System error.</p>		
    </card>
    </wml><%
}

if (singleServer.equals("true") && !clientRef.equals("")) {
    ItemLogger log = new ItemLogger(clientRef);
    log.setStatus(status);
    log.setStatusDesc(statusDesc);    
    log.setRetrieved(MiscDate.now24sql());    
    log.update();    
}

sqlstr = "UPDATE itemOrderLog SET aStatus='" + status + "',aRetrieveCount=(aRetrieveCount+1)"
        + ",aStatusDesc='" + statusDesc + "',aRetrieved='" + MiscDate.now24sql() + "'"
        + " WHERE aUnique='" + id + "'"; 
System.out.println(sqlstr);             
DBHStatic.execUpdate(con, sqlstr);  

DBHStatic.closeConnection(con);


// EI MERKKEJÄ BODY-OSAAN !!!


%>