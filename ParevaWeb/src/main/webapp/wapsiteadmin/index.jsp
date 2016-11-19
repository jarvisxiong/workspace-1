
<%@ include file="/WEB-INF/jspf/wapsiteadmin/index.jspf" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">

	<link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all" rel="stylesheet" type="text/css" />
	<link href="/lib/templates/static/AdminPanel/assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
	<link href="/lib/templates/static/AdminPanel/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<!-- END GLOBAL MANDATORY STYLES -->
	<!-- BEGIN THEME GLOBAL STYLES -->
	<link href="/lib/templates/static/AdminPanel/assets/global/css/components-md.min.css" rel="stylesheet" id="style_components" type="text/css" />
	<link href="/lib/templates/static/AdminPanel/assets/global/css/plugins-md.min.css" rel="stylesheet" type="text/css" />
	<!-- END THEME GLOBAL STYLES -->
	<!-- BEGIN THEME LAYOUT STYLES -->
	<link href="/lib/templates/static/AdminPanel/assets/layouts/layout/css/layout.min.css" rel="stylesheet" type="text/css" />
	<link href="/lib/templates/static/AdminPanel/assets/layouts/layout/css/themes/darkblue.min.css" rel="stylesheet" type="text/css" id="style_color" />
  <style>
	.contentHolder embed,
	object {
		min-height: 1000px;
		width: 100%;
		height: 100%;
	}
    .alertHolder {
        min-height: 49px;
        margin-top: -20px;
        margin-bottom: -20px;
    }
    iframe {
      width: 100%;
      height: 100%;
      min-height: 1000px;
      border: none;
    }
    body {
    	background: white;
    }

    .landingPageSettings input[type="text"] {
    	width: 85px;
    }

    .fa-cog {
    	line-height: 10px;
    	font-size: 12px;
    }

    .spinOnHover:hover i {
    	-webkit-animation: fa-spin 2s infinite linear;
    	animation: fa-spin 2s infinite linear;
    }

    .table .btn {
    	padding: 7px;
    }

	.table .btn:last-child {
		margin-right: 0;
	}
  </style>


<script>

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

function editTheme(url,themeLocation){
	/* window.open('', 'TheWindow');
	document.getElementById('TheForm').submit();
	 */
	 var themeEditorForm = document.createElement("form");
	 themeEditorForm.target = "editor";
	 //previewForm.target = "_blank";
	 themeEditorForm.method = "POST"; // or "post" if appropriate
	 themeEditorForm.action = url;

	  	var themeLocationInput = document.createElement("input");
	  	themeLocationInput.type = "text";
	  	themeLocationInput.name = "themeLocationInput";
	  	themeLocationInput.value = themeLocation;
	 /*   
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
	    */
	    themeEditorForm.appendChild(themeLocationInput);

 	    document.body.appendChild(themeEditorForm);
	    // preview = window.open("", "preview", "status=0,title=0,height=600,width=800,scrollbars=1","false");
	    
	   window.open("", "editor", "status=0,title=0,height=600,width=800,scrollbars=1");
	   themeEditorForm.submit();
	/* if (preview) {
		previewForm.submit();
	} else {
	    alert('You must allow popups for this preview to work.');
	} */ 
	
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

    
function updateThemeSetting(action,k,namevalue) {
    	document.getElementById("ss").value="";
    	document.getElementById("editTheme").value=action;
    	document.getElementById("themeName").value=namevalue;
    	var themeStatus = "themeStatus"+k;
    	var appendMe = '<input type="checkbox" id="'+themeStatus+'"  value="1" >'
    	$("form1").append(appendMe);
    	document.getElementById("themeStatus").value="true";
    	/*if(document.getElementById("themeStatus"+k).checked)
    		document.getElementById("themeStatus").value="true";
    	else
    		document.getElementById("themeStatus").value="false";
    	*/
    	document.forms["form1"].submit();
        /* thisform.ss.value="";
        thisform.up.value="1";
       
        thisform.submit(); */
    }



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
    	thisform = $("#form1");
        thisform.ss.value="";
        thisform.submit();
    }

