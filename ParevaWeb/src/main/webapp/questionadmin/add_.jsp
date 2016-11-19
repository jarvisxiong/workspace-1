<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/Question"/>
<form name="form1" method="post" action="../questionadmin/add.jsp">
<c:if test = "${questionPackId == ''}">
<select name="question_pack_id" id="question_pack_id">
							<option value="">Select QuestionPack</option>
							<c:forEach items="${questionPackList}" var="questionPack">
							<option value="${questionPack.aUnique}">${questionPack.aName}</option>
							</c:forEach>
						</select>
						</c:if>

<input type="hidden" name="question_pack_id" value="${questionPackId}">
Question: <input type="text" name="question" value=""><br/>
OptionA: <input type="text" name="option_a" value=""><br/>
OptionB: <input type="text" name="option_b" value="" ><br/>
CorrectOtpion: <input type="text" name="correct_option" value="" ><br/>
<input type ="submit" name="save" value="save">

</form>