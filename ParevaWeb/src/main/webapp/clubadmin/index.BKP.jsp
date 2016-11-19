<%@include file="/WEB-INF/jspf/clubadmin/index.jspf"%>

<html> 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">

<%--@include file="design.jsp"--%>
<link href="/static/AdminPanel/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="/static/AdminPanel/assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
<!-- END GLOBAL MANDATORY STYLES -->
<!-- BEGIN PAGE LEVEL PLUGINS -->
<link href="/static/AdminPanel/assets/global/plugins/datatables/datatables.min.css" rel="stylesheet" type="text/css" />
<link href="/static/AdminPanel/assets/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN THEME GLOBAL STYLES -->
<link href="/static/AdminPanel/assets/global/css/components-md.min.css" rel="stylesheet" id="style_components" type="text/css" />
<link href="/static/AdminPanel/assets/global/css/plugins-md.min.css" rel="stylesheet" type="text/css" />
<!-- END THEME GLOBAL STYLES -->
<style>
    .flag {
        width: 16px;
    }
    body {
        background: white;
    }
</style>
</head>
<body class="page-header-fixed page-sidebar-closed-hide-logo page-content-white page-md">

<%--@include file="../umeadmin/defMenus.jsp" --%>



<form method="post" action="<%=fileName%>.jsp" id="mainForm">



<div class="row">
    
    <div class="col-md-12">
        <div class="alertHolder">

        </div>
        <!-- BEGIN EXAMPLE TABLE PORTLET-->
        <div class="portlet light bordered">
            <div class="portlet-title">
                <div class="caption font-dark">
                    <i class="icon-settings font-dark"></i>
                    <span class="caption-subject bold uppercase">
                        <%if (regionid.equals("")){%>
                            All Mobile Clubs
                        <%} else { %>
                            <%=regionid %> Mobile Clubs
                        <%} %>
                    </span>
                </div>
            </div>
            <div class="portlet-body">
                <div class="table-toolbar">
                    <div class="row">
                        <div class="col-md-6 col-sm-6">
                                <div class="input-group">
                                    <span class="input-group-btn">
                                        <button type="submit" name="add" value="Add" class="btn sbold green">
                                            Add New 
                                            <i class="fa fa-plus"></i>
                                        </button>
                                    </span>
                                    <input  type="text" 
                                        class="form-control" 
                                        name="addName" 
                                        value="" 
                                        size="30" 
                                        placeholder="Name of the new club (e.g. Angel's club)" />
                                </div>
                        </div>
                        <div class="col-md-6">
                            <div class="btn-group pull-right">
                                <button class="btn green  btn-outline dropdown-toggle" data-toggle="dropdown">
                                    Select Region: <%=regionid %>
                                    <i class="fa fa-angle-down"></i>
                                </button>
                                <ul class="dropdown-menu pull-right countryHolder">
                                    <li>
                                        <a  href="javascript:;" 
                                            class="js_countrySelect" 
                                            data-country=" ">
                                            <img class="flag" src="/static/AdminPanel/assets/global/img/flags/wd.png"/>
                                            ALL 
                                        </a>
                                    </li>
                                <% for (int i=0; i<regionlist.size(); i++) { %>
                                    <li>
                                        <a  href="javascript:;" 
                                            class="js_countrySelect" 
                                            data-country="<%=regionlist.get(i)%>">
<img class="flag" src="/static/AdminPanel/assets/global/img/flags/<%=regionlist.get(i).toString().toLowerCase()%>.png"/>
                                            <%=regionlist.get(i)%> 
                                        </a>
                                    </li>
                                <%}%>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                <table class="table table-striped table-bordered table-hover table-checkable order-column" id="sample_1"> 
                    <thead>
                        <tr>
                            <th> Status </th>
                            <th> Private Name </th>
                            <th> Country </th>
                            <th> Public Name </th>
                            <th> Wap Domain </th>
                        </tr>
                    </thead>
                    <tbody>

                        <% for (int i=0; i<list.size(); i++) {
                            item = list.get(i);
                            umedomain = umesdc.getDomainMap().get(item.getWapDomain());
                        %>
                            <tr class="odd gradeX">
                                <td> 
                                    <% if (item.getActive() == 1 ) { %>
                                        <span class="label label-sm label-success"> 
                                            <%=item.getActive()%> 
                                        </span>
                                    <% } else { %>
                                        <span class="label label-sm label-default"> 
                                            <%=item.getActive()%> 
                                        </span>
                                    <% } %>
                                </td>

                                <td> 
                                    <a href="clubDetails.jsp?unq=<%=item.getUnique()%>">
                                        <%=item.getName()%>
                                    </a> 
                                    [<%=item.getUnique()%>]
                                </td>

                                <td>
<% if(item.getRegion().length()>0)  { %>
<img class="flag" src="/static/AdminPanel/assets/global/img/flags/<%=item.getRegion().toString().toLowerCase()%>.png"/>
<% } %>                                    
                                    <%=item.getRegion()%>
                                </td>

                                <td>
                                    <%=item.getClubName()%>

                                </td>
                                <td>
                                    <% if (umedomain!=null) {%><%=umedomain.getDefaultUrl()%><%}%>
                                </td>
                                
                            </tr>
                       <% } %>

                    </tbody>
                </table>
            </div>
        </div>
        <!-- END EXAMPLE TABLE PORTLET-->
    </div>
