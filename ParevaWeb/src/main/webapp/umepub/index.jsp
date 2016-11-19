<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/umepub/index_1.jspf" %>

<html>

<head>
<link href="<%=contenturl%>/css/default.css" rel="stylesheet" type="text/css">
<link rel="shortcut icon" href="../favicon.ico" type="image/x-icon"/>
  <link rel="stylesheet" type="text/css" href="<%=contenturl%>/css/style.css" />
		<script src="<%=contenturl%>/js/cufon-yui.js" type="text/javascript"></script>
		<script src="<%=contenturl%>/js/ChunkFive_400.font.js" type="text/javascript"></script>
		<script type="text/javascript">
			Cufon.replace('h1',{ textShadow: '1px 1px #fff'});
			Cufon.replace('h2',{ textShadow: '1px 1px #fff'});
			Cufon.replace('h3',{ textShadow: '1px 1px #000'});
			Cufon.replace('.back');
		</script>
</head>

<body>
    <div id="content-wrapper">
<form action="/" method="post" target="_top" class="form_wrapper">
     
<input type="hidden" name="login" value="1">

<div>
    <label>Username:</label>
		<input type="text" class="textbox_white" size="30" name="anyx_login" value="">
</div>
<div>             
<label>Password:</label>
		<input type="password" class="textbox_white" size="30" name="anyx_pw" value="">
		</div>
<div class="bottom">
<input type="submit" name="login" value="&nbsp;&nbsp;Login&nbsp;&nbsp;" class="button">
<div class="clear"></div>
<%=statusMsg%>
</div>


</form>
     </div>
</body>
</html>
