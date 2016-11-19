<%@ page import="com.mixmobile.anyx.sdk.*, java.util.*, java.io.*, java.sql.*, net.sourceforge.wurfl.core.*" %>

<%
//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "iso-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "supphones_list";
//***************************************************************************************************

Device device = null;
WURFLHolder wurflHolder = (WURFLHolder) SdkTempCache.cxt.getAttribute("net.sourceforge.wurfl.core.WURFLHolder");
WURFLUtils utils = wurflHolder.getWURFLUtils();
String wbrand = "";
String wmodel = "";

DBHandler dbH = (DBHandler) Misc.cxt.getAttribute("dbHandler");
String anyxDb = System.getProperty("dbName");
String statusMsg = "";
Connection con = dbH.getConnection(anyxDb);
ResultSet rs = null;
String sqlstr = "";

String dataDir = System.getProperty("installDir") + "/data/javagames";

String unique = aReq.get("unq");
String submit = aReq.get("doclear");
String clear = aReq.get("clear");
String ftype = aReq.get("ftype", "game");

String phone = "";

List<String> phones = new ArrayList();
Map<String, List<String>> map = new Hashtable<String, List<String>>();
List list = null;

if (!submit.equals("") && !clear.equals("")) {
    
    File ff = null;
    String jad = "";
    
    sqlstr = "SELECT * FROM supportedPhones WHERE aJavaUnique='" + unique + "' AND aFileType='" + ftype + "'";
    rs = DBHStatic.getRs(con, sqlstr);
    while(rs.next()) {
        jad = rs.getString("aJadFile");
        ff = new File(dataDir + "/" + unique + "_" + ftype + "/" + jad);
        if (ff.exists() && !ff.isDirectory()) ff.delete();
        
        jad = jad.substring(0,jad.length()-4) + ".jar";
        ff = new File(dataDir + "/" + unique + "_" + ftype + "/" + jad);
        if (ff.exists() && !ff.isDirectory()) ff.delete();        
        sqlstr = "DELETE FROM supportedPhones WHERE aUnique='" + rs.getString("aUnique") + "'";
        DBHStatic.execUpdate(con, sqlstr);
    }
    rs.close();
}

try {
sqlstr = "SELECT aPhoneUnique, aJadFile, aLangCode FROM supportedPhones WHERE aJavaUnique='" + unique + "' ORDER BY aPhoneUnique";
System.out.println(sqlstr);
rs = DBHStatic.getRs(con, sqlstr);
    
while(rs.next()) {
    device = utils.getDeviceById(rs.getString("aPhoneUnique"));
    if (device==null) {
        System.out.println("Device NOT found: " + rs.getString("aPhoneUnique"));
        continue;
    }

    wbrand = device.getCapability("brand_name").trim().toUpperCase();
    wmodel = device.getCapability("model_name").trim().toUpperCase();

    if (wbrand.equals("")) wbrand = "UNKNOWN_BRAND";
    if (wmodel.equals("")) wbrand = "UNKNOWN_MODEL";

    wbrand = Misc.replaceChars(wbrand, " ", "");
    wbrand = Misc.replaceChars(wbrand, "-", "");
    wmodel = Misc.replaceChars(wmodel, " ", "");
    wmodel = Misc.replaceChars(wmodel, "-", "");

    list = map.get(wbrand + " " + wmodel);
    if (list==null) {
        list = new ArrayList<String>();        
        map.put(wbrand + " " + wmodel, list);
        phones.add(wbrand + " " + wmodel);
    }
    list.add(rs.getString("aPhoneUnique") + " : " + rs.getString("aJadFile") + " : " + rs.getString("aLangCode"));
}
rs.close();
} catch (Exception rr) { System.out.println(rr); }
String orgTitle = "";
sqlstr = "SELECT * FROM javaGames WHERE aUnique='" + unique + "'";
rs = dbH.getRs(con, sqlstr);
if (rs.next()) {
    orgTitle = Misc.utfToUnicode(rs.getString("aTitle"), pageEnc);
}
rs.close();

dbH.closeConnection(con);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">

<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td>
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="blue_14"><b>Java Game Details: </b> <span class="grey_12"><b><%=orgTitle%></b></span></td>
            <td align="right" valign="bottom" class="red_11"><%=statusMsg%></td>
    </tr>
    </table>    
</td></tr>
<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", "5")); %>
    <%@ include file="/itemadmin/java/tabs.jsp" %>
    <br>
</td></tr>

<tr><td align="left" class="grey_11">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom">                
            <select name="ftype" onChange="javascript:this.form.submit();">
                <option value="game" <% if(ftype.equals("game")){%> selected <%}%>>Real game files</option>
                <option value="demo"<% if(ftype.equals("demo")){%> selected <%}%>>Demo files</option>
            </select>
        </td>
        <td align="right">
        Clear all files:&nbsp;&nbsp;<input type="checkbox" name="clear" value="1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<Input type="submit" name="doclear" value="&nbsp;Clear&nbsp;">
        </td>
    </tr>
    </table>
</td></tr>
<tr><td align="left" class="grey_12">
<%
    if (phones.size()>0) {
        
        String str = "";
        String manu = "";
        String model = "";        
        String curManu = "";
        int k = 0;        
        String row = "";

        Iterator iterator = map.keySet().iterator();
        String key = "";
        String  value = "";

        for (int i=0; i<phones.size(); i++) {
            phone = phones.get(i);

            list = map.get(phone);
            
            if (row.equals("row2")) row = "";
            else row = "row2";

            manu = phone.substring(0, phone.indexOf(" "));
            model = phone.substring(phone.indexOf(" ")+1);

            if (curManu.equals("")) {  
                curManu = manu;        
%>
                <table cellpadding="6" cellspacing="0" border="0" class="tableview" width="100%">
                    <tr><th colspan="4" align="left"><b><%=curManu.toUpperCase()%></b></th></tr>
                    <tr class="<%=row%>"><td><%=model%></td><td><img src="/images/glass_dot.gif" width="30" height="1"></td>
                    <td align="right"><select>
                        <% for (int kv=0; kv<list.size(); kv++) { %>
                        <option value=""><%=list.get(kv)%></option>
                        <% } %>
                        </select>
                    </td>
                    </tr>
<%
                
            }
            else if (!manu.equalsIgnoreCase(curManu)) {
                curManu = manu;
%>              
                <tr><th colspan="4" align="left"><b><%=curManu.toUpperCase()%></b></th></tr>
                <tr class="<%=row%>"><td><%=model%></td><td><img src="/images/glass_dot.gif" width="30" height="1"></td>
                    <td align="right"><select>
                       <% for (int kv=0; kv<list.size(); kv++) { %>
                        <option value=""><%=list.get(kv)%></option>
                        <% } %>
                        </select>
                    </td>
                </tr>
<%
                   
            }
            else {
%>
                <tr class="<%=row%>"><td><%=model%></td><td><img src="/images/glass_dot.gif" width="30" height="1"></td>
                <td align="right"><select>
                        <% for (int kv=0; kv<list.size(); kv++) { %>
                        <option value=""><%=list.get(kv)%></option>
                        <% } %>
                        </select>
                    </td>
                </tr>
<%
            } 
        }

%>
                </table>
   
     <% } else { %>
     
     <br><br>
     No Phones Supported
     <br><br>
     
     <% } %>

</td></tr>
<tr><td ><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

</table>

</form>


</body>
</html>




