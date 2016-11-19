<%@include file="/WEB-INF/jspf/clubadmin/index.jspf"%>
<html> 
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1" name="viewport" />
    <!-- END GLOBAL MANDATORY STYLES -->
    <!-- BEGIN PAGE LEVEL PLUGINS 
    <link href="/static/AdminPanel/assets/global/plugins/datatables/datatables.min.css" rel="stylesheet" type="text/css" />
    <link href="/static/AdminPanel/assets/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />-->
    <!-- END PAGE LEVEL PLUGINS -->

    <style>
    .flag {
        width: 16px;
    }
    .dataTables_filter {
        float: right;
    }

    </style>
</head>
<body>
<div class="alertHolder"></div> 
<div class="clubAdminHolder">

        <h3 class="page-title">
            <%if (regionid.equals("")){%>
                All Mobile Clubs
            <%} else { %>
                <%=regionid %> Mobile Clubs
            <%} %>
            <small><span id="statusMsg" style="visibility: hidden"><%=statusMsg%></span></small>
        </h3>

        <form method="post" action="../clubadmin/<%=fileName%>.jsp" id="mainForm">
            <select name="regionid" id="region" style="visibility: hidden; position: absolute;" >
                <option value=" " selected>Select Region</option>
            </select> 
        </form>
            <div class="portlet-body updateArea">
                <div class="table-toolbar">
                    <div class="row">
                        <div class="col-md-6 col-sm-6">
                            <div class="input-group">
                                <span class="input-group-btn">
                                    <button type="submit" name="add" value="Add" class="btn sbold green" id="newClub">
                                        Add New 
                                        <i class="fa fa-plus"></i>
                                    </button>
                                </span>
                                <input  type="text" 
                                class="form-control" 
                                name="addName" 
                                id="addName"
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
    
        <table class="table table-striped table-bordered table-hover table-checkable order-column" id="clubAdminTable"> 
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
                        <a href="javascript:;" 
                            data-url="clubDetails.jsp?unq=<%=item.getUnique()%>"
                            class="jsLoad">
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
        <!-- END EXAMPLE TABLE PORTLET-->
</div>

<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="/static/AdminPanel/assets/global/scripts/datatable.js" type="text/javascript"></script>
<script src="/static/AdminPanel/assets/global/plugins/datatables/datatables.min.js" type="text/javascript"></script>
<script src="/static/AdminPanel/assets/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->

<script>


