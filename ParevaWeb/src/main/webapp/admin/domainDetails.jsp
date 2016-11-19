<%@include file="/WEB-INF/jspf/admin/domainDetails.jspf"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">

<script language="JavaScript"> 
//hide this stuff from other browsers 

function useDefault(val) {
    if (val == true) {
        document.thisForm.aDefPage.disabled=1;
        document.thisForm.aDefHost.disabled=1;              
        document.thisForm.aDefIp.disabled=1;
        document.thisForm.aDefPort.disabled=1;      
        document.thisForm.aTimeout.disabled=1;
    }
    else {
        document.thisForm.aDefPage.disabled=0;
        document.thisForm.aDefHost.disabled=0;
        document.thisForm.aDefIp.disabled=0;
        document.thisForm.aDefPort.disabled=0;      
        document.thisForm.aTimeout.disabled=0;
    }   
}

// end the hiding comment --> 
</script> 

</head>
<body>

<div class="alertHolder"></div> 
<div class="statusMsg" style="visibility: hidden; position: absolute;"><%=statusMsg%></div>

    <h3 class="page-title">
        Domain Details
    </h3>

    <button data-url='domains.jsp' type="button" class="btn btn-outline sbold uppercase jsLoad">Back to domains</button>

    <form   method="post" action="admin/domainDetails.jsp" name="thisForm"
            id="domainDetaisForm" role="form" class="form-horizontal">

    <input type="hidden" name="dmid" value="<%=dmid%>">
    <!--<input type="hidden" name="contenturl" value="cf04032015.mmvideos.co.uk">-->
    <div class="row">
        <div class="form-body col-md-6">
            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="Unique"><%=lp.get(8)%></label>
                <div class="col-md-8">
                    <input type="text" readonly value="<%=sdcd.getUnique()%>" class="form-control" id="Unique">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="Unique"><%=lp.get(9)%></label>
                <div class="col-md-8">
                    <input type="text" readonly value="<%=sdcd.getModified()%>" class="form-control" >
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="Unique"><%=lp.get(10)%></label>
                <div class="col-md-8">
                    <input type="text" readonly value="<%=sdcd.getCreated()%>" class="form-control" >
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aActive">Active</label>
                    <div class="md-checkbox-list">
                        <div class="md-checkbox col-md-8">
                            <input type="checkbox"  name="aActive" id="aActive" class="md-check" value='1' 
                                <% if (sdcd.getActive()==1){%> checked <%}%>
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
                <label class="col-md-4 control-label" for="aName"><%=lp.get(12)%></label>
                <div class="col-md-8">
                    <input type="text" name="aName" value="<%=sdcd.getName()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aKey">Key:</label>
                <div class="col-md-8">
                    <input type="text" name="aKey" value="<%=sdcd.getKey()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aDefUrl"><%=lp.get(13)%></label>
                <div class="col-md-8">
                    <input type="text" name="aDefUrl" value="<%=sdcd.getDefaultUrl()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aAliasUrl1">Alias Url 1:</label>
                <div class="col-md-8">
                    <input type="text" name="aAliasUrl1" value="<%=sdcd.getAliasUrl1()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aAliasUrl2">Alias Url 2:</label>
                <div class="col-md-8">
                    <input type="text" name="aAliasUrl2" value="<%=sdcd.getAliasUrl2()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aAliasUrlList">Alias Url List <br/>(in addition to the above two, one per line):</label>
                <div class="col-md-8">
                    <textarea name="aAliasUrlList" class="form-control" rows="4"><%=sdcd.getaAliasUrlList()%></textarea>
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="contenturl">Content URL:</label>
                <div class="col-md-8">
                    <input type="text" name="contenturl" value="<%=sdcd.getContentUrl()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aPartnerDomain">Partner Domain:</label>
                <div class="col-md-8">
                    <select name="aPartnerDomain" class="form-control">
                        <option value="" selected >Select Partner Domain</option>
                        <% for (int i=0; i<anyxsdc.getDomainList().size(); i++) {
                                dd = anyxsdc.getDomainList().get(i);
                                if (dd==sdcd) continue;
                        %>
                            <option value="<%=dd.getUnique()%>" 
                                <% if (dd.getUnique().equals(sdcd.getPartnerDomain())){%> 
                                selected 
                                <%}%>><%=dd.getName()%>
                            </option>
                        <% } %>
                    </select>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="redirecturl">Redirect URL: </label>
                <div class="col-md-8">
                    <input type="text" name="redirecturl" value="<%=sdcd.getRedirectUrl()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                    <span class="help-block">[Third Party Service, leave blank if served by UME]</span>
                </div>
            </div>
            
            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aDefLang"><%=lp.get(14)%></label>
                <div class="col-md-8">
                    <select name="aDefLang" class="form-control">
                        <% for (int i=0; i<anyxsdc.getLanguageList().size(); i++) {
                            sdclang = anyxsdc.getLanguageList().get(i);
                        %>
                            <option value="<%=sdclang.getLanguageCode()%>" 
                                <% if (sdcd.getDefaultLang().equals(sdclang.getLanguageCode())) {%> 
                                selected 
                                <%}%>><%=sdclang.getLanguageName()%>&nbsp;&nbsp;&nbsp;
                            </option>
                        <% } %>
                    </select>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aUseHomeDir">Use Home Directory:</label>
                    <div class="md-checkbox-list">
                        <div class="md-checkbox col-md-8">
                            <input type="checkbox" name="aUseHomeDir" id="aUseHomeDir" class="md-check" value='1' 
                                <% if (sdcd.getUseHomeDir()==1){%> checked <%}%>
                            />
                            <label for="aUseHomeDir">
                                <span></span>
                                <span class="check"></span>
                                <span class="box"></span> 
                            </label>
                        </div>
                    </div>
            </div>
        
            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aDefPublicDir">Default Public Directory:</label>
                <div class="col-md-8">
                    <input type="text" name="aDefPublicDir" value="<%=sdcd.getDefPublicDir()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aDefPublicPage">Default Public Page:</label>
                <div class="col-md-8">
                    <input type="text" name="aDefPublicPage" value="<%=sdcd.getDefPublicPage()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>            

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aDefPrivateDir">Default Private Directory:</label>
                <div class="col-md-8">
                    <input type="text" name="aDefPrivateDir" value="<%=sdcd.getDefPrivateDir()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>        

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aDefPrivatePage">Default Private Page:</label>
                <div class="col-md-8">
                    <input type="text" name="aDefPrivatePage" value="<%=sdcd.getDefPrivatePage()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>        

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aDefNumber"><%=lp.get(15)%></label>
                <div class="col-md-8">
                    <input type="text" name="aDefNumber" value="<%=sdcd.getDefSmsNumber()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aDefSmsSrvc"><%=lp.get(16)%></label>
                <div class="col-md-8">
                    <input type="text" name="aDefSmsSrvc" value="<%=sdcd.getDefSmsSrvc()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>        

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aSmsErrorSrvc"><%=lp.get(17)%></label>
                <div class="col-md-8">
                    <input type="text" name="aSmsErrorSrvc" value="<%=sdcd.getSmsErrorSrvc()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>        
 
            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aWapIps">Use WAP Authentication:</label>
                    <div class="md-checkbox-list">
                        <div class="md-checkbox col-md-8">
                            <input type="checkbox" name="aWapIps" id="aWapIps" class="md-check" value='1' 
                                <% if (sdcd.getWapIps().equals("1")){%> 
                                checked 
                                <%}%>
                            />
                            <label for="aWapIps">
                                <span></span>
                                <span class="check"></span>
                                <span class="box"></span> 
                            </label>
                        </div>
                    </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aMsisdnHeaders">
                    Msisdn Headers: <br />(each on separate line)</label>
                <div class="col-md-8">
                    <textarea name="aMsisdnHeaders" class="form-control" rows="5"><%=sdcd.getMsisdnHeaders()%></textarea>
                    <div class="form-control-focus"> </div>
                </div>
            </div>       

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aInitialRedirect">Initial Redirect Url</label>
                <div class="col-md-8">
                    <input type="text" name="aInitialRedirect" value="<%=sdcd.getaInitialRedirectUrl()%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>
        </div>
            <!--
            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="templateLocation"><%=lp.get(32)%></label>
                <div class="col-md-8">
                    <input type="text" name="templateLocation" value="<%= templateLocation%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>
        
            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="templateSuffix"><%=lp.get(33)%></label>
                <div class="col-md-8">
                    <input type="text" name="templateSuffix" value="<%= templateSuffix%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="templateStaticFolder"><%=lp.get(34)%></label>
                <div class="col-md-8">
                    <input type="text" name="templateStaticFolder" value="<%= templateStaticFolder%>" class="form-control">
                    <div class="form-control-focus"> </div>
                </div>
            </div>

            <div class="form-group form-md-line-input">
                <label class="col-md-4 control-label" for="aDescription"><%=lp.get(24)%></label>
                <div class="col-md-8">
                    <textarea name="aDescription" class="form-control" rows="3"><%=sdcd.getDescription()%></textarea>
                    <div class="form-control-focus"> </div>
                </div>
            </div>
        
