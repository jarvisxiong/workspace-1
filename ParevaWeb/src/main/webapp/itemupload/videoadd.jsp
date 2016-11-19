<%@page import="ume.pareva.cms.*"%>
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="ume.pareva.sdk.*"%>
<%@page import="ume.pareva.dao.*"%>
<%@page import="ume.pareva.pojo.*"%>

<%
//***************************************************************************************************
SdcRequest aReq = new SdcRequest(request);
UmeUser sdcuser = aReq.getUser();
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();

String domain = dmn.getUnique();
String langcode = aReq.getLanguage().getLanguageCode();

String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));
LangProps lp = LangProps.getFromContext(service.getUnique(), fileName, langcode, domain, application, true);
//***************************************************************************************************
ContentProviderDao contentproviderdao=null;
ItemCategoryDao itemcategorydao=null;

 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);     
      contentproviderdao=(ContentProviderDao)ac.getBean("contentproviderdao");
       itemcategorydao=(ItemCategoryDao) ac.getBean("itemcategorydao");
       
     }
      catch(Exception e){
          e.printStackTrace();
      }


String statusMsg = "";

java.util.List<ContentProvider> list  = contentproviderdao.getAllContentProviders("AAB1177483172161","name");

UmeTempCmsCache.itemCategoryMap =itemcategorydao.getCategoryMap();

java.util.List<ItemCategory> catlist = UmeTempCmsCache.itemCategoryMap.get("video");
java.util.List<ItemCategory> sublist = null;
ItemCategory cat = null;
ItemCategory subcat = null;

%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
    <link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
    
    <script src="http://cdn.jquerytools.org/1.2.4/full/jquery.tools.min.js"></script>

    <script type="text/javascript" src="/lib/swfobject/swfobject.js"></script>
    
    <link rel="stylesheet" href="/lib/jquery/uploadify-2.1.4/uploadify.css" media="screen" />
    <script type="text/javascript" src="/lib/jquery/uploadify-2.1.4/jquery.uploadify.v2.1.4.min.js"></script>

    <script>

    var curowner = '<%=sdcuser.getUserGroup()%>';
    var curtype = '';
    var curcat = '';
    var curtitle = '';
    var curclassf = '';
    var curTimUrl = '';

    function updateUploader() {

        var elem = document.getElementById("owner");
        if (elem!=null) curowner = elem[elem.selectedIndex].value;

        var elem = document.getElementById("ttype");
        curtype = elem.value;

        var elem = document.getElementById("cat");
        curcat = elem[elem.selectedIndex].value;

        var elem = document.getElementById("title");
        curtitle = elem.value;

        var elem = document.getElementById("classf");
        curclassf = elem[elem.selectedIndex].value;

        var elem = document.getElementById("timUrl");;
        curTimUrl = elem.value;

        if (curowner=='') {
            alert("Please select Content Provider");
            return;
        }
        
        if (curclassf=='') {
            alert("Please select Classification");
            return;
        }

        var $upload = $("#file_upload");
        $upload.uploadifySettings('scriptData', {'type':'video', 'owner':curowner, 'ttype':curtype, 'cat':curcat, 'title':curtitle, 'classf':curclassf, 'timUrl':curTimUrl});
        $upload.uploadifyUpload();
    }


    $(document).ready(function() {

         $('#file_upload').uploadify({
             'uploader'  : '/lib/jquery/uploadify-2.1.4/uploadify.swf',
             'script'    : '/umeadmin/act_upload.jsp',
             'cancelImg' : '/lib/jquery/uploadify-2.1.4/cancel.png',
             'scriptData': {'type':'video', 'owner':curowner, 'ttype':curtype, 'cat':curcat, 'title':curtitle, 'classf':curclassf, 'timUrl':curTimUrl},
             'method'    :'GET',             
             'fileDesc'  : 'Video Files',
             'multi'     : true,
             'folder'       : '/uploads',
             'scriptAccess' : 'always',
              onAllComplete: function(event, data){
                  $("#filesUploaded").html("Files Uploaded: " + data.filesUploaded);
                  $("#uploadStatus").show();
              },
              onComplete: function(event, ID, fileObj, response, data) {
                  //alert(event + ": " + ID + ": " + response);
              },
              onSelect: function(a,b,c){
                 $("#uploadStatus").hide();
              },
              'buttonText': 'SELECT FILES'
        });

    });     
