<%@ include file="/WEB-INF/jspf/wapsiteadmin/modpromo5.jspf"%>
<script>
	function changeDivClass() {
		var treeDiv = document.getElementById('treeDiv');
		if (treeDiv != null) {
			treeDiv.style.position = "absolute";
		}
	}
</script>
<script type="text/javascript" src="lib/js/mktree.js"></script>
<link rel="stylesheet" href="lib/css/mktree.css">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="JavaScript">
	function submitForm(thisForm, fvalue) {
		thisForm.ss.value = fvalue;
		thisForm.submit();
	}
	function win(urlPath) {
		var winl = (screen.width - 400) / 2;
		var wint = (screen.height - 800) / 2;
		var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top='
				+ wint + ',left=' + winl;
		newWin = window.open(urlPath, 'sim', settings);
		newWin.focus();
	}
</script>
</head>
<body>
	<form action="<%=fileName%>.jsp" method="post">
		<input type="hidden" name="ss" value="1"> <input type="hidden"
			name="srvc" value="<%=srvc%>"> <input type="hidden"
			name="cmd" value="<%=cmd%>"> <input type="hidden" name="clnt"
			value="<%=client%>"> <a
			href="index.jsp?regionid=<%=regionid%>">Back</a><br />
		<div class="treeDiv" id="treeDiv">
			<table>
				<tr>
					<td>&nbsp;</td>
				</tr>

				<tr>
					<td colspan="1" align="left" valign="top" nowrap="nowrap"><jsp:include
							page="/VideoCategory" /></td>
				</tr>
			</table>
		</div>
		<input type="submit" value="Apply Changes">
	</form>
</body>
</html>


