<%@ include file="/WEB-INF/jspf/itemadmin/index.jspf" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<link rel="stylesheet" href="/lib/previewplay.css" media="screen" />

<script src="/lib/global_anyx.js" language="javascript"></script>
<script type="text/javascript" src="/lib/JavaScriptFlashGateway.js"></script>

<script type="text/javascript" language="javascript">

    function submitform(src, form) {
        if (src=="sort") form.cat.value="";
        form.submit();
    }

    function submitForm2 (thisForm, ind, source) {
        thisForm.si.value=ind
        thisForm.formsrc.value=source;
        thisForm.submit();
    }

    function submitForm3 (thisForm, ind, source) {
        thisForm.si.value=ind
        thisForm.formsrc.value=source;
        thisForm.sss.value="";
        thisForm.cat.selectedIndex=0;
        thisForm.submit();
    }

</script>

</head>
<body>
<div class="previewsample"><script type="text/javascript" src="/lib/previewplay.js"></script></div>
<form action="<%=fileName%>.jsp" method="post" name="form1">
<input type="hidden" name="si" value="">
<input type="hidden" name="formsrc" value="">

<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="3" border="0" width="100%">
        <tr>
            <td align="left" valign="bottom" class="big_blue">Item Admin</td>
            <td align="right" class="status_red"><b><%=statusMsg%></b></td>
        </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td>

    <table cellpadding="6" cellspacing="0" border="0" width="100%">

    <tr>
    <td class="grey_11">Item Type:</td>
    <td colspan="2">
        <select name="itype" onChange="javascript:submitForm3(this.form,0,1);">
            <option value="all">[Select]</option>
            <optgroup label="Master Tones">
                <option value="mastertone" <% if (itype.equals("mastertone")){ %> selected <% } %>>Real Tones</option>
                <option value="truetone" <% if (itype.equals("truetone")){ %> selected <% } %>>True Tones</option>
                <option value="funtone" <% if (itype.equals("funtone")){ %> selected <% } %>>Fun Tones & Sound Effects</option>
            </optgroup>
            <option value="java" <% if (itype.equals("java")){ %> selected <% } %>>Java Games</option>
            <option value="bgimage" <% if (itype.equals("bgimage")){ %> selected <% } %>>Bg Images</option>
            <option value="video" <% if (itype.equals("video")){ %> selected <% } %>>Videos</option>
            <option value="gifanim" <% if (itype.equals("gifanim")){ %> selected <% } %>>Animations</option>
            
            <option value="ringtone" <% if (itype.equals("ringtone")){ %> selected <% } %>>Poly/Mono Tones</option>
            
            <!--<option value="theme" <% if (itype.equals("theme")){ %> selected <% } %>>Themes</option>-->
        </select>
        <!--
        <select name="itype" onChange="javascript:submitform('sort', this.form);">
            <option value="none">[Select]</option>
            <option value="cr" <% if (sort.equals("created")){%> selected <%}%>>Creation Date</option>
            <option value="tt" <% if (sort.equals("title")){%> selected <%}%>>Title</option>
            <option value="pr" <% if (sort.equals("pr")){%> selected <%}%>>Price Group</option>
        </select>
        -->
    </td>
    </tr>
     <tr>
        <td class="grey_11">Category:</td>
        <td colspan="2">
        <select name="cat" onChange="javascript:this.form.submit();">
            <option value="all">[Select]</option>

            <% if (catlist!=null) {
                    for (int i=0; i<catlist.size(); i++) {
                    ItemCategory ic = catlist.get(i);
            %>
            <option value="<%=ic.getUnique()%>" <% if (cat.equals(ic.getUnique())){%> selected <%}%>><%=ic.getName1()%> (<%=ic.getItemCount()%>)  </option>
            <%
		if (ic.getSubCategories().size()>0) {
                    for (int k=0; k<ic.getSubCategories().size(); k++) {
                        ItemCategory icc = ic.getSubCategories().get(k);
	%>
	<option value="<%=icc.getUnique()%>" <% if (cat.equals(icc.getUnique())){%> selected <%}%>>&nbsp;:&nbsp;<%=icc.getName1()%></option>
	<%
				}
			}
		}
        }
        %>
        </select>
        </td>
     </tr>
    <% if (isAdmin) {%>
     <tr>
        <td class="grey_11">Content Provider:</td>
        <td colspan="2">
            <select name="owner" onChange="javascript:submitForm2(this.form,0,0);">
              
              <option value="all">[Select]</option>
              
              <% for (int i=0; i<cps.size(); i++) {
                  ContentProvider cprov = cps.get(i);
                  if (!isAdmin && !cprov.getUnique().equals(sdcuser.getUserGroup())) continue;
                %>
               <option value="<%=cprov.getUnique()%>" <% if (owner.equals(cprov.getUnique())){%> selected <%}%>><%=cprov.getName()%></option>
               <% } %>
            </select>
        </td>
    </tr>
    <%}%>
     <tr>
	<td class="grey_11">Classification:</td>
	<td colspan="2">
            <select name="classification">
                <option value="all">[Select]</option>
                <option value="safe" <% if (classification.equals("safe")){%> selected <%}%>>Safe</option>
                <option value="bikini" <% if (classification.equals("bikini")){%> selected <%}%>>Bikini</option>
                <option value="topless" <% if (classification.equals("topless")){%> selected <%}%>>Topless</option>
                <option value="fullnude" <% if (classification.equals("fullnude")){%> selected <%}%>>Full Nude</option>
                <option value="hardcore" <% if (classification.equals("hardcore")){%> selected <%}%>>Hardcore</option>
            </select>
	</td>
     </tr>
     <tr>
        <td class="grey_11">Search:</td>
        <td>
            <input type="text" name="sss" value="<%=search%>" size=20>
        </td>
        <td align="right"><input type="submit" name="getitems" value="&nbsp;GET&nbsp;" onClick="javascript:submitForm2(this.form,0,1);"></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
