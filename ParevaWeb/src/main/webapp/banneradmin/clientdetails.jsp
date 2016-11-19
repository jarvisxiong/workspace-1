<%@ include file="/WEB-INF/jspf/coreimport.jspf" %>
<%@ include file="/WEB-INF/jspf/db.jspf" %>

<%
//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String adminGroup = aReq.getAdminGroup();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "iso-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "clients";


ClientDao clientdao=null;
ServerParam appserverparameters=null;

try{
     ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
     ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
     clientdao=(ClientDao) ac.getBean("clientdao");
     appserverparameters=(ServerParam) ac.getBean("appserverparameters");
    
     }
     catch(Exception e){
         e.printStackTrace();
     }

//***************************************************************************************************

if (!adminGroup.equals("9")) {
    try { application.getRequestDispatcher("/clientmanagement/clientdetails.jsp?unq=" + ugid).forward(request,response); }
    catch (Exception e) { System.out.println(e); }
    return;
}

String parent = System.getProperty("CMS_clientParent");
String statusMsg = "";
//Connection con = null;
//ResultSet rs = null;
//ResultSet rs2 = null;
String sqlstr = "";
Query query=null;
Transaction trans=dbsession.beginTransaction();
String add = aReq.get("add");
String dm = aReq.get("dm");
String ss = aReq.get("sort", "cr");
String title = aReq.get("title").trim();
String owner = aReq.get("owner").trim();
String unique = "";

if (dm.equals("")) {
    Cookie[] cookies = request.getCookies();
    Cookie ck = null;
    if (cookies!=null) {
        for (int i=0; i<cookies.length; i++) {
            ck = cookies[i];
            if (ck!=null && ck.getName().equals("_MXMDOMAINUNIQUE")) { dm = ck.getValue(); break; }
        }
    }
}
else {
    if (dm.equals("null")) dm = "";
    Cookie ck = new Cookie("_MXMDOMAINUNIQUE", dm);
    ck.setMaxAge(-1);
    ck.setPath("/");
    response.addCookie(ck);
}

if (!add.equals("")) {

    if (title.equals("")) statusMsg = "Client name is missing.";
    else {
        //con = DBHStatic.getConnection();

        title = Misc.encodeForDb(title, pageEnc);
        unique = Misc.generateUniqueId();

        sqlstr = "INSERT INTO userGroups VALUES('" + unique + "','" + title +"','deviprasad'"
                + ",'1','" + MiscDate.now24sql() + "','" + MiscDate.now24sql() + "','','0','" + parent + "','1','')";

        System.out.println(sqlstr);
        query=dbsession.createSQLQuery(sqlstr);
        query.executeUpdate();
        //DBHStatic.execUpdate(con, sqlstr);
        //DBHStatic.closeConnection(con);
        appserverparameters.reloadUserGroupNames();

        //DBHStatic.closeConnection(con);

        try { Misc.cxt.getRequestDispatcher("/clientmanagement/clientdetails.jsp?unq=" + unique).forward(request,response); }
        catch (Exception e) { System.out.println(e); }
        return;
    }
}
trans.commit();
dbsession.close();

java.util.List list  = clientdao.getAllClients(ss);

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form action="<%=fileName%>.jsp" method="post" name="form1">

<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="3" border="0" width="100%">
        <tr>
            <td align="left" valign="bottom" class="big_blue">Clients</td>
            <td align="right" class="status_red"><b><%=statusMsg%></b></td>
        </tr>
    </table>

</tr></td>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

    <table cellpadding="6" cellspacing="0" border="0" width="100%">
    <tr>
        <td align="left" class="grey_12"><b>ADD NEW CLIENT</b></td>
    <td class="grey_11">Client Name:&nbsp;&nbsp;<input type="text" size="30" name="title" class="textbox" value="<%=title%>"></td>
    <td align="right"><input type="submit" name="add" value="&nbsp;ADD NEW&nbsp;"></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

    <table cellpadding="6" cellspacing="0" border="0" width="100%">
    <tr>
    <td class="grey_11">Sort List by:</td>
    <td class="grey_11">
        <select name="sort" onChange="javascript:this.form.submit();">
            <option value="cr" <% if (ss.equals("cr")){%> selected <%}%>>Created&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
            <option value="nm" <% if (ss.equals("nm")){%> selected <%}%>>Name</option>
            <option value="cn" <% if (ss.equals("cn")){%> selected <%}%>>Region</option>
        </select>
    </td>
    <td align="right"><input type="submit" name="list" value="&nbsp;GET&nbsp;"></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

<br>
<table cellpadding="8" cellspacing="0" border="0" width="100%" class="tableview">

    <tr>
      <th align="left">&nbsp;</th>
      <th align="left">Name</th>
      <th align="center">Region</th>
      <th align="right">Created</th>
   </tr>

    <%

        for (int i=0; i<list.size(); i++) {
            Client client = (Client) list.get(i);
            String img = client.getImagePath("mobimage", 0, 2);

     %>
        <tr>
          <td width="150"><a href="clientdetails.jsp?unq=<%=client.getUnique()%>&sort=<%=ss%>"><img src="<%=img%>" border="0"></a></td>
          <td align="left" valign="middle"><a href="clientdetails.jsp?unq=<%=client.getUnique()%>&sort=<%=ss%>"><%=client.getName()%></a></td>
          <td align="center" valign="middle">&nbsp;<%=client.getRegion()%></a></td>
          <td align="right" valign="middle"><%=client.getCreated()%></a></td>
        </tr>

        <% 	} %>
 </table>

</td></tr>
</table>

</form>

</body>
</html>



