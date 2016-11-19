<%@include file="coreimport.jsp"%>
<%@include file="db.jsp"%>


<%

//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";

String fileName = "ticket";
//***************************************************************************************************

String client = aReq.get("cid").trim();
String item = aReq.get("item").trim();
String msisdn = aReq.get("msisdn").trim();
String itype = aReq.get("itype").trim().toLowerCase();
String clientRef = aReq.get("cref").trim();
String ctype = aReq.get("ctype").trim();
String iLang = aReq.get("ilang", lang);
String iParam = aReq.get("iparam");
Query query=null;
Transaction trans=dbsession.beginTransaction();


String sqlstr = "";

String resp = "";
String itemUnique = "";
String owner = "";
String logUnique = "";
String itemName = "";

if (itype.equals("video")) {
    
    sqlstr = "SELECT * FROM videoClips WHERE aUnique='" + item + "' AND aStatus>'0'";
    System.out.println(sqlstr);
    query=dbsession.createSQLQuery(sqlstr).addScalar("aUnique").addScalar("aOwner").addScalar("aName2").addScalar("aName1");
    java.util.List result=query.list();
    if (result.size()>0) {
        for(Object o:result){
            Object[] row=(Object[]) o;
        itemUnique = String.valueOf(row[0]);
        owner = String.valueOf(row[1]);  
        itemName = Misc.utfToUnicode(String.valueOf(row[2]), pageEnc);
        if (itemName.equals("")) itemName = Misc.utfToUnicode(String.valueOf(row[3]), pageEnc);
        if (itemName.equals("")) itemName = "item_name";
        if (!ctype.equals("")) itemName = itemName.toLowerCase() + "." + ctype;
        else itemName = itemName.toLowerCase() + ".3gp";
    }
}

}
else if (itype.equals("java") || itype.equals("javapro")) {
    
    sqlstr = "SELECT * FROM javaGames WHERE aUnique='" + item + "' AND aStatus>'0'";
    System.out.println(sqlstr);
     query=dbsession.createSQLQuery(sqlstr).addScalar("aUnique").addScalar("aOwner");
    java.util.List result=query.list();
    if (result.size()>0) {
        for(Object o:result){
            Object[] row=(Object[]) o;
        itemUnique = String.valueOf(row[0]);
        owner = String.valueOf(row[1]);  
        itemName = "java.jad";
    }
}
 
}


if (!itemUnique.equals("")) { 
    
    logUnique = Misc.generateLogin(6) + Misc.generateUniqueId();
    
    sqlstr = "INSERT INTO itemOrderLog VALUES('" + logUnique + "','" + itype + "','" + itemUnique + "','" + Misc.parseMobileNumber(msisdn) + "'"
            + ",'" + client + "','pending','ticket ordered','" + MiscDate.now24sql() + "','2008-01-01 12:00:00','','0','','','" + owner + "'"
            + ",'" + clientRef + "','" + iLang + "','" + iParam +"')";
    System.out.println(sqlstr);
    query=dbsession.createSQLQuery(sqlstr)
    
    resp = "http://dls.mixem.com/m/" + itemName + "?i=" + logUnique + "&axud=1";
    System.out.println("TICKET RESP: " + resp);
    
}
else { resp = "error_item_not_found"; }

out.println(resp);

trans.commit();
dbsession.close();

%>