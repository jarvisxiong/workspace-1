<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@ page import="ume.pareva.dao.*, ume.pareva.snp.*, ume.pareva.sdk.*,ume.pareva.pojo.*, java.util.*, java.sql.Connection, java.sql.SQLException,
                java.text.DecimalFormat" %><%
try {
//***************************************************************************************************
SdcRequest aReq = new SdcRequest(request);
UmeUser sdcuser = aReq.getUser();
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();

String domain = dmn.getUnique();
String dmid = domain;
String langcode = aReq.getLanguage().getLanguageCode();

String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));

UmeTempCache anyxsdc=null;
UmeLanguagePropertyDao langpropdao=null;
UmeUserDetailsDao userdetailsdao=null;
SnpUserDao snpuserdao=null;
try{
ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
langpropdao=(UmeLanguagePropertyDao) ac.getBean("umelanguagepropertydao");
userdetailsdao=(UmeUserDetailsDao) ac.getBean("umeuserdetailsdao");
snpuserdao=(SnpUserDao) ac.getBean("snpuserdao");
anyxsdc=(UmeTempCache) ac.getBean("umesdc");

}
catch(Exception e){
    e.printStackTrace();
}




SdcLanguageProperty lp = langpropdao.get(fileName, service, aReq.getLanguage(), dmn);
//***************************************************************************************************
String uid = sdcuser.getUnique();
String parsedmobile=sdcuser.getParsedMobile();

UmeUserDetails ud = sdcuser.getUserDetails();
System.out.println("userDetails in settings: " + ud);
if (ud==null) { 
    ud = new UmeUserDetails();
    ud.setParsedMobile(parsedmobile);    
    userdetailsdao.addNewDetails(ud);    
    sdcuser.setUserDetails(ud);
    new UmePersonalCache().setUser(sdcuser);
}

SdcLanguage sdcl = null;

DecimalFormat df1 = new DecimalFormat("##0.00");
String statusMsg="";
String statusMsg2 = "";
boolean pwChanged = false;
boolean showPwFields = !aReq.get("showPasswords").equals("");

System.out.println("LKSJDLKJSDLKJ: " + showPwFields);


String saveButton = aReq.get("save");
String changePwd = aReq.get("chPwd");

if (!changePwd.equals("")) {
    
    String pw1 = aReq.get("pwd1");
    String pw2 = aReq.get("pwd2");
    
    if (pw1.length()<=4) statusMsg2 = "Password must be at least 5 characters long.";
    else if (!SdcMisc.isHtmlSafe(pw1)) statusMsg2 = "Invalid characters. Use only standard characters.";
    else if (!pw1.equals(pw2)) statusMsg2 = "Passwords do not match.";
    else {
        sdcuser.updateMap("password", pw1);
        snpuserdao.commitUpdateMap((SnpUser)sdcuser);
        statusMsg2 = "Password has been changed successfully";
        sdcuser.clearUpdateMap();
        //UmePersonalCache.setUser(sdcuser);
    }
}
else if (!saveButton.equals("")) {
    
    String error = "";
    String value = "";
    
    Enumeration en = request.getParameterNames();
    for (;en.hasMoreElements();) {
        String elem = (String) en.nextElement();
        if (elem.startsWith("upd_")) {
            value = aReq.get(elem);
            if (!SdcMisc.isHtmlSafe(value)) {
                error = "Invalid characters. Use only standard characters";
                break;
            }
            sdcuser.updateMap(elem, value);
        }
        
    }

    if (error.equals("")) {
        String stat = snpuserdao.commitUpdateMap((SnpUser)sdcuser);

        if (!stat.equals("")) statusMsg += lp.get(stat);
        else statusMsg += lp.get("saved");
    }
    else statusMsg = error;
    
    sdcuser.clearUpdateMap();
    //UmePersonalCache.setUser(sdcuser);
    
}


int cs=4;
String bgColor="#EEEEEE";

List<SdcLanguage> langlist = anyxsdc.getLanguageList();

%>

<html>
<head>
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<script>
  
<% if (!statusMsg.equals("")){%> alert("<%=statusMsg%>"); <%}%>

