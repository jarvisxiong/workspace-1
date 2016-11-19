<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/Question"/>

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

<div class="statusMsg">

</div>

<form role="form" name="form1" method="post" action="../questionadmin/saveQuestion.jsp"> 


<div class="col-md-6">
		<div class="portlet light bordered">

			<div class="portlet-title">
                <div class="caption font-green">
                    <i class="icon-pin font-green"></i>
                    <span class="caption-subject bold uppercase">Edit Question</span>
                </div>

                <div class="col-md-6">
                	<div class="btn-group pull-right">
                        <button class="btn green  btn-outline dropdown-toggle" data-toggle="dropdown">
                            Language
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

            <div class="portlet-body form">

	            <div class="form-body">
 
	            	<c:if test = "${questionPackId == ''}">
	            	<div class="form-group form-md-line-input form-md-floating-label has-info">
						<select class="form-control edited" id="question_pack_id" name="question_pack_id">
							<!--<option value="">Select QuestionPack</option>-->
							<c:forEach items="${questionPackList}" var="questionPack">
								<option value="${questionPack.aUnique}"
									<c:forEach items="${questionInPacks}" var="questionInPack">
										<c:if test="${questionInPack.aUnique eq questionPack.aUnique}">
											<c:out value="selected"/>
										</c:if>
									</c:forEach> >
										${questionPack.aName}
								</option>
							</c:forEach>
						</select>
						<label for="form_control_1">Select QuestionPack</label>
					</div>
					</c:if>

					<input type="hidden" name="question_pack_id" value="${questionPackId}">

					<div class="form-group form-md-line-input form-md-floating-label">
					    <input type="text" class="form-control edited" value="${language}" name="language">
					    <label for="form_control_1">Language:</label>
					    <span class="help-block">${language}</span>
					</div>						

					<div class="form-group form-md-line-input form-md-floating-label">
					    <input type="text" class="form-control edited" value="${question.question}" name="question">
					    <label for="form_control_1">Question:</label>
					    <span class="help-block">${question.question}</span>
					</div>

					<div class="form-group form-md-line-input form-md-floating-label">
                        <input type="text" class="form-control edited" value="${question.optionA}" name="option_a">
                        <label for="form_control_1">OptionA:</label>
                        <span class="help-block">${question.optionA}</span>
                    </div>

                    <div class="form-group form-md-line-input form-md-floating-label">
                        <input type="text" class="form-control edited" value="${question.optionB}" name="option_b">
                        <label for="form_control_1">OptionB:</label>
                        <span class="help-block">${question.optionB}</span>
                    </div>

                    <div class="form-group form-md-line-input form-md-floating-label">
                        <input type="text" class="form-control edited" value="${question.correctOption}" name="correct_option">
                        <label for="form_control_1">Correct Otpion:</label>
                        <span class="help-block">${question.correctOption}</span>
                    </div>
				</div>

				<div class="form-actions noborder">
                    <input type="submit" name="save" value="save" class="btn blue" id="submit" />
                </div>
			</div> <!-- body form -->
		</div> <!-- portlet close -->
	</div><!-- close the row -->

</form>

<script>
	$('form').submit(function(event) {
            $("#submit").attr("value", "Sending data, please wait...");
            var $form = $(this);
            $.ajax({
                url : $form.attr('action'),
                type: "post",
                data: $form.serialize()
            }).done(function(data) {
                alert(data);
            	$("#submit").attr("value", "Save");
                var success =  $($.parseHTML(data)).filter(".statusMsg").text(); 
               	
                alertInfo(success);
            });
            event.preventDefault();
        });
</script>