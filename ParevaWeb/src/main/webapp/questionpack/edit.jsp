<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/jspf/coreimport.jspf"%>
<jsp:include page="/QuestionPack"/>

<div class="statusMsg" style="display: none;">${msg}</div>
<div class="reloadContainer">

    <div class="alertHolder"></div> 

    <%
    QuestionPack questionPack=(QuestionPack)request.getAttribute("questionPack");

    if(questionPack!=null){
    %>
    <form name="form1" method="post" action="../questionpack/edit.jsp?unique=${questionPack.aUnique}">
    <%
    } else {
       
    %>
    <form name="form1" method="post" action="../questionpack/edit.jsp">
    <% 
    }
    %>

    <input type="hidden" name="action" value="${action}">

   

    <div class="row">
        <div class="col-md-12">
            <div class="portlet light bordered">
                <div class="portlet-title">
                    <div class="caption">
                        <i class="icon-bubble font-green-sharp"></i>
                        <span class="caption-subject font-green-sharp bold uppercase">Question Pack Details (${questionPack.aName})</span>
                    </div>
                    <div class="actions">
                        <div class="btn-group">
                            <a class="btn green-haze btn-outline btn-circle btn-sm js_loadURL" data-url="/questionpack/index.jsp?lang=en" > Back to all Question Packs
                                <i class="fa fa-mail-reply"></i>
                            </a>
                        </div>
                    </div>
                </div>
                <div class="portlet-body">
                    <ul class="nav nav-tabs">
                        <li class="active">
                            <a href="#tab_2_1" data-toggle="tab" aria-expanded="true">${action} Question Pack Details </a>
                        </li>
                        <li class="">
                            <a href="#tab_2_2" data-toggle="tab" aria-expanded="false"> Pack Questions </a>
                        </li>
                    </ul>
                    <div class="tab-content">
                        <div class="tab-pane fade active in" id="tab_2_1">
                            <div class="portlet-body form">
                                        <form role="form" action="edit.jsp" method="post">
                                        <input type="hidden" name="action" value="${action}">
                                            <div class="form-body">

                                                <div class="row">
                                                    <div class="form-group form-md-line-input col-md-8">
                                                        <input type="text" name="name" value="${questionPack.aName}" class="form-control" id="form_control_1" >
                                                        <label for="form_control_1">Question Pack Name</label>
                                                        <span class="help-block">The name of the pack</span>
                                                    </div>

                                                    <div class="form-group form-md-line-input col-md-4">
                                                        <input type="text" name="unique" value="${questionPack.aUnique}" class="form-control" readonly="" id="form_control_3">
                                                        <label for="form_control_1">Pack ID</label>
                                                    </div>

                                                </div>

                                                <div class="row">
                                                    <div class="form-group form-md-line-input has-info col-md-8">
                                                        <select class="form-control" id="form_control_2" name="language" >
                                                            <option value="Spanish" 
                                                                <c:if test="${questionPack.aLanguage == 'Spanish'}">
                                                                    selected 
                                                                </c:if>
                                                            >
                                                                Spanish
                                                            </option>
                                                            <option value="English"
                                                                <c:if test="${questionPack.aLanguage == 'English'}">
                                                                    selected 
                                                                </c:if>
                                                            >
                                                                English
                                                            </option>
                                                            <option value="French" 
                                                                <c:if test="${questionPack.aLanguage == 'French'}">
                                                                    selected 
                                                                </c:if>
                                                            >
                                                                French
                                                            </option>
                                                            <option value="Italian"
                                                                <c:if test="${questionPack.aLanguage == 'Italian'}">
                                                                    selected 
                                                                </c:if>
                                                            >
                                                                Italian
                                                            </option>
                                                            <option value="Portugese"  
                                                                <c:if test="${questionPack.aLanguage == 'Portugese'}">
                                                                    selected 
                                                                </c:if>
                                                            >
                                                                Portugese
                                                            </option>

                                                            
                                                        </select>
                                                        <label for="form_control_1">Language</label>
                                                    </div>
                                                    
                                                    
                                                    <div class="form-group form-md-line-input col-md-4">
                                                        <input type="text" class="form-control" readonly="" value="${numberOfQuestions}" id="form_control_4" >
                                                        <label for="form_control_1">Questions</label>
                                                    </div>
                                                </div>

                                                <div class="form-group form-md-line-input">
                                                    <input type="text" name="created" class="form-control" readonly="" value="${created}" id="form_control_5" >
                                                    <label for="form_control_1">Created</label>
                                                </div>
                                                
                                            </div>
                                            <div class="form-actions noborder">
                                                <button type ="submit" name="save" value="save" class="btn blue">Save</button>
                                                <button type="button" class="btn red pull-right">Delete</button>
                                            </div>
                                        </form>
                                    </div>
                        </div>
                        <div class="tab-pane fade " id="tab_2_2">
                            

                            <div class="portlet-body">
                                <div class="table-toolbar">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="btn-group">
                                                <!--<button id="sample_editable_1_new" class="btn sbold green js_loadURL" data-url="../questionupload/index.jsp">-->
                                                <div id="sample_editable_1_new" class="btn sbold green">
                                                    <a href="../questionadmin/add.jsp?question_pack_id=${questionPack.aUnique}">ADD Questions</a>
                                                    <i class="fa fa-plus"></i>
                                                </div>
                                            </div>
                                        </div>
                                        <!--
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
                                    -->
                                    </div>
                                </div>
                                <div id="sample_1_wrapper" class="dataTables_wrapper no-footer">

                                    <table class="table table-striped table-bordered table-hover table-checkable order-column" id="questionPackTable" >
                                        
                                        <thead>
                                            <tr>
                                                <th> QID </th>
                                                <th> Question </th>
                                                <th> Answer 1 </th>
                                                <th> Answer 2 </th>
                                                <th> Correct Answer </th>
                                                <th> 
                                                    <input type="checkbox" class="group-checkable" data-set="#sample_1 .checkboxes" /> 
                                                </th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            
                                            <c:forEach items="${questionList}" var="question">
						        
                                                <tr class="gradeX" role="row"> 
                                                    <td>
                                                    	<c:out value="${question.questionId}" />
                                                    </td>
                                                    <td>
                                                       <c:out value="${question.question}" />
                                                    </td>
                                                    <td>
                                                        <c:out value="${question.optionA}" />
                                                    </td>
                                                    <td>
                                                        <c:out value="${question.optionB}" />
                                                    </td>
                                                    <td>
                                                        <c:out value="${question.correctOption}" />
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" class="checkboxes" value="1" /> </td>
                                                    </td>
                                                </tr>
 											</c:forEach>
                                                <tr class="gradeX" role="row"> 
                                                    <td>
                                                        1234
                                                    </td>
                                                    <td>
                                                        Who's John Gault?
                                                    </td>
                                                    <td>
                                                        Madan's Mom
                                                    </td>
                                                    <td>
                                                        Alex' Mom
                                                    </td>
                                                    <td>
                                                        1
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" class="checkboxes" value="1" /> </td>
                                                    </td>
                                                </tr>

                                                <tr class="gradeX" role="row"> 
                                                    <td>
                                                        12345
                                                    </td>
                                                    <td>
                                                        Who's you mum?
                                                    </td>
                                                    <td>
                                                        Madan
                                                    </td>
                                                    <td>
                                                        Alex
                                                    </td>
                                                    <td>
                                                        1
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" class="checkboxes" value="1" /> </td>
                                                    </td>
                                                </tr>
                                            
                                        </tbody>

                                    </table>

                                </div>

                                <button type="button" class="btn red pull-right">Delete Selected From Pack</button>
                            </div>

                        </div>
                        
                    </div>
                </div>
            </div>
        </div>
    </div>

    </form>
<script src="/lib/templates/static/AdminPanel/assets/pages/scripts/table-datatables-managed.min.js" type="text/javascript"></script>
    <script>

    $( document ).ready(function() {

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
                    "info": "Showing _START_ to _END_ of _TOTAL_ questions",
                    "infoEmpty": "No records found",
                    "infoFiltered": "(filtered1 from _MAX_ total questions)",
                    "lengthMenu": "Show _MENU_",
                    "search": "Search:",
                    "zeroRecords": "No matching questions found",
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

        $(".js_loadURL").off('click').on("click", function() {
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
    });
    </script>

</div>