<% if (pageCount>1) { %>

    <tr>
    <td align="left" class="grey_11">
    <% if (sIndex>0) { %>
            <a href="javascript:submitForm2(document.form1,<%=(sIndex-maxCount)%>,0);"><span class="blue_11">PREVIOUS</span></a> |
    <% } else { %>
            PREVIOUS |
    <% }
            if (((sIndex+maxCount)/maxCount)<pageCount) { %>
    <a href="javascript:submitForm2(document.form1,<%=(sIndex+maxCount)%>,0);"><span class="blue_11">NEXT</span></a> |
    <% } else { %>
            NEXT |
    <%
            }

    for (int k=0; k<pageCount; k++) {
        int newIndex = k*maxCount;
        if (newIndex==sIndex) { out.print("<span class='red_11' style='font-family: Verdana;'><b>" + (k+1) + "</b></span>"); }
        else {
    %>
                <a href="javascript:submitForm2(document.form1,<%=newIndex%>,0);"><span class="blue_11" style="font-family: Verdana;"><b><%=(k+1)%></b></span></a>
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
          <th align="left" width="150">Item</th>
          <th align="left" width="200">&nbsp;</th>
          <th align="center" width="100">Price Group</th>
          <th align="center" width="200">Status
          <!--
          <select name="clnt" onChange="javascript:this.form.submit();">
          <option value="" <% if (clnt.equals("")){%> selected <%}%>>[Select Client]</option>
          <% for (int i=0; i<clients.size(); i++) {
              Client client = clients.get(i);
            %>
           <option value="<%=client.getUnique()%>" <% if (clnt.equals(client.getUnique())){%> selected <%}%>><%=client.getName()%></option>
           <% } %>-->
          <!--  </select>-->
          </th>
          <th align="right">Provider</th>
       </tr>

    <%
    if (list!=null) {
        String itemStat = "";
        
        if (list.size()==0) {
    %>        
    <tr><td align="center" colspan="5"><b>No Results</b></td></tr>
      <%  } else {

        for (int i=0; i<list.size(); i++) {
            item = (ContentItem) list.get(i);

            if (item.getStatus()==1) itemStat = "<span class=\"green_11\">Active</span>";
            else itemStat = "<span class=\"red_11\">Disabled</span>";

            flashid = "flashid_" + Misc.generateUniqueId();
            System.out.println(item.getOwner());
    %>

        <tr>
            <% if (itype.equals("mastertone") || itype.equals("truetone") || itype.equals("funtone")){
                itype = "mastertone";
            %>
            <td valign="top" align="left" width="300">
                <a href="<%=itype%>/itemdetails.jsp?unq=<%=item.getUnique()%>"><%=item.getTitle()%></a>
            </td>
            <td width="50">
                <a href="javascript:void(0)" onclick="javascript:playSample('preview/<%=item.getOwner()%>/<%=item.getPreviewFile()%>.flv', '<%=flashid%>','<%=durl%>')">
            <img id="<%=flashid%>" src="/images/media_play_blue.gif" alt="" border="0"/></a>
            </td>

            <% } else if (itype.equals("ringtone")){
                Ringtone rtone = (Ringtone) item;
            %>
            <td valign="top" align="left" width="300"><a href="<%=itype%>/itemdetails.jsp?unq=<%=item.getUnique()%>"><%=item.getTitle()%></a></td>
            <td width="50">
                <a href="javascript:void(0)" onclick="javascript:playSample('<%=rtone.getSampleFile()%>', 'poly_<%=flashid%>','<%=durl%>',2)">
            <img id="poly_<%=flashid%>" src="/images/media_play_blue.gif" alt="" border="0"/></a>

            &nbsp;&nbsp;
            <a href="javascript:void(0)" onclick="javascript:playSample('<%=rtone.getMonoSample()%>', 'mono_<%=flashid%>','<%=durl%>',1)">
            <img id="mono_<%=flashid%>" src="/images/media_play_blue.gif" alt="" border="0"/></a>
            </td>

            <% } else if (itype.equals("truetone") || itype.equals("funtone")){
            %>
            <td valign="top" align="left" width="300"><a href="<%=itype%>/itemdetails.jsp?unq=<%=item.getUnique()%>"><%=item.getTitle()%></a></td>
            <td width="50">
                <a href="javascript:void(0)" onclick="javascript:playSample('<%=item.getPreviewFile()%>.mp3', '<%=flashid%>','<%=durl%>',3)">
            <img id="<%=flashid%>" src="/images/media_play_blue.gif" alt="" border="0"/></a>
            
            </td>
            
            <% } else { %>
            <td><a href="<%=itype%>/itemdetails.jsp?unq=<%=item.getUnique()%>"><img src="<%=item.getPreviewFile()%>" border="0" width="80px" height="70px"></a></td>
            <td valign="top" align="left" width="200"><a href="<%=itype%>/itemdetails.jsp?unq=<%=item.getUnique()%>"><%=item.getTitle()%></a></td>
            <% } %>
            
            <td valign="top" align="center" width="100"><%=item.getPriceGroup()%></td>
            <td valign="top" align="center" width="200"><%=itemStat%></td>
            <td valign="top" align="right" class="black_10"><%=SdkTempCache.getUserGroupName(item.getOwner())%></td>
        </tr>

    <%
        }
        
               }
    } %>
</table>
</td></tr>
</table>

</form>

</body>
</html>