</script>

</head>
<body>
<%=landingPageCampaigns %>
<div class="alertHolder"></div> 
<div class="statusMsg" style="visibility: hidden; position: absolute;"><%=statusMsg%></div>

    <h3 class="page-title">
        General Settings
    </h3>

<div class="reloadArea">

    <form action="<%=fileName%>.jsp" method="post" name="form1" id="form1">

    <input type="hidden" name="ss" value="1" id="ss">
	<input type="hidden" name="action" id="action" value="">
	<input type="hidden" name="landing_page" id="landing_page" >
	<input type="hidden" name="startTime" id="startTime" >
	<input type="hidden" name="endTime" id="endTime" >
	<input type="hidden" name="networkName" id="networkName" >
	<input type="hidden" name="landingStatus" id="landingStatus" >


		<div class="row">
	        <div class="form-body col-md-5">
	            <div class="form-group form-md-line-input">
	                <label class="col-md-4 control-label" for="regionid">Region:</label>
	                <div class="col-md-8">
	                    <select name="regionid" id="region" class="form-control jsResetThemeSubmit" >
	                        <option value="" selected >[Select Region]</option>
	                        <% for (int i=0; i<regionlist.size(); i++) {
                        		regionprops =  regionlist.get(i);
                        		System.out.println("Region list count "+regionlist.size()+ " Region Props: "+regionprops);
                    		%>
                    		<option value=<%=regionprops%> 
                    			<% if (regionprops.equals(regionid)){%> selected <%}%>> 
                    			<%=regionprops%> 
                    		</option>
                    		<%}%>
	                    </select>
	                </div>
	            </div>
	            
	            <div class="form-group form-md-line-input">
	                <label class="col-md-4 control-label" for="dm">Domain:</label>
	                <div class="col-md-8">
	                	<%if(regionid!=null && !regionid.trim().equalsIgnoreCase("")){%>
		                    <select name="dm" id="dm" class="form-control jsResetThemeSubmit" >
		                        <option value="null" selected >[Select Domain]</option>
		                        <% for (int i=0; i<dms.size(); i++) {
			                        props = (String[]) dms.get(i);
			                    %>
			                    <option value="<%=props[0]%>" 
			                    	<% if (props[0].equals(dm)){
			                        	foundDomain = true;
			                        	//client=props[3];
			                        	domainlink = "http://" + props[2] + "/";
			                    	%> selected
			                    <%}%>
			                    ><%=props[1]%> -- http://<%=props[2]%></option>

			                    <% } %>
			                </select>
			            <%}%>
	                </div>
	            </div>



 <%-----------------------------------------------------------------------------------------------------------------------%>	
 
    <%if(regionid!=null && !regionid.trim().equalsIgnoreCase("")){%>
    	<% if (!dm.equals("") && !dm.equals("null")) {%>

    		<div class="form-group form-md-line-input" >
                <label class="col-md-4 control-label" for="ts">Theme Settings:</label>
                <div class="col-md-8">
                	<select name="themeName" id="themeName" class="form-control jsChangeSubmit">
                		<option value="" >[Select Theme]</option>
	                        <%for(int i=0;i<templateList.size();i++){
								Map<String,String> templateMap=(Map<String,String>)templateList.get(i);
								String templateName=templateMap.get("templateName");
								String templateStatus=templateMap.get("status");
		            		%>	
                    		<option value="<%=templateName%>" 
                    				<% if (templateStatus.equals("1")){%> selected <%}%> > 
                    				<%=templateName%>
                    		</option>
                    		<%}%>
                	</select>
                </div>
            </div>
			
			  <!-- ------------------------------------------------------------------------------------------------------------------------ -->
            <%if (subTemplatesExist==true){ %>
            <div class="form-group form-md-line-input" >
                <label class="col-md-4 control-label" for="ts">Niche:</label>
                <div class="col-md-8">
                	<select name="subThemeName" id="subThemeName" class="form-control jsChangeSubmit">
                		<option value="" >[Select Theme]</option>
	                        <%for(int i=0;i<subTemplateList.size();i++){
								Map<String,String> templateMap=(Map<String,String>)subTemplateList.get(i);
								String templateName=templateMap.get("templateName");
								String templateStatus=templateMap.get("status");
		            		%>	
                    		<option value="<%=templateName%>" 
                    				<% if (templateStatus.equals("1")){%> selected <%}%> > 
                    				<%=templateName%>
                    		</option>
                    		<%}%>
                	</select>
                </div>
            </div>
            <%} %>
            <!-- ------------------------------------------------------------------------------------------------------------------------ -->
            
            
                  
            <div class="form-group form-md-line-input" >
                <label class="col-md-4 control-label" for="template_page">Template Page:</label>
                <div class="col-md-8">
                	<select name="template_page" id="template_page" class="form-control jsChangeSubmit">
                		<option value="" >[Select Template Page]</option>
	                        <%for(int k=0;k<templateFiles.size();k++){%>
								<option 
									value="<%=templateFiles.get(k)%>" 
									<% if (template_page.equals(templateFiles.get(k))){%> selected <%}%>
								>
									<%=templateFiles.get(k)%>
								</option>
                    		<%}%>
                	</select>
                </div>
            </div>


	<% 	
		if (!template_page.equals("")){
	%>
		<div class="form-group form-md-line-input" >
                <label class="col-md-4 control-label" for="previewNetwork">Select Network:</label>
                <div class="col-md-8">
                	<select name="previewNetwork" id="previewNetwork" class="form-control" >
                		<option value=" " selected>Select Network</option>
            				<option value="all">all</option>
            				<option value="default">default</option>
								<%for (int i=0;i<networks.size();i++) {
									String[] networkParameter=new String[2];
									networkParameter=(String[])networks.get(i);
								%>
							<option value="<%=networkParameter[0]%>"><%=networkParameter[0] %></option>
								<%} %>
						</select>
                </div>
            </div>

            <div class="form-group form-md-line-input">
            	<label class="col-md-4 control-label" for="title">Preview Options:</label>
                <div class="col-md-4">
					<a 	href="javascript:preview('<%=domainlink%>','<%=template_page%>','false')"
                   		class="btn btn-outline btn-circle btn-sm purple">
                   		<i class="fa fa-search-plus"></i>Preview
                   	</a> 
                </div>
                <div class="col-md-4">
                    <a 	href="javascript:preview('<%=domainlink%>','<%=template_page%>','true')"
                    	class="btn btn-outline btn-circle btn-sm purple">
                    	<i class="fa fa-search-plus"></i>Preview Identified
                    </a>
                </div>
            </div>
		
 		
			<a style="display:none;" href = "editTemplatePage.jsp?templateName=<%=template_page%>&templateLocation=<%=location%>&regionid=<%=regionid%>&dm=<%=dm%>&domainlink=<%=domainlink%>">|Edit</a>

			<%} %>

<%------------------------------------------------------------------------------------------%>
			<div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="title">Domain Title:</label>
                <div class="col-md-8">
                    <input type="text" name="title" value="<%=params.get("title")%>" class="form-control" />
                    <div class="form-control-focus"> </div>
                </div>
            </div>
  
  			<div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="title">Header / Footer:</label>
                <div class="col-md-8">
                	<a 	href="header.jsp"
                    	class="btn btn-outline btn-circle btn-sm purple spinOnHover">
                    	<i class="fa fa-cog"></i>Modify Images
                    </a>
                </div>
            </div>
           
<%-------------------------------------------------------------------------------------------------------------------------------------------%>

				
	
<%-------------------------------------------------------------------------------------------------------------------------------------------%>	            
            

            	
	        <%}}%>



 <%if(regionid!=null && !regionid.trim().equalsIgnoreCase("")){%>
<% if (list.size()>0) {  %>
<% }}%>


	
	</div>

	<div class="form-body col-md-7">
		<div class="portlet light bordered landingPageSettings">
		    <div class="portlet-title">
		        <div class="caption">
		            <i class="icon-social-dribbble font-green"></i>
		            <span class="caption-subject font-green bold uppercase">Landing Page Setting</span>
		        </div>
				<div class="actions">
		            <div class="btn-group btn-group-devided" data-toggle="buttons">
		                <label class="btn btn-transparent red btn-outline btn-circle btn-sm active jsAddLandingToggle">
		                	Add New Landing Page 
		                	<i class="fa fa-angle-down"></i>
		                </label>
		            </div>
		        </div>
		    </div>
		    <div class="portlet-body">
		    	<div class="addNewLandingHolder" style="display: none;">
		    	<b>Add Landing Page:</b>
					<select name="addLanding" id="addLanding" >
		          		<option value=" " selected>Select Landing Page</option>
		          		<%for (int i=0;i<domainsLandingPagesToBeAdded.size();i++){ %>       
			            <option value="<%=domainsLandingPagesToBeAdded.get(i)%>">
			            	<%=domainsLandingPagesToBeAdded.get(i)%></option>
						<%} %>
					</select>
					<button type="submit" class="btn blue btn-sm formSubmit">
                        <i class="fa fa-plus"></i> 
                        Add Landing Page
                    </button>
				
		    	</div>

		        <div class="table-scrollable">
		            <table class="table table-hover">
		                <thead>
		                    <tr>
		                        <th> LandingPage </th>
		                        <th> StartTime </th>
		                        <th> EndTime </th>
		                        <th> Network </th>
		                        <th> Status </th>
		                        <th> Action </th>
		                        
		                    </tr>
		                </thead>
		                <tbody>
		                   
		                    <%for(int k=0;k<landingPages.size();k++){
								HashMap landingMap=(HashMap)landingPages.get(k);
		                    %>	
				
							<tr>
								<td><%=landingMap.get("landingPage")%>
		                    		<input 	type="hidden" 
		                    				class="form-control"
		                    				value="<%=landingMap.get("landingPage")%>" 
		                    				id="landingPage<%=k%>" />
		                    	</td>
								<td>
									<input 	type="text" 
											class="form-control"
											value="<%=landingMap.get("startTime")%>" 
											id="startTime<%=k%>" />
								</td>
								<td>
									<input 	type="text" 
											class="form-control"
											value="<%=landingMap.get("endTime") %>" 
											id="endTime<%=k%>">
								</td>
								<td>
									<select  id="networkName<%=k%>" class="form-control">
										<option value=" " selected>Select Network</option>
										<option value="all" <% if (landingMap.get("network").equals("all")){%> selected <%}%>>all</option>
										<option value="default" <% if (landingMap.get("network").equals("default")){%> selected <%}%>>default</option>
										<%for (int i=0;i<networks.size();i++) {
											String[] networkParameter=new String[2];
											networkParameter=(String[])networks.get(i);
										%>
											<option value="<%=networkParameter[0]%>" <% if (landingMap.get("network").equals(networkParameter[0])){%> selected <%}%>><%=networkParameter[0] %></option>
										<%} %>
									</select>
								</td>
								<td>
		                            <div class="md-checkbox-list">
				                        <div class="md-checkbox col-md-8">
				                            <input 	type="checkbox"  
				                            		name="landingStatus<%=k%>" 
				                            		id="landingStatus<%=k%>" 
				                            		class="md-check" 
				                            		value='<%=landingMap.get("status")%>' 
				                                <%if (landingMap.get("status").equals("1")){%>checked<%} %>
				                            >
				                            <label for="landingStatus<%=k%>">
				                                <span></span>
				                                <span class="check"></span>
				                                <span class="box"></span>
				                            </label>
				                        </div>
				                    </div>



		                        </td>
								<td>

									<a 	class="btn btn-outline btn-circle green btn-sm black"
										href="javascript:updateLandingPage('edit','<%=k%>')">
										<i class="fa fa-save"></i>Save
									</a>
									
									<a 	class="btn btn-outline btn-circle red btn-sm blue"
										href="javascript:updateLandingPage('delete','<%=k%>')">
										<i class="fa fa-trash-o"></i> 
									</a>
								</td>
							</tr>
						<%}%> 

		                </tbody>
		            </table>
		        </div>
		    </div>
		</div>

		<div class="portlet light bordered landingPageSettings">
		    <div class="portlet-title">
		        <div class="caption">
		            <i class="icon-social-dribbble font-green"></i>
		            <span class="caption-subject font-green bold uppercase">Page Elements</span>
		        </div>
				<div class="actions">
		            <div class="btn-group btn-group-devided" data-toggle="buttons">
		                <label class="btn btn-transparent red btn-outline btn-circle btn-sm active jsAddLandingToggle">
		                	Add New Elements
		                	<i class="fa fa-angle-down"></i>
		                </label>
		            </div>
		        </div>
		    </div>
		    <div class="portlet-body">

		    	<div class="addNewLandingHolder" style="display: none;">
					<div class="form-group form-md-line-input row">
		                <label class="col-md-2 control-label" for="addsrvc">Add Element:</label>
						 <div class="col-md-3">
	               
	                    <select name="classification" id="classification" 
	                    		class="form-control jsChangeSubmit" >
	                        <option value=" " selected>Select Classification</option>
				            <%for(int i=0;i<classificationList.size();i++) {%>
							<option value="<%=classificationList.get(i)%>" 
								<% if (classification.equals(classificationList.get(i)))
									{%> selected <%}%>
								>
								<%=classificationList.get(i) %>
							</option>
							<%} %>
	                    </select>
	                
	            </div>

	            
	                <div class="col-md-3">
	                    <select name="contenttype" id="contenttype" 
	                    		class="form-control jsChangeSubmit" >
	                        <option value=" " selected>Select ContentType</option>
				            <%for(int i=0;i<contentTypeList.size();i++) {%>
								<option value="<%=contentTypeList.get(i)%>" <% if (contentType.equals(contentTypeList.get(i))){%> selected <%}%>><%=contentTypeList.get(i) %></option>
								<%} %>
	                    </select>
	                </div>
	           
		                <div class="col-md-3">
		                    <select name="addsrvc" id="addsrvc" class="form-control" >
		                        <option value=" " selected>Select Element</option>
					            <% for (int i=0; i<modules.size(); i=i+2) {
	                                String stitle = (String) modules.get(i);
	                                slist = (java.util.List) modules.get(i+1);
	                            %>
	                            <optgroup label="<%=stitle%>">
		                            <% for (int k=0; k<slist.size(); k++) {
		                                props = (String[]) slist.get(k);
		                                System.out.println("props[0]_props[1] = "+props[0]+"_"+props[1]);
		                            %>
		                            <option value="<%=props[0]%>_<%=Misc.hex8Code(props[1])%>">
		                            	<%=props[1]%>
		                            </option>
		                            <% } %>
	                            </optgroup>
	                            <%}%>
		                    </select>
		                </div>
		                <div class="col-md-3">
		                    <select name="category" class="form-control">
	                         	<option value="">Select Category</option>
		                        <% for (int i=0; i<list.size();i++) {
		                                String[] serviceList=(String[])list.get(i);
		                            	//String stitle = (String) modules.get(i);
		                                //slist = (java.util.List) modules.get(i+1);
		                      	%>    
	                                <option value="<%=serviceList[0]%>">
	                                	<%=serviceList[2]%>
	                                </option>
								<%}%>
	                        </select>
	                    </div>
	                    <div class="col-md-3">
	                        <select name="number_of_elements" class="form-control">
	                         	<option value="0" selected>Num of elements</option>
								<% for(int number_of_elements=1;number_of_elements<=10;number_of_elements++)
								{ %>
								<option value="<%=number_of_elements%>">
									<%= number_of_elements%>
								</option>
								<% } %>
	                        </select>
	                    </div>
	                    	<button type="submit" name="add" value="Add" class="btn blue btn-sm formSubmit">
                        		<i class="fa fa-plus"></i> 
                        		Add
                        	</button>
		                </div>
		            </div>
		    	</div>
		    	<%if(regionid!=null && !regionid.trim().equalsIgnoreCase("")){ %>
					<% if (list.size()>0) {  %>

			        <div class="table-scrollable">
			            <table class="table table-hover">
			                <thead>
			                    <tr>
			                        <th> Element Type </th>
			                        <th> Name </th>
			                        <th> Active </th>
			                        <th> Actions </th>
			                        
			                    </tr>
			                </thead>
			                <tbody>
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
								<tr>
									<td>
										<%=props[5]%>
			                    	</td>

									<td>
										<input type="text" name="title_<%=props[0]%>" value="<%=props[2]%>" class="form-control" />
									</td>

									<td>
			                            <div class="md-checkbox-list">
					                        <div class="md-checkbox col-md-8">
					                            <input 	type="checkbox"  
					                            		name="act_<%=props[0]%>" 
					                            		class="md-check" 
					                            		value='1' 
					                            		id="act_<%=props[0]%>"
					                                <% if (props[4].equals("1")){%> checked <%}%>
					                            >
					                            <label for="act_<%=props[0]%>">
					                                <span></span>
					                                <span class="check"></span>
					                                <span class="box"></span>
					                            </label>
					                        </div>
					                    </div>
			                        </td>
									<td>
	                					<%if (!editLink.equals("")) { %> 
	                					<a href="<%=editLink%>&srvc=<%=props[0]%>&dm=<%=dm%>&clnt=<%=clientunique%>&regionid=<%=regionid%>">
											<a 	class="btn btn-outline btn-circle green btn-sm black spinOnHover"
												href="<%=editLink%>&srvc=<%=props[0]%>&dm=<%=dm%>&clnt=<%=clientunique%>&regionid=<%=regionid%>">
												<i class="fa fa-cog"></i>Edit
											</a>
										<% } else {} %>
										<a 	class="btn btn-outline btn-circle red btn-sm blue"
											href="javascript:win2('delmodule.jsp?srvc=<%=props[0]%>&ind=<%=ind%>')">
											<i class="fa fa-trash-o"></i> 
										</a>
									</td>
								</tr>
							
							<% } %>
			                </tbody>
			            </table>
			        </div>
			    	<%}
				}%> 
		    </div>
		</div>


		<input type="submit" name="save" value="Save" class="btn blue btn-block" >

		</div>
	</div>




	        
	</form>
