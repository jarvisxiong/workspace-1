<%@ include file="global-wap-header.jsp" %>
<%


%>

<%@ include file="xhtmlhead.jsp" %>
<body>
<div align="center"><div class="container">
<div class="header" align="left">
    <img src="/images/wap/<%=dImages.get("img_header2_" + handset.getImageProfile())%>" alt=""
        width="<%=handset.getFullWidth()%>" height="<%=handset.getImageHeight("header2", dImages)%>" />
</div>

<div class="title" align="left"><b><%=lp.get("error")%></b></div>

<div class="item" align="left"><%=lp.get("authnerror")%></a></div>

<%@ include file="footer.jsp" %>
