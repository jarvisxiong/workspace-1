<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Question Pack</title>

    <style>
	    .flag {
	        width: 16px;
	    }
	    .dataTables_filter {
	        float: right;
	    }

	    table.dataTable td.sorting_1, table.dataTable td.sorting_2, table.dataTable td.sorting_3, table.dataTable th.sorting_1, table.dataTable th.sorting_2, table.dataTable th.sorting_3 {
	        background: transparent !important;
	    }

	    table { white-space: nowrap; }

    </style>
</head>
<body>

	
<jsp:include page="/QuestionPack"/>

<div class="alertHolder"></div> 

    <div class="col-md-12">
        <!-- BEGIN EXAMPLE TABLE PORTLET-->
        <div class="portlet light bordered">
            <div class="portlet-title">
                <div class="caption font-dark">
                    <i class="icon-settings font-dark"></i>
                    <span class="caption-subject bold uppercase"> Question Packs</span>
                </div>
                <!--<div class="actions">
                    <div class="btn-group btn-group-devided" data-toggle="buttons">
                        <label class="btn btn-transparent dark btn-outline btn-circle btn-sm active js_loadURL" data-url="../questionupload/index.jsp">
                            <input type="radio" name="options" class="toggle" id="option1" >
                            	Upload New Question Pack
                        </label>
                        <label class="btn btn-transparent dark btn-outline btn-circle btn-sm">
                            <input type="radio" name="options" class="toggle" id="option2">Settings</label>
                    </div>
                </div>-->
            </div>
            <div class="portlet-body">
                <div class="table-toolbar">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="btn-group">
                                <button id="sample_editable_1_new" class="btn sbold green js_loadURL" data-url="../questionpack/edit.jsp?action=Create"> 
                                	Create New Question Pack
                                    <i class="fa fa-plus"></i>
                                </button>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="btn-group pull-right">
                                <button class="btn green  btn-outline dropdown-toggle" data-toggle="dropdown">
                                    Select Language
                                    <i class="fa fa-angle-down"></i>
                                </button>
                                <ul class="dropdown-menu pull-right countryHolder">
                                    <li>
                                        <a  href="javascript:;" 
                                        class="js_countrySelect" 
                                        data-country=" ">
                                        <img class="flag" src="/lib/templates/static/AdminPanel/assets/global/img/flags/wd.png"/>
                                            ALL 
                                        </a>
                                    </li>
                                    
                                  <c:forEach items="${languageList}" var="language">
                                  
                                    <li>
                                        <a  href="javascript:;" 
                                        class="js_countrySelect" 
                                        data-country="${language}">
                                        <img class="flag" src="http://admin.pareva.umelimited.com/static/AdminPanel/assets/global/img/flags/${language.toLowerCase()}.png"/>
                                        ${language}
	                                    </a>
	                                </li>
                                  
                                  </c:forEach>
                                  
                                  
	                                
	                            </ul>
	                        </div>
	                    </div>
                    </div>
                </div>
                <div id="sample_1_wrapper" class="dataTables_wrapper no-footer">

	                <table class="table table-striped table-bordered table-hover table-checkable order-column" id="questionPackTable" >
			            
			            <thead>
			                <tr>
			                    <th style="width: 150px;"> Question Pack ID </th>
			                    <th> Name </th>
			                    <th> Language </th>
			                    <th> Created </th>
			                    <th> Edit </th>
			                </tr>
			            </thead>

	                    <tbody>
		       				<c:forEach items="${questionPackList}" var="questionPack">
						        <tr class="gradeX" role="row"> 
									<td>
										<c:out value="${questionPack.aUnique}" />
									</td>
									<td>
										<c:out value="${questionPack.aName}" />
									</td>
									<td>
										<img class="flag" 
										src="http://admin.pareva.umelimited.com/static/AdminPanel/assets/global/img/flags/<c:out value="${questionPack.aLanguage.toLowerCase()}" />.png"/> 
										<c:out value="${questionPack.aLanguage}" />
									</td>
									<td>
									<fmt:formatDate value="${questionPack.aCreated}" pattern="yyyy-MM-dd HH:mm:ss"/>
										<%-- <c:out value="${questionPack.aCreated}" /> --%>
									</td>
									<td>
										<a class="btn btn-xs green js_loadURL" data-url="../questionpack/edit.jsp?action=Update&unique=${questionPack.aUnique}">
                                            <i class="fa fa-cogs"></i> 
                                            Edit
                                        </a>
									</td>
						        </tr>
						    </c:forEach>
	                    </tbody>

	                </table>

	            </div>
            </div>
           
        </div>
        <!-- END EXAMPLE TABLE PORTLET-->
    </div>

<%-- <table>
<tr>
<th>QuestionPackId</th>
<th>QuestionPackName</th>
<th>QuestionPackLanguage</th>
<th>Created Date</th>
</tr>

<c:forEach items="${questionPackList}" var="questionPack">
        <tr>
          <td><c:out value="${questionPack.aUnique}" /><td>
          <td><c:out value="${questionPack.aName}" /><td>
          <td><c:out value="${questionPack.aLanguage}" /><td>
          <td><c:out value="${questionPack.aCreated}" /><td>
        </tr>
      </c:forEach>
</table> --%>

<%-- <table>
<tr>
<th>QuestionPackId</th>
<th>QuestionPackName</th>
<th>QuestionPackLanguage</th>
<th>Created Date</th>
</tr>