</div>

<form id="TheForm" name="TheForm" method="post" action="wapsiteadmin/<%=domainlink%>">
		<input type="hidden" name="previewLandingPage" value="<%=template_page%>" />
</form>

<!-- BEGIN CORE PLUGINS -->
<script src="/lib/templates/static/AdminPanel/assets/global/plugins/jquery.min.js" type="text/javascript"></script>
<script src="/lib/templates/static/AdminPanel/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<script src="/lib/templates/static/AdminPanel/assets/global/plugins/js.cookie.min.js" type="text/javascript"></script>
<script src="/lib/templates/static/AdminPanel/assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script>
<!-- END CORE PLUGINS -->

<!-- BEGIN THEME GLOBAL SCRIPTS -->
<script src="/lib/templates/static/AdminPanel/assets/global/scripts/app.min.js" type="text/javascript"></script>
<!-- END THEME GLOBAL SCRIPTS -->
<!-- BEGIN THEME LAYOUT SCRIPTS -->
<script src="/lib/templates/static/AdminPanel/assets/layouts/layout/scripts/layout.min.js" type="text/javascript"></script>
<script src="/lib/templates/static/AdminPanel/assets/layouts/layout/scripts/demo.min.js" type="text/javascript"></script>
<!-- END THEME LAYOUT SCRIPTS -->


