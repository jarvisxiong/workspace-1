<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/Question"/>
<form name="form1" method="post" action="../questionadmin/add.jsp">
<select multiple name="question_pack_id" id="question_pack_id">
							<option value="">Select QuestionPack</option>
							<c:forEach items="${questionPackList}" var="questionPack">
							<option value="${questionPack.aUnique}" 
								<c:forEach items="${questionInPacks}" var="questionInPack">
									<c:if test="${questionInPack.aUnique eq questionPack.aUnique}">
										<c:out value="selected"/>
									</c:if>
								</c:forEach>>${questionPack.aName}</option>
							</c:forEach>
						</select>
<input type ="text" name="language" value="${language}">						

<input type="hidden" name="question_pack_id" value="${questionPackId}">
Question: <input type="text" name="question" value="${question.question}"><br/>
OptionA: <input type="text" name="option_a" value="${question.optionA}"><br/>
OptionB: <input type="text" name="option_b" value="${question.optionB}" ><br/>
CorrectOtpion: <input type="text" name="correct_option" value="${question.correctOption}" ><br/>
<input type ="submit" name="save" value="save">

</form>