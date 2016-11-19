<%@ page import="com.mixmobile.anyx.sdk.*, java.util.*, java.sql.*" %>
<%
   AnyxRequest aReq = new AnyxRequest(request);
   String itemlist[] = aReq.get("itemlist[]").split(",");
   
   String cat = aReq.get("cat");   
   if (cat.equals("")) return;
   
   Connection con = DBHStatic.getConnection();
   String sqlstr = "DELETE FROM itemLists WHERE aListUnique='" + cat + "'";
   DBHStatic.execUpdate(con, sqlstr);
   
   for (int i=0; i<itemlist.length; i++) {
       sqlstr = "INSERT INTO itemLists VALUES('" + cat + "','" + itemlist[i] + "','" + i + "')";
       DBHStatic.execUpdate(con, sqlstr);
   }
   DBHStatic.closeConnection(con);
%>