</script>

</head>
<body>      

    <form action="<%=fileName%>.jsp" method="post" style="padding: 0px; margin: 0px;">
    <input type="hidden" name="ttype" id="ttype" value="">
    
<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td>    
            <table cellspacing="0" cellpadding="4" border="0" width="100%">
            <tr>
                <td align="left" valign="bottom" class="big_blue">Add New Videos</td>
                <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
            </tr>
        </table>
</td></tr>        
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>     

        <div id="uploadStatus" style="display:none;" class="statusmsg" >
            <span>
                You files were uploaded. When the processing is finished the files will added into the catalogue. This might take a while.
                <br>
                <a href="/itemadmin/index.jsp?itype=video&cat=all">Go to Videos page</a>
            </span><br/><br/>
            <span id="filesUploaded"></span><br/>
        </div>
        
        <table cellspacing="0" cellpadding="6" border="0" width="100%">
            
            <tr>
                <td>Select Content Provider:</td>
                <td style="width:80%;">
                <select name="owner" id="owner">
                    <% if (sdcuser.getAdminGroup()==9) {%>
                        <option value="">[Select Provider]</option>
                        <%
                            for (int i=0; i<list.size(); i++) {
                                ContentProvider cp = (ContentProvider) list.get(i);
                         %>
                        <option value="<%=cp.getUnique()%>"><%=cp.getName()%></option>
                        <% } %>
                    <%} else { %>
                    <option value="<%=sdcuser.getUserGroup()%>"><%=SdkTempCache.getUserGroupName(sdcuser.getUserGroup())%></option>
                    <%}%>
                </select>
                </td>
            </tr>
            
            <tr>
                <td>Category:</td>
                <td colspan="2">
                <select name="cat" id="cat">
                    <option value="all">[Select]</option>

                    <% if (catlist!=null) {
                            for (int i=0; i<catlist.size(); i++) {
                                cat = catlist.get(i);
                                sublist = cat.getSubCategories();
                    %>
                    <option value="<%=cat.getUnique()%>"><%=cat.getName1()%> - <%=cat.getClassification()%> (<%=cat.getItemCount()%>)  </option>
                     <%
                    if (sublist!=null) {
                        for (int k=0; k<sublist.size(); k++) {
                            subcat = (ItemCategory) sublist.get(k);
                    %>
                    <option value="<%=subcat.getUnique()%>">&nbsp;&nbsp;: <%=subcat.getName1()%> (<%=subcat.getItemCount()%>)  </option>
                    <% }
                    }
                    %>

                    <%      }
                       }
                    %>
                </select>
            </td>
            </tr>

            <tr>
                <td>Classification:</td>
                <td colspan="2">                
                    <select name="classf" id="classf">
                        <option value="">[Select]</option>
                        <option value="safe">Safe</option>
                        <option value="bikini">Bikini</option>
                        <option value="topless">Topless</option>
                        <option value="fullnude">Full Nude</option>
                        <option value="hardcore" selected>Hardcore</option>
                    </select>
                </td>
            </tr>

            <tr>
                <td>Title:</td>
                <td colspan="2">
                <input type="text" name="title" id="title" style="width:150px;">
                    
            </td>
            </tr>
            <tr>
                <td >Select files ():</td>
                <td align="left">
                    <input type="file" name="file_upload" id="file_upload">
                    
                </td>
            </tr>
            <tr>
                <td>TIM ID:</td>
                <td colspan="2">
                <input type="text" name="timUrl" id="timUrl" style="width:350px;">                    
                </td>
            </tr>  
            <tr>
                <td class="grey_12">&nbsp;</td>
                <td>
                    <input type="button" name="upload" value="Upload" onclick="javascript:updateUploader();" style="width:150px;">
                </td>
            </tr>
          
        </table>

</td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td class="grey_12">
    You can user this service to upload videos. You can select multiple files at the same time and each one of them are added as
    unique content items.
</td></tr>    
</table>   
</form>
                    
    </body>
</html>




