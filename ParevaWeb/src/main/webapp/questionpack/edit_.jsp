<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/jspf/coreimport.jspf"%>
<jsp:include page="/QuestionPack"/>

${action} Question Pack<br/>
${msg}
<form name="form1" method="post" action="edit.jsp">
<%
QuestionPack questionPack=(QuestionPack)request.getAttribute("questionPack");
if(questionPack!=null){	
%>
<input type="hidden" name="action" value="${action}">
<input type="text" name="unique" value="${questionPack.aUnique}" >
<input type="text" name="name" value="${questionPack.aName}">
<input type="text" name="language" value="${questionPack.aLanguage}">
<input type="text" name="created" value="${created}" >
<input type ="submit" name="save" value="save">
<%	
}else{
%>
<input type="hidden" name="action" value="${action}">
<input type="text" name="name" value="">
<input type="text" name="language" value="">
<input type ="submit" name="save" value="save">
<%	
}
%>

</form>