<%@include file="/WEB-INF/jspf/clubadmin/clubDetails.jspf"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
    <link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

    <form method="post" action="<%=fileName%>.jsp?unq=<%=unq%>">
    <input type="hidden" name="unq" value="<%=unq%>">

<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td valign="top" align="left">
            <table cellspacing="0" cellpadding="5" border="0" width="100%">
                <tr>
                    <td align="left" valign="bottom" class="blue_14"><b>Mobile Club Details</b></td>
                    <td align="right" valign="bottom" class="red_11"><nobr><%=statusMsg%></nobr></td>
                </tr>
            </table>
</td></tr>

<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", "0")); %>
    <%@ include file="/clubadmin/tabs.jsp" %>
    <br>
</td></tr>
<tr><td><img src="/images/glass_dot.gif" height="20" width="1"></td></tr>
<tr><td>


        <table cellspacing="0" cellpadding="5" border="0" width="100%">
            
        <tr> 
            <td align="left" class="grey_11">Unique:</td>
            <td width="60%" align="left" class="grey_11"><%=unq%></td>
        </tr>
        <tr>
            <td align="left" class="grey_11">Active:</td>
            <td align="left" class="grey_11"><input type="checkbox" name="aActive" value="1" <% if (item.getActive()==1){%> checked <%}%>></td>
        </tr>
        <tr> 
            <td align="left" class="grey_11">Private Name:</td>
            <%
            if(item.getName()!=null && item.getName().trim().length()>0)
            {
             %>
            <td align="left" class="grey_11"><input type="text" name="aName" size="30" value="<%=item.getName()%>" class="textbox"></td>
            <%} else{ %>   
            <td align="left" class="grey_11"><input type="text" name="aName" size="30" value="<%=item.getName()%>" class="textbox"></td>
            <%}%>
        </tr>

        <tr>
            <td align="left" class="grey_11">Region: </td>
            <td align="left" class="grey_11">
                <select name="aRegion">
                    <option value="">[Select Region]</option>
                    <option value="ZA" <% if (item.getRegion().equals("ZA")){%> selected <%}%>>South Africa</option>
                    <option value="KE" <% if (item.getRegion().equals("KE")){%> selected <%}%>>Kenya</option>
                    <option value="UK" <% if (item.getRegion().equals("UK")){%> selected <%}%>>UK</option>
                    <option value="IT" <% if (item.getRegion().equals("IT")){%> selected <%}%>>Italy</option>
                    <option value="ES" <% if (item.getRegion().equals("ES")){%> selected <%}%>>Spain</option>
                    <option value="DE" <% if (item.getRegion().equals("DE")){%> selected <%}%>>Germany</option>
		      <option value="RU" <% if (item.getRegion().equals("RU")){%> selected <%}%>>Russia</option>
		 	<option value="FR" <% if (item.getRegion().equals("FR")){%> selected <%}%>>France</option>
			<option value="MX" <% if (item.getRegion().equals("MX")){%> selected <%}%>>Mexico</option>
			<option value="ZI" <% if (item.getRegion().equals("ZI")){%> selected <%}%>>Zimbabwe</option>
                        <option value="IN" <% if (item.getRegion().equals("IN")){%> selected <%}%>>India</option>
                        <option value="AU" <% if (item.getRegion().equals("AU")){%> selected <%}%>>Australia</option>
                        <option value="IRE" <% if (item.getRegion().equals("IRE")){%> selected <%}%>>Ireland</option>
                </select>
            </td>
        </tr>
        <tr>
            <td align="left" class="grey_11">SMS Number:</td>
            <%
            if(item.getSmsNumber()!=null && item.getSmsNumber().trim().length()>0)
            {
             %>
             <td align="left" class="grey_11" name="aSmsNumber"><%=item.getSmsNumber()%></td>   
            
        <%} else{ %>
            <td align="left" class="grey_11"><input type="text" name="aSmsNumber" size="10" value="<%=item.getSmsNumber()%>" class="textbox"></td> 
        <%}%>
        </tr>
        
        <tr>
           
            <td align="left" class="grey_11">SMS Extension / Campaign ID:</td>
              <%
            if(item.getSmsExt()!=null && item.getSmsExt().trim().length()>0)
            {
             %>
            <td align="left" class="grey_11"><%=item.getSmsExt()%>
            
            <%} else{ %>
             <td align="left" class="grey_11"><input type="text" name="aSmsExt" size="10" value="<%=item.getSmsExt()%>" class="textbox"></td>
        <%}%>
        </tr>
         <tr> 
            <td align="left" class="grey_11">Keyword:</td>
            <td align="left" class="grey_11"><input type="text" name="aKeyword" size="15" value="<%=kw.toUpperCase()%>" class="textbox"></td>
        </tr>
        
        <tr> 
            <td align="left" class="grey_11">Public Name:</td>
            <td align="left" class="grey_11"><input type="text" name="aClubName" size="30" value="<%=item.getClubName()%>" class="textbox"></td>
        </tr>

        <tr> 
            <td align="left" class="grey_11">WAP Domain:</td>
            <td align="left" class="grey_11">
                <select name="aWapDomain">
                        <option value="">[Select]</option>
                        <%
                        for (int i=0; i<umesdc.getDomainList().size(); i++) {
                                umedomain = umesdc.getDomainList().get(i);
                                if (umedomain.getActive()==0) continue;
                        %>
                                <option value="<%=umedomain.getUnique()%>" <% if (item.getWapDomain().equals(umedomain.getUnique())) { %> selected <%}%>>
                                    <%=umedomain.getName()%>&nbsp;&nbsp;</option>
                        <%}%>
                </select>
            </td>
        </tr>     
        <tr> 
            <td align="left" class="grey_11">WEB Domain:</td>
            <td align="left" class="grey_11">
                <select name="aWebDomain">
                        <option value="">[Select]</option>
                        <%
                        for (int i=0; i<umesdc.getDomainList().size(); i++) {
                                umedomain = umesdc.getDomainList().get(i);
                                if (umedomain.getActive()==0) continue;
                        %>
                                <option value="<%=umedomain.getUnique()%>" <% if (item.getWebDomain().equals(umedomain.getUnique())) { %> selected <%}%>>
                                    <%=umedomain.getName()%>&nbsp;&nbsp;</option>
                        <%}%>
                </select>
                
            </td>
        </tr>
                
        <tr> 
            <td align="left" class="grey_11">End User Price: </td>
            <td align="left" class="grey_11">
                <input type="text" name="aPrice" size="4" value="<%=item.getPrice()%>" class="textbox">&nbsp;
                <select name="aCurrency">
                    <option value="ZAR" <% if (item.getCurrency().equals("ZAR")){%> selected <%}%>>RAND (R)</option>
                    <option value="KES" <% if (item.getCurrency().equals("KES")){%> selected <%}%>>SHILLING (KSh)</option>
                    <option value="EUR" <% if (item.getCurrency().equals("EUR")){%> selected <%}%>>EUR (&euro;)</option>
                    <option value="USD" <% if (item.getCurrency().equals("USD")){%> selected <%}%>>USD (&#36;)</option>
                    <option value="GBP" <% if (item.getCurrency().equals("GBP")){%> selected <%}%>>GBP (&pound;)</option>
                    <option value="RS" <% if (item.getCurrency().equals("RS")){%> selected <%}%>>Rupees (RS)</option>
                    <option value="AUD" <% if (item.getCurrency().equals("AUD")){%> selected <%}%>>AUD (&#36;)</option>
                </select>
            </td>
        </tr>
        
               <tr> 
            <td align="left" class="grey_11">Classification: </td>
            <td align="left" class="grey_11">
            	<select class="textbox" name="classification">
            	<option value="">Select Classification</option>
					<%for(int i=0;i<classificationList.size();i++) {
						
					%>
            		<option value="<%=classificationList.get(i)%>" <%if (item.getClassification().equals(classificationList.get(i))){%> selected <%}%>>&nbsp;&nbsp;<%=classificationList.get(i)%></option>
	
            		<%} %>
				</select>
            
            
            </td>
        </tr>
        
        <tr> 
            <td align="left" class="grey_11">Frequency per day: </td>
            <%-- <%if(frequency!=null && frequency.trim().length()>0 && !frequency.trim().equalsIgnoreCase("")) {%>
            <td align="left" class="grey_11"><%=frequency%>
            
            <%} else {%>
           --%>
            <td align="left" class="grey_11"><input type="text" name="frequency" size="4" value="<%=frequency%>" class="textbox"></td>
            <%-- <%}%> --%>
        </tr>
        
           <tr> 
            <td align="left" class="grey_11">Free Day: </td>
            <%-- <%if(freeday!=null && freeday.trim().length()>0) {%>
            <td align="left" class="grey_11"><%=freeday%>
            
            <%} else {%>
           --%>
            <td align="left" class="grey_11"><input type="text" name="freeday" size="4" value="<%=freeday%>" class="textbox"></td>
            <%-- <%}%> --%>
        </tr>
        
        <tr> 
            <td align="left" class="grey_11">Club Type:</td>
            <td align="left" class="grey_11">
                <select name="aType">                 
                    <option value="credit" <% if (item.getType().equals("credit")){%> selected <%}%>>Credits</option>
                    <option value="perday" <% if (item.getType().equals("perday")){%> selected <%}%>>Item per Day</option>
                    <option value="subscription" <% if (item.getType().equals("subscription")){%> selected <%}%>>Subscription</option>
                    <option value="competition" <% if (item.getType().equals("competition")){%> selected <%}%>>Competition</option>
                    <option value="ppd" <% if (item.getType().equals("ppd")){%> selected <%}%>>PPD</option>
                    <option value="admin" <% if (item.getType().equals("admin")){%> selected <%}%>>Admin</option>
                    <option value="api" <% if (item.getType().equals("api")){%> selected <%}%>>API</option>
                </select>
            </td>
        </tr>
        <tr>
            <td align="left" class="grey_11">Opt In:</td>
            <td align="left" class="grey_11">
                <select name="aOptIn">
                    <option value="0" <% if (item.getOptIn()==0){%> selected <%}%>>None</option>
                    <option value="1" <% if (item.getOptIn()==1){%> selected <%}%>>Single</option>
                    <option value="2" <% if (item.getOptIn()==2){%> selected <%}%>>Double</option>
                </select>
            </td>
        </tr>
        <!--
        <tr>
            <td align="left" class="grey_11">AQM Apo:</td>
            <td align="left" class="grey_11"><input type="checkbox" name="aAqmAbo" value="1" <% if (item.getAqmAbo()==1){%> checked <%}%>></td>
        </tr>
        -->
        <tr> 
            <td align="left" class="grey_11">Credit Amount: </td>
            <td align="left" class="grey_11"><input type="text" name="aCreditAmount" size="4" value="<%=item.getCreditAmount()%>" class="textbox"></td>
        </tr>
        <tr>
            <td align="left" class="grey_11">Keep Old Credits:</td>
            <td align="left" class="grey_11"><input type="checkbox" name="aKeepOldCredits" value="1" <% if (item.getKeepOldCredits()==1){%> checked <%}%>></td>
        </tr>
        
        <tr>
            <td align="left" class="grey_11">Billing Type:</td>
            <td align="left" class="grey_11">
                <select name="billingType">
                    <option value="" selected >Select Billing Type</option>
                    <option value="Subscription" <% if (clubdetails.getBillingType().equals("Subscription")){%> selected <%}%>>Subscription</option>
                    
                    <option value="Adhoc" <% if (clubdetails.getBillingType().equals("Adhoc")){%> selected <%}%>>Adhoc</option>
                </select>
            </td>
        </tr>
        
        <tr> 
            <td align="left" class="grey_11">Billing Period:</td>
            <td align="left" class="grey_11"><input type="text" name="aPeriod" size="2" value="<%=item.getPeriod()%>" class="textbox"> day(s).</td>
        </tr>
 
        <%
        if(item.getRegion().equalsIgnoreCase("it")){
         %>
        <tr> 
            <td align="left" class="grey_11">Italy Service Settings: </td>
            <td align="left" class="grey_11">
                <nobr> Account:
                    <input type="text" name="aOtpSoneraId" size="20" value="<%=item.getOtpSoneraId()%>" class="textbox">
                    &nbsp;&nbsp;
                    Password:
                    <input type="text" name="aOtpTelefiId" size="20" value="<%=item.getOtpTelefiId()%>" class="textbox">
                </nobr>
            </td>
        </tr>
        <%}%>
         <tr> 
          
                  <td align="left" class="grey_11">OTP Service Name (GUID): </td>
            <%if(item!=null && item.getOtpServiceName()!=null && !((item.getOtpServiceName().trim().length())<=0)){%>
            <td align="left" class="grey_11"><%=item.getOtpServiceName()%></td>
            <%}else{%>
            <td align="left" class="grey_11"><input type="text" name="aOtpServiceName" size="40" value="<%=item.getOtpServiceName()%>" class="textbox"></td>
        <%}%>

		</tr>
       
       <tr>
            <td align="left" class="grey_11">Service Type:</td>
            <td align="left" class="grey_11">
                <select name="serviceType">
                    <option value="" selected >Select Service Type</option>
                    <option value="Content" <% if (clubdetails.getServiceType().equals("Content")){%> selected <%}%>>Content</option>
                    
                    <option value="Competition" <% if (clubdetails.getServiceType().equals("Competition")){%> selected <%}%>>Competition</option>
                </select>
            </td>
        </tr>
        
        
        <tr>
            <td align="left" class="grey_11">Payment Type:</td>
            <td align="left" class="grey_11">
                <select name="paymentType">
                    <option value="" selected >Select Payment Type</option>
                    <option value="PSMS" <% if (clubdetails.getPaymentType().equals("PSMS")){%> selected <%}%>>PSMS</option>
                    
                    <option value="DCB" <% if (clubdetails.getPaymentType().equals("DCB")){%> selected <%}%>>DCB</option>
                    <option value="EXTERNAL" <% if (clubdetails.getPaymentType().equals("EXTERNAL")){%> selected <%}%>>EXTERNAL</option>
                </select>
            </td>
        </tr>
        
        <tr>
            <td align="left" class="grey_11">Served By:</td>
            <td align="left" class="grey_11">
                <select name="servedBy">
                    <option value="" selected >Select Served By</option>
                    <option value="UME" <% if (clubdetails.getServedBy().equals("UME")){%> selected <%}%>>UME</option>
                    
                    <option value="ThirdParty" <% if (clubdetails.getServedBy().equals("ThirdParty")){%> selected <%}%>>Third Party</option>
                </select>
            </td>
        </tr>
        
        </table>
            </td></tr>
            
            <tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
            <tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
            <tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
            <tr><td align="right"><input type="submit" name="save" value="Save" style="width:150px;"></td></tr>
            
        </table>

    </form>
</body>
</html>
