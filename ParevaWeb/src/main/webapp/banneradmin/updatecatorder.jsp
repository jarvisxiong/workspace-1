<%@ include file="/WEB-INF/jspf/coreimport.jspf" %>
<%@ include file="/WEB-INF/jspf/db.jspf" %>

<%
   AdminRequest aReq = new AdminRequest(request);
   String itemlist[] = aReq.get("itemlist[]").split(",");
   
   String cat = aReq.get("cat");   
   if (cat.equals("")) return;
   
   //Connection con = DBHStatic.getConnection();
   String sqlstr = "DELETE FROM itemLists WHERE aListUnique='" + cat + "'";
   Transaction trans=dbsession.beginTransaction();
   Query query=null;
   query=dbsession.createSQLQuery(sqlstr);
   query.executeUpdate();
   //DBHStatic.execUpdate(con, sqlstr);
   
   for (int i=0; i<itemlist.length; i++) {
       sqlstr = "INSERT INTO itemLists VALUES('" + cat + "','" + itemlist[i] + "','" + i + "')";
       query=dbsession.createSQLQuery(sqlstr);
       query.executeUpdate();
       //DBHStatic.execUpdate(con, sqlstr);
   }
   trans.commit();
   dbsession.close();
   //DBHStatic.closeConnection(con);
%>