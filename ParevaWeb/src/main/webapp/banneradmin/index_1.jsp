<%@ include file="/WEB-INF/jspf/coreimport.jspf" %>

<%
//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
System.out.print("domainrr"+domain);
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "iso-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "index";
//***************************************************************************************************


BannerAdDao banneraddao=null;
ClientDao clientdao=null;

 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      banneraddao=(BannerAdDao) ac.getBean("banneraddao");
      clientdao=(ClientDao) ac.getBean("clientdao");
     
      }
      catch(Exception e){
          e.printStackTrace();
      }


String statusMsg = "";
String sqlstr = "";

java.util.List list  = new ArrayList();
BannerAd banner = null;
Client client = null;
String[] props = null;
String itemlist = "itemlist";
itemlist = "";

String add = aReq.get("add");
String dm = aReq.get("dm");
String ss = aReq.get("sort", "cr");
String title = aReq.get("title").trim();
String owner = aReq.get("owner").trim();
String cat = aReq.get("cat");
int sIndex = Integer.parseInt(aReq.get("si", "0"));
String unique = "";

int pageCount = 0;
int maxCount = 10;
int count = 0;

if (!add.equals("")) {

    if (owner.equals("")) statusMsg = "Please select Client.";
    else {
        banner = banneraddao.addBanner(owner, title);
        System.out.println("Added Banner = "+banner);
        
      //  DBHStatic.closeConnection(con);
        try { 
        	Misc.cxt.getRequestDispatcher("/banneradmin/itemdetails.jsp?add=1&unq=" + banner.getUnique()).forward(request,response); 
        	}
        catch (Exception e) { System.out.println(e); }
        return;
    }
}

System.out.println("%%%%%%%%%%"+ss+"???????????cat"+cat);

java.util.List banners = banneraddao.getAllBanners("", ss, cat);
java.util.List clients  = clientdao.getAllClients("ADC1177659328952", ss);

pageCount = count/maxCount;
if ((count%maxCount)>0) pageCount++;


%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="/lib/global_anyx.js"  language="javascript"></script>
<script src="/lib/scriptaculous/prototype.js" language="javascript"></script>
<script src="/lib/scriptaculous/scriptaculous.js" language="javascript"></script>
<script type="text/javascript" language="javascript">
        
    function submitform(src, form) {
        if (src=="sort") form.cat.value="";
        form.submit();
    }
    function submitForm2 (thisForm, ind) {
        thisForm.si.value=ind;
        thisForm.submit();
    }
    
</script>


</head>

<body> 
  
    
   
    
<form action="<%=fileName%>.jsp" method="post" name="form1">
<input type="hidden" name="si" value="<%=sIndex%>">

<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td valign="top" align="left">
    
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
        <tr>
            <td align="left" valign="bottom" class="big_blue">Banner Ads</td>
            <td align="right" class="status_red"><b><%=statusMsg%></b></td>
        </tr>
    </table>
    
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

    <table cellpadding="6" cellspacing="0" border="0" width="100%">
    <tr>
        <td class="grey_11"><b>Add New Banner</b> &nbsp;&nbsp; Title:&nbsp;&nbsp;<input type="text" size="30" name="title" value="<%=title%>"></td>
    <td class="grey_11">Client:&nbsp;&nbsp;
    <select name="owner">
        <option value="">[Select]</option>                
    <%     
       for (int i=0; i<clients.size(); i++) { 
           client = (Client) clients.get(i);
           
    %>                
        <option value="<%=client.getUnique()%>" <% if (owner.equals(client.getUnique())){%> selected <%}%>><%=client.getName()%></option>               
    <% } %>
    
    </select>
    </td>
    <td align="right"><input type="submit" name="add" value="&nbsp;ADD NEW&nbsp;"></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

    <table cellpadding="6" cellspacing="0" border="0" width="100%">
    <tr>
    <td class="grey_11">Sort List by:&nbsp;&nbsp;
        <select name="sort" onChange="javascript:submitform('sort', this.form);">
            <option value="none">[Select]</option>
            <option value="cr" <% if (ss.equals("cr")){%> selected <%}%>>Creation Date</option>
            <option value="ow" <% if (ss.equals("ow")){%> selected <%}%>>Merchant&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
            <option value="tt" <% if (ss.equals("tt")){%> selected <%}%>>Title</option>            
        </select>
    </td>

    <td align="right"><input type="submit" name="list" value="&nbsp;GET&nbsp;"></td>
    </tr>
    </table>


</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
<% if (pageCount>1) { %>

    <tr>
    <td align="left" class="grey_11">
    <% if (sIndex>0) { %>
            <a href="javascript:submitForm2(document.form1,'<%=(sIndex-maxCount)%>');"><span class="blue_11">PREVIOUS</span></a> |
    <% } else { %>
            PREVIOUS |
    <% }
            if (((sIndex+maxCount)/maxCount)<pageCount) { %>
    <a href="javascript:submitForm2(document.form1,'<%=(sIndex+maxCount)%>');"><span class="blue_11">NEXT</span></a> |
    <% } else { %>
            NEXT |
    <%
        }

    for (int k=0; k<pageCount; k++) {
        
        int newIndex = k*maxCount;
        if (newIndex==sIndex) { out.print("<span class='red_11' style='font-family: Verdana;'><b>" + (k+1) + "</b></span>"); }
        else {
    %>
                <a href="javascript:submitForm2(document.form1,'<%=newIndex%>');"><span class="blue_11" style="font-family: Verdana;"><b><%=(k+1)%></b></span></a>
    <%
        }
    }
    %>

    </td>
</tr>
<% } %>


<tr><td>                
    <br>

    <table cellpadding="4" cellspacing="0" border="0" width="100%" class="tableview">
        <tr>
          <th align="left" width="200">&nbsp;</th>
          <th align="left" width="200">Banner</th>
          <th align="left" width="100">Client</th>
          <th align="center" width="50">Status</th>          
          <th align="right">Created</th>
       </tr>
   </table>
   
   <ul id="<%=itemlist%>" style="list-style-type: none; padding: 0; margin: 0;">
       
    <%     
        for (int i=0; i<banners.size(); i++) {
            banner = (BannerAd) banners.get(i);

            String img = banner.getImagePath("mobimage", 0, 2);
      
            if (img.equals("")) {
                for (int k=0; k<clients.size(); k++) {
                    client = (Client) clients.get(k);
                    if (client.getUnique().equals(banner.getOwner())) {
                        img =client.getImagePath("mobimage", 0, 2);
                        
                        break;
                    }
                }
            }             

    %>
    <li id="item_<%=banner.getUnique()%>">
        <table cellpadding="4" cellspacing="0" border="0" width="100%" class="tableview">
            <tr>
                <td width="200"><a href="itemdetails.jsp?unq=<%=banner.getUnique()%>&sort=<%=ss%>"><img src="<%=img%>" border="0"></a></td>
                <td valign="middle" align="left" width="200"><a href="itemdetails.jsp?unq=<%=banner.getUnique()%>&sort=<%=ss%>"><%=banner.getTitle()%></a></td>
                <td valign="middle" align="left" width="100"><%=SdkTempCache.userGroupNames.get(banner.getOwner())%></td>
                <td valign="middle" align="center" width="50"><%=banner.getStatus()%></td>
                <td valign="middle" align="right" class="black_10"><%=banner.getCreated()%></td>
            </tr>
         </table>
    </li>
    <%     
        } %>
    </ul>
</td></tr>
</table>

</form>
</body>
</html>


<% // DBHStatic.closeConnection(con); %>