<c:forEach items="${questionPackList}" var="questionPack">
        <tr>
          <td><c:out value="${questionPack.questionPackId}" /><td>
          <td><c:out value="${questionPack.questionPackName}" /><td>
          <td><c:out value="${questionPack.questionPackLanguage}" /><td>
          <td><c:out value="${questionPack.aCreated}" /><td>
        </tr>
      </c:forEach>
</table>
<br/><br/> 
<form name="questionPack" method="post" action="index.jsp">
 <input type="hidden" name="action" value="create"/> 
<input type="hidden" name="action" value=""/>
QuestionPack Name: <input type="text" name="questionPackName" value=""/><br/>
QuestionPack Language: <select name="questionPackLanguage">
<option value="">Select Language</option>
<option value="EN">English</option>
<option value="FR">French</option>
<option value="ES">Spanish</option>
</select>
<br/>

Search By Question: <input type="text" name="searchByQuestionPackName" value=""/>
QuestionPack Language: <select name="filterByQuestionPackLanguage">
<option value="">Select Language</option>
<option value="EN">English</option>
<option value="FR">French</option>
<option value="ES">Spanish</option>
<input type="submit" value="save">
</form>--%>


<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="/lib/templates/static/AdminPanel/assets/global/scripts/datatable.js" type="text/javascript"></script>
<script src="/lib/templates/static/AdminPanel/assets/global/plugins/datatables/datatables.min.js" type="text/javascript"></script>
<script src="/lib/templates/static/AdminPanel/assets/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->


<script type="text/javascript">
$( document ).ready(function() {

    var contentHolder = $('body').find('.contentHolder');

    function loadPageEmbed(pageUrl) {
        var myHtml = '<object data="'+ pageUrl +'" >' 
                +'  <embed src="'+ pageUrl + '"></embed>' 
                +'    Error: Embedded data could not be displayed.'
                +'    </object>'
     contentHolder.html(myHtml);
     //contentHolder.removeClass("loadingWrapper");
    //$('.contentHolder embed').attr("src", pageUrl).hide().show();
    }

    function loadPageIframe(pageUrl) {http://www.w3schools.com
        var myHtml = '<iframe width="100%" height="1500px;" style="border: 0;" src="'+ pageUrl +'"></iframe>';
              
        contentHolder.html(myHtml); 
    //$('.contentHolder embed').attr("src", pageUrl).hide().show();
    }

    function loadPageJQ(pageUrl) {
        contentHolder.load( pageUrl, function() {
            $(".loadingWrapper").removeClass("active");
        });
    }

    $(".nav-item a, .js_loadURL").off('click').on("click", function() {
        var url = $(this).data("url");
        console.log(url);

        if (url.indexOf("wapsiteadmin") >= 0) {
            loadPageIframe(url);
            console.log('1');
            stop = 1;
        } else if (url.indexOf("clubadmin") >= 0 || url.indexOf("domains") >= 0 || url.indexOf("questionpack") >= 0) {
            var loadJQ = true;
            stop = 0;
        } else {
            var loadJQ = false;
            stop = 0;
        }

        if (url.length > 0  ) {
            if (stop == 1) {
                console.log('return')
                return;
            } else if (loadJQ) {
                //$(".page-content-wrapper").addClass("loadingWrapper").delay( 5000 ).addClass("orDont").delay( 5000 ).addClass("whatever");
                loadPageJQ(url);

                $("body").find(".loadingWrapper").addClass("active");
               // $(".page-content-wrapper .loadingWrapper::before").delay( 2000 ).slideUp().addClass("orDont").delay( 2000 ).slideDown();
            } else {
                loadPageEmbed(url);
            } 
        }
    });


	var TableDatatablesManaged = function () {
        var initTable1 = function () {
        	console.log('1');
            var table = $('#questionPackTable');
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
                    [5, 10, 20, -1],
                    [5, 10, 20, "All"] // change per page values here
                ],
                // set the initial value
                "pageLength": 10,            
                "pagingType": "bootstrap_full_number",
                "columnDefs": [{  // set default column settings
                    'orderable': true,
                    'targets': [0]
                }, {
                    "searchable": true,
                    "targets": [0]
                }],
                "order": [
                    [1, "asc"]
                ] // set first column as a default sort by asc
            });

            //var tableWrapper = jQuery('#clubAdminTable_wrapper');
        }

        return {
        	
            //main function to initiate the module
            init: function () {
                if (!jQuery().dataTable) {
                	console.log('3');
                    return;
                }
                console.log('4');
                initTable1();
            }
        };
    }();

    TableDatatablesManaged.init();

    /*var country;
    $(".js_countrySelect").off().one("click", function() {
        if (country != $(this).data("country")) {
            country = $(this).data("country");
            $("#region option").val(country);
            //$("#mainForm").submit();
            getNewData();
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
                $(".loadingWrapper").removeClass("active");
            },
            error: function (jXHR, textStatus, errorThrown) {
                alert(errorThrown);
                $(".loadingWrapper").removeClass("active");
            }
        });
    }
 
 
    $(".page-content-wrapper").off().one("click", ".jsLoad", function(e) {
        var url = $(this).data("url");
        $('.contentHolder').load("../clubadmin/" + url);
    });*/








});
</script>
</body>
</html>