</div>

<select name="regionid" id="region" style="visibility: hidden">
    <option value=" " selected>Select Region</option>
</select> 
</form>

 <!-- BEGIN CORE PLUGINS -->
<script src="/static/AdminPanel/assets/global/plugins/jquery.min.js" type="text/javascript"></script>
<script src="/static/AdminPanel/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<!-- END CORE PLUGINS -->
<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="/static/AdminPanel/assets/global/scripts/datatable.js" type="text/javascript"></script>
<script src="/static/AdminPanel/assets/global/plugins/datatables/datatables.min.js" type="text/javascript"></script>
<script src="/static/AdminPanel/assets/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN THEME GLOBAL SCRIPTS -->
<script src="/static/AdminPanel/assets/global/scripts/app.min.js" type="text/javascript"></script>
<!-- END THEME GLOBAL SCRIPTS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="/static/AdminPanel/assets/pages/scripts/table-datatables-managed.min.js" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->

<script>
$( document ).ready(function() {
    var country;
    $(".js_countrySelect").on("click", function() {
        if (country != $(this).data("country")) {
            country = $(this).data("country");
            $("#region option").val(country);
            $("#mainForm").submit();
        }
    });

    <% if(statusMsg.length()>0)  { %>
        App.alert({ 
            container   : ".alertHolder",   // alerts parent container
            place       : 'append',         // append or prepent in container
            type        : 'info',         // alert's type
            message     : '<%=statusMsg%>', // alert's message
            close       : true,             // make alert closable
            reset       : false,            // close all previouse alerts first
            icon        : 'fa fa-user',    // icon 
            closeInSeconds: 5,           // auto close after defined seconds 
            focus       : true              // auto scroll to the alert after shown 

            // TODO eventually - if Madan sends me the type of status Message can make it success, fail, info etc.
        });
    <% } %>

});

</script>
</body>
</html>

<%--  
$('option:selected', 'select[name="options"]').removeAttr('selected');
//Using the value
$('select[name="options"]').find('option[value="3"]').attr("selected",true);
//Using the text
$('select[name="options"]').find('option:contains("Blue")').attr("selected",true);
    ====== OLD CODE removed by Angel - 2/2/2016


            <tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
            <tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
            <tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
 
            <tr><td>
                    <table cellspacing="0" cellpadding="5" border="0" class="tableview" width="100%">
                        <tr>
                            <th align="left">Private Name</th>
                            <th align="left">Public Name</th>
                            <th align="center">Opt-In</th>
                            <th align="center">Wap Domain</th>
                            <th align="right">Active</th>
                        </tr>


                        <% for (int i=0; i<list.size(); i++) {
                            item = list.get(i);
                            umedomain = umesdc.getDomainMap().get(item.getWapDomain());
                        %>

                            <tr>
                                <td align="left"><a href="clubDetails.jsp?unq=<%=item.getUnique()%>"><%=item.getName()%></a> [<%=item.getUnique()%>]</td>
                                <td align="left"><%=item.getClubName()%></td>
                                <td align="center"><%=item.getOptIn()%></td>
                                <td align="center">
                                    <% if (umedomain!=null) {%><%=umedomain.getDefaultUrl()%><%}%>
                                </td>
                                <td align="right"><%=item.getActive()%></td>
                            </tr>

                       <% } %>

                    </table>
            </td></tr>

 --%>