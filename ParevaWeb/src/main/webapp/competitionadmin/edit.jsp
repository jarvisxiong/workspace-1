<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/WEB-INF/jspf/coreimport.jspf"%>
<jsp:include page="/Competition" />
${action}
<div class="statusMsg" style="display: none;">${msg}</div>
<div class="reloadContainer">

	<div class="alertHolder"></div>


	<div class="row">
		<div class="col-md-12">
			<div class="portlet light bordered">
				<div class="portlet-title">
					<div class="caption">
						<i class="icon-bubble font-green-sharp"></i> <span
							class="caption-subject font-green-sharp bold uppercase">Competition
							Details (${competition.competition.aName})</span>
					</div>
					<div class="actions">
						<div class="btn-group">
							<a
								class="btn green-haze btn-outline btn-circle btn-sm js_loadURL"
								data-url="/questionpack/index.jsp?lang=en"> Back to all
								Competitions <i class="fa fa-mail-reply"></i>
							</a>
						</div>
					</div>
				</div>
				<div class="portlet-body">
					<ul class="nav nav-tabs">
						<li class="active"><a href="#tab_2_1" data-toggle="tab"
							aria-expanded="true">${action} Competition Details </a></li>
						<li class=""><a href="#tab_2_2" data-toggle="tab"
							aria-expanded="false"> Competition Questions </a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane fade active in" id="tab_2_1">
							<div class="portlet-body form">
								<form role="form" action="../competitionadmin/edit.jsp"
									method="post">
									<input type="hidden" name="action" value="${action}">
									<div class="form-body">

										<div class="row">
											<div class="form-group form-md-line-input col-md-8">
												<input type="text" name="name"
													value="${competition.competition.aName}"
													class="form-control" id="form_control_1"> <label
													for="form_control_1">Competition Name</label> <span
													class="help-block">The name of the Competition</span>
											</div>

											<div class="form-group form-md-line-input col-md-8">
												<input type="checkbox" name="status"
													<c:if test="${competition.competition.aStatus}">checked</c:if>
													class="form-control" id="form_control_1"> <label
													for="form_control_1">Active</label>

											</div>

											<div class="row">
												<div class="form-group form-md-line-input has-info col-md-8">
													<select class="form-control" id="form_control_region"
														name="market">
														<option value=""><c:out value="Select Region" /></option>
														<c:forEach items="${competition.regions}" var="region">

															<option value="${region}"
																<c:if test="${region == competition.competition.aMarket}">
                                                                    selected 
                                                                </c:if>>
																<c:out value="${region}" />
															</option>

														</c:forEach>




													</select> <label for="form_control_1">Region</label>
												</div>

												<div class="form-group form-md-line-input has-info col-md-8">
													<select multiple class="form-control"
														id="form_control_region" name="question_pack">
														<option value=""><c:out value="Select Question Pack" /></option>
														<c:forEach items="${competition.questionPackList}" var="questionPack">
															<option value="${questionPack.aUnique}"
																<c:forEach items="${competition.questionList}" var="question">
																	<c:if test="${questionPack.aUnique eq question.questionPack.aUnique}">
																		<c:out value="selected"/>
																	</c:if>
																</c:forEach>>
															${questionPack.aName}</option>
														</c:forEach>




													</select> <label for="form_control_1">Question Pack</label>
												</div>

												<div class="form-group form-md-line-input has-info col-md-8">
													<select class="form-control" id="form_control_club"
														name="club">
														<option value=""><c:out value="Select Club" /></option>
														<c:forEach items="${mobileClubs}" var="club">

															<option value="${club.unique}"
																<c:if test="${competition.competition.aClubUnique == club.unique}">
                                                                    selected 
                                                                </c:if>>
																<c:out value="${club.name}" />
															</option>

														</c:forEach>
													</select> <label for="form_control_1">Club</label>
												</div>


												<div class="form-group form-md-line-input col-md-4">
													<input type="text" name="unique"
														value="${competition.competition.aUnique}"
														class="form-control" readonly="" id="form_control_3">
													<label for="form_control_1">Competition ID</label>
												</div>

											</div>

											<div class="row">
												<div class="form-group form-md-line-input has-info col-md-8">
													<select class="form-control" id="form_control_2"
														name="language">
														<option value="Spanish"
															<c:if test="${competition.competitionLanguage == 'Spanish'}">
                                                                    selected 
                                                                </c:if>>
															Spanish</option>
														<option value="English"
															<c:if test="${competition.competitionLanguage == 'English'}">
                                                                    selected 
                                                                </c:if>>
															English</option>
														<option value="French"
															<c:if test="${competition.competitionLanguage == 'French'}">
                                                                    selected 
                                                                </c:if>>
															French</option>
														<option value="Italian"
															<c:if test="${competition.competitionLanguage == 'Italian'}">
                                                                    selected 
                                                                </c:if>>
															Italian</option>
														<option value="Portugese"
															<c:if test="${competition.competitionLanguage == 'Portugese'}">
                                                                    selected 
                                                                </c:if>>
															Portugese</option>


													</select> <label for="form_control_1">Language</label>
												</div>


												<div class="form-group form-md-line-input col-md-4">
													<input type="text" class="form-control" readonly=""
														value="${competition.numberOfQuestions}"
														id="form_control_4"> <label for="form_control_1">Questions</label>
												</div>
												<div class="row">
													<div
														class="form-group form-md-line-input has-info col-md-8">
														<select class="form-control" id="form_control_2"
															name="period">
															<option value="Daily"
																<c:if test="${competition.competition.aPeriod == 'Daily'}">
                                                                    selected 
                                                                </c:if>>
																Daily</option>
															<option value="Weekly"
																<c:if test="${competition.competition.aPeriod == 'Weekly'}">
                                                                    selected 
                                                                </c:if>>
																Weekly</option>
															<option value="Monthly"
																<c:if test="${competition.competition.aPeriod == 'Monthly'}">
                                                                    selected 
                                                                </c:if>>
																Monthly</option>
															<option value="Annual"
																<c:if test="${competition.competition.aPeriod == 'Annual'}">
                                                                    selected 
                                                                </c:if>>
																Annual</option>
															<option value="Adhoc"
																<c:if test="${competition.competition.aPeriod == 'Adhoc'}">
                                                                    selected 
                                                                </c:if>>
																Adhoc</option>


														</select> <label for="form_control_1">Period</label>
													</div>

													<div class="form-group form-md-line-input col-md-4">
														<input type="text" name="frequency" class="form-control"
															value="${competition.competition.aFrequency}"
															id="form_control_4"> <label for="form_control_1">Question
															Frequency</label>
													</div>
													<div class="form-group form-md-line-input col-md-4">
														<input type="text" name="prize" class="form-control"
															value="${competition.competition.aPrize}"
															id="form_control_4"> <label for="form_control_1">Prize</label>
													</div>
													<div class="form-group form-md-line-input col-md-4">
														<fmt:formatDate
															value="${competition.competition.aStartDate}"
															pattern="yyyy-MM-dd HH:mm:ss" var="startDate" />
														<input type="text" name="start_date"
															placeholder="yyyy-mm-dd hh:mm:ss" class="form-control"
															value="${startDate}" id="form_control_4"> <label
															for="form_control_1">Start Date</label>
													</div>

													<div class="form-group form-md-line-input col-md-4">
														<fmt:formatDate value="${competition.lastDrawDate}"
															pattern="yyyy-MM-dd HH:mm:ss" var="lastDrawDate" />
														<input type="text" class="form-control" readonly=""
															value="${lastDrawDate}" id="form_control_4"> <label
															for="form_control_1">Last Draw Date</label>
													</div>
													<div class="form-group form-md-line-input col-md-4">
														<fmt:formatDate value="${competition.nextDrawDate}"
															pattern="yyyy-MM-dd HH:mm:ss" var="nextDrawDate" />
														<input type="text" class="form-control" readonly=""
															value="${nextDrawDate}" id="form_control_4"> <label
															for="form_control_1">Next Draw Date</label>
													</div>
												</div>

												<div class="form-group form-md-line-input">
													<input type="text" name="created" class="form-control"
														readonly="" value="${created}" id="form_control_5">
													<label for="form_control_1">Created</label>
												</div>

											</div>
											<div class="form-actions noborder">
												<button type="submit" name="save" value="save"
													class="btn blue">Save</button>
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
													<a
														href="../questionadmin/add.jsp?question_pack_id=${questionPack.aUnique}">ADD
														Questions</a> <i class="fa fa-plus"></i>
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

									<table
										class="table table-striped table-bordered table-hover table-checkable order-column"
										id="questionPackTable">

										<thead>
											<tr>
												<th>QID</th>
												<th>Question</th>
												<th>Answer 1</th>
												<th>Answer 2</th>
												<th>Correct Answer</th>
												<th>Active Date</th>
												<th>From Pack</th>
											</tr>
										</thead>

										<tbody>

											<c:forEach items="${competition.questionList}" var="question">
												<c:forEach items="${question.questions}" var="question_">
													<tr class="gradeX" role="row">
														<td><c:out value="${question_.questionId}" /></td>
														<td><c:out value="${question_.question}" /></td>
														<td><c:out value="${question_.optionA}" /></td>
														<td><c:out value="${question_.optionB}" /></td>
														<td><c:out value="${question_.correctOption}" /></td>
														<td><fmt:formatDate value="${question_.aCreated}"
																pattern="yyyy-MM-dd HH:mm:ss" /></td>
														<td><c:out value="${question.questionPack.aName}" /></td>

													</tr>
												</c:forEach>
											</c:forEach>


										</tbody>

									</table>

								</div>

								<!--  <button type="button" class="btn red pull-right">Delete Selected From Pack</button> -->
							</div>

						</div>

					</div>
				</div>
			</div>
		</div>
	</div>

	</form>
	<script
		src="/lib/templates/static/AdminPanel/assets/pages/scripts/table-datatables-managed.min.js"
		type="text/javascript"></script>
	<script src="https://code.jquery.com/jquery-3.1.1.js"
		integrity="sha256-16cdPddA6VdVInumRGo6IbivbERE8p7CQR3HzTBuELA="
		crossorigin="anonymous"></script>
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
    
    $('#form_control_region').on('change', function() {
    	var region=this.value;
    	$.getJSON({
    		  url: 'fetchClubByRegion.jsp',
    		  type: 'POST',
    		  data: { 'region':region}
    		}).done(function(data) {
    			var $el = $("#form_control_club");
    			$el.empty(); // remove old options
    			$.each(data, function(key,value) {
    			  $el.append($("<option></option>")
    			     .attr("value", value).text(key));
    			});
    		 });
            event.preventDefault();
    	});
    
    </script>

</div>