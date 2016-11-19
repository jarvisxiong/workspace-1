<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/jspf/wapsiteadmin/index.jspf" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="/lib/global_anyx.js" language="javascript"></script>
<script src="/lib/scriptaculous/prototype.js" language="javascript"></script>
<script src="/lib/scriptaculous/scriptaculous.js" language="javascript"></script>

<script language="JavaScript">
    var newwindow = '';
	
    function openwin(url) {

        var winl = (screen.width-400)/2;
        var wint = (screen.height-800)/2;
        var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;
        newwindow=window.open(url,'sim',settings);
        newwindow.focus();
    }

    function win(url) {

        var winl = (screen.width-400)/2;
        var wint = (screen.height-800)/2;
        var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;
        if (!newwindow.closed && newwindow.location) {
            newwindow.location.href = url;
        }
    }

    function win2(urlPath) {
        var winl = (screen.width-200)/2;
        var wint = (screen.height-100)/2;
        var settings = 'height=100,width=200,directories=no,resizable=no,status=no,scrollbars=no,menubar=no,location=no,top=' + wint + ',left=' + winl;
        delWin = window.open(urlPath,'mmsdel',settings);
        delWin.focus();
    }

    function form_submit(thisform) {
        thisform.ss.value="";
        thisform.submit();
    }
    
    function updateLandingPage(action,k) {
    	
    	document.getElementById("ss").value="";
    	document.getElementById("action").value=action;
    	document.getElementById("landing_page").value=document.getElementById("landingPage"+k).value;
    	document.getElementById("startTime").value=document.getElementById("startTime"+k).value;
    	document.getElementById("endTime").value=document.getElementById("endTime"+k).value;
    	document.getElementById("networkName").value=document.getElementById("networkName"+k).value;
    	if(document.getElementById("landingStatus"+k).checked)
    		document.getElementById("landingStatus").value="true";
    	else
    		document.getElementById("landingStatus").value="false";
    	
    	document.forms["form1"].submit();
        /* thisform.ss.value="";
        thisform.up.value="1";
       
        thisform.submit(); */
    }
    

</script>

<script language=JavaScript src="/lib/picker.js"></script>

<script language="JavaScript">
function change_color (form, form_element, subpage) {
    TCP.popupwithsubmit(form_element,0,form, "http://<%=System.getProperty(dm + "_url")%>/simulator.jsp?pg=" + subpage, window);
}

function preview(url,landingPage,msisdnExist){
	/* window.open('', 'TheWindow');
	document.getElementById('TheForm').submit();
	 */
	 var previewForm = document.createElement("form");
	 previewForm.target = "preview";
	 //previewForm.target = "_blank";
	 previewForm.method = "POST"; // or "post" if appropriate
	 previewForm.action = url;

	    var landingPageInput = document.createElement("input");
	    landingPageInput.type = "text";
	    landingPageInput.name = "previewLandingPage";
	    landingPageInput.value = landingPage;
	    
	    var msisdnExistInput = document.createElement("input");
	    msisdnExistInput.type = "text";
	    msisdnExistInput.name = "msisdnexist";
	    msisdnExistInput.value = msisdnExist;
	    
	    var previewNetworkInput = document.createElement("input");
	    previewNetworkInput.type = "text";
	    previewNetworkInput.name = "previewNetworkInput";
	    previewNetworkInput.value = document.getElementById("previewNetwork").value;
	    //alert(document.getElementById("previewNetwork").value);
	    previewForm.appendChild(landingPageInput);
	    previewForm.appendChild(msisdnExistInput);
	    previewForm.appendChild(previewNetworkInput);

	    document.body.appendChild(previewForm);
	    // preview = window.open("", "preview", "status=0,title=0,height=600,width=800,scrollbars=1","false");
	    
	   window.open("", "preview", "status=0,title=0,height=600,width=800,scrollbars=1");
	    previewForm.submit();
	/* if (preview) {
		previewForm.submit();
	} else {
	    alert('You must allow popups for this preview to work.');
	} */ 
	
}

</script>

</head>
<body>

