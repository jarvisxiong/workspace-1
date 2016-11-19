<%@include file="/WEB-INF/jspf/itemadmin/mastertone/contentfiles.jspf"%>


<html>
    <head>
            <meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
    <link rel="stylesheet" href="<%=stylesheet%>" type="text/css">

    <script src="http://cdn.jquerytools.org/1.2.4/full/jquery.tools.min.js"></script>

    <script type="text/javascript" src="/lib/swfobject/swfobject.js"></script>

    <link rel="stylesheet" href="/lib/jquery/uploadify-2.1.4/uploadify.css" media="screen" />
    <script type="text/javascript" src="/lib/jquery/uploadify-2.1.4/jquery.uploadify.v2.1.4.min.js"></script>

    <script>
    
    function updateUploader() {


        var $upload = $("#file_upload");
        $upload.uploadifySettings('scriptData', {'itemunq':'<%=unique%>', 'type':'masterres'});

        $upload.uploadifyUpload();
    }

    $(document).ready(function() {

         $('#file_upload').uploadify({
             'uploader'  : '/lib/jquery/uploadify-2.1.4/uploadify.swf',
             'script'    : '/admin_pub/act_upload.jsp',
             'cancelImg' : '/lib/jquery/uploadify-2.1.4/cancel.png',
             'scriptData': {'itemunq':'<%=unique%>', 'type':'masterres'},
             'method'    :'GET',
             'fileExt'   : '*.mp3;*.aac;*.m4a;*.amr;*.awb',
             'fileDesc'  : 'Audio Files',
             'multi'     : false,
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

<form action="<%=fileName%>.jsp" method="post" name="form1">
<input type="hidden" name="unq" value="<%=unique%>">

<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="blue_14"><b>Master Tone Details: </b> <span class="grey_12"><b><%=item.getTitle()%></b></span></td>
        <td align="right" valign="bottom" class="status">&nbsp;</td>
    </tr>
    </table>    
</td></tr>
<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", "1")); %>
    <%@ include file="/itemadmin/mastertone/tabs.jsp" %>
    <br>
</td></tr>

<tr>
<td valign="top" align="left">

        <div id="uploadStatus" style="display:none;" class="statusmsg" >
            <span>
                You files were uploaded. When the processing is finished the files will added into the catalogue. This might take a while.
                
            </span><br/><br/>
            <span id="filesUploaded"></span><br/>
        </div>

        <table cellspacing="0" cellpadding="6" border="0" width="100%">

            <tr>
                <td align="left">
                    <input type="file" name="file_upload" id="file_upload">
                    <br>
                    <input type="button" name="upload" value="Upload" onclick="javascript:updateUploader();" style="width:150px;">

                </td>
            </tr>
        </table>

</td></tr>
<tr><td valign="top" align="left">

        <table cellspacing="0" cellpadding="6" border="0" width="100%" class="dotted">
            
            <th align="left">Content Type</th>
            <th align="left">Data File</th>
            <th align="left">Mime Type</th>
            <th align="left">Size</th>
            <th align="left">Exists</th>
            <th align="left">&nbsp;</th>


            <%
            boolean exists = false;
            File datafile = null;
            for (Iterator it = item.getResourceMap().keySet().iterator(); it.hasNext();) {
                list = item.getResourceMap().get((String) it.next());

                for (int k=0; k<list.size(); k++) {
                    res = list.get(k);
                    datafile = new File(System.getProperty("installDir") + res.getDataFile());

                    System.out.println("data: " + datafile);
                    exists = datafile.exists();

            %>
            <tr>
            <td align="left"><%=res.getContentType()%></td>
            <td align="left"><%=res.getDataFile()%></td>
            <td align="left"><%=res.getMimeType()%></td>
            <td align="left"><%=datafile.length()%></td>
            <td align="left"><%=exists%></td>
            <td align="left"><input type="checkbox" name="sel_<%=res.getUnique()%>"></td>

            </tr>
            <% }
                } %>
        </table>
</td></tr>
<tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
<tr><td align="right"><input type="submit" name="del" value="Delete Selected"></td></tr>
</table>

        </form>
    </body>
</html>





