<%@include file="/WEB-INF/jspf/itemadmin/java/addnewgame.jspf"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">
<input type="hidden" name="ftype" value="<%=fileType%>">

<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="blue_14"><b>Add New Game</b></td>
            <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>    
</td></tr>
<tr><td ><img src="/images/glass_dot.gif" height="2" width="1"></td></tr>
<tr><td ><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>   
<tr><td ><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
<tr>
<td valign="top" align="left">   
    <table cellpadding="6" cellspacing="0" border="0" width="100%">        
        
        <tr>
        <td class="blue_12">Select Source Directory</td>
        <td>
        <select name="rootdir" onChange="javascript:this.form.submit();">
        <option value="">[Select]</option>
    <%         
        for (int i=0; i<upFiles.size(); i++) {
            file = (File) upFiles.get(i);
            if (!file.isDirectory()) continue;            
            name = file.getName();
            if (name.startsWith("__")) continue;
            hexName = Misc.hex8Code(file.getPath());
            //System.out.println("file path: " + file.getPath());
    %>
        <option value="<%=hexName%>" <% if (hexrootdir.equals(hexName)){%> selected <%}%>><%=name%></option>
    <% 	} %>
        </select>
        </td></tr>
   
<% if (!rootdir.equals("")) { %>

        <tr>
        <td class="blue_12">Select XML Profile File</td>
        <td>
        <select name="proffile">
        <option value="">[Select]</option>
    <%         
        for (int i=0; i<allfiles.size(); i++) {
            file = (File) allfiles.get(i);
            if (file.isDirectory()) continue;            
            name = file.getName();
            if (!name.toLowerCase().endsWith(".xml")) continue;
            hexName = file.getPath();
            hexName = Misc.hex8Code(hexName);
    %>
        <option value="<%=hexName%>" <% if (profFile.equals(hexName)){%> selected <%}%>><%=name%></option>
    <% 	} %>
        </select>
        </td>
        <td align="right"><input type="submit" name="parse" value="Parse Profile File"></td>
    </tr>
    

<%}%>
    </table>
</td></tr>
<tr><td ><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
<tr><td ><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<% if (!statusMsg2.equals("")) { %>
<tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
<tr><td class="red_11" align="left"><b>XML PARSE ERROR:</b><br><%=statusMsg2%></td></tr>

<% } %>
<tr><td>
<br>
    <table cellpadding="6" cellspacing="0" border="0" width="100%" class="tableview">
    <tr><th align="left"><b>Phones Added</b></th></tr>  
        
        <% 
         Iterator iterator = phonesAdded.keySet().iterator();
 
         while (iterator.hasNext()) {
               Object key = iterator.next();
               java.util.List value = (java.util.List) phonesAdded.get(key);
        %>
        
         <tr><td class="row2"><b><%=(String) key%></b></td></tr>
         <tr><td class="grey_11">      
        <%
               if (value != null) {
                   for (int i=0; i<value.size(); i++) {               
        %>
          <%=value.get(i)%>, 
        
                  <%
                   }
             }  
            %>
        </td></tr>
 <%
        }
             %>
    </table>
</td></tr>
<tr><td>
<br>
    <table cellpadding="6" cellspacing="0" border="0" width="100%" class="tableview">
        <tr><th align="left"><b>Version Updates</b></th></tr>
        
        <%  
           String row = "";
           
            for (int i=0; i<versionUpdates.size(); i++) { 
                if (row.equals("row2")) row = "";
                else row = "row2"; 
        %>
        
        <tr><td class="<%=row%>"><%=(String) versionUpdates.get(i)%></td></tr>
        <%
             }  
     %>
       </table>
</td></tr>
<tr><td>
<br>
    <table cellpadding="6" cellspacing="0" border="0" width="100%" class="tableview">
        <tr><th align="left"><b>Updated Files</b></th></tr>
        
        <%  
           row = "";
           
            for (int i=0; i<updatedFiles.size(); i++) { 
                if (row.equals("row2")) row = "";
                else row = "row2"; 
        %>
        
        <tr><td class="<%=row%>"><%=(String) updatedFiles.get(i)%></td></tr>
        <%
             }  
     %>
       </table>
</td></tr>
<tr><td>
<br>
    <table cellpadding="6" cellspacing="0" border="0" width="100%" class="tableview">
    <tr><th align="left"><b>Jars Not Found</b></th></tr>   
        
        <% 
            row = "";
            for (int i=0; i<jarsNotFound.size(); i++) { 
                 if (row.equals("row2")) row = "";
                else row = "row2"; 
        %>
        
         <tr><td class="<%=row%>"><%=(String) jarsNotFound.get(i)%></td></tr>
        <%
             }  
     %>
       </table>
</td></tr>

<tr><td>
<br>
    <table cellpadding="6" cellspacing="0" border="0" width="100%" class="tableview">
    <tr><th align="left"><b>Phones Not Found</b></th></tr>
        
        <% 
            row = "";
            for (int i=0; i<phonesNotFound.size(); i++) { 
                 if (row.equals("row2")) row = "";
                else row = "row2"; 
        %>
        
         <tr><td class="<%=row%>"><%=(String) phonesNotFound.get(i)%></td></tr>
        <%
             }  
     %>
       </table>
</td></tr>


</table>
<br><br><br>
</form>


</body>
</html>