<form action="<%=fileName%>.jsp" method="post" name="form1">
<input type="hidden" name="ss" value="1" id="ss">
<input type="hidden" name="action" id="action" value="">
<input type="hidden" name="landing_page" id="landing_page" >
<input type="hidden" name="startTime" id="startTime" >
<input type="hidden" name="endTime" id="endTime" >
<input type="hidden" name="networkName" id="networkName" >
<input type="hidden" name="landingStatus" id="landingStatus" >
<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="big_blue"><b>General Settings</b></td>
            <td align="right" valign="bottom" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;</td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td valign="top" align="left">
    <br>
    <table cellspacing="0" cellpadding="6" border="0">
        <tr>
            <td align="left" class="grey_12"><nobr><b>Region:</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></td>
                <td align="left">
                  
            <select name="regionid" id="region" onChange="javascript:form_submit(this.form);">
            <option value=" " selected>Select Region</option>
            
                <%
                     for (int i=0; i<regionlist.size(); i++) {
                        regionprops =  regionlist.get(i);
                        System.out.println("REgion list count "+regionlist.size()+ " Region Props: "+regionprops);
                    %>
                    <option value=<%=regionprops%> <% if (regionprops.equals(regionid)){%> selected <%}%>> <%=regionprops%> </option>
                   
                    <%}%>
                   

            </select> 
                   
            </td>
        </tr>
        <tr>
            <%if(regionid!=null && !regionid.trim().equalsIgnoreCase("")){%>
            <td align="left" class="grey_12"><nobr><b>Domain:</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></td>
            <td align="left">
                <select name="dm" onChange="javascript:form_submit(this.form);">
                    <option value="null">[Select Domain]</option>
                    <%
                   
                      for (int i=0; i<dms.size(); i++) {
                        props = (String[]) dms.get(i);
                    %>
                    <option value="<%=props[0]%>" <% if (props[0].equals(dm)){
                        foundDomain = true;
                        //client=props[3];
                        domainlink = "http://" + props[2] + "/";
                    %> selected
                    <%}%>><%=props[1]%> -- http://<%=props[2]%></option>

                    <% } %>
                </select>
            </td>
            <%}%>
        </tr>
 <%-----------------------------------------------------------------------------------------------------------------------%>	
 
        <%if(regionid!=null && !regionid.trim().equalsIgnoreCase("")){%>
    <% if (!dm.equals("") && !dm.equals("null")) {%>
	<tr>
	<td align="left" class="grey_12"><b>Template Theme:</b></td>
	<td align="left">
                  
            <select name="themeName" id="themeName" onChange="javascript:form_submit(this.form);">
            <option value=" " selected>Select Template Theme</option>
			<% 
			for(int i=0;i<templateList.size();i++){
				Map<String,String> templateMap=(Map<String,String>)templateList.get(i);
				String templateName=templateMap.get("templateName");
				String templateStatus=templateMap.get("status");
				%>
				<option value="<%=templateName%>" <% if (templateStatus.equals("1")){%> selected <%}%>><%=templateName%></option>
			
			<%} %>
			</select>
			
</td>	

<%--  
<% if (!templateid.equals("")){%>	
<td align="left">	
	<a href="edittemplate.jsp?srvc=<%=props[0]%>&dm=<%=dm%>&clnt=<%=clientunique%>"><span class="blue_11">[Edit]</span></a>
</td>
<%}%>
--%>	
	</tr>
	
	
	

	
	<tr>
	<td align="left" class="grey_12"><b>Template Page:</b></td>
	<td align="left">
                  
            <select name="template_page" id="template_page" onChange="javascript:form_submit(this.form);">
            <option value="" selected>Select Template Page</option>
            <%for(int k=0;k<templateFiles.size();k++){%>
			<option value="<%=templateFiles.get(k)%>" <% if (template_page.equals(templateFiles.get(k))){%> selected <%}%>><%=templateFiles.get(k)%></option>
			
			<%-- 
			<option value="Terms" <% if (template_page.equals("Terms")){%> selected <%}%>>Terms</option>
			<option value="Landing1" <% if (template_page.equals("Landing1")){%> selected <%}%>>Landing1</option>
			<option value="Landing2" <% if (template_page.equals("Landing2")){%> selected <%}%>>Landing2</option>
			<option value="Landing3" <% if (template_page.equals("Landing3")){%> selected <%}%>>Landing3</option>
			--%>
			<%}%> 
			
			</select>
			
</td>
<% 	
if (!template_page.equals("")){


%>

<td><select  id="previewNetwork">
            					<option value=" " selected>Select Network</option>
            					<option value="all">all</option>
            					<option value="default">default</option>
								<%for (int i=0;i<networks.size();i++) {
									String[] networkParameter=new String[2];
									networkParameter=(String[])networks.get(i);
								%>
								<option value="<%=networkParameter[0]%>"><%=networkParameter[0] %></option>
								<%} %>
						</select></td>



<td>

	
	
<a href="javascript:preview('<%=domainlink%>','<%=template_page%>','false')">Preview</a>|
<a href="javascript:preview('<%=domainlink%>','<%=template_page%>','true')">Preview Identified</a>|

<%-- <a target="_blank" href="<%=domainlink%>?previewLandingPage=<%=template_page%>">Preview</a>
 --%>	 		
			<a href = "editTemplatePage.jsp?templateName=<%=template_page%>&templateLocation=<%=location%>&regionid=<%=regionid%>&dm=<%=dm%>&domainlink=<%=domainlink%>">Edit</a>
			
			
			</td>
			<%} %>
<%--  
<% if (!templateid.equals("")){%>	
<td align="left">	
	<a href="edittemplate.jsp?srvc=<%=props[0]%>&dm=<%=dm%>&clnt=<%=clientunique%>"><span class="blue_11">[Edit]</span></a>
</td>
<%}%>
--%>	
	</tr>
	
	<%----------------------------------------------------------------------------------------------------------------------------------------------------%>
	
	<tr>
	<td align="left" class="grey_12"><b>CSS Files:</b></td>
	<td align="left">
                  
            <select name="cssFile" id="cssFile" onChange="javascript:form_submit(this.form);">
            <option value="" selected>Select CSS File</option>
            <%for(int k=0;k<cssFileList.size();k++){%>
			<option value="<%=cssFileList.get(k)%>" <% if (cssFile.equals(cssFileList.get(k))){%> selected <%}%>><%=cssFileList.get(k)%></option>
			
			<%-- 
			<option value="Terms" <% if (template_page.equals("Terms")){%> selected <%}%>>Terms</option>
			<option value="Landing1" <% if (template_page.equals("Landing1")){%> selected <%}%>>Landing1</option>
			<option value="Landing2" <% if (template_page.equals("Landing2")){%> selected <%}%>>Landing2</option>
			<option value="Landing3" <% if (template_page.equals("Landing3")){%> selected <%}%>>Landing3</option>
			--%>
			<%}%> 
			
			</select>
			
</td>
<td>
<% 	
if (!cssFile.equals("")){

%>
			<a href = "editCSS.jsp?cssFile=<%=cssFile%>&cssFileLocation=<%=cssLocation%>&regionid=<%=regionid%>&dm=<%=dm%>">Edit</a>
			
			<%} %>
			</td>
<%--  
<% if (!templateid.equals("")){%>	
<td align="left">	
	<a href="edittemplate.jsp?srvc=<%=props[0]%>&dm=<%=dm%>&clnt=<%=clientunique%>"><span class="blue_11">[Edit]</span></a>
</td>
<%}%>
--%>	
	</tr>
	
	
<%----------------------------------------------------------------------------------------------------------------------------------------------------%>
		
	
	
	
	
	

<tr>
<td align="left" class="grey_12"><b>Landing Page Setting:</b></td>

<td align="left">
                  
           
             <table cellspacing="0" cellpadding="10" border="0" class="tableview" width="100%">
				<tr>
					<th>LandingPage</th><th>StartTime</th><th>EndTime</th><th>Network</th><th>Status</<th><th>Action</th>
					</tr>
					<%for(int k=0;k<landingPages.size();k++){
            			HashMap landingMap=(HashMap)landingPages.get(k);
                                
            		%>	
            		
            			<tr>
            			
            			<td><%=landingMap.get("landingPage")%>
                                <input type="hidden" value="<%=landingMap.get("landingPage")%>" id="landingPage<%=k%>"></td>
            			<td><input type="text" value="<%=landingMap.get("startTime")%>" id="startTime<%=k%>"></td>
            			<td><input type="text" value="<%=landingMap.get("endTime") %>" id="endTime<%=k%>"></td>
            			<td><select  id="networkName<%=k%>">
            					<option value=" " selected>Select Network</option>
            					<option value="all" <% if (landingMap.get("network").equals("all")){%> selected <%}%>>all</option>
            					<option value="default" <% if (landingMap.get("network").equals("default")){%> selected <%}%>>default</option>
								<%for (int i=0;i<networks.size();i++) {
									String[] networkParameter=new String[2];
									networkParameter=(String[])networks.get(i);
								%>
								<option value="<%=networkParameter[0]%>" <% if (landingMap.get("network").equals(networkParameter[0])){%> selected <%}%>><%=networkParameter[0] %></option>
								<%} %>
						</select></td>
						<td>
                                    <input type="checkbox" id="landingStatus<%=k%>" value="<%=landingMap.get("status")%>" 
                                           <%if (landingMap.get("status").equals("1")){%>checked<%} %>></td>
            			<td><a href="javascript:updateLandingPage('edit','<%=k%>')">Save</a>|<a href="javascript:updateLandingPage('delete','<%=k%>')">Delete</a></td>
            			</tr>
            			<!-- System.out.println(landingMap.get("landingPage"));
            	System.out.println(landingMap.get("startTime"));
            	System.out.println(landingMap.get("endTime"));
            	System.out.println(landingMap.get("network")); -->
			
			<%}%> 
					
				       
    		</table>
            
            
			
			<%-- 
			<option value="Terms" <% if (template_page.equals("Terms")){%> selected <%}%>>Terms</option>
			<option value="Landing1" <% if (template_page.equals("Landing1")){%> selected <%}%>>Landing1</option>
			<option value="Landing2" <% if (template_page.equals("Landing2")){%> selected <%}%>>Landing2</option>
			<option value="Landing3" <% if (template_page.equals("Landing3")){%> selected <%}%>>Landing3</option>
			--%>
			
			
</td>
</tr>


<%-- <%if(!landingPage.equals("")){ %>
<tr>
<td></td>
<td align="left" class="grey_12">
<select name="network_name" id="network_name" onChange="javascript:form_submit(this.form);">
            <option value=" " selected>Select Network</option>
            <option value="all" <% if (networkName.equals("all")){%> selected <%}%>>all</option>
            <option value="default" <% if (networkName.equals("default")){%> selected <%}%>>default</option>
			<%for (int i=0;i<networks.size();i++) {
				String[] networkParameter=new String[2];
				networkParameter=(String[])networks.get(i);
			%>
			<option value="<%=networkParameter[0]%>" <% if (networkName.equals(networkParameter[0])){%> selected <%}%>><%=networkParameter[0] %></option>
			<%} %>
			</select>

</td>

</tr>
<tr>
<td></td>
<td align="left" class="grey_12"><b>Start Time:</b>&nbsp;<input type="text" name="start_time" value="<%=startTime%>" placeholder="hh:mm:ss"/></td>
</tr>
<tr>
<td></td>
<td align="left" class="grey_12"><b>End Time:</b>&nbsp;<input type="text" name="end_time" value="<%=endTime%>" placeholder="hh:mm:ss"/></td>
</tr>


<tr>
<td></td>
<td><input type="submit" value="Save" ></td>

</tr>

<%} %> --%>



	<tr>
	<td align="left" class="grey_12"><b>Add Landing Page:</b></td>
	<td align="left">
	<select name="addLanding" id="addLanding">
          <option value=" " selected>Select Landing Page</option>
          <%for (int i=0;i<domainsLandingPagesToBeAdded.size();i++){ %>       
             
            
			<option value="<%=domainsLandingPagesToBeAdded.get(i)%>"><%=domainsLandingPagesToBeAdded.get(i)%></option>
			
			<%} %>
			</select>
			
	</td>	
	<td><input type="button" value="Add Landing Page" onclick="javascript:form_submit(this.form);"></td>


	</tr>

	
	<tr>
	<td align="left" class="grey_12"><b>Create Page:</b></td>
	
	<td align="left" class="grey_12"><b>Name:</b>&nbsp;<input type="text" name="page_name"/></td>
	
	
	
	<td align="left">
                  
            <select name="page_type" id="page_type">
            <option value=" " selected>Page Type</option>
			<option value="text">Text</option>
			<option value="landing">Landing</option>
			<option value="index">Index</option>
			</select>
			
	</td>	


	</tr>
	<tr>
		<td></td>
		<td><input type="submit" value = "Create"></td>
	</tr>







<%-----------------------------------------------------------------------------------------------------------------------%>
	
	
	
	
            
            <tr>
                <td align="left" class="grey_12"><nobr><b>Sub Page:</b></nobr></td>
                <td align="left">
                    <select name="subpage" onChange="javascript:form_submit(this.form);">
                        <option value="">main_page</option>
                        <%
                           sublink = "";
                          for (int i=0; i<sublist.size(); i++) {
                            props = (String[]) sublist.get(i);
                        %>
                        <option value="<%=props[0]%>" <% if (props[0].equals(subpage)){ 
                               sublink = "?pg=" + props[0];
                            %>
                            selected<%}%>><%=props[2]%></option>

                        <% } %>
                    </select>
                    &nbsp;&nbsp;
                    <input type="submit" name="delsub" value="Remove Sub Page" onclick="return confirm('Are you sure you want to delete this sub page and all its modules?');">
                </td>
                <td align="left">
                    <input type="textbox" name="newsub" value="<%=newsub%>" style="width:100px;">&nbsp;&nbsp;
                    <input type="submit" name="addsub" value="Add New Sub Page">
                </td>
            </tr>

            <tr>
                <td align="left" class="grey_12"><nobr><b>Sub Page Link:</b></nobr></td>
                <td align="left"><a href="<%=(domainlink + sublink)%>" target="_blank"><%=(domainlink + sublink)%></a></td>
                <td align="left">
                    <%-- <a href="javascript:openwin('<%=domainlink%>simulator.jsp<%=sublink%>');">Open/refresh simulator window</a> --%>
                    <a href="javascript:openwin('<%=domainlink%>simulator.jsp?p=index');">Open/refresh simulator window</a> 
                </td>
            </tr>
            <tr>
                <td align="left" class="grey_12"><nobr><b>Domain Title:</b></nobr></td>
                <td colspan="2" align="left">
                 <input type="text" name="title" value="<%=params.get("title")%>" size="60">
                </td>
            </tr>
            <%--   
            <tr>
                <td class="grey_12"><b>Classification:</b></td>
                <td class="grey_11">
                    <select name="classification">
                        <option value="">[Select]</option>
                        <option value="safe" <% if (params.get("classification")!=null && params.get("classification").equals("safe")){%> selected <%}%>>Safe</option>
                        <option value="bikini" <% if (params.get("classification")!=null && params.get("classification").equals("bikini")){%> selected <%}%>>Bikini</option>
                        <option value="topless" <% if (params.get("classification")!=null && params.get("classification").equals("topless")){%> selected <%}%>>Topless</option>
                        <option value="fullnude" <% if (params.get("classification")!=null && params.get("classification").equals("fullnude")){%> selected <%}%>>Full Nude</option>
                        <option value="hardcore" <% if (params.get("classification")!=null && params.get("classification").equals("hardcore")){%> selected <%}%>>Hardcore</option>
                    </select>
                </td>
            </tr>
--%>
            <tr>
                <td align="left" class="grey_12"><nobr><b>Show disclaimer for Adult content:</b></nobr></td>
                <td colspan="2" align="left">
                 <input type="checkbox" name="adultcontent" value="1" <% if (params.get("adultcontent")!=null && params.get("adultcontent").equals("1")){%> checked <%}%>>
                </td>
            </tr>
            <%--   
            <tr>
                <td align="left" class="grey_12"><nobr><b>Additional CSS File:</b></nobr></td>
                <td colspan="2" align="left">
                 <input type="text" name="css" value="<%=params.get("css")%>" size="60">
                </td>
            </tr>
            
            
            <tr>
                <td align="left" class="grey_12"><nobr><b>Show Demo Games:</b></nobr></td>
                <td colspan="2" align="left">
                 <input type="checkbox" name="demo" value="1" <% if (demo.equals("1")){%> checked <%}%>>
                </td>
            </tr>
           --%>
            <tr>
                <td align="left" class="grey_12"><nobr><b>Header / Footer:</b></nobr></td>
                <td colspan="2" align="left"> <a href="header.jsp">Modify Images</a></td>
            </tr>
            
<%-------------------------------------------------------------------------------------------------------------------------------------------%>

			<tr>
	<td align="left" class="grey_12"><b>Add Classification:</b></td>
	
	<td align="left">
                  
            <select name="classification" id="classification" onChange="javascript:form_submit(this.form);">
            <option value=" " selected>Select Classification</option>
            <%for(int i=0;i<classificationList.size();i++) {%>
			<option value="<%=classificationList.get(i)%>" <% if (classification.equals(classificationList.get(i))){%> selected <%}%>><%=classificationList.get(i) %></option>
			<%} %>
			<%-- 
			<option value="Bikini" <% if (classification.equals("Bikini")){%> selected <%}%>>Bikini</option>
			<option value="Nude" <% if (classification.equals("Nude")){%> selected <%}%>>Nude</option>
			<option value="Topless" <% if (classification.equals("Topless")){%> selected <%}%>>Topless</option>
			--%>
			</select>
			
</td>	
<%--  
<% if (!templateid.equals("")){%>	
<td align="left">	
	<a href="edittemplate.jsp?srvc=<%=props[0]%>&dm=<%=dm%>&clnt=<%=clientunique%>"><span class="blue_11">[Edit]</span></a>
</td>
<%}%>
--%>	
	</tr>
	
	
	
			<tr>
	<td align="left" class="grey_12"><b>Add Master Content Type:</b></td>
	
	<td align="left">
                  
            <select name="contenttype" id="contenttype" onChange="javascript:form_submit(this.form);">
            <option value=" " selected>Select ContentType</option>
            <%for(int i=0;i<contentTypeList.size();i++) {%>
			<option value="<%=contentTypeList.get(i)%>" <% if (contentType.equals(contentTypeList.get(i))){%> selected <%}%>><%=contentTypeList.get(i) %></option>
			<%} %>
			<%-- 
			<option value="BgImage" <% if (contenttype.equals("BgImage")){%> selected <%}%>>BgImage</option>
			<option value="Java" <% if (contenttype.equals("Java")){%> selected <%}%>>Java</option>
			<option value="MasterTone" <% if (contenttype.equals("MasterTone")){%> selected <%}%>>MasterTone</option>
			<option value="TrueTone" <% if (contenttype.equals("TrueTone")){%> selected <%}%>>TrueTone</option>
			<option value="Video" <% if (contenttype.equals("Video")){%> selected <%}%>>Video</option>
			--%>
			</select>
			
</td>	
<%--  
<% if (!templateid.equals("")){%>	
<td align="left">	
	<a href="edittemplate.jsp?srvc=<%=props[0]%>&dm=<%=dm%>&clnt=<%=clientunique%>"><span class="blue_11">[Edit]</span></a>
</td>
<%}%>
--%>	
	</tr>
	
<%-------------------------------------------------------------------------------------------------------------------------------------------%>	            
            
            
           <%--   
            <tr>
                <td align="left" class="grey_12"><nobr><b>Price Groups:</b></nobr></td>
                 <td colspan="2" align="left"> <a href="pricegroups.jsp">Modify Price Groups</a></td>
            </tr>
            --%>
            <tr><td colspan="3"><img src="/images/glass_dot.gif" width="1" height="3"></td></tr>
            <tr>
                <%-- 
                <td align="left" class="grey_12"><nobr><b>Add Service Module:</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></td>
                --%>
                <td align="left" class="grey_12"><nobr><b>Add Element:</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></td>
                    <td align="left">
                        <select name=addsrvc>
                            <%-- 
                            <option value="">[Select Service]</option>
                            --%>
                            <option value="">[Select Element]</option>
                            <%---------------------------------------------------------------------------------------------------%>
                            <%-- 
                            <optgroup label="Video">
                            	<option value="Hot Video" <% if (addSrvc.equals("Hot Video")){%> selected <%}%>>Hot Video</option>
                            	<option value="VideoCategory_" <% if (addSrvc.equals("Category")){%> selected <%}%>>Category</option>
                            </optgroup>
                            
                            <optgroup label="WallPaper">
                            	<option value="Hot Wallpaper" <% if (addSrvc.equals("Hot Wallpaper")){%> selected <%}%>>Hot Wallpaper</option>
                            	<option value="WallpaperCategory_" <% if (addSrvc.equals("Category")){%> selected <%}%>>Category</option>
                            </optgroup>
                            
                            <optgroup label="Tones">
                            	<option value="Truetone" <% if (addSrvc.equals("Truetone")){%> selected <%}%>>Truetone</option>
                            	<option value="TonesCategory_" <% if (addSrvc.equals("Category")){%> selected <%}%>>Category</option>
                            </optgroup>
                            
                            --%>
                            
														
							<%---------------------------------------------------------------------------------------------------%>
                           
                            <% for (int i=0; i<modules.size(); i=i+2) {
                                String stitle = (String) modules.get(i);
                                slist = (java.util.List) modules.get(i+1);

                            %>
                            <optgroup label="<%=stitle%>">
                            <% for (int k=0; k<slist.size(); k++) {
                                props = (String[]) slist.get(k);
                                System.out.println("props[0]_props[1] = "+props[0]+"_"+props[1]);
                            %>
                            <option value="<%=props[0]%>_<%=Misc.hex8Code(props[1])%>"><%=props[1]%></option>
                            <% } %>
                            </optgroup>
                            <%}%>
                            
                            
                        </select>
                    </td>
                    
                 <%----------------------------------------------------------------------------------------------------------- --%> 
                 
                 <td align="left">
                        <select name=category>
                         
                            <option value="">[Select Category]</option>
                          
                           
                            <% for (int i=0; i<list.size();i++) {
                                String[] serviceList=(String[])list.get(i);
                            	//String stitle = (String) modules.get(i);
                                //slist = (java.util.List) modules.get(i+1);
                             %>    
                                <option value="<%=serviceList[0]%>"><%=serviceList[2]%></option>
	
                           
                            
                            <%}%>
                            
                            
                        </select>
                    </td>  
                <%-----------------------------------------------------------------------------------------------------%>    
                    <td align="left">
                    <select name="number_of_elements" id="number_of_elements">
            			<option value="0" selected>Number Of Elements</option>
						<% 
						for(int number_of_elements=1;number_of_elements<=10;number_of_elements++){
							%>
							<option value="<%=number_of_elements%>"><%= number_of_elements%></option>
							<%} %>
					</select>
                </td>
                
                <%-----------------------------------------------------------------------------------------------------%>    
                
                    <td align="left">
                    <input type="submit" name="add" value="&nbsp;Add&nbsp;">
                </td>
            </tr>
        <%}}%>
    
    </table>