</script>

</head>
<body>

<% if (showPwFields) { %>

<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr>
<td valign="top" align="left">
<br>
<% if (!pwChanged) { %>
<h2><font color="maroon"><%=statusMsg2%></font></h2>
<% } else { %>
<h2><%=statusMsg2%></h2>
<% } %>
</td>
</tr>


<tr><td><img src="/images/glass_dot.gif" width="1" height="1"></td></tr>
<tr><td><img src="/images/grey_dot.gif" width="100%" height="1"></td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="5"></td></tr>

<tr><td>
<form method="post" action="<%=fileName%>.jsp">
    <input type="hidden" name="showPasswords" value="1">
	<table cellspacing="0" cellpadding="3" border="0" width="100%">
		<tr bgcolor="<%=bgColor%>">
			<td align="left" class="grey_11"><%=lp.get(62)%></td>
			<td colspan="2" align="left" class="grey_11"><input type="password" size="10" name="pwd1" >&nbsp;&nbsp;<%=lp.get(63)%></td>
		</tr>
		<tr bgcolor="<%=bgColor%>">
			<td align="left" class="grey_11"><%=lp.get(64)%></td>
			<td align="left"><input type="password" size="10" name="pwd2" ></td>
			<td align="right"><input type="submit" name="chPwd" value="<%=lp.get(90)%>"></td>
		</tr>
	</table>
</form>
</td>
</tr>


</table>

<% } else { %>

<table cellspacing="0" cellpadding="0" border="0" width="95%" height="100%">
<tr>
<td valign="top" align="left">

<form method="post" action="<%=fileName%>.jsp">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
	<td align="left" valign="top">

		<table cellspacing="0" cellpadding="6" border="0" width="100%">
		<tr valign="top">
			<td bgcolor="#FFFFFF" class="big_blue">My Settings</td>
			<td bgcolor="#FFFFFF" class="status" align="right">&nbsp;<%=statusMsg%></td>
		</tr>
		<tr>
			<td colspan="2" class="grey_11" align="right">
			
				&nbsp;
			</td>
		</tr>
		</table>

	</td>
</tr>
<tr><td><img src="/images/grey_dot.gif" width="100%" height="1"></td></tr>
<tr>
<td>
	<table cellspacing="0" cellpadding="4" border="0" width="100%" style="background-color:<%=bgColor%>;">
	<tr>
	<td align="left" class="grey_11"><%=lp.get(67)%></td>
	<td align="left" class="small_blue"><b><%=sdcuser.getParsedMobile()%></b></td>
	<td align="left" width="100%">&nbsp;</td>
	
	</tr>
	</table>
</td>
</tr>
<tr><td><img src="/images/grey_dot.gif" width="100%" height="1"></td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="15"></td></tr>

<tr><td><img src="/images/grey_dot.gif" width="100%" height="1"></td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="5"></td></tr>
<tr><td class="grey_11"><%=lp.get(91)%></td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="5"></td></tr>
<tr><td>

	<table cellspacing="0" cellpadding="6" border="0"  width="100%" style="background-color:<%=bgColor%>;">
	<tr>    
                <td align="left" class="grey_11">Login:</td>
		<td align="left"><input type="text" size="20" name="upd_login" value="<%=sdcuser.getLogin()%>" ></td>
                
		<td align='left' class="small_blue"><%=lp.get(95)%></td>

                <td align='left' class="small_blue"><%=sdcuser.getUnique()%></td>
		
	</tr>
	<tr>
		<td align="left" class="grey_11"><%=lp.get(70)%></td>
		<td align="left"><input type="text" size="20" name="upd_firstname" value="<%=sdcuser.getFirstName()%>" ></td>
		<td align='left' class="grey_11"><%=lp.get(71)%></td>
		<td align='left'><input type="text" size="20" name="upd_lastname" value="<%=sdcuser.getLastName()%>" ></td>
	</tr>
	
	<tr>
		<td align="left" class="grey_11"><%=lp.get(74)%></td>
		<td align="left">
			<select name="upd_language">
                        <option value="">[Select]</option>                
                    <% 

                    for (int i=0; i<langlist.size(); i++) {
                        sdcl = langlist.get(i);

                    %>               
                        <option value="<%=sdcl.getLanguageCode()%>" <% if (sdcuser.getLanguage().equals(sdcl.getLanguageCode())){%> selected <%}%>><%=sdcl.getLanguageName()%></option>               
                    <% } %>
                    </select>
		</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td align="left" class="grey_11"><%=lp.get(75)%></td>
		<td colspan="3" align="left"><input type="text" size="40" name="upd_email" value="<%=sdcuser.getEmail()%>" ></td>
	</tr>
	
	
	</table>

</td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="15"></td></tr>
<tr><td><img src="/images/grey_dot.gif" width="100%" height="1"></td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="5"></td></tr>
<tr><td class="grey_11"><%=lp.get(92)%></td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="5"></td></tr>
<tr><td>
	<table cellspacing="0" cellpadding="3" border="0" width="100%" style="background-color:<%=bgColor%>;">
	<tr>
		<td align='left' class="grey_11"><%=lp.get(79)%></td>
		<td align='left'><input type="text" size="20" name="upd_company" value="<%=ud.getCompany()%>" ></td>
		<td align="left" class="grey_11"><%=lp.get(80)%></td>
		<td align="left"><input type="text" size="20" name="upd_jobtitle" value="<%=ud.getJobTitle()%>" ></td>
	</tr>
	<tr>
		<td align='left' class="grey_11"><%=lp.get(81)%></td>
		<td align='left'><input type="text" size="20" name="upd_businesstel" value="<%=ud.getBusinessTel()%>" ></td>
		<td align="left" class="grey_11"><%=lp.get(82)%></td>
		<td align="left"><input type="text" size="20" name="upd_businessfax" value="<%=ud.getBusinessFax()%>" ></td>
	</tr>
	<tr>
		<td align='left' class="grey_11"><%=lp.get(83)%></td>
		<td align='left'><input type="text" size="20" name="upd_hometel" value="<%=ud.getHomeTel()%>" ></td>
		<td align="left" class="grey_11"><%=lp.get(84)%></td>
		<td align="left"><input type="text" size="20" name="upd_street" value="<%=ud.getStreet()%>" ></td>
	</tr>
	<tr>
		<td align='left' class="grey_11"><%=lp.get(85)%></td>
		<td align='left'><input type="text" size="20" name="upd_zip" value="<%=ud.getZip()%>" ></td>
		<td align="left" class="grey_11"><%=lp.get(86)%></td>
		<td align="left"><input type="text" size="20" name="upd_city" value="<%=ud.getCity()%>" ></td>
	</tr>
	<tr>
		<td align='left' class="grey_11"><%=lp.get(87)%></td>
		<td align='left'><input type="text" size="20" name="upd_stateregion" value="<%=ud.getStateRegion()%>" ></td>
		<td align="left" class="grey_11"><%=lp.get(88)%></td>
		<td align="left"><input type="text" size="20" name="upd_country" value="<%=ud.getCountry()%>" ></td>
	</tr>
	<tr>
		<td align='left' class="grey_11"><%=lp.get(89)%></td>
		<td colspan="3" align='left'><input type="text" size="20" name="upd_homepage" value="<%=ud.getWebPage()%>" ></td>
	</tr>
	</table>

</td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="5"></td></tr>
<tr><td align="right"><input type="submit" name="save" value="&nbsp;&nbsp;<%=lp.get(93)%>&nbsp;&nbsp;"></td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="8"></td></tr>
<tr><td><img src="/images/grey_dot.gif" width="100%" height="1"></td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="5"></td></tr>
<tr><td class="grey_11" align="center"><input type="submit" name="showPasswords" value="Change Password"></td></tr>
<tr><td><img src="/images/glass_dot.gif" width="1" height="5"></td></tr>
<tr><td><img src="/images/grey_dot.gif" width="100%" height="1"></td></tr>
</table>
<br><br>
</td>
</tr>
</table>


<% } %>

</body>
</html>

<% } catch (Exception e) { System.out.println(e); } %>