<script>
	// MY JS HERE
$( document ).ready(function() {



	function submitFunc() {
		thisform = $("#form1");
        thisform.find("#ss").val("");
        thisform.submit();
	}
	
	function submitResetTheme() {
		thisform = $("#form1");
        thisform.find("#ss").val("");
        thisform.find("#themeName").val("");
        
        thisform.submit();
	}

	$('select.jsChangeSubmit').on('change', function() {
		submitFunc();
	});
	
	$('select.jsResetThemeSubmit').on('change', function() {
		submitResetTheme();
	});
	

	$('.formSubmit').on('click', function() {
		submitFunc();
	});

	$(".jsAddLandingToggle").on("click", function() {
		console.log($(this))
		$(this).parents(".portlet").find(".addNewLandingHolder").slideToggle("fast");
	}); 

	$('select.jsUpdateThemeSettings').on('change', function() {
		var k = $(this).val();
		var idLookup = '#knum'+k;
		var option = $(this).find(idLookup);
		updateThemeSetting("edit", $(this).val(), option.data("namevalue"));
	  	//alert( this.value ); // or $(this).val()
	});


	/*$('body').on("change", ".jsChangeRegion", function() {
		console.log('formSubmitted');
		var frm = $("#newForm");
		var data = frm.serialize();

		$.ajax({
	        type: frm.attr('method'),
	        url: frm.attr('action'),
	        data: data,
	        success: function (data) {
	            $("#submit").attr("value", "Save");
	            var parsed  = $.parseHTML(data);
	            var success = $(parsed).filter(".statusMsg").text();
	            var portlet = $(parsed).find(".domainShitWrapper");
	            $("body").find(".domainShit").html(portlet);
	        }
	    });
	});*/

    
    
    

    
});
</script>
 

