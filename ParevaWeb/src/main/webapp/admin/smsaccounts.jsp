<%@include file="/WEB-INF/jspf/admin/smsaccounts.jspf"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="/lib/global_anyx.js" language="javascript"></script>
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


<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td valign="top" align="left">
    
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
        <tr>
            <td align="left" valign="bottom" class="big_blue">SMS Accounts</td>
            <td align="right" class="status_red"><b><%=statusMsg%></b></td>
        </tr>
    </table>
    
</tr></td>   
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

    <table cellpadding="6" cellspacing="0" border="0" width="100%">
    <tr>
        <td class="grey_11">Name: <input type="text" size="30" name="name" value="<%=name%>"></td>
    <td class="grey_11">Domain: 
    <select name="dmid">
        <option value="">[Select]</option>                
    <% 

    for (int i=0; i<dmlist.size(); i++) {
        sdcd = dmlist.get(i);

    %>               
        <option value="<%=sdcd.getUnique()%>" <% if (dmid.equals(sdcd.getUnique())){%> selected <%}%>><%=sdcd.getName()%></option>               
    <% } %>
    </select>
    </td>
    <td class="grey_11">Country: 
    <select name="lng">
        <option value="">[Select]</option>                
              
        <option value="fi" <% if (lang.equals("fi")){%> selected <%}%>>Finland</option> 
        <option value="ug" <% if (lang.equals("ug")){%> selected <%}%>>Uganda</option> 
        <option value="ke" <% if (lang.equals("ke")){%> selected <%}%>>Kenya</option> 
        <option value="sa" <% if (lang.equals("sa")){%> selected <%}%>>Saudi-Arabia</option> 
        <option value="it" <% if (lang.equals("it")){%> selected <%}%>>Italy</option> 

    </select>
    </td>
    <td class="grey_11">Def SMS Number: <input type="text" size="10" name="sms" value="<%=sms%>"></td>
    <td align="right"><input type="submit" name="add" value="&nbsp;ADD NEW&nbsp;"></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>

<tr><td>        
        
    <br>
    <table cellpadding="4" cellspacing="0" border="0" width="100%" class="dotted">

        <tr>

          <th align="left">SMS Account</th>
          <th align="center">Domain</th>
          <th align="center">Country</th>  
          <th align="center">SMS Number</th> 
          <th align="right">Select</th>  
           
       </tr>
<%
    for (Iterator it = map.keySet().iterator(); it.hasNext();) {
        key = (String) it.next();
        value = map.get(key);
        System.out.println("KEY: " + key);
        System.out.println(umesdc.getDomainMap());
        
        dmName = ""; lngCode = ""; smsNumber = "";
        
        if (key.indexOf("_sms")>-1) continue;
        
        if (!key.startsWith("default"))  {
            
            if (key.indexOf("_")>-1) {
                if (umesdc.getDomainMap().get(key.substring(0,key.indexOf("_")))==null) continue;
                dmName = umesdc.getDomainMap().get(key.substring(0,key.indexOf("_"))).getName();
                lngCode = key.substring(key.indexOf("_")+1);
            }
            else if (key.length()>10) {
                if (umesdc.getDomainMap().get(key)==null) continue;
                dmName = umesdc.getDomainMap().get(key).getName();
                lngCode = "All Countries";
            }
            else {
                dmName = "All Domains";
                lngCode = key;
            }
            
            if (umesdc.getSmsAccountMap().get(value + "_sms")!=null) smsNumber = umesdc.getSmsAccountMap().get(value + "_sms");
        }
        else { 
            key = "<b>" + key + "</b>";
            dmName = key;
            lngCode = "";
        }           
%>
        
    <tr>

          <td align="left"><%=value%></td>
          <td align="center"><%=dmName%></td>
          <td align="center"><%=lngCode%></td>  
          <td align="center"><%=smsNumber%></td> 
          <td align="right">
              <% if (!key.startsWith("default"))  { %>
              <input type="checkbox" value="sel_<%=key%>">
              <% } %>
          </td>
       </tr>

<%
   }
%>
       
   </table>
   
</td></tr>
</table>

</form>

</body>
</html>
