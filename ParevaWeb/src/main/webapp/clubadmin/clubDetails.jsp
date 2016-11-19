<%@include file="/WEB-INF/jspf/clubadmin/clubDetails.jspf"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
</head>
<body>
<div class="alertHolder"></div> 
<%-- int _curitem = Integer.parseInt(aReq.get("_curitem", "0")); %>
<%@ include file="/clubadmin/tabs.jsp" --%>

<h3 class="page-title">
    Mobile Club Details - <%=item.getName()%>
</h3>

<button data-urlNo='clubDetails.jsp?unq=<%=unq%>' type="button" class="btn dark">Properties</button>
<button data-url='msgs.jsp?unq=<%=unq%>' type="button" class="btn btn-outline sbold uppercase jsUrlLoad">Messages</button>
<button data-url='index.jsp' type="button" class="btn btn-outline sbold uppercase jsUrlLoad">Back to all clubs</button>

<div class="statusMsg"><%=statusMsg%></div>

<form id="clubDetaisForm" role="form" class="form-horizontal" method="get"  style=""
        action="../clubadmin/<%=fileName%>.jsp?unq=<%=unq%>">
    <input type="hidden" name="unq" value="<%=unq%>" >
    <div class="form-body col-md-6">
        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="Unique">Unique</label>
            <div class="col-md-8">
                <input type="text" readonly value="<%=unq%>" class="form-control" id="Unique">
                <div class="form-control-focus"> </div>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aActive">Active</label>
                <div class="md-checkbox-list">
                    <div class="md-checkbox col-md-8">
                        <input type="checkbox"  name="aActive" id="aActive" class="md-check" value='1' 
                            <% if (item.getActive()==1){%> checked <%}%>
                        >
                        <label for="aActive">
                            <span></span>
                            <span class="check"></span>
                            <span class="box"></span> Is it ACTIVE? 
                        </label>
                    </div>
                </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aName">Private Name</label>
            <div class="col-md-8">
                <input type="text" name="aName" size="30" value="<%=item.getName()%>" class="form-control">
                <div class="form-control-focus"> </div>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="form_control_1">Region</label>
            <div class="col-md-8">
                <select class="form-control" name="aRegion">
                    <option value="">[Select Region]</option>
                    <option value="ZA" <% if (item.getRegion().equals("ZA")){%> selected <%}%>>
                        <img class="flag" src="/static/AdminPanel/assets/global/img/flags/za.png"/>
                        South Africa</option>
                    <option value="KE" <% if (item.getRegion().equals("KE")){%> selected <%}%>>
                        Kenya</option>
                    <option value="UK" <% if (item.getRegion().equals("UK")){%> selected <%}%>>
                        UK</option>
                    <option value="IT" <% if (item.getRegion().equals("IT")){%> selected <%}%>>
                        <img class="flag" src="/static/AdminPanel/assets/global/img/flags/it.png"/>
                        Italy</option> 
                    <option value="ES" <% if (item.getRegion().equals("ES")){%> selected <%}%>>
                        Spain</option>
                    <option value="DE" <% if (item.getRegion().equals("DE")){%> selected <%}%>>
                        Germany</option>
                    <option value="RU" <% if (item.getRegion().equals("RU")){%> selected <%}%>>
                        Russia</option>
                    <option value="FR" <% if (item.getRegion().equals("FR")){%> selected <%}%>>
                        France</option>
                    <option value="MX" <% if (item.getRegion().equals("MX")){%> selected <%}%>>
                        Mexico</option>
                    <option value="ZI" <% if (item.getRegion().equals("ZI")){%> selected <%}%>>
                        Zimbabwe</option>
                    <option value="IN" <% if (item.getRegion().equals("IN")){%> selected <%}%>>
                        India</option>
                    <option value="AU" <% if (item.getRegion().equals("AU")){%> selected <%}%>>
                        Australia</option>
                    <option value="IE"<% if (item.getRegion().equals("IE")){%> selected <%}%>>
                        Ireland</option>
                    
                    <option value="PT"<% if (item.getRegion().equals("PT")){%> selected <%}%>>
                        Portugal</option>
                    
                    <option value="NO"<% if (item.getRegion().equals("NO")){%> selected <%}%>>
                        Norway</option>
                    <option value="CH"<% if (item.getRegion().equals("CH")){%> selected <%}%>>
                        Switzerland</option>
                     <option value="SE"<% if (item.getRegion().equals("SE")){%> selected <%}%>>
                        Sweden</option>
                </select>
                <div class="form-control-focus"> </div>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aSmsNumber">SMS Number</label>
            <div class="col-md-8">
                <input type="text" name="aSmsNumber" value="<%=item.getSmsNumber()%>" class="form-control" id="aSmsNumber"
                    <% if(item.getSmsNumber()!=null && item.getSmsNumber().trim().length()>0) { %>
                        readonly 
                    <% } %>
                >
                <div class="form-control-focus"> </div>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aSmsExt">SMS Extension / Campaign ID</label>
            <div class="col-md-8">
                <input type="text" name="aSmsExt" value="<%=item.getSmsExt()%>" class="form-control" id="aSmsExt"
                    <% if(item.getSmsExt()!=null && item.getSmsExt().trim().length()>0) { %>
                        readonly 
                    <% } %>
                >
                <div class="form-control-focus"> </div>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aKeyword">Keyword</label>
            <div class="col-md-8">
                <input type="text" name="aKeyword" value="<%=kw.toUpperCase()%>" class="form-control" id="aKeyword" >
                <div class="form-control-focus"> </div>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aClubName">Public Name</label>
            <div class="col-md-8">
                <input type="text" name="aClubName" value="<%=item.getClubName()%>" class="form-control" id="aClubName" >
                <div class="form-control-focus"> </div>
            </div>
        </div>
                
                

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aWapDomain">WAP Domain</label>
            <div class="col-md-8">
                <select class="form-control" name="aWapDomain">
                    <option value="select">[Select]</option>
                        <%
                        for (int i=0; i<umesdc.getDomainList().size(); i++) {
                            umedomain = umesdc.getDomainList().get(i);
                            if (umedomain.getActive()==0) continue;
                        %>
                            <option value="<%=umedomain.getUnique()%>" 
                                <% if (item.getWapDomain().equals(umedomain.getUnique())) { %> selected <%}%>
                            >
                            <%=umedomain.getName()%>
                            </option>
                        <%}%>
                </select>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aWebDomain">WEB Domain</label>
            <div class="col-md-8">
                <select class="form-control" name="aWebDomain">
                   <option value="select">[Select]</option>
                    <%
                    for (int i=0; i<umesdc.getDomainList().size(); i++) {
                        umedomain = umesdc.getDomainList().get(i);
                        if (umedomain.getActive()==0) continue;
                    %>
                        <option value="<%=umedomain.getUnique()%>" 
                            <% if (item.getWebDomain().equals(umedomain.getUnique())) { %> selected <%}%>
                        >
                            <%=umedomain.getName()%>
                        </option>
                    <%}%>
                </select>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aPrice">End User Price</label>
            <div class="col-md-8">
                <div class="input-group">
                    <div class="input-group-control">
                        <input type="text" class="form-control" placeholder="Price" name="aPrice" size="4" value="<%=item.getPrice()%>" style="width: 130px;" />
                        <select name="aCurrency" class="form-control" style="width: 130px;">
                            <option value="ZAR" <% if (item.getCurrency().equals("ZAR")){%> selected <%}%>>RAND (R)</option>
                            <option value="KES" <% if (item.getCurrency().equals("KES")){%> selected <%}%>>SHILLING (KSh)</option>
                            <option value="EUR" <% if (item.getCurrency().equals("EUR")){%> selected <%}%>>EUR (&euro;)</option>
                            <option value="USD" <% if (item.getCurrency().equals("USD")){%> selected <%}%>>USD (&#36;)</option>
                            <option value="GBP" <% if (item.getCurrency().equals("GBP")){%> selected <%}%>>GBP (&pound;)</option>
                            <option value="RS" <% if (item.getCurrency().equals("RS")){%> selected <%}%>>Rupees (RS)</option>
                            <option value="AUD" <% if (item.getCurrency().equals("AUD")){%> selected <%}%>>AUD (&#36;)</option>
                            <option value="NOK" <% if (item.getCurrency().equals("NOK")){%> selected <%}%>>NOK</option>
                            <option value="SEK" <% if (item.getCurrency().equals("SEK")){%> selected <%}%>>SEK</option>
                        </select>
                        <div class="form-control-focus"> </div>
                    </div>
                        
                </div>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="classification">Classification</label>
            <div class="col-md-8">
                <select class="form-control" name="classification">
                <option value="">Select Classification</option>
                    <% for(int i=0;i<classificationList.size();i++) { %>
                    <option value="<%=classificationList.get(i)%>" <%if (item.getClassification().equals(classificationList.get(i))){%> selected <%}%>>&nbsp;&nbsp;<%=classificationList.get(i)%></option>
    
                    <%} %>
                </select>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="frequency">Frequency per day</label>
            <div class="col-md-8">
                <input type="text" name="frequency" value="<%=frequency%>" class="form-control" id="frequency" >
                <div class="form-control-focus"> </div>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="frequency">Free Day</label>
            <div class="col-md-8">
                <input type="text" name="freeday" value="<%=freeday%>" class="form-control" id="freeday" >
                <div class="form-control-focus"> </div>
            </div>
        </div>
                
            <%if(item.getRegion().equalsIgnoreCase("UK") || item.getRegion().equalsIgnoreCase("IE")){%>
          <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="messagespoof">Message Spoof</label>
            <div class="col-md-8">
                <input type="text" name="messagespoof" size="30" value="<%=messagespoof%>" class="form-control">
                <div class="form-control-focus"> </div>
            </div>
        </div>
                <%}%>
                
                <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="companycode">Company Code</label>
            <div class="col-md-8">
                <input type="text" name="companycode" size="30" value="<%=companycode%>" class="form-control">
                <div class="form-control-focus"> </div>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aType">Club Type</label>
            <div class="col-md-8">
                <select name="aType" class="form-control">                 
                    <option value="credit" <% if (item.getType().equals("credit")){%> selected <%}%>>Credits</option>
                    <option value="perday" <% if (item.getType().equals("perday")){%> selected <%}%>>Item per Day</option>
                    <option value="subscription" <% if (item.getType().equals("subscription")){%> selected <%}%>>Subscription</option>
                    <option value="competition" <% if (item.getType().equals("competition")){%> selected <%}%>>Competition</option>
                    <option value="ppd" <% if (item.getType().equals("ppd")){%> selected <%}%>>PPD</option>
                    <option value="admin" <% if (item.getType().equals("admin")){%> selected <%}%>>Admin</option>
                    <option value="api" <% if (item.getType().equals("api")){%> selected <%}%>>API</option>
                </select>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aType">Opt In</label>
            <div class="col-md-8">
                <select name="aOptIn" class="form-control">
                    <option value="0" <% if (item.getOptIn()==0){%> selected <%}%>>None</option>
                    <option value="1" <% if (item.getOptIn()==1){%> selected <%}%>>Single</option>
                    <option value="2" <% if (item.getOptIn()==2){%> selected <%}%>>Double</option>
                </select>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aCreditAmount">Credit Amount</label>
            <div class="col-md-8">
                <input type="text" name="aCreditAmount" value="<%=item.getCreditAmount()%>" class="form-control" id="freeday" >
                <div class="form-control-focus"> </div>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aKeepOldCredits">Keep Old Credits</label>
                <div class="md-checkbox-list">
                    <div class="md-checkbox col-md-8">
                        <input type="checkbox"  name="aKeepOldCredits" id="aKeepOldCredits" class="md-check" value='1' 
                            <% if (item.getKeepOldCredits()==1){%> checked <%}%>
                        >
                        <label for="aKeepOldCredits">
                            <span></span>
                            <span class="check"></span>
                            <span class="box"></span> 
                        </label>
                    </div>
                </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aType">Billing Type</label>
            <div class="col-md-8">
                <select name="billingType" class="form-control">
                    <option value="" selected >Select Billing Type</option>
                    <option value="Subscription" 
                        <% if (clubdetails.getBillingType().equals("Subscription")){%> selected <%}%>>
                        Subscription
                    </option>
                    <option value="Adhoc" 
                        <% if (clubdetails.getBillingType().equals("Adhoc")){%> selected <%}%>>
                        Adhoc
                    </option>
                </select>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aPeriod">Billing Period</label>
            <div class="col-md-8">
                <div class="input-group right-addon">
                    <input type="text" name="aPeriod" value="<%=item.getPeriod()%>" class="form-control" id="aPeriod" >
                    <span class="input-group-addon">day(s)</span>
                </div>
                <div class="form-control-focus"> </div>
            </div>
        </div>


        <%  if(item.getRegion().equalsIgnoreCase("it")){ %>

        <div class="row">
            <label class="col-md-4 control-label" for="aOtpSoneraId">Italy Service Settings</label>
            <div class="col-md-4">
                <div class="form-group form-md-line-input form-md-floating-label">
                    <div class="input-icon">
                        <input name="aOtpSoneraId" size="20" value="<%=item.getOtpSoneraId()%>" class="form-control">
                        <label for="form_control_1">Account</label>
                        <!--<span class="help-block">Username for Italy</span>-->
                        <i class="fa fa-user"></i>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="form-group form-md-line-input form-md-floating-label">
                    <div class="input-icon">
                        <input name="aOtpTelefiId" size="20" value="<%=item.getOtpTelefiId()%>" class="form-control">
                        <label for="form_control_1">Password</label>
                        <!--<span class="help-block">Password for Italy's account</span>-->
                        <i class="fa fa-key"></i>
                    </div>
                </div>
            </div>
        </div>

        <%}%>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="aSmsNumber">
                <%  if(item.getRegion().equalsIgnoreCase("uk")){ %>
                Service ID
                <%} else if(item.getRegion().equalsIgnoreCase("ie")) { %>
                Ekey
                <%} else { %>
                OTP Service Name (GUID)
                <%} %>
            </label>
            <div class="col-md-8">
                <input name="aOtpServiceName" size="40" value="<%=item.getOtpServiceName()%>" class="form-control" 
                <% if(item!=null && item.getOtpServiceName()!=null && !((item.getOtpServiceName().trim().length())<=0)){%>
                        readonly 
                <% } %>
                >
                <div class="form-control-focus"> </div>
            </div>
        </div>

       <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="primaryServiceType">Primary Service</label>
            <div class="col-md-8">
                <select name="primaryServiceType" class="form-control" >
                    <option value="" >Select Service Type</option>
                    <option value="Content" 
                        <% if (clubdetails.getServiceType().equals("Content")){%> selected <%}%>>
                        Content
                    </option>
                    <option value="Competition" 
                        <% if (clubdetails.getServiceType().equals("Competition")){%> selected <%}%>>
                        Competition
                    </option>
                    <option value="Mobiplanet" 
                        <% if (clubdetails.getServiceType().equals("Mobiplanet")){%> selected <%}%>>
                        Mobiplanet
                    </option>
                </select>
            </div>
        </div>
        
         <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="secondaryServiceType">Secondary Service</label>
            <div class="col-md-8">
                <select name="secondaryServiceType" class="form-control" >
                    <option value="" >Select Service Type</option>
                    <option value="Content" 
                        <% if (activeSecondaryService.contains("Content")){%> selected <%}%>>
                        Content
                    </option>
                    <option value="Competition" 
                        <% if (activeSecondaryService.contains("Competition")){%> selected <%}%>>
                        Competition
                    </option>
                    <option value="Mobiplanet" 
                        <% if (activeSecondaryService.contains("Mobiplanet")){%> selected <%}%>>
                        Mobiplanet
                    </option>
                </select>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="paymentType">Payment Type</label>
            <div class="col-md-8" class="form-control" >
                <select name="paymentType" class="form-control">
                    <option value="" >
                        Payment Type
                    </option>
                    <option value="PSMS" <% if (clubdetails.getPaymentType().equals("PSMS")){%> selected <%}%>>
                        PSMS
                    </option>
                    <option value="DCB" <% if (clubdetails.getPaymentType().equals("DCB")){%> selected <%}%>>
                        DCB
                    </option>
                    <option value="EXTERNAL" <% if (clubdetails.getPaymentType().equals("EXTERNAL")){%> selected <%}%>>
                        EXTERNAL
                    </option>
                </select>
            </div>
        </div>

        <div class="form-group form-md-line-input">
            <label class="col-md-4 control-label" for="servedBy">Served By</label>
            <div class="col-md-8" class="form-control" >
                <select id="servedBy" name="servedBy" class="form-control">
                    <option value="" >Select Served By</option>
                    <option value="UME" <% if (clubdetails.getServedBy().equals("UME")){%> selected <%}%>>UME</option>
                    <option value="ThirdParty" <% if (clubdetails.getServedBy().equals("ThirdParty")){%> selected <%}%>>Third Party</option>
                </select>
            </div>
        </div>

    </div>

    <div class="form-actions">
        <div class="row">
            <div class="col-md-offset-2 col-md-8">
                <button type="button" class="btn default">Cancel</button>
                <input id="submit" type="submit" name="save" value="Save" class="btn blue">
                <input type="hidden" name="save" value="Save">
            </div>
        </div>
    </div>

</form>



<script>

$( document ).ready(function() {
    $('form').submit(function(event) {
        $("#submit").attr("value", "Sending data, please wait...");
        var $form = $(this);
        $.ajax({
            url : $form.attr('action'),
            type: "post",
            data: $form.serialize()
        }).done(function(data) {
            $("#submit").attr("value", "Save");
            var success =  $($.parseHTML(data)).filter(".statusMsg").text(); 
            if ($("select#servedBy").val()=="ThirdParty") {
                success += " <br /> <a class='jsLoad' href='../admin/domainDetails.jsp?dmid=<%=item.getWapDomain()%>'>Please CLICK HERE for the domains admin page and update the redirect URL there. </a> <br />Thank you! "
            } 
            alertInfo(success);
        });
        event.preventDefault();
    });

    function alertInfo(alertText) {
        App.alert({ 
            container   : ".alertHolder",   // alerts parent container
            place       : 'append',         // append or prepent in container
            type        : 'info',         // alert's type
            message     : alertText, // alert's message
            close       : true,             // make alert closable
            reset       : true,            // close all previouse alerts first
            icon        : 'fa fa-info',    // icon 
            closeInSeconds: 120,           // auto close after defined seconds 
            focus       : true              // auto scroll to the alert after shown 

            // TODO eventually - if Madan sends me the type of status Message can make it success, fail, info etc.
        });
    }

    $(".contentHolder ").on("click", ".jsLoad", function(e) {
        if ($(this).data("url")) {
            var url = $(this).data("url");
        } else {
            var url = $(this).attr("href");
        }
        
        $('.contentHolder').load("../clubadmin/" + url);
        e.preventDefault();
    });

    $(".jsUrlLoad").on("click", function(e) {
        var url = $(this).data("url");
        $('.contentHolder').html("");
        $('.contentHolder').load("../clubadmin/" + url);
      });



});
</script>
</body>
</html>