$( document ).ready(function() {
// Table prototype
    var TableDatatablesManaged = function () {
        var initTable1 = function () {
            var table = $('#clubAdminTable');
            // begin first table
            table.dataTable({
                // Internationalisation. For more info refer to http://datatables.net/manual/i18n
                "language": {
                    "aria": {
                        "sortAscending": ": activate to sort column ascending",
                        "sortDescending": ": activate to sort column descending"
                    },
                    "emptyTable": "No data available in table",
                    "info": "Showing _START_ to _END_ of _TOTAL_ clubs",
                    "infoEmpty": "No records found",
                    "infoFiltered": "(filtered1 from _MAX_ total clubs)",
                    "lengthMenu": "Show _MENU_",
                    "search": "Search:",
                    "zeroRecords": "No matching clubs found",
                    "paginate": {
                        "previous":"Prev",
                        "next": "Next",
                        "last": "Last",
                        "first": "First"
                    }
                },

                // Or you can use remote translation file
                //"language": {
                //   url: '//cdn.datatables.net/plug-ins/3cfcc339e89/i18n/Portuguese.json'
                //},

                // Uncomment below line("dom" parameter) to fix the dropdown overflow issue in the datatable cells. The default datatable layout
                // setup uses scrollable div(table-scrollable) with overflow:auto to enable vertical scroll(see: assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js). 
                // So when dropdowns used the scrollable div should be removed. 
                //"dom": "<'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r>t<'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>",

                "bStateSave": true, // save datatable state(pagination, sort, etc) in cookie.

                "columnDefs": [ {
                    "targets": 0,
                    "orderable": false,
                    "searchable": false
                }],

                "lengthMenu": [
                    [5, 15, 20, -1],
                    [5, 15, 20, "All"] // change per page values here
                ],
                // set the initial value
                "pageLength": 20,            
                "pagingType": "bootstrap_full_number",
                "columnDefs": [{  // set default column settings
                    'orderable': true,
                    'targets': [0]
                }, {
                    "searchable": false,
                    "targets": [0]
                }],
                "order": [
                    [1, "asc"]
                ] // set first column as a default sort by asc
            });

            var tableWrapper = jQuery('#clubAdminTable_wrapper');
        }

        return {
            //main function to initiate the module
            init: function () {
                if (!jQuery().dataTable) {
                    return;
                }
                initTable1();
            }
        };
    }();

    TableDatatablesManaged.init();

    var country;
    $(".js_countrySelect").on("click", function() {
        if (country != $(this).data("country")) {
            country = $(this).data("country");
            $("#region option").val(country);
            //$("#mainForm").submit();
            getNewData();
        }
    });

    $("#newClub").on("click", function() {
        var newClub = $("#addName").val();
        if (newClub.length > 0 ) {
            $.post( $("#mainForm").attr('action'), { add: "Add", addName: newClub, regionid: '<%=regionid %>' })
              .done(function( data ) {
                var temp = $(data).find('#statusMsg');
                var alertMsg = temp.text();
                if  (alertMsg.length < 1 ) {
                     alertMsg = newClub + 'was added';
                }
                App.alert({ 
                    container   : ".alertHolder",   // alerts parent container
                    message     : alertMsg , // alert's message
                    closeInSeconds: 1000,           // auto close after defined seconds 
                    focus       : false,
                    });
                getNewData();
              });
          } else {
            App.alert({ 
                container   : ".alertHolder",   // alerts parent container
                message     : 'Name Field Empty' , // alert's message
                closeInSeconds: 5,           // auto close after defined seconds 
                focus       : false,
                type        : 'danger',
                });
          }
        
    });

    function getNewData() {
        $.ajax({
            url : $("#mainForm").attr('action'),
            type: "post",
            async: true,
            data: $("#mainForm").serialize(),
            success: function (data) {
                var temp = $(data).find('.contentHolder');
                //console.log(temp);
                $(".contentHolder").html(data);
                //TableDatatablesManaged.init();
            },
            error: function (jXHR, textStatus, errorThrown) {
                alert(errorThrown);
            }
        });
    }
    /*
    $('#mainForm').on('submit', function(e) {
        e.preventDefault();
        $.ajax({
            url : $(this).attr('action'),
            type: "post",
            async: true,
            data: $(this).serialize(),
            success: function (data) {
                var temp = $(data).find('#clubAdminTable');
                //console.log(temp);
                $(".contentHolder").html(data);
                //TableDatatablesManaged.init();
            },
            error: function (jXHR, textStatus, errorThrown) {
                alert(errorThrown);
            }
        });
    });*/
    var statusMsg = '<%=statusMsg%>';
    <% if((statusMsg).length()>0)  { %>
        App.alert({ 
            container   : ".alertHolder",   // alerts parent container
            place       : 'append',         // append or prepent in container
            type        : 'info',         // alert's type
            message     : statusMsg, // alert's message
            close       : true,             // make alert closable
            reset       : false,            // close all previouse alerts first
            icon        : 'fa fa-user',    // icon 
            closeInSeconds: 5,           // auto close after defined seconds 
            focus       : false              // auto scroll to the alert after shown 

            // TODO eventually - if Madan sends me the type of status Message can make it success, fail, info etc.
        });
        <% } %>

    $(".jsLoad").on("click", function(e) {
        var url = $(this).data("url");
        $('.contentHolder').load("../clubadmin/" + url);
    });

    });

</script>
</body>
</html>