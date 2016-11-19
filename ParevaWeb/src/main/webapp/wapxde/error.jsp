<%@ include file="global-wap-header.jsp"%>

<%
	String status = (String) request.getSession().getAttribute(
			"error_message");

	if (status == null) {
		status = "";
	}
%>

<body>
	<div align="center">
		<div class="container">
			<div class="header" align="left">
				
			</div>
		 
			<div class="title" align="left">
				<b><%=lp.get("error")%></b>
			</div>
			 


			<%=status%>

			<%
				if (status.trim().length() <= 0) {
			%>
			<div class="item" align="left"><%=lp.get("not_found")%></div>
			<%
				}
			%>