</td></tr>

 <%if(regionid!=null && !regionid.trim().equalsIgnoreCase("")){%>
<% if (list.size()>0) {  %>
<tr><td ><img src="/images/glass_dot.gif" width="1" height="6"></td></tr>
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="10" border="0" class="tableview" width="100%">
        <tr>
            <%-- 
            <th width="150" align="left" class="grey_12"><nobr><b>Service Type</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></th>
            <th width="160" align="left" class="grey_12"><b>Title</b></th>
            <th width="70" align="center" class="grey_12"><b>Active</b></th>
            <th width="80" align="left" class="grey_12"><b>Bg Color</b></th>
            <th width="80" align="left" class="grey_12"><b>Font Color</b></th>
            <th width="80">&nbsp;</th>
            
            --%>
            
            <th width="20%" align="left" class="grey_12"><nobr><b>Element Type</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></th>
            <th width="20%" align="left" class="grey_12"><b>Name</b></th>
            <th width="20%" align="center" class="grey_12"><b>Active</b></th>
            <th width="20%">&nbsp;</th>

            <th width="20%">&nbsp;</th>
        </tr>
    </table>

    <ul id="itemlist" style="list-style-type: none; padding: 0; margin: 0;">
<%
    String editLink = "";
    String bgcolor = "";

    for (int i=0; i<list.size(); i++) {
        props = (String[]) list.get(i);
        ind = Integer.parseInt(props[3]);

        //System.out.println("props[8]: " + props[8]);

        if (props[8].equals("hotgame")) editLink = "modpromo.jsp?cmd=hotgame";
        else if (props[8].equals("weekgame")) editLink = "modpromo.jsp?cmd=weekgame";
        else if (props[8].equals("mainpromo")) editLink = "modpromo.jsp?cmd=mainpromo";
        else if (props[8].equals("contest")) editLink = "modcontest.jsp?cmd=";
        else if (props[8].equals("freetext")) editLink = "modtext.jsp?cmd=";
        else if (props[8].equals("hotmaster")) editLink = "modpromo2.jsp?cmd=hotmaster";
        else if (props[8].equals("hotbg")) editLink = "modpromo3.jsp?cmd=hotbg";
        else if (props[8].equals("hotvideo")) editLink = "modpromo4.jsp?cmd=hotvideo";
        else if (props[8].equals("exturl")) editLink = "modexturl.jsp?cmd=";
        else if (props[8].equals("menu")) editLink = "modmenu.jsp?cmd=";
        else if (props[8].equals("fonecta_edut")) editLink = "mod_fonecta_edut.jsp?cmd=";
        else if (props[8].equals("videocategory")) editLink = "modpromo5.jsp?cmd=videocategory";
       
        else editLink = "";

        //System.out.println(props[5] + ": " + ind + ": " + list.size());
        if (props[5].equals("Divider")) bgcolor = "#DDDDDD";
        else bgcolor = "#FFFFFF";

%>
    <li id="item_<%=props[0]%>">
    <table cellspacing="0" cellpadding="10" border="0" class="tableview" width="100%">
        <tr bgcolor="<%=bgcolor%>">
            <td width="20%" align="left" class="grey_11"><%=props[5]%></td>
            <td width="20%" align="left" class="grey_12"><input type="text" name="title_<%=props[0]%>" size=20 value="<%=props[2]%>"></td>
            <td width="20%" align="center" class="grey_12"><input type="checkbox" value="1" name="act_<%=props[0]%>" <% if (props[4].equals("1")){%> checked <%}%>></td>
            <!--  
            <td width="80" align="left"><nobr><input type="text" size="8" name="bg_<%=props[0]%>" value="<%=props[6]%>"  style="background-color:<%=props[6]%>;">
                <a href="javascript:change_color(form1, form1.bg_<%=props[0]%>,'<%=subpage%>');"><img src="/images/sel.gif" border="0"></a></nobr></td>
            <td width="80" align="left"><nobr><input type="text" size="8" name="font_<%=props[0]%>" value="<%=props[7]%>" style="background-color:<%=props[7]%>;">
                <a href="javascript:change_color(form1, form1.font_<%=props[0]%>,'<%=subpage%>');"><img src="/images/sel.gif" border="0"></a></nobr></td>
                -->
                <!--
                <td align="center">
                    <% if (ind>1) { %> <a href="<%=fileName%>.jsp?cmd=up&srvc=<%=props[0]%>&ind=<%=ind%>"><span class="blue_11">[Up]</span></a>
                    <% } else {%> <span class="grey_11">[Up]</span> <%}%>

                   <%if ((list.size()-ind)>0) { %> <a href="<%=fileName%>.jsp?cmd=dwn&srvc=<%=props[0]%>&ind=<%=ind%>"><span class="blue_11">[Down]</span></a>
                    <% } else {%> <span class="grey_11">[Down]</span> <%}%>
                </td>
                -->
                <td width="20%">
                   <%if (!editLink.equals("")) { %> <a href="<%=editLink%>&srvc=<%=props[0]%>&dm=<%=dm%>&clnt=<%=clientunique%>&regionid=<%=regionid%>"><span class="blue_11">[Edit]</span></a>
                    <% } else {%> &nbsp; <%}%>
                </td>
                <td  width="20%"><nobr>&nbsp;&nbsp;<a href="javascript:win2('delmodule.jsp?srvc=<%=props[0]%>&ind=<%=ind%>')"><span class="blue_11">[Delete]</span></a></nobr></td>
        </tr>
    </table>
    </li>
    <% } %>
</ul>
</td></tr>
<tr><td align="right"><br><input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;"></td></tr>
<tr><td><br><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<% }}%>

</table>

</form>

<form id="TheForm" name="TheForm" method="post" action="<%=domainlink%>">
		<input type="hidden" name="previewLandingPage" value="<%=template_page%>" />
	
	</form>

<script type="text/javascript">
    if ( navigator.appName.toLowerCase().indexOf('microsoft') == -1 )
    Sortable.create(
        "itemlist",
        {
            onUpdate: function(){
                temp = Sortable.serialize('itemlist');
                temp = temp.split ( '&itemlist[]=' );
                makeRequest( 'updatemoduleorder.jsp?' + temp , '' );
                //win('http://<%=System.getProperty(dm + "_url")%>/simulator.jsp<%=sublink%>');
            }
        }
    );
</script>
</body>
</html>