</body>
</html>




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


 <%--     
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
                    <a href="javascript:openwin('<%=domainlink%>simulator.jsp<%=sublink%>');">Open/refresh simulator window</a> 
                    <a href="javascript:openwin('<%=domainlink%>simulator.jsp?p=index');">Open/refresh simulator window</a> 
                </td>
            </tr>
            --%>


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

            <tr>
                <td align="left" class="grey_12"><nobr><b>Show disclaimer for Adult content:</b></nobr></td>
                <td colspan="2" align="left">
                 <input type="checkbox" name="adultcontent" value="1" <% if (params.get("adultcontent")!=null && params.get("adultcontent").equals("1")){%> checked <%}%>>
                </td>
            </tr>
             
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


           <%--  
<% if (!templateid.equals("")){%>	
<td align="left">	
	<a href="edittemplate.jsp?srvc=<%=props[0]%>&dm=<%=dm%>&clnt=<%=clientunique%>"><span class="blue_11">[Edit]</span></a>
</td>
<%}%>
--%>
<%--   
            <tr>
                <td align="left" class="grey_12"><nobr><b>Price Groups:</b></nobr></td>
                 <td colspan="2" align="left"> <a href="pricegroups.jsp">Modify Price Groups</a></td>
            </tr>
            --%>
         
                <%-- 
                <td align="left" class="grey_12"><nobr><b>Add Service Module:</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></td>
                --%>

                            <%-- 
                            <option value="">[Select Service]</option>
                            --%>
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