<%@ include file="/WEB-INF/jspf/coreimport.jspf" %>
<%@ include file="/WEB-INF/jspf/db.jspf" %>

<script>
function form_submit(thisform) {

    thisform.submit();
}

</script>

<%
//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String region="";
String classification="";
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "itemdetails";
//***************************************************************************************************
java.util.List classificationList=new ArrayList();
java.util.List regionList=new ArrayList();
java.util.List dms = new ArrayList();
boolean commited=false;
if(request.getParameter("region")!=null)
	region=request.getParameter("region");

if(request.getParameter("classification")!=null)
	classification=request.getParameter("classification");


BannerAdDao banneraddao=null;
ItemImageDao itemimagesdao=null;

 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      banneraddao=(BannerAdDao) ac.getBean("banneraddao");
      itemimagesdao=(ItemImageDao) ac.getBean("itemimagesdao");
     
      }
      catch(Exception e){
          e.printStackTrace();
      }


try {
String unique = aReq.get("unq");

//BannerAdDao dao = CmsDaoFactory.getBannerAdDao();
BannerAd  banner = banneraddao.getBanner(unique);
System.out.println("banner: " + banner);
if (banner==null) return;

if(region.equals(""))
	region=banner.getRegion();

if(classification.equals(""))
	classification=banner.getClassification();

System.out.println("Region from Database: "+region);
System.out.println("Classification from Database: "+classification);

SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//Connection con = DBHStatic.getConnection();
//ResultSet rs = null;
Transaction trans=dbsession.beginTransaction();
String sqlstr = "";
Query query=null;
String[] props = null;
String statusMsg = "";

ItemCategory cat = null;

String save = aReq.get("save"); 
String delete = aReq.get("del");
String add = aReq.get("add");
String renew = aReq.get("renew");
String ss = aReq.get("sort");

if (!save.equals("")) {
    
    String catStr = "";
    String[] catArray = request.getParameterValues("catg");
    if (catArray!=null) {            
        for (int i=0; i<catArray.length; i++) {
            if (catStr.length()>0) catStr += "?";
            catStr += catArray[i];
        }
    }

    String domStr = "";
    String[] domArray = request.getParameterValues("dom");
    if (domArray!=null) {
        for (int i=0; i<domArray.length; i++) {
            if (domStr.length()>0) domStr += "?";
            domStr += domArray[i];
            System.out.println("domstr"+domStr);
        }
    }
        
    banner.setTitle(aReq.get("title"));
    banner.setDomain(domStr);
    banner.setShortDesc(aReq.get("sdesc"));
    banner.setLongDesc(aReq.get("ldesc"));
    banner.setWebLink(aReq.get("weblink"));
    banner.setMobileLink(aReq.get("mobilelink"));

    banner.setAddress(aReq.get("address"));
    banner.setAgeGroup(aReq.get("age"));
    banner.setGender(aReq.get("gender"));
   
    //coupon.setKeywords(aReq.get("keywords"));        
    banner.setStatus(Integer.parseInt(aReq.get("status")));
  	banner.setRegion(region);
  	banner.setClassification(classification);
    try { banner.setValidThru(sdf.parse(aReq.get("validthru"))); } catch (Exception e) { System.out.println(e); }
    try { banner.setPublish(sdf.parse(aReq.get("publish"))); } catch (Exception e) { System.out.println(e); }

    banneraddao.saveBanner(banner);
    statusMsg = "Information saved";
    
}
else if (!renew.equals("")) {

   
}  
else if (!aReq.get("confdel").equals("")) {

    sqlstr = "DELETE FROM adBanner WHERE aUnique='" +unique+ "'";
    System.out.println(sqlstr);
    query=dbsession.createSQLQuery(sqlstr);
    query.executeUpdate();
      
    
    //DBHStatic.execUpdate(con, sqlstr);

    sqlstr = "DELETE FROM itemImages WHERE aItemUnique='" + unique + "'";
    System.out.println(sqlstr);
    query=dbsession.createSQLQuery(sqlstr);
    query.executeUpdate();
    //DBHStatic.execUpdate(con, sqlstr);
	
    itemimagesdao.deleteItemImages(banner.getImageMap());
    
    trans.commit();
    dbsession.close();
    
    //DBHStatic.closeConnection(con);
    try { application.getRequestDispatcher("/banneradmin/index.jsp?del=ok").forward(request,response); }
    catch (Exception e) { System.out.println(e); }
    return;
}

//List cats = CmsDaoFactory.getItemCategoryDao().getCategories("coupon","");


String params = "sort=" + ss;


sqlstr="select distinct(aClassification) from itemCategories where aClassification<>''";
query=dbsession.createSQLQuery(sqlstr).addScalar("aClassification");
java.util.List classficationResult=query.list();
for(Object c:classficationResult)
{
	String classificationrow=c.toString();
	classificationList.add(classificationrow);
}



sqlstr="select distinct(aRegion) from mobileClubs where aRegion<>''";
query=dbsession.createSQLQuery(sqlstr).addScalar("aRegion");
java.util.List regionresult=query.list();
for(Object regiono:regionresult){
	String regionrow=regiono.toString();
	regionList.add(regionrow);
}

if(!region.equals("")){

//sqlstr = "SELECT * FROM domains WHERE aDomainUnique!='fjq32V44EqwaKUb' AND aActive='1' ORDER BY aName";
//sqlstr = "SELECT * FROM domains WHERE aActive='1' ORDER BY aName";
sqlstr="select dm.aDomainUnique, dm.aName from domains dm, mobileClubs mc where dm.aDomainUnique=mc.aWapDomain and mc.aRegion='"+region+"' AND dm.aActive='1' order by aName";

query=dbsession.createSQLQuery(sqlstr).addScalar("aDomainUnique").addScalar("aName");
java.util.List domainList=query.list();
//rs = DBHStatic.getRs(con, sqlstr);

for(Object o:domainList) {
    props = new String[2];
    Object [] row=(Object[]) o;
    props[0] = String.valueOf(row[0]);
    props[1] = Misc.utfToUnicode(String.valueOf(row[1]), pageEnc);
    //props[2] = String.valueOf(row[2]);
    //props[3] = String.valueOf(row[3]);
    //props[4] = String.valueOf(row[4]);
    dms.add(props);
    
}
}
trans.commit();
dbsession.close();

%>


<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="javascript">
function submitForm (thisForm) {
    thisForm.del.value="1";
    thisForm.submit();
}
function win(urlPath) {
    var winl = (screen.width-400)/2;
    var wint = (screen.height-700)/2;
    var settings = 'height=700,width=400,directories=no,resizable=no,status=no,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;
    newWin = window.open(urlPath,'sim',settings);    
}
</script>
</head>
<body>

<% if (!delete.equals("")) { %>

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr><td align="left" valign="bottom" class="grey_12"><br><b>
    Are your sure you want to permanently delete this banner?
    </b>
    </td></tr>
    <tr><td  align="left" valign="bottom" class="normal_blue"><br>
    <a href="<%=fileName%>.jsp?&unq=<%=unique%>&confdel=<%=unique%>&<%=params%>"><b>DELETE</b></a>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <a href="<%=fileName%>.jsp?unq=<%=unique%>&<%=params%>"><b>CANCEL</b></a>
    </td></tr>
    </table>

<% } else { %>

<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">
<input type="hidden" name="del" value="">

<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
        <tr>
            <td align="left" valign="bottom" class="blue_14"><b>Banner Details: </b> <span class="grey_12"><b><%=banner.getTitle()%></b></span></td>
            <td align="right" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;<a href="index_1.jsp?<%=params%>">Back</a></td>
        </tr>
    </table>
    
</tr></td>   

<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", "0")); %>
    
    <%@ include file="/banneradmin/tabs.jsp" %>
    <br>
</td></tr>
<tr><td><img src="/images/glass_dot.gif" height="20" width="1"></td></tr>
<tr><td>
   
<tr>
<td valign="top" align="left">      
    
	<table cellpadding="6" cellspacing="0" border="0" width="99%">
          
        <tr>
	<td class="grey_11">Banner Title:</td>
	<td><input type="text" size="40" name="title" value="<%=banner.getTitle()%>"></td>
	</tr>
        <tr>
        <td class="grey_11">Status:</td>
        <td class="grey_11"><b>
        <input type="radio" name="status" value="1" <% if (banner.getStatus()==1){%> checked <% } %>>Active
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="radio" name="status" value="0" <% if (banner.getStatus()==0){%> checked <% } %>>Inactive
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="radio" name="status" value="2" <% if (banner.getStatus()==2){%> checked <% } %>>Hidden
        </b>
        </td>
        </tr>
         
        <tr>
        <td class="grey_11">Created:</td>
        <td class="grey_11"><%=banner.getCreated()%>
        &nbsp;&nbsp;&nbsp;<input type="submit" name="renew" value="Reset">
        </td>
        </tr>
       
     
        <tr>
        <td class="grey_11">Publish:</td>
        <td class="grey_11">
            <input type="text" name="publish" value="<%=sdf.format(banner.getPublish())%>" size="12">
        </td>
        </tr>
        
        <tr>
        <td class="grey_11">Valid Thru:</td>
        <td class="grey_11">
            <input type="text" name="validthru" value="<%=sdf.format(banner.getValidThru())%>" size="12">
        </td>
        </tr>
        
        <tr>
        <td class="grey_11" valign="top">Short Description:<br>(max 255 chars)</td>
        <td class="grey_11">
            <textarea name="sdesc" cols="50" rows="3"><%=banner.getShortDesc()%></textarea>
        </td>
        </tr>
        
        <tr>
        <td class="grey_11" valign="top">Long Description:</td>
        <td class="grey_11">
            <textarea name="ldesc" cols="50" rows="5"><%=banner.getLongDesc()%></textarea>
        </td>
        </tr>
            
         <tr>
	<td class="grey_11">Web Link:</td>
	<td><input type="text" size="40" name="weblink" value="<%=banner.getWebLink()%>"></td>
	</tr>

        <tr>
	<td class="grey_11">Mobile Link:</td>
	<td><input type="text" size="40" name="mobilelink" value="<%=banner.getMobileLink()%>"></td>
	</tr>

        <tr>
        <td class="grey_11" valign="top">Address:</td>
        <td class="grey_11">
            <textarea name="address" cols="50" rows="3"><%=banner.getAddress()%></textarea>
        </td>
        </tr>
        
       
    
    <tr>
	<td class="grey_11" valign="top">Classification:</td><td>
	<select name="classification">
    	<option value="">Select Classification</option>
	<%
		for(int i=0;i<classificationList.size();i++){
	%>		
		<option value=<%=classificationList.get(i)%> <% if (classificationList.get(i).equals(classification)){%> selected <%}%> > <%=classificationList.get(i)%> </option>
             	     
     <%} %>	

	</select>
	</td>
	</tr>
 
 	<tr>
	<td class="grey_11" valign="top">Regions:</td><td>
	<select name="region" onChange="javascript:form_submit(this.form);">
		<option value="">Select Region</option>
	<%
		for(int i=0;i<regionList.size();i++){
	%>		
		<option value=<%=regionList.get(i)%> <% if (regionList.get(i).equals(region)){%> selected <%}%> > <%=regionList.get(i)%> </option>
             	     
     <%} %>            

	</select>
	</td>
	</tr>
 	
	
	
	
	
	
	
	
	

	</table>
        
</td>
</tr>
</td></tr>

<tr>
<td align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
        <td>
	<input type="button" name="delete" value="&nbsp;Delete&nbsp;" onClick="javascript:submitForm(this.form);">
	</td>
         
	<td  align="right">
	<input type="submit" name="save" value="&nbsp;&nbsp;&nbsp;Save&nbsp;&nbsp;&nbsp;">
	</td></tr>
    </table>
</td></tr>
<tr><td ><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

</table>
</form>

<% } %>

</body>
</html>

<%  //DBHStatic.closeConnection(con); %>

<% } catch (Exception e) { System.out.println(e); } %>