-->
        <!-- end first column start second column............................................ -->

        <div class="form-body col-md-6">
            <div class="portlet light bordered">
                <div class="portlet-title">
                    <div class="caption font-red-sunglo">
                        <i class="icon-check font-red-sunglo"></i>
                        <span class="caption-subject bold uppercase">Default stuff</span>
                    </div>
                    <div class="actions col-md-8">
                       <div class="form-group form-md-line-input">
                            <label class="col-md-1 control-label" for="aDefValues"></label>
                                <div class="md-checkbox-list" onClick="useDefault(this.form.aDefValues.checked);">
                                    <div class="md-checkbox col-md-12">
                                        <input type="checkbox"  name="aDefValues" id="aDefValues" class="md-check" value='1' 
                                            <%if (sdcd.getUseDefault()==1){%> 
                                            checked 
                                            <%}%>
                                        >
                                        <label for="aDefValues">
                                            <span></span>
                                            <span class="check"></span>
                                            <span class="box"></span> Use default values 
                                        </label>
                                    </div>
                                </div>
                        </div>
                    </div>
                </div>
                <div class="portlet-body form">
                    <div class="form-body">

                        
                        <div class="form-group form-md-line-input">
                            <label class="col-md-4 control-label" for="aDefPage"><%=lp.get(19)%></label>
                            <div class="col-md-8">
                                <input type="text" 
                                name="aDefPage"
                                <%if (sdcd.getUseDefault()==1){%> readonly <%}%> 
                                value="<%=sdcd.getDefaultPage()%>" class="form-control" id="aDefPage">
                                <div class="form-control-focus"> </div>
                            </div>
                        </div>

                        <div class="form-group form-md-line-input">
                            <label class="col-md-4 control-label" for="aDefHost"><%=lp.get(20)%></label>
                            <div class="col-md-8">
                                <input type="text" 
                                name="aDefHost"
                                <%if (sdcd.getUseDefault()==1){%> readonly <%}%> 
                                value="<%=sdcd.getDefaultHost()%>" class="form-control" id="aDefHost">
                                <div class="form-control-focus"> </div>
                            </div>
                        </div>

                        <div class="form-group form-md-line-input">
                            <label class="col-md-4 control-label" for="aDefIp"><%=lp.get(21)%></label>
                            <div class="col-md-8">
                                <input type="text" 
                                name="aDefIp"
                                <%if (sdcd.getUseDefault()==1){%> readonly <%}%> 
                                value="<%=sdcd.getDefaultIp()%>" class="form-control" id="aDefIp">
                                <div class="form-control-focus"> </div>
                            </div>
                        </div>

                        <div class="form-group form-md-line-input">
                            <label class="col-md-4 control-label" for="aDefPort"><%=lp.get(22)%></label>
                            <div class="col-md-8">
                                <input type="text" 
                                name="aDefPort"
                                <%if (sdcd.getUseDefault()==1){%> readonly <%}%> 
                                value="<%=sdcd.getDefaultPort()%>" class="form-control" id="aDefPort">
                                <div class="form-control-focus"> </div>
                            </div>
                        </div>

                        <div class="form-group form-md-line-input">
                            <label class="col-md-4 control-label" for="aTimeout"><%=lp.get(23)%></label>
                            <div class="col-md-8">
                                <input type="text" 
                                name="aTimeout"
                                <%if (sdcd.getUseDefault()==1){%> readonly <%}%> 
                                value="<%=sdcd.getReadTimeout()%>" class="form-control" id="aTimeout">
                                <div class="form-control-focus"> </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        
            <div class="portlet portlet box red">
                <div class="portlet-title">
                    <div class="caption ">
                        <i class="icon-check "></i>
                        <span class="caption-subject bold uppercase"><%=lp.get(25)%></span>
                    </div>
                    <div class="tools">
                        Madan's area, don't touch
                        <a href="javascript:;" class="expand" data-original-title="" title=""> </a>
                    </div>
                </div>
                <div class="portlet-body form portlet-collapsed">
                    <div class="form-body">
                        <%
                        boolean selected=false;
                        boolean serviceInPackage = false;

                        for (int i=0; i<anyxsdc.getPackageList().size(); i++) {
                            sdcpack = anyxsdc.getPackageList().get(i);

                                selected=false;
                            
                            for (int k=0; k<sdcd.getPackageList().size(); k++) {
                                    dmpack = sdcd.getPackageList().get(k);
                                    if (dmpack.getUnique().equals(sdcpack.getUnique())) { selected=true; break; }
                            }
                        %>
                        <div class="form-group form-md-line-input">
                            <label class="col-md-4 control-label" for="pack_<%=sdcpack.getUnique()%>">
                                <%=sdcpack.getName()%>
                            </label>
                                <div class="md-checkbox-list">
                                    <div class="md-checkbox col-md-8">
                                        <input type="checkbox"  
                                                name="pack_<%=sdcpack.getUnique()%>" 
                                                id="pack_<%=sdcpack.getUnique()%>" class="md-check" value='1' 
                                                <% if (selected) { %> checked <%}%>
                                        >
                                        <label for="pack_<%=sdcpack.getUnique()%>">
                                            <span></span>
                                            <span class="check"></span>
                                            <span class="box"></span> 
                                            <% if (sdcpack.getActive()==1) {%>Active<%} else {%>Disabled<%}%>
                                        </label>
                                    </div>
                                </div>
                        </div>

                        <%
                            for (int k=0; k<anyxsdc.getServiceList().size(); k++) {
                                    sdcservice = anyxsdc.getServiceList().get(k);

                                    if (sdcservice.getPackageList()==null) continue;

                                    serviceInPackage = false;

                                    for (int t=0; t<sdcservice.getPackageList().size(); t++) {
                                        if (sdcservice.getPackageList().get(t).getUnique().equals(sdcpack.getUnique())) {
                                            serviceInPackage = true;
                                            break;
                                        }
                                    }

                                    if (!serviceInPackage) continue;

                        %>
                                
                                <tr bgcolor="#EEEEEE">
                                <td>&nbsp;</td>
                                <td class="blue_10">&nbsp;&nbsp;&nbsp;<%=sdcservice.getName()%></td>
                                <td class="grey_10">Sec Level: <%=sdcservice.getSecLevel()%></td>
                                <td class="grey_10"><% if (sdcservice.getActive()==1) {%><%=lp.get(26)%><%} else {%><%=lp.get(27)%><%}%></td>
                                </tr>       
                            <%  
                            }
                        }

                        %>      


                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-offset-2 col-md-10">
            <input type="submit" id="submit" name="save" value="&nbsp;&nbsp;<%=lp.get(28)%>&nbsp;&nbsp;" class="btn blue">
        </div>
    </div>
    
</form>

<script>
    $( "form" ).submit(function(ev) {
        var action = '&save=Save';
        var frm = $(this);
        var data = frm.serialize()+action;
        var el = $(this).closest(".row").children(".portlet-body");
        $("#submit").attr("value", "Senging data, please wait...");
        App.blockUI({
            target: el,
            animate: true,
            overlayColor: 'black',
            message: 'Please Wait, we are either pricessing or the server is out of memory. Again. James?'
            }); 
        $.ajax({
            type: frm.attr('method'),
            url: frm.attr('action'),
            data: data,
            success: function (data) {
                $("#submit").attr("value", "Save");
                App.unblockUI(el);
                var parsed  = $.parseHTML(data);
                var success = $(parsed).filter(".statusMsg").text();
                var portlet = $(parsed).find(".row");
                alertInfo(success);
                $(".row").html(portlet);
            }
        });
        ev.preventDefault();
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
</script>

</body>
